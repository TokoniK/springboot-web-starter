# springboot-web-starter
Spring Boot Web / RestAPI CRUD Starter including nifty AbstractService and AbstractController

Reusable structure to fast track spring boot Rest API / Web projects
AbstractController provides CRUD endpoints for configured entities together with OpenAPI Documentation.
OpenAPI docs are accessible at **<host>:<port>/<context-root>/swagger-ui/index.html**

Key Notes:
- Service classes should extend AbstractService as seen in example services\TestService
- Controller classes should extend AbstractController as seen in example services\TestController
