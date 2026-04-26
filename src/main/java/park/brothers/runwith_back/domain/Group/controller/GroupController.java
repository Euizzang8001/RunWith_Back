package park.brothers.runwith_back.domain.Group.controller;

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
    @Operation(summary = "그룹 생성",description = "새로운 그룹을 생성합니다.")
    public ResponseEntity<Object> save(
            @Parameter(hidden = true) @AuthenticationPrincipal String runnerId,
            @Parameter(
                    description = "그룹 생성 정보",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE)
            )
            @RequestPart(value = "request") @Valid CreateGroupRequestDto createGroupRequestDto,
            @Parameter(
                    description = "그룹 이미지"
            )
            @RequestPart(value = "image", required = false)MultipartFile image,
            HttpServletRequest request
    ) throws IOException { //BindingResult은 DTO만
        String threadName = Thread.currentThread().getName();

        CreateGroupResponseDto createGroupResponseDto = groupService.save(runnerId, createGroupRequestDto, image);
        log.info("[{}] [{} {}] runnerId: {} - 그룹 생성 성공 | 응답 데이터 - groupId: {}", threadName, request.getMethod(), request.getRequestURI(), runnerId, createGroupResponseDto.getGroupId());
        return ResponseEntity.status(HttpStatus.CREATED).body(createGroupResponseDto);
    }

    //유사 이름의 그룹 얻기
    @GetMapping
    @Operation(summary = "그룹 조회",description = "검색한 이름이 들어간 모든 그룹을 조회합니다. / groupName을 입력하지 않으면 전체 조회합니다.")
    public ResponseEntity<Object> findGroupsBySimilarName(
            @Parameter(hidden = true) @AuthenticationPrincipal String runnerId,
            @Parameter( description = "검색할 그룹 이름" )
            @RequestParam(required = false) String groupName,
            @Parameter( description = "검색을 시작할 index" )
            @RequestParam int offset,
            @Parameter( description = "검색 결과 수" )
            @RequestParam int limit,
            HttpServletRequest request
    ){
        String threadName = Thread.currentThread().getName();

        List<GetGroupResponseDto> groups = groupService.getGroupsBySimilarName(groupName, offset, limit);
        log.info("[{}] [{} {}] runnerId: {} - 그룹 조회 성공 | 요청 데이터 - groupName: {}, offset: {}, limit: {} | 응답 데이터 - 조회된 그룹 수: {}", threadName, request.getMethod(), request.getRequestURI(), runnerId, groupName, offset, limit, groups.size());
        return ResponseEntity.status(HttpStatus.OK).body(groups);
    }

    //그룹 삭제하기
    @DeleteMapping("/{groupId}")
    @Operation(summary = "그룹 삭제",description = "특정 그룹을 삭제합니다.")
    public ResponseEntity<Object> deleteGroup(
            @Parameter(hidden = true) @AuthenticationPrincipal String runnerId,
            @PathVariable String groupId,
            HttpServletRequest request
    ) {
        String threadName = Thread.currentThread().getName();

        groupService.delete(runnerId, groupId);
        log.info("[{}] [{} {}] runnerId: {} - 그룹 삭제 성공 | 요청 데이터 - groupId: {}", threadName, request.getMethod(), request.getRequestURI(), runnerId, groupId);
        return ResponseEntity.status(HttpStatus.OK).body(new CommonMessage("그룹이 성공적으로 삭제되었습니다."));
    }

    //그룹 수정하기
    @Operation(summary = "그룹 수정",description = "특정 그룹 정보를 수정합니다.")
    @PatchMapping(value = {"/{groupId}"}, consumes = {MediaType.MULTIPART_FORM_DATA_VALUE})
    public ResponseEntity<Object> reviseGroupInfo(
            @Parameter(hidden = true) @AuthenticationPrincipal String runnerId,
            @Parameter(
                    description = "그룹 id"
            )
            @PathVariable String groupId,
            @Parameter(
                    description = "그룹 수정 정보",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE)
            )
            @RequestPart(value = "request") @Valid ReviseGroupRequestDto reviseGroupRequestDto,
            @Parameter(
                    description = "그룹 수정 이미지"
            )
            @RequestPart(value = "image", required = false) MultipartFile image,
            HttpServletRequest request
    ) throws IllegalAccessException, IOException {
        String threadName = Thread.currentThread().getName();

        ReviseGroupResponseDto reviseGroupResponseDto = groupService.reviseGroup(runnerId, groupId, reviseGroupRequestDto, image);
        log.info("[{}] [{} {}] runnerId: {} - 그룹 수정 성공 | 요청 데이터 - groupId: {}", threadName, request.getMethod(), request.getRequestURI(), runnerId, groupId);
        return ResponseEntity.status(HttpStatus.OK).body(reviseGroupResponseDto);
    }

    //자신의 그룹 정보(자신만의 일정을 담은) 조회하기
    @GetMapping("/self")
    @Operation(summary = "셀프 그룹 정보 조회",description = "로그인한 러너의 셀프 그룹을 조회합니다.")
    public ResponseEntity<Object> getMyGroupInfo(
            @Parameter(hidden = true) @AuthenticationPrincipal String runnerId,
            HttpServletRequest request
    ){
        String threadName = Thread.currentThread().getName();

        GetGroupResponseDto getGroupResponseDto = groupService.getMyGroup(runnerId);
        log.info("[{}] [{} {}] runnerId: {} - 자신의 셀프 그룹 조회 성공 | 응답 데이터 - selfGroupId: {}", threadName, request.getMethod(), request.getRequestURI(), runnerId, getGroupResponseDto.getGroupId());
        return ResponseEntity.status(HttpStatus.OK).body(getGroupResponseDto);
    }
}
