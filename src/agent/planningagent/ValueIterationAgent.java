package agent.planningagent;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import environnement.Action;
import environnement.Etat;
import environnement.MDP;
import util.HashMapUtil;


/**
 * Cet agent met a jour sa fonction de valeur avec value iteration 
 * et choisit ses actions selon la politique calculee.
 * @author laetitiamatignon
 *
 */
public class ValueIterationAgent extends PlanningValueAgent{
	
	protected final boolean TEST = true;
	protected double gamma;
	protected double gammaInit;
	protected HashMapUtil Values;
	
	/**
	 * 
	 * @param gamma
	 * @param mdp
	 */
	public ValueIterationAgent(double gamma,MDP mdp) {
		super(mdp);
		this.gammaInit = gamma;
		//*** VOTRE CODE
		this.reset();
	
	}
	
	public ValueIterationAgent(MDP mdp) {
		this(0.9,mdp);
	}
	
	/**
	 * 
	 * Mise a jour de V: effectue UNE iteration de value iteration 
	 */
	@Override
	public void updateV(){
		//delta est utilise pour detecter la convergence de l'algorithme
		//lorsque l'on planifie jusqu'a convergence, on arrete les iterations lorsque
		//delta < epsilon 
		this.delta=0.0;
		//*** VOTRE CODE
		
		if(this.TEST)
			System.out.println("Calcul de V(s)");
		
		//Copie de l'ancienne Hashmap pour modifier la nouvelle directement
		HashMapUtil duplicateValues = (HashMapUtil)Values.clone();
        for (Etat etat : mdp.getEtatsAccessibles()) {
            double maxValue = 0;// -Double.MAX_VALUE;
            for (Action action : mdp.getActionsPossibles(etat)) {
                double somme = 0;
                double recompense = 0;
                double transition = 0;
                double precedente = 0;
                try {
                	//Pour un état et une action, on récupère la liste des transitions
                	HashMapUtil map = (HashMapUtil) mdp.getEtatTransitionProba(etat, action);
                    for (Etat etatMap : map.keySet()) {
                    	//On calcul la somme à maximiser
                    	transition = map.get(etatMap);
                    	recompense = mdp.getRecompense(etat, action, etatMap);
                    	precedente = gamma * duplicateValues.get(etatMap);
                        somme += transition * (recompense + precedente);
                    }
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
                //Récupération de la valeur max des actions
                maxValue = Math.max(maxValue, somme);
            }
            //Mise à jour de Vk(S) en fonction des valeurs à k-1
            this.Values.put(etat, maxValue);
            
            if(this.TEST)
            	System.out.println("Etat : "+etat+" -> max : "+maxValue);
        }
        
        //Maximisation de l'erreur
        double max_erreur = 0;
        double ancienne = 0;
        double nouvelle = 0;
        for (Etat etat : this.Values.keySet()) {
        	ancienne = duplicateValues.get(etat);
        	nouvelle = this.Values.get(etat);
            max_erreur = Math.max(max_erreur, Math.abs(ancienne - nouvelle));
        }
        this.delta = max_erreur;
        
        //Mise à jour de vmax et vmin pour affichage du gradient de couleur
        //vmax est la valeur max pour tout s de V
      	//vmin est la valeur min pour tout s de V
        this.vmax = 0;
        this.vmin = Integer.MAX_VALUE;
        for (double value : this.Values.values()) {
        	vmax = Math.max(value, vmax);
        	vmin = Math.min(value, vmin);
        }
		
		//******************* a laisser a la fin de la methode
		this.notifyObs();
	}
	
	
	/**
	 * renvoi l'action executee par l'agent dans l'etat e 
	 */
	@Override
	public Action getAction(Etat e) {
		//*** VOTRE CODE
		
		if(this.TEST)
			System.out.println("---------------------------------------------------");
		
		//Mise à jour des valeurs
		updateV();
		
		Action action = null;
		List<Action> actions = getPolitique(e);
		if(actions.size() == 1){
        	action = actions.get(0);
        } else if (actions.size() > 1) {
            int i = new Random().nextInt(actions.size());
            action = actions.get(i);
        }
		
		if(this.TEST)
			System.out.println("Direction choisie : "+action+"\n");
		
		return action;
	}
	
	@Override
	public double getValeur(Etat _e) {
		//*** VOTRE CODE
        return this.Values.get(_e);
	}
	/**
	 * renvoi la (les) action(s) de plus forte(s) valeur(s) dans l'etat e 
	 * (plusieurs actions sont renvoyees si valeurs identiques, liste vide si aucune action n'est possible)
	 */
	@Override
	public List<Action> getPolitique(Etat _e) {
		List<Action> actions = new ArrayList<Action>();
		//*** VOTRE CODE
		
        double v_max = -Integer.MAX_VALUE;
        
        if(this.TEST)
        	System.out.println("\nPour l'état : "+_e);
        
        for(Action action : mdp.getActionsPossibles(_e)) {
            try {
            	HashMapUtil map = (HashMapUtil)mdp.getEtatTransitionProba(_e, action);
                double somme = 0.0;
                double recompense = 0.0;
                double transition = 0.0;
                double precedente = 0.0;
                
                //Calcul de la somme pour chaque état
                for(Etat etat_suivant : map.keySet()) {
                	transition = map.get(etat_suivant);
                	recompense = mdp.getRecompense(_e, action, etat_suivant);
                	precedente = gamma * getValeur(etat_suivant);
                    somme += transition * (recompense + precedente);
                }
                
                //Si la somme est supérieur, on vide la liste et on ajoute l'action
                if(somme > v_max) {
                	actions.clear();
                	actions.add(action);
                    v_max = somme;
                } //Si égale, on ajoute l'action
                else if(somme == v_max) {
                	actions.add(action);
                }
                
                if(this.TEST)
                	System.out.println("	Action : "+action.toString()+" ("+action.ordinal()+") -> somme : "+somme);
                
                
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        
        return actions;
	}
	
	@Override
	public void reset() {
		super.reset();
		//*** VOTRE CODE
		this.gamma = this.gammaInit;
        this.Values = new HashMapUtil();
        for (Etat e : mdp.getEtatsAccessibles()) {
            this.Values.put(e, 0.0);
        }
        
		/*-----------------*/
		this.notifyObs();
	}

	@Override
	public void setGamma(double gamma) {
		this.gamma = gamma;
	}
}
