package park.brothers.runwith_back.domain.Runner.repository;

import jakarta.persistence.EntityManager;
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
public class JPARunnerRepository implements RunnerRepository {

    private final EntityManager em;

    public JPARunnerRepository(EntityManager em) {
        this.em = em;
    }

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
}
