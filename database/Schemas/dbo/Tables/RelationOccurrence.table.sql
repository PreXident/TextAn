CREATE TABLE [dbo].[RelationOccurrence]
(

        id_relation_occurence int PRIMARY KEY,
        id_relation int NOT NULL FOREIGN KEY
                REFERENCES Relation(id_relation),
        id_document int NOT NULL FOREIGN KEY
                REFERENCES Document(id_document),
        position int NOT NULL,
        anchor nvarchar
)
