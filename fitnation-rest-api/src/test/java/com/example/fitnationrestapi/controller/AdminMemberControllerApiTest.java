package com.example.fitnationrestapi.controller;

import com.example.fitnationcommon.dto.response.MemberListResponse;
import com.example.fitnationcommon.dto.response.PagedResponse;
import com.example.fitnationrestapi.exception.GlobalExceptionHandler;
import com.example.fitnationuser.service.AdminMemberService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;
import java.util.List;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.isNull;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class AdminMemberControllerApiTest {

    @Mock
    private AdminMemberService adminMemberService;

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        LocalValidatorFactoryBean validator = new LocalValidatorFactoryBean();
        validator.afterPropertiesSet();
        mockMvc = MockMvcBuilders.standaloneSetup(new AdminMemberController(adminMemberService))
                .setControllerAdvice(new GlobalExceptionHandler())
                .setValidator(validator)
                .build();
    }

    @Test
    void getMembers_defaultParams_returns200() throws Exception {
        PagedResponse<MemberListResponse> response = PagedResponse.<MemberListResponse>builder()
                .items(List.of())
                .page(0).size(20).totalElements(0).totalPages(0).hasNext(false)
                .build();

        when(adminMemberService.getMembers(any(), any(), any(), isNull(), isNull()))
                .thenReturn(response);

        mockMvc.perform(get("/api/admin/members"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.page").value(0))
                .andExpect(jsonPath("$.size").value(20))
                .andExpect(jsonPath("$.totalElements").value(0));
    }

    @Test
    void getMembers_invalidSortField_returns400() throws Exception {
        when(adminMemberService.getMembers(any(), any(), any(), any(), any()))
                .thenThrow(new IllegalArgumentException("Invalid sort field 'badField'"));

        mockMvc.perform(get("/api/admin/members")
                        .param("sort", "badField,desc"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("INVALID_ARGUMENT"));
    }

    @Test
    void getMembers_withQ_passesSearchToService() throws Exception {
        MemberListResponse item = MemberListResponse.builder()
                .id(1L).email("jon@test.com").firstName("Jon").lastName("Doe")
                .userStatus("ACTIVE").build();

        PagedResponse<MemberListResponse> response = PagedResponse.<MemberListResponse>builder()
                .items(List.of(item))
                .page(0).size(20).totalElements(1).totalPages(1).hasNext(false)
                .build();

        when(adminMemberService.getMembers(any(), any(), any(), eq("jon"), isNull()))
                .thenReturn(response);

        mockMvc.perform(get("/api/admin/members").param("q", "jon"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalElements").value(1))
                .andExpect(jsonPath("$.items[0].email").value("jon@test.com"));
    }
}