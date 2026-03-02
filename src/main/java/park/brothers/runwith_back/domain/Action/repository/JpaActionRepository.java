package park.brothers.runwith_back.domain.Action.repository;

import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import park.brothers.runwith_back.domain.Action.entity.Action;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
@RequiredArgsConstructor
@Slf4j
@Primary
@Transactional(readOnly = true)
public class JpaActionRepository implements ActionRepository{

    private final EntityManager em;

    //action저장
    @Override
    @Transactional
    public Action save(Action action) {
        em.persist(action);
        return action;
    }

    //action 삭제
    @Override
    @Transactional
    public void delete(Action action) {
        em.remove(action);
    }

    //id로 action 하나 얻기
    @Override
    public Optional<Action> findById(UUID id) {
        return Optional.ofNullable(em.createQuery("select a from Action a where a.id = :id", Action.class)
                .setParameter("id", id)
                .getSingleResult());
    }

    //같은 스케줄에서 중복된 actions들 모두 반환
    @Override
    public List<Action> findOverlappedActions(UUID scheduleId, int startHour, int startMinute, int endHour, int endMinute) {
        int carStartMinute = startHour * 60 + startMinute;
        int carEndMinute = endHour * 60 + endMinute;
        return em.createQuery("select a from Action a where a.schedule.id = :scheduleId and a.startHour * 60 + a.startMinute < :carEndMinute and a.endHour * 60 + a.endMinute > :carStartMinute", Action.class)
                .setParameter("carStartMinute", carStartMinute)
                .setParameter("carEndMinute", carEndMinute)
                .setParameter("scheduleId", scheduleId)
                .getResultList();
    }

    //Action 수정
    @Override
    @Transactional
    public Action reviseAction(Action action, String name, String description, int startHour, int startMinute, int endHour, int endMinute, int maxImageSize) {
        action.setName(name);
        action.setDescription(description);
        action.setStartHour(startHour);
        action.setStartMinute(startMinute);
        action.setEndHour(endHour);
        action.setEndMinute(endMinute);
        action.setMaxImageSize(maxImageSize);

        return action;
    }

    //ScheduleId로 찾고, 시간에 따라 정렬
    @Override
    public List<Action> findActionsByScheduleId(UUID scheduleId) {
        return em.createQuery("select a from Action a where a.schedule.id = :scheduleId order by a.startHour * 60 + a.startMinute", Action.class)
                .setParameter("scheduleId", scheduleId)
                .getResultList();
    }
}
