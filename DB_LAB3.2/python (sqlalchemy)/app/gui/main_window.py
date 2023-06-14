from .login_window import *
from  utils.utils import CSV_FILEPATH, User, MessageDialog, FindDialog, EditDialog, OneInputDialog, InsertDialog, CreateUserDialog, DeleteUserDialog
import psycopg2
from sqlalchemy import create_engine, select, MetaData, Table, and_, text, func
from psycopg2.extras import execute_values
from pathlib import Path

WINDOW_HEIGHT = 720
WINDOW_WIDTH = 1280
STYLE_DIR = 'app/src'
QUIRIES_DIR = 'quieries'

class mainWindow(QWidget):
    def __init__(self, parent: QMainWindow, user: User, *args, **kwargs) -> None:
        super().__init__(*args, **kwargs)
        self.window_height = WINDOW_HEIGHT
        self.window_width = WINDOW_WIDTH
        self.parent = parent
        self.user = user
        self.columns_dtypes = []
        self.engine = None
        self.is_admin = 'admin' in self.user.permission

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

            QPushButton[enabled='false'] {
                    border: 2px solid #ccc;
                    color: #ccc;
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

            QTableWidget {
                padding-top: 3px;
                padding-left: 4px;
                background: transparent;
                border: 2px solid #fff;
                outline: none;
                border-radius: 6px;
                color: #fff;
                font-weight: 500;
                margin-left: 40px;
            }

            QHeaderView {
                background: transparent;
                outline: none;
                color: #fff;
                font-weight: 500;
            }

            QHeaderView::section {
                background: transparent;
                outline: none;
                color: #fff;
                font-weight: 500;
            }

            QTableView QTableCornerButton::section {
                background: transparent;
                border: 2px solid #fff;
                outline: none;
                border-radius: 6px;
                color: #fff;
            }

            QTableView::item {
                background: transparent;
                border: 1px solid #fff;
                outline: none;
                color: #fff;
            }

            QScrollBar:horizontal {
                background: transparent;
                border: 1px solid #fff;
                outline: none;
                border-radius: 6px;
                color: #fff;
            }
            
            QScrollBar:vertical {
                background: transparent;
                border: 1px solid #fff;
                outline: none;
                border-radius: 6px;
                color: #fff;
            }

            QTableWidget::item:selected {
                background: #fff;
                color: #162938;
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

        self.open_btn = QPushButton('Open', self)
        self.open_btn.setFont(QFont(self.families[0], 12))
        self.open_btn.move(self.window_width-350, 100)
        self.open_btn.clicked.connect(lambda: self.open_one_input('open'))

        self.create_btn = QPushButton('Create', self)
        self.create_btn.setFont(QFont(self.families[0], 12))
        self.create_btn.move(self.window_width-350, 175)
        self.create_btn.clicked.connect(lambda: self.open_one_input('create'))
        self.create_btn.setEnabled(self.is_admin)

        self.delete_btn = QPushButton('Delete', self)
        self.delete_btn.setFont(QFont(self.families[0], 12))
        self.delete_btn.move(self.window_width-350, 250)
        self.delete_btn.clicked.connect(lambda: self.open_one_input('delete'))
        self.delete_btn.setEnabled(self.is_admin)

        self.find_btn = QPushButton('Find', self)
        self.find_btn.setFont(QFont(self.families[0], 12))
        self.find_btn.move(self.window_width-350, 400)
        self.find_btn.clicked.connect(self.open_find)

        self.drop_btn = QPushButton('Drop', self)
        self.drop_btn.setFont(QFont(self.families[0], 12))
        self.drop_btn.move(self.window_width-350, 475)
        self.drop_btn.clicked.connect(self.open_drop)
        self.drop_btn.setEnabled(self.is_admin)

        self.edit_btn = QPushButton('Edit', self)
        self.edit_btn.setFont(QFont(self.families[0], 12))
        self.edit_btn.move(self.window_width-350, 550)
        self.edit_btn.clicked.connect(lambda: self.open_one_input('edit'))
        self.edit_btn.setEnabled(self.is_admin)

        self.insert_btn = QPushButton('Insert', self)
        self.insert_btn.setFont(QFont(self.families[0], 12))
        self.insert_btn.move(self.window_width-350, 625)
        self.insert_btn.clicked.connect(self.open_insert)
        self.insert_btn.setEnabled(self.is_admin)

        self.quit_btn = QPushButton('Quit', self)
        self.quit_btn.setFont(QFont(self.families[0], 12))
        self.quit_btn.move(self.window_width-1250, 625)
        self.quit_btn.clicked.connect(self.close)

        self.create_user_btn = QPushButton('Create user', self)
        self.create_user_btn.setFont(QFont(self.families[0], 12))
        self.create_user_btn.move(self.window_width-950, 625)
        self.create_user_btn.clicked.connect(self.open_create_user)
        self.create_user_btn.setEnabled(self.is_admin)

        self.delete_user_btn = QPushButton('Delete user', self)
        self.delete_user_btn.setFont(QFont(self.families[0], 12))
        self.delete_user_btn.move(self.window_width-650, 625)
        self.delete_user_btn.clicked.connect(self.open_delete_user)
        self.delete_user_btn.setEnabled(self.is_admin)

        self.table = QTableWidget(self)
        self.table.setEditTriggers(QAbstractItemView.NoEditTriggers)
        self.table.move(self.window_width-1250, 100)
        self.table.resize(900, 500)

    def open_delete_user(self):
        if self.engine is not None:
            with self.engine.connect() as connection:
                query = select('*').select_from(text('pg_roles'))
                result = [value[0] for value in connection.execute(query).fetchall() if (value[0] != 'guest') and ('pg' not in value[0]) and (value[0] != 'postgres')]
            
            self.delete_users = DeleteUserDialog(self, result)
            self.delete_users.delete_signal.connect(self.delete_user)
            self.delete_users.show()
        else:
            self.msg_box = MessageDialog(self, "DATABASE WAS NOT OPENED")
            self.msg_box.show()

    def delete_user(self, username):
        if self.engine is not None:
            try:
                with self.engine.connect() as connection:
                    connection.execute(func.deleteUserByUsername(username, self.database_name))
                    connection.commit()
            except Exception as ex:
                self.msg_box = MessageDialog(self, str(ex).split('\n')[0].split(')')[-1])
                self.msg_box.show()
        else:
            self.msg_box = MessageDialog(self, "DATABASE WAS NOT OPENED")
            self.msg_box.show()

    def open_create_user(self):
        if self.engine is not None:
            self.create = CreateUserDialog(self)
            self.create.create_signal.connect(self.create_user)
            self.create.show()
        else:
            self.msg_box = MessageDialog(self, "DATABASE WAS NOT OPENED")
            self.msg_box.show()

    def create_user(self, values):
        if self.engine is not None:
            try:
                with self.engine.connect() as connection:
                    username, password, permission = values
                    print(username, password, permission, self.database_name, 'my_table')
                    connection.execute(func.createUserWithPermission(username, password, permission, self.database_name, 'my_table'))
                    connection.commit()
            except Exception as ex:
                self.msg_box = MessageDialog(self, str(ex).split('\n')[0].split(')')[-1])
                self.msg_box.show()
                print(str(ex))
        else:
            self.msg_box = MessageDialog(self, "DATABASE WAS NOT OPENED")
            self.msg_box.show()

    def open_input(self, label: str, title: str):
        dialog = QInputDialog(self)
        dialog.setWindowTitle(title)
        dialog.setLabelText(label)
        dialog.setTextValue("")
        dialog.resize(900, 500)

        oImage = QImage(STYLE_DIR+'\\background.jpg')
        sImage = oImage.scaled(QSize(self.window_width, self.window_height))        
        palette = QPalette()
        palette.setBrush(QPalette.Window, QBrush(sImage))                        
        dialog.setPalette(palette)

        id = QFontDatabase.addApplicationFont(STYLE_DIR+"\\Poppins-SemiBold.ttf")
        if id < 0: print("Error")
        dialog.families = QFontDatabase.applicationFontFamilies(id)

        dialog.move(self.window_width-1250, 100)
        dialog.setWindowFlags(QtCore.Qt.FramelessWindowHint)
        le = dialog.findChild(QLineEdit)
        le.setFont(QFont(self.families[0], 20))

        ok, value = (
            dialog.exec_() == QDialog.Accepted,
            dialog.textValue(),
        )

        return ok, value

    def open_db(self, database_name):
        if database_name != '':
            self.database_name = database_name
            try:
                self.engine = create_engine(f"postgresql+psycopg2://{self.user.username}:{self.user.password}@localhost/{database_name}")

                with self.engine.connect() as connection:
                    metadata = MetaData()
                    self.psql_table = Table('my_table', metadata, autoload_with=self.engine)
                    
                    statement = select(self.psql_table)
                    result = connection.execute(statement)
                    self.columns = list(result.keys())

                    statement = select(self.psql_table).order_by(self.psql_table.columns.id.asc())
                    data = connection.execute(statement).fetchall()

                    if self.is_admin:
                        stored_procedures = Path(f'{QUIRIES_DIR}/stored_procedures.txt').read_text()
                        connection.execute(text(stored_procedures))

                        self.columns_dtypes = connection.execute(func.getColumnTypes()).fetchall()[0][0]
                        connection.commit()

                        connection.execute(func.createRoles(self.database_name))
                        connection.commit()

                self.table.setColumnCount(len(self.columns))
                self.table.setHorizontalHeaderLabels(self.columns)
                self.table.setRowCount(len(data))
                for i, values in enumerate(data):

                    for j in range(len(values)):
                        self.table.setItem(i, j, QTableWidgetItem(str(values[j])))
            except Exception as ex:
                self.msg_box = MessageDialog(self, str(ex).split('\n')[0].split(')')[-1])
                self.msg_box.show()

    def clear_table(self):
        self.table.clearContents() 
        self.table.setRowCount(0) 
        self.table.setColumnCount(0) 

    def refresh_table(self):
        self.clear_table()

        with self.engine.connect() as connection:
            statement = select(self.psql_table)
            result = connection.execute(statement)

            statement = select(self.psql_table).order_by(self.psql_table.columns.id.asc())
            data = connection.execute(statement).fetchall()

            self.columns = list(result.keys())

        self.table.setColumnCount(len(self.columns))
        self.table.setHorizontalHeaderLabels(self.columns)
        self.table.setRowCount(len(data))
        for i, values in enumerate(data):
            for j in range(len(values)):
                self.table.setItem(i, j, QTableWidgetItem(str(values[j])))

    def open_find(self):
        if self.engine is not None:
            self.find_dialog = FindDialog(self)
            self.find_dialog.find_signal.connect(self.find)
            self.find_dialog.show()
        else:
            self.msg_box = MessageDialog(self, "DATABASE WAS NOT OPENED")
            self.msg_box.show()

    def find(self, values):
        self.table.clearSelection()
        column_name = values[0]
        column_value = values[1]

        with self.engine.connect() as connection:
            result = connection.execute(func.getId(column_name, column_value)).fetchall()[0][0]

        self.table.setSelectionMode(QAbstractItemView.MultiSelection)
        for location in result:
            for row in range(self.table.rowCount()):
                item = self.table.item(row, 0)
                if item and item.text() == str(location):
                    self.table.selectRow(row)
                    continue
        self.table.setSelectionMode(QAbstractItemView.ExtendedSelection)

    def open_drop(self):
        if self.engine is not None:
            self.drop_dialog = FindDialog(self)
            self.drop_dialog.find_signal.connect(self.drop)
            self.drop_dialog.show()
        else:
            self.msg_box = MessageDialog(self, "DATABASE WAS NOT OPENED")
            self.msg_box.show()

    def drop(self, values):
        column_name = values[0]
        column_value = values[1]

        with self.engine.connect() as connection:
            connection.execute(func.deleteByColumn(column_name, column_value))
            connection.commit()

        self.refresh_table()

    def create_db(self, database_name):
        if database_name != '':
            try:
                engine = create_engine(f"postgresql+psycopg2://{self.user.username}:{self.user.password}@localhost/postgres",
                                       isolation_level="AUTOCOMMIT")
                with engine.connect() as connection:
                    connection.execute(text(f"CREATE DATABASE {database_name}"))
                del engine

                engine = create_engine(f"postgresql+psycopg2://{self.user.username}:{self.user.password}@localhost/{database_name}")
                with engine.connect() as connection:
                    create_query = text(Path(f'{QUIRIES_DIR}/create_table.txt').read_text())
                    connection.execute(create_query)
                    connection.commit()
                del engine
            except Exception as ex:
                self.msg_box = MessageDialog(self, str(ex).split('\n')[0].split(')')[-1])
                self.msg_box.show()

    def delete_db(self, database_name):
        if database_name != '':
            try:
                if self.engine:
                    self.engine.dispose()
                    self.clear_table()
                engine = create_engine(f"postgresql+psycopg2://{self.user.username}:{self.user.password}@localhost/postgres",
                                       isolation_level="AUTOCOMMIT")
                with engine.connect() as connection:
                    delete_query = text(Path(f'{QUIRIES_DIR}/delete_database.txt').read_text() % database_name)
                    connection.execute(delete_query)
                
            except Exception as ex:
                self.msg_box = MessageDialog(self, str(ex).split('\n')[0].split(')')[-1])
                self.msg_box.show()

    def open_one_input(self, function: str):
        if function.lower() == 'edit':
            if self.engine is not None:
                self.one_input = OneInputDialog(self, "enter ID: ", 'EDIT')
                self.one_input.input_signal.connect(self.open_edit)
                self.one_input.show()
            else:
                self.msg_box = MessageDialog(self, "DATABASE WAS NOT OPENED")
                self.msg_box.show()
        elif function.lower() == 'create':
            self.one_input = OneInputDialog(self, "enter database name: ", 'CREATE')
            self.one_input.input_signal.connect(self.create_db)
            self.one_input.show()
        elif function.lower() == 'open':
            self.one_input = OneInputDialog(self, "enter database name: ", 'OPEN')
            self.one_input.input_signal.connect(self.open_db)
            self.one_input.show()
        else:
            self.one_input = OneInputDialog(self, "enter database name: ", 'DELETE')
            self.one_input.input_signal.connect(self.delete_db)
            self.one_input.show()
    
    def open_edit(self, id):
        if self.engine is not None:
            if id != '':
                with self.engine.connect() as connection:
                    statement = select(self.psql_table).where(self.psql_table.columns.id == id)
                    data = list(connection.execute(statement).fetchall()[0])
                
                self.edit_dialog = EditDialog(self, data)
                self.edit_dialog.edit_signal.connect(self.edit)
                self.edit_dialog.show()
        else:
            self.msg_box = MessageDialog(self, "DATABASE WAS NOT OPENED")
            self.msg_box.show()

    def edit(self, dictionary: dict):
        values = list(dictionary.values())
        current_id = values[0]
        values = [f"'{value}'" for value in values]

        with self.engine.connect() as connection:
            connection.execute(func.editValues(values, current_id))
            connection.commit()

        self.refresh_table()

    def open_insert(self):
        if self.engine is not None:
            self.insert_dialog = InsertDialog(self)
            self.insert_dialog.insert_signal.connect(self.insert)
            self.insert_dialog.show()
        else:
            self.msg_box = MessageDialog(self, "DATABASE WAS NOT OPENED")
            self.msg_box.show()

    def insert(self, dictionary: dict):
        values = list(dictionary.values())

        with self.engine.connect() as connection:
            connection.execute(func.insertValues(values))
            connection.commit()

        self.refresh_table()