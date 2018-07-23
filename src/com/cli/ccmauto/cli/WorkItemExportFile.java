package com.cli.ccmauto.cli;

import java.io.IOException;
import java.util.List;

import com.beust.jcommander.Parameter;
import com.beust.jcommander.Parameters;
import com.csvreader.CsvWriter;

@Parameters(commandDescription = "Exporting existing work items based on provided work items id(s) tp CSV file.")
public class WorkItemExportFile {
	
	@Parameter(names = { "-ids", "--identifiers" }, description = "list of work items id(s) that will be exported to CSV file. Example: -ids 1 2 3 434", order=1, required = true)
	public String inputFile;
	
	@Parameter(names = { "-o", "--outputFile" }, description = "CSV file that will be used output file", order=2, required = true)
	public String outputFile;
	
	public int id;
	public String projectArea;
	public String summary;
	public String status;
	public String description;
	public String category;
	public String owner;
	public String priority;
	public String severity;
	public String tags;
	public Long duration;
	public String dueDate;
	public String plannedFor;
	public String type;

	public void generateOutputFile(String fileName,List<WorkItemExportFile> lista)
	{
		CsvWriter product = new CsvWriter(fileName);
		try {
			String[] header = {"projectArea","id","summary","type","category","plannedFor","description","owner","priority","severity","tags","duration","dueDate"};
			product.writeRecord(header);
			product.writeComment("List of found work items with id(s)");			

			for (WorkItemExportFile wi:lista)
			{
				String [] tab = {wi.projectArea,Integer.toString(wi.id),wi.summary,wi.type,wi.category,wi.plannedFor,wi.description,wi.owner,wi.priority,wi.severity,wi.tags,wi.duration.toString(),wi.dueDate};
				product.writeRecord(tab);
			}

			
			product.close();
			System.out.println("Created output file - "+fileName);
		} catch (IOException e) {
			System.out.print("Application was not able to create "+fileName+" file.");
			System.exit(1);
		}

	}
}
