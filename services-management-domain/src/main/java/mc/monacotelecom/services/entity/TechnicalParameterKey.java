package mc.monacotelecom.services.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import mc.monacotelecom.services.enums.TechnicalParameterType;

import java.io.Serializable;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TechnicalParameterKey implements Serializable {

    private String parameterCode;

    private TechnicalParameterType parameterType;
}
