package ru.practicum.publicApi.service.event;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.StatsClient;
import ru.practicum.base.dao.EventRepository;
import ru.practicum.base.dto.event.EventFullDto;
import ru.practicum.base.dto.event.EventShortDto;
import ru.practicum.base.enums.State;
import ru.practicum.base.exception.NotFoundException;
import ru.practicum.base.exception.ValidationException;
import ru.practicum.base.mapper.DateTimeMapper;
import ru.practicum.base.mapper.EventMapper;
import ru.practicum.base.model.Event;
import ru.practicum.base.model.EventSearchCriteria;
import ru.practicum.base.util.page.MyPageRequest;
import ru.practicum.dto.EndpointHitDto;
import ru.practicum.dto.ViewStatsDto;
import ru.practicum.publicApi.dto.RequestParamForEvent;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class PublicEventsServiceImpl implements PublicEventsService {

    private final EventRepository eventRepository;

    private final StatsClient statsClient;

    @Value("${ewm.service.name}")
    private String serviceName;


    @Transactional
    @Override
    public List<EventShortDto> getAll(RequestParamForEvent param) {
        log.info("Получение списка всех опубликованных событий");
        MyPageRequest pageable = createPageable(param.getSort(), param.getFrom(), param.getSize());
        EventSearchCriteria eventSearchCriteria = createCriteria(param);

        List<EventShortDto> eventShorts = EventMapper.toEventShortDtoList(eventRepository
                .findAllWithFilters(pageable, eventSearchCriteria).toList());

        if (eventShorts.isEmpty()) {
            throw new ValidationException("Событие должно быть опубликовано");
        }
        saveEndpointHit(param.getRequest());
        return eventShorts;
    }


    @Transactional
    @Override
    public EventFullDto get(Long id, HttpServletRequest request) {
        log.info("Получение события с ID = {}", id);
        Event event = getEvent(id);
        if (!event.getState().equals(State.PUBLISHED)) {
            throw new NotFoundException(String.format("Событие с id=%d не опубликовано", id));
        }

        saveEndpointHit(request);
        //всегда будет 1 объект в листе
        List<ViewStatsDto> viewStatsDtoList = getStats(request);
        event.setViews(viewStatsDtoList.getFirst().getHits());
        eventRepository.flush();
        return EventMapper.toEventFullDto(event);
    }

    private Event getEvent(Long id) {
        return eventRepository.findById(id)
                .orElseThrow(() -> new NotFoundException(String.format("Событие с id = %s не найдено", id)));
    }

    private void saveEndpointHit(HttpServletRequest request) {

        EndpointHitDto endpointHit = EndpointHitDto.builder()
                .ip(request.getRemoteAddr())
                .uri(request.getRequestURI())
                .app(serviceName)
                .timestamp(LocalDateTime.now())
                .build();
        statsClient.save(endpointHit);
    }

    private List<ViewStatsDto> getStats(HttpServletRequest request) {
        return statsClient.getStats(
                DateTimeMapper.toStringDate(LocalDateTime.now().minusYears(2)),
                DateTimeMapper.toStringDate(LocalDateTime.now().plusYears(2)),
                List.of(request.getRequestURI()),
                true
        );
    }

    private MyPageRequest createPageable(String sort, int from, int size) {
        MyPageRequest pageable = null;
        if (sort == null || sort.equalsIgnoreCase("EVENT_DATE")) {
            pageable = new MyPageRequest(from, size,
                    Sort.by(Sort.Direction.ASC, "event_date"));
        } else if (sort.equalsIgnoreCase("VIEWS")) {
            pageable = new MyPageRequest(from, size,
                    Sort.by(Sort.Direction.ASC, "views"));
        }
        return pageable;
    }

    private EventSearchCriteria createCriteria(RequestParamForEvent param) {
        return EventSearchCriteria.builder()
                .text(param.getText())
                .categories(param.getCategories())
                .rangeEnd(param.getRangeEnd())
                .rangeStart(param.getRangeStart())
                .paid(param.getPaid())
                .onlyAvailable(param.getOnlyAvailable())
                .build();
    }
}
