package ru.practicum.adminApi.service.comment;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import ru.practicum.base.dao.CommentRepository;
import ru.practicum.base.dto.comment.CommentDto;
import ru.practicum.base.dto.comment.UpdateCommentAdminRequest;
import ru.practicum.base.enums.AdminStatusComment;
import ru.practicum.base.enums.StatusComment;
import ru.practicum.base.exception.ConflictException;
import ru.practicum.base.exception.NotFoundException;
import ru.practicum.base.mapper.CommentMapper;
import ru.practicum.base.model.Comment;
import ru.practicum.base.util.page.MyPageRequest;

import java.util.List;
import java.util.Objects;

@Slf4j
@Service
@RequiredArgsConstructor
public class AdminCommentServiceImpl implements AdminCommentService {

    private final CommentRepository repository;

    @Override
    public CommentDto verifyComment(Long commentId, UpdateCommentAdminRequest request) {
        log.info("Публикация/отклонение {} комментария с ID = {}", request.getStatusAction(), commentId);
        Comment comment = getComment(commentId);

        checkStatus(comment);
        if (request.getStatusAction().equals(AdminStatusComment.PUBLISHED_COMMENT)) {
            comment.setStatus(StatusComment.CONFIRMED);
        }
        if (request.getStatusAction().equals(AdminStatusComment.CANCELED_COMMENT)) {
            comment.setStatus(StatusComment.REJECTED);
        }
        comment = repository.save(comment);
        return CommentMapper.toDto(comment);
    }

    @Override
    public List<CommentDto> getCommentsForVerification(List<StatusComment> statuses, List<Long> events, Integer from, Integer size) {
        log.info("Получение всех комментариев по параметрам");
        MyPageRequest pageable = new MyPageRequest(from, size,
                Sort.by(Sort.Direction.ASC, "created"));
        List<Comment> comments = repository.findEventsByParams(
                statuses, events, pageable);

        return comments.stream()
                .map(CommentMapper::toDto)
                .toList();
    }

    private Comment getComment(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new NotFoundException(String.format("Комментарий с ID = %s не найден", id)));
    }

    private void checkStatus(Comment comment) {
        if (!Objects.equals(comment.getStatus(), StatusComment.PENDING)) {
            throw new ConflictException("Комментарий не может быть в состояниях: Опубликован или Отклонен");
        }
    }
}
