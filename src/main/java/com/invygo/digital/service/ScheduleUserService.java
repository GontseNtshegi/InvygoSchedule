package com.invygo.digital.service;

import com.invygo.digital.domain.ScheduleUser;
import java.util.List;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Service Interface for managing {@link ScheduleUser}.
 */
public interface ScheduleUserService {
    /**
     * Save a scheduleUser.
     *
     * @param scheduleUser the entity to save.
     * @return the persisted entity.
     */
    Mono<ScheduleUser> save(ScheduleUser scheduleUser);

    /**
     * Updates a scheduleUser.
     *
     * @param scheduleUser the entity to update.
     * @return the persisted entity.
     */
    Mono<ScheduleUser> update(ScheduleUser scheduleUser);

    /**
     * Partially updates a scheduleUser.
     *
     * @param scheduleUser the entity to update partially.
     * @return the persisted entity.
     */
    Mono<ScheduleUser> partialUpdate(ScheduleUser scheduleUser);

    /**
     * Get all the scheduleUsers.
     *
     * @return the list of entities.
     */
    Flux<ScheduleUser> findAll();

    /**
     * Returns the number of scheduleUsers available.
     * @return the number of entities in the database.
     *
     */
    Mono<Long> countAll();

    /**
     * Get the "id" scheduleUser.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    Mono<ScheduleUser> findOne(Long id);

    /**
     * Delete the "id" scheduleUser.
     *
     * @param id the id of the entity.
     * @return a Mono to signal the deletion
     */
    Mono<Void> delete(Long id);
}
