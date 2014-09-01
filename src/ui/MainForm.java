package ui;

import util.XlsToJrxmlGenerator;

import javax.swing.*;
import javax.swing.filechooser.FileFilter;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: IPC_Server
 * Date: 8/26/14
 * Time: 1:05 PM
 * To change this template use File | Settings | File Templates.
 */
public class MainForm {
    private JPanel mainPanel;
    private JButton button1;
    private JTextField fileTextField;
    private JFileChooser jFileChooser;
    private JButton generateJRXMLButton;

    private JComboBox orientationComboBox;
    File selectedFile;
    public MainForm() {

        jFileChooser = new JFileChooser();
        jFileChooser.setFileFilter(new FileFilter() {
            @Override
            public boolean accept(File f) {
                String filename = f.toString();
                if(filename==null) {
                    return false;
                }
                filename = filename.toLowerCase();
                return f.isDirectory() || filename.endsWith(".xls") || filename.endsWith(".xlsx");
            }

            @Override
            public String getDescription() {
                return null;  //To change body of implemented methods use File | Settings | File Templates.
            }
        });
        jFileChooser.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                selectedFile = jFileChooser.getSelectedFile();
                if(selectedFile!=null) {
                    fileTextField.setText(selectedFile.toString());
                    jFileChooser.setCurrentDirectory(selectedFile.getParentFile());
                }

            }
        });


        button1.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

                jFileChooser.showOpenDialog(mainPanel);
            }
        });

        generateJRXMLButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

                if(selectedFile!=null) {
                    try {
                        boolean portrait = orientationComboBox.getSelectedItem().toString().equals("Portrait");
                        List<File> outputFiles= XlsToJrxmlGenerator.process(selectedFile, portrait);
                        String resultText = "<html>Generated output file/s:<br>";
                        for(File outPutFile: outputFiles) {
                            resultText+=outPutFile+"<br>";
                        }
                        resultText+="</html>";

                        JOptionPane.showMessageDialog(mainPanel,resultText);
                    } catch (Exception e1) {
                        try {
                            e1.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
                            StringWriter sw = new StringWriter();
                            PrintWriter pw = new PrintWriter(sw);
                            e1.printStackTrace(pw);
                            pw.close();
                            sw.close();
                            JOptionPane.showMessageDialog(mainPanel,sw.toString());
                        } catch (Exception e2) {
                            e2.printStackTrace();
                        }

                    }

                }

            }
        });
    }

    public JPanel getMainPanel() {
        return mainPanel;
    }
}
