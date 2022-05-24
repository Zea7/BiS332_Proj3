from src.api.interaction_request import get_interaction
from src.api.side_effect_request import get_id
from src.design.main_window import MainWindow
from src.design.search_drug_window import SearchDrugWindow
from PyQt5.QtWidgets import *
import sys

# names = ["aa-adefovir", "cladribine", "Tenofovir"]
# get_interaction(names)

# name = input()
# while type(get_id(name)) == list:
#     name=input()
# print(get_id(name))
    
app = QApplication(sys.argv)
window = SearchDrugWindow()
window.show()
app.exec_()