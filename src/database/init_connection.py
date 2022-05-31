import subprocess
import shutil
import jpype


class JVMConnection():
    def __init__(self):
        self.__start_connection_with_java()
        self.__init_jvm()

    def __start_connection_with_java(self):
        subprocess.run(["cd", "./com/src/database","&","javac", "-d", ".","*.java","-encoding","UTF8","&","jar","-cvf","database.jar","com\src\database\*.class"], shell=True, check=True)
        source = r"./com/src/database/database.jar"
        destination = r"./src/database/database.jar"
        shutil.copyfile(source, destination)
    
    def __init_jvm(self):
        classpath = "D:\Coding\Languages\JDBC\postgresql-42.3.6.jar;./src/database/database.jar"
        jpype.startJVM(
            "-ea",
            classpath=classpath.split(";")
        ) 
        self.DBAccess = jpype.JClass("com.src.database.DBAccess")
        self.FileParser = jpype.JClass("com.src.database.FileParser")
        self.testmain = jpype.JClass("com.src.database.testmain")
        self.pj3 = jpype.JClass("com.src.database.pj3")
    

    def init_db_biojoin2(self, args):
        return self.testmain.initDB(args)
    
    def init_db_pharmgkb(self):
        subprocess.run(["cd", "./com/src/database","&","java", "com.src.database.pj3",
                        "../../../bio_data/gene_gene_relation.txt", "../../../bio_data/gene_disease_relation.txt", 
                        "../../../bio_data/geneIDinfo.txt",
                        "../../../bio_data/CTD_chem_gene_ixns.tsv",
                        "../../../bio_data/CTD_chemicals_diseases.tsv"], shell=True, check=True)
    
    def __del__(self):
        jpype.shutdownJVM()

init_biojoin_database = ["../../bio_data/SNP.txt", "../../bio_data/Homo_sapiens_gene_info.txt", "../../bio_data/disease_OMIM.txt", "../../bio_data/gene_OMIM.txt"]

