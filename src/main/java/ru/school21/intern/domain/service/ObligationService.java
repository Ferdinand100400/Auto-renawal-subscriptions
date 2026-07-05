package ru.school21.intern.domain.service;

import org.springframework.stereotype.Service;
import ru.school21.intern.datalayer.mapper.ObligationMapper;
import ru.school21.intern.domain.exception.ActiveObligationWithTitleAlreadyExists;
import ru.school21.intern.domain.exception.NotActiveStatusObligation;
import ru.school21.intern.domain.exception.NotFindObligationById;
import ru.school21.intern.domain.model.Category;
import ru.school21.intern.domain.model.ObligationDto;
import ru.school21.intern.domain.model.Recurrence;
import ru.school21.intern.domain.model.Status;
import ru.school21.intern.domain.repository.ObligationRepository;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.UUID;

@Service
public class ObligationService {

    private final ObligationRepository obligationRepo;

    public ObligationService(ObligationRepository obligationRepo) {
        this.obligationRepo = obligationRepo;
    }

    public ObligationDto createNewObligation(ObligationDto obligationDto) {
        Date currentDate = new Date();
        if (obligationDto.nextPaymentDate().before(currentDate))
            obligationDto.setStatus(Status.EXPIRED);
        else
            obligationDto.setStatus(Status.ACTIVE);
        obligationRepo.save(ObligationMapper.dtoToEntity(obligationDto));
        if (obligationRepo.findActiveByTitle(obligationDto.title().toLowerCase()).isPresent())
            throw new ActiveObligationWithTitleAlreadyExists(obligationDto);
        return obligationDto;
    }

    public List<ObligationDto> getAllObligation(Category category, Status status) {
        lazyExpiry();
        if (category == null && status == null)
            return ObligationMapper.listEntityToListDto(obligationRepo.findAll()).stream()
                    .sorted(Comparator.comparing(ObligationDto::nextPaymentDate))
                    .toList();
        if (category != null && status == null)
            return getObligationByCategory(category);
        if (category == null)
            return getObligationByStatus(status);
        return getObligationByCategoryAndStatus(category, status);
    }

    private List<ObligationDto> getObligationByCategory(Category category) {
        return getAllObligation(null, null).stream()
                .filter(o -> o.category().equals(category))
                .toList();
    }

    private List<ObligationDto> getObligationByStatus(Status status) {
        return getAllObligation(null, null).stream()
                .filter(o -> o.status().equals(status))
                .toList();
    }

    private List<ObligationDto> getObligationByCategoryAndStatus(Category category, Status status) {
        return getObligationByCategory(category).stream()
                .filter(o -> o.status().equals(status))
                .toList();
    }

    public List<ObligationDto> getObligationByNextPaymentToRange(int n) {
        return getAllObligation(null, null).stream()
                .filter(o -> (o.nextPaymentDate().after(new Date()))
                        && o.nextPaymentDate().before(new Date(new Date().getTime() + n * 24 * 60 * 60 * 1000L)))
                .toList();
    }

    public List<ObligationDto> getObligationByNextPaymentToRange() {
        return getObligationByNextPaymentToRange(7);
    }

    public ObligationDto pay(UUID id) {
        ObligationDto obligationDto = ObligationMapper.entityToDto(
                obligationRepo.findById(id).orElseThrow(() -> new NotFindObligationById(id))
        );
        if (!obligationDto.status().equals(Status.ACTIVE))
            throw new NotActiveStatusObligation(obligationDto.status().value());
        if (obligationDto.recurrence().equals(Recurrence.NULL)) {
            obligationDto.setStatus(Status.CANCELLED);
            obligationRepo.save(ObligationMapper.dtoToEntity(obligationDto));
            return obligationDto;
        }
        LocalDate localDate = obligationDto.nextPaymentDate().toInstant()
                .atZone(ZoneId.systemDefault())
                .toLocalDate();
        LocalDate newDate = null;
        if (obligationDto.recurrence().equals(Recurrence.MONTHLY))
            newDate = localDate.plusMonths(1);
        if (obligationDto.recurrence().equals(Recurrence.QUARTERLY))
            newDate = localDate.plusMonths(3);
        if (obligationDto.recurrence().equals(Recurrence.YEARLY))
            newDate = localDate.plusYears(1);
        if (newDate != null)
            obligationDto.updateNextPaymentDate(Date.from(newDate.atStartOfDay(ZoneId.systemDefault()).toInstant()));
        obligationRepo.save(ObligationMapper.dtoToEntity(obligationDto));
        return obligationDto;
    }

    public void cancelObligation(UUID id) {
        ObligationDto obligationDto = ObligationMapper.entityToDto(
                obligationRepo.findById(id).orElseThrow(() -> new NotFindObligationById(id))
        );
        if (!obligationDto.status().equals(Status.ACTIVE))
            throw new NotActiveStatusObligation(obligationDto.status().value());
        obligationDto.setStatus(Status.CANCELLED);
        obligationRepo.save(ObligationMapper.dtoToEntity(obligationDto));
    }

    public void deleteObligation(UUID id) {
        obligationRepo.deleteById(id);
    }

    private void lazyExpiry() {
        for (ObligationDto obligationDto : ObligationMapper.listEntityToListDto(obligationRepo.findAll())) {
            if (obligationDto.recurrence().equals(Recurrence.NULL)
                    && obligationDto.status().equals(Status.ACTIVE)
                    && obligationDto.nextPaymentDate().before(new Date())) {
                obligationDto.setStatus(Status.EXPIRED);
                obligationRepo.save(ObligationMapper.dtoToEntity(obligationDto));
            }
        }
    }
}
