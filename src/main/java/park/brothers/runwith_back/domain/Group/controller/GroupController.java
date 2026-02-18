package park.brothers.runwith_back.domain.Group.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import park.brothers.runwith_back.common.CommonMessage;
import park.brothers.runwith_back.domain.Group.dto.Request.CreateGroupRequestDto;
import park.brothers.runwith_back.domain.Group.dto.Request.ReviseGroupRequestDto;
import park.brothers.runwith_back.domain.Group.dto.Response.CreateGroupResponseDto;
import park.brothers.runwith_back.domain.Group.dto.Response.GetGroupResponseDto;
import park.brothers.runwith_back.domain.Group.dto.Request.DeleteGroupRequestDto;
import park.brothers.runwith_back.domain.Group.dto.Response.ReviseGroupResponseDto;
import park.brothers.runwith_back.domain.Group.service.GroupService;
import park.brothers.runwith_back.common.Response.ValidationErrorUtils;

import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/groups")
public class GroupController {

    private final GroupService groupService;

    //그룹 생성 api
    @PostMapping
    public ResponseEntity<Object> save(@RequestBody @Valid CreateGroupRequestDto createGroupRequestDto, BindingResult bindingResult){ //BindingResult은 DTO만
        if (bindingResult.hasErrors()) {
            return ValidationErrorUtils.handleValidationErrors(bindingResult);
        }

        CreateGroupResponseDto createGroupResponseDto = groupService.save(createGroupRequestDto);

        return ResponseEntity.status(HttpStatus.CREATED).body(createGroupResponseDto);
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

    @DeleteMapping()
    public ResponseEntity<Object> deleteGroup(@RequestBody @Valid DeleteGroupRequestDto deleteGroupRequestDto, BindingResult bindingResult) {
        if(bindingResult.hasErrors()){
            return ValidationErrorUtils.handleValidationErrors(bindingResult);
        }
        groupService.delete(deleteGroupRequestDto);
        return ResponseEntity.status(HttpStatus.OK).body(new CommonMessage("그룹이 성공적으로 삭제되었습니다."));
    }

    @PatchMapping
    public ResponseEntity<Object> reviseGroupInfo(
            @RequestBody @Valid ReviseGroupRequestDto reviseGroupRequestDto,
            BindingResult bindingResult
            ) throws IllegalAccessException {
        if(bindingResult.hasErrors()){
            return ValidationErrorUtils.handleValidationErrors(bindingResult);
        }
        ReviseGroupResponseDto reviseGroupResponseDto = groupService.reviseGroup(reviseGroupRequestDto);

        return ResponseEntity.status(HttpStatus.OK).body(reviseGroupResponseDto);
    }
}
