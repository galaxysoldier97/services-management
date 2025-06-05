package mc.monacotelecom.services.dto.request;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;
import mc.monacotelecom.services.dto.TagActionDTO;

import java.util.List;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class ChangeTagsDTO {

    private List<TagActionDTO> changeTags;

}
