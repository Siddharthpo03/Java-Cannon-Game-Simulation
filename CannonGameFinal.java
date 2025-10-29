import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.awt.Color;
import java.util.Random;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.image.BufferedImage;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import java.io.File;


class FireEffect {
    private int x, y;
    private int life = 40; // Duration of fire effect in frames
    private Color[] colors = {Color.ORANGE, Color.RED, Color.YELLOW, Color.BLUE, Color.WHITE};

    public FireEffect(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public void draw(Graphics2D g) {
        if (life > 0) {
            g.setColor(colors[(int) (Math.random() * colors.length)]);
            int size = (int) (Math.random() * 30) + 20;
            g.fillOval(x - size / 2, y - size / 2, size, size);
            life--;
        }
    }

    public boolean isDone() {
        return life <= 0;
    }
}

class Ball {
    private double x;
    private double y;
    private int size;
    private int power;
    private double diameter;

    private double speedX;
    private double speedY;

    private double velocity;
    private Color color;

    private double velocityX, velocityY;

    //----------------------------------------------------------//

    public Ball(int x, int y, int diameter, int speedX, int speedY, Color color, int size, int power) {
        this.x = (double) x;
        this.y = (double) y;
        this.diameter = (double) diameter;
        this.speedX = (double) speedX/4.5;
        this.speedY = (double) ((speedY/4.5) * -1);
        this.color = color;
        this.size = size;
        this.power = power;
        this.velocity = this.speedY;

    }

    public int getX() {
        return (int) x;
    }
    public int getY() {
        return (int) y;
    }
    public int getDiameter() {
        return (int) diameter;
    }
    public Color getColor() {
        return color;
    }

    public int getSize() {
        return size;
    }

    public int getPower() {
        return power;
    }
    public double getVelocityX() {
        return velocityX;
    }
    public void setVelocityX( double velocityX) {
        this.velocityX = velocityX;
    }

    public double getVelocityY() {
        return velocityY;
    }
    public void setVelocityY(double velocityY) {
        this.velocity = velocityY;
    }

    public void update() {

        //-------- X ----------//

        x += velocityX;
        y += velocityY;

        x = x + speedX;
        speedX = speedX * 0.996; //Air resistance

            //If it hits the walls reverse
            if(x > GamePanel.WIDTH-diameter || x < 0){
                speedX = speedX * -1;
            }

            //If it is slow enough stop
            if(speedX < 0 && speedX > -0.1 || speedX > 0 && speedX < 0.1){
                speedX = 0;
            }

            //If the ball is done bouncing, add more resistance
            if(velocity < 0.1 && y==GamePanel.HEIGHT-diameter){
                speedX = speedX * 0.996;
            }

            //-------- Y ----------//

            velocity = velocity * 0.999; //Air resistance
            velocity = velocity + 0.4; //Gravity
            y = y + velocity;

            // if it hits the bottom
            if(y+diameter >= GamePanel.HEIGHT){
                velocity=velocity * -1+0.55;
                //If it won't be able to bounce back
                if(velocity + 0.4 + y + diameter >= GamePanel.HEIGHT){
                    y = GamePanel.HEIGHT - diameter;
                }
            }
        }

    public double getRadius(){
        return this.diameter / 2;
    }
}

class Cannon_1 {

    private int fireButtonX = 105;
    private int fireButtonY = 350;
    private int fireButtonWidth = 100;
    private int fireButtonHeight = 50;
    private int clearButtonX = 320;
    private int clearButtonY = 25;
    private int clearButtonWidth = 100;
    private int clearButtonHeight = 40;

    private int colorSelectionX = 50;
    private int colorSelectionY = 75;
    private int colorBoxWidth = 20;
    private Color colorSelected = new Color(85, 85, 85);

    private int diameter = 100;
    private int width = 300;

    private int x = 90;
    private int y = GamePanel.HEIGHT - diameter - 50;

    private int ballX;
    private int ballY;

    private int angle;
    private int size;
    private int power;

    private ArrayList<Ball> balls = new ArrayList<Ball>();

    //----------------------------------------------------------//

    public Cannon_1() {
    }

    public Graphics2D draw(Graphics2D g, int angle, int size, int power) {

        this.angle = angle;
        this.size = size;
        this.power = power;

        g = drawBalls(g);
        g = drawCannon(g);
        g = drawButtons(g);
        g = drawColorSelection(g);

        return g;
    }

