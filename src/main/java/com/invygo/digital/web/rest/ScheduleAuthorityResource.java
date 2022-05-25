package com.invygo.digital.web.rest;

import com.invygo.digital.domain.ScheduleAuthority;
import com.invygo.digital.repository.ScheduleAuthorityRepository;
import com.invygo.digital.service.ScheduleAuthorityService;
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
 * REST controller for managing {@link com.invygo.digital.domain.ScheduleAuthority}.
 */
@RestController
@RequestMapping("/api")
public class ScheduleAuthorityResource {

    private final Logger log = LoggerFactory.getLogger(ScheduleAuthorityResource.class);

    private static final String ENTITY_NAME = "stuffSchedulingScheduleAuthority";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final ScheduleAuthorityService scheduleAuthorityService;

    private final ScheduleAuthorityRepository scheduleAuthorityRepository;

    public ScheduleAuthorityResource(
        ScheduleAuthorityService scheduleAuthorityService,
        ScheduleAuthorityRepository scheduleAuthorityRepository
    ) {
        this.scheduleAuthorityService = scheduleAuthorityService;
        this.scheduleAuthorityRepository = scheduleAuthorityRepository;
    }

    /**
     * {@code POST  /schedule-authorities} : Create a new scheduleAuthority.
     *
     * @param scheduleAuthority the scheduleAuthority to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new scheduleAuthority, or with status {@code 400 (Bad Request)} if the scheduleAuthority has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("/schedule-authorities")
    public Mono<ResponseEntity<ScheduleAuthority>> createScheduleAuthority(@Valid @RequestBody ScheduleAuthority scheduleAuthority)
        throws URISyntaxException {
        log.debug("REST request to save ScheduleAuthority : {}", scheduleAuthority);
        if (scheduleAuthority.getId() != null) {
            throw new BadRequestAlertException("A new scheduleAuthority cannot already have an ID", ENTITY_NAME, "idexists");
        }
        return scheduleAuthorityService
            .save(scheduleAuthority)
            .map(result -> {
                try {
                    return ResponseEntity
                        .created(new URI("/api/schedule-authorities/" + result.getId()))
                        .headers(HeaderUtil.createEntityCreationAlert(applicationName, false, ENTITY_NAME, result.getId().toString()))
                        .body(result);
                } catch (URISyntaxException e) {
                    throw new RuntimeException(e);
                }
            });
    }

    /**
     * {@code PUT  /schedule-authorities/:id} : Updates an existing scheduleAuthority.
     *
     * @param id the id of the scheduleAuthority to save.
     * @param scheduleAuthority the scheduleAuthority to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated scheduleAuthority,
     * or with status {@code 400 (Bad Request)} if the scheduleAuthority is not valid,
     * or with status {@code 500 (Internal Server Error)} if the scheduleAuthority couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/schedule-authorities/{id}")
    public Mono<ResponseEntity<ScheduleAuthority>> updateScheduleAuthority(
        @PathVariable(value = "id", required = false) final Long id,
        @Valid @RequestBody ScheduleAuthority scheduleAuthority
    ) throws URISyntaxException {
        log.debug("REST request to update ScheduleAuthority : {}, {}", id, scheduleAuthority);
        if (scheduleAuthority.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, scheduleAuthority.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        return scheduleAuthorityRepository
            .existsById(id)
            .flatMap(exists -> {
                if (!exists) {
                    return Mono.error(new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound"));
                }

                return scheduleAuthorityService
                    .update(scheduleAuthority)
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
     * {@code PATCH  /schedule-authorities/:id} : Partial updates given fields of an existing scheduleAuthority, field will ignore if it is null
     *
     * @param id the id of the scheduleAuthority to save.
     * @param scheduleAuthority the scheduleAuthority to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated scheduleAuthority,
     * or with status {@code 400 (Bad Request)} if the scheduleAuthority is not valid,
     * or with status {@code 404 (Not Found)} if the scheduleAuthority is not found,
     * or with status {@code 500 (Internal Server Error)} if the scheduleAuthority couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/schedule-authorities/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public Mono<ResponseEntity<ScheduleAuthority>> partialUpdateScheduleAuthority(
        @PathVariable(value = "id", required = false) final Long id,
        @NotNull @RequestBody ScheduleAuthority scheduleAuthority
    ) throws URISyntaxException {
        log.debug("REST request to partial update ScheduleAuthority partially : {}, {}", id, scheduleAuthority);
        if (scheduleAuthority.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, scheduleAuthority.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        return scheduleAuthorityRepository
            .existsById(id)
            .flatMap(exists -> {
                if (!exists) {
                    return Mono.error(new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound"));
                }

                Mono<ScheduleAuthority> result = scheduleAuthorityService.partialUpdate(scheduleAuthority);

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
     * {@code GET  /schedule-authorities} : get all the scheduleAuthorities.
     *
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of scheduleAuthorities in body.
     */
    @GetMapping("/schedule-authorities")
    public Mono<List<ScheduleAuthority>> getAllScheduleAuthorities() {
        log.debug("REST request to get all ScheduleAuthorities");
        return scheduleAuthorityService.findAll().collectList();
    }

    /**
     * {@code GET  /schedule-authorities} : get all the scheduleAuthorities as a stream.
     * @return the {@link Flux} of scheduleAuthorities.
     */
    @GetMapping(value = "/schedule-authorities", produces = MediaType.APPLICATION_NDJSON_VALUE)
    public Flux<ScheduleAuthority> getAllScheduleAuthoritiesAsStream() {
        log.debug("REST request to get all ScheduleAuthorities as a stream");
        return scheduleAuthorityService.findAll();
    }

    /**
     * {@code GET  /schedule-authorities/:id} : get the "id" scheduleAuthority.
     *
     * @param id the id of the scheduleAuthority to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the scheduleAuthority, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/schedule-authorities/{id}")
    public Mono<ResponseEntity<ScheduleAuthority>> getScheduleAuthority(@PathVariable Long id) {
        log.debug("REST request to get ScheduleAuthority : {}", id);
        Mono<ScheduleAuthority> scheduleAuthority = scheduleAuthorityService.findOne(id);
        return ResponseUtil.wrapOrNotFound(scheduleAuthority);
    }

    /**
     * {@code DELETE  /schedule-authorities/:id} : delete the "id" scheduleAuthority.
     *
     * @param id the id of the scheduleAuthority to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/schedule-authorities/{id}")
    @ResponseStatus(code = HttpStatus.NO_CONTENT)
    public Mono<ResponseEntity<Void>> deleteScheduleAuthority(@PathVariable Long id) {
        log.debug("REST request to delete ScheduleAuthority : {}", id);
        return scheduleAuthorityService
            .delete(id)
            .map(result ->
                ResponseEntity
                    .noContent()
                    .headers(HeaderUtil.createEntityDeletionAlert(applicationName, false, ENTITY_NAME, id.toString()))
                    .build()
            );
    }
}
