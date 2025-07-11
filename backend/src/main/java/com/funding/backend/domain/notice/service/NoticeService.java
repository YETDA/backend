package com.funding.backend.domain.notice.service;

import com.funding.backend.domain.notice.dto.request.NoticeCreateRequestDto;
import com.funding.backend.domain.notice.dto.request.NoticeUpdateRequestDto;
import com.funding.backend.domain.notice.dto.response.NoticeReseponseDto;
import com.funding.backend.domain.notice.entity.Notice;
import com.funding.backend.domain.notice.repository.NoticeRepository;
import com.funding.backend.domain.project.entity.Project;
import com.funding.backend.domain.project.service.ProjectService;
import com.funding.backend.domain.user.entity.User;
import com.funding.backend.domain.user.service.UserService;
import com.funding.backend.global.exception.BusinessLogicException;
import com.funding.backend.global.exception.ExceptionCode;
import com.funding.backend.security.jwt.TokenService;
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
    private final UserService userService;
    private final TokenService tokenService;

    /**
     * 공지사항을 생성합니다.
     *
     * @param noticeCreateRequestDto   생성할 공지사항 정보
     * @return 생성된 공지사항 정보
     */
    @Transactional
    public NoticeReseponseDto createNotice(NoticeCreateRequestDto noticeCreateRequestDto) {

        Project project = projectService.findProjectById(noticeCreateRequestDto.getProjectId());
        User loginUser = userService.getUserOrThrow(tokenService.getUserIdFromAccessToken());

        projectService.validProjectUser(project.getUser(), loginUser);

        Notice notice = Notice.builder()
                .title(noticeCreateRequestDto.getTitle())
                .content(noticeCreateRequestDto.getContent())
                .project(project)
                .build();

        Notice savedNotice = noticeRepository.save(notice);

        return new NoticeReseponseDto(savedNotice);
    }

    /**
     * 공지사항을 수정합니다.
     *
     * @param noticeId 공지사항 ID
     * @param noticeUpdateRequestDto   수정할 공지사항 정보
     * @return 수정된 공지사항 정보
     */
    @Transactional
    public NoticeReseponseDto updateNotice(Long noticeId, NoticeUpdateRequestDto noticeUpdateRequestDto) {

        Notice notice = findNoticeById(noticeId);
        User loginUser = userService.getUserOrThrow(tokenService.getUserIdFromAccessToken());

        projectService.validProjectUser(notice.getProject().getUser(), loginUser);

        notice.update(noticeUpdateRequestDto.getTitle(), noticeUpdateRequestDto.getContent());

        return new NoticeReseponseDto(notice);
    }

    @Transactional
    /**
     * 공지사항을 삭제합니다.
     *
     * @param noticeId 공지사항 ID
     */
    public void deleteNotice(Long noticeId) {

        Notice notice = findNoticeById(noticeId);
        User loginUser = userService.getUserOrThrow(tokenService.getUserIdFromAccessToken());

        projectService.validProjectUser(notice.getProject().getUser(), loginUser);

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