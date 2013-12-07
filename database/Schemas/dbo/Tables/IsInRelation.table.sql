CREATE TABLE [dbo].[IsInRelation]
(

        id_is_in_relation int PRIMARY KEY,
        id_relation int NOT NULL FOREIGN KEY
                REFERENCES Relation(id_relation),
        id_object int NOT NULL FOREIGN KEY
                REFERENCES Object(id_object),
        order_in_relation int NOT NULL
)