    private Graphics2D drawCannon(Graphics2D g) {

        diameter = size + 50;

        //-------- Curve Cannon Barrel ----------//
        int barrelLength = width;
        int curveDepth = diameter / 100; // Adjust Depth Of The Curve As Needed

        //------- Start And End Points Of The Barrel -------//
        int x1 = x;
        int y1 = y + diameter;
        int x2 = x + barrelLength;
        int y2 = y + diameter;

        //------ Control point to create the curve (adjust to modify the curve shape) -------//
        int controlX = x + barrelLength / 2;


        //-------- Turn Cannon ----------//
        int xPoly[] = {x, x + width, x + width, x};
        int yPoly[] = {y, y, y + diameter,y + diameter};
        int i;
        for (i = 0; i < xPoly.length; i++ ){
            int newXY[] = rotateXY(xPoly[i], yPoly[i], angle, x, y + diameter);
            xPoly[i] = newXY[0];
            yPoly[i] = newXY[1];
        }
        for (i = 0; i < xPoly.length; i++){
            yPoly[i] = yPoly[i] + y + 100 - yPoly[3]; //keeps it fixed to the corner
        }


        // Where the ball will be spawning
        ballX = xPoly[1];
        ballY = yPoly[1];
        ballX += xPoly[2] - xPoly[1] - diameter - 2;

        //Cannon
        g.setColor(Color.BLACK);
        Polygon poly = new Polygon(xPoly, yPoly, xPoly.length);
        g.fillPolygon(poly);
        //Wheel
        g.setColor(new Color(139,69,19));
        g.fillOval(x - 25, GamePanel.HEIGHT - 100, 100, 100);

        return g;
    }

    private Graphics2D drawBalls(Graphics2D g) {

        //If fire button is clicked
        if (GamePanel.click == true && GamePanel.cursorX > fireButtonX && GamePanel.cursorX < fireButtonX + fireButtonWidth && GamePanel.cursorY > fireButtonY && GamePanel.cursorY < fireButtonY + fireButtonHeight) {
            balls.add(new Ball(ballX, ballY, diameter, (int) (((double) power * -1) - ((double) power * -1) / ((double) 157) * (angle * -1)), (int) (((double) power * -1) / ((double) 157) * (angle * -1)), colorSelected, size, power));
            playSound("E:\\Sounds\\cannonfire.wav");
            playSound("E:\\Sounds\\\\metal.wav");
            GamePanel.click = false;
        }

        //If clear button is clicked
        if (GamePanel.click == true && GamePanel.cursorX > clearButtonX && GamePanel.cursorX < clearButtonX + fireButtonWidth && GamePanel.cursorY > clearButtonY && GamePanel.cursorY < clearButtonY + clearButtonHeight) {
            balls.removeAll(balls);
            GamePanel.click = false;
        }

        //Draw balls
        for (int i = 0; i < balls.size(); i++) {
            g.setColor(balls.get(i).getColor());
            g.fillOval(balls.get(i).getX(), balls.get(i).getY(), balls.get(i).getDiameter(), balls.get(i).getDiameter());
            balls.get(i).update();

        }



        return g;
    }

    public void playSound(String soundFilePath) {
        new Thread(() -> {
            try (AudioInputStream audioIn = AudioSystem.getAudioInputStream(new File(soundFilePath))) {
                Clip clip = AudioSystem.getClip();
                clip.open(audioIn);
                clip.start();
            } catch (Exception e) {
                System.out.println("Cannot Find The File...! Use WAV File Only...!");
                System.out.println("Check For The Error In Cannon 1...!");
                e.printStackTrace();
            }
        }).start();
    }

    private Graphics2D drawButtons(Graphics2D g) {

        //fire button
        g.setColor(Color.RED);
        g.fillRect(fireButtonX, fireButtonY, fireButtonWidth, fireButtonHeight);
        g.setColor(Color.BLACK);
        Font f = new Font("Calibri", Font.BOLD, 48);
        g.setFont(f);
        g.drawString("FIRE", fireButtonX + 7, fireButtonY + fireButtonHeight - 10);

        //clear button
        g.setColor(Color.CYAN);
        g.fillRect(clearButtonX, clearButtonY, clearButtonWidth, clearButtonHeight);
        g.setColor(Color.BLACK);
        f = new Font("Calibri", Font.BOLD, 32);
        g.setFont(f);
        g.drawString("CLEAR", clearButtonX + 7, clearButtonY + clearButtonHeight - 10);


        return g;
    }

