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

	entityFromDB = daasClient.createEntity(entity);
    or
	updatedEntityFromDB = daasClient.updateEntity(entity);
    
Entity passed should extend DAAS Entity class : 'com.bbytes.daas.domain.Entity'  

### Delete Entity

	String status = daasClient.deleteEntity(entityToBeDeleted);
    
Entity passed should extend DAAS Entity class : 'com.bbytes.daas.domain.Entity' 
    
### Relate Entities : 
For eg:  Entity A ----relation-name----> Entity B

	boolean status = daasClient.addRelation(A, B, "relation-name");
    
    
### Delete Relation between Entities : 
For eg: To remove only the relation  Entity A ----relation-name----> Entity B 

    boolean status = daasClient.removeRelation(A, B, "relation-name");
   
### Get right side entities in a relation :   
For eg: To get all 'B' entities with given relation name to A   Entity A ----relation-name----> Entity B 

	List<T extends Entity> rightSideRelatedEntityList = daasClient.getRightSideRelatedEntities(A, "relation-name",
				B.class);
                
### Get left side entities in a relation :   
For eg: To get all 'A' entities with given relation name to B   Entity A ----relation-name----> Entity B 

	List<T extends Entity> rightSideRelatedEntityList = daasClient.getLeftSideRelatedEntities(B, "relation-name",
				A.class);
    
## Query

### Get count of a entity type : 
To get the count of entity type A
	Long size = daasClient.getEntitySize(A.class);
    
    
    
    

