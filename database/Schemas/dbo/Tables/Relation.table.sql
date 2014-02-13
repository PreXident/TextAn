CREATE TABLE [dbo].[Relation]
(
        id_relation int PRIMARY KEY,
        id_relation_type int NOT NULL FOREIGN KEY
                REFERENCES RelationType (id_relation_type)
)
