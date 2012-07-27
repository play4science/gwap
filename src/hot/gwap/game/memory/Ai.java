package gwap.game.memory;

import gwap.game.RecommendedTag;
import gwap.model.Tag;
import gwap.model.action.Tagging;
import gwap.model.resource.ArtResource;
import gwap.tools.ArtResourceFrequency;
import gwap.tools.ArtResourceFrequencyBean;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Random;
import java.util.Set;

import javax.persistence.EntityManager;
import javax.persistence.Query;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Create;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Logger;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.international.LocaleSelector;
import org.jboss.seam.log.Log;

@Name("gwapGameMemoryAi")
@Scope(ScopeType.CONVERSATION)
public class Ai extends Player {

	@In
	private SharedGame gwapGameMemorySharedGame;
	@In
	private ReplayBean gwapGameMemoryReplayBean;
	@In
	private Player gwapGameMemoryPlayer;
	@In	private LocaleSelector localeSelector;
	@In(create = true)
	private ArtResourceFrequencyBean artResourceFrequencyBean;

	@In
	private EntityManager entityManager;

	@Logger
	private Log log;

	@In(create = true)
	private RecommendedTag recommendedTag;

	private List<ArtResource> guesserResources = new ArrayList<ArtResource>();
	private ArtResource lastResource = null;
	private List<ArtResource> candidateResources = null;	
	private Set<Tag> allTags = null;
	private int lastSizeDescriptions;
	private int lastSizeAnswers;
	private long nextAction;
	private long nextActionForce;
	private boolean noTags;

	public Ai() {
	}

	@Create
	public void Init() {
	}

	@Override
	public boolean isAi() {
		return true;
	}

	@Override
	public boolean isTimedOut() {
		return false;
	}

	private void newResource(ArtResource resource)
	{
		allTags=gwapGameMemoryReplayBean.allTagsForResource(resource);
		
		if (allTags.size()<5)
			noTags=true;
		else
			noTags=false;
			
		lastResource=resource;		
		nextAction=0;
		nextActionForce=new Date().getTime()+100000;		
	}
	
