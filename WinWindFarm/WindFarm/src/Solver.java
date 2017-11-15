import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Random;

public class Solver {

    WindFarmLayoutEvaluator wfle;
    boolean[][] individuals;
    double[] fits;
    Random rand;
    int worstPosition;
    double maxfit = Double.MIN_VALUE;
    int num_individuals;
    double mutateRate = 0.08;
    
    ArrayList<double[]> grid;

    public Solver(WindFarmLayoutEvaluator evaluator) {
        wfle = evaluator;
        rand = new Random();
        grid = new ArrayList<double[]>();
        
        // set up any parameter here, e.g pop size, cross_rate etc.
        num_individuals = 50;  // change this to anything you want
        
        
    }
    
    
   
    public void run_cw() {
    

    	
    /************set up grid for scenario chosen  ***************************/	
    // do not change or remove this section of code
    // centers must be > 8*R apart and avoid obstacles
    	
  	double interval = 8.001 * wfle.getTurbineRadius();

  	for (double x=0.0; x<wfle.getFarmWidth(); x+=interval) {
  	    for (double y=0.0; y<wfle.getFarmHeight(); y+=interval) {
                boolean valid = true;
                for (int o=0; o<wfle.getObstacles().length; o++) {
                    double[] obs = wfle.getObstacles()[o];
                    if (x>obs[0] && y>obs[1] && x<obs[2] && y<obs[3]) {
                        valid = false;
                    }
                }

                if (valid) {
                    double[] point = {x, y};
                    grid.add(point);
                }
            }
        }
  	
  	
  	
  	/************initialize a population:*****************************/
  	
        //  the variable grid.size() denotes the 
  		// maximum number of turbines for the given scenario
  	
        individuals = new boolean[num_individuals][grid.size()];
        fits = new double[num_individuals];

        for (int p=0; p<num_individuals; p++) {
            for (int i=0; i<grid.size(); i++) {
                individuals[p][i] = rand.nextBoolean();
            }
        }

       /****** evaluate initial population  *************************/
     
        // this populates the fit[] array with the fitness values for each solution 0.0012725460166271383
        evaluate();

        /**** PUT YOUR OPTIMISER CODE HERE ***********/
        
        for (int i=0; i<(1000); i++) {
        	System.out.println(i);
        	boolean[] parent1 = selection(2);
        	boolean[] parent2 = selection(2);

			boolean[] c1 = crossover(parent1, parent2);
			
			c1 = mutation(c1);

			double childFitness = evaluate_individual(c1);
			
			if(childFitness > maxfit){
				individuals[worstPosition] = c1;
				fits[worstPosition] = childFitness;
				System.out.println("replacement " + childFitness); 
			}

        	     	
        }
        evaluate();
      }

	public boolean[] selection(int tnSize)
	{	
			ArrayList<boolean[]> tournament = new ArrayList<boolean[]>();
			boolean[] chosenParent = null;
			int pos = rand.nextInt(this.individuals.length-1);
			double bestFitness = 100;
			
			for(int i = 0; i < tnSize; i++)
			{	
				tournament.add(this.individuals[pos]);
			}
			
			for(boolean[] indiv : tournament)
			{
				if(fits[pos] < bestFitness)
				{
					bestFitness = fits[pos];
					chosenParent = indiv;
				}
			}
			return chosenParent;
	}
    
    private boolean[] crossover(boolean[] p1, boolean[] p2) {
		boolean[] c1 = new boolean[this.grid.size()];
		
			int max = this.grid.size() - 1;
			int min = 1;
			int crossoverPoint = rand.nextInt(max + min) + min;
			for(int k = 0; k < p1.length; k++)
			{
				 if (k<crossoverPoint){
				      c1[k] = p1[k];
				 }else if(k < this.grid.size()){
				    c1[k]= p2[k];
				    }
				 }

		return c1;
	}
    
    private boolean[] mutation(boolean[] child) {
		
			for(int i = 0; i < child.length; i++){
				double mutateChance = new BigDecimal(rand.nextDouble()).setScale(2, RoundingMode.HALF_UP).doubleValue();
				if(mutateChance <= mutateRate)
				{
					if(child[i] == false){
						child[i] = true;
					} 
					else{
						child[i] = false;
					}
				}
			}
		return child;
	}
    
    // evaluate a single chromosome
    private double evaluate_individual(boolean[] child) {
 
       
         int nturbines=0;
         for (int i=0; i<grid.size(); i++) {
                if (child[i]) {
                    nturbines++;
                }
         }
            

          double[][] layout = new double[nturbines][2];
            int l_i = 0;
            for (int i=0; i<grid.size(); i++) {
                if (child[i]) {
                    layout[l_i][0] = grid.get(i)[0];
                    layout[l_i][1] = grid.get(i)[1];
                    l_i++;
                }
            }
	    
	    double coe;
	    if (wfle.checkConstraint(layout)) {
		wfle.evaluate(layout);
		coe = wfle.getEnergyCost();
		//System.out.println("layout valid");
	    } else {
		coe = Double.MAX_VALUE;
	    }

     return coe;
	  
        
    }

    // evaluates the whole population 0.0014990008161565244
    private void evaluate() {
        double minfit = Double.MAX_VALUE;
        for (int p=0; p<num_individuals; p++) {
            int nturbines=0;
            for (int i=0; i<grid.size(); i++) {
                if (individuals[p][i]) {
                    nturbines++;
                }
            }

            double[][] layout = new double[nturbines][2];
            int l_i = 0;
            for (int i=0; i<grid.size(); i++) {
                if (individuals[p][i]) {
                    layout[l_i][0] = grid.get(i)[0];
                    layout[l_i][1] = grid.get(i)[1];
                    l_i++;
                }
            }
	    
	    double coe;
	    if (wfle.checkConstraint(layout)) {
		wfle.evaluate(layout);
		coe = wfle.getEnergyCost();
	    } else {
		coe = Double.MAX_VALUE;
	    }

            fits[p] = coe;
            if (fits[p] < minfit) {
                minfit = fits[p];
            }
            if (fits[p] > maxfit) {
                maxfit = fits[p];
                worstPosition = p;
            }
        }
        System.out.println("min " + minfit);
    }

    
    
}
