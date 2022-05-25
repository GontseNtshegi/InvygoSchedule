package com.invygo.digital.service.impl;

import com.invygo.digital.domain.ScheduleUser;
import com.invygo.digital.repository.ScheduleUserRepository;
import com.invygo.digital.service.ScheduleUserService;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Service Implementation for managing {@link ScheduleUser}.
 */
@Service
@Transactional
public class ScheduleUserServiceImpl implements ScheduleUserService {

    private final Logger log = LoggerFactory.getLogger(ScheduleUserServiceImpl.class);

    private final ScheduleUserRepository scheduleUserRepository;

    public ScheduleUserServiceImpl(ScheduleUserRepository scheduleUserRepository) {
        this.scheduleUserRepository = scheduleUserRepository;
    }

    @Override
    public Mono<ScheduleUser> save(ScheduleUser scheduleUser) {
        log.debug("Request to save ScheduleUser : {}", scheduleUser);
        return scheduleUserRepository.save(scheduleUser);
    }

    @Override
    public Mono<ScheduleUser> update(ScheduleUser scheduleUser) {
        log.debug("Request to save ScheduleUser : {}", scheduleUser);
        return scheduleUserRepository.save(scheduleUser);
    }

    @Override
    public Mono<ScheduleUser> partialUpdate(ScheduleUser scheduleUser) {
        log.debug("Request to partially update ScheduleUser : {}", scheduleUser);

        return scheduleUserRepository
            .findById(scheduleUser.getId())
            .map(existingScheduleUser -> {
                if (scheduleUser.getEmail() != null) {
                    existingScheduleUser.setEmail(scheduleUser.getEmail());
                }
                if (scheduleUser.getLogin() != null) {
                    existingScheduleUser.setLogin(scheduleUser.getLogin());
                }
                if (scheduleUser.getFirstname() != null) {
                    existingScheduleUser.setFirstname(scheduleUser.getFirstname());
                }
                if (scheduleUser.getLastname() != null) {
                    existingScheduleUser.setLastname(scheduleUser.getLastname());
                }
                if (scheduleUser.getPassword() != null) {
                    existingScheduleUser.setPassword(scheduleUser.getPassword());
                }

                return existingScheduleUser;
            })
            .flatMap(scheduleUserRepository::save);
    }

    @Override
    @Transactional(readOnly = true)
    public Flux<ScheduleUser> findAll() {
        log.debug("Request to get all ScheduleUsers");
        return scheduleUserRepository.findAll();
    }

    public Mono<Long> countAll() {
        return scheduleUserRepository.count();
    }

    @Override
    @Transactional(readOnly = true)
    public Mono<ScheduleUser> findOne(Long id) {
        log.debug("Request to get ScheduleUser : {}", id);
        return scheduleUserRepository.findById(id);
    }

    @Override
    public Mono<Void> delete(Long id) {
        log.debug("Request to delete ScheduleUser : {}", id);
        return scheduleUserRepository.deleteById(id);
    }
}
