package park.brothers.runwith_back.domain.Runner.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import park.brothers.runwith_back.domain.Belong.entity.Belong;
import park.brothers.runwith_back.domain.Belong.repository.BelongRepository;
import park.brothers.runwith_back.domain.Group.entity.Group;
import park.brothers.runwith_back.domain.Group.repository.GroupRepository;
import park.brothers.runwith_back.domain.Runner.dto.Request.CreateRunnerRequestDto;
import park.brothers.runwith_back.domain.Runner.dto.Response.CreateRunnerResponseDto;
import park.brothers.runwith_back.domain.Runner.entity.Runner;
import park.brothers.runwith_back.domain.Runner.repository.RunnerRepository;
import park.brothers.runwith_back.external.AWS_S3.AWSS3Service;

import java.io.IOException;

@Service
@RequiredArgsConstructor
public class Version1RunnerService implements RunnerService {

    private final RunnerRepository runnerRepository;
    private final GroupRepository groupRepository;
    private final BelongRepository belongRepository;

    private final AWSS3Service awss3Service;

    @Override
    public CreateRunnerResponseDto save(CreateRunnerRequestDto createRunnerRequestDto, MultipartFile image) throws IllegalAccessError, IOException {
        //러너 생성
        Runner runner = new Runner();
        if(runnerRepository.findByName(createRunnerRequestDto.getName()).isPresent()){
            throw new IllegalAccessError("이미 존재하는 이름입니다.");
        }
        runner.setName(createRunnerRequestDto.getName());
        runner.setPassword(createRunnerRequestDto.getPassword());
        runner.setEmail(createRunnerRequestDto.getEmail());

        Runner savedRunner = runnerRepository.save(runner);

        //러너가 리더인 그룹 하나 생성
        Group group = new Group();
        group.setIsSelf(true);
        group.setName(runner.getName() + "'s self group");
        group.setDescription(runner.getName() + "'s self group");
        group.setCertificationCriteria(0);
        Group savedGroup = groupRepository.save(group);

        //러너가 이 그룹의 리더이자 속한다는 것을 나타낸 belong 객체 저장
        Belong belong = new Belong();
        belong.setLeader(true);
        belong.setGroup(savedGroup);
        belong.setNickname(runner.getName());
        belong.setRunner(savedRunner);
        belongRepository.save(belong);

        //이미지 저장하고 presignedurl받기
        String presignedImageUrl = awss3Service.putImageToAWSS3(
                image,
                "runners",
                runner.getId(),
                0
        );

        //리턴해줄 값
        return new CreateRunnerResponseDto(
                savedRunner.getId(),
                savedRunner.getName(),
                savedRunner.getEmail(),
                presignedImageUrl
        );
    }

    @Override
    public Boolean checkDuplication(CreateRunnerRequestDto createRunnerRequestDto) {
        return runnerRepository.checkDuplication(createRunnerRequestDto.getName(), createRunnerRequestDto.getEmail());
    }
}
