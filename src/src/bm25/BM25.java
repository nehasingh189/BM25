package src.bm25;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.NavigableMap;
import java.util.NavigableSet;
import java.util.Set;
import java.util.TreeMap;

import src.bm25.NormalizationConstant;

public class BM25 {
	
		NormalizationConstant nc= new NormalizationConstant();
		int maxRetrieve = 0;
		Map <String,String> h1 = new HashMap<String,String>();
		int enterDocToSearch = 100;
		int givenResultSize;
		int N = 0;
		double avgLenOfDoc = 0;
		String line;
		int lineNumber=1;
		BufferedWriter writer1;
	/**
	 * @throws IOException 
	 * Read the inverted index in hashtable called h1
	 * Use this hashtable to search for word/words in the query to return document ids where the word repeats and term frequency   
	 */
	public void readInvertedIndex(String filename) throws IOException{
		//readers for inverted index
		//FileReader fr = new FileReader("index.out");
		FileReader fr = new FileReader(filename);
		BufferedReader br = new BufferedReader(fr);
		//read inverted index and create a hashmap of all values in inverted index
			while((line=br.readLine())!=null){
			String[] token=line.split("\\->");
			h1.put(token[0], token[1]);
		}
			br.close();
	}
	
	/**
	 * @throws IOException 
	 * 	Read the query file line by line
	 *  Read each word per query and calculate the score for every word in that query
	 *  Sort the results per query in desc order of score
	 */
	public void readQueriesFile(String queryFile, int maxResultSize, String returnFileName) throws IOException{
		//readers for queries
		FileReader fr1 = new FileReader(queryFile);
		BufferedReader br1 = new BufferedReader(fr1);
		//PrintWriter writer = new PrintWriter("myQuery.txt", "UTF-8");
		File file = new File(returnFileName);
		file.createNewFile();
		writer1 = new BufferedWriter(new FileWriter(file));
		String OutK=null;
		//read queries.txt
		
		N = nc.getNumberOfDoc(); //N is total number of docs in the collection
		avgLenOfDoc = nc.calculateAverageDocumentLength(); //Get the avg length of document
		
		/**
		 * The while loop reads query.txt file line by line
		 * For every word in the query --> Get the document ids where the word is present along with its frequency from invertedIndex to calculate the score
		 **/
		while((line=br1.readLine())!=null)
		{
			int termFrequency=0; 
			maxRetrieve = 0;
				//	split the query words based on spaces
				Hashtable<String, Double> h3 = new Hashtable<String,Double>();
				//TreeMap is used to sort values based on the score. 
				TreeMap<Double,String> hTree = new TreeMap<Double,String>(Collections.reverseOrder());
				HashMap <String,String> h2temp = new HashMap<String,String>();
				String[] token1 = line.split("\\s+");
				for(int i=0; i<token1.length; i++){
					
					if(!h2temp.containsKey(token1[i]))
					{
						termFrequency=1;
						String h1Val = h1.get(token1[i]);    
						// For every word in the query read the docid 
						//it is present in from h1 (invertedindex.txt)
						h2temp.put(token1[i], h1Val);
					}
					else{ 
						termFrequency = termFrequency +1;
					}
					} // end of for
				// Calculates the score for the query
				h3= retrieveFinalScore(h2temp, N, avgLenOfDoc, termFrequency, lineNumber); 

				// Fill the treemap with score results - TreeMap is used for sorting the results
				for (HashMap.Entry<String,Double> entry : h3.entrySet()) {
					 hTree.put(entry.getValue(), entry.getKey());
			        }

				Set set = hTree.entrySet();
		        Iterator i = set.iterator();
		        int rank = 0;
		        while(i.hasNext() && maxResultSize > maxRetrieve) 
		        {
		        	maxRetrieve++;
		        	Map.Entry me = (Map.Entry) i.next();
		        	rank++;
		        	String result = (String) me.getValue();
		        	double scores = (double) me.getKey();
		        	result = result.replace("rank", String.valueOf(rank));
		        	result = result.replace("BM_Score", String.valueOf(scores));
		        	
		        	System.out.println(result);
		        	writer1.write(result);
		        	writer1.newLine();
		        		        	
		        }
		        System.out.println("---------------------------------------------");
		        writer1.write("---------------------------------------------");
		        writer1.newLine();
				lineNumber++;
			            
				//System.out.println("------------------");
		} //end of while
		
		
		fr1.close();
		br1.close();
		writer1.flush();
		writer1.close();
	}
	
	public Hashtable<String,Double> retrieveFinalScore(HashMap<String,String> h2, int NOfDoc,double davgLenDoc, int qfi,int queryID) throws IOException
	{
			Set<String> keys = h2.keySet();
        	Iterator<String> itr = keys.iterator();
 
 	        String key;
 	        String value;
        	String temp1="";
        	Hashtable <String,Double> h3temp = new Hashtable<String,Double>();
	        while(itr.hasNext())
        	{
	        	  
	            key = (String)itr.next();
        	    value = (String)h2.get(key);
        	            	    
        	    // Calculate the score for each word in the single query
        	    String[] arrCommDoc = value.split("\\)");
        	    int ni = arrCommDoc.length;
        	    for(int j=0;j<arrCommDoc.length;j++)
        	    {
        	    	String[] s1 = arrCommDoc[j].split("\\,");
        	    	String docId = s1[0].substring(1);
        	    	int fi= Integer.parseInt(s1[1]);
        	    	double K = nc.calculateValueK(docId,davgLenDoc);
        	    	double score = getScore(NOfDoc,ni,qfi,fi,K);
        	    	String outK = queryID + " Q0 " + docId + " rank BM_Score NiNe";
        	    	//System.out.println(key + " Initial score "+ score);
        	    	
        	    	//if this combination of queryID and documentID is not present in hashtable,
        	    	// add score along with query id and doc id in hashtable. 
        	    	if(!h3temp.containsKey(outK))
        	    	{
        	    		h3temp.put(outK, score);
        	    	}
        	    	else
        	    	{
        	    		// if this combination of queryID and documentID is  present in hashtabl
        	    		// we just update the score. 
        	    		Double h1Val = h3temp.get(outK);
        	    		score = score + h1Val;
        	    		h3temp.put(outK, score);		
     
        	    	}
        	    	
        	    }
		    }
	    return h3temp;
	}
	

// Public method to Calculate score

 public double getScore(int N, int ni, int qfi, int fi, double K){
	int ri=0;
	int R=0;
	double k1 = 1.2;
	double k2 = 100;
	double b = 0.75;
	double eq10=0;
	try
	{

		double eq1 = 	(ri + 0.5) / (R - ri + 0.5);
		double eq2 = 	(ni - ri + 0.5) / (N - ni - R + ri + 0.5);
		double eq3 = 	(k1 + 1) * fi;
		double eq4 = 	(K + fi);
		double eq5 = 	(k2 + 1) * qfi;
		double eq6 = 	(k2 + qfi);
		double eq7 = 	eq1 / eq2;
		double eq8 = 	eq3 / eq4;
		double eq9 = 	eq5 / eq6;
		eq10 = 	Math.log(eq7 * eq8 * eq9);
		//return eq10;

	}
	catch(Exception ex) {}
	return eq10;

}
	
 
 public static void main(String[] args) throws IOException{
	String index=args[0];
	String query=args[1];
	String resultSize=args[2];
	String resultfileName=args[3];
	int resultSetsize=Integer.parseInt(resultSize);
	BM25 bm25 = new BM25();
	bm25.readInvertedIndex(index);
	bm25.readQueriesFile(query,resultSetsize,resultfileName); 
 }
}
