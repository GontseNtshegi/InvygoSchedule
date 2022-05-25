package com.invygo.digital.service.impl;

import com.invygo.digital.domain.RoleAuthority;
import com.invygo.digital.repository.RoleAuthorityRepository;
import com.invygo.digital.service.RoleAuthorityService;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Service Implementation for managing {@link RoleAuthority}.
 */
@Service
@Transactional
public class RoleAuthorityServiceImpl implements RoleAuthorityService {

    private final Logger log = LoggerFactory.getLogger(RoleAuthorityServiceImpl.class);

    private final RoleAuthorityRepository roleAuthorityRepository;

    public RoleAuthorityServiceImpl(RoleAuthorityRepository roleAuthorityRepository) {
        this.roleAuthorityRepository = roleAuthorityRepository;
    }

    @Override
    public Mono<RoleAuthority> save(RoleAuthority roleAuthority) {
        log.debug("Request to save RoleAuthority : {}", roleAuthority);
        return roleAuthorityRepository.save(roleAuthority);
    }

    @Override
    public Mono<RoleAuthority> update(RoleAuthority roleAuthority) {
        log.debug("Request to save RoleAuthority : {}", roleAuthority);
        return roleAuthorityRepository.save(roleAuthority);
    }

    @Override
    public Mono<RoleAuthority> partialUpdate(RoleAuthority roleAuthority) {
        log.debug("Request to partially update RoleAuthority : {}", roleAuthority);

        return roleAuthorityRepository
            .findById(roleAuthority.getId())
            .map(existingRoleAuthority -> {
                if (roleAuthority.getUserId() != null) {
                    existingRoleAuthority.setUserId(roleAuthority.getUserId());
                }
                if (roleAuthority.getRoleId() != null) {
                    existingRoleAuthority.setRoleId(roleAuthority.getRoleId());
                }

                return existingRoleAuthority;
            })
            .flatMap(roleAuthorityRepository::save);
    }

    @Override
    @Transactional(readOnly = true)
    public Flux<RoleAuthority> findAll() {
        log.debug("Request to get all RoleAuthorities");
        return roleAuthorityRepository.findAll();
    }

    public Mono<Long> countAll() {
        return roleAuthorityRepository.count();
    }

    @Override
    @Transactional(readOnly = true)
    public Mono<RoleAuthority> findOne(Long id) {
        log.debug("Request to get RoleAuthority : {}", id);
        return roleAuthorityRepository.findById(id);
    }

    @Override
    public Mono<Void> delete(Long id) {
        log.debug("Request to delete RoleAuthority : {}", id);
        return roleAuthorityRepository.deleteById(id);
    }
}
