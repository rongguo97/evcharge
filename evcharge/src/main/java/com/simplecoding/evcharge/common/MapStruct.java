package com.simplecoding.evcharge.common;
// 목적: dto <-> 엔티티 정보를 복사해주는 함수명만 작성하는 곳



import com.simplecoding.evcharge.station.dto.StationDto;
import com.simplecoding.evcharge.station.entity.Station;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(componentModel = "spring",                                                  // 플로그인을 spring 에 사용한다는 의미
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE  // null 제외 기능(update 시 사용)
)
public interface MapStruct {
// ... 기존 코드들 생략

    // 5) 충전소 복사: dto <-> 엔티티
    StationDto toDto(Station station);                                  // 엔티티 -> DTO 복사
    Station toEntity(StationDto dto);                                  // DTO -> 엔티티 복사
    void updateFromDto(StationDto dto, @MappingTarget Station station); // 수정(더티 체킹)
}














