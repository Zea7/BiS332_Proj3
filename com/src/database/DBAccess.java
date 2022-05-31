package com.src.database;


import java.io.*;
import java.sql.*;
import java.util.*;

import javax.xml.stream.events.Namespace;

class DBAccess {
	Connection DBConn = null;
	Statement DBStmt = null;

	DBAccess() throws Exception {
		DBConnect();
	}

	private void DBConnect() throws Exception {
		String driver = "org.postgresql.Driver";
		String url = "jdbc:postgresql://localhost:5432/"+ "s20190581";
		String ID= "s20190581"; 
		String Passwd= "zeaear714"; 

			Class.forName(driver);
			DBConn = DriverManager.getConnection(url, ID, Passwd);
		DBStmt = DBConn.createStatement();
	}

	public void DBInitiate() throws Exception {
		DBDrop("gene_disease");
		DBDrop("snp");
		DBDrop("imsi");
		DBDrop("disease");
		DBDrop("gene");
		DBCreateGeneTable();
		DBCreateSNPTable();
		DBCreateDiseaseTable();
		DBCreateGeneDiseaseTable();
		DBCreateImsiTable();
	}

	public void DBList() throws Exception {
		String columnName;
		ResultSet rs = null;
		List<String> tableNameList = new ArrayList<String>();
		List<Integer> rowCountList = new ArrayList<Integer>();
		int tableCount = 0;
		int i;
		String query = "SELECT tablename FROM PG_TABLES where schemaname = 'public';";
		rs = DBStmt.executeQuery(query);
		
		System.out.println("==============Current Table list=================");
		while(rs.next()) {	// save table name in 'tableNameList'
			tableNameList.add(rs.getString("tablename"));
			tableCount += 1;
		}

		for(i=0; i<tableCount; i++){	// save the number of rows in 'rowCountList'
			query = "select count(*) from " +tableNameList.get(i) +";";
			rs = DBStmt.executeQuery(query);
			rs.next();
			rowCountList.add(rs.getInt("count"));
		}

		for(i=0; i<tableCount; i++){	// print {TABLENAME}({COLUMN_NAME}) {ROWCOUNT}rows
			columnName = "";
			query = "SELECT column_name FROM INFORMATION_SCHEMA.COLUMNS WHERE  TABLE_NAME = \'"+ tableNameList.get(i)+"\';";
			rs = DBStmt.executeQuery(query);
			while(rs.next()){	// save column names in 'columnName'
				columnName = columnName + rs.getString("column_name")+',';
			}
			System.out.println((i+1)+". "+tableNameList.get(i)+"(" +columnName.substring(0,columnName.length()-1)+") "+rowCountList.get(i)+"rows");
		}
		System.out.println("=================================================");
	}
	
	private void DBCreateGeneTable() throws Exception {
		String query = "CREATE TABLE Gene (\n"
		+"tax_ID integer,\n"
		+"gene_ID integer PRIMARY KEY,\n"
		+"symbol VARCHAR(100),\n"
		+"synonyms VARCHAR(500),\n"
		+"chromosome VARCHAR(10),\n"
		+"map_location VARCHAR(100),\n"
		+"gene_description VARCHAR(255),\n"
		+"gene_type VARCHAR(100),\n"
		+"modification_date DATE\n"
		+");";
		DBStmt.executeUpdate(query);
		System.out.println(query);
	}

	private void DBCreateSNPTable() throws Exception {
		String query = "CREATE TABLE SNP (\n"
		+"SNP_ID integer PRIMARY KEY,\n"
		+"chromosome VARCHAR(10),\n"
		+"SNP_position bigint,\n"
		+"gene_symbol_neighbor VARCHAR(100),\n"
		+"allele_ancestral VARCHAR(1),\n"
		+"allele_minor VARCHAR(1)\n"
		+");";
		DBStmt.executeUpdate(query);
		System.out.println(query);
	}

	private void DBCreateDiseaseTable() throws Exception {
		String query = "CREATE TABLE Disease (\n"
		+"disease_ID integer PRIMARY KEY,\n"
		+"disease_name VARCHAR(200)\n"
		+");";
		DBStmt.executeUpdate(query);
		System.out.println(query);
	}

	private void DBCreateGeneDiseaseTable() throws Exception {
		String query = "CREATE TABLE Gene_Disease (\n"
		+"gene_ID integer CONSTRAINT gene_ID REFERENCES Gene (gene_ID),\n"
		+"symbol VARCHAR(100),\n"
		+"disease_ID integer CONSTRAINT disease_ID REFERENCES Disease (disease_ID),\n"
		+"PRIMARY KEY (gene_ID, disease_ID)\n"
		+");";
		DBStmt.executeUpdate(query);
		System.out.println(query);
	}

