-- Create the default  user fortextan (maybe MySql specific)

-- user used to connect from localhost
-- CREATE USER 'textan_user'@'localhost' IDENTIFIED BY 'textanpassword';
GRANT SELECT, INSERT, UPDATE, DELETE, EXECUTE, SHOW VIEW
  ON textan.* TO 'textan_user'@'localhost' IDENTIFIED BY 'textanpassword';

-- user used to connect from any host
-- CREATE USER 'textan_user'@'%' IDENTIFIED BY 'textanpassword';
GRANT SELECT, INSERT, UPDATE, DELETE, EXECUTE, SHOW VIEW
  ON textan.* TO 'textan_user'@'%' IDENTIFIED BY 'textanpassword';

FLUSH PRIVILEGES;