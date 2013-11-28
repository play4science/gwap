/*
 * This file is part of gwap, an open platform for games with a purpose
 *
 * Copyright (C) 2013
 * Project play4science
 * Lehr- und Forschungseinheit für Programmier- und Modellierungssprachen
 * Ludwig-Maximilians-Universität München
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 * 
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package gwap.model.resource;

import gwap.model.ArtResourceRating;
import gwap.model.Person;
import gwap.model.Source;
import gwap.tools.ImageAccessBean;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.Lob;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Transient;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;

@NamedQueries({
	// internal usage only (no enabled check)
	@NamedQuery(name="artResource.all", query="select r from ArtResource r"),
	@NamedQuery(name="artResource.byId", query="select r from ArtResource r where id=:id"),
	@NamedQuery(name="artResource.byIdWithTeasers", query="select r from ArtResource r inner join fetch r.teasers where r.id=:id"),
	@NamedQuery(name="artResource.byIdNotInCache", query="select r from ArtResource r where id=:id and " +
			"not exists (select id from ArtResourceCache c where c.resource = r and name = :name and language = :language)"),
	
	// public usage
	@NamedQuery(name="artResource.nrEnabled", query="select count(r) from ArtResource r where r.enabled=true"),
	@NamedQuery(name="artResource.enabled", query="select r from ArtResource r where r.enabled=true order by id"),
	@NamedQuery(name="artResource.teaserByLanguage",
			    query="select t.description " +
				      "from ArtResource r join r.teasers t " +
				      "where r=:resource and t.language=:language and " +
				      "  r.enabled=true"),
				      
    @NamedQuery(name="artResource.tagFrequencyByTagNameAndLanguageAndThreshold",
	            query="select r " +
	                  "from ArtResource r " +
	                  "where " +
	                  "  r.enabled=true and " +
	                  "  r.id in ( " +
	                  "    select t.resource.id " +
	                  "    from Tagging t " +
		              "    where lower(t.tag.name)=lower(:tagName) and t.tag.language=:language and t.resource.enabled=true " +
		              "    group by t.resource.id, t.tag.id " +
		              "    having count(t.person) >= :threshold " +
		              "    order by count(t.person) desc )"),
		              
	@NamedQuery(name="artResource.random", 
			    query="select r " +
			    	  "from ArtResource r " +
			    	  "where r.enabled=true " +
			    	  "order by random()"),	
	
	@NamedQuery(name="artResource.woelfflin", 
			  	query="select r " +
			  		  "from ArtResource r " +
			  		  "where r.enabled=true and " +
			  		  //"r.id < 5000 and r.id > 999 and " +
/*			  		  "ascii(substr(r.dateCreated,1,1)) >= 48 and ascii(substr(r.dateCreated,1,1)) <= 57 and " +
			  		  "ascii(substr(r.dateCreated,2,1)) >= 48 and ascii(substr(r.dateCreated,2,1)) <= 57 and " +
			  		  "ascii(substr(r.dateCreated,3,1)) >= 48 and ascii(substr(r.dateCreated,3,1)) <= 57 and " +
			  		  "ascii(substr(r.dateCreated,4,1)) >= 48 and ascii(substr(r.dateCreated,4,1)) <= 57 and " +
			  		  "cast(substr(r.dateCreated,1,4) as INTEGER) < 1770 and " +
			  		  "cast(substr(r.dateCreated,1,4) as INTEGER) > 1400 and " +
			  		  */
			  		  "r.artist is not null "+
					 "order by random()"),		    	 
					 
    @NamedQuery(name="artResource.randomCustom", 
    			query="select r " +
    				  "from ArtResource r " +
    				  "where r.enabled=true and " +
    				  "      r.source=:source " +
		    	  	  "order by random()"),
	@NamedQuery(name="artResource.idRandom", 
		    query="select r.id " +
		    	  "from ArtResource r " +
		    	  "where r.enabled=true " +
		    	  "order by random()"),			    	  
 	@NamedQuery(name="artResource.withTeaser", 
	  			query="select distinct r " +
			    	  "from ArtResource r join r.teasers t " +
			    	  "where t is not null and " +
			    	  "  r.enabled=true " +
			    	  "order by r.id"),
	@NamedQuery(name="artResource.withTeaserCustom", 
	   			query="select distinct r " +
	   			  	  "from ArtResource r join r.teasers t " +
	    			  "where t is not null and " +
	    			  "  r.enabled=true " +
	    			  " and r.source = :source " +
	    	  		  "order by r.id"),
	@NamedQuery(name="artResource.randomWithTeaser", 
			    query="select distinct r " +
			   	      "from ArtResource r join r.teasers t " +
			   	 	  "where t is not null and " +
	                  "  r.enabled=true " +
			   		  "order by random()"),
	@NamedQuery(name="artResource.randomWithoutTeaser", 
			    query="select r " +
			    	  "from ArtResource r left outer join r.teasers t " +
			    	  "where t is null and " +
	                  "  r.enabled=true " +
			    	  "order by random()"),
	@NamedQuery(name="artResource.randomWithoutTeaserInLanguage", 
				query="select r " +
					  "from ArtResource r join r.teasers t " +
					  "where not exists (from ArtResourceTeaser t2 where t2.resource = r and t2.language = :language) and " +
	                  "  r.enabled=true " +
					  "order by random()"),
	@NamedQuery(name="artResource.randomWithoutTaggings",
			    query="select t.resource " +
				      "from Tagging t right outer join t.resource " +
				      "where t is null and " +
				      "  t.resource.enabled=true " +
				      "order by random()"),
    @NamedQuery(name="artResource.notTaggedResourceId",
    			query="select r.id " +
    				  "from ArtResource r left join r.taggings t " + 
    				  "where " +
    				  "  t.id is null and " +
    				  "  r.enabled=true " +
    				  "order by random()"
    			),
    @NamedQuery(name="artResource.notTaggedResourceIdCustom",
    			query="select r.id " +
    				  "from ArtResource r left join r.taggings t " + 
    				  "where " +
    				  "  t.id is null and " +
    				  "  r.enabled=true and " +
    				  "  r.source=:source " +
    				  "order by random()"
    			),
    @NamedQuery(name="artResource.notTaggedResourceIdWithTeaser",
    			query="select r.id " +
    				  "from ArtResource r left join r.taggings t join r.teasers " + 
    				  "where " +
    				  "  t.id is null and " +
    				  "  r.enabled=true " +
    				  "order by random()"
    			),
    @NamedQuery(name="artResource.notTaggedResourceIdWithTeaserCustom",
    			query="select r.id " +
    				  "from ArtResource r left join r.taggings t join r.teasers " + 
    				  "where " +
    				  "  t.id is null and " +
    				  "  r.enabled=true and " +
    				  "  r.source=:source " +
    				  "order by random()"
    			),
	@NamedQuery(name="artResource.leastTaggedResourceId",
				query="select r.id " +
					  "from ArtResource r join r.taggings t " +
					  "where " +
					  "  t.tag.language=:language and" +
					  "  r.enabled=true " +
					  "group by r.id " +
					  "order by count(r.id) asc, random() "
					  ),
	@NamedQuery(name="artResource.leastTaggedResourceIdCustom",
				query="select r.id " +
					  "from ArtResource r join r.taggings t " +
					  "where " +
					  "  t.tag.language=:language and " +
					  "  r.enabled=true and " +
					  "  r.source=:source " +
					  "group by r.id " +
					  "order by count(r.id) asc, random() "
					  ),
	@NamedQuery(name="artResource.leastTaggedResourceIdMaxCount",
					query="select r.id " +
						  "from ArtResource r join r.taggings t " +
						  "where " +
						  "  t.tag.language=:language and" +
						  "  r.enabled=true " +
						  "group by r.id " +
						  "having count(r.id) <= :maxCount " +
						  "order by count(r.id) asc, random() "
						  ),
	@NamedQuery(name="artResource.leastTaggedResourceIdMaxCountCustom",
					query="select r.id " +
						  "from ArtResource r join r.taggings t " +
						  "where " +
						  "  t.tag.language=:language and " +
						  "  r.enabled=true and " +
						  "  r.source=:source " +
						  "group by r.id " +
						  "having count(r.id) <= :maxCount " +
						  "order by count(r.id) asc, random() "
						  ),
	@NamedQuery(name="artResource.leastTaggedResourceIdWithTeaser",
				query="select r.id " +
					  "from ArtResource r join r.taggings t join r.teasers " +
					  "where " +
					  "  t.tag.language=:language and" +
					  "  r.enabled=true " +
					  "group by r.id " +
					  "order by count(r.id) asc, random() "
					  ),
	@NamedQuery(name="artResource.leastTaggedResourceIdWithTeaserCustom",
				query="select r.id " +
					  "from ArtResource r join r.taggings t join r.teasers " +
					  "where " +
					  "  t.tag.language=:language and " +
					  "  r.enabled=true and " +
					  "  r.source=:source " +
					  "group by r.id " +
					  "order by count(r.id) asc, random() "
					  ),
	@NamedQuery(name="artResource.leastTaggedResourceIdLimit",
			query="select r.id " +
				  "from ArtResource r inner join r.taggings t " +
				  "where " +
				  "t.tag.language = :language and " +
				  "r.enabled=true " +
				  "and r.id in (:limitlist) " +
				  "group by r.id " +
				  "order by count(r.id) asc " ),					  
	@NamedQuery(name="artResource.atLeastTaggedResourceId",
	            query="select t.resource.id " +
	                  "from Tagging t " +
	                  "where " +
	                  "  t.resource.enabled=true " +
	                  "group by t.resource.id " +
	                  "having count(t.id) >= :minTaggings " +
	                  "order by random() " ),
    @NamedQuery(name="artResource.atLeastTaggedResourceByLanguageId",
    			query="select t.resource.id " +
    				  "from Tagging t " +
    				  "where " +
    				  "  t.resource.enabled=true and " +
    				  "  t.tag.language=:language " +
    				  "group by t.resource.id " +
    				  "having count(t.id) >= :minTaggings " +
    				  "order by random() " ),
    @NamedQuery(name="artResource.atLeastTaggedResourceByLanguageIdCustom",
    			query="select t.resource.id " +
    			      "from Tagging t " +
    			      "where " +
    			      "  t.resource.enabled=true and " +
    			      "  t.tag.language=:language and " +
    			      "  t.resource.source=:source " +
    			      "group by t.resource.id " +
    			      "having count(t.id) >= :minTaggings " +
	  "order by random() " ),
    @NamedQuery(name="artResource.atLeastTaggedResourceIdCustom",
    			query="select t.resource.id " +
    				  "from Tagging t " +
    				  "where " +
    				  "  t.resource.enabled=true and " +
    				  "  t.resource.source=:source " +
    				  "group by t.resource.id " +
    				  "having count(t.id) >= :minTaggings " +
    				  "order by random() " ),
  	@NamedQuery(name="artResource.atLeastTaggedResourceIdLimit",
            query="select t.resource.id " +
                  "from Tagging t " +
                  "where " +
                  "t.resource.enabled=true " +
                  "and t.resource.id in (:limitlist) "+
                  "and t.tag.language = :language "+
                  "group by t.resource.id " +
                  "having count(t.id) >= :minTaggings " +
                  "order by random() " ),	                  
	  @NamedQuery(name="artResource.byNotIdRandom",
	  			query="select r from ArtResource r where id!=:id and " +
	  					"r.enabled=true " +
	  					"order by random()"),
	  /*@NamedQuery(name="artResource.bySimilarityId",
	  			query="select r from ArtResource r " +
	  				  "left join r.taggings t1 " +
	  				  "left join t1.tag.taggings t2 " +
	  				  "where t2.resource.id=:id and r.id!=:id " +
	  				  "group by r.id, r.enabled, r.artist, r.dateCreated, r.institution, r.location, r.path, r.source " +
	  				  "order by count(t1) desc"),*/
	  @NamedQuery(name="artResource.bySimilarityIdAndNotIdListLimit",
			  	query="select new gwap.tools.ArtResourceFrequency(r, count(t1)) from ArtResource r " +
			  			"left join r.taggings t1 " +
			  			"left join t1.tag.taggings t2 " +
			  			"where t2.resource.id=:id and r.id!=:id and t1.tag.language=:lang and " +
			  			"r not in (:others) and " +
			  			"r.id in (:limitlist) and " +
			  			"r.enabled=true " +
			  			"group by r.id, r.enabled, r.artist, r.dateCreated, r.institution, r.location, r.path, r.source " +
			  			"order by count(t1) desc"),
	  @NamedQuery(name="artResource.bySimilarityIdAndNotIdListAtLeastTaggedLimit",
		  	query="select new gwap.tools.ArtResourceFrequency(r, count(t1)) from ArtResource r " +
		  			"left join r.taggings t1 " +
		  			"left join t1.tag.taggings t2 " +
		  			"where t2.resource.id=:id and r.id!=:id and t1.tag.language=:lang and " +
		  			"r not in (:others) and " +
		  			"r.id in (:limitlist) and " +
		  			"r.enabled=true " +
		  			"group by r.id, r.enabled, r.artist, r.dateCreated, r.institution, r.location, r.path, r.source " +
		  			"having count(t1) >= :minTaggings " +
		  			"order by count(t1) desc"),			  			
	  @NamedQuery(name="artResource.byNotIdListRandom",
	  			query="select r from ArtResource r " +
	  				  "where r not in (:others) and " +
	  				  "r.enabled=true " +
	  				  "order by random()"),
	  @NamedQuery(name="artResource.resourcesByGameRoundId",
  				query="select r from GameRound g " +
  					  "inner join g.resources r " +
  					  "where g.id=:id and " +
  					  "r.enabled=true "
	  					),
  	  /*@NamedQuery(name="artResource.playedResourceIdsByGameRoundId",
  			  	query="select res.id from Description d " +
  			  			"left join d.gameRound g " +
  			  			"left join g.resources res " +
  			  			"where g.id=:id and d.tagRating.resource.id=res.id " +
  			  			"group by res.id, res.enabled " +
  			  			"order by min(d.created)"),*/
		@NamedQuery(name="artResource.playedResourceIdsByGameRoundId",
			  	query="select r.id from Tagging t " +
			  			"left join t.gameRound g " +
			  			"left join g.resources r " +
			  			"where g.id=:id and t.resource.id=r.id and " +
			  			"r.enabled=true " +
			  			"group by r.id, r.enabled " +
			  			"order by min(t.created)"),
	@NamedQuery(name="artResource.FrequencyByIdListAndTagNames",
  			  	query="select new gwap.tools.ArtResourceFrequency(r, count(t1)) " +
  			  			"from ArtResource r " +
  			  			"inner join r.taggings t1 " +
  			  			"where r.id in (:resids) and t1.tag in (:tags) " +  			  			
  			  			"and t1.tag.language=:lang " +
  			  			"group by r.id, r.enabled, r.artist, r.dateCreated, r.institution, r.location, r.path, r.source "),
  	  @NamedQuery(name="artResource.FrequencyInTagsByIdListAndTagNames",
  			  query="select new gwap.tools.ArtResourceFrequency(r, count(distinct t1.tag)) " +
  			  		"from ArtResource r " +
  			  		"inner join r.taggings t1 " +
  			  		"where r.id in (:resids) and t1.tag in (:tags) " +
  			  		"and t1.tag.language=:lang " +
  			  		"and t1.person.id!=:personid " +
  			  		"group by r.id, r.enabled, r.artist, r.dateCreated, r.institution, r.location, r.path, r.source"),  			  		
  	   @NamedQuery(name="artResource.tagCount",
  			  	query="select count(distinct tag) from Tagging t " +
  			  			"left join t.tag tag " +
  			  			"where t.resource.id=:resid and tag.language=:lang"),
	   @NamedQuery(name="artResource.tagCountForTagname",
			  	query="select count(distinct tag) from Tagging t " +
			  			"left join t.tag tag " +
			  			"where t.resource.id=:resid and tag.language=:lang and lower(tag.name)=:tagname"),  			  			
 	   @NamedQuery(name="artResource.taggingCount",
 			   	query="select count(t) from Tagging t " +
 			   			"where t.resource.id=:resid and t.tag.language=:lang"),
 	   @NamedQuery(name="artResource.gameLocations",
  			  	query="select a from ArtResource a " +
  			  			"join a.virtualTaggings v join v.virtualTaggingTypes t " +
  			  			"where t.id = :virtualTaggingTypeId " +
  			  			"and a.shownLocation.type = 'APP' " +
  			  		    "and not exists (from ArtResource a2 where a2.isVersionOf.artist.deviceId = :deviceId and a2 = a)"),
  	   @NamedQuery(name="artResource.getRandomPictures",
  			  	query="select a from ArtResource a " +
  			  			"where a.origin = :origin and " +
  			  			"not exists (from ArtResourceRating r where r.person.deviceId = :deviceId and r.resource = a) " +
  			  			"order by random()"),
	   @NamedQuery(name="artResource.byOriginAndDeviceId",
			  	query="select count(*) from ArtResource a where a.origin = :origin and a.artist.deviceId = :deviceId"),
	   @NamedQuery(name="artResource.hasBeenPlayedSince",
			   	query="select count(*) from ArtResource r join r.gameRounds g join g.person p " +
			   			"where coalesce(p.personConnected, p) = :person " +
			   			"and g.startDate >= :startDate " +
						"and r = :resource")
  	})

