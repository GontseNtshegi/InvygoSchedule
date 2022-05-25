package com.invygo.digital.web.rest;

import com.invygo.digital.domain.ScheduleUser;
import com.invygo.digital.repository.ScheduleUserRepository;
import com.invygo.digital.service.ScheduleUserService;
import com.invygo.digital.web.rest.errors.BadRequestAlertException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import tech.jhipster.web.util.HeaderUtil;
import tech.jhipster.web.util.reactive.ResponseUtil;

/**
 * REST controller for managing {@link com.invygo.digital.domain.ScheduleUser}.
 */
@RestController
@RequestMapping("/api")
public class ScheduleUserResource {

    private final Logger log = LoggerFactory.getLogger(ScheduleUserResource.class);

    private static final String ENTITY_NAME = "stuffSchedulingScheduleUser";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final ScheduleUserService scheduleUserService;

    private final ScheduleUserRepository scheduleUserRepository;

    public ScheduleUserResource(ScheduleUserService scheduleUserService, ScheduleUserRepository scheduleUserRepository) {
        this.scheduleUserService = scheduleUserService;
        this.scheduleUserRepository = scheduleUserRepository;
    }

    /**
     * {@code POST  /schedule-users} : Create a new scheduleUser.
     *
     * @param scheduleUser the scheduleUser to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new scheduleUser, or with status {@code 400 (Bad Request)} if the scheduleUser has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("/schedule-users")
    public Mono<ResponseEntity<ScheduleUser>> createScheduleUser(@Valid @RequestBody ScheduleUser scheduleUser) throws URISyntaxException {
        log.debug("REST request to save ScheduleUser : {}", scheduleUser);
        if (scheduleUser.getId() != null) {
            throw new BadRequestAlertException("A new scheduleUser cannot already have an ID", ENTITY_NAME, "idexists");
        }
        return scheduleUserService
            .save(scheduleUser)
            .map(result -> {
                try {
                    return ResponseEntity
                        .created(new URI("/api/schedule-users/" + result.getId()))
                        .headers(HeaderUtil.createEntityCreationAlert(applicationName, false, ENTITY_NAME, result.getId().toString()))
                        .body(result);
                } catch (URISyntaxException e) {
                    throw new RuntimeException(e);
                }
            });
    }

    /**
     * {@code PUT  /schedule-users/:id} : Updates an existing scheduleUser.
     *
     * @param id the id of the scheduleUser to save.
     * @param scheduleUser the scheduleUser to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated scheduleUser,
     * or with status {@code 400 (Bad Request)} if the scheduleUser is not valid,
     * or with status {@code 500 (Internal Server Error)} if the scheduleUser couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/schedule-users/{id}")
    public Mono<ResponseEntity<ScheduleUser>> updateScheduleUser(
        @PathVariable(value = "id", required = false) final Long id,
        @Valid @RequestBody ScheduleUser scheduleUser
    ) throws URISyntaxException {
        log.debug("REST request to update ScheduleUser : {}, {}", id, scheduleUser);
        if (scheduleUser.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, scheduleUser.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        return scheduleUserRepository
            .existsById(id)
            .flatMap(exists -> {
                if (!exists) {
                    return Mono.error(new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound"));
                }

                return scheduleUserService
                    .update(scheduleUser)
                    .switchIfEmpty(Mono.error(new ResponseStatusException(HttpStatus.NOT_FOUND)))
                    .map(result ->
                        ResponseEntity
                            .ok()
                            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, false, ENTITY_NAME, result.getId().toString()))
                            .body(result)
                    );
            });
    }

    /**
     * {@code PATCH  /schedule-users/:id} : Partial updates given fields of an existing scheduleUser, field will ignore if it is null
     *
     * @param id the id of the scheduleUser to save.
     * @param scheduleUser the scheduleUser to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated scheduleUser,
     * or with status {@code 400 (Bad Request)} if the scheduleUser is not valid,
     * or with status {@code 404 (Not Found)} if the scheduleUser is not found,
     * or with status {@code 500 (Internal Server Error)} if the scheduleUser couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/schedule-users/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public Mono<ResponseEntity<ScheduleUser>> partialUpdateScheduleUser(
        @PathVariable(value = "id", required = false) final Long id,
        @NotNull @RequestBody ScheduleUser scheduleUser
    ) throws URISyntaxException {
        log.debug("REST request to partial update ScheduleUser partially : {}, {}", id, scheduleUser);
        if (scheduleUser.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, scheduleUser.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        return scheduleUserRepository
            .existsById(id)
            .flatMap(exists -> {
                if (!exists) {
                    return Mono.error(new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound"));
                }

                Mono<ScheduleUser> result = scheduleUserService.partialUpdate(scheduleUser);

                return result
                    .switchIfEmpty(Mono.error(new ResponseStatusException(HttpStatus.NOT_FOUND)))
                    .map(res ->
                        ResponseEntity
                            .ok()
                            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, false, ENTITY_NAME, res.getId().toString()))
                            .body(res)
                    );
            });
    }

    /**
     * {@code GET  /schedule-users} : get all the scheduleUsers.
     *
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of scheduleUsers in body.
     */
    @GetMapping("/schedule-users")
    public Mono<List<ScheduleUser>> getAllScheduleUsers() {
        log.debug("REST request to get all ScheduleUsers");
        return scheduleUserService.findAll().collectList();
    }

    /**
     * {@code GET  /schedule-users} : get all the scheduleUsers as a stream.
     * @return the {@link Flux} of scheduleUsers.
     */
    @GetMapping(value = "/schedule-users", produces = MediaType.APPLICATION_NDJSON_VALUE)
    public Flux<ScheduleUser> getAllScheduleUsersAsStream() {
        log.debug("REST request to get all ScheduleUsers as a stream");
        return scheduleUserService.findAll();
    }

    /**
     * {@code GET  /schedule-users/:id} : get the "id" scheduleUser.
     *
     * @param id the id of the scheduleUser to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the scheduleUser, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/schedule-users/{id}")
    public Mono<ResponseEntity<ScheduleUser>> getScheduleUser(@PathVariable Long id) {
        log.debug("REST request to get ScheduleUser : {}", id);
        Mono<ScheduleUser> scheduleUser = scheduleUserService.findOne(id);
        return ResponseUtil.wrapOrNotFound(scheduleUser);
    }

    /**
     * {@code DELETE  /schedule-users/:id} : delete the "id" scheduleUser.
     *
     * @param id the id of the scheduleUser to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/schedule-users/{id}")
    @ResponseStatus(code = HttpStatus.NO_CONTENT)
    public Mono<ResponseEntity<Void>> deleteScheduleUser(@PathVariable Long id) {
        log.debug("REST request to delete ScheduleUser : {}", id);
        return scheduleUserService
            .delete(id)
            .map(result ->
                ResponseEntity
                    .noContent()
                    .headers(HeaderUtil.createEntityDeletionAlert(applicationName, false, ENTITY_NAME, id.toString()))
                    .build()
            );
    }
}