    private Graphics2D drawColorSelection(Graphics2D g) {

        Color[] boxColors = new Color[]{new Color(85, 85, 85), new Color(3, 61, 180), new Color(255, 0, 0), new Color(27, 137, 60), new Color(255, 177, 14), new Color(164, 73, 164)};
        int padding = 10;
        int menuBoxWidth = (colorBoxWidth * 2 * boxColors.length) + padding * 2;
        int menuBoxHeight = colorBoxWidth + 40;

        // For Menu Box
        g.setColor(new Color(200, 200, 200));
        g.fillRect(colorSelectionX - padding, colorSelectionY - padding - 24, menuBoxWidth, menuBoxHeight);

        g.setColor(Color.BLACK);
        Font f = new Font("Calibri", Font.BOLD, 24);
        g.setFont(f);
        g.drawString("Color", colorSelectionX, colorSelectionY - 12);



        for (int i = 0; i < boxColors.length; i++){
            if (boxColors[i].getRGB() == colorSelected.getRGB()) {
                g.setColor(Color.BLACK); //Draw the black frame around selected color
                g.fillRect(colorSelectionX + colorBoxWidth * i * 2 - 4, colorSelectionY - 4, colorBoxWidth + 8, colorBoxWidth + 8);
            }
            //Draw colors
            g.setColor(boxColors[i]);
            g.fillRect(colorSelectionX + colorBoxWidth * i * 2, colorSelectionY, colorBoxWidth, colorBoxWidth);

            //If the color is clicked select it
            if (GamePanel.click == true && GamePanel.cursorX > (colorSelectionX + colorBoxWidth * i * 2) && GamePanel.cursorX < (colorSelectionX + colorBoxWidth * i * 2) + colorBoxWidth && GamePanel.cursorY > colorSelectionY && GamePanel.cursorY < colorSelectionY + colorBoxWidth) {
                colorSelected = boxColors[i];
                GamePanel.click = false;
            }
        }

        return g;
    }

    private int[] rotateXY(int x, int y, int angle, int cx, int cy) {

        double tempX = x - cx;
        double tempY = y - cy;

        double rotatedX = tempX*Math.cos((double) angle / 100) - tempY*Math.sin((double) angle / 100);
        double rotatedY = tempX*Math.sin((double) angle / 100) + tempY*Math.cos((double) angle / 100);

        x = (int) (rotatedX + cx);
        y = (int) (rotatedY + cy);

        return new int[] {x, y};

    }

    public ArrayList<Ball> getBalls() {
        return balls;
    }
}


class Cannon_2 {

    private int fireButtonX = 1275;
    private int fireButtonY = 350;
    private int fireButtonWidth = 100;
    private int fireButtonHeight = 50;
    private int clearButtonX = 1075;
    private int clearButtonY = 25;
    private int clearButtonWidth = 100;
    private int clearButtonHeight = 40;

    private int colorSelectionX = 1200;
    private int colorSelectionY = 75;
    private int colorBoxWidth = 20;
    private Color colorSelected = new Color(85, 85, 85);

    private int diameter = 100;
    private int width = 300;

    private int x = 1400;
    private int y = GamePanel.HEIGHT - diameter - 50;

    private int ballX;
    private int ballY;

    private int angle;
    private int size;
    private int power;

    private ArrayList<Ball> balls = new ArrayList<Ball>();

    //----------------------------------------------------------//

    public Cannon_2() {
    }

    public Graphics2D draw(Graphics2D g, int angle, int size, int power) {

        this.angle = angle;
        this.size = size;
        this.power = power;

        g = drawBalls(g);
        g = drawCannon(g);
        g = drawButtons(g);
        g = drawColorSelection(g);

        return g;
    }

