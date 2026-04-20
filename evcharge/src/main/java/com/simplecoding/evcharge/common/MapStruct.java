package com.simplecoding.evcharge.common;
// 목적: dto <-> 엔티티 정보를 복사해주는 함수명만 작성하는 곳



import com.simplecoding.evcharge.auth.dto.MemberDto;
import com.simplecoding.evcharge.auth.entity.Member;
import com.simplecoding.evcharge.station.dto.StationDto;
import com.simplecoding.evcharge.station.entity.Station;
import org.mapstruct.*;

@Mapper(componentModel = "spring",                                                  // 플로그인을 spring 에 사용한다는 의미
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE  // null 제외 기능(update 시 사용)
)
public interface MapStruct {
// ... 기존 코드들 생략

    // 1. Entity -> DTO (마이페이지 조회 등)
    MemberDto toDto(Member member);
    // 2. DTO -> Entity (회원가입 등)
    Member toEntity(MemberDto memberDto);

//     * 3. 정보 수정 (DTO의 내용을 기존 Entity에 덮어쓰기)
//    비어있는 값 무시
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
//    pk는 절대 안바뀜
    @Mapping(target = "email", ignore = true) // PK는 수정 불가
//  가입일은 못바꿈
//  DTO ->ENTITY
    void updateFromDto(MemberDto dto, @MappingTarget Member entity);
//    요약:이메일과 가입일은 건드리지 말고, 리액트에서 보내준 값들 중에서 비어있지 않은(Not Null) 값들만 골라서 기존 회원 정보를 업데이트해라

    // 1. DTO -> Entity (날짜 포맷 지정)
    @Mapping(source = "statUpdateDatetime", target = "statUpdateDatetime", dateFormat = "yyyyMMddHHmmss")
    @Mapping(source = "lng", target = "lng") // DTO의 lng를 Entity의 lng로 매핑
    Station toEntity(StationDto stationDto);

    // 2. Entity -> DTO (날짜 포맷 지정)
    @Mapping(source = "statUpdateDatetime", target = "statUpdateDatetime", dateFormat = "yyyyMMddHHmmss")
    @Mapping(source = "lng", target = "lng")
    StationDto toDto(Station station);

    // 3. 수정 시 사용 (Dirty Checking용)
    @Mapping(source = "statUpdateDatetime", target = "statUpdateDatetime", dateFormat = "yyyyMMddHHmmss")
    @Mapping(source = "lng", target = "lng")
    void updateFromDto(StationDto stationDto, @MappingTarget Station station);
}














