package ru.practicum.base.dto.user;

import lombok.*;

@Data
@Builder
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class UserDto {

    private String email;

    private Long id;

    private String name;
}
