import java.util.*;
import java.util.concurrent.*;
import java.lang.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import java.io.*;
import java.awt.image.*;
import javax.imageio.*;
public class DisplayBoard extends JPanel implements KeyListener {
    private long SLEEP_BEFORE_AUTO_RESTART = 1000;
    private JFrame frame;
    private Color DEFAULT_COLOR = Color.BLACK;
    private Color BORDER_COLOR = Color.WHITE;
    private static int DEFAULT_SCALE = 25;
    private int margin_w;
    private int margin_h;
    private int frame_h;
    private int frame_w;
    private int scale;
    private int high_score;
    private boolean paused = false;
    private JLabel score_label;
    private JLabel game_over_label; //text changed in pause menu to "Game Paused"
    private JLabel block_label;
    private JButton continue_button; //only shown in pause menu
    private Component fixedAreaBetweenPanes;
    private Game game;
    private JPanel glassPanel;
    public boolean auto_restart = false;
    public boolean borders = true;
    private boolean[] modifiers;
    private String[] modifierNames;
    public DisplayBoard() {
        this(new Snake(),0,0);
    }
    public DisplayBoard(Game game) {
        this(game, 0, 0, DEFAULT_SCALE);
    }
    public DisplayBoard(Game game, int scale) {
        this(game,0,0, scale);
    }
    public DisplayBoard(Game game, int margin_w, int margin_h) {
        this(game,margin_w,margin_h,DEFAULT_SCALE);
    }
    public DisplayBoard(Game game,  int margin_w, int margin_h, int scale) {
        this.scale = scale;
        try {
            System.setProperty("apple.laf.useScreenMenuBar", "true");
            System.setProperty("com.apple.mrj.application.apple.menu.about.name", "Snake");
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch(Throwable t) {
            System.out.println("ERROR: "+t);
        }
        this.game = game; this.margin_w = margin_w; this.margin_h = margin_h;
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
        setScoreLabel(true);
    }
    private void setScoreLabel(boolean updateHighScore) {
        if (game instanceof ScoredGame) {
            ScoredGame g = (ScoredGame) game;
            if (updateHighScore)
                high_score = Math.max(g.getScore(), high_score);
            score_label.setText("Score: "+g.getScore()+"       High Score: "+high_score);
        }
    }
    private String removeParts(String s) {
        s = s.replaceAll("--","");
        s = s.replaceAll("<","");
        s = s.replaceAll(">","");
        s = s.replaceAll("\\]","");
        s = s.replaceAll("\\[","");
        s = s.replaceAll("|","");
        s = s.replaceAll("\\{","");
        s = s.replaceAll("\\}","");
        s = s.replaceAll("=","");
        return s;
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
                JMenu menu = new JMenu("Options");
                JMenu subMenu = null;
                JMenu subSubMenu = null;
                boolean create;
                boolean non_radio;
	            for (int i=0;i<modifiers.length;i++) {
	            	create = true;
                    non_radio = false;
                    if (menu == null) {
                        continue;
                    }
                    if (modifierNames[i].contains("|")) {
                        subMenu.add(subSubMenu);
                        menu.add(subMenu);
                        if (menu.getSubElements().length>0) {
                            menuBar.add(menu);
                        }
                        String s = modifierNames[i];
                        s = removeParts(s);
                        menu = new JMenu(s);
                        continue;
                    }
                    if (modifierNames[i].contains("{")) {
                        String s = modifierNames[i];
                        s = removeParts(s);
                        subMenu = new JMenu(s);
                        create = false;
                    }
                    if (modifierNames[i].contains("}")) {
                        subMenu.add(subSubMenu);
                        menu.add(subMenu);
                        subSubMenu = null;
                        subMenu = null;
                        create = false;
                    }
                    if (modifierNames[i].contains("[")) {
                        if (subMenu != null) {
                            String s = modifierNames[i];
                            s = removeParts(s);
                            subSubMenu = new JMenu(s);
                            create = false;
                        } else {
                            String s = modifierNames[i];
                            s = removeParts(s);
                            subMenu = new JMenu(s);
                            create = false;
                        }
                    }
                    if (modifierNames[i].contains("]")) {
                        if (subSubMenu != null) {
                            subMenu.add(subSubMenu);
                            subSubMenu = null;
                            create = false;
                        } else if (subMenu != null) {
                            menu.add(subMenu);
                            subMenu = null;
                            create = false;
                        }
                    }
	            	if (modifierNames[i].contains(">")) {
	            		create = false;
	            		group = null;
	            	}
	            	if (modifierNames[i].contains("--")) {
	            		create = false;
                        if (subSubMenu != null) {
                            subMenu.addSeparator();
                        } else if (subMenu != null) {
                            menu.addSeparator();
                        }
	            	}
	            	if (modifierNames[i].contains("<")) {
	            		create = false;
	            		group = new ButtonGroup();
	            	}
                    if (modifierNames[i].contains("=")) {
                        non_radio = true;
                        modifierNames[i] = removeParts(modifierNames[i]);
                    }
	            	if (create) {
		            	rb = new JRadioButtonMenuItem(modifierNames[i]);
		            	rb.setSelected(modifiers[i]);
		            	final JRadioButtonMenuItem rb1 = rb;
                        final boolean non_rad = non_radio;
		            	final int i1 = i;
                        final JMenuItem jmi = rb;
	            		rb.addItemListener(new ItemListener() {
	            			@Override
	            			public void itemStateChanged(ItemEvent e) {
	            				if (e.getStateChange()==ItemEvent.SELECTED)  {
	            					modifiers[i1] = true;
	            				} else if (e.getStateChange()==ItemEvent.DESELECTED) {
	            					modifiers[i1] = false;
	            				}
	            				game.setModifiers(modifiers);
                                if (non_rad) {
                                    jmi.setSelected(false);
                                }
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
                        if (subSubMenu != null) {
                            subSubMenu.add(rb);
                        } else if (subMenu != null) {
                            subMenu.add(rb);
                        } else {
                            menu.add(rb);
                        }
					}
				}
                menuBar.add(menu);
	         }

            JMenu menuDisplay = new JMenu("View");
            //menu.setMnemonic(KeyEvent.VK_A);
            menuDisplay.getAccessibleContext().setAccessibleDescription("View options");
            menuBar.add(menuDisplay);
   //          rbMenuItemWrap = new JRadioButtonMenuItem("Wrapping");
   //          rbMenuItemWrap.setSelected(false);
   //          rbMenuItemWrap.addActionListener(new ActionListener() {
			 
			//     @Override
			//     public void actionPerformed(ActionEvent event) {
			//  		if (game instanceof WrappingGame) {
			//     		WrappingGame g = (WrappingGame) game;
			//     		wrapping = rbMenuItemWrap.isSelected();
			//     		g.setWrap(wrapping);
			//     	}
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
            menuDisplay.add(rbMenuItemRestart);
            menuBar.add(menuDisplay);
            // JMenu menuPopUp = new JMenu("POP UP TEST");

            // popup1 = new JPopupMenu();
            // JMenuItem menuItem = new JMenuItem("A popup menu item");
            // //menuItem.addActionListener(this);
            // popup1.add(menuItem);
            // menuItem = new JMenuItem("Another popup menu item");
            // //menuItem.addActionListener(this);
            // popup1.add(menuItem);

            // //Add listener to components that can bring up popup menus.
            // MouseListener popupListener = new PopupListener();
            // menuPopUp.addMouseListener(popupListener);
            // menuBar.addMouseListener(popupListener);
            // menuPopUp.add(popup1);
            // menuBar.add(menuPopUp);
            
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
                continue_button=new JButton("Continue");  
                continue_button.setBackground(Color.ORANGE);
                //restart.setBounds(frame.getWidth()/2+50,frame.getHeight()/2,80,30);  
                continue_button.addActionListener(new ActionListener(){  
                    public void actionPerformed(ActionEvent e){
                            pause();
                            // game.update(null);
                        }  
                    });
                continue_button.setOpaque(true);
                continue_button.setBorderPainted(false);
                buttonPane.add(continue_button); 
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
                            paused = false;
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
    public void pause() {
        if (frame.getGlassPane().isVisible() && !paused) {
            return;
        }
        game.pause();
        if (paused==true) {
            paused = false;
            frame.getGlassPane().setVisible(false);
            return;
        }
        paused = true;
        if (game instanceof ScoredGame)
            setScoreLabel(false);
        if (game instanceof Java_2048)
            setBlockLabel();
        continue_button.setVisible(true);
        game_over_label.setText("Game Paused");
        frame.getGlassPane().setVisible(true);
    }
    public void gameOver() {
        if (game instanceof ScoredGame)
            setScoreLabel();
        if (game instanceof Java_2048)
            setBlockLabel();
    	if (auto_restart) {
    		game.restart();
            try {
                TimeUnit.MILLISECONDS.sleep(SLEEP_BEFORE_AUTO_RESTART);
            } catch(Throwable t) {
                ;
            }
    		return;
    	}
        paused = false;
        continue_button.setVisible(false);
        game_over_label.setText("Game Over");
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
        if (e.getKeyCode()==KeyEvent.VK_ESCAPE) {
            pause();
        }
        if (!paused) {
            game.update(e);
        }
    }
    public void keyTyped(KeyEvent e) {
    	/* Do nothing */
    }
    public void keyReleased(KeyEvent e) {
        /* Do nothing */
    }
}

