package park.brothers.runwith_back.domain.Group.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import park.brothers.runwith_back.common.Exceptions.ResourceNotFoundException;
import park.brothers.runwith_back.domain.Belong.entity.Belong;
import park.brothers.runwith_back.domain.Belong.repository.BelongRepository;
import park.brothers.runwith_back.domain.Group.dto.Request.CreateGroupRequestDto;
import park.brothers.runwith_back.domain.Group.dto.Request.ReviseGroupRequestDto;
import park.brothers.runwith_back.domain.Group.dto.Response.CreateGroupResponseDto;
import park.brothers.runwith_back.domain.Group.dto.Response.GetGroupResponseDto;
import park.brothers.runwith_back.domain.Group.dto.Response.ReviseGroupResponseDto;
import park.brothers.runwith_back.domain.Group.entity.Group;
import park.brothers.runwith_back.domain.Group.repository.GroupRepository;
import park.brothers.runwith_back.domain.Runner.entity.Runner;
import park.brothers.runwith_back.domain.Runner.repository.RunnerRepository;
import park.brothers.runwith_back.external.AWS_S3.AWSS3Service;

import java.io.IOException;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class Version1GroupService implements GroupService {

    private final GroupRepository groupRepository;
    private final RunnerRepository runnerRepository;
    private final BelongRepository belongRepository;
    private final AWSS3Service awss3Service;

    @Override
    public CreateGroupResponseDto save(String runnerId, CreateGroupRequestDto createGroupRequestDto, MultipartFile image) throws IllegalAccessError, IOException {
        //이미 존재하는 그룹 이름인지 확인하기
        Optional<Group> existGroup = groupRepository.findByName(createGroupRequestDto.getGroupName());
        if(existGroup.isPresent()){
            throw new IllegalAccessError("이미 존재하는 그룹 이름입니다.");
        }

        //저장하려는 러너가 존재하는지 확인
        Optional<Runner> runner = runnerRepository.findById(runnerId);

        if(runner.isEmpty()){
            throw new IllegalAccessError("존재하지 않은 러너입니다.");
        }

         //그룹 객체 생성
         Group group = new Group();
         group.setName(createGroupRequestDto.getGroupName());
         group.setDescription(createGroupRequestDto.getGroupDescription());
         group.setIsSelf(false);
         group.setCertificationCriteria(createGroupRequestDto.getGroupCertificationCriteria());
         Group savedGroup = groupRepository.save(group);

         //이 그룹에 자기가 속했고, 리더임을 나타내는 Belong객체 생성
         Belong belong = new Belong();
         belong.setRunner(runner.get());
         belong.setGroup(savedGroup);
         belong.setNickname(createGroupRequestDto.getGroupNickname());
         belong.setLeader(true);
         Belong savedBelong = belongRepository.save(belong);
        System.out.println("savedBelong = " + savedBelong);

         //이미지가 존재하면 저장하고 presignedurl 받기, 없으면 null return
         String presignedImageUrl = (image != null && !image.isEmpty())
                 ? awss3Service.putImageToAWSS3(image, "groups", savedGroup.getId().toString(), 0)
                 : null;

         return new CreateGroupResponseDto(
                 savedGroup.getId().toString(),
                 savedGroup.getName(),
                 savedGroup.getDescription(),
                 presignedImageUrl,
                 savedGroup.getCertificationCriteria()
         );

    }

    //모든 그룹 얻기
    @Override
    public List<GetGroupResponseDto> getAllGroups() {
        List<Group> groups = groupRepository.findAll(0, 100);

        return groups.stream()
                .map(group -> new GetGroupResponseDto(
                        group.getId().toString(),
                        group.getName(),
                        group.getDescription(),
                        awss3Service.getImagePresignedUrl("groups", group.getId().toString(), 0))
                )
                .collect(Collectors.toList());
    }

    //유사 이름을 가진 그룹 정보 얻기
    @Override
    public List<GetGroupResponseDto> getGroupsBySimilarName(String name, int offset, int limit) {
        List<Group> groups;
        if(name == null){
            groups = groupRepository.findAll(offset, limit);
        } else {
            groups = groupRepository.findBySimilarName(name, offset, limit);
        }
        return groups.stream()
                .map(group -> new GetGroupResponseDto(
                        group.getId().toString(),
                        group.getName(),
                        group.getDescription(),
                        awss3Service.getImagePresignedUrl("groups", group.getId().toString(), 0))
                )
                .collect(Collectors.toList());
    }


    @Override
    public void delete(String runnerId, String groupId) {
        UUID groupUUID = UUID.fromString(groupId);

        Optional<Group> group = groupRepository.findById(groupUUID);
        //group이 없으면 에러
        if(group.isEmpty()){
            throw new IllegalAccessError("존재하지 않는 그룹입니다.");
        }

        Optional<Runner> runner = runnerRepository.findById(runnerId);

        //runner가 없으면 에러
        if(runner.isEmpty()){
            throw new IllegalAccessError("존재하지 않는 러너입니다.");
        }
        List<Belong> belongs = belongRepository.findByGroupId(groupUUID);

        //그룹애 속해있는 인원이 1명이 아님.
        if(belongs.size() > 1){
            throw new IllegalAccessError("그룹에 속해있는 인원이 2명 이상입니다.");
        }

        //그룹의 리더가 삭재하는 것이 아님
        Belong firstBelong = belongs.getFirst();
        if(!firstBelong.isLeader() || !firstBelong.getRunner().getId().equals(runner.get().getId())){
            throw new IllegalAccessError("리더만 삭제할 수 있습니다.");
        }

        belongRepository.deleteByRunnerIdAndGroupId(runnerId, UUID.fromString(groupId));
        groupRepository.delete(group.get());
    }

    //그룹 정보 수정
    @Override
    public ReviseGroupResponseDto reviseGroup(String runnerId, String groupId, ReviseGroupRequestDto reviseGroupRequestDto, MultipartFile image) throws IllegalAccessError, IOException {
        Optional<Group> group = groupRepository.findById(UUID.fromString(groupId));
        //그룹이 없으면 에러
        if(group.isEmpty()){
            throw new IllegalAccessError("존재하지 않는 그룹입니다.");
        }

        //러너가 없으면 에러
        Optional<Runner> runner = runnerRepository.findById(runnerId);
        if(runner.isEmpty()){
            throw new IllegalAccessError("존재하지 않는 러너입니다.");
        }

        Optional<Belong> belong = belongRepository.findByRunnerIdAndGroupId(runnerId, UUID.fromString(groupId));

        //그룹에 속하지 않으면 에러
        if(belong.isEmpty()){
            throw new IllegalAccessError("해당 러너는 그룹에 속하지 않습니다.");
        }
        //그룹의 리더가 아니면 에러
        if(!belong.get().isLeader()){
            throw new IllegalAccessError("해당 러너는 이 그룹의 리더가 아닙니다.");
        }

        if(reviseGroupRequestDto.getGroupCertificationCriteria() != 0){
            group.get().setCertificationCriteria(reviseGroupRequestDto.getGroupCertificationCriteria());
        }
        if(reviseGroupRequestDto.getGroupDescription() != null){
            group.get().setDescription(reviseGroupRequestDto.getGroupDescription());
        }

        String presignedImageUrl = (image != null && !image.isEmpty())
                ? awss3Service.putImageToAWSS3(image, "groups", groupId, 0)
                : awss3Service.getImagePresignedUrl("groups", groupId, 0);

        return new ReviseGroupResponseDto(
                group.get().getId().toString(),
                group.get().getName(),
                group.get().getDescription(),
                presignedImageUrl,
                group.get().getCertificationCriteria()
        );
    }


    //self group 정보 조회
    @Override
    public GetGroupResponseDto getMyGroup(String runnerId) {
        Optional<Group> group = groupRepository.findSelfGroupByRunnerId(runnerId);

        //셀프 그룹이 존재하지 않으면 오류
        if(group.isEmpty()){
            throw new ResourceNotFoundException("셀프 그룹이 존재하지 않습니다");
        }

        return new GetGroupResponseDto(
                group.get().getId().toString(),
                group.get().getName(),
                group.get().getDescription(),
                awss3Service.getImagePresignedUrl("runners", runnerId, 0)
        );
    }
}
