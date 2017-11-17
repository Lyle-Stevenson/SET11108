public class StartOptimiser {
  public static int nSc;

  public static void main(String argv[]) throws Exception {	  
	nSc = 0;
	KusiakLayoutEvaluator eval = new KusiakLayoutEvaluator();
	WindScenario sc = new WindScenario("./Scenarios/practice_"+nSc+".xml");	  
	eval.initialize(sc);
	Solver algorithm = new Solver(eval, 100, 100, 10);
	algorithm.run_cw();
  }
}
