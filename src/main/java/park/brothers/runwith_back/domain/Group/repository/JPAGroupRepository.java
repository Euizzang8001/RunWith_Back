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

@Repository
@Slf4j
@Primary
@Transactional
@RequiredArgsConstructor
public class JPAGroupRepository implements GroupRepository {

    private final EntityManager em;

    @Override
    public Group save(Group group) {
        em.persist(group);
        return group;
    }

    @Override
    public void delete(Group group) {
        em.remove(group);
    }

    @Override
    public List<Group> findAll() {
        return em.createQuery("select g from Group g where g.isSelf = false", Group.class)
                .getResultList();
    }

    @Override
    public Optional<Group> findByName(String name) {
        List<Group> groups = em.createQuery("select g from Group g where g.name = :name", Group.class)
                .setParameter("name", name)
                .getResultList();
        return groups.isEmpty() ? Optional.empty() : Optional.of(groups.get(0));
    }

    public List<Group> findBySimilarName(String name) {
        return em.createQuery("select g from Group g where g.name like :name", Group.class)
                .setParameter("name", "%" + name + "%") //jpa query에서 파라미터를 커스텀하는 방식
                .getResultList();
    }

    @Override
    public Optional<Group> findById(Long id) {
        return Optional.ofNullable(em.createQuery("select g from Group g where g.id = :id", Group.class)
                .setParameter("id", id)
                .getSingleResult());
    }
}
