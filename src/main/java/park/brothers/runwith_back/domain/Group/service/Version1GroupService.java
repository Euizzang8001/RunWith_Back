package park.brothers.runwith_back.domain.Group.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import park.brothers.runwith_back.domain.Belong.entity.Belong;
import park.brothers.runwith_back.domain.Belong.repository.BelongRepository;
import park.brothers.runwith_back.domain.Group.dto.Request.CreateGroupRequestDto;
import park.brothers.runwith_back.domain.Group.dto.Request.ReviseGroupRequestDto;
import park.brothers.runwith_back.domain.Group.dto.Response.CreateGroupResponseDto;
import park.brothers.runwith_back.domain.Group.dto.Response.GetGroupResponseDto;
import park.brothers.runwith_back.domain.Group.dto.Request.DeleteGroupRequestDto;
import park.brothers.runwith_back.domain.Group.dto.Response.ReviseGroupResponseDto;
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
    public CreateGroupResponseDto save(CreateGroupRequestDto createGroupRequestDto) throws IllegalAccessError {
        Optional<Runner> runner = runnerRepository.findById(createGroupRequestDto.getRunnerId());

        //이미 존재하는 그룹 이름인지 확인하기
        if(groupRepository.findByName(createGroupRequestDto.getName()) != null){
            throw new IllegalAccessError("이미 존재하는 그룹 이름입니다.");
        }

        //저장하려는 러너가 존재하는지 확인
        if(runner.isEmpty()){
            throw new IllegalAccessError("존재하지 않은 러너입니다.");
        }

         //그룹 객체 생성
         Group group = new Group();
         group.setName(createGroupRequestDto.getName());
         group.setDescription(createGroupRequestDto.getDescription());
         group.setIsSelf(false);
         if(createGroupRequestDto.getImageLink() != null){
             group.setImageLink(createGroupRequestDto.getImageLink());
         }
         group.setCertificationCriteria(createGroupRequestDto.getCertificationCriteria());
         Group savedGroup = groupRepository.save(group);

         //그룹 가져오기
         Group saved_group = groupRepository.findByName(createGroupRequestDto.getName());

         //이 그룹에 자기가 속했고, 리더임을 나타내는 Belong객체 생성
         Belong belong = new Belong();
         belong.setRunner(runner.get());
         belong.setGroup(saved_group);
         belong.setNickname(createGroupRequestDto.getNickname());
         belong.setLeader(true);
         belongRepository.save(belong);

         return new CreateGroupResponseDto(
                 savedGroup.getId(),
                 savedGroup.getName(),
                 savedGroup.getDescription(),
                 saved_group.getImageLink()
         );

    }

    @Override
    public List<GetGroupResponseDto> getAllGroups() {
        List<Group> groups = groupRepository.findAll();

        return groups.stream()
                .map(group -> new GetGroupResponseDto(group.getId(), group.getName(), group.getDescription(), group.getImageLink()))
                .collect(Collectors.toList());
    }

    @Override
    public GetGroupResponseDto getGroupByName(String name) {
        Group group = groupRepository.findByName(name);
        return new GetGroupResponseDto(group.getId(), group.getName(), group.getDescription(), group.getImageLink());

    }

    @Override
    public List<GetGroupResponseDto> getGroupsBySimilarName(String name) {
        List<Group> groups = groupRepository.findBySimilarName(name);
        return groups.stream()
                .map(group -> new GetGroupResponseDto(group.getId(), group.getName(), group.getDescription(), group.getImageLink()))
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

    //그룹 정보 수정
    @Override
    public ReviseGroupResponseDto reviseGroup(ReviseGroupRequestDto reviseGroupRequestDto) throws IllegalAccessException{
        Long groupId = reviseGroupRequestDto.getId();
        Long runnerId = reviseGroupRequestDto.getRunnerId();

        Group group = groupRepository.getById(groupId);
        Optional<Belong> belong = belongRepository.findByRunnerIdAndGroupId(runnerId, groupId);

        if(belong.isEmpty()){
            throw new IllegalArgumentException("해당 러너는 그룹에 속하지 않습니다.");
        }
        if(!belong.get().isLeader()){
            throw new IllegalAccessException("해당 러너는 이 그룹의 리더가 아닙니다.");
        }

        if(reviseGroupRequestDto.getCertificationCriteria() != 0){
            group.setCertificationCriteria(reviseGroupRequestDto.getCertificationCriteria());
        }
        if(reviseGroupRequestDto.getDescription() != null){
            group.setDescription(reviseGroupRequestDto.getDescription());
        }
        if(reviseGroupRequestDto.getImageLink() != null){
            group.setImageLink(reviseGroupRequestDto.getImageLink());
        }
        return new ReviseGroupResponseDto(
                group.getId(),
                group.getName(),
                group.getDescription(),
                group.getImageLink(),
                group.getCertificationCriteria()
        );
    }
}
