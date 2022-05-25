package com.invygo.digital.repository.rowmapper;

import com.invygo.digital.domain.Schedule;
import io.r2dbc.spi.Row;
import java.time.ZonedDateTime;
import java.util.function.BiFunction;
import org.springframework.stereotype.Service;

/**
 * Converter between {@link Row} to {@link Schedule}, with proper type conversions.
 */
@Service
public class ScheduleRowMapper implements BiFunction<Row, String, Schedule> {

    private final ColumnConverter converter;

    public ScheduleRowMapper(ColumnConverter converter) {
        this.converter = converter;
    }

    /**
     * Take a {@link Row} and a column prefix, and extract all the fields.
     * @return the {@link Schedule} stored in the database.
     */
    @Override
    public Schedule apply(Row row, String prefix) {
        Schedule entity = new Schedule();
        entity.setId(converter.fromRow(row, prefix + "_id", Long.class));
        entity.setUserId(converter.fromRow(row, prefix + "_user_id", Long.class));
        entity.setWorkDate(converter.fromRow(row, prefix + "_work_date", ZonedDateTime.class));
        entity.setHours(converter.fromRow(row, prefix + "_hours", Long.class));
        return entity;
    }
}
