package com.funding.backend.domain.qna.dto.request;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class AnswerRequestDto {

    private String answer;
    private Long userId;

}
