package park.brothers.runwith_back.domain.Schedule.repository;


import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Repository;
import park.brothers.runwith_back.domain.Schedule.entity.Schedule;

import java.time.LocalDate;
import java.util.List;

@Repository
@Slf4j
@Primary
@RequiredArgsConstructor
public class JpaScheduleRepository implements ScheduleRepository{

    private final EntityManager em;

    //schedule 객체 저장
    @Override
    public void save(Schedule schedule) {
        em.persist(schedule);
    }

    // 스케줄 객체 삭제
    @Override
    public void delete(Long id) {
        Schedule schedule = em.createQuery("select s from Schedule s where id = :id", Schedule.class)
                .setParameter("id", id)
                .getSingleResult();
        em.remove(schedule);
    }

    //전체 스케줄 조회
    @Override
    public List<Schedule> getAllSchedule() {
        return em.createQuery("select s from Schedule s", Schedule.class)
                .getResultList();
    }

    //LocalDateTime에 따라 조회
    @Override
    public List<Schedule> getByLocalDate(LocalDate localDate) {
        return em.createQuery("select s from Schedule s where s.scheduleYear = :year and s.scheduleMonth = :month", Schedule.class)
                .setParameter("year", localDate.getYear())
                .setParameter("month", localDate.getMonthValue())
                .getResultList();
    }

    //Belong Id에 따라 조회
    @Override
    public List<Schedule> getByBelongId(Long belongId) {
        return em.createQuery("select s from Schedule s where s.belong.id = :belongId", Schedule.class)
                .setParameter("belongId", belongId)
                .getResultList();
    }

    //BelongId와 LocalDateTime에 따라 조회
    @Override
    public List<Schedule> getByBelongIdAndLocalDate(Long belongId, LocalDate localDateTime) {
        return em.createQuery("select s from Schedule s where s.scheduleYear = :year and s.scheduleMonth = :month and s.belong.id = :belongId", Schedule.class)
                .setParameter("year", localDateTime.getYear())
                .setParameter("month", localDateTime.getMonthValue())
                .setParameter("belongId", belongId)
                .getResultList();
    }

    //스케줄 수정하기
    @Override
    public void reviseSchedule(Long id, String description) {
        Schedule schedule = em.createQuery("select s from Schedule s where s.id = :id", Schedule.class)
                .setParameter("id", id)
                .getSingleResult();
        schedule.setDescription(description);
    }

    //id로 스케줄 객체 하나 찾기
    @Override
    public Schedule getScheduleById(Long id) {
        return em.createQuery("select s from Schedule s where s.id = :id", Schedule.class)
                .setParameter("id", id)
                .getSingleResult();
    }
}
