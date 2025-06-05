package mc.monacotelecom.services.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TagActivationKey implements Serializable {

    private String tagCode;
    private String actvCode;
}
