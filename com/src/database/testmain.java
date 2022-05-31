
// testmain.java
// $ java main
package com.src.database;


import java.io.*;
import java.sql.*;
import java.util.*;

public class testmain {

	public static void initDB(String[] args) throws Exception {
		FileParser parser = new FileParser();
		parser.initiate(args);
	}

	public static void getDrugList() throws Exception {
		FileParser parser = new FileParser();
		BufferedReader reader = new BufferedReader(new FileReader("./bio_data/Disease_List.txt"));
		FileWriter drugWriter = new FileWriter("./bio_data/Drug_List.txt");
		FileWriter disWriter = new FileWriter("./bio_data/Related_Disease_List.txt");

		ArrayList<String> diseaseNames = new ArrayList<String>();
		ArrayList<String> drugList;
		String line;
		while((line=reader.readLine())!=null){
			System.out.println(line);
			line = line.replace("\n","");
			diseaseNames.add(line);
			drugList = parser.dbAccess.findDrugWithDisName(line);
			drugWriter.write(line + " (");
			for(String name:drugList){
				drugWriter.write(name + ", ");
			}
			drugWriter.write(")" + System.lineSeparator());
			drugWriter.flush();
		}

		ArrayList<String> relatedDis = parser.dbAccess.findRelatedDisWithNames(diseaseNames);
		for(String name:relatedDis){
			disWriter.write(name + System.lineSeparator());
			disWriter.flush();
		}

		drugWriter.close();
		disWriter.close();
		reader.close();

	}
	public static void main(String[] args) throws Exception {
		try {
			int choice;
			Scanner scan = new Scanner(System.in);
			String table = "";
			String condition = "";
			String input = "";
			int inputInt;
			FileParser parser = new FileParser();
			ArrayList<String> diseaeses = new ArrayList<String>();
			ArrayList<String> disease_names = new ArrayList<String>();
			ArrayList<Integer> disease_ids = new ArrayList<Integer>();
			String[] inputArr;
			int index;
			System.out.println("Welcome to a Gene-Dieases DB.");
			testmain.getDrugList();

			String[] menuList = new String[11];
			menuList[0] = "#0. Initiate the Database							";
			menuList[1] = "#1. find related genes2";
			menuList[2] = "#2. find related diseases";
			menuList[3] = "#3. find related genes";
			menuList[4] = "#4. disease id(OMIM)들을 받아서 연관된 gene ids를 반환";
			menuList[5] = "#5. disease id(OMIM)들을 받아서 연관된 diseases를 반환 (가능성 높은 순)";
			menuList[6] = "#6. disease ids -> (gene ids ->) drug ids";
			menuList[7] = "#7. ";
			menuList[8] = "#8. Current Table List";
			menuList[9] = "#9. Quit		  									";
			menuList[10] = "#################################################";
			
			String menu = String.format("%s\n%s\n%s\n%s\n%s\n%s\n%s\n%s\n%s\n%s\n%s\n%s\nEnter Your Choice: ",
			menuList[10], menuList[0], menuList[1],menuList[2], menuList[3], menuList[4], menuList[5], menuList[6], menuList[7], menuList[8], menuList[9], menuList[10]);
			System.out.print(menu);
			choice = Integer.parseInt(scan.nextLine());
			
			while(true){
				if(choice==0){
					System.out.println("Initiate DB.");
					parser.initiate(args);

				} else if (choice==1) {
                    System.out.print("Enter NCBI gene id : ");
					inputInt = Integer.parseInt(scan.nextLine());
					parser.dbAccess.findRelatedGenes2(inputInt);

                }else if (choice==3) {
					System.out.print("Enter disease OMIM id : ");
					inputInt = Integer.parseInt(scan.nextLine());
					parser.dbAccess.findRelatedGenes(inputInt);

				} else if (choice==4) {
					System.out.print("Enter disease OMIM id (enter 999999 to finish) : ");
					inputInt = Integer.parseInt(scan.nextLine());
					while (inputInt != 999999) {
						disease_ids.add(inputInt);
						System.out.print("Enter disease OMIM id (enter 999999 to finish) : ");
						inputInt = Integer.parseInt(scan.nextLine());
					}
					parser.dbAccess.possible_gene(disease_ids);

				} else if (choice==5) {
					System.out.print("Enter disease OMIM id (enter 999999 to finish) : ");
					inputInt = Integer.parseInt(scan.nextLine());
					while (inputInt != 999999) {
						disease_ids.add(inputInt);
						System.out.print("Enter disease OMIM id (enter 999999 to finish) : ");
						inputInt = Integer.parseInt(scan.nextLine());
					}
					parser.dbAccess.possible_dis(disease_ids);

				} else if (choice==6) {
					System.out.print("Enter disease OMIM id (enter 999999 to finish) : ");
					inputInt = Integer.parseInt(scan.nextLine());
					while (inputInt != 999999) {
						disease_ids.add(inputInt);
						System.out.print("Enter disease OMIM id (enter 999999 to finish) : ");
						inputInt = Integer.parseInt(scan.nextLine());
					}
					parser.dbAccess.findDrugWithDisease(disease_ids);

				} else if (choice==7) {
					System.out.print("Enter disease OMIM id (enter 999999 to finish) : ");
					inputInt = Integer.parseInt(scan.nextLine());
					while (inputInt != 999999) {
					   disease_ids.add(inputInt);
					   System.out.print("Enter disease OMIM id (enter 999999 to finish) : ");
					   inputInt = Integer.parseInt(scan.nextLine());
					}
					diseaeses = parser.dbAccess.possible_dis(disease_ids);
					System.out.print("Enter the indexes for drug search: ");
					input = scan.nextLine();
					inputArr = input.split(" ");
					for(String str : inputArr){
					   index = Integer.parseInt(str);
					   disease_names.add(diseaeses.get(index));
					}
					parser.dbAccess.findDrugWithDisease2(disease_names);
	 
				}else if(choice==8){	// current table list
					parser.dbAccess.DBList();

				} else if(choice==9){	// quit
					System.out.println("Quit.");
					break;

				} else {
					System.out.println("WARNING: Please Enter a number 1 ~ 6");
				}
				System.out.print(menu);

				// variable initiation
				disease_ids = new ArrayList<Integer>();
				choice = Integer.parseInt(scan.nextLine());
			}
			parser.dbAccess.DBDisconnect();
		} 
		catch (Exception ex) {
			System.out.println("end");
			ex.printStackTrace();
		}
	}
}
