DAAS
====

Database as a service

DAAS is the layer that sits on top of GraphDB for storage . DAAS supports schemaless data storage using REST api.
It has security layer using OAuth 2.0


Backend DB setup in cluster mode

The OrientDB v1.6 is used to store data

1. https://github.com/orientechnologies/orientdb/wiki/Distributed-Configuration
2. https://github.com/orientechnologies/orientdb/wiki/Replication


## Usage




DaasClient : It is a java rest client for DAAS rest server apis

### Login:

	DaasClient daasClient = new DaasClient(host, port);
    boolean success = daasClient.login("accnName", "appName", "accnUser", "accnPassword");
    
### Save/update Entity

	daasClient.createEntity(entity);
    or
	daasClient.updateEntity(entity);
    
Entity passed should extend DAAS Entity class : com.bbytes.daas.domain.Entity   

    
    
    
    
    
    
    
    
    
    
    
    
    
    

