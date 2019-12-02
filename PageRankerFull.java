
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
 * This class implements PageRank algorithm on simple graph structure. Put your
 * name(s), ID(s), and section here.
 *
 */
public class PageRankerFull {
	/**
	 * This class reads the direct graph stored in the file "inputLinkFilename" into
	 * memory. Each line in the input file should have the following format: <pid_1>
	 * <pid_2> <pid_3> .. <pid_n>
	 * 
	 * Where pid_1, pid_2, ..., pid_n are the page IDs of the page having links to
	 * page pid_1. You can assume that a page ID is an integer.
	 */
	private Set<String> info = new LinkedHashSet<String>();
	private double d = 0.85;
	private Set<Integer> allPages = null;
	Map<Integer, Double> pageRank = null;
	Map<Integer, Set<Integer>> pageLinkin = null;
	Map<Integer, Set<Integer>> pageLinkout = null;
	Set<Integer> sinkNodes = null;
	private double previousPerplexity = -2124;
	private double nextPerplexity = -234442;
	private int count = 1;
	private Set<Double> perplexity = new LinkedHashSet<Double>();

	public void loadData(String inputLinkFilename) {
		try {
			File fs = new File(inputLinkFilename); // new File
			Scanner sc = new Scanner(fs);
			while (sc.hasNextLine()) { // EOF
				String line = sc.nextLine();
				info.add(line);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * This method will be called after the graph is loaded into the memory. This
	 * method initialize the parameters for the PageRank algorithm including setting
	 * an initial weight to each page.
	 */
	public void initialize() {
		allPages = new LinkedHashSet<Integer>(); // set of all page
		sinkNodes = new LinkedHashSet<Integer>(); // set of sink nodes (node no out edge)
		pageLinkin = new HashMap<Integer, Set<Integer>>(); // set of pages that link to page (have edge in)
		pageLinkout = new HashMap<Integer, Set<Integer>>(); // set of pages that out-links from page (have edge out)
		pageRank = new HashMap<Integer, Double>();

		for (String line : info) {
			String[] temp = line.split(" ");
			Set<Integer> tempLinkin = new LinkedHashSet<Integer>();
			for (int i = 0; i < temp.length; i++) {
				int node = Integer.parseInt(temp[i]);
				allPages.add(node); // add all page to container.
				if (i > 0) { // this condition for filter starter at first index.
					tempLinkin.add(node); // create tmp for store all node that node link to page.
					pageLinkout.get(node).add(Integer.parseInt(temp[0])); // add adajacent's pages to the page.
				}
			}
			pageLinkin.put(Integer.parseInt(temp[0]), tempLinkin);
		}

		for (Integer page : allPages) {
			pageRank.put(page, 1.0 / allPages.size());
			sinkNodes.add(page);
		}
	}

	/**
	 * Computes the perplexity of the current state of the graph. The definition of
	 * perplexity is given in the project specs.
	 */

	public double getPerplexity() {
		double perplexity = 0.0;
		double power = 0.0;
		for (Integer p : pageRank.keySet()) {
			double score = pageRank.get(p);
			power = power + (score * Math.log(score) / Math.log(2));
		}

		perplexity = Math.pow(2, -1 * power);
		return perplexity;
	}

	/**
	 * Returns true if the perplexity converges (hence, terminate the PageRank
	 * algorithm). Returns false otherwise (and PageRank algorithm continue to
	 * update the page scores).
	 */
	public boolean isConverge() {
		int pre = ((int) previousPerplexity) % 10;
		int next = ((int) nextPerplexity) % 10;
		if (pre == next) {
			count++;
			if (count == 4)
				return true;

		} else {
			previousPerplexity = nextPerplexity;
			count = 1;
		}
		return false;
	}

	/**
	 * The main method of PageRank algorithm. Can assume that initialize() has been
	 * called before this method is invoked. While the algorithm is being run, this
	 * method should keep track of the perplexity after each iteration.
	 * 
	 * Once the algorithm terminates, the method generates two output files. [1]
	 * "perplexityOutFilename" lists the perplexity after each iteration on each
	 * line. The output should look something like:
	 * 
	 * 183811 79669.9 86267.7 72260.4 75132.4
	 * 
	 * Where, for example,the 183811 is the perplexity after the first iteration.
	 *
	 * [2] "prOutFilename" prints out the score for each page after the algorithm
	 * terminate. The output should look something like:
	 * 
	 * 1 0.1235 2 0.3542 3 0.236
	 * 
	 * Where, for example, 0.1235 is the PageRank score of page 1.
	 * 
	 */
	public void runPageRank(String perplexityOutFilename, String prOutFilename) {

		while (!isConverge()) {
			Map<Integer, Double> newPR = new HashMap<Integer, Double>();

			double sinkPR = 0;
			for (Integer unReachableNode : sinkNodes)
				sinkPR += pageRank.get(unReachableNode);

			for (Integer p : allPages) {
				double scoreNewPR = (1 - d) / allPages.size();
				scoreNewPR = scoreNewPR + (d * sinkPR / allPages.size());
				for (Integer q : pageLinkin.get(p)) {
					scoreNewPR = scoreNewPR + (d * pageRank.get(q) / pageLinkout.get(q).size());
				}
				newPR.put(p, scoreNewPR);
			}

			for (Integer p : allPages)
				pageRank.put(p, newPR.get(p));

			nextPerplexity = getPerplexity();
			perplexity.add(nextPerplexity);
		}

		for (Integer page : pageRank.keySet())
			System.out.println(page + " " + pageRank.get(page));

		try {
			FileWriter fw = new FileWriter(prOutFilename);
			FileWriter fw1 = new FileWriter(perplexityOutFilename);
			for (Integer p : pageRank.keySet())
				fw.append(p + " " + pageRank.get(p) + "\n");

			for (Double per : perplexity)
				fw1.append(per + "\n");

			fw.close();
			fw1.close();
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	/**
	 * Return the top K page IDs, whose scores are highest.
	 */
	public Integer[] getRankedPages(int K) {
		Map<Integer, Double> sortPageRank = pageRank.entrySet().stream()
				.sorted(Map.Entry.comparingByValue(Collections.reverseOrder())).limit(K)
				.collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue, (e1, e2) -> e1, LinkedHashMap::new));
		int e = 0;
		Integer[] topPage = new Integer[sortPageRank.size()];
		for (Integer page : sortPageRank.keySet()) {
			topPage[e] = page;
			e++;
		}

		return topPage;

	}

	public static void main(String args[]) {
		long startTime = System.currentTimeMillis();
		PageRanker pageRanker = new PageRanker();
		// pageRanker.loadData("citeseer.dat");
		pageRanker.loadData("test.dat");
		pageRanker.initialize();
		pageRanker.runPageRank("perplexity.out", "pr_scores.out");
		Integer[] rankedPages = pageRanker.getRankedPages(100);
		double estimatedTime = (double) (System.currentTimeMillis() - startTime) / 1000.0;

		System.out.println("Top 100 Pages are:\n" + Arrays.toString(rankedPages));
		System.out.println("Proccessing time: " + estimatedTime + " seconds");
	}
}
