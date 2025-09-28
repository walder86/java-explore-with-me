package ru.practicum.adminApi.service.compilation;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.base.dao.CompilationRepository;
import ru.practicum.base.dao.EventRepository;
import ru.practicum.base.dto.compilation.CompilationDto;
import ru.practicum.base.dto.compilation.NewCompilationDto;
import ru.practicum.base.dto.compilation.UpdateCompilationRequest;
import ru.practicum.base.exception.NotFoundException;
import ru.practicum.base.mapper.CompilationMapper;
import ru.practicum.base.model.Compilation;
import ru.practicum.base.model.Event;

import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class AdminCompilationServiceImpl implements AdminCompilationService {

    private final CompilationRepository compilationRepository;
    private final EventRepository eventRepository;

    @Transactional
    @Override
    public CompilationDto save(NewCompilationDto newCompilationDto) {
        log.info("Создание подборки: {}", newCompilationDto);
        Compilation compilation = CompilationMapper.toEntity(newCompilationDto);
        compilation.setEvents(findEvents(newCompilationDto.getEvents()));
        compilation = compilationRepository.save(compilation);
        return CompilationMapper.toDto(compilation);
    }

    @Transactional
    @Override
    public void delete(Long compId) {
        log.info("Удаление подборки с ID = {}", compId);
        compilationRepository.deleteById(compId);
    }

    @Transactional
    @Override
    public CompilationDto update(Long compId, UpdateCompilationRequest dto) {
        log.info("Обновление подборки {} с ID = {}", dto, compId);
        Compilation compilation = getCompilation(compId);
        compilation.setEvents(findEvents(dto.getEvents()));
        compilationRepository.flush();
        return CompilationMapper.toDto(compilation);
    }

    private Compilation getCompilation(Long compId) {
        return compilationRepository.findById(compId)
                .orElseThrow(() -> new NotFoundException(String.format("Подборка с ID = %s не найдена", compId)));
    }

    private List<Event> findEvents(Set<Long> eventsId) {
        if (eventsId == null || eventsId.isEmpty()) {
            return List.of();
        }
        return eventRepository.findAllByIdIn(eventsId);
    }
}
