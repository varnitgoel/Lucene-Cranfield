package irassgn;

import java.io.*;
import java.io.IOException;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import java.nio.file.Paths;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.search.BooleanQuery;
import org.apache.lucene.search.BoostQuery;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.BooleanClause.Occur;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.search.similarities.BM25Similarity;
//************************************************************************************************************************
public class filesearch 
{
  private filesearch() {}
  public static void main(String[] args) throws Exception 
  {
	  String queryString = "";
	  File file = new File("/home/ubuntu/Lucene-Cranfield/result.txt");
	  BufferedReader br = new BufferedReader(new FileReader(new File("/home/ubuntu/Lucene-Cranfield/cran/cran.qry")));
	  String readline;
 
	  BufferedWriter writer = new BufferedWriter(new FileWriter(file));

	  int i = 0;
	  while((readline = br.readLine()) != null)
	  {
		  if(readline.startsWith(".I") && i!=0)
		  {
			  System.out.println(i + ". " + queryString);
			  queryString = queryString.replaceAll("\\?", "");
			  runQuery(queryString, i, writer);
		  }
		  else if (readline.startsWith(".W"))
		  {
			  i++;
			  queryString = "";
		  }
		  else 
		  {
			  queryString = queryString + " " + readline;
		  }
	  }
	  System.out.println(i + ". " + queryString);
	  queryString = queryString.replaceAll("\\?", "");
	  runQuery(queryString, i, writer);
	  writer.close();
	  br.close();
	  }
//************************************************************************************************************************  
  	public static void runQuery(String queryString, int queryNo, BufferedWriter writer) throws IOException, ParseException
  	{
  		String index = "/home/ubuntu/Lucene-Cranfield/indexes";
	    IndexReader reader = DirectoryReader.open(FSDirectory.open(Paths.get(index)));
	    IndexSearcher searcher = new IndexSearcher(reader);
	    searcher.setSimilarity(new BM25Similarity());
	    Analyzer analyzer = new StandardAnalyzer();
	    
		QueryParser parser1 = new QueryParser("title", analyzer);
		QueryParser parser2 = new QueryParser("author", analyzer);
		QueryParser parser3 = new QueryParser("contents", analyzer);
		    
		Query query1 = parser1.parse(queryString);
		Query query2 = parser2.parse(queryString);
		Query query3 = parser3.parse(queryString);
		
	    Query boostedTermQuery1 = new BoostQuery(query1, (float) 2.5);
	    Query boostedTermQuery3 = new BoostQuery(query3, (float) 0.7);
	
	    BooleanQuery.Builder booleanQuery = new BooleanQuery.Builder();
	    booleanQuery.add(boostedTermQuery1, Occur.SHOULD);
	    booleanQuery.add(query2, Occur.SHOULD);
	    booleanQuery.add(boostedTermQuery3, Occur.SHOULD);        
	    TopDocs docs = searcher.search(booleanQuery.build(), 15);
	        System.out.println ("priority documents: " + docs.scoreDocs.length);
	    for( ScoreDoc doc : docs.scoreDocs) {
	        Document thisDoc = searcher.doc(doc.doc);
	        writer.write(queryNo + " " + thisDoc.get("index") + "\n");
	    }
	reader.close();
  	}
}
