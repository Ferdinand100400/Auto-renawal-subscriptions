package ru.school21.intern.datalayer.repository;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import ru.school21.intern.datalayer.entity.Obligation;
import ru.school21.intern.datalayer.mapper.ObligationRowMapper;
import ru.school21.intern.domain.repository.ObligationRepository;

import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public class JdbcObligationRepository implements ObligationRepository {

    private final JdbcTemplate jdbcTemplate;

    public JdbcObligationRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public Obligation save(Obligation obligation) {
        if (obligation.id() == null) {
            return insert(obligation);
        } else {
            return update(obligation);
        }
    }

    @Override
    public List<Obligation> findAll() {
        return jdbcTemplate.query("SELECT * FROM obligations", new ObligationRowMapper());
    }

    @Override
    public Optional<Obligation> findById(UUID id) {
        return Optional.ofNullable(jdbcTemplate.queryForObject("SELECT * FROM obligations WHERE id = ?", new ObligationRowMapper(), id));
    }

    @Override
    public Optional<Obligation> findActiveByTitle(String title) {
        return Optional.ofNullable(jdbcTemplate.queryForObject("SELECT * FROM obligations WHERE status = 'active' AND title = ?", new ObligationRowMapper(), title));
    }

    @Override
    public void deleteById(UUID id) {
        String sql = "DELETE FROM obligations WHERE id = ?";
        jdbcTemplate.update(sql, id);
    }

    private Obligation insert(Obligation obligation) {
        String sql = """
            INSERT INTO obligations (
                id, title, amount, currency, category, recurrence,
                next_payment_date, status, created_at, updated_at
            ) VALUES (?, ?, ?, ?, ?::categoty_enum, ?::recurrence_enum, ?, ?::status_enum, ?, ?)
        """;

        UUID id = UUID.randomUUID();
        Timestamp now = Timestamp.from(java.time.Instant.now());

        KeyHolder keyHolder = new GeneratedKeyHolder();

        jdbcTemplate.update(connection -> {
            PreparedStatement ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            ps.setObject(1, id);
            ps.setString(2, obligation.title());
            ps.setBigDecimal(3, obligation.amount());
            ps.setString(4, obligation.currency());
            ps.setString(5, obligation.category().value());
            ps.setString(6, obligation.recurrence().value());
            if (obligation.nextPaymentDate() != null) {
                ps.setDate(7, new Date(obligation.nextPaymentDate().getTime()));
            } else {
                ps.setNull(7, java.sql.Types.DATE);
            }
            ps.setString(8, obligation.status().value());
            ps.setTimestamp(9, now);
            ps.setTimestamp(10, now);
            return ps;
        }, keyHolder);

        // Если есть сгенерированный ключ
        if (keyHolder.getKey() != null) {
            // id уже сгенерирован, обновляем даты
            obligation.setCreatedAt(now);
            obligation.setUpdatedAt(now);
        }

        return obligation;
    }

    private Obligation update(Obligation obligation) {
        String sql = """
            UPDATE obligations 
            SET title = ?, amount = ?, currency = ?, category = ?::categoty_enum, 
                recurrence = ?::recurrence_enum, next_payment_date = ?, status = ?::status_enum, updated_at = ?
            WHERE id = ?
        """;

        Timestamp now = Timestamp.from(java.time.Instant.now());

        jdbcTemplate.update(sql,
                obligation.title(),
                obligation.amount(),
                obligation.currency(),
                obligation.category().value(),
                obligation.recurrence().value(),
                obligation.nextPaymentDate() != null
                        ? new Timestamp(obligation.nextPaymentDate().getTime())
                        : null,
                obligation.status().value(),
                now,
                obligation.id()
        );
        obligation.setUpdatedAt(now);
        return obligation;
    }
}
