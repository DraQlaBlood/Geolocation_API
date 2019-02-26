# Mars_Backend_Utils_API
Location API 
JAVA 8 - Spring Boot - Spring Cloud (Feign)

This API purpose is to get users inofrmation like (remote IP address, city , country...) when they send 
a request.

End point : localhost:4001/location

POJO Model
  LocationDetails{
    city	(type string)
    country	(type string)
    ipAddress	(type string)
    latitude	(type string)
    longitude	(type string)
  }
