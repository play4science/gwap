-- SQL statements which are executed at application startup if hibernate.hbm2ddl.auto is 'create' or 'create-drop'

-- all sources
insert into source (id, url, description) values  (1, '/images', 'Standard');

-- gametypes
INSERT INTO gametype (id, name, label, description, rounds, players, roundduration, platform, workflow, enabled) VALUES (1, 'imageLabeler', 'Image Labeler', 'Google Image Labeler in Seam', 5, 2, 60, 'artigo', 'gameImageLabeler', true);
INSERT INTO gametype (id, name, label, description, rounds, players, roundduration, platform, workflow, enabled) VALUES (2, 'gwapGameMemory', 'Memory', 'An image labelling game similar to Memory', 2, 2, 90, 'artigo', '', true);
INSERT INTO gametype (id, name, label, description, rounds, players, roundduration, platform, workflow, enabled) VALUES (3, 'gwapGameTest', 'Test', 'A simple test scenario', 1, 2, 120, NULL, '', true);
INSERT INTO gametype (id, name, label, description, rounds, players, roundduration, platform, workflow, enabled) VALUES (4, 'mitAssociate', 'Associate', 'Associate statements to their region', 1, 1, 30, 'metropolitalia', NULL, true);
INSERT INTO gametype (id, name, label, description, rounds, players, roundduration, platform, workflow, enabled) VALUES (5, 'gwapGameMemoryTurn', 'Memory', 'An image labelling game similar to Memory, turn-based mode', 2, 2, 0, 'artigo', NULL, true);
INSERT INTO gametype (id, name, label, description, rounds, players, roundduration, platform, workflow, enabled) VALUES (6, 'gwapGameMemoryCluster', 'Memory', 'An image labelling game similar to Memory, clustering mode', 2, 2, 0, NULL, NULL, true);
INSERT INTO gametype (id, name, label, description, rounds, players, roundduration, platform, workflow, enabled) VALUES (7, 'mitStatementLabeler', 'StatementLabeler', '', 3, 1, 60, 'metropolitalia', NULL, true);
INSERT INTO gametype (id, name, label, description, rounds, players, roundduration, platform, workflow, enabled) VALUES (9, 'mitAccenti', 'accenti urbani', 'Associate audio resources to their region', 10, 1, NULL, 'accentiurbani', NULL, true);
INSERT INTO gametype (id, name, label, description, rounds, players, roundduration, platform, workflow, enabled) VALUES (10, 'mitRecognize', 'MetropolItalia Recognize', 'Recognize statements as true or false', 3, 1, NULL, 'metropolitalia', NULL, true);
INSERT INTO gametype (id, name, label, description, rounds, players, roundduration, platform, workflow, enabled) VALUES (11, 'tabooImageLabeler', 'Taboo Image Labeler', 'Image Labeler with Taboo words', 5, 2, 60, 'artigo', 'tabooImageLabeler', true);
INSERT INTO gametype (id, name, label, description, rounds, players, roundduration, platform, workflow, enabled) VALUES (13, 'mitRecognizeSingle', 'MetropolItalia Single Round', 'Recognize statements', 1, 1, NULL, 'metropolitalia', 'mitRecognizeSingle', true);
INSERT INTO gametype (id, name, label, description, rounds, players, roundduration, platform, workflow, enabled) VALUES (14, 'combino', 'Combino', 'An image labelling game to combine image tags', 5, 2, 90, 'artigo', 'combino', NULL);

-- all roles
INSERT INTO role (id, role, rolename) VALUES (1, 'admin', 'Admin');
INSERT INTO role (id, role, rolename) VALUES (2, 'player', 'Player');
INSERT INTO role (id, role, rolename) VALUES (3, 'artigo.creator', 'Artigo Creator');
INSERT INTO role (id, role, rolename) VALUES (4, 'metropolitalia.admin', 'Metropolitalia Admin');
INSERT INTO role (id, role, rolename) VALUES (5, 'metropolitalia.player', 'Metropolitalia Player');
INSERT INTO role (id, role, rolename) VALUES (6, 'artigo.admin', 'Artigo Admin');
INSERT INTO role (id, role, rolename) VALUES (7, 'artigo.hiwi', 'Artigo HiWi');
INSERT INTO role (id, role, rolename) VALUES (8, 'artigo.translator', 'Artigo Translator');
INSERT INTO role (id, role, rolename) VALUES (9, 'metropolitalia.hiwi', 'Metropolitalia HiWi');

-- set to highest id of all tables
select setval('hibernate_sequence', 100);