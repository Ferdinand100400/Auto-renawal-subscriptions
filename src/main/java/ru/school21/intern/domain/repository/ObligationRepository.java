package ru.school21.intern.domain.repository;

import org.springframework.stereotype.Repository;
import ru.school21.intern.datalayer.entity.Obligation;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface ObligationRepository {

    void save(Obligation obligation);
    List<Obligation> findAll();
    Optional<Obligation> findById(UUID id);
    Optional<Obligation> findActiveByTitle(String title);
    void deleteById(UUID id);

}
