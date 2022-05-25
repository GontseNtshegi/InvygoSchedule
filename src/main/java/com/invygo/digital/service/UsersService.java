package com.invygo.digital.service;

import com.invygo.digital.domain.Users;
import java.util.List;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Service Interface for managing {@link Users}.
 */
public interface UsersService {
    /**
     * Save a users.
     *
     * @param users the entity to save.
     * @return the persisted entity.
     */
    Mono<Users> save(Users users);

    /**
     * Updates a users.
     *
     * @param users the entity to update.
     * @return the persisted entity.
     */
    Mono<Users> update(Users users);

    /**
     * Partially updates a users.
     *
     * @param users the entity to update partially.
     * @return the persisted entity.
     */
    Mono<Users> partialUpdate(Users users);

    /**
     * Get all the users.
     *
     * @return the list of entities.
     */
    Flux<Users> findAll();

    /**
     * Returns the number of users available.
     * @return the number of entities in the database.
     *
     */
    Mono<Long> countAll();

    /**
     * Get the "id" users.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    Mono<Users> findOne(Long id);

    /**
     * Delete the "id" users.
     *
     * @param id the id of the entity.
     * @return a Mono to signal the deletion
     */
    Mono<Void> delete(Long id);
}
