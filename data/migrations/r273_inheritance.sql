begin transaction;

/* Resources */

alter table artresource 
	add column source_id int, 
	add column path varchar(255), 
	add column enabled boolean,
	DROP CONSTRAINT fkb54e37717da4631d,
	ADD CONSTRAINT  fkb54e37714c7136ce foreign key (source_id) references source(id);

update artresource set (source_id, path, enabled) = 
	(resource.source_id, resource.path, resource.enabled) from resource where resource.id = artresource.id;

insert into artresource (id, enabled, source_id, path) 
	(select resource.id, resource.enabled, resource.source_id, resource.path from
		resource where not exists (select ar2.id from artresource ar2 where ar2.id=resource.id));

alter table resourcedescription rename to artresourceteaser;
		
ALTER TABLE artresourceteaser
	DROP CONSTRAINT fk611bbaeec739e0ee,
	ADD CONSTRAINT  fk683d4d215de3a851 FOREIGN KEY (resource_id) REFERENCES artresource(id),
	DROP CONSTRAINT resourcedescription_pkey,
	ADD CONSTRAINT  artresourceteaser_pkey PRIMARY KEY (id);

alter table artresourcetitle
	DROP CONSTRAINT fkba0f96768dad409,
	ADD CONSTRAINT  fkba0f9675de3a851 FOREIGN KEY (resource_id) REFERENCES artresource(id);

ALTER TABLE gameround_resource
	DROP CONSTRAINT fk376a8c7166c91b77;


/* Miscellaneous constraint stuff */

ALTER TABLE tagging
	ADD COLUMN gameround_id int,
	DROP CONSTRAINT fk6ed6495c739e0ee,
	DROP CONSTRAINT fk6ed6495dedab48e,
	ADD CONSTRAINT  fk74946a56dedab48e6ed6495 FOREIGN KEY (person_id) REFERENCES person(id),
	ADD CONSTRAINT  fk6ed64955de3a851 FOREIGN KEY (resource_id) REFERENCES artresource(id),
	ADD CONSTRAINT  fk74946a56ed70b0a66ed6495 FOREIGN KEY (gameround_id) REFERENCES gameround(id);

update tagging set gameround_id =
	gameround_tagging.gameround_id from gameround_tagging where gameround_tagging.taggings_id = id;

drop table resource;

drop table gameround_tagging;

commit;