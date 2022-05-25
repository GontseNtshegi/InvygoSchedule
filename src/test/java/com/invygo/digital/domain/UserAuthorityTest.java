package com.invygo.digital.domain;

import static org.assertj.core.api.Assertions.assertThat;

import com.invygo.digital.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class UserAuthorityTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(UserAuthority.class);
        UserAuthority userAuthority1 = new UserAuthority();
        userAuthority1.setId(1L);
        UserAuthority userAuthority2 = new UserAuthority();
        userAuthority2.setId(userAuthority1.getId());
        assertThat(userAuthority1).isEqualTo(userAuthority2);
        userAuthority2.setId(2L);
        assertThat(userAuthority1).isNotEqualTo(userAuthority2);
        userAuthority1.setId(null);
        assertThat(userAuthority1).isNotEqualTo(userAuthority2);
    }
}
