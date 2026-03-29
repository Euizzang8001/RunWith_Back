package park.brothers.runwith_back.domain.Group.repository;


import park.brothers.runwith_back.domain.Group.entity.Group;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface GroupRepository {
    Group save(Group group);

    void delete(Group group);

    List<Group> findAll(int offset, int limit);

    Optional<Group> findByName(String name);

    List<Group> findBySimilarName(String name, int offset, int limit);

    Optional<Group> findById(UUID groupId);

    Optional<Group> findSelfGroupByRunnerId(String runnerId);

    Group revise(Group group, String groupDescription, int groupCertificationCriteria);
}
