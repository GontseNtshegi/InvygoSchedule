package com.invygo.digital.service.impl;

import com.invygo.digital.domain.Roles;
import com.invygo.digital.repository.RolesRepository;
import com.invygo.digital.service.RolesService;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Service Implementation for managing {@link Roles}.
 */
@Service
@Transactional
public class RolesServiceImpl implements RolesService {

    private final Logger log = LoggerFactory.getLogger(RolesServiceImpl.class);

    private final RolesRepository rolesRepository;

    public RolesServiceImpl(RolesRepository rolesRepository) {
        this.rolesRepository = rolesRepository;
    }

    @Override
    public Mono<Roles> save(Roles roles) {
        log.debug("Request to save Roles : {}", roles);
        return rolesRepository.save(roles);
    }

    @Override
    public Mono<Roles> update(Roles roles) {
        log.debug("Request to save Roles : {}", roles);
        return rolesRepository.save(roles);
    }

    @Override
    public Mono<Roles> partialUpdate(Roles roles) {
        log.debug("Request to partially update Roles : {}", roles);

        return rolesRepository
            .findById(roles.getId())
            .map(existingRoles -> {
                if (roles.getRoleName() != null) {
                    existingRoles.setRoleName(roles.getRoleName());
                }

                return existingRoles;
            })
            .flatMap(rolesRepository::save);
    }

    @Override
    @Transactional(readOnly = true)
    public Flux<Roles> findAll() {
        log.debug("Request to get all Roles");
        return rolesRepository.findAll();
    }

    public Mono<Long> countAll() {
        return rolesRepository.count();
    }

    @Override
    @Transactional(readOnly = true)
    public Mono<Roles> findOne(Long id) {
        log.debug("Request to get Roles : {}", id);
        return rolesRepository.findById(id);
    }

    @Override
    public Mono<Void> delete(Long id) {
        log.debug("Request to delete Roles : {}", id);
        return rolesRepository.deleteById(id);
    }
}
