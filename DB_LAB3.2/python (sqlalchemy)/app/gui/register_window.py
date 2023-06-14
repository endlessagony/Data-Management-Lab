from .login_window import *
from  utils.utils import CSV_FILEPATH

WINDOW_HEIGHT = 720
WINDOW_WIDTH = 1280
STYLE_DIR = 'app/src'

class registerWindow(QWidget):
    def __init__(self, parent: QMainWindow, *args, **kwargs) -> None:
        super().__init__(*args, **kwargs)
        self.window_height = WINDOW_HEIGHT
        self.window_width = WINDOW_WIDTH
        self.parent = parent

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
        """)

        self.setup_ui()

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

        self.setWindowFlags(QtCore.Qt.FramelessWindowHint)

        self.username_label = QLabel('username', self)
        self.username_label.setFont(QFont(self.families[0], 20))
        self.username_label.move(self.window_width-1100, 250)

        self.username_input = QLineEdit(self)
        self.username_input.setFont(QFont(self.families[0], 20))
        self.username_input.resize(QSize(700, 50))
        self.username_input.move(self.window_width-900, 250)

        self.password_label = QLabel('password', self)
        self.password_label.setFont(QFont(self.families[0], 20))
        self.password_label.move(self.window_width-1100, 325)
        
        self.password_input = QLineEdit(self)
        self.password_input.setEchoMode(QLineEdit.Password)
        self.password_input.setFont(QFont(self.families[0], 20))
        self.password_input.resize(QSize(700, 50))
        self.password_input.move(self.window_width-900, 325)

        self.password2_label = QLabel('confirm', self)
        self.password2_label.setFont(QFont(self.families[0], 20))
        self.password2_label.move(self.window_width-1100, 400)
        
        self.password2_input = QLineEdit(self)
        self.password2_input.setEchoMode(QLineEdit.Password)
        self.password2_input.setFont(QFont(self.families[0], 20))
        self.password2_input.resize(QSize(700, 50))
        self.password2_input.move(self.window_width-900, 400)

        self.permission_label = QLabel('permission', self)
        self.permission_label.setFont(QFont(self.families[0], 20))
        self.permission_label.move(self.window_width-1100, 475)

        self.permission_input = QComboBox(self)
        self.permission_input.addItems(['admin', 'guest'])
        self.permission_input.setFont(QFont(self.families[0], 20))
        self.permission_input.resize(QSize(700, 50))
        self.permission_input.move(self.window_width-900, 475)
        
        self.quit_btn = QPushButton('Quit', self)
        self.quit_btn.setFont(QFont(self.families[0], 12))
        self.quit_btn.move(self.window_width-495, 550)
        self.quit_btn.clicked.connect(self.close)

        self.login_btn = QPushButton('Register', self)
        self.login_btn.setFont(QFont(self.families[0], 12))
        self.login_btn.move(self.window_width-1100, 550)
        self.login_btn.clicked.connect(self.sign_up)

        self.register_btn = QPushButton('Already have an account?', self)
        self.register_btn.setFont(QFont(self.families[0], 12))
        self.register_btn.move(self.window_width-795, 550)
        self.register_btn.clicked.connect(self.sign_in)

    def error_animate(self, line):
        if line == 'password1':
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
            animation = QPropertyAnimation(self.password2_input, b"geometry")
            animation.setDuration(120)
            animation.setLoopCount(3)
            animation.setEasingCurve(QtCore.QEasingCurve.InOutQuad)

            original_rect = self.password2_input.geometry()
            self.password2_input.setStyleSheet("border: 2px solid red;")
            animation.setStartValue(QRect(original_rect.x() - 5, original_rect.y(), original_rect.width(), original_rect.height()))
            animation.setEndValue(QRect(original_rect.x(), original_rect.y(), original_rect.width(), original_rect.height()))
            animation.start()
            animation.finished.connect(lambda: animation.stop())

        del original_rect
    
    def sign_up(self):
        username = self.username_input.text()
        password1 = self.password_input.text()
        password2 = self.password2_input.text()
        permission = self.permission_input.currentText()
        if password1 != password2:
            self.error_animate('password1')
            self.error_animate('password2')
        else:
            with open(CSV_FILEPATH, 'a+') as file:
                file.write(f'{username},{password1},{permission}\n')
            self.hide()
            self.parent.show()

    def sign_in(self):
        self.hide()
        self.parent.show()

