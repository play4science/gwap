alter table source rename column description to name;
insert into source (id,url,name) values (
	3, 
	'/usr/local/home/kneissl/jboss-4.2.3.GA/server/default/deploy/GWAP.ear/artigo.war/', 
	'kunsthalle-karlsruhe');

alter table source add column homepage varchar(255);
update source set homepage='http://www.kunsthalle-karlsruhe.de/' where id=3;