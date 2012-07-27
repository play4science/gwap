package gwap.game.test;

import java.util.ArrayList;
import java.util.List;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;

@Name("gwapGameTestSharedGame")
@Scope(ScopeType.CONVERSATION)
public class SharedGame extends gwap.game.SharedGame<Player> {

	List<String> messages = new ArrayList<String>();

	public SharedGame() {
		messages = new ArrayList<String>();
	}

	@Override
	public boolean isCompatible(gwap.game.SharedGame game) {
		return (game instanceof SharedGame);
	}

	public List<String> getMessages() {
		return messages;
	}

	public void sendMessage(String message) {
		messages.add(message);
	}

	@Override
	public void startNewRound() {
	}
}
