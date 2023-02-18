import java.util.*;
import java.lang.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import java.io.*;
import java.awt.image.*;
import javax.imageio.*;
public class DisplayBoard extends JPanel implements KeyListener {
    private JFrame frame;
    private Color DEFAULT_COLOR = Color.BLACK;
    private Color BORDER_COLOR = Color.WHITE;
    private int DEFAULT_SCALE = 25;
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
    public boolean auto_restart = false;
    private boolean[] modifiers;
    private String[] modifierNames;
    public DisplayBoard() {
        this(new Snake(),0,0);
    }
    public DisplayBoard(Game game) {
        this(game,0,0);
    }
    public DisplayBoard(Game game,  int margin_w, int margin_h) {
        try {
            System.setProperty("apple.laf.useScreenMenuBar", "true");
            System.setProperty("com.apple.mrj.application.apple.menu.about.name", "Snake");
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch(Throwable t) {
            System.out.println("ERROR: "+t);
        }
        this.game = game; this.margin_w = margin_w; this.margin_h = margin_h;
        this.scale = DEFAULT_SCALE;
        this.modifiers = game.getModifiers();
        this.modifierNames = game.getModifierNames();
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
    private void setup() {
        frame = new JFrame(game.getName());
        frame.setFocusable(true);
        frame.requestFocusInWindow();
        frame.addKeyListener(this);
        //System.out.println("MARGIN: "+margin_w+" "+margin_h);
        frame.setPreferredSize(new Dimension((game.getBoard()[0].length)*(scale+1)+1+2*margin_w,  (game.getBoard().length)*(scale+1)+23+2*margin_h));
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        {
            JMenuBar menuBar = new JMenuBar();
            JMenu menu = new JMenu("Options");
            menu.setMnemonic(KeyEvent.VK_A);
            menu.getAccessibleContext().setAccessibleDescription("Game and display options");
            menuBar.add(menu);
            //JMenuItem menuItem = new JMenuItem("A text-only menu item",KeyEvent.VK_T);
            //menuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_1, ActionEvent.ALT_MASK));
            //menuItem.getAccessibleContext().setAccessibleDescription("This doesn't really do anything");
            // menu.add(menuItem);
            // menuItem = new JMenuItem("Both text and icon", new ImageIcon("images/middle.gif"));
            // menuItem.setMnemonic(KeyEvent.VK_B);
            //menu.add(menuItem);
            // menuItem = new JMenuItem(new ImageIcon("images/middle.gi"));
            // menuItem.setMnemonic(KeyEvent.VK_D);
            // menu.add(menuItem);
            //menu.addSeparator();
            //ButtonGroup group = new ButtonGroup();
            if (modifiers != null && modifiers.length != 0) {
                JRadioButtonMenuItem rb;
                ButtonGroup group = null;
                for (int i=0;i<modifiers.length;i++) {
                    boolean create = true;
                    if (modifierNames[i].equals(">")) {
                        create = false;
                        group = null;
                    }
                    if (modifierNames[i].contains("--")) {
                        create = false;
                        menu.addSeparator();
                    }
                    if (modifierNames[i].contains("<")) {
                        create = false;
                        group = new ButtonGroup();
                    }
                    if (create) {
                        rb = new JRadioButtonMenuItem(modifierNames[i]);
                        rb.setSelected(modifiers[i]);
                        final JRadioButtonMenuItem rb1 = rb;
                        final int i1 = i;
                        rb.addItemListener(new ItemListener() {
                            @Override
                            public void itemStateChanged(ItemEvent e) {
                                if (e.getStateChange()==ItemEvent.SELECTED)  {
                                    modifiers[i1] = true;
                                } else if (e.getStateChange()==ItemEvent.DESELECTED) {
                                    modifiers[i1] = false;
                                }
                                game.setModifiers(modifiers);
                            }
                        });
                        rb.addActionListener(new ActionListener() {
                         
                            @Override
                            public void actionPerformed(ActionEvent event) {
                                modifiers[i1] = rb1.isSelected();
                                game.setModifiers(modifiers);
                            }
                        });
                    
                        if (group != null) {
                            group.add(rb);
                        }
                        menu.add(rb);
                    }
                }
             }


   //          rbMenuItemWrap = new JRadioButtonMenuItem("Wrapping");
   //          rbMenuItemWrap.setSelected(false);
   //          rbMenuItemWrap.addActionListener(new ActionListener() {
             
            //     @Override
            //     public void actionPerformed(ActionEvent event) {
            //          if (game instanceof WrappingGame) {
            //          WrappingGame g = (WrappingGame) game;
            //          wrapping = rbMenuItemWrap.isSelected();
            //          g.setWrap(wrapping);
            //      }
            //     }
            // });
   //          //rbMenuItem.setMnemonic(KeyEvent.VK_R);
   //          //group.add(rbMenuItem);
   //          menu.add(rbMenuItemWrap);
            JRadioButtonMenuItem rbMenuItemRestart = new JRadioButtonMenuItem("Auto Restart");
            rbMenuItemRestart.setSelected(false);
            rbMenuItemRestart.addActionListener(new ActionListener() {
             
                @Override
                public void actionPerformed(ActionEvent event) {
                    auto_restart = rbMenuItemRestart.isSelected();
                }
            });
            // group.add(rbMenuItemRestart);
            menu.add(rbMenuItemRestart);
            // rbMenuItem = new JRadioButtonMenuItem("Wrapping");
            // rbMenuItem.setSelected(false);
            // group.add(rbMenuItem);
            // menu.add(rbMenuItem);
            // rbMenuItem = new JRadioButtonMenuItem("Wrapping");
            // rbMenuItem.setSelected(false);
            // group.add(rbMenuItem);
            // menu.add(rbMenuItem);
            frame.setJMenuBar(menuBar);
        }
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
                    glassPanel.add(title_panel);
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
                    glassPanel.add(score_panel);

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
                    glassPanel.add(block_panel);
                }
                // block_label.setSize(new Dimension(frame.getWidth(),30));
                // score_label.setSize(new Dimension(frame.getWidth(),30));
                // game_over_label.setSize(new Dimension(frame.getWidth(),30));
                
                
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
                JButton restart=new JButton("Restart");  
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
        if (auto_restart) {
            game.restart();
            return;
        }
        if (game instanceof ScoredGame)
            setScoreLabel();
     //    if (game instanceof Java_2048)
     //     setBlockLabel();
        frame.getGlassPane().setVisible(true);
    }
    public void paintComponent(Graphics g) {
        // int centering_constant = 20;
        // System.out.println(frame.getWidth()+" " +frame.getHeight());
        // double centering_weight = 0.46;
        // fixedAreaBetweenPanes.setSize(new Dimension(0,frame.getHeight()/2+90));
        // block_label.setSize(new Dimension((int)(frame.getWidth()*centering_weight)-centering_constant,30));
        // score_label.setSize(new Dimension((int)(frame.getWidth()*centering_weight)-centering_constant,30));
        // game_over_label.setSize(new Dimension((int)(frame.getWidth()*centering_weight)-centering_constant,30));
        super.paintComponent(g);
        setScale();
        Graphics2D g2d = (Graphics2D) g;
        if (game instanceof ImageBoard) {
            String[][] board = ((ImageBoard) game).getImages();
            BufferedImage image;
            String file_name;
            for (int r = 0;r<board.length;r++) {
                for (int c = 0;c<board[0].length;c++) {
                    g2d.setColor(BORDER_COLOR);
                    g2d.fillRect(c*(scale+1)+margin_w, (game.getBoard().length-r-1)*(scale+1)+margin_h, scale+2, scale+2);
                    if (board[r][c] == null) {
                        g2d.setColor(DEFAULT_COLOR);
                        g2d.fillRect(c*(scale+1)+1+margin_w, (game.getBoard().length-r-1)*(scale+1)+1+margin_h, scale, scale);
                    } else {
                        file_name = board[r][c];
                        try {
                            image = ImageIO.read(new File(file_name));
                            g2d.drawImage(image,c*(scale+1)+1+margin_w, (game.getBoard().length-r-1)*(scale+1)+1+margin_h,scale,scale,this);
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
        game.update(e);
    }
    public void keyTyped(KeyEvent e) {
        /* Do nothing */
    }
    public void keyReleased(KeyEvent e) {
        /* Do nothing */
    }
}

