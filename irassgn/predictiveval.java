package irassgn;
import java.io.*;
//************************************************************************************************************************
public class predictiveval
{
	public static int cranResult[][];
	public static int flag = 0;
	public static int counter;
	public static int fileSearchResult[][];
	
	public static void main(String args[]) throws IOException
	{
		int i = 0;
		String cranqresults = "/home/ubuntu/Lucene-Cranfield/cran/cranqrel";
		String resultFile = "/home/ubuntu/Lucene-Cranfield/result.txt";
		String readline;
		BufferedReader readCranrel = new BufferedReader(new FileReader(new File(cranqresults)));
		while(readCranrel.readLine() != null)
		{
			i++;
		}
		cranResult = new int[i][3];
		fileSearchResult = new int[225*15][2];
		readCranrel = new BufferedReader(new FileReader(new File(cranqresults)));
//************************************************************************************************************************
		i = 0;
		while((readline = readCranrel.readLine()) != null)
		{
			readline = readline.replaceAll("\\s+", " ");
			if(readline.charAt(readline.length() - 1) != 32)
			{
				cranResult[i][0] = Integer.parseInt(readline.substring(0, readline.indexOf(32)));
				cranResult[i][1] = Integer.parseInt(readline.substring(readline.indexOf(32) + 1, readline.indexOf(32, readline.indexOf(32) + 1)));
				cranResult[i][2] = Integer.parseInt(readline.substring(readline.indexOf(32, readline.indexOf(32) + 1) + 1));
			}
			else
			{
				cranResult[i][0] = Integer.parseInt(readline.substring(0, readline.indexOf(32)));
				cranResult[i][1] = Integer.parseInt(readline.substring(readline.indexOf(32) + 1, readline.indexOf(32, readline.indexOf(32) + 1)));
				cranResult[i][2] = Integer.parseInt(readline.substring(readline.indexOf(32, readline.indexOf(32) + 1) + 1,readline.lastIndexOf(32)));
			}
			if(cranResult[i][2] < 4)
			{
				i++;
			}
		}
		counter = i;
		readCranrel = new BufferedReader(new FileReader(new File(resultFile)));
		i = 0;
		while((readline = readCranrel.readLine()) != null)
		{
			readline = readline.replaceAll("\\s+", " ");
			if(i<3374)
			{
				fileSearchResult[i][0] = Integer.parseInt(readline.substring(0, readline.indexOf(32)));
				fileSearchResult[i][1] = Integer.parseInt(readline.substring(readline.indexOf(32) + 1));
				i++;
			}
		}
		calculateRecallPrecision(cranResult, fileSearchResult);
		readCranrel.close();
	}
//************************************************************************************************************************	
	private static void calculateRecallPrecision(int[][] results, int[][] searchResults) throws IOException 
	{
		File file = new File("Precision_Recall.txt"); 
		BufferedWriter writer = new BufferedWriter(new FileWriter(file));
		int match = 0;
		double recall = 0;
		double avg_recall = 0;
		double val = 0;
		double avg_val = 0;
		int flag2 = 1;
		int flag3 = 0;
		
		System.out.println(counter);
		for(int i = 0; i < (counter); i++)
		{
			if(flag2 != results[i][0])
			{
				recall = (double)match/(i-flag3);
				flag3 = i;
				avg_recall = avg_recall + recall;
				flag2++;
				flag = flag + 15;
				val = (double)match/15.0;
				avg_val = avg_val + val;
				
				match = 0;
				writer.write( results[i - 1][0] + " " + Math.round(val * 100.0)/100.0 + " " + Math.round(recall * 100.0)/100.0 + "\n");
				System.out.println(results[i - 1][0] + " " + Math.round(val * 100.0)/100.0 + " " + Math.round(recall * 100.0)/100.0);
			}
			if(verifyResult(results[i][1], searchResults))
			{
				match++;
			}	
		}
		recall = (double)match/(counter - 1 -flag3);
		avg_recall = avg_recall + recall;
		val = (double)match/15.0;
		avg_val = avg_val + val;
		
		match = 0;
		System.out.println(results[counter - 1][0] + " " + val + " " + recall);
		writer.write(results[counter - 1][0] + " " + val + " " + recall + "\n");
		
		writer.write("Average Precission = " + Math.round((avg_val/225)*100.0)/ 100.0 + "\n");
		writer.write("Average Recall = " + Math.round((avg_recall/225)*100.0)/ 100.0);
		System.out.println("Result written in file Precision_Recall.txt");
		writer.close();
	}
//************************************************************************************************************************
	public static boolean verifyResult(int fileIndex, int[][] searchResults)
	{
		for(int i = flag; i < (flag + 15); i++)
		{
			if(i<3375)
			{
				if(searchResults[i][1] == fileIndex)
				{
					return true;
				}
			}
		}
		return false;
	}
}
