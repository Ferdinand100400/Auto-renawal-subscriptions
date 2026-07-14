package ru.school21.intern.datalayer.mapper;

import org.jspecify.annotations.Nullable;
import org.springframework.jdbc.core.RowMapper;
import ru.school21.intern.datalayer.entity.Payment;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.UUID;


public class PaymentRowMapper implements RowMapper<Payment> {

    @Override
    public @Nullable Payment mapRow(ResultSet rs, int rowNum) throws SQLException {
        return new Payment(
                rs.getObject("id", UUID.class),
                rs.getObject("obligation_id", UUID.class),
                rs.getBigDecimal("amount"),
                rs.getString("currency"),
                rs.getTimestamp("paid_at")
        );
    }
}
