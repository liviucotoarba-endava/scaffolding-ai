package com.scaffolding.userserviceapi;

import com.scaffolding.userserviceapi.entities.User;
import com.scaffolding.userserviceapi.repos.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(properties = "spring.datasource.url=jdbc:h2:mem:users-integration;DB_CLOSE_DELAY=-1")
@AutoConfigureMockMvc
class UserApiIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserRepository userRepository;

    @BeforeEach
    void clearDatabase() {
        userRepository.deleteAll();
    }

    @Test
    void postCreatesUserInDatabaseAndReturns201() throws Exception {
        var result = mockMvc.perform(post("/api/users").contentType(MediaType.APPLICATION_JSON).content("""
                        {"name":"Ada Lovelace","email":"ada@example.com"}
                        """))
                .andExpect(status().isCreated())
                .andExpect(header().exists("Location"))
                .andExpect(jsonPath("$.id").isNumber())
                .andExpect(jsonPath("$.name").value("Ada Lovelace"))
                .andExpect(jsonPath("$.email").value("ada@example.com"))
                .andReturn();

        var users = userRepository.findAll();
        assertThat(users).hasSize(1);
        User user = users.getFirst();
        assertThat(user.getName()).isEqualTo("Ada Lovelace");
        assertThat(user.getEmail()).isEqualTo("ada@example.com");
        assertThat(result.getResponse().getHeader("Location")).isEqualTo("http://localhost/api/users/" + user.getId());
        jsonPath("$.id").value(user.getId().intValue()).match(result);
    }

    @Test
    void getListsPersistedUsers() throws Exception {
        User user = saveUser();

        mockMvc.perform(get("/api/users"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].id").value(user.getId().intValue()))
                .andExpect(jsonPath("$[0].name").value(user.getName()))
                .andExpect(jsonPath("$[0].email").value(user.getEmail()));
    }

    @Test
    void getByIdReturnsPersistedUser() throws Exception {
        User user = saveUser();

        mockMvc.perform(get("/api/users/{id}", user.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(user.getId().intValue()))
                .andExpect(jsonPath("$.name").value(user.getName()))
                .andExpect(jsonPath("$.email").value(user.getEmail()));
    }

    @Test
    void putReplacesPersistedFieldsWithoutChangingId() throws Exception {
        User user = saveUser();

        mockMvc.perform(put("/api/users/{id}", user.getId()).contentType(MediaType.APPLICATION_JSON).content("""
                        {"name":"Grace Hopper","email":"grace@example.com"}
                        """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(user.getId().intValue()))
                .andExpect(jsonPath("$.name").value("Grace Hopper"))
                .andExpect(jsonPath("$.email").value("grace@example.com"));

        User updatedUser = userRepository.findById(user.getId()).orElseThrow();
        assertThat(updatedUser.getName()).isEqualTo("Grace Hopper");
        assertThat(updatedUser.getEmail()).isEqualTo("grace@example.com");
        assertThat(userRepository.count()).isEqualTo(1L);
    }

    @Test
    void deleteRemovesPersistedUserAndReturns204() throws Exception {
        User user = saveUser();

        mockMvc.perform(delete("/api/users/{id}", user.getId()))
                .andExpect(status().isNoContent())
                .andExpect(content().string(""));

        assertThat(userRepository.existsById(user.getId())).isFalse();
        assertThat(userRepository.count()).isZero();
    }

    @ParameterizedTest
    @MethodSource("validPatches")
    void patchPersistsSelectedFieldsAndReturnsUpdatedUser(String json, String name, String email) throws Exception {
        User user = saveUser();

        mockMvc.perform(patch("/api/users/{id}", user.getId()).contentType(MediaType.APPLICATION_JSON).content(json))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(user.getId().intValue()))
                .andExpect(jsonPath("$.name").value(name))
                .andExpect(jsonPath("$.email").value(email));

        User saved = userRepository.findById(user.getId()).orElseThrow();
        assertThat(saved.getName()).isEqualTo(name);
        assertThat(saved.getEmail()).isEqualTo(email);
        assertThat(userRepository.count()).isEqualTo(1);
        mockMvc.perform(get("/api/users/{id}", user.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value(name))
                .andExpect(jsonPath("$.email").value(email));
    }

    @ParameterizedTest
    @MethodSource("invalidPatches")
    void patchRejectsInvalidValuesWithoutChangingPersistedUser(String json) throws Exception {
        User user = saveUser();

        mockMvc.perform(patch("/api/users/{id}", user.getId()).contentType(MediaType.APPLICATION_JSON).content(json))
                .andExpect(status().isBadRequest());

        User saved = userRepository.findById(user.getId()).orElseThrow();
        assertThat(saved.getName()).isEqualTo(user.getName());
        assertThat(saved.getEmail()).isEqualTo(user.getEmail());
        assertThat(userRepository.count()).isEqualTo(1);
    }

    @Test
    void patchMissingUserReturns404WithoutCreatingUser() throws Exception {
        mockMvc.perform(patch("/api/users/999999").contentType(MediaType.APPLICATION_JSON)
                        .content("{\"name\":\"Grace Hopper\"}"))
                .andExpect(status().isNotFound())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_PROBLEM_JSON))
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.detail").value("User with id 999999 was not found"));

        assertThat(userRepository.count()).isZero();
    }

    private static Stream<Arguments> validPatches() {
        return Stream.of(
                Arguments.of("{\"name\":\"Grace Hopper\"}", "Grace Hopper", "ada@example.com"),
                Arguments.of("{\"email\":\"grace@example.com\"}", "Ada Lovelace", "grace@example.com"),
                Arguments.of("{\"name\":\"Grace Hopper\",\"email\":\"grace@example.com\"}",
                        "Grace Hopper", "grace@example.com"),
                Arguments.of("{}", "Ada Lovelace", "ada@example.com")
        );
    }

    private static Stream<String> invalidPatches() {
        return Stream.of(
                "{\"name\":null}",
                "{\"email\":null}",
                "{\"name\":\" \"}",
                "{\"email\":\" \"}",
                "{\"email\":\"invalid-email\"}",
                "{\"name\":\"" + "a".repeat(101) + "\"}",
                "{\"email\":\"" + "a".repeat(255) + "\"}",
                "{\"name\":\"Grace Hopper\",\"email\":null}",
                "{\"name\":null,\"email\":\"grace@example.com\"}",
                "{\"name\":\"Grace Hopper\",\"email\":\"invalid-email\"}"
        );
    }

    private User saveUser() {
        User user = new User();
        user.setName("Ada Lovelace");
        user.setEmail("ada@example.com");
        return userRepository.save(user);
    }
}
