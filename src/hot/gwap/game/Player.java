package gwap.game;

import gwap.model.GameRound;
import gwap.model.Person;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.EventListener;
import java.util.List;

import org.ajax4jsf.event.PushEventListener;
import org.jboss.seam.annotations.End;
import org.jboss.seam.annotations.Logger;
import org.jboss.seam.log.Log;

public abstract class Player<P extends Player<P>> implements Serializable {
	@Logger	                 private Log log;


	//Time to wait before user is considered disconnected
	final long TIMEOUT=10000;
	
	//Time to wait for a second player before adding an Ai
	final long WAIT_TIMEOUT=6000;
	
	//Set to true to allow player to be matched with an Ai
	private boolean allowAi=false;
	

	/* This method should be implemented and called from the view using
	 * a PUSH object
	 * The returned value is used to redirect the user
	 * See testGame for further examples 
	 */
	public abstract String notified();
	
	private SharedGame sharedGame;
	private PlayerMatcher playerMatcher;

	private	Person person=null;
	
	private P partner=null;
	
	private Date lastAccess=new Date();
	private Date created=new Date();
	
	private List<String> notifiers=new ArrayList<String>();

	private int id;
	
	private String conversation; 
		
	private List<GameRound> gameRounds=new ArrayList<GameRound>();
	private GameRound gameRound;
	
	private PushEventListener listener;	


	
	public String getConversation() {
		return conversation;
	}
	
	public void setConversation(String conversation) {
		this.conversation = conversation;
	}
	
	public void addNotifierAll(String notifier)
	{
		addNotifier(notifier);
		if (partner!=null)
			partner.addNotifier(notifier);
	
	}
	
	/* Strange effect, that I don't really understand:
	 * If this method is synchronized, deadlocks ensue (how? this method does nothing complicated)
	 * If there's an inner synchronized block, everything appears to be fine
	 * 
	 * http://www.javamex.com/tutorials/synchronization_synchronized_method.shtml states:
	 * "When synchronized is used in a method declaration, it is marked as a flag on the method.
	 * The compiler does not insert instructions to acquire and release the lock; instead, the
	 * VM sees the flag on the method declaration and knows to "automatically" acquire and
	 * release the lock on method entry and exit"
	 * This might be a reason
	 * 
	 * In any case, EJB specification explicitly forbids using java synchronization
	 * 
	 * It appears that the PlayerMatcher and the associated classes will likely fail in a
	 * clustered environment.
	 */
	
	public void addNotifier(String notifier)
	{
		//log.info("Notified: "+notifier);
		synchronized(notifiers)
		{
			if (!notifiers.contains(notifier))
				notifiers.add(notifier);
		}		
	}
	
	public boolean isNotified(String notifier)
	{
		synchronized(notifiers)
		{
			return notifiers.remove(notifier);
		}
	}
	
	public void setAllowAi(boolean allowAi) {
		this.allowAi = allowAi;
	}

	public void setSharedGame(SharedGame sharedGame) {
		this.sharedGame = sharedGame;
	}

	public void setPlayerMatcher(PlayerMatcher playerMatcher) {
		this.playerMatcher = playerMatcher;
	}

	public P getPartner() {
		return partner;
	}

	public void setPartner(P partner) {
		this.partner = partner;
	}
	
	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}
	
	public PushEventListener getListener() {
		return listener;
	}
	
	public Person getPerson() {
		return person;
	}
	
	public void setPerson(Person person) {
		this.person = person;
	}
	
	public boolean isMatched()
	{
		return partner!=null;	
	}
	
	public boolean isTimedOut()
	{
		return (new Date().getTime()-lastAccess.getTime())>TIMEOUT;
	}
	
    public void addListener(EventListener listener) {
        synchronized (listener) {        	
        	this.listener=(PushEventListener)listener;
        	
        	//Make sure notifier action fires at least once
        	this.listener.onEvent(null);
        }        
    }	
    
    public void signalPartner()
    {
    	signalPartner(null);
    }
    
    public void signalPartner(String updated)
    {
    	if (partner!=null)
    		partner.signal(updated);
    }

    public void signalAll(String updated)    
    {
    	signalPartner(updated);
    	signal(updated);
    }
    
    public void signal(String updated)    
    {
    	//log.info("signal #0", updated);
    	if (updated!=null && updated!="")
    	{
    		//log.info("adding notifier");
    		addNotifier(updated);
    	}
    	
    	if (listener!=null)
    		listener.onEvent(null);
    }
    
    public boolean isAi()
    {
    	return false;
    }
    

    public void signal()
    {
    	signal("");
    }

    public void forceTimeout()
    {
		lastAccess=new Date(0);
	}
    
    
    public GameRound getGameRound() {
		return gameRound;
	}

	public void startRound()
	{
    	//sharedGame.getGameSessionBeanNew().getGameSession();
		
    	
		if (!sharedGame.isFirstRound())
			endRound();	
						
		gameRound=new GameRound();
		
		log.info("Starting game round #"+sharedGame.getCurrentRound()+" "+gameRound.hashCode());		
		log.info("gameSession id=#0, code=#1", sharedGame.getGameSessionBeanNew().getGameSession().getId(), sharedGame.getGameSessionBeanNew().getGameSession().hashCode());
		
		gameRound.setNumber(sharedGame.getCurrentRound());
				
		sharedGame.getGameSessionBeanNew().addGameRound(gameRound, isAi()?null:person);
	}
	
	public void endRound()
	{	
		
		gameRound.setScore(sharedGame.getLastScore().intValue());
		gameRounds.add(gameRound);
		
		log.info("Ending game round #0", gameRound.getId());
		
		sharedGame.getGameSessionBeanNew().endGameRound(gameRound);		
	}
	
	public List<GameRound> getGameRounds()
	{
		return gameRounds;
	}
	
    @End
    public synchronized String poll(int round)
    {    
    	lastAccess=new Date();
    	
    	if (partner!=null)
    	{
    		if (partner.isAi())
    		{
    			partner.poll(round);
    		}
    		else if (partner.isTimedOut())
        	{
        		partner=null;
        		forceTimeout();
        		
        		return "abandoned";
        	}   			
    	}	
    	
    	if (sharedGame!=null && sharedGame.isRunning())
    	{    		
    		sharedGame.checkRound(0);
    	}
    	else
    	{
    		if (allowAi && (new Date().getTime()-created.getTime()>WAIT_TIMEOUT))
    		{
	    		if (playerMatcher!=null)
	    			playerMatcher.MatchTimeOut(person);
    		}
    	}
   	
    	
    	return null;
    }

}
