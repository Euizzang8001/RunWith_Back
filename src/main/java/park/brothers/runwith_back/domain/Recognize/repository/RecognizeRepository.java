package park.brothers.runwith_back.domain.Recognize.repository;

import jakarta.validation.constraints.NotNull;
import park.brothers.runwith_back.domain.Recognize.entity.Recognize;

import java.util.Optional;
import java.util.UUID;

public interface RecognizeRepository {
    Recognize save(Recognize recognize);

    Recognize revise(Recognize recognize, @NotNull boolean recognizing);

    Optional<Recognize> findByBelongIdAndScheduleId(UUID belongId, UUID scheduleId);
}
