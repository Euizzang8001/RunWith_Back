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
        //이미 존재하는 그룹 이름인지 확인하기
        Optional<Group> existGroup = groupRepository.findByName(createGroupRequestDto.getName());
        if(existGroup.isPresent()){
            throw new IllegalAccessError("이미 존재하는 그룹 이름입니다.");
        }

        //저장하려는 러너가 존재하는지 확인
        Optional<Runner> runner = runnerRepository.findById(createGroupRequestDto.getRunnerId());

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

         //이 그룹에 자기가 속했고, 리더임을 나타내는 Belong객체 생성
         Belong belong = new Belong();
         belong.setRunner(runner.get());
         belong.setGroup(savedGroup);
         belong.setNickname(createGroupRequestDto.getNickname());
         belong.setLeader(true);
         belongRepository.save(belong);

         return new CreateGroupResponseDto(
                 savedGroup.getId(),
                 savedGroup.getName(),
                 savedGroup.getDescription(),
                 savedGroup.getImageLink(),
                 savedGroup.getCertificationCriteria()
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

        Optional<Group> group = groupRepository.findById(groupId);
        //group이 없으면 에러
        if(group.isEmpty()){
            throw new IllegalAccessError("존재하지 않는 그룹입니다.");
        }

        Optional<Runner> runner = runnerRepository.findById(runnerId);

        //runner가 없으면 에러
        if(runner.isEmpty()){
            throw new IllegalAccessError("존재하지 않는 러너입니다.");
        }
        List<Belong> belongs = belongRepository.findByGroupId(groupId);

        //그룹애 속해있는 인원이 1명이 아님.
        if(belongs.size() > 1){
            throw new IllegalAccessError("그룹에 속해있는 인원이 2명 이상입니다.");
        }

        //그룹의 리더가 삭재하는 것이 아님
        if(!belongs.get(0).isLeader() || belongs.get(0).getRunner() != runner.get()){
            throw new IllegalAccessError("리더만 삭제할 수 있습니다.");
        }

        belongRepository.deleteByRunnerIdAndGroupId(runnerId, groupId);
        groupRepository.delete(group.get());
    }

    //그룹 정보 수정
    @Override
    public ReviseGroupResponseDto reviseGroup(ReviseGroupRequestDto reviseGroupRequestDto) throws IllegalAccessError{
        Long groupId = reviseGroupRequestDto.getGroupId();
        Long runnerId = reviseGroupRequestDto.getRunnerId();

        Optional<Group> group = groupRepository.findById(groupId);
        //그룹이 없으면 에러
        if(group.isEmpty()){
            throw new IllegalAccessError("존재하지 않는 그룹입니다.");
        }

        //러너가 없으면 에러
        Optional<Runner> runner = runnerRepository.findById(runnerId);
        if(runner.isEmpty()){
            throw new IllegalAccessError("존재하지 않는 러너입니다.");
        }

        Optional<Belong> belong = belongRepository.findByRunnerIdAndGroupId(runnerId, groupId);

        //그룹에 속하지 않으면 에러
        if(belong.isEmpty()){
            throw new IllegalAccessError("해당 러너는 그룹에 속하지 않습니다.");
        }
        //그룹의 리더가 아니면 에러
        if(!belong.get().isLeader()){
            throw new IllegalAccessError("해당 러너는 이 그룹의 리더가 아닙니다.");
        }

        if(reviseGroupRequestDto.getCertificationCriteria() != 0){
            group.get().setCertificationCriteria(reviseGroupRequestDto.getCertificationCriteria());
        }
        if(reviseGroupRequestDto.getDescription() != null){
            group.get().setDescription(reviseGroupRequestDto.getDescription());
        }
        if(reviseGroupRequestDto.getImageLink() != null){
            group.get().setImageLink(reviseGroupRequestDto.getImageLink());
        }
        return new ReviseGroupResponseDto(
                group.get().getId(),
                group.get().getName(),
                group.get().getDescription(),
                group.get().getImageLink(),
                group.get().getCertificationCriteria()
        );
    }
}
