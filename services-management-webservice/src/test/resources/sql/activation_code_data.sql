SET
FOREIGN_KEY_CHECKS = 0;

insert into provisioning_tag (id, tag_code, description, activity, category, access_type, nature, persistent)
values (145, 'IEFBOX', 'Box ftth', 'INTERNET', 'ACCESS', 'FTTH', 'E', true),
       (146, 'IEFBOX2', 'Box ftth 2', 'INTERNET', 'ACCESS', 'FTTH', 'E', true);

insert into activation_code (id, activ_code, description, nature, network_component)
values (1, 'HPX_126', 'BroadBand Mobile ', 'PROFILE', 'SPG'),
       (2, 'HOD_72', 'Service Data GPRS', 'OPTION', 'SPG'),
       (4, 'HOG_HBOX20', 'Data Bucket 4 - 750 GB - 20 / 5Mps', 'BUCKET', 'PCRF'),
       (5, 'HOTU_HBOX60', 'Top Up 1 - 50 GB - 60 / 5Mps', 'BOOSTER', 'PCRF'),
       (6, 'GPRSLOCK', 'Lock GPRS service', 'LOCK', 'SPG'),
       (7, 'ODBIC', 'Bar all incoming calls', 'BARRING', 'SPG'),
       (8, 'HOV_CWAIT', 'Call Waiting ', 'CONFIG', 'CS');

insert into service (id, crm_service_id, service_category, service_activity, subscription_id, number, creation_date,
                     activation_date, deactivation_date, suspension_date, resumed_date, cancellation_date, status,
                     action_request, date_request, action_planned, planned_date, action_status, action_start,
                     action_end, customer_no, service_order_id, barring_date, reason_request, main_range_id)
values (1, '3', 'ACCESS', 'MOBILE', null, '35790000111', '2020-09-08 23:59:23', '2020-09-09 00:29:32', null, null, null,
        null, 'ACTIVATED', 'ACTIVATION', '2020-09-08 23:59:23', false, null, 'COMPLETED', '2020-09-08 23:59:23', null,
        null, null, null, null, null);

insert into service_access (id, parent_service_id, equipment_id, access_point_id, access_type, equipment_category,
                            tech_id)
values (1, null, 2, null, 'BBHB', null, '212100000000001');

insert into service_activation(activ_value, service_id, activ_code)
values (null, 1, 'HPX_126'),
       (null, 1, 'MISSING');

insert into tag_activation (tag_value, tag_code, activ_code)
values (null, 'IEFBOX', 'GPRSLOCK'),
       (null, 'IEFBOX2', 'ODBIC');

insert into provisioning_action(id, tag_code, tag_action, service_action)
values (1, 'IEFBOX', 'PURCHASE', null),
       (2, 'IEFBOX', 'CANCEL', null),
       (3, 'IEFBOX2', 'PURCHASE', null),
       (4, 'IEFBOX2', 'CANCEL', null);

insert into provisioning_product (id, product_code, action_request, tag_code, tag_action)
values (1, 'PAY_AS_YOU_GO_1', 'ACTIVATION', 'IEFBOX', 'PURCHASE'),
       (2, 'PAY_AS_YOU_GO_1', 'DEACTIVATION', 'IEFBOX', 'CANCEL'),
       (3, 'PAY_AS_YOU_GO_2', 'ACTIVATION', 'IEFBOX2', 'PURCHASE'),
       (4, 'PAY_AS_YOU_GO_2', 'DEACTIVATION', 'IEFBOX2', 'CANCEL');

SET
FOREIGN_KEY_CHECKS = 1;