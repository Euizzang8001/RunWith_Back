package park.brothers.runwith_back.domain.Runner.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import park.brothers.runwith_back.domain.Runner.dto.CreateRunnerDto;
import park.brothers.runwith_back.domain.Runner.entity.Runner;
import park.brothers.runwith_back.domain.Runner.repository.RunnerRepository;

@Service
@RequiredArgsConstructor
public class Version1RunnerService implements RunnerService {

    private final RunnerRepository runnerRepository;

    @Override
    public void save(CreateRunnerDto createRunnerDto) {
        Runner runner = new Runner();
        runner.setName(createRunnerDto.getName());
        runner.setPassword(createRunnerDto.getPassword());
        runner.setEmail(createRunnerDto.getEmail());

        if(createRunnerDto.getImageLink() != null){
            runner.setImageLink(createRunnerDto.getImageLink());
        }

        runnerRepository.save(runner);
    }
}
