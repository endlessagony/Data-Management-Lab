import os
from pathlib import Path
import typing
from PyQt5 import QtCore, QtWidgets
from PyQt5.QtCore import *
from PyQt5.QtGui import *
from PyQt5.QtWidgets import *
import sys
from PyQt5.QtWidgets import QWidget
import csv

STYLE_DIR = 'app/src'
PARENT_DIR = Path(".")
CSV_DIR = f'{PARENT_DIR}/users_data'
CSV_FILEPATH = f'{CSV_DIR}/users_data.csv'

def create_users_data():
    if os.path.exists(CSV_DIR) == False:
        os.mkdir(CSV_DIR)
        with open(CSV_FILEPATH, 'w') as file:
            file.write('username,password,permission\n')

def change_permission(current_username, new_permission):
    with open(CSV_FILEPATH, 'r') as file:
        reader = csv.DictReader(file)
        data = list(reader)
        for row in data:
            if row['username'] == current_username:
                row['permission'] = new_permission
                break

        # Write the modified data back to the CSV file
    with open(CSV_FILEPATH, 'w', newline='') as file:
        writer = csv.DictWriter(file, fieldnames=['username', 'password', 'permission'])
        writer.writeheader()
        writer.writerows(data)

class CreateUserDialog(QWidget):
    create_signal = QtCore.pyqtSignal(list)
    def __init__(self, parent: QMainWindow, *args, **kwargs):
        super().__init__(*args, **kwargs)
        self.window_height = parent.window_height//2
        self.window_width = parent.window_width//2
        self.parent = parent
        self.usernames, self.passwords = get_registered_users_info()

        self.setWindowTitle('ERROR')
        
        self.setup_ui()

    def setup_ui(self):
        self.resize(self.window_width, self.window_height)
        self.setWindowFlags(QtCore.Qt.FramelessWindowHint)

        self.setStyleSheet("""
            QLabel{
                position: absolute;
                color: #fff;
                text-decoration: none;
                border-radius: 20px;
                font-weight: 500;
                margin-left: 40px;
            }

            QComboBox {
                padding-top: 3px;
                padding-left: 4px;
                width: 250px;
                height: 50px;
                background: transparent;
                border: 2px solid #fff;
                outline: none;
                border-radius: 6px;
                color: #fff;
                font-weight: 500;
                margin-left: 40px;
            }

            QComboBox:on { /* shift the text when the popup opens */
                padding-top: 3px;
                padding-left: 4px;
            }

            QLineEdit {
                    padding-top: 3px;
                    padding-left: 4px;
                    width: 250px;
                    height: 50px;
                    background: transparent;
                    border: 2px solid #fff;
                    outline: none;
                    border-radius: 6px;
                    color: #fff;
                    font-weight: 500;
                    margin-left: 40px;
            }

            QPushButton {
                    width: 175px;
                    height: 50px;
                    background: transparent;
                    border: 2px solid #fff;
                    outline: none;
                    border-radius: 6px;
                    color: #fff;
                    margin-left: 40px;
            }

            QPushButton::hover {
                    background: #fff;
                    color: #162938;
            }
        """)

        oImage = QImage(STYLE_DIR+'\\background.jpg')
        sImage = oImage.scaled(QSize(self.window_width, self.window_height))        
        palette = QPalette()
        palette.setBrush(QPalette.Window, QBrush(sImage))                        
        self.setPalette(palette)

        id = QFontDatabase.addApplicationFont(STYLE_DIR+"\\Poppins-SemiBold.ttf")
        if id < 0: print("Error")
        self.families = QFontDatabase.applicationFontFamilies(id)

        self.username_label = QLabel('username', self)
        self.username_label.setFont(QFont(self.families[0], 20))
        self.username_label.move(0, 40)

        self.username_combobox = QComboBox(self)
        self.username_combobox.setFont(QFont(self.families[0], 20))
        self.username_combobox.addItems(self.usernames)
        self.username_combobox.move(200, 40)
        self.username_combobox.resize(QSize(400, 50))
        self.username_combobox.currentTextChanged.connect(self.autofill_password)

        self.password_label = QLabel('password', self)
        self.password_label.setFont(QFont(self.families[0], 20))
        self.password_label.move(0, 115)

        self.password_input = QLineEdit(self)
        self.password_input.setEchoMode(QLineEdit.Password)
        self.password_input.setFont(QFont(self.families[0], 20))
        self.password_input.move(200, 115)
        self.password_input.resize(QSize(400, 50))
        self.autofill_password(self.username_combobox.currentText())

        self.permission_label = QLabel('permission', self)
        self.permission_label.setFont(QFont(self.families[0], 20))
        self.permission_label.move(0, 190)

        self.permission_combobox = QComboBox(self)
        self.permission_combobox.setFont(QFont(self.families[0], 20))
        self.permission_combobox.addItems(['guest', 'admin'])
        self.permission_combobox.move(200, 190)
        self.permission_combobox.resize(QSize(400, 50))

        self.quit_btn = QPushButton('Quit', self)
        self.quit_btn.setFont(QFont(self.families[0], 12))
        self.quit_btn.move(200, 265)
        self.quit_btn.clicked.connect(self.close)

        self.submit_btn = QPushButton('Submit', self)
        self.submit_btn.setFont(QFont(self.families[0], 12))
        self.submit_btn.move(0, 265)
        self.submit_btn.clicked.connect(self.get_values)

    def autofill_password(self, username):
        if username not in self.usernames:
            self.msg = MessageDialog(self.parent, 'THERE IS NO REGISTERED USERS')
            self.msg.show()
            self.close()
        else:
            password = self.passwords[self.usernames.index(username)]
            self.password_input.setText(password)
            self.password_input.setReadOnly(True)
            self.password_input.setStyleSheet( """
            QLineEdit {
                padding-top: 3px;
                padding-left: 4px;
                width: 250px;
                height: 50px;
                background: transparent;
                border: 2px solid #CCCCCC;
                outline: none;
                border-radius: 6px;
                color: #CCCCCC;
                font-weight: 500;
                margin-left: 40px;
            }""")

    def get_values(self):
        username = self.username_combobox.currentText()
        password = self.password_input.text()
        permission = self.permission_combobox.currentText()

        change_permission(username, permission)

        self.create_signal.emit([username, password, permission])
        self.close()

