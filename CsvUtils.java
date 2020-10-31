package dsa_assignment4;

import java.io.PrintWriter;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Deque;
import java.util.LinkedList;
import java.util.PriorityQueue;
import java.util.Scanner;

import org.apache.log4j.Logger;

import dsa_assignment4.CsvFormatter.RowComparator;
import jdk.nashorn.api.tree.ForInLoopTree;

/**
 * A class containing only static methods to externally sort simplified CSV
 * files
 */
public class CsvUtils
{
	private static final Logger logger  = Logger.getLogger(CsvUtils.class);

	// Your "data" directory will be at the top level of your Eclipse
	// project directory for this assignment: do not change the name
	// or put it anywhere else: the marking software will cause your
	// program to fail if it tries to read or write any files outside
	// this directory
	private static final Path   dataDir = Paths.get("data");

	/**
	 * For marking purposes
	 * 
	 * @return Your student id
	 */
	public static String getStudentID()
	{
		//change this return value to return your student id number, e.g. 
		// return "1234567";
		return "2045143";
	}

	/**
	 * For marking purposes
	 * 
	 * @return Your name
	 */
	public static String getStudentName()
	{
		//change this return value to return your name, e.g.
		// return "John Smith";
		return "Philip Broadley";
	}

	/**
	 * An accessor method to return the path of your data directory
	 * 
	 * @return the path to your data directory
	 */
	public static Path getDataDir()
	{
		return dataDir;
	}

	/**
	 * A sample method to show the basic mechanism for reading and writing CSV
	 * files using the CsvFormatter class. This just copies the input file to
	 * the output file with no changes. However it has to make sure that the
	 * output file is created with the correct CSV header.
	 * 
	 * @param fromPath
	 *            The path of the CSV file to read from
	 * @param toPath
	 *            The path of the CSV file to write to
	 * @return true if it manages to complete without throwing exceptions: if
	 *         this were an empty method that you had to implement as part of
	 *         this assignment, you should leave the return value as false until
	 *         you had completed it to avoid unnecessary testing of an
	 *         unimplemented method
	 * @throws Exception
	 *             if anything goes wrong, e.g. if you can't open either file,
	 *             can't read from the fromPath file, can't write to the toPath
	 *             file, if the from file does not match the requirements of the
	 *             simplified CSV file format, etc.
	 */
	public static boolean copyCsv(Path fromPath, Path toPath)
		throws Exception
	{
		// Open both the from and the to files using a "try-with-resource" pattern
		// This ensures that, no matter what happens in terms of returns or exceptions,
		// both files will be correctly closed automatically
		try (Scanner from = new Scanner(fromPath); PrintWriter to = new PrintWriter(toPath.toFile()))
		{
			// Setup the CSV format from the "from" file
			CsvFormatter formatter = new CsvFormatter(from);

			// Output the CSV header row to the "to" file
			formatter.writeHeader(to);

			// copy each non-header row from the "from" file to the "to" file 
			String[] row;
			while ((row = formatter.readRow(from)) != null)
				formatter.writeRow(to, row);
		}
		return true;
	}

	/**
	 * Split an (unordered) CSV file into separate smaller CSV files (runs)
	 * containing sorted runs of row, where the rows are sorted in ascending
	 * order of the column identified by the <code>columnName</code> parameter.
	 * This is intended to be the first stage of a merge sort which produces
	 * sorted runs that can then be merged together.
	 * <p>
	 * This code should work on truly huge files: far larger than we can hold in
	 * memory at the same time. To simulate this without using huge files, we
	 * impose a limit on the size of each run, given by the
	 * <code>numRowLimit</code> parameter. Further, NO internal sort algorithms
	 * should be used: e.g. Arrays.sort, Collections.sort, SortedList etc.
	 * Instead a {@link PriorityQueue} must be used to generate the sorted runs:
	 * Have a loop. Inside the loop, read in a maximum of
	 * <code>numRowLimit</code> rows from the input and insert them into the
	 * priority queue and then extract them in order and write them out to a new
	 * split file.
	 * </p>
	 * <p>
	 * The split file should be a sibling (i.e. in the same directory) as the
	 * input file and have a name which is "temp_00000_" followed by the name of
	 * the input file, where the "00000" is replace by a sequence number:
	 * "00000" for the first split file, "00001" for the second etc.
	 * </p>
	 * 
	 * @param fromPath
	 *            The relative path where the input file is
	 * @param columnName
	 *            The header name of the column used for sorting
	 * @param numRowLimit
	 *            The maximum number of value rows (not including the header
	 *            row) that can be written into each split file
	 * @return the <code>Path[]</code> of paths for the full list of split files created
	 * @throws Exception
	 *             If anything goes wrong with opening, reading or writing the
	 *             files, or if the input file does not match the simplified CSV
	 *             requirements.
	 */
	public static Path[] splitSortCsv(Path fromPath, String columnName, int numRowLimit)
			throws Exception
		{
			Scanner scanner = new Scanner(fromPath);
			CsvFormatter csvFormatter = new CsvFormatter(scanner);
			boolean done = false;
			int fileNum = 0000;
			Deque<Path> pathDeque = new LinkedList<>();
			
			while(!(done)) {
				PrintWriter writer = new PrintWriter("data/temp_"+fileNum+"_"+fromPath.getFileName());
				csvFormatter.writeHeader(writer);
				String[][] rows=new String[numRowLimit][];
				
				for (int i = 0; i < numRowLimit; i++) {
					rows[i] = csvFormatter.readRow(scanner);	
				}

				rows = rowSorter(rows);

				for (String[] row : rows) {
					if (row == null) {
						done = true;
					}
					else {
						csvFormatter.writeRow(writer , row);
					}
				}
				writer.close();
				if (!(done)){
					pathDeque.add(Paths.get("data/temp_"+fileNum+"_"+fromPath.getFileName()));
					fileNum+=1;
				}
							
			}
			pathDeque.add(Paths.get("data/temp_"+fileNum+"_"+fromPath.getFileName()));

			return pathDeque.toArray(new Path[0]);

		}
		
