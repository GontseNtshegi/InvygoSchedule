package com.invygo.digital.repository;

import com.invygo.digital.domain.Roles;
import org.springframework.data.domain.Pageable;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.relational.core.query.Criteria;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Spring Data SQL reactive repository for the Roles entity.
 */
@SuppressWarnings("unused")
@Repository
public interface RolesRepository extends ReactiveCrudRepository<Roles, Long>, RolesRepositoryInternal {
    @Override
    <S extends Roles> Mono<S> save(S entity);

    @Override
    Flux<Roles> findAll();

    @Override
    Mono<Roles> findById(Long id);

    @Override
    Mono<Void> deleteById(Long id);
}

interface RolesRepositoryInternal {
    <S extends Roles> Mono<S> save(S entity);

    Flux<Roles> findAllBy(Pageable pageable);

    Flux<Roles> findAll();

    Mono<Roles> findById(Long id);
    // this is not supported at the moment because of https://github.com/jhipster/generator-jhipster/issues/18269
    // Flux<Roles> findAllBy(Pageable pageable, Criteria criteria);

}