class MessageDialog(QWidget):
    def __init__(self, parent: QMainWindow, message: str, *args, **kwargs):
        super().__init__(*args, **kwargs)
        self.window_height = parent.window_height//4
        self.window_width = len(message) * 20
        self.parent = parent
        if 'fatal:' in message.lower():
            message = message.lower().split('fatal:')[-1].strip()
        if "\"" in message:
            first_part, mid_part, second_part = message.split("\"")[0].upper().strip(), message.split("\"")[1].strip(), message.split("\"")[-1].upper().strip()
            self.message = first_part + " \"" + mid_part + "\" " + second_part
        else:
            self.message = message

        self.setWindowTitle('ERROR')
        
        self.setup_ui()

    def setup_ui(self):
        self.resize(self.window_width, self.window_height)
        self.setWindowFlags(QtCore.Qt.FramelessWindowHint)

        self.setStyleSheet("""
            QLabel{
                position: absolute;
                color: #fff;
                text-decoration: none;
                border-radius: 20px;
                font-weight: 500;
                margin-left: 40px;
            }

            QComboBox {
                padding-top: 3px;
                padding-left: 4px;
                width: 250px;
                height: 50px;
                background: transparent;
                border: 2px solid #fff;
                outline: none;
                border-radius: 6px;
                color: #fff;
                font-weight: 500;
                margin-left: 40px;
            }

            QComboBox:on { /* shift the text when the popup opens */
                padding-top: 3px;
                padding-left: 4px;
            }

            QLineEdit {
                    padding-top: 3px;
                    padding-left: 4px;
                    width: 250px;
                    height: 50px;
                    background: transparent;
                    border: 2px solid #fff;
                    outline: none;
                    border-radius: 6px;
                    color: #fff;
                    font-weight: 500;
                    margin-left: 40px;
            }

            QPushButton {
                    width: 175px;
                    height: 50px;
                    background: transparent;
                    border: 2px solid #fff;
                    outline: none;
                    border-radius: 6px;
                    color: #fff;
                    margin-left: 40px;
            }

            QPushButton::hover {
                    background: #fff;
                    color: #162938;
            }
        """)

        oImage = QImage(STYLE_DIR+'\\background.jpg')
        sImage = oImage.scaled(QSize(self.window_width, self.window_height))        
        palette = QPalette()
        palette.setBrush(QPalette.Window, QBrush(sImage))                        
        self.setPalette(palette)

        id = QFontDatabase.addApplicationFont(STYLE_DIR+"\\Poppins-SemiBold.ttf")
        if id < 0: print("Error")
        self.families = QFontDatabase.applicationFontFamilies(id)

        self.feature_label = QLabel(self.message, self)
        self.feature_label.setFont(QFont(self.families[0], 20))
        self.feature_label.move(self.window_width-self.window_width, 40)

        self.ok_btn = QPushButton('OK', self)
        self.ok_btn.setFont(QFont(self.families[0], 12))
        self.ok_btn.move(self.window_width-self.window_width, 115)
        self.ok_btn.clicked.connect(self.close)

