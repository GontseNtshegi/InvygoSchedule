package com.invygo.digital.domain;

import static org.assertj.core.api.Assertions.assertThat;

import com.invygo.digital.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class RoleAuthorityTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(RoleAuthority.class);
        RoleAuthority roleAuthority1 = new RoleAuthority();
        roleAuthority1.setId(1L);
        RoleAuthority roleAuthority2 = new RoleAuthority();
        roleAuthority2.setId(roleAuthority1.getId());
        assertThat(roleAuthority1).isEqualTo(roleAuthority2);
        roleAuthority2.setId(2L);
        assertThat(roleAuthority1).isNotEqualTo(roleAuthority2);
        roleAuthority1.setId(null);
        assertThat(roleAuthority1).isNotEqualTo(roleAuthority2);
    }
}
