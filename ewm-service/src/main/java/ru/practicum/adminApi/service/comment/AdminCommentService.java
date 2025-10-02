package ru.practicum.adminApi.service.comment;

import ru.practicum.base.dto.comment.CommentDto;
import ru.practicum.base.dto.comment.UpdateCommentAdminRequest;
import ru.practicum.base.enums.StatusComment;

import java.util.List;

public interface AdminCommentService {
    CommentDto verifyComment(Long commentId, UpdateCommentAdminRequest request);

    List<CommentDto> getCommentsForVerification(List<StatusComment> statuses, List<Long> events, Integer from, Integer size);
}
