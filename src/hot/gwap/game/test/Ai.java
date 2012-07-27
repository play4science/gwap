package gwap.game.test;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;

@Name("gwapGameTestAi")
@Scope(ScopeType.CONVERSATION)
public class Ai extends Player {
	private static final long serialVersionUID = 1L;

	@Override
	public boolean isAi() {
		return true;
	}

	@Override
	public boolean isTimedOut() {
		return false;
	}

	@Override
	public synchronized String poll(int round) {
		setMessage("This is the AI!");
		sendMessage();
		return "";
	}
}
