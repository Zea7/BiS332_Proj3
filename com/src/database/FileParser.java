
// FileParser.java
package com.src.database;

import java.io.*;
import java.sql.*;
import java.util.*;

class FileParser {

	DBAccess dbAccess = null;

	FileParser() throws Exception {
		System.out.println("File Parser");
		dbAccess = new DBAccess();
	}

	protected void initiate(String[] args) throws Exception {
		System.out.println("Create Table & Data Insertion");
		dbAccess.DBInitiate();
		for (int i = 0; i < 4; i++) {
			FileReader fileReader = new FileReader(args[i]);
			BufferedReader bufferReader = new BufferedReader(fileReader);
			String[] lineArray;
			int cnt = 0;
			
			String line = bufferReader.readLine();
			line = bufferReader.readLine();
			while(line != null) {
				lineArray = line.split("\t");
				insertDataToDB(lineArray, i);
				line = bufferReader.readLine();
				cnt = cnt + 1;
				if (cnt % 10000 == 1) {System.out.println(cnt + " records inserted");}
			}
			System.out.println("Data insertion is done.");
			bufferReader.close();
		}
		dbAccess.makeGeneDisease();
	}

	private	void insertDataToDB(String[] records, int idx) throws Exception {
		if (idx == 0) {
			dbAccess.insertSNP(records);
		} else if (idx == 1) {
			dbAccess.insertGene(records);	
		} else if (idx == 2) {
			dbAccess.insertDisease(records);
		} else {
			dbAccess.insertGeneDisease(records);
		}
	} 
}


