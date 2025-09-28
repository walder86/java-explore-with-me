package ru.practicum.privateApi.service.event;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.base.dao.CategoriesRepository;
import ru.practicum.base.dao.EventRepository;
import ru.practicum.base.dao.RequestRepository;
import ru.practicum.base.dao.UserRepository;
import ru.practicum.base.dto.event.*;
import ru.practicum.base.dto.request.ParticipationRequestDto;
import ru.practicum.base.enums.State;
import ru.practicum.base.enums.UserStateAction;
import ru.practicum.base.exception.ConflictException;
import ru.practicum.base.exception.NotFoundException;
import ru.practicum.base.exception.ValidationException;
import ru.practicum.base.mapper.EventMapper;
import ru.practicum.base.mapper.RequestMapper;
import ru.practicum.base.model.Category;
import ru.practicum.base.model.Event;
import ru.practicum.base.model.Request;
import ru.practicum.base.model.User;
import ru.practicum.base.util.page.MyPageRequest;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import static ru.practicum.base.enums.Status.CONFIRMED;
import static ru.practicum.base.enums.Status.REJECTED;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class PrivateEventsServiceImpl implements PrivateEventsService {

    private final EventRepository eventRepository;

    private final UserRepository userRepository;

    private final RequestRepository requestRepository;

    private final CategoriesRepository categoriesRepository;

    @Override
    public List<EventShortDto> getAll(Long userId, Integer from, Integer size) {
        log.info("Получение всех событий у пользователя с ID = {}", userId);
        MyPageRequest pageRequest = new MyPageRequest(from, size,
                Sort.by(Sort.Direction.ASC, "id"));
        return EventMapper.toEventShortDtoList(
                eventRepository.findAll(pageRequest).toList());
    }

    @Override
    public EventFullDto get(Long userId, Long eventId) {
        log.info("Получения события с ID = {} у пользователя с ID = {}", eventId, userId);
        Event event = getEvent(userId, eventId);
        return EventMapper.toEventFullDto(event);
    }

    @Override
    public List<ParticipationRequestDto> getRequests(Long userId, Long eventId) {
        getEvent(userId, eventId);
        return RequestMapper.toDtoList(requestRepository.findAllByEventId(eventId));
    }

    @Transactional
    @Override
    public EventFullDto create(Long userId, NewEventDto eventDto) {
        log.info("Создание события {} у пользователя с ID = {}", eventDto, userId);
        checkEventDate(eventDto.getEventDate());
        setDefaultValue(eventDto);
        Event event = EventMapper.toEntity(eventDto);
        event.setCategory(getCategory(eventDto.getCategory()));
        event.setPublishedOn(LocalDateTime.now());
        event.setInitiator(getUser(userId));
        event.setViews(0L);
        event = eventRepository.save(event);
        return EventMapper.toEventFullDto(event);
    }

    @Transactional
    @Override
    public EventFullDto update(Long userId, Long eventId, UpdateEventUserRequest eventDto) {
        log.info("Обновление события: {} с ID = {} и ID пользователя = {}", eventDto, eventId, userId);
        Event eventTarget = getEvent(userId, eventId);
        checkEventDate(eventDto.getEventDate());

        if (eventDto.getCategory() != null) {
            eventTarget.setCategory(getCategory(eventDto.getCategory()));
        }

        if (eventTarget.getState().equals(State.PUBLISHED)) {
            throw new ConflictException("Событие не опубликовано");
        }

        if (Objects.equals(UserStateAction.CANCEL_REVIEW, eventDto.getStateAction())) {
            eventTarget.setState(State.CANCELED);
        } else if (Objects.equals(UserStateAction.SEND_TO_REVIEW, eventDto.getStateAction())) {
            eventTarget.setState(State.PENDING);
        }
        setFields(eventDto, eventTarget);

        eventRepository.flush();
        return EventMapper.toEventFullDto(eventTarget);
    }


    @Transactional
    @Override
    public EventRequestStatusUpdateResult updateRequestStatus(Long userId, Long eventId, EventRequestStatusUpdateRequest request) {
        List<ParticipationRequestDto> confirmedRequests = List.of();
        List<ParticipationRequestDto> rejectedRequests = List.of();

        List<Long> requestIds = request.getRequestIds();
        List<Request> requests = requestRepository.findAllByIdIn(requestIds);

        String status = request.getStatus();

        if (status.equals(REJECTED.toString())) {
            boolean isConfirmedRequestExists = requests.stream()
                    .anyMatch(r -> r.getStatus().equals(CONFIRMED));
            if (isConfirmedRequestExists) {
                throw new ConflictException("Невозможно отклонить подтвержденные запросы");
            }
            rejectedRequests = requests.stream()
                    .peek(r -> r.setStatus(REJECTED))
                    .map(RequestMapper::toParticipationRequestDto)
                    .collect(Collectors.toList());
            return new EventRequestStatusUpdateResult(confirmedRequests, rejectedRequests);
        }

        Event event = eventRepository.findByIdAndInitiatorId(eventId, userId)
                .orElseThrow(() -> new NotFoundException(
                        String.format("Событие с id = %s и userId = %s не найдено", eventId, userId)));
        Long participantLimit = event.getParticipantLimit();
        Long approvedRequests = event.getConfirmedRequests();
        long availableParticipants = participantLimit - approvedRequests;
        long potentialParticipants = requestIds.size();

        if (participantLimit > 0 && participantLimit.equals(approvedRequests)) {
            throw new ConflictException(String.format("У события с id=%d достигнут лимит участников", eventId));
        }

        if (status.equals(CONFIRMED.toString())) {
            if (participantLimit.equals(0L) || (potentialParticipants <= availableParticipants && !event.getRequestModeration())) {
                confirmedRequests = requests.stream()
                        .peek(r -> {
                            if (!r.getStatus().equals(CONFIRMED)) {
                                r.setStatus(CONFIRMED);
                            } else {
                                throw new ConflictException(String.format("Запрос с ID = %d уже подтвержден", r.getId()));
                            }
                        })
                        .map(RequestMapper::toParticipationRequestDto)
                        .collect(Collectors.toList());
                event.setConfirmedRequests(approvedRequests + potentialParticipants);
            } else {
                confirmedRequests = requests.stream()
                        .limit(availableParticipants)
                        .peek(r -> {
                            if (!r.getStatus().equals(CONFIRMED)) {
                                r.setStatus(CONFIRMED);
                            } else {
                                throw new ConflictException(String.format("Запрос с ID = %d уже подтвержден", r.getId()));
                            }
                        })
                        .map(RequestMapper::toParticipationRequestDto)
                        .collect(Collectors.toList());
                rejectedRequests = requests.stream()
                        .skip(availableParticipants)
                        .peek(r -> {
                            if (!r.getStatus().equals(REJECTED)) {
                                r.setStatus(REJECTED);
                            } else {
                                throw new ConflictException(String.format("Запрос с ID = %d уже отклонен", r.getId()));
                            }
                        })
                        .map(RequestMapper::toParticipationRequestDto)
                        .collect(Collectors.toList());
                event.setConfirmedRequests(confirmedRequests.size());
            }
        }
        eventRepository.flush();
        requestRepository.flush();
        return new EventRequestStatusUpdateResult(confirmedRequests, rejectedRequests);
    }


    private void checkEventDate(LocalDateTime eventDate) {
        if (eventDate != null && eventDate.isBefore(LocalDateTime.now().plusHours(2))) {
            throw new ValidationException("Дата и время, на которые запланировано событие" +
                    " не могут быть указаны ранее, чем через два часа после текущего момента.");
        }
    }

    private void setDefaultValue(NewEventDto eventDto) {
        if (eventDto.getRequestModeration() == null) {
            eventDto.setRequestModeration(true);
        }
        if (eventDto.getPaid() == null) {
            eventDto.setPaid(false);
        }
        if (eventDto.getParticipantLimit() == null) {
            eventDto.setParticipantLimit(0L);
        }
    }

    private Event getEvent(Long userId, Long eventId) {
        return eventRepository.findByIdAndInitiatorId(eventId, userId)
                .orElseThrow(() -> new NotFoundException(
                        String.format("Событие с ID = %s и ID пользователя = %s не найдено", eventId, userId)));
    }

    private Category getCategory(Long eventDto) {
        return categoriesRepository.findById(eventDto)
                .orElseThrow(() -> new NotFoundException(String.format("Категория с ID = %s не найдена", eventDto)));
    }

    private User getUser(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException(String.format("Пользователь с ID = %s не найден", userId)));
    }

    private static void setFields(UpdateEventUserRequest dto, Event event) {
        event.setAnnotation(dto.getAnnotation() != null ? dto.getAnnotation() : event.getAnnotation());
        event.setPaid(dto.getPaid() != null ? dto.getPaid() : event.getPaid());
        event.setDate(dto.getEventDate() != null ? dto.getEventDate() : event.getDate());
        event.setDescription(dto.getDescription() != null ? dto.getDescription() : event.getDescription());
        event.setTitle(dto.getTitle() != null ? dto.getTitle() : event.getTitle());
        event.setParticipantLimit(dto.getParticipantLimit() != null ? dto.getParticipantLimit() : event.getParticipantLimit());
    }

}
