update gametype set rounds=10, platform = 'accentiurbani', label='accenti urbani' where name='mitAccenti';

insert into audioresource (id, enabled, path, datecreated, location, source_id, creator_id) values
	(1, TRUE, 'sampleaudio/Ausschnitt_1_aus_DGmtA02B_Bari.mp3', NULL, 8332, 2, NULL),
	(2, TRUE, 'sampleaudio/Ausschnitt_1_aus_DGmtA01D_Bergamo.mp3', NULL, 8325, 2, NULL);  
