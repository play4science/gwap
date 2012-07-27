alter table statementlocationassignment rename to locationassignment;
alter table locationassignment rename column statement_id to resource_id;
alter table locationassignment drop constraint fk74946a56dedab48ebd659411;
alter table locationassignment add constraint  fk74946a56dedab48ecd086082 FOREIGN KEY (person_id) REFERENCES person(id);
alter table locationassignment drop constraint fk74946a56ed70b0a6bd659411;
alter table locationassignment add constraint  fk74946a56ed70b0a6cd086082 FOREIGN KEY (gameround_id) REFERENCES gameround(id);
alter table locationassignment drop constraint fkbd6594117f0cbc46;
alter table locationassignment add constraint  fkcd0860827f0cbc46 FOREIGN KEY (location_id) REFERENCES location(id);
alter table locationassignment drop constraint fkbd65941110d23a2e;