/**
 * ArtResource that can be tagged in a game. The resource could be an image that
 * is tagged with "tree". 
 * 
 * @author Fabian Kneißl
 * 
 */

@Entity
@Name("artResource")
@Scope(ScopeType.CONVERSATION)
public class ArtResource extends Resource {
	
	private static final long serialVersionUID = 1L;
	
	public static final String ORIGIN_APP_USER = "vom Nutzer gemacht";
	public static final String ORIGIN_APP_CRIMESCENE = "Bild aus App-Spiel";

	@ManyToOne
	private Person artist;
	
	@ManyToOne							private Source source;
	@OneToMany(mappedBy="resource",
		cascade=CascadeType.PERSIST)	private Set<ArtResourceTitle> titles = new HashSet<ArtResourceTitle>();
	@OneToMany(mappedBy="resource")		private List<ArtResourceTeaser> teasers = new ArrayList<ArtResourceTeaser>();
	@OneToMany(mappedBy="resource",
		cascade=CascadeType.PERSIST)    private Set<ArtResourceRating> ratings = new HashSet<ArtResourceRating>();
	@OneToOne                           private Location shownLocation;   // Ort der Abblidung (ex. Marienplatz)
	@ManyToOne                          private ArtResource isVersionOf;  // A photo references the original artwork 
	
