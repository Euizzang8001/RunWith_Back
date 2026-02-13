package park.brothers.runwith_back.domain.Runner.repository;

import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import park.brothers.runwith_back.domain.Runner.entity.Runner;

import java.util.*;

@Repository
@Slf4j
@Primary
@Transactional
@RequiredArgsConstructor
public class JPARunnerRepository implements RunnerRepository {

    private final EntityManager em;

    @Override
    @Transactional
    public void save(Runner runner) {
        em.persist(runner);
    }

    @Override
    public Optional<Runner> findByEmail(String email) {
        List<Runner> result = em.createQuery("select r from Runner r where r.email = :email", Runner.class)
                .setParameter("email", email)
                .getResultList();

        return result.stream().findFirst();
    }

    @Override
    public Optional<Runner> findById(Long runnerId) {
        return Optional.ofNullable(em.createQuery("select r from Runner r where r.id = :id", Runner.class)
                .setParameter("id", runnerId)
                .getSingleResult());
    }

    @Override
    public Runner getByEmail(String email) {
        return em.createQuery("select r from Runner r where r.email = :email", Runner.class)
                .setParameter("email", email)
                .getSingleResult();
    }

    @Override
    public Optional<Runner> findByName(String name) {
        return Optional.ofNullable(em.createQuery("select r from Runner r where r.name = :name", Runner.class)
                .setParameter("name", name)
                .getSingleResult());
    }

    //DB에서 동일한 정보의 러너가 있는지 확인
    @Override
    public Boolean checkDuplication(String name, String email) {
         Optional<Runner> runner = Optional.ofNullable(em.createQuery("select r from Runner r where r.name = :name or r.email = :email", Runner.class)
                 .setParameter("name", name)
                 .setParameter("email", email)
                 .getSingleResult());
        return runner.isPresent();
    }
}
