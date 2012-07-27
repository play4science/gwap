INSERT INTO role (id, role, rolename) VALUES
  (10,'tagatag.tester','TagATag Tester');

/* update person_role set roles_id= where roles_id=1 and persons_id in (); */

CREATE TABLE metatagging (
    id bigint NOT NULL,
    created timestamp without time zone,
    score integer,
    person_id bigint,
    gameround_id bigint,
    tag_id bigint,
    tagresource_id bigint,
    resource_id bigint
);

