package com.invygo.digital.service.impl;

import com.invygo.digital.domain.ScheduleAuthority;
import com.invygo.digital.repository.ScheduleAuthorityRepository;
import com.invygo.digital.service.ScheduleAuthorityService;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Service Implementation for managing {@link ScheduleAuthority}.
 */
@Service
@Transactional
public class ScheduleAuthorityServiceImpl implements ScheduleAuthorityService {

    private final Logger log = LoggerFactory.getLogger(ScheduleAuthorityServiceImpl.class);

    private final ScheduleAuthorityRepository scheduleAuthorityRepository;

    public ScheduleAuthorityServiceImpl(ScheduleAuthorityRepository scheduleAuthorityRepository) {
        this.scheduleAuthorityRepository = scheduleAuthorityRepository;
    }

    @Override
    public Mono<ScheduleAuthority> save(ScheduleAuthority scheduleAuthority) {
        log.debug("Request to save ScheduleAuthority : {}", scheduleAuthority);
        return scheduleAuthorityRepository.save(scheduleAuthority);
    }

    @Override
    public Mono<ScheduleAuthority> update(ScheduleAuthority scheduleAuthority) {
        log.debug("Request to save ScheduleAuthority : {}", scheduleAuthority);
        return scheduleAuthorityRepository.save(scheduleAuthority);
    }

    @Override
    public Mono<ScheduleAuthority> partialUpdate(ScheduleAuthority scheduleAuthority) {
        log.debug("Request to partially update ScheduleAuthority : {}", scheduleAuthority);

        return scheduleAuthorityRepository
            .findById(scheduleAuthority.getId())
            .map(existingScheduleAuthority -> {
                if (scheduleAuthority.getName() != null) {
                    existingScheduleAuthority.setName(scheduleAuthority.getName());
                }

                return existingScheduleAuthority;
            })
            .flatMap(scheduleAuthorityRepository::save);
    }

    @Override
    @Transactional(readOnly = true)
    public Flux<ScheduleAuthority> findAll() {
        log.debug("Request to get all ScheduleAuthorities");
        return scheduleAuthorityRepository.findAll();
    }

    public Mono<Long> countAll() {
        return scheduleAuthorityRepository.count();
    }

    @Override
    @Transactional(readOnly = true)
    public Mono<ScheduleAuthority> findOne(Long id) {
        log.debug("Request to get ScheduleAuthority : {}", id);
        return scheduleAuthorityRepository.findById(id);
    }

    @Override
    public Mono<Void> delete(Long id) {
        log.debug("Request to delete ScheduleAuthority : {}", id);
        return scheduleAuthorityRepository.deleteById(id);
    }
}
