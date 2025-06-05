# services-management changelog


## 2.28.0
* [MTOSS-2222] Code refacto + reactivate sonar
* [MTOSS-2174] Added Service Action column on the Provisioning Product Excel export, deprecated Provisioning Action Excel export
* [MTOSS-2218] Added tagCodes to ActivationCodeDTO


## 2.24.0
* [MTOSS-1893] handling addRequest transactions
* [MTOSS-1979] send data as JSON in messaging queues
* [MTOSS-1861] Better error handling around common-job/common-job-web
* [MTOSS-1833] No 404 error on get actions for tags
* [MTOSS-1802] Fix database on readme and deleteAllParams endpoint implementation
* [MTOSS-1755] Search Provisioning Tags by category and persistent
* [MTOSS-1826] Search job configurations by activity, category and status

## 2.23.0
* Search provisioning products
* Merge with dbmdl repository
* Remove TagActivationCodeDTO containing IDs, always returning full details
* [MTOSS-1673] Fix shared activation codes being wrongly removed
* [MTOSS-1673] Fixed change tags returning shared service_activation even when already existing, if existing service_tag is not in the request 
* Fix Job Config creation not returning ID

## 2.22.0
* [MTOSS-1621] Integration OpenApi 3
* [MTOSS-1620] Using jsonPath instead of json for better test validation

## 2.21.0
* auth-lib, common 1.1.31
* [MTOSS-1522] Added new ProvisioningImporter
* [MTOSS-1518] Optimized tests execution
* [MTOSS-1537] Redone data model with IdClass instead of Embedded notation
* [MTOSS-1463] Merge integration repository with application

## 2.20.0
* [MTOSS-1457] Importer asynchronous 
* [MTOSS-833] Added pre cleaning table functionality to importers

## 2.19.0
* [MTOSS-1347] Cleaning service audit 
* [MTOSS-834]  Added exporter for following entities: ProvisioningTag ,ActivationCode,ProvisioningAction,ProvisioningProduct
* [MTOSS-1346] Added search for serviceAccess and serviceComponent

## 2.18.0
* [MTOSS-1392] Fix OpenAPI doc around generics (forCodeGeneration = true)
* [MTOSS-1422] Fixed changeTags not removing service activations
* [MTOSS-1343] Added api getServiceAccessCrmIdIn
* [MTOSS-1385] Deactivate service component resets the crmServiceId
* [MTOSS-832] Remove foreign key constraint between service tags and activations
* [MTOSS-1373] ProvisioningTag importer set componentType to null
* [MTOSS-1351] Error messages review
* [MTOSS-831] Review of provisioning model entities
* [MTOSS-1315] Review error codes (avoid 500)
* [MTOSS-780] Force capacity on delete endpoints for provisioning entities
* [MTOSS-1277] Add componentType in provisioning tag and adapt API calls
* [MTOSS-1276] Switch component type from enum to String type in service component
* [MTOSS-1295] Parent 4.0.14 + TraceId Logging using Sleuth

## 2.17.0
* [MTOSS-1251] Increase tag/activation code to 255
* [MTOSS-864] Updated return value for ProvisioningProduct decompose method

## 2.18.0
* [MTOSS-1322] Handled parallel calls on techId creation

## 2.16.0
* [GI-788] Removed SUSPENDED to BARRED transition
* [GI-788] Unbarring now put service in previous status instead of ACTIVATED (+unbarring allowed on suspended service)
* [GI-788] Resum_oper now put service in previous status instead of ACTIVATED
* [GI-768] Added serviceCategory to crmServiceId, serviceActivity composite key
* [GI-732] Add service search filter for access crm service id

## 2.15.0
* Removed dependency on websockets
* [GI-719] Align liquibase with Entity schema
* [GI-717] Enable multiple fields updates in service update endpoint
* [GI-714] Better validation for update service
* [GI-707] Added value 'CHGE_OFFER' to provisioningProductActionRequest
* [GI-680] Add new service component Teams
* [GI-681] Fixed Service Parameters API PATCH/POST to make them work with multiple service parameters
* Migrate provisioning_tag.access_type to remove PREPAY value

