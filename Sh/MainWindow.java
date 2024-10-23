import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.FileReader;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.Timer;

public class MainWindow extends JFrame {
    private AttendanceManager manager;
    private JTable table;
    private DefaultTableModel tableModel;
    private JTextField pinField;
    private JTextField classFieldManual;
    private JTextField diaryNumberFieldManual;
    private JTextField firstNameField;
    private JTextField lastNameField;
    private JTextField classFieldStudent;
    private JTextField diaryNumberFieldStudent;
    private JTextField cardPinFieldStudent;
    private Timer timer;
    private String studentsString;
    private Set<String> duplicatePins = new HashSet<>(); // Globalny zestaw do przechowywania zduplikowanych PIN-ów
    private LogFile logFile;
    public MainWindow() {

        super("Attendance Manager");
        try {
            logFile = new LogFile("log.txt");
            logFile.log("Aplikacja została uruchomiona.");

        } catch (IOException e) {
            e.printStackTrace();
        }
        manager = new AttendanceManager();

        setLayout(new BorderLayout());
        String[] columns = {"Godzina", "Card Pin", "Imię", "Nazwisko", "Klasa", "Nr"};
        tableModel = new DefaultTableModel(columns, 0);
        table = new JTable(tableModel);
        table.setPreferredScrollableViewportSize(new Dimension(700, 500));
        table.setFillsViewportHeight(true);

        JScrollPane scrollPane = new JScrollPane(table);
        add(scrollPane, BorderLayout.NORTH);

        // Pin field and buttons
        JPanel topPanel = new JPanel(new GridLayout(1, 1));
        pinField = new JTextField();
        pinField.addKeyListener(new KeyAdapter() {
            @Override
            public void keyReleased(KeyEvent e) {
                if (timer != null) {
                    timer.cancel();
                }
                timer = new Timer();
                timer.schedule(new TimerTask() {
                    @Override
                    public void run() {
                        processCardPinInput();
                    }
                }, 200);
            }
        });

        topPanel.add(new JLabel("Card PIN"));
        topPanel.add(pinField);

        JButton addButton = new JButton("Dodaj");
        JButton checkInButton = new JButton("Check In");
        JButton loadButton = new JButton("Załaduj z pliku");
        JButton removeButton = new JButton("Usuń wybrane");

        topPanel.add(addButton);
        topPanel.add(checkInButton);
        topPanel.add(loadButton);
        topPanel.add(removeButton);

        add(topPanel, BorderLayout.CENTER);

        // Manual add panel
        JPanel manualAddPanel = new JPanel(new GridLayout(3, 2));
        manualAddPanel.add(new JLabel("Class"));
        classFieldManual = new JTextField();
        manualAddPanel.add(classFieldManual);

        manualAddPanel.add(new JLabel("Diary Number"));
        diaryNumberFieldManual = new JTextField();
        manualAddPanel.add(diaryNumberFieldManual);

        JButton manualAddToTableButton = new JButton("Wpisz");
        manualAddPanel.add(manualAddToTableButton);

        manualAddPanel.setVisible(false);
        add(manualAddPanel, BorderLayout.SOUTH);

        // Add student panel
        JPanel addStudentPanel = new JPanel(new GridLayout(1, 2));


        addStudentPanel.add(new JLabel("Klasa"));
        classFieldStudent = new JTextField();
        addStudentPanel.add(classFieldStudent);

        addStudentPanel.add(new JLabel("Nr Dzięnika"));
        diaryNumberFieldStudent = new JTextField();
        addStudentPanel.add(diaryNumberFieldStudent);

        JButton addStudentToFileButton = new JButton("Dodaj");
        addStudentPanel.add(addStudentToFileButton);

        addStudentPanel.setVisible(false);
        add(addStudentPanel, BorderLayout.SOUTH);

        addButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                manualAddPanel.setVisible(false);
                addStudentPanel.setVisible(true);
            }
        });

        manualAddToTableButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

            }
        });

        addStudentToFileButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                processAddStudentToFile();
            }
        });

        checkInButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                processCardPinInput();
            }
        });

        loadButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                loadStudentsFromFile();
            }
        });

        removeButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                removeSelectedRow();
            }
        });

        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(800, 600);
        setVisible(true);

        // Load students from file and print to terminal
        loadStudentsFromFile();
        System.out.println(studentsString);
    }

    private void processCardPinInput() {
        String cardPin = pinField.getText();
        if (!cardPin.trim().isEmpty()) {
            manager.checkInStudent(cardPin);
            updateTableWithCheckedInStudent(cardPin);
            pinField.setText("");
            logFile.log("PIN karty przetworzony: " + cardPinFieldStudent.getText());

        }
        if(!cardPin.trim().isEmpty() && !manager.getStudents().containsKey(cardPin)) {
            pinField.setText("");
        }
    }

    private void processAddStudentToFile() {
        // Pobierz dane z pól tekstowych
        String studentClass = classFieldStudent.getText();
        String diaryNumber = diaryNumberFieldStudent.getText();

        // Sprawdzenie, czy oba pola są wypełnione
        if (studentClass.isEmpty() || diaryNumber.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Proszę wypełnić zarówno klasę, jak i numer dziennika.", "Błąd", JOptionPane.ERROR_MESSAGE);
            return;
        }

        // Czytanie pliku i szukanie właściwego wpisu
        try (BufferedReader reader = new BufferedReader(new FileReader("student_data.csv"))) {
            String line;
            boolean studentFound = false;

            while ((line = reader.readLine()) != null) {
                String[] studentData = line.split(",");

                // Sprawdzenie, czy linia ma odpowiednią liczbę kolumn
                if (studentData.length >= 5) {
                    String classInFile = studentData[3].trim();
                    String diaryNumberInFile = studentData[4].trim();

                    // Sprawdzenie, czy klasa i numer dziennika pasują
                    if (classInFile.equalsIgnoreCase(studentClass) && diaryNumberInFile.equals(diaryNumber)) {
                        // Pobranie aktualnej daty i godziny
                        String currentDateTime = new SimpleDateFormat("dd.MM.yyyy-HH:mm:ss").format(new Date());
                        // Dodanie danych studenta do modelu tabeli
                        tableModel.addRow(new Object[]{currentDateTime, studentData[2], studentData[0], studentData[1], classInFile, diaryNumberInFile});
                        JOptionPane.showMessageDialog(MainWindow.this, "Student został dodany do tabeli.", "Potwierdzenie", JOptionPane.INFORMATION_MESSAGE);
                        studentFound = true;
                        classFieldStudent.setText("");
                        diaryNumberFieldStudent.setText("");
                        logFile.log("Dodano studenta do pliku: " + firstNameField.getText() + " " + lastNameField.getText());
                        break; // Jeśli znaleziono, zatrzymaj wyszukiwanie
                    }
                }
            }

            if (!studentFound) {
                JOptionPane.showMessageDialog(this, "Nie znaleziono studenta z podaną klasą i numerem dziennika.", "Informacja", JOptionPane.WARNING_MESSAGE);
            }

        } catch (IOException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Błąd odczytu pliku student_data.csv.", "Błąd", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void updateTableWithCheckedInStudent(String cardPin) {
        // Sprawdzenie istniejących duplikatów i dodanie nowego wpisu do zestawu duplikatów jeśli istnieje
        boolean pinExists = false;
        for (int i = 0; i < tableModel.getRowCount(); i++) {
            if (tableModel.getValueAt(i, 1).equals(cardPin)) { // Zakładamy, że numer PIN znajduje się w 2. kolumnie (index 1)
                pinExists = true;
                duplicatePins.add(cardPin);
                break;
            }
        }

        // Dodanie nowego wiersza do tabeli
        String currentDate = getCurrentDate();
        Student student = manager.getStudents().get(cardPin);
        Object[] rowData = {
                currentDate,
                student.cardPin,
                student.firstName,
                student.lastName,
                student.classNumber,
                student.schoolDiaryNumber
        };
        tableModel.addRow(rowData);

        // Ustawienie niestandardowego rendery dla całej tabeli po dodaniu nowego wiersza
        RowColorRenderer rowColorRenderer = new RowColorRenderer(duplicatePins);
        for (int columnIndex = 0; columnIndex < table.getColumnCount(); columnIndex++) {
            table.getColumnModel().getColumn(columnIndex).setCellRenderer(rowColorRenderer);
        }
        table.repaint(); // Odświeżenie tabeli, aby wyświetlić nowy renderer
        logFile.log("Zaktualizowano tabelę dla studenta z PINem: " + cardPin);
    }

    private String getCurrentDate() {
        return new SimpleDateFormat("dd.MM.yyyy-HH:mm:ss").format(new Date());
    }

    public void loadStudentsFromFile() {
        manager.loadStudentsFromFile("student_data.csv");

        // Initialize studentsString with student data
        StringBuilder sb = new StringBuilder();
        for (Map.Entry<String, Student> entry : manager.getStudents().entrySet()) {
            Student student = entry.getValue();
            sb.append(String.format("Student: %s %s, Card Pin: %s, Class: %s, Diary Number: %s\n",
                    student.firstName, student.lastName, student.cardPin, student.classNumber, student.schoolDiaryNumber));
        }
        studentsString = sb.toString();
        System.out.println("Students loaded from file successfully.");
        logFile.log("Załadowano studentów z pliku." + studentsString);
    }

    private void removeSelectedRow() {
        int selectedRow = table.getSelectedRow();
        if (selectedRow >= 0) {
            tableModel.removeRow(selectedRow);
            logFile.log("Usunięto wybrany wiersz z tabeli." + selectedRow);
        } else {
            JOptionPane.showMessageDialog(this, "Proszę wybrać wiersz do usunięcia");

        }
    }


    public static void main(String[] args) {
        MainWindow window = new MainWindow();
        window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        window.setSize(800, 600);
        window.setVisible(true);
    }

}