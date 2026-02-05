package park.brothers.runwith_back.domain.Group.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import park.brothers.runwith_back.domain.Group.dto.CreateGroupDto;
import park.brothers.runwith_back.domain.Group.dto.GetGroupResponseDto;
import park.brothers.runwith_back.domain.Group.service.GroupService;
import park.brothers.runwith_back.common.Response.ValidationErrorUtils;

import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/groups")
public class GroupController {

    private final GroupService groupService;

    @PostMapping
    public ResponseEntity<Object> save(@RequestBody @Valid CreateGroupDto createGroupDto, BindingResult bindingResult){ //BindingResult은 DTO만
        if (bindingResult.hasErrors()) {
            return ValidationErrorUtils.handleValidationErrors(bindingResult);
        }

        groupService.save(createGroupDto);

        return ResponseEntity.status(HttpStatus.OK).body(createGroupDto);
    }

    @GetMapping("/{name}")
    public ResponseEntity<Object> findGroupsBySimilarName(@PathVariable @Valid String name){
        List<GetGroupResponseDto> groups = groupService.getGroupsBySimilarName(name);
        return ResponseEntity.status(HttpStatus.OK).body(groups);
    }

    @GetMapping
    public ResponseEntity<Object> getAllGroups() {
        List<GetGroupResponseDto> groups = groupService.getAllGroups();
        return ResponseEntity.status(HttpStatus.OK).body(groups);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Object> deleteGroup(@PathVariable @Valid Long id) {
        groupService.delete(id);
        return ResponseEntity.status(HttpStatus.OK).body(id);
    }
}
