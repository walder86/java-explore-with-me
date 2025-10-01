package ru.practicum.privateApi.service.comment;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.base.dao.CommentRepository;
import ru.practicum.base.dao.EventRepository;
import ru.practicum.base.dao.UserRepository;
import ru.practicum.base.dto.comment.CommentDto;
import ru.practicum.base.dto.comment.NewCommentRequest;
import ru.practicum.base.dto.comment.UpdateCommentUserRequest;
import ru.practicum.base.enums.State;
import ru.practicum.base.enums.StatusComment;
import ru.practicum.base.exception.ConflictException;
import ru.practicum.base.exception.DeniedAccess;
import ru.practicum.base.exception.NotFoundException;
import ru.practicum.base.mapper.CommentMapper;
import ru.practicum.base.model.Comment;
import ru.practicum.base.model.Event;
import ru.practicum.base.model.User;

import java.time.LocalDateTime;
import java.util.Objects;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class PrivateCommentServiceImpl implements PrivateCommentService {

    private final CommentRepository commentRepository;

    private final UserRepository userRepository;

    private final EventRepository eventRepository;

    @Transactional
    @Override
    public CommentDto createComment(Long userId, NewCommentRequest newCommentRequest) {
        log.info("Создание комментария {} пользователем с ID = {}", newCommentRequest, userId);
        User commentator = getCommentator(userId);
        Event event = getEvent(newCommentRequest.getEvent());
        if (!event.getState().equals(State.PUBLISHED)) {
            throw new ConflictException("Комментарий можно оставлять только для опубликованных событий");
        }
        Comment comment = CommentMapper.toEntity(newCommentRequest);
        comment.setEvent(event);
        comment.setCommentator(commentator);
        comment = commentRepository.save(comment);
        return CommentMapper.toDto(comment);
    }

    @Transactional
    @Override
    public CommentDto cancelComment(Long userId, Long commentId) {
        log.info("Отмена публикации комментария с ID = {} пользователя с ID = {}", commentId, userId);
        Comment comment = getComment(commentId);
        if (!Objects.equals(comment.getStatus(), StatusComment.PENDING)) {
            throw new ConflictException("Отменить публикацию можно только у комментариев, ожидающих подтверждения");
        }

        checkCommentator(userId, comment);
        comment.setStatus(StatusComment.CANCELED);
        comment = commentRepository.save(comment);
        return CommentMapper.toDto(comment);
    }

    @Transactional
    @Override
    public CommentDto updateComment(Long userId, Long commentId, UpdateCommentUserRequest updateCommentUserRequest) {
        log.info("Редактирование комментария {} у пользователя с ID = {}", updateCommentUserRequest, userId);
        Comment comment = getComment(commentId);
        if (Objects.equals(comment.getStatus(), StatusComment.REJECTED) ||
                Objects.equals(comment.getStatus(), StatusComment.CANCELED)) {
            throw new ConflictException("Редактировать можно только опубликованный комментарий или комментарий, ожидающий подтверждения");
        }

        checkCommentator(userId, comment);
        comment.setText(updateCommentUserRequest.getText());
        comment.setCreated(LocalDateTime.now());
        comment.setStatus(StatusComment.PENDING);
        comment = commentRepository.save(comment);

        return CommentMapper.toDto(comment);
    }

    private User getCommentator(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException(String.format("Пользователь с ID = %s не найден", userId)));
    }

    private Event getEvent(Long eventId) {
        return eventRepository.findById(eventId)
                .orElseThrow(() -> new NotFoundException(String.format("Событие с ID = %s не найдено", eventId)));
    }

    private Comment getComment(Long id) {
        return commentRepository.findById(id)
                .orElseThrow(() -> new NotFoundException(String.format("Комментарий с ID = %s не найден", id)));
    }

    private static void checkCommentator(Long userId, Comment comment) {
        if (!Objects.equals(comment.getCommentator().getId(), userId)) {
            throw new DeniedAccess("Отменить публикацию комментария может только сам комментатор");
        }
    }
}
