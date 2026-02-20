package park.brothers.runwith_back.domain.Schedule.repository;

import park.brothers.runwith_back.domain.Schedule.entity.Schedule;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ScheduleRepository {
    void save(Schedule schedule);

    void delete(UUID id);

    List<Schedule> fintAllSchedule();

    List<Schedule> findByLocalDate(LocalDate localDate);

    List<Schedule> findByBelongId(UUID belongId);

    List<Schedule> findByBelongIdAndLocalDate(UUID belongId, LocalDate localDate);

    void reviseSchedule(UUID id, String description);

    Optional<Schedule> findScheduleById(UUID scheduleId);
}
