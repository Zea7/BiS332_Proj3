import itertools
from .init_connection import JVMConnection
from ..api.interaction_request import get_interaction

class DrugFinder(JVMConnection):
    def __init__(self):
        super().__init__()
    
    def get_drug_list(self, disease_names):
        with open("./bio_data/Disease_List.txt", "w", encoding="utf8") as f:
            for name in disease_names:
                f.write(name + "\n")
        self.testmain.getDrugList()
        with open("./bio_data/Drug_List.txt", "r", encoding='utf-8') as f:
            drug_lines = f.readlines()
        with open("./bio_data/Related_Disease_List.txt", "r", encoding='utf-8') as f:
            related_disease = f.readlines()

        for index, name in enumerate(related_disease):
            related_disease[index] = name.replace("\n", "")
        
        drug_list = []
        drug_set = []
        for line in drug_lines:
            data = line.split("(")[1].split(", ")[:-1]
            if len(data) >= 1:
                drug_set.append(data)
            drug_list.extend(data)
            print(data)
        
        bad_couple = []
        ret = get_interaction(drug_list)
        for data in ret:
            if data['rating'] in ['X','D','C']:
                bad_couple.append(data['name'])
        print(bad_couple)
        
        return_drugs = []
        drug_product = list(itertools.product(*drug_set))
        print(drug_product)
        for drugs in drug_product:
            check = list(set(drugs).intersection(i) for i in bad_couple)
            possible = True
            for i in check:
                if len(i) == 2:
                    possible = False
                    break
            
            if possible:
                return_drugs = list(drugs)
                break
    
        print(return_drugs)
        return return_drugs, related_disease
                
    
        
    def __del__(self):
        super().__del__()
