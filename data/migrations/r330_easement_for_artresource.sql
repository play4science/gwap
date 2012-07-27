-- add easemant aka usage rights
alter table artresource add column easement boolean;
update artresource set easement=true;

-- easement is true by default
alter table textresource add column easement boolean default true;
