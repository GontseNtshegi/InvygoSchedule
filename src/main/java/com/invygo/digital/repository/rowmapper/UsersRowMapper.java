package com.invygo.digital.repository.rowmapper;

import com.invygo.digital.domain.Users;
import io.r2dbc.spi.Row;
import java.util.function.BiFunction;
import org.springframework.stereotype.Service;

/**
 * Converter between {@link Row} to {@link Users}, with proper type conversions.
 */
@Service
public class UsersRowMapper implements BiFunction<Row, String, Users> {

    private final ColumnConverter converter;

    public UsersRowMapper(ColumnConverter converter) {
        this.converter = converter;
    }

    /**
     * Take a {@link Row} and a column prefix, and extract all the fields.
     * @return the {@link Users} stored in the database.
     */
    @Override
    public Users apply(Row row, String prefix) {
        Users entity = new Users();
        entity.setId(converter.fromRow(row, prefix + "_id", Long.class));
        entity.setEmail(converter.fromRow(row, prefix + "_email", String.class));
        entity.setName(converter.fromRow(row, prefix + "_name", String.class));
        entity.setSurname(converter.fromRow(row, prefix + "_surname", String.class));
        entity.setPassword(converter.fromRow(row, prefix + "_password", String.class));
        return entity;
    }
}