	private void DBCreateImsiTable() throws Exception {
		String query = "CREATE TABLE Imsi (\n"
		+"symbol VARCHAR(100),\n"
		+"disease_ID integer\n"
		+");";
		DBStmt.executeUpdate(query);
		System.out.println(query);
	}

	public void DBDrop(String table) throws Exception {
		String query = "DROP TABLE " + table +";";
		try {
			DBStmt.executeUpdate(query);
			System.out.println(query);
		} catch (Exception ex) {
			System.out.println(ex);
		}
		
	}

	public void DBDisconnect() throws Exception {
		DBConn.close();
	}

	public void insertSNP(String[] records) throws Exception {
		// System.out.println("insertData : SNP");
	
		String query = "";
        for (int i = 0; i < records.length; i++) {
			if (((i % 6) == 0) || (i % 6) == 2) {	// column 1(snp id), 3(position) -> number
				query = query + records[i] + ",";
			}
			else {		// others -> string
				query = query + "'" + records[i] + "',";
			}
		}
		query = "INSERT INTO SNP VALUES (" + query.substring(0, query.length()-1) + ");"; // remove ','
		DBStmt.executeUpdate(query);
	}

	public void insertGene(String[] records) throws Exception {
		// System.out.println("insertData : Gene");
	
		String query = "";
        for (int i = 0; i < records.length; i++) {
			if (((i % 9) == 0) || (i % 9) == 1) {	// column 1(tax id), 2(gene id) -> number
				query = query + records[i] + ",";
			} else if ((i % 9) == 8) {		// column 9(mod date) -> date
				query = query + "'" + records[i].substring(0, 4) + "-" + records[i].substring(4, 6) + "-" + records[i].substring(6, 8) + "',";
			}
			else {		// others -> string
				query = query + "'" + records[i] + "',";
			}
		}
		query = "INSERT INTO Gene VALUES (" + query.substring(0, query.length()-1) + ");"; // remove ','
		DBStmt.executeUpdate(query);
	}

	public void insertDisease(String[] records) throws Exception {
		//System.out.println("insertData : Disease");
	
		String query = "";
        for (int i = 0; i < records.length; i++) {
			if (records[i].matches("[0-9]+")) {	// number
				query = query + records[i] + ","; 
			} else {
				query = query + "'" + records[i] + "',";
			}
		}
		query = "INSERT INTO Disease VALUES (" + query.substring(0, query.length()-1) + ");"; // remove ','
		DBStmt.executeUpdate(query);
	}

	public void insertGeneDisease(String[] records) throws Exception {
		// System.out.println("insertData : Imsi");

		String query = "";
        for (int i = 0; i < records.length; i++) {
			if (i != 0) {	// number
				query = query + records[i] + ","; 
			} else {
				query = query + "'" + records[i] + "',";
			}
		}
		query = "INSERT INTO Imsi VALUES (" + query.substring(0, query.length()-1) + ");"; // remove ','
		DBStmt.executeUpdate(query);
	}

	public void makeGeneDisease() throws Exception {
		// System.out.println("makeTable : GeneDisease");

		String endQuery = "INSERT INTO Gene_Disease ( gene_id, symbol, disease_id) \n" 
			+ "SELECT a.gene_id, b.symbol, b.disease_id \n" 
			+ "FROM Gene a \n"
			+ "INNER JOIN Imsi b ON a.symbol = b.symbol;";
		DBStmt.executeUpdate(endQuery);
		System.out.println(endQuery);
	}

	public void findGeneInfoWithGeneSymbol(String symbol) throws Exception {
		System.out.println("\nfind Gene Information using Gene symbol.");

		// count the number of targets
		ResultSet rs = null;
		String query = "SELECT count(*) FROM Gene WHERE symbol = \'" + symbol + "\';";
		rs = DBStmt.executeQuery(query);
		rs.next();
		System.out.println(rs.getInt("count") +" gene information is found.");

		// print targets
		query = "SELECT * FROM Gene WHERE symbol = \'" + symbol + "\';";
		rs = DBStmt.executeQuery(query);
		int ctax;
		int cgid;
		String csym = null;
		String csyn = null;
		String cmap = null;
		String cdes = null;
		String ctyp = null;
		String cdat = null;
		System.out.println("=========================================================================================================================");
		System.out.println("#Tax ID"+"\t"+"Gene ID"+"\t"+"Symbol\t"+
		"Synonyms\t"+"Chromosome\t"+"Map Location\t"+"Description\t"+
		"Type of Gene\t"+"Modification Date");
		System.out.println("=========================================================================================================================");
		while(rs.next()) {
			ctax = rs.getInt("tax_ID");
			cgid = rs.getInt("gene_ID");
			csym = rs.getString("symbol");
			csyn = rs.getString("chromosome");
			cmap = rs.getString("map_location");
			cdes = rs.getString("gene_description");
			ctyp = rs.getString("gene_type");
			cdat = rs.getString("modification_date");
			System.out.println(ctax + "\t" + cgid + "\t" + csym + "\t"
			+ csyn + "\t" + cmap + "\t" + cdes + "\t" + ctyp + "\t" + 
			cdat);
		}
		System.out.println("=========================================================================================================================");
	}

