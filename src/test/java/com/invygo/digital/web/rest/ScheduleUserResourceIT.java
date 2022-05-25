package com.invygo.digital.web.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.is;

import com.invygo.digital.IntegrationTest;
import com.invygo.digital.domain.ScheduleUser;
import com.invygo.digital.repository.EntityManager;
import com.invygo.digital.repository.ScheduleUserRepository;
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
 * Integration tests for the {@link ScheduleUserResource} REST controller.
 */
@IntegrationTest
@AutoConfigureWebTestClient(timeout = IntegrationTest.DEFAULT_ENTITY_TIMEOUT)
@WithMockUser
class ScheduleUserResourceIT {

    private static final String DEFAULT_EMAIL = "AAAAAAAAAA";
    private static final String UPDATED_EMAIL = "BBBBBBBBBB";

    private static final String DEFAULT_LOGIN = "AAAAAAAAAA";
    private static final String UPDATED_LOGIN = "BBBBBBBBBB";

    private static final String DEFAULT_FIRSTNAME = "AAAAAAAAAA";
    private static final String UPDATED_FIRSTNAME = "BBBBBBBBBB";

    private static final String DEFAULT_LASTNAME = "AAAAAAAAAA";
    private static final String UPDATED_LASTNAME = "BBBBBBBBBB";

    private static final String DEFAULT_PASSWORD = "AAAAAAAAAA";
    private static final String UPDATED_PASSWORD = "BBBBBBBBBB";

    private static final String ENTITY_API_URL = "/api/schedule-users";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong count = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private ScheduleUserRepository scheduleUserRepository;

    @Autowired
    private EntityManager em;

    @Autowired
    private WebTestClient webTestClient;

    private ScheduleUser scheduleUser;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static ScheduleUser createEntity(EntityManager em) {
        ScheduleUser scheduleUser = new ScheduleUser()
            .email(DEFAULT_EMAIL)
            .login(DEFAULT_LOGIN)
            .firstname(DEFAULT_FIRSTNAME)
            .lastname(DEFAULT_LASTNAME)
            .password(DEFAULT_PASSWORD);
        return scheduleUser;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static ScheduleUser createUpdatedEntity(EntityManager em) {
        ScheduleUser scheduleUser = new ScheduleUser()
            .email(UPDATED_EMAIL)
            .login(UPDATED_LOGIN)
            .firstname(UPDATED_FIRSTNAME)
            .lastname(UPDATED_LASTNAME)
            .password(UPDATED_PASSWORD);
        return scheduleUser;
    }

    public static void deleteEntities(EntityManager em) {
        try {
            em.deleteAll(ScheduleUser.class).block();
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
        scheduleUser = createEntity(em);
    }

    @Test
    void createScheduleUser() throws Exception {
        int databaseSizeBeforeCreate = scheduleUserRepository.findAll().collectList().block().size();
        // Create the ScheduleUser
        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(scheduleUser))
            .exchange()
            .expectStatus()
            .isCreated();

        // Validate the ScheduleUser in the database
        List<ScheduleUser> scheduleUserList = scheduleUserRepository.findAll().collectList().block();
        assertThat(scheduleUserList).hasSize(databaseSizeBeforeCreate + 1);
        ScheduleUser testScheduleUser = scheduleUserList.get(scheduleUserList.size() - 1);
        assertThat(testScheduleUser.getEmail()).isEqualTo(DEFAULT_EMAIL);
        assertThat(testScheduleUser.getLogin()).isEqualTo(DEFAULT_LOGIN);
        assertThat(testScheduleUser.getFirstname()).isEqualTo(DEFAULT_FIRSTNAME);
        assertThat(testScheduleUser.getLastname()).isEqualTo(DEFAULT_LASTNAME);
        assertThat(testScheduleUser.getPassword()).isEqualTo(DEFAULT_PASSWORD);
    }

    @Test
    void createScheduleUserWithExistingId() throws Exception {
        // Create the ScheduleUser with an existing ID
        scheduleUser.setId(1L);

        int databaseSizeBeforeCreate = scheduleUserRepository.findAll().collectList().block().size();

        // An entity with an existing ID cannot be created, so this API call must fail
        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(scheduleUser))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the ScheduleUser in the database
        List<ScheduleUser> scheduleUserList = scheduleUserRepository.findAll().collectList().block();
        assertThat(scheduleUserList).hasSize(databaseSizeBeforeCreate);
    }

