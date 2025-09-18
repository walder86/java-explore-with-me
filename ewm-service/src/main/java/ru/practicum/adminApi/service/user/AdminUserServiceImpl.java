package ru.practicum.adminApi.service.user;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.base.dao.UserRepository;
import ru.practicum.base.dto.user.NewUserRequest;
import ru.practicum.base.dto.user.UserDto;
import ru.practicum.base.exception.ConflictException;
import ru.practicum.base.mapper.UserMapper;
import ru.practicum.base.model.User;
import ru.practicum.base.util.page.MyPageRequest;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AdminUserServiceImpl implements AdminUserService {

    private final UserRepository userRepository;

    @Override
    public List<UserDto> getAll(List<Long> idUsers, Integer from, Integer size) {
        log.info("Запрос пользователей по IDs: {}", idUsers);
        List<User> users;
        MyPageRequest pageable = new MyPageRequest(from, size, Sort.by(Sort.Direction.ASC, "id"));
        if (idUsers == null || idUsers.isEmpty()) {
            users = userRepository.findAll(pageable).toList();
        } else {
            users = userRepository.findAllByIdIn(idUsers, pageable);
        }
        return UserMapper.toUserDtoList(users);
    }

    @Transactional
    @Override
    public UserDto save(NewUserRequest dto) {
        log.info("Добавление пользователя: {}", dto);
        checkEmail(dto);
        User user = UserMapper.toEntity(dto);
        user = userRepository.save(user);
        return UserMapper.toUserDto(user);
    }

    private void checkEmail(NewUserRequest dto) {
        if (userRepository.existsUserByEmail(dto.getEmail())) {
            throw new ConflictException(String.format("Email %s уже занят", dto.getEmail()));
        }
    }

    @Transactional
    @Override
    public void delete(Long userId) {
        log.info("Удаление пользователя с ID = {}", userId);
        userRepository.deleteById(userId);
    }
}
