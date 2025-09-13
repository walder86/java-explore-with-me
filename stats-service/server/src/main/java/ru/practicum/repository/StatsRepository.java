package ru.practicum.repository;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.model.EndpointHit;
import ru.practicum.model.ViewStats;

import java.time.LocalDateTime;
import java.util.List;

public interface StatsRepository extends JpaRepository<EndpointHit, Long> {

    @Query(" SELECT new ru.practicum.model.ViewStats(endpointHit.app, endpointHit.uri, COUNT(DISTINCT endpointHit.ip)) " +
            "FROM EndpointHit endpointHit " +
            "WHERE endpointHit.timestamp BETWEEN ?1 AND ?2 " +
            "AND (endpointHit.uri IN (?3) OR (?3) is NULL) " +
            "GROUP BY endpointHit.app, endpointHit.uri " +
            "ORDER BY COUNT(DISTINCT endpointHit.ip) DESC ")
    List<ViewStats> findUniqueViewStats(LocalDateTime start, LocalDateTime end, List<String> uris, PageRequest pageable);


    @Query(" SELECT new ru.practicum.model.ViewStats(endpointHit.app, endpointHit.uri, COUNT(endpointHit.ip)) " +
            "FROM EndpointHit endpointHit " +
            "WHERE endpointHit.timestamp BETWEEN ?1 AND ?2 " +
            "AND (endpointHit.uri IN (?3) OR (?3) is NULL) " +
            "GROUP BY endpointHit.app, endpointHit.uri " +
            "ORDER BY COUNT(endpointHit.ip) DESC ")
    List<ViewStats> findViewStats(LocalDateTime start, LocalDateTime end, List<String> uris, PageRequest pageable);
}
