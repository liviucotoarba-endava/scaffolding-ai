package com.scaffolding.userserviceapi.controllers;

import com.scaffolding.userserviceapi.dto.UserRequestDto;
import com.scaffolding.userserviceapi.dto.UserResponseDto;
import com.scaffolding.userserviceapi.exceptions.UserNotFoundException;
import com.scaffolding.userserviceapi.services.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.List;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
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
}
