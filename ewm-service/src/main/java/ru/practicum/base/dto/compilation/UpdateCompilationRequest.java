package ru.practicum.base.dto.compilation;

import jakarta.validation.constraints.Size;
import lombok.*;
import ru.practicum.base.util.notblanknull.NotBlankNull;

import java.util.Set;

@Data
@Builder
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class UpdateCompilationRequest {
    private Set<Long> events;
    private Boolean pinned;
    @NotBlankNull
    @Size(min = 1, max = 50)
    private String title;
}
