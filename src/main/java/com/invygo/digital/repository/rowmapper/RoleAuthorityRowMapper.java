package com.invygo.digital.repository.rowmapper;

import com.invygo.digital.domain.RoleAuthority;
import io.r2dbc.spi.Row;
import java.util.function.BiFunction;
import org.springframework.stereotype.Service;

/**
 * Converter between {@link Row} to {@link RoleAuthority}, with proper type conversions.
 */
@Service
public class RoleAuthorityRowMapper implements BiFunction<Row, String, RoleAuthority> {

    private final ColumnConverter converter;

    public RoleAuthorityRowMapper(ColumnConverter converter) {
        this.converter = converter;
    }

    /**
     * Take a {@link Row} and a column prefix, and extract all the fields.
     * @return the {@link RoleAuthority} stored in the database.
     */
    @Override
    public RoleAuthority apply(Row row, String prefix) {
        RoleAuthority entity = new RoleAuthority();
        entity.setId(converter.fromRow(row, prefix + "_id", Long.class));
        entity.setUserId(converter.fromRow(row, prefix + "_user_id", Long.class));
        entity.setRoleId(converter.fromRow(row, prefix + "_role_id", Long.class));
        return entity;
    }
}
