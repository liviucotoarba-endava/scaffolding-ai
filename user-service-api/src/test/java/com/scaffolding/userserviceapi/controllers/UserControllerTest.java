package com.scaffolding.userserviceapi.controllers;

import com.scaffolding.userserviceapi.dto.UserPatchRequestDto;
import com.scaffolding.userserviceapi.dto.UserRequestDto;
import com.scaffolding.userserviceapi.dto.UserResponseDto;
import com.scaffolding.userserviceapi.exceptions.UserNotFoundException;
import com.scaffolding.userserviceapi.services.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class UserControllerTest {

    private static final UserResponseDto RESPONSE = new UserResponseDto(1L, "Ada Lovelace", "ada@example.com");
    private static final String REQUEST_JSON = """
            {"name":"Ada Lovelace","email":"ada@example.com"}
            """;

    @Mock
    private UserService userService;

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(new UserController(userService))
                .setControllerAdvice(new ApiExceptionHandler())
                .build();
    }

    @Test
    void findAllReturnsUserDtos() throws Exception {
        when(userService.findAll()).thenReturn(List.of(RESPONSE));

        mockMvc.perform(get("/api/users"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].name").value(RESPONSE.name()))
                .andExpect(jsonPath("$[0].email").value(RESPONSE.email()));
    }

    @Test
    void findByIdReturnsUserDto() throws Exception {
        when(userService.findById(1L)).thenReturn(RESPONSE);

        mockMvc.perform(get("/api/users/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value(RESPONSE.name()))
                .andExpect(jsonPath("$.email").value(RESPONSE.email()));
    }

    @Test
    void createReturns201WithDtoAndLocation() throws Exception {
        UserRequestDto request = new UserRequestDto(RESPONSE.name(), RESPONSE.email());
        when(userService.create(request)).thenReturn(RESPONSE);

        mockMvc.perform(post("/api/users").contentType(MediaType.APPLICATION_JSON).content(REQUEST_JSON))
                .andExpect(status().isCreated())
                .andExpect(header().string("Location", "http://localhost/api/users/1"))
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value(RESPONSE.name()))
                .andExpect(jsonPath("$.email").value(RESPONSE.email()));
    }

    @Test
    void updateReturnsUpdatedDto() throws Exception {
        UserRequestDto request = new UserRequestDto("Grace Hopper", "grace@example.com");
        when(userService.update(1L, request))
                .thenReturn(new UserResponseDto(1L, request.name(), request.email()));

        mockMvc.perform(put("/api/users/1").contentType(MediaType.APPLICATION_JSON).content("""
                        {"name":"Grace Hopper","email":"grace@example.com"}
                        """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value(request.name()))
                .andExpect(jsonPath("$.email").value(request.email()));
    }

    @Test
    void deleteReturns204WithoutBody() throws Exception {
        mockMvc.perform(delete("/api/users/1"))
                .andExpect(status().isNoContent())
                .andExpect(content().string(""));

        verify(userService).delete(1L);
    }

    @Test
    void findByIdReturns404WhenUserDoesNotExist() throws Exception {
        when(userService.findById(99L)).thenThrow(new UserNotFoundException(99L));

        mockMvc.perform(get("/api/users/99"))
                .andExpect(status().isNotFound())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_PROBLEM_JSON))
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.detail").value("User with id 99 was not found"));
    }

    @Test
    void createRejectsInvalidEmailWithoutCallingService() throws Exception {
        mockMvc.perform(post("/api/users").contentType(MediaType.APPLICATION_JSON).content("""
                        {"name":"Ada Lovelace","email":"invalid-email"}
                        """))
                .andExpect(status().isBadRequest());

        verifyNoInteractions(userService);
    }

    @Test
    void updateRejectsMissingNameWithoutCallingService() throws Exception {
        mockMvc.perform(put("/api/users/1").contentType(MediaType.APPLICATION_JSON).content("""
                        {"email":"ada@example.com"}
                        """))
                .andExpect(status().isBadRequest());

        verifyNoInteractions(userService);
    }

    @Test
    void patchReturnsUpdatedDtoAndPassesOnlySuppliedFields() throws Exception {
        when(userService.patch(eq(1L), any(UserPatchRequestDto.class)))
                .thenReturn(new UserResponseDto(1L, "Grace Hopper", "ada@example.com"));

        mockMvc.perform(patch("/api/users/1").contentType(MediaType.APPLICATION_JSON)
                        .content("{\"name\":\"Grace Hopper\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("Grace Hopper"))
                .andExpect(jsonPath("$.email").value("ada@example.com"));

        verify(userService).patch(eq(1L), argThat(request -> "Grace Hopper".equals(request.getName())
                && request.getEmail() == null));
    }

    @ParameterizedTest
    @ValueSource(strings = {"{\"name\":null}", "{\"email\":null}", "{\"name\":\" \"}",
            "{\"email\":\"bad-email\"}"})
    void patchRejectsInvalidFieldsWithoutCallingService(String json) throws Exception {
        mockMvc.perform(patch("/api/users/1").contentType(MediaType.APPLICATION_JSON).content(json))
                .andExpect(status().isBadRequest());

        verifyNoInteractions(userService);
    }

    @Test
    void patchReturns404WhenUserDoesNotExist() throws Exception {
        when(userService.patch(eq(99L), any(UserPatchRequestDto.class)))
                .thenThrow(new UserNotFoundException(99L));

        mockMvc.perform(patch("/api/users/99").contentType(MediaType.APPLICATION_JSON)
                        .content("{\"email\":\"grace@example.com\"}"))
                .andExpect(status().isNotFound())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_PROBLEM_JSON))
                .andExpect(jsonPath("$.detail").value("User with id 99 was not found"));
    }
}
