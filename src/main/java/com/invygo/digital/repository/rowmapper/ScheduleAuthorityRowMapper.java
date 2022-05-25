package com.invygo.digital.repository.rowmapper;

import com.invygo.digital.domain.ScheduleAuthority;
import io.r2dbc.spi.Row;
import java.util.function.BiFunction;
import org.springframework.stereotype.Service;

/**
 * Converter between {@link Row} to {@link ScheduleAuthority}, with proper type conversions.
 */
@Service
public class ScheduleAuthorityRowMapper implements BiFunction<Row, String, ScheduleAuthority> {

    private final ColumnConverter converter;

    public ScheduleAuthorityRowMapper(ColumnConverter converter) {
        this.converter = converter;
    }

    /**
     * Take a {@link Row} and a column prefix, and extract all the fields.
     * @return the {@link ScheduleAuthority} stored in the database.
     */
    @Override
    public ScheduleAuthority apply(Row row, String prefix) {
        ScheduleAuthority entity = new ScheduleAuthority();
        entity.setId(converter.fromRow(row, prefix + "_id", Long.class));
        entity.setName(converter.fromRow(row, prefix + "_name", String.class));
        return entity;
    }
}
