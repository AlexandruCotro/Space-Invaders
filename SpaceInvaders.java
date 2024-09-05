import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.Random;
import javax.swing.*;

public class SpaceInvaders extends JPanel implements ActionListener, KeyListener {

    class Block {
        int x;
        int y;
        int width;
        int height;
        Image img;
        boolean alive = true;
        boolean used = false;

        Block(int x, int y, int width, int height, Image img) {
            this.x = x;
            this.y = y;
            this.width = width;
            this.height = height;
            this.img = img;
        }
    }

    //board
    int titleSize = 40; // Adjusted tile size for larger resolution
    int rows = 18;
    int columns = 32;
    int boardWidth;
    int boardHeight;

    Image shipImg;
    Image alienImg;
    Image alienCyanImg;
    Image alienMagentaImg;
    Image alienYellowImg;
    ArrayList<Image> alienImgArray;

    //ship
    Block ship;
    int shipWidth = titleSize * 2;
    int shipHeight = titleSize;
    int shipX;
    int shipY;
    int shipVelocityX = titleSize;

    //aliens
    ArrayList<Block> alienArray;
    int alienWidth = titleSize * 2;
    int alienHeight = titleSize;
    int alienX = titleSize;
    int alienY = titleSize;

    int alienRows = 2;
    int alienColumns = 5;  // More aliens due to wider screen
    int alienCount = 0;
    int alienVelocityX = 3;  // Increased alien speed for larger space

    //bullets
    ArrayList<Block> bulletArray;
    int bulletWidth = titleSize / 8;
    int bulletHeight = titleSize / 2;
    int bulletVelocityY = -10;

    Timer gameLoop;
    int score = 0;
    boolean gameOver = false;

    // Constructor modified to accept the new board width and height
    SpaceInvaders(int boardWidth, int boardHeight) {
        this.boardWidth = boardWidth;
        this.boardHeight = boardHeight;
        this.shipX = boardWidth / 2 - shipWidth / 2;
        this.shipY = boardHeight - titleSize * 2;

        setPreferredSize(new Dimension(boardWidth, boardHeight));
        setBackground(Color.black);
        setFocusable(true);
        addKeyListener(this);

        // Load images
        shipImg = new ImageIcon(getClass().getResource("./ship.png")).getImage();
        alienImg = new ImageIcon(getClass().getResource("./alien.png")).getImage();
        alienCyanImg = new ImageIcon(getClass().getResource("./alien-cyan.png")).getImage();
        alienMagentaImg = new ImageIcon(getClass().getResource("./alien-magenta.png")).getImage();
        alienYellowImg = new ImageIcon(getClass().getResource("./alien-yellow.png")).getImage();

        alienImgArray = new ArrayList<>();
        alienImgArray.add(alienImg);
        alienImgArray.add(alienCyanImg);
        alienImgArray.add(alienMagentaImg);
        alienImgArray.add(alienYellowImg);

        ship = new Block(shipX, shipY, shipWidth, shipHeight, shipImg);
        alienArray = new ArrayList<>();
        bulletArray = new ArrayList<>();

        // Game timer
        gameLoop = new Timer(1000 / 60, this);
        createAliens();
        gameLoop.start();
    }

