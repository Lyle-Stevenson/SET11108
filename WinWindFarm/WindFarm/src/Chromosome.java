import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;

public class Chromosome implements Comparable<Chromosome> {
	
	
	private double fitness;
	Random rand;
	
	private boolean[] chromosome_Problem;
	
	public Chromosome(int size){
		this.rand = new Random();
		chromosome_Problem = new boolean[size];
	}

	public Chromosome generateChromosome()
	{	
		for (int i=0; i< this.chromosome_Problem.length; i++) {
            this.chromosome_Problem[i] = rand.nextBoolean();
        }
		return this;
	}
	
	public void setGene(int pos, boolean newGene){
		this.chromosome_Problem[pos] = newGene;
	}
	
	public boolean getGene(int pos){
		return this.chromosome_Problem[pos];
	}
   
	public double getFitness() {
		return fitness;
	}

	public void setFitness(double cost) {
		this.fitness = cost;
	}
	
	@Override
	public int compareTo(Chromosome chromosome) {
        Double remainder = chromosome.getFitness() - this.getFitness();
        if (remainder > 0) {
            return 1;
        }
        if (remainder < 0) {
            return -1;
        }
        return 0;
    }
}

