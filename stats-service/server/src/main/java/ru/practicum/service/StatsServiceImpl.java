package ru.practicum.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.dto.EndpointHitDto;
import ru.practicum.dto.ViewStatsDto;
import ru.practicum.exception.BadRequestException;
import ru.practicum.mapper.EndpointHitMapper;
import ru.practicum.mapper.ViewStatsMapper;
import ru.practicum.repository.StatsRepository;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class StatsServiceImpl implements StatsService {

    private final StatsRepository repository;

    @Override
    public List<ViewStatsDto> getStats(LocalDateTime start, LocalDateTime end, List<String> uris, Boolean unique) {
        log.info("Запрос на получения статистики");
        checkDate(start, end);
        PageRequest pageable = PageRequest.of(0, 10);
        if (unique) {
            return ViewStatsMapper.toDtoList(repository.findUniqueViewStats(start, end, uris, pageable));
        } else {
            return ViewStatsMapper.toDtoList(repository.findViewStats(start, end, uris, pageable));
        }
    }

    @Transactional
    @Override
    public EndpointHitDto createEndpointHit(EndpointHitDto dto) {
        log.info("Запрос на сохранение информации об обращении на конкретный эндпоинт");
        return EndpointHitMapper.toDto(repository.save(EndpointHitMapper.toEntity(dto)));
    }

    private void checkDate(LocalDateTime startDate, LocalDateTime endDate) {
        if (startDate.isAfter(endDate)) {
            throw new BadRequestException("Дата начала не может быть раньше даты окончания");
        }
    }
}
