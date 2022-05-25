package com.invygo.digital.service;

import com.invygo.digital.domain.Schedule;
import java.util.List;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Service Interface for managing {@link Schedule}.
 */
public interface ScheduleService {
    /**
     * Save a schedule.
     *
     * @param schedule the entity to save.
     * @return the persisted entity.
     */
    Mono<Schedule> save(Schedule schedule);

    /**
     * Updates a schedule.
     *
     * @param schedule the entity to update.
     * @return the persisted entity.
     */
    Mono<Schedule> update(Schedule schedule);

    /**
     * Partially updates a schedule.
     *
     * @param schedule the entity to update partially.
     * @return the persisted entity.
     */
    Mono<Schedule> partialUpdate(Schedule schedule);

    /**
     * Get all the schedules.
     *
     * @return the list of entities.
     */
    Flux<Schedule> findAll();

    /**
     * Returns the number of schedules available.
     * @return the number of entities in the database.
     *
     */
    Mono<Long> countAll();

    /**
     * Get the "id" schedule.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    Mono<Schedule> findOne(Long id);

    /**
     * Delete the "id" schedule.
     *
     * @param id the id of the entity.
     * @return a Mono to signal the deletion
     */
    Mono<Void> delete(Long id);
}
