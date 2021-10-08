package pacman;
import javax.swing.JPanel;
import javax.swing.Timer;
import java.awt.Font;
import java.awt.event.*;
import javax.swing.ImageIcon;
import java.awt.Image;
import java.awt.Graphics;
import java.awt.*;

//import java.awt.event.KeyAdapter;
import java.awt.Dimension;

    


public class Model extends JPanel implements ActionListener 
{
        
        private Dimension d;
        private final Font smallFont = new Font("Arial", Font.BOLD, 14);
        private boolean inGame = false;
        private boolean dying = false;

        private final int BLOCK_SIZE = 24;
        private final int N_BLOCKS = 15;
        private final int SCREEN_SIZE = N_BLOCKS * BLOCK_SIZE;
        private final int MAX_GHOSTS = 12;
        private final int PACMAN_SPEED = 6;

        private int N_GHOSTS = 6;
        private int lives, score;
        private int[] dx, dy;
        private int[] ghost_x,ghost_y,ghost_dx,ghost_dy,ghostspeed;
        //images for the hearts,pacman,and ghost
        private Image heart,ghost;
        private Image up,down,left,right;
        //pacman location
        private int pacman_x,pacman_y,pacman_dx,pacman_dy;
        private int req_dx,req_dy;
        //other details
    
        private final int validspeeds[]={1,2,3,4,6,8};
        private final int maxspeed=6;
        private int currentspeed=3;
        private short [] screendata;
        private Timer timer;
    
        private final short levelData[]={
        19, 18, 18, 18, 18, 18, 18, 18, 18, 18, 18, 18, 18, 18, 22,
        17, 16, 16, 16, 16, 24, 16, 16, 16, 16, 16, 16, 16, 16, 20,
        25, 24, 24, 24, 28, 0, 17, 16, 16, 16, 16, 16, 16, 16, 20,
        0,  0,  0,  0,  0,  0, 17, 16, 16, 16, 16, 16, 16, 16, 20,
        19, 18, 18, 18, 18, 18, 16, 16, 16, 16, 24, 24, 24, 24, 20,
        17, 16, 16, 16, 16, 16, 16, 16, 16, 20, 0,  0,  0,   0, 21,
        17, 16, 16, 16, 16, 16, 16, 16, 16, 20, 0,  0,  0,   0, 21,
        17, 16, 16, 16, 24, 16, 16, 16, 16, 20, 0,  0,  0,   0, 21,
        17, 16, 16, 20, 0, 17, 16, 16, 16, 16, 18, 18, 18, 18, 20,
        17, 24, 24, 28, 0, 25, 24, 24, 16, 16, 16, 16, 16, 16, 20,
        21, 0,  0,  0,  0,  0,  0,   0, 17, 16, 16, 16, 16, 16, 20,
        17, 18, 18, 22, 0, 19, 18, 18, 16, 16, 16, 16, 16, 16, 20,
        17, 16, 16, 20, 0, 17, 16, 16, 16, 16, 16, 16, 16, 16, 20,
        17, 16, 16, 20, 0, 17, 16, 16, 16, 16, 16, 16, 16, 16, 20,
        25, 24, 24, 24, 26, 24, 24, 24, 24, 24, 24, 24, 24, 24, 28
       };
       
        //0=blue 1= left border 2=top border 4=right border 8= bottom border 16 =dots that pacman collects
    
        public Model()
        {
            loadImages();
            initVariables();
            addKeyListener(new TAdapter());
            setFocusable(true);
            initGame();//this starts the game
        }
        
        private void loadImages()
        {
            down = new ImageIcon("C:/Users/SHREYANS/Pictures/java project/down.gif").getImage();
            up = new ImageIcon("C:/Users/SHREYANS/Pictures/java project/up.gif").getImage();
            left = new ImageIcon("C:/Users/SHREYANS/Pictures/java project/left.gif").getImage();
            right = new ImageIcon("C:/Users/SHREYANS/Pictures/java project/right.gif").getImage();
            heart = new ImageIcon("C:/Users/SHREYANS/Pictures/java project/heart.gif").getImage();
            ghost = new ImageIcon("C:/Users/SHREYANS/Pictures/java project/ghost.gif").getImage();
        }
        
