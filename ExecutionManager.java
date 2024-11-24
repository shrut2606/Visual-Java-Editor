import java.io.*;

public class ExecutionManager {
    public static String compileAndRun(String code) {
        try {
            // Save the code to a temporary Java file
            File tempFile = new File("Temp.java");
            try (BufferedWriter writer = new BufferedWriter(new FileWriter(tempFile))) {
                writer.write(code);
            }

            // Compile the Java file
            Process compileProcess = Runtime.getRuntime().exec("javac Temp.java");
            compileProcess.waitFor();

            // Run the compiled Java file
            Process runProcess = Runtime.getRuntime().exec("java Temp");
            BufferedReader reader = new BufferedReader(new InputStreamReader(runProcess.getInputStream()));
            StringBuilder output = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                output.append(line).append("\n");
            }
            return output.toString();
        } catch (Exception e) {
            e.printStackTrace();
            return "Error during compilation or execution";
        }
    }
}