class FindDialog(QWidget):
    find_signal = QtCore.pyqtSignal(list)

    def __init__(self, parent: QMainWindow, *args, **kwargs) -> None:
        super().__init__(*args, **kwargs)
        self.column_name = None
        self.column_value = None
        self.window_height = parent.window_height//3
        self.window_width = parent.window_width//3
        self.parent = parent
        
        self.setup_ui()
    
    def setup_ui(self):
        self.resize(self.window_width, self.window_height)
        self.move(1150, 180)
        self.setWindowFlags(QtCore.Qt.FramelessWindowHint)

        self.setStyleSheet("""
            QLabel{
                position: absolute;
                color: #fff;
                text-decoration: none;
                border-radius: 20px;
                font-weight: 500;
                margin-left: 40px;
            }

            QComboBox {
                padding-top: 3px;
                padding-left: 4px;
                width: 250px;
                height: 50px;
                background: transparent;
                border: 2px solid #fff;
                outline: none;
                border-radius: 6px;
                color: #fff;
                font-weight: 500;
                margin-left: 40px;
            }

            QComboBox:on { /* shift the text when the popup opens */
                padding-top: 3px;
                padding-left: 4px;
            }

            QLineEdit {
                    padding-top: 3px;
                    padding-left: 4px;
                    width: 250px;
                    height: 50px;
                    background: transparent;
                    border: 2px solid #fff;
                    outline: none;
                    border-radius: 6px;
                    color: #fff;
                    font-weight: 500;
                    margin-left: 40px;
            }

            QPushButton {
                    width: 175px;
                    height: 50px;
                    background: transparent;
                    border: 2px solid #fff;
                    outline: none;
                    border-radius: 6px;
                    color: #fff;
                    margin-left: 40px;
            }

            QPushButton::hover {
                    background: #fff;
                    color: #162938;
            }
        """)

        oImage = QImage(STYLE_DIR+'\\background.jpg')
        sImage = oImage.scaled(QSize(self.window_width, self.window_height))        
        palette = QPalette()
        palette.setBrush(QPalette.Window, QBrush(sImage))                        
        self.setPalette(palette)

        id = QFontDatabase.addApplicationFont(STYLE_DIR+"\\Poppins-SemiBold.ttf")
        if id < 0: print("Error")
        self.families = QFontDatabase.applicationFontFamilies(id)

        self.feature_label = QLabel('feature', self)
        self.feature_label.setFont(QFont(self.families[0], 15))
        self.feature_label.move(self.window_width-450, 20)

        self.feature_input = QComboBox(self)
        self.feature_input.addItems(self.parent.columns)
        self.feature_input.setFont(QFont(self.families[0], 15))
        self.feature_input.move(self.window_width-300, 20)
        self.feature_input.resize(QSize(275, 50))

        self.value_label = QLabel('value', self)
        self.value_label.setFont(QFont(self.families[0], 15))
        self.value_label.move(self.window_width-450, 95)

        self.value_input = QLineEdit(self)
        self.value_input.resize(QSize(275, 50))
        self.value_input.setFont(QFont(self.families[0], 15))
        self.value_input.move(self.window_width-300, 95)

        self.quit_btn = QPushButton('Quit', self)
        self.quit_btn.setFont(QFont(self.families[0], 12))
        self.quit_btn.move(self.window_width-225, 170)
        self.quit_btn.clicked.connect(self.close)

        self.submit_btn = QPushButton('Submit', self)
        self.submit_btn.setFont(QFont(self.families[0], 12))
        self.submit_btn.move(self.window_width-450, 170)
        self.submit_btn.clicked.connect(self.get_values)

    def get_values(self):
        self.column_name = self.feature_input.currentText()
        self.column_value = self.value_input.text()

        self.find_signal.emit([self.column_name, self.column_value])
        self.close()

