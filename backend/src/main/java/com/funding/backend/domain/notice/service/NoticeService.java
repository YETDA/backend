package com.funding.backend.domain.notice.service;

import com.funding.backend.domain.notice.dto.request.NoticeCreateRequestDto;
import com.funding.backend.domain.notice.dto.response.NoticeReseponseDto;
import com.funding.backend.domain.notice.entity.Notice;
import com.funding.backend.domain.notice.repository.NoticeRepository;
import com.funding.backend.domain.project.entity.Project;
import com.funding.backend.domain.project.service.ProjectService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
     * @param noticeCreateRequestDto   공지사항 생성 요청용 DTO
     * @return 생성된 공지사항 응답용 DTO
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

}