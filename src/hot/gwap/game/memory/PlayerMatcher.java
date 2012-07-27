package gwap.game.memory;

import gwap.tools.Pair;

import org.jboss.seam.Component;
import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Out;
import org.jboss.seam.annotations.Scope;

@Scope(ScopeType.APPLICATION)
@Name("gwapGameMemoryPlayerMatcher")
public class PlayerMatcher extends gwap.game.PlayerMatcher<SharedGame, Player> {
	
	private static final long serialVersionUID = 1L;

	@Out(required=true)		private SharedGame gwapGameMemorySharedGame;
	@Out(required=true)		private Player gwapGameMemoryPlayer;

	public PlayerMatcher()
	{
		super("gwapGameMemory");	
	}

	public void enqueue(int gamePreset)	
	{
		Player p=(Player)Component.forName("gwapGameMemoryPlayer").newInstance();
		SharedGame g=(SharedGame)Component.forName("gwapGameMemorySharedGame").newInstance();
		
		String gameTypeName="gwapGameMemory";
		
		if (gamePreset==1)
		{
			g.setMoveMode(true);
			g.setMoves(30);
			g.setAlternatingMode(true);
			gameTypeName="gwapGameMemoryTurn";
			//p.setAllowAi(false);
		}
		else if (gamePreset==2)
		{
			g.setMoveMode(true);
			g.setMoves(30);
			g.setAllowQuestions(false);
			g.setClusteringMode(true);
			gameTypeName="gwapGameMemoryCluster";
			p.setAllowAi(false);
		}
		

		
		Pair<SharedGame, Player> m=match(g, p, gameTypeName);
		gwapGameMemorySharedGame=m.a;
		gwapGameMemoryPlayer=m.b;		
	}
}