    @Test
    void checkEmailIsRequired() throws Exception {
        int databaseSizeBeforeTest = scheduleUserRepository.findAll().collectList().block().size();
        // set the field null
        scheduleUser.setEmail(null);

        // Create the ScheduleUser, which fails.

        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(scheduleUser))
            .exchange()
            .expectStatus()
            .isBadRequest();

        List<ScheduleUser> scheduleUserList = scheduleUserRepository.findAll().collectList().block();
        assertThat(scheduleUserList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    void checkLoginIsRequired() throws Exception {
        int databaseSizeBeforeTest = scheduleUserRepository.findAll().collectList().block().size();
        // set the field null
        scheduleUser.setLogin(null);

        // Create the ScheduleUser, which fails.

        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(scheduleUser))
            .exchange()
            .expectStatus()
            .isBadRequest();

        List<ScheduleUser> scheduleUserList = scheduleUserRepository.findAll().collectList().block();
        assertThat(scheduleUserList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    void checkFirstnameIsRequired() throws Exception {
        int databaseSizeBeforeTest = scheduleUserRepository.findAll().collectList().block().size();
        // set the field null
        scheduleUser.setFirstname(null);

        // Create the ScheduleUser, which fails.

        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(scheduleUser))
            .exchange()
            .expectStatus()
            .isBadRequest();

        List<ScheduleUser> scheduleUserList = scheduleUserRepository.findAll().collectList().block();
        assertThat(scheduleUserList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    void checkLastnameIsRequired() throws Exception {
        int databaseSizeBeforeTest = scheduleUserRepository.findAll().collectList().block().size();
        // set the field null
        scheduleUser.setLastname(null);

        // Create the ScheduleUser, which fails.

        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(scheduleUser))
            .exchange()
            .expectStatus()
            .isBadRequest();

        List<ScheduleUser> scheduleUserList = scheduleUserRepository.findAll().collectList().block();
        assertThat(scheduleUserList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    void checkPasswordIsRequired() throws Exception {
        int databaseSizeBeforeTest = scheduleUserRepository.findAll().collectList().block().size();
        // set the field null
        scheduleUser.setPassword(null);

        // Create the ScheduleUser, which fails.

        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(scheduleUser))
            .exchange()
            .expectStatus()
            .isBadRequest();

        List<ScheduleUser> scheduleUserList = scheduleUserRepository.findAll().collectList().block();
        assertThat(scheduleUserList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    void getAllScheduleUsersAsStream() {
        // Initialize the database
        scheduleUserRepository.save(scheduleUser).block();

        List<ScheduleUser> scheduleUserList = webTestClient
            .get()
            .uri(ENTITY_API_URL)
            .accept(MediaType.APPLICATION_NDJSON)
            .exchange()
            .expectStatus()
            .isOk()
            .expectHeader()
            .contentTypeCompatibleWith(MediaType.APPLICATION_NDJSON)
            .returnResult(ScheduleUser.class)
            .getResponseBody()
            .filter(scheduleUser::equals)
            .collectList()
            .block(Duration.ofSeconds(5));

        assertThat(scheduleUserList).isNotNull();
        assertThat(scheduleUserList).hasSize(1);
        ScheduleUser testScheduleUser = scheduleUserList.get(0);
        assertThat(testScheduleUser.getEmail()).isEqualTo(DEFAULT_EMAIL);
        assertThat(testScheduleUser.getLogin()).isEqualTo(DEFAULT_LOGIN);
        assertThat(testScheduleUser.getFirstname()).isEqualTo(DEFAULT_FIRSTNAME);
        assertThat(testScheduleUser.getLastname()).isEqualTo(DEFAULT_LASTNAME);
        assertThat(testScheduleUser.getPassword()).isEqualTo(DEFAULT_PASSWORD);
    }

    @Test
    void getAllScheduleUsers() {
        // Initialize the database
        scheduleUserRepository.save(scheduleUser).block();

        // Get all the scheduleUserList
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
            .value(hasItem(scheduleUser.getId().intValue()))
            .jsonPath("$.[*].email")
            .value(hasItem(DEFAULT_EMAIL))
            .jsonPath("$.[*].login")
            .value(hasItem(DEFAULT_LOGIN))
            .jsonPath("$.[*].firstname")
            .value(hasItem(DEFAULT_FIRSTNAME))
            .jsonPath("$.[*].lastname")
            .value(hasItem(DEFAULT_LASTNAME))
            .jsonPath("$.[*].password")
            .value(hasItem(DEFAULT_PASSWORD));
    }

    @Test
    void getScheduleUser() {
        // Initialize the database
        scheduleUserRepository.save(scheduleUser).block();

        // Get the scheduleUser
        webTestClient
            .get()
            .uri(ENTITY_API_URL_ID, scheduleUser.getId())
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isOk()
            .expectHeader()
            .contentType(MediaType.APPLICATION_JSON)
            .expectBody()
            .jsonPath("$.id")
            .value(is(scheduleUser.getId().intValue()))
            .jsonPath("$.email")
            .value(is(DEFAULT_EMAIL))
            .jsonPath("$.login")
            .value(is(DEFAULT_LOGIN))
            .jsonPath("$.firstname")
            .value(is(DEFAULT_FIRSTNAME))
            .jsonPath("$.lastname")
            .value(is(DEFAULT_LASTNAME))
            .jsonPath("$.password")
            .value(is(DEFAULT_PASSWORD));
    }

    @Test
    void getNonExistingScheduleUser() {
        // Get the scheduleUser
        webTestClient
            .get()
            .uri(ENTITY_API_URL_ID, Long.MAX_VALUE)
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isNotFound();
    }

    @Test
    void putNewScheduleUser() throws Exception {
        // Initialize the database
        scheduleUserRepository.save(scheduleUser).block();

        int databaseSizeBeforeUpdate = scheduleUserRepository.findAll().collectList().block().size();

        // Update the scheduleUser
        ScheduleUser updatedScheduleUser = scheduleUserRepository.findById(scheduleUser.getId()).block();
        updatedScheduleUser
            .email(UPDATED_EMAIL)
            .login(UPDATED_LOGIN)
            .firstname(UPDATED_FIRSTNAME)
            .lastname(UPDATED_LASTNAME)
            .password(UPDATED_PASSWORD);

        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, updatedScheduleUser.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(updatedScheduleUser))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the ScheduleUser in the database
        List<ScheduleUser> scheduleUserList = scheduleUserRepository.findAll().collectList().block();
        assertThat(scheduleUserList).hasSize(databaseSizeBeforeUpdate);
        ScheduleUser testScheduleUser = scheduleUserList.get(scheduleUserList.size() - 1);
        assertThat(testScheduleUser.getEmail()).isEqualTo(UPDATED_EMAIL);
        assertThat(testScheduleUser.getLogin()).isEqualTo(UPDATED_LOGIN);
        assertThat(testScheduleUser.getFirstname()).isEqualTo(UPDATED_FIRSTNAME);
        assertThat(testScheduleUser.getLastname()).isEqualTo(UPDATED_LASTNAME);
        assertThat(testScheduleUser.getPassword()).isEqualTo(UPDATED_PASSWORD);
    }

    @Test
    void putNonExistingScheduleUser() throws Exception {
        int databaseSizeBeforeUpdate = scheduleUserRepository.findAll().collectList().block().size();
        scheduleUser.setId(count.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, scheduleUser.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(scheduleUser))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the ScheduleUser in the database
        List<ScheduleUser> scheduleUserList = scheduleUserRepository.findAll().collectList().block();
        assertThat(scheduleUserList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void putWithIdMismatchScheduleUser() throws Exception {
        int databaseSizeBeforeUpdate = scheduleUserRepository.findAll().collectList().block().size();
        scheduleUser.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, count.incrementAndGet())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(scheduleUser))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the ScheduleUser in the database
        List<ScheduleUser> scheduleUserList = scheduleUserRepository.findAll().collectList().block();
        assertThat(scheduleUserList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void putWithMissingIdPathParamScheduleUser() throws Exception {
        int databaseSizeBeforeUpdate = scheduleUserRepository.findAll().collectList().block().size();
        scheduleUser.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(scheduleUser))
            .exchange()
            .expectStatus()
            .isEqualTo(405);

        // Validate the ScheduleUser in the database
        List<ScheduleUser> scheduleUserList = scheduleUserRepository.findAll().collectList().block();
        assertThat(scheduleUserList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void partialUpdateScheduleUserWithPatch() throws Exception {
        // Initialize the database
        scheduleUserRepository.save(scheduleUser).block();

        int databaseSizeBeforeUpdate = scheduleUserRepository.findAll().collectList().block().size();

        // Update the scheduleUser using partial update
        ScheduleUser partialUpdatedScheduleUser = new ScheduleUser();
        partialUpdatedScheduleUser.setId(scheduleUser.getId());

        partialUpdatedScheduleUser.email(UPDATED_EMAIL).firstname(UPDATED_FIRSTNAME).lastname(UPDATED_LASTNAME).password(UPDATED_PASSWORD);

        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, partialUpdatedScheduleUser.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(partialUpdatedScheduleUser))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the ScheduleUser in the database
        List<ScheduleUser> scheduleUserList = scheduleUserRepository.findAll().collectList().block();
        assertThat(scheduleUserList).hasSize(databaseSizeBeforeUpdate);
        ScheduleUser testScheduleUser = scheduleUserList.get(scheduleUserList.size() - 1);
        assertThat(testScheduleUser.getEmail()).isEqualTo(UPDATED_EMAIL);
        assertThat(testScheduleUser.getLogin()).isEqualTo(DEFAULT_LOGIN);
        assertThat(testScheduleUser.getFirstname()).isEqualTo(UPDATED_FIRSTNAME);
        assertThat(testScheduleUser.getLastname()).isEqualTo(UPDATED_LASTNAME);
        assertThat(testScheduleUser.getPassword()).isEqualTo(UPDATED_PASSWORD);
    }

    @Test
    void fullUpdateScheduleUserWithPatch() throws Exception {
        // Initialize the database
        scheduleUserRepository.save(scheduleUser).block();

        int databaseSizeBeforeUpdate = scheduleUserRepository.findAll().collectList().block().size();

        // Update the scheduleUser using partial update
        ScheduleUser partialUpdatedScheduleUser = new ScheduleUser();
        partialUpdatedScheduleUser.setId(scheduleUser.getId());

        partialUpdatedScheduleUser
            .email(UPDATED_EMAIL)
            .login(UPDATED_LOGIN)
            .firstname(UPDATED_FIRSTNAME)
            .lastname(UPDATED_LASTNAME)
            .password(UPDATED_PASSWORD);

        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, partialUpdatedScheduleUser.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(partialUpdatedScheduleUser))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the ScheduleUser in the database
        List<ScheduleUser> scheduleUserList = scheduleUserRepository.findAll().collectList().block();
        assertThat(scheduleUserList).hasSize(databaseSizeBeforeUpdate);
        ScheduleUser testScheduleUser = scheduleUserList.get(scheduleUserList.size() - 1);
        assertThat(testScheduleUser.getEmail()).isEqualTo(UPDATED_EMAIL);
        assertThat(testScheduleUser.getLogin()).isEqualTo(UPDATED_LOGIN);
        assertThat(testScheduleUser.getFirstname()).isEqualTo(UPDATED_FIRSTNAME);
        assertThat(testScheduleUser.getLastname()).isEqualTo(UPDATED_LASTNAME);
        assertThat(testScheduleUser.getPassword()).isEqualTo(UPDATED_PASSWORD);
    }

    @Test
    void patchNonExistingScheduleUser() throws Exception {
        int databaseSizeBeforeUpdate = scheduleUserRepository.findAll().collectList().block().size();
        scheduleUser.setId(count.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, scheduleUser.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(scheduleUser))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the ScheduleUser in the database
        List<ScheduleUser> scheduleUserList = scheduleUserRepository.findAll().collectList().block();
        assertThat(scheduleUserList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void patchWithIdMismatchScheduleUser() throws Exception {
        int databaseSizeBeforeUpdate = scheduleUserRepository.findAll().collectList().block().size();
        scheduleUser.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, count.incrementAndGet())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(scheduleUser))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the ScheduleUser in the database
        List<ScheduleUser> scheduleUserList = scheduleUserRepository.findAll().collectList().block();
        assertThat(scheduleUserList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void patchWithMissingIdPathParamScheduleUser() throws Exception {
        int databaseSizeBeforeUpdate = scheduleUserRepository.findAll().collectList().block().size();
        scheduleUser.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(scheduleUser))
            .exchange()
            .expectStatus()
            .isEqualTo(405);

        // Validate the ScheduleUser in the database
        List<ScheduleUser> scheduleUserList = scheduleUserRepository.findAll().collectList().block();
        assertThat(scheduleUserList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void deleteScheduleUser() {
        // Initialize the database
        scheduleUserRepository.save(scheduleUser).block();

        int databaseSizeBeforeDelete = scheduleUserRepository.findAll().collectList().block().size();

        // Delete the scheduleUser
        webTestClient
            .delete()
            .uri(ENTITY_API_URL_ID, scheduleUser.getId())
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isNoContent();

        // Validate the database contains one less item
        List<ScheduleUser> scheduleUserList = scheduleUserRepository.findAll().collectList().block();
        assertThat(scheduleUserList).hasSize(databaseSizeBeforeDelete - 1);
    }
}
