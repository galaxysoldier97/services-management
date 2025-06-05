SET
FOREIGN_KEY_CHECKS = 0;

ALTER TABLE activation_code
    ALTER COLUMN id RESTART WITH 1;
ALTER TABLE import_error
    ALTER COLUMN id RESTART WITH 1;
ALTER TABLE import_history
    ALTER COLUMN id RESTART WITH 1;
ALTER TABLE job_configuration
    ALTER COLUMN id RESTART WITH 1;
ALTER TABLE provisioning_action
    ALTER COLUMN id RESTART WITH 1;
ALTER TABLE provisioning_product
    ALTER COLUMN id RESTART WITH 1;
ALTER TABLE provisioning_tag
    ALTER COLUMN id RESTART WITH 1;
ALTER TABLE service
    ALTER COLUMN id RESTART WITH 1;
ALTER TABLE technical_parameter
    ALTER COLUMN id RESTART WITH 1;

delete
from activation_code;
delete
from auditenversinfo;
delete
from import_error;
delete
from import_history;
delete
from job_configuration;
delete
from provisioning_action;
delete
from provisioning_action_parameter;
delete
from provisioning_product;
delete
from provisioning_tag;
delete
from service;
delete
from service_access;
delete
from service_access_aud;
delete
from service_activation;
delete
from service_aud;
delete
from service_component;
delete
from service_component_aud;
delete
from service_parameter;
delete
from service_tag;
delete
from tag_activation;
delete
from technical_parameter;

SET
FOREIGN_KEY_CHECKS = 1;