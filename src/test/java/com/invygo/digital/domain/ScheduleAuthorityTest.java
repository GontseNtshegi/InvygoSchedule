package com.invygo.digital.domain;

import static org.assertj.core.api.Assertions.assertThat;

import com.invygo.digital.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class ScheduleAuthorityTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(ScheduleAuthority.class);
        ScheduleAuthority scheduleAuthority1 = new ScheduleAuthority();
        scheduleAuthority1.setId(1L);
        ScheduleAuthority scheduleAuthority2 = new ScheduleAuthority();
        scheduleAuthority2.setId(scheduleAuthority1.getId());
        assertThat(scheduleAuthority1).isEqualTo(scheduleAuthority2);
        scheduleAuthority2.setId(2L);
        assertThat(scheduleAuthority1).isNotEqualTo(scheduleAuthority2);
        scheduleAuthority1.setId(null);
        assertThat(scheduleAuthority1).isNotEqualTo(scheduleAuthority2);
    }
}
