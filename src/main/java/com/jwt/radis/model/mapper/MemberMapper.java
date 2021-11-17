package com.jwt.radis.model.mapper;

import com.jwt.radis.model.dto.MemberCreationRequest;
import com.jwt.radis.model.dto.MemberResponse;
import com.jwt.radis.model.entity.Member;
import org.mapstruct.Mapper;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.mapstruct.ReportingPolicy;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring",
        unmappedTargetPolicy = ReportingPolicy.ERROR,
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.SET_TO_DEFAULT
)
public interface MemberMapper {

    MemberMapper INSTANCE = Mappers.getMapper(MemberMapper.class);


     MemberResponse modelToDto(Member member);

    Member dtoToModel(MemberCreationRequest member);

}