SET
FOREIGN_KEY_CHECKS = 0;

insert into activation_code (id, activ_code, description, nature, network_component)
values (1, 'HPX_126', 'BroadBand Mobile ', 'PROFILE', 'SPG');

insert into provisioning_tag (id, tag_code, description, activity, category, access_type, nature, persistent)
values (1, 'HAXACC', 'Mobile Access ', 'MOBILE', 'ACCESS', 'BBHB', 'A', true);

insert into service (id, crm_service_id, service_category, service_activity, subscription_id, number, creation_date,
                     activation_date, deactivation_date, suspension_date, resumed_date, cancellation_date, status,
                     action_request, date_request, action_planned, planned_date, action_status, action_start,
                     action_end, customer_no, service_order_id, barring_date, reason_request, main_range_id)
values (1, '3', 'ACCESS', 'MOBILE', null, '35790000111', '2020-09-08 23:59:23', '2020-09-09 00:29:32', null, null, null,
        null, 'ACTIVATED', 'ACTIVATION', '2020-09-08 23:59:23', false, null, 'COMPLETED', '2020-09-08 23:59:23', null,
        null, null, null, null, null),
       (2, '10', 'COMPONENT', 'TELEPHONY', null, null, '2021-02-05 15:55:53', null, null, null, null, null, 'PENDING',
        null, null, null, null, null, null, null, null, null, null, null, null),
       (3, null, 'ACCESS', 'MOBILE', 3, '35790000112', '2020-09-08 23:59:23', '2020-09-09 00:29:32', null, null, null,
        null, 'CANCELED', 'ACTIVATION', '2020-09-08 23:59:23', false, null, 'COMPLETED', '2020-09-08 23:59:23', null,
        null, null, null, null, null),
       (4, '11', 'COMPONENT', 'TELEPHONY', 4, null, '2021-02-05 15:55:53', null, null, null, null, null, 'CANCELED',
        null, null, null, null, null, null, null, null, null, null, null, 8),
       (5, '5', 'ACCESS', 'MOBILE', 2, '35790000113', '2020-09-08 23:59:23', '2020-09-09 00:29:32', null, null, null,
        null, 'CANCELED', 'ACTIVATION', '2020-09-08 23:59:23', false, null, 'COMPLETED', '2020-09-08 23:59:23', null,
        10, null, null, null, null),
       (6, null, 'ACCESS', 'INTERNET', null, '35790000114', '2020-09-08 23:59:23', '2020-09-08 00:29:32', null, null,
        null,
        null, 'BARRED', 'UNBARRING', '2020-09-08 23:59:23', true, null, 'PENDING', '2020-09-08 23:59:23', null,
        null, null, null, null, null),
       (7, null, 'ACCESS', 'INTERNET', null, '35790000115', '2020-09-08 23:59:23', '2020-09-08 00:29:32', null, null,
        null,
        null, 'PENDING', 'ACTIVATION', '2020-09-08 23:59:23', true, null, 'PENDING', '2020-09-08 23:59:23', null,
        null, null, null, null, null),
       (8, '12', 'COMPONENT', 'TELEPHONY', 5, null, '2021-02-05 15:55:54', null, null, null, null, null, 'ACTIVATED',
        null, null, null, null, null, null, null, null, null, null, null, null),
       (9, '13', 'COMPONENT', 'TELEPHONY', 6, null, '2021-02-05 15:55:55', null, null, null, null, null, 'ACTIVATED',
        null, null, null, null, null, null, null, null, null, null, null, null);

insert into service_access (id, parent_service_id, equipment_id, access_point_id, access_type, equipment_category,
                            ont_id, tech_id)
values (1, null, 2, null, 'BBHB', null, 'BKR01:1-1-1-2-5', '212100000000001'),
       (3, null, 3, 15, 'MOBILE', null, 'BKR01:1-1-1-2-4', '212100000000001'),
       (5, 1, null, null, 'MOBILE', null, null, '212100000000001'),
       (6, null, null, null, 'FTTH', null, null, '212100000000001'),
       (7, 3, null, null, 'FTTH', null, null, '212100000000002');

