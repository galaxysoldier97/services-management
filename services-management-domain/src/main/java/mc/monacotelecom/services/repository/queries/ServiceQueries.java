package mc.monacotelecom.services.repository.queries;

public final class ServiceQueries {
    private ServiceQueries() {
    }

    public static final String GET_CANDIDATE_SERVICES_FOR_CLEANUP = "select tmp.id, " +
            "       max(timestamp) as lastStatusChange, " +
            "       crmServiceId, " +
            "       status " +
            "from (select s.id, " +
            "             s.crm_service_id                           as crmServiceId, " +
            "             revision_id, " +
            "             s.status, " +
            "             s.service_activity, " +
            "             s.service_category, " +
            "             lag(sa.status) over (partition by s.id order by revision_id) as lag_status " +
            "      from service_aud sa " +
            "               inner join service s on sa.id = s.id " +
            "      where sa.status = :status " +
            "      and s.status = :status " +
            "      and (:category is null or s.service_category = :category) " +
            "      and (:activity is null or s.service_activity = :activity) " +
            "      order by s.id) as tmp " +
            "         inner join auditenversinfo on revision_id = auditenversinfo.id " +
            "    and (lag_status <> :status or lag_status is null) " +
            "    and (CURRENT_DATE - INTERVAL :delay DAY) >= FROM_UNIXTIME(timestamp / 1000) " +
            "group by id;";

    public static final String DELETE_AUDIT_FOR_SERVICE_IDS = "delete from service_aud where id in :ids";
    public static final String DELETE_AUDIT_FOR_SERVICE_COMPONENT_IDS = "delete from service_component_aud where id in :ids";
    public static final String DELETE_AUDIT_FOR_SERVICE_ACCESS_IDS = "delete from service_access_aud where id in :ids";
}
