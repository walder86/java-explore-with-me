package ru.practicum.publicApi.service.comment;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import ru.practicum.base.dao.CommentRepository;
import ru.practicum.base.dao.EventRepository;
import ru.practicum.base.dto.comment.CommentDto;
import ru.practicum.base.enums.StatusComment;
import ru.practicum.base.exception.NotFoundException;
import ru.practicum.base.mapper.CommentMapper;
import ru.practicum.base.model.Comment;
import ru.practicum.base.model.Event;
import ru.practicum.base.util.page.MyPageRequest;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class PublicCommentServiceImpl implements PublicCommentService {

    private final CommentRepository commentRepository;

    private final EventRepository eventRepository;

    @Override
    public List<CommentDto> getComments(Long eventId, Integer from, Integer size) {
        log.info("Получение комментариев у события с ID = {}", eventId);
        Event event = getEvent(eventId);
        MyPageRequest pageable = new MyPageRequest(from, size,
                Sort.by(Sort.Direction.ASC, "created"));
        List<Comment> comments = commentRepository.findAllByStatusAndEvent(StatusComment.CONFIRMED, event, pageable);

        return comments.stream()
                .map(CommentMapper::toDto)
                .toList();
    }

    private Event getEvent(Long eventId) {
        return eventRepository.findById(eventId)
                .orElseThrow(() -> new NotFoundException(String.format("Событие с ID = %s не найдено", eventId)));
    }
}