	public void findGeneSymWithChromo(String chromosome) throws Exception {
		System.out.println("\nGiven a chromosome id, find gene symbols located in the chromosome.");
		// count the number of targets
		ResultSet rs = null;
		String query = "SELECT count(symbol) FROM Gene WHERE chromosome = \'" + chromosome + "\';";
		rs = DBStmt.executeQuery(query);
		rs.next();
		System.out.println(rs.getInt("count") +" gene symbols are found.");

		// print targets
		query = "SELECT symbol FROM Gene WHERE chromosome = \'" + chromosome + "\';";
		rs = DBStmt.executeQuery(query);
		System.out.println("================================================");
		System.out.println("Gene Symbol List which located in the chromosome " + chromosome);
		System.out.println("================================================");
		while (rs.next()) {
			System.out.print(rs.getString("symbol") + "\t");
		}
		System.out.println("\n================================================");
	}

	public void findDiseaseWithSNP(String snpID) throws Exception {
		System.out.println("Given a SNP ID, find all diseases associated with the SNP.");
		// count the number of targets
		ResultSet rs = null;
		String query = "SELECT count(d.disease_name) FROM SNP s INNER JOIN Gene g ON " +
		"(s.gene_symbol_neighbor = g.symbol AND s.chromosome = g.chromosome) " +
		"INNER JOIN Gene_Disease gd ON g.gene_ID = gd.gene_ID INNER JOIN Disease d ON gd.disease_ID = d.disease_ID " + 
		"WHERE s.SNP_ID = " + snpID + ";";
		rs = DBStmt.executeQuery(query);
		rs.next();
		System.out.println(rs.getInt("count") +" diseases are found.");

		// print targets
		query = "SELECT d.disease_name as Disease_name FROM SNP s INNER JOIN Gene g ON " +
		"(s.gene_symbol_neighbor = g.symbol AND s.chromosome = g.chromosome) " +
		"INNER JOIN Gene_Disease gd ON g.gene_ID = gd.gene_ID INNER JOIN Disease d ON gd.disease_ID = d.disease_ID " + 
		"WHERE s.SNP_ID = " + snpID + ";";
		rs = DBStmt.executeQuery(query);
		System.out.println("===========================================================");
		System.out.println("disease name list which is associated with snp id " + snpID);
		System.out.println("===========================================================");
		while (rs.next()) {
			System.out.println(rs.getString("Disease_name"));
		}
		System.out.println("===========================================================");
	}

	public void findSNPWithDisease(String diseaseName) throws Exception {
		System.out.println("Given a disease name, find all SNP IDs associated with the disease.");
		// count the number of targets
		ResultSet rs = null;
		String query = "SELECT count(s.SNP_ID) FROM Disease d INNER JOIN Gene_Disease gd " + 
		"ON d.disease_ID = gd.disease_ID INNER JOIN Gene g ON gd.gene_ID = g.gene_ID " +
		"INNER JOIN SNP s ON s.gene_symbol_neighbor = g.symbol WHERE d.disease_name = "+
		"\'" + diseaseName + "\';";
		rs = DBStmt.executeQuery(query);
		rs.next();
		System.out.println(rs.getInt("count") +" SNP IDs are found.");

		// print targets
		query = "SELECT s.SNP_ID as SNP_ID FROM Disease d INNER JOIN Gene_Disease gd " + 
		"ON d.disease_ID = gd.disease_ID INNER JOIN Gene g ON gd.gene_ID = g.gene_ID " +
		"INNER JOIN SNP s ON s.gene_symbol_neighbor = g.symbol WHERE d.disease_name = "+
		"\'" + diseaseName + "\';";
		rs = DBStmt.executeQuery(query);
		System.out.println("=================================================================================");
		System.out.println("SNP ID list which is associated with disease " + diseaseName);
		System.out.println("=================================================================================");
		while (rs.next()) {
			System.out.print(rs.getInt("SNP_ID")+"\t");
		}
		System.out.println("\n=================================================================================");
	}

