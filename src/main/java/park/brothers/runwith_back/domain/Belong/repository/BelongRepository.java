package park.brothers.runwith_back.domain.Belong.repository;

import jakarta.validation.constraints.NotNull;
import park.brothers.runwith_back.domain.Belong.entity.Belong;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface BelongRepository {
    Optional<Belong> findByRunnerIdAndGroupId(UUID runnerId, UUID groupId);

    Optional<Object> findByGroupIdAndNickname(Long groupId, String nickname);

    Belong save(Belong belong);

    void deleteByRunnerIdAndGroupId(UUID runnerId, UUID groupId);

    List<Belong> findByRunnerId(UUID runnerId);

    List<Belong> findByGroupId(UUID groupId);

    void changeIsLeader(UUID runnerId, UUID groupId, boolean isLeader);

    Belong getById(@NotNull UUID id);
}
