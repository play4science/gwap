ALTER TABLE gametype ADD COLUMN platform VARCHAR(255);

UPDATE gametype SET platform = 'artigo' WHERE id <= 2;
UPDATE gametype SET platform = 'metropolitalia' WHERE name = 'mitAssociate';
