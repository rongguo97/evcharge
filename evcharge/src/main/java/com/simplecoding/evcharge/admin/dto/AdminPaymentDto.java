package com.simplecoding.evcharge.admin.dto;

import lombok.*;
import java.time.LocalDateTime;

public class AdminPaymentDto {

    @Getter
    @Setter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Response {
        private Long payId;
        private String email;
        private Long reservationId;
        private String paymentType;
        private Long amount;
        private LocalDateTime createdAt;

        // 만약 프론트엔드에서 사용자 이름이나
        // 충전소 이름을 같이 보고 싶다면 여기에 필드를 추가하고
        // Service에서 Join 쿼리로 채워주면됨.
        // private String memberName;
    }

    // 혹시나 관리자가 결제 취소 사유 등을 입력해야 한다면 추가
    @Getter @Setter @Builder @NoArgsConstructor @AllArgsConstructor
    public static class Request {
        private String email;
        private Long amount;
        private String reason;
    }
}