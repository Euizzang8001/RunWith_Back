package park.brothers.runwith_back.domain.Recognize.repository;

import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import park.brothers.runwith_back.domain.Recognize.entity.Recognize;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
@Primary
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class JPARecognizeRepository implements RecognizeRepository {

    private final EntityManager em;

    //recognize 객체 저장
    @Override
    @Transactional
    public Recognize save(Recognize recognize) {
        em.persist(recognize);
        return recognize;
    }

    //인정 여부 수정
    @Override
    @Transactional
    public Recognize revise(Recognize recognize, boolean recognizing) {
        recognize.setRecognizing(recognizing);
        return recognize;
    }

    //belong id와 스케줄 id로 recognize찾기
    @Override
    public Optional<Recognize> findByBelongIdAndScheduleId(UUID belongId, UUID scheduleId) {
        return em.createQuery("select r from Recognize r where r.recognizingBelong.id = :belongId and r.recognizedSchedule.id = :scheduleId", Recognize.class)
                .setParameter("belongId", belongId)
                .setParameter("scheduleId", scheduleId)
                .getResultStream()
                .findFirst();
    }

    //Recognize삭제
    @Override
    @Transactional
    public void delete(Recognize recognize) {
        em.remove(recognize);
    }

    //스케줄id로 Recognize찾기
    @Override
    public List<Recognize> findByScheduleId(UUID scheduleId) {
        return em.createQuery("select r from Recognize r where r.recognizedSchedule.id = :scheduleId", Recognize.class)
                .setParameter("scheduleId", scheduleId)
                .getResultList();
    }


    //러너가 특정 스케줄을 인정하고 있는지 확인
    @Override
    public Optional<Recognize> findByRunnerIdAndScheduleId(String runnerId, UUID scheduleId) {
        return em.createQuery("select r from Recognize r where r.recognizingBelong.runner.id = :runnerId and r.recognizedSchedule.id = :scheduleId", Recognize.class)
                .setParameter("runnerId", runnerId)
                .setParameter("scheduleId", scheduleId)
                .getResultStream()
                .findFirst();
    }

    //스케줄 id를 가진 recognize 전체 삭제
    @Override
    @Transactional
    public void deleteByScheduleId(UUID scheduleId) {
        em.createQuery("delete from Recognize r where r.recognizedSchedule.id = :scheduleId")
            .setParameter("scheduleId", scheduleId)
            .executeUpdate();
    }
}
