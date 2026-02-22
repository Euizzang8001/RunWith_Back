package park.brothers.runwith_back.domain.Belong.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import park.brothers.runwith_back.common.Exceptions.DuplicateResourceException;
import park.brothers.runwith_back.common.Exceptions.NotAcceptableException;
import park.brothers.runwith_back.common.Exceptions.ResourceNotFoundException;
import park.brothers.runwith_back.common.Exceptions.UnauthorizedException;
import park.brothers.runwith_back.domain.Belong.dto.Request.ChangeLeaderRequestDto;
import park.brothers.runwith_back.domain.Belong.dto.Request.CreateBelongRequestDto;
import park.brothers.runwith_back.domain.Belong.dto.Request.DeleteBelongRequestDto;
import park.brothers.runwith_back.domain.Belong.dto.Response.ChangeLeaderResponseDto;
import park.brothers.runwith_back.domain.Belong.dto.Response.CreateBelongResponseDto;
import park.brothers.runwith_back.domain.Belong.entity.Belong;
import park.brothers.runwith_back.domain.Belong.repository.BelongRepository;
import park.brothers.runwith_back.domain.Group.dto.Response.GetGroupResponseDto;
import park.brothers.runwith_back.domain.Group.entity.Group;
import park.brothers.runwith_back.domain.Group.repository.GroupRepository;
import park.brothers.runwith_back.domain.Runner.dto.Response.GetRunnerResponseDto;
import park.brothers.runwith_back.domain.Runner.entity.Runner;
import park.brothers.runwith_back.domain.Runner.repository.RunnerRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class Version1BelongService implements BelongService{

    private final BelongRepository belongRepository;
    private final RunnerRepository runnerRepository;
    private final GroupRepository groupRepository;

    // 그룹 참여
    @Override
    public CreateBelongResponseDto joinGroup(CreateBelongRequestDto createBelongRequestDto) {
        String runnerId = createBelongRequestDto.getRunnerId();
        String groupId = createBelongRequestDto.getGroupId();
        String nickname = createBelongRequestDto.getBelongNickname();

        //이미 가입한 그룹이 아니면 가입
        if (belongRepository.findByRunnerIdAndGroupId(UUID.fromString(runnerId), UUID.fromString(groupId)).isPresent()) {
            throw new DuplicateResourceException("이미 그룹에 가입되어 있습니다.");
        }

        // 닉네임 중복 확인
        if (belongRepository.findByGroupIdAndNickname(UUID.fromString(groupId), nickname).isPresent()) {
            throw new DuplicateResourceException("이미 사용 중인 닉네임입니다.");
        }

        // 엔티티 조회
        Runner runner = runnerRepository.findById(UUID.fromString(runnerId))
                .orElseThrow(() -> new ResourceNotFoundException("존재하지 않는 러너입니다."));

        Group group = groupRepository.findById(UUID.fromString(groupId))
                .orElseThrow(() -> new ResourceNotFoundException("존재하지 않는 그룹입니다."));

        // 가입 처리
        Belong belong = new Belong();
        belong.setRunner(runner);
        belong.setGroup(group);
        belong.setLeader(false);
        belong.setNickname(nickname);

        Belong savedBelong = belongRepository.save(belong);

        return new CreateBelongResponseDto(
                savedBelong.getId().toString(),
                savedBelong.getRunner().getId().toString(),
                savedBelong.getGroup().getId().toString(),
                savedBelong.getNickname(),
                savedBelong.isLeader()
        );
    }
    
    //그룹 탈퇴 기능
    @Override
    public void leaveGroup(String belongId, DeleteBelongRequestDto deleteBelongRequestDto) {
        String groupId = deleteBelongRequestDto.getGroupId();
        String runnerId = deleteBelongRequestDto.getRunnerId();

        UUID groupUUID = UUID.fromString(groupId);
        UUID runnerUUID = UUID.fromString(runnerId);
        UUID belongUUID = UUID.fromString(belongId);

        //러너 id와 그룹id로 belong 찾기
        Optional<Belong> foundBelong = belongRepository.findByRunnerIdAndGroupId(UUID.fromString(runnerId), UUID.fromString(groupId));
        
        //러너가 그룹에 속하지 않을 때
        if(foundBelong.isEmpty()){
            throw new ResourceNotFoundException("해당 러너는 이 그룹에 속하지 않습니다.");
        }

        //찾은 빌롱이 삭제하려는 빌롱이 아닐 때
        if(foundBelong.get().getId() != belongUUID){
            throw new NotAcceptableException("삭제하려는 belong이 그룹과 러너에 일치하지 않습니다.");
        }
        belongRepository.deleteByRunnerIdAndGroupId(groupUUID, runnerUUID);
    }

    //특정 러너가 속한 모든 그룹 가져오기
    @Override
    public List<GetGroupResponseDto> getAllGroupsRunnerJoin(String runnerId) {
        List<Belong> belongs = belongRepository.findByRunnerId(UUID.fromString(runnerId));
        return belongs.stream()
                .map(belong -> new GetGroupResponseDto(belong.getGroup().getId().toString(), belong.getGroup().getName(), belong.getGroup().getDescription(), "test_image_link"))
                .collect(Collectors.toList());
    }

    //특정 그룹에 속한 모든 러너들 가져오기
    @Override
    public List<GetRunnerResponseDto> getAllRunnersInGroup(String groupId) {
        List<Belong> belongs = belongRepository.findByGroupId(UUID.fromString(groupId));
        return belongs.stream()
                .map(belong -> new GetRunnerResponseDto(belong.getRunner().getId().toString(), belong.getRunner().getName(), "test_image_link"))
                .collect(Collectors.toList());
    }

    //특정 그룹의 리더 변경하기
    @Override
    public ChangeLeaderResponseDto changeLeader(String oldLeaderRunnerId, ChangeLeaderRequestDto changeLeaderRequestDto) {
        String newLeaderRunnerId = changeLeaderRequestDto.getNewLeaderRunnerId();
        String groupId = changeLeaderRequestDto.getGroupId();

        //UUID 변환
        UUID oldLeaderRunnerUUID = UUID.fromString(oldLeaderRunnerId);
        UUID newLeaderRunnerUUID = UUID.fromString(newLeaderRunnerId);
        UUID groupUUID = UUID.fromString(groupId);

        //이전 리더가 리더인지 확인
        Optional<Belong> belong = belongRepository.findByRunnerIdAndGroupId(oldLeaderRunnerUUID, groupUUID);
        if(belong.isEmpty() || !belong.get().isLeader()){
            throw new UnauthorizedException("리더가 아닙니다.");
        }

        belongRepository.changeIsLeader(oldLeaderRunnerUUID, UUID.fromString(groupId), false);
        belongRepository.changeIsLeader(newLeaderRunnerUUID, UUID.fromString(groupId), true);

        return new ChangeLeaderResponseDto(
                oldLeaderRunnerId,
                newLeaderRunnerId,
                groupId
        );
    }
}
