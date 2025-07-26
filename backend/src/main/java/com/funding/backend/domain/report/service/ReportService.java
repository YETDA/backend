package com.funding.backend.domain.report.service;

import com.funding.backend.domain.admin.service.AdminUserManagingService;
import com.funding.backend.domain.project.entity.Project;
import com.funding.backend.domain.project.service.ProjectService;
import com.funding.backend.domain.report.dto.request.ReportRequestDto;
import com.funding.backend.domain.report.dto.response.ReportResponseDto;
import com.funding.backend.domain.report.dto.response.ReportStatsResponseDto;
import com.funding.backend.domain.report.entity.Report;
import com.funding.backend.domain.report.repository.ReportRepository;
import com.funding.backend.domain.user.entity.User;
import com.funding.backend.domain.user.service.UserService;
import com.funding.backend.enums.ProjectStatus;
import com.funding.backend.enums.ReportStatus;
import com.funding.backend.enums.RoleType;
import com.funding.backend.global.exception.BusinessLogicException;
import com.funding.backend.global.exception.ExceptionCode;
import com.funding.backend.security.jwt.TokenService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@AllArgsConstructor
public class ReportService {

    private final ReportRepository reportRepository;
    private final UserService userService;
    private final ProjectService projectService;
    private final TokenService tokenService;
    private final AdminUserManagingService adminUserManagingService;

    //조회 및 예외 헬퍼
    private Report findReportOrThrow(Long reportId){
        return reportRepository.findById(reportId)
                .orElseThrow(() -> new BusinessLogicException(ExceptionCode.REPORT_NOT_FOUND));
    }

    //현재 사용자 id 추출
    private Long getCurrentUserId(){
        try {
            return  tokenService.getUserIdFromAccessToken();
        }catch (BusinessLogicException e){
            throw new BusinessLogicException(ExceptionCode.ACCESS_TOKEN_NOT_FOUND);
        }
    }

    // 내가 신고한 것만 반환
    private Report findMyReportOrThrow(Long reportId, Long reporterId){
        Report report = findReportOrThrow(reportId);
        if(!report.getReporter().getId().equals(reporterId)){
            throw new BusinessLogicException(ExceptionCode.REPORT_ACCESS_DENIED);
        }
        return report;
    }


    /*
    조회
    사용자별 신고내역, 전체 신고목록, 상세조회, 프로젝트별 신고내역
     */
    // 신고내역 조회(본인)
    @Transactional(readOnly = true)
    public Page<ReportResponseDto> getMyReports(Pageable pageable){
        Long userId = getCurrentUserId();

        User reporter = userService.getUserOrThrow(userId);

        return reportRepository.findByReporter(reporter, pageable)
                .map(report -> ReportResponseDto.from(report));
    }

    // 전체 신고목록(관리자)
    @Transactional(readOnly = true)
    public Page<ReportResponseDto> getAllReports(ReportStatus status,Pageable pageable){
        adminUserManagingService.validAdmin();

        if(status != null){
            return reportRepository.findByReportStatus(status,pageable)
                    .map(report -> ReportResponseDto.from(report));
        }
        return reportRepository.findAll(pageable)
                .map(report -> ReportResponseDto.from(report));
    }

    //신고 상세조회(관리자/작성자)
    @Transactional(readOnly = true)
    public ReportResponseDto getReportDetail(Long reportId){
        Long userId = getCurrentUserId();

        boolean isAdmin = false;
        try {
            adminUserManagingService.validAdmin();
            isAdmin = true;
        }catch (BusinessLogicException e){
        }

        Report report = findReportOrThrow(reportId);

        if(!isAdmin && !report.getReporter().getId().equals(userId)){
            throw new BusinessLogicException(ExceptionCode.REPORT_ACCESS_DENIED);
        }
        return ReportResponseDto.from(report);
    }

    //프로젝트별 신고 내역
    @Transactional(readOnly = true)
    public Page<ReportResponseDto> getReportsByProject(Long projectId, Pageable pageable){
        adminUserManagingService.validAdmin();

        return reportRepository.findByProjectId(projectId, pageable)
                .map(report -> ReportResponseDto.from(report));
    }

    /*
    CRUD
     */
    //신고하기
    @Transactional
    public ReportResponseDto createReport(ReportRequestDto requestDto){

        Long userId = getCurrentUserId();
        User reporter = userService.getUserOrThrow(userId);
        Project project = projectService.findProjectById(requestDto.getProjectId());

        //신고자가 해당 프로젝트를 이미 신고했는지 확인
        if(reportRepository.existsByReporterAndProject(reporter, project)){
            throw new BusinessLogicException(ExceptionCode.ALREADY_REPORTED);
        }

        Report report = Report.builder()
                .reporter(reporter)
                .project(project)
                .reportCategory(requestDto.getReportCategory())
                .content(requestDto.getContent())
                .reportStatus(ReportStatus.PENDING)
                .build();
        reportRepository.save(report);

        return ReportResponseDto.from(report);
    }

    //수정
    @Transactional
    public ReportResponseDto updateReport(Long reportId, ReportRequestDto requestDto){
        Long userId = getCurrentUserId();
        Report report = findMyReportOrThrow(reportId, userId);

        if(report.getReportStatus() != ReportStatus.PENDING){
            throw new BusinessLogicException(ExceptionCode.ALREADY_APPROVED);
        }

        report.setReportCategory(requestDto.getReportCategory());
        report.setContent(requestDto.getContent());

        return ReportResponseDto.from(report);
    }

    //삭제
    @Transactional
    public void deleteReport(Long reportId){
        Long userId = getCurrentUserId();
        Report report = findMyReportOrThrow(reportId, userId);

        if(report.getReportStatus() != ReportStatus.PENDING){
            throw new BusinessLogicException(ExceptionCode.ALREADY_APPROVED);
        }

        reportRepository.delete(report);
    }


    /*
    신고 관리
     */

    //신고 승인
    @Transactional
    public void approveReport(Long reportId){
        adminUserManagingService.validAdmin();

        Report report = findReportOrThrow(reportId);

        if(report.getReportStatus() != ReportStatus.PENDING){
            throw new BusinessLogicException(ExceptionCode.ALREADY_APPROVED);
        }
        Project project = report.getProject();

        //프로젝트 신고 승인 중복 불가
        if(reportRepository.existsByProjectAndReportStatus(project, ReportStatus.APPROVED)){
            throw new BusinessLogicException(ExceptionCode.ALREADY_APPROVED);
        }
        //프로젝트 비공개
        project.setProjectStatus(ProjectStatus.REJECTED);

        Long reportedUserId = project.getUser().getId();
        userService.reportUser(reportedUserId);
        report.setReportStatus(ReportStatus.APPROVED);
    }

    //신고 반려
    @Transactional
    public void rejectReport(Long reportId){
        adminUserManagingService.validAdmin();

        Report report = findReportOrThrow(reportId);
        if(report.getReportStatus() != ReportStatus.PENDING){
            throw new BusinessLogicException(ExceptionCode.ALREADY_APPROVED);
        }

        report.setReportStatus(ReportStatus.REJECTED);
    }

    //신고 통계
    public ReportStatsResponseDto getReportStats(){
        long totalCount = reportRepository.count();
        long pendingCount = reportRepository.countByReportStatus(ReportStatus.PENDING);
        long approvedCount = reportRepository.countByReportStatus(ReportStatus.APPROVED);
        long rejectedCount = reportRepository.countByReportStatus(ReportStatus.REJECTED);

        return new ReportStatsResponseDto(totalCount, pendingCount, approvedCount, rejectedCount);
    }


}
