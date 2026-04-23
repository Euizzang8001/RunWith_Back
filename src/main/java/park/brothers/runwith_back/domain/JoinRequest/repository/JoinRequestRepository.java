package park.brothers.runwith_back.domain.JoinRequest.repository;

import park.brothers.runwith_back.domain.JoinRequest.entity.JoinRequest;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface JoinRequestRepository {

    Optional<JoinRequest> findById(UUID joinRequestUUID);

    Optional<JoinRequest> findByRunnerIdAndGroupId(String runnerId, UUID groupId);

    JoinRequest save(JoinRequest newJoinRequest);

    void delete(JoinRequest joinRequest);

    List<JoinRequest> findByGroupId(UUID groupUUID);

    List<JoinRequest> findByRunnerId(String runnerId);
}
