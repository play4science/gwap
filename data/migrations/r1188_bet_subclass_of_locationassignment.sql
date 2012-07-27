begin transaction;

alter table locationassignment add column notEvaluated boolean;
alter table bet add column notEvaluated boolean;

/* set the new property notEvaluated correctly */
update locationassignment set notEvaluated = false;
update bet set notEvaluated = false;
update bet b set notEvaluated = true where exists 
	(select la.id from locationassignment la where la.person_id = b.person_id and la.resource_id=b.resource_id);

commit;