package ru.practicum.base.mapper;


import lombok.experimental.UtilityClass;
import ru.practicum.base.dto.request.ParticipationRequestDto;
import ru.practicum.base.model.Event;
import ru.practicum.base.model.Request;
import ru.practicum.base.model.User;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import static ru.practicum.base.enums.Status.CONFIRMED;
import static ru.practicum.base.enums.Status.PENDING;


@UtilityClass
public final class RequestMapper {

    public static Request toRequest(Event event, User requester) {
        return Request.builder()
                .requester(requester)
                .event(event)
                .created(LocalDateTime.now())
                .status(event.getParticipantLimit() != 0 && event.getRequestModeration() ? PENDING : CONFIRMED)
                .build();
    }

    public static ParticipationRequestDto toParticipationRequestDto(Request entity) {
        return ParticipationRequestDto.builder()
                .id(entity.getId())
                .requester(entity.getRequester().getId())
                .created(entity.getCreated())
                .event(entity.getEvent().getId())
                .status(entity.getStatus())
                .build();
    }

    public static List<ParticipationRequestDto> toDtoList(List<Request> requests) {
        return requests.stream().map(RequestMapper::toParticipationRequestDto).collect(Collectors.toList());
    }
}
