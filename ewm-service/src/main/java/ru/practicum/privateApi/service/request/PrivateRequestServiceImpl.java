package ru.practicum.privateApi.service.request;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.base.dao.EventRepository;
import ru.practicum.base.dao.RequestRepository;
import ru.practicum.base.dao.UserRepository;
import ru.practicum.base.dto.request.ParticipationRequestDto;
import ru.practicum.base.enums.State;
import ru.practicum.base.enums.Status;
import ru.practicum.base.exception.ConflictException;
import ru.practicum.base.exception.NotFoundException;
import ru.practicum.base.mapper.RequestMapper;
import ru.practicum.base.model.Event;
import ru.practicum.base.model.Request;
import ru.practicum.base.model.User;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class PrivateRequestServiceImpl implements PrivateRequestService {


    private final UserRepository userRepository;

    private final RequestRepository requestRepository;

    private final EventRepository eventRepository;

    @Override
    public List<ParticipationRequestDto> getRequests(Long userId) {
        if (!userRepository.existsById(userId)) {
            throw new NotFoundException(String.format("Пользователь с ID = %s не найден", userId));
        }
        return RequestMapper.toDtoList(requestRepository.findAllByRequesterId(userId));
    }

    @Transactional
    @Override
    public ParticipationRequestDto create(Long userId, Long eventId) {
        final Event event = getEvent(eventId);
        final User user = getUser(userId);

        if (requestRepository.existsByRequesterIdAndEventId(userId, eventId)) {
            throw new ConflictException(String.format("Запрос с requesterId=%d и eventId=%d уже существует", userId, eventId));
        }
        if (userId.equals(event.getInitiator().getId())) {
            throw new ConflictException(String.format("Пользователь с id=%d не может быть инициатором", userId));
        }
        if (!event.getState().equals(State.PUBLISHED)) {
            throw new ConflictException(String.format("Событие с id=%d не опубликовано", eventId));
        }
        if (event.getParticipantLimit() != 0 && event.getParticipantLimit().equals(event.getConfirmedRequests())) {
            throw new ConflictException(String.format("У события с id=%d достигнут лимит участников", eventId));
        }
        if (!event.getRequestModeration()) {
            event.setConfirmedRequests(event.getConfirmedRequests() + 1);
            eventRepository.save(event);
        }

        return RequestMapper.toParticipationRequestDto(requestRepository.save(RequestMapper.toRequest(event, user)));
    }

    @Transactional
    @Override
    public ParticipationRequestDto update(Long userId, Long requestId) {
        Request request = getRequest(userId, requestId);
        request.setStatus(Status.CANCELED);
        return RequestMapper.toParticipationRequestDto(requestRepository.save(request));
    }

    private Request getRequest(Long userId, Long requestId) {
        return requestRepository.findByIdAndRequesterId(requestId, userId)
                .orElseThrow(() -> new NotFoundException(String.format("Запрос с id=%d " +
                        "и requesterId=%d не найден", requestId, userId)));
    }

    private User getUser(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException(String.format("Пользователь с id = %s не найден", userId)));
    }

    private Event getEvent(Long eventId) {
        return eventRepository.findById(eventId)
                .orElseThrow(() -> new NotFoundException(String.format("Событие с id = %s не найдено", eventId)));
    }
}