## 2.14.0
* [GI-640] Add search capacities on tag/product for activation codes - [JIRA](https://jira.itsf.io/browse/GI-640)
* [GI-659] Added DEACTIVATED to valid statuses for service removal - [JIRA](https://jira.itsf.io/browse/GI-659)
* [GI-656] Removed reference to PREPAY value - [JIRA](https://jira.itsf.io/browse/GI-656)
* [GI-643] Synchronized TechID generation - [JIRA](https://jira.itsf.io/browse/GI-643)
* [GI-642] Fixed FTTH service creating failing in UNM synchronization - [JIRA](https://jira.itsf.io/browse/GI-642)
* [GI-596] Renamed rangeId into mainRangeId - [JIRA](https://jira.itsf.io/browse/GI-596)

## 2.13.0
* [GI-600] Update API service parameter - [JIRA](https://jira.itsf.io/browse/GI-600)
* [GI-629] Using ubi8-derived base image with fontconfig - [JIRA](https://jira.itsf.io/browse/GI-629)
* [GI-599] Update TechnicalParameter uniqueness constraint - [JIRA](https://jira.itsf.io/browse/GI-599)
* [GI-594] Push artifacts to Nexus, Spring Boot 2.6.7 - [JIRA](https://jira.itsf.io/browse/GI-594)
* [GI-622] Fixed search service by serviceId - [JIRA](https://jira.itsf.io/browse/GI-622)
* [GI-603] Fixed TrunkSIP techId incorrectly managed - [JIRA](https://jira.itsf.io/browse/GI-603)
* [GI-601] Increase tag code to 32 characters - [JIRA](https://jira.itsf.io/browse/GI-601)
* [GI-595] Increase activCode field to 32 characters - [JIRA](https://jira.itsf.io/browse/GI-595)
* Fixed issues with changelogs 080 and 081
* [GI-589] Added CAS to ComponentType (+ Common 1.1.13) - [JIRA](https://jira.itsf.io/browse/GI-589)
* [GI-585] Search service component with componentType - [JIRA](https://jira.itsf.io/browse/GI-585)
* [GI-513] Review UNM-SVC configuration - [JIRA](https://jira.itsf.io/browse/GI-513)
* [GI-563] Add CHGE_MSISDN to ProvisioningProductActionRequest - [JIRA](https://jira.itsf.io/browse/GI-563)
* [GI-538] Spring Boot 2.6.6 + Springfox 3.0.0 - [JIRA](https://jira.itsf.io/browse/GI-538)

## 2.12.0
* [GI-473] Idempotency on update state endpoints - [JIRA](https://jira.itsf.io/browse/GI-473)
* [GI-540] Better error handling for service update number/range/crmRef - [JIRA](https://jira.itsf.io/browse/GI-540)
* [GI-469] Remove devtools dependency - [JIRA](https://jira.itsf.io/browse/GI-469)
* [GI-474] Increase crmServiceId size - [JIRA](https://jira.itsf.io/browse/GI-474)
* [GOSS-1968] Update swagger config - [JIRA](https://jira.itsf.io/browse/GOSS-1968)
* [GOSS-1346] Used commons-security instead of common-keycloak - [JIRA](https://jira.itsf.io/browse/GOSS-1346)
* [GOSS-1359] Reset ontId on Service deactivation - [JIRA](https://jira.itsf.io/browse/GOSS-1359)

## 2.11.0
* [GOSS-1351] Better messages for update state errors - [JIRA](https://jira.itsf.io/browse/GOSS-1351)
* [GOSS-1353] add new paths in enumController - [JIRA](https://jira.itsf.io/browse/GOSS-1353)
* [GOSS-1008] Aligned entities with liquidbase - [JIRA](https://jira.itsf.io/browse/GOSS-1008)
* [GISS-1315] Updated value accessType in serviceAccess entity and componentType in serviceComponent entity - [JIRA](https://jira.itsf.io/browse/GOSS-1315)
* [GOSS-1295] Removed sql-connector dependency - [JIRA](https://jira.itsf.io/browse/GOSS-1295)
* [GOSS-928] Added Swagger doc yaml -[JIRA](https://jira.itsf.io/browse/GOSS-928)
* [GOSS-1302] Added value OPI to network_component on ActivationCode entity -[JIRA](https://jira.itsf.io/browse/GOSS-1302)

## 2.10.0
* [GOSS-1260] Added JsonIgnoresProperty to dtos - [JIRA](https://jira.itsf.io/browse/GOSS-1260)
* [GOSS-1228] Keycloak multi-realm support - [JIRA](https://jira.itsf.io/browse/GOSS-1228)
* [GOSS-1245] Added MOBILE network, endpoint to retrieve them all - [JIRA](https://jira.itsf.io/browse/GOSS-1245)
* [GOSS-902] Keycloak authorization model refactoring - [JIRA](https://jira.itsf.io/browse/GOSS-902)

## 2.8.0
* [GOSS-534] Improve tests on importers - [JIRA](https://jira.itsf.io/browse/GOSS-534)
* Fixed service component techId generation when componentType is null
* [GOSS-964] Modification deleteService added optional "forced" for deleting the serviceComponents with the service - [JIRA](https://jira.itsf.io/browse/GOSS-964)
* [GOSS-922] Common Importer API - [JIRA](https://jira.itsf.io/browse/GOSS-922)
* Added techId to service search
* Added changeTechId/removeTechId actions on service

## 2.7.0
* Fix multiple children for same parent service
* [GOSS-855] OntID Management on service access - [JIRA](https://jira.itsf.io/browse/GOSS-855)
* [GOSS-859] Added SDP to NetworkComponent - [JIRA](https://jira.itsf.io/browse/GOSS-859)
* [GOSS-413] Change tags regression tests & better request validation - [JIRA](https://jira.itsf.io/browse/GOSS-413)
* [GOSS-708] Aligned datasource driver with guidelines - [JIRA](https://jira.itsf.io/browse/GOSS-708)
* [GOSS-799] Improved error handling when trying to set already existing crmServiceId on service - [JIRA](https://jira.itsf.io/browse/GOSS-799)

## 2.6.0
* [GOSS-709] Created -report module to aggregate jacoco coverage data - [JIRA](https://jira.itsf.io/browse/GOSS-709)
* Modified base image not to use root in the container anymore
* [GOSS-565] Use common Audit entity, remove unused tables, fix audit on AC and TP - [JIRA](https://jira.itsf.io/browse/GOSS-565)
* [GOSS-461] Added entity ServiceParameter - [JIRA](https://jira.itsf.io/browse/GOSS-461)
* [GOSS-462] New rules of techId calculation - [JIRA](https://jira.itsf.io/browse/GOSS-462)
* [GOSS-550] Transform techId to String and merged it into service - [JIRA](https://jira.itsf.io/browse/GOSS-550)
* [GOSS-406] Integration tests, externalized clock configuration - [JIRA](https://jira.itsf.io/browse/GOSS-406)

## 2.5.0
* Fixed rollbackDeactivation error handling in case without enough revisions
* [GOSS-264] Removed Dockerfile to use jib plugin - [JIRA](https://jira.itsf.io/browse/GOSS-264)
* [GOSS-43] Added JSON support on logs - [JIRA](https://jira.itsf.io/browse/GOSS-43)
* [GOSS-443] Added STREAMWIDE to NetworkComponent - [JIRA](https://jira.itsf.io/browse/GOSS-443)
* Managed Unbarring case where activateRequest should let Service as Barred
* Allowed Barred as starting status for BARRING addRequest (barring on barring scenario for Epic)
* Added upload to Nexus part in Jenkinsfile
* [GOSS-231] Fix API provisioningActionsParameter - [JIRA](https://jira.itsf.io/browse/GOSS-231)

## 2.4.0
* [GOSS-231] Add new API provisioningActions,provisioningProducts - [JIRA](https://jira.itsf.io/browse/GOSS-231)
* [GOSS-90] Updated environment variables as expected in the guideline - [JIRA](https://jira.itsf.io/browse/GOSS-90) - [Confluence](https://confluence.itsf.io/display/GUIDES/Environment+Variables)

## 2.3.0
- [GOSS-210] Fix importer behavior when another import is in progress - [JIRA](https://jira.itsf.io/browse/GOSS-210)
- [GOSS-132] Add missing API provisioningActions,provisioningActionParameters,provisioningProducts - [JIRA](https://jira.itsf.io/browse/GOSS-132)
- [GOSS-148] Add template variables value for pool-size and minimum-idle properties - [JIRA](https://jira.itsf.io/browse/GOSS-148)

## 2.2.0 - 19/02/2021
