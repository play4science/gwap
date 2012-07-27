package gwap.game;

import gwap.model.CombinedTag;
import gwap.model.GameRound;
import gwap.model.resource.ArtResource;

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
import org.jboss.seam.annotations.Out;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.annotations.datamodel.DataModel;
import org.jboss.seam.core.Events;
import org.jboss.seam.international.LocaleSelector;
import org.jboss.seam.log.Log;

@Name("combineOpponentBean")
@Scope(ScopeType.PAGE)
public class CombineOpponentBean implements Serializable {

	private static final long serialVersionUID = 1L;

	@Create	                 public void init()    { log.info("Creating"); }
	@Destroy                 public void destroy() { log.info("Destroying"); }

	@Logger                  private Log log;	
	@In                      private EntityManager entityManager;
	@In                      private LocaleSelector localeSelector;
	@In(create=true)         private ArtResource resource;
	@In(required=false)      private CombineGameSessionBean combineGameSessionBean;
	@In(create=true) @Out	 private GameRound gameRound;
	@DataModel               private List<CombinedTag> opponentCombinedTags = new ArrayList<CombinedTag>();

	private List<CombinedTag> allOpponentTags;
	private List<Date> allOpponentTagsDelay = new ArrayList<Date>();
	private boolean updated = false;
	private Integer maxOpponentTags = 15;
	
	@SuppressWarnings("unchecked")
	public void initAllOpponentTags() {
		opponentCombinedTags = new ArrayList<CombinedTag>();
		
		Query query = entityManager.createNamedQuery("combination.randomCombinedTagsByResourceAndLanguage");
		query.setParameter("resource", resource);
		query.setParameter("language", localeSelector.getLanguage());
		query.setMaxResults(maxOpponentTags);
		allOpponentTags = query.getResultList();
		
		log.info("allOpponentTags (resId=#1, lang=#2): #0", allOpponentTags, resource.getId(), localeSelector.getLanguage());
		
		Calendar calendar = new GregorianCalendar();
		Random random = new Random();
		calendar.setTime(gameRound.getStartDate());
		
		calendar.add(Calendar.MILLISECOND, 1500 + random.nextInt(3000)); // initial delay
		
		for (int i=0; i < allOpponentTags.size(); i++) {
			calendar.add(Calendar.MILLISECOND, 2500 + random.nextInt(4000));
			allOpponentTagsDelay.add(calendar.getTime());
		}
		
		gameRound.setOpponentCombinedTags(opponentCombinedTags);
	}
	
	@Factory("opponentCombinedTags")
	public String updateOpponentTags() {
		if (combineGameSessionBean.roundExpired()) {
			return "next";
		} else {
			if (allOpponentTags.size() > 0 && allOpponentTagsDelay.get(0).before(new Date())) {
				opponentCombinedTags.add(allOpponentTags.remove(0));
				allOpponentTagsDelay.remove(0);
				updated = true;
				
				log.info("opponentCombinedTags (resId=#1): #0", opponentCombinedTags, resource.getId());
			}
			
			Events.instance().raiseEvent("checkForMatchingCombinedTags");
		}
		
		return null;
	}

	public boolean getUpdated() {
		return updated;
	}
	
	public List<CombinedTag> getOpponentTags() {
		return opponentCombinedTags;
	}
}
