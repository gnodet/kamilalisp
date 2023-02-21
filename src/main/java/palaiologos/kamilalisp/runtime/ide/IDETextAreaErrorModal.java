package palaiologos.kamilalisp.runtime.ide;

import javax.swing.*;
import javax.swing.event.InternalFrameAdapter;
import javax.swing.event.InternalFrameEvent;
import java.awt.*;

public class IDETextAreaErrorModal extends IDEModal {
    private JButton confirmButton;

    public IDETextAreaErrorModal(JDesktopPane parentComponent, String message, String text) {
        super(parentComponent);
        setTitle("Error");
        setMaximizable(false);
        setResizable(false);
        setClosable(true);
        setIconifiable(false);
        JPanel contentPane = new JPanel();
        contentPane.setBorder(null);
        contentPane.setBackground(Color.decode("#10141C"));
        setContentPane(contentPane);
        GroupLayout layout = new GroupLayout(contentPane);
        contentPane.setLayout(layout);
        layout.setAutoCreateGaps(true);
        layout.setAutoCreateContainerGaps(true);
        JLabel icon = new JLabel();
        icon.setIcon(UIManager.getIcon("OptionPane.errorIcon"));
        JLabel label = new JLabel(message);
        JTextArea textArea = new JTextArea(text);
        confirmButton = new JButton("OK");
        layout.setHorizontalGroup(layout.createSequentialGroup()
                .addComponent(icon)
                .addGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING)
                        .addComponent(label)
                        .addComponent(textArea)
                        .addComponent(confirmButton)));
        layout.setVerticalGroup(layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                        .addComponent(icon)
                        .addComponent(label))
                .addComponent(textArea)
                .addComponent(confirmButton));
    }

    public void display(Runnable onClose) {
        confirmButton.addActionListener(e -> {
            onClose.run();
            dispose();
        });
        addInternalFrameListener(new InternalFrameAdapter() {
            @Override
            public void internalFrameClosed(InternalFrameEvent e) {
                onClose.run();
                IDETextAreaErrorModal.this.dispose();
            }
        });
        super.display();
    }
}
