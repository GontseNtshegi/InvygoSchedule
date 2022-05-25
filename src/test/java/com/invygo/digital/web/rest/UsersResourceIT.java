package com.invygo.digital.web.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.is;

import com.invygo.digital.IntegrationTest;
import com.invygo.digital.domain.Users;
import com.invygo.digital.repository.EntityManager;
import com.invygo.digital.repository.UsersRepository;
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
 * Integration tests for the {@link UsersResource} REST controller.
 */
@IntegrationTest
@AutoConfigureWebTestClient(timeout = IntegrationTest.DEFAULT_ENTITY_TIMEOUT)
@WithMockUser
class UsersResourceIT {

    private static final String DEFAULT_EMAIL = "AAAAAAAAAA";
    private static final String UPDATED_EMAIL = "BBBBBBBBBB";

    private static final String DEFAULT_NAME = "AAAAAAAAAA";
    private static final String UPDATED_NAME = "BBBBBBBBBB";

    private static final String DEFAULT_SURNAME = "AAAAAAAAAA";
    private static final String UPDATED_SURNAME = "BBBBBBBBBB";

    private static final String DEFAULT_PASSWORD = "AAAAAAAAAA";
    private static final String UPDATED_PASSWORD = "BBBBBBBBBB";

    private static final String ENTITY_API_URL = "/api/users";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong count = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private UsersRepository usersRepository;

    @Autowired
    private EntityManager em;

    @Autowired
    private WebTestClient webTestClient;

    private Users users;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Users createEntity(EntityManager em) {
        Users users = new Users().email(DEFAULT_EMAIL).name(DEFAULT_NAME).surname(DEFAULT_SURNAME).password(DEFAULT_PASSWORD);
        return users;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Users createUpdatedEntity(EntityManager em) {
        Users users = new Users().email(UPDATED_EMAIL).name(UPDATED_NAME).surname(UPDATED_SURNAME).password(UPDATED_PASSWORD);
        return users;
    }

    public static void deleteEntities(EntityManager em) {
        try {
            em.deleteAll(Users.class).block();
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
        users = createEntity(em);
    }

    @Test
    void createUsers() throws Exception {
        int databaseSizeBeforeCreate = usersRepository.findAll().collectList().block().size();
        // Create the Users
        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(users))
            .exchange()
            .expectStatus()
            .isCreated();

        // Validate the Users in the database
        List<Users> usersList = usersRepository.findAll().collectList().block();
        assertThat(usersList).hasSize(databaseSizeBeforeCreate + 1);
        Users testUsers = usersList.get(usersList.size() - 1);
        assertThat(testUsers.getEmail()).isEqualTo(DEFAULT_EMAIL);
        assertThat(testUsers.getName()).isEqualTo(DEFAULT_NAME);
        assertThat(testUsers.getSurname()).isEqualTo(DEFAULT_SURNAME);
        assertThat(testUsers.getPassword()).isEqualTo(DEFAULT_PASSWORD);
    }

    @Test
    void createUsersWithExistingId() throws Exception {
        // Create the Users with an existing ID
        users.setId(1L);

        int databaseSizeBeforeCreate = usersRepository.findAll().collectList().block().size();

        // An entity with an existing ID cannot be created, so this API call must fail
        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(users))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Users in the database
        List<Users> usersList = usersRepository.findAll().collectList().block();
        assertThat(usersList).hasSize(databaseSizeBeforeCreate);
    }

    @Test
    void checkEmailIsRequired() throws Exception {
        int databaseSizeBeforeTest = usersRepository.findAll().collectList().block().size();
        // set the field null
        users.setEmail(null);

        // Create the Users, which fails.

        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(users))
            .exchange()
            .expectStatus()
            .isBadRequest();

