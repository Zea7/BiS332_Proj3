package com.src.database;

import java.io.*;
import java.sql.*;
import java.util.*;
// $ java pj3 gene_gene_relation.txt gene_disease_relation.txt geneIDinfo.txt CTD_chem_gene_ixns.tsv CTD_chemicals_diseases.tsv

public class pj3 {
	public static void main(String[] args) throws Exception {
		try {
			System.out.println("Using \'relation\' in PharmGKB,");
			System.out.println("Construct gene-gene & gene-disease table");

			Scanner scan = new Scanner(System.in);
			DBAccess dbAccess = new DBAccess();

			dbAccess.DBDrop("gene_gene");
			dbAccess.DBDrop("gene_dis");
			dbAccess.DBDrop("gene_IDinfo");
			dbAccess.DBDrop("gene_drug");
			dbAccess.DBDrop("disease_drug");
			dbAccess.CreateGeneGeneTable();
			dbAccess.CreateGeneDisTable();
			dbAccess.CreateGeneIDinfoTable();
			dbAccess.CreateGeneDrugTable();
			dbAccess.CreateDiseaseDrugTable();

			for (int i = 0; i < 5; i++) {
				FileReader fileReader = new FileReader(args[i]);
				BufferedReader bufferReader = new BufferedReader(fileReader);
				String[] lineArray;
				int cnt = 0;
				
				String line = bufferReader.readLine();
				
				if (i == 3 || i == 4) {
					for(int j=0;j<29;j++)	// remove lines for gene_drung data file
            			line = bufferReader.readLine();
				} else 
					line = bufferReader.readLine();
				while(line != null) {
					lineArray = line.split("\t");
					if (i == 0) { dbAccess.insertGeneGene(lineArray); }
					else if (i == 1) { dbAccess.insertGeneDis(lineArray); }
					else if (i == 2) { dbAccess.insertGeneIDinfo(lineArray); }
					else if (i == 3) { dbAccess.insertGeneDrug(lineArray); }
					else if (i == 4) { dbAccess.insertDiseaseDrug(lineArray); }
					line = bufferReader.readLine();
					cnt = cnt + 1;
					if (cnt % 10000 == 1) {System.out.println(cnt + " records inserted");}
				}
				System.out.println("Data insertion is done.");
			}

			dbAccess.DBDisconnect();
		} 
		catch (Exception ex) {
			System.out.println("end");
			ex.printStackTrace();
		}
	}
}

