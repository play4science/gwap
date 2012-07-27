INSERT INTO role (id, role, rolename) VALUES
	(6, 'artigo.admin', 'Artigo Admin'), 
	(7, 'artigo.hiwi', 'Artigo HiWi'), 
	(8, 'artigo.translator', 'Artigo Translator'),
	(9, 'metropolitalia.hiwi', 'Metropolitalia HiWi');

UPDATE role SET role='artigo.creator', rolename='Artigo Creator' WHERE id=3;

/* update person_role set roles_id= where roles_id=1 and persons_id in (); */