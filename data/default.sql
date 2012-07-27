
-- gametypes
INSERT INTO gametype (id, description, label, name, players, roundduration, rounds, workflow) VALUES (1, 'Google Image Labeler in Seam', 'Image Labeler', 'imageLabeler', 2, 30, 3, 'gameImageLabeler');

-- all roles
INSERT INTO role (id, role, rolename) VALUES (1, 'admin', 'Admin');
INSERT INTO role (id, role, rolename) VALUES (2, 'player', 'Player');
INSERT INTO role (id, role, rolename) VALUES (3, 'creator', 'Creator');
