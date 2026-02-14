package park.brothers.runwith_back.domain.Group.repository;


import park.brothers.runwith_back.domain.Group.entity.Group;
import java.util.List;
import java.util.Optional;

public interface GroupRepository {
    Group save(Group group);

    void delete(Group group);

    List<Group> findAll();

    Group findByName(String name);

    List<Group> findBySimilarName(String name);

    Group getById(Long id);

    Optional<Group> findById(Long groupId);
}
