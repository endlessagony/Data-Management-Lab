package gui;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableModel;
import javax.swing.table.TableRowSorter;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.sql.CallableStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import utils.databaseAccess;
import utils.user;

public class mainWindow extends JDialog {
    public static user static_user;
    public ResultSet data;
    public static JTable table;
    private JScrollPane scrollPane;
    private ArrayList<String> columnNames = new ArrayList<>();;
    public String databaseName;
    int columnCount;
    ArrayList<String> columnsDTypes;
    databaseAccess access;
    public mainWindow(user current_user) throws SQLException {
        super((JDialog)null);
        setModal(true);
        this.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        this.setResizable(false);
        this.setAlwaysOnTop(false);

        static_user = current_user;
        String permission = static_user.getPermission();
        boolean isAdmin = permission.equals("admin");

        Dimension dimension = Toolkit.getDefaultToolkit().getScreenSize();
        int x = (int) ((dimension.getWidth() - getWidth()) / 2);
        int y = (int) ((dimension.getHeight() - getHeight()) / 2);
        this.setBounds(400, 300, 1200, 800);
        this.setLocation(x, y);
        this.setLocationRelativeTo(null);
        this.getContentPane().setLayout(null);

        table = new JTable();
        table.setBounds(30, 40, 900, 700);
        table.setDefaultEditor(Object.class, null);
        table.getTableHeader().setReorderingAllowed(false);

        scrollPane = new JScrollPane(table);
        scrollPane.setBounds(10, 11, 964, 739);
        this.getContentPane().add(scrollPane);

        JButton openDatabaseButton = new JButton("Open");
        openDatabaseButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    openDatabase();
                    access = new databaseAccess(static_user.getUsername(), static_user.getPassword(),
                            databaseName, "open", mainWindow.this, static_user);
                } catch (SQLException | IOException ex) {
                    throw new RuntimeException(ex);
                }
            }
        });
        openDatabaseButton.setBounds(990, 10,180, 25);
        getContentPane().add(openDatabaseButton);

        JButton createDatabaseButton = new JButton("Create");
        createDatabaseButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                openDatabaseCreationWindow();
            }
        });
        createDatabaseButton.setBounds(990, 50,180, 25);
        getContentPane().add(createDatabaseButton);
        createDatabaseButton.setEnabled(isAdmin);

        JButton dropDatabaseButton = new JButton("Delete");
        dropDatabaseButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                openDatabaseDeleteWindow();
            }
        });
        dropDatabaseButton.setBounds(990, 90,180, 25);
        getContentPane().add(dropDatabaseButton);
        dropDatabaseButton.setEnabled(isAdmin);

        JButton selectButton = new JButton("Find");
        selectButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                openFindWindow();
            }
        });
        selectButton.setBounds(990, 170,180, 25);
        getContentPane().add(selectButton);

        JButton deleteButton = new JButton("Drop");
        deleteButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                openDropWindow();
            }
        });
        deleteButton.setBounds(990, 210,180, 25);
        getContentPane().add(deleteButton);
        deleteButton.setEnabled(isAdmin);

        JButton editButton = new JButton("Edit");
        editButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    openEditWindow();
                } catch (SQLException | IOException ex) {
                    throw new RuntimeException(ex);
                }
            }
        });
        editButton.setBounds(990, 250,180, 25);
        getContentPane().add(editButton);
        editButton.setEnabled(isAdmin);

        JButton addButton = new JButton("Insert");
        addButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    openAddWindow();
                } catch (SQLException | IOException ex) {
                    throw new RuntimeException(ex);
                }
            }
        });
        addButton.setBounds(990, 290,180, 25);
        getContentPane().add(addButton);
        addButton.setEnabled(isAdmin);

        JButton createUserButton = new JButton("Create user");
        createUserButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                openCreateUserWindow();
            }
        });
        createUserButton.setBounds(990, 370,180, 25);
        getContentPane().add(createUserButton);
        createUserButton.setEnabled(isAdmin);

        JButton deleteUserButton = new JButton("Delete user");
        deleteUserButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    openDeleteUserWindow();
                } catch (SQLException ex) {
                    throw new RuntimeException(ex);
                }
            }
        });
        deleteUserButton.setBounds(990, 410,180, 25);
        getContentPane().add(deleteUserButton);
        deleteUserButton.setEnabled(isAdmin);