        List<Users> usersList = usersRepository.findAll().collectList().block();
        assertThat(usersList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    void checkNameIsRequired() throws Exception {
        int databaseSizeBeforeTest = usersRepository.findAll().collectList().block().size();
        // set the field null
        users.setName(null);

        // Create the Users, which fails.

        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(users))
            .exchange()
            .expectStatus()
            .isBadRequest();

        List<Users> usersList = usersRepository.findAll().collectList().block();
        assertThat(usersList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    void checkSurnameIsRequired() throws Exception {
        int databaseSizeBeforeTest = usersRepository.findAll().collectList().block().size();
        // set the field null
        users.setSurname(null);

        // Create the Users, which fails.

        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(users))
            .exchange()
            .expectStatus()
            .isBadRequest();

        List<Users> usersList = usersRepository.findAll().collectList().block();
        assertThat(usersList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    void checkPasswordIsRequired() throws Exception {
        int databaseSizeBeforeTest = usersRepository.findAll().collectList().block().size();
        // set the field null
        users.setPassword(null);

        // Create the Users, which fails.

        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(users))
            .exchange()
            .expectStatus()
            .isBadRequest();

        List<Users> usersList = usersRepository.findAll().collectList().block();
        assertThat(usersList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    void getAllUsersAsStream() {
        // Initialize the database
        usersRepository.save(users).block();

        List<Users> usersList = webTestClient
            .get()
            .uri(ENTITY_API_URL)
            .accept(MediaType.APPLICATION_NDJSON)
            .exchange()
            .expectStatus()
            .isOk()
            .expectHeader()
            .contentTypeCompatibleWith(MediaType.APPLICATION_NDJSON)
            .returnResult(Users.class)
            .getResponseBody()
            .filter(users::equals)
            .collectList()
            .block(Duration.ofSeconds(5));

        assertThat(usersList).isNotNull();
        assertThat(usersList).hasSize(1);
        Users testUsers = usersList.get(0);
        assertThat(testUsers.getEmail()).isEqualTo(DEFAULT_EMAIL);
        assertThat(testUsers.getName()).isEqualTo(DEFAULT_NAME);
        assertThat(testUsers.getSurname()).isEqualTo(DEFAULT_SURNAME);
        assertThat(testUsers.getPassword()).isEqualTo(DEFAULT_PASSWORD);
    }

    @Test
    void getAllUsers() {
        // Initialize the database
        usersRepository.save(users).block();

        // Get all the usersList
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
            .value(hasItem(users.getId().intValue()))
            .jsonPath("$.[*].email")
            .value(hasItem(DEFAULT_EMAIL))
            .jsonPath("$.[*].name")
            .value(hasItem(DEFAULT_NAME))
            .jsonPath("$.[*].surname")
            .value(hasItem(DEFAULT_SURNAME))
            .jsonPath("$.[*].password")
            .value(hasItem(DEFAULT_PASSWORD));
    }

    @Test
    void getUsers() {
        // Initialize the database
        usersRepository.save(users).block();

        // Get the users
        webTestClient
            .get()
            .uri(ENTITY_API_URL_ID, users.getId())
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isOk()
            .expectHeader()
            .contentType(MediaType.APPLICATION_JSON)
            .expectBody()
            .jsonPath("$.id")
            .value(is(users.getId().intValue()))
            .jsonPath("$.email")
            .value(is(DEFAULT_EMAIL))
            .jsonPath("$.name")
            .value(is(DEFAULT_NAME))
            .jsonPath("$.surname")
            .value(is(DEFAULT_SURNAME))
            .jsonPath("$.password")
            .value(is(DEFAULT_PASSWORD));
    }

    @Test
    void getNonExistingUsers() {
        // Get the users
        webTestClient
            .get()
            .uri(ENTITY_API_URL_ID, Long.MAX_VALUE)
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isNotFound();
    }

    @Test
    void putNewUsers() throws Exception {
        // Initialize the database
        usersRepository.save(users).block();

        int databaseSizeBeforeUpdate = usersRepository.findAll().collectList().block().size();

        // Update the users
        Users updatedUsers = usersRepository.findById(users.getId()).block();
        updatedUsers.email(UPDATED_EMAIL).name(UPDATED_NAME).surname(UPDATED_SURNAME).password(UPDATED_PASSWORD);

        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, updatedUsers.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(updatedUsers))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the Users in the database
        List<Users> usersList = usersRepository.findAll().collectList().block();
        assertThat(usersList).hasSize(databaseSizeBeforeUpdate);
        Users testUsers = usersList.get(usersList.size() - 1);
        assertThat(testUsers.getEmail()).isEqualTo(UPDATED_EMAIL);
        assertThat(testUsers.getName()).isEqualTo(UPDATED_NAME);
        assertThat(testUsers.getSurname()).isEqualTo(UPDATED_SURNAME);
        assertThat(testUsers.getPassword()).isEqualTo(UPDATED_PASSWORD);
    }

    @Test
    void putNonExistingUsers() throws Exception {
        int databaseSizeBeforeUpdate = usersRepository.findAll().collectList().block().size();
        users.setId(count.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, users.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(users))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Users in the database
        List<Users> usersList = usersRepository.findAll().collectList().block();
        assertThat(usersList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void putWithIdMismatchUsers() throws Exception {
        int databaseSizeBeforeUpdate = usersRepository.findAll().collectList().block().size();
        users.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, count.incrementAndGet())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(users))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Users in the database
        List<Users> usersList = usersRepository.findAll().collectList().block();
        assertThat(usersList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void putWithMissingIdPathParamUsers() throws Exception {
        int databaseSizeBeforeUpdate = usersRepository.findAll().collectList().block().size();
        users.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(users))
            .exchange()
            .expectStatus()
            .isEqualTo(405);

        // Validate the Users in the database
        List<Users> usersList = usersRepository.findAll().collectList().block();
        assertThat(usersList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void partialUpdateUsersWithPatch() throws Exception {
        // Initialize the database
        usersRepository.save(users).block();

        int databaseSizeBeforeUpdate = usersRepository.findAll().collectList().block().size();

        // Update the users using partial update
        Users partialUpdatedUsers = new Users();
        partialUpdatedUsers.setId(users.getId());

        partialUpdatedUsers.name(UPDATED_NAME).password(UPDATED_PASSWORD);

        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, partialUpdatedUsers.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(partialUpdatedUsers))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the Users in the database
        List<Users> usersList = usersRepository.findAll().collectList().block();
        assertThat(usersList).hasSize(databaseSizeBeforeUpdate);
        Users testUsers = usersList.get(usersList.size() - 1);
        assertThat(testUsers.getEmail()).isEqualTo(DEFAULT_EMAIL);
        assertThat(testUsers.getName()).isEqualTo(UPDATED_NAME);
        assertThat(testUsers.getSurname()).isEqualTo(DEFAULT_SURNAME);
        assertThat(testUsers.getPassword()).isEqualTo(UPDATED_PASSWORD);
    }

    @Test
    void fullUpdateUsersWithPatch() throws Exception {
        // Initialize the database
        usersRepository.save(users).block();

        int databaseSizeBeforeUpdate = usersRepository.findAll().collectList().block().size();

        // Update the users using partial update
        Users partialUpdatedUsers = new Users();
        partialUpdatedUsers.setId(users.getId());

        partialUpdatedUsers.email(UPDATED_EMAIL).name(UPDATED_NAME).surname(UPDATED_SURNAME).password(UPDATED_PASSWORD);

        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, partialUpdatedUsers.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(partialUpdatedUsers))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the Users in the database
        List<Users> usersList = usersRepository.findAll().collectList().block();
        assertThat(usersList).hasSize(databaseSizeBeforeUpdate);
        Users testUsers = usersList.get(usersList.size() - 1);
        assertThat(testUsers.getEmail()).isEqualTo(UPDATED_EMAIL);
        assertThat(testUsers.getName()).isEqualTo(UPDATED_NAME);
        assertThat(testUsers.getSurname()).isEqualTo(UPDATED_SURNAME);
        assertThat(testUsers.getPassword()).isEqualTo(UPDATED_PASSWORD);
    }

    @Test
    void patchNonExistingUsers() throws Exception {
        int databaseSizeBeforeUpdate = usersRepository.findAll().collectList().block().size();
        users.setId(count.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, users.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(users))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Users in the database
        List<Users> usersList = usersRepository.findAll().collectList().block();
        assertThat(usersList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void patchWithIdMismatchUsers() throws Exception {
        int databaseSizeBeforeUpdate = usersRepository.findAll().collectList().block().size();
        users.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, count.incrementAndGet())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(users))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Users in the database
        List<Users> usersList = usersRepository.findAll().collectList().block();
        assertThat(usersList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void patchWithMissingIdPathParamUsers() throws Exception {
        int databaseSizeBeforeUpdate = usersRepository.findAll().collectList().block().size();
        users.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(users))
            .exchange()
            .expectStatus()
            .isEqualTo(405);

        // Validate the Users in the database
        List<Users> usersList = usersRepository.findAll().collectList().block();
        assertThat(usersList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void deleteUsers() {
        // Initialize the database
        usersRepository.save(users).block();

        int databaseSizeBeforeDelete = usersRepository.findAll().collectList().block().size();

        // Delete the users
        webTestClient
            .delete()
            .uri(ENTITY_API_URL_ID, users.getId())
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isNoContent();

        // Validate the database contains one less item
        List<Users> usersList = usersRepository.findAll().collectList().block();
        assertThat(usersList).hasSize(databaseSizeBeforeDelete - 1);
    }
}
