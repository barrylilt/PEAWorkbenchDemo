CREATE TABLE LND.WB_AUTH_ROLE (RoleID INTEGER, RoleName VARCHAR(50));
CREATE TABLE LND.WB_USER_ROLE (UserID INTEGER, RoleID INTEGER);

ALTER TABLE LND.WB_USER_PROFILE ADD Password VARCHAR(100);
ALTER TABLE LND.WB_USER_PROFILE ADD IsActive VARCHAR(1);
ALTER TABLE LND.WB_USER_PROFILE ADD Theme VARCHAR(100);
ALTER TABLE LND.WB_USER_PROFILE ADD DateFormat VARCHAR(100);
ALTER TABLE LND.WB_USER_PROFILE ADD Language varchar(100);

insert into LND.WB_AUTH_ROLE values (1, 'Default');
insert into LND.WB_AUTH_ROLE values (2, 'Admin');
