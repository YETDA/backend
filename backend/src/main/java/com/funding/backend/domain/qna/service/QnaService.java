package com.funding.backend.domain.qna.service;

import com.funding.backend.domain.project.entity.Project;
import com.funding.backend.domain.project.service.ProjectService;
import com.funding.backend.domain.qna.dto.request.AnswerRequestDto;
import com.funding.backend.domain.qna.dto.request.QnaRequestDto;
import com.funding.backend.domain.qna.dto.response.AnswerResponseDto;
import com.funding.backend.domain.qna.dto.response.QnaResponseDto;
import com.funding.backend.domain.qna.entity.Qna;
import com.funding.backend.domain.qna.repository.QnaRepository;
import com.funding.backend.domain.user.entity.User;
import com.funding.backend.domain.user.service.UserService;
import com.funding.backend.global.exception.BusinessLogicException;
import com.funding.backend.global.exception.ExceptionCode;
import com.funding.backend.security.jwt.TokenService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

@Service
@RequiredArgsConstructor
public class QnaService {

    private final UserService userService;
    private final ProjectService projectService;
    private final QnaRepository qnaRepository;
    private final TokenService tokenService;

    // 사용자 ID 조회

    //QnA 조회 메서드
    private Qna findQnaById(Long qnaId){
        return  qnaRepository.findById(qnaId)
                .orElseThrow(()->new BusinessLogicException(ExceptionCode.QNA_NOT_FOUND));
    }

    //현재 사용자 id 추출
    private Long getCurrentUserId(){
        try {
            return  tokenService.getUserIdFromAccessToken();
        }catch (BusinessLogicException e){
                return  null;
        }
    }

    //권한 체크(공개/비공개 여부 확인)
    private boolean privateQna(Qna qna, Long currentUserId){
        if (currentUserId == null){
            return false;
        }

        //QnA 작성자 또는 프로젝트 작성자만 true
        return qna.getUser().getId().equals(currentUserId) ||
                qna.getProject().getUser().getId().equals(currentUserId);
    }

    //비공개 처리를 위한 DTO 후처리
    private QnaResponseDto processPrivateContent(QnaResponseDto dto, Qna qna, Long currentUserId){

        boolean canView = qna.isVisibility() || privateQna(qna, currentUserId);

        if (!canView) {
            return QnaResponseDto.builder()
                    .id(dto.getId())
                    .title("비공개 질문입니다.")
                    .content("")
                    .projectId(dto.getProjectId())
                    .userId(dto.getUserId())
                    .visibility(dto.isVisibility())
                    .createdAt(dto.getCreatedAt())
                    .modifiedAt(dto.getModifiedAt())
                    .build();
        }
        return dto;
    }


    /*
    QnA 관련 기능
     */

    //QnA 상세 조회
    @Transactional(readOnly = true)
    public QnaResponseDto findByQnaId(Long qnaId) {
        Qna qna = findQnaById(qnaId);
        Long currentUserId = getCurrentUserId();

        QnaResponseDto dto = QnaResponseDto.from(qna);

        return processPrivateContent(dto,qna,currentUserId);
    }

    //전체 QnA 조회
    @Transactional(readOnly = true)
    public Page<QnaResponseDto> findAllQna(Pageable pageable){
        Page<Qna> qnaPage = qnaRepository.findAll(pageable);
        Long currentUserId = getCurrentUserId();

        return  qnaPage.map(qna -> {
            QnaResponseDto dto = QnaResponseDto.from(qna);
            return processPrivateContent(dto,qna, currentUserId);
        });

    }

    //프로젝트별 QnA 조회
    @Transactional(readOnly = true)
    public Page<QnaResponseDto> findQnaByProjectId(Long projectId, Pageable pageable){
        projectService.findProjectById(projectId);


        Page<Qna> qnaPage = qnaRepository.findByProjectId(projectId, pageable);
        Long currentUserId = getCurrentUserId();

        return qnaPage.map(qna -> {QnaResponseDto dto = QnaResponseDto.from(qna);
            return  processPrivateContent(dto,qna,currentUserId);});
    }


