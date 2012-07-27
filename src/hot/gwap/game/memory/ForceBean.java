package gwap.game.memory;

import java.io.Serializable;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;

@Scope(ScopeType.SESSION)
@Name("gwapGameMemoryForceBean")
public class ForceBean implements Serializable {
	
	private static final long serialVersionUID = 1L;
	
	private String enteredId;
	
	private long forcedId=0;

	public long getForcedId() {
		return forcedId;
	}
	
	public void updateForcedId()
	{
		long v;
		try
		{
			v=Long.parseLong(enteredId);					
		}
		catch (Exception e)
		{
			forcedId=0;
			return;
		}
		
		forcedId=v;
	
	}


	public String getEnteredId() {
		return enteredId;
	}

	public void setEnteredId(String enteredId) {
		this.enteredId = enteredId;
	}
}
