package com.funding.backend.domain.notice.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;

@Getter
@Schema(name = "Notice Create Request DTO", description = "공지사항 생성 DTO")
public class NoticeCreateRequestDto {

    // TODO: 공지사항 제목 제한 길이 결정
    @NotBlank(message = "제목은 필수입니다.")
    @Size(max = 100, message = "제목은 100자 이내여야 합니다.")
    @Schema(description = "공지사항 제목", example = "펀딩 결제 안내")
    private String title;

    // TODO: 공지사항 내용 제한 길이 결정
    @NotBlank(message = "내용은 필수입니다.")
    @Size(max = 1000, message = "내용은 1000자 이내여야 합니다.")
    @Schema(description = "공지사항 내용", example = "결제는 오후 6시에 진행됩니다.")
    private String content;
}
