package gwap.game.test;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;

@Scope(ScopeType.CONVERSATION)
@Name("gwapGameTestPlayer")
public class Player extends gwap.game.Player<Player> {

	private static final long serialVersionUID = 1L;

	@In
	private SharedGame gwapGameTestSharedGame;

	private String message;

	public Player() {
		setAllowAi(true);
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public void sendMessage() {
		gwapGameTestSharedGame.sendMessage(message);
		message = "";
		signalPartner();
	}

	@Override
	public synchronized String notified() {
		if (isNotified("endGame")) {
			return "home";
		}

		return null;
	}
}
