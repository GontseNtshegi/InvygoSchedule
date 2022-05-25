package com.invygo.digital.service.impl;

import com.invygo.digital.domain.UserAuthority;
import com.invygo.digital.repository.UserAuthorityRepository;
import com.invygo.digital.service.UserAuthorityService;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Service Implementation for managing {@link UserAuthority}.
 */
@Service
@Transactional
public class UserAuthorityServiceImpl implements UserAuthorityService {

    private final Logger log = LoggerFactory.getLogger(UserAuthorityServiceImpl.class);

    private final UserAuthorityRepository userAuthorityRepository;

    public UserAuthorityServiceImpl(UserAuthorityRepository userAuthorityRepository) {
        this.userAuthorityRepository = userAuthorityRepository;
    }

    @Override
    public Mono<UserAuthority> save(UserAuthority userAuthority) {
        log.debug("Request to save UserAuthority : {}", userAuthority);
        return userAuthorityRepository.save(userAuthority);
    }

    @Override
    public Mono<UserAuthority> update(UserAuthority userAuthority) {
        log.debug("Request to save UserAuthority : {}", userAuthority);
        return userAuthorityRepository.save(userAuthority);
    }

    @Override
    public Mono<UserAuthority> partialUpdate(UserAuthority userAuthority) {
        log.debug("Request to partially update UserAuthority : {}", userAuthority);

        return userAuthorityRepository
            .findById(userAuthority.getId())
            .map(existingUserAuthority -> {
                if (userAuthority.getUserId() != null) {
                    existingUserAuthority.setUserId(userAuthority.getUserId());
                }
                if (userAuthority.getRoleId() != null) {
                    existingUserAuthority.setRoleId(userAuthority.getRoleId());
                }

                return existingUserAuthority;
            })
            .flatMap(userAuthorityRepository::save);
    }

    @Override
    @Transactional(readOnly = true)
    public Flux<UserAuthority> findAll() {
        log.debug("Request to get all UserAuthorities");
        return userAuthorityRepository.findAll();
    }

    public Mono<Long> countAll() {
        return userAuthorityRepository.count();
    }

    @Override
    @Transactional(readOnly = true)
    public Mono<UserAuthority> findOne(Long id) {
        log.debug("Request to get UserAuthority : {}", id);
        return userAuthorityRepository.findById(id);
    }

    @Override
    public Mono<Void> delete(Long id) {
        log.debug("Request to delete UserAuthority : {}", id);
        return userAuthorityRepository.deleteById(id);
    }
}
