package ru.practicum.base.dto.compilation;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.util.Set;

@Data
@Builder
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class NewCompilationDto {
    private Set<Long> events;
    private boolean pinned;
    @NotBlank
    @Size(min = 1, max = 50)
    private String title;

}
