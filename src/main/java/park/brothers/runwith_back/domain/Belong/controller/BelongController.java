package park.brothers.runwith_back.domain.Belong.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import park.brothers.runwith_back.common.CommonMessage;
import park.brothers.runwith_back.common.Response.ValidationErrorUtils;
import park.brothers.runwith_back.domain.Belong.dto.Request.ChangeLeaderRequestDto;
import park.brothers.runwith_back.domain.Belong.dto.Request.CreateBelongRequestDto;
import park.brothers.runwith_back.domain.Belong.dto.Request.DeleteBelongRequestDto;
import park.brothers.runwith_back.domain.Belong.dto.Response.ChangeLeaderResponseDto;
import park.brothers.runwith_back.domain.Belong.dto.Response.CreateBelongResponseDto;
import park.brothers.runwith_back.domain.Belong.dto.Response.GetBelongOfGroupResponseDto;
import park.brothers.runwith_back.domain.Belong.dto.Response.GetBelongOfRunnerResponseDto;
import park.brothers.runwith_back.domain.Belong.service.BelongService;
import park.brothers.runwith_back.domain.Group.dto.Response.GetGroupResponseDto;
import park.brothers.runwith_back.domain.Runner.dto.Response.GetRunnerResponseDto;

import java.util.List;


@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/belongs")
public class BelongController {

    private final BelongService belongService;

    //그룹 참여 api(belong 생성)
    @PostMapping
    public ResponseEntity<Object> save(
            @AuthenticationPrincipal String runnerId,
            @RequestBody @Valid CreateBelongRequestDto createBelongRequestDto,
            BindingResult bindingResult){
        if(bindingResult.hasErrors()){
            return ValidationErrorUtils.handleValidationErrors(bindingResult);
        }

        CreateBelongResponseDto createBelongResponseDto = belongService.joinGroup(runnerId, createBelongRequestDto);

        return ResponseEntity.status(HttpStatus.CREATED).body(createBelongResponseDto);
    }

    //그룹 탈퇴 api
    @DeleteMapping("/{groupId}")
    public ResponseEntity<Object> leave(
            @AuthenticationPrincipal String runnerId,
            @PathVariable @Valid String groupId
    ){
        belongService.leaveGroup(runnerId, groupId);

        return ResponseEntity.status(HttpStatus.OK).body(new CommonMessage("그룹에서 성공적으로 탈퇴되었습니다."));
    }

    //특정 runner가 속한 모든 그룹들을 응답 받는 api
    @GetMapping("/runnerId={runnerId}")
    public ResponseEntity<Object> getAllGroupsRunnerJoin(@PathVariable @Valid String runnerId){
        List<GetBelongOfGroupResponseDto> groups = belongService.getAllGroupsRunnerJoin(runnerId);
        return ResponseEntity.status(HttpStatus.OK).body(groups);
    }

    //특정 그룹에 속한 모든 runner들을 응답 받는 api
    @GetMapping("/groupId={groupId}")
    public ResponseEntity<Object> getAllRunnersInGroup(@PathVariable @Valid String groupId){
        List<GetBelongOfRunnerResponseDto> runners = belongService.getAllRunnersInGroup(groupId);
        return ResponseEntity.status(HttpStatus.OK).body(runners);
    }

    //그룹의 리더 변경 api
    @PatchMapping("/leader")
    public ResponseEntity<Object> changeLeader(
            @AuthenticationPrincipal String oldLeaderRunnerId,
            @RequestBody @Valid ChangeLeaderRequestDto changeLeaderRequestDto,
            BindingResult bindingResult){
        if(bindingResult.hasErrors()){
            return ValidationErrorUtils.handleValidationErrors(bindingResult);
        }

        belongService.changeLeader(oldLeaderRunnerId, changeLeaderRequestDto);

        return ResponseEntity.status(HttpStatus.ACCEPTED).body(new CommonMessage("해당 그룹의 리더가 성공적으로 변경되었습니다."));
    }






}
