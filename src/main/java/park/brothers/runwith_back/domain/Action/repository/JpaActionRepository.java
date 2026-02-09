package park.brothers.runwith_back.domain.Action.repository;

import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Repository;
import park.brothers.runwith_back.domain.Action.entity.Action;

import java.util.List;

@Repository
@RequiredArgsConstructor
@Slf4j
@Primary
public class JpaActionRepository implements ActionRepository{

    private final EntityManager em;

    //action저장
    @Override
    public void save(Action action) {
        em.persist(action);
    }

    //action 삭제
    @Override
    public void delete(Action action) {
        em.remove(action);
    }

    //id로 action 하나 얻기
    @Override
    public Action getById(Long id) {
        return em.createQuery("select a from Action a where a.id = :id", Action.class)
                .setParameter("id", id)
                .getSingleResult();
    }

    //같은 스케줄에서 중복된 actions들 모두 반환
    @Override
    public List<Action> getOverlappedActions(Long scheduleId, int startHour, int startMinute, int endHour, int endMinute) {
        int carStartMinute = startHour * 60 + startMinute;
        int carEndMinute = endHour * 60 + endMinute;
        return em.createQuery("select a from Action a where a.schedule.id = :scheduleId and a.startHour * 60 + a.startMinute < :carEndMinute and a.endHour * 60 + a.endMinute > :carStartMinute", Action.class)
                .setParameter("carStartMinute", carStartMinute)
                .setParameter("carEndMinute", carEndMinute)
                .getResultList();
    }

    //Action 수정
    @Override
    public void reviseAction(Long id, String name, String description, int startHour, int startMinute, int endHour, int endMinute) {
        Action action = em.createQuery("select a from Action a where a.id = :id", Action.class)
                .setParameter("id", id)
                .getSingleResult();
        action.setName(name);
        action.setDescription(description);
        action.setStartHour(startHour);
        action.setStartMinute(startMinute);
        action.setEndHour(endHour);
        action.setEndMinute(endMinute);
    }

    //ScheduleId로 찾고, 시간에 따라 정렬
    @Override
    public List<Action> getActionsByScheduleId(Long scheduleId) {
        return em.createQuery("select a from Action a where a.schedule.id = :scheduleId order by a.startHour * 60 + a.startMinute", Action.class)
                .setParameter("scheduleId", scheduleId)
                .getResultList();
    }
}
