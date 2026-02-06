package park.brothers.runwith_back.domain.Group.repository;


import park.brothers.runwith_back.domain.Group.entity.Group;
import java.util.List;

public interface GroupRepository {
    void save(Group group);

    void delete(Group group);

    List<Group> findAll();

    Group findByName(String name);

    List<Group> findBySimilarName(String name);

    Group getById(Long id);
}
