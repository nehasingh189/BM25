package src.bm25;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;
import src.bm25.documentLength;;

public class Indexer {
			
	public void CreateIndex(String documentName, String indexFileName) throws IOException{
	HashMap<String, String> index= new HashMap<String, String>();
	// use Hashtable 'key' to store word/tokens and value to store docID and term frequency.
	 BufferedReader br;
	 FileReader fr;
	 documentLength dl = new documentLength();
	 dl.calculateDocumentLength(documentName);
	 try{
		 fr= new FileReader(documentName);
		 br=new BufferedReader(fr);
		 String docID= null;
		 String line;
		 String docIdPrefix = "#";
		 String digitOnlyRegex = "\\d+";
		 PrintWriter writer = new PrintWriter(indexFileName, "UTF-8");
		 while((line=br.readLine())!=null){
			 String[] token=line.split("\\s+");
			 //array of tokens will save individual tokens. In document these tokens are separated by spaces
			 int termFrequency = 0;
				for(int i=0; i<token.length; i++){
					if(token[i].equals(docIdPrefix)){
						//create docID by combining # and the following string which will mostly be the document ID
						docID = token[i]+token[i+1];
					}
					else{
						if(!token[i].matches(digitOnlyRegex)){
							//if the token is not present in index, add it to index and set term frequency to 1
								if(!index.containsKey(token[i])){
									termFrequency = 1;
									//create the postings of the format (docID,frequency)
									String temp="(" +docID+ "," +termFrequency +")";
									index.put(token[i], temp);
								}
								else{
									//if the token is prsent in the index, we need to increment its term frequency 
									String temp=index.get(token[i]);
									if(temp.contains(docID)){
									//parse the string to locate the docID and update the term frequency
									String[] parseTemp=temp.split("\\)");
									for(int x=0; x<parseTemp.length;x++){
											if(parseTemp[x].contains(docID)){
											 	//read the value after "," and increment the term frequency by 1
												String temp1 = parseTemp[x]; 
												// temp1 should now contain a unique string in format (docid,tf)
												String[] zap = temp1.split("\\,"); 
												// we split temp1 to extract the term frequency
												termFrequency=Integer.parseInt(zap[1]); 
												// postion 1 after "," contains the termFrequency, hence increment that
												termFrequency++;
												String temp2="("+docID+","+termFrequency;
												temp=temp.replace(temp1,temp2);
												index.put(token[i], temp);
											}	
										}
									}
									else{
										termFrequency=1;
                                        String tempNew="("+docID+","+termFrequency+")";
                                        index.put(token[i],temp + tempNew);
                                     }
								}
							}
						
						}
					}
			 	}
		 for (HashMap.Entry<String,String> entry : index.entrySet()) {
	            writer.println(entry.getKey() + "->" + entry.getValue());
	        }
			System.out.println("length of index is " + index.size());
			writer.close();
		 }
	 catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public static void main (String [] args) throws IOException{
		String corpus=args[0];
		String index = args[1];
		Indexer indexer=new Indexer();
		indexer.CreateIndex(corpus,index);
	}
}
 

