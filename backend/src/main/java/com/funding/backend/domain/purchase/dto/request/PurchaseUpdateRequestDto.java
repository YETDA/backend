package com.funding.backend.domain.purchase.dto.request;


import com.funding.backend.enums.ProjectType;
import com.funding.backend.enums.ProvidingMethod;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import java.util.ArrayList;
import java.util.List;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;

@Getter
@Setter
@NoArgsConstructor
public class PurchaseUpdateRequestDto {

    @NotNull(message = "프로젝트 타입은 필수입니다.")
    private ProjectType projectType;

    @NotBlank(message = "제목은 필수입니다.")
    @Size(max = 100, message = "제목은 100자 이내여야 합니다.")
    private String title;

    @NotBlank(message = "소개글은 필수입니다.")
    @Size(max = 200, message = "소개글은 200자 이내여야 합니다.")
    private String introduce;

    @NotBlank(message = "내용은 필수입니다.")
    @Size(max = 1000, message = "소개글은 1000자 이내여야 합니다.")
    private String content;

    //이미지
    private List<MultipartFile> contentImage = new ArrayList<>();

    @NotBlank(message = "Git 주소는 필수입니다.")
    private String gitAddress;

    @NotNull(message = "구매 카테고리는 필수입니다.")
    private Long purchaseCategoryId;

    @NotNull(message = "평균 전송 소요시간은 필수입니다.")
    @Size(max = 20, message = "평균시간 작성 길이는 20자 이내여야 합니다.")
    private String averageDeliveryTime; // ✅ 필드명 변경

}
