package ru.practicum.privateApi.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.base.dto.comment.CommentDto;
import ru.practicum.base.dto.comment.NewCommentRequest;
import ru.practicum.base.dto.comment.UpdateCommentUserRequest;
import ru.practicum.privateApi.service.comment.PrivateCommentService;

@RestController
@RequiredArgsConstructor
@RequestMapping("/users/{userId}/comments")
public class PrivateCommentController {

    private final PrivateCommentService commentService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public CommentDto create(@Valid @RequestBody NewCommentRequest newCommentRequest,
                             @PathVariable Long userId) {
        return commentService.createComment(userId, newCommentRequest);
    }

    @PatchMapping("/{commentId}/cancel")
    public CommentDto cancel(@PathVariable Long userId, @PathVariable Long commentId) {
        return commentService.cancelComment(userId, commentId);
    }

    @PatchMapping("/{commentId}")
    public CommentDto update(@Valid @RequestBody UpdateCommentUserRequest updateCommentUserRequest,
                             @PathVariable Long userId,
                             @PathVariable Long commentId) {
        return commentService.updateComment(userId, commentId, updateCommentUserRequest);
    }

}

