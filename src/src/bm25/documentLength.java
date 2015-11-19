package src.bm25;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;

public class documentLength {
	
	/**
	 * 
	 * @param documentName : this method takes name of a document and parses it to produce a list of
	 * 							(docID, doclength) pairs. 
	 * @throws IOException
	 */
	public void calculateDocumentLength(String documentName) throws IOException{
			HashMap<String,String> docLengthHash = new HashMap<String,String>();
			FileReader fr= new FileReader(documentName);
			BufferedReader br =new BufferedReader(fr);
			PrintWriter writer = new PrintWriter("docLeng.txt", "UTF-8");
			String line;
			String docID =null;
			String docIdPrefix = "#";
			String digitOnlyRegex ="\\d+";
			int docIDLength =0;
			try{
				 while((line=br.readLine())!=null){
				 String[] token=line.split("\\s+");
				
				 for(int i=0; i<token.length; i++){
					if(token[i].equals(docIdPrefix)){ 
					//create docID by combining # and the following string which will be the document ID
					docID = token[i]+token[i+1];
					docIDLength=0;
					}
					else{
						if((!token[i].matches(digitOnlyRegex))&&(!token[i].equals(docIdPrefix))){
							docIDLength++;  
							String getLegnthofDocID=String.valueOf(docIDLength);
							docLengthHash.put(docID, getLegnthofDocID);
						}
						}
					}
				 }
				 //write the values of (dicId, docLength) in a file docLeng.txt
				 for (HashMap.Entry<String,String> entry : docLengthHash.entrySet()) {
			            writer.println(entry.getKey() + "=" + entry.getValue());
			        }
					System.out.println("length of index is " + docLengthHash.size());
					writer.close();
					br.close();
				
			}
			catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		 
		}
	}
