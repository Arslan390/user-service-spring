package com.example.projectTest.controller;

import com.example.projectTest.dto.UserDto;
import com.example.projectTest.exception.DuplicateEmailException;
import com.example.projectTest.exception.UserNotFoundException;
import com.example.projectTest.exception.UsersNotFoundException;
import com.example.projectTest.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import tools.jackson.databind.ObjectMapper;

import java.time.Instant;
import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(UserController.class)
public class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private UserService userService;

    private UserDto testUserDto;

    @BeforeEach
    void setUp() {
        testUserDto = UserDto.builder()
                .id(1L)
                .email("test@example.com")
                .name("Arslan")
                .age(25)
                .createdAt(Instant.now())
                .build();
    }

    //------------Вернуть всех пользователей---------------
    @Nested
    @DisplayName("Тестирование GET findAll")
    class FindAllTest {

        @Test
        @DisplayName("Успешный возврат списка пользователей")
        void shouldFindAll() throws Exception {
            List<UserDto> users = List.of(testUserDto);
            when(userService.findAll()).thenReturn(users);

            mockMvc.perform(get("/api/users"))
                    .andDo(print())
                    .andExpect(status().isOk())
                    .andExpect(content().contentType("application/json"))
                    .andExpect(jsonPath("$").isArray())
                    .andExpect(jsonPath("$[0].id").value(testUserDto.getId()))
                    .andExpect(jsonPath("$[0].email").value(testUserDto.getEmail()))
                    .andExpect(jsonPath("$[0].name").value(testUserDto.getName()));
        }

        @Test
        @DisplayName("Нет пользователей → 404")
        void shouldFindAllNoUsers() throws Exception {

            when(userService.findAll()).thenThrow(new UsersNotFoundException());

            mockMvc.perform(get("/api/users"))
                    .andDo(print())
                    .andExpect(status().isNotFound())
                    .andExpect(content().contentType("application/json"))
                    .andExpect(jsonPath("$.status").value(404))
                    .andExpect(jsonPath("$.error").value("NOT_FOUND"))
                    .andExpect(jsonPath("$.message").value("Список пользователей пуст"));
        }
    }

    //------------Поиск пользователей по id----------------
    @Nested
    @DisplayName("Тестирование GET /api/users/{id}")
    class FindByIdTest {

        @Test
        @DisplayName("Успешный поиск пользователя по ID")
        void shouldFindById() throws Exception {
            when(userService.findById(1L)).thenReturn(testUserDto);

            mockMvc.perform(get("/api/users/1"))
                    .andDo(print())
                    .andExpect(status().isOk())
                    .andExpect(content().contentType("application/json"))
                    .andExpect(jsonPath("$.id").value(testUserDto.getId()))
                    .andExpect(jsonPath("$.email").value(testUserDto.getEmail()))
                    .andExpect(jsonPath("$.name").value(testUserDto.getName()));
        }

        @Test
        @DisplayName("Пользователь не найден → 404")
        void shouldFindByIdNotFound() throws Exception {
            when(userService.findById(999L)).thenThrow(new UserNotFoundException(String.format("Пользователь с id %d не найден.", 999L)));

            mockMvc.perform(get("/api/users/999"))
                    .andDo(print())
                    .andExpect(status().isNotFound())
                    .andExpect(content().contentType("application/json"))
                    .andExpect(jsonPath("$.status").value(404))
                    .andExpect(jsonPath("$.error").value("NOT_FOUND"))
                    .andExpect(jsonPath("$.message").value("Пользователь с id 999 не найден."));
        }
    }

    //-----------Поиск пользователей по Email--------------
    @Nested
    @DisplayName("Тестирование GET /api/users/findByEmail")
    class FindByEmailTest {

        @Test
        @DisplayName("Успешный поиск пользователя по email")
        void shouldFindByEmail() throws Exception {
            when(userService.findByEmail("test@example.com")).thenReturn(testUserDto);

            mockMvc.perform(get("/api/users/findByEmail")
                            .param("email", "test@example.com"))
                    .andDo(print())
                    .andExpect(status().isOk())
                    .andExpect(content().contentType("application/json"))
                    .andExpect(jsonPath("$.id").value(testUserDto.getId()))
                    .andExpect(jsonPath("$.email").value(testUserDto.getEmail()))
                    .andExpect(jsonPath("$.name").value(testUserDto.getName()));
        }

        @Test
        @DisplayName("Пользователь с email не найден → 404")
        void shouldFindByEmailNotFound() throws Exception {
            when(userService.findByEmail("nonexistent@example.com")).thenThrow(new UserNotFoundException("Пользователь с email nonexistent@example.com не найден."));

            mockMvc.perform(get("/api/users/findByEmail")
                            .param("email", "nonexistent@example.com"))
                    .andDo(print())
                    .andExpect(status().isNotFound())
                    .andExpect(content().contentType("application/json"))
                    .andExpect(jsonPath("$.status").value(404))
                    .andExpect(jsonPath("$.error").value("NOT_FOUND"))
                    .andExpect(jsonPath("$.message").value("Пользователь с email nonexistent@example.com не найден."));
        }
    }

    //-------------Создание пользоватея--------------------
    @Nested
    @DisplayName("Тестирование POST /api/users")
    class CreateTest {

        @Test
        @DisplayName("Успешное создание пользователя")
        void shouldCreate() throws Exception {
            when(userService.create(any(UserDto.class))).thenReturn(testUserDto);

            mockMvc.perform(post("/api/users")
                            .contentType("application/json")
                            .content(objectMapper.writeValueAsString(new UserDto())))
                    .andDo(print())
                    .andExpect(status().isCreated())
                    .andExpect(content().contentType("application/json"))
                    .andExpect(jsonPath("$.id").value(testUserDto.getId()));
        }

        @Test
        @DisplayName("Дублирующийся email → 409")
        void shouldCreateDuplicateEmail() throws Exception {
            when(userService.create(any(UserDto.class)))
                    .thenThrow(new DuplicateEmailException());

            mockMvc.perform(post("/api/users")
                            .contentType("application/json")
                            .content(objectMapper.writeValueAsString(new UserDto())))
                    .andDo(print())
                    .andExpect(status().isConflict())
                    .andExpect(jsonPath("$.status").value(409))
                    .andExpect(jsonPath("$.error").value("CONFLICT"))
                    .andExpect(jsonPath("$.message").value("Email уже существует"));
        }

        @Test
        @DisplayName("Невалидные данные → 400")
        void shouldCreateInValidateData() throws Exception {
            when(userService.create(any(UserDto.class)))
                    .thenThrow(new IllegalArgumentException("Недопустимый формат электронной почты"));

            mockMvc.perform(post("/api/users")
                            .contentType("application/json")
                            .content(objectMapper.writeValueAsString(new UserDto())))
                    .andDo(print())
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.status").value(400))
                    .andExpect(jsonPath("$.error").value("BAD_REQUEST"))
                    .andExpect(jsonPath("$.message").value("Недопустимый формат электронной почты"));
        }

    }

    //-------------Обновление пользователя-----------------
    @Nested
    @DisplayName("Тестирование PUT /api/users/{id}")
    class UpdateTest {

        @Test
        @DisplayName("Успешное обновление всех полей")
        void shouldUpdateAllFields() throws Exception {
            UserDto updatedUser = testUserDto;
            updatedUser.setName("Updated Name");
            updatedUser.setEmail("updated@example.com");
            updatedUser.setAge(45);
            when(userService.update(eq(1L), eq("updated@example.com"), eq("Updated Name"), eq(45)))
                    .thenReturn(updatedUser);

            mockMvc.perform(put("/api/users/1")
                            .param("email", "updated@example.com")
                            .param("name", "Updated Name")
                            .param("age", "45"))
                    .andDo(print())
                    .andExpect(status().isOk())
                    .andExpect(content().contentType("application/json"))
                    .andExpect(jsonPath("$.id").value(1L))
                    .andExpect(jsonPath("$.name").value("Updated Name"))
                    .andExpect(jsonPath("$.email").value("updated@example.com"))
                    .andExpect(jsonPath("$.age").value(45));
        }

        @Test
        @DisplayName("Обновление только имени")
        void shouldUpdateOnlyName() throws Exception {
            UserDto updatedUser = testUserDto;
            updatedUser.setName("Changed Name");
            when(userService.update(eq(1L), isNull(), eq("Changed Name"), isNull()))
                    .thenReturn(updatedUser);

            mockMvc.perform(put("/api/users/1")
                            .param("name", "Changed Name"))
                    .andDo(print())
                    .andExpect(status().isOk())
                    .andExpect(content().contentType("application/json"))
                    .andExpect(jsonPath("$.name").value("Changed Name"));
        }

        @Test
        @DisplayName("Пользователь не найден → 404")
        void shouldUpdateNotFound() throws Exception {
            when(userService.update(eq(999L), isNull(), anyString(), isNull()))
                    .thenThrow(new UserNotFoundException(String.format("Пользователь с id %d не найден.", 999L)));

            mockMvc.perform(put("/api/users/999")
                            .param("name", "New Name"))
                    .andDo(print())
                    .andExpect(status().isNotFound())
                    .andExpect(jsonPath("$.status").value(404))
                    .andExpect(jsonPath("$.message").value("Пользователь с id 999 не найден."));
        }

        @Test
        @DisplayName("Дублирующийся email → 409")
        void shouldUpdateDuplicateEmail() throws Exception {
            when(userService.update(anyLong(), eq("duplicate@example.com"), isNull(), isNull()))
                    .thenThrow(new DuplicateEmailException());

            mockMvc.perform(put("/api/users/1")
                            .param("email", "duplicate@example.com"))
                    .andDo(print())
                    .andExpect(status().isConflict())
                    .andExpect(jsonPath("$.status").value(409))
                    .andExpect(jsonPath("$.message").value("Email уже существует"));
        }
    }

    // --------------Удаление пользователя----------------
    @Nested
    @DisplayName("Тестирование DELETE /api/users/{id}")
    class DeleteTest {

        @Test
        @DisplayName("Успешное удаление пользователя")
        void shouldDelete() throws Exception {
            doNothing().when(userService).delete(1L);

            mockMvc.perform(delete("/api/users/1"))
                    .andDo(print())
                    .andExpect(status().isNoContent())
                    .andExpect(content().string(""));
        }

        @Test
        @DisplayName("Удаление несуществующего пользователя → 404")
        void shouldDeleteNotFound() throws Exception {
            doThrow(new UserNotFoundException("Пользователь с id 999 не найден.")).when(userService).delete(999L);

            mockMvc.perform(delete("/api/users/999"))
                    .andDo(print())
                    .andExpect(status().isNotFound())
                    .andExpect(jsonPath("$.status").value(404))
                    .andExpect(jsonPath("$.error").value("NOT_FOUND"))
                    .andExpect(jsonPath("$.message").value("Пользователь с id 999 не найден."));
        }

        @Test
        @DisplayName("Внутренняя ошибка сервера → 500")
        void shouldDeleteInternalError() throws Exception {
            doThrow(new RuntimeException("Database error")).when(userService).delete(2L);

            mockMvc.perform(delete("/api/users/2"))
                    .andDo(print())
                    .andExpect(status().isInternalServerError())
                    .andExpect(jsonPath("$.status").value(500));
        }
    }
}
