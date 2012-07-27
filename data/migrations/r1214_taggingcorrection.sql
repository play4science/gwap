begin transaction;

-- Tagging refers to Resources in general and not only to ArtResources, thus drop foreign key constraint to ArtResource
alter table taggingcorrection 
	add column accepted boolean,
	add column correctedtag_id bigint,
	add constraint "fkbd5069d3642690cf" foreign key (correctedtag_id) references tag(id),
	drop constraint "fkbd5069d3a6314896";

delete from taggingcorrection where originaltag_id = (select t.tag_id from tagging t where t.id = tagging_id);
	
update taggingcorrection set accepted = true;
update taggingcorrection set correctedtag_id = (select t.tag_id from tagging t where t.id = tagging_id); 

-- do manually after all servers have been restarted
--alter table taggingcorrection drop column tagging_id;

commit;