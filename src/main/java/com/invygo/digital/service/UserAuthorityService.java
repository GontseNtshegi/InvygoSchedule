package com.invygo.digital.service;

import com.invygo.digital.domain.UserAuthority;
import java.util.List;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Service Interface for managing {@link UserAuthority}.
 */
public interface UserAuthorityService {
    /**
     * Save a userAuthority.
     *
     * @param userAuthority the entity to save.
     * @return the persisted entity.
     */
    Mono<UserAuthority> save(UserAuthority userAuthority);

    /**
     * Updates a userAuthority.
     *
     * @param userAuthority the entity to update.
     * @return the persisted entity.
     */
    Mono<UserAuthority> update(UserAuthority userAuthority);

    /**
     * Partially updates a userAuthority.
     *
     * @param userAuthority the entity to update partially.
     * @return the persisted entity.
     */
    Mono<UserAuthority> partialUpdate(UserAuthority userAuthority);

    /**
     * Get all the userAuthorities.
     *
     * @return the list of entities.
     */
    Flux<UserAuthority> findAll();

    /**
     * Returns the number of userAuthorities available.
     * @return the number of entities in the database.
     *
     */
    Mono<Long> countAll();

    /**
     * Get the "id" userAuthority.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    Mono<UserAuthority> findOne(Long id);

    /**
     * Delete the "id" userAuthority.
     *
     * @param id the id of the entity.
     * @return a Mono to signal the deletion
     */
    Mono<Void> delete(Long id);
}
