alter table person 
	add column birthyear integer,
	add column hometown_id bigint,
	add column gender varchar(255),
	add column education varchar(255),
	add constraint fk8e488775a058ca8a foreign key (hometown_id) references location(id);