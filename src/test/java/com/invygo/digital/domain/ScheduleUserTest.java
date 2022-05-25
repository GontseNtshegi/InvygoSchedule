package com.invygo.digital.domain;

import static org.assertj.core.api.Assertions.assertThat;

import com.invygo.digital.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class ScheduleUserTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(ScheduleUser.class);
        ScheduleUser scheduleUser1 = new ScheduleUser();
        scheduleUser1.setId(1L);
        ScheduleUser scheduleUser2 = new ScheduleUser();
        scheduleUser2.setId(scheduleUser1.getId());
        assertThat(scheduleUser1).isEqualTo(scheduleUser2);
        scheduleUser2.setId(2L);
        assertThat(scheduleUser1).isNotEqualTo(scheduleUser2);
        scheduleUser1.setId(null);
        assertThat(scheduleUser1).isNotEqualTo(scheduleUser2);
    }
}
