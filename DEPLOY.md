# Technical Repository - Services Management (TECREP-SVC) 

[[_TOC_]]

## Database
SVC is designed to work with MariaDB 10.7+.
Earlier versions of MySQL and MariaDB could work but are not advised.

## Keycloak
- Create a client named `services-management`.
- Set `Authorization Enabled` to `ON`.
- Create roles: SERVICE_DELETE, SERVICE_READ, SERVICE_WRITE
- Give these roles to users, groups, clients or roles that need access to the service.
- An authorization model configuration file is available [here](services-management-authz-config.json). Import it in `services-management` > `Authorization` > `Settings` > `Import`.
- Remove resource `Default Resource` and policy `Default policy`.

## Environment variables (data model migration)
On the container, put these environment variables and replace the entry point with the command `liquibase update`

| Environment variable name               | Description | Format | Example / Default Value                  | Required |
|:----------------------------------------|-------------|--------|:-----------------------------------------|:---------|
| LIQUIBASE_COMMAND_URL                   |             | string | `jdbc:mariadb://localhost:3306/svcmgmt`  | Y        |
| LIQUIBASE_COMMAND_USERNAME (secret)     |             | string | `admin`                                  | Y        |
| LIQUIBASE_COMMAND_PASSWORD (secret)     |             | string | `password`                               | Y        |
| LIQUIBASE_COMMAND_CHANGELOG_FILE        |             | string | `db/changelog/db.changelog-master.xml`   | Y        |
| LIQUIBASE_SEARCH_PATH                   |             | string | `/liquibase/changelog`                   | Y        |

## Environment Variables (main application)
### Database connection
| Environment variable name                | Description                                                                 | Format | Example / Default Value                      | Required |
|:-----------------------------------------|-----------------------------------------------------------------------------|--------|:---------------------------------------------|:---------|
| DATASOURCE_URL                           |                                                                             | string | `jdbc:mariadb://localhost:3306/svcmgmt`      | Y        |
| DATASOURCE_USERNAME (secret)             |                                                                             | string | `admin`                                      | Y        |
| DATASOURCE_PASSWORD (secret)             |                                                                             | string | `password`                                   | Y        |
| SPRING_DATASOURCE_DRIVERCLASSNAME        |                                                                             | string | `org.mariadb.jdbc.Driver`                    | N        |
| SPRING_DATASOURCE_HIKARI_MAXIMUMPOOLSIZE |                                                                             | number | `10`                                         | N        |
| SPRING_DATASOURCE_HIKARI_MINIMUMIDLE     |                                                                             | number | `3`                                          | N        |

### RabbitMQ
| Environment variable name                | Description                                                                 | Format | Example / Default Value                      | Required |
|:-----------------------------------------|-----------------------------------------------------------------------------|--------|:---------------------------------------------|:---------|
| RABBITMQ_HOST                            |                                                                             | string | `localhost`                                  | Y        |
| RABBITMQ_PORT                            |                                                                             | number | `5672`                                       | Y        |
| RABBITMQ_USERNAME (secret)               |                                                                             | string | `guest`                                      | Y        |
| RABBITMQ_PASSWORD (secret)               |                                                                             | string | `guestpwd`                                   | Y        |
| RABBITMQ_VIRTUAL_HOST                    |                                                                             | string | `/`                                          | N        |

### Integration with Keycloak
#### Mono-realm
If `spring.profiles.active` does not contain `keycloak-multitenant`

| Environment variable name                | Description                                                                 | Format | Example / Default Value       | Required |
|:-----------------------------------------|-----------------------------------------------------------------------------|--------|:------------------------------|:---------|
| KEYCLOAK_AUTH_SERVER_URL                 |                                                                             | string | `http://localhost:5555/auth`  | Y        |
| KEYCLOAK_REALM                           |                                                                             | string | `monaco-telecom-dev`          | Y        |
| KEYCLOAK_RESOURCE (secret)               |                                                                             | string | `services-management`         | Y        |
| KEYCLOAK_CREDENTIALS_SECRET (secret)     |                                                                             | string | `0123456789`                  | Y        |

#### Multi-realm
If `spring.profiles.active` contains `keycloak-multitenant`

| Environment variable name                | Description                                                                  | Format | Example / Default Value       | Required |
|:-----------------------------------------|------------------------------------------------------------------------------|--------|:------------------------------|:---------|
| KEYCLOAK_0_AUTH_SERVER_URL               |                                                                              | string | `http://localhost:5555/auth`  | Y        |
| KEYCLOAK_0_REALM                         |                                                                              | string | `monaco-telecom-dev`          | Y        |
| KEYCLOAK_0_RESOURCE (secret)             |                                                                              | string | `services-management`         | Y        |
| KEYCLOAK_0_CREDENTIALS_SECRET (secret)   |                                                                              | string | `0123456789`                  | Y        |
| KEYCLOAK_1_AUTH_SERVER_URL               |                                                                              | string | `http://localhost:5556/auth`  | Y        |
| KEYCLOAK_1_REALM                         |                                                                              | string | `epic-ci`                     | Y        |
| KEYCLOAK_1_RESOURCE (secret)             |                                                                              | string | `services-management`         | Y        |
| KEYCLOAK_1_CREDENTIALS_SECRET (secret)   |                                                                              | string | `9876543210`                  | Y        |

### Miscellaneous
| Environment variable name                | Description                                                                 | Format | Example / Default Value                      | Required |
|:-----------------------------------------|-----------------------------------------------------------------------------|--------|:---------------------------------------------|:---------|
| SERVER_PORT                              |                                                                             | number | `8080`                                       | N        |
| UNM_SYNC                                 | If true, services updates will be notified to UNM through RabbitMQ          | bool   | `false`                                      | N        |
| PLMN_CODE                                | PLMN code used in techId generation for FREEDHOME services                  | number | `21210`                                      | N        |