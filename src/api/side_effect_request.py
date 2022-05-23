import copy
from html.parser import HTMLParser
import requests

class __HTMLParser_GetIDS(HTMLParser):
    def handle_starttag(self, tag, attrs):
        if tag=="a":
            get_side_effect(attrs[2][1])
            
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
    
    __parse_html_get_ids(response.content.decode('ascii'))
    
def get_side_effect(id_url):
    url = "http://sideeffects.embl.de/" + id_url
    response = requests.get(url)
    
    __parse_html_get_side_effects(response.content.decode('ascii'))

def __parse_html_get_side_effects(html):
    parser = __HTMLParser_GetSideEffects()
    parser.feed(html)
    print(parser.get_side_effects())
    
def __parse_html_get_ids(html:str):
    parser = __HTMLParser_GetIDS()
    parser.feed(html)
    
if __name__ == "__main__":
    name = input()
    get_id(name)
    