import java.awt.*;
import java.awt.event.*;

class Paddle {
    final static int WIDTH = 60;
    final static int HEIGHT = 16;


    final static int R1 = 5;
    final static int R2 = 10;
    final static int R3 = 25;


    int x, y;

    Paddle()
    {
        x = Pong.WIDTH/2;
        y = Pong.HEIGHT - Paddle.HEIGHT/2;
    }

    // Foten
    void draw(Graphics g)
    {
        g.setColor(Color.black);
        g.fillRect(x - Paddle.WIDTH/2, y - Paddle.HEIGHT/2,
                Paddle.WIDTH, Paddle.HEIGHT);
    }



    void move(int newx)
    {
        if (newx < Paddle.WIDTH/2)
            x = Paddle.WIDTH/2;
        else if (newx > Pong.WIDTH - Paddle.WIDTH/2)
            x = Pong.WIDTH - Paddle.WIDTH/2;
        else
            x = newx;
    }
}

class Ball {
    // hur stor bollen är
    final static int WIDTH = 10;
    final static int HEIGHT = 10;

    // hur snabbt bollen kan röra sig upp och ned
    final static int VERTICAL_VELOCITY = 7;

    // positionen av bollen
    int x, y;


    int vx, vy;

    Ball()
    {
        x = Pong.WIDTH/3;
        y = Ball.HEIGHT/3;
        vx = 0;
        vy = 0;
    }

    // Bollen.
    void draw(Graphics g)
    {
        g.setColor(Color.blue);
        g.fillRect(x - Ball.WIDTH/2, y - Ball.HEIGHT/2,
                Ball.WIDTH, Ball.HEIGHT);
    }

    // Klicka för att starta bollen.
    void start_moving()
    {
        vx = 0;
        vy = VERTICAL_VELOCITY;
    }


    // Bollen dör då den träffar marken
    void move_one_step(Paddle Fot)
    {
        x += vx;
        y += vy;


        if (x <= Ball.WIDTH/2 || x >= Pong.WIDTH - Ball.WIDTH/2)
        {

            vx = -vx;
        }
        if (y <= Ball.HEIGHT/2)
        {
            vy = -vy;
        }
        else if (y > Pong.HEIGHT - Ball.HEIGHT/2)
        {

            x = Pong.WIDTH/2;
            y = Ball.HEIGHT/2;
            vx = 0;
            vy = 0;
        }
        else if (y + Ball.HEIGHT/2 > Pong.HEIGHT - Paddle.HEIGHT)
        {
            int px = Fot.x;
            int py = Fot.y;

            if (x >= px - Paddle.R1 && x <= px + Paddle.R1)
            {
                vy = -vy;
            }
            else if (x >= px - Paddle.R2 && x <= px + Paddle.R2)
            {
                vx+= (x > px? 1 : -1);
                vy = -vy;
            }
            else if (x >= px - Paddle.R3 && x <= px + Paddle.R3)
            {
                vx+= (x > px? 2 : -2);
                vy = -vy;
            }
            else if (x + Ball.WIDTH/2 >= px - Paddle.WIDTH/2 && x - Ball.WIDTH/2 <= px + Paddle.WIDTH/2)
            {
                vx+= (x > px? 3 : -3);
                vy = -vy;
            }
        }
    }
}
public class Pong extends Panel implements Runnable {

    // hur stort spelet spelplanet är
    static final int WIDTH = 400;
    static final int HEIGHT = 400;

    // hur många frames per sekund
    final int FRAME_RATE = 60;

    Thread thread;
    Image img;
    Graphics g;

    Ball ball;
    Paddle Fot;

    public void init() {
        ball = new Ball();
        Fot = new Paddle();
        img = createImage(WIDTH,HEIGHT);
        g = img.getGraphics();

        enableEvents(MouseEvent.MOUSE_MOVED | MouseEvent.MOUSE_PRESSED);
    }

    public void start() {
        thread = new Thread(this);
        thread.start();
    }

    public void stop() {
        thread.interrupt();
    }

    public void processMouseMotionEvent(MouseEvent e) {

        Fot.move(e.getX());
    }

    public void processMouseEvent(MouseEvent e) {

        if (e.getID() == MouseEvent.MOUSE_PRESSED) {
            ball.start_moving();
        }
    }

    public void run() {
        try
        {
            while (true)
            {
                ball.move_one_step(Fot);
                g.setColor(Color.green);
                g.fillRect(0, 0, getWidth(), getHeight());
                ball.draw(g);
                Fot.draw(g);
                repaint();
                Thread.sleep(1000/FRAME_RATE);
            }
        }
        catch (InterruptedException ie)
        {
            System.err.print("Interrupted!\n" + ie);
        }
    }

    public void paint(Graphics g) {
        g.drawImage(img, 0, 0, WIDTH, HEIGHT, this);
    }

    public void update(Graphics g) {
        paint(g);
    }

    public static void main(String args[]) {
        Frame frame = new Frame();
        frame.addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowClosing(java.awt.event.WindowEvent e) {
                System.exit(0);
            };
        });

        Pong pong = new Pong();
        pong.setSize(WIDTH, HEIGHT);
        frame.add(pong);
        frame.pack();
        frame.setSize(WIDTH, HEIGHT+20);
        frame.setVisible(true);
        pong.init();
        pong.start();
    }
}