    public void paintComponent(Graphics g){
        super.paintComponent(g);
        draw(g);
    }
    public void draw(Graphics g){
        //ship
        g.drawImage(ship.img, ship.x,ship.y,ship.width,ship.height, null);

        //aliens
        for(int i=0;i<alienArray.size();++i){
            Block alien = alienArray.get(i);
            if(alien.alive){
                g.drawImage(alien.img,alien.x,alien.y,alien.width,alien.height,null);

            }
        }
        //bullets
        g.setColor(Color.white);
        for(int i=0;i<bulletArray.size();++i){
            Block bullet = bulletArray.get(i);
            if(!bullet.used){
//                g.drawRect(bullet.x,bullet.y,bullet.width,bullet.height);
                g.fillRect(bullet.x,bullet.y,bullet.width,bullet.height);
            }
        }

        //score
        g.setColor(Color.white);
        g.setFont(new Font("Arial", Font.PLAIN, 32));
        if(gameOver){
            g.drawString("Game Over: "+ String.valueOf(score), 10,35);

        }else{
            g.drawString(String.valueOf(score), 10,35);
        }


    }
    public void move(){
        //aliens
        for(int i=0;i<alienArray.size();i++){
            Block alien = alienArray.get(i);
            if(alien.alive) {
                alien.x += alienVelocityX;

                //if aliens touch the border
                if(alien.x + alien.width >= boardWidth || alien.x <=0) {
                    alienVelocityX *= -1;
                    alien.x += alienVelocityX * 2;


                    //move all aliens down by one row
                    for (int j = 0; j < alienArray.size(); ++j) {
                        alienArray.get(j).y += alienHeight;

                    }
                  }
                if(alien.y >= ship.y){
                    gameOver = true;
                }
                }
              }
        //bullets
        for(int i=0;i< bulletArray.size();++i){
            Block bullet = bulletArray.get(i);
            bullet.y += bulletVelocityY;
        //bullet colliusion with aliens
        for(int j = 0;j< alienArray.size();++j) {
            Block alien = alienArray.get(j);
            if (!bullet.used && alien.alive && detectCollision(bullet, alien)) {

                bullet.used = true;
                alien.alive = false;
                alienCount--;
                score += 100;

            }
          }
        }
        //clear bullets
        while(bulletArray.size() > 0 && (bulletArray.get(0).used || bulletArray.get(0).y < 0)){
            bulletArray.remove(0);  //removes the first element of the array
        }
        //next level
        if(alienCount == 0){
            //increase the number of aliens in columns and rows by 1
            score += alienColumns * alienRows * 100;//bonus points
            alienColumns = Math.min(alienColumns + 1, columns/2 - 2); //cap column at 16/2 - 2 = 6
            alienRows = Math.min((alienRows) + 1, rows -6); //capr ow at 16-6= 10
            alienArray.clear();
            bulletArray.clear();
            alienVelocityX = 3;
            createAliens();
        }

    }



    public void createAliens(){
        Random random = new Random();
        for(int r =0;r<alienRows;++r){
            int randomImageIndex = random.nextInt(alienImgArray.size());
            for(int c=0;c<alienColumns;++c){

                Block alien = new Block(
                        alienX + c*alienWidth,
                        alienY + r*alienHeight,
                            alienWidth,
                            alienHeight,
                            alienImgArray.get(randomImageIndex)
                );
                alienArray.add(alien);
            }
        }
        alienCount = alienArray.size();
    }

    public boolean detectCollision(Block a, Block b){
        return  a.x < b.x + b.width &&
                a.x + a.width > b.x &&
                a.y < b.y + b.height &&
                a.y + a.height > b.y;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        move();
        repaint();
        if(gameOver){
            gameLoop.stop();
        }

    }

    @Override
    public void keyTyped(KeyEvent e) {

    }

    @Override
    public void keyPressed(KeyEvent e) {
        if(gameOver){//any key to restart{
            ship.x = shipX;
            alienArray.clear();
            bulletArray.clear();
            score =0 ;
            alienVelocityX = 1;
            alienColumns = 3;
            alienRows = 2;
            gameOver = false;
            createAliens();
            gameLoop.start();

        }
        if(e.getKeyCode() == KeyEvent.VK_LEFT && ship.x - shipVelocityX >= 0){
            ship.x -= shipVelocityX;
        }
        else if(e.getKeyCode() == KeyEvent.VK_RIGHT && ship.x + ship.width + shipVelocityX <= boardWidth){
            ship.x += shipVelocityX;
        }

    }

    @Override
    public void keyReleased(KeyEvent e) {
       if(e.getKeyCode() == KeyEvent.VK_SPACE){
            Block bullet = new Block(ship.x + shipWidth*15/32, ship.y,bulletWidth,bulletHeight,null);
            bulletArray.add(bullet);

        }

    }
}
