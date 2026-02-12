package park.brothers.runwith_back.domain.Belong.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import park.brothers.runwith_back.domain.Belong.dto.Request.ChangeLeaderRequestDto;
import park.brothers.runwith_back.domain.Belong.dto.Request.JoinGroupRequestDto;
import park.brothers.runwith_back.domain.Belong.dto.Request.LeaveGroupRequestDto;
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
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class Version1BelongService implements BelongService{

    private final BelongRepository belongRepository;
    private final RunnerRepository runnerRepository;
    private final GroupRepository groupRepository;

    // 그룹 참여
    @Override
    public void joinGroup(JoinGroupRequestDto joinGroupRequestDto) {
        Long runnerId = joinGroupRequestDto.getRunnerId();
        Long groupId = joinGroupRequestDto.getGroupId();
        String nickname = joinGroupRequestDto.getNickname();

        //이미 가입한 그룹이 아니면 가입
        if (belongRepository.findByRunnerIdAndGroupId(runnerId, groupId).isPresent()) {
            throw new IllegalStateException("이미 그룹에 가입되어 있습니다.");
        }

        // 닉네임 중복 확인
        if (belongRepository.findByGroupIdAndNickname(groupId, nickname).isPresent()) {
            throw new IllegalStateException("이미 사용 중인 닉네임입니다.");
        }

        // 엔티티 조회
        Runner runner = runnerRepository.findById(runnerId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 러너입니다."));

        Group group = groupRepository.findById(groupId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 그룹입니다."));

        // 가입 처리
        Belong belong = new Belong();
        belong.setRunner(runner);
        belong.setGroup(group);
        belong.setLeader(false);
        belong.setNickname(nickname);

        belongRepository.save(belong);
    }
    
    //그룹 탈퇴 기능
    @Override
    public void leaveGroup(LeaveGroupRequestDto leaveGroupRequestDto) {
        Long groupId = leaveGroupRequestDto.getGroupId();
        Long runnerId = leaveGroupRequestDto.getRunnerId();
        
        //그룹에 속해있으면 탈퇴 가능
        if(belongRepository.findByRunnerIdAndGroupId(runnerId, groupId).isPresent()){
            belongRepository.deleteByRunnerIdAndGroupId(runnerId, groupId);
        }
    }

    //특정 러너가 속한 모든 그룹 가져오기
    @Override
    public List<GetGroupResponseDto> getAllGroupsRunnerJoin(Long runnerId) {
        List<Belong> belongs = belongRepository.findByRunnerId(runnerId);
        return belongs.stream()
                .map(belong -> new GetGroupResponseDto(belong.getGroup().getId(), belong.getGroup().getName(), belong.getGroup().getDescription(), belong.getGroup().getImageLink()))
                .collect(Collectors.toList());
    }

    //특정 그룹에 속한 모든 러너들 가져오기
    @Override
    public List<GetRunnerResponseDto> getAllRunnersInGroup(Long groupId) {
        List<Belong> belongs = belongRepository.findByGroupId(groupId);
        return belongs.stream()
                .map(belong -> new GetRunnerResponseDto(belong.getRunner().getId(), belong.getRunner().getName(), belong.getRunner().getImageLink()))
                .collect(Collectors.toList());
    }

    //특정 그룹의 리더 변경하기
    @Override
    public void changeLeader(ChangeLeaderRequestDto changeLeaderRequestDto) {
        Long beforeLeaderId = changeLeaderRequestDto.getBeforeLeaderId();
        Long afterLeaderId = changeLeaderRequestDto.getAfterLeaderId();
        Long groupId = changeLeaderRequestDto.getGroupId();

        //이전 리더가 리더인지 확인
        Optional<Belong> belong = belongRepository.findByRunnerIdAndGroupId(beforeLeaderId, groupId);
        if(belong.isPresent() && !belong.get().isLeader()){
            throw new IllegalStateException("리더가 아닙니다.");
        }

        belongRepository.changeIsLeader(beforeLeaderId, groupId, false);
        belongRepository.changeIsLeader(afterLeaderId, groupId, true);
    }
}
