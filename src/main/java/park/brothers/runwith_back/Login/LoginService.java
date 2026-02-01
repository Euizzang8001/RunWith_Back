package park.brothers.runwith_back.Login;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import park.brothers.runwith_back.Runner.MemoryRunnerRepository;
import park.brothers.runwith_back.Runner.Runner;

@Service
@RequiredArgsConstructor
public class LoginService {

    private final MemoryRunnerRepository runnerRepository;

    public Runner login(String email, String password){
        return runnerRepository.findByEmail(email)
                .filter(r -> r.getPassword().equals(password))
                .orElse(null);
    }

}
