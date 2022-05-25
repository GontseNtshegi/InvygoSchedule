package com.invygo.digital.service;

import com.invygo.digital.domain.Roles;
import java.util.List;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Service Interface for managing {@link Roles}.
 */
public interface RolesService {
    /**
     * Save a roles.
     *
     * @param roles the entity to save.
     * @return the persisted entity.
     */
    Mono<Roles> save(Roles roles);

    /**
     * Updates a roles.
     *
     * @param roles the entity to update.
     * @return the persisted entity.
     */
    Mono<Roles> update(Roles roles);

    /**
     * Partially updates a roles.
     *
     * @param roles the entity to update partially.
     * @return the persisted entity.
     */
    Mono<Roles> partialUpdate(Roles roles);

    /**
     * Get all the roles.
     *
     * @return the list of entities.
     */
    Flux<Roles> findAll();

    /**
     * Returns the number of roles available.
     * @return the number of entities in the database.
     *
     */
    Mono<Long> countAll();

    /**
     * Get the "id" roles.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    Mono<Roles> findOne(Long id);

    /**
     * Delete the "id" roles.
     *
     * @param id the id of the entity.
     * @return a Mono to signal the deletion
     */
    Mono<Void> delete(Long id);
}
