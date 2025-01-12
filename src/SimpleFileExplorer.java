import javax.swing.*;
import javax.swing.filechooser.FileSystemView;

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

    private JTextArea fileInfoArea;

    private JTextField currentPathField;


    public SimpleFileExplorer() {

        setTitle("Basit Dosya Gezgini");

        setSize(800, 400);

        setDefaultCloseOperation(EXIT_ON_CLOSE);

        setLocationRelativeTo(null);


        listModel = new DefaultListModel<>();

        fileList = new JList<>(listModel);

        fileList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);


        // Özel hücre görüntüleyici ekle
        fileList.setCellRenderer(new DefaultListCellRenderer() {
            private FileSystemView fileSystemView = FileSystemView.getFileSystemView();

            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value,
                    int index, boolean isSelected, boolean cellHasFocus) {
                JLabel label = (JLabel) super.getListCellRendererComponent(
                        list, value, index, isSelected, cellHasFocus);
                
                if (value instanceof File) {
                    File file = (File) value;
                    label.setIcon(fileSystemView.getSystemIcon(file));
                    label.setText(file.getName());
                }
                return label;
            }
        });

        // Seçim değişikliğini dinle
        fileList.addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                updateFileInfo();
            }
        });

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


        // Mevcut dizin yolunu gösterecek metin kutusu

        currentPathField = new JTextField();

        currentPathField.setEditable(true);

        currentPathField.setBorder(BorderFactory.createTitledBorder("Mevcut Dizin"));


        // Dizin yolu değiştiğinde işleyici ekle

        currentPathField.addActionListener(e -> {

            String newPath = currentPathField.getText().trim();

            File newDirectory = new File(newPath);

            if (newDirectory.exists() && newDirectory.isDirectory()) {

                openDirectory(newDirectory);

            } else {

                JOptionPane.showMessageDialog(this, 

                    "Geçersiz dizin yolu!", 

                    "Hata", 

                    JOptionPane.ERROR_MESSAGE);

                currentPathField.setText(currentDirectory.getAbsolutePath());

            }

        });


        // Enter tuşuna basıldığında da çalışması için KeyListener ekle

        currentPathField.addKeyListener(new KeyAdapter() {

            @Override

            public void keyPressed(KeyEvent e) {

                if (e.getKeyCode() == KeyEvent.VK_ENTER) {

                    String newPath = currentPathField.getText().trim();

                    File newDirectory = new File(newPath);

                    if (newDirectory.exists() && newDirectory.isDirectory()) {

                        openDirectory(newDirectory);

                    } else {

                        JOptionPane.showMessageDialog(SimpleFileExplorer.this, 

                            "Geçersiz dizin yolu!", 

                            "Hata", 

                            JOptionPane.ERROR_MESSAGE);

                        currentPathField.setText(currentDirectory.getAbsolutePath());

                    }

                }

            }

        });


        JPanel buttonPanel = new JPanel(new BorderLayout());

        JPanel navigationPanel = new JPanel();

        navigationPanel.add(backButton);

        navigationPanel.add(forwardButton);

        buttonPanel.add(navigationPanel, BorderLayout.WEST);

        buttonPanel.add(currentPathField, BorderLayout.CENTER);


        JScrollPane scrollPane = new JScrollPane(fileList);

        fileInfoArea = new JTextArea();

        fileInfoArea.setEditable(false);

        fileInfoArea.setBorder(BorderFactory.createTitledBorder("Dosya Bilgileri"));


        JPanel infoPanel = new JPanel(new BorderLayout());

        infoPanel.add(fileInfoArea, BorderLayout.CENTER);

        infoPanel.setPreferredSize(new Dimension(250, 0)); // Panelin genişliğini ayarlayın


        add(buttonPanel, BorderLayout.NORTH);

        add(scrollPane, BorderLayout.CENTER);

        add(infoPanel, BorderLayout.EAST);


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

            // Önce klasörleri ekle

            for (File file : files) {

                if (file.isDirectory()) {

                    listModel.addElement(file);

                }

            }

            

            // Sonra dosyaları ekle

            for (File file : files) {

                if (!file.isDirectory()) {

                    listModel.addElement(file);

                }

            }

        }

        updateFileInfo();

        currentPathField.setText(currentDirectory.getAbsolutePath());

    }


    private void updateFileInfo() {

        File selectedFile = fileList.getSelectedValue();

        if (selectedFile != null) {

            StringBuilder info = new StringBuilder();

            info.append("İsim: ").append(selectedFile.getName()).append("\n");

            info.append("Tür: ").append(selectedFile.isDirectory() ? "Klasör" : "Dosya").append("\n");

            info.append("Boyut: ").append(selectedFile.isDirectory() ? "N/A" : formatFileSize(selectedFile.length())).append("\n");

            fileInfoArea.setText(info.toString());

        } else {

            fileInfoArea.setText("Seçili dosya yok.");

        }

    }


    private String formatFileSize(long size) {

        return String.format("%.2f MB", size / (1024.0 * 1024.0));

    }


    public static void main(String[] args) {

        SwingUtilities.invokeLater(() -> {

            SimpleFileExplorer explorer = new SimpleFileExplorer();

            explorer.setVisible(true);

        });

    }

} 