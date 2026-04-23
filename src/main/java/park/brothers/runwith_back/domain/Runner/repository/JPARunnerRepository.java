package park.brothers.runwith_back.domain.Runner.repository;

import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import park.brothers.runwith_back.domain.Runner.entity.Runner;

import java.util.*;

@Repository
@Primary
@Transactional
@RequiredArgsConstructor
public class JPARunnerRepository implements RunnerRepository {

    private final EntityManager em;

    //러너 저장하기
    @Override
    @Transactional
    public Runner save(Runner runner) {
        em.persist(runner);
        return runner;
    }


    //Id로 러너 찾기
    @Override
    public Optional<Runner> findById(String id) {
        return em.createQuery("select r from Runner r where r.id = :id", Runner.class)
                .setParameter("id", id)
                .getResultStream()
                .findFirst();
    }

    //이름으로 러너 찾기
    @Override
    public Optional<Runner> findByName(String name) {
        return em.createQuery("select r from Runner r where r.name = :name", Runner.class)
                .setParameter("name", name)
                .getResultStream()
                .findFirst();
    }

    //러너 정보 수정
    @Override
    @Transactional
    public void reviseRunner(Runner runner, String runnerName) {
        runner.setName(runnerName);
        em.merge(runner);
    }

}
