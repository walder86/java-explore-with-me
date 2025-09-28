package ru.practicum.base.dto.event;

import lombok.*;
import ru.practicum.base.enums.AdminStateAction;

@Data
@Builder
@ToString
@EqualsAndHashCode(callSuper = false)
@NoArgsConstructor
@AllArgsConstructor
public class UpdateEventAdminRequest extends UpdateEventRequest {

    private AdminStateAction stateAction;


}
