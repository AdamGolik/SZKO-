# MainWindow Application

## Description

Aplikacja MainWindow jest prostym narzędziem do zarządzania obecnością studentów. Umożliwia dodawanie, przetwarzanie oraz monitorowanie obecności za pomocą tabeli wyświetlanej w GUI.

## Zawartość

### Pliki

- `MainWindow.java`: Główna klasa aplikacji, która implementuje GUI oraz funkcje związane z zarządzaniem obecnością.

### Implementacja

#### `MainWindow.java`

```java
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
    private Set<String> duplicatePins = new HashSet<>(); // Global set for storing duplicate PINs
    private LogFile logFile;
    public MainWindow() {/* implementation omitted for shortness */}

    private void processCardPinInput() {/* implementation omitted for shortness */}

    private void processAddStudentToFile() {/* implementation omitted for shortness */}

    private void updateTableWithCheckedInStudent(String cardPin) {/* implementation omitted for shortness */}

    private String getCurrentDate() {/* implementation omitted for shortness */}

    public void loadStudentsFromFile() {/* implementation omitted for shortness */}

    private void removeSelectedRow() {/* implementation omitted for shortness */}

    public static void main(String[] args) {/* implementation omitted for shortness */}
}
```

## Instrukcje

### Kompilacja i Uruchomienie

- Upewnij się, że masz zainstalowane JDK.
- Skompiluj pliki źródłowe za pomocą `javac`:
  ```sh
  javac *.java
  ```
- Uruchom aplikację za pomocą `java`:
  ```sh
  java MainWindow
  ```

### Problemy

- Jeśli program nie działa poprawnie, upewnij się, że masz zainstalowaną najnowszą wersję JDK.
  - Windows: [Pobierz JDK](https://www.oracle.com/java/technologies/downloads/#jdk23-windows)
  - macOS: [Pobierz JDK](https://www.oracle.com/java/technologies/downloads/#jdk23-mac)
  - Linux: [Pobierz JDK](https://www.oracle.com/java/technologies/downloads/#jdk23-linux)

## Wkład

Proszę zgłaszać wszelkie problemy lub sugestie dotyczące ulepszeń aplikacji na adres [adamgolik031@gmail.com].
