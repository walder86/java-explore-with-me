package ru.practicum.adminApi.service.event;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.base.dao.EventRepository;
import ru.practicum.base.dto.event.EventFullDto;
import ru.practicum.base.dto.event.UpdateEventAdminRequest;
import ru.practicum.base.enums.AdminStateAction;
import ru.practicum.base.enums.State;
import ru.practicum.base.exception.ConflictException;
import ru.practicum.base.exception.NotFoundException;
import ru.practicum.base.exception.ValidationException;
import ru.practicum.base.mapper.EventMapper;
import ru.practicum.base.model.Event;
import ru.practicum.base.util.page.MyPageRequest;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class AdminEventsServiceImpl implements AdminEventsService {

    private final EventRepository eventRepository;

    @Transactional
    @Override
    public EventFullDto update(Long eventId, UpdateEventAdminRequest dto) {
        log.info("Обновление события {} с ID = {}", dto, eventId);
        if (dto.getEventDate() != null) {
            checkEventDate(dto.getEventDate());
        }
        Event event = getEvent(eventId);

        if (Objects.equals(event.getState(), State.PUBLISHED) || Objects.equals(event.getState(), State.CANCELED)) {
            throw new ConflictException("Событие для публикации не может быть в состояниях: Опубликовано или Отменено");
        } else {
            if (Objects.equals(dto.getStateAction(), AdminStateAction.PUBLISH_EVENT)) {
                event.setState(State.PUBLISHED);
            }
            if (Objects.equals(dto.getStateAction(), AdminStateAction.REJECT_EVENT)) {
                event.setState(State.CANCELED);
            }
        }

        setFields(dto, event);
        eventRepository.flush();

        return EventMapper.toEventFullDto(event);
    }

    private static void setFields(UpdateEventAdminRequest dto, Event event) {
        event.setAnnotation(dto.getAnnotation() != null ? dto.getAnnotation() : event.getAnnotation());
        event.setPaid(dto.getPaid() != null ? dto.getPaid() : event.getPaid());
        event.setDate(dto.getEventDate() != null ? dto.getEventDate() : event.getDate());
        event.setDescription(dto.getDescription() != null ? dto.getDescription() : event.getDescription());
        event.setTitle(dto.getTitle() != null ? dto.getTitle() : event.getTitle());
        event.setParticipantLimit(dto.getParticipantLimit() != null ? dto.getParticipantLimit() : event.getParticipantLimit());
    }

    private Event getEvent(Long eventId) {
        return eventRepository.findById(eventId)
                .orElseThrow(() -> new NotFoundException(String.format("Событие с ID = %s не найдено", eventId)));
    }

    @Override
    public List<EventFullDto> getAll(
            List<Long> users, List<State> states, List<Long> categories,
            LocalDateTime rangeStart, LocalDateTime rangeEnd, int from, int size) {
        MyPageRequest pageable = new MyPageRequest(from, size,
                Sort.by(Sort.Direction.ASC, "id"));
        List<Event> events = eventRepository.findEventsByParams(
                users, states, categories, rangeStart,
                rangeEnd, pageable);
        return events.stream()
                .map(EventMapper::toEventFullDto)
                .collect(Collectors.toList());
    }

    private void checkEventDate(LocalDateTime eventDate) {
        if (eventDate.isBefore(LocalDateTime.now().plusHours(2))) {
            throw new ValidationException("Дата и время, на которые запланировано событие" +
                    " не могут быть указаны ранее, чем через два часа после текущего момента.");
        }
    }
}
