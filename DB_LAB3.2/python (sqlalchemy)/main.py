from PyQt5 import QtCore, QtWidgets
from PyQt5.QtCore import *
from PyQt5.QtGui import *
from PyQt5.QtWidgets import *
import sys
from app.gui.login_window import loginWindow
from  utils.utils import create_users_data

if __name__ == "__main__":
    create_users_data()
    app = QtWidgets.QApplication(sys.argv)
    login_form = loginWindow()
    login_form.show()
    sys.exit(app.exec_())