package ru.school21.intern.datalayer.mapper;

import org.springframework.jdbc.core.RowMapper;
import org.springframework.lang.Nullable;
import ru.school21.intern.datalayer.entity.Obligation;
import ru.school21.intern.domain.model.Category;
import ru.school21.intern.domain.model.Recurrence;
import ru.school21.intern.domain.model.Status;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.UUID;


public class ObligationRowMapper implements RowMapper<Obligation> {

    @Override
    public @Nullable Obligation mapRow(ResultSet rs, int rowNum) throws SQLException {
        String category = rs.getString("category");
        String recurrence = rs.getString("recurrence");
        String status = rs.getString("status");
        return new Obligation(
                rs.getObject("id", UUID.class),
                rs.getString("title"),
                rs.getBigDecimal("amount"),
                rs.getString("currency"),
                Category.valueOf(category),
                Recurrence.valueOf(recurrence),
                rs.getDate("next_payment_date"),
                Status.valueOf(status),
                rs.getTimestamp("created_at"),
                rs.getTimestamp("updated_at")
        );
    }
}
