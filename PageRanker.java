//Name(s):
//ID
//Section
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Scanner;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * This class implements PageRank algorithm on simple graph structure.
 * Put your name(s), ID(s), and section here.
 *
 */
public class PageRanker {
	/**
	 * This class reads the direct graph stored in the file "inputLinkFilename" into memory.
	 * Each line in the input file should have the following format:
	 * <pid_1> <pid_2> <pid_3> .. <pid_n>
	 * 
	 * Where pid_1, pid_2, ..., pid_n are the page IDs of the page having links to page pid_1. 
	 * You can assume that a page ID is an integer.
	 */
	private Set<String> info = new LinkedHashSet<String>();
	private double d = 0.85;
	private Set<Integer> allPage = null;
	Map<Integer, Double> pageRank = null;
	Map<Integer, Set<Integer>> pageLinkin = null;
	Map<Integer, Set<Integer>> pageLinkout = null;
	Set<Integer> noOutlink = null;
	private double previousPerplexity = -2124;
	private double nextPerplexity = -234442;
	private int count = 1;
	private Set<Double> perplexity = new LinkedHashSet<Double>();
	
	public void loadData(String inputLinkFilename){


		try {
			
			File file = new File(inputLinkFilename);
			Scanner scnr = new Scanner(file);
			while(scnr.hasNextLine()){
			   //String line = scnr.nextLine();
			   //System.out.println(line);
				info.add(scnr.nextLine());
			}
		}catch(Exception e){
		e.printStackTrace();
			
		}
		for(String test: info) {
			//System.out.println(test);
		}
	}
	
	
	
	
	/**
	 * This method will be called after the graph is loaded into the memory.
	 * This method initialize the parameters for the PageRank algorithm including
	 * setting an initial weight to each page.
	 */
	public void initialize(){
		allPage = new LinkedHashSet<Integer>();
		pageRank = new HashMap<Integer, Double>();
		pageLinkin = new HashMap<Integer, Set<Integer>>();
		pageLinkout = new HashMap<Integer, Set<Integer>>();
		noOutlink = new LinkedHashSet<Integer>();
		
		for(String allInfo:info) {
			String[] temp = allInfo.split(" ");
			for(int i=0;i<temp.length;i++) {
				allPage.add(Integer.parseInt(temp[i]));
				
			};
		
	
		Set<Integer> tempLinkin = new LinkedHashSet<Integer>();
		for(int i =1;i<temp.length;i++) {// use 1 because 0 is keyset
			tempLinkin.add(Integer.parseInt(temp[i]));
		}
		pageLinkin.put(Integer.parseInt(temp[0]), tempLinkin);
		
		for(int i =1;i<temp.length;i++) {
			if(!pageLinkout.containsKey(Integer.parseInt(temp[i]))) {
				Set<Integer> tempLinkout = new LinkedHashSet<Integer>();
				tempLinkout.add(Integer.parseInt(temp[0]));
				pageLinkout.put(Integer.parseInt(temp[i]), tempLinkout);
			}
			else {
				pageLinkout.get(Integer.parseInt(temp[i])).add(Integer.parseInt(temp[0]));
			}
		}
		
		
		}
		
		for(Integer page: allPage) {
			pageRank.put(page, 1.0/allPage.size());
			//System.out.println("page"+pageRank.get(page));
			if(!pageLinkout.containsKey(page)) {
				noOutlink.add(page);
			}
		}
		
//		for(Integer a: noOutlink) {
//			System.out.println("This is" + a);
//		}
//		for(Integer p: pageLinkin.keySet()) {
//			System.out.println(p+" " + pageLinkin.get(p));
//	}
//		for(Integer q: pageLinkout.keySet()) {
//			System.out.println(q+" " + pageLinkout.get(q));
//		}
		
		
	
	}
	/**
	 * Computes the perplexity of the current state of the graph. The definition
	 * of perplexity is given in the project specs.
	 */
	
	
	
	public double getPerplexity(){
		double perplexity = 0.0;
		double power =0.0;
		for(Integer p: pageRank.keySet()) {
			double score =pageRank.get(p);
			power+= score* log2(score);
		}
		
		perplexity= Math.pow(2, -power);
		return perplexity;
		}
	
	public double log2(double score) {
		return Math.log(score)/Math.log(2);
	}
	
