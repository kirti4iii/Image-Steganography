import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.Objects;

public class EncodePage extends JFrame {
    private JPanel EncodePage;
    private JTextArea msg;
    private JButton menuButton;
    private JButton ImgSrcBtn;
    private JButton EncodeBtn;
    private JButton ResetBtn;
    private JButton SaveBtn;
    private JLabel SrcImgLabel;
    private JPanel SrcImgPanel;
    private JLabel DesImgLabel;
    private JPanel DesImgPanel;
    private ImageIcon img;
    private String FilePath;
    private BufferedImage SteganoImg;
    private int encodeFlag=1;
    private final JDialog dialog;
    private final JLabel dialogMsg;
    private Integer maskLsbZero=0;
    private final char[] arr;
    private String format;

    private boolean isAlphaNum(String s){
        for (final char c : s.toCharArray()) {
            if (!(Character.isLetter(c)||Character.isDigit(c)||c==' ')) {
                return false;
            }
        }
        return true;
    }

    EncodePage(){
        arr =  new char[64];
        arr[0]=' ';
        arr[1]='\'';
        for(int i=2;i<12;i++){
            arr[i]=(char)(i+46);
        }
        for(int i=12;i<38;i++){
            arr[i]=(char)(i+53);
        }
        for(int i=38;i<64;i++){
            arr[i]=(char)(i+59);
        }
        System.out.println("encode page\n");
        setTitle("Image Steganographer");
        setContentPane(EncodePage);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(800,600);
        setBackground(Color.BLACK);
        setResizable(false);
        setVisible(true);
        dialog = new JDialog(this);
        dialogMsg = new JLabel();
        dialogMsg.setHorizontalAlignment(JLabel.CENTER);
        dialog.setSize(300,200);
        dialog.add(dialogMsg);

        for(int i=0;i<30;i++){
            maskLsbZero |= (1<<i);
        }
        maskLsbZero=maskLsbZero<<2;

        EncodeBtn.setEnabled(false);
        SaveBtn.setEnabled(false);
        ResetBtn.setEnabled(false);


        menuButton.addActionListener(e -> {
            dispose();
            Main.main(null);
        });
        ImgSrcBtn.addActionListener(e -> {
            String user =  System.getProperty("user.name");
            JFileChooser FileChooser = new JFileChooser(new File("C:\\Users\\"+user+"\\Desktop"));
            FileNameExtensionFilter filter = new FileNameExtensionFilter("Images (jpg, jpeg, png)", "jpg", "jpeg", "png");
            FileChooser.addChoosableFileFilter(filter);
            FileChooser.setAcceptAllFileFilterUsed(false);
            int res = FileChooser.showOpenDialog(null);
            if(res == JFileChooser.APPROVE_OPTION){
                if(FileChooser.getSelectedFile().exists()){
                    encodeFlag=res;
                    EncodeBtn.setEnabled(true);
                    ResetBtn.setEnabled(true);
                    FilePath = FileChooser.getSelectedFile().getAbsolutePath();
                    img = new ImageIcon(FilePath);
                    SrcImgLabel.setText("");
                    Image temp = img.getImage();
                    ImageIcon labelImg = new ImageIcon(temp.getScaledInstance(SrcImgPanel.getWidth(),SrcImgPanel.getHeight(),Image.SCALE_SMOOTH));
                    SrcImgLabel.setIcon(labelImg);
                    SaveBtn.setEnabled(false);
                }
                else{
                    dialogMsg.setText("enter valid file location");
                    dialog.setVisible(true);
                }
            }
            else{
                if(encodeFlag == JFileChooser.CANCEL_OPTION){
                    EncodeBtn.setEnabled(false);
                    ResetBtn.setEnabled(false);
                    SaveBtn.setEnabled(false);
                }
            }
        });
        ResetBtn.addActionListener(e -> {
            encodeFlag = 1;
            EncodeBtn.setEnabled(false);
            ResetBtn.setEnabled(false);
            SaveBtn.setEnabled(false);
            SrcImgLabel.setIcon(null);
            DesImgLabel.setIcon(null);
            SteganoImg = null;
            format = null;
        });
        EncodeBtn.addActionListener(e -> {
            String msgToBeEncoded = msg.getText().trim();
            if(msgToBeEncoded.length()>(img.getIconHeight()* img.getIconWidth())){
                dialogMsg.setText("msg length greater than image size");
                dialog.setVisible(true);
            }
            else if(msgToBeEncoded.isEmpty()){
                dialogMsg.setText("enter msg in msg field");
                dialog.setVisible(true);
            }
            else if (!isAlphaNum(msgToBeEncoded)) {
                dialogMsg.setText("only numbers, letters and spaces are allowed");
                dialog.setVisible(true);
            } else{
                try {
                    SteganoImg = ImageIO.read(new File(FilePath));
                } catch (IOException ex) {
                    throw new RuntimeException(ex);
                }
                int loopFlag=0;
                char temp;
                SaveBtn.setEnabled(true);
                for(int x=0;x<SteganoImg.getWidth();x++){
                    for(int y=0;y<SteganoImg.getHeight();y++){
                        if(msgToBeEncoded.length()<x+y-1){
                            loopFlag=1;
                            break;
                        }
                        int pixel = SteganoImg.getRGB(x,y);
                        Color color = new Color(pixel);

                        int red = color.getRed();
                        int green = color.getGreen();
                        int blue = color.getBlue();


                        red = red & maskLsbZero;
                        green = green & maskLsbZero;
                        blue = blue & maskLsbZero;

                        if(x+y==0)
                            temp = '\'';
                        else if (x+y<=msgToBeEncoded.length()) {
                            temp = msgToBeEncoded.charAt(x+y-1);
                        } else
                            temp = '\'';


                        int tempNum = Arrays.binarySearch(arr,temp);

                        int bitMask = (tempNum &(0b00110000))>>4;
                        red |= bitMask;
                        bitMask = (tempNum &(0b00001100))>>2;
                        green |= bitMask;
                        bitMask = (tempNum &(0b00000011));
                        blue |= bitMask;
                        color = new Color(red,green,blue);
                        SteganoImg.setRGB(x,y,color.getRGB());


                    }
                    if(loopFlag==1){
                        break;
                    }
                }
                ImageIcon labelImg = new ImageIcon(SteganoImg.getScaledInstance(DesImgPanel.getWidth(),DesImgPanel.getHeight(),Image.SCALE_SMOOTH));
                DesImgLabel.setIcon(null);
                DesImgLabel.setIcon(labelImg);

            }
        });
        SaveBtn.addActionListener(e -> {
            String user =  System.getProperty("user.name");
            JFileChooser FileChooser = new JFileChooser(new File("C:\\Users\\"+user+"\\Desktop"));
            FileNameExtensionFilter filter = new FileNameExtensionFilter("Images (jpg, jpeg, png)", "jpg", "jpeg", "png");
            FileChooser.addChoosableFileFilter(filter);
            FileChooser.setAcceptAllFileFilterUsed(false);
            int res = FileChooser.showSaveDialog(null);
            if(res==JFileChooser.APPROVE_OPTION){
                File output = FileChooser.getSelectedFile();
                String name = output.getName();
                try{
                    format = name.substring(name.lastIndexOf("."));
                    format = format.substring(1);
                    if(!(Objects.equals(format, "png") || Objects.equals(format, "jpeg") || Objects.equals(format, "jpg"))){
                        dialogMsg.setText("not an valid image format");
                        dialog.setVisible(true);
                    }
                }
                catch (Exception ex){
                    format = "png";
                    FileChooser.setSelectedFile(new File(FileChooser.getSelectedFile().getAbsolutePath().concat(".png")));

                    output = FileChooser.getSelectedFile();
                }
                try {
                    ImageIO.write(SteganoImg, format, new File(output.getAbsolutePath()));
                    SteganoImg = ImageIO.read(output);
                } catch (IOException ex) {
                    dialogMsg.setText("Failed to save image!");
                    dialog.setVisible(true);
                }
            }
        });
    }
}