class EditDialog(QWidget):
    edit_signal = QtCore.pyqtSignal(dict)

    def __init__(self, parent: QMainWindow, column_values: list, *args, **kwargs):
        super().__init__(*args, **kwargs)
        self.column_name = None
        self.column_value = None
        self.columns = parent.columns
        self.window_height = 700
        self.window_width = 1000
        self.parent = parent
        self.column_values = column_values
        self.input_values = []

        self.setup_ui()
    
    def setup_ui(self):
        self.resize(self.window_width, self.window_height)
        self.move(150, 180)
        # self.setWindowFlags(QtCore.Qt.FramelessWindowHint)

        self.setStyleSheet("""
            QLabel{
                position: absolute;
                color: #fff;
                text-decoration: none;
                border-radius: 20px;
                font-weight: 500;
                margin-left: 40px;
            }

            QComboBox {
                padding-top: 3px;
                padding-left: 4px;
                width: 250px;
                height: 50px;
                background: transparent;
                border: 2px solid #fff;
                outline: none;
                border-radius: 6px;
                color: #fff;
                font-weight: 500;
                margin-left: 40px;
            }

            QComboBox:on { /* shift the text when the popup opens */
                padding-top: 3px;
                padding-left: 4px;
            }

            QLineEdit {
                    padding-top: 3px;
                    padding-left: 4px;
                    width: 250px;
                    height: 50px;
                    background: transparent;
                    border: 2px solid #fff;
                    outline: none;
                    border-radius: 6px;
                    color: #fff;
                    font-weight: 500;
                    margin-left: 40px;
            }

            QPushButton {
                    width: 175px;
                    height: 50px;
                    background: transparent;
                    border: 2px solid #fff;
                    outline: none;
                    border-radius: 6px;
                    color: #fff;
                    margin-left: 40px;
            }

            QPushButton::hover {
                    background: #fff;
                    color: #162938;
            }
        """)

        oImage = QImage(STYLE_DIR+'\\background.jpg')
        sImage = oImage.scaled(QSize(self.window_width, self.window_height))        
        palette = QPalette()
        palette.setBrush(QPalette.Window, QBrush(sImage))                        
        self.setPalette(palette)

        id = QFontDatabase.addApplicationFont(STYLE_DIR+"\\Poppins-SemiBold.ttf")
        if id < 0: print("Error")
        self.families = QFontDatabase.applicationFontFamilies(id)

        addition = 75
        counter = 0
        for i, (column, column_value) in enumerate(zip(self.columns, self.column_values)):
            if i % 2 == 0 or i == 0:
                right_addition = 0
                if i != 0:
                    height_move = 20+(i-counter)*addition
                else:
                    height_move = 20+i*addition
            else:
                right_addition = 500
                counter += 1
            feature_label = QLabel(column, self)
            feature_label.setFont(QFont(self.families[0], 15))
            feature_label.move(self.window_width-1000+right_addition, height_move)

            feature_input = QLineEdit(self)
            feature_input.setFont(QFont(self.families[0], 15))
            feature_input.move(self.window_width-800+right_addition, height_move)
            feature_input.resize(QSize(275, 50))
            feature_input.setText(str(column_value))
            self.input_values.append(feature_input)
            if i == 0:
                feature_input.setReadOnly(True)

        self.quit_btn = QPushButton('Quit', self)
        self.quit_btn.setFont(QFont(self.families[0], 12))
        self.quit_btn.move(self.window_width-775, height_move+addition)
        self.quit_btn.clicked.connect(self.close)

        self.submit_btn = QPushButton('Submit', self)
        self.submit_btn.setFont(QFont(self.families[0], 12))
        self.submit_btn.move(self.window_width-1000, height_move+addition)
        self.submit_btn.clicked.connect(self.get_values)

    def get_values(self):
        values = {}
        for i, attr in enumerate(self.columns):
            if self.input_values[i].text() == '':
                values[attr] = 'null'
            else:
                values[attr] = self.input_values[i].text()

        self.edit_signal.emit(values)
        self.close()

