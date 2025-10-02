package ru.practicum.base.mapper;

import lombok.experimental.UtilityClass;
import ru.practicum.base.dto.comment.CommentDto;
import ru.practicum.base.dto.comment.NewCommentRequest;
import ru.practicum.base.enums.StatusComment;
import ru.practicum.base.model.Comment;

import java.time.LocalDateTime;

@UtilityClass
public final class CommentMapper {

    public static Comment toEntity(NewCommentRequest dto) {
        return Comment.builder()
                .text(dto.getText())
                .created(LocalDateTime.now())
                .status(StatusComment.PENDING)
                .build();
    }

    public static CommentDto toDto(Comment entity) {
        return CommentDto.builder()
                .id(entity.getId())
                .text(entity.getText())
                .created(entity.getCreated())
                .event(EventMapper.toEventShortDto(entity.getEvent()))
                .commentator(UserMapper.toUserShortDto(entity.getCommentator()))
                .status(entity.getStatus())
                .build();
    }

}
