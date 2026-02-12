package park.brothers.runwith_back.domain.Runner.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import park.brothers.runwith_back.domain.Belong.entity.Belong;
import park.brothers.runwith_back.domain.Belong.repository.BelongRepository;
import park.brothers.runwith_back.domain.Group.entity.Group;
import park.brothers.runwith_back.domain.Group.repository.GroupRepository;
import park.brothers.runwith_back.domain.Runner.dto.Request.CreateRunnerRequestDto;
import park.brothers.runwith_back.domain.Runner.entity.Runner;
import park.brothers.runwith_back.domain.Runner.repository.RunnerRepository;


@Service
@RequiredArgsConstructor
public class Version1RunnerService implements RunnerService {

    private final RunnerRepository runnerRepository;
    private final GroupRepository groupRepository;
    private final BelongRepository belongRepository;

    @Override
    public void save(CreateRunnerRequestDto createRunnerRequestDto) {
        //러너 생성
        Runner runner = new Runner();
        runner.setName(createRunnerRequestDto.getName());
        runner.setPassword(createRunnerRequestDto.getPassword());
        runner.setEmail(createRunnerRequestDto.getEmail());

        if(createRunnerRequestDto.getImageLink() != null){
            runner.setImageLink(createRunnerRequestDto.getImageLink());
        }

        runnerRepository.save(runner);

        //러너가 리더인 그룹 하나 생성
        Group group = new Group();
        group.setIsSelf(true);
        group.setName(runner.getName() + "'s self group");
        group.setDescription(runner.getName() + "'s self group");
        group.setCertificationCriteria(0);
        groupRepository.save(group);

        //러너가 이 그룹의 리더이자 속한다는 것을 나타낸 belong 객체 저장
        Group selfGroup =  groupRepository.findByName(runner.getName() + "'s self group");
        Runner selfRunner = runnerRepository.getByEmail(runner.getEmail());

        Belong belong = new Belong();
        belong.setLeader(true);
        belong.setGroup(selfGroup);
        belong.setNickname(runner.getName());
        belong.setRunner(selfRunner);
        belongRepository.save(belong);
    }
}
