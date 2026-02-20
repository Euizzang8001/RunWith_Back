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

    public String login(LoginRequestDto loginRequestDto) {
        Optional<Runner> runner = runnerRepository.findByEmail(loginRequestDto.getLoginEmail())
                .filter(r -> r.getPassword().equals(loginRequestDto.getLoginPassword()));

        //runner가 있으면 로그인 성공
        if(runner.isPresent()){
            return runner.get().getId().toString();
        }
        //없으면 실패
        else {
            return "";
        }
    }

}
