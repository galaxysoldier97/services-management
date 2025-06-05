SET
FOREIGN_KEY_CHECKS = 0;

insert into activation_code (id, activ_code, description, nature, network_component)
values  (1, 'HPX_126', 'BroadBand Mobile ', 'PROFILE', 'SPG'),
        (2, 'HOD_72', 'Service Data GPRS', 'OPTION', 'SPG');

insert into provisioning_tag (id, tag_code, description, activity, category, access_type, nature, persistent)
values  (1, 'HAXACC', 'Mobile Access ', 'MOBILE', 'ACCESS', 'BBHB', 'A', true),
        (2, 'HPXBRDBAND1', 'Broadband In a Box - Profile 1', 'INTERNET', 'ACCESS', 'MOBILE', 'P', true);

insert into tag_activation (tag_value, tag_code, activ_code)
values (367001600, 'HAXACC', 'HPX_126'),
       (367001601, 'HPXBRDBAND1', 'HOD_72');

SET FOREIGN_KEY_CHECKS = 1;