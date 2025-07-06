package com.funding.backend.global.utils.s3;



import com.funding.backend.domain.project.entity.Project;
import com.funding.backend.domain.projectImage.entity.ProjectImage;
import com.funding.backend.domain.projectImage.repository.ProjectImageRepository;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import lombok.RequiredArgsConstructor;
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
                String storedName = url.substring(url.lastIndexOf("/") + 1);

                ProjectImage pi = ProjectImage.builder()
                        .imageUrl(url)
                        .storedFileName(storedName)
                        .originalFilename(image.getOriginalFilename())
                        .project(project)
                        .build();

                savedList.add(projectImageRepository.save(pi));
            } catch (IOException e) {
                throw new RuntimeException("이미지 업로드 실패", e);
            }
        }
        return savedList;
    }


    public List<ProjectImage> updateImageList(List<ProjectImage> beforeImages, List<MultipartFile> newImages, Project project) {
        List<ProjectImage> updated = s3Uploader.autoImagesUploadAndDelete(beforeImages, newImages, project);

        // DB에 새로 추가된 건 저장
        for (ProjectImage image : updated) {
            if (image.getId() == null) {
                projectImageRepository.save(image);
            }
        }

        return updated;
    }




}
