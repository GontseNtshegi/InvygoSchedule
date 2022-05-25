package com.invygo.digital.web.rest;

import static com.invygo.digital.web.rest.TestUtil.sameInstant;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.is;

import com.invygo.digital.IntegrationTest;
import com.invygo.digital.domain.Schedule;
import com.invygo.digital.repository.EntityManager;
import com.invygo.digital.repository.ScheduleRepository;
import java.time.Duration;
import java.time.Instant;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
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
 * Integration tests for the {@link ScheduleResource} REST controller.
 */
@IntegrationTest
@AutoConfigureWebTestClient(timeout = IntegrationTest.DEFAULT_ENTITY_TIMEOUT)
@WithMockUser
class ScheduleResourceIT {

    private static final Long DEFAULT_USER_ID = 1L;
    private static final Long UPDATED_USER_ID = 2L;

    private static final ZonedDateTime DEFAULT_WORK_DATE = ZonedDateTime.ofInstant(Instant.ofEpochMilli(0L), ZoneOffset.UTC);
    private static final ZonedDateTime UPDATED_WORK_DATE = ZonedDateTime.now(ZoneId.systemDefault()).withNano(0);

    private static final Long DEFAULT_HOURS = 1L;
    private static final Long UPDATED_HOURS = 2L;

    private static final String ENTITY_API_URL = "/api/schedules";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong count = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private ScheduleRepository scheduleRepository;

    @Autowired
    private EntityManager em;

    @Autowired
    private WebTestClient webTestClient;

    private Schedule schedule;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Schedule createEntity(EntityManager em) {
        Schedule schedule = new Schedule().userId(DEFAULT_USER_ID).workDate(DEFAULT_WORK_DATE).hours(DEFAULT_HOURS);
        return schedule;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Schedule createUpdatedEntity(EntityManager em) {
        Schedule schedule = new Schedule().userId(UPDATED_USER_ID).workDate(UPDATED_WORK_DATE).hours(UPDATED_HOURS);
        return schedule;
    }

    public static void deleteEntities(EntityManager em) {
        try {
            em.deleteAll(Schedule.class).block();
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
        schedule = createEntity(em);
    }

    @Test
    void createSchedule() throws Exception {
        int databaseSizeBeforeCreate = scheduleRepository.findAll().collectList().block().size();
        // Create the Schedule
        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(schedule))
            .exchange()
            .expectStatus()
            .isCreated();

        // Validate the Schedule in the database
        List<Schedule> scheduleList = scheduleRepository.findAll().collectList().block();
        assertThat(scheduleList).hasSize(databaseSizeBeforeCreate + 1);
        Schedule testSchedule = scheduleList.get(scheduleList.size() - 1);
        assertThat(testSchedule.getUserId()).isEqualTo(DEFAULT_USER_ID);
        assertThat(testSchedule.getWorkDate()).isEqualTo(DEFAULT_WORK_DATE);
        assertThat(testSchedule.getHours()).isEqualTo(DEFAULT_HOURS);
    }

    @Test
    void createScheduleWithExistingId() throws Exception {
        // Create the Schedule with an existing ID
        schedule.setId(1L);

        int databaseSizeBeforeCreate = scheduleRepository.findAll().collectList().block().size();

        // An entity with an existing ID cannot be created, so this API call must fail
        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(schedule))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Schedule in the database
        List<Schedule> scheduleList = scheduleRepository.findAll().collectList().block();
        assertThat(scheduleList).hasSize(databaseSizeBeforeCreate);
    }

    @Test
    void checkUserIdIsRequired() throws Exception {
        int databaseSizeBeforeTest = scheduleRepository.findAll().collectList().block().size();
        // set the field null
        schedule.setUserId(null);

        // Create the Schedule, which fails.

        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(schedule))
            .exchange()
            .expectStatus()
            .isBadRequest();

