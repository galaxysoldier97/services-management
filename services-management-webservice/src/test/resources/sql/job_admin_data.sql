SET
FOREIGN_KEY_CHECKS = 0;

insert into job_configuration (id, days, enabled, operation, status, activity, category)
values  (1, 2, true, 'SERVICES_PURGE', 'DEACTIVATED', 'INTERNET', 'ACCESS'),
        (2, 1, false, 'SERVICES_PURGE', 'CANCELED', null, 'COMPONENT');

SET
FOREIGN_KEY_CHECKS = 1;