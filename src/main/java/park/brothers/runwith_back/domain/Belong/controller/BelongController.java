package park.brothers.runwith_back.domain.Belong.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import park.brothers.runwith_back.common.CommonMessage;
import park.brothers.runwith_back.domain.Belong.dto.Request.ChangeLeaderRequestDto;
import park.brothers.runwith_back.domain.Belong.dto.Request.CreateBelongRequestDto;
import park.brothers.runwith_back.domain.Belong.dto.Response.CreateBelongResponseDto;
import park.brothers.runwith_back.domain.Belong.dto.Response.GetBelongOfGroupResponseDto;
import park.brothers.runwith_back.domain.Belong.dto.Response.GetBelongOfRunnerResponseDto;
import park.brothers.runwith_back.domain.Belong.service.BelongService;

import java.util.List;


@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/belongs")
public class BelongController {

    private final BelongService belongService;

    //그룹 참여 api(belong 생성)
    @PostMapping
    @Operation(summary = "그룹 가입하기",description = "그룹에 가입합니다.")
    public ResponseEntity<Object> save(
            @Parameter(hidden = true) @AuthenticationPrincipal String runnerId,
            @Parameter(
                    description = "그룹 가입 정보",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE)
            )
            @RequestBody @Valid CreateBelongRequestDto createBelongRequestDto,
            HttpServletRequest request
    ){
        String threadName = Thread.currentThread().getName();

        CreateBelongResponseDto createBelongResponseDto = belongService.joinGroup(runnerId, createBelongRequestDto);
        log.info("[{}] [{} {}] runnerId: {} - 그룹 가입 성공 | 요청 데이터 - groupId: {} | 응답 데이터 - belongId: {}", threadName, request.getMethod(), request.getRequestURI(), runnerId, createBelongRequestDto.getGroupId(), createBelongResponseDto.getBelongId());
        return ResponseEntity.status(HttpStatus.CREATED).body(createBelongResponseDto);
    }

    //그룹 탈퇴 api
    @DeleteMapping("/groups/{groupId}")
    @Operation(summary = "그룹 탈퇴하기",description = "그룹에서 탈퇴합니다.")
    public ResponseEntity<Object> leave(
            @Parameter(hidden = true) @AuthenticationPrincipal String runnerId,
            @Parameter(
                    description = "탈퇴할 그룹의 ID"
            )
            @PathVariable String groupId,
            HttpServletRequest request
    ){
        String threadName = Thread.currentThread().getName();

        belongService.leaveGroup(runnerId, groupId);
        log.info("[{}] [{} {}] runnerId: {} - 그룹 탈퇴 성공 | 요청 데이터 - groupId: {}", threadName, request.getMethod(), request.getRequestURI(), runnerId, groupId);

        return ResponseEntity.status(HttpStatus.OK).body(new CommonMessage("그룹에서 성공적으로 탈퇴되었습니다."));
    }

    //특정 runner가 속한 모든 그룹들을 응답 받는 api
    @GetMapping("/runners/{runnerId}")
    @Operation(summary = "특정 러너가 속한 모든 그룹 조회",description = "특정 러너가 속한 모든 그룹을 조회합니다.")
    public ResponseEntity<Object> getAllGroupsRunnerJoin(
            @Parameter(hidden = true) @AuthenticationPrincipal String selfRunnerId,
            @Parameter(
                    description = "검색할 러너의 ID"
            )
            @PathVariable String runnerId,
            HttpServletRequest request
    ){
        String threadName = Thread.currentThread().getName();

        List<GetBelongOfGroupResponseDto> groups = belongService.getAllGroupsRunnerJoin(runnerId);
        log.info("[{}] [{} {}] runnerId: {} - 특정 러너가 속한 모든 그룹 조회 성공 | 요청 데이터 - 검색할 runnerId: {} | 응답 데이터 - 조회된 그룹 수: {}", threadName, request.getMethod(), request.getRequestURI(), selfRunnerId, runnerId, groups.size());

        return ResponseEntity.status(HttpStatus.OK).body(groups);
    }

    //특정 그룹에 속한 모든 runner들을 응답 받는 api
    @GetMapping("/groups/{groupId}")
    @Operation(summary = "특정 그룹에 속한 모든 러너 조회",description = "특정 그룹에 속한 모든 러너들을 조회합니다.")
    public ResponseEntity<Object> getAllRunnersInGroup(
            @Parameter(hidden = true) @AuthenticationPrincipal String runnerId,
            @Parameter(
                    description = "검색할 그룹의 ID"
            )
            @PathVariable String groupId,
            HttpServletRequest request
    ){
        String threadName = Thread.currentThread().getName();

        List<GetBelongOfRunnerResponseDto> runners = belongService.getAllRunnersInGroup(groupId);
        log.info("[{}] [{} {}] runnerId: {} - 특정 그룹에 속한 모든 러너 조회 성공 | 요청 데이터 - 조회할 groupId: {} | 응답 데이터 - 조회된 러너 수: {}", threadName, request.getMethod(), request.getRequestURI(), runnerId, groupId, runners.size());

        return ResponseEntity.status(HttpStatus.OK).body(runners);
    }

    //그룹의 리더 변경 api
    @PatchMapping("/leader")
    @Operation(summary = "그룹 리더 변경",description = "로그인한 유저는 현재 리더이며, 리더를 다른 그룹원에 넘겨줍니다.")
    public ResponseEntity<Object> changeLeader(
            @Parameter(hidden = true) @AuthenticationPrincipal String oldLeaderRunnerId,
            @Parameter(
                    description = "리더 변경 정보",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE)
            )
            @RequestBody @Valid ChangeLeaderRequestDto changeLeaderRequestDto,
            HttpServletRequest request
    ){
        String threadName = Thread.currentThread().getName();

        belongService.changeLeader(oldLeaderRunnerId, changeLeaderRequestDto);
        log.info("[{}] [{} {}] runnerId: {} - 그룹 리더 변경 성공 | 요청 데이터 - groupId: {}, 리더가 된 runnerId: {}", threadName, request.getMethod(), request.getRequestURI(), oldLeaderRunnerId, changeLeaderRequestDto.getGroupId(), changeLeaderRequestDto.getNewLeaderRunnerId());

        return ResponseEntity.status(HttpStatus.ACCEPTED).body(new CommonMessage("해당 그룹의 리더가 성공적으로 변경되었습니다."));
    }

    //내가 속한 그룹들을 알 수 있는 api
    @GetMapping("/me")
    @Operation(summary = "내가 속한 그룹 조회", description = "내가 소속한 그룹(셀프 그룹 제외)을 조회합니다.")
    public ResponseEntity<Object> getMyGroups(
            @Parameter(hidden = true) @AuthenticationPrincipal String runnerId,
            HttpServletRequest request
    ){
        String threadName = Thread.currentThread().getName();

        List<GetBelongOfGroupResponseDto> groups = belongService.getAllGroupsRunnerJoin(runnerId);
        log.info("[{}] [{} {}] runnerId: {} - 내가 속한 그룹 조회 성공 | 응답 데이터 - 조회된 그룹 수: {}", threadName, request.getMethod(), request.getRequestURI(), runnerId, groups.size());
        return ResponseEntity.status(HttpStatus.OK).body(groups);
    }



}
