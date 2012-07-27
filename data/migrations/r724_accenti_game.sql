insert into gametype (id, name, label, description, rounds, players, roundduration, workflow, platform) values 
	(9, 'mitAccenti', 'Metropolitalia Accenti', 'Associate audio resources to their region', NULL, 1, NULL, NULL, NULL);
insert into source (id, url, description) values
	(2, '/usr/local/home/kneissl/jboss-4.2.3.GA/server/default/deploy/GWAP.ear/metropolitalia.war/', 'Metropolitalia');
insert into audioresource (id, enabled, path, datecreated, location, source_id, creator_id) values
	(1, TRUE, 'sampleaudio/Ausschnitt_1_aus_DGmtA02B_Bari.mp3', NULL, 8332, 2, NULL),
	(2, TRUE, 'sampleaudio/Ausschnitt_1_aus_DGmtA01D_Bergamo.mp3', NULL, 8325, 2, NULL);  
