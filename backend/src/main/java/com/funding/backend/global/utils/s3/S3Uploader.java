package com.funding.backend.global.utils.s3;


import com.amazonaws.AmazonServiceException;
import com.amazonaws.SdkClientException;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.funding.backend.domain.project.entity.Project;
import com.funding.backend.domain.projectImage.entity.ProjectImage;
import com.funding.backend.global.exception.BusinessLogicException;
import com.funding.backend.global.exception.ExceptionCode;
import com.funding.backend.global.utils.CreateRandomNumber;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
@RequiredArgsConstructor
@Slf4j
public class S3Uploader {

    @Value("${cloud.aws.s3.bucket}")
    private String bucket;

    private final AmazonS3 amazonS3;

    @Value("${url.s3}")
    private String S3_FIX_URL;




    // S3에 이미지 등록
    public String uploadFile(MultipartFile multipartFile) throws IOException {
        String fileName = multipartFile.getOriginalFilename();

        //파일 형식 구하기
        String ext = fileName.split("\\.")[1];
        log.info("fileName 확인 !!" + fileName);
        log.info("파일 형식 확인 !! " + ext);

        // 랜덤한 UUID를 이용한 고유 파일명 생성
        String uniqueFileName = CreateRandomNumber.timeBasedRandomName() + "." + ext;



        String contentType = switch (ext) {
            case "jpeg", "jpg" -> "image/jpeg";
            case "png" -> "image/png";
            case "txt" -> "text/plain";
            case "csv" -> "text/csv";
            default -> "application/octet-stream";
        };

        try {
            ObjectMetadata metadata = new ObjectMetadata();
            metadata.setContentType(contentType);

            //S3에 파일 업로드
            amazonS3.putObject(new PutObjectRequest(bucket, uniqueFileName, multipartFile.getInputStream(), metadata)
                    .withCannedAcl(CannedAccessControlList.PublicRead));
        } catch (AmazonServiceException e) {
            e.printStackTrace();
        } catch (SdkClientException e) {
            e.printStackTrace();
        }

//        //object 정보 가져오기 (디버깅용) 데이터 잘 들어갔는지 확인하려고
//        ListObjectsV2Result listObjectsV2Result = amazonS3.listObjectsV2(bucket);
//        List<S3ObjectSummary> objectSummaries = listObjectsV2Result.getObjectSummaries();
//
//        for (S3ObjectSummary object: objectSummaries) {
//            log.info("object = " + object.toString());
//        }
        return amazonS3.getUrl(bucket, uniqueFileName).toString();
    }



    // S3에 이미지 삭제
    public void deleteFile(String imageUrl) {
        try {
            String key = imageUrl.contains(".com/") ? imageUrl.split(".com/")[1] : imageUrl;
            amazonS3.deleteObject(bucket, key);

        } catch (AmazonServiceException e) {
           // System.err.println(e.getErrorMessage());
        } catch (Exception exception) {
            throw new BusinessLogicException(ExceptionCode.S3_DELETE_ERROR);
        }
    }

    public List<ProjectImage> autoImagesUploadAndDelete(List<ProjectImage> beforeImages, List<MultipartFile> newImages, Project project) {
        // 기존 저장된 storedFileName 리스트
        List<String> beforeFileNames = beforeImages.stream()
                .map(ProjectImage::getStoredFileName)
                .toList();

        // 새로운 파일 이름 리스트 (originalName → 비교용 아님!)
        List<String> newOriginalNames = newImages.stream()
                .map(MultipartFile::getOriginalFilename)
                .toList();

        // 새로 업로드할 파일들: storedFileName이 없기 때문에 originalFilename만 비교해서 유사 필터링 (주의: 완전 정확하지 않음)
        List<MultipartFile> imagesToUpload = newImages.stream()
                .filter(file -> beforeImages.stream().noneMatch(
                        img -> img.getOriginalFilename().equals(file.getOriginalFilename())
                ))
                .toList();

        // 삭제할 파일들: before 중에서 newOriginalNames에 없는 originalFilename
        List<ProjectImage> imagesToDelete = beforeImages.stream()
                .filter(img -> !newOriginalNames.contains(img.getOriginalFilename()))
                .toList();

        // 실제 업로드 수행
        List<ProjectImage> uploadedImages = new ArrayList<>();
        for (MultipartFile file : imagesToUpload) {
            try {
                String imageUrl = uploadFile(file); // → S3에 저장하고 URL 리턴
                String storedFileName = imageUrl.substring(imageUrl.lastIndexOf("/") + 1);

                uploadedImages.add(ProjectImage.builder()
                        .imageUrl(imageUrl)
                        .storedFileName(storedFileName)
                        .originalFilename(file.getOriginalFilename())
                        .project(project)
                        .build());
            } catch (IOException e) {
                throw new RuntimeException("파일 업로드 실패: " + file.getOriginalFilename(), e);
            }
        }

        // 삭제 수행
        for (ProjectImage image : imagesToDelete) {
            deleteFile(image.getStoredFileName()); // UUID 기반 이름으로 삭제
        }

        // 결과 리스트: 기존 유지 + 새로 추가
        List<ProjectImage> result = beforeImages.stream()
                .filter(img -> !imagesToDelete.contains(img))
                .collect(Collectors.toList());

        result.addAll(uploadedImages);

        return result;
    }

    public S3FileInfo uploadAnyFile(MultipartFile multipartFile) throws IOException {
        String originalFileName = multipartFile.getOriginalFilename();
        String ext = originalFileName.substring(originalFileName.lastIndexOf('.') + 1).toLowerCase();
        String uniqueFileName = CreateRandomNumber.timeBasedRandomName() + "." + ext;

        // 기본 MIME 설정
        String contentType = switch (ext) {
            case "jpeg", "jpg" -> "image/jpeg";
            case "png" -> "image/png";
            case "pdf" -> "application/pdf";
            case "zip" -> "application/zip";
            case "txt" -> "text/plain";
            case "csv" -> "text/csv";
            case "py" -> "text/x-python";
            case "html" -> "text/html";
            case "docx" -> "application/vnd.openxmlformats-officedocument.wordprocessingml.document";
            case "pptx" -> "application/vnd.openxmlformats-officedocument.presentationml.presentation";
            default -> "application/octet-stream";
        };

        try {
            ObjectMetadata metadata = new ObjectMetadata();
            metadata.setContentType(contentType);
            metadata.setContentLength(multipartFile.getSize());

            amazonS3.putObject(new PutObjectRequest(bucket, uniqueFileName, multipartFile.getInputStream(), metadata)
                    .withCannedAcl(CannedAccessControlList.PublicRead));

            String fileUrl = amazonS3.getUrl(bucket, uniqueFileName).toString();

            return new S3FileInfo(fileUrl, originalFileName, multipartFile.getSize(), contentType);
        } catch (AmazonServiceException | SdkClientException e) {
            throw new RuntimeException("파일 업로드 실패: " + originalFileName, e);
        }
    }








}
