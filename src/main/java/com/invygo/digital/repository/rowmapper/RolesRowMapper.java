package com.invygo.digital.repository.rowmapper;

import com.invygo.digital.domain.Roles;
import io.r2dbc.spi.Row;
import java.util.function.BiFunction;
import org.springframework.stereotype.Service;

/**
 * Converter between {@link Row} to {@link Roles}, with proper type conversions.
 */
@Service
public class RolesRowMapper implements BiFunction<Row, String, Roles> {

    private final ColumnConverter converter;

    public RolesRowMapper(ColumnConverter converter) {
        this.converter = converter;
    }

    /**
     * Take a {@link Row} and a column prefix, and extract all the fields.
     * @return the {@link Roles} stored in the database.
     */
    @Override
    public Roles apply(Row row, String prefix) {
        Roles entity = new Roles();
        entity.setId(converter.fromRow(row, prefix + "_id", Long.class));
        entity.setRoleName(converter.fromRow(row, prefix + "_role_name", String.class));
        return entity;
    }
}
