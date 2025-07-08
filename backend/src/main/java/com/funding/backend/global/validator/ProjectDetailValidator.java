package com.funding.backend.global.validator;

import com.funding.backend.domain.project.dto.request.ProjectCreateRequestDto;
import com.funding.backend.enums.ProjectType;
import com.funding.backend.global.validator.annotaion.ValidProjectDetail;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class ProjectDetailValidator implements ConstraintValidator<ValidProjectDetail, ProjectCreateRequestDto> {
    @Override
    public boolean isValid(ProjectCreateRequestDto dto, ConstraintValidatorContext context) {
        if (dto.getProjectType() == ProjectType.PURCHASE) {
            if (dto.getPurchaseDetail() == null) {
                context.disableDefaultConstraintViolation();
                context.buildConstraintViolationWithTemplate("구매형 프로젝트 상세 정보는 필수입니다.")
                        .addPropertyNode("purchaseDetail")
                        .addConstraintViolation();
                return false;
            }
        } else if (dto.getProjectType() == ProjectType.DONATION) {
            if (dto.getDonationDetail() == null) {
                context.disableDefaultConstraintViolation();
                context.buildConstraintViolationWithTemplate("기부형 프로젝트 상세 정보는 필수입니다.")
                        .addPropertyNode("donationDetail")
                        .addConstraintViolation();
                return false;
            }
        }
        return true;
    }
}
