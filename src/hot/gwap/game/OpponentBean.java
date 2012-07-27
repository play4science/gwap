package gwap.game;

import gwap.model.GameRound;
import gwap.model.Tag;
import gwap.model.resource.ArtResource;
import gwap.tools.TagSemantics;
import gwap.wrapper.TagFrequency;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Random;

import javax.persistence.EntityManager;
import javax.persistence.Query;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Create;
import org.jboss.seam.annotations.Destroy;
import org.jboss.seam.annotations.Factory;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Logger;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.annotations.datamodel.DataModel;
import org.jboss.seam.core.Events;
import org.jboss.seam.international.LocaleSelector;
import org.jboss.seam.log.Log;

@Name("opponentBean")
@Scope(ScopeType.PAGE)
public class OpponentBean implements Serializable {

	private static final long serialVersionUID = 1L;

	@Create	                 public void init()    { log.info("Creating"); initAllOpponentTags(); }
	@Destroy                 public void destroy() { log.info("Destroying"); }

	@Logger                  private Log log;	
	@In                      private EntityManager entityManager;
	@In                      private LocaleSelector localeSelector;
	@In(create=true)         private ArtResource resource;
	@In                      private GameSessionBean gameSessionBean;
	@In                      private GameRound gameRound;
	@In(required=false)      private List<TagFrequency> tabooTags;
	@DataModel               private List<Tag> opponentTags = new ArrayList<Tag>();

	private List<Tag> allOpponentTags;
	private List<Date> allOpponentTagsDelay = new ArrayList<Date>();
	private boolean updated = false;
	private Integer maxOpponentTags = 50;
	
	@SuppressWarnings("unchecked")
	public void initAllOpponentTags() {
		log.info("Updating Results");
		Query query = entityManager.createNamedQuery("tagging.randomTagByResourceAndLanguage");
		query.setParameter("resource", resource);
		query.setParameter("language", localeSelector.getLanguage());
		query.setMaxResults(maxOpponentTags);
		allOpponentTags = query.getResultList();
		if (tabooTags != null) {
			for (int i = 0; i < allOpponentTags.size(); i++) {
				if (TagSemantics.containsNotNormalized2(tabooTags, allOpponentTags.get(i).getName()) != null) {
					allOpponentTags.remove(i);
					i--;
				}
			}
		}
		
		Calendar calendar = new GregorianCalendar();
		Random random = new Random();
		calendar.setTime(gameRound.getStartDate());
		
		if (tabooTags != null)
			calendar.add(Calendar.MILLISECOND, 4000 + random.nextInt(3000)); // initial delay
		else
			calendar.add(Calendar.MILLISECOND, 2000 + random.nextInt(2000)); // initial delay
			
		for (int i=0; i < allOpponentTags.size(); i++) {
			calendar.add(Calendar.MILLISECOND, 1500 + random.nextInt(3000));
			allOpponentTagsDelay.add(calendar.getTime());
		}
		
		log.info(allOpponentTags);
		
		gameRound.setOpponentTags(opponentTags);
	}
	
	@Factory("opponentTags")
	public String updateOpponentTags() {
//		log.info("Updating opponent tags");
		
		if (gameSessionBean.roundExpired()) {
			log.info("Round expired");
			return "next";
		} else if (allOpponentTags.size() > 0) {
			Date nextOpponentTagsUpdate = allOpponentTagsDelay.get(0);
			if (nextOpponentTagsUpdate.before(new Date())) {
				opponentTags.add(allOpponentTags.remove(0));
				allOpponentTagsDelay.remove(0);
				log.info(opponentTags.get(opponentTags.size()-1));
				Events.instance().raiseEvent("checkForMatchingTags");
				updated = true;
			}
		}
		return null;
	}

	public boolean getUpdated() {
		return updated;
	}
	
	public List<Tag> getOpponentTags() {
		return opponentTags;
	}
}
