import numpy as np

def get_recommended_drug_list(drug_list):
    drug_data = []
    for data in drug_list:
        drug_data.append(list(data.values())[0])
        
    
    return np.array(np.meshgrid(["dfdf","dfdfd"],["adf","asdrf","qwer"]), dtype=object).T.reshape(-1, 2)

if __name__=="__main__":
    data = []
    print(get_recommended_drug_list(data))