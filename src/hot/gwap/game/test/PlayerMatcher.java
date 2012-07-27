package gwap.game.test;

import gwap.tools.Pair;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Out;
import org.jboss.seam.annotations.Scope;

@Scope(ScopeType.APPLICATION)
@Name("gwapGameTestPlayerMatcher")
public class PlayerMatcher extends gwap.game.PlayerMatcher<SharedGame, Player> {

	private static final long serialVersionUID = 1L;

	@Out(required = true)
	private SharedGame gwapGameTestSharedGame;
	
	@Out(required = true)
	private Player gwapGameTestPlayer;

	public PlayerMatcher() {
		super("gwapGameTest");
	}
	
	public void enqueue() {
		Pair<SharedGame, Player> p = match();
		gwapGameTestSharedGame = p.a;
		gwapGameTestPlayer = p.b;
	}
}
