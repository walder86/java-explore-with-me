package ru.practicum.base.dto.event;

import lombok.*;
import ru.practicum.base.enums.UserStateAction;


@Data
@Builder
@ToString
@EqualsAndHashCode(callSuper = false)
@NoArgsConstructor
@AllArgsConstructor
public class UpdateEventUserRequest extends UpdateEventRequest {

    private UserStateAction stateAction;

}
