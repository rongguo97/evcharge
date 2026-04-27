package com.simplecoding.evcharge.reservation.entity;

import com.simplecoding.evcharge.station.entity.Station;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "TB_RESERVATION")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Reservation {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "res_seq")
    @SequenceGenerator(name = "res_seq", sequenceName = "SQ_RESERVATION", allocationSize = 1)
    @Column(name = "RESERVATION_ID")
    private Long reservationId;

    // ✅ 1. 이 필드가 진짜 조인을 수행하는 핵심입니다!
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "STATION_ID", nullable = false)
    private Station station;

    // ❌ 2. @Column(name = "STATION_NAME") 필드는 삭제했습니다. (DB에 없으므로)

    @Column(name = "EMAIL", nullable = false, length = 100)
    private String email;

    @Column(name = "START_TIME", nullable = false)
    private LocalDateTime startTime;

    @Column(name = "END_TIME", nullable = false)
    private LocalDateTime endTime;

    private String status;

    @Column(name = "R_Date", length = 20)
    private String rDate;

    // ✅ 3. 만약 엔티티에서 바로 이름을 꺼내고 싶다면 이런 메서드를 추가하면 됩니다.
    // @Transient는 DB 컬럼으로 만들지 말라는 뜻입니다.
    @Transient
    public String getStationNameFromJoin() {
        return this.station != null ? this.station.getStationName() : null;
    }

    @PostLoad
    public void cleanUpDate() {
        if (this.rDate != null && this.rDate.length() > 10) {
            this.rDate = this.rDate.substring(0, 10);
        }
    }
}