package mc.monacotelecom.services.mapper;

import mc.monacotelecom.services.dto.ActivationCodeDTO;
import mc.monacotelecom.services.dto.request.CreateActivationCodeDTO;
import mc.monacotelecom.services.entity.ActivationCode;
import mc.monacotelecom.services.entity.TagActivation;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.mapstruct.factory.Mappers;

import java.util.Set;
import java.util.stream.Collectors;

import static java.util.Objects.nonNull;

@Mapper
public interface ActivationCodeMapper {

    ActivationCodeMapper INSTANCE = Mappers.getMapper(ActivationCodeMapper.class);

    @Mapping(target = "code", source = "activCode")
    @Mapping(target = "activationCodeId", source = "internalId")
    @Mapping(target = "tagCodes", source = "tagActivations", qualifiedByName = "extractCodes")
    ActivationCodeDTO toDto(ActivationCode activationCode);

    @Mapping(target = "internalId", source = "activationCodeId")
    @Mapping(target = "activCode", source = "code")
    ActivationCode toEntity(ActivationCodeDTO activationCodeDTO);

    @Mapping(target = "activCode", source = "code")
    ActivationCode toEntity(CreateActivationCodeDTO activationCodeDTO);

    @Named("extractCodes")
    static Set<String> extractCodes(Set<TagActivation> tagActivations){
        return nonNull(tagActivations)? tagActivations.stream().map(TagActivation::getTagCode).collect(Collectors.toSet()) : Set.of();
    }
}
