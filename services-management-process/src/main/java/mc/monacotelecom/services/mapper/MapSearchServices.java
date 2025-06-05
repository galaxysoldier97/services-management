package mc.monacotelecom.services.mapper;

import mc.monacotelecom.services.dto.search.SearchServiceAccessDTO;
import mc.monacotelecom.services.dto.search.SearchServiceComponentDTO;
import mc.monacotelecom.services.dto.search.SearchServiceDTO;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper
public interface MapSearchServices {

    MapSearchServices INSTANCE = Mappers.getMapper(MapSearchServices.class);

    SearchServiceAccessDTO toServiceAccess(SearchServiceDTO searchServiceDTO);
    SearchServiceComponentDTO toServiceComponent(SearchServiceDTO searchServiceDTO);
}
