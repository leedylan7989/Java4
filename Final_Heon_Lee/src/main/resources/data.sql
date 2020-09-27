/*
 * Trainer, 123
 * 
 * Client, 123
 */
insert into User (username, encryptedpassword)
values ('Trainer', '$2a$10$PrI5Gk9L.tSZiW9FXhTS8O8Mz9E97k2FZbFvGFFaSsiTUIl.TCrFu');

insert into User (username, encryptedpassword)
values ('Client', '$2a$10$PrI5Gk9L.tSZiW9FXhTS8O8Mz9E97k2FZbFvGFFaSsiTUIl.TCrFu');

insert into Role (rolename) values ('ROLE_TRAINER');

insert into Role (rolename) values ('ROLE_CLIENT');

insert into user_roles (users_id, roles_id) values (1, 1);
insert into user_roles (users_id, roles_id) values (2, 2);

insert into TRAINER (name) values ('Trainer1');
insert into Appointment (client, month, day, start, message, trainer) values ('Client1', 4, 9, 10, 'Leg Day', 'Trainer1');