class DeleteUserDialog(QWidget):
    delete_signal = QtCore.pyqtSignal(str)

    def __init__(self, parent: QMainWindow, users: list, *args, **kwargs):
        super().__init__(*args, **kwargs)
        self.parent = parent
        self.users = users
        self.window_height = parent.window_height//3
        self.window_width = parent.window_width//2

        self.setup_ui()

    def setup_ui(self):
        self.resize(self.window_width, self.window_height)
        self.setWindowFlags(QtCore.Qt.FramelessWindowHint)

        self.setStyleSheet("""
            QLabel{
                position: absolute;
                color: #fff;
                text-decoration: none;
                border-radius: 20px;
                font-weight: 500;
                margin-left: 40px;
            }

            QComboBox {
                padding-top: 3px;
                padding-left: 4px;
                width: 250px;
                height: 50px;
                background: transparent;
                border: 2px solid #fff;
                outline: none;
                border-radius: 6px;
                color: #fff;
                font-weight: 500;
                margin-left: 40px;
            }

            QComboBox:on { /* shift the text when the popup opens */
                padding-top: 3px;
                padding-left: 4px;
            }

            QLineEdit {
                    padding-top: 3px;
                    padding-left: 4px;
                    width: 250px;
                    height: 50px;
                    background: transparent;
                    border: 2px solid #fff;
                    outline: none;
                    border-radius: 6px;
                    color: #fff;
                    font-weight: 500;
                    margin-left: 40px;
            }

            QPushButton {
                    width: 175px;
                    height: 50px;
                    background: transparent;
                    border: 2px solid #fff;
                    outline: none;
                    border-radius: 6px;
                    color: #fff;
                    margin-left: 40px;
            }

            QPushButton::hover {
                    background: #fff;
                    color: #162938;
            }
        """)

        oImage = QImage(STYLE_DIR+'\\background.jpg')
        sImage = oImage.scaled(QSize(self.window_width, self.window_height))        
        palette = QPalette()
        palette.setBrush(QPalette.Window, QBrush(sImage))                        
        self.setPalette(palette)

        id = QFontDatabase.addApplicationFont(STYLE_DIR+"\\Poppins-SemiBold.ttf")
        if id < 0: print("Error")
        self.families = QFontDatabase.applicationFontFamilies(id)

        self.username_label = QLabel('user', self)
        self.username_label.setFont(QFont(self.families[0], 20))
        self.username_label.move(0, 40)

        self.username_combobox = QComboBox(self)
        self.username_combobox.setFont(QFont(self.families[0], 20))
        self.username_combobox.addItems(self.users)
        self.username_combobox.move(200, 40)
        self.username_combobox.resize(QSize(400, 50))

        self.quit_btn = QPushButton('Quit', self)
        self.quit_btn.setFont(QFont(self.families[0], 12))
        self.quit_btn.move(200, 115)
        self.quit_btn.clicked.connect(self.close)

        self.submit_btn = QPushButton('Submit', self)
        self.submit_btn.setFont(QFont(self.families[0], 12))
        self.submit_btn.move(0, 115)
        self.submit_btn.clicked.connect(self.get_values)

    def get_values(self):
        username = self.username_combobox.currentText()

        self.delete_signal.emit(username)
        self.close()    