        private void initVariables()
        {
            screendata = new short[N_BLOCKS * N_BLOCKS];
            d = new Dimension(400, 400);
            ghost_x = new int[MAX_GHOSTS];
            ghost_dx = new int[MAX_GHOSTS];
            ghost_y = new int[MAX_GHOSTS];
            ghost_dy = new int[MAX_GHOSTS];
            ghostspeed = new int[MAX_GHOSTS];
            dx = new int[4];
            dy = new int[4];
        
            timer = new Timer(40, this);
            timer.start();
        }
        
         private void initGame() 
         {
            lives = 3;
            score = 0;
            initLevel();
            N_GHOSTS = 6;
            currentspeed= 3;
        }
        
        private void initLevel() 
        {
            for (int i = 0; i < N_BLOCKS * N_BLOCKS; i++)
            {
                screendata[i] = levelData[i];
            }
            continueLevel();
        }
        
        private void playGame(Graphics2d g2d)
        {
            if (dying)
            {
                death();
            }
            else
            {
                movePacman();
                drawPacman(g2d);
                moveGhosts(g2d);
                checkMaze();
            }
        }
        public void movePacman()
        {
            int pos;
            short ch;
            if(pacman_x%BLOCK_SIZE == 0 && pacman_y%BLOCK_SIZE == 0)
            {
                pos=pacman_x/BLOCK_SIZE + N_BLOCKS * (int) (pacman_y/BLOCK_SIZE);
                ch=ScreenData(pos);
                if((ch &16)!=0)
                {
                    ScreenData[pos]= (short) (ch & 15);
                    score++;
                }
                if(req_dx!=0 || req_dy!=0)
                {
                    
                }
            }
        }
         private void continueLevel() 
         {

            int dx = 1;
            int random;

            for (int i = 0; i < N_GHOSTS; i++) 
            {

                ghost_y[i] = 4 * BLOCK_SIZE; //start position
                ghost_x[i] = 4 * BLOCK_SIZE;
                ghost_dy[i] = 0;
                ghost_dx[i] = dx;
                dx = -dx;
                random = (int) (Math.random() * (currentspeed + 1));

                if (random > currentspeed) 
                {
                    random = currentspeed;
                }

                ghostspeed[i] = validspeeds[random];
            }
            //pacman ka start position after every death
            pacman_x = 7 * BLOCK_SIZE;  //start position
            pacman_y = 11 * BLOCK_SIZE;
            pacman_dx = 0;    //reset direction move
            pacman_dy = 0;
            req_dx = 0;        // reset direction controls
            req_dy = 0;
            dying = false;
        }
        public void paintComponent(Graphics g)
        {
            super.paintComponent(g);
            Graphics2D g2d = (Graphics2D) g;
            g2d.setColor(Color.black);
            g2d.fillRect(0,0,d.width,d.height);
            
            drawMaze();
            drawScore();
            
            if(inGame)
            {
                playGame(g2d);
            }
            else
            {
                showIntroScreen(g2d);
            }
            
            Toolkit.getDefaultToolkit().sync();
        }
        
        class TAdapter extends KeyAdapter
        {
            public void keyPressed(KeyEvent e)
            {
                 int key = e.getKeyCode();
                 if (inGame) 
                 {
                    if (key == KeyEvent.VK_LEFT) 
                    {
                        req_dx = -1;
                        req_dy = 0;
                    } 
                    else if (key == KeyEvent.VK_RIGHT)
                    {
                        req_dx = 1;
                        req_dy = 0;
                    } 
                    else if (key == KeyEvent.VK_UP)
                    {
                        req_dx = 0;
                        req_dy = -1;
                    } else if (key == KeyEvent.VK_DOWN) 
                    {
                        req_dx = 0;
                        req_dy = 1;
                    } else if (key == KeyEvent.VK_ESCAPE && timer.isRunning()) 
                    {
                     inGame = false;
                    }
                }
                else 
                {
                    if (key == KeyEvent.VK_SPACE)
                    {
                        inGame = true;
                        initGame();
                    }
                }
        }
        
        
        
        
        
            
        
            
            
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    }
    @Override
        public void actionPerformed(ActionEvent e)
        {
        repaint();
        }
}

