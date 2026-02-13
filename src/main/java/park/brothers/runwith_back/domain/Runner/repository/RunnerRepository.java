package park.brothers.runwith_back.domain.Runner.repository;

import jakarta.validation.constraints.NotEmpty;
import park.brothers.runwith_back.domain.Runner.entity.Runner;

import java.util.Optional;

public interface RunnerRepository {
    Runner save(Runner runner);

    Optional<Runner> findByEmail(String email);

    Optional<Runner> findById(Long runnerId);

    Optional<Runner> findByName(@NotEmpty String name);

    Boolean checkDuplication(@NotEmpty String name, @NotEmpty String email);
}
