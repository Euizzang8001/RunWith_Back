package park.brothers.runwith_back.domain.Belong.repository;

import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import park.brothers.runwith_back.domain.Belong.entity.Belong;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
@Primary
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class JPABelongRepository implements BelongRepository{

    private final EntityManager em;

    //러너 id와 group id로 belong객체 찾기
    @Override
    public Optional<Belong> findByRunnerIdAndGroupId(String runnerId, UUID groupId) {
        return em.createQuery("select b from Belong b where b.runner.id = :runnerId and b.group.id = :groupId", Belong.class)
                .setParameter("runnerId", runnerId)
                .setParameter("groupId", groupId)
                .getResultStream()
                .findFirst();
    }

    //그룹id와 닉네임으로 객체 찾기
    @Override
    public Optional<Belong> findByGroupIdAndNickname(UUID groupId, String nickname) {
        return em.createQuery("select b from Belong b where b.group.id = :groupId and b.nickname = :nickname", Belong.class)
                .setParameter("groupId", groupId)
                .setParameter("nickname", nickname)
                .getResultStream()
                .findFirst();
    }

    //belong 저장
    @Override
    @Transactional
    public Belong save(Belong belong) {
        em.persist(belong);
        return belong;
    }

    @Override
    @Transactional
    public void deleteByRunnerIdAndGroupId(String runnerId, UUID groupId) {
        Optional<Belong> belong = Optional.ofNullable(em.createQuery("select b from Belong b where b.runner.id = :runnerId and b.group.id = :groupId", Belong.class)
                .setParameter("runnerId", runnerId)
                .setParameter("groupId", groupId)
                .getSingleResult());
        belong.ifPresent(em::remove);
    }

    @Override
    public List<Belong> findByRunnerId(String runnerId) {
        return em.createQuery("select b from Belong b where b.runner.id = :runnerId and b.group.isSelf = false", Belong.class)
                        .setParameter("runnerId", runnerId)
                        .getResultList();
    }

    @Override
    public List<Belong> findByGroupId(UUID groupId) {
        return em.createQuery("select b from Belong b where b.group.id = :groupId", Belong.class)
                .setParameter("groupId", groupId)
                .getResultList();
    }

    @Override
    @Transactional
    public void changeIsLeader(String runnerId, UUID groupId, boolean isLeader) {
        Belong belong = em.createQuery("select b from Belong b where b.runner.id = :runnerId and b.group.id = :groupId", Belong.class)
                .setParameter("runnerId", runnerId)
                .setParameter("groupId", groupId)
                .getSingleResult();

        belong.setLeader(isLeader);
    }

    @Override
    public Optional<Belong> findById(UUID id) {
        return em.createQuery("select b from Belong b where b.id = :id", Belong.class)
                .setParameter("id", id)
                .getResultStream()
                .findFirst();
    }
    
    //특정 러너가 리더로 속한 그룹에 다른 러너가 존재하는지 확인하기
    @Override
    public boolean existGroupWithOtherMembersWhereRunnerIsLeader(String runnerId) {
        String jpql = "select b1.id from Belong b1 " +
                "inner join Belong b2 on b1.group.id = b2.group.id " +
                "where b1.runner.id = :runnerId " +
                "and b1.isLeader = true " +
                "and b2.runner.id != :runnerId";

        List<UUID> result = em.createQuery(jpql, UUID.class)
                .setParameter("runnerId", runnerId)
                .setMaxResults(1)
                .getResultList();

        return !result.isEmpty();
    }
    
    //특정 러너가 리더인 모든 그룹 가져오기
    @Override
    public List<Belong> findGroupsWhereRunnerIsLeader(String runnerId) {
        return em.createQuery("select b from Belong b where b.runner.id = :runnerId and b.isLeader = true", Belong.class)
                .setParameter("runnerId", runnerId)
                .getResultList();
    }
}
