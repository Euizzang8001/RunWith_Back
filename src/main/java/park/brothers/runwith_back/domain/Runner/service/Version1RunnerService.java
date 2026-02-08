package park.brothers.runwith_back.domain.Runner.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import park.brothers.runwith_back.domain.Runner.dto.Request.CreateRunnerRequestDto;
import park.brothers.runwith_back.domain.Runner.entity.Runner;
import park.brothers.runwith_back.domain.Runner.repository.RunnerRepository;

@Service
@RequiredArgsConstructor
public class Version1RunnerService implements RunnerService {

    private final RunnerRepository runnerRepository;

    @Override
    public void save(CreateRunnerRequestDto createRunnerRequestDto) {
        Runner runner = new Runner();
        runner.setName(createRunnerRequestDto.getName());
        runner.setPassword(createRunnerRequestDto.getPassword());
        runner.setEmail(createRunnerRequestDto.getEmail());

        if(createRunnerRequestDto.getImageLink() != null){
            runner.setImageLink(createRunnerRequestDto.getImageLink());
        }

        runnerRepository.save(runner);
    }
}
