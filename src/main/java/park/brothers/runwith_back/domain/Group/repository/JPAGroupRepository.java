package park.brothers.runwith_back.domain.Group.repository;

import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import park.brothers.runwith_back.domain.Group.entity.Group;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
@Primary
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class JPAGroupRepository implements GroupRepository {

    private final EntityManager em;

    @Override
    @Transactional
    public Group save(Group group) {
        em.persist(group);
        return group;
    }

    @Override
    @Transactional
    public void delete(Group group) {
        em.remove(group);
    }

    @Override
    public List<Group> findAll(int offset, int limit) {
        return em.createQuery("select g from Group g where g.isSelf = false", Group.class)
                .setFirstResult(offset)
                .setMaxResults(limit)
                .getResultList();
    }

    @Override
    public Optional<Group> findByName(String name) {
        return em.createQuery("select g from Group g where g.name = :name and g.isSelf = false", Group.class)
                .setParameter("name", name)
                .getResultStream()
                .findFirst();
    }

    public List<Group> findBySimilarName(String name, int offset, int limit) {
        return em.createQuery("select g from Group g where g.name like :name and g.isSelf = false", Group.class)
                .setParameter("name", "%" + name + "%")
                .setFirstResult(offset)
                .setMaxResults(limit)
                .getResultList();
    }

    @Override
    public Optional<Group> findById(UUID id) {
        return em.createQuery("select g from Group g where g.id = :id", Group.class)
                .setParameter("id", id)
                .getResultStream()
                .findFirst();
    }

    //특정 러너의 id로 셀프 그룹 찾기
    @Override
    public Optional<Group> findSelfGroupByRunnerId(String runnerId) {

        return em.createQuery(
                        "select g from Group g " +
                                "inner join Belong b on b.group = g " +
                                "where g.isSelf = true and b.isLeader = true and b.runner.id = :runnerId", Group.class)
                .setParameter("runnerId", runnerId)
                .setMaxResults(1)
                .getResultStream()
                .findFirst();
    }

    //그룹 정보 수정
    @Override
    @Transactional
    public Group revise(Group group, String groupDescription, int groupCertificationCriteria) {
        if(groupDescription != null){
            group.setDescription(groupDescription);
        }

        if(groupCertificationCriteria > 0){
            group.setCertificationCriteria(groupCertificationCriteria);
        }

        return group;
    }
}
