package com.invygo.digital.service;

import com.invygo.digital.domain.ScheduleAuthority;
import java.util.List;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Service Interface for managing {@link ScheduleAuthority}.
 */
public interface ScheduleAuthorityService {
    /**
     * Save a scheduleAuthority.
     *
     * @param scheduleAuthority the entity to save.
     * @return the persisted entity.
     */
    Mono<ScheduleAuthority> save(ScheduleAuthority scheduleAuthority);

    /**
     * Updates a scheduleAuthority.
     *
     * @param scheduleAuthority the entity to update.
     * @return the persisted entity.
     */
    Mono<ScheduleAuthority> update(ScheduleAuthority scheduleAuthority);

    /**
     * Partially updates a scheduleAuthority.
     *
     * @param scheduleAuthority the entity to update partially.
     * @return the persisted entity.
     */
    Mono<ScheduleAuthority> partialUpdate(ScheduleAuthority scheduleAuthority);

    /**
     * Get all the scheduleAuthorities.
     *
     * @return the list of entities.
     */
    Flux<ScheduleAuthority> findAll();

    /**
     * Returns the number of scheduleAuthorities available.
     * @return the number of entities in the database.
     *
     */
    Mono<Long> countAll();

    /**
     * Get the "id" scheduleAuthority.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    Mono<ScheduleAuthority> findOne(Long id);

    /**
     * Delete the "id" scheduleAuthority.
     *
     * @param id the id of the entity.
     * @return a Mono to signal the deletion
     */
    Mono<Void> delete(Long id);
}
