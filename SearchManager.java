import javax.swing.*;

public class SearchManager {
    private JTextArea textArea;

    public SearchManager(JTextArea textArea) {
        this.textArea = textArea;
    }

    // Search functionality
    public void search(String keyword) {
        String text = textArea.getText();
        if (text.contains(keyword)) {
            textArea.select(text.indexOf(keyword), text.indexOf(keyword) + keyword.length());
        } else {
            JOptionPane.showMessageDialog(null, "Keyword not found");
        }
    }

    // Replace functionality
    public void replace(String findText, String replaceText) {
        String text = textArea.getText();
        text = text.replaceAll(findText, replaceText);
        textArea.setText(text);
    }
}