package com.invygo.digital.web.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.is;

import com.invygo.digital.IntegrationTest;
import com.invygo.digital.domain.UserAuthority;
import com.invygo.digital.repository.EntityManager;
import com.invygo.digital.repository.UserAuthorityRepository;
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
 * Integration tests for the {@link UserAuthorityResource} REST controller.
 */
@IntegrationTest
@AutoConfigureWebTestClient(timeout = IntegrationTest.DEFAULT_ENTITY_TIMEOUT)
@WithMockUser
class UserAuthorityResourceIT {

    private static final Long DEFAULT_USER_ID = 1L;
    private static final Long UPDATED_USER_ID = 2L;

    private static final Long DEFAULT_ROLE_ID = 1L;
    private static final Long UPDATED_ROLE_ID = 2L;

    private static final String ENTITY_API_URL = "/api/user-authorities";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong count = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private UserAuthorityRepository userAuthorityRepository;

    @Autowired
    private EntityManager em;

    @Autowired
    private WebTestClient webTestClient;

    private UserAuthority userAuthority;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static UserAuthority createEntity(EntityManager em) {
        UserAuthority userAuthority = new UserAuthority().userId(DEFAULT_USER_ID).roleId(DEFAULT_ROLE_ID);
        return userAuthority;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static UserAuthority createUpdatedEntity(EntityManager em) {
        UserAuthority userAuthority = new UserAuthority().userId(UPDATED_USER_ID).roleId(UPDATED_ROLE_ID);
        return userAuthority;
    }

    public static void deleteEntities(EntityManager em) {
        try {
            em.deleteAll(UserAuthority.class).block();
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
        userAuthority = createEntity(em);
    }

    @Test
    void createUserAuthority() throws Exception {
        int databaseSizeBeforeCreate = userAuthorityRepository.findAll().collectList().block().size();
        // Create the UserAuthority
        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(userAuthority))
            .exchange()
            .expectStatus()
            .isCreated();

        // Validate the UserAuthority in the database
        List<UserAuthority> userAuthorityList = userAuthorityRepository.findAll().collectList().block();
        assertThat(userAuthorityList).hasSize(databaseSizeBeforeCreate + 1);
        UserAuthority testUserAuthority = userAuthorityList.get(userAuthorityList.size() - 1);
        assertThat(testUserAuthority.getUserId()).isEqualTo(DEFAULT_USER_ID);
        assertThat(testUserAuthority.getRoleId()).isEqualTo(DEFAULT_ROLE_ID);
    }

    @Test
    void createUserAuthorityWithExistingId() throws Exception {
        // Create the UserAuthority with an existing ID
        userAuthority.setId(1L);

        int databaseSizeBeforeCreate = userAuthorityRepository.findAll().collectList().block().size();

        // An entity with an existing ID cannot be created, so this API call must fail
        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(userAuthority))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the UserAuthority in the database
        List<UserAuthority> userAuthorityList = userAuthorityRepository.findAll().collectList().block();
        assertThat(userAuthorityList).hasSize(databaseSizeBeforeCreate);
    }

    @Test
    void checkUserIdIsRequired() throws Exception {
        int databaseSizeBeforeTest = userAuthorityRepository.findAll().collectList().block().size();
        // set the field null
        userAuthority.setUserId(null);

        // Create the UserAuthority, which fails.

        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(userAuthority))
            .exchange()
            .expectStatus()
            .isBadRequest();

