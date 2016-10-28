package main;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.net.URISyntaxException;

import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;

public class MainWindow extends JPanel implements ActionListener
{
    private static final long serialVersionUID = -371374960795143493L;
    private boolean bNew = false;
    private boolean bError = false;
    private boolean bSuccess = false;
    private JLabel errorLabel;
    private JButton btnGo;
    private JRadioButton btnNew;
    private JRadioButton btnOld;
    private JLabel descLabel;
    private String errorMessage;
    private Color errorColour;

    public void initialise()
    {
        descLabel = new JLabel("Choose which FBI you want and press Go.");
        Font labelFont = descLabel.getFont();
        descLabel.setFont(new Font(labelFont.getName(), Font.PLAIN, 24));
        descLabel.setMaximumSize(new Dimension(500, 30));
        descLabel.setMinimumSize(new Dimension(500, 30));
        add(descLabel);

        JLabel blankLabel1 = new JLabel("   ");
        blankLabel1.setMinimumSize(new Dimension(30, 30));
        add(blankLabel1);

        btnOld = new JRadioButton("Old (stable) FBI");
        btnOld.setActionCommand("old");
        btnOld.addActionListener(this);
        labelFont = btnOld.getFont();
        btnOld.setFont(new Font(labelFont.getName(), Font.PLAIN, 20));
        btnOld.setMaximumSize(new Dimension(500, 20));
        btnOld.setMinimumSize(new Dimension(500, 20));
        add(btnOld);
        JLabel blankLabel2 = new JLabel("   ");
        blankLabel2.setMinimumSize(new Dimension(20, 20));
        add(blankLabel2);

        btnNew = new JRadioButton("Newest FBI");
        btnNew.setActionCommand("new");
        btnNew.addActionListener(this);
        labelFont = btnNew.getFont();
        btnNew.setFont(new Font(labelFont.getName(), Font.PLAIN, 20));
        btnNew.setMaximumSize(new Dimension(500, 20));
        btnNew.setMinimumSize(new Dimension(500, 20));
        add(btnNew);

        ButtonGroup group = new ButtonGroup();
        group.add(btnOld);
        group.add(btnNew);

        JLabel blankLabel3 = new JLabel("   ");
        blankLabel3.setMinimumSize(new Dimension(30, 30));
        add(blankLabel3);

        btnGo = new JButton("Go");
        btnGo.setActionCommand("go");
        btnGo.addActionListener(this);
        btnGo.setMaximumSize(new Dimension(100, 40));
        btnGo.setMinimumSize(new Dimension(100, 40));
        add(btnGo);

        errorLabel = new JLabel();
        labelFont = errorLabel.getFont();
        errorLabel.setFont(new Font(labelFont.getName(), Font.PLAIN, 16));
        add(errorLabel);
        errorColour = Color.GREEN;
    }

    public MainWindow()
    {
        initialise();
    }

    private static void createWindow()
    {
        JFrame frame = new JFrame("MainWindow");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setPreferredSize(new Dimension(500, 500));

        MainWindow newContentPane = new MainWindow();
        newContentPane.setLayout(new BoxLayout(newContentPane, BoxLayout.PAGE_AXIS));
        newContentPane.setOpaque(true);
        frame.setContentPane(newContentPane);

        frame.setLocationRelativeTo(null);
        frame.pack();
        frame.setVisible(true);
    }

    public static void main(String[] args)
    {
        createWindow();
    }

    @Override
    public void actionPerformed(ActionEvent e)
    {
        if ("go".equals(e.getActionCommand()))
        {
            try
            {
                bError = false;
                bSuccess = false;
                (new Injector()).inject(bNew);
                bSuccess = true;
            }
            catch (NoFiles9FolderException f9)
            {
                f9.printStackTrace();
                bError = true;
                errorMessage = "No files9 folder found. InjectorHelper should go in the root of your SD card and the files9 folder should be in the root as well.";
            }
            catch(NoHsAppException hs)
            {
                hs.printStackTrace();
                bError = true;
                errorMessage = "hs.app is not in your files9 folder. Check you dumped it properly.";
            }
            catch(IOException io)
            {
                io.printStackTrace();
                bError = true;
                errorMessage = "Something went wrong with reading your hs.app.";
            }
            catch(InterruptedException|URISyntaxException ex)
            {
                ex.printStackTrace();
                bError = true;
                errorMessage = "Universal Inject Generator was not able to be launched.";
            }

            if (bError)
            {
                errorColour = Color.RED;
            }
            else
            {
                errorLabel.setText("");
            }
            if (bSuccess)
            {
                errorColour = Color.GREEN;
                errorMessage = "Success! You can put your SD card back and inject FBI now.";
            }
            errorLabel.setText(errorMessage);
            errorLabel.setForeground(errorColour);
        }
        if ("new".equals(e.getActionCommand()))
        {
            bNew = true;
        }
        if ("old".equals(e.getActionCommand()))
        {
            bNew = false;
        }
    }
}
