-- This is to reset the hibernate sequence counter
-- If it does not exist yet, create it with
-- CREATE SEQUENCE hibernate_sequence;
CREATE VIEW tmp_max_id AS 
	SELECT MAX(tokenid) AS id FROM authenticationtoken UNION 
	SELECT MAX(id) AS id FROM artresource UNION 
	SELECT MAX(id) AS id FROM artresourceteaser UNION 
	SELECT MAX(id) AS id FROM artresourcetitle UNION 
	SELECT MAX(id) AS id FROM gameround UNION 
	SELECT MAX(id) AS id FROM gamesession UNION 
	SELECT MAX(id) AS id FROM gametype UNION 
	SELECT MAX(id) AS id FROM message UNION 
	SELECT MAX(id) AS id FROM person UNION 
	SELECT MAX(id) AS id FROM role UNION 
	SELECT MAX(id) AS id FROM source UNION 
	SELECT MAX(id) AS id FROM tag UNION 
	SELECT MAX(id) AS id FROM tagging;

SELECT setval('hibernate_sequence', max(id)) FROM tmp_max_id;

DROP VIEW tmp_max_id;