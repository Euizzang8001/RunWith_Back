package park.brothers.runwith_back.domain.JoinRequest.repository;

import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import park.brothers.runwith_back.domain.JoinRequest.entity.JoinRequest;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
@Primary
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class JPAJoinRequestRepository implements JoinRequestRepository {

    private final EntityManager em;


    //id로 찾기
    @Override
    public Optional<JoinRequest> findById(UUID joinRequestUUID) {
        return em.createQuery("select j from JoinRequest j where j.id = :id", JoinRequest.class)
                .setParameter("id", joinRequestUUID)
                .getResultStream()
                .findFirst();
    }

    //runner와 group으로 찾기
    @Override
    public Optional<JoinRequest> findByRunnerIdAndGroupId(String runnerId, UUID groupId) {
        return em.createQuery("select j from JoinRequest j where j.runner.id = :runnerId and j.group.id = :groupId", JoinRequest.class)
                .setParameter("runnerId", runnerId)
                .setParameter("groupId", groupId)
                .getResultStream()
                .findFirst();
    }

    //저장하기
    @Override
    @Transactional
    public JoinRequest save(JoinRequest newJoinRequest) {
        em.persist(newJoinRequest);
        return newJoinRequest;
    }

    //삭제하기
    @Override
    @Transactional
    public void delete(JoinRequest joinRequest) {
        em.remove(joinRequest);
    }

    //그룹 id로 모두 찾기
    @Override
    public List<JoinRequest> findByGroupId(UUID groupUUID) {
        return em.createQuery("select j from JoinRequest j where j.group.id = :groupId", JoinRequest.class)
                .setParameter("groupId", groupUUID)
                .getResultList();
    }

    //러너 id로 모두 찾기
    @Override
    public List<JoinRequest> findByRunnerId(String runnerId) {
        return em.createQuery("select j from JoinRequest j where j.runner.id = :runnerId", JoinRequest.class)
                .setParameter("runnerId", runnerId)
                .getResultList();
    }
}
