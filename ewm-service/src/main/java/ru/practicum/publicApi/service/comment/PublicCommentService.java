package ru.practicum.publicApi.service.comment;

import ru.practicum.base.dto.comment.CommentDto;

import java.util.List;

public interface PublicCommentService {
    List<CommentDto> getComments(Long eventId, Integer from, Integer size);
}
