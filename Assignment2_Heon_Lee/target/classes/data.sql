insert into Professor (pname) values ('Professor');
insert into Student (sid, sname, exercises, assignment1, assignment2, midterm, final_Exam, final_Project) 
values ('000000001','Student1', 90.0, 90.0, 90.0, 90.0, 90.0, 90.0);
insert into Student (sid, sname, exercises, assignment1, assignment2, midterm, final_Exam, final_Project) 
values ('000000002','Student2', 80.0, 80.0, 80.0, 80.0, 80.0, 80.0);
insert into Student (sid, sname, exercises, assignment1, assignment2, midterm, final_Exam, final_Project) 
values ('000000003','Student3', 89.0, 89.0, 89.0, 89.0, 89.0, 89.0);
insert into Student (sid, sname, exercises, assignment1, assignment2, midterm, final_Exam, final_Project) 
values ('000000004','Student4', 88.0, 88.0, 88.0, 88.0, 88.0, 88.0);
insert into Student (sid, sname, exercises, assignment1, assignment2, midterm, final_Exam, final_Project) 
values ('000000005','Student5', 91.0, 91.0, 91.0, 91.0, 91.0, 91.0);
insert into Student (sid, sname, exercises, assignment1, assignment2, midterm, final_Exam, final_Project) 
values ('000000006','Student6', 94.0, 94.0, 94.0, 94.0, 94.0, 94.0);
insert into Student (sid, sname, exercises, assignment1, assignment2, midterm, final_Exam, final_Project) 
values ('000000007','Student7', 80.0, 80.0, 80.0, 80.0, 80.0, 80.0);
insert into Student (sid, sname, exercises, assignment1, assignment2, midterm, final_Exam, final_Project) 
values ('000000008','Student8', 100.0, 100.0, 100.0, 100.0, 100.0, 100.0);
insert into Student (sid, sname, exercises, assignment1, assignment2, midterm, final_Exam, final_Project) 
values ('000000009','Student9', 90.0, 80.0, 90.0, 90.0, 90.0, 90.0);
insert into Student (sid, sname, exercises, assignment1, assignment2, midterm, final_Exam, final_Project) 
values ('000000010','Student10', 89.0, 89.0, 89.0, 89.0, 89.0, 89.0);

/*
 * 
 * 
 * Professor user
 * 
 * Username: Professor
 * Password: 123
 * */
insert into User (username, encryptedpassword)
values ('Professor', '$2a$10$PrI5Gk9L.tSZiW9FXhTS8O8Mz9E97k2FZbFvGFFaSsiTUIl.TCrFu');

insert into User (username, encryptedpassword)
values ('Student1', '$2a$10$beopysGCcynEqcz774XjLu4YMLgtCDSFdOZfweAXSlMrDbMFEjIyC');

insert into User (username, encryptedpassword)
values ('Student2', '$2a$10$Tj/3ooV2B7mxs5UTO.fHYOff9pF5Yvqx4hkOvbEMaz2EHHijLkXHG');

insert into User (username, encryptedpassword)
values ('Student3', '$2a$10$R0bH6.6/pravxQEs22rke.Pszn8T.vE5XNSE9OfAKZOGAIXRxjIBa');

insert into User (username, encryptedpassword)
values ('Student4', '$2a$10$WGru56zG.9LpYKmKiV.b1O/yCDAfh3g/yFRqIa6qlqH4h3lesEa4q');

insert into User (username, encryptedpassword)
values ('Student5', '$2a$10$1yNe1ZNAbiURNTowC2lJO.Vf23QrebkzOmve.pura3A9jYoMAXhne');

insert into User (username, encryptedpassword)
values ('Student6', '$2a$10$ob7giENczWt9WgN.49o4NOu6mS6z5CnwHx6gF3lfo.z5W3yOqoQPu');

insert into User (username, encryptedpassword)
values ('Student7', '$2a$10$b4P/EYi5cpA70DIN1um/r.Wr06bPlEErLMmePyhj7p0kShxWGMM7e');

insert into User (username, encryptedpassword)
values ('Student8', '$2a$10$nr/JAmfeHTK.lLW7Pn21guLtDTQrbAKK6d6JgBa.WWdvrZVlydhee');

insert into User (username, encryptedpassword)
values ('Student9', '$2a$10$eqgptwKIHMMNg6KiexhcsuHbQntUSNbyUdSgjm4DQUVkGnHx7WE4a');

insert into User (username, encryptedpassword)
values ('Student10', '$2a$10$pGQhNjU4hx3RzKXFumYYhezZ0YDjc4jJknoVvpjnM.T1nFhO5Cs7O');

insert into Role (rolename) values ('ROLE_PROFESSOR');

insert into Role (rolename) values ('ROLE_STUDENT');

insert into user_roles (users_id, roles_id) values (1, 1);
insert into user_roles (users_id, roles_id) values (2, 2);
insert into user_roles (users_id, roles_id) values (3, 2);
insert into user_roles (users_id, roles_id) values (4, 2);
insert into user_roles (users_id, roles_id) values (5, 2);
insert into user_roles (users_id, roles_id) values (6, 2);
insert into user_roles (users_id, roles_id) values (7, 2);
insert into user_roles (users_id, roles_id) values (8, 2);
insert into user_roles (users_id, roles_id) values (9, 2);
insert into user_roles (users_id, roles_id) values (10, 2);