CREATE TABLE [dbo].[AliasOccurrence]
(

	id_alias_occurence int PRIMARY KEY,
	id_alias int FOREIGN KEY
		REFERENCES Alias(id_alias), 
	id_document int FOREIGN KEY
		REFERENCES Document(id_document), 
	position int NOT NULL
)
