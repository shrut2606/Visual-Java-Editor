import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.regex.Pattern;

public class ReplaceDialog extends JDialog {
    private JTextField findField;
    private JTextField replaceField;
    private JButton findButton;
    private JButton replaceButton;
    private JButton replaceAllButton;  // Added a new button for replace all
    private JButton closeButton;
    private EditorPane editorPane;

    public ReplaceDialog(Frame parent, EditorPane editorPane) {
        super(parent, "Replace", true);
        this.editorPane = editorPane;

        // Set up the dialog box
        setSize(300, 150);
        setLocationRelativeTo(parent);
        setLayout(new FlowLayout());

        findField = new JTextField(20);
        replaceField = new JTextField(20);
        findButton = new JButton("Find Next");
        replaceButton = new JButton("Replace");
        replaceAllButton = new JButton("Replace All");  // Added "Replace All" button
        closeButton = new JButton("Close");

        findButton.addActionListener(e -> findNext());
        replaceButton.addActionListener(e -> replaceText());
        replaceAllButton.addActionListener(e -> replaceAllText());  // Implement the replace all logic
        closeButton.addActionListener(e -> {
            editorPane.clearHighlights();  // Clear highlights when closing
            setVisible(false);
        });

        add(new JLabel("Find:"));
        add(findField);
        add(new JLabel("Replace:"));
        add(replaceField);
        add(findButton);
        add(replaceButton);
        add(replaceAllButton);  // Add replace all button to the dialog
        add(closeButton);
    }

    private void findNext() {
        String searchTerm = findField.getText();
        if (!searchTerm.isEmpty()) {
            editorPane.clearHighlights();
            editorPane.highlightText(searchTerm);
        }
    }

    private void replaceText() {
        String searchTerm = findField.getText();
        String replaceTerm = replaceField.getText();

        if (!searchTerm.isEmpty() && !replaceTerm.isEmpty()) {
            String text = editorPane.getTextArea().getText();
            text = text.replaceFirst(Pattern.quote(searchTerm), replaceTerm);  // Replace the first occurrence
            editorPane.getTextArea().setText(text);
        }
    }

    // Replace all occurrences of the search term
    private void replaceAllText() {
        String searchTerm = findField.getText();
        String replaceTerm = replaceField.getText();

        if (!searchTerm.isEmpty() && !replaceTerm.isEmpty()) {
            String text = editorPane.getTextArea().getText();
            text = text.replaceAll(Pattern.quote(searchTerm), replaceTerm);  // Replace all occurrences
            editorPane.getTextArea().setText(text);
        }
    }
}