package ru.practicum.adminApi.controller;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.practicum.adminApi.service.comment.AdminCommentService;
import ru.practicum.base.dto.comment.CommentDto;
import ru.practicum.base.dto.comment.UpdateCommentAdminRequest;
import ru.practicum.base.enums.StatusComment;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/admin/comments")
public class AdminCommentController {

    private final AdminCommentService commentService;

    @PatchMapping("/{commentId}/verify")
    public CommentDto publish(@PathVariable Long commentId, @RequestBody UpdateCommentAdminRequest request) {
        return commentService.verifyComment(commentId, request);
    }

    @GetMapping
    public List<CommentDto> getComments(
            @RequestParam(required = false) List<StatusComment> statuses,
            @RequestParam(required = false) List<Long> events,
            @Valid @PositiveOrZero @RequestParam(defaultValue = "0") Integer from,
            @Valid @Positive @RequestParam(defaultValue = "10") Integer size) {
        return commentService.getCommentsForVerification(statuses, events, from, size);
    }
}
