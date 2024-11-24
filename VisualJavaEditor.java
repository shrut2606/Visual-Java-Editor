import javax.swing.*;
import javax.swing.event.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import javax.swing.text.*;
import javax.tools.*;
import java.util.*;


public class VisualJavaEditor {
    private JFrame frame;
    private EditorPane editorPane;
    private LineNumberPanel lineNumberPanel;
    private JTextArea outputArea;

    public VisualJavaEditor() {
        // Set up the main frame
        frame = new JFrame("Visual Java Editor");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(800, 600);

        // Create the editor pane (TextArea)
        editorPane = new EditorPane();
        JScrollPane scrollPane = new JScrollPane(editorPane.getTextArea());
        frame.getContentPane().add(scrollPane, BorderLayout.CENTER);

        //Output area
        outputArea = new JTextArea();
        outputArea.setEditable(false);
        JScrollPane outputScrollPane = new JScrollPane(outputArea);
        outputScrollPane.setPreferredSize(new Dimension(800, 150));
        frame.getContentPane().add(outputScrollPane, BorderLayout.SOUTH);

        // Create a panel for line numbers
        lineNumberPanel = new LineNumberPanel(editorPane.getTextArea());
        scrollPane.setRowHeaderView(lineNumberPanel);

        // Create a menu bar with file operations
        JMenuBar menuBar = new JMenuBar();

        JMenu fileMenu = new JMenu("File");
        JMenuItem newFile = new JMenuItem("New");
        JMenuItem openFile = new JMenuItem("Open");
        JMenuItem saveFile = new JMenuItem("Save");
        JMenuItem exitItem = new JMenuItem("Exit");

        newFile.addActionListener(e -> newFile());
        openFile.addActionListener(e -> openFile());
        saveFile.addActionListener(e -> saveFile());
        exitItem.addActionListener(e -> System.exit(0));

        fileMenu.add(newFile);
        fileMenu.add(openFile);
        fileMenu.add(saveFile);
        fileMenu.addSeparator();
        fileMenu.add(exitItem);

        JMenu editMenu = new JMenu("Edit");
        JMenuItem findItem = new JMenuItem("Find");
        JMenuItem replaceItem = new JMenuItem("Replace");

        findItem.addActionListener(e -> openFindDialog());
        replaceItem.addActionListener(e -> openReplaceDialog());

        editMenu.add(findItem);
        editMenu.add(replaceItem);

        JMenu runMenu = new JMenu("Run");
        JMenuItem runItem = new JMenuItem("Run");

        runItem.addActionListener(e -> runCode());

        runMenu.add(runItem);

        menuBar.add(fileMenu);
        menuBar.add(editMenu);
        menuBar.add(runMenu);

        frame.setJMenuBar(menuBar);

        // Add a DocumentListener to update line numbers
        editorPane.getTextArea().getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) {
                updateLineNumbers();
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                updateLineNumbers();
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                updateLineNumbers();
            }
        });

        // Add key listener to sync line numbers after pressing Enter
        editorPane.getTextArea().addKeyListener(new KeyAdapter() {
            
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    // editorPane.handleEnterPress();
                    SwingUtilities.invokeLater(() -> updateLineNumbers());
                }
            }
        });

        // Show the frame
        frame.setVisible(true);
    }

    private void updateLineNumbers() {
        // This forces the line number panel to repaint and sync with the text area
        lineNumberPanel.repaint();
    }

    private void newFile() {
        editorPane.getTextArea().setText("");  // Clear the text area
    }

    private void openFile() {
        JFileChooser fileChooser = new JFileChooser();
        int returnValue = fileChooser.showOpenDialog(frame);

        if (returnValue == JFileChooser.APPROVE_OPTION) {
            File selectedFile = fileChooser.getSelectedFile();
            String filePath = selectedFile.getAbsolutePath();

            try (BufferedReader reader = new BufferedReader(new FileReader(selectedFile))) {
                StringBuilder content = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null) {
                    content.append(line).append("\n");
                }
                editorPane.getTextArea().setText(content.toString());
            } catch (IOException ex) {
                JOptionPane.showMessageDialog(frame, "Error opening file: " + ex.getMessage(), 
                                              "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void saveFile() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Save As");
        fileChooser.setSelectedFile(new File("untitled.java"));

        int returnValue = fileChooser.showSaveDialog(frame);
        if (returnValue == JFileChooser.APPROVE_OPTION) {
            File selectedFile = fileChooser.getSelectedFile();
            String filePath = selectedFile.getAbsolutePath();

            if (!filePath.endsWith(".java")) {
                selectedFile = new File(filePath + ".java");
            }

            try (BufferedWriter writer = new BufferedWriter(new FileWriter(selectedFile))) {
                writer.write(editorPane.getTextArea().getText());
                JOptionPane.showMessageDialog(frame, "File saved successfully!");
            } catch (IOException ex) {
                JOptionPane.showMessageDialog(frame, "Error saving file: " + ex.getMessage(),
                                              "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void runCode() {
        StringBuilder output = new StringBuilder();
        output.append("");
        String code = editorPane.getTextArea().getText();
            if (code.trim().isEmpty()) {
                JOptionPane.showMessageDialog(frame, "No code to run!", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
    
            try {
                // Write the code to a temporary file
                File tempFile = new File("TempProgram.java");
                try (BufferedWriter writer = new BufferedWriter(new FileWriter(tempFile))) {
                    writer.write(code);
                }
    
                // Compile the Java file using JavaCompiler API
                JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();
                if (compiler == null) {
                    outputArea.setText("JavaCompiler not found. Make sure you are running a JDK, not a JRE.");
                    return;
                }
    
                // Use DiagnosticCollector to capture compilation errors
                DiagnosticCollector<JavaFileObject> diagnostics = new DiagnosticCollector<>();
                StandardJavaFileManager fileManager = compiler.getStandardFileManager(diagnostics, null, null);
                Iterable<? extends JavaFileObject> compilationUnits = fileManager.getJavaFileObjects(tempFile);
                JavaCompiler.CompilationTask task = compiler.getTask(null, fileManager, diagnostics, null, null, compilationUnits);
    
                boolean success = task.call();
    
                if (!success) {
                    // Display compilation errors
                    StringBuilder errorOutput = new StringBuilder("Compilation failed:\n");
                    for (Diagnostic<?> diagnostic : diagnostics.getDiagnostics()) {
                        errorOutput.append("Error on line ").append(diagnostic.getLineNumber()).append(": ");
                        errorOutput.append(diagnostic.getMessage(null)).append("\n");
                    }
                    outputArea.setText(errorOutput.toString());
                    fileManager.close();
                    return;
                }
    
                // Run the compiled program
                ProcessBuilder processBuilder = new ProcessBuilder("java", tempFile.getName().replace(".java", ""));
                processBuilder.redirectErrorStream(true);
                Process process = processBuilder.start();
    
                // Capture the output of the program
                try (BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()))) {
                    // StringBuilder output = new StringBuilder();
                    String line;
                    while ((line = reader.readLine()) != null) {
                        output.append(line).append("\n");
                    }
                    outputArea.setText(output.toString());
                }
    
                fileManager.close();
    
                // Delete the temporary files after execution
                tempFile.delete();
                new File("TempProgram.class").delete();
            } catch (IOException e) {
                outputArea.setText("Error: " + e.getMessage());
            }
        }

    private String getCompilationErrors(File sourceFile) {
        // Get the compilation error messages
        StringBuilder errorMessages = new StringBuilder();
        try {
            ProcessBuilder processBuilder = new ProcessBuilder("javac", sourceFile.getAbsolutePath());
            Process process = processBuilder.start();
            process.waitFor();
            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getErrorStream()));
            String line;
            while ((line = reader.readLine()) != null) {
                errorMessages.append(line).append("\n");
            }
        } catch (IOException | InterruptedException ex) {
            errorMessages.append("Error while compiling: ").append(ex.getMessage());
        }
        return errorMessages.toString();
    }

    private void openFindDialog() {
        FindDialog findDialog = new FindDialog(frame, editorPane);
        findDialog.setVisible(true);
    }

    private void openReplaceDialog() {
        ReplaceDialog replaceDialog = new ReplaceDialog(frame, editorPane);
        replaceDialog.setVisible(true);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new VisualJavaEditor());
    }
}