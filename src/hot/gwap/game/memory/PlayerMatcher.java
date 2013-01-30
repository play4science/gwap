/*
 * This file is part of gwap
 *
 * Copyright (C) 2013
 * Project play4science
 * Lehr- und Forschungseinheit für Programmier- und Modellierungssprachen
 * Ludwig-Maximilians-Universität München
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

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
