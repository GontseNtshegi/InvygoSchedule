package com.invygo.digital.web.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.is;

import com.invygo.digital.IntegrationTest;
import com.invygo.digital.domain.RoleAuthority;
import com.invygo.digital.repository.EntityManager;
import com.invygo.digital.repository.RoleAuthorityRepository;
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
 * Integration tests for the {@link RoleAuthorityResource} REST controller.
 */
@IntegrationTest
@AutoConfigureWebTestClient(timeout = IntegrationTest.DEFAULT_ENTITY_TIMEOUT)
@WithMockUser
class RoleAuthorityResourceIT {

    private static final Long DEFAULT_USER_ID = 1L;
    private static final Long UPDATED_USER_ID = 2L;

    private static final Long DEFAULT_ROLE_ID = 1L;
    private static final Long UPDATED_ROLE_ID = 2L;

    private static final String ENTITY_API_URL = "/api/role-authorities";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong count = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private RoleAuthorityRepository roleAuthorityRepository;

    @Autowired
    private EntityManager em;

    @Autowired
    private WebTestClient webTestClient;

    private RoleAuthority roleAuthority;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static RoleAuthority createEntity(EntityManager em) {
        RoleAuthority roleAuthority = new RoleAuthority().userId(DEFAULT_USER_ID).roleId(DEFAULT_ROLE_ID);
        return roleAuthority;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static RoleAuthority createUpdatedEntity(EntityManager em) {
        RoleAuthority roleAuthority = new RoleAuthority().userId(UPDATED_USER_ID).roleId(UPDATED_ROLE_ID);
        return roleAuthority;
    }

    public static void deleteEntities(EntityManager em) {
        try {
            em.deleteAll(RoleAuthority.class).block();
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
        roleAuthority = createEntity(em);
    }

    @Test
    void createRoleAuthority() throws Exception {
        int databaseSizeBeforeCreate = roleAuthorityRepository.findAll().collectList().block().size();
        // Create the RoleAuthority
        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(roleAuthority))
            .exchange()
            .expectStatus()
            .isCreated();

        // Validate the RoleAuthority in the database
        List<RoleAuthority> roleAuthorityList = roleAuthorityRepository.findAll().collectList().block();
        assertThat(roleAuthorityList).hasSize(databaseSizeBeforeCreate + 1);
        RoleAuthority testRoleAuthority = roleAuthorityList.get(roleAuthorityList.size() - 1);
        assertThat(testRoleAuthority.getUserId()).isEqualTo(DEFAULT_USER_ID);
        assertThat(testRoleAuthority.getRoleId()).isEqualTo(DEFAULT_ROLE_ID);
    }

    @Test
    void createRoleAuthorityWithExistingId() throws Exception {
        // Create the RoleAuthority with an existing ID
        roleAuthority.setId(1L);

        int databaseSizeBeforeCreate = roleAuthorityRepository.findAll().collectList().block().size();

        // An entity with an existing ID cannot be created, so this API call must fail
        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(roleAuthority))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the RoleAuthority in the database
        List<RoleAuthority> roleAuthorityList = roleAuthorityRepository.findAll().collectList().block();
        assertThat(roleAuthorityList).hasSize(databaseSizeBeforeCreate);
    }

    @Test
    void checkUserIdIsRequired() throws Exception {
        int databaseSizeBeforeTest = roleAuthorityRepository.findAll().collectList().block().size();
        // set the field null
        roleAuthority.setUserId(null);

        // Create the RoleAuthority, which fails.

        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(roleAuthority))
            .exchange()
            .expectStatus()
            .isBadRequest();

