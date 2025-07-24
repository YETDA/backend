package com.funding.backend.domain.report.dto.request;

import com.funding.backend.enums.ReportCategory;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ReportRequestDto {
    public Long projectId;
    private ReportCategory reportCategory;
    private String content;
}
