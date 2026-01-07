package com.example.projectTest.service;

import com.example.projectTest.dto.UserDto;
import com.example.projectTest.entity.User;
import com.example.projectTest.exception.DuplicateEmailException;
import com.example.projectTest.exception.UserNotFoundException;
import com.example.projectTest.exception.UsersNotFoundException;
import com.example.projectTest.mapper.UserMapper;
import com.example.projectTest.repository.UserRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private UserMapper userMapper;

    @InjectMocks
    private UserService userService;


    // ------------ Тестирование findAll() ------------
    @Nested
    @DisplayName("Тестирование findAll()")
    class FindAllTest {
        @Test
        @DisplayName("Успешный возврат списка пользователей")
        void shouldFindAll() {
            when(userRepository.findAll()).thenReturn(List.of(new User()));
            when(userMapper.toUserDto(any(User.class))).thenReturn(new UserDto());

            List<UserDto> result = userService.findAll();

            assertNotNull(result);
            assertEquals(1, result.size());
            verify(userRepository, times(1)).findAll();
            verify(userMapper, times(1)).toUserDto(any(User.class));
        }

        @Test
        @DisplayName("Нет пользователей → UsersNotFoundException")
        void shouldFindAllNoUsers() {
            when(userRepository.findAll()).thenReturn(List.of());

            assertThrows(UsersNotFoundException.class, () -> userService.findAll());
            verify(userRepository, times(1)).findAll();
        }
    }

    // ------------ Тестирование create() ------------
    @Nested
    @DisplayName("Тестирование create()")
    class CreateTest {

        @Test
        @DisplayName("Успешное создание пользователя")
        void shouldCreate() {
            String validEmail = "test@example.com";
            String validName = "John Doe";
            int validAge = 30;
            UserDto inputUserDto = UserDto.builder()
                    .email(validEmail)
                    .name(validName)
                    .age(validAge)
                    .build();
            User expectedUserEntity = User.builder()
                    .email(validEmail)
                    .name(validName)
                    .age(validAge)
                    .build();
            when(userMapper.toUser(inputUserDto)).thenReturn(expectedUserEntity);
            when(userRepository.save(expectedUserEntity)).thenReturn(expectedUserEntity);
            when(userMapper.toUserDto(expectedUserEntity)).thenReturn(inputUserDto);

            UserDto result = userService.create(inputUserDto);

            assertNotNull(result);
            assertEquals(inputUserDto.getEmail(), result.getEmail());
            verify(userMapper, times(1)).toUser(inputUserDto);
            verify(userRepository, times(1)).save(expectedUserEntity);
            verify(userMapper, times(1)).toUserDto(expectedUserEntity);
        }
    }

    // ---------------------тестирование update------------------------
    @Nested
    @DisplayName("Тестирование update()")
    class UpdateTest {
        @Test
        @DisplayName("Успешное обновление всех полей")
        void shouldUpdateAllFields() {
            Long userId = 1L;
            String newEmail = "updated@example.com";
            String newName = "Updated Name";
            Integer newAge = 25;

            User existingUser = User.builder().id(userId).email("old@example.com").name("Old Name").age(30).build();
            User updatedUser = User.builder().id(userId).email(newEmail).name(newName).age(newAge).build();
            UserDto updatedUserDto = UserDto.builder().email(newEmail).name(newName).age(newAge).build();

            when(userRepository.findById(userId)).thenReturn(Optional.of(existingUser));
            when(userRepository.existsByEmail(newEmail)).thenReturn(false);
            when(userRepository.save(updatedUser)).thenReturn(updatedUser);
            when(userMapper.toUserDto(updatedUser)).thenReturn(updatedUserDto);

            UserDto result = userService.update(userId, newEmail, newName, newAge);

            assertNotNull(result);
            assertEquals(newEmail, result.getEmail());
            assertEquals(newName, result.getName());
            assertEquals(newAge, result.getAge());
            verify(userRepository, times(1)).findById(userId);
            verify(userRepository, times(1)).existsByEmail(newEmail);
            verify(userRepository, times(1)).save(updatedUser);
        }

        @Test
        @DisplayName("Пользователь не найден → UserNotFoundException")
        void shouldThrowUserNotFoundExceptionWhenUpdating() {
            Long nonExistentId = 999L;
            when(userRepository.findById(nonExistentId)).thenReturn(Optional.empty());

            assertThrows(UserNotFoundException.class,
                    () -> userService.update(nonExistentId, "new@example.com", "New Name", 25));
            verify(userRepository, times(1)).findById(nonExistentId);
            verify(userRepository, never()).existsByEmail(anyString());
            verify(userRepository, never()).save(any(User.class));
        }
    }

    // ------------ Тестирование delete() ------------
    @Nested
    @DisplayName("Тестирование delete()")
    class DeleteTest {

        @Test
        @DisplayName("Успешное удаление пользователя")
        void shouldDeleteUser() {
            Long userId = 1L;
            when(userRepository.existsById(userId)).thenReturn(true);

            userService.delete(userId);

            verify(userRepository, times(1)).existsById(userId);
            verify(userRepository, times(1)).deleteById(userId);
        }

        @Test
        @DisplayName("Пользователь не найден при удалении → UserNotFoundException")
        void shouldThrowUserNotFoundExceptionWhenDeleting() {
            Long nonExistentId = 999L;
            when(userRepository.existsById(nonExistentId)).thenReturn(false);

            assertThrows(UserNotFoundException.class, () -> userService.delete(nonExistentId));
            verify(userRepository, times(1)).existsById(nonExistentId);
            verify(userRepository, never()).deleteById(anyLong());
        }
    }

    // ------------ Тестирование findById() ------------
    @Nested
    @DisplayName("Тестирование findById()")
    class FindByIdTest {

        @Test
        @DisplayName("Успешный поиск пользователя по Id")
        void shouldFindById() {
            Long userId = 1L;
            User user = User.builder().id(userId).email("test@example.com").name("John").age(25).build();
            UserDto userDto = UserDto.builder().email("test@example.com").name("John").age(25).build();

            when(userRepository.findById(userId)).thenReturn(Optional.of(user));
            when(userMapper.toUserDto(user)).thenReturn(userDto);

            UserDto result = userService.findById(userId);

            assertNotNull(result);
            assertEquals("test@example.com", result.getEmail());
            verify(userRepository, times(1)).findById(userId);
            verify(userMapper, times(1)).toUserDto(user);
        }

        @Test
        @DisplayName("Пользователь не найден по Id → UserNotFoundException")
        void shouldThrowUserNotFoundExceptionWhenFindingById() {
            Long nonExistentId = 999L;
            when(userRepository.findById(nonExistentId)).thenReturn(Optional.empty());

            assertThrows(UserNotFoundException.class, () -> userService.findById(nonExistentId));
            verify(userRepository, times(1)).findById(nonExistentId);
        }
    }

    // ------------ Тестирование findByEmail() ------------
    @Nested
    @DisplayName("Тестирование findByEmail()")
    class FindByEmailTest {

        @Test
        @DisplayName("Успешный поиск пользователя по Email")
        void shouldFindByEmail() {
            String email = "test@example.com";
            User user = User.builder().id(1L).email(email).name("John").age(25).build();
            UserDto userDto = UserDto.builder().email(email).name("John").age(25).build();

            when(userRepository.findByEmail(email)).thenReturn(Optional.of(user));
            when(userMapper.toUserDto(user)).thenReturn(userDto);

            UserDto result = userService.findByEmail(email);

            assertNotNull(result);
            assertEquals(email, result.getEmail());
            verify(userRepository, times(1)).findByEmail(email);
            verify(userMapper, times(1)).toUserDto(user);
        }

        @Test
        @DisplayName("Пользователь не найден по Email → UserNotFoundException")
        void shouldThrowUserNotFoundExceptionWhenFindingByEmail() {
            String nonExistentEmail = "nonexistent@example.com";
            when(userRepository.findByEmail(nonExistentEmail)).thenReturn(Optional.empty());

            assertThrows(UserNotFoundException.class, () -> userService.findByEmail(nonExistentEmail));
            verify(userRepository, times(1)).findByEmail(nonExistentEmail);
        }
    }

    // ------------ Тестирование checkUniqueEmail() ------------
    @Nested
    @DisplayName("Тестирование checkUniqueEmail()")
    class CheckUniqueEmailTest {

        @Test
        @DisplayName("Email уникален → исключение не выбрасывается")
        void shouldNotThrowWhenEmailIsUnique() {
            String uniqueEmail = "unique@example.com";
            when(userRepository.existsByEmail(uniqueEmail)).thenReturn(false);

            // Метод не должен выбрасывать исключение
            assertDoesNotThrow(() -> userService.checkUniqueEmail(uniqueEmail));
            verify(userRepository, times(1)).existsByEmail(uniqueEmail);
        }

        @Test
        @DisplayName("Дублирующийся email → DuplicateEmailException")
        void shouldThrowDuplicateEmailExceptionWhenEmailExists() {
            String duplicateEmail = "existing@example.com";
            when(userRepository.existsByEmail(duplicateEmail)).thenReturn(true);

            assertThrows(DuplicateEmailException.class, () -> userService.checkUniqueEmail(duplicateEmail));
            verify(userRepository, times(1)).existsByEmail(duplicateEmail);
        }
    }
}