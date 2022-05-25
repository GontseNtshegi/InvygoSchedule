package com.invygo.digital.web.rest;

import com.invygo.digital.domain.UserAuthority;
import com.invygo.digital.repository.UserAuthorityRepository;
import com.invygo.digital.service.UserAuthorityService;
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
 * REST controller for managing {@link com.invygo.digital.domain.UserAuthority}.
 */
@RestController
@RequestMapping("/api")
public class UserAuthorityResource {

    private final Logger log = LoggerFactory.getLogger(UserAuthorityResource.class);

    private static final String ENTITY_NAME = "stuffSchedulingUserAuthority";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final UserAuthorityService userAuthorityService;

    private final UserAuthorityRepository userAuthorityRepository;

    public UserAuthorityResource(UserAuthorityService userAuthorityService, UserAuthorityRepository userAuthorityRepository) {
        this.userAuthorityService = userAuthorityService;
        this.userAuthorityRepository = userAuthorityRepository;
    }

    /**
     * {@code POST  /user-authorities} : Create a new userAuthority.
     *
     * @param userAuthority the userAuthority to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new userAuthority, or with status {@code 400 (Bad Request)} if the userAuthority has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("/user-authorities")
    public Mono<ResponseEntity<UserAuthority>> createUserAuthority(@Valid @RequestBody UserAuthority userAuthority)
        throws URISyntaxException {
        log.debug("REST request to save UserAuthority : {}", userAuthority);
        if (userAuthority.getId() != null) {
            throw new BadRequestAlertException("A new userAuthority cannot already have an ID", ENTITY_NAME, "idexists");
        }
        return userAuthorityService
            .save(userAuthority)
            .map(result -> {
                try {
                    return ResponseEntity
                        .created(new URI("/api/user-authorities/" + result.getId()))
                        .headers(HeaderUtil.createEntityCreationAlert(applicationName, false, ENTITY_NAME, result.getId().toString()))
                        .body(result);
                } catch (URISyntaxException e) {
                    throw new RuntimeException(e);
                }
            });
    }

    /**
     * {@code PUT  /user-authorities/:id} : Updates an existing userAuthority.
     *
     * @param id the id of the userAuthority to save.
     * @param userAuthority the userAuthority to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated userAuthority,
     * or with status {@code 400 (Bad Request)} if the userAuthority is not valid,
     * or with status {@code 500 (Internal Server Error)} if the userAuthority couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/user-authorities/{id}")
    public Mono<ResponseEntity<UserAuthority>> updateUserAuthority(
        @PathVariable(value = "id", required = false) final Long id,
        @Valid @RequestBody UserAuthority userAuthority
    ) throws URISyntaxException {
        log.debug("REST request to update UserAuthority : {}, {}", id, userAuthority);
        if (userAuthority.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, userAuthority.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        return userAuthorityRepository
            .existsById(id)
            .flatMap(exists -> {
                if (!exists) {
                    return Mono.error(new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound"));
                }

                return userAuthorityService
                    .update(userAuthority)
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
     * {@code PATCH  /user-authorities/:id} : Partial updates given fields of an existing userAuthority, field will ignore if it is null
     *
     * @param id the id of the userAuthority to save.
     * @param userAuthority the userAuthority to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated userAuthority,
     * or with status {@code 400 (Bad Request)} if the userAuthority is not valid,
     * or with status {@code 404 (Not Found)} if the userAuthority is not found,
     * or with status {@code 500 (Internal Server Error)} if the userAuthority couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/user-authorities/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public Mono<ResponseEntity<UserAuthority>> partialUpdateUserAuthority(
        @PathVariable(value = "id", required = false) final Long id,
        @NotNull @RequestBody UserAuthority userAuthority
    ) throws URISyntaxException {
        log.debug("REST request to partial update UserAuthority partially : {}, {}", id, userAuthority);
        if (userAuthority.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, userAuthority.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        return userAuthorityRepository
            .existsById(id)
            .flatMap(exists -> {
                if (!exists) {
                    return Mono.error(new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound"));
                }

                Mono<UserAuthority> result = userAuthorityService.partialUpdate(userAuthority);

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
     * {@code GET  /user-authorities} : get all the userAuthorities.
     *
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of userAuthorities in body.
     */
    @GetMapping("/user-authorities")
    public Mono<List<UserAuthority>> getAllUserAuthorities() {
        log.debug("REST request to get all UserAuthorities");
        return userAuthorityService.findAll().collectList();
    }

    /**
     * {@code GET  /user-authorities} : get all the userAuthorities as a stream.
     * @return the {@link Flux} of userAuthorities.
     */
    @GetMapping(value = "/user-authorities", produces = MediaType.APPLICATION_NDJSON_VALUE)
    public Flux<UserAuthority> getAllUserAuthoritiesAsStream() {
        log.debug("REST request to get all UserAuthorities as a stream");
        return userAuthorityService.findAll();
    }

    /**
     * {@code GET  /user-authorities/:id} : get the "id" userAuthority.
     *
     * @param id the id of the userAuthority to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the userAuthority, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/user-authorities/{id}")
    public Mono<ResponseEntity<UserAuthority>> getUserAuthority(@PathVariable Long id) {
        log.debug("REST request to get UserAuthority : {}", id);
        Mono<UserAuthority> userAuthority = userAuthorityService.findOne(id);
        return ResponseUtil.wrapOrNotFound(userAuthority);
    }

    /**
     * {@code DELETE  /user-authorities/:id} : delete the "id" userAuthority.
     *
     * @param id the id of the userAuthority to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/user-authorities/{id}")
    @ResponseStatus(code = HttpStatus.NO_CONTENT)
    public Mono<ResponseEntity<Void>> deleteUserAuthority(@PathVariable Long id) {
        log.debug("REST request to delete UserAuthority : {}", id);
        return userAuthorityService
            .delete(id)
            .map(result ->
                ResponseEntity
                    .noContent()
                    .headers(HeaderUtil.createEntityDeletionAlert(applicationName, false, ENTITY_NAME, id.toString()))
                    .build()
            );
    }
}
