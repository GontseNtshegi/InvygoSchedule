package com.invygo.digital.repository.rowmapper;

import com.invygo.digital.domain.UserAuthority;
import io.r2dbc.spi.Row;
import java.util.function.BiFunction;
import org.springframework.stereotype.Service;

/**
 * Converter between {@link Row} to {@link UserAuthority}, with proper type conversions.
 */
@Service
public class UserAuthorityRowMapper implements BiFunction<Row, String, UserAuthority> {

    private final ColumnConverter converter;

    public UserAuthorityRowMapper(ColumnConverter converter) {
        this.converter = converter;
    }

    /**
     * Take a {@link Row} and a column prefix, and extract all the fields.
     * @return the {@link UserAuthority} stored in the database.
     */
    @Override
    public UserAuthority apply(Row row, String prefix) {
        UserAuthority entity = new UserAuthority();
        entity.setId(converter.fromRow(row, prefix + "_id", Long.class));
        entity.setUserId(converter.fromRow(row, prefix + "_user_id", Long.class));
        entity.setRoleId(converter.fromRow(row, prefix + "_role_id", Long.class));
        return entity;
    }
}
