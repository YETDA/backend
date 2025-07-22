package com.funding.backend.domain.alarm.policy;


import com.funding.backend.domain.alarm.enums.AlarmType;
import com.funding.backend.enums.RoleType;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class NotificationPolicy {

    public static List<AlarmType> getAllowedTypesByRole(RoleType role) {
        return switch (role) {
//            case ADMIN -> Arrays.asList(
//                    NotificationType.NEW_MANAGEMENT_DASHBOARD
//            );
            case ADMIN -> Arrays.asList(
                    AlarmType.PURCHASE_PROJECT_REQUEST

            );
            case USER -> Arrays.asList(
                    AlarmType.PROJECT_PURCHASED,
                    AlarmType.PROJECT_PURCHASE_SUCCESS

            );
            default -> Collections.emptyList();
        };
    }
}
