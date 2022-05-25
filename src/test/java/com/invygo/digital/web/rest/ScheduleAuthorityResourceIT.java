package com.invygo.digital.web.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.is;

import com.invygo.digital.IntegrationTest;
import com.invygo.digital.domain.ScheduleAuthority;
import com.invygo.digital.repository.EntityManager;
import com.invygo.digital.repository.ScheduleAuthorityRepository;
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
 * Integration tests for the {@link ScheduleAuthorityResource} REST controller.
 */
@IntegrationTest
@AutoConfigureWebTestClient(timeout = IntegrationTest.DEFAULT_ENTITY_TIMEOUT)
@WithMockUser
class ScheduleAuthorityResourceIT {

    private static final String DEFAULT_NAME = "AAAAAAAAAA";
    private static final String UPDATED_NAME = "BBBBBBBBBB";

    private static final String ENTITY_API_URL = "/api/schedule-authorities";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong count = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private ScheduleAuthorityRepository scheduleAuthorityRepository;

    @Autowired
    private EntityManager em;

    @Autowired
    private WebTestClient webTestClient;

    private ScheduleAuthority scheduleAuthority;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static ScheduleAuthority createEntity(EntityManager em) {
        ScheduleAuthority scheduleAuthority = new ScheduleAuthority().name(DEFAULT_NAME);
        return scheduleAuthority;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static ScheduleAuthority createUpdatedEntity(EntityManager em) {
        ScheduleAuthority scheduleAuthority = new ScheduleAuthority().name(UPDATED_NAME);
        return scheduleAuthority;
    }

    public static void deleteEntities(EntityManager em) {
        try {
            em.deleteAll(ScheduleAuthority.class).block();
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
        scheduleAuthority = createEntity(em);
    }

    @Test
    void createScheduleAuthority() throws Exception {
        int databaseSizeBeforeCreate = scheduleAuthorityRepository.findAll().collectList().block().size();
        // Create the ScheduleAuthority
        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(scheduleAuthority))
            .exchange()
            .expectStatus()
            .isCreated();

        // Validate the ScheduleAuthority in the database
        List<ScheduleAuthority> scheduleAuthorityList = scheduleAuthorityRepository.findAll().collectList().block();
        assertThat(scheduleAuthorityList).hasSize(databaseSizeBeforeCreate + 1);
        ScheduleAuthority testScheduleAuthority = scheduleAuthorityList.get(scheduleAuthorityList.size() - 1);
        assertThat(testScheduleAuthority.getName()).isEqualTo(DEFAULT_NAME);
    }

    @Test
    void createScheduleAuthorityWithExistingId() throws Exception {
        // Create the ScheduleAuthority with an existing ID
        scheduleAuthority.setId(1L);

        int databaseSizeBeforeCreate = scheduleAuthorityRepository.findAll().collectList().block().size();

        // An entity with an existing ID cannot be created, so this API call must fail
        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(scheduleAuthority))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the ScheduleAuthority in the database
        List<ScheduleAuthority> scheduleAuthorityList = scheduleAuthorityRepository.findAll().collectList().block();
        assertThat(scheduleAuthorityList).hasSize(databaseSizeBeforeCreate);
    }

    @Test
    void checkNameIsRequired() throws Exception {
        int databaseSizeBeforeTest = scheduleAuthorityRepository.findAll().collectList().block().size();
        // set the field null
        scheduleAuthority.setName(null);

        // Create the ScheduleAuthority, which fails.

        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(scheduleAuthority))
            .exchange()
            .expectStatus()
            .isBadRequest();

