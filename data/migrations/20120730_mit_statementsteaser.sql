insert into statement (id, enabled, text) values 
	(1, false, 'Ho incontrato la Lucia al mercato stamattina.'),
	(2, false, 'Lucia pranza sempre al tocco.'),
	(3, false, 'Lucia ha entrato la macchina in garage.');

insert into geopoint (id, latitude, longitude) values
	(10001, 45.269403, 10.359128),
	(10002, 42.059822, 13.314450),
	(10003, 37.598403, 14.296233);

insert into statementsteaser (id, teaser, publicationdate) values
	(1, '', '2012-07-30');

	
insert into statementwithgeopoint (id, statementsteaser_id, geopoint_id, statement_id) values
	(1, 1, 10001, 1),
	(2, 1, 10002, 2),
	(3, 1, 10003, 3);
	