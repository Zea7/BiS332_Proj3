import sys
from PyQt5.QtWidgets import *
from PyQt5 import uic
from ..api.side_effect_request import get_id

form_class = uic.loadUiType("./UI/search_drug.ui")[0]


class SearchDrugWindow(QMainWindow, form_class):
    def __init__(self):
        super().__init__()
        self.setupUi(self)
        self.SearchButton.clicked.connect(self.__search_for_drug)
    
    def __search_for_drug(self):
        name = self.DrugName.toPlainText()
        get_id(name)