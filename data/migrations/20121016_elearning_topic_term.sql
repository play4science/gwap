alter table topic add column enabled boolean;

create table term_rejectedtag (
	term_id bigint not null,
	rejectedtags_id bigint not null,
	constraint fkf6dad30922bf61e9 FOREIGN KEY (rejectedtags_id) REFERENCES tag(id),
    constraint fkf6dad309ab41a2e6 FOREIGN KEY (term_id) REFERENCES term(id)
);