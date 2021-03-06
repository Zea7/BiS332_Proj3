from dis import dis
import sys
from PyQt5.QtWidgets import *
from PyQt5.QtCore import *
from PyQt5 import uic
from PyQt5.QtGui import *

from ..database.find_drugs import DrugFinder

from .result_window import ResultWindow

form_class = uic.loadUiType("./UI/main.ui")[0]
form_class_loading = uic.loadUiType("./UI/loading.ui")[0]

class MainWindow(QMainWindow, form_class):
    def __init__(self):
        super().__init__()
        self.__init_design()
        
    def __init_design(self):
        self.setupUi(self)
        self.CheckBox.clicked.connect(self.__handdleButtonClickEdit)
        self.AnalyzeBox.clicked.connect(self.__handdleButtonClickList)
        self.DiseaseList.itemClicked.connect(self.__delete_item)
        
        
    def __delete_item(self, item):
        print("clicked")
        print(item)
        self.DiseaseList.takeItem(self.DiseaseList.row(item))
                
    def __handdleButtonClickEdit(self, button):
        role = self.CheckBox.buttonRole(button)
        if role == QDialogButtonBox.ApplyRole:
            self.__get_disease_name()
        elif role == QDialogButtonBox.HelpRole:
            self.__show_help()
            
    def __handdleButtonClickList(self, button):
        role = self.AnalyzeBox.buttonRole(button)
        if role == QDialogButtonBox.ApplyRole:
            self.Loading = Loading(self)
            self.__analyze()
        elif role == QDialogButtonBox.ResetRole:
            self.__reset_disease_list()
            
    def __reset_disease_list(self):
        self.DiseaseList.clear()
            
    def __analyze(self):
        disease_list = []
        for i in range(self.DiseaseList.count()):
            disease_list.append(self.DiseaseList.item(i).text())
        conn = DrugFinder()
        
        drug_list, related_disease = conn.get_drug_list(disease_list)
        
        del_list = []
        for name in related_disease:
            if name in disease_list:
                del_list.append(name)
        for i in del_list:
            related_disease.remove(i)
        print(related_disease)
        print(disease_list)
        self.result = ResultWindow(drug_list=drug_list, disease_list=disease_list, related_disease_list=related_disease, main = self)
        self.result.show()
        self.hide()
        
        

            
    def __show_help(self):
        title = "Help"
        Text = "????????? ????????? ?????? ????????? ????????? ????????? ????????? ????????????, ?????? ?????? Apply??? ????????? ?????? ??? ???????????? ???????????????.\n\n????????? ????????? ?????? ????????? ???????????? ???????????? ???, "  + \
            "?????? ????????? ?????? ?????? Apply??? ????????? ???????????? ?????? ???????????? ?????? ??? ?????????, ??? ?????? ???????????? ??? ??? ?????? ?????? ???????????????."
        QMessageBox.information(self,title, Text)
        
    def __get_disease_name(self):
        disease_name = self.DiseaseEdit.toPlainText()
        print(disease_name)
        item = QListWidgetItem(disease_name)
        self.DiseaseList.addItem(item)
        self.DiseaseEdit.clear()
        
        
class Loading(QWidget,form_class_loading):
    def __init__(self, parent):
        super(Loading, self).__init__(parent)
        self.setupUi(self)
        self.center()
        self.show()
        self.movie = QMovie('./UI/loading.gif', QByteArray(), self)
        self.movie.setCacheMode(QMovie.CacheAll)
        self.label.setMovie(self.movie)
        self.movie.start()
        
        self.setWindowFlag(Qt.FramelessWindowHint)
        
        
        
    def center(self):
        size = self.size()
        ph = self.parent().geometry().height()
        pw = self.parent().geometry().width()
        self.move(int(pw/2 - size.width()/2), int(ph/2 - size.height()/2))
        
            
if __name__ == "__main__":
    app = QApplication(sys.argv)
    
    myWindow = MainWindow()
    
    myWindow.show()
    
    app.exec_()