package park.brothers.runwith_back.domain.Belong.repository;

import park.brothers.runwith_back.domain.Belong.entity.Belong;

import java.util.List;
import java.util.Optional;

public interface BelongRepository {
    Optional<Belong> findByRunnerIdAndGroupId(Long runnerId, Long groupId);

    Optional<Object> findByGroupIdAndNickname(Long groupId, String nickname);

    void save(Belong belong);

    void deleteByRunnerIdAndGroupId(Long runnerId, Long groupId);

    List<Belong> findByRunnerId(Long runnerId);

    List<Belong> findByGroupId(Long groupId);

    void changeIsLeader(Long runnerId, Long groupId, boolean isLeader);
}
