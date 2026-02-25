package park.brothers.runwith_back.domain.Group.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import park.brothers.runwith_back.common.CommonMessage;
import park.brothers.runwith_back.domain.Group.dto.Request.CreateGroupRequestDto;
import park.brothers.runwith_back.domain.Group.dto.Request.ReviseGroupRequestDto;
import park.brothers.runwith_back.domain.Group.dto.Response.CreateGroupResponseDto;
import park.brothers.runwith_back.domain.Group.dto.Response.GetGroupResponseDto;
import park.brothers.runwith_back.domain.Group.dto.Response.ReviseGroupResponseDto;
import park.brothers.runwith_back.domain.Group.service.GroupService;
import park.brothers.runwith_back.common.Response.ValidationErrorUtils;

import java.io.IOException;
import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/groups")
public class GroupController {

    private final GroupService groupService;

    //그룹 생성 api
    @PostMapping(consumes = {MediaType.MULTIPART_FORM_DATA_VALUE})
    public ResponseEntity<Object> save(
            @AuthenticationPrincipal String runnerId,
            @RequestPart(value = "request") @Valid CreateGroupRequestDto createGroupRequestDto,
            BindingResult bindingResult,
            @RequestPart(value = "image", required = false)MultipartFile image
            ) throws IOException { //BindingResult은 DTO만
        if (bindingResult.hasErrors()) {
            return ValidationErrorUtils.handleValidationErrors(bindingResult);
        }

        CreateGroupResponseDto createGroupResponseDto = groupService.save(runnerId, createGroupRequestDto, image);

        return ResponseEntity.status(HttpStatus.CREATED).body(createGroupResponseDto);
    }

    //유사 이름의 그룹 얻기
    @GetMapping("/groupName={groupName}")
    public ResponseEntity<Object> findGroupsBySimilarName(@PathVariable @Valid String groupName){
        List<GetGroupResponseDto> groups = groupService.getGroupsBySimilarName(groupName);
        return ResponseEntity.status(HttpStatus.OK).body(groups);
    }

    // 모든 그룹 얻기
    @GetMapping
    public ResponseEntity<Object> getAllGroups() {
        List<GetGroupResponseDto> groups = groupService.getAllGroups();
        return ResponseEntity.status(HttpStatus.OK).body(groups);
    }

    //그룹 삭제하기
    @DeleteMapping("/groupId={groupId}")
    public ResponseEntity<Object> deleteGroup(
            @AuthenticationPrincipal String runnerId,
            @PathVariable @Valid String groupId
    ) {
        groupService.delete(runnerId, groupId);
        return ResponseEntity.status(HttpStatus.OK).body(new CommonMessage("그룹이 성공적으로 삭제되었습니다."));
    }

    //그룹 수정하기
    @PostMapping(value = {"/groupId={groupId}"}, consumes = {MediaType.MULTIPART_FORM_DATA_VALUE})
    public ResponseEntity<Object> reviseGroupInfo(
            @AuthenticationPrincipal String runnerId,
            @PathVariable @Valid String groupId,
            @RequestPart(value = "request") @Valid ReviseGroupRequestDto reviseGroupRequestDto,
            BindingResult bindingResult,
            @RequestPart(value = "image", required = false) MultipartFile image
            ) throws IllegalAccessException, IOException {
        if(bindingResult.hasErrors()){
            return ValidationErrorUtils.handleValidationErrors(bindingResult);
        }
        ReviseGroupResponseDto reviseGroupResponseDto = groupService.reviseGroup(runnerId, groupId, reviseGroupRequestDto, image);

        return ResponseEntity.status(HttpStatus.OK).body(reviseGroupResponseDto);
    }
}
