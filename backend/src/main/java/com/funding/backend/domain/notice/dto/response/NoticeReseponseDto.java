package com.funding.backend.domain.notice.dto.response;

import com.funding.backend.domain.notice.entity.Notice;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Schema(name = "Notice Response DTO", description = "공지사항 응답 DTO")
public class NoticeReseponseDto {
    @Schema(description = "공지사항 ID", example = "1")
    private Long id;

    @Schema(description = "공지사항 제목", example = "펀딩 결제 안내")
    private String noticeTitle;

    @Schema(description = "공지사항 내용", example = "결제는 오후 6시에 진행됩니다.")
    private String noticeContent;

    @Schema(description = "생성일", example = "2025-07-15T14:25:14.293Z")
    private LocalDateTime createdAt;

    @Schema(description = "수정일", example = "2025-07-16T14:25:14.293Z")
    private LocalDateTime modifiedAt;

    public NoticeReseponseDto(Notice notice) {
        this.id = notice.getId();
        this.noticeTitle = notice.getTitle();
        this.noticeContent = notice.getContent();
        this.createdAt = notice.getCreatedAt();
        this.modifiedAt = notice.getModifiedAt();
    }
}