//        JButton switchButton = new JButton("Switch account");
//        switchButton.addActionListener(new ActionListener() {
//            @Override
//            public void actionPerformed(ActionEvent e) {
//                try {
//                    access.deleteAllConnections();
//                    access.connection.close();
//                } catch (SQLException ex) {
//                    checkOpenedDatabase();
//                }
//                dispose();
//                openLoginWindow();
//            }
//        });
//        switchButton.setBounds(990, 684,180, 25);
//        getContentPane().add(switchButton);

        JButton exitButton = new JButton("Exit");
        exitButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                dispose();
                System.exit(ABORT);
            }
        });
        exitButton.setBounds(990, 724,180, 25);
        getContentPane().add(exitButton);
    }

    public void openAddWindow() throws SQLException, IOException {
        if (!checkOpenedDatabase()){
            return;
        }
//        addWindow add = new addWindow(columnNames, databaseName, static_user);
        addWindow add = new addWindow(columnNames, databaseName, static_user, access);
        add.showWindow();

        if (!add.isDisplayable()){
            data = add.updatedData;
            resfreshTable();
        }
    }

    public void showWindow(){
        this.setVisible(true);
    }

    public void openLoginWindow(){
        loginWindow login = new loginWindow();
        login.showWindow();
    }

    public void openDatabaseCreationWindow(){
        databaseCreationWindow creator = new databaseCreationWindow(static_user);
        creator.showWindow();
    }

    public void openEditWindow() throws SQLException, IOException {
        if (!checkOpenedDatabase()){
            return;
        }
        editWindow edit = new editWindow(columnNames, databaseName, static_user, access);
        edit.showWindow();

        if (!edit.isDisplayable()){
            data = edit.updatedData;
            resfreshTable();
        }
    }

    public void openDatabaseDeleteWindow() {
        deleteDatabaseWindow deleteWindow = new deleteDatabaseWindow(static_user, access);
        deleteWindow.showWindow();

        if (!deleteWindow.isDisplayable()){
            DefaultTableModel model = (DefaultTableModel) table.getModel();
            model.setRowCount(0);
            data = null;
        }
    }

    public boolean checkOpenedDatabase(){
        if (databaseName == null){
            JOptionPane.showMessageDialog(mainWindow.this,
                    "Database wasn't opened!", "Error",
                    JOptionPane.ERROR_MESSAGE);
            return false;
        } else {
            return true;
        }
    }

    public void openCreateUserWindow(){
        if (!checkOpenedDatabase()){
            return;
        }
        createUserWindow create = new createUserWindow(static_user, databaseName, access);
        create.showWindow();
    }

    public void openFindWindow(){
        if (!checkOpenedDatabase()){
            return;
        }
        dropSelection();
        findWindow find = new findWindow(columnNames, databaseName, static_user, access);
        find.showWindow();

//        if (!find.isDisplayable()){
//            List<Integer> ids = find.getIds();
//            if (!(ids == null)) {
//                System.out.println(ids);
//                for (int id : ids) {
//                    table.addRowSelectionInterval(id - 1, id - 1);
//                    table.setSelectionBackground(Color.PINK);
//                    table.setSelectionForeground(Color.BLACK);
//                }
//            }
//        }
        if (!find.isDisplayable()){
            List<Integer> ids = find.getIds();
            if (!(ids == null)) {
                for (int i = 0; i < table.getRowCount(); i++) {
                    Object value = table.getValueAt(i, 0);
                    if (ids.contains(value)) {
                        table.addRowSelectionInterval(i, i);
                        table.setSelectionBackground(Color.PINK);
                        table.setSelectionForeground(Color.BLACK);
                    }
                }
            }
        }
    }

    public void openDropWindow(){
        if (!checkOpenedDatabase()){
            return;
        }
        dropWindow drop = new dropWindow(databaseName, static_user, columnNames, access);
        drop.showWindow();

        if (!drop.isDisplayable()){
            data = drop.updatedData;
            resfreshTable();
        }
    }

    public void dropSelection(){
        int rowCount = table.getRowCount();
        table.addRowSelectionInterval(0, rowCount-1);
        table.setSelectionBackground(Color.WHITE);
        table.removeRowSelectionInterval(0, rowCount-1);
    }

    public void resfreshTable(){
        try {
            DefaultTableModel tableModel = new DefaultTableModel();
            ResultSetMetaData metaData = data.getMetaData();
            columnCount = metaData.getColumnCount();
            columnNames.clear();
            for (int columnIndex = 1; columnIndex <= columnCount; columnIndex++) {
                tableModel.addColumn(metaData.getColumnLabel(columnIndex));
                columnNames.add(metaData.getColumnLabel(columnIndex));
            }
            Object[] row = new Object[columnCount];
            while (data.next()) {
                for (int i = 0; i < columnCount; i++) {
                    row[i] = data.getObject(i + 1);
                }
                tableModel.addRow(row);
            }
            table.setModel(tableModel);
            table.setDefaultEditor(Object.class, null);
            table.getTableHeader().setReorderingAllowed(false);

            TableRowSorter<TableModel> sorter = new TableRowSorter<>(tableModel);
            sorter.setComparator(0, new Comparator<Integer>() {
                @Override
                public int compare(Integer id1, Integer id2) {
                    return Integer.compare(id1, id2);
                }
            });
            sorter.setSortKeys(Collections.singletonList(new RowSorter.SortKey(0, SortOrder.ASCENDING)));
            table.setRowSorter(sorter);
        } catch (Exception e) {
            System.out.println(e);
        }

        dropSelection();
    }

    public void openDeleteUserWindow() throws SQLException {
        if (!checkOpenedDatabase()){
            return;
        }
        deleteUserWindow delete = new deleteUserWindow(databaseName, static_user, access);
        delete.showWindow();
    }

    public void openDatabase() throws SQLException, IOException {
        openDatabasWindow open = new openDatabasWindow(this, static_user);
        open.showWindow();

        if (!open.isDisplayable()) {
            if (!checkOpenedDatabase()){
                return;
            }
            data = open.getData();
            databaseName = open.databaseName;
            try {
                DefaultTableModel tableModel = new DefaultTableModel();
                ResultSetMetaData metaData = data.getMetaData();
                columnCount = metaData.getColumnCount();
                for (int columnIndex = 1; columnIndex <= columnCount; columnIndex++) {
                    tableModel.addColumn(metaData.getColumnLabel(columnIndex));
                    columnNames.add(metaData.getColumnLabel(columnIndex));
                }
                Object[] row = new Object[columnCount];
                while (data.next()) {
                    for (int i = 0; i < columnCount; i++) {
                        row[i] = data.getObject(i + 1);
                    }
                    tableModel.addRow(row);
                }
                table.setModel(tableModel);
                table.setDefaultEditor(Object.class, null);
                table.getTableHeader().setReorderingAllowed(false);

                TableRowSorter<TableModel> sorter = new TableRowSorter<>(tableModel);
                sorter.setComparator(0, new Comparator<Integer>() {
                    @Override
                    public int compare(Integer id1, Integer id2) {
                        return Integer.compare(id1, id2);
                    }
                });
                sorter.setSortKeys(Collections.singletonList(new RowSorter.SortKey(0, SortOrder.ASCENDING)));
                table.setRowSorter(sorter);
            } catch (Exception e) {
                System.out.println(e);
            }
        }

    }
}
