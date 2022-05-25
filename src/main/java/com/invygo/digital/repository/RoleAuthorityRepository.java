package com.invygo.digital.repository;

import com.invygo.digital.domain.RoleAuthority;
import org.springframework.data.domain.Pageable;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.relational.core.query.Criteria;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Spring Data SQL reactive repository for the RoleAuthority entity.
 */
@SuppressWarnings("unused")
@Repository
public interface RoleAuthorityRepository extends ReactiveCrudRepository<RoleAuthority, Long>, RoleAuthorityRepositoryInternal {
    @Override
    <S extends RoleAuthority> Mono<S> save(S entity);

    @Override
    Flux<RoleAuthority> findAll();

    @Override
    Mono<RoleAuthority> findById(Long id);

    @Override
    Mono<Void> deleteById(Long id);
}

interface RoleAuthorityRepositoryInternal {
    <S extends RoleAuthority> Mono<S> save(S entity);

    Flux<RoleAuthority> findAllBy(Pageable pageable);

    Flux<RoleAuthority> findAll();

    Mono<RoleAuthority> findById(Long id);
    // this is not supported at the moment because of https://github.com/jhipster/generator-jhipster/issues/18269
    // Flux<RoleAuthority> findAllBy(Pageable pageable, Criteria criteria);

}
