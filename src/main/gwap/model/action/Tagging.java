/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (c) 2009-11, Lehrstuhl PMS (http://www.pms.ifi.lmu.de/)
 * All rights reserved.
 * 
 */

package gwap.model.action;

import gwap.model.Tag;
import gwap.model.resource.Resource;

import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;

/**
 * A tagging saves which user used a tag to tag a resource.
 * 
 * @author Christoph Wieser
 */

@NamedQueries( { 
	@NamedQuery(
			name = "tagging.tagFrequencyByResourceAndLanguage",
			query = "select new gwap.wrapper.TagFrequency(t.tag.name, count(t.tag.name), t.tag.id) " +
					"from Tagging t " +
					"where t.resource=:resource and t.tag.language=:language " +
					"group by t.tag.name,t.tag.id " +
					"having count(t.tag.name) >= :threshold " +
					"order by count(t.tag.name) desc"),
	@NamedQuery(
			name = "tagging.tagFrequencyByResourceAndLanguageFromTagRatings",
			query = "select new gwap.wrapper.TagFrequency(tr.tag.name, tr.rating, tr.tag.id) " +
					"from TagRating tr " +
					"where tr.rating>=:threshold " +
					"and tr.resource=:resource " +
					"and tr.tag.language=:language " +
					"order by tr.rating"),
	@NamedQuery(
			name = "tagging.randomTaggingsByResourceAndLanguage",
			query = "select tag from Tagging t " +
					"where t.resource=:resource and t.tag.language=:language " +
					"order by random()"),
	@NamedQuery(
			name = "tagging.randomTagByResourceAndLanguage",
			query = "select tag from Tagging t " +
					"left join t.tag tag " +
					"where  t.resource=:resource and tag.language=:language " +
					"group by tag.id, tag.blacklisted, tag.language, tag.name " +
					"order by random()"),
	@NamedQuery(
			name = "tagging.taggingsByGameAndResourceId",					
			query = "select t from Tagging t " +
					"where t.resource.id=:resid and " +
					"t.gameRound.id=:gameid " +					
					"order by t.created"),
	@NamedQuery(
			name = "tagging.taggingsByTag",
			query = "select count(*) from Tagging t where t.tag=:tag"),
	@NamedQuery(
			name="tagging.byResource",
			query="select t.tag.name, count(t.id) from Tagging t " +
					"where t.resource.id=:resourceId group by t.tag.name having count(t.id)>=:minOccurrence"),
					
	@NamedQuery(
			name="tagging.topCorrectAnswers",
			query="select new gwap.wrapper.BackstageAnswer(t.name, count(*)) from Term r join r.confirmedTags t join r.taggings tg join tg.gameRound gr " +
					"where r.id=:resourceId and gr.gameSession.externalSessionId=:externalSessionId " +
					"and t.id=tg.tag.id " +
					"group by t.name order by count(*) desc"),
	@NamedQuery(
			name="tagging.topUnknownAnswers",
			query="select new gwap.wrapper.BackstageAnswer(t.name, count(*)) from Term r join r.taggings tg join tg.tag t " +
					"where r.id=:resourceId and tg.gameRound.gameSession.externalSessionId=:externalSessionId " +
					"and t.id not in (select t2.id from r.confirmedTags t2) and t.id not in (select t2.id from r.rejectedTags t2) " +
					"group by t.name order by count(*) desc"),
	@NamedQuery(
			name="tagging.topWrongAnswers",
			query="select new gwap.wrapper.BackstageAnswer(t.name, count(*)) from Term r join r.rejectedTags t join r.taggings tg join tg.gameRound gr " +
					"where r.id=:resourceId and gr.gameSession.externalSessionId=:externalSessionId " +
					"and t.id=tg.tag.id " +
					"group by t.name order by count(*) desc"),
	@NamedQuery(
			name="tagging.topCorrectAnswersGeneral",
			query="select new gwap.wrapper.BackstageAnswer(t.name, count(*)) from Term r join r.confirmedTags t join r.taggings tg " +
					"where r.id=:resourceId " +
					"and t.id=tg.tag.id " +
					"group by t.name order by count(*) desc"),
	@NamedQuery(
			name="tagging.topUnknownAnswersGeneral",
			query="select new gwap.wrapper.BackstageAnswer(t.name, count(*)) from Term r join r.taggings tg join tg.tag t " +
					"where r.id=:resourceId " +
					"and t.id not in (select t2.id from r.confirmedTags t2) and t.id not in (select t2.id from r.rejectedTags t2) " + 
					"group by t.name order by count(*) desc"),
	@NamedQuery(
			name="tagging.topWrongAnswersGeneral",
			query="select new gwap.wrapper.BackstageAnswer(t.name, count(*)) from Term r join r.rejectedTags t join r.taggings tg " +
					"where r.id=:resourceId " +
					"and t.id=tg.tag.id " +
					"group by t.name order by count(*) desc"),
	@NamedQuery(
			name = "tagging.tagFrequencyBySource",
			query = "select r.externalId, t.tag.name, t.tag.language, count(t.tag.name) " +
					"from ArtResource r join r.taggings t " +
					"where r.source.name=:source " +
					"group by r.externalId, t.tag.name, t.tag.language " +
					"having count(t.tag.name) >= :threshold " +
					"order by r.externalId, t.tag.language, t.tag.name"),
	@NamedQuery(
			name = "tagging.tagFrequencyBySourceAndLanguage",
			query = "select r.externalId, t.tag.name, t.tag.language, count(t.tag.name) " +
					"from ArtResource r join r.taggings t " +
					"where r.source.name=:source and t.tag.language=:language " +
					"group by r.externalId, t.tag.name, t.tag.language " +
					"having count(t.tag.name) >= :threshold " +
					"order by r.externalId, t.tag.language, t.tag.name"),
	@NamedQuery(
			name = "tagging.taggingsByTagNameResourceAndGameround",
			query = "select t from Tagging t where lower(t.tag.name)=lower(:tagName) and t.resource=:resource and t.gameRound = :gameRound"),
	@NamedQuery(
			name="tagging.unknownAnswers",
			query="select r.id, t.id, count(*) from Term r join r.taggings tg join tg.tag t " +
					"where t.id not in (select t2.id from r.confirmedTags t2) and t.id not in (select t2.id from r.rejectedTags t2) " + 
					"group by r.id, t.id order by r.id, count(*) desc")

})

@Entity
@Name("tagging")
@Scope(ScopeType.EVENT)
public class Tagging extends Action {

	private static final long serialVersionUID = 1L;

	@ManyToOne	private Tag tag;
	@ManyToOne	private Resource resource;

	public Tag getTag() {
		return tag;
	}

	public void setTag(Tag tag) {
		this.tag = tag;
	}

	public Resource getResource() {
		return resource;
	}

	public void setResource(Resource resource) {
		this.resource = resource;
	}
	
	@Override
	public String toString() {
		if (person!=null)
			return getPerson().toString() + " tagged " + resource.toString() + " with " + tag;
		else
			return "The players tagged " + resource.toString() + " with " + tag;			
	}
	
}