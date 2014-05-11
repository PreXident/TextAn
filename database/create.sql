CREATE DATABASE IF NOT EXISTS textan 
  DEFAULT CHARACTER SET utf8
  DEFAULT COLLATE utf8_general_ci;
USE textan;


CREATE TABLE Document (
	id_document int PRIMARY KEY AUTO_INCREMENT, 
	added datetime,
	processed datetime NULL,
	text text CHARSET utf8 NOT NULL,
	version int DEFAULT 0 NOT NULL
);

CREATE TABLE RelationType(
  id_relation_type INT PRIMARY KEY AUTO_INCREMENT,
  name NVARCHAR (255) UNIQUE
);

CREATE TABLE ObjectType (
	id_object_type int PRIMARY KEY AUTO_INCREMENT, 
	name NVARCHAR (255) UNIQUE
);

CREATE TABLE Object (
	id_object int PRIMARY KEY AUTO_INCREMENT, 
	id_object_type int NOT NULL,
	data NVARCHAR (255),
  CONSTRAINT FK_OBJECT_TO_TYPE FOREIGN KEY (id_object_type)
		REFERENCES ObjectType(id_object_type)
);

CREATE TABLE Alias (
	id_alias int PRIMARY KEY AUTO_INCREMENT, 
	id_object int NOT NULL,
	alias NVARCHAR(255) NOT NULL,
  CONSTRAINT FK_ALIAS_ID_OBJECT
   FOREIGN KEY (id_object)
		REFERENCES Object(id_object)
);


CREATE TABLE AliasOccurrence (
	id_alias_occurence int PRIMARY KEY AUTO_INCREMENT,
	id_alias int NOT NULL, 
	id_document int NOT NULL, 
	position int NOT NULL,
  CONSTRAINT FK_ALIASOCCURENCE_IDALIAS
   FOREIGN KEY (id_alias)
		REFERENCES Alias(id_alias),
  CONSTRAINT FK_ALIASOCCURENCE_IDDOCUMENT
   FOREIGN KEY (id_document)
		REFERENCES Document(id_document)
);

CREATE TABLE Relation
(
        id_relation int PRIMARY KEY AUTO_INCREMENT,
        id_relation_type int NOT NULL,
        CONSTRAINT FK_RELATION_RELTYPE FOREIGN KEY (id_relation_type)
                REFERENCES RelationType (id_relation_type)
);

CREATE TABLE IsInRelation
(
        id_is_in_relation int PRIMARY KEY AUTO_INCREMENT,
        id_relation int NOT NULL,
        CONSTRAINT FK_ISINRELATION_RELATION
          FOREIGN KEY (id_relation)
                REFERENCES Relation(id_relation),
        id_object int NOT NULL,
        CONSTRAINT FK_ISINRELATION_OBJECT
          FOREIGN KEY (id_object)
                REFERENCES Object(id_object),
        order_in_relation int NOT NULL
);

CREATE TABLE JoinedObjects
(
        id_new_object int PRIMARY KEY,
          CONSTRAINT FK_PK_JOINEDOBJECTS_ID
            FOREIGN KEY (id_new_object)
                  REFERENCES Object(id_object),
        id_old_object1 int NOT NULL,
          CONSTRAINT FK_JOINEDOBJECTS_OLDOBJ1
            FOREIGN KEY (id_old_object1)
                  REFERENCES Object(id_object),
        id_old_object2 int NOT NULL,
          CONSTRAINT FK_JOINEDOBJECTS_OLDOBJ2
            FOREIGN KEY (id_old_object2)
                  REFERENCES Object(id_object),
        from_date date,
        to_date date
);

-- TODO add constrant: joined objects have to have the same type


CREATE TABLE RelationOccurrence
(

        id_relation_occurence int PRIMARY KEY AUTO_INCREMENT,
        id_relation int NOT NULL,
        CONSTRAINT FK_RELOCCURENCE_RELATION
          FOREIGN KEY (id_relation)
            REFERENCES Relation(id_relation),
        id_document int NOT NULL,
        CONSTRAINT FK_RELOCCURENCE_DOCUMENT
          FOREIGN KEY (id_document)
                REFERENCES Document(id_document),
        position int NOT NULL,
        anchor NVARCHAR(255)
);


-- Create user for textan (maybe MySql specific)

-- user used to connect from localhost
-- CREATE USER 'textan_user'@'localhost' IDENTIFIED BY 'textanpassword';
GRANT SELECT, INSERT, UPDATE, DELETE, EXECUTE, SHOW VIEW 
  ON textan.* TO 'textan_user'@'localhost' IDENTIFIED BY 'textanpassword';

-- user used to connect from any host
-- CREATE USER 'textan_user'@'%' IDENTIFIED BY 'textanpassword';
GRANT SELECT, INSERT, UPDATE, DELETE, EXECUTE, SHOW VIEW 
  ON textan.* TO 'textan_user'@'%' IDENTIFIED BY 'textanpassword';

FLUSH PRIVILEGES;

-- For remove user use (from superuser account):
-- DROP USER 'textan_user'@'localhost';
-- DROP USER 'textan_user'@'%';
