package com.funding.backend.domain.project.dto.response;

import lombok.Getter;
import lombok.Setter;

/**
 * 프로젝트 상세 응답의 공통 부모 인터페이스
 */
@Getter
@Setter
public abstract class ProjectResponseDto {
    Long projectId;
    Long viewCount;
}
