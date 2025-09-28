package ru.practicum.base.dao;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.practicum.base.enums.State;
import ru.practicum.base.model.Category;
import ru.practicum.base.model.Event;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.Set;

public interface EventRepository extends JpaRepository<Event, Long>, EventCriteriaRepository {


    boolean existsByCategory(Category category);


    Optional<Event> findByIdAndInitiatorId(Long id, Long userId);

    List<Event> findAllByIdIn(Set<Long> ids);


    @Query("SELECT e FROM Event e " +
            "WHERE (e.initiator.id IN (:users) OR (:users) IS NULL) " +
            "AND (e.state IN (:states) OR (:states) IS NULL) " +
            "AND (e.category.id IN (:categories) OR (:categories) IS NULL) " +
            "AND (e.date BETWEEN coalesce(:rangeStart, e.date) AND coalesce(:rangeEnd, e.date))")
    List<Event> findEventsByParams(
            @Param("users") List<Long> users,
            @Param("states") List<State> states,
            @Param("categories") List<Long> categories,
            @Param("rangeStart") LocalDateTime rangeStart,
            @Param("rangeEnd") LocalDateTime rangeEnd,
            Pageable pageable);


}
