import java.util.*;
import java.lang.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import java.io.*;
import java.awt.image.*;
import javax.imageio.*;
public class OldDisplayBoard extends JPanel implements KeyListener {
    private JFrame frame;
    private Color DEFAULT_COLOR = Color.BLACK;
    private Color BORDER_COLOR = Color.WHITE;
    private int DEFAULT_SCALE = 100;
    private int margin_w;
    private int margin_h;
    private int frame_h;
    private int frame_w;
    private int scale;
    private int high_score;
    private JLabel score_label;
    private JLabel game_over_label;
    private JLabel block_label;
    private Component fixedAreaBetweenPanes;
    private Game game;
    private JPanel glassPanel;
    private boolean hasBorder = false;
    private HashMap<String, BufferedImage> allImages;
    private boolean firstTimePainting = true;
    public OldDisplayBoard() {
        this(new Pacman(),0,0);
    }
    public OldDisplayBoard(Game game) {
        this(game,0,0);
    }
    public OldDisplayBoard(Game game,  int margin_w, int margin_h) {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch(Throwable t) {
            System.out.println("ERROR: "+t);
        }
        this.game = game; this.margin_w = margin_w; this.margin_h = margin_h;
        this.scale = DEFAULT_SCALE;
        setup();
    }
    private void setBlockLabel() {
        if (game instanceof Java_2048) {
            Java_2048 g = (Java_2048) game;
            block_label.setText("Greatest Block: "+g.getHighBlock());
        }
    }
    private void setScoreLabel() {
        if (game instanceof ScoredGame) {
            ScoredGame g = (ScoredGame) game;
            high_score = Math.max(g.getScore(), high_score);
            score_label.setText("Score: "+g.getScore()+"       High Score: "+high_score);
        }
    }
    private void loadImages(String s) {
        File dir = new File(s);
        File[] directoryListing = dir.listFiles();
        try {
            if (directoryListing != null) {
                for (File child : directoryListing) {
                   allImages.put(child.getName(), ImageIO.read(child));
                }
            } else {
                System.out.println("ERROR LOADING IMAGE FILES");
            }
        } catch(Throwable t) {
            System.out.println("ERROR LOADING IMAGES");
        }
    }
    private void setup() {
        this.allImages = new HashMap<String,BufferedImage>();
        if (game instanceof ImageBoard) {
            loadImages(((ImageBoard)game).getImageFolder());
        }
        frame = new JFrame(game.getName());
        frame.setFocusable(true);
        frame.requestFocusInWindow();
        frame.addKeyListener(this);
        //System.out.println("MARGIN: "+margin_w+" "+margin_h);
        if (hasBorder) {
            frame.setPreferredSize(new Dimension((game.getBoard()[0].length)*(scale+1)+1+2*margin_w,  (game.getBoard().length)*(scale+1)+23+2*margin_h));
        } else {
            frame.setPreferredSize(new Dimension((game.getBoard()[0].length)*(scale),  (game.getBoard().length)*(scale)));
        }
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.getContentPane().add(this);
        {
            glassPanel = new JPanel()
            {
                protected void paintComponent(Graphics g)
                {
                    g.setColor( getBackground() );
                    g.fillRect(0, 0, getWidth(), getHeight());
                    super.paintComponent(g);
                }
            };
            glassPanel.setOpaque(false); // background of parent will be painted first
            glassPanel.setBackground( new Color(125,20,20, 180) );
            glassPanel.setLayout(new BoxLayout(glassPanel, BoxLayout.Y_AXIS));
            //glassPanel.setBounds(frame.getWidth()/2,frame.getHeight()/2,200,100);
            // glassPanel.setBackground(new Color(125, 125, 125, 0));
            // glassPanel.setBounds(frame.getWidth()/2+50,frame.getHeight()/2, 200,200);
            //JPanel titlePane = new JPanel();
            // titlePane.setLayout(new BoxLayout(titlePane, BoxLayout.PAGE_AXIS));
            JPanel title_panel = new JPanel();
            JPanel score_panel = new JPanel();
            JPanel block_panel = new JPanel();
            title_panel.setOpaque(false);
            score_panel.setOpaque(false);
            block_panel.setOpaque(false);
            {
                {
                    title_panel.setLayout(new FlowLayout(FlowLayout.CENTER));
                    game_over_label = new JLabel("Game Over", SwingConstants.CENTER);
                    //Dimension size = game_over_label.getPreferredSize();
                    //game_over_label.setBounds(frame.getWidth()/2,frame.getHeight()/2, size.width, size.height);
                    game_over_label.setFont(new Font("Arial",Font.BOLD,30));
                    game_over_label.setForeground(Color.RED);
                    game_over_label.setHorizontalAlignment(SwingConstants.CENTER);
                    title_panel.add(game_over_label);
                }
                if (game instanceof ScoredGame) {
                    score_panel.setLayout(new FlowLayout(FlowLayout.CENTER));
                    ScoredGame g = (ScoredGame) game;
                    high_score = Math.max(g.getScore(),high_score);
                    score_label = new JLabel("Score: "+g.getScore()+"       High Score: "+high_score, SwingConstants.CENTER);
                    //Dimension size = game_over_label.getPreferredSize();
                    //game_over_label.setBounds(frame.getWidth()/2,frame.getHeight()/2, size.width, size.height);
                    score_label.setFont(new Font("Arial",Font.BOLD,20));
                    score_label.setForeground(Color.RED);
                    score_label.setHorizontalAlignment(SwingConstants.CENTER);
                    score_panel.add(score_label);
                }
                if (game instanceof Java_2048) {
                    block_panel.setLayout(new FlowLayout(FlowLayout.CENTER));
                    Java_2048 g = (Java_2048) game;
                    block_label = new JLabel("Greatest Block: "+g.getHighBlock(), SwingConstants.CENTER);
                    //Dimension size = game_over_label.getPreferredSize();
                    //game_over_label.setBounds(frame.getWidth()/2,frame.getHeight()/2, size.width, size.height);
                    block_label.setFont(new Font("Arial",Font.BOLD,20));
                    block_label.setForeground(Color.RED);
                    block_label.setHorizontalAlignment(SwingConstants.CENTER);
                    block_panel.add(block_label);
                }
                // block_label.setSize(new Dimension(frame.getWidth(),30));
                // score_label.setSize(new Dimension(frame.getWidth(),30));
                // game_over_label.setSize(new Dimension(frame.getWidth(),30));
                glassPanel.add(title_panel);
                glassPanel.add(score_panel);
                glassPanel.add(block_panel);
            }
            JPanel buttonPane = new JPanel();
            //buttonPane.setBorder(BorderFactory.createLineBorder(Color.black));
            buttonPane.setLayout(new FlowLayout(FlowLayout.CENTER));
            {
                JButton quit=new JButton("Quit");
                quit.addActionListener(new ActionListener(){  
                    public void actionPerformed(ActionEvent e){  
                            System.exit(0); 
                        }  
                    });
                quit.setBackground(Color.ORANGE); 
                quit.setOpaque(true); 
                quit.setBorderPainted(false); 
                JButton restart = new JButton("Restart");  
                restart.setBackground(Color.ORANGE);
                //restart.setBounds(frame.getWidth()/2+50,frame.getHeight()/2,80,30);  
                restart.addActionListener(new ActionListener(){  
                    public void actionPerformed(ActionEvent e){
                            glassPanel.setVisible(false);
                            frame.getGlassPane().setVisible(false);
                            game.restart();
                            // game.update(null);
                        }  
                    });
                restart.setOpaque(true);
                restart.setBorderPainted(false);
                buttonPane.add(restart);  
                buttonPane.add(quit); 
                //glassPanel.setBorder(BorderFactory.createEmptyBorder(frame.getWidth()/2,frame.getHeight()/2,100,100));
            }
            buttonPane.setOpaque(false); 
            // titlePane.setOpaque(false); 
            // glassPanel.add(titlePane);
            int between_height = 0;
            fixedAreaBetweenPanes = Box.createRigidArea(new Dimension(0,between_height));
            glassPanel.add(fixedAreaBetweenPanes);
            glassPanel.add(buttonPane);
            glassPanel.setBorder(BorderFactory.createLineBorder(Color.RED, 10));
            //System.out.println(frame.getWidth()/2 + " "+frame.getHeight()/2);
            frame.setGlassPane(glassPanel);
        }
        frame.pack();
        frame_w = frame.getContentPane().getWidth();
        frame_h = frame.getContentPane().getHeight();
        frame.setLocationByPlatform(true);
        frame.setVisible(true);
    }
    public void gameOver() {
        setScoreLabel();
        setBlockLabel();
        frame.getGlassPane().setVisible(true);
    }


