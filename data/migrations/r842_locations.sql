begin transaction;

alter table person drop constraint fk8e488775a058ca8a;
alter table locationassignment drop constraint fkcd0860827f0cbc46;

create table locationhierarchy (
	id bigint primary key,
	name varchar(255),
	location_id bigint,
	sublocation_id bigint,
	constraint fkcd092de07f0cbc46 FOREIGN KEY (location_id) REFERENCES location(id),
	constraint fkcd092de0e9919606 FOREIGN KEY (sublocation_id) REFERENCES location(id)
);

delete from locationhierarchy;
delete from locationgeopoint;
delete from audioresource;
delete from location;
delete from geopoint;

\i ../metropolitalia/locations.sql
\i ../metropolitalia/accenti.sql

update person set hometown_id = 6053 where hometown_id = 8332;
update person set hometown_id = 1895 where hometown_id = 8325;
update person set hometown_id = 7212 where hometown_id = 8336;
update person set hometown_id = 6534 where hometown_id = 8334;
update person set hometown_id = 4386 where hometown_id = 8328;
update person set hometown_id = 1282 where hometown_id = 8323;
update person set hometown_id = 6173 where hometown_id = 8333;
update person set hometown_id = 1809 where hometown_id = 8324;
update person set hometown_id = 5278 where hometown_id = 8331;
update person set hometown_id = 6775 where hometown_id = 8335;
update person set hometown_id = 3852 where hometown_id = 8327;
update person set hometown_id = 4616 where hometown_id = 8329;
update person set hometown_id = 4893 where hometown_id = 8330;
update person set hometown_id =  272 where hometown_id = 8322;
update person set hometown_id = 3454 where hometown_id = 8326;

update locationassignment set location_id = 6053 where location_id = 8332;
update locationassignment set location_id = 1895 where location_id = 8325;
update locationassignment set location_id = 7212 where location_id = 8336;
update locationassignment set location_id = 6534 where location_id = 8334;
update locationassignment set location_id = 4386 where location_id = 8328;
update locationassignment set location_id = 1282 where location_id = 8323;
update locationassignment set location_id = 6173 where location_id = 8333;
update locationassignment set location_id = 1809 where location_id = 8324;
update locationassignment set location_id = 5278 where location_id = 8331;
update locationassignment set location_id = 6775 where location_id = 8335;
update locationassignment set location_id = 3852 where location_id = 8327;
update locationassignment set location_id = 4616 where location_id = 8329;
update locationassignment set location_id = 4893 where location_id = 8330;
update locationassignment set location_id =  272 where location_id = 8322;
update locationassignment set location_id = 3454 where location_id = 8326;

alter table person add constraint fk8e488775a058ca8a FOREIGN KEY (hometown_id) REFERENCES location(id);
alter table locationassignment add constraint fkcd0860827f0cbc46 FOREIGN KEY (location_id) REFERENCES location(id);

alter table location drop column containedin_id;

commit;