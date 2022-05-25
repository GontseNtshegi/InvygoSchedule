package com.invygo.digital.repository;

import com.invygo.digital.domain.ScheduleAuthority;
import org.springframework.data.domain.Pageable;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.relational.core.query.Criteria;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Spring Data SQL reactive repository for the ScheduleAuthority entity.
 */
@SuppressWarnings("unused")
@Repository
public interface ScheduleAuthorityRepository extends ReactiveCrudRepository<ScheduleAuthority, Long>, ScheduleAuthorityRepositoryInternal {
    @Override
    <S extends ScheduleAuthority> Mono<S> save(S entity);

    @Override
    Flux<ScheduleAuthority> findAll();

    @Override
    Mono<ScheduleAuthority> findById(Long id);

    @Override
    Mono<Void> deleteById(Long id);
}

interface ScheduleAuthorityRepositoryInternal {
    <S extends ScheduleAuthority> Mono<S> save(S entity);

    Flux<ScheduleAuthority> findAllBy(Pageable pageable);

    Flux<ScheduleAuthority> findAll();

    Mono<ScheduleAuthority> findById(Long id);
    // this is not supported at the moment because of https://github.com/jhipster/generator-jhipster/issues/18269
    // Flux<ScheduleAuthority> findAllBy(Pageable pageable, Criteria criteria);

}
