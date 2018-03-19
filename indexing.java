package irassgn;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.Date;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.LongPoint;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.StringField;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig.OpenMode;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.index.Term;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;

public class indexing 
{
  public static void main(String[] args) 
  {    
    String indexPath = "C:\\Users\\Varnit Goel\\Desktop\\index\\";
    String docsPath = "C:\\Users\\Varnit Goel\\Desktop\\split_documents\\";
    boolean create = true;

    final Path docDir = Paths.get(docsPath);
    if (!Files.isReadable(docDir)) 
    {
      System.out.println("Document directory '" +docDir.toAbsolutePath()+ "'invalid");
      System.exit(1);
    }
    
    Date start = new Date();
    try 
    {
      Directory dir = FSDirectory.open(Paths.get(indexPath));
      Analyzer analyzer = new StandardAnalyzer();
      IndexWriterConfig iwc = new IndexWriterConfig(analyzer);

      if (create) 
      {
        iwc.setOpenMode(OpenMode.CREATE);
      } else 
      {
        iwc.setOpenMode(OpenMode.CREATE_OR_APPEND);
      }

      IndexWriter writer = new IndexWriter(dir, iwc);
      indexDocs(writer, docDir);

      writer.close();

      Date end = new Date();
      System.out.println(end.getTime() - start.getTime() + " total milliseconds");
    } catch (IOException e) 
    {
      System.out.println("Error Observed");
    }
  }
//********************************************************************************************
  static void indexDocs(final IndexWriter writer, Path path) throws IOException 
  {
    if (Files.isDirectory(path)) 
    {
      Files.walkFileTree(path, new SimpleFileVisitor<Path>() 
      {
        @Override
        public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException 
        {
          try 
          {
            indexDoc(writer, file, attrs.lastModifiedTime().toMillis());
            System.out.println("visitFile: " + file);
          } catch (IOException ignore) 
          {
              System.out.println("NA");
          }
          return FileVisitResult.CONTINUE;
        }
      });
    } else 
    {
      indexDoc(writer, path, Files.getLastModifiedTime(path).toMillis());
    }
  }
//************************************************************************************************
   	static void indexDoc(IndexWriter writer, Path file, long lastModified) throws IOException 
   	{   		
		InputStream stream1 = Files.newInputStream(file);
		InputStream stream2 = Files.newInputStream(file);
		  
	    	BufferedReader br = null;
	    	BufferedReader br1 = null;
	    	  
	    	String title = "";
	    	String author = "";
	    	String content = "";
	    	String line;
	    	String index = "";
	    	  
	    	br = new BufferedReader(new InputStreamReader(stream1));
	    	br1 = new BufferedReader(new InputStreamReader(stream2));
	    	int l = 0;
	    	int x = 0;
	    	while ((line = br1.readLine()) != null)
	    	{
	    		l++;
	    	}
	    	String[] lines = new String[l];
	    	while ((line = br.readLine()) != null)
	    	{
	    		lines[x] = line;
	    		x++;
	    	}
	    	for(int i = 0; i<lines.length; i++)
		    {
		    		if (lines[i].equals(".T"))
		      	{
		    			int j = i+1;
		    			while(!lines[j].equals(".A"))
		      	  	{
		      	  		title = title + " " + lines[j];
		      	  		j++;
		      	  	}
		      	 }
		    		else if(lines[i].equals(".A"))
		        	{
		        	  int j = i+1;
		        	  while(!lines[j].equals(".B"))
		        	  {
		        	  	author = author + " " + lines[j];
		        	  	j++;
		        	  }
		        	 }
		        	 else if (lines[i].equals(".W"))
		        	 {
		        	 	int j = i+1;
		        	 	while(j<l)
		        	 	{
		        	 		content = content + " " + lines[j];
		        	 		j++;
		        	 	}
		        	 }
		        	 else if (lines[i].startsWith(".I"))
		        	 {
		        		 index = lines[i].substring(2);
		        	 }
		      }
    
      InputStream auth = new ByteArrayInputStream(author.getBytes(StandardCharsets.UTF_8));
      InputStream titl = new ByteArrayInputStream(title.getBytes(StandardCharsets.UTF_8));
      InputStream cont = new ByteArrayInputStream(content.getBytes(StandardCharsets.UTF_8));
      InputStream indx = new ByteArrayInputStream(index.getBytes(StandardCharsets.UTF_8));

      	//Create new document
	      Document doc = new Document();
	      
	      Field pathField = new StringField("path", file.toString(), Field.Store.YES);
	      doc.add(pathField);

	      doc.add(new LongPoint("modified", lastModified));
	      
	    	  String x3 = (new BufferedReader(new InputStreamReader(indx)).readLine());

	      if(x3 != null)
	      {
	    	  	Field ind = new StringField("index", x3, Field.Store.YES);
	    	  	doc.add(ind);
	      }
	      doc.add(new TextField("contents", new BufferedReader(new InputStreamReader(cont, StandardCharsets.UTF_8))));
	      doc.add(new TextField("title", new BufferedReader(new InputStreamReader(titl, StandardCharsets.UTF_8))));
	      doc.add(new TextField("author", new BufferedReader(new InputStreamReader(auth, StandardCharsets.UTF_8))));

	      if (writer.getConfig().getOpenMode() == OpenMode.CREATE) {
	        System.out.println("adding " + file);
	        writer.addDocument(doc);
	      } else 
	      {
	        System.out.println("updating " + file);
	        writer.updateDocument(new Term("path", file.toString()), doc);
	      }
   	}
}
     
     

   	