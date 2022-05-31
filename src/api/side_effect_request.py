import copy
from html.parser import HTMLParser
import requests

class __HTMLParser_GetIDS(HTMLParser):
    def __init__(self):
        super().__init__()
        self.drug_count = 0
        self.in_drug_list = False
        self.drug_names = []
        self.drug_ids = []
        self.get_names = False
        
    def handle_starttag(self, tag, attrs):
        if tag=="a" and self.in_drug_list:
            self.drug_count += 1
            self.drug_ids.append(attrs[2][1])
            self.get_names= True
            
    def handle_endtag(self, tag):
        if tag == "td" and self.in_drug_list:
            self.in_drug_list = False
            self.get_names = False
            # self.check_wheter_correct_drug_or_not()

    def check_wheter_correct_drug_or_not(self, name):
        if self.drug_count == 0:
            print("No Match Drugs")
            return "No Match Drugs"
        elif self.drug_count > 1:
            print("There are many drugs with simular name")
            print("Which do you want?")
            for index, i in enumerate(self.drug_names):
                print(i.replace("\n",""))
                if i == name:
                    return get_side_effect(self.drug_ids[index])
            return self.drug_names
        else:
            print("Get Side Effects")
            return get_side_effect(self.drug_ids[0])
                
            
    def handle_data(self, data):
        if data.strip("\n") == "Drugs:":
            self.in_drug_list = True
        
        if self.get_names:
            if len(data.strip()) != 0:
                self.drug_names.append(data)
            
class __HTMLParser_GetSideEffects(HTMLParser):
    def __init__(self):
        super().__init__()
        self.table_finder = False
        self.table_no = 0
        self.side_effects = {}
        self.in_body = False
        self.body_no = 0
        self.body_depth = 1
        self.temp = ""
        self.side_effect_check = False
        self.get_data = False
    
    def handle_starttag(self, tag, attrs):
        if tag=="div":
            if attrs[0] == ('id', 'drugInfoTable'):
                self.table_finder = True
                self.table_no += 1
        
        if tag=="tr" and self.table_finder and len(attrs) == 1:
            if attrs[0] in [('class', 'bg1'), ('class', 'bg2')] and self.table_no == 1:
                self.in_body = True
                self.body_no += 1
        if tag=="td" and self.in_body:
            if self.body_no == 1:
                self.body_depth += 1
                    
            elif self.body_no == 2:
                self.get_data = True
                self.body_no = 0

        if tag=="a" and self.body_depth == 2:
            self.body_depth = 1
            self.side_effect_check = True
            self.body_no += 1
            
    def handle_endtag(self, tag):
        if tag=="table":
            self.table_finder = False
            
    def handle_data(self, data):
        if self.side_effect_check:
            self.side_effect_check = False
            self.temp = data.strip("\n")
        
        if self.get_data:
            self.get_data = False
            self.side_effects[self.temp] = data.strip('\n')
    
    def get_side_effects(self):
        side_effects = copy.deepcopy(self.side_effects)
        for key, value in self.side_effects.items():
            if value == "":
                del(side_effects[key])
        return side_effects

def get_id(name):
    url = "http://sideeffects.embl.de/searchBox/?q=" + name
    response = requests.get(url)
    
    return __parse_html_get_ids(response.content.decode('ascii'), name)
    
def get_side_effect(id_url):
    url = "http://sideeffects.embl.de/" + id_url
    response = requests.get(url)
    
    return __parse_html_get_side_effects(response.content.decode('ascii'))

def __parse_html_get_side_effects(html):
    parser = __HTMLParser_GetSideEffects()
    parser.feed(html)
    return parser.get_side_effects()
    
def __parse_html_get_ids(html:str, name):
    parser = __HTMLParser_GetIDS()
    parser.feed(html)
    
    return parser.check_wheter_correct_drug_or_not(name)
    
if __name__ == "__main__":
    name = input()
    while type(get_id(name)) == list:
        name=input()
    