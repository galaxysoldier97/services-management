package mc.monacotelecom.services.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ServiceParameterKey implements Serializable {

    private Long serviceId;
    private String parameterCode;
    private String parameterType;
}
