import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class FindDialog extends JDialog {
    private JTextField findField;
    private JButton findButton;
    private JButton closeButton;
    private EditorPane editorPane;

    public FindDialog(Frame parent, EditorPane editorPane) {
        super(parent, "Find", true);
        this.editorPane = editorPane;

        // Set up the dialog box
        setSize(300, 120);
        setLocationRelativeTo(parent);
        setLayout(new FlowLayout());

        findField = new JTextField(20);
        findButton = new JButton("Find Next");
        closeButton = new JButton("Close");

        findButton.addActionListener(e -> findNext());
        closeButton.addActionListener(e -> setVisible(false));

        add(new JLabel("Find:"));
        add(findField);
        add(findButton);
        add(closeButton);
    }

    private void findNext() {
        String searchTerm = findField.getText();
        if (!searchTerm.isEmpty()) {
            editorPane.clearHighlights();
            editorPane.highlightText(searchTerm);
        }
    }
}