/* merge Nord-est & Nord-ovest into Nord */
begin transaction;
update location set name = 'Nord' where id = 8306;
update locationassignment set location_id = 8306 where location_id = 8307;
update locationhierarchy set location_id = 8306 where location_id = 8307;
delete from locationhierarchy where sublocation_id = 8307;
update bet set location_id=8306 where location_id=8307;

delete from locationgeopoint where location_id = 8306 and id < 260647;
delete from locationgeopoint where location_id = 8306 and id > 260717;
delete from locationgeopoint where location_id = 8307 and id > 260757 and id < 260796;
update locationgeopoint set id=id+2111 where location_id = 8306;
update locationgeopoint set id=id+2033 where location_id = 8307 and id >= 260796;
update locationgeopoint set id=id+2000 where location_id = 8307 and id <= 260757;
update locationgeopoint set location_id=8306 where location_id = 8307;

delete from location where id=8307;

commit;