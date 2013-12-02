CREATE TABLE [dbo].[Alias]
(
	id_alias int PRIMARY KEY, 
	id_object int NOT NULL FOREIGN KEY
		REFERENCES Object(id_object),
	alias nvarchar NOT NULL
)
