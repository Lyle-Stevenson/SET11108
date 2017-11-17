import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;

public class Solver {

    WindFarmLayoutEvaluator wfle;
    ArrayList<Chromosome> individuals;
    double[] fits;
    Random rand;
    double maxfit = Double.MIN_VALUE;
    int num_individuals;
    int generations;
    int climbs;
    int chromosomeDistance = 20;
    double mutateRate = 0.08;
    
    ArrayList<double[]> grid;

    public Solver(WindFarmLayoutEvaluator evaluator,int population, int generationsNum, int climbItterations) { 
        wfle = evaluator;
        rand = new Random();
        grid = new ArrayList<double[]>();
        
        // set up any parameter here, e.g pop size, cross_rate etc. 0.0012730013041638835
        num_individuals = population;  // change this to anything you want
        generations = generationsNum; 
        climbs = climbItterations;     
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
  	
        individuals = new ArrayList<Chromosome>();
        fits = new double[num_individuals];

        for (int p=0; p<num_individuals; p++) {
        		Chromosome chromo = new Chromosome(this.grid.size());
                individuals.add(chromo.generateChromosome());
        }

       /****** evaluate initial population  *************************/
     
        // this populates the fit[] array with the fitness values for each solution 0.0012775370037469375
        evaluate();
        
        Collections.sort(individuals);
        System.out.println("min " + individuals.get(individuals.size()-1).getFitness());
        /**** PUT YOUR OPTIMISER CODE HERE ***********/
        
        for (int i=0; i<(generations); i++) {
        	System.out.println(i);
        	Chromosome parent1 = selection(2);
        	Chromosome parent2 = selection(1);
        	Chromosome c1;
        	
        	for(int j = 0; j < 10; j++){
	        	if(calculateDistance(parent1, parent2) > chromosomeDistance || parent1 == parent2){
	        		parent2 = selection(1);
	        	}
        	}
        		
        	c1 = crossover(parent1, parent2);
        	
			c1 = mutation(c1);

			c1.setFitness(evaluate_individual(c1));
			
			c1 = hillclimb(c1);
			
			System.out.println(c1.getFitness() + "  vs " + individuals.get(0).getFitness());
			
			if(c1.getFitness() < individuals.get(0).getFitness()){
				individuals.remove(0);
				individuals.add(c1);
				Collections.sort(individuals);
				System.out.println("replacement " + c1.getFitness()); 
			}

        }
        System.out.println("min " + individuals.get(individuals.size()-1).getFitness());
        System.out.println("Evaluations: " + wfle.getNumberOfEvaluation());
      }

	public Chromosome selection(int tnSize)
	{	
			ArrayList<Chromosome> tournament = new ArrayList<Chromosome>();
			Chromosome chosenParent = null;
			int pos = rand.nextInt(num_individuals-1);
			double bestFitness = 100;
			
			for(int i = 0; i < tnSize; i++)
			{	
				tournament.add(this.individuals.get(pos));
			}
			
			for(Chromosome indiv : tournament)
			{
				if(indiv.getFitness() < bestFitness)
				{
					bestFitness = indiv.getFitness();
					chosenParent = indiv;
				}
			}
			return chosenParent;
	}
    //Uniform crossover.
    private Chromosome crossover(Chromosome p1, Chromosome p2) {
    	Chromosome c1 = new Chromosome(this.grid.size());
		
			for(int k = 0; k < this.grid.size(); k++)
			{
				 int uniformChance = rand.nextInt(1);
				 if (uniformChance == 0){
				      c1.setGene(k, p1.getGene(k));
				 }else{
					 c1.setGene(k, p2.getGene(k));
				    }
				 }

		return c1;
	}
    
    private Chromosome mutation(Chromosome child) {
		
			for(int i = 0; i < this.grid.size(); i++){
				double mutateChance = new BigDecimal(rand.nextDouble()).setScale(2, RoundingMode.HALF_UP).doubleValue();
				if(mutateChance <= mutateRate)
				{
					if(child.getGene(i) == false){
						child.setGene(i, true);
					} 
					else{
						child.setGene(i, false);
					}
				}
			}
		return child;
	}
    
    public Chromosome hillclimb(Chromosome child)
	{	
    		Chromosome temp = child;
			int move = rand.nextInt(this.grid.size());
    		for(int i = 0; i < climbs; i++){
    			if(temp.getGene(move)){
    				temp.setGene(move, false);
    			}else{
    				temp.setGene(move, true);
    			}
    			temp.setFitness(evaluate_individual(temp));
    			if(temp.getFitness() < child.getFitness()){
    				child = temp;
    			}
    		}
    	
			return child;
	}
    
    public float calculateDistance(Chromosome p1, Chromosome p2){
    	float difference = 0;
    	
    	for(int i = 0; i < grid.size(); i++){
    		if(p1.getGene(i) != p2.getGene(i)){
    			difference ++;
    		}
    	}
    	difference = difference / grid.size() * 100;
    	int distance = Math.round(difference);
    	
    	return distance;
    }
    
    // evaluate a single chromosome
    private double evaluate_individual(Chromosome child) {
 
       
         int nturbines=0;
         for (int i=0; i<grid.size(); i++) {
                if (child.getGene(i)) {
                    nturbines++;
                }
         }
            

          double[][] layout = new double[nturbines][2];
            int l_i = 0;
            for (int i=0; i<grid.size(); i++) {
                if (child.getGene(i)) {
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

    // evaluates the whole population
    private void evaluate() {
        double minfit = Double.MAX_VALUE;
        for (int p=0; p<num_individuals; p++) {
        	Chromosome indiv = individuals.get(p);
            int nturbines=0;
            for (int i=0; i<grid.size(); i++) {
                if (indiv.getGene(i)) {
                    nturbines++;
                }
            }

            double[][] layout = new double[nturbines][2];
            int l_i = 0;
            for (int i=0; i<grid.size(); i++) {
                if (indiv.getGene(i)) {
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
	    	indiv.setFitness(coe);
        }
        
    }

    
    
}
