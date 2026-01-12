package com.example.projectTest.service;

import com.example.projectTest.dto.CreateUserDto;
import com.example.projectTest.dto.UpdateUserDto;
import com.example.projectTest.dto.UserDto;
import com.example.projectTest.entity.User;
import com.example.projectTest.exception.DuplicateEmailException;
import com.example.projectTest.exception.EmptyListException;
import com.example.projectTest.exception.UserNotFoundException;
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
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private UserMapper userMapper;

    @InjectMocks
    private UserService userService;

    //------------Тестирование findAll------------------
    @Nested
    @DisplayName("Тестирование findAll")
    class FindAllTests {
        @Test
        @DisplayName("Успешный возврат списка пользователей.")
        void shouldFindAll() {
            User user = User.builder()
                    .id(1L)
                    .email("test@mail.ru")
                    .name("Test")
                    .age(25)
                    .build();
            UserDto userDto = UserDto.builder()
                    .id(1L)
                    .email("test@mail.ru")
                    .name("Test")
                    .age(25)
                    .build();
            when(userRepository.findAll()).thenReturn(List.of(user));
            when(userMapper.toUserDto(user)).thenReturn(userDto);

            List<UserDto> result = userService.findAll();

            assertNotNull(result);
            assertFalse(result.isEmpty());
            assertEquals(1, result.size());
            verify(userRepository, times(1)).findAll();
            verify(userMapper, times(1)).toUserDto(any(User.class));
        }

        @Test
        @DisplayName("Нет пользователей -> EmptyListException")
        void shouldNotFindAll() {
            when(userRepository.findAll()).thenReturn(List.of());

            assertThrows(EmptyListException.class, () -> userService.findAll());
            verify(userRepository, times(1)).findAll();
            verifyNoInteractions(userMapper);
        }
    }

    //-------------Тестирование findById-------------------
    @Nested
    @DisplayName("Тестирование findById")
    class FindByIdTests {

        @Test
        @DisplayName("Успешный поиск пользователя по Id")
        void shouldFindById() {
            User user = User.builder()
                    .id(1L)
                    .email("test@mail.ru")
                    .name("Test")
                    .age(25)
                    .build();
            UserDto userDto = UserDto.builder()
                    .id(1L)
                    .email("test@mail.ru")
                    .name("Test")
                    .age(25)
                    .build();

            when(userRepository.findById(1L)).thenReturn(Optional.of(user));
            when(userMapper.toUserDto(user)).thenReturn(userDto);

            UserDto result = userService.findById(1L);

            assertNotNull(result);
            assertEquals(userDto, result);
            verify(userRepository, times(1)).findById(1L);
            verify(userMapper, times(1)).toUserDto(user);
        }

        @Test
        @DisplayName("Пользователь не найден по Id")
        void shouldNotFindById() {
            when(userRepository.findById(1L)).thenReturn(Optional.empty());

            assertThrows(UserNotFoundException.class, () -> userService.findById(1L));
            verify(userRepository, times(1)).findById(1L);
            verifyNoInteractions(userMapper);
        }
    }

    //--------------Тестирование findByEmail---------------
    @Nested
    @DisplayName("Тестирование findByEmail")
    class FindByEmailTests {

        @Test
        @DisplayName("Успешный поиск пользователя по Email")
        void shouldFindByEmail() {
            User user = User.builder()
                    .id(1L)
                    .email("test@mail.ru")
                    .name("Test")
                    .age(25)
                    .build();
            UserDto userDto = UserDto.builder()
                    .id(1L)
                    .email("test@mail.ru")
                    .name("Test")
                    .age(25)
                    .build();

            when(userRepository.findByEmail("test@mail.ru")).thenReturn(Optional.of(user));
            when(userMapper.toUserDto(user)).thenReturn(userDto);

            UserDto result = userService.findByEmail("test@mail.ru");

            assertNotNull(result);
            assertEquals(userDto, result);
            verify(userRepository, times(1)).findByEmail("test@mail.ru");
            verify(userMapper, times(1)).toUserDto(user);
        }

        @Test
        @DisplayName("Пользователь не найден по Email")
        void shouldNotFindByEmail() {
            when(userRepository.findByEmail("test@mail.ru")).thenReturn(Optional.empty());

            assertThrows(UserNotFoundException.class, () -> userService.findByEmail("test@mail.ru"));
            verify(userRepository, times(1)).findByEmail("test@mail.ru");
            verifyNoInteractions(userMapper);
        }
    }

    //-----------------Тестирование create--------------------
    @Nested
    @DisplayName("Тестирование create")
    class CreateTests {

        @Test
        @DisplayName("Успешное создание пользователя.")
        void shouldCreate() {
            CreateUserDto createUserDto = CreateUserDto.builder()
                    .email("test@mail.ru")
                    .name("Test")
                    .age(25)
                    .build();
            User user = User.builder()
                    .id(1L)
                    .email("test@mail.ru")
                    .name("Test")
                    .age(25)
                    .build();
            UserDto userDto = UserDto.builder()
                    .id(1L)
                    .email("test@mail.ru")
                    .name("Test")
                    .age(25)
                    .build();
            when(userMapper.toEntity(createUserDto)).thenReturn(user);
            when(userRepository.save(user)).thenReturn(user);
            when(userMapper.toUserDto(user)).thenReturn(userDto);

            UserDto result = userService.create(createUserDto);

            assertNotNull(result);
            assertEquals(userDto, result);
            verify(userMapper, times(1)).toEntity(createUserDto);
            verify(userRepository, times(1)).save(user);
            verify(userMapper, times(1)).toUserDto(user);
        }

        @Test
        @DisplayName("Ошибка при создании пользователя")
        void shouldNotCreate() {
            CreateUserDto createUserDto = CreateUserDto.builder()
                    .email("test@mail.ru")
                    .name("Test")
                    .age(25)
                    .build();
            User user = User.builder()
                    .id(1L)
                    .email("test@mail.ru")
                    .name("Test")
                    .age(25)
                    .build();
            UserDto userDto = UserDto.builder()
                    .id(1L)
                    .email("test@mail.ru")
                    .name("Test")
                    .age(25)
                    .build();

            when(userMapper.toEntity(createUserDto)).thenReturn(user);
            when(userRepository.save(user)).thenThrow(DuplicateEmailException.class);

            assertThrows(DuplicateEmailException.class, () -> userService.create(createUserDto));
            verify(userMapper, times(1)).toEntity(createUserDto);
            verify(userRepository, times(1)).save(user);
        }
    }

    //----------------Тестирование delete-------------------
    @Nested
    @DisplayName("Тестирование delete")
    class DeleteTests {

        @Test
        void shouldDelete() {
            userService.delete(1L);
            verify(userRepository, times(1)).deleteById(1L);
        }
    }

    //-----------------Тестирование update-------------------
    @Nested
    @DisplayName("Тестирование update")
    class UpdateTests {

        @Test
        @DisplayName("Успешное обновление пользователя")
        void shouldUpdate() {
            Long id = 1L;
            UpdateUserDto updateUserDto = UpdateUserDto.builder()
                    .email("test@mail.ru")
                    .name("Test")
                    .age(25)
                    .build();
            User user = User.builder()
                    .id(1L)
                    .email("test@mail.ru")
                    .name("Test")
                    .age(25)
                    .build();
            UserDto userDto = UserDto.builder()
                    .id(1L)
                    .email("test@mail.ru")
                    .name("Test")
                    .age(25)
                    .build();

            when(userRepository.findById(id)).thenReturn(Optional.of(user));
            when(userRepository.save(user)).thenReturn(user);
            when(userMapper.toUserDto(user)).thenReturn(userDto);

            UserDto result = userService.update(id, updateUserDto);

            assertNotNull(result);
            assertEquals(userDto, result);
            verify(userRepository, times(1)).findById(id);
            verify(userRepository, times(1)).save(user);
            verify(userMapper, times(1)).toUserDto(user);
        }

        @Test
        @DisplayName("Ошибка пользователь не найден")
        void shouldThrowUserNotFoundExceptionWhenUpdating() {
            Long id = 1L;

            when(userRepository.findById(id)).thenReturn(Optional.empty());

            assertThrows(UserNotFoundException.class, () -> userService.update(id, UpdateUserDto.builder().build()));
            verify(userRepository, times(1)).findById(id);
            verify(userRepository, never()).save(any());
            verifyNoInteractions(userMapper);
        }

        @Test
        @DisplayName("Ошибка Email существует")
        void shouldTrowDuplicateEmailExceptionWhenUpdating() {
            Long id = 1L;
            UpdateUserDto updateUserDto = UpdateUserDto.builder()
                    .email("test@mail.ru")
                    .name("Test")
                    .age(25)
                    .build();
            User user = User.builder()
                    .id(1L)
                    .email("test@mail.ru")
                    .name("Test")
                    .age(25)
                    .build();

            when(userRepository.findById(id)).thenReturn(Optional.of(user));
            when(userRepository.save(user)).thenThrow(DuplicateEmailException.class);

            assertThrows(DuplicateEmailException.class, () -> userService.update(id, updateUserDto));
            verify(userRepository, times(1)).findById(id);
            verify(userRepository, times(1)).save(user);
            verifyNoInteractions(userMapper);
        }
    }
}