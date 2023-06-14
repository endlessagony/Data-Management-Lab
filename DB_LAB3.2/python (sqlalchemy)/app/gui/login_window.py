import typing
from PyQt5 import QtCore, QtWidgets
from PyQt5.QtCore import *
from PyQt5.QtGui import *
from PyQt5.QtWidgets import *
import sys
from PyQt5.QtWidgets import QWidget
from  utils.utils import CSV_FILEPATH, User
from app.gui.register_window import registerWindow
from app.gui.main_window import mainWindow

WINDOW_HEIGHT = 720
WINDOW_WIDTH = 1280
STYLE_DIR = 'app/src'

class loginWindow(QMainWindow):
    def __init__(self, *args, **kwargs) -> None:
        super().__init__(*args, **kwargs)
        self.window_height = WINDOW_HEIGHT
        self.window_width = WINDOW_WIDTH

        self.setStyleSheet("""
            QLabel{
                position: absolute;
                color: #fff;
                text-decoration: none;
                border-radius: 20px;
                font-weight: 500;
                margin-left: 40px;
            }

            QPushButton {
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

            QPushButton::hover {
                    background: #fff;
                    color: #162938;
            }

            QGroupBox {
                    background: transparent;
                    border: 2px solid rgba(255, 255, 255, .5);
                    border-radius: 20px;
            }

            QLineEdit {
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
        """)

        self.setup_ui()
        self.register_window = None
        self.main_window = None

    def setup_ui(self):
        self.resize(self.window_width, self.window_height)
        self.setWindowTitle("PySQL")

        oImage = QImage(STYLE_DIR+'\\background.jpg')
        sImage = oImage.scaled(QSize(1280, 720))        
        palette = QPalette()
        palette.setBrush(QPalette.Window, QBrush(sImage))                        
        self.setPalette(palette)

        id = QFontDatabase.addApplicationFont(STYLE_DIR+"\\Poppins-SemiBold.ttf")
        if id < 0: print("Error")
        self.families = QFontDatabase.applicationFontFamilies(id)

        self.logo_label = QLabel('PySQL', self)
        self.logo_label.setFont(QFont(self.families[0], 30))
        self.logo_label.move(self.window_width-1250, 40)
        self.logo_label.resize(400, 60)

        self.setWindowFlags(QtCore.Qt.FramelessWindowHint)

        self.username_label = QLabel('username', self)
        self.username_label.setFont(QFont(self.families[0], 20))
        self.username_label.move(self.window_width-1100, 250)
        self.username_label.resize(400, 60)

        self.username_input = QLineEdit(self)
        self.username_input.setFont(QFont(self.families[0], 20))
        self.username_input.resize(QSize(700, 50))
        self.username_input.move(self.window_width-900, 250)

        self.password_label = QLabel('password', self)
        self.password_label.setFont(QFont(self.families[0], 20))
        self.password_label.move(self.window_width-1100, 325)
        self.password_label.resize(400, 60)
        
        self.password_input = QLineEdit(self)
        self.password_input.setEchoMode(QLineEdit.Password)
        self.password_input.setFont(QFont(self.families[0], 20))
        self.password_input.resize(QSize(700, 50))
        self.password_input.move(self.window_width-900, 325)

        self.quit_btn = QPushButton('Quit', self)
        self.quit_btn.setFont(QFont(self.families[0], 12))
        self.quit_btn.move(self.window_width-495, 400)
        self.quit_btn.clicked.connect(self.close)
        self.quit_btn.resize(295, 50)

        self.login_btn = QPushButton('Login', self)
        self.login_btn.setFont(QFont(self.families[0], 12))
        self.login_btn.move(self.window_width-1100, 400)
        self.login_btn.clicked.connect(self.sign_in)
        self.login_btn.resize(295, 50)

        self.register_btn = QPushButton('Doesn\'t have an account?', self)
        self.register_btn.setFont(QFont(self.families[0], 12))
        self.register_btn.move(self.window_width-795, 400)
        self.register_btn.clicked.connect(self.sign_up)
        self.register_btn.resize(295, 50)

    def error_animate(self, line):
        if line == 'password':
            animation = QPropertyAnimation(self.password_input, b"geometry")
            animation.setDuration(120)
            animation.setLoopCount(3)
            animation.setEasingCurve(QtCore.QEasingCurve.InOutQuad)

            original_rect = self.password_input.geometry()
            self.password_input.setStyleSheet("border: 2px solid red;")
            animation.setStartValue(QRect(original_rect.x() - 5, original_rect.y(), original_rect.width(), original_rect.height()))
            animation.setEndValue(QRect(original_rect.x(), original_rect.y(), original_rect.width(), original_rect.height()))
            animation.start()
            animation.finished.connect(lambda: animation.stop())
        else:
            animation = QPropertyAnimation(self.username_input, b"geometry")
            animation.setDuration(120)
            animation.setLoopCount(3)
            animation.setEasingCurve(QtCore.QEasingCurve.InOutQuad)

            original_rect = self.username_input.geometry()
            self.username_input.setStyleSheet("border: 2px solid red;")
            animation.setStartValue(QRect(original_rect.x() - 5, original_rect.y(), original_rect.width(), original_rect.height()))
            animation.setEndValue(QRect(original_rect.x(), original_rect.y(), original_rect.width(), original_rect.height()))
            animation.start()
            animation.finished.connect(lambda: animation.stop())


    def sign_in(self):
        input_password = self.password_input.text()
        input_username = self.username_input.text()
        correct_password = ''
        with open(CSV_FILEPATH, 'r') as file:
            lines = file.readlines()[1:]
            for line in lines:
                username, password, _ = line.split(',')
                if username == input_username:
                    if input_password == password:
                        correct_password = password
        
        if input_password == correct_password:
            current_user = User(input_username, input_password)
            if self.main_window is None:
                self.main_window = mainWindow(self, current_user)
            self.main_window.show()
            self.hide()
        elif correct_password != input_password:
            self.error_animate('password')
        elif input_username == '':
            self.error_animate('username')

    def sign_up(self):
        if self.register_window is None:
            self.register_window = registerWindow(self)
        self.register_window.show()
        self.hide()
