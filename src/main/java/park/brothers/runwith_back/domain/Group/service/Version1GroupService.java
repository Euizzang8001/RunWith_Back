package park.brothers.runwith_back.domain.Group.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import park.brothers.runwith_back.domain.Group.dto.CreateGroupDto;
import park.brothers.runwith_back.domain.Group.dto.GetGroupResponseDto;
import park.brothers.runwith_back.domain.Group.entity.Group;
import park.brothers.runwith_back.domain.Group.repository.GroupRepository;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class Version1GroupService implements GroupService {

    private final GroupRepository groupRepository;

    @Override
    public void save(CreateGroupDto createGroupDto) {
         if(groupRepository.findByName(createGroupDto.getName()) == null){
             Group group = new Group();
             group.setName(createGroupDto.getName());
             groupRepository.save(group);
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
    public void delete(Long id) {
        Group group = groupRepository.getById(id);
        groupRepository.delete(group);
    }
}
