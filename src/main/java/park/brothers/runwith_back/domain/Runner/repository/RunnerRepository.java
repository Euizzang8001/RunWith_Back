package park.brothers.runwith_back.domain.Runner.repository;

import jakarta.validation.constraints.NotEmpty;
import park.brothers.runwith_back.domain.Runner.entity.Runner;

import java.util.Optional;

public interface RunnerRepository {
    void save(Runner runner);

    Optional<Runner> findByEmail(String email);

    Optional<Runner> findById(Long runnerId);

    Runner getByEmail(String email);

    Optional<Runner> findByName(@NotEmpty String name);

    Boolean checkDuplication(@NotEmpty String name, @NotEmpty String email);
}
