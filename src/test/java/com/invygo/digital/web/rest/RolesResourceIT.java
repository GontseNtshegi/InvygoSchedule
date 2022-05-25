package com.invygo.digital.web.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.is;

import com.invygo.digital.IntegrationTest;
import com.invygo.digital.domain.Roles;
import com.invygo.digital.repository.EntityManager;
import com.invygo.digital.repository.RolesRepository;
import java.time.Duration;
import java.util.List;
import java.util.Random;
import java.util.concurrent.atomic.AtomicLong;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.reactive.server.WebTestClient;

/**
 * Integration tests for the {@link RolesResource} REST controller.
 */
@IntegrationTest
@AutoConfigureWebTestClient(timeout = IntegrationTest.DEFAULT_ENTITY_TIMEOUT)
@WithMockUser
class RolesResourceIT {

    private static final String DEFAULT_ROLE_NAME = "AAAAAAAAAA";
    private static final String UPDATED_ROLE_NAME = "BBBBBBBBBB";

    private static final String ENTITY_API_URL = "/api/roles";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong count = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private RolesRepository rolesRepository;

    @Autowired
    private EntityManager em;

    @Autowired
    private WebTestClient webTestClient;

    private Roles roles;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Roles createEntity(EntityManager em) {
        Roles roles = new Roles().roleName(DEFAULT_ROLE_NAME);
        return roles;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Roles createUpdatedEntity(EntityManager em) {
        Roles roles = new Roles().roleName(UPDATED_ROLE_NAME);
        return roles;
    }

    public static void deleteEntities(EntityManager em) {
        try {
            em.deleteAll(Roles.class).block();
        } catch (Exception e) {
            // It can fail, if other entities are still referring this - it will be removed later.
        }
    }

    @AfterEach
    public void cleanup() {
        deleteEntities(em);
    }

    @BeforeEach
    public void initTest() {
        deleteEntities(em);
        roles = createEntity(em);
    }

    @Test
    void createRoles() throws Exception {
        int databaseSizeBeforeCreate = rolesRepository.findAll().collectList().block().size();
        // Create the Roles
        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(roles))
            .exchange()
            .expectStatus()
            .isCreated();

        // Validate the Roles in the database
        List<Roles> rolesList = rolesRepository.findAll().collectList().block();
        assertThat(rolesList).hasSize(databaseSizeBeforeCreate + 1);
        Roles testRoles = rolesList.get(rolesList.size() - 1);
        assertThat(testRoles.getRoleName()).isEqualTo(DEFAULT_ROLE_NAME);
    }

