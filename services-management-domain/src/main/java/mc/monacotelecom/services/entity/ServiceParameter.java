package mc.monacotelecom.services.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.io.Serializable;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "service_parameter")
@IdClass(ServiceParameterKey.class)
public class ServiceParameter implements Serializable {

    @Column(nullable = false, columnDefinition = "VARCHAR(80)")
    private String value;

    @Id
    @Column(name = "service_id", nullable = false, updatable = false, insertable = false)
    private long serviceId;

    @Id
    @Column(name = "parameter_code", nullable = false, updatable = false, insertable = false, columnDefinition = "varchar(32)")
    private String parameterCode;

    @Id
    @Column(name = "parameter_type", nullable = false, updatable = false, insertable = false, columnDefinition = "varchar(32)")
    private String parameterType;

    @MapsId
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "service_id", referencedColumnName = "id", nullable = false)
    private Service service;

    @MapsId
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "parameter_code", referencedColumnName = "parameter_code", nullable = false)
    @JoinColumn(name = "parameter_type", referencedColumnName = "parameter_type", nullable = false)
    private TechnicalParameter technicalParameter;
}