	public void updateRecord(String table, String target, String condition) throws Exception {
		System.out.println("\nupdate records in \'" + table + "\' which satisfy \'" + condition + "\' to \'" + target + "\'");
		
		String query = "UPDATE " + table + " set " + target + " where " + condition + ";";
		DBStmt.executeUpdate(query);
		System.out.println(query);

		// count the number of updated rows
		ResultSet rs = null;
		query = "SELECT count(*) from "+ table +" where " + condition  + ";";
		rs = DBStmt.executeQuery(query);
		rs.next();
		System.out.println(rs.getInt("count") +" rows updated.");
	}

	public void deleteRecord(String table, String condition) throws Exception {
		// count the number of deleted rows
		int deletedRowCount;
		ResultSet rs = null;
		String query = "SELECT count(*) from "+ table +" where " + condition  + ";";
		rs = DBStmt.executeQuery(query);
		rs.next();
		deletedRowCount = rs.getInt("count");

		System.out.println("\nDelete records in \'" + table + "\' which satisfy \'" + condition + "\'");
		query = "DELETE from " + table + " where " + condition + ";";
		DBStmt.executeUpdate(query);
		System.out.println(query);
		System.out.println(deletedRowCount+" rows deleted.");
	}

	public void readData() throws Exception {
		System.out.println("readData");
		ResultSet rs = null;
		int cno;
		String cname = null;
		String cphone = null;

		String query = "select * from mydata;";
		rs = DBStmt.executeQuery(query);
		System.out.println("===========================");
		System.out.println("NO"+"\t"+"NAME"+"\t"+"PHONE");
		System.out.println("===========================");
		while(rs.next()) {
			cno = rs.getInt("no");
			cname = rs.getString("name");
			cphone = rs.getString("phone");
			System.out.println(cno+"\t"+cname+"\t"+cphone);
		}
		System.out.println("===========================");
	}

	public void CreateGeneGeneTable() throws Exception {
		String query = "CREATE TABLE gene_gene (\n"
		+"gene1_ID VARCHAR(100),\n"
		+"gene1_name VARCHAR(100),\n"
		+"gene2_ID VARCHAR(100),\n"
		+"gene2_name VARCHAR(100)\n"
		+");";
		DBStmt.executeUpdate(query);
		System.out.println(query);
	}

	public void CreateGeneDisTable() throws Exception {
		String query = "CREATE TABLE gene_dis (\n"
		+"gene_ID VARCHAR(100),\n"
		+"gene_name VARCHAR(100),\n"
		+"disease_ID VARCHAR(100),\n"
		+"disease_name VARCHAR(100),\n"
		+"importance integer\n"
		+");";
		DBStmt.executeUpdate(query);
		System.out.println(query);
	}

	public void CreateGeneIDinfoTable() throws Exception {
		String query = "CREATE TABLE gene_IDinfo (\n"
		+"PharmGKB_ID VARCHAR(100),\n"
		+"gene_ID integer,\n"
		+"gene_name VARCHAR(500),\n"
		+"symbol VARCHAR(100)\n"
		+");";
		DBStmt.executeUpdate(query);
		System.out.println(query);
	}

	public void CreateGeneDrugTable() throws Exception {
		String query = "CREATE TABLE Gene_drug (\n"
		+"drug VARCHAR(200),\n"
		+"drug_ID VARCHAR(100),\n"
		+"CasRN VARCHAR(100),\n"
		+"symbol VARCHAR(30),\n"
		+"gene_ID integer,\n"
		+"gene_forms VARCHAR(100),\n"
		+"organism VARCHAR(50),\n"
		+"organism_ID integer,\n"
		+"interaction VARCHAR(600),\n"
		+"interaction_actions VARCHAR(500),\n"
		+"PubMed_ID integer\n"
		+");";
		DBStmt.executeUpdate(query);
		System.out.println(query);
	}

	public void CreateDiseaseDrugTable() throws Exception {
		String query = "CREATE TABLE disease_drug (\n"
		+"drug VARCHAR(200),\n"
		+"drug_ID VARCHAR(10),\n"
		+"CasRN VARCHAR(100),\n"
		+"disease VARCHAR(200),\n"
		+"disease_ID VARCHAR(20),\n"
		+"direct_evidence VARCHAR(100),\n"
		+"inference_gene_symbol VARCHAR(50),\n"
		+"inference_score real,\n"
		+"Omim_ID VARCHAR(100),\n"
		+"PubMed_ID integer\n"
		+");";
		DBStmt.executeUpdate(query);
		System.out.println(query);
	}

	public void insertGeneGene(String[] records) throws Exception {
		String query = "";
		for (int i = 0; i < records.length; i++) {
			if ((i == 0) || (i == 1) || (i == 3) || (i==4)) {
				query = query + "'" + records[i] + "',";
			}
		}
		query = "INSERT INTO gene_gene VALUES (" + query.substring(0, query.length()-1) + ");"; // remove ','
		DBStmt.executeUpdate(query);
	}

