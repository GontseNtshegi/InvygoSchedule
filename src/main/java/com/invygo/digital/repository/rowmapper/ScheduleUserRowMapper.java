package com.invygo.digital.repository.rowmapper;

import com.invygo.digital.domain.ScheduleUser;
import io.r2dbc.spi.Row;
import java.util.function.BiFunction;
import org.springframework.stereotype.Service;

/**
 * Converter between {@link Row} to {@link ScheduleUser}, with proper type conversions.
 */
@Service
public class ScheduleUserRowMapper implements BiFunction<Row, String, ScheduleUser> {

    private final ColumnConverter converter;

    public ScheduleUserRowMapper(ColumnConverter converter) {
        this.converter = converter;
    }

    /**
     * Take a {@link Row} and a column prefix, and extract all the fields.
     * @return the {@link ScheduleUser} stored in the database.
     */
    @Override
    public ScheduleUser apply(Row row, String prefix) {
        ScheduleUser entity = new ScheduleUser();
        entity.setId(converter.fromRow(row, prefix + "_id", Long.class));
        entity.setEmail(converter.fromRow(row, prefix + "_email", String.class));
        entity.setLogin(converter.fromRow(row, prefix + "_login", String.class));
        entity.setFirstname(converter.fromRow(row, prefix + "_firstname", String.class));
        entity.setLastname(converter.fromRow(row, prefix + "_lastname", String.class));
        entity.setPassword(converter.fromRow(row, prefix + "_password", String.class));
        return entity;
    }
}
