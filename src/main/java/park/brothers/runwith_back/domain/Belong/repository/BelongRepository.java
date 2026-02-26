package park.brothers.runwith_back.domain.Belong.repository;

import jakarta.validation.constraints.NotNull;
import park.brothers.runwith_back.domain.Belong.entity.Belong;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface BelongRepository {
    Optional<Belong> findByRunnerIdAndGroupId(String runnerId, UUID groupId);

    Optional<Belong> findByGroupIdAndNickname(UUID groupId, String nickname);

    Belong save(Belong belong);

    void deleteByRunnerIdAndGroupId(String runnerId, UUID groupId);

    List<Belong> findByRunnerId(String runnerId);

    List<Belong> findByGroupId(UUID groupId);

    void changeIsLeader(String runnerId, UUID groupId, boolean isLeader);

    Optional<Belong> findById(@NotNull UUID id);
}
