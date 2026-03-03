package park.brothers.runwith_back.domain.JoinRequest.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import park.brothers.runwith_back.common.Exceptions.NotAcceptableException;
import park.brothers.runwith_back.common.Exceptions.ResourceNotFoundException;
import park.brothers.runwith_back.common.Exceptions.UnauthorizedException;
import park.brothers.runwith_back.domain.Belong.entity.Belong;
import park.brothers.runwith_back.domain.Belong.repository.BelongRepository;
import park.brothers.runwith_back.domain.Group.entity.Group;
import park.brothers.runwith_back.domain.Group.repository.GroupRepository;
import park.brothers.runwith_back.domain.JoinRequest.dto.request.CreateJoinRequestRequestDto;
import park.brothers.runwith_back.domain.JoinRequest.dto.response.CreateJoinRequestResponseDto;
import park.brothers.runwith_back.domain.JoinRequest.dto.response.GetJoinRequestResponseDto;
import park.brothers.runwith_back.domain.JoinRequest.dto.response.GetMyJoinRequestsResponseDto;
import park.brothers.runwith_back.domain.JoinRequest.entity.JoinRequest;
import park.brothers.runwith_back.domain.JoinRequest.repository.JoinRequestRepository;
import park.brothers.runwith_back.domain.Runner.entity.Runner;
import park.brothers.runwith_back.domain.Runner.repository.RunnerRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class Version1JoinRequestService implements JoinRequestService {

    private final BelongRepository belongRepository;
    private final JoinRequestRepository joinRequestRepository;
    private final RunnerRepository runnerRepository;
    private final GroupRepository groupRepository;

    //그룹 가입 신청 생성하기
    @Override
    public CreateJoinRequestResponseDto save(String runnerId, CreateJoinRequestRequestDto createJoinRequestRequestDto) {
        //러너가 존재하지 않으면 안됨
        Optional<Runner> runner = runnerRepository.findById(runnerId);
        if(runner.isEmpty()){
            throw new ResourceNotFoundException("존재하지 않는 러너입니다.");
        }

        //그룹이 존재하지 않으면 안됨
        String groupStrId = createJoinRequestRequestDto.getGroupId();
        UUID groupId = UUID.fromString(groupStrId);
        Optional<Group> group = groupRepository.findById(groupId);
        if(group.isEmpty()){
            throw new ResourceNotFoundException("존재하지 않는 그룹입니다.");
        }

        //이미 가입되어 있으면 안됨
        Optional<Belong> belong = belongRepository.findByRunnerIdAndGroupId(runnerId, groupId);
        if(belong.isPresent()){
            throw new NotAcceptableException("이미 가입된 그룹입니다.");
        }

        //이미 신청한 그룹이면 더이상 신청 안됨
        Optional<JoinRequest> joinRequest = joinRequestRepository.findByRunnerIdAndGroupId(runnerId, groupId);
        if(joinRequest.isPresent()){
            throw new NotAcceptableException("이미 신청한 그룹입니다.");
        }

        //신청하기
        JoinRequest newJoinRequest = new JoinRequest();
        newJoinRequest.setRunner(runner.get());
        newJoinRequest.setGroup(group.get());

        JoinRequest savedJoinRequest = joinRequestRepository.save(newJoinRequest);

        return new CreateJoinRequestResponseDto(
                savedJoinRequest.getId().toString(),
                savedJoinRequest.getGroup().getId().toString(),
                savedJoinRequest.getGroup().getName(),
                savedJoinRequest.getCreatedAt()
        );
    }

    //그룹 가입 신청 철회
    @Override
    public void delete(String runnerId, String joinRequestId) {
        UUID joinRequestUUID = UUID.fromString(joinRequestId);
        Optional<JoinRequest> joinRequest = joinRequestRepository.findById(joinRequestUUID);

        //신청이 존재하지 않으면 오류
        if(joinRequest.isEmpty()){
            throw new ResourceNotFoundException("존재하지 않는 그룹 가입 신청입니다.");
        }

        //러너가 존재하지 않으면 오류
        Optional<Runner> runner = runnerRepository.findById(runnerId);
        if(runner.isEmpty()){
            throw new ResourceNotFoundException("존재하지 않는 러너입니다.");
        }

        //해당 러너가 이 가입 신청의 주인이 아니면 오류
        if(!joinRequest.get().getRunner().getId().equals(runnerId)){
            throw new UnauthorizedException("신청한 러너만이 가입 신청을 철회할 수 있습니다.");
        }

        joinRequestRepository.delete(joinRequest.get());
    }

    //그룹 가입 신청 승인
    @Override
    public void accept(String runnerId, String joinRequestId) {
        UUID joinRequestUUID = UUID.fromString(joinRequestId);
        Optional<JoinRequest> joinRequest = joinRequestRepository.findById(joinRequestUUID);

        //신청이 존재하지 않으면 오류
        if(joinRequest.isEmpty()){
            throw new ResourceNotFoundException("존재하지 않는 그룹 가입 신청입니다.");
        }

        //러너가 존재하지 않으면 오류
        Optional<Runner> runner = runnerRepository.findById(runnerId);
        if(runner.isEmpty()){
            throw new ResourceNotFoundException("존재하지 않는 러너입니다.");
        }

        //신청 관리 러너가 그룹에 속하는지 확인
        Optional<Belong> belong = belongRepository.findByRunnerIdAndGroupId(runnerId, joinRequest.get().getGroup().getId());
        if(belong.isEmpty()){
            throw new ResourceNotFoundException("이 러너는 그룹에 속하지 않습니다.");
        }

        //리더인지 확인
        if(!belong.get().isLeader()){
            throw new UnauthorizedException("리더만이 신청 관리를 할 수 있습니다.");
        }

        Belong newBelong = new Belong();
        newBelong.setRunner(joinRequest.get().getRunner());
        newBelong.setGroup(joinRequest.get().getGroup());
        newBelong.setLeader(false);
        newBelong.setNickname(UUID.randomUUID().toString());

        belongRepository.save(newBelong);
        joinRequestRepository.delete(joinRequest.get());
    }

    // 그룹 가입 신청 거부
    @Override
    public void reject(String runnerId, String joinRequestId) {
        UUID joinRequestUUID = UUID.fromString(joinRequestId);
        Optional<JoinRequest> joinRequest = joinRequestRepository.findById(joinRequestUUID);

        //신청이 존재하지 않으면 오류
        if(joinRequest.isEmpty()){
            throw new ResourceNotFoundException("존재하지 않는 그룹 가입 신청입니다.");
        }

        //러너가 존재하지 않으면 오류
        Optional<Runner> runner = runnerRepository.findById(runnerId);
        if(runner.isEmpty()){
            throw new ResourceNotFoundException("존재하지 않는 러너입니다.");
        }

        //신청 관리 러너가 그룹에 속하는지 확인
        Optional<Belong> belong = belongRepository.findByRunnerIdAndGroupId(runnerId, joinRequest.get().getGroup().getId());
        if(belong.isEmpty()){
            throw new ResourceNotFoundException("이 러너는 그룹에 속하지 않습니다.");
        }

        //리더인지 확인
        if(!belong.get().isLeader()){
            throw new UnauthorizedException("리더만이 신청 관리를 할 수 있습니다.");
        }

        joinRequestRepository.delete(joinRequest.get());
    }

    //리더가 그룹 신청 조회를 하는 기능
    @Override
    public List<GetJoinRequestResponseDto> getJoinRequestOfGroup(String runnerId, String groupId) {
        //러너 존재 확인
        Optional<Runner> runner = runnerRepository.findById(runnerId);
        if(runner.isEmpty()){
            throw new ResourceNotFoundException("존재하지 않는 러너입니다.");
        }

        //그룹 존재 확인
        UUID groupUUID = UUID.fromString(groupId);
        Optional<Group> group = groupRepository.findById(groupUUID);
        if(group.isEmpty()){
            throw new ResourceNotFoundException("존재하지 않는 그룹입니다.");
        }

        //신청 관리 러너가 그룹에 속하는지 확인
        Optional<Belong> belong = belongRepository.findByRunnerIdAndGroupId(runnerId, groupUUID);
        if(belong.isEmpty()){
            throw new ResourceNotFoundException("이 러너는 그룹에 속하지 않습니다.");
        }

        //리더인지 확인
        if(!belong.get().isLeader()){
            throw new UnauthorizedException("리더만이 신청 관리를 할 수 있습니다.");
        }

        List<JoinRequest> joinRequests = joinRequestRepository.findByGroupId(groupUUID);

        return joinRequests.stream()
                .map(joinRequest -> new GetJoinRequestResponseDto(joinRequest.getId().toString(), joinRequest.getRunner().getId(), joinRequest.getCreatedAt()))
                .collect(Collectors.toList());
    }


    //내가 신청한 신청 리스트 조회하기
    @Override
    public List<GetMyJoinRequestsResponseDto> getMyJoinRequest(String runnerId) {
        //러너 존재 확인
        Optional<Runner> runner = runnerRepository.findById(runnerId);
        if(runner.isEmpty()){
            throw new ResourceNotFoundException("존재하지 않는 러너입니다.");
        }

        List<JoinRequest> joinRequests = joinRequestRepository.findByRunnerId(runnerId);

        return joinRequests.stream()
                .map(joinRequest -> new GetMyJoinRequestsResponseDto(joinRequest.getId().toString(), joinRequest.getGroup().getId().toString(), joinRequest.getCreatedAt()))
                .collect(Collectors.toList());
    }
}