	@Override
	public synchronized String poll(int round)
	{		
		notified();
		
		ResourceGridBean rg=gwapGameMemorySharedGame.getResourceGridBean();
		ArtResource resource=rg.getGoal();
		
		boolean newResource=false;
		if (resource!=null && resource!=lastResource)
		{
			newResource(resource);
			newResource=true;
		}
		
		if (isGuesser())
		{			
			if (resource!=null)
			{
				List<Tagging> descriptions=gwapGameMemorySharedGame.getDescriptions();
				List<Question> answers=gwapGameMemorySharedGame.getAnswers();
				if (newResource)
				{
					guesserResources=new ArrayList<ArtResource>(rg.getValidResources());
					lastSizeDescriptions=descriptions.size();
					lastSizeAnswers=answers.size();
				}
				
				ArtResource guess=null;
				
				//New description added or question answered
				if ((descriptions.size()!=lastSizeDescriptions ||
					 answers.size()!=lastSizeAnswers
					 || new Date().getTime()>nextActionForce) &&
					 new Date().getTime()>nextAction)
				{
					nextActionForce=new Date().getTime()+2000+new Random().nextInt(4000);
					if (descriptions.size()>0)
					{						
						lastSizeDescriptions=descriptions.size();
						lastSizeAnswers=answers.size();
						
						Query query=entityManager.createNamedQuery("artResource.FrequencyInTagsByIdListAndTagNames");
						List<Long> resIds=new ArrayList<Long>();
						for (ArtResource r : guesserResources)
						{
							resIds.add(r.getId());
						}
						
						List<Tag> tags=new ArrayList<Tag>();
						for (Tagging t : descriptions)
						{
							tags.add(t.getTag());
						}
						
						query.setParameter("resids", resIds);
						query.setParameter("tags", tags);					
						query.setParameter("lang", localeSelector.getLanguage());
						query.setParameter("personid", gwapGameMemoryPlayer.getPerson().getId());
						List<ArtResourceFrequency> res=query.getResultList();
	
						List<Tag> blocks=new ArrayList<Tag>();
						for (Question a : answers)
						{
							if (a.getAnswer()==1)
							{								
								
							}							
							else if (a.getAnswer()==2)
							{
								blocks.add(a.getQuestion());								
							}
						}					
						
						if (blocks.size()>0)
						{
							query.setParameter("tags", blocks);				
						
							//Res2 contains the number of blocked tags that have been applied to a given resource
							List<ArtResourceFrequency> res2=query.getResultList();
							artResourceFrequencyBean.normalizeAll(res, 1.0/tags.size(),res2);
						}
						else
						{
							artResourceFrequencyBean.normalizeAll(res, 1.0/tags.size());
						}
						
						gwapGameMemorySharedGame.TESTsetFreq(res);
						signalPartner("images");

						candidateResources=null;
						
						//Target Image is not tagged, fall back to random guessing
						if (noTags)
						{
							if (res.size()>0)
							{	
								if (res.get(0).getCount()>new Random().nextDouble()+0.3)
									guess=res.get(new Random().nextInt(res.size())).getResource();
								else
									guess=rg.getGoal();
							}
							else
							{
								Random r=new Random();
								if (descriptions.size()>=r.nextInt(3)) {
									if (r.nextDouble()>0.2)
										guess=rg.getGoal();
									else
										guess=guesserResources.get(new Random().nextInt(guesserResources.size()));
								}
								else	//Take an arbitrary resource to ask questions
								{
									candidateResources=new ArrayList<ArtResource>();
									candidateResources.add(guesserResources.get(r.nextInt(guesserResources.size())));
								}
							}
								
						}
						else
						{
							if (res.size()>0)
							{												
								if (res.size()==1)
									guess=res.get(0).getResource();
								else
								{
									Random r=new Random();
									if ((res.get(0).getCount()>=1.0 && res.get(1).getCount()<1.0) ||	//All tags are applied to only one resource
										(res.get(0).getCount()/res.get(1).getCount()>1.2) ||		
										descriptions.size()>=2+r.nextInt(3))
											guess=res.get(0).getResource();
									else
									{
										candidateResources=new ArrayList<ArtResource>();
										int i=0;
										while (i<res.size() && res.get(0).getCount()/res.get(i).getCount()<2)
										{
											candidateResources.add(res.get(i).getResource());
											i++;											
										}
									}
								}						
							}
							else
							{
								Random r=new Random();
								if (descriptions.size()>=r.nextInt(3))		
								{
									if (r.nextDouble()>0.3)
										guess=rg.getGoal();
									else
										guess=guesserResources.get(r.nextInt(guesserResources.size()));
								}
								else	//Take an arbitrary resource to ask questions
								{
									candidateResources=new ArrayList<ArtResource>();
									candidateResources.add(guesserResources.get(r.nextInt(guesserResources.size())));
								}
							}
						}						
					}	
				}
			
				if (guess==null && gwapGameMemorySharedGame.getAllowDescriptions() && 
						(gwapGameMemorySharedGame.getAlternatingModeTurn()==1 || !gwapGameMemorySharedGame.getAlternatingMode()))
				{
					if ((candidateResources==null || candidateResources.size()==0) && gwapGameMemorySharedGame.getAlternatingMode()) //We have to make a guess, lest the game is blocked
					{							
						candidateResources=new ArrayList<ArtResource>();
						candidateResources.add(guesserResources.get(new Random().nextInt(guesserResources.size())));							
					}

					if (candidateResources!=null && new Date().getTime()>nextAction)
					{
						nextAction=new Date().getTime()+1000+new Random().nextInt(6000);
						
						boolean done=false;
						if (candidateResources.size()>1)
						{
							
							ArtResource r=candidateResources.get(0);
														
							Query query=entityManager.createNamedQuery("tag.uniqueByResourceList");
							
							List<Tag> taglist=new ArrayList<Tag>();
							if (descriptions.isEmpty())
								taglist.add(new Tag());
							else
							{
								for (Tagging t : descriptions)
								{
									taglist.add(t.getTag());									
								}
							}
							
							query.setParameter("language", localeSelector.getLanguage());
							query.setParameter("res", r);
							query.setParameter("reslist", candidateResources);
							query.setParameter("taglist", taglist);
							query.setMaxResults(3);
							List<Tag> tags=query.getResultList();
							if (tags.size()>0)
							{
								log.info("Found unique tags for resource: #0", r.getId());															
								for (Tag t:tags)
								{
									log.info("#0",t.getName());
								}								
								
								Tag t=tags.get(new Random().nextInt(tags.size()));
								
								setEnteredTag(t.getName());
								sendQuestion(round);
								done=true;
							}
						}
						
						if (!done)
						{
							ArtResource r=candidateResources.get(new Random().nextInt(candidateResources.size()));
							Set<Tag> tag=gwapGameMemoryReplayBean.allTagsForResource(r, 1);
							Iterator<Tag> t=tag.iterator();
							if (t.hasNext())
							{
								setEnteredTag(t.next().getName());
								sendQuestion(round);
								done=true;
							}
						}
						
						if (!done)
						{
							guess=guesserResources.get(new Random().nextInt(guesserResources.size()));
						}
							
					}						
				}
				
				if (guess!=null)
				{					
					guesserResources.remove(guess);
					//Select resource
					gwapGameMemorySharedGame.resourceClicked(guess, round, this);
					//Confirm
					gwapGameMemorySharedGame.resourceClicked(guess, round, this);
				} //Ask a question, if currently possible								
				
								
			}
		}
		else			
		{			
			//Simulate Describer (to verify tags)			
			if (resource==null)
			{
				log.info("Selecting new resource");				

				ArtResource next=gwapGameMemoryReplayBean.getNextResource();
				if (next!=null)
				{					
					gwapGameMemorySharedGame.resourceClicked(next, round, this);					
					resource=next;
				}
				else {
					//Select a random resource					
					List<ArtResource> resources=new ArrayList<ArtResource>(gwapGameMemorySharedGame.getResourceGridBean().getResources());
					Collections.shuffle(resources);
					for (ArtResource r:resources)
					{
						if (r!=null)
						{
							resource=r;
							gwapGameMemorySharedGame.resourceClicked(resource, round, this);
							break;
						}				
					}
				}			
				newResource=true;
				newResource(resource);
			}
	
			if (newResource)
			{
				gwapGameMemoryReplayBean.updateDescriptions(resource);					
			}

			//Send descriptions
			if (gwapGameMemorySharedGame.getAllowDescriptions() &&
			   (gwapGameMemorySharedGame.getAlternatingModeTurn()==0 || !gwapGameMemorySharedGame.getAlternatingMode()))
				
			{
				List<Tagging> ts;
				
				if (gwapGameMemorySharedGame.getAlternatingMode())
					ts=gwapGameMemoryReplayBean.getDescriptions(1);
				else
					ts=gwapGameMemoryReplayBean.getDescriptions();
					
				
				for (Tagging t : ts)		
				{				
					gwapGameMemorySharedGame.sendDescription(t, round);					
				}				
				gwapGameMemoryPlayer.signal("tags");
			}
			
			//Answer questions
			if (gwapGameMemorySharedGame.getAllowQuestions() &&
			   (gwapGameMemorySharedGame.getAlternatingModeTurn()==2 || !gwapGameMemorySharedGame.getAlternatingMode()))
			{
				List<Question> questions=gwapGameMemorySharedGame.getQuestions();
				while (questions.size()>0)
				{
					Question q=questions.get(0);
					
					//if tags are available, answer
					if (!noTags)
					{
						if (allTags!=null && allTags.contains(q.getQuestion()))
							answerQuestion(q,1, round);
						else
							answerQuestion(q,2, round);
					}			
					//if there are no tags and we are in alternating mode,
					//answer randomly to avoid blocking the game
					else if (gwapGameMemorySharedGame.getAlternatingMode())
					{
						answerQuestion(q,1+new Random().nextInt(2), round);					
					}

				}
				if (gwapGameMemorySharedGame.getAlternatingMode())
				{
					//Wait a bit
					nextAction=new Date().getTime()+2000+new Random().nextInt(5000);
				}
				gwapGameMemorySharedGame.getQuestions().clear();
			
			}
		}	
		
		
		return "";
	}
}
