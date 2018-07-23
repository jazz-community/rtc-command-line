package com.cli.ccmauto.cli;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import com.beust.jcommander.Parameter;
import com.beust.jcommander.Parameters;
import com.csvreader.CsvReader;
import com.csvreader.CsvWriter;

@Parameters(commandDescription = "Creating new work item based on provided parameters from CSV file.")
public class WorkItemCreateFile {
	
	@Parameter(names = { "-i", "--inputFile" }, description = "CSV file that will be used to create work items in IBM Rational Team Concert. Use -e or --example command to generate sample csv file", order=1, required = true)
	public String inputFile;
	
	@Parameter(names = { "-o", "--outputFile" }, description = "CSV file that will be used output file", order=2)
	public String outputFile;
	
	@Parameter(names = { "-e", "--example" }, description = "Generate sample CSV input file", order=3, help=true)
	public boolean example;
	
	public void generateExampleFile()
	{
		CsvWriter product = new CsvWriter("createWorkItemFromFileExample.csv");
		try {
			String[] header = {"projectArea","summary","type","description","owner","priority","severity","tags","duration","dueDate","plannedFor"};
			product.writeComment("This is sample CSV file that can be used to create multiple Work Items using ACCM app.");
			product.writeComment("Please leave empty values if do not want to specify option parameters.");
			product.writeComment("Mandatory columns: projectArea, summary, type");
			product.writeComment("Optional columns: description, owner, priority, severity, tags, duration, dueDate, plannedFor.");
			product.writeRecord(header);
			
			product.close();
			System.out.println("Generated sample file - createWorkItemFromFileExample.csv");
		} catch (IOException e) {
			System.out.print("Application was not able to create createWorkItemFromFileExample file.");
			System.exit(1);
		}

	}

	public void generateOutputFile(String fileName,List<WorkItemCreate> fails, List<WorkItemCreate> success)
	{
		CsvWriter product = new CsvWriter(fileName);
		try {
			String[] header = {"Creation status","id","projectArea","summary","type","description","owner","priority","severity","tags","duration","dueDate","plannedFor"};
			product.writeRecord(header);
			product.writeComment("List of successfully created work items with id(s)");			

			for (WorkItemCreate wi:success)
			{
				String [] tab = {wi.creationStatus,Integer.toString(wi.id),wi.projectArea,wi.summary,wi.type,wi.description,wi.owner,wi.priority,wi.severity,wi.tags,wi.dueDate,wi.plannedFor};
				product.writeRecord(tab);
			}
			product.writeComment("List of not created work items with reasons of failure");
			for (WorkItemCreate wi:fails)
			{
				String [] tab = {wi.creationStatus,Integer.toString(wi.id),wi.projectArea,wi.summary,wi.type,wi.description,wi.owner,wi.priority,wi.severity,wi.tags,wi.dueDate,wi.plannedFor};
				product.writeRecord(tab);
			}
			
			product.close();
			System.out.println("Created output file - "+fileName);
		} catch (IOException e) {
			System.out.print("Application was not able to create "+fileName+" file.");
			System.exit(1);
		}

	}
	
	public List<WorkItemCreate> importWorkItems(String filename)
	{
		List<WorkItemCreate> lista = new ArrayList<WorkItemCreate>();
		try{
		CsvReader wi = new CsvReader(filename);
		
		wi.readHeaders();
		while (wi.readRecord())
		{
			if(wi.getRawRecord().startsWith("#"))
			{
				wi.skipLine();
			}
			else{
				String projectArea=null;
				String summary=null;
				String type=null;
				String description=null;
				String owner=null;
				String priority=null;
				String severity=null;
				String tags=null;
				Long duration=null;
				String dueDate=null;
				String plannedFor=null;
				
				//Parsowanie
				if (wi.get("projectArea").length()>0)
				{
					projectArea=wi.get("projectArea");
				}
				
				if (wi.get("summary").length()>0)
				{
					summary=wi.get("summary");
				}
				
				if (wi.get("type").length()>0)
				{
					type=wi.get("type");
				}
				
				if (wi.get("description").length()>0)
				{
					description=wi.get("description");
				}
				
				if (wi.get("owner").length()>0)
				{
					owner=wi.get("owner");
				}
				
				if (wi.get("priority").length()>0)
				{
					priority=wi.get("priority");
				}
				
				if (wi.get("severity").length()>0)
				{
					severity=wi.get("severity");
				}
				
				if (wi.get("tags").length()>0)
				{
					tags=wi.get("tags");
				}
				
				if (wi.get("duration").length()>0)
				{
					duration=new Long(wi.get("duration"));
				}
				
				if (wi.get("dueDate").length()>0)
				{
					dueDate=wi.get("dueDate");
				}
				
				if (wi.get("plannedFor").length()>0)
				{
					plannedFor=wi.get("plannedFor");
				}
				
				if (projectArea == null || summary==null || type==null)
				{
					throw new IOException();
				}
				WorkItemCreate newWI = new WorkItemCreate();
				newWI.projectArea=projectArea;				
				newWI.summary=summary;
				newWI.type=type;
				newWI.description=description;
				newWI.owner=owner;
				newWI.priority=priority;
				newWI.severity=severity;
				newWI.tags=tags;
				newWI.duration=duration;
				newWI.dueDate=dueDate;
				newWI.plannedFor=plannedFor;
				lista.add(newWI);
			}
		}
		wi.close();
		}
		catch(FileNotFoundException ex)
		{
			System.out.print("File "+filename+" does not exist.");
			
		} catch (IOException e) {
			System.out.print("File "+filename+" is not valid input file.");
			System.exit(1);
		}
		
		return lista;
	}
}
