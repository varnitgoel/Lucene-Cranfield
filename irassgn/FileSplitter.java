package irassgn;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import org.apache.lucene.analysis.Analyzer;
public class FileSplitter 
{
	static Analyzer stop;
	static Analyzer snowball;

	private static class Splitter extends Thread 
	{
		String fileinput;
		String direcoutput;

		Splitter(String inputFile, String direcoutput) 
		{
			super();
			this.fileinput = inputFile;
			this.direcoutput = direcoutput;

		}
		public void run() 
		{
			processFile(fileinput, direcoutput);
		}
	}
	public static void main(String[] args) 
	{
		String inputDirectory = "/home/ubuntu/Lucene-Cranfield/cran/";
		Splitter documents_splitter = new Splitter(inputDirectory + "cran.all.1400", "/home/ubuntu/Lucene-Cranfield/splitfiles/");
		documents_splitter.start();

		try 
		{
			documents_splitter.join();
		} 
		catch (InterruptedException e) 
		{
			e.printStackTrace();
		}
	}
	public static void processFile(String inputFile, String outputDirectory) 
	{
		BufferedReader bufferedReader = null;
		String line;
		String output = "";
		Integer counter = 0;

	try {
			bufferedReader = new BufferedReader(new FileReader(new File(inputFile)));
			bufferedReader.readLine();
			while ((line = bufferedReader.readLine()) != null) 
			{
				if (line.startsWith(".T")) 
				{
					counter ++;
				}
				if(line.startsWith(".I") && counter > 0) 
				{
					writeOutput(outputDirectory + counter.toString() + ".txt", output);
					output = "";
				}
				output = output + line + System.getProperty("line.separator");
			}
			writeOutput(outputDirectory + counter.toString() + ".txt", output);
			bufferedReader.close();
		}
		catch (IOException e) 
		{
			e.printStackTrace();
		}	
		finally 
		{
			if (bufferedReader != null)
				try 
				{
					bufferedReader.close();
				} 
				catch (IOException e) 
				{
					e.printStackTrace();
				}
		}
	}
//*************************************************************************************************	
	public static void writeOutput(String outputFileName, String output) throws IOException 
	{
		final File outputDirectory = new File(outputFileName);

		if (!outputDirectory.exists()) 
		{
			outputDirectory.createNewFile();
		}
		Writer fileWriter = new FileWriter(outputDirectory);
		BufferedWriter bwrite = new BufferedWriter(fileWriter);
		bwrite.write(output);
		bwrite.close();
		fileWriter.close();
	}	
}