	/**
	 * Returns true if the perplexity converges (hence, terminate the PageRank algorithm).
	 * Returns false otherwise (and PageRank algorithm continue to update the page scores). 
	 */
	public boolean isConverge(){
		int pre = unit(previousPerplexity);
		int next = unit(nextPerplexity);
		if(pre == next) {
			count+=1;
			if(count ==4) {
				return true;
			}	
			else {
				return false;
			
				 }
	
		}
		else {
			previousPerplexity = nextPerplexity;
			count=1;
		}
		return false;
		
	}
	
	public int unit(double number) {
		int num= (int) number;
		num = num%10;
		return num;
	}
	
	/**
	 * The main method of PageRank algorithm. 
	 * Can assume that initialize() has been called before this method is invoked.
	 * While the algorithm is being run, this method should keep track of the perplexity
	 * after each iteration. 
	 * 
	 * Once the algorithm terminates, the method generates two output files.
	 * [1]	"perplexityOutFilename" lists the perplexity after each iteration on each line. 
	 * 		The output should look something like:
	 *  	
	 *  	183811
	 *  	79669.9
	 *  	86267.7
	 *  	72260.4
	 *  	75132.4
	 *  
	 *  Where, for example,the 183811 is the perplexity after the first iteration.
	 *
	 * [2] "prOutFilename" prints out the score for each page after the algorithm terminate.
	 * 		The output should look something like:
	 * 		
	 * 		1	0.1235
	 * 		2	0.3542
	 * 		3 	0.236
	 * 		
	 * Where, for example, 0.1235 is the PageRank score of page 1.
	 * 
	 */
	public void runPageRank(String perplexityOutFilename, String prOutFilename){
		
		while(!isConverge()) {
			Map<Integer, Double> newPR = new HashMap<Integer,Double>();
			double sinkPR = 0;
			for(Integer noOut: noOutlink) {
				sinkPR+=pageRank.get(noOut);
			}
			for(Integer p : allPage ) {
				double scoreNewPR = (1-d)/allPage.size();
				scoreNewPR += d*sinkPR/ allPage.size();
				if(pageLinkin.containsKey(p)) {
					for(Integer q: pageLinkin.get(p)) {
						scoreNewPR+= d*pageRank.get(q)/pageLinkout.get(q).size();
				}
				
					
				}
			newPR.put(p, scoreNewPR);
			}
			
			for(Integer p: allPage) {
				pageRank.put(p, newPR.get(p));
			}
			
			nextPerplexity = getPerplexity();
			perplexity.add(nextPerplexity);
			//System.out.println(getPerplexity());
			//break;
			
		}
		
		for(Integer page: pageRank.keySet()) {
			System.out.println(page+" "+pageRank.get(page));
		}
		 
		try{    
	           FileWriter fw=new FileWriter(prOutFilename);
	           FileWriter fw1 = new FileWriter(perplexityOutFilename);
	           for(Integer p: pageRank.keySet()) {
	        	   fw.append(p + " " +pageRank.get(p)+"\n");
	           }
	           for(Double per:perplexity) {
	        	   fw1.append(per+"\n");
	           }
	           fw.close();
	           fw1.close();
	          }catch(Exception e)
					{e.printStackTrace();
	          }    
	   
		
	}
	
	
	/**
	 * Return the top K page IDs, whose scores are highest.
	 */
	public Integer[] getRankedPages(int K){
		Map<Integer, Double> sortPageRank = 
		 pageRank.entrySet()
         .stream()
         .sorted(Map.Entry.comparingByValue(Collections.reverseOrder()))
         .limit(K)
         .collect(Collectors.toMap(
           Map.Entry::getKey, 
           Map.Entry::getValue, 
           (e1, e2) -> e1, 
           LinkedHashMap::new
         ));
		int i =0;
		Integer[] topPage = new Integer[sortPageRank.size()];
		for(Integer page: sortPageRank.keySet()) {
				topPage[i++] = page;
		}
		
		return topPage;
		
	}
	
	public static void main(String args[])
	{
	long startTime = System.currentTimeMillis();
		PageRanker pageRanker =  new PageRanker();
		//pageRanker.loadData("citeseer.dat");
		pageRanker.loadData("test.dat");
		pageRanker.initialize();
		pageRanker.runPageRank("perplexity.out", "pr_scores.out");
		Integer[] rankedPages = pageRanker.getRankedPages(100);
	double estimatedTime = (double)(System.currentTimeMillis() - startTime)/1000.0;
		
		System.out.println("Top 100 Pages are:\n"+Arrays.toString(rankedPages));
		System.out.println("Proccessing time: "+estimatedTime+" seconds");
	}
}
