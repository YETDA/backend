package com.funding.backend.domain.qna.service;

import com.funding.backend.domain.project.entity.Project;
import com.funding.backend.domain.project.repository.ProjectRepository;
import com.funding.backend.domain.qna.dto.request.AnswerRequestDto;
import com.funding.backend.domain.qna.dto.request.QnaRequestDto;
import com.funding.backend.domain.qna.dto.response.AnswerResponseDto;
import com.funding.backend.domain.qna.dto.response.QnaResponseDto;
import com.funding.backend.domain.qna.entity.Qna;
import com.funding.backend.domain.qna.repository.QnaRepository;
import com.funding.backend.domain.user.entity.User;
import com.funding.backend.domain.user.repository.UserRepository;
import com.funding.backend.global.exception.BusinessLogicException;
import com.funding.backend.global.exception.ExceptionCode;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

@Service
@RequiredArgsConstructor
public class QnaService {

    private final UserRepository userRepository;
    private final ProjectRepository projectRepository;
    private final QnaRepository qnaRepository;

    /*
    공개/비공개 여부 체크
     */

    //권한 체크
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
    public QnaResponseDto findByQnaId(Long qnaId, Long currentUserId) {
        Qna qna = qnaRepository.findById(qnaId)
                .orElseThrow(() -> new BusinessLogicException(ExceptionCode.QNA_NOT_FOUND));

        QnaResponseDto dto = QnaResponseDto.from(qna);

        return processPrivateContent(dto,qna,currentUserId);
    }

    //전체 QnA 조회
    @Transactional(readOnly = true)
    public Page<QnaResponseDto> findAllQna(Pageable pageable, Long currentUserId){
        Page<Qna> qnaPage = qnaRepository.findAll(pageable);

        return  qnaPage.map(qna -> {
            QnaResponseDto dto = QnaResponseDto.from(qna);
            return processPrivateContent(dto, qna, currentUserId);
        });
    }

    //프로젝트별 QnA 조회
    @Transactional(readOnly = true)
    public Page<QnaResponseDto> findQnaByProjectId(Long projectId, Pageable pageable, Long currentUserId){
        projectRepository.findById(projectId)
                .orElseThrow(() -> new BusinessLogicException(ExceptionCode.PROJECT_NOT_FOUND));

        Page<Qna> qnaPage = qnaRepository.findByProjectId(projectId, pageable);

        return  qnaPage.map(qna -> {
            QnaResponseDto dto = QnaResponseDto.from(qna);
            return processPrivateContent(dto, qna, currentUserId);
        });
    }


    //사용자별 QnA 조회
    @Transactional(readOnly = true)
    public Page<QnaResponseDto> findQnaByUserId(Long userId, Pageable pageable, Long currentUserId){
        userRepository.findById(userId)
                .orElseThrow(()-> new RuntimeException("사용자를 찾을 수 없습니다."));

        Page<Qna> qnaPage = qnaRepository.findByUserId(userId, pageable);

        return  qnaPage.map(qna -> {
            QnaResponseDto dto = QnaResponseDto.from(qna);
            return processPrivateContent(dto, qna, currentUserId);
        });

    }

    //QnA 작성
    @Transactional
    public QnaResponseDto createQna(QnaRequestDto requestDto){
        User user = userRepository.findById(requestDto.getUserId())
                .orElseThrow(() -> new RuntimeException("사용자를 찾을 수 없습니다."));

        Project project = projectRepository.findById(requestDto.getProjectId())
                .orElseThrow(() -> new BusinessLogicException(ExceptionCode.PROJECT_NOT_FOUND));

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
        Qna qna = qnaRepository.findById(qnaId)
                .orElseThrow(() -> new BusinessLogicException(ExceptionCode.QNA_NOT_FOUND));

        if (!qna.getUser().getId().equals(requestDto.getUserId())){
            throw new RuntimeException("수정 권한이 없습니다.");
        }

        qna.setTitle(requestDto.getTitle());
        qna.setContent(requestDto.getContent());
        qna.setVisibility(requestDto.isVisibility());

        return QnaResponseDto.from(qna);
    }

    //QnA 삭제
    @Transactional
    public void deleteQna(Long qnaId, Long userId){
        Qna qna = qnaRepository.findById(qnaId)
                .orElseThrow(() -> new BusinessLogicException(ExceptionCode.QNA_NOT_FOUND));

        if(!qna.getUser().getId().equals(userId)){
            throw new RuntimeException("삭제 권한이 없습니다.");
        }

        qnaRepository.delete(qna);
    }

    /*
    답변 관련 기능
     */

    //답변 작성
    @Transactional
    public AnswerResponseDto createAnswer(Long qnaId, AnswerRequestDto requestDto){
        Qna qna = qnaRepository.findById(qnaId)
                .orElseThrow(() -> new BusinessLogicException(ExceptionCode.QNA_NOT_FOUND));

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
        Qna qna = qnaRepository.findById(qnaId)
                .orElseThrow(() -> new BusinessLogicException(ExceptionCode.QNA_NOT_FOUND));

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
        Qna qna = qnaRepository.findById(qnaId)
                .orElseThrow(() -> new BusinessLogicException(ExceptionCode.QNA_NOT_FOUND));

        if(!StringUtils.hasText(qna.getAnswer())){
            throw new BusinessLogicException(ExceptionCode.ANSWER_NOT_FOUND);
        }

        qna.setAnswer(null);
        qnaRepository.save(qna);
    }
}