class InsertDialog(QWidget):
    insert_signal = QtCore.pyqtSignal(dict)

    def __init__(self, parent: QMainWindow, *args, **kwargs):
        super().__init__(*args, **kwargs)
        self.window_height = 700
        self.window_width = 1000
        self.parent = parent
        self.input_values = []
        self.columns = parent.columns

        self.setup_ui()
    
    def setup_ui(self):
        self.resize(self.window_width, self.window_height)
        self.move(150, 180)
        self.setWindowFlags(QtCore.Qt.FramelessWindowHint)

        self.setStyleSheet("""
            QLabel{
                position: absolute;
                color: #fff;
                text-decoration: none;
                border-radius: 20px;
                font-weight: 500;
                margin-left: 40px;
            }

            QComboBox {
                padding-top: 3px;
                padding-left: 4px;
                width: 250px;
                height: 50px;
                background: transparent;
                border: 2px solid #fff;
                outline: none;
                border-radius: 6px;
                color: #fff;
                font-weight: 500;
                margin-left: 40px;
            }

            QComboBox:on { /* shift the text when the popup opens */
                padding-top: 3px;
                padding-left: 4px;
            }

            QLineEdit {
                    padding-top: 3px;
                    padding-left: 4px;
                    width: 250px;
                    height: 50px;
                    background: transparent;
                    border: 2px solid #fff;
                    outline: none;
                    border-radius: 6px;
                    color: #fff;
                    font-weight: 500;
                    margin-left: 40px;
            }

            QPushButton {
                    width: 175px;
                    height: 50px;
                    background: transparent;
                    border: 2px solid #fff;
                    outline: none;
                    border-radius: 6px;
                    color: #fff;
                    margin-left: 40px;
            }

            QPushButton::hover {
                    background: #fff;
                    color: #162938;
            }
        """)

        oImage = QImage(STYLE_DIR+'\\background.jpg')
        sImage = oImage.scaled(QSize(self.window_width, self.window_height))        
        palette = QPalette()
        palette.setBrush(QPalette.Window, QBrush(sImage))                        
        self.setPalette(palette)

        id = QFontDatabase.addApplicationFont(STYLE_DIR+"\\Poppins-SemiBold.ttf")
        if id < 0: print("Error")
        self.families = QFontDatabase.applicationFontFamilies(id)

        addition = 75
        counter = 0
        for i, column in enumerate(self.columns):
            if i % 2 == 0 or i == 0:
                right_addition = 0
                if i != 0:
                    height_move = 20+(i-counter)*addition
                else:
                    height_move = 20+i*addition
            else:
                right_addition = 500
                counter += 1
            feature_label = QLabel(column, self)
            feature_label.setFont(QFont(self.families[0], 15))
            feature_label.move(self.window_width-1000+right_addition, height_move)

            feature_input = QLineEdit(self)
            feature_input.setFont(QFont(self.families[0], 15))
            feature_input.move(self.window_width-800+right_addition, height_move)
            feature_input.resize(QSize(275, 50))
            self.input_values.append(feature_input)

        self.quit_btn = QPushButton('Quit', self)
        self.quit_btn.setFont(QFont(self.families[0], 12))
        self.quit_btn.move(self.window_width-775, height_move+addition)
        self.quit_btn.clicked.connect(self.close)

        self.submit_btn = QPushButton('Submit', self)
        self.submit_btn.setFont(QFont(self.families[0], 12))
        self.submit_btn.move(self.window_width-1000, height_move+addition)
        self.submit_btn.clicked.connect(self.insert_values)

    def insert_values(self):
        values = {}
        for i, attr in enumerate(self.columns):
            if self.input_values[i].text() == '':
                values[attr] = 'null'
            else:
                values[attr] = self.input_values[i].text()

        self.insert_signal.emit(values)
        self.close()

