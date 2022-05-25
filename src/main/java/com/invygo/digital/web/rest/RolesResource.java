package com.invygo.digital.web.rest;

import com.invygo.digital.domain.Roles;
import com.invygo.digital.repository.RolesRepository;
import com.invygo.digital.service.RolesService;
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
 * REST controller for managing {@link com.invygo.digital.domain.Roles}.
 */
@RestController
@RequestMapping("/api")
public class RolesResource {

    private final Logger log = LoggerFactory.getLogger(RolesResource.class);

    private static final String ENTITY_NAME = "stuffSchedulingRoles";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final RolesService rolesService;

    private final RolesRepository rolesRepository;

    public RolesResource(RolesService rolesService, RolesRepository rolesRepository) {
        this.rolesService = rolesService;
        this.rolesRepository = rolesRepository;
    }

    /**
     * {@code POST  /roles} : Create a new roles.
     *
     * @param roles the roles to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new roles, or with status {@code 400 (Bad Request)} if the roles has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("/roles")
    public Mono<ResponseEntity<Roles>> createRoles(@Valid @RequestBody Roles roles) throws URISyntaxException {
        log.debug("REST request to save Roles : {}", roles);
        if (roles.getId() != null) {
            throw new BadRequestAlertException("A new roles cannot already have an ID", ENTITY_NAME, "idexists");
        }
        return rolesService
            .save(roles)
            .map(result -> {
                try {
                    return ResponseEntity
                        .created(new URI("/api/roles/" + result.getId()))
                        .headers(HeaderUtil.createEntityCreationAlert(applicationName, false, ENTITY_NAME, result.getId().toString()))
                        .body(result);
                } catch (URISyntaxException e) {
                    throw new RuntimeException(e);
                }
            });
    }

    /**
     * {@code PUT  /roles/:id} : Updates an existing roles.
     *
     * @param id the id of the roles to save.
     * @param roles the roles to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated roles,
     * or with status {@code 400 (Bad Request)} if the roles is not valid,
     * or with status {@code 500 (Internal Server Error)} if the roles couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/roles/{id}")
    public Mono<ResponseEntity<Roles>> updateRoles(
        @PathVariable(value = "id", required = false) final Long id,
        @Valid @RequestBody Roles roles
    ) throws URISyntaxException {
        log.debug("REST request to update Roles : {}, {}", id, roles);
        if (roles.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, roles.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        return rolesRepository
            .existsById(id)
            .flatMap(exists -> {
                if (!exists) {
                    return Mono.error(new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound"));
                }

                return rolesService
                    .update(roles)
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
     * {@code PATCH  /roles/:id} : Partial updates given fields of an existing roles, field will ignore if it is null
     *
     * @param id the id of the roles to save.
     * @param roles the roles to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated roles,
     * or with status {@code 400 (Bad Request)} if the roles is not valid,
     * or with status {@code 404 (Not Found)} if the roles is not found,
     * or with status {@code 500 (Internal Server Error)} if the roles couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/roles/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public Mono<ResponseEntity<Roles>> partialUpdateRoles(
        @PathVariable(value = "id", required = false) final Long id,
        @NotNull @RequestBody Roles roles
    ) throws URISyntaxException {
        log.debug("REST request to partial update Roles partially : {}, {}", id, roles);
        if (roles.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, roles.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        return rolesRepository
            .existsById(id)
            .flatMap(exists -> {
                if (!exists) {
                    return Mono.error(new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound"));
                }

                Mono<Roles> result = rolesService.partialUpdate(roles);

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
     * {@code GET  /roles} : get all the roles.
     *
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of roles in body.
     */
    @GetMapping("/roles")
    public Mono<List<Roles>> getAllRoles() {
        log.debug("REST request to get all Roles");
        return rolesService.findAll().collectList();
    }

    /**
     * {@code GET  /roles} : get all the roles as a stream.
     * @return the {@link Flux} of roles.
     */
    @GetMapping(value = "/roles", produces = MediaType.APPLICATION_NDJSON_VALUE)
    public Flux<Roles> getAllRolesAsStream() {
        log.debug("REST request to get all Roles as a stream");
        return rolesService.findAll();
    }

    /**
     * {@code GET  /roles/:id} : get the "id" roles.
     *
     * @param id the id of the roles to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the roles, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/roles/{id}")
    public Mono<ResponseEntity<Roles>> getRoles(@PathVariable Long id) {
        log.debug("REST request to get Roles : {}", id);
        Mono<Roles> roles = rolesService.findOne(id);
        return ResponseUtil.wrapOrNotFound(roles);
    }

    /**
     * {@code DELETE  /roles/:id} : delete the "id" roles.
     *
     * @param id the id of the roles to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/roles/{id}")
    @ResponseStatus(code = HttpStatus.NO_CONTENT)
    public Mono<ResponseEntity<Void>> deleteRoles(@PathVariable Long id) {
        log.debug("REST request to delete Roles : {}", id);
        return rolesService
            .delete(id)
            .map(result ->
                ResponseEntity
                    .noContent()
                    .headers(HeaderUtil.createEntityDeletionAlert(applicationName, false, ENTITY_NAME, id.toString()))
                    .build()
            );
    }
}
