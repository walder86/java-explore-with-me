package ru.practicum.publicApi.controller;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.practicum.base.dto.comment.CommentDto;
import ru.practicum.publicApi.service.comment.PublicCommentService;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/comments")
public class PublicCommentController {

    private final PublicCommentService commentService;

    @GetMapping
    public List<CommentDto> getComments(@RequestParam Long eventId,
                                        @Valid @PositiveOrZero @RequestParam(defaultValue = "0") Integer from,
                                        @Valid @Positive @RequestParam(defaultValue = "10") Integer size) {
        return commentService.getComments(eventId, from, size);
    }
}
