package park.brothers.runwith_back.domain.Runner.service;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthException;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import park.brothers.runwith_back.common.Exceptions.NotAcceptableException;
import park.brothers.runwith_back.common.Exceptions.ResourceNotFoundException;
import park.brothers.runwith_back.domain.Action.repository.ActionRepository;
import park.brothers.runwith_back.domain.Belong.entity.Belong;
import park.brothers.runwith_back.domain.Belong.repository.BelongRepository;
import park.brothers.runwith_back.domain.Group.entity.Group;
import park.brothers.runwith_back.domain.Group.repository.GroupRepository;
import park.brothers.runwith_back.domain.Runner.dto.Request.CreateRunnerRequestDto;
import park.brothers.runwith_back.domain.Runner.dto.Request.ReviseMyInfoRequestDto;
import park.brothers.runwith_back.domain.Runner.dto.Response.CreateRunnerResponseDto;
import park.brothers.runwith_back.domain.Runner.dto.Response.GetMyInfoResponseDto;
import park.brothers.runwith_back.domain.Runner.entity.Runner;
import park.brothers.runwith_back.domain.Runner.repository.RunnerRepository;
import park.brothers.runwith_back.domain.Schedule.repository.ScheduleRepository;
import park.brothers.runwith_back.external.AWS_S3.AWSS3Service;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class Version1RunnerService implements RunnerService {

    private final RunnerRepository runnerRepository;
    private final GroupRepository groupRepository;
    private final BelongRepository belongRepository;
    private final ScheduleRepository scheduleRepository;
    private final ActionRepository actionRepository;

    private final AWSS3Service awss3Service;

    @Override
    @Transactional
    public CreateRunnerResponseDto save(String runnerId, CreateRunnerRequestDto createRunnerRequestDto, MultipartFile image) throws IllegalAccessError, IOException {
        //러너 생성
        Runner runner = new Runner();
        //중복 확인
        runner.setId(runnerId);
        runner.setName(createRunnerRequestDto.getRunnerName());


        Runner savedRunner = runnerRepository.save(runner);

        //러너가 리더인 그룹 하나 생성
        Group group = new Group();
        group.setIsSelf(true);
        group.setName(runner.getName() + "만의 트랙");
        group.setDescription("러너님만의 트랙입니다. 마음껏 계획을 세우고 달려가세요!");
        group.setCertificationCriteria(0);
        Group savedGroup = groupRepository.save(group);

        //러너가 이 그룹의 리더이자 속한다는 것을 나타낸 belong 객체 저장
        Belong belong = new Belong();
        belong.setLeader(true);
        belong.setGroup(savedGroup);
        belong.setNickname(runner.getName());
        belong.setRunner(savedRunner);
        belongRepository.save(belong);

        //이미지가 존재하면 저장하고 presignedurl받기 / 없으면 null return
        String presignedImageUrl = (image != null && !image.isEmpty())
                ? awss3Service.putImageToAWSS3(image, "runners", runnerId, 0)
                : null;

        //리턴해줄 값
        return new CreateRunnerResponseDto(
                savedRunner.getName(),
                presignedImageUrl
        );
    }

    //이미 저장된 러너인지 확인
    @Override
    public Boolean isSavedRunner(String runnerId){
        return runnerRepository.findById(runnerId).isPresent();
    }

    //로그인한 러너 정보 조회
    @Override
    public GetMyInfoResponseDto findRunner(String runnerId) {
        Optional<Runner> runner = runnerRepository.findById(runnerId);
        if(runner.isEmpty()){
            throw new ResourceNotFoundException("존재하지 않는 러너입니다.");
        }

        return new GetMyInfoResponseDto(
                runner.get().getName(),
                awss3Service.getImagePresignedUrl("runners", runnerId, 0)
        );
    }

    //러너 수정 서비스
    @Override
    @Transactional
    public GetMyInfoResponseDto revise(String runnerId, ReviseMyInfoRequestDto reviseMyInfoRequestDto, MultipartFile image) throws IOException {
        Optional<Runner> runner = runnerRepository.findById(runnerId);

        if(runner.isEmpty()){
            throw new ResourceNotFoundException("존재하지 않는 러너입니다.");
        }

        //이름이 변경될 예정이면 이름 + 셀프 그룹 이름 수정
        String runnerName;
        if(reviseMyInfoRequestDto != null && !reviseMyInfoRequestDto.getRunnerName().isEmpty()){
            //셀프 그룹 찾기
            Optional<Group> group = groupRepository.findSelfGroupByRunnerId(runnerId);
            if(group.isEmpty()){
                throw new ResourceNotFoundException("셀프 그룹이 존재하지 않는 러너입니다.");
            }

            runnerRepository.reviseRunner(runner.get(), reviseMyInfoRequestDto.getRunnerName());
            runnerName = reviseMyInfoRequestDto.getRunnerName();

            //셀프 그룹 이름 수정
            groupRepository.reviseSelfGroupName(group.get(), runnerName);
        } else {
            runnerName = runner.get().getName();
        }

        //이미지가 비어있지 않다면 이미지 수정
        String presignedImageUrl = (image != null && !image.isEmpty())
                ? awss3Service.putImageToAWSS3(image, "runners", runnerId, 0)
                : awss3Service.getImagePresignedUrl("runners", runnerId, 0);


        return new GetMyInfoResponseDto(
                runnerName,
                presignedImageUrl
        );

    }


    //러너 삭제(탈퇴) 서비스
    @Override
    @Transactional
    public void deleteRunner(String runnerId) throws FirebaseAuthException {
        //데이터베이스 내 삭제
        //특정 그룹의 리더이고, 그 그룹에 자신을 제외한 러너가 속해있으면 안된다.
        if (belongRepository.existGroupWithOtherMembersWhereRunnerIsLeader(runnerId)) {
            throw new NotAcceptableException("러너가 리더로 존재하는 그룹에 다른 러너가 속해있습니다.");
        }

        //리더로 속한 그룹 모두 삭제
        List<Belong> belongs = belongRepository.findGroupsWhereRunnerIsLeader(runnerId);
        for(Belong belong : belongs){
            groupRepository.delete(belong.getGroup());
        }
        runnerRepository.delete(runnerId);

        //파이어베이스 내 계정 삭제
        FirebaseAuth.getInstance().deleteUser(runnerId);

    }


}
