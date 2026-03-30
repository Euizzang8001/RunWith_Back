package park.brothers.runwith_back.domain.Schedule.repository;


import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import park.brothers.runwith_back.domain.Schedule.entity.Schedule;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
@Primary
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class JpaScheduleRepository implements ScheduleRepository{

    private final EntityManager em;

    //schedule 객체 저장
    @Override
    @Transactional
    public Schedule save(Schedule schedule) {
        em.persist(schedule);
        return schedule;
    }

    // 스케줄 객체 삭제
    @Override
    @Transactional
    public void delete(Schedule schedule) {
        em.remove(schedule);
    }

    //전체 스케줄 조회
    @Override
    public List<Schedule> findAllSchedule() {
        return em.createQuery("select s from Schedule s order by s.scheduleYear, s.scheduleMonth, s.scheduleDate", Schedule.class)
                .getResultList();
    }

    //LocalDateTime에 따라 조회
    @Override
    public List<Schedule> findByLocalDate(LocalDate localDate) {
        return em.createQuery("select s from Schedule s where s.scheduleYear = :year and s.scheduleMonth = :month order by s.scheduleYear, s.scheduleMonth, s.scheduleDate", Schedule.class)
                .setParameter("year", localDate.getYear())
                .setParameter("month", localDate.getMonthValue())
                .getResultList();
    }

    //Belong Id에 따라 조회
    @Override
    public List<Schedule> findByBelongId(UUID belongId) {
        return em.createQuery("select s from Schedule s where s.belong.id = :belongId order by s.scheduleYear, s.scheduleMonth, s.scheduleDate", Schedule.class)
                .setParameter("belongId", belongId)
                .getResultList();
    }

    //BelongId와 LocalDateTime에 따라 조회
    @Override
    public List<Schedule> findByBelongIdAndLocalDate(UUID belongId, LocalDate localDateTime) {
        return em.createQuery("select s from Schedule s where s.scheduleYear = :year and s.scheduleMonth = :month and s.belong.id = :belongId order by s.scheduleYear, s.scheduleMonth, s.scheduleDate", Schedule.class)
                .setParameter("year", localDateTime.getYear())
                .setParameter("month", localDateTime.getMonthValue())
                .setParameter("belongId", belongId)
                .getResultList();
    }

    //스케줄 수정하기
    @Override
    @Transactional
    public void reviseSchedule(Schedule schedule, String description) {
        schedule.setDescription(description);
    }

    //id로 스케줄 객체 하나 찾기
    @Override
    public Optional<Schedule> findScheduleById(UUID id) {
        return Optional.ofNullable(em.createQuery("select s from Schedule s where s.id = :id", Schedule.class)
                .setParameter("id", id)
                .getSingleResult());
    }

    //러너 id와 날짜로 스케줄 찾기
    @Override
    public List<Schedule> findByRunnerIdAndLocalDate(String runnerId, LocalDate localDateTime) {
        return em.createQuery("select s from Schedule s where s.scheduleYear = :year and s.scheduleMonth = :month and s.belong.runner.id = :runnerId order by s.scheduleYear, s.scheduleMonth, s.scheduleDate", Schedule.class)
                .setParameter("year", localDateTime.getYear())
                .setParameter("month", localDateTime.getMonthValue())
                .setParameter("runnerId", runnerId)
                .getResultList();
    }

    //오늘
    @Override
    public List<Schedule> findScheduleByRunnerId(String runnerId) {
        return em.createQuery("select s from Schedule s where s.belong.runner.id = :runnerId order by s.scheduleYear, s.scheduleMonth, s.scheduleDate", Schedule.class)
                .setParameter("runnerId", runnerId)
                .getResultList();
    }
}
