begin transaction;

create table characterization (
	id bigint not null primary key,
	created timestamp,
	score integer,
	name varchar(255),
	value integer,
	person_id bigint,
	gameround_id bigint,
	resource_id bigint,
	constraint "fk74946a56dedab48ecf2b1d3b" FOREIGN KEY (person_id) REFERENCES person(id),
    constraint "fk74946a56ed70b0a6cf2b1d3b" FOREIGN KEY (gameround_id) REFERENCES gameround(id)
);

-- Update old ids so that the new ones do not overlap
update statementcharacterization set id=id-2 where id in (select c1.id from statementcharacterization c1 join statementcharacterization c2 on c1.id=c2.id-1);
update statementcharacterization set id=id-1 where id in (select c1.id from statementcharacterization c1 join statementcharacterization c2 on c1.id=c2.id-2);

-- Load old values
insert into characterization (id, name, value, created, gameround_id, person_id, resource_id) (select id-1000000000, 'maturity', maturity, created, gameround_id, 
person_id, statement_id from statementcharacterization where maturity <> 0);
insert into characterization (id, name, value, created, gameround_id, person_id, resource_id) (select id+1-1000000000, 'cultivation', cultivation, created, gameround_id, 
person_id, statement_id from statementcharacterization where cultivation <> 0);
insert into characterization (id, name, value, created, gameround_id, person_id, resource_id) (select id+2-1000000000, 'gender', gender, created, gameround_id, 
person_id, statement_id from statementcharacterization where gender <> 0);

-- Change to new data format (the order is important here!)
update characterization set value = 3 where value >= -20 and value <= 20;
update characterization set value = 1 where value < -20;
update characterization set value = 2 where value > 20;

-- Rename old table (if wanted)
alter table statementCharacterization rename to old_statementCharacterization;

commit;