SET
FOREIGN_KEY_CHECKS = 0;

insert into provisioning_tag (id, tag_code, description, activity, category, access_type, nature, persistent,
                              parent_tag_code, component_type)
values (1, 'HAXACC', 'Mobile Access ', 'MOBILE', 'ACCESS', 'BBHB', 'A', true, null, 'TEAMS'),
       (2, 'HPXBRDBAND1', 'Broadband In a Box - Profile 1', 'MOBILE', 'ACCESS', 'BBHB', 'P', true, null, 'TEAMS'),
       (3, 'HECARD', 'Sim Card', 'MOBILE', 'ACCESS', 'BBHB', 'E', true, null, 'TEAMS'),
       (4, 'HODATA1', 'Data Option 1', 'INTERNET', 'COMPONENT', 'BBHB', 'O', true, null, 'TEAMS'),
       (5, 'HUCCHGE', 'Change SIM Card', 'MOBILE', 'ACCESS', 'MOBILE', 'U', false, 'HODATA1', 'CAS'),
       (6, 'HUCCHGE2', 'Change SIM Card 2', 'MOBILE', 'ACCESS', 'MOBILE', 'U', false, 'HPXBRDBAND1', null);

insert into provisioning_action (id, tag_code, tag_action, service_action)
values (1, 'HAXACC', 'PURCHASE', null),
       (2, 'HAXACC', 'CANCEL', null),
       (3, 'HECARD', 'PURCHASE', null);

insert into provisioning_product (id, product_code, action_request, tag_code, tag_action)
values (1, 'PAY_AS_YOU_GO_1', 'ACTIVATION', 'HAXACC', 'PURCHASE'),
       (2, 'PAY_AS_YOU_GO_1', 'DEACTIVATION', 'HAXACC', 'CANCEL');

insert into provisioning_action_parameter (parameter_value, tag_code, tag_action, parameter_code, parameter_type)
values ('TEST', 'HAXACC', 'PURCHASE', 'PARAM1', 'STATIC');

insert into technical_parameter (id, description, parameter_code, parameter_type)
values (1, 'Technical Parameter 1', 'PARAM1', 'STATIC');

insert into activation_code (id, activ_code, description, nature, network_component)
values (1, 'HPX_126', 'BroadBand Mobile ', 'PROFILE', 'SPG');

insert into tag_activation (tag_value, tag_code, activ_code)
values (null, 'HECARD', 'HPX_126');

insert into service_tag (tag_value, tag_code, service_id)
values (null, 'HUCCHGE', 1),
       (null, 'HUCCHGEMISSING', 1);

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