    /* NEED TO LOAD IN FILES BEFOREHAND, SO THERE IS NO REPETITION OF LOADING IN */
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        setScale();
        Graphics2D g2d = (Graphics2D) g;
        if (game instanceof ImageBoard) {
            //paint background
            BufferedImage backgroundImage = allImages.get(((ImageBoard) game).getBackgroundImage());
            if (hasBorder) {
                g2d.drawImage(backgroundImage,0,0,(game.getBoard()[0].length)*(scale+1)+1,  (game.getBoard().length)*(scale+1)+1, this);
            } else {
                g2d.drawImage(backgroundImage,0,0,(game.getBoard()[0].length)*(scale)+1,  (game.getBoard().length)*(scale)+1, this);
            }
            String[][] board = ((ImageBoard) game).getImages();
            BufferedImage image;
            String file_name;
            for (int r = 0;r<board.length;r++) {
                for (int c = 0;c<board[0].length;c++) {
                    if (hasBorder) {
                        g2d.setColor(BORDER_COLOR);
                        g2d.fillRect(c*(scale+1)+margin_w, (game.getBoard().length-r-1)*(scale+1)+margin_h, scale+2, scale+2);
                    }
                    if (board[r][c] == null) {
                        continue;
                        // g2d.setColor(DEFAULT_COLOR);
                        // if (hasBorder) {
                        //     g2d.fillRect(c*(scale+1)+1+margin_w, (game.getBoard().length-r-1)*(scale+1)+1+margin_h, scale, scale);
                        // } else {
                        //     g2d.fillRect(c*(scale)+1+margin_w, (game.getBoard().length-r)*(scale)+1+margin_h, scale, scale);
                        // }
                    } else {
                        file_name = board[r][c];
                        try {
                            image = allImages.get(file_name);
                            //image = ImageIO.read(new File(file_name));
                            if (image == null) {
                                //System.out.println("ERROR: FILE NOT LOADED IN");
                                //throw new RuntimeException();
                            } else if (hasBorder) {
                                g2d.drawImage(image,c*(scale+1)+1+margin_w, (game.getBoard().length-r-1)*(scale+1)+1+margin_h,scale,scale,this);
                            } else {
                                g2d.drawImage(image,c*(scale)+1+margin_w, (game.getBoard().length-r-1)*(scale)+1+margin_h,scale,scale,this);
                            }
                        } catch (Throwable t) {
                            System.out.println("ERROR READING FILE "+file_name);
                        }
                    }
                }
            }
        } else {
            Color[][] board = game.getBoard();
            for (int r = 0;r<board.length;r++) {
                for (int c = 0;c<board[0].length;c++) {
                    g2d.setColor(BORDER_COLOR);
                    g2d.fillRect(c*(scale+1)+margin_w, (game.getBoard().length-r-1)*(scale+1)+margin_h, scale+2, scale+2);
                    if (board[r][c] != null) {
                        g2d.setColor(board[r][c]);
                    } else {
                        g2d.setColor(DEFAULT_COLOR);
                    }
                    g2d.fillRect(c*(scale+1)+1+margin_w, (game.getBoard().length-r-1)*(scale+1)+1+margin_h, scale, scale);
                }
            }
        }
        //System.out.println("done painting");
        firstTimePainting = false;
    }
    private void setScale() {
        frame_w = frame.getWidth();
        frame_h = frame.getHeight();
        //(game.getBoard()[0].length)*(scale+1)+1+2*margin_w
        int scale_1 = (int)((frame_w-2*margin_w-1)/((double)game.getBoard()[0].length)-1);
        //(game.getBoard().length)*(scale+1)+23+2*margin_h
        int scale_2 = (int)((frame_h-2*margin_h-23)/((double)game.getBoard().length)-1);
        scale = Math.min(scale_1,scale_2);
    }
    public void keyPressed(KeyEvent e) {
        //System.out.println("Updating game");
        game.update(e);
    }
    public void keyTyped(KeyEvent e) {
        /* Do nothing */
    }
    public void keyReleased(KeyEvent e) {
        /* Do nothing */
    }
}
