-- SQL statements which are executed at application startup if hibernate.hbm2ddl.auto is 'create' or 'create-drop'

-- all sources
insert into source (id, url, description) values  (1, '/images', 'Standard');

-- gametypes
INSERT INTO gametype (id, description, label, name, players, roundduration, rounds, workflow) VALUES (1, 'Google Image Labeler in Seam', 'Image Labeler', 'imageLabeler', 2, 30, 3, 'gameImageLabeler');

-- all roles
INSERT INTO role (id, role, rolename) VALUES (1, 'admin', 'Admin');
INSERT INTO role (id, role, rolename) VALUES (2, 'player', 'Player');
INSERT INTO role (id, role, rolename) VALUES (3, 'creator', 'Creator');

-- set next id
insert into hibernate_sequences (sequence_name, sequence_next_hi_value) values ('AuthenticationToken', ( select max(tokenid)+1 from authenticationtoken ));
insert into hibernate_sequences (sequence_name, sequence_next_hi_value) values ('GameRound',           ( select max(id)+1 from gameround ));
insert into hibernate_sequences (sequence_name, sequence_next_hi_value) values ('GameSession',         ( select max(id)+1 from gamesession ));
insert into hibernate_sequences (sequence_name, sequence_next_hi_value) values ('GameType',            ( select max(id)+1 from gametype ));
insert into hibernate_sequences (sequence_name, sequence_next_hi_value) values ('Highscore',           ( select max(id)+1 from highscore ));
insert into hibernate_sequences (sequence_name, sequence_next_hi_value) values ('Person',              ( select max(id)+1 from person ));
insert into hibernate_sequences (sequence_name, sequence_next_hi_value) values ('Resource',            ( select max(id)+1 from resource ));
insert into hibernate_sequences (sequence_name, sequence_next_hi_value) values ('ResourceDescription', ( select max(id)+1 from resourcedescription ));
insert into hibernate_sequences (sequence_name, sequence_next_hi_value) values ('Role',                ( select max(id)+1 from role ));
insert into hibernate_sequences (sequence_name, sequence_next_hi_value) values ('Source',              ( select max(id)+1 from source ));
insert into hibernate_sequences (sequence_name, sequence_next_hi_value) values ('Tag',                 ( select max(id)+1 from tag ));
insert into hibernate_sequences (sequence_name, sequence_next_hi_value) values ('Tagging',             ( select max(id)+1 from tagging ));
insert into hibernate_sequences (sequence_name, sequence_next_hi_value) values ('TagFrequency',        ( select max(id)+1 from tagfrequency ));
update hibernate_sequences set sequence_next_hi_value='1' where sequence_next_hi_value is null;
