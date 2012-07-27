ALTER TABLE authenticationtoken	ALTER COLUMN tokenid TYPE bigint;

ALTER TABLE artresource			ALTER COLUMN id TYPE bigint,
								ALTER COLUMN source_id TYPE bigint,
								ALTER COLUMN artist_id TYPE bigint;
ALTER TABLE artresourceteaser	ALTER COLUMN id TYPE bigint,
								ALTER COLUMN resource_id TYPE bigint;
ALTER TABLE artresourcetitle	ALTER COLUMN id TYPE bigint,
								ALTER COLUMN resource_id TYPE bigint;
ALTER TABLE gameround			ALTER COLUMN id TYPE bigint,
								ALTER COLUMN person_id TYPE bigint,
								ALTER COLUMN gamesession_id TYPE bigint;
ALTER TABLE gameround_resource	ALTER COLUMN gamerounds_id TYPE bigint,
								ALTER COLUMN resources_id TYPE bigint;
ALTER TABLE gameround_tag		ALTER COLUMN gamerounds_id TYPE bigint,
								ALTER COLUMN opponenttags_id TYPE bigint;
ALTER TABLE gamesession			ALTER COLUMN id TYPE bigint,
								ALTER COLUMN gametype_id TYPE bigint;
ALTER TABLE gametype			ALTER COLUMN id TYPE bigint;
ALTER TABLE highscore			ALTER COLUMN id TYPE bigint;
ALTER TABLE message				ALTER COLUMN id TYPE bigint;
ALTER TABLE person				ALTER COLUMN id TYPE bigint,
								ALTER COLUMN personconnected_id TYPE bigint;
ALTER TABLE person_role			ALTER COLUMN persons_id TYPE bigint,
								ALTER COLUMN roles_id TYPE bigint;
ALTER TABLE role				ALTER COLUMN id TYPE bigint;
ALTER TABLE source				ALTER COLUMN id TYPE bigint;
ALTER TABLE tag					ALTER COLUMN id TYPE bigint;
ALTER TABLE tagfrequency		ALTER COLUMN id TYPE bigint;
ALTER TABLE tagging				ALTER COLUMN id TYPE bigint,
								ALTER COLUMN person_id TYPE bigint,
								ALTER COLUMN tag_id TYPE bigint,
								ALTER COLUMN resource_id TYPE bigint,
								ALTER COLUMN gameround_id TYPE bigint;

