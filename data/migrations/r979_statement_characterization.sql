alter table statementcharacterization 
	drop column male, 
	drop column female,
	drop column correct,
	add column gender integer;

create table familiarity (
	id bigint primary key,
	created timestamp, 
	person_id bigint,
	gameround_id bigint,
	familiar boolean,
	resource_id bigint,
	constraint fk74946a56dedab48ecb593e9 FOREIGN KEY (person_id) REFERENCES person(id),
	constraint fk74946a56ed70b0a6cb593e9 FOREIGN KEY (gameround_id) REFERENCES gameround(id)
);

alter table statement
	add column points integer;