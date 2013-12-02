CREATE TABLE [dbo].[Object]
(
	id_object int PRIMARY KEY, 
	id_object_type int NOT NULL FOREIGN KEY
		REFERENCES ObjectType (id_object_type),
	data nvarchar
)
