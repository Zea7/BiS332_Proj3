import sys
from PyQt5.QtWidgets import *
from PyQt5 import uic

form_class = uic.loadUiType("./UI/main.ui")[0]

class MainWindow(QMainWindow, form_class):
    def __init__(self):
        super().__init__()
        self.setupUi(self)
        self.EnterButton.clicked.connect(self.__get_disease_name)
        self.FindingLabel.hide()
        self.SearchProgressBar.hide()
        self.SearchProgressBar.setValue(0)
        
    def __get_disease_name(self):
        print(self.DiseaseInput.toPlainText())
        self.FindingLabel.show()
        self.SearchProgressBar.show()
    
        
    
            
if __name__ == "__main__":
    app = QApplication(sys.argv)
    
    myWindow = MainWindow()
    
    myWindow.show()
    
    app.exec_()