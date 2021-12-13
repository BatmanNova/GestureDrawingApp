import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.File;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;
import java.io.IOException;

public class App implements ActionListener {

    JFrame frame;
    JPanel picturePanel;
    JLabel pictureLabel;
    JPanel mainPanel;
    boolean pictureSet;
    boolean timerPaused;
    BufferedImage image;
    Image dimg;
    int currentImageIndex;

    JPanel titlePanel;
    JLabel myTitle1;
    JLabel myTitle2;

    JPanel timerPanel;
    JButton setTimer;
    JTextField timerField;
    JLabel timerLabel;
    int timerLength;
    int tempTimer = 0;
    Timer timer;

    JFileChooser chooser;
    JPanel folderPanel;
    JPanel folderInfoPanel;
    JButton setFolder;
    JLabel folderLabel1; // path to folder
    JLabel folderLabel2; // how many images in folder
    String folderPath;
    int numOfImages;

    JPanel settingsPanel;
    JButton start;

    int buttonWidth = 50;
    int buttonHeight = 20;

    JPanel bottomPanel;
    JButton previousImage;
    JButton nextImage;
    JButton pauseTimer;
    JLabel timerDuration;
    
    File[] filesInDirectory;

    public App() {
        //Creating a frame
        frame = new JFrame("Gesture Drawing App");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(1000, 800);
        frame.setLocationRelativeTo(null);
        
        //Creating a mainPanel
        mainPanel = new JPanel(new BorderLayout());
        mainPanel.setLayout(new GridLayout(1,4));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        mainPanel.setBackground(Color.GRAY);

        //Creating titlePanel
        titlePanel = new JPanel(new GridLayout(2,1));
        myTitle1 = new JLabel("Gesture Drawing App");
        myTitle1.setFont(new Font("Arial", Font.BOLD, 30));
        myTitle2 = new JLabel("by Drew Penn");

        //Creating setFolder button and label
        folderPanel = new JPanel(new GridLayout(1, 2));
        folderInfoPanel = new JPanel(new GridLayout(2, 1));
        setFolder = new JButton("Select Folder");
        setFolder.addActionListener(this);
        folderLabel1 = new JLabel("No folder selected");
        folderLabel2 = new JLabel("no images in folder");

        //Creating timerSet button
        timerPanel = new JPanel(new GridLayout(1, 2));
        setTimer = new JButton("Set Timer:");
        timerField = new JTextField(5);

        //Sets the textfield to "" if the user clicks into the text field.
        timerField.addMouseListener(new MouseAdapter(){
            @Override
            public void mouseClicked(MouseEvent e){
                timerField.setText("");
            }
        });

        setTimer.addActionListener(this);
        timerLabel = new JLabel("No timer set");

        //Creating start button
        settingsPanel = new JPanel(new GridLayout(1, 2));
        start = new JButton("Start");
        start.addActionListener(this);

        //Creating bottom panel
        bottomPanel = new JPanel(new GridLayout(1, 4));
        previousImage = new JButton("Previous");
        previousImage.addActionListener(this);
        nextImage = new JButton("Next");
        nextImage.addActionListener(this);
        pauseTimer = new JButton("Pause/Unpause");
        pauseTimer.addActionListener(this);
        timerDuration = new JLabel(tempTimer + " seconds");

        //Adding Panels to mainPanel
        titlePanel.add(myTitle1);
        titlePanel.add(myTitle2);
        
        folderInfoPanel.add(folderLabel1);
        folderInfoPanel.add(folderLabel2);
        
        folderPanel.add(setFolder);
        folderPanel.add(folderInfoPanel);
        
        timerPanel.add(setTimer);
        timerPanel.add(timerField);
        
        settingsPanel.add(timerLabel);
        settingsPanel.add(start);

        bottomPanel.add(previousImage);
        bottomPanel.add(nextImage);
        bottomPanel.add(pauseTimer);
        bottomPanel.add(timerDuration);

        mainPanel.add(titlePanel);
        mainPanel.add(folderPanel);
        mainPanel.add(timerPanel);
        mainPanel.add(settingsPanel);

        //Adding the mainPanel to the frame
        frame.add(mainPanel, BorderLayout.NORTH);    

        //Adding picture panel to frame
        picturePanel = new JPanel();
        picturePanel.setLayout(new GridLayout(1, 1));
        picturePanel.setBackground(Color.GRAY);

        //Adding the bottomPanel to the frame
        frame.add(picturePanel, BorderLayout.CENTER);
        frame.add(bottomPanel, BorderLayout.SOUTH);

        //Setting visible
        frame.setVisible(true);
    }
    