	public void insertGeneDis(String[] records) throws Exception {
		String query = "";
		for (int i = 0; i < records.length; i++) {
			if ((i == 0) || (i == 1) || (i == 3) || (i==4)) {
				query = query + "'" + records[i] + "',";
			}
			else if (i == 7) {
				if (records[i].equals("associated")) { query = query + "3" + ","; }
				else if (records[i].equals("ambiguous")) { query = query + "2" + ","; }
				else if (records[i].equals("not associated")) { query = query + "1" + ","; }
				else { query = query + "0" + ","; }
			}
		}
		query = "INSERT INTO gene_dis VALUES (" + query.substring(0, query.length()-1) + ");"; // remove ','
		DBStmt.executeUpdate(query);
	}

	public void insertGeneIDinfo(String[] records) throws Exception {
		String query = "";
		for (int i = 0; i < records.length; i++) {
			if ((i == 0) || (i == 2) || (i == 3)) {
				query = query + "'" + records[i] + "',";
			} else if (i == 1) {
				query = query + records[i] + ",";
			}
		}
		query = "INSERT INTO gene_IDinfo VALUES (" + query.substring(0, query.length()-1) + ");"; // remove ','
		DBStmt.executeUpdate(query);
	}

	public void insertGeneDrug(String[] records) throws Exception {
		
		String query = "";
        for (int i = 0; i < records.length; i++) {
			if (records[i].contains("'")){
				String[] strArr = records[i].split("'");
				records[i] = String.join("''",strArr);
			}
			if (records[i].equals("")){
				query = query + "null,";
			} else {
				if ((i % 11) == 4 || (i % 11) == 7 || (i % 11) == 10) {	// 5(gene_ID), 8(organism_ID), 11(PubMed_ID) -> number
					query = query + records[i] + ",";
				}
				else {		// others -> string
					query = query + "'" + records[i] + "',";
				}
			}
		}
		query = "INSERT INTO gene_drug VALUES (" + query.substring(0, query.length()-1) + ");"; // remove ','
		DBStmt.executeUpdate(query);
	}

	public void insertDiseaseDrug(String[] records) throws Exception {
		
		int index = 0;
		String query = "";
        for (int i = 0; i < records.length; i++) {
			if (records[i].contains("'")){
				String[] strArr = records[i].split("'");
				records[i] = String.join("''",strArr);
			}
			if (records[i].equals("")){
				query = query + "null,";
			} else {

				if ((i % 10) == 7 || (i % 10) == 9) {	// 8(inference_score), 10(PubMed_ID) -> number
					query = query + records[i] + ",";
				}
				else {		// others -> string
					query = query + "'" + records[i] + "',";
				}
			}
		}
		query = "INSERT INTO disease_drug VALUES (" + query.substring(0, query.length()-1) + ");"; // remove ','
		DBStmt.executeUpdate(query);
	}

	// disease ID(OMIM) -> gene ID(NCBI)
	public ArrayList<Integer> findRelatedGenes(int diseaseid) throws Exception {
		// disease OMIM id를 받아 gene_disease table에서 검색, 연관된 gene들의 NCBI id를 반환
		ArrayList<Integer> geneids = new ArrayList<Integer>();
		ResultSet rs = null;

		//System.out.println("================================================");
		//System.out.println("NCBI id of genes which are related with diseases");
		//System.out.println("================================================");

		String query = "SELECT gene_ID FROM Gene_Disease WHERE disease_ID = " + diseaseid + ";";
		rs = DBStmt.executeQuery(query);
		while (rs.next()) {
			//System.out.print(rs.getInt("gene_ID") + "\t");
			geneids.add(rs.getInt("gene_ID"));
		}
		return geneids;
	}

	// gene ID(NCBI) -> gene ID(PharmGKB)
	public ArrayList<Integer> findRelatedGenes2(int geneid) throws Exception {
		// Gene table의 gene_id를 받아 gene_gene table에서 검색, 연관된 gene들의 pharmGKB id를 반환
		ArrayList<Integer> geneids = new ArrayList<Integer>();
		ArrayList<String> GKBids = new ArrayList<String>();
		ResultSet rs = null;

		// System.out.println("================================================");
		// System.out.println("PharmGKB id of genes which are related with " + geneid);
		// System.out.println("================================================");
		geneids.add(geneid);
		String query = "SELECT PharmGKB_ID FROM gene_IDinfo WHERE gene_ID = " + geneid + ";";
		rs = DBStmt.executeQuery(query);
		//rs.next();
		if (!rs.next()) return geneids;
		String pharmGKBid = rs.getString("PharmGKB_ID");
		//System.out.print(pharmGKBid + "\t");
		
		query = "SELECT gene2_ID FROM gene_gene WHERE gene1_ID = \'" + pharmGKBid + "\';";
		rs = DBStmt.executeQuery(query);
		while (rs.next()) {
			//System.out.print(rs.getString("gene2_ID") + "\t");
			GKBids.add(rs.getString("gene2_ID"));
		}

		for(String str : GKBids){
			query = "SELECT gene_id FROM gene_IDinfo WHERE pharmgkb_id = \'" + str + "\';";
			rs = DBStmt.executeQuery(query);
			while (rs.next()) {
				//System.out.print(rs.getString("gene2_ID") + "\t");
				geneids.add(rs.getInt("gene_id"));
			}
		}
		// System.out.println("\n================================================");
		return geneids;
	}

