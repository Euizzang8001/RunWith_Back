package park.brothers.runwith_back.domain.Runner.repository;

import jakarta.validation.constraints.NotEmpty;
import park.brothers.runwith_back.domain.Runner.entity.Runner;

import java.util.Optional;

public interface RunnerRepository {
    Runner save(Runner runner);

    Optional<Runner> findById(String id);

    Optional<Runner> findByName(@NotEmpty String name);

    void reviseRunner(Runner runner, @NotEmpty String runnerName);

    void delete(String runnerId);
}
