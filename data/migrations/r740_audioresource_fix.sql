begin transaction;
alter table audioresource add column location_id bigint;
alter table audioresource add constraint fka81cbde47f0cbc46 FOREIGN KEY (location_id) REFERENCES location(id);
update audioresource set location_id = cast(location as bigint);
alter table audioresource drop column location;
commit;