package com.simplecoding.evcharge.station.controller;

import com.simplecoding.evcharge.common.ApiResponse;
import com.simplecoding.evcharge.common.CommonUtil;
import com.simplecoding.evcharge.station.dto.StationDto;
import com.simplecoding.evcharge.station.entity.Station;
import com.simplecoding.evcharge.station.service.StationService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/station") // 요청하신 대로 /station 주소 사용
@RequiredArgsConstructor
@Slf4j // 로그 확인용 (필요시)
public class StationController {

    private final StationService stationService;
    private final CommonUtil util;

    /**
     * 1. 충전소 목록 조회 (검색어 + 페이징)
     * 호출 예: /api/station?searchKeyword=강남&page=0&size=10
     */
    @GetMapping("/station")
    public ResponseEntity<Object> selectStationList(
            @RequestParam(required = false) String searchKeyword,
            @PageableDefault(page = 0, size = 3) Pageable pageable) {
        // 1. 서비스 호출 (결과는 이미 Page<StationDto>)
        Page<StationDto> stations = stationService.selectStationList(searchKeyword, pageable);

        // 2. ApiResponse 규격에 맞춰 응답 생성
        // success, message, data, currentPage, totalElements 순서
        ApiResponse<Page<StationDto>> response = new ApiResponse<>(
                true,
                "조회 성공",
                stations,
                stations.getNumber(),
                stations.getTotalElements()
        );

        return new ResponseEntity<>(response, HttpStatus.OK);
    }



//     충전소 하나 조회
    @GetMapping("/{stationId}")
    public ResponseEntity<ApiResponse<StationDto>> selectStationDetail(@PathVariable long stationId) {

        // 1. 서비스 호출 (결과는 StationDto)
        StationDto dto = stationService.selectStationDetail(stationId);

        // 2. ApiResponse 담기 (단건 조회이므로 페이징 정보는 0, 0 혹은 제외 가능)
        ApiResponse<StationDto> response = new ApiResponse<>(true, "상세조회 성공", dto, 0, 0);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }
//    타입 조회
    @GetMapping("/search")
    public ResponseEntity<ApiResponse<List<StationDto>>> getStationsByType(@RequestParam("type") String cType) {
        List<StationDto> list = stationService.selectStationListByType(cType);

        // 목록 조회이므로 데이터 개수를 count에 넣어줍니다.
        ApiResponse<List<StationDto>> response =
                new ApiResponse<>(true, "타입별 충전소 조회 성공", list, list.size(), 1);

        return new ResponseEntity<>(response, HttpStatus.OK);
    }
    //    수정:@PutMapping사용
    @PutMapping("/station/{stationId}")
    public ResponseEntity<Void>update(@PathVariable long stationId,
                                      @Valid @RequestBody StationDto stationDto,
                                      BindingResult result){
        util.checkBindingResult(result);  //유효성 위반하면 에러메세지가 표시됩니다.
        stationDto.setStationId(stationId);          //기본키 저장
        stationService.updateFromDto(stationDto); //수정
//      ApiResponse<StationDto> response = new ApiResponse<>(true, "수정 성공", null, 0, 0);
        return  ResponseEntity.ok().build(); //ok신호만 보냄
    }
    //    삭제
    @DeleteMapping("/station/{stationId}")
    public ResponseEntity<Void> delet(@PathVariable long stationId){
        stationService.deleteStation(stationId);
        // ApiResponse 규격에 맞춰 성공 메시지 전달
//        ApiResponse<Void> response = new ApiResponse<>(true, "삭제 성공", null, 0, 0);
        return  ResponseEntity.ok().build();
    }
}