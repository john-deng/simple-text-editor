package com.example;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.io.*;
import java.net.URL;
import org.apache.batik.transcoder.image.ImageTranscoder;
import org.apache.batik.transcoder.*;
import org.apache.batik.transcoder.TranscoderInput;
import org.apache.batik.transcoder.TranscoderOutput;
import org.apache.batik.transcoder.SVGAbstractTranscoder;
import org.apache.batik.anim.dom.SAXSVGDocumentFactory;
import org.apache.batik.util.XMLResourceDescriptor;
import org.w3c.dom.svg.SVGDocument;


public class SimpleTextEditor extends JFrame {
    private JTextArea textArea = new JTextArea(20, 60);
    private JFileChooser fc = new JFileChooser();

    // 在类的成员变量区域添加
    private JMenuItem menuItemUndo;
    private JMenuItem menuItemRedo;
    private JMenuItem menuItemDelete;

    public SimpleTextEditor() {
        // Set the Nimbus look and feel with dark theme colors
        setLookAndFeel();

        // 设置文本区域的颜色为暗黑主题
        textArea.setBackground(Color.BLACK); // 文本区背景设为黑色
        textArea.setForeground(Color.WHITE); // 文本字体设为白色

        JScrollPane scrollPane = new JScrollPane(textArea, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        add(scrollPane);

        JMenuBar menuBar = new JMenuBar();
        menuBar.setBackground(new Color(45, 45, 45));
        menuBar.setForeground(Color.WHITE);
        setJMenuBar(menuBar);

        // Create file menu with dark theme style
        JMenu fileMenu = createMenu("File", new Color(70, 70, 70), Color.WHITE);
        menuBar.add(fileMenu);
        System.out.println("show icon path: " + getResource("img/open.png"));
        // Add menu items with scaled icons and dark theme style
        fileMenu.add(createMenuItem("Open", loadSvgIcon("img/open_dark.svg"), new Color(70, 70, 70), Color.WHITE, this::openFile));
        fileMenu.add(createMenuItem("Save", loadSvgIcon("img/save_dark.svg"), new Color(70, 70, 70), Color.WHITE, this::saveFile));
        fileMenu.add(createMenuItem("Save As", null, new Color(70, 70, 70), Color.WHITE, this::saveFile));
        fileMenu.addSeparator(); // 添加分隔线

        fileMenu.add(createMenuItem("Reload All from Disk", new ImageIcon(getResource("img/refresh_dark.png")), new Color(70, 70, 70), Color.WHITE, this::reloadAction));
        fileMenu.addSeparator(); // 添加分隔线
        // Exit menu item
        fileMenu.add(createMenuItem("Exit", null, new Color(70, 70, 70), Color.WHITE, e -> exitApplication()));

        // add edit menu

        // 省略其他代码...

        // 在构造器中添加以下代码
        JMenu editMenu = this.createMenu("Edit", new Color(70, 70, 70), Color.WHITE);
        menuBar.add(editMenu);

        // 添加Edit菜单项
       // menuItemUndo = this.createMenuItem("Undo", scaleIcon(new ImageIcon(getResource("img/undo.png"))), new Color(70, 70, 70), Color.WHITE, this::undoAction);
        menuItemUndo = this.createMenuItem("Undo", loadSvgIcon("img/undo_dark.svg"), new Color(70, 70, 70), Color.WHITE, this::undoAction);
        menuItemRedo = this.createMenuItem("Redo", loadSvgIcon("img/redo_dark.svg"), new Color(70, 70, 70), Color.WHITE, this::redoAction);
        menuItemDelete = this.createMenuItem("Delete", null, new Color(70, 70, 70), Color.WHITE, this::deleteAction);
        editMenu.add(menuItemUndo);
        editMenu.add(menuItemRedo);
        editMenu.add(menuItemDelete);

        editMenu.addSeparator(); // 添加分隔线

        // 添加其他Edit菜单项，实际的行为（action）需要根据需求实现
        editMenu.add(this.createMenuItem("Cut", loadSvgIcon("img/menu-cut_dark.svg"), new Color(70, 70, 70), Color.WHITE, this::cutAction));
        editMenu.add(this.createMenuItem("Copy", loadSvgIcon("img/copy_dark.svg"), new Color(70, 70, 70), Color.WHITE, this::copyAction));
        editMenu.add(this.createMenuItem("Paste", null, new Color(70, 70, 70), Color.WHITE, this::pasteAction));
        // 省略添加其他菜单项...


        setTitle("Simple Text Editor");
        pack();
        setDefaultCloseOperation(EXIT_ON_CLOSE);
    }

    private URL getResource(String path) {
        return getClass().getClassLoader().getResource(path);
    }

    // Method to scale an icon to match text size
    private Icon scaleIcon(ImageIcon icon) {
        Image img = icon.getImage();
        Image scaledImg = img.getScaledInstance(16, 16, Image.SCALE_SMOOTH);
        return new ImageIcon(scaledImg);
    }

    private Icon loadSvgIcon(String path) {
        try {
            System.out.println("loadSvgIcon path = " + path);
            String parser = XMLResourceDescriptor.getXMLParserClassName();
            SAXSVGDocumentFactory f = new SAXSVGDocumentFactory(parser);
            URL url = getResource(path);
            SVGDocument doc = f.createSVGDocument(url.toString());

            TranscoderInput input = new TranscoderInput(doc);
            BufferedImageTranscoder t = new BufferedImageTranscoder();

            t.transcode(input, null);
            BufferedImage img = t.getBufferedImage();
            return new ImageIcon(img);
        } catch (Exception e) {
            System.out.println("loadSvgIcon e = " + e);
            e.printStackTrace();
            return null;
        }
    }

    static class BufferedImageTranscoder extends ImageTranscoder {
        private BufferedImage img = null;

        @Override
        public BufferedImage createImage(int w, int h) {
            BufferedImage bi = new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);
            return bi;
        }

        @Override
        public void writeImage(BufferedImage img, TranscoderOutput out) {
            this.img = img;
        }

        public BufferedImage getBufferedImage() {
            return img;
        }

        @Override
        protected void setImageSize(float width, float height) {
            if (width > 0 && height > 0) {
                super.setImageSize(width, height);
            }
        }
    }