        List<UserAuthority> userAuthorityList = userAuthorityRepository.findAll().collectList().block();
        assertThat(userAuthorityList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    void checkRoleIdIsRequired() throws Exception {
        int databaseSizeBeforeTest = userAuthorityRepository.findAll().collectList().block().size();
        // set the field null
        userAuthority.setRoleId(null);

        // Create the UserAuthority, which fails.

        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(userAuthority))
            .exchange()
            .expectStatus()
            .isBadRequest();

        List<UserAuthority> userAuthorityList = userAuthorityRepository.findAll().collectList().block();
        assertThat(userAuthorityList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    void getAllUserAuthoritiesAsStream() {
        // Initialize the database
        userAuthorityRepository.save(userAuthority).block();

        List<UserAuthority> userAuthorityList = webTestClient
            .get()
            .uri(ENTITY_API_URL)
            .accept(MediaType.APPLICATION_NDJSON)
            .exchange()
            .expectStatus()
            .isOk()
            .expectHeader()
            .contentTypeCompatibleWith(MediaType.APPLICATION_NDJSON)
            .returnResult(UserAuthority.class)
            .getResponseBody()
            .filter(userAuthority::equals)
            .collectList()
            .block(Duration.ofSeconds(5));

        assertThat(userAuthorityList).isNotNull();
        assertThat(userAuthorityList).hasSize(1);
        UserAuthority testUserAuthority = userAuthorityList.get(0);
        assertThat(testUserAuthority.getUserId()).isEqualTo(DEFAULT_USER_ID);
        assertThat(testUserAuthority.getRoleId()).isEqualTo(DEFAULT_ROLE_ID);
    }

    @Test
    void getAllUserAuthorities() {
        // Initialize the database
        userAuthorityRepository.save(userAuthority).block();

        // Get all the userAuthorityList
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
            .value(hasItem(userAuthority.getId().intValue()))
            .jsonPath("$.[*].userId")
            .value(hasItem(DEFAULT_USER_ID.intValue()))
            .jsonPath("$.[*].roleId")
            .value(hasItem(DEFAULT_ROLE_ID.intValue()));
    }

    @Test
    void getUserAuthority() {
        // Initialize the database
        userAuthorityRepository.save(userAuthority).block();

        // Get the userAuthority
        webTestClient
            .get()
            .uri(ENTITY_API_URL_ID, userAuthority.getId())
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isOk()
            .expectHeader()
            .contentType(MediaType.APPLICATION_JSON)
            .expectBody()
            .jsonPath("$.id")
            .value(is(userAuthority.getId().intValue()))
            .jsonPath("$.userId")
            .value(is(DEFAULT_USER_ID.intValue()))
            .jsonPath("$.roleId")
            .value(is(DEFAULT_ROLE_ID.intValue()));
    }

    @Test
    void getNonExistingUserAuthority() {
        // Get the userAuthority
        webTestClient
            .get()
            .uri(ENTITY_API_URL_ID, Long.MAX_VALUE)
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isNotFound();
    }

    @Test
    void putNewUserAuthority() throws Exception {
        // Initialize the database
        userAuthorityRepository.save(userAuthority).block();

        int databaseSizeBeforeUpdate = userAuthorityRepository.findAll().collectList().block().size();

        // Update the userAuthority
        UserAuthority updatedUserAuthority = userAuthorityRepository.findById(userAuthority.getId()).block();
        updatedUserAuthority.userId(UPDATED_USER_ID).roleId(UPDATED_ROLE_ID);

        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, updatedUserAuthority.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(updatedUserAuthority))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the UserAuthority in the database
        List<UserAuthority> userAuthorityList = userAuthorityRepository.findAll().collectList().block();
        assertThat(userAuthorityList).hasSize(databaseSizeBeforeUpdate);
        UserAuthority testUserAuthority = userAuthorityList.get(userAuthorityList.size() - 1);
        assertThat(testUserAuthority.getUserId()).isEqualTo(UPDATED_USER_ID);
        assertThat(testUserAuthority.getRoleId()).isEqualTo(UPDATED_ROLE_ID);
    }

    @Test
    void putNonExistingUserAuthority() throws Exception {
        int databaseSizeBeforeUpdate = userAuthorityRepository.findAll().collectList().block().size();
        userAuthority.setId(count.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, userAuthority.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(userAuthority))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the UserAuthority in the database
        List<UserAuthority> userAuthorityList = userAuthorityRepository.findAll().collectList().block();
        assertThat(userAuthorityList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void putWithIdMismatchUserAuthority() throws Exception {
        int databaseSizeBeforeUpdate = userAuthorityRepository.findAll().collectList().block().size();
        userAuthority.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, count.incrementAndGet())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(userAuthority))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the UserAuthority in the database
        List<UserAuthority> userAuthorityList = userAuthorityRepository.findAll().collectList().block();
        assertThat(userAuthorityList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void putWithMissingIdPathParamUserAuthority() throws Exception {
        int databaseSizeBeforeUpdate = userAuthorityRepository.findAll().collectList().block().size();
        userAuthority.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(userAuthority))
            .exchange()
            .expectStatus()
            .isEqualTo(405);

        // Validate the UserAuthority in the database
        List<UserAuthority> userAuthorityList = userAuthorityRepository.findAll().collectList().block();
        assertThat(userAuthorityList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void partialUpdateUserAuthorityWithPatch() throws Exception {
        // Initialize the database
        userAuthorityRepository.save(userAuthority).block();

        int databaseSizeBeforeUpdate = userAuthorityRepository.findAll().collectList().block().size();

        // Update the userAuthority using partial update
        UserAuthority partialUpdatedUserAuthority = new UserAuthority();
        partialUpdatedUserAuthority.setId(userAuthority.getId());

        partialUpdatedUserAuthority.userId(UPDATED_USER_ID).roleId(UPDATED_ROLE_ID);

        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, partialUpdatedUserAuthority.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(partialUpdatedUserAuthority))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the UserAuthority in the database
        List<UserAuthority> userAuthorityList = userAuthorityRepository.findAll().collectList().block();
        assertThat(userAuthorityList).hasSize(databaseSizeBeforeUpdate);
        UserAuthority testUserAuthority = userAuthorityList.get(userAuthorityList.size() - 1);
        assertThat(testUserAuthority.getUserId()).isEqualTo(UPDATED_USER_ID);
        assertThat(testUserAuthority.getRoleId()).isEqualTo(UPDATED_ROLE_ID);
    }

    @Test
    void fullUpdateUserAuthorityWithPatch() throws Exception {
        // Initialize the database
        userAuthorityRepository.save(userAuthority).block();

        int databaseSizeBeforeUpdate = userAuthorityRepository.findAll().collectList().block().size();

        // Update the userAuthority using partial update
        UserAuthority partialUpdatedUserAuthority = new UserAuthority();
        partialUpdatedUserAuthority.setId(userAuthority.getId());

        partialUpdatedUserAuthority.userId(UPDATED_USER_ID).roleId(UPDATED_ROLE_ID);

        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, partialUpdatedUserAuthority.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(partialUpdatedUserAuthority))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the UserAuthority in the database
        List<UserAuthority> userAuthorityList = userAuthorityRepository.findAll().collectList().block();
        assertThat(userAuthorityList).hasSize(databaseSizeBeforeUpdate);
        UserAuthority testUserAuthority = userAuthorityList.get(userAuthorityList.size() - 1);
        assertThat(testUserAuthority.getUserId()).isEqualTo(UPDATED_USER_ID);
        assertThat(testUserAuthority.getRoleId()).isEqualTo(UPDATED_ROLE_ID);
    }

    @Test
    void patchNonExistingUserAuthority() throws Exception {
        int databaseSizeBeforeUpdate = userAuthorityRepository.findAll().collectList().block().size();
        userAuthority.setId(count.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, userAuthority.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(userAuthority))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the UserAuthority in the database
        List<UserAuthority> userAuthorityList = userAuthorityRepository.findAll().collectList().block();
        assertThat(userAuthorityList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void patchWithIdMismatchUserAuthority() throws Exception {
        int databaseSizeBeforeUpdate = userAuthorityRepository.findAll().collectList().block().size();
        userAuthority.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, count.incrementAndGet())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(userAuthority))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the UserAuthority in the database
        List<UserAuthority> userAuthorityList = userAuthorityRepository.findAll().collectList().block();
        assertThat(userAuthorityList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void patchWithMissingIdPathParamUserAuthority() throws Exception {
        int databaseSizeBeforeUpdate = userAuthorityRepository.findAll().collectList().block().size();
        userAuthority.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(userAuthority))
            .exchange()
            .expectStatus()
            .isEqualTo(405);

        // Validate the UserAuthority in the database
        List<UserAuthority> userAuthorityList = userAuthorityRepository.findAll().collectList().block();
        assertThat(userAuthorityList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void deleteUserAuthority() {
        // Initialize the database
        userAuthorityRepository.save(userAuthority).block();

        int databaseSizeBeforeDelete = userAuthorityRepository.findAll().collectList().block().size();

        // Delete the userAuthority
        webTestClient
            .delete()
            .uri(ENTITY_API_URL_ID, userAuthority.getId())
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isNoContent();

        // Validate the database contains one less item
        List<UserAuthority> userAuthorityList = userAuthorityRepository.findAll().collectList().block();
        assertThat(userAuthorityList).hasSize(databaseSizeBeforeDelete - 1);
    }
}
