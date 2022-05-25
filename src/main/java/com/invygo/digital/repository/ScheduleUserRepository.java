package com.invygo.digital.repository;

import com.invygo.digital.domain.ScheduleUser;
import org.springframework.data.domain.Pageable;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.relational.core.query.Criteria;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Spring Data SQL reactive repository for the ScheduleUser entity.
 */
@SuppressWarnings("unused")
@Repository
public interface ScheduleUserRepository extends ReactiveCrudRepository<ScheduleUser, Long>, ScheduleUserRepositoryInternal {
    @Override
    <S extends ScheduleUser> Mono<S> save(S entity);

    @Override
    Flux<ScheduleUser> findAll();

    @Override
    Mono<ScheduleUser> findById(Long id);

    @Override
    Mono<Void> deleteById(Long id);
}

interface ScheduleUserRepositoryInternal {
    <S extends ScheduleUser> Mono<S> save(S entity);

    Flux<ScheduleUser> findAllBy(Pageable pageable);

    Flux<ScheduleUser> findAll();

    Mono<ScheduleUser> findById(Long id);
    // this is not supported at the moment because of https://github.com/jhipster/generator-jhipster/issues/18269
    // Flux<ScheduleUser> findAllBy(Pageable pageable, Criteria criteria);

}
