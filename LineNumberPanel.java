import javax.swing.*;
import java.awt.*;

public class LineNumberPanel extends JPanel {
    private JTextArea textArea;

    public LineNumberPanel(JTextArea textArea) {
        this.textArea = textArea;
        setBackground(Color.LIGHT_GRAY);
        setPreferredSize(new Dimension(50, textArea.getHeight()));
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        drawLineNumbers(g);
    }

    private void drawLineNumbers(Graphics g) {
        int lineHeight = textArea.getFontMetrics(textArea.getFont()).getHeight();
        int numberOfLines = textArea.getLineCount();
        g.setColor(Color.BLACK);
        
        // Drawing line numbers
        for (int i = 0; i < numberOfLines; i++) {
            String lineNumber = String.valueOf(i + 1);
            g.drawString(lineNumber, 5, (i + 1) * lineHeight);
        }
    }

    public void updateLineNumbers() {
        revalidate();
        repaint();
    }
}
