package park.brothers.runwith_back.domain.Group.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import park.brothers.runwith_back.domain.Belong.entity.Belong;
import park.brothers.runwith_back.domain.Belong.repository.BelongRepository;
import park.brothers.runwith_back.domain.Group.dto.Request.CreateGroupRequestDto;
import park.brothers.runwith_back.domain.Group.dto.Response.GetGroupResponseDto;
import park.brothers.runwith_back.domain.Group.dto.Request.DeleteGroupRequestDto;
import park.brothers.runwith_back.domain.Group.entity.Group;
import park.brothers.runwith_back.domain.Group.repository.GroupRepository;
import park.brothers.runwith_back.domain.Runner.entity.Runner;
import park.brothers.runwith_back.domain.Runner.repository.RunnerRepository;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class Version1GroupService implements GroupService {

    private final GroupRepository groupRepository;
    private final RunnerRepository runnerRepository;
    private final BelongRepository belongRepository;

    @Override
    public void save(CreateGroupRequestDto createGroupRequestDto) {
        Optional<Runner> runner = runnerRepository.findById(createGroupRequestDto.getRunnerId());

         if(groupRepository.findByName(createGroupRequestDto.getName()) == null && runner.isPresent()){
             //그룹 객체 생성
             Group group = new Group();
             group.setName(createGroupRequestDto.getName());
             groupRepository.save(group);

             //그룹 가져오기
             Group saved_group = groupRepository.findByName(createGroupRequestDto.getName());

             //이 그룹에 자기가 속했고, 리더임을 나타내는 Belong객체 생성
             Belong belong = new Belong();
             belong.setRunner(runner.get());
             belong.setGroup(saved_group);
             belong.setNickname(createGroupRequestDto.getNickname());
             belong.setLeader(true);
             belongRepository.save(belong);
         }
    }

    @Override
    public List<GetGroupResponseDto> getAllGroups() {
        List<Group> groups = groupRepository.findAll();

        return groups.stream()
                .map(group -> new GetGroupResponseDto(group.getId(), group.getName()))
                .collect(Collectors.toList());
    }

    @Override
    public GetGroupResponseDto getGroupByName(String name) {
        Group group = groupRepository.findByName(name);
        return new GetGroupResponseDto(group.getId(), group.getName());

    }

    @Override
    public List<GetGroupResponseDto> getGroupsBySimilarName(String name) {
        List<Group> groups = groupRepository.findBySimilarName(name);
        return groups.stream()
                .map(group -> new GetGroupResponseDto(group.getId(), group.getName()))
                .collect(Collectors.toList());
    }

    @Override
    public void delete(DeleteGroupRequestDto deleteGroupRequestDto) {
        Long groupId = deleteGroupRequestDto.getGroupId();
        Long runnerId = deleteGroupRequestDto.getRunnerId();

        Group group = groupRepository.getById(groupId);
        Optional<Runner> runner = runnerRepository.findById(runnerId);
        if(runner.isPresent()){
            List<Belong> belongs = belongRepository.findByGroupId(groupId);

            if(belongs.size() == 1 && belongs.get(0).isLeader() && belongs.get(0).getRunner() == runner.get()) {
                belongRepository.deleteByRunnerIdAndGroupId(runnerId, groupId);
                groupRepository.delete(group);
            }
        }
    }
}
