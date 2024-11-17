import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.File;
import java.util.Stack;

public class SimpleFileExplorer extends JFrame {
    private JList<File> fileList;
    private DefaultListModel<File> listModel;
    private File currentDirectory;
    private Stack<File> historyStack;
    private JButton backButton;
    private JButton forwardButton;
    private File forwardDirectory;

    public SimpleFileExplorer() {
        setTitle("Basit Dosya Gezgini");
        setSize(600, 400);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        listModel = new DefaultListModel<>();
        fileList = new JList<>(listModel);
        fileList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        // Klasör içeriğini göster
        fileList.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) {
                    File selectedFile = fileList.getSelectedValue();
                    if (selectedFile != null && selectedFile.isDirectory()) {
                        openDirectory(selectedFile);
                    }
                }
            }
        });

        // İleri geri butonları
        historyStack = new Stack<>();
        backButton = new JButton("Geri");
        forwardButton = new JButton("İleri");

        backButton.addActionListener(e -> goBack());
        forwardButton.addActionListener(e -> goForward());

        JPanel buttonPanel = new JPanel();
        buttonPanel.add(backButton);
        buttonPanel.add(forwardButton);

        JScrollPane scrollPane = new JScrollPane(fileList);
        add(buttonPanel, BorderLayout.NORTH);
        add(scrollPane, BorderLayout.CENTER);

        // Başlangıç dizinini ayarla
        currentDirectory = new File(System.getProperty("user.home"));
        openDirectory(currentDirectory);
    }

    private void openDirectory(File directory) {
        if (currentDirectory != null) {
            historyStack.push(currentDirectory);
        }
        currentDirectory = directory;
        forwardDirectory = null; // Yeni bir dizine geçildiğinde ileri dizini sıfırla
        updateTitleAndList();
    }

    private void goBack() {
        if (!historyStack.isEmpty()) {
            forwardDirectory = currentDirectory; // Mevcut dizini ileri dizin olarak sakla
            currentDirectory = historyStack.pop(); // Önceki dizine geç
            updateTitleAndList();
        }
    }

    private void goForward() {
        if (forwardDirectory != null) {
            historyStack.push(currentDirectory); // Mevcut dizini geri dizin olarak sakla
            currentDirectory = forwardDirectory; // İleri dizine geç
            forwardDirectory = null; // İleri dizini sıfırla
            updateTitleAndList();
        }
    }

    private void updateTitleAndList() {
        setTitle("Basit Dosya Gezgini - " + currentDirectory.getAbsolutePath());
        listModel.clear();

        File[] files = currentDirectory.listFiles();
        if (files != null) {
            for (File file : files) {
                listModel.addElement(file);
            }
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            SimpleFileExplorer explorer = new SimpleFileExplorer();
            explorer.setVisible(true);
        });
    }
}