SET
FOREIGN_KEY_CHECKS = 0;

insert into technical_parameter (id, description, parameter_code, parameter_type)
values (1, 'Technical Parameter 1', 'PARAM1', 'STATIC'),
       (2, 'Technical Parameter 2', 'PARAM2', 'CONTEXT'),
       (3, 'Technical Parameter 3', 'PARAM3', 'CONTEXT'),
       (4, 'Technical Parameter 4', 'PARAM4', 'CONTEXT'),
       (5, 'Technical Parameter 4', 'PARAM4', 'STATIC');

insert into provisioning_tag (id, tag_code, description, activity, category, access_type, nature, persistent)
values (1, 'HAXACC', 'Mobile Access ', 'MOBILE', 'ACCESS', 'BBHB', 'A', true),
       (2, 'HAXACC2', 'Mobile Access ', 'MOBILE', 'ACCESS', 'BBHB', 'A', true);

insert into provisioning_action(id, tag_code, tag_action, service_action)
values (1, 'HAXACC', 'PURCHASE', null),
       (2, 'HAXACC2', 'CANCEL', null);

insert into provisioning_action_parameter (parameter_value, tag_code, tag_action, parameter_code, parameter_type)
values (null, 'HAXACC', 'PURCHASE', 'PARAM1', 'STATIC'),
       (null, 'HAXACC', 'PURCHASE', 'PARAM2', 'CONTEXT'),
       (null, 'HAXACC2', 'CANCEL', 'PARAM2', 'CONTEXT');

insert into service_parameter (parameter_code, parameter_type, service_id, value)
values ('Technical Parameter 4', 'CONTEXT', 1, 'TEST');

insert into service (id, crm_service_id, service_category, service_activity, subscription_id, number, creation_date,
                     activation_date, deactivation_date, suspension_date, resumed_date, cancellation_date, status,
                     action_request, date_request, action_planned, planned_date, action_status, action_start,
                     action_end, customer_no, service_order_id, barring_date, reason_request, main_range_id)
values (1, '3', 'ACCESS', 'MOBILE', null, '35790000111', '2020-09-08 23:59:23', '2020-09-09 00:29:32', null, null, null,
        null, 'ACTIVATED', 'ACTIVATION', '2020-09-08 23:59:23', false, null, 'COMPLETED', '2020-09-08 23:59:23', null,
        null, null, null, null, null);

insert into service_access (id, parent_service_id, equipment_id, access_point_id, access_type, equipment_category,
                            ont_id, tech_id)
values (1, null, 2, null, 'BBHB', null, null, '212100000000001');

SET
FOREIGN_KEY_CHECKS = 1;