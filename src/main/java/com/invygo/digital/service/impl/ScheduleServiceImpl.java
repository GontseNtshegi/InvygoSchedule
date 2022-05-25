package com.invygo.digital.service.impl;

import com.invygo.digital.domain.Schedule;
import com.invygo.digital.repository.ScheduleRepository;
import com.invygo.digital.service.ScheduleService;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Service Implementation for managing {@link Schedule}.
 */
@Service
@Transactional
public class ScheduleServiceImpl implements ScheduleService {

    private final Logger log = LoggerFactory.getLogger(ScheduleServiceImpl.class);

    private final ScheduleRepository scheduleRepository;

    public ScheduleServiceImpl(ScheduleRepository scheduleRepository) {
        this.scheduleRepository = scheduleRepository;
    }

    @Override
    public Mono<Schedule> save(Schedule schedule) {
        log.debug("Request to save Schedule : {}", schedule);
        return scheduleRepository.save(schedule);
    }

    @Override
    public Mono<Schedule> update(Schedule schedule) {
        log.debug("Request to save Schedule : {}", schedule);
        return scheduleRepository.save(schedule);
    }

    @Override
    public Mono<Schedule> partialUpdate(Schedule schedule) {
        log.debug("Request to partially update Schedule : {}", schedule);

        return scheduleRepository
            .findById(schedule.getId())
            .map(existingSchedule -> {
                if (schedule.getUserId() != null) {
                    existingSchedule.setUserId(schedule.getUserId());
                }
                if (schedule.getWorkDate() != null) {
                    existingSchedule.setWorkDate(schedule.getWorkDate());
                }
                if (schedule.getHours() != null) {
                    existingSchedule.setHours(schedule.getHours());
                }

                return existingSchedule;
            })
            .flatMap(scheduleRepository::save);
    }

    @Override
    @Transactional(readOnly = true)
    public Flux<Schedule> findAll() {
        log.debug("Request to get all Schedules");
        return scheduleRepository.findAll();
    }

    public Mono<Long> countAll() {
        return scheduleRepository.count();
    }

    @Override
    @Transactional(readOnly = true)
    public Mono<Schedule> findOne(Long id) {
        log.debug("Request to get Schedule : {}", id);
        return scheduleRepository.findById(id);
    }

    @Override
    public Mono<Void> delete(Long id) {
        log.debug("Request to delete Schedule : {}", id);
        return scheduleRepository.deleteById(id);
    }
}
