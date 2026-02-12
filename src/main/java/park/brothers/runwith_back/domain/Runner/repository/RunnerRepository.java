package park.brothers.runwith_back.domain.Runner.repository;

import park.brothers.runwith_back.domain.Runner.entity.Runner;

import java.util.Optional;

public interface RunnerRepository {
    void save(Runner runner);

    Optional<Runner> findByEmail(String email);

    Optional<Runner> findById(Long runnerId);

    Runner getByEmail(String email);
}
