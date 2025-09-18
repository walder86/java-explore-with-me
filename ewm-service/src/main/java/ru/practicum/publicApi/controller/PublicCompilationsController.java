package ru.practicum.publicApi.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.practicum.base.dto.compilation.CompilationDto;
import ru.practicum.publicApi.service.compilation.PublicCompilationsService;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/compilations")
public class PublicCompilationsController {

    public final PublicCompilationsService compilationsService;

    @GetMapping
    public List<CompilationDto> getAll(@RequestParam(required = false) Boolean pinned,
                                       @RequestParam(defaultValue = "0") int from,
                                       @RequestParam(defaultValue = "10") int size) {
        return compilationsService.getAll(pinned, from, size);
    }

    @GetMapping("/{comId}")
    public CompilationDto get(@PathVariable Long comId) {
        return compilationsService.get(comId);
    }
}