    public static void main(String[] args) throws Exception {
        new App();
    }

    @Override
    public void actionPerformed(ActionEvent e) {

        if (e.getSource() == setFolder){
            // user directory
            String userhome = System.getProperty("user.home");
            chooser = new JFileChooser(userhome +"\\Pictures");
            chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
            
            // disable the "All files" option.
            chooser.setAcceptAllFileFilterUsed(false);
            chooser.showOpenDialog(null);

            //displays folder path   
            folderPath = chooser.getSelectedFile().getAbsolutePath();
            folderLabel1.setText(folderPath);
            numOfImages = chooser.getSelectedFile().listFiles().length;
            folderLabel2.setText("images in folder: " + numOfImages);

       } else if(e.getSource() == setTimer){
            setTimer();

       } else if(e.getSource() == start){   
            GrabFilesAndShuffle();
            SetImage();
            StartTimer();
            
       } else if(e.getSource() == previousImage){
           DecreaseImageIndex();
           StartTimer();
           //timer full value

       } else if(e.getSource() == nextImage){
           IncreaseImageIndex();
           StartTimer();
           //timer full value

       } else if(e.getSource() == pauseTimer){
           PauseTimer();
       }
    }

    public void GrabFilesAndShuffle(){
        //Grabs the list of files and then shuffles them.
        filesInDirectory = chooser.getSelectedFile().listFiles();
        List<File> files = Arrays.asList(filesInDirectory);
        Collections.shuffle(files);
        filesInDirectory = files.toArray(new File[files.size()]);
    }
    
    public void UpdatePicture(){
        pictureLabel = new JLabel(new ImageIcon(new ImageIcon(image).getImage().getScaledInstance(picturePanel.getWidth(), picturePanel.getHeight(), Image.SCALE_SMOOTH)));
        picturePanel.removeAll();
        picturePanel.add(pictureLabel);
    }

    public void SetImage(){
        try{
            image = ImageIO.read(new File(filesInDirectory[currentImageIndex].getAbsolutePath()));
        } catch (IOException e){
            System.out.println("Error: " + e);
        }
        UpdatePicture();
    }

    public void setTimer(){
        String strTimer = timerField.getText();
        timerLength = Integer.parseInt(strTimer);
        timerLength *= 1000;
        timerLabel.setText("Timer set to: " + timerLength/1000);
    }

    public void StartTimer(){
        timer = new Timer();
        if(timerPaused){
            //do nothing because temptimer is paused value
            timerPaused = false;
        } else {
            tempTimer = 1000 + timerLength; //adding a second because images take a second to load
        }

        int delay = 1000; //delay of timer counting down
        timer.schedule(new TimerTask(){
            @Override
            public void run(){
                tempTimer -= delay;
                UpdateTimerDisplay();
            }
        }, delay, 1000);
        timer.schedule(new TimerTask(){
            @Override
            public void run(){
                IncreaseImageIndex();
                StartTimer();
            }
        }, tempTimer);
        
        SetImage();
    }

    public void PauseTimer(){
        if(timerPaused){
            StartTimer();
        } else {
            timer.cancel();
            timerPaused = true;
        }
    }

    public void UpdateTimerDisplay(){
        timerDuration.setText("Timer: " + tempTimer/1000);
    }

    public void IncreaseImageIndex(){
        timer.cancel();
        timer.purge();
        currentImageIndex++;
    }

    public void DecreaseImageIndex(){
        timer.cancel();
        timer.purge();
        currentImageIndex--;
    }
}

