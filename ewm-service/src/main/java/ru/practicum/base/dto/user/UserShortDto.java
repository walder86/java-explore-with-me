package ru.practicum.base.dto.user;

import lombok.*;

@Data
@Builder
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class UserShortDto {

    private Long id;

    private String name;
}