        List<Schedule> scheduleList = scheduleRepository.findAll().collectList().block();
        assertThat(scheduleList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    void checkWorkDateIsRequired() throws Exception {
        int databaseSizeBeforeTest = scheduleRepository.findAll().collectList().block().size();
        // set the field null
        schedule.setWorkDate(null);

        // Create the Schedule, which fails.

        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(schedule))
            .exchange()
            .expectStatus()
            .isBadRequest();

        List<Schedule> scheduleList = scheduleRepository.findAll().collectList().block();
        assertThat(scheduleList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    void checkHoursIsRequired() throws Exception {
        int databaseSizeBeforeTest = scheduleRepository.findAll().collectList().block().size();
        // set the field null
        schedule.setHours(null);

        // Create the Schedule, which fails.

        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(schedule))
            .exchange()
            .expectStatus()
            .isBadRequest();

        List<Schedule> scheduleList = scheduleRepository.findAll().collectList().block();
        assertThat(scheduleList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    void getAllSchedulesAsStream() {
        // Initialize the database
        scheduleRepository.save(schedule).block();

        List<Schedule> scheduleList = webTestClient
            .get()
            .uri(ENTITY_API_URL)
            .accept(MediaType.APPLICATION_NDJSON)
            .exchange()
            .expectStatus()
            .isOk()
            .expectHeader()
            .contentTypeCompatibleWith(MediaType.APPLICATION_NDJSON)
            .returnResult(Schedule.class)
            .getResponseBody()
            .filter(schedule::equals)
            .collectList()
            .block(Duration.ofSeconds(5));

        assertThat(scheduleList).isNotNull();
        assertThat(scheduleList).hasSize(1);
        Schedule testSchedule = scheduleList.get(0);
        assertThat(testSchedule.getUserId()).isEqualTo(DEFAULT_USER_ID);
        assertThat(testSchedule.getWorkDate()).isEqualTo(DEFAULT_WORK_DATE);
        assertThat(testSchedule.getHours()).isEqualTo(DEFAULT_HOURS);
    }

    @Test
    void getAllSchedules() {
        // Initialize the database
        scheduleRepository.save(schedule).block();

        // Get all the scheduleList
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
            .value(hasItem(schedule.getId().intValue()))
            .jsonPath("$.[*].userId")
            .value(hasItem(DEFAULT_USER_ID.intValue()))
            .jsonPath("$.[*].workDate")
            .value(hasItem(sameInstant(DEFAULT_WORK_DATE)))
            .jsonPath("$.[*].hours")
            .value(hasItem(DEFAULT_HOURS.intValue()));
    }

    @Test
    void getSchedule() {
        // Initialize the database
        scheduleRepository.save(schedule).block();

        // Get the schedule
        webTestClient
            .get()
            .uri(ENTITY_API_URL_ID, schedule.getId())
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isOk()
            .expectHeader()
            .contentType(MediaType.APPLICATION_JSON)
            .expectBody()
            .jsonPath("$.id")
            .value(is(schedule.getId().intValue()))
            .jsonPath("$.userId")
            .value(is(DEFAULT_USER_ID.intValue()))
            .jsonPath("$.workDate")
            .value(is(sameInstant(DEFAULT_WORK_DATE)))
            .jsonPath("$.hours")
            .value(is(DEFAULT_HOURS.intValue()));
    }

    @Test
    void getNonExistingSchedule() {
        // Get the schedule
        webTestClient
            .get()
            .uri(ENTITY_API_URL_ID, Long.MAX_VALUE)
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isNotFound();
    }

    @Test
    void putNewSchedule() throws Exception {
        // Initialize the database
        scheduleRepository.save(schedule).block();

        int databaseSizeBeforeUpdate = scheduleRepository.findAll().collectList().block().size();

        // Update the schedule
        Schedule updatedSchedule = scheduleRepository.findById(schedule.getId()).block();
        updatedSchedule.userId(UPDATED_USER_ID).workDate(UPDATED_WORK_DATE).hours(UPDATED_HOURS);

        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, updatedSchedule.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(updatedSchedule))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the Schedule in the database
        List<Schedule> scheduleList = scheduleRepository.findAll().collectList().block();
        assertThat(scheduleList).hasSize(databaseSizeBeforeUpdate);
        Schedule testSchedule = scheduleList.get(scheduleList.size() - 1);
        assertThat(testSchedule.getUserId()).isEqualTo(UPDATED_USER_ID);
        assertThat(testSchedule.getWorkDate()).isEqualTo(UPDATED_WORK_DATE);
        assertThat(testSchedule.getHours()).isEqualTo(UPDATED_HOURS);
    }

    @Test
    void putNonExistingSchedule() throws Exception {
        int databaseSizeBeforeUpdate = scheduleRepository.findAll().collectList().block().size();
        schedule.setId(count.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, schedule.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(schedule))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Schedule in the database
        List<Schedule> scheduleList = scheduleRepository.findAll().collectList().block();
        assertThat(scheduleList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void putWithIdMismatchSchedule() throws Exception {
        int databaseSizeBeforeUpdate = scheduleRepository.findAll().collectList().block().size();
        schedule.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, count.incrementAndGet())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(schedule))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Schedule in the database
        List<Schedule> scheduleList = scheduleRepository.findAll().collectList().block();
        assertThat(scheduleList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void putWithMissingIdPathParamSchedule() throws Exception {
        int databaseSizeBeforeUpdate = scheduleRepository.findAll().collectList().block().size();
        schedule.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(schedule))
            .exchange()
            .expectStatus()
            .isEqualTo(405);

        // Validate the Schedule in the database
        List<Schedule> scheduleList = scheduleRepository.findAll().collectList().block();
        assertThat(scheduleList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void partialUpdateScheduleWithPatch() throws Exception {
        // Initialize the database
        scheduleRepository.save(schedule).block();

        int databaseSizeBeforeUpdate = scheduleRepository.findAll().collectList().block().size();

        // Update the schedule using partial update
        Schedule partialUpdatedSchedule = new Schedule();
        partialUpdatedSchedule.setId(schedule.getId());

        partialUpdatedSchedule.userId(UPDATED_USER_ID);

        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, partialUpdatedSchedule.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(partialUpdatedSchedule))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the Schedule in the database
        List<Schedule> scheduleList = scheduleRepository.findAll().collectList().block();
        assertThat(scheduleList).hasSize(databaseSizeBeforeUpdate);
        Schedule testSchedule = scheduleList.get(scheduleList.size() - 1);
        assertThat(testSchedule.getUserId()).isEqualTo(UPDATED_USER_ID);
        assertThat(testSchedule.getWorkDate()).isEqualTo(DEFAULT_WORK_DATE);
        assertThat(testSchedule.getHours()).isEqualTo(DEFAULT_HOURS);
    }

    @Test
    void fullUpdateScheduleWithPatch() throws Exception {
        // Initialize the database
        scheduleRepository.save(schedule).block();

        int databaseSizeBeforeUpdate = scheduleRepository.findAll().collectList().block().size();

        // Update the schedule using partial update
        Schedule partialUpdatedSchedule = new Schedule();
        partialUpdatedSchedule.setId(schedule.getId());

        partialUpdatedSchedule.userId(UPDATED_USER_ID).workDate(UPDATED_WORK_DATE).hours(UPDATED_HOURS);

        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, partialUpdatedSchedule.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(partialUpdatedSchedule))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the Schedule in the database
        List<Schedule> scheduleList = scheduleRepository.findAll().collectList().block();
        assertThat(scheduleList).hasSize(databaseSizeBeforeUpdate);
        Schedule testSchedule = scheduleList.get(scheduleList.size() - 1);
        assertThat(testSchedule.getUserId()).isEqualTo(UPDATED_USER_ID);
        assertThat(testSchedule.getWorkDate()).isEqualTo(UPDATED_WORK_DATE);
        assertThat(testSchedule.getHours()).isEqualTo(UPDATED_HOURS);
    }

    @Test
    void patchNonExistingSchedule() throws Exception {
        int databaseSizeBeforeUpdate = scheduleRepository.findAll().collectList().block().size();
        schedule.setId(count.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, schedule.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(schedule))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Schedule in the database
        List<Schedule> scheduleList = scheduleRepository.findAll().collectList().block();
        assertThat(scheduleList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void patchWithIdMismatchSchedule() throws Exception {
        int databaseSizeBeforeUpdate = scheduleRepository.findAll().collectList().block().size();
        schedule.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, count.incrementAndGet())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(schedule))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Schedule in the database
        List<Schedule> scheduleList = scheduleRepository.findAll().collectList().block();
        assertThat(scheduleList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void patchWithMissingIdPathParamSchedule() throws Exception {
        int databaseSizeBeforeUpdate = scheduleRepository.findAll().collectList().block().size();
        schedule.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(schedule))
            .exchange()
            .expectStatus()
            .isEqualTo(405);

        // Validate the Schedule in the database
        List<Schedule> scheduleList = scheduleRepository.findAll().collectList().block();
        assertThat(scheduleList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void deleteSchedule() {
        // Initialize the database
        scheduleRepository.save(schedule).block();

        int databaseSizeBeforeDelete = scheduleRepository.findAll().collectList().block().size();

        // Delete the schedule
        webTestClient
            .delete()
            .uri(ENTITY_API_URL_ID, schedule.getId())
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isNoContent();

        // Validate the database contains one less item
        List<Schedule> scheduleList = scheduleRepository.findAll().collectList().block();
        assertThat(scheduleList).hasSize(databaseSizeBeforeDelete - 1);
    }
}
