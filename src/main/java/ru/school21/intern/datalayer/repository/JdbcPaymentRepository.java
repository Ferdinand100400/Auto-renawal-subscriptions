package ru.school21.intern.datalayer.repository;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import ru.school21.intern.datalayer.entity.Payment;
import ru.school21.intern.datalayer.mapper.PaymentRowMapper;
import ru.school21.intern.domain.repository.PaymentRepository;

import java.sql.PreparedStatement;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.List;
import java.util.UUID;

public class JdbcPaymentRepository implements PaymentRepository {

    private final JdbcTemplate jdbcTemplate;

    public JdbcPaymentRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public Payment createPayment(Payment payment) {
        String sql = """
            INSERT INTO payments (
                id, obligation_id, amount, currency, paid_at
            ) VALUES (?, ?, ?, ?, ?)
        """;

        UUID id = UUID.randomUUID();
        Timestamp now = Timestamp.from(java.time.Instant.now());

        KeyHolder keyHolder = new GeneratedKeyHolder();

        jdbcTemplate.update(connection -> {
            PreparedStatement ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            ps.setObject(1, id);
            ps.setObject(2, payment.obligation_id());
            ps.setBigDecimal(3, payment.amount());
            ps.setString(4, payment.currency());
            ps.setTimestamp(5, now);
            return ps;
        }, keyHolder);

        // Если есть сгенерированный ключ
        if (keyHolder.getKey() != null) {
            // id уже сгенерирован, обновляем даты
            payment.setPaidAt(now);
        }

        return payment;
    }

    @Override
    public List<Payment> findAllByObligationId(UUID obligationId) {
        return jdbcTemplate.query(
                "SELECT * FROM payments WHERE obligation_id = ?",
                new PaymentRowMapper(),
                obligationId
        );
    }

    @Override
    public void deleteById(UUID id) {
        String sql = "DELETE FROM payments WHERE id = ?";
        jdbcTemplate.update(sql, id);
    }
}
