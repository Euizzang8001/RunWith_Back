package park.brothers.runwith_back.domain.Belong.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import park.brothers.runwith_back.common.Exceptions.DuplicateResourceException;
import park.brothers.runwith_back.common.Exceptions.ResourceNotFoundException;
import park.brothers.runwith_back.common.Exceptions.UnauthorizedException;
import park.brothers.runwith_back.domain.Action.entity.Action;
import park.brothers.runwith_back.domain.Action.repository.ActionRepository;
import park.brothers.runwith_back.domain.Belong.dto.Request.ChangeLeaderRequestDto;
import park.brothers.runwith_back.domain.Belong.dto.Request.CreateBelongRequestDto;
import park.brothers.runwith_back.domain.Belong.dto.Response.CreateBelongResponseDto;
import park.brothers.runwith_back.domain.Belong.dto.Response.GetBelongOfGroupResponseDto;
import park.brothers.runwith_back.domain.Belong.dto.Response.GetBelongOfRunnerResponseDto;
import park.brothers.runwith_back.domain.Belong.entity.Belong;
import park.brothers.runwith_back.domain.Belong.repository.BelongRepository;
import park.brothers.runwith_back.domain.Group.entity.Group;
import park.brothers.runwith_back.domain.Group.repository.GroupRepository;
import park.brothers.runwith_back.domain.Runner.entity.Runner;
import park.brothers.runwith_back.domain.Runner.repository.RunnerRepository;
import park.brothers.runwith_back.domain.Schedule.entity.Schedule;
import park.brothers.runwith_back.domain.Schedule.repository.ScheduleRepository;
import park.brothers.runwith_back.external.AWS_S3.AWSS3Service;

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
    private final ScheduleRepository scheduleRepository;
    private final ActionRepository actionRepository;
    private final AWSS3Service aWSS3Service;

    // 그룹 참여
    @Override
    public CreateBelongResponseDto joinGroup(String runnerId, CreateBelongRequestDto createBelongRequestDto) {
        String groupId = createBelongRequestDto.getGroupId();
        String nickname = createBelongRequestDto.getBelongNickname();

        //이미 가입한 그룹이 아니면 가입
        if (belongRepository.findByRunnerIdAndGroupId(runnerId, UUID.fromString(groupId)).isPresent()) {
            throw new DuplicateResourceException("이미 그룹에 가입되어 있습니다.");
        }

        // 닉네임 중복 확인
        if (belongRepository.findByGroupIdAndNickname(UUID.fromString(groupId), nickname).isPresent()) {
            throw new DuplicateResourceException("이미 사용 중인 닉네임입니다.");
        }

        // 엔티티 조회
        Runner runner = runnerRepository.findById(runnerId)
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
                savedBelong.getGroup().getId().toString(),
                savedBelong.getNickname(),
                savedBelong.isLeader()
        );
    }
    
    //그룹 탈퇴 기능
    @Override
    public void leaveGroup(String runnerId, String groupId) {
        UUID groupUUID = UUID.fromString(groupId);


        //러너 id와 그룹id로 belong 찾기
        Optional<Belong> foundBelong = belongRepository.findByRunnerIdAndGroupId(runnerId, groupUUID);
        
        //러너가 그룹에 속하지 않을 때
        if(foundBelong.isEmpty()){
            throw new ResourceNotFoundException("해당 러너는 이 그룹에 속하지 않습니다.");
        }

        //그룹에서 생성된 Schedule 추출
        List<Schedule> schedules = scheduleRepository.findByBelongId(foundBelong.get().getId());

        //스케줄로부터 생성된 모든 Actions들 삭제
        for(Schedule schedule : schedules){
            List<Action> actions = actionRepository.findActionsByScheduleId(schedule.getId());
            for(Action action : actions){
                actionRepository.delete(action);
            }
            //actions들 삭제 다 하면 schedule를 삭제
            scheduleRepository.delete(schedule);
        }

        belongRepository.deleteByRunnerIdAndGroupId(runnerId, groupUUID);
    }

    //특정 러너가 속한 모든 그룹 가져오기
    @Override
    public List<GetBelongOfGroupResponseDto> getAllGroupsRunnerJoin(String runnerId) {
        List<Belong> belongs = belongRepository.findByRunnerId(runnerId);
        return belongs.stream()
                .map(belong -> new GetBelongOfGroupResponseDto(belong.getId().toString(), belong.getGroup().getId().toString(), belong.getGroup().getName(), belong.getGroup().getDescription(), belong.isLeader(), aWSS3Service.getImagePresignedUrl("groups", belong.getGroup().getId().toString(), 0)))
                .collect(Collectors.toList());
    }

    //특정 그룹에 속한 모든 러너들 가져오기
    @Override
    public List<GetBelongOfRunnerResponseDto> getAllRunnersInGroup(String groupId) {
        List<Belong> belongs = belongRepository.findByGroupId(UUID.fromString(groupId));
        return belongs.stream()
                .map(belong -> new GetBelongOfRunnerResponseDto(belong.getId().toString(), belong.getRunner().getId(), belong.getRunner().getName(), belong.isLeader(),aWSS3Service.getImagePresignedUrl("runners",belong.getRunner().getId(), 0 )))
                .collect(Collectors.toList());
    }

    //특정 그룹의 리더 변경하기
    @Override
    public void changeLeader(String oldLeaderRunnerId, ChangeLeaderRequestDto changeLeaderRequestDto) {
        String newLeaderRunnerId = changeLeaderRequestDto.getNewLeaderRunnerId();
        String groupId = changeLeaderRequestDto.getGroupId();

        //UUID 변환
        UUID groupUUID = UUID.fromString(groupId);

        //이전 리더가 리더인지 확인
        Optional<Belong> belong = belongRepository.findByRunnerIdAndGroupId(oldLeaderRunnerId, groupUUID);
        if (belong.isEmpty()) {
            throw new UnauthorizedException("그룹에 속해있지 않습니다.");
        }
        if (!belong.get().isLeader()) {
            throw new UnauthorizedException("리더가 아닙니다.");
        }

        belongRepository.changeIsLeader(oldLeaderRunnerId, UUID.fromString(groupId), false);
        belongRepository.changeIsLeader(newLeaderRunnerId, UUID.fromString(groupId), true);

    }
}
