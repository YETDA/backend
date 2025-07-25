package com.funding.backend.domain.report.dto.response;

import com.funding.backend.domain.report.entity.Report;
import com.funding.backend.enums.ReportCategory;
import com.funding.backend.enums.ReportStatus;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class ReportResponseDto {
    private Long id;
    private Long projectId;
    private Long reporterId;
    private ReportCategory reportCategory;
    private String content;
    private ReportStatus reportStatus;

    public static ReportResponseDto from(Report report) {
        return  ReportResponseDto.builder()
                .id(report.getId())
                .projectId(report.getProject().getId())
                .reporterId(report.getReporter().getId())
                .reportCategory(report.getReportCategory())
                .content(report.getContent())
                .reportStatus(report.getReportStatus())
                .build();
    }


}
