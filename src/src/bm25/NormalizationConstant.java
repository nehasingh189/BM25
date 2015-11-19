package src.bm25;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

public class NormalizationConstant {
	
	double K=0;
	double k1=1.2;
	double b = 0.75;
	double oneMinusB = (1-b);
	double avgdl;
	int dl;
	String line;
	String newLine = "\\n";
	int lineNumber=0;
	
	/**
	 * 
	 * @param docid
	 * @return length of the document specified by docid
	 * @throws IOException
	 */
	public int getdocumentLength(String docid) throws IOException{
		FileReader fr = new FileReader("docLeng.txt"); 
		BufferedReader br = new BufferedReader(fr);
		int ln=0;
		while((line=br.readLine())!=null){
		ln++;
		String[] token=line.split("\\=");
		for(int i=0; i<token.length; i++){
			if(token[i].equals(docid)){
				int length = fetchDocumentLength(ln);
				//System.out.println("doc length is" + length);
				dl=length;
				}
			}
		}
		ln=0;
		br.close();
		return dl;
	}
		
	/**
	 * 
	 * @param lineNumber
	 * @return document length stored in given line
	 * @throws IOException
	 */
		public int fetchDocumentLength(int lineNumber) throws IOException{
			FileReader fr = new FileReader("docLeng.txt"); 
			BufferedReader br = new BufferedReader(fr);
			int counter=0;
			int doclength = 0;
			while((line=br.readLine())!=null){
			counter++;
				if(counter==lineNumber){
					String[] token=line.split("\\=");
					String length=token[1];
					doclength=Integer.parseInt(length);
				}
			}
			br.close();
			return doclength;
		}
	
	/**
	 * 
	 * @return the average document length for the given corpus
	 * @throws IOException
	 */
	public double calculateAverageDocumentLength() throws IOException{
		FileReader fr = new FileReader("docLeng.txt"); 
		BufferedReader br = new BufferedReader(fr);
		int totalDoclength = 0;
		int doclength = 0;
		double averageDocLength=0;
		while((line=br.readLine())!=null){
			lineNumber++;
			doclength = fetchDocumentLength(lineNumber);
			totalDoclength=totalDoclength+doclength;
		}
		averageDocLength=(double)totalDoclength/(double)lineNumber;
		//System.out.println("average doc length is"+averageDocLength);
		lineNumber=0;
		br.close();
		return averageDocLength;
	}
	
	/**
	 * 
	 * @param docID
	 * @return calculates the normalizing constant K for the given doc
	 * @throws IOException
	 */
	public double calculateValueK(String docID,double avgLenDoc) throws IOException{
		double divideDlbyaverageDL;
		int docLength=0;
		//double averageDocLength=calculateAverageDocumentLength();
		double multipleitbyB;
		double sumit;
		double multiplebyk1;
		//get dl 
		docLength=getdocumentLength(docID);
		//divide document length by avg document length
		divideDlbyaverageDL=docLength/((float)avgLenDoc);
		//multiply by b
		multipleitbyB=multiplybyB(divideDlbyaverageDL);
		//add (1-b)
		sumit=multipleitbyB+oneMinusB;
		//multiply by k1
		multiplebyk1=k1*sumit;
		K=multiplebyk1;
		//return K
		return K;
	}
	
	/**
	 * 
	 * @param value
	 * @return multiple given value by b, b=0.75
	 */
	public double multiplybyB(double value){
		double mulB=0;
		mulB=b*value;
		return mulB;
	} 
	
	public int getNumberOfDoc() throws IOException{
		FileReader fr = new FileReader("docLeng.txt"); 
		BufferedReader br = new BufferedReader(fr);
		int counter=0;
		while((line=br.readLine())!=null){
		counter++;
		}
		br.close();
		return counter;
	}

/*public static void main(String[] args) throws IOException{
		NormalizationConstant nc= new NormalizationConstant();
		nc.getdocumentLength("#932");
		nc.calculateAverageDocumentLength();
		nc.calculateValueK("#933");
	}
	*/
	
}
