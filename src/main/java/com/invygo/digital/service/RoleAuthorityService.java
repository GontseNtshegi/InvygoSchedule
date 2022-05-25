package com.invygo.digital.service;

import com.invygo.digital.domain.RoleAuthority;
import java.util.List;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Service Interface for managing {@link RoleAuthority}.
 */
public interface RoleAuthorityService {
    /**
     * Save a roleAuthority.
     *
     * @param roleAuthority the entity to save.
     * @return the persisted entity.
     */
    Mono<RoleAuthority> save(RoleAuthority roleAuthority);

    /**
     * Updates a roleAuthority.
     *
     * @param roleAuthority the entity to update.
     * @return the persisted entity.
     */
    Mono<RoleAuthority> update(RoleAuthority roleAuthority);

    /**
     * Partially updates a roleAuthority.
     *
     * @param roleAuthority the entity to update partially.
     * @return the persisted entity.
     */
    Mono<RoleAuthority> partialUpdate(RoleAuthority roleAuthority);

    /**
     * Get all the roleAuthorities.
     *
     * @return the list of entities.
     */
    Flux<RoleAuthority> findAll();

    /**
     * Returns the number of roleAuthorities available.
     * @return the number of entities in the database.
     *
     */
    Mono<Long> countAll();

    /**
     * Get the "id" roleAuthority.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    Mono<RoleAuthority> findOne(Long id);

    /**
     * Delete the "id" roleAuthority.
     *
     * @param id the id of the entity.
     * @return a Mono to signal the deletion
     */
    Mono<Void> delete(Long id);
}
