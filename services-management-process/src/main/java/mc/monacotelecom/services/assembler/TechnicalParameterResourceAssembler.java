package mc.monacotelecom.services.assembler;

import mc.monacotelecom.services.dto.TechnicalParameterDTO;
import mc.monacotelecom.services.entity.TechnicalParameter;
import mc.monacotelecom.services.mapper.TechnicalParameterMapper;
import org.springframework.hateoas.server.mvc.RepresentationModelAssemblerSupport;

public class TechnicalParameterResourceAssembler extends RepresentationModelAssemblerSupport<TechnicalParameter, TechnicalParameterDTO> {

    private TechnicalParameterResourceAssembler(Class<?> controllerClass) {
        super(controllerClass, TechnicalParameterDTO.class);
    }

    public static TechnicalParameterResourceAssembler of(Class<?> controllerClass) {
        return new TechnicalParameterResourceAssembler(controllerClass);
    }

    @Override
    public TechnicalParameterDTO toModel(TechnicalParameter entity) {
        return TechnicalParameterMapper.INSTANCE.toDto(entity);
    }

    public TechnicalParameter toEntity(TechnicalParameterDTO technicalParameterDTO) {
        return TechnicalParameterMapper.INSTANCE.toEntity(technicalParameterDTO);
    }
}