	public ArrayList<String> findRelatedDis(List geneids) throws Exception {
		// geneids List를 받아 gene_dis table에서 검색, 연관된 diseases name을 반환
		ResultSet rs = null;
		String query;
		String geneid;
		ArrayList<String> disease_names = new ArrayList<String>();
		String dis_id;
		String dis_name;
		int importance;

		// System.out.println("================================================");
		// System.out.println("Related disease list");
		// System.out.println("================================================");
		// System.out.println("#disease ID"+"\t"+"disease name"+"\t"+"importance\n");
		for (int i = 0; i < geneids.size(); i++) {
			geneid = geneids.get(i).toString();
			query = "SELECT gd.gene_id, d.disease_id, d.disease_name from gene_disease gd, disease d WHERE (d.disease_id = gd.disease_id) AND (gd.gene_ID =" + geneid + ");";
			rs = DBStmt.executeQuery(query);
			while(rs.next()) {
				dis_name = rs.getString("disease_name");
				//System.out.println(dis_id + "\t" + dis_name + "\t" + importance);
				disease_names.add(dis_name);
			}
		}
		// System.out.println("================================================");
		return disease_names;
	}


// gene ID(PharmGKB) -> disease name
/*
	public ArrayList<String> findRelatedDis(List geneids) throws Exception {
		// geneids List를 받아 gene_dis table에서 검색, 연관된 diseases name을 반환
		ResultSet rs = null;
		String query;
		String geneid;
		ArrayList<String> disease_names = new ArrayList<String>();
		String dis_id;
		String dis_name;
		int importance;

		// System.out.println("================================================");
		// System.out.println("Related disease list");
		// System.out.println("================================================");
		// System.out.println("#disease ID"+"\t"+"disease name"+"\t"+"importance\n");
		for (int i = 0; i < geneids.size(); i++) {
			geneid = geneids.get(i).toString();
			query = "SELECT * from gene_dis WHERE gene_ID ='" + geneid + "\';";
			rs = DBStmt.executeQuery(query);
			while(rs.next()) {
				dis_id = rs.getString("disease_ID");
				dis_name = rs.getString("disease_name");
				importance = rs.getInt("importance");
				//System.out.println(dis_id + "\t" + dis_name + "\t" + importance);
				disease_names.add(dis_name);
				if (importance == 3) {disease_names.add(dis_name);}	// importance가 높은 경우 한번 더 추가해서 weight를 준다
			}
		}
		// System.out.println("================================================");
		return disease_names;
	}
*/


	public ArrayList<Integer> possible_gene(List diseaseids) throws Exception {
		ResultSet rs = null;
		String query;
		int dis_OMIM_id;
		int geneid;
		ArrayList<Integer> orig_genes = new ArrayList<Integer>();
		ArrayList<Integer> result = new ArrayList<Integer>();
		ArrayList<Integer> tempint;

		System.out.println("================================================");
		System.out.println("NCBI id of genes which are related with diseases");
		System.out.println("================================================");

		// 입력한 disease OMIM id와 연관된 gene id(NCBI)를 모두 찾아 orig_genes에 저장 (gene_disease table 이용)
		for (int i = 0; i < diseaseids.size(); i++) {
			dis_OMIM_id = (int)diseaseids.get(i);
			tempint = findRelatedGenes(dis_OMIM_id);
			orig_genes.addAll(tempint);
		}
		
		for (int i = 0; i < orig_genes.size(); i++) {
			geneid = (int)orig_genes.get(i);
			if (!result.contains(geneid)) { 
				result.add(geneid);
				System.out.print(geneid + "\t");
			}
		}

		System.out.println("\n================================================");
		
		return result;
	}

	private ArrayList<String> findDiseaseName(List diseaseids) throws Exception {
		ResultSet rs = null;
		ArrayList<String> diseaseList = new ArrayList<String>();
		int dis_OMIM_id;
		String query = "";
		String dis_name = "";

		for (int i = 0; i < diseaseids.size(); i++) {
			dis_OMIM_id = (int)diseaseids.get(i);
			query = "SELECT disease_name from disease WHERE disease_id = "+dis_OMIM_id +";";
			rs = DBStmt.executeQuery(query);
			while(rs.next()) {
				dis_name = rs.getString("disease_name");
				
			}
			diseaseList.add(dis_name);
		}
		return diseaseList;
	}

