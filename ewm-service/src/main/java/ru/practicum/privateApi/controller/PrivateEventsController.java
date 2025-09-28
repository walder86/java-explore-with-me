package ru.practicum.privateApi.controller;


import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.base.dto.event.*;
import ru.practicum.base.dto.request.ParticipationRequestDto;
import ru.practicum.base.enums.Status;
import ru.practicum.base.exception.ConflictException;
import ru.practicum.privateApi.service.event.PrivateEventsService;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/users/{userId}/events")
public class PrivateEventsController {

    public final PrivateEventsService service;

    @GetMapping
    public List<EventShortDto> getAll(@PathVariable Long userId,
                                     @RequestParam(defaultValue = "0") Integer from,
                                     @RequestParam(defaultValue = "10") Integer size) {
        return service.getAll(userId, from, size);
    }

    @GetMapping("/{eventId}")
    public EventFullDto get(@PathVariable Long userId,
                            @PathVariable Long eventId) {
        return service.get(userId, eventId);
    }

    @GetMapping("/{eventId}/requests")
    public List<ParticipationRequestDto> getRequests(@PathVariable Long userId,
                                                     @PathVariable Long eventId) {
        return service.getRequests(userId, eventId);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public EventFullDto create(@PathVariable Long userId,
                               @RequestBody @Valid NewEventDto eventDto) {
        return service.create(userId, eventDto);
    }

    @PatchMapping("/{eventId}")
    public EventFullDto update(@PathVariable Long userId, @PathVariable Long eventId,
                               @RequestBody @Valid UpdateEventUserRequest eventDto) {
        return service.update(userId, eventId, eventDto);
    }

    @PatchMapping("/{eventId}/requests")
    public EventRequestStatusUpdateResult updateRequestStatus(@PathVariable Long userId,
                                                              @PathVariable Long eventId,
                                                              @RequestBody EventRequestStatusUpdateRequest request) {
        if (Status.from(request.getStatus()) == null) {
            throw new ConflictException("Status is not validate");
        }
        return service.updateRequestStatus(userId, eventId, request);
    }

}