    private Graphics2D drawCannon(Graphics2D g) {

        diameter = size + 50;

        //-------- Curve Cannon Barrel ----------//
        int barrelLength = width;
        int curveDepth = diameter / 2; // Adjust Depth Of The Curve As Needed

        //------- Start And End Points Of The Barrel -------//
        int x1 = x;
        int y1 = y + diameter;
        int x2 = x + barrelLength;
        int y2 = y + diameter;

        //------ Control point to create the curve (adjust to modify the curve shape) -------//
        int controlX = x + barrelLength / 2;


        //-------- Turn Cannon ----------//
        int [] xPoly = {x, x - width, x - width, x};
        int [] yPoly = {y, y, y + diameter, y + diameter};

        for (int i = 0; i < xPoly.length; i++) {
            int [] newXY = rotateXY(xPoly[i], yPoly[i], -angle, x, y + diameter);
            xPoly[i] = newXY[0];
            yPoly[i] = newXY[1];
        }

        for (int i = 0; i < xPoly.length; i++){
            yPoly[i] = yPoly[i] + y + 100 - yPoly[3]; //keeps it fixed to the corner
        }


        // Where the ball will be spawning
        ballX = xPoly[1];
        ballY = yPoly[1];
        ballX += xPoly[2] - xPoly[1] - diameter + 90;

        //Cannon
        g.setColor(Color.BLACK);
        Polygon poly = new Polygon(xPoly, yPoly, xPoly.length);
        g.fillPolygon(poly);
        //Wheel
        g.setColor(new Color(139,69,19));
        g.fillOval(x - 25, GamePanel.HEIGHT - 100, 100, 100);

        return g;
    }

    private Graphics2D drawBalls(Graphics2D g) {

        //If fire button is clicked
        if (GamePanel.click == true && GamePanel.cursorX > fireButtonX && GamePanel.cursorX < fireButtonX + fireButtonWidth && GamePanel.cursorY > fireButtonY && GamePanel.cursorY < fireButtonY + fireButtonHeight) {
            balls.add(new Ball(ballX, ballY, diameter, (int) (((double) power * 1) - ((double) power * 1) / ((double) 203) * (angle * -1)), (int) (((double) power * 1) / ((double) 203) * (angle * 1)), colorSelected, size, power));
            playSound("E:\\Sounds\\cannonfire.wav");
            playSound("E:\\Sounds\\\\Metal.wav");
            GamePanel.click = false;

        }

        //If clear button is clicked
        if (GamePanel.click == true && GamePanel.cursorX > clearButtonX && GamePanel.cursorX < clearButtonX + fireButtonWidth && GamePanel.cursorY > clearButtonY && GamePanel.cursorY < clearButtonY + clearButtonHeight) {
            balls.removeAll(balls);
            GamePanel.click = false;
        }

        //Draw balls
        for (int i = 0; i < balls.size(); i++) {
            g.setColor(balls.get(i).getColor());
            g.fillOval(balls.get(i).getX(), balls.get(i).getY(), balls.get(i).getDiameter(), balls.get(i).getDiameter());
            balls.get(i).update();
        }
        return g;
    }

    public void playSound(String soundFilePath) {
        new Thread(() -> {
            try (AudioInputStream audioIn = AudioSystem.getAudioInputStream(new File(soundFilePath))) {
                Clip clip = AudioSystem.getClip();
                clip.open(audioIn);
                clip.start();
            } catch (Exception e) {
                System.out.println("Cannot Find The File...! Use WAV File Only...!");
                System.out.println("Error Found In Cannon 2...!");
                e.printStackTrace();
            }
        }).start();
    }

    private Graphics2D drawButtons(Graphics2D g) {

        //fire button
        g.setColor(Color.RED);
        g.fillRect(fireButtonX, fireButtonY, fireButtonWidth, fireButtonHeight);
        g.setColor(Color.BLACK);
        Font f = new Font("Calibri", Font.BOLD, 48);
        g.setFont(f);
        g.drawString("FIRE", fireButtonX + 7, fireButtonY + fireButtonHeight - 10);

        //clear button
        g.setColor(Color.CYAN);
        g.fillRect(clearButtonX, clearButtonY, clearButtonWidth, clearButtonHeight);
        g.setColor(Color.BLACK);
        f = new Font("Calibri", Font.BOLD, 32);
        g.setFont(f);
        g.drawString("CLEAR", clearButtonX + 7, clearButtonY + clearButtonHeight - 10);


        return g;
    }