	public ArrayList<String> possible_dis(List diseaseids) throws Exception {
		// disease OMIM id들을 받아 걸릴 확률이 높은 disease 10개의 PharmGKB id를 반환
		int index = 0; 
		System.out.println("================================================");
		ResultSet rs = null;
		String query;
		int dis_OMIM_id;
		int geneid;
		ArrayList<Integer> orig_genes = new ArrayList<Integer>(); // common genes
		ArrayList<Integer> tempint;
		ArrayList<Integer> relate_genes = new ArrayList<Integer>();
		ArrayList<String> tempstr;
		ArrayList<String> related_diseases;
		ArrayList<String> result = new ArrayList<String>();

		// 입력한 disease OMIM id와 연관된 gene id(NCBI)를 모두 찾아 orig_genes에 저장 (gene_disease table 이용)
		for (int i = 0; i < diseaseids.size(); i++) {
			dis_OMIM_id = (int)diseaseids.get(i);
			tempint = findRelatedGenes(dis_OMIM_id);
			orig_genes.addAll(tempint);
		}

		// PharmGKB에서 검색하여 연관된 모든 gene들의 NCBI id를 저장
		for (int i = 0; i < orig_genes.size(); i++) {
			geneid = (int)orig_genes.get(i);
			tempint = findRelatedGenes2(geneid);
			relate_genes.addAll(tempint);
		}

		// 입력 OMIM ID, disease name 매칭
		ArrayList<String> diseaseNameArr = new ArrayList<String>();
		diseaseNameArr = findDiseaseName(diseaseids);
		Map<String,Integer> map_IDdisease = new HashMap<String, Integer>();
		for(int i = 0; i < diseaseids.size(); i++) {
			map_IDdisease.put(diseaseNameArr.get(i),(int)diseaseids.get(i));
		}

		// map에 각 disease가 나온 빈도 수를 세서 저장
		related_diseases = findRelatedDis(orig_genes);
		Map<String, Integer> map = new HashMap<String, Integer>();
		for (String str : related_diseases) {
			Integer cnt = map.get(str);
			if (cnt == null) { map.put(str, 1); }
			else { map.put(str, cnt + 1); }
		}

		// Occurence 많은 순으로 정렬
		List<String> listKeySet = new ArrayList<>(map.keySet());
		Collections.sort(listKeySet, (value1, value2) -> (map.get(value2).compareTo(map.get(value1))));
		for(String key : listKeySet) {
			if (map.get(key)>1){	// 2번 이상 나올 때
				if (map_IDdisease.get(key)!=null){
					System.out.println(index + ". "+ key + "(" + map_IDdisease.get(key)+") , " + "occurence : " + map.get(key));
				} else{
					System.out.println(index + ". "+  key + " , " + "occurence : " + map.get(key));
				}
				result.add(key);
				index = index + 1;
			}
		}
		return result;
	}

	// disease IDs 받아 관련 gene IDs 찾고, gene IDs로 drug IDs 찾기
	public void findDrugWithDisease(List diseaseids) throws Exception {
		System.out.println("\nfind Gene Information using Gene symbol.");
		List geneIDs = possible_gene(diseaseids);
		String geneIDArr = "";
		if(geneIDs.size()>0){
			for(int i = 0; i<geneIDs.size();i++)
				geneIDArr = geneIDArr + geneIDs.get(i) + ", ";
			geneIDArr = geneIDArr.substring(0, geneIDArr.length()-2); // remove ','
		}

		// count the number of targets
		ResultSet rs = null;
		String query = "SELECT count( DISTINCT drug ) FROM gene_drug WHERE gene_id in (" + geneIDArr + ");";
		rs = DBStmt.executeQuery(query);
		rs.next();
		System.out.println(rs.getInt("count") +" gene information is found.");
		
		// print targets
		query = "SELECT * FROM gene_drug WHERE gene_id in (" + geneIDArr + ");";
		rs = DBStmt.executeQuery(query);
		String cdrungName = null;
		String cdrugID;
		String cCasRN = null;
		String csym = null;
		int cgeneID;
		String cgeneForm = null;
		String corganism = null;
		int corganismID;
		String cinter = null;
		String cinteraction = null;
		int cpmid;
		System.out.println("=========================================================================================================================");
		System.out.println("drug"+"\t"+"drug_ID"+"\t"+"CasRN\t"+
		"symbol\t"+"gene_ID\t"+"gene_form\t"+"organism\t"+
		"organism_ID\t"+"interaction\t"+"interaction_action\t"+"PMID");
		System.out.println("=========================================================================================================================");
		while(rs.next()) {
			cdrungName = rs.getString("drug");
			cdrugID = rs.getString("drug_id");
			cCasRN = rs.getString("casrn");
			csym = rs.getString("symbol");
			cgeneID = rs.getInt("gene_id");
			cgeneForm = rs.getString("gene_forms");
			corganism = rs.getString("organism");
			corganismID = rs.getInt("organism_id");
			cinter = rs.getString("interaction");
			cinteraction = rs.getString("interaction_actions");
			cpmid = rs.getInt("pubmed_id");
			System.out.println(cdrungName + "\t" + cdrugID + "\t" + cCasRN + "\t"
			+ csym + "\t" + cgeneID + "\t" + cgeneForm + "\t" + corganism + "\t" + 
			corganismID + "\t" + cinter + "\t" + cinteraction + "\t" + cpmid);
		}
		System.out.println("=========================================================================================================================");
	}

