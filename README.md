# TecRep - Services Management

TecRep (Technical Repository) Services Management is a microservice managing services, the representation of the client provisioning status.

More information [here](https://confluence.itsf.io/display/ION/Tecrep+Services+Management)

# Sonarqube

Information about code quality can be found on [this related sonarqube link](https://sonarqube.steelhome.internal/dashboard?id=services-management)

[![Bugs](https://sonarqube.steelhome.internal/api/project_badges/measure?project=services-management&metric=bugs)](https://sonarqube.steelhome.internal/dashboard?id=services-management)

[![Security](https://sonarqube.steelhome.internal/api/project_badges/measure?project=services-management&metric=security_rating)](https://sonarqube.steelhome.internal/dashboard?id=services-management)

[![Code Smells](https://sonarqube.steelhome.internal/api/project_badges/measure?project=services-management&metric=code_smells)](https://sonarqube.steelhome.internal/dashboard?id=services-management)

[![Coverage](https://sonarqube.steelhome.internal/api/project_badges/measure?project=services-management&metric=coverage)](https://sonarqube.steelhome.internal/dashboard?id=services-management)

[![ncloc](https://sonarqube.steelhome.internal/api/project_badges/measure?project=services-management&metric=ncloc)](https://sonarqube.steelhome.internal/dashboard?id=services-management)

[![Vulnerabilities](https://sonarqube.steelhome.internal/api/project_badges/measure?project=services-management&metric=vulnerabilities)](https://sonarqube.steelhome.internal/dashboard?id=services-management)

# Liquibase

To run liquibase locally, execute this command from equipments-management-webservice module:
```
mvn liquibase:update -Dliquibase.url=jdbc:mariadb://localhost:3306/svcmgmt -Dliquibase.changeLogFile=db/changelog/db.changelog-master.xml -Dliquibase.password=rootpwd -Dliquibase.username=root
```

## Logging to a custom folder

When starting the service you can enable file logging and choose where the log
files are written. Activate the `log-to-file` profile and set
`BASE_LOG_FOLDER` to the desired directory. If this property is not defined the
application uses a folder named `mislogs` under the current working directory:

```
java -DBASE_LOG_FOLDER=/path/to/my/logs \
     -Dspring.profiles.active=log-to-file \
     -jar services-management-webservice.jar
```

Logs will be stored under `/path/to/my/logs/<application name>/`.
If `BASE_LOG_FOLDER` is not provided, logs default to `mislogs/<application name>/`.
