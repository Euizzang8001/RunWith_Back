package park.brothers.runwith_back.domain.Schedule.repository;

import park.brothers.runwith_back.domain.Schedule.entity.Schedule;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ScheduleRepository {
    Schedule save(Schedule schedule);

    void delete(Schedule schedule);

    List<Schedule> findAllSchedule();

    List<Schedule> findByLocalDate(LocalDate localDate);

    List<Schedule> findByBelongId(UUID belongId);

    List<Schedule> findByBelongIdAndLocalDate(UUID belongId, LocalDate localDate);

    void reviseSchedule(Schedule schedule, String description);

    Optional<Schedule> findScheduleById(UUID scheduleId);

    List<Schedule> findByRunnerIdAndLocalDate(String runnerId, LocalDate localDate);

    List<Schedule> findScheduleByRunnerId(String runnerId);
}