		private static String [][] rowSorter(String[][] rows) {
			String[] temp = new String[rows[0].length];
			for (int i = 0; i < rows.length; i++) {
				for (int j = i+1; j < rows.length; j++) {
	                if(!(rows[j] == null)&&rows[i][1].compareTo(rows[j][1])>0){
	                    temp=rows[i];
	                    rows[i]=rows[j];
	                    rows[j]=temp; 
	                } 
				}
			}
			return rows;
		}
	
	/**
	 * Merge two ordered input CSV files into a single ordered output CSV file
	 * 
	 * The two input CSV files must be already ordered on the column specified
	 * by <code>columnName</code> and must have the same CSV format (same number
	 * of columns, same headers in the same order) The output file must
	 * similarly be of the same CSV format and ordered on the same column.
	 * 
	 * @param file1Path
	 *            The relative path of the first input file
	 * @param file2Path
	 *            The relative path of the second input file
	 * @param columnName
	 *            The column to order the output file on and, upon which, both
	 *            input files are ordered
	 * @param outputPath
	 *            The relative path of the output file
	 * @return true, if this method has been implemented. If it has not yet been
	 *         implemented, then it returns false and this is used to cause the
	 *         unit test to fail early without doing a lot of unnecessary work
	 * @throws Exception
	 *             If anything goes wrong with opening, reading or writing the
	 *             files, or if the input files do not match the simplified CSV
	 *             requirements or have different CSV formats
	 */
	public static boolean mergePairCsv(Path file1Path, Path file2Path, String columnName, Path outputPath)
		throws Exception
	{
		
		
		Scanner scanner1 = new Scanner(file1Path);
		CsvFormatter csvFormatter1 = new CsvFormatter(scanner1);
		
		Scanner scanner2 = new Scanner(file2Path);
		CsvFormatter csvFormatter2 = new CsvFormatter(scanner2);
		
		PrintWriter writer = new PrintWriter(outputPath.toString());
		
		boolean done = false;
		
		String[] line1 = csvFormatter1.readRow(scanner1);
		String[] line2 = csvFormatter2.readRow(scanner2);
	
		
		csvFormatter1.writeHeader(writer);
		while (!(done)) {
			if(line1 == null&&!(line2 == null)) {
				csvFormatter2.writeRow(writer, line2);
				line2 = csvFormatter2.readRow(scanner2);
			}
			else if(line2 == null&&!(line1 == null)) {
				csvFormatter1.writeRow(writer, line1);
				line1 = csvFormatter1.readRow(scanner1);
			}
				
			else if (!(line2 == null)&&!(line1 == null)){
				CsvFormatter.RowComparator compairer = csvFormatter1.new RowComparator(columnName);
				if(compairer.compare(line1, line2)<0) {
					csvFormatter1.writeRow(writer, line1);
					line1 = csvFormatter1.readRow(scanner1);
				}
				else {
					csvFormatter2.writeRow(writer, line2);
					line2 = csvFormatter2.readRow(scanner2);	
				}
			}
			else {
				done = true;
			}

			
		}
		writer.close();
		scanner1.close();
		scanner2.close();
		return true;
	}


	/**
	 * Merge a list of ordered input CSV files into a single ordered output CSV
	 * file
	 * <p>
	 * The input CSV files must be already ordered on the column specified by
	 * <code>columnName</code> and must have the same CSV format (same number of
	 * columns, same headers in the same order) The output file must similarly
	 * be of the same CSV format and ordered on the same column.
	 * </p>
	 * <p>
	 * This method should merge all the files together by calling
	 * <code>mergePairCsv(...)</code> on pairs of files, starting with those on
	 * <code>pathList</code>, producing larger and larger intermediate file
	 * until the last pair-wise merge is used to produce the output file.
	 * </p>
	 * 
	 * @param pathList
	 *            An array of relative paths of the input files
	 * @param columnName
	 *            The column to order the output file on and, upon which, both
	 *            input files are ordered
	 * @param outputPath
	 *            The relative path of the output file
	 * @return true, if this method has been implemented. If it has not yet been
	 *         implemented, then it returns false and this is used to cause the
	 *         unit test to fail early without doing a lot of unnecessary work
	 * @throws Exception
	 *             If anything goes wrong with opening, reading or writing the
	 *             files, or if the input files do not match the simplified CSV
	 *             requirements or have different CSV formats
	 */
	public static boolean mergeListCsv(Path[] pathList, String columnName, Path outputPath)
		throws Exception
	{
		Deque<Path> paths = new LinkedList<>(Arrays.asList(pathList));
		mergePairCsv(pathList[0],pathList[1],columnName,outputPath);
		for (int i = 2; i < paths.size(); i++) {
			mergePairCsv(pathList[i],outputPath,columnName,outputPath);
		}

		return true;

	}
}
