package ru.practicum.base.dto.comment;

import lombok.*;
import ru.practicum.base.enums.AdminStatusComment;

@Data
@Builder
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class UpdateCommentAdminRequest {

    private AdminStatusComment statusAction;
}
