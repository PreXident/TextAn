CREATE TABLE [dbo].[JoinedObjects]
(
        id_new_object int PRIMARY KEY,
        id_old_object1 int NOT NULL FOREIGN KEY
                REFERENCES Object(id_object),
        id_old_object2 int NOT NULL FOREIGN KEY
                REFERENCES Object(id_object),
        from_date date DEFAULT GETDATE(),
        to_date date
)

-- TODO add constrant: joined objects have to have the same type
