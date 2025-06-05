SET
FOREIGN_KEY_CHECKS = 0;

insert into provisioning_tag (id, tag_code, description, activity, category, access_type, nature, persistent)
values  (1, 'HAXACC', 'Mobile Access ', 'MOBILE', 'ACCESS', 'BBHB', 'A', true),
        (2, 'HECARD', 'Sim Card', 'TELEPHONY', 'COMPONENT', 'MOBILE', 'E', true);

insert into provisioning_action(id, tag_code, tag_action, service_action)
values (1, 'HAXACC', 'PURCHASE', null),
       (2, 'HAXACC', 'CANCEL', null),
       (3, 'HECARD', 'PURCHASE', null);

insert into technical_parameter (id, description, parameter_code, parameter_type)
values (1, 'Technical Parameter 1', 'PARAM1', 'STATIC'),
       (2, 'Technical Parameter 2', 'PARAM2', 'STATIC');

insert into provisioning_action_parameter (parameter_value, tag_code, tag_action, parameter_code, parameter_type)
values ('TEST', 'HAXACC', 'PURCHASE', 'PARAM1', 'STATIC'),
       ('TEST2', 'HECARD', 'PURCHASE', 'PARAM2', 'STATIC');

SET FOREIGN_KEY_CHECKS = 1;