    private Graphics2D drawColorSelection(Graphics2D g) {

        g.setColor(Color.BLACK);
        Font f = new Font("Calibri", Font.BOLD, 24);
        g.setFont(f);
        g.drawString("Color", colorSelectionX, colorSelectionY - 12);

        Color[] boxColors = new Color[]{new Color(85, 85, 85), new Color(3, 61, 180), new Color(255, 0, 0), new Color(27, 137, 60), new Color(255, 177, 14), new Color(164, 73, 164)};

        int padding = 10;
        int menuBoxWidth = (colorBoxWidth * 2 * boxColors.length) + padding * 2;
        int menuBoxHeight = colorBoxWidth + 40;

        // For Menu Box
        g.setColor(new Color(200, 200, 200));
        g.fillRect(colorSelectionX - padding, colorSelectionY - padding - 24, menuBoxWidth, menuBoxHeight);

        g.setColor(Color.BLACK);
        Font font = new Font("Calibri", Font.BOLD, 24);
        g.setFont(font);
        g.drawString("Color", colorSelectionX, colorSelectionY - 12);


        for (int i = 0; i < boxColors.length; i++){
            if (boxColors[i].getRGB() == colorSelected.getRGB()) {
                g.setColor(Color.BLACK); //Draw the black frame around selected color
                g.fillRect(colorSelectionX + colorBoxWidth * i * 2 - 4, colorSelectionY - 4, colorBoxWidth + 8, colorBoxWidth + 8);
            }
            //Draw colors
            g.setColor(boxColors[i]);
            g.fillRect(colorSelectionX + colorBoxWidth * i * 2, colorSelectionY, colorBoxWidth, colorBoxWidth);

            //If the color is clicked select it
            if (GamePanel.click == true && GamePanel.cursorX > (colorSelectionX + colorBoxWidth * i * 2) && GamePanel.cursorX < (colorSelectionX + colorBoxWidth * i * 2) + colorBoxWidth && GamePanel.cursorY > colorSelectionY && GamePanel.cursorY < colorSelectionY + colorBoxWidth) {
                colorSelected = boxColors[i];
                GamePanel.click = false;
            }
        }

        return g;
    }

    private int[] rotateXY(int x, int y, int angle, int cx, int cy) {

        double tempX = x - cx;
        double tempY = y - cy;

        double rotatedX = tempX*Math.cos((double) angle / 100) - tempY*Math.sin((double) angle / 100);
        double rotatedY = tempX*Math.sin((double) angle / 100) + tempY*Math.cos((double) angle / 100);

        x = (int) (rotatedX + cx);
        y = (int) (rotatedY + cy);

        return new int[] {x, y};

    }

    public ArrayList<Ball> getBalls() {
        return balls;
    }
}

class Cloud {
    private int size;
    private double x;
    private int y;
    private double speed;
    private Random rand = new Random();

    //----------------------------------------------------------//

    public Cloud() {
        resetCloud();
        x = rand.nextInt(GamePanel.WIDTH);
    }

    public Graphics2D draw(Graphics2D g) {
        this.update();
        g.setColor(new Color(204, 204, 204));
        g.fillOval((int)x, y, size, size);
        g.fillOval((int)x + (size / 2) + (size / 6), y - (size / 3), size, size);
        g.fillOval((int)x + size + (size / 3), y, size, size);

        return g;
    }
    void update() {
        x += speed;
        if (x > GamePanel.WIDTH) {
            resetCloud();
        }

    }

    private void resetCloud() {
        y = (int) rand.nextInt((400) + 1);
        size = (int) rand.nextInt(60 - 30) + 30;
        x = ((size * 2.5) + (int) rand.nextInt(400)) * -1; //x is below 0 so that the cloud dosen't immediately respawn
        speed = (Math.random() * 0.5 + 0.25);
    }
}

class GamePanel extends JPanel implements Runnable, MouseListener, MouseMotionListener {


    public void playSound(String soundFilePath) {
        new Thread(() -> {
            try (AudioInputStream audioIn = AudioSystem.getAudioInputStream(new File(soundFilePath))) {
                Clip clip = AudioSystem.getClip();
                clip.open(audioIn);
                clip.start();
            } catch (Exception e) {
                System.out.println("File Not Found...! Only WAV Are Accepted...!");
                System.out.println("Error In Game Panel...!");
                e.printStackTrace();
            }
        }).start();
    }



    public static int WIDTH = 1550;
    public static int HEIGHT = 794;
    private ArrayList<FireEffect> fireEffects = new ArrayList<>();

    private Thread thread;
    private boolean running;
    private BufferedImage image;
    private Graphics2D g;
    private int fps = 60;

    private Cannon_1 cannon1 = new Cannon_1();
    private SliderInput angleSlider1 = new SliderInput(50, 155, 157, 0, "Angle");
    private SliderInput sizeSlider1 = new SliderInput(50, 225, 0, 75, "Size");
    private SliderInput powerSlider1 = new SliderInput(50, 295, 150, 0, "Power");

