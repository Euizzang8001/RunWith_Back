package park.brothers.runwith_back.domain.Login.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import park.brothers.runwith_back.domain.Login.dto.Request.LoginRequestDto;
import park.brothers.runwith_back.domain.Runner.entity.Runner;
import park.brothers.runwith_back.domain.Runner.repository.RunnerRepository;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class Version1LoginService implements LoginService {

    private final RunnerRepository runnerRepository;

    public Long login(LoginRequestDto loginRequestDto) {
        Optional<Runner> runner = runnerRepository.findByEmail(loginRequestDto.getEmail())
                .filter(r -> r.getPassword().equals(loginRequestDto.getPassword()));

        return runner.map(Runner::getId).orElse(null);
    }

}