class OneInputDialog(QWidget):
    input_signal = QtCore.pyqtSignal(str)

    def __init__(self, parent: QMainWindow, label: str, title: str, *args, **kwargs):
        super().__init__(*args, **kwargs)
        self.parent = parent
        self.title = title
        self.label = label
        self.window_height = 200
        self.window_width = len(label) * 45

        self.resize(self.window_width, self.window_height)
        self.setStyleSheet("""
            QLabel{
                position: absolute;
                color: #fff;
                text-decoration: none;
                border-radius: 20px;
                font-weight: 500;
                margin-left: 40px;
            }

            QComboBox {
                padding-top: 3px;
                padding-left: 4px;
                width: 250px;
                height: 50px;
                background: transparent;
                border: 2px solid #fff;
                outline: none;
                border-radius: 6px;
                color: #fff;
                font-weight: 500;
                margin-left: 40px;
            }

            QComboBox:on { /* shift the text when the popup opens */
                padding-top: 3px;
                padding-left: 4px;
            }

            QLineEdit {
                    padding-top: 3px;
                    padding-left: 4px;
                    width: 250px;
                    height: 50px;
                    background: transparent;
                    border: 2px solid #fff;
                    outline: none;
                    border-radius: 6px;
                    color: #fff;
                    font-weight: 500;
                    margin-left: 40px;
            }

            QPushButton {
                    width: 175px;
                    height: 50px;
                    background: transparent;
                    border: 2px solid #fff;
                    outline: none;
                    border-radius: 6px;
                    color: #fff;
                    margin-left: 40px;
            }

            QPushButton::hover {
                    background: #fff;
                    color: #162938;
            }
        """)

        self.setup_ui()

    def setup_ui(self):
        self.setWindowTitle(self.title)

        oImage = QImage(STYLE_DIR+'\\background.jpg')
        sImage = oImage.scaled(QSize(self.window_width, self.window_height))        
        palette = QPalette()
        palette.setBrush(QPalette.Window, QBrush(sImage))                        
        self.setPalette(palette)

        id = QFontDatabase.addApplicationFont(STYLE_DIR+"\\Poppins-SemiBold.ttf")
        if id < 0: print("Error")
        self.families = QFontDatabase.applicationFontFamilies(id)

        self.setWindowFlags(QtCore.Qt.FramelessWindowHint)
        
        self.qlabel = QLabel(self.label, self)
        self.qlabel.setFont(QFont(self.families[0], 20))
        self.qlabel.move(self.window_width-(len(self.label) * 45), 25)

        self.input = QLineEdit(self)
        self.input.setFont(QFont(self.families[0], 20))
        self.input.move(self.window_width-(len(self.label) * 28), 20)
        self.input.resize(QSize((len(self.label) * 25), 50))

        self.quit_btn = QPushButton('Quit', self)
        self.quit_btn.setFont(QFont(self.families[0], 12))
        self.quit_btn.move(self.window_width-(len(self.label) * 45) + 200, 95)
        self.quit_btn.clicked.connect(self.close)

        self.submit_btn = QPushButton('Submit', self)
        self.submit_btn.setFont(QFont(self.families[0], 12))
        self.submit_btn.move(self.window_width-(len(self.label) * 45), 95)
        self.submit_btn.clicked.connect(self.get_values)

    def get_values(self):
        value = self.input.text()
        self.input_signal.emit(value)
        self.close()

def get_registered_users_info():
    usernames = []
    passwords = []
    with open(CSV_FILEPATH, 'r') as file:
        for i, line in enumerate(file.readlines()):
            if i > 1:
                values = line.split(',')
                usernames.append(values[0])
                passwords.append(values[1])
    return usernames, passwords

class User:
    def __init__(self, username, password):
        self.username = username
        self.password = password
        
        with open(CSV_FILEPATH, 'r') as file:
            lines = file.readlines()[1:]
            for line in lines:
                username, password, _ = line.split(',')
                if username == self.username:
                    self.permission = _