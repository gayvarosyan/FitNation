package com.example.fitnationcommon.event;

import com.example.fitnationcommon.enums.UserRole;
import lombok.Getter;
import org.springframework.context.ApplicationEvent;

@Getter
public class UserSoftDeletedEvent extends ApplicationEvent {

    private final Long     userId;
    private final UserRole role;
    private final Long     assignedTrainerId;
    private final Long     assignedNutritionPlanId;

    public UserSoftDeletedEvent(Object source,
                                Long userId,
                                UserRole role,
                                Long assignedTrainerId,
                                Long assignedNutritionPlanId) {
        super(source);
        this.userId                 = userId;
        this.role                   = role;
        this.assignedTrainerId      = assignedTrainerId;
        this.assignedNutritionPlanId = assignedNutritionPlanId;
    }
}