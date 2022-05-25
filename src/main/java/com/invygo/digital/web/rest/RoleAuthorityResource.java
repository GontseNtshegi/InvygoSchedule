package com.invygo.digital.web.rest;

import com.invygo.digital.domain.RoleAuthority;
import com.invygo.digital.repository.RoleAuthorityRepository;
import com.invygo.digital.service.RoleAuthorityService;
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
 * REST controller for managing {@link com.invygo.digital.domain.RoleAuthority}.
 */

public class RoleAuthorityResource {

    private final Logger log = LoggerFactory.getLogger(RoleAuthorityResource.class);

    private static final String ENTITY_NAME = "stuffSchedulingRoleAuthority";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final RoleAuthorityService roleAuthorityService;

    private final RoleAuthorityRepository roleAuthorityRepository;

    public RoleAuthorityResource(RoleAuthorityService roleAuthorityService, RoleAuthorityRepository roleAuthorityRepository) {
        this.roleAuthorityService = roleAuthorityService;
        this.roleAuthorityRepository = roleAuthorityRepository;
    }

    /**
     * {@code POST  /role-authorities} : Create a new roleAuthority.
     *
     * @param roleAuthority the roleAuthority to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new roleAuthority, or with status {@code 400 (Bad Request)} if the roleAuthority has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("/role-authorities")
    public Mono<ResponseEntity<RoleAuthority>> createRoleAuthority(@Valid @RequestBody RoleAuthority roleAuthority)
        throws URISyntaxException {
        log.debug("REST request to save RoleAuthority : {}", roleAuthority);
        if (roleAuthority.getId() != null) {
            throw new BadRequestAlertException("A new roleAuthority cannot already have an ID", ENTITY_NAME, "idexists");
        }
        return roleAuthorityService
            .save(roleAuthority)
            .map(result -> {
                try {
                    return ResponseEntity
                        .created(new URI("/api/role-authorities/" + result.getId()))
                        .headers(HeaderUtil.createEntityCreationAlert(applicationName, false, ENTITY_NAME, result.getId().toString()))
                        .body(result);
                } catch (URISyntaxException e) {
                    throw new RuntimeException(e);
                }
            });
    }

    /**
     * {@code PUT  /role-authorities/:id} : Updates an existing roleAuthority.
     *
     * @param id the id of the roleAuthority to save.
     * @param roleAuthority the roleAuthority to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated roleAuthority,
     * or with status {@code 400 (Bad Request)} if the roleAuthority is not valid,
     * or with status {@code 500 (Internal Server Error)} if the roleAuthority couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/role-authorities/{id}")
    public Mono<ResponseEntity<RoleAuthority>> updateRoleAuthority(
        @PathVariable(value = "id", required = false) final Long id,
        @Valid @RequestBody RoleAuthority roleAuthority
    ) throws URISyntaxException {
        log.debug("REST request to update RoleAuthority : {}, {}", id, roleAuthority);
        if (roleAuthority.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, roleAuthority.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        return roleAuthorityRepository
            .existsById(id)
            .flatMap(exists -> {
                if (!exists) {
                    return Mono.error(new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound"));
                }

                return roleAuthorityService
                    .update(roleAuthority)
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
     * {@code PATCH  /role-authorities/:id} : Partial updates given fields of an existing roleAuthority, field will ignore if it is null
     *
     * @param id the id of the roleAuthority to save.
     * @param roleAuthority the roleAuthority to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated roleAuthority,
     * or with status {@code 400 (Bad Request)} if the roleAuthority is not valid,
     * or with status {@code 404 (Not Found)} if the roleAuthority is not found,
     * or with status {@code 500 (Internal Server Error)} if the roleAuthority couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/role-authorities/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public Mono<ResponseEntity<RoleAuthority>> partialUpdateRoleAuthority(
        @PathVariable(value = "id", required = false) final Long id,
        @NotNull @RequestBody RoleAuthority roleAuthority
    ) throws URISyntaxException {
        log.debug("REST request to partial update RoleAuthority partially : {}, {}", id, roleAuthority);
        if (roleAuthority.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, roleAuthority.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        return roleAuthorityRepository
            .existsById(id)
            .flatMap(exists -> {
                if (!exists) {
                    return Mono.error(new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound"));
                }

                Mono<RoleAuthority> result = roleAuthorityService.partialUpdate(roleAuthority);

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
     * {@code GET  /role-authorities} : get all the roleAuthorities.
     *
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of roleAuthorities in body.
     */
    @GetMapping("/role-authorities")
    public Mono<List<RoleAuthority>> getAllRoleAuthorities() {
        log.debug("REST request to get all RoleAuthorities");
        return roleAuthorityService.findAll().collectList();
    }

    /**
     * {@code GET  /role-authorities} : get all the roleAuthorities as a stream.
     * @return the {@link Flux} of roleAuthorities.
     */
    @GetMapping(value = "/role-authorities", produces = MediaType.APPLICATION_NDJSON_VALUE)
    public Flux<RoleAuthority> getAllRoleAuthoritiesAsStream() {
        log.debug("REST request to get all RoleAuthorities as a stream");
        return roleAuthorityService.findAll();
    }

    /**
     * {@code GET  /role-authorities/:id} : get the "id" roleAuthority.
     *
     * @param id the id of the roleAuthority to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the roleAuthority, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/role-authorities/{id}")
    public Mono<ResponseEntity<RoleAuthority>> getRoleAuthority(@PathVariable Long id) {
        log.debug("REST request to get RoleAuthority : {}", id);
        Mono<RoleAuthority> roleAuthority = roleAuthorityService.findOne(id);
        return ResponseUtil.wrapOrNotFound(roleAuthority);
    }

    /**
     * {@code DELETE  /role-authorities/:id} : delete the "id" roleAuthority.
     *
     * @param id the id of the roleAuthority to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/role-authorities/{id}")
    @ResponseStatus(code = HttpStatus.NO_CONTENT)
    public Mono<ResponseEntity<Void>> deleteRoleAuthority(@PathVariable Long id) {
        log.debug("REST request to delete RoleAuthority : {}", id);
        return roleAuthorityService
            .delete(id)
            .map(result ->
                ResponseEntity
                    .noContent()
                    .headers(HeaderUtil.createEntityDeletionAlert(applicationName, false, ENTITY_NAME, id.toString()))
                    .build()
            );
    }
}