    private Cannon_2 cannon2 = new Cannon_2();
    private String generateSpaces(int spaceCount) {
        StringBuilder spaces = new StringBuilder();
        for (int i = 0; i < spaceCount; i++) {
            spaces.append(" "); // Add one space for each iteration
        }
        return spaces.toString();
    }
    private SliderInput angleSlider2 = new SliderInput(1200, 155, 157, 0, generateSpaces(242) + "Angle");
    private SliderInput sizeSlider2 = new SliderInput(1200, 225, 0, 75, generateSpaces(242) + "Size");
    private SliderInput powerSlider2 = new SliderInput(1200, 295, 150, 0, generateSpaces(242) + "Power");



    private Cloud cloud1 = new Cloud();
    private Cloud cloud2 = new Cloud();
    private Cloud cloud3 = new Cloud();
    private Cloud cloud4 = new Cloud();

    //----------------------------------------------------------//

    public GamePanel() {
        super();
        setPreferredSize(new Dimension(WIDTH, HEIGHT));
        setFocusable(true);
        requestFocus();
        addMouseMotionListener(this);
        addMouseListener(this);
        playSound("E:\\Sounds\\gamestart.wav");

    }

    public void addNotify() {
        super.addNotify();
        if (thread == null) {
            thread = new Thread(this);
            thread.start();
        }
    }

    public void run() {
        running = true;

        image = new BufferedImage(WIDTH, HEIGHT, BufferedImage.TYPE_INT_RGB);
        g = (Graphics2D) image.getGraphics();
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        //fps
        long startTime;
        long URDTimeMillis;
        long waitTime;
        long targetTime = 1000 / fps;

        //-------------------- GAME LOOP -----------------//
        while (running) {
            startTime = System.nanoTime();

            gameRender();
            gameDraw();


            //fps
            URDTimeMillis = (System.nanoTime() - startTime) / 1000000;
            waitTime = targetTime - URDTimeMillis;
            try {
                Thread.sleep(waitTime);
            }
            catch (Exception e) {}

        }
    }


    private void gameRender() {

        //Background and grass
        g.setColor(new Color(197, 244, 243));
        g.fillRect(0, 0, WIDTH, HEIGHT);
        g.setColor(new Color(228, 222, 109));
        g.fillRect(0, HEIGHT - 50, WIDTH, 50);

        //Clouds
        g = cloud1.draw(g);
        g = cloud2.draw(g);
        g = cloud3.draw(g);
        g = cloud4.draw(g);

        //Cannon
        g = cannon1.draw(g, angleSlider1.getValue(), sizeSlider1.getValue(), powerSlider1.getValue());

        g = cannon2.draw(g, angleSlider2.getValue(), sizeSlider2.getValue(), powerSlider2.getValue());

        checkCollisions();
        for(int i = fireEffects.size() - 1; i >= 0; i--) {
            FireEffect fire = fireEffects.get(i);
            fire.draw(g);
            if(fire.isDone()) {
                fireEffects.remove(i);
            }
        }

        //Sliders
        g = angleSlider1.draw(g);
        g = sizeSlider1.draw(g);
        g = powerSlider1.draw(g);

        g = angleSlider2.draw(g);
        g = sizeSlider2.draw(g);
        g = powerSlider2.draw(g);


    }

    private void gameDraw() {
        Graphics g2 = this.getGraphics();
        g2.drawImage(image, 0, 0, null);
        g2.dispose();
    }


    //-------------------- MOUSE LISTENING -----------------//
    static boolean dragging = false;
    static boolean click = false;
    static int cursorX = 0;
    static int cursorY = 0;

    @Override
    public void mouseClicked(MouseEvent e) {
        click = true;
    }

    @Override
    public void mouseDragged(MouseEvent e) {
        dragging = true;
        cursorX = e.getX();
        cursorY = e.getY();
    }

    @Override
    public void mouseMoved(MouseEvent e) {
        dragging = false;
        click = false;
        cursorX = e.getX();
        cursorY = e.getY();
    }

    //EMPTY FUNCTIONS
    @Override
    public void mouseEntered(MouseEvent e) {}
    @Override
    public void mouseExited(MouseEvent e) {}
    @Override
    public void mouseReleased(MouseEvent e) {}
    @Override
    public void mousePressed(MouseEvent e) {}

