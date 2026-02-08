package park.brothers.runwith_back.domain.Schedule.repository;

import park.brothers.runwith_back.domain.Schedule.entity.Schedule;

import java.time.LocalDate;
import java.util.List;

public interface ScheduleRepository {
    void save(Schedule schedule);

    void delete(Long id);

    List<Schedule> getAllSchedule();

    List<Schedule> getByLocalDate(LocalDate localDate);

    List<Schedule> getByBelongId(Long belongId);

    List<Schedule> getByBelongIdAndLocalDate(Long belongId, LocalDate localDate);

    void reviseSchedule(Long id, String description);
}
