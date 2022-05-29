import sys
from PyQt5.QtWidgets import *
from PyQt5 import uic

from ..api.side_effect_request import get_id

form_class = uic.loadUiType("./UI/result_page.ui")[0]

class ResultWindow(QMainWindow, form_class):
    def __init__(self, drug_list = [], disease_list=[]):
        super().__init__()
        self.setupUi(self)
        self.drug_list = drug_list
        self.__init_ui()
        self.disease_list = disease_list
        self.drug_name = ""
        
    def __init_ui(self):
        for name in self.drug_list:
            self.DrugList.addItem(QListWidgetItem(name, None, 0))
        
        self.DrugList.itemClicked.connect(self.__get_side_effects)   
        
    def __get_disease_names(self):
        pass

    def __get_drug_list(self):
        pass
    
    def __get_side_effects(self):
        if self.drug_name == self.DrugList.currentItem().text():
            return
        else:
            self.drug_name = self.DrugList.currentItem().text()
            
        self.SideEffectList.clear()
        if type(self.drug_name) == str:
            self.SideEffectList.addItem(self.drug_name)
            
            
        else:
            for name, probability in get_id(self.drug_name).items():
                text = name.strip() + " : " + probability
                self.SideEffectList.addItem(text)