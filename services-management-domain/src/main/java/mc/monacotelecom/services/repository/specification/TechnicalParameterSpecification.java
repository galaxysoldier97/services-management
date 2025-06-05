package mc.monacotelecom.services.repository.specification;

import mc.monacotelecom.services.entity.TechnicalParameter;
import org.springframework.data.jpa.domain.Specification;



public final class TechnicalParameterSpecification {
    private TechnicalParameterSpecification() {
    }

    public static Specification<TechnicalParameter> hasCode(String parameterCode) {
        return (root, query, cb) -> cb.equal(root.get("parameterCode"), parameterCode);
    }
}
