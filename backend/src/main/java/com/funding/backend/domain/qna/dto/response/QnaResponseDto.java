package com.funding.backend.domain.qna.dto.response;

import com.funding.backend.domain.qna.entity.Qna;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
@Builder
public class QnaResponseDto {

    private Long id;
    private String title;
    private String content;
    private Long projectId;
    private Long userId;
    private boolean visibility;
    private LocalDateTime createdAt;
    private LocalDateTime modifiedAt;

    public static QnaResponseDto from(Qna qna){
        return QnaResponseDto.builder()
                .id(qna.getId())
                .title(qna.getTitle())
                .content(qna.getContent())
                .projectId(qna.getProject().getId())
                .userId(qna.getUser().getId())
                .visibility(qna.isVisibility())
                .createdAt(qna.getCreatedAt())
                .build();
    }

}
