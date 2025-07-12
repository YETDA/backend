package com.funding.backend.domain.projectSubCategory.dto.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ProjectSubRequestDto {

  private Long subjectCategoryId;
  private String subjectCategoryName;
}