	// disease IDs 받아 관련 drug IDs 찾기
	public ArrayList<String>  findDrugWithDisease2(List diseaeses) throws Exception {

		System.out.println("\nfind Gene Information using Gene symbol.");
		HashSet<String> set = new HashSet<String>();
		String diseaseArr = "";
		String diseaseStr = "";
		if(diseaeses.size()>0){
			for(int i = 0; i<diseaeses.size();i++){
				System.out.println(diseaeses.get(i));
				diseaseStr = "" + diseaeses.get(i);
				diseaseStr = diseaseStr.toLowerCase();
				if(diseaseStr.contains("$"))
					diseaseStr = diseaseStr.substring(1,diseaseStr.length()-1);
				if(diseaseStr.contains(","))
					diseaseStr = diseaseStr.split(",")[0]; // processing
				if(diseaseStr.contains("cancer"))
					diseaseArr = diseaseArr + "'"+ diseaseStr.replace("cancer","neoplasms") + "', ";
				diseaseArr = diseaseArr + "'"+ diseaseStr + "', ";
			}
			diseaseArr = diseaseArr.substring(0, diseaseArr.length()-2); // remove ','
		} else 
			diseaseArr = "'" + diseaseArr + "'";

		// count the number of targets
		ResultSet rs = null;
		String query = "SELECT count(*) FROM disease_drug WHERE (LOWER(disease) IN ("+ diseaseArr +"))  AND (direct_evidence = 'therapeutic');";
		rs = DBStmt.executeQuery(query);
		rs.next();
		System.out.println(rs.getInt("count") +" drug information is found.");
		
		// print targets
		//drug,drug_id,casrn,disease,disease_id,inference_score,omim_id
		query = "SELECT * FROM disease_drug WHERE (LOWER(disease) IN ("+ diseaseArr +"))  AND (direct_evidence = 'therapeutic');";
		rs = DBStmt.executeQuery(query);
		String cdrungName = null;
		String cdrugID = null;
		String cCasRN = null;
		String cdisease = null;
		String cdiseaseID = null;
		String cdirectEvidence= null;
		String cinferSymbol = null;
		double cscore;
		String comimID = null;
		int cpmid;
		System.out.println("=========================================================================================================================");
		System.out.println("drug"+"\t"+"drug_ID"+"\t"+"disease\t"+"disease_ID");
		System.out.println("=========================================================================================================================");
		while(rs.next()) {
			cdrungName = rs.getString("drug");
			cdrugID = rs.getString("drug_id");
			cdisease  = rs.getString("disease");
			cdiseaseID = rs.getString("disease_ID");
			System.out.println( cdrungName + "\t" + cdrugID + "\t" + cdisease + "\t" + cdiseaseID);
			set.add(cdrungName);
		}
		System.out.println("=========================================================================================================================");
		ArrayList<String> list = new ArrayList<>(set);
		return list;
	}

	public int findDisID(String DiseaseName) throws Exception {
      String query = "SELECT disease_ID FROM disease WHERE disease_name = \'" + DiseaseName + "\'";
      ResultSet rs = DBStmt.executeQuery(query);
	  int id = 0;
	  while(rs.next()){
		  id = rs.getInt("disease_ID");
	  }

	  System.out.println(id);

      return id;
   }

   public ArrayList<String> findRelatedDisWithNames(ArrayList<String> names) throws Exception {
      ArrayList<Integer> result = new ArrayList<Integer>();
      for(String i:names){
         result.add(findDisID(i));
      }

      return possible_dis(result);
   }

   public ArrayList<String> findDrugWithDisName(String name) throws Exception {
      ArrayList<String> ret = new ArrayList<String>();
      ret.add(name);
      return findDrugWithDisease2(ret);
   }
}
