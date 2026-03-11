package com.george.usermanagementapi.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.george.usermanagementapi.dto.request.CreateUserRequest;
import com.george.usermanagementapi.dto.request.PatchUserRequest;
import com.george.usermanagementapi.dto.request.UpdateUserRequest;
import com.george.usermanagementapi.dto.response.UserResponse;
import com.george.usermanagementapi.exception.EmailAlreadyExistsException;
import com.george.usermanagementapi.exception.GlobalExceptionHandler;
import com.george.usermanagementapi.exception.ResourceNotFoundException;
import com.george.usermanagementapi.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.*;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.List;

import static org.hamcrest.Matchers.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Integration tests for {@link UserController} using the Spring MVC test slice.
 *
 * <p>{@code @WebMvcTest} loads only the web layer (controller, filter chain,
 * exception handlers) — the service is replaced with a Mockito mock via
 * {@code @MockBean}, so no database or application context wiring is needed.
 */
@WebMvcTest(UserController.class)
@Import(GlobalExceptionHandler.class)
@DisplayName("UserController")
class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private UserService userService;

    private UserResponse sampleResponse;

    private static final String BASE_URL = "/api/v1/users";

    @BeforeEach
    void setUp() {
        sampleResponse = UserResponse.builder()
                .id(1L)
                .firstName("John")
                .lastName("Doe")
                .email("john.doe@example.com")
                .phoneNumber("+1-555-123-4567")
                .createdAt(LocalDateTime.of(2024, 1, 15, 10, 30))
                .updatedAt(LocalDateTime.of(2024, 1, 15, 10, 30))
                .build();
    }

    // ── POST /api/v1/users ─────────────────────────────────────────────────────

    @Nested
    @DisplayName("POST /api/v1/users")
    class CreateUser {

        @Test
        @DisplayName("returns 201 Created with user body on success")
        void shouldReturn201_whenValid() throws Exception {
            // given
            CreateUserRequest request = CreateUserRequest.builder()
                    .firstName("John")
                    .lastName("Doe")
                    .email("john.doe@example.com")
                    .phoneNumber("+1-555-123-4567")
                    .build();

            given(userService.createUser(any(CreateUserRequest.class))).willReturn(sampleResponse);

            // when / then
            mockMvc.perform(post(BASE_URL)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isCreated())
                    .andExpect(jsonPath("$.id").value(1))
                    .andExpect(jsonPath("$.firstName").value("John"))
                    .andExpect(jsonPath("$.email").value("john.doe@example.com"));
        }

        @Test
        @DisplayName("returns 400 Bad Request when required fields are missing")
        void shouldReturn400_whenFieldsMissing() throws Exception {
            // given — email and lastName are missing
            String body = """
                    { "firstName": "John" }
                    """;

            // when / then
            mockMvc.perform(post(BASE_URL)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(body))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.fieldErrors.email").exists())
                    .andExpect(jsonPath("$.fieldErrors.lastName").exists());
        }

        @Test
        @DisplayName("returns 400 Bad Request when email format is invalid")
        void shouldReturn400_whenEmailInvalid() throws Exception {
            // given
            CreateUserRequest request = CreateUserRequest.builder()
                    .firstName("John")
                    .lastName("Doe")
                    .email("not-an-email")
                    .build();

            // when / then
            mockMvc.perform(post(BASE_URL)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.fieldErrors.email").exists());
        }

        @Test
        @DisplayName("returns 409 Conflict when email is already registered")
        void shouldReturn409_whenEmailConflict() throws Exception {
            // given
            CreateUserRequest request = CreateUserRequest.builder()
                    .firstName("John")
                    .lastName("Doe")
                    .email("taken@example.com")
                    .build();

            given(userService.createUser(any(CreateUserRequest.class)))
                    .willThrow(new EmailAlreadyExistsException("taken@example.com"));

            // when / then
            mockMvc.perform(post(BASE_URL)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isConflict())
                    .andExpect(jsonPath("$.status").value(409))
                    .andExpect(jsonPath("$.message").value(containsString("taken@example.com")));
        }
    }

    // ── GET /api/v1/users ──────────────────────────────────────────────────────

    @Nested
    @DisplayName("GET /api/v1/users")
    class GetAllUsers {

        @Test
        @DisplayName("returns 200 OK with paginated users")
        void shouldReturn200WithPage() throws Exception {
            // given
            Pageable pageable = PageRequest.of(0, 10, Sort.by("createdAt").descending());
            Page<UserResponse> page = new PageImpl<>(List.of(sampleResponse), pageable, 1);

            given(userService.getAllUsers(any(Pageable.class))).willReturn(page);

            // when / then
            mockMvc.perform(get(BASE_URL)
                            .param("page", "0")
                            .param("size", "10"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.content", hasSize(1)))
                    .andExpect(jsonPath("$.content[0].email").value("john.doe@example.com"))
                    .andExpect(jsonPath("$.totalElements").value(1));
        }
    }

    // ── GET /api/v1/users/{id} ─────────────────────────────────────────────────

    @Nested
    @DisplayName("GET /api/v1/users/{id}")
    class GetUserById {

        @Test
        @DisplayName("returns 200 OK with user body when found")
        void shouldReturn200_whenFound() throws Exception {
            // given
            given(userService.getUserById(1L)).willReturn(sampleResponse);

            // when / then
            mockMvc.perform(get(BASE_URL + "/1"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.id").value(1))
                    .andExpect(jsonPath("$.firstName").value("John"));
        }

        @Test
        @DisplayName("returns 404 Not Found when user does not exist")
        void shouldReturn404_whenNotFound() throws Exception {
            // given
            given(userService.getUserById(99L))
                    .willThrow(new ResourceNotFoundException("User", 99L));

            // when / then
            mockMvc.perform(get(BASE_URL + "/99"))
                    .andExpect(status().isNotFound())
                    .andExpect(jsonPath("$.status").value(404))
                    .andExpect(jsonPath("$.message").value(containsString("99")));
        }
    }

    // ── PUT /api/v1/users/{id} ─────────────────────────────────────────────────

    @Nested
    @DisplayName("PUT /api/v1/users/{id}")
    class UpdateUser {

        @Test
        @DisplayName("returns 200 OK with updated user")
        void shouldReturn200_whenValid() throws Exception {
            // given
            UpdateUserRequest request = UpdateUserRequest.builder()
                    .firstName("Jane")
                    .lastName("Smith")
                    .email("jane.smith@example.com")
                    .build();

            given(userService.updateUser(eq(1L), any(UpdateUserRequest.class)))
                    .willReturn(sampleResponse);

            // when / then
            mockMvc.perform(put(BASE_URL + "/1")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.id").value(1));
        }

        @Test
        @DisplayName("returns 404 Not Found when user does not exist")
        void shouldReturn404_whenNotFound() throws Exception {
            // given
            UpdateUserRequest request = UpdateUserRequest.builder()
                    .firstName("Jane").lastName("Smith").email("jane@example.com").build();

            given(userService.updateUser(eq(99L), any(UpdateUserRequest.class)))
                    .willThrow(new ResourceNotFoundException("User", 99L));

            // when / then
            mockMvc.perform(put(BASE_URL + "/99")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isNotFound());
        }
    }

    // ── PATCH /api/v1/users/{id} ───────────────────────────────────────────────

    @Nested
    @DisplayName("PATCH /api/v1/users/{id}")
    class PatchUser {

        @Test
        @DisplayName("returns 200 OK with patched user")
        void shouldReturn200_whenValid() throws Exception {
            // given — only updating firstName
            PatchUserRequest request = PatchUserRequest.builder()
                    .firstName("Updated")
                    .build();

            given(userService.patchUser(eq(1L), any(PatchUserRequest.class)))
                    .willReturn(sampleResponse);

            // when / then
            mockMvc.perform(patch(BASE_URL + "/1")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.id").value(1));
        }

        @Test
        @DisplayName("returns 400 Bad Request when provided email is malformed")
        void shouldReturn400_whenEmailInvalidInPatch() throws Exception {
            // given
            String body = """
                    { "email": "bad-email" }
                    """;

            // when / then
            mockMvc.perform(patch(BASE_URL + "/1")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(body))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.fieldErrors.email").exists());
        }
    }

    // ── DELETE /api/v1/users/{id} ──────────────────────────────────────────────

    @Nested
    @DisplayName("DELETE /api/v1/users/{id}")
    class DeleteUser {

        @Test
        @DisplayName("returns 204 No Content on successful deletion")
        void shouldReturn204_whenDeleted() throws Exception {
            // given
            willDoNothing().given(userService).deleteUser(1L);

            // when / then
            mockMvc.perform(delete(BASE_URL + "/1"))
                    .andExpect(status().isNoContent());

            then(userService).should().deleteUser(1L);
        }

        @Test
        @DisplayName("returns 404 Not Found when user does not exist")
        void shouldReturn404_whenNotFound() throws Exception {
            // given
            willThrow(new ResourceNotFoundException("User", 42L))
                    .given(userService).deleteUser(42L);

            // when / then
            mockMvc.perform(delete(BASE_URL + "/42"))
                    .andExpect(status().isNotFound())
                    .andExpect(jsonPath("$.message").value(containsString("42")));
        }
    }
}
