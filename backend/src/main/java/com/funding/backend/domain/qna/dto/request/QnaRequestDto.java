package com.funding.backend.domain.qna.dto.request;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class QnaRequestDto {

    private String title;
    private String content;
    private Long projectId;
    private Long userId;
    private boolean visibility;

}