    //사용자별 QnA 조회
    @Transactional(readOnly = true)
    public Page<QnaResponseDto> findQnaByUserId(Long userId, Pageable pageable){
       userService.getUserOrThrow(userId);

        Page<Qna> qnaPage = qnaRepository.findByUserId(userId, pageable);
        Long currentUserId = getCurrentUserId();

        return qnaPage.map(qna -> {QnaResponseDto dto = QnaResponseDto.from(qna);
            return  processPrivateContent(dto,qna,currentUserId);});
    }

    //QnA 작성
    @Transactional
    public QnaResponseDto createQna(QnaRequestDto requestDto){
        Long currentUserId = getCurrentUserId();

        if(currentUserId == null){
            throw  new BusinessLogicException(ExceptionCode.ACCESS_TOKEN_NOT_FOUND);
        }

        User user = userService.getUserOrThrow(currentUserId);

        Project project =projectService.findProjectById(requestDto.getProjectId());

        Qna qna = Qna.builder()
                .title(requestDto.getTitle())
                .content(requestDto.getContent())
                .visibility(requestDto.isVisibility())
                .user(user)
                .project(project)
                .build();

        return QnaResponseDto.from(qnaRepository.save(qna));
    }

    //QnA 수정
    @Transactional
    public QnaResponseDto updateQna(Long qnaId, QnaRequestDto requestDto){
        Long currentUserId = getCurrentUserId();

        if(currentUserId == null){
            throw  new BusinessLogicException(ExceptionCode.ACCESS_TOKEN_NOT_FOUND);
        }

        Qna qna = findQnaById(qnaId);

        if (!qna.getUser().getId().equals(currentUserId)){
            throw new BusinessLogicException(ExceptionCode.QNA_ACESS_DENIED);
        }

        qna.setTitle(requestDto.getTitle());
        qna.setContent(requestDto.getContent());
        qna.setVisibility(requestDto.isVisibility());

        return QnaResponseDto.from(qna);
    }

    //QnA 삭제
    @Transactional
    public void deleteQna(Long qnaId){
        Long currentUserId = getCurrentUserId();

        if(currentUserId == null){
            throw  new BusinessLogicException(ExceptionCode.ACCESS_TOKEN_NOT_FOUND);
        }

        Qna qna = findQnaById(qnaId);

        if(!qna.getUser().getId().equals(currentUserId)){
            throw new BusinessLogicException(ExceptionCode.QNA_ACESS_DENIED);
        }

        qnaRepository.delete(qna);
    }

    /*
    답변 관련 기능
     */

    //답변 작성
    @Transactional
    public AnswerResponseDto createAnswer(Long qnaId, AnswerRequestDto requestDto){
        Qna qna = findQnaById(qnaId);

        if(StringUtils.hasText(qna.getAnswer())){
            throw new BusinessLogicException(ExceptionCode.ANSWER_ALREADY_EXISTS);
        }

        qna.setAnswer(requestDto.getAnswer());
        Qna save = qnaRepository.save(qna);
        return AnswerResponseDto.from(save);
    }

    //답변 수정
    @Transactional
    public AnswerResponseDto updateAnswer(Long qnaId, AnswerRequestDto requestDto){
        Qna qna = findQnaById(qnaId);

        if(!StringUtils.hasText(qna.getAnswer())){
            throw new BusinessLogicException(ExceptionCode.ANSWER_NOT_FOUND);
        }

        qna.setAnswer(requestDto.getAnswer());
        Qna save = qnaRepository.save(qna);
        return AnswerResponseDto.from(save);
    }

    //답변 삭제(answer필드 null 처리)
    @Transactional
    public void deleteAnswer(Long qnaId){
        Qna qna = findQnaById(qnaId);

        if(!StringUtils.hasText(qna.getAnswer())){
            throw new BusinessLogicException(ExceptionCode.ANSWER_NOT_FOUND);
        }

        qna.setAnswer(null);
        qnaRepository.save(qna);
    }
}