        List<RoleAuthority> roleAuthorityList = roleAuthorityRepository.findAll().collectList().block();
        assertThat(roleAuthorityList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    void checkRoleIdIsRequired() throws Exception {
        int databaseSizeBeforeTest = roleAuthorityRepository.findAll().collectList().block().size();
        // set the field null
        roleAuthority.setRoleId(null);

        // Create the RoleAuthority, which fails.

        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(roleAuthority))
            .exchange()
            .expectStatus()
            .isBadRequest();

        List<RoleAuthority> roleAuthorityList = roleAuthorityRepository.findAll().collectList().block();
        assertThat(roleAuthorityList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    void getAllRoleAuthoritiesAsStream() {
        // Initialize the database
        roleAuthorityRepository.save(roleAuthority).block();

        List<RoleAuthority> roleAuthorityList = webTestClient
            .get()
            .uri(ENTITY_API_URL)
            .accept(MediaType.APPLICATION_NDJSON)
            .exchange()
            .expectStatus()
            .isOk()
            .expectHeader()
            .contentTypeCompatibleWith(MediaType.APPLICATION_NDJSON)
            .returnResult(RoleAuthority.class)
            .getResponseBody()
            .filter(roleAuthority::equals)
            .collectList()
            .block(Duration.ofSeconds(5));

        assertThat(roleAuthorityList).isNotNull();
        assertThat(roleAuthorityList).hasSize(1);
        RoleAuthority testRoleAuthority = roleAuthorityList.get(0);
        assertThat(testRoleAuthority.getUserId()).isEqualTo(DEFAULT_USER_ID);
        assertThat(testRoleAuthority.getRoleId()).isEqualTo(DEFAULT_ROLE_ID);
    }

    @Test
    void getAllRoleAuthorities() {
        // Initialize the database
        roleAuthorityRepository.save(roleAuthority).block();

        // Get all the roleAuthorityList
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
            .value(hasItem(roleAuthority.getId().intValue()))
            .jsonPath("$.[*].userId")
            .value(hasItem(DEFAULT_USER_ID.intValue()))
            .jsonPath("$.[*].roleId")
            .value(hasItem(DEFAULT_ROLE_ID.intValue()));
    }

    @Test
    void getRoleAuthority() {
        // Initialize the database
        roleAuthorityRepository.save(roleAuthority).block();

        // Get the roleAuthority
        webTestClient
            .get()
            .uri(ENTITY_API_URL_ID, roleAuthority.getId())
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isOk()
            .expectHeader()
            .contentType(MediaType.APPLICATION_JSON)
            .expectBody()
            .jsonPath("$.id")
            .value(is(roleAuthority.getId().intValue()))
            .jsonPath("$.userId")
            .value(is(DEFAULT_USER_ID.intValue()))
            .jsonPath("$.roleId")
            .value(is(DEFAULT_ROLE_ID.intValue()));
    }

    @Test
    void getNonExistingRoleAuthority() {
        // Get the roleAuthority
        webTestClient
            .get()
            .uri(ENTITY_API_URL_ID, Long.MAX_VALUE)
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isNotFound();
    }

    @Test
    void putNewRoleAuthority() throws Exception {
        // Initialize the database
        roleAuthorityRepository.save(roleAuthority).block();

        int databaseSizeBeforeUpdate = roleAuthorityRepository.findAll().collectList().block().size();

        // Update the roleAuthority
        RoleAuthority updatedRoleAuthority = roleAuthorityRepository.findById(roleAuthority.getId()).block();
        updatedRoleAuthority.userId(UPDATED_USER_ID).roleId(UPDATED_ROLE_ID);

        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, updatedRoleAuthority.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(updatedRoleAuthority))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the RoleAuthority in the database
        List<RoleAuthority> roleAuthorityList = roleAuthorityRepository.findAll().collectList().block();
        assertThat(roleAuthorityList).hasSize(databaseSizeBeforeUpdate);
        RoleAuthority testRoleAuthority = roleAuthorityList.get(roleAuthorityList.size() - 1);
        assertThat(testRoleAuthority.getUserId()).isEqualTo(UPDATED_USER_ID);
        assertThat(testRoleAuthority.getRoleId()).isEqualTo(UPDATED_ROLE_ID);
    }

    @Test
    void putNonExistingRoleAuthority() throws Exception {
        int databaseSizeBeforeUpdate = roleAuthorityRepository.findAll().collectList().block().size();
        roleAuthority.setId(count.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, roleAuthority.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(roleAuthority))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the RoleAuthority in the database
        List<RoleAuthority> roleAuthorityList = roleAuthorityRepository.findAll().collectList().block();
        assertThat(roleAuthorityList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void putWithIdMismatchRoleAuthority() throws Exception {
        int databaseSizeBeforeUpdate = roleAuthorityRepository.findAll().collectList().block().size();
        roleAuthority.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, count.incrementAndGet())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(roleAuthority))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the RoleAuthority in the database
        List<RoleAuthority> roleAuthorityList = roleAuthorityRepository.findAll().collectList().block();
        assertThat(roleAuthorityList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void putWithMissingIdPathParamRoleAuthority() throws Exception {
        int databaseSizeBeforeUpdate = roleAuthorityRepository.findAll().collectList().block().size();
        roleAuthority.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(roleAuthority))
            .exchange()
            .expectStatus()
            .isEqualTo(405);

        // Validate the RoleAuthority in the database
        List<RoleAuthority> roleAuthorityList = roleAuthorityRepository.findAll().collectList().block();
        assertThat(roleAuthorityList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void partialUpdateRoleAuthorityWithPatch() throws Exception {
        // Initialize the database
        roleAuthorityRepository.save(roleAuthority).block();

        int databaseSizeBeforeUpdate = roleAuthorityRepository.findAll().collectList().block().size();

        // Update the roleAuthority using partial update
        RoleAuthority partialUpdatedRoleAuthority = new RoleAuthority();
        partialUpdatedRoleAuthority.setId(roleAuthority.getId());

        partialUpdatedRoleAuthority.userId(UPDATED_USER_ID);

        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, partialUpdatedRoleAuthority.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(partialUpdatedRoleAuthority))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the RoleAuthority in the database
        List<RoleAuthority> roleAuthorityList = roleAuthorityRepository.findAll().collectList().block();
        assertThat(roleAuthorityList).hasSize(databaseSizeBeforeUpdate);
        RoleAuthority testRoleAuthority = roleAuthorityList.get(roleAuthorityList.size() - 1);
        assertThat(testRoleAuthority.getUserId()).isEqualTo(UPDATED_USER_ID);
        assertThat(testRoleAuthority.getRoleId()).isEqualTo(DEFAULT_ROLE_ID);
    }

    @Test
    void fullUpdateRoleAuthorityWithPatch() throws Exception {
        // Initialize the database
        roleAuthorityRepository.save(roleAuthority).block();

        int databaseSizeBeforeUpdate = roleAuthorityRepository.findAll().collectList().block().size();

        // Update the roleAuthority using partial update
        RoleAuthority partialUpdatedRoleAuthority = new RoleAuthority();
        partialUpdatedRoleAuthority.setId(roleAuthority.getId());

        partialUpdatedRoleAuthority.userId(UPDATED_USER_ID).roleId(UPDATED_ROLE_ID);

        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, partialUpdatedRoleAuthority.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(partialUpdatedRoleAuthority))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the RoleAuthority in the database
        List<RoleAuthority> roleAuthorityList = roleAuthorityRepository.findAll().collectList().block();
        assertThat(roleAuthorityList).hasSize(databaseSizeBeforeUpdate);
        RoleAuthority testRoleAuthority = roleAuthorityList.get(roleAuthorityList.size() - 1);
        assertThat(testRoleAuthority.getUserId()).isEqualTo(UPDATED_USER_ID);
        assertThat(testRoleAuthority.getRoleId()).isEqualTo(UPDATED_ROLE_ID);
    }

    @Test
    void patchNonExistingRoleAuthority() throws Exception {
        int databaseSizeBeforeUpdate = roleAuthorityRepository.findAll().collectList().block().size();
        roleAuthority.setId(count.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, roleAuthority.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(roleAuthority))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the RoleAuthority in the database
        List<RoleAuthority> roleAuthorityList = roleAuthorityRepository.findAll().collectList().block();
        assertThat(roleAuthorityList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void patchWithIdMismatchRoleAuthority() throws Exception {
        int databaseSizeBeforeUpdate = roleAuthorityRepository.findAll().collectList().block().size();
        roleAuthority.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, count.incrementAndGet())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(roleAuthority))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the RoleAuthority in the database
        List<RoleAuthority> roleAuthorityList = roleAuthorityRepository.findAll().collectList().block();
        assertThat(roleAuthorityList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void patchWithMissingIdPathParamRoleAuthority() throws Exception {
        int databaseSizeBeforeUpdate = roleAuthorityRepository.findAll().collectList().block().size();
        roleAuthority.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(roleAuthority))
            .exchange()
            .expectStatus()
            .isEqualTo(405);

        // Validate the RoleAuthority in the database
        List<RoleAuthority> roleAuthorityList = roleAuthorityRepository.findAll().collectList().block();
        assertThat(roleAuthorityList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void deleteRoleAuthority() {
        // Initialize the database
        roleAuthorityRepository.save(roleAuthority).block();

        int databaseSizeBeforeDelete = roleAuthorityRepository.findAll().collectList().block().size();

        // Delete the roleAuthority
        webTestClient
            .delete()
            .uri(ENTITY_API_URL_ID, roleAuthority.getId())
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isNoContent();

        // Validate the database contains one less item
        List<RoleAuthority> roleAuthorityList = roleAuthorityRepository.findAll().collectList().block();
        assertThat(roleAuthorityList).hasSize(databaseSizeBeforeDelete - 1);
    }
}