	private String path;
	private String dateCreated;
	private String location;        // Ausstellungsort (ex. Staatliche Kunstsammlung ...)
	
	private String institution;  // Einrichtung
	@Lob
	private String origin;       // Kauf, Leihgabe, Geschenk, Mittelverwendung 
	private Boolean easement;  // permission to use the ArtResource is given by the author
	private Boolean skip;  // if ArtResource is not suitable to be tagged by users (e.g., boring)
	@Transient
	private String url;
	
	public String getDefaultTitle() {
		if (titles.size() == 1)
			return titles.iterator().next().getTitle();
		else if (titles.size() > 1)
			//FIXME: do it more sensibly
			return titles.iterator().next().getTitle();
		else
			return "";
	}
	
	public String getArtistName() {
		try {
			String artistAsString = "";
			if (artist.getForename() != null)
				artistAsString += artist.getForename() + " ";
			if (artist.getSurname() != null)
				artistAsString += artist.getSurname();
			return artistAsString;
		} catch (NullPointerException e) {
			return null;
		}
	}
	
	public Person getArtist() {
		return artist;
	}
	public void setArtist(Person artist) {
		this.artist = artist;
	}
	public Set<ArtResourceTitle> getTitles() {
		return titles;
	}
	public void setTitles(Set<ArtResourceTitle> titles) {
		this.titles = titles;
	}
	public String getDateCreated() {
		return dateCreated;
	}
	public void setDateCreated(String dateCreated) {
		this.dateCreated = dateCreated;
	}
	public String getLocation() {
		return location;
	}
	public void setLocation(String location) {
		this.location = location;
	}
	public String getInstitution() {
		return institution;
	}	
	public void setInstitution(String institution) {
		this.institution = institution;
	}
	public String getOrigin() {
		return origin;
	}
	public void setOrigin(String origin) {
		this.origin = origin;
	}
	public Source getSource() {
		return source;
	}
	public void setSource(Source source) {
		this.source = source;
	}
	public List<ArtResourceTeaser> getTeasers() {
		return teasers;
	}
	public void setTeasers(List<ArtResourceTeaser> teasers) {
		this.teasers = teasers;
	}
	public String getPath() {
		return path;
	}
	public void setPath(String path) {
		this.path = path;
	}	
	public void setUrl(String url) {
		this.url = url;
	}
	public String getUrl() {
		if (url == null)
			ImageAccessBean.setResourceUrl(this);
		return url;
	}
	public Boolean getEasement() {
		return easement;
	}
	public void setEasement(Boolean easement) {
		this.easement = easement;
	}
	public Boolean getSkip() {
		return skip;
	}
	public void setSkip(Boolean skip) {
		this.skip = skip;
	}
	public void setShownLocation(Location shownLocation) {
		this.shownLocation = shownLocation;
	}
	public Location getShownLocation() {
		return shownLocation;
	}
	public ArtResource getIsVersionOf() {
		return isVersionOf;
	}

	public void setIsVersionOf(ArtResource isVersionOf) {
		this.isVersionOf = isVersionOf;
	}

	public Set<ArtResourceRating> getRatings() {
		return ratings;
	}

	public void setRatings(Set<ArtResourceRating> ratings) {
		this.ratings = ratings;
	}

	@Override
	public String toString() {
		return "ArtResource#" + id;
	}
}
