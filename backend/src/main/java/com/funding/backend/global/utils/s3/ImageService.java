package com.funding.backend.global.utils.s3;



import com.funding.backend.domain.project.entity.Project;
import com.funding.backend.domain.projectImage.entity.ProjectImage;
import com.funding.backend.domain.projectImage.repository.ProjectImageRepository;
import com.funding.backend.global.exception.BusinessLogicException;
import com.funding.backend.global.exception.ExceptionCode;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.apache.commons.codec.binary.Hex;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
@RequiredArgsConstructor
public class ImageService {

    private final S3Uploader s3Uploader;
    private final ProjectImageRepository projectImageRepository;

    public String saveImage(MultipartFile multipartFile){
        try {
            return s3Uploader.uploadFile(multipartFile); // 업로드 후 URL 반환
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public String updateImage(MultipartFile multipartFile,String beforeImageUrl){
        deleteImage(beforeImageUrl);
        return saveImage(multipartFile);
    }

    public void deleteImage(String fileName){
        s3Uploader.deleteFile(fileName);
    }


    public List<ProjectImage> saveImageList(List<MultipartFile> images, Project project) {
        List<ProjectImage> savedList = new ArrayList<>();
        for (MultipartFile image : images) {
            try {
                String url = s3Uploader.uploadFile(image);

                //uuid이름 cat.jpg -> alskdjf12.jpg
                String storedName = url.substring(url.lastIndexOf("/") + 1);

                ProjectImage pi = ProjectImage.builder()
                        .imageUrl(url)
                        //여기는 alskdjf12.jpg  저장
                        .storedFileName(storedName)
                        //여기는 cat.jgg 저장
                        .originalFilename(image.getOriginalFilename())
                        //파일 사이즈
                        .fileSize(image.getSize())
                        .project(project)
                        .build();

                savedList.add(projectImageRepository.save(pi));
            } catch (IOException e) {
                throw new BusinessLogicException(ExceptionCode.IMAGE_UPLOAD_FAILED);
            }
        }
        return savedList;
    }


    public List<ProjectImage> updateImageList(List<ProjectImage> beforeImages, List<MultipartFile> newImages, Project project) {
        // S3 업로드 및 삭제 로직 포함
        List<ProjectImage> updated = s3Uploader.autoImagesUploadAndDelete(beforeImages, newImages, project);

        // 순서 재정렬 (프론트에서 보낸 순서를 기준으로)
        for (int i = 0; i < updated.size(); i++) {
            ProjectImage image = updated.get(i);
            image.setImageOrder(i);
            projectImageRepository.save(image);
        }


        // 삭제된 이미지 DB 제거
        for (ProjectImage oldImage : beforeImages) {
            if (!updated.contains(oldImage)) {
                projectImageRepository.delete(oldImage);
            }
        }

        return updated;
    }

    public S3FileInfo saveFile(MultipartFile multipartFile) {
        try {
            return s3Uploader.uploadAnyFile(multipartFile);
        } catch (IOException e) {
            throw new BusinessLogicException(ExceptionCode.FILE_UPLOAD_FAILED);
        }
    }


    public String getETagFromFileUrl(String fileUrl) {
        return s3Uploader.getETag(fileUrl);
    }

    public String calculateETag(MultipartFile file) throws IOException {
        // 일반적으로 MD5 기반
        try (InputStream is = file.getInputStream()) {
            MessageDigest md = MessageDigest.getInstance("MD5");
            byte[] buffer = new byte[8192];
            int bytesRead;
            while ((bytesRead = is.read(buffer)) != -1) {
                md.update(buffer, 0, bytesRead);
            }
            byte[] digest = md.digest();
            return Hex.encodeHexString(digest);
        } catch (NoSuchAlgorithmException e) {
            throw new BusinessLogicException(ExceptionCode.ETAG_HASH_FAILED);
        }
    }

    public String generateFileHash(String originalFileName, long fileSize, String contentType) {
        String input = originalFileName + fileSize + contentType;
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            byte[] digest = md.digest(input.getBytes(StandardCharsets.UTF_8));
            return Hex.encodeHexString(digest);  // Apache Commons Codec
        } catch (NoSuchAlgorithmException e) {
            throw new BusinessLogicException(ExceptionCode.MD5_HASH_FAILED);
        }
    }





}