    private void checkCollisions() {
        ArrayList<Ball> balls1 = cannon1.getBalls();
        ArrayList<Ball> balls2 = cannon2.getBalls();

        // Temporary lists to track balls to be removed
        ArrayList<Ball> toRemoveBalls1 = new ArrayList<>();
        ArrayList<Ball> toRemoveBalls2 = new ArrayList<>();

        // Iterate over each ball and check for collisions
        for (Ball ball1 : balls1) {
            for (Ball ball2 : balls2) {
                int dx = ball1.getX() - ball2.getX();
                int dy = ball1.getY() - ball2.getY();
                double distance = Math.sqrt(dx * dx + dy * dy);
                double radiusSum = ball1.getRadius() + ball2.getRadius();

                if (distance <= radiusSum) {
                    handleCollision(ball1, ball2, toRemoveBalls1, toRemoveBalls2); // Pass the removal lists here
                }
            }
        }

        // Remove the balls from their respective lists
        balls1.removeAll(toRemoveBalls1);
        balls2.removeAll(toRemoveBalls2);
    }

    private void handleCollision(Ball ball1, Ball ball2, ArrayList<Ball> toRemoveBalls1, ArrayList<Ball> toRemoveBalls2) {
        // Define collision reaction here: e.g., stop balls, reverse direction, etc.
        // Example: Simple stop or bounce logic
        int fireX = (ball1.getX() + ball2.getX()) / 2;
        int fireY = (ball1.getY() + ball2.getY()) / 2;

        if (ball1.getSize() > ball2.getSize()) {
            // Ball 1 is larger, remove ball2
            toRemoveBalls2.add(ball2);
        } else if (ball1.getSize() < ball2.getSize()) {
            // Ball 2 is larger, remove ball1
            // Ball 2 has more power, remove ball 1
            toRemoveBalls1.add(ball1);
        } else if(ball1.getPower() < ball2.getPower()) {
            toRemoveBalls2.add(ball2);
        } else if(ball1.getPower() > ball2.getPower()) {
            toRemoveBalls1.add(ball1);
        } else {
            // Sizes are equal, remove both
            toRemoveBalls1.add(ball1);
            toRemoveBalls2.add(ball2);
        }
        fireEffects.add(new FireEffect((ball1.getX() + ball2.getX()) / 2, (ball1.getY() + ball2.getY()) / 2));

        playSound("E:\\Sounds\\pixel brust.wav");
    }

}


class SliderInput {

    private int min;
    private int max;
    private int x;
    private int y;

    private int width = 250;
    private int height = 10;

    private String label;

    private int sliderX;
    private int sliderWidth = 10;
    private int sliderHeight = 30;
    private boolean sliderGrapped = false;

    //----------------------------------------------------------//

    public SliderInput(int x, int y, int min, int max, String label) {
        this.min = min;
        this.max = max;
        this.x = x;
        this.y = y;
        this.label = label;

        this.sliderX = (width / 2) - (sliderWidth / 2);
    }

    public Graphics2D draw(Graphics2D g) {

        if (GamePanel.dragging && GamePanel.cursorX > (sliderX - 10) + x && GamePanel.cursorX < sliderX + (sliderWidth + 10) + x && GamePanel.cursorY > y && GamePanel.cursorY < y + height) {
            sliderGrapped = true;
        }
        if (!GamePanel.dragging) {
            sliderGrapped = false;
        }
        if (sliderGrapped && GamePanel.cursorX > x + (sliderWidth / 2) && GamePanel.cursorX < x + width - 1) {
            sliderX = GamePanel.cursorX - x - (sliderWidth / 2);
        }

        g.setColor(Color.GRAY);
        g.fillRect(x, y, width, height);

        g.setColor(Color.BLACK);
        g.fillRect(sliderX + x, y - (sliderHeight / 3), sliderWidth, sliderHeight);

        g.setColor(Color.BLACK);
        Font f = new Font("Calibri", Font.BOLD, 24);
        g.setFont(f);
        g.drawString(label, (width / 2) - (sliderWidth / 2), y - 12);


        return g;
    }

    public int getValue() {

        return (int) ((double) (sliderX + (sliderWidth / 2)) / (double) width * (double) (max - min));

    }


}


public class CannonGameFinal{

    public static void main(String[] args) {
        JFrame window = new JFrame();
        window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        window.setContentPane(new GamePanel());
        window.pack();
        window.setVisible(true);
        window.setResizable(true);
    }

}