        List<ScheduleAuthority> scheduleAuthorityList = scheduleAuthorityRepository.findAll().collectList().block();
        assertThat(scheduleAuthorityList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    void getAllScheduleAuthoritiesAsStream() {
        // Initialize the database
        scheduleAuthorityRepository.save(scheduleAuthority).block();

        List<ScheduleAuthority> scheduleAuthorityList = webTestClient
            .get()
            .uri(ENTITY_API_URL)
            .accept(MediaType.APPLICATION_NDJSON)
            .exchange()
            .expectStatus()
            .isOk()
            .expectHeader()
            .contentTypeCompatibleWith(MediaType.APPLICATION_NDJSON)
            .returnResult(ScheduleAuthority.class)
            .getResponseBody()
            .filter(scheduleAuthority::equals)
            .collectList()
            .block(Duration.ofSeconds(5));

        assertThat(scheduleAuthorityList).isNotNull();
        assertThat(scheduleAuthorityList).hasSize(1);
        ScheduleAuthority testScheduleAuthority = scheduleAuthorityList.get(0);
        assertThat(testScheduleAuthority.getName()).isEqualTo(DEFAULT_NAME);
    }

    @Test
    void getAllScheduleAuthorities() {
        // Initialize the database
        scheduleAuthorityRepository.save(scheduleAuthority).block();

        // Get all the scheduleAuthorityList
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
            .value(hasItem(scheduleAuthority.getId().intValue()))
            .jsonPath("$.[*].name")
            .value(hasItem(DEFAULT_NAME));
    }

    @Test
    void getScheduleAuthority() {
        // Initialize the database
        scheduleAuthorityRepository.save(scheduleAuthority).block();

        // Get the scheduleAuthority
        webTestClient
            .get()
            .uri(ENTITY_API_URL_ID, scheduleAuthority.getId())
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isOk()
            .expectHeader()
            .contentType(MediaType.APPLICATION_JSON)
            .expectBody()
            .jsonPath("$.id")
            .value(is(scheduleAuthority.getId().intValue()))
            .jsonPath("$.name")
            .value(is(DEFAULT_NAME));
    }

    @Test
    void getNonExistingScheduleAuthority() {
        // Get the scheduleAuthority
        webTestClient
            .get()
            .uri(ENTITY_API_URL_ID, Long.MAX_VALUE)
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isNotFound();
    }

    @Test
    void putNewScheduleAuthority() throws Exception {
        // Initialize the database
        scheduleAuthorityRepository.save(scheduleAuthority).block();

        int databaseSizeBeforeUpdate = scheduleAuthorityRepository.findAll().collectList().block().size();

        // Update the scheduleAuthority
        ScheduleAuthority updatedScheduleAuthority = scheduleAuthorityRepository.findById(scheduleAuthority.getId()).block();
        updatedScheduleAuthority.name(UPDATED_NAME);

        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, updatedScheduleAuthority.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(updatedScheduleAuthority))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the ScheduleAuthority in the database
        List<ScheduleAuthority> scheduleAuthorityList = scheduleAuthorityRepository.findAll().collectList().block();
        assertThat(scheduleAuthorityList).hasSize(databaseSizeBeforeUpdate);
        ScheduleAuthority testScheduleAuthority = scheduleAuthorityList.get(scheduleAuthorityList.size() - 1);
        assertThat(testScheduleAuthority.getName()).isEqualTo(UPDATED_NAME);
    }

