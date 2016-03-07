package agent.planningagent;


import java.util.List;
import java.util.Random;

import environnement.Action;
import environnement.Etat;
import environnement.MDP;
/**
 * Cet agent choisit une action aleatoire parmi toutes les autorisees dans chaque etat
 * @author lmatignon
 *
 */
public class AgentRandom extends PlanningValueAgent{
	
	
	public AgentRandom(MDP _m) {
		super(_m);
	}

	@Override
	public Action getAction(Etat e) {
		 List<Action> actions = this.getPolitique(e);
		 
		 if(actions.size() == 0){
			return null; 
		 }
		 
		 Random r = new Random();
		 Action action = actions.get(r.nextInt(actions.size()));
		 
		 return action;
	}

	
	
	@Override
	public double getValeur(Etat _e) {
		return 0.0;
	}

	

	@Override
	public List<Action> getPolitique(Etat _e) {
		return this.mdp.getActionsPossibles(_e);
	}

	@Override
	public void updateV() {
		System.out.println("l'agent random ne planifie pas");
	}

	@Override
	public void setGamma(double parseDouble) {
		// TODO Auto-generated method stub
		
	}




}
