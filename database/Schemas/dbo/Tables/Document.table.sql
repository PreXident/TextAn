CREATE TABLE [dbo].[Document]
(
	id_document int PRIMARY KEY, 
	added date DEFAULT GETDATE(),
	processed date NULL,
	text nvarchar NOT NULL
)
