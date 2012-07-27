begin transaction;

/* Insert new location hierarchy for accenti profile */
insert into locationhierarchy 
	select l2.sublocation_id+55, 'mit.accenti.profile', 8310, l2.sublocation_id 
	from locationhierarchy l1 join locationhierarchy l2 on l1.sublocation_id=l2.location_id 
	where l1.location_id=8310;

insert into locationhierarchy 
	select l2.sublocation_id+8359, 'mit.accenti.profile', l2.location_id, l2.sublocation_id 
	from locationhierarchy l1 join locationhierarchy l2 on l1.sublocation_id=l2.location_id  
	where l1.name = 'mit' and l2.sublocation_id not in 
		(select sublocation_id from locationhierarchy where name='mit.accenti.profile');

commit;