USE textan;

DROP TABLE AliasOccurrence;
DROP TABLE Alias;

DROP TABLE IsInRelation;

DROP TABLE JoinedObjects;

DROP TABLE RelationOccurrence;
DROP TABLE Relation;

DROP TABLE Object;
DROP TABLE ObjectType;
DROP TABLE RelationType;

DROP TABLE Document;

DROP TABLE GlobalVersion;
DROP TABLE Audit;

-- For remove user use (from superuser account):
DROP USER 'textan_user'@'localhost';
DROP USER 'textan_user'@'%';