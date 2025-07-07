package com.funding.backend.domain.notice.service;

import com.funding.backend.domain.notice.dto.request.NoticeCreateRequestDto;
import com.funding.backend.domain.notice.dto.request.NoticeUpdateRequestDto;
import com.funding.backend.domain.notice.dto.response.NoticeReseponseDto;
import com.funding.backend.domain.notice.entity.Notice;
import com.funding.backend.domain.notice.repository.NoticeRepository;
import com.funding.backend.domain.project.entity.Project;
import com.funding.backend.domain.project.service.ProjectService;
import com.funding.backend.global.exception.BusinessLogicException;
import com.funding.backend.global.exception.ExceptionCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class NoticeService {

    private final NoticeRepository noticeRepository;
    private final ProjectService projectService;

    // TODO: 추후 회원 기능 개발 시 주석 해제
    /**
     * 공지사항을 생성합니다.
     *
     * @param loginUserId 로그인한 사용자 ID (회원 기능 개발 시 주석 해제)
     * @param projectId 프로젝트 ID
     * @param noticeCreateRequestDto   생성할 공지사항 정보
     * @return 생성된 공지사항 정보
     */
    @Transactional
    public NoticeReseponseDto createNotice(/* Long loginUserId, */Long projectId, NoticeCreateRequestDto noticeCreateRequestDto) {

        Project project = projectService.findProjectById(projectId);
//        User loginUser = userService.findUserById(loginUserId);

//        projectService.validProjectUser(project.getUser(), loginUser);

        Notice notice = Notice.builder()
                .title(noticeCreateRequestDto.getTitle())
                .content(noticeCreateRequestDto.getContent())
                .project(project)
                .build();

        Notice savedNotice = noticeRepository.save(notice);

        return new NoticeReseponseDto(savedNotice);
    }

    // TODO: 추후 회원 기능 개발 시 주석 해제
    /**
     * 공지사항을 수정합니다.
     *
     * @param loginUserId 로그인한 사용자 ID (회원 기능 개발 시 주석 해제)
     * @param noticeId 공지사항 ID
     * @param noticeUpdateRequestDto   수정할 공지사항 정보
     * @return 수정된 공지사항 정보
     */
    @Transactional
    public NoticeReseponseDto updateNotice(/* Long loginUserId, */Long noticeId, NoticeUpdateRequestDto noticeUpdateRequestDto) {

        Notice notice = findNoticeById(noticeId);
//        User loginUser = userService.findUserById(loginUserId);

//        projectService.validProjectUser(notice.getProject().getUser(), loginUser);

        notice.update(noticeUpdateRequestDto.getTitle(), noticeUpdateRequestDto.getContent());

        return new NoticeReseponseDto(notice);
    }

    @Transactional
    /**
     * 공지사항을 삭제합니다.
     *
     * @param noticeId 공지사항 ID
     */
    public void deleteNotice(/* Long loginUserId, */Long noticeId) {

//        User loginUser = userService.findUserById(loginUserId);
//
//        projectService.validProjectUser(notice.getProject().getUser(), loginUser);

        Notice notice = findNoticeById(noticeId);
        noticeRepository.delete(notice);
    }

    /**
     * 특정 프로젝트의 모든 공지사항을 조회합니다.
     *
     * @param projectId 프로젝트 ID
     * @return 해당 프로젝트의 공지사항 목록
     */
    public List<NoticeReseponseDto> findNoticesByProjectId(Long projectId) {
        Project project = projectService.findProjectById(projectId);
        List<Notice> notices = noticeRepository.findByProjectOrderByCreatedAtDesc(project);
        return notices.stream()
                .map(NoticeReseponseDto::new)
                .toList();
    }

    /**
     * 공지사항 ID를 기준으로 공지사항을 조회합니다.
     *
     * @param noticeId 공지사항 ID
     * @return 조회된 공지사항 엔티티
     */
    public Notice findNoticeById(Long noticeId) {
        return noticeRepository.findById(noticeId)
                .orElseThrow(() -> new BusinessLogicException(ExceptionCode.NOTICE_NOT_FOUND));
    }

}