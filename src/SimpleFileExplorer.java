import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.File;

public class SimpleFileExplorer extends JFrame {
    private JList<File> fileList;
    private DefaultListModel<File> listModel;
    private File currentDirectory;

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

        JScrollPane scrollPane = new JScrollPane(fileList);
        add(scrollPane, BorderLayout.CENTER);

        // Başlangıç dizinini ayarla
        currentDirectory = new File(System.getProperty("user.home"));
        openDirectory(currentDirectory);
    }

    private void openDirectory(File directory) {
        currentDirectory = directory;
        setTitle("Basit Dosya Gezgini - " + currentDirectory.getAbsolutePath());
        listModel.clear();

        File[] files = directory.listFiles();
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
