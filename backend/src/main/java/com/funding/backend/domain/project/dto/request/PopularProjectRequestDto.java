package com.funding.backend.domain.project.dto.request;

import com.funding.backend.enums.PopularProjectSortType;
import com.funding.backend.enums.ProjectTypeFilter;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class PopularProjectRequestDto {

    @Schema(description = "프로젝트 타입", example = "PURCHASE")
    private ProjectTypeFilter projectType;

    @Schema(description = "정렬 기준", example = "LIKE")
    private PopularProjectSortType sortType;
}
