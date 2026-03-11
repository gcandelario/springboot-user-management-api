package com.george.usermanagementapi.service;

import com.george.usermanagementapi.dto.request.CreateUserRequest;
import com.george.usermanagementapi.dto.request.PatchUserRequest;
import com.george.usermanagementapi.dto.request.UpdateUserRequest;
import com.george.usermanagementapi.dto.response.UserResponse;
import com.george.usermanagementapi.entity.User;
import com.george.usermanagementapi.exception.EmailAlreadyExistsException;
import com.george.usermanagementapi.exception.ResourceNotFoundException;
import com.george.usermanagementapi.mapper.UserMapper;
import com.george.usermanagementapi.repository.UserRepository;
import com.george.usermanagementapi.service.impl.UserServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.*;

/**
 * Unit tests for {@link UserServiceImpl}.
 *
 * <p>All collaborators ({@link UserRepository}, {@link UserMapper}) are mocked
 * with Mockito so each test exercises the service logic in complete isolation.
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("UserServiceImpl")
class UserServiceImplTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private UserMapper userMapper;

    @InjectMocks
    private UserServiceImpl userService;

    // ── Shared fixtures ────────────────────────────────────────────────────────

    private User user;
    private UserResponse userResponse;

    @BeforeEach
    void setUp() {
        user = User.builder()
                .id(1L)
                .firstName("John")
                .lastName("Doe")
                .email("john.doe@example.com")
                .phoneNumber("+1-555-123-4567")
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        userResponse = UserResponse.builder()
                .id(1L)
                .firstName("John")
                .lastName("Doe")
                .email("john.doe@example.com")
                .phoneNumber("+1-555-123-4567")
                .createdAt(user.getCreatedAt())
                .updatedAt(user.getUpdatedAt())
                .build();
    }

    // ── createUser ─────────────────────────────────────────────────────────────

    @Nested
    @DisplayName("createUser")
    class CreateUser {

        @Test
        @DisplayName("returns saved user response when email is unique")
        void shouldCreateUser_whenEmailIsUnique() {
            // given
            CreateUserRequest request = CreateUserRequest.builder()
                    .firstName("John")
                    .lastName("Doe")
                    .email("john.doe@example.com")
                    .build();

            given(userRepository.existsByEmail(request.getEmail())).willReturn(false);
            given(userMapper.toEntity(request)).willReturn(user);
            given(userRepository.save(user)).willReturn(user);
            given(userMapper.toResponse(user)).willReturn(userResponse);

            // when
            UserResponse result = userService.createUser(request);

            // then
            assertThat(result).isNotNull();
            assertThat(result.getId()).isEqualTo(1L);
            assertThat(result.getEmail()).isEqualTo("john.doe@example.com");

            then(userRepository).should().existsByEmail(request.getEmail());
            then(userRepository).should().save(user);
        }

        @Test
        @DisplayName("throws EmailAlreadyExistsException when email is taken")
        void shouldThrow_whenEmailAlreadyExists() {
            // given
            CreateUserRequest request = CreateUserRequest.builder()
                    .firstName("John")
                    .lastName("Doe")
                    .email("existing@example.com")
                    .build();

            given(userRepository.existsByEmail(request.getEmail())).willReturn(true);

            // when / then
            assertThatThrownBy(() -> userService.createUser(request))
                    .isInstanceOf(EmailAlreadyExistsException.class)
                    .hasMessageContaining("existing@example.com");

            then(userRepository).should(never()).save(any());
        }
    }

    // ── getAllUsers ────────────────────────────────────────────────────────────

    @Nested
    @DisplayName("getAllUsers")
    class GetAllUsers {

        @Test
        @DisplayName("returns mapped page of users")
        void shouldReturnPagedUsers() {
            // given
            Pageable pageable = PageRequest.of(0, 10, Sort.by("createdAt").descending());
            Page<User> userPage = new PageImpl<>(List.of(user), pageable, 1);

            given(userRepository.findAll(pageable)).willReturn(userPage);
            given(userMapper.toResponse(user)).willReturn(userResponse);

            // when
            Page<UserResponse> result = userService.getAllUsers(pageable);

            // then
            assertThat(result).isNotNull();
            assertThat(result.getTotalElements()).isEqualTo(1);
            assertThat(result.getContent().get(0).getEmail()).isEqualTo("john.doe@example.com");
        }

        @Test
        @DisplayName("returns empty page when no users exist")
        void shouldReturnEmptyPage_whenNoUsersExist() {
            // given
            Pageable pageable = PageRequest.of(0, 10);
            given(userRepository.findAll(pageable)).willReturn(Page.empty(pageable));

            // when
            Page<UserResponse> result = userService.getAllUsers(pageable);

            // then
            assertThat(result.getContent()).isEmpty();
            assertThat(result.getTotalElements()).isZero();
        }
    }

    // ── getUserById ───────────────────────────────────────────────────────────

    @Nested
    @DisplayName("getUserById")
    class GetUserById {

        @Test
        @DisplayName("returns user response when user exists")
        void shouldReturnUser_whenFound() {
            // given
            given(userRepository.findById(1L)).willReturn(Optional.of(user));
            given(userMapper.toResponse(user)).willReturn(userResponse);

            // when
            UserResponse result = userService.getUserById(1L);

            // then
            assertThat(result.getId()).isEqualTo(1L);
            assertThat(result.getFirstName()).isEqualTo("John");
        }

        @Test
        @DisplayName("throws ResourceNotFoundException when user does not exist")
        void shouldThrow_whenUserNotFound() {
            // given
            given(userRepository.findById(99L)).willReturn(Optional.empty());

            // when / then
            assertThatThrownBy(() -> userService.getUserById(99L))
                    .isInstanceOf(ResourceNotFoundException.class)
                    .hasMessageContaining("99");
        }
    }

    // ── updateUser ────────────────────────────────────────────────────────────

    @Nested
    @DisplayName("updateUser")
    class UpdateUser {

        @Test
        @DisplayName("returns updated user when data is valid")
        void shouldUpdateUser_whenValid() {
            // given
            UpdateUserRequest request = UpdateUserRequest.builder()
                    .firstName("Jane")
                    .lastName("Smith")
                    .email("jane.smith@example.com")
                    .build();

            given(userRepository.findById(1L)).willReturn(Optional.of(user));
            given(userRepository.existsByEmailAndIdNot(request.getEmail(), 1L)).willReturn(false);
            given(userRepository.save(user)).willReturn(user);
            given(userMapper.toResponse(user)).willReturn(userResponse);

            // when
            UserResponse result = userService.updateUser(1L, request);

            // then
            assertThat(result).isNotNull();
            then(userMapper).should().updateEntity(user, request);
            then(userRepository).should().save(user);
        }

        @Test
        @DisplayName("throws ResourceNotFoundException when user not found")
        void shouldThrow_whenUserNotFound() {
            // given
            UpdateUserRequest request = UpdateUserRequest.builder()
                    .firstName("Jane").lastName("Smith").email("jane@example.com").build();

            given(userRepository.findById(99L)).willReturn(Optional.empty());

            // when / then
            assertThatThrownBy(() -> userService.updateUser(99L, request))
                    .isInstanceOf(ResourceNotFoundException.class);
        }

        @Test
        @DisplayName("throws EmailAlreadyExistsException when new email belongs to another user")
        void shouldThrow_whenEmailTakenByAnotherUser() {
            // given
            UpdateUserRequest request = UpdateUserRequest.builder()
                    .firstName("John").lastName("Doe").email("taken@example.com").build();

            given(userRepository.findById(1L)).willReturn(Optional.of(user));
            given(userRepository.existsByEmailAndIdNot("taken@example.com", 1L)).willReturn(true);

            // when / then
            assertThatThrownBy(() -> userService.updateUser(1L, request))
                    .isInstanceOf(EmailAlreadyExistsException.class)
                    .hasMessageContaining("taken@example.com");

            then(userRepository).should(never()).save(any());
        }
    }

    // ── patchUser ─────────────────────────────────────────────────────────────

    @Nested
    @DisplayName("patchUser")
    class PatchUser {

        @Test
        @DisplayName("applies only the provided fields")
        void shouldPatchUser_withPartialFields() {
            // given
            PatchUserRequest request = PatchUserRequest.builder()
                    .firstName("Updated")
                    .build();  // lastName, email, phoneNumber are null

            given(userRepository.findById(1L)).willReturn(Optional.of(user));
            given(userRepository.save(user)).willReturn(user);
            given(userMapper.toResponse(user)).willReturn(userResponse);

            // when
            UserResponse result = userService.patchUser(1L, request);

            // then
            assertThat(result).isNotNull();
            then(userMapper).should().patchEntity(user, request);
            // email check should be skipped because request.getEmail() is null
            then(userRepository).should(never()).existsByEmailAndIdNot(any(), any());
        }

        @Test
        @DisplayName("checks email uniqueness when a new email is provided")
        void shouldCheckEmailUniqueness_whenEmailIsInPatch() {
            // given
            PatchUserRequest request = PatchUserRequest.builder()
                    .email("new@example.com")
                    .build();

            given(userRepository.findById(1L)).willReturn(Optional.of(user));
            given(userRepository.existsByEmailAndIdNot("new@example.com", 1L)).willReturn(false);
            given(userRepository.save(user)).willReturn(user);
            given(userMapper.toResponse(user)).willReturn(userResponse);

            // when
            userService.patchUser(1L, request);

            // then
            then(userRepository).should().existsByEmailAndIdNot("new@example.com", 1L);
        }

        @Test
        @DisplayName("throws ResourceNotFoundException when user not found")
        void shouldThrow_whenUserNotFound() {
            // given
            PatchUserRequest request = PatchUserRequest.builder().firstName("X").build();
            given(userRepository.findById(99L)).willReturn(Optional.empty());

            // when / then
            assertThatThrownBy(() -> userService.patchUser(99L, request))
                    .isInstanceOf(ResourceNotFoundException.class);
        }
    }

    // ── deleteUser ────────────────────────────────────────────────────────────

    @Nested
    @DisplayName("deleteUser")
    class DeleteUser {

        @Test
        @DisplayName("deletes the user when they exist")
        void shouldDeleteUser_whenExists() {
            // given
            given(userRepository.findById(1L)).willReturn(Optional.of(user));

            // when
            userService.deleteUser(1L);

            // then
            then(userRepository).should().delete(user);
        }

        @Test
        @DisplayName("throws ResourceNotFoundException when user does not exist")
        void shouldThrow_whenUserNotFound() {
            // given
            given(userRepository.findById(42L)).willReturn(Optional.empty());

            // when / then
            assertThatThrownBy(() -> userService.deleteUser(42L))
                    .isInstanceOf(ResourceNotFoundException.class)
                    .hasMessageContaining("42");

            then(userRepository).should(never()).delete(any());
        }
    }
}