    @Test
    void putNonExistingScheduleAuthority() throws Exception {
        int databaseSizeBeforeUpdate = scheduleAuthorityRepository.findAll().collectList().block().size();
        scheduleAuthority.setId(count.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, scheduleAuthority.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(scheduleAuthority))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the ScheduleAuthority in the database
        List<ScheduleAuthority> scheduleAuthorityList = scheduleAuthorityRepository.findAll().collectList().block();
        assertThat(scheduleAuthorityList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void putWithIdMismatchScheduleAuthority() throws Exception {
        int databaseSizeBeforeUpdate = scheduleAuthorityRepository.findAll().collectList().block().size();
        scheduleAuthority.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, count.incrementAndGet())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(scheduleAuthority))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the ScheduleAuthority in the database
        List<ScheduleAuthority> scheduleAuthorityList = scheduleAuthorityRepository.findAll().collectList().block();
        assertThat(scheduleAuthorityList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void putWithMissingIdPathParamScheduleAuthority() throws Exception {
        int databaseSizeBeforeUpdate = scheduleAuthorityRepository.findAll().collectList().block().size();
        scheduleAuthority.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(scheduleAuthority))
            .exchange()
            .expectStatus()
            .isEqualTo(405);

        // Validate the ScheduleAuthority in the database
        List<ScheduleAuthority> scheduleAuthorityList = scheduleAuthorityRepository.findAll().collectList().block();
        assertThat(scheduleAuthorityList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void partialUpdateScheduleAuthorityWithPatch() throws Exception {
        // Initialize the database
        scheduleAuthorityRepository.save(scheduleAuthority).block();

        int databaseSizeBeforeUpdate = scheduleAuthorityRepository.findAll().collectList().block().size();

        // Update the scheduleAuthority using partial update
        ScheduleAuthority partialUpdatedScheduleAuthority = new ScheduleAuthority();
        partialUpdatedScheduleAuthority.setId(scheduleAuthority.getId());

        partialUpdatedScheduleAuthority.name(UPDATED_NAME);

        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, partialUpdatedScheduleAuthority.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(partialUpdatedScheduleAuthority))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the ScheduleAuthority in the database
        List<ScheduleAuthority> scheduleAuthorityList = scheduleAuthorityRepository.findAll().collectList().block();
        assertThat(scheduleAuthorityList).hasSize(databaseSizeBeforeUpdate);
        ScheduleAuthority testScheduleAuthority = scheduleAuthorityList.get(scheduleAuthorityList.size() - 1);
        assertThat(testScheduleAuthority.getName()).isEqualTo(UPDATED_NAME);
    }

    @Test
    void fullUpdateScheduleAuthorityWithPatch() throws Exception {
        // Initialize the database
        scheduleAuthorityRepository.save(scheduleAuthority).block();

        int databaseSizeBeforeUpdate = scheduleAuthorityRepository.findAll().collectList().block().size();

        // Update the scheduleAuthority using partial update
        ScheduleAuthority partialUpdatedScheduleAuthority = new ScheduleAuthority();
        partialUpdatedScheduleAuthority.setId(scheduleAuthority.getId());

        partialUpdatedScheduleAuthority.name(UPDATED_NAME);

        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, partialUpdatedScheduleAuthority.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(partialUpdatedScheduleAuthority))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the ScheduleAuthority in the database
        List<ScheduleAuthority> scheduleAuthorityList = scheduleAuthorityRepository.findAll().collectList().block();
        assertThat(scheduleAuthorityList).hasSize(databaseSizeBeforeUpdate);
        ScheduleAuthority testScheduleAuthority = scheduleAuthorityList.get(scheduleAuthorityList.size() - 1);
        assertThat(testScheduleAuthority.getName()).isEqualTo(UPDATED_NAME);
    }

    @Test
    void patchNonExistingScheduleAuthority() throws Exception {
        int databaseSizeBeforeUpdate = scheduleAuthorityRepository.findAll().collectList().block().size();
        scheduleAuthority.setId(count.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, scheduleAuthority.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(scheduleAuthority))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the ScheduleAuthority in the database
        List<ScheduleAuthority> scheduleAuthorityList = scheduleAuthorityRepository.findAll().collectList().block();
        assertThat(scheduleAuthorityList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void patchWithIdMismatchScheduleAuthority() throws Exception {
        int databaseSizeBeforeUpdate = scheduleAuthorityRepository.findAll().collectList().block().size();
        scheduleAuthority.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, count.incrementAndGet())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(scheduleAuthority))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the ScheduleAuthority in the database
        List<ScheduleAuthority> scheduleAuthorityList = scheduleAuthorityRepository.findAll().collectList().block();
        assertThat(scheduleAuthorityList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void patchWithMissingIdPathParamScheduleAuthority() throws Exception {
        int databaseSizeBeforeUpdate = scheduleAuthorityRepository.findAll().collectList().block().size();
        scheduleAuthority.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(scheduleAuthority))
            .exchange()
            .expectStatus()
            .isEqualTo(405);

        // Validate the ScheduleAuthority in the database
        List<ScheduleAuthority> scheduleAuthorityList = scheduleAuthorityRepository.findAll().collectList().block();
        assertThat(scheduleAuthorityList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void deleteScheduleAuthority() {
        // Initialize the database
        scheduleAuthorityRepository.save(scheduleAuthority).block();

        int databaseSizeBeforeDelete = scheduleAuthorityRepository.findAll().collectList().block().size();

        // Delete the scheduleAuthority
        webTestClient
            .delete()
            .uri(ENTITY_API_URL_ID, scheduleAuthority.getId())
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isNoContent();

        // Validate the database contains one less item
        List<ScheduleAuthority> scheduleAuthorityList = scheduleAuthorityRepository.findAll().collectList().block();
        assertThat(scheduleAuthorityList).hasSize(databaseSizeBeforeDelete - 1);
    }
}
