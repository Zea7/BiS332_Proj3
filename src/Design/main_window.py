import sys
from PyQt5.QtWidgets import *
from PyQt5 import uic
from PyQt5.QtGui import *

form_class = uic.loadUiType("./UI/main.ui")[0]

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
            self.__analyze()
        elif role == QDialogButtonBox.ResetRole:
            self.__reset_disease_list()
            
    def __reset_disease_list(self):
            self.DiseaseList.clear()
            
    def __analyze(self):
        pass
            
    def __show_help(self):
        title = "Help"
        Text = "추가할 병명에 현재 가지고 계시는 질병의 이름을 입력하고, 옆에 있는 Apply를 누르면 아래 병 리스트에 추가됩니다.\n\n가지고 계시는 모든 질병을 리스트에 추가하신 뒤, "  + \
            "병명 리스트 옆에 있는 Apply를 누르면 취약하신 질병 리스트와 추천 약 리스트, 각 약별 부작용을 볼 수 있는 창을 띄워줍니다."
        QMessageBox.information(self,title, Text)
        
    def __get_disease_name(self):
        disease_name = self.DiseaseEdit.toPlainText()
        print(disease_name)
        item = QListWidgetItem(disease_name)
        self.DiseaseList.addItem(item)
        
    
    
        
    
            
if __name__ == "__main__":
    app = QApplication(sys.argv)
    
    myWindow = MainWindow()
    
    myWindow.show()
    
    app.exec_()