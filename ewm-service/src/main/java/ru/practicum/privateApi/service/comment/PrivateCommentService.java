package ru.practicum.privateApi.service.comment;

import ru.practicum.base.dto.comment.CommentDto;
import ru.practicum.base.dto.comment.NewCommentRequest;
import ru.practicum.base.dto.comment.UpdateCommentUserRequest;

public interface PrivateCommentService {
    CommentDto createComment(Long userId, NewCommentRequest newCommentRequest);

    CommentDto cancelComment(Long userId, Long commentId);

    CommentDto updateComment(Long userId, Long commentId, UpdateCommentUserRequest updateCommentUserRequest);
}
