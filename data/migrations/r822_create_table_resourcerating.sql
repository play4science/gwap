create table resourcerating (
	id bigint primary key,
	created timestamp, 
	person_id bigint,
	gameround_id bigint,
	rating bigint,
	resource_id bigint,
	constraint fk74946a56dedab48ebf216b6b FOREIGN KEY (person_id) REFERENCES person(id),
	constraint fk74946a56ed70b0a6bf216b6b FOREIGN KEY (gameround_id) REFERENCES gameround(id)
);