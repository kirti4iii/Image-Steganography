import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class DecodePage extends JFrame{
    private JButton menuButton;
    private JButton ImgSrcBtn;
    private JButton DecodeBtn;
    private JPanel ImgPanel;
    private JLabel ImgLabel;
    private JPanel DecodePage;
    private final JDialog dialog;
    private final JLabel dialogMsg;
    private String FilePath;
    private BufferedImage img;
    private final char[] arr;

    private final int LsbMask = 0b00000011;

    public DecodePage(){
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
        System.out.println("decode page\n");
        dialog = new JDialog(this);
        dialogMsg = new JLabel();
        JScrollPane scroller = new JScrollPane(dialogMsg, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        dialogMsg.setHorizontalAlignment(JLabel.CENTER);
        dialog.add(scroller);
        dialog.setSize(400,300);
        setTitle("Image Steganographer");
        setContentPane(DecodePage);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(800,600);
        setBackground(Color.BLACK);
        setResizable(false);
        setVisible(true);
        DecodeBtn.setEnabled(false);
        menuButton.addActionListener(e -> {
            dispose();
            SwingUtilities.invokeLater(Main::new);
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
                    FilePath = FileChooser.getSelectedFile().getAbsolutePath();
                    ImageIcon img = new ImageIcon(FilePath);
                    ImgLabel.setText("");
                    Image temp = img.getImage();
                    ImageIcon labelImg = new ImageIcon(temp.getScaledInstance(ImgPanel.getWidth(),ImgPanel.getHeight(),Image.SCALE_SMOOTH));
                    ImgLabel.setIcon(labelImg);
                    DecodeBtn.setEnabled(true);
                }
                else{
                    dialogMsg.setText("enter valid file location");
                    dialog.setVisible(true);
                }
            }
            else{
                DecodeBtn.setEnabled(false);
            }
        });
        DecodeBtn.addActionListener(e -> {
            try {
                img = ImageIO.read(new File(FilePath));
            } catch (IOException ex) {
                throw new RuntimeException(ex);
            }
            StringBuilder msg= new StringBuilder();
            int flag;
            for (int x=0;x<img.getWidth();x++){
                flag=0;
                char c;
                for(int y=0;y<img.getHeight();y++){
                    int charAscii = 0b0;
                    int rgb = img.getRGB(x,y);
                    Color color = new Color(rgb);
                    charAscii |= (color.getRed() & LsbMask);
                    charAscii = charAscii<<2;
                    charAscii = ((color.getGreen() & LsbMask) | charAscii);
                    charAscii = charAscii<<2;
                    charAscii = ((color.getBlue() & LsbMask) | charAscii);
                    c = arr[charAscii];

                    if(c != '\'' && x+y==0){
                        flag=1;
                        dialogMsg.setText("<<No msg found>>");
                        break;
                    }
                    else if(c=='\''){
                        if(x+y!=0){
                            flag=1;
                            dialogMsg.setText(msg.toString());
                            break;
                        }
                    }
                    else{
                        msg.append(c);
                    }
                }
                if(flag==1){
                    break;
                }
            }
            dialog.setVisible(true);
        });
    }

}
