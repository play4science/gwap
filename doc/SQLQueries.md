# SQL Queries

## Artigo
*   Number of taggings:

        select count(*) from tagging;

*   Number of taggings in a month:

        select count(*) from tagging 
		where created >= DATE('2012-04-01') and created < DATE('2012-05-01');

*   Number of matched taggings:

        select sum(foo.cnt) from (
			select 1 as cnt from tagging t 
			group by t.resource_id, t.tag_id having count(*) > 1) as foo;

*   Number of newly matched taggings in a month (approximately):

        select sum(foo.cnt) from (
			select 1 as cnt from tagging t 
			join tagging t2 on t.resource_id=t2.resource_id and t.tag_id 1) as foo;

*   Number of taggings matched only in this month:

        select sum(foo.cnt) from (
			select 1 as cnt from tagging t where created >= DATE('2012-04-01') and created < DATE('2012-05-01') 
			group by t.resource_id, t.tag_id having count(*) > 1) as foo;

*   Taggings per resource:

        select avg(cnt), max(cnt), count(*) from (
			select count(*) as cnt from (
				select tag.name, count(tag.name), t.resource_id as resource_id from tagging t 
				join tag on tag.id=t.tag_id where tag.language='de' 
				group by tag.name, t.resource_id having count(tag.name) > 1) as foo 
			group by resource_id) as bar;

*   Tagging corrections:

        select c.created, a.id as artresource_id, c.accepted as accepted, ot.id originaltag_id, 
			ot.name originaltag_name, ot.language originaltag_language, ct.id correctedtag_id, 
			ct.name correctedtag_name, ct.language correctedtag_language 
		from taggingcorrection c join tag ot on c.originaltag_id=ot.id 
		join tag ct on c.correctedtag_id=ct.id join gameround_resource gr on c.gameround_id=gr.gamerounds_id 
		join artresource a on gr.resources_id=a.id order by c.created;

*   Monthly Highscore all non-beta games:

        select
            p2.username as username,
            p2.forename || ' ' || p2.surname as name,
            p2.email as email,
            sum(g.score) as score
        from
            GameRound g
            join Person p on g.person_id = p.id
            join GameSession s on g.gamesession_id = s.id
            join Person p2 on p2.id=coalesce(p.personConnected_id, p.id)
        where
            g.endDate >= date '2012-09-10'
            and g.endDate < date'2012-09-17'
            and s.gametype_id in (1,2,5,6,11)
        group by
            p2.username,
            p2.id
        having sum(g.score)>0
        order by sum(g.score) desc
        limit 10;
	

## Metropolitalia
*   Delete own actions:

        delete from locationassignment where person_id in (
			select id from person where person.personconnected_id in (OWN_IDS) or person.id in (OWN_IDS)); 
		delete from bet where person_id in (
			select id from person where person.personconnected_id in (OWN_IDS) or person.id in (OWN_IDS)); 
		delete from statementannotation_statementtoken where statementannotation_id in (
			select id from statementannotation where person_id in (
				select id from person where person.personconnected_id in (OWN_IDS) or person.id in (OWN_IDS)));
		delete from statementannotation where person_id in (
			select id from person where person.personconnected_id in (OWN_IDS) or person.id in (OWN_IDS)); 
		delete from statementcharacterization where person_id in (
			select id from person where person.personconnected_id in (OWN_IDS) or person.id in (OWN_IDS));
		delete from familiarity where person_id in (
			select id from person where person.personconnected_id in (OWN_IDS) or person.id in (OWN_IDS));

*   All statements:

        select s.id, s.text, 
			array_to_string(array(
				select t.value from statementstandardtoken st join token t on st.token_id=t.id 
				where st.statement_id=s.id order by sequencenumber), ' ') as standard, 
			array_to_string(array(
				select l.name from bet b join location l on b.location_id=l.id 
				where b.resource_id=s.id and b.person_id is null), ', ') as original_location, 
			array_to_string(array(
				select l.name from locationassignment la join location l on la.location_id=l.id 
				where la.resource_id=s.id and la.notevaluated=false 
				union select l.name from bet b join location l on b.location_id=l.id 
				where b.resource_id=s.id and b.notevaluated=false and b.person_id is not null), ', ') as locations 
		from statement s where enabled=true order by s.text;
