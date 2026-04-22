package com.simplecoding.evcharge.reservation.dto;

import com.simplecoding.evcharge.reservation.entity.Status;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder // 💡 빌더 패턴을 추가하면 테스트 코드나 서비스에서 객체 생성 시 편리합니다.
@ToString
public class ReservationDto {

    // 1. 예약 ID (생성 시에는 Null일 수 있으므로 @NotNull 제거 고려)
    private Long id;

    // 2. [중요] chargerId -> stationId로 변경
    @NotNull(message = "충전소 ID는 필수입니다.")
    private Long stationId;

    // 3. 사용자 이메일 (@NotBlank는 문자열이 비어있지 않음을 보장)
    @NotBlank(message = "이메일은 필수입니다.")
    private String email;

    @NotNull(message = "시작 시간은 필수입니다.")
    private LocalDateTime startTime;

    private LocalDateTime endTime; // 종료 시간은 서비스에서 자동 계산되므로 필수 체크 제외 가능

    private String status; // 기본값 "RESERVED"

    // 4. [선택/추천] 화면에 보여줄 때 필요한 추가 정보 (Station 정보 등)
    // 리액트 등 프론트에서 충전소 이름을 바로 보여주고 싶을 때 유용합니다.
    private String stationName;
    private String address;

    private String rDate;

    // 초과요금 추가 (서비스에서 쓰고 있었음)
    private int overstayFee;

    public ReservationDto(Long reservationId, Long stationId, @NonNull String email, @NonNull LocalDateTime startTime, LocalDateTime endTime, Status status, String stationName, String address) {
        this.reservationId = reservationId;
        this.stationId = stationId;
        this.email = email;
        this.startTime = startTime;
        this.endTime = endTime;
        this.status = status;
        this.stationName = stationName;
        this.address = address;
    }
}