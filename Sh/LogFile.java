import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.Date;

public class LogFile {
    private PrintWriter writer;

    public LogFile(String fileName) throws IOException {
        writer = new PrintWriter(new FileWriter(fileName, true)); // 'true' włącza tryb dopisania do pliku
        writer.println("========================================================================================================================================");
        writer.flush(); // upewnij się, że dane są natychmiast zapisywane do pliku
    }

    public void log(String message) {
        String timeStamp = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
        writer.println(timeStamp + " - " + message);
        writer.flush(); // upewnij się, że dane są natychmiast zapisywane do pliku
    }

    public void close() {
        writer.close();
    }
}