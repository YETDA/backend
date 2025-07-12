package com.funding.backend.domain.qna.dto.response;

import com.funding.backend.domain.qna.entity.Qna;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@AllArgsConstructor
@Builder
public class AnswerResponseDto {
    private Long id;
    private String answer;

    public static AnswerResponseDto from(Qna qna){
        return AnswerResponseDto.builder()
                .id(qna.getId())
                .answer(qna.getAnswer())
                .build();
    }
}
