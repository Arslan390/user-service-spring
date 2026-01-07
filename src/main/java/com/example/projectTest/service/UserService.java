package com.example.projectTest.service;

import com.example.projectTest.dto.UserDto;
import com.example.projectTest.entity.User;
import com.example.projectTest.exception.DuplicateEmailException;
import com.example.projectTest.exception.UserNotFoundException;
import com.example.projectTest.exception.UsersNotFoundException;
import com.example.projectTest.mapper.UserMapper;
import com.example.projectTest.repository.UserRepository;
import com.example.projectTest.util.ValidationUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
@Slf4j
@Transactional
@Service
public class UserService {

    private final UserRepository userRepository;

    private final UserMapper userMapper;

    //---------------Получить всех пользователей------------------
    public List<UserDto> findAll() {
        log.info("Запустился метод получения всех пользователей в UserService");
        List<User> users = userRepository.findAll();
        if (users.isEmpty()) {
            log.error("Метод findAll вернул пустой список");
            throw new UsersNotFoundException();
        }
        return users.stream().map(userMapper ::toUserDto).toList();
    }

    //------------Создать нового пользователя----------------------
    public UserDto create(UserDto createUser) {
        log.info("Запустился метод создания нового пользователя в UserService");
        ValidationUtils.validateEmail(createUser.getEmail());
        ValidationUtils.validateName(createUser.getName());
        ValidationUtils.validateAge(createUser.getAge());
        checkUniqueEmail(createUser.getEmail());
        log.info("Email уникален, выполняется создание пользователя...");
        User newUser = userMapper.toUser(createUser);
        userRepository.save(newUser);
        log.info("Пользователь {} успешно создан.", newUser);
        return userMapper.toUserDto(newUser);
    }

    //-------Редактировать данные существующего пользователя--------
    public UserDto update(Long id, String email, String name, Integer age) {
        log.info("Запустился метод редактирования данных пользователя в UserService");
        log.info("Проверяем есть ли в базе пользователь с переданым Id = {}.",id);
        Optional<User> optionalUser = userRepository.findById(id);
        if (optionalUser.isEmpty()) {
            log.error("Пользователь с Id = {} не найден.", id);
            throw new UserNotFoundException(String.format("Пользователь с id %d не найден.", id));
        }
        log.info("Пользователь с Id = {} найден, начинаем перезапись данных...", id);
        User user = optionalUser.get();
        if (email != null && !email.equals(user.getEmail())) {
            log.info("Проверяем валидность и уникальность Email {}", email);
            ValidationUtils.validateEmail(email);
            checkUniqueEmail(email);
            user.setEmail(email);
        }
        if (name != null && !name.equals(user.getName())) {
            log.info("Проверяем валидность Имени {}", name);
            ValidationUtils.validateName(name);
            user.setName(name);
        }
        if (age != null && !age.equals(user.getAge())) {
            log.info("Проверяем валидность возроста {}", age);
            ValidationUtils.validateAge(age);
            user.setAge(age);
        }
        log.info("Сохраняем новые данные.");
        userRepository.save(user);
        return userMapper.toUserDto(user);
    }

    //--------------------Удалить пользователя---------------------
    public void delete(Long id) {
        log.info("Запустился метод удаления пользователя с Id {} в UserService", id);
        if (!userRepository.existsById(id)) {
            log.error("Пользователь для удаления с Id = {} не найден.", id);
            throw new UserNotFoundException(String.format("Пользователь с id %d не найден.", id));
        }
        log.info("Выполняем удаление пользователя с Id {}", id);
        userRepository.deleteById(id);
    }

    //---------------метод для проверки уникальности email--------------------
    public void checkUniqueEmail(String email) {
        log.info("Запустился метод проверки уникальности Email");
        if (userRepository.existsByEmail(email)) {
            log.error("Email = {} уже существует в базе.", email);
            throw new DuplicateEmailException();
        }
    }

    //----------------------Поиск пользователя по Id-------------------------
    public UserDto findById(Long id) {
        log.info("Запустился метод поиска пользователя по Id {}", id);
        Optional<User> optionalUser = userRepository.findById(id);
        if (optionalUser.isEmpty()) {
            throw new UserNotFoundException(String.format("Пользователь с id %d не найден.", id));
        }
        User findUser = optionalUser.get();
        return userMapper.toUserDto(findUser);
    }

    //----------------------Поиск пользователя по Email---------------------
    public UserDto findByEmail(String email) {
        log.info("Запустился метод поиска пользователя по email {}", email);
        Optional<User> optionalUser = userRepository.findByEmail(email);
        if (optionalUser.isEmpty()) {
            throw new UserNotFoundException(String.format("Пользователь с email %s не найден.", email));
        }
        User findUser = optionalUser.get();
        return userMapper.toUserDto(findUser);
    }
}