    private void openFile(ActionEvent e) {
        if (fc.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
            try {
                textArea.read(new FileReader(fc.getSelectedFile()), null);
            } catch (IOException ex) {
                JOptionPane.showMessageDialog(this, "File could not be opened", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void saveFile(ActionEvent e) {
        if (fc.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
            try {
                textArea.write(new FileWriter(fc.getSelectedFile()));
            } catch (IOException ex) {
                JOptionPane.showMessageDialog(this, "File could not be saved", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
    private void exitApplication() {
        System.exit(0);
    }

    private void setLookAndFeel() {
        // 设置暗黑主题的UIManager属性
        try {
            for (UIManager.LookAndFeelInfo info : UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    UIManager.setLookAndFeel(info.getClassName());
                    UIManager.put("Menu.opaque", true);
                    UIManager.put("control", new Color(43, 45, 48));
                    UIManager.put("info", new Color(43, 45, 48));
                    UIManager.put("nimbusBase", new Color(7, 7, 7));
                    UIManager.put("nimbusAlertYellow", new Color(248, 187, 0));
                    UIManager.put("nimbusDisabledText", new Color(86, 85, 85));
                    UIManager.put("nimbusFocus", new Color(115, 164, 209));
                    UIManager.put("nimbusGreen", new Color(176, 179, 50));
                    UIManager.put("nimbusInfoBlue", new Color(66, 139, 221));
                    UIManager.put("nimbusLightBackground", new Color(18, 30, 49));
                    UIManager.put("nimbusOrange", new Color(191, 98, 4));
                    UIManager.put("nimbusRed", new Color(169, 46, 34));
                    UIManager.put("nimbusSelectedText", new Color(68, 126, 218));
                    UIManager.put("nimbusSelectionBackground", new Color(104, 93, 156));
                    UIManager.put("text", new Color(230, 230, 230));
                    break;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private JMenu createMenu(String title, Color background, Color foreground) {
        JMenu menu = new JMenu(title);
        menu.setBackground(background);
        menu.setForeground(foreground);
        return menu;
    }

    private JMenuItem createMenuItem(String title, Icon icon, Color background, Color foreground, ActionListener action) {
        JMenuItem menuItem = new JMenuItem(title, icon);
        menuItem.setBackground(background);
        menuItem.setForeground(foreground);
        menuItem.addActionListener(action);
        return menuItem;
    }

    // edit menu action
    // 最后，实现这些行为的方法
    private void reloadAction(ActionEvent e) {
        // 实现撤销的逻辑
        System.out.println("e = " + e.toString());
    }
    private void undoAction(ActionEvent e) {
        // 实现撤销的逻辑
        System.out.println("e = " + e.toString());
    }

    private void deleteAction(ActionEvent e) {
        // 实现重做的逻辑
        System.out.println("e = " + e.toString());
    }

    private void redoAction(ActionEvent e) {
        // 实现重做的逻辑
        System.out.println("e = " + e.toString());
    }

    private void cutAction(ActionEvent e) {
        textArea.cut();
    }

    private void copyAction(ActionEvent e) {
        textArea.copy();
    }

    private void pasteAction(ActionEvent e) {
        textArea.paste();
    }

// 省略其他方法的实现...



    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new SimpleTextEditor().setVisible(true));
    }
}