insert into service_component (id, access_service_id, component_type, tech_id)
values (2, null, 'TEAMS', null),
       (4, 5, 'TEAMS', null),
       (8, 7, null, null),
       (9, 7, 'TEAMS', null);

insert into service_activation(activ_value, service_id, activ_code)
values (4, 1, 'HPX_126'),
       (4, 5, 'HPX_126'),
       (null, 4, 'MISSINGCODE');

insert into service_tag(tag_value, service_id, tag_code)
values (2, 1, 'HAXACC'),
       (null, 5, 'MISSINGTAG'),
       (null, 5, 'HAXACC');

insert into service_aud (id, revision_id, revision_type, crm_service_id, service_category, service_activity,
                         subscription_id, number, creation_date, activation_date, deactivation_date, suspension_date,
                         resumed_date, cancellation_date, status, action_request, date_request, action_planned,
                         planned_date, action_status, action_start, action_end, customer_no, service_order_id,
                         barring_date, reason_request, main_range_id)
values (1, 52, 0, null, 'ACCESS', 'MOBILE', null, null, '2020-10-22 14:15:21', null, null, null, null, null, 'PENDING',
        null, null, null, null, null, null, null, null, null, null, null, null),
       (1, 55, 1, '610', 'ACCESS', 'MOBILE', null, null, '2020-10-22 14:15:21', null, null, null, null, null, 'PENDING',
        null, null, null, null, null, null, null, 20000000031, null, null, null, null),
       (1, 58, 1, '610', 'ACCESS', 'MOBILE', null, null, '2020-10-22 14:15:21', null, null, null, null, null, 'PENDING',
        'ACTIVATION', '2020-10-22 14:15:22', false, null, 'PENDING', '2020-10-22 14:15:22', null, 20000000031,
        '07ZX1KZ8_610-7', null, null, null),
       (1, 61, 1, '610', 'ACCESS', 'MOBILE', null, '35796760612', '2020-10-22 14:15:21', null, null, null, null, null,
        'PENDING', 'ACTIVATION', '2020-10-22 14:15:22', false, null, 'PENDING', '2020-10-22 14:15:22', null,
        20000000031, '07ZX1KZ8_610-7', null, null, null),
       (1, 64, 1, '610', 'ACCESS', 'MOBILE', null, '35796760612', '2020-10-22 14:15:21', '2020-10-22 17:54:29', null,
        null, null, null, 'ACTIVATED', 'ACTIVATION', '2020-10-22 14:15:22', false, null, 'COMPLETED',
        '2020-10-22 14:15:22', '2020-10-22 17:54:29', 20000000031, '07ZX1KZ8_610-7', null, null, null);


insert into service_access_aud (id, revision_id, parent_service_id, equipment_id, access_point_id,
                                access_type, equipment_category, tech_id)
values (1, 52, null, null, null, 'BBHB', null, '212100000000001'),
       (1, 55, null, null, null, 'BBHB', null, '212100000000001'),
       (1, 58, null, null, null, 'BBHB', null, '212100000000001'),
       (1, 61, null, null, null, 'BBHB', null, '212100000000001'),
       (1, 64, null, null, null, 'BBHB', null, '212100000000001'),
       (1, 97, null, null, null, 'BBHB', null, '212100000000001');

insert into auditenversinfo (id, timestamp, user_id)
values (52, 1603368921732, 'service-account-services-management-workflow-connector'),
       (55, 1603368921951, 'j.lethiec'),
       (58, 1603368922243, 'h.amri'),
       (61, 1603369026183, 'b.nzonlia'),
       (64, 1603382069134, 'j.martin'),
       (97, 1603976989792, 'c.davino');

INSERT INTO service_parameter (parameter_code, parameter_type, service_id, value)
VALUES ('PARAM1', 'CONTEXT', 5, '10'),
       ('PARAM3', 'CONTEXT', 5, '11');

INSERT INTO technical_parameter (id, description, parameter_code, parameter_type)
VALUES (20, null, 'PARAM1', 'CONTEXT'),
       (21, null, 'PARAM2', 'STATIC'),
       (22, null, 'PARAM3', 'CONTEXT');

SET
FOREIGN_KEY_CHECKS = 1;
