package com.invygo.digital.domain;

import java.io.Serializable;
import java.time.ZonedDateTime;
import javax.validation.constraints.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

/**
 * A Schedule.
 */
@Table("schedule")
public class Schedule implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @Column("id")
    private Long id;

    @NotNull(message = "must not be null")
    @Column("user_id")
    private Long userId;

    @NotNull(message = "must not be null")
    @Column("work_date")
    private ZonedDateTime workDate;

    @NotNull(message = "must not be null")
    @Column("hours")
    private Long hours;

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public Schedule id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getUserId() {
        return this.userId;
    }

    public Schedule userId(Long userId) {
        this.setUserId(userId);
        return this;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public ZonedDateTime getWorkDate() {
        return this.workDate;
    }

    public Schedule workDate(ZonedDateTime workDate) {
        this.setWorkDate(workDate);
        return this;
    }

    public void setWorkDate(ZonedDateTime workDate) {
        this.workDate = workDate;
    }

    public Long getHours() {
        return this.hours;
    }

    public Schedule hours(Long hours) {
        this.setHours(hours);
        return this;
    }

    public void setHours(Long hours) {
        this.hours = hours;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Schedule)) {
            return false;
        }
        return id != null && id.equals(((Schedule) o).id);
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "Schedule{" +
            "id=" + getId() +
            ", userId=" + getUserId() +
            ", workDate='" + getWorkDate() + "'" +
            ", hours=" + getHours() +
            "}";
    }
}
