import sys
from PyQt5.QtWidgets import *
from PyQt5 import uic

from ..api.side_effect_request import get_id

form_class = uic.loadUiType("./UI/result_page.ui")[0]


class ResultWindow(QMainWindow, form_class):
    def __init__(self, drug_list = [], disease_list=[], related_disease_list=[], main = None):
        super().__init__()
        self.setupUi(self)
        self.drug_list = drug_list

        self.disease_list = disease_list
        self.related_disease_list = related_disease_list
        self.MainWindow = main
    
        self.drug_name = ""
        self.__init_ui()
        
    def __init_ui(self):
        for name in self.drug_list:
            self.DrugList.addItem(QListWidgetItem(name, None, 0))
        for name in self.disease_list:
            self.DiseaseList.addItem(QListWidgetItem(name))
        for name in self.related_disease_list:
            self.RelatedDiseaseList.addItem(QListWidgetItem(name))
        
        self.DrugList.itemClicked.connect(self.__get_side_effects)   
        
    def __get_side_effects(self):
        if self.drug_name == self.DrugList.currentItem().text():
            return
        else:
            self.drug_name = self.DrugList.currentItem().text()
            
        self.SideEffectList.clear()
        
        sideeffects = get_id(self.drug_name)
        if type(sideeffects) == str:
            self.SideEffectList.addItem("Could not find same drug")
            
        else:
            if len(sideeffects) < 1:
                self.SideEffectList.addItem("No Side Effects Found!")
                return 
            for name, probability in sideeffects.items():
                text = name.strip() + " : " + probability
                self.SideEffectList.addItem(text)