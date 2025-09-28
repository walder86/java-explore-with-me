package ru.practicum.base.dto.event;

import lombok.*;
import ru.practicum.base.dto.request.ParticipationRequestDto;

import java.util.List;

@Data
@Builder
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class EventRequestStatusUpdateResult {
    private List<ParticipationRequestDto> confirmedRequests;
    private List<ParticipationRequestDto> rejectedRequests;
}
