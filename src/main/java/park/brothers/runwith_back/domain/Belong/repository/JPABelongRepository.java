package park.brothers.runwith_back.domain.Belong.repository;

import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import park.brothers.runwith_back.domain.Belong.entity.Belong;

import java.util.List;
import java.util.Optional;

@Repository
@Slf4j
@Primary
@Transactional
@RequiredArgsConstructor
public class JPABelongRepository implements BelongRepository{

    private final EntityManager em;

    //러너 id와 group id로 belong객체 찾기
    @Override
    public Optional<Belong> findByRunnerIdAndGroupId(Long runnerId, Long groupId) {
         return Optional.ofNullable(em.createQuery("select b from Belong b where b.runner.id = :runnerId and b.group.id = :groupId", Belong.class)
                 .setParameter("runnerId", runnerId)
                 .setParameter("groupId", groupId)
                 .getSingleResult());
    }

    //그룹id와 닉네임으로 객체 찾기
    @Override
    public Optional<Object> findByGroupIdAndNickname(Long groupId, String nickname) {
        return Optional.ofNullable(
                em.createQuery("select b from Belong b where b.group.id = :groupId and b.nickname = :nickname")
                        .setParameter("groupId", groupId)
                        .setParameter("nickname", nickname)
                        .getSingleResultOrNull()
        );
    }

    @Override
    public Belong save(Belong belong) {
        em.persist(belong);
        return belong;
    }

    @Override
    public void deleteByRunnerIdAndGroupId(Long runnerId, Long groupId) {
        Optional<Belong> belong = Optional.ofNullable(em.createQuery("select b from Belong b where b.runner.id = :runnerId and b.group.id = :groupId", Belong.class)
                .setParameter("runnerId", runnerId)
                .setParameter("groupId", groupId)
                .getSingleResult());
        if(belong.isPresent()){
            em.remove(belong);
        }
    }

    @Override
    public List<Belong> findByRunnerId(Long runnerId) {
        return em.createQuery("select b from Belong b where b.runner.id = :runnerId", Belong.class)
                        .setParameter("runnerId", runnerId)
                        .getResultList();
    }

    @Override
    public List<Belong> findByGroupId(Long groupId) {
        return em.createQuery("select b from Belong b where b.group.id = :groupId", Belong.class)
                .setParameter("groupId", groupId)
                .getResultList();
    }

    @Override
    public void changeIsLeader(Long runnerId, Long groupId, boolean isLeader) {
        Belong belong = em.createQuery("select b from Belong b where b.runner.id = :runnerId and b.group.id = :groupId", Belong.class)
                .setParameter("runnerId", runnerId)
                .setParameter("groupId", groupId)
                .getSingleResult();

        belong.setLeader(isLeader);
    }

    @Override
    public Belong getById(Long id) {
        return em.createQuery("select b from Belong b where b.id = :id", Belong.class)
                .setParameter("id", id)
                .getSingleResult();
    }
}