    @Test
    void createRolesWithExistingId() throws Exception {
        // Create the Roles with an existing ID
        roles.setId(1L);

        int databaseSizeBeforeCreate = rolesRepository.findAll().collectList().block().size();

        // An entity with an existing ID cannot be created, so this API call must fail
        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(roles))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Roles in the database
        List<Roles> rolesList = rolesRepository.findAll().collectList().block();
        assertThat(rolesList).hasSize(databaseSizeBeforeCreate);
    }

    @Test
    void checkRoleNameIsRequired() throws Exception {
        int databaseSizeBeforeTest = rolesRepository.findAll().collectList().block().size();
        // set the field null
        roles.setRoleName(null);

        // Create the Roles, which fails.

        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(roles))
            .exchange()
            .expectStatus()
            .isBadRequest();

        List<Roles> rolesList = rolesRepository.findAll().collectList().block();
        assertThat(rolesList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    void getAllRolesAsStream() {
        // Initialize the database
        rolesRepository.save(roles).block();

        List<Roles> rolesList = webTestClient
            .get()
            .uri(ENTITY_API_URL)
            .accept(MediaType.APPLICATION_NDJSON)
            .exchange()
            .expectStatus()
            .isOk()
            .expectHeader()
            .contentTypeCompatibleWith(MediaType.APPLICATION_NDJSON)
            .returnResult(Roles.class)
            .getResponseBody()
            .filter(roles::equals)
            .collectList()
            .block(Duration.ofSeconds(5));

        assertThat(rolesList).isNotNull();
        assertThat(rolesList).hasSize(1);
        Roles testRoles = rolesList.get(0);
        assertThat(testRoles.getRoleName()).isEqualTo(DEFAULT_ROLE_NAME);
    }

    @Test
    void getAllRoles() {
        // Initialize the database
        rolesRepository.save(roles).block();

        // Get all the rolesList
        webTestClient
            .get()
            .uri(ENTITY_API_URL + "?sort=id,desc")
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isOk()
            .expectHeader()
            .contentType(MediaType.APPLICATION_JSON)
            .expectBody()
            .jsonPath("$.[*].id")
            .value(hasItem(roles.getId().intValue()))
            .jsonPath("$.[*].roleName")
            .value(hasItem(DEFAULT_ROLE_NAME));
    }

    @Test
    void getRoles() {
        // Initialize the database
        rolesRepository.save(roles).block();

        // Get the roles
        webTestClient
            .get()
            .uri(ENTITY_API_URL_ID, roles.getId())
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isOk()
            .expectHeader()
            .contentType(MediaType.APPLICATION_JSON)
            .expectBody()
            .jsonPath("$.id")
            .value(is(roles.getId().intValue()))
            .jsonPath("$.roleName")
            .value(is(DEFAULT_ROLE_NAME));
    }

    @Test
    void getNonExistingRoles() {
        // Get the roles
        webTestClient
            .get()
            .uri(ENTITY_API_URL_ID, Long.MAX_VALUE)
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isNotFound();
    }

    @Test
    void putNewRoles() throws Exception {
        // Initialize the database
        rolesRepository.save(roles).block();

        int databaseSizeBeforeUpdate = rolesRepository.findAll().collectList().block().size();

        // Update the roles
        Roles updatedRoles = rolesRepository.findById(roles.getId()).block();
        updatedRoles.roleName(UPDATED_ROLE_NAME);

        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, updatedRoles.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(updatedRoles))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the Roles in the database
        List<Roles> rolesList = rolesRepository.findAll().collectList().block();
        assertThat(rolesList).hasSize(databaseSizeBeforeUpdate);
        Roles testRoles = rolesList.get(rolesList.size() - 1);
        assertThat(testRoles.getRoleName()).isEqualTo(UPDATED_ROLE_NAME);
    }

    @Test
    void putNonExistingRoles() throws Exception {
        int databaseSizeBeforeUpdate = rolesRepository.findAll().collectList().block().size();
        roles.setId(count.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, roles.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(roles))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Roles in the database
        List<Roles> rolesList = rolesRepository.findAll().collectList().block();
        assertThat(rolesList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void putWithIdMismatchRoles() throws Exception {
        int databaseSizeBeforeUpdate = rolesRepository.findAll().collectList().block().size();
        roles.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, count.incrementAndGet())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(roles))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Roles in the database
        List<Roles> rolesList = rolesRepository.findAll().collectList().block();
        assertThat(rolesList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void putWithMissingIdPathParamRoles() throws Exception {
        int databaseSizeBeforeUpdate = rolesRepository.findAll().collectList().block().size();
        roles.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(roles))
            .exchange()
            .expectStatus()
            .isEqualTo(405);

        // Validate the Roles in the database
        List<Roles> rolesList = rolesRepository.findAll().collectList().block();
        assertThat(rolesList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void partialUpdateRolesWithPatch() throws Exception {
        // Initialize the database
        rolesRepository.save(roles).block();

        int databaseSizeBeforeUpdate = rolesRepository.findAll().collectList().block().size();

        // Update the roles using partial update
        Roles partialUpdatedRoles = new Roles();
        partialUpdatedRoles.setId(roles.getId());

        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, partialUpdatedRoles.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(partialUpdatedRoles))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the Roles in the database
        List<Roles> rolesList = rolesRepository.findAll().collectList().block();
        assertThat(rolesList).hasSize(databaseSizeBeforeUpdate);
        Roles testRoles = rolesList.get(rolesList.size() - 1);
        assertThat(testRoles.getRoleName()).isEqualTo(DEFAULT_ROLE_NAME);
    }

    @Test
    void fullUpdateRolesWithPatch() throws Exception {
        // Initialize the database
        rolesRepository.save(roles).block();

        int databaseSizeBeforeUpdate = rolesRepository.findAll().collectList().block().size();

        // Update the roles using partial update
        Roles partialUpdatedRoles = new Roles();
        partialUpdatedRoles.setId(roles.getId());

        partialUpdatedRoles.roleName(UPDATED_ROLE_NAME);

        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, partialUpdatedRoles.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(partialUpdatedRoles))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the Roles in the database
        List<Roles> rolesList = rolesRepository.findAll().collectList().block();
        assertThat(rolesList).hasSize(databaseSizeBeforeUpdate);
        Roles testRoles = rolesList.get(rolesList.size() - 1);
        assertThat(testRoles.getRoleName()).isEqualTo(UPDATED_ROLE_NAME);
    }

    @Test
    void patchNonExistingRoles() throws Exception {
        int databaseSizeBeforeUpdate = rolesRepository.findAll().collectList().block().size();
        roles.setId(count.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, roles.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(roles))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Roles in the database
        List<Roles> rolesList = rolesRepository.findAll().collectList().block();
        assertThat(rolesList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void patchWithIdMismatchRoles() throws Exception {
        int databaseSizeBeforeUpdate = rolesRepository.findAll().collectList().block().size();
        roles.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, count.incrementAndGet())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(roles))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Roles in the database
        List<Roles> rolesList = rolesRepository.findAll().collectList().block();
        assertThat(rolesList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void patchWithMissingIdPathParamRoles() throws Exception {
        int databaseSizeBeforeUpdate = rolesRepository.findAll().collectList().block().size();
        roles.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(roles))
            .exchange()
            .expectStatus()
            .isEqualTo(405);

        // Validate the Roles in the database
        List<Roles> rolesList = rolesRepository.findAll().collectList().block();
        assertThat(rolesList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void deleteRoles() {
        // Initialize the database
        rolesRepository.save(roles).block();

        int databaseSizeBeforeDelete = rolesRepository.findAll().collectList().block().size();

        // Delete the roles
        webTestClient
            .delete()
            .uri(ENTITY_API_URL_ID, roles.getId())
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isNoContent();

        // Validate the database contains one less item
        List<Roles> rolesList = rolesRepository.findAll().collectList().block();
        assertThat(rolesList).hasSize(databaseSizeBeforeDelete - 1);
    }
}
