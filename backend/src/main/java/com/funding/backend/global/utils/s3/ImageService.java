package com.funding.backend.global.utils.s3;



import com.funding.backend.domain.projectImage.entity.ProjectImage;
import java.io.IOException;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
@RequiredArgsConstructor
public class ImageService {

    private final S3Uploader s3Uploader;

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


    //이미지 여러개 저장
    public List<String> saveImageList(List<MultipartFile> images){
        try {
            return s3Uploader.saveMultiImages(images); // 업로드 후 URL 반환
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void updateImageList(List<ProjectImage> beforeImages, List<MultipartFile> images){

    }



}
