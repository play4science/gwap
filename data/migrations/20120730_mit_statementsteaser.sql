insert into statement (id, enabled, text) values 
	(1, true, 'Ho incontrato la Lucia al mercato stamattina.'),
	(2, true, 'Lucia pranza sempre al tocco.'),
	(3, true, 'Lucia ha entrato la macchina in garage.');
	
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
	
insert into statementtoken (statement_id, id, sequencenumber, token_id) values
	(1, 1, 0, (select id from token where value='Ho')),
	(1, 2, 1, (select id from token where value='incontrato')),
	(1, 3, 2, (select id from token where value='la')),
	(1, 4, 3, (select id from token where value='Lucia')),
	(1, 5, 4, (select id from token where value='al')),
	(1, 6, 5, (select id from token where value='mercato')),
	(1, 7, 6, (select id from token where value='stamattina')),
	(1, 8, 7, (select id from token where value='.')),
	(2, 9, 0, (select id from token where value='Lucia')),
	(2,10, 1, (select id from token where value='pranza')),
	(2,11, 2, (select id from token where value='sempre')),
	(2,12, 3, (select id from token where value='al')),
	(2,13, 4, (select id from token where value='tocco')),
	(2,14, 5, (select id from token where value='.')),
	(3,15, 0, (select id from token where value='Lucia')),
	(3,16, 1, (select id from token where value='ha')),
	(3,17, 2, (select id from token where value='entrato')),
	(3,18, 3, (select id from token where value='la')),
	(3,19, 4, (select id from token where value='macchina')),
	(3,20, 5, (select id from token where value='in')),
	(3,21, 6, (select id from token where value='garage')),
	(3,22, 7, (select id from token where value='.'));

/* check these for null values */
select * from statementtoken where id < 23;
/* and insert like
insert into token (id, value) values (4, 'stamattina');
update statementtoken set token_id=4 where id=7;
insert into token (id, value) values (5, 'pranza');
update statementtoken set token_id=5 where id=10;
*/