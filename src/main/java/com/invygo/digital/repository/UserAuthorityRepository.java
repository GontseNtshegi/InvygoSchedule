package com.invygo.digital.repository;

import com.invygo.digital.domain.UserAuthority;
import org.springframework.data.domain.Pageable;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.relational.core.query.Criteria;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Spring Data SQL reactive repository for the UserAuthority entity.
 */
@SuppressWarnings("unused")
@Repository
public interface UserAuthorityRepository extends ReactiveCrudRepository<UserAuthority, Long>, UserAuthorityRepositoryInternal {
    @Override
    <S extends UserAuthority> Mono<S> save(S entity);

    @Override
    Flux<UserAuthority> findAll();

    @Override
    Mono<UserAuthority> findById(Long id);

    @Override
    Mono<Void> deleteById(Long id);
}

interface UserAuthorityRepositoryInternal {
    <S extends UserAuthority> Mono<S> save(S entity);

    Flux<UserAuthority> findAllBy(Pageable pageable);

    Flux<UserAuthority> findAll();

    Mono<UserAuthority> findById(Long id);
    // this is not supported at the moment because of https://github.com/jhipster/generator-jhipster/issues/18269
    // Flux<UserAuthority> findAllBy(Pageable pageable, Criteria criteria);

}
