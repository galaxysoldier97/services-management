SET
FOREIGN_KEY_CHECKS = 0;

insert into provisioning_tag (id, tag_code, description, activity, category, access_type, nature, persistent)
values  (1, 'HAXACC', 'Mobile Access ', 'MOBILE', 'ACCESS', 'BBHB', 'A', true),
        (2, 'HPXBRDBAND1', 'Broadband In a Box - Profile 1', 'MOBILE', 'ACCESS', 'BBHB', 'P', true),
        (3, 'HECARD', 'Sim Card', 'MOBILE', 'ACCESS', 'BBHB', 'E', true),
        (4, 'HODATA1', 'Data Option 1', 'INTERNET', 'COMPONENT', 'BBHB', 'O', true),
        (5, 'HUCCHGE', 'Change SIM Card', 'MOBILE', 'ACCESS', 'MOBILE', 'U', false);

insert into provisioning_action(id, tag_code, tag_action, service_action)
values (1, 'HAXACC', 'PURCHASE', null),
       (2, 'HAXACC', 'CANCEL', null),
       (3, 'HECARD', 'PURCHASE', 'ACTIVATION');

insert into provisioning_product (id, product_code, action_request, tag_code, tag_action)
values (1, 'PAY_AS_YOU_GO_1', 'ACTIVATION', 'HAXACC', 'PURCHASE');

insert into provisioning_action_parameter (parameter_value, tag_code, tag_action, parameter_code, parameter_type)
values ('TEST', 'HAXACC', 'PURCHASE', 'PARAM1', 'STATIC');

insert into technical_parameter (id, description, parameter_code, parameter_type)
values (1, 'Technical Parameter 1', 'PARAM1', 'STATIC'),
       (2, 'Technical Parameter 2', 'PARAM2', 'STATIC');

SET FOREIGN_KEY_CHECKS = 1;