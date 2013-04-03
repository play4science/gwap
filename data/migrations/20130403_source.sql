begin transaction;

alter table source add column platform varchar(255), add column description varchar(255);

update source set platform = 'artigo';

alter table topic add column source_id bigint, add constraint fk4d3dd0f4c7136ce FOREIGN KEY (source_id) REFERENCES source(id);

alter table term add column source_id bigint, add constraint fk27b88c4c7136ce FOREIGN KEY (source_id) REFERENCES source(id);

commit;