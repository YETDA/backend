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
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
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
            amazonS3.putObject(new PutObjectRequest(bucket, uniqueFileName, multipartFile.getInputStream(), metadata));
        } catch (AmazonServiceException e) {
            e.printStackTrace();
        } catch (SdkClientException e) {
            e.printStackTrace();
        }
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
        // 1. 기존 storedFileName 리스트 (uuid 기반)
        List<String> beforeStoredNames = beforeImages.stream()
                .map(ProjectImage::getStoredFileName)
                .toList();

        // 2. 기존 이미지 map: originalFilename → ProjectImage
        Map<String, ProjectImage> originalNameToBeforeImage = beforeImages.stream()
                .collect(Collectors.toMap(ProjectImage::getOriginalFilename, Function.identity(), (a, b) -> a));

        // 3. 최종 반환될 이미지 리스트 (프론트 순서대로 구성)
        List<ProjectImage> orderedResult = new ArrayList<>();

        // 4. 업로드 대상 및 재사용 구분
        for (MultipartFile newFile : newImages) {
            String originalName = newFile.getOriginalFilename();

            ProjectImage matched = originalNameToBeforeImage.get(originalName);
            boolean isReuse = matched != null && newFile.getSize() == matched.getFileSize();

            if (isReuse) {
                // 기존 이미지 재사용
                orderedResult.add(matched);
            } else {
                // 새 이미지 업로드
                try {
                    String imageUrl = uploadFile(newFile);
                    String storedFileName = imageUrl.substring(imageUrl.lastIndexOf("/") + 1);

                    ProjectImage uploaded = ProjectImage.builder()
                            .imageUrl(imageUrl)
                            .storedFileName(storedFileName)
                            .originalFilename(originalName)
                            .fileSize(newFile.getSize())
                            .project(project)
                            .build();

                    orderedResult.add(uploaded);
                } catch (IOException e) {
                    throw new RuntimeException("파일 업로드 실패: " + originalName, e);
                }
            }
        }

        // 5. 삭제 대상: 기존 이미지 중에서 프론트에 포함되지 않은 것
        Set<String> newOriginalNames = newImages.stream()
                .map(MultipartFile::getOriginalFilename)
                .collect(Collectors.toSet());

        List<ProjectImage> imagesToDelete = beforeImages.stream()
                .filter(img -> !newOriginalNames.contains(img.getOriginalFilename()))
                .toList();

        for (ProjectImage image : imagesToDelete) {
            deleteFile(image.getStoredFileName()); // S3 삭제
        }

        return orderedResult; // ✅ 순서 보장된 리스트 반환
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

            amazonS3.putObject(new PutObjectRequest(bucket, uniqueFileName, multipartFile.getInputStream(), metadata));

            String fileUrl = amazonS3.getUrl(bucket, uniqueFileName).toString();

            return new S3FileInfo(fileUrl, originalFileName, multipartFile.getSize(), contentType);
        } catch (SdkClientException e) {
            throw new RuntimeException("파일 업로드 실패: " + originalFileName, e);
        }
    }








}
