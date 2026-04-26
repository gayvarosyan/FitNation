package com.example.fitnationprogress.mapper;

import com.example.fitnationprogress.dto.CreateUserProgressEntryRequest;
import com.example.fitnationprogress.model.UserProgressEntry;
import com.example.fitnationuser.user.User;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;

class UserProgressMapperTest {

    private final UserProgressMapper mapper = new UserProgressMapper();

    @Test
    void toEntity_mapsAllFields() {
        User user = User.builder().id(15L).build();
        CreateUserProgressEntryRequest request = new CreateUserProgressEntryRequest(
                LocalDateTime.of(2026, 4, 20, 10, 0),
                BigDecimal.valueOf(81.2),
                BigDecimal.valueOf(15.2),
                BigDecimal.valueOf(41.3),
                BigDecimal.valueOf(78.1),
                BigDecimal.valueOf(98.2),
                BigDecimal.valueOf(96.3),
                "progress");

        UserProgressEntry entry = mapper.toEntity(user, request);

        assertEquals(15L, entry.getUser().getId());
        assertEquals(request.recordedAt(), entry.getRecordedAt());
        assertEquals(request.weight(), entry.getWeight());
        assertEquals(request.notes(), entry.getNotes());
    }
}
