package com.funding.backend.domain.donation.dto.request;

import java.time.LocalDate;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
// 프로젝트 생성할때 쓰이는 milestoneRequestDto
public class DonationMilestoneRequestDto {

    private String title;
    private String content;
    private LocalDate dueDate;

}
