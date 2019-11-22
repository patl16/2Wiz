package com.company;



import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.*;
import java.security.Key;
import java.util.ArrayList;
import java.util.Random;

public class GameFrame extends JPanel implements KeyListener, ActionListener {

    // Class variables
    // State of the Game

    // coordinates of where your avatar is on the map
    int x = 5;
    int y = 5;

    int tilesize = 64;
    private int[][] map;

    private int map_width;
    private int map_height;
    private int tilecount;
    private Tile[] tiles;

    private int mapoffset_x;
    private int mapoffset_y;

    private int scrollanimate_offset_x=0;
    private int scrollanimate_offset_y=0;
    private Timer timer;
    private boolean isscrolling = false;
    private int timer_count = 0;
    private int scrollanimate_dir;
    private boolean respond_to_keys;

    public Image avatar_up;
    public Image avatar_down;
    public Image avatar_left;
    public Image avatar_right;
    public Image bullet_image;

    public int avatar_direction = 1;

    public static int UP = 0;
    public static int DOWN = 1;
    public static int LEFT = 2;
    public static int RIGHT =3;
    public java.util.List<NPC> npcs = new ArrayList();

    public GameFrame()
    {
        super();
        respond_to_keys = true;
        // Create the Timer
        timer = new Timer(2,this);
        timer.start();


        // Load Images
        try {

            avatar_left = ImageIO.read(new File("Player_LView.png"));
            avatar_right = ImageIO.read(new File("Player_RView.png"));
            avatar_up = ImageIO.read(new File("Player_BView.png"));
            avatar_down = ImageIO.read(new File("Player_FView.png"));
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    public void CreateNPCS() {
        Random r = new Random();

        for(int i = 0; i < 50; ++i) {
            NPC n = new NPC();
            n.x = r.nextInt(this.map_width);
            n.y = r.nextInt(this.map_height);
            this.npcs.add(n);
        }

    }

    public void LoadTiles()
    {
        try {
            FileReader fr = new FileReader("tiles.txt");
            BufferedReader br = new BufferedReader(fr);
            String line;
            line = br.readLine();
            tilecount = Integer.parseInt(line);
            tiles = new Tile[tilecount];
            for (int i=0; i < tilecount;i++)
            {
                line = br.readLine();
                String[] parts = line.split(" ");

                tiles[i] = new Tile();
                tiles[i].filename = parts[1];

                try {
                    File pathToFile = new File(tiles[i].filename);
                    tiles[i].img = ImageIO.read(pathToFile);
                } catch (IOException ex) {
                    ex.printStackTrace();
                }


                if (parts[2].equals("true"))
                    tiles[i].obstacle = true;
                else
                    tiles[i].obstacle = false;

            }




        }
        catch (FileNotFoundException fnfe)
        {

        }
        catch (IOException ex)
        {

        }
    }

    public void LoadMap()
    {
        try {
            FileReader fr = new FileReader("map.txt");
            BufferedReader br = new BufferedReader(fr);
            String line;
            line = br.readLine();
            map_width = Integer.parseInt(line);
            line = br.readLine();
            map_height = Integer.parseInt(line);
            map = new int[map_height][map_width];
            int row = 0;
            while ((line = br.readLine()) != null)
            {
                String[] parts = line.split(" ");
                for (int col =0; col < parts.length; col++)
                {
                    map[row][col] = Integer.parseInt(parts[col]);
                }
                row++;
            }
            row = 0;


        }
        catch (FileNotFoundException fnfe)
        {
            System.out.println("Can't find map.txt!");


        }
        catch (IOException ioe)
        {
            System.out.println("Error reading map.txt");
        }

    }
    public boolean isObstacle(int x, int y)
    {
        if (x < 0 || x >= map_width || y<0 || y>= map_height) return true;

        return tiles[map[y][x]].obstacle;
    }


    @Override
    public void keyTyped(KeyEvent e) {

    }

    @Override
    public void keyPressed(KeyEvent e)
    {
        if (respond_to_keys == false) return;

        if (e.getKeyCode() == KeyEvent.VK_LEFT)
        {
            if (!isObstacle(x-1,y)) {
                x = x - 1;
                if (willAdjustMapPosition()) {
                    scrollanimate_dir = LEFT;
                    respond_to_keys = false;
                    isscrolling = true;

                }
                avatar_direction = LEFT;
            }
        }
        else if (e.getKeyCode() == KeyEvent.VK_RIGHT)
        {
            if (!isObstacle(x+1,y)) {
                x = x + 1;
                if (willAdjustMapPosition()) {
                    scrollanimate_dir = RIGHT;
                    respond_to_keys = false;
                    isscrolling = true;

                }
                avatar_direction = RIGHT;
            }
        }
        else if (e.getKeyCode()==KeyEvent.VK_DOWN)
        {
            if (!isObstacle(x,y+1)) {
                y = y + 1;
                if (willAdjustMapPosition()) {
                    scrollanimate_dir = DOWN;
                    respond_to_keys = false;
                    isscrolling = true;
                }
                avatar_direction = DOWN;
            }
        }
        else if (e.getKeyCode()==KeyEvent.VK_UP)
        {
            if (!isObstacle(x,y-1)) {
                y = y - 1;
                if (willAdjustMapPosition()) {
                    scrollanimate_dir = UP;
                    respond_to_keys = false;
                    isscrolling = true;
                    //scrollanimate_timer.start();
                }
                avatar_direction = UP;
            }
        }
        repaint();
    }

    @Override
    public void keyReleased(KeyEvent e) {

    }

    public void actionPerformed(ActionEvent e) {

        // this is called every 2 ms
        timer_count++;
        // move all of the bullets every 30ms

            repaint();
        // handle scrolling every 2ms
        if (isscrolling == true) {
            if (scrollanimate_dir == RIGHT) {

                scrollanimate_offset_x+=1;
                if (scrollanimate_offset_x > tilesize) {
                    scrollanimate_offset_x = 0;
                    isscrolling = false;
                    AdjustMapPosition();
                    respond_to_keys = true;
                }
            } else if (scrollanimate_dir == LEFT) {

                scrollanimate_offset_x-=1;
                if (scrollanimate_offset_x < -1 * tilesize) {
                    scrollanimate_offset_x = 0;
                    isscrolling = false;
                    AdjustMapPosition();
                    respond_to_keys = true;
                }

            } else if (scrollanimate_dir == DOWN) {

                scrollanimate_offset_y+=1;
                if (scrollanimate_offset_y > tilesize) {
                    scrollanimate_offset_y = 0;
                    isscrolling = false;
                    AdjustMapPosition();
                    respond_to_keys = true;
                }
            } else if (scrollanimate_dir == UP) {
                scrollanimate_offset_y-=1;

                if (scrollanimate_offset_y < -1 * tilesize) {
                    scrollanimate_offset_y = 0;
                    isscrolling = false;
                    AdjustMapPosition();
                    respond_to_keys = true;
                }

            }
            repaint();
        }
    }


    public boolean willAdjustMapPosition()
    {
        boolean willadjust = false;
        if ((x-mapoffset_x)>12) { willadjust=true;}
        if ((x-mapoffset_x)<3) { willadjust=true;}
        if ((y-mapoffset_y)>9) { willadjust=true;}
        if ((y-mapoffset_y)<3) { willadjust=true;}
        return willadjust;
    }
    public void AdjustMapPosition()
    {

        if ((x-mapoffset_x)>12) { mapoffset_x++; }
        if ((x-mapoffset_x)<3) { mapoffset_x--;}
        if ((y-mapoffset_y)>9) { mapoffset_y++;}
        if ((y-mapoffset_y)<3) { mapoffset_y--; }

    }




    public void paint(Graphics g) {
        super.paint(g);
       
        //draw the map

        int maxrow = Math.min(mapoffset_y + 14, map_height);
        int maxcol = Math.min(mapoffset_x + 18, map_width);


        int offsetx = scrollanimate_offset_x + mapoffset_x * tilesize;
        int offsety = scrollanimate_offset_y + mapoffset_y * tilesize;

        for (int row = mapoffset_y; row < maxrow; row++) {
            for (int col = mapoffset_x; col < maxcol; col++) {
                //tiles[map[row][col]]
                if (row >= 0 && col >= 0) {
                    g.drawImage(tiles[map[row][col]].img, col * tilesize - offsetx - tilesize, row * tilesize - offsety - tilesize, tilesize, tilesize, null);
                }
            }
        }


        // draw the main character
        g.setColor(Color.BLACK);
        if (avatar_direction == LEFT)
            g.drawImage(avatar_left, (x) * tilesize - offsetx - tilesize, (y) * tilesize - offsety - tilesize, tilesize, tilesize, null);
        if (avatar_direction == RIGHT)
            g.drawImage(avatar_right, (x) * tilesize - offsetx - tilesize, (y) * tilesize - offsety - tilesize, tilesize, tilesize, null);
        if (avatar_direction == UP)
            g.drawImage(avatar_up, (x) * tilesize - offsetx - tilesize, (y) * tilesize - offsety - tilesize, tilesize, tilesize, null);
        if (avatar_direction == DOWN)
            g.drawImage(avatar_down, (x) * tilesize - offsetx - tilesize, (y) * tilesize - offsety - tilesize, tilesize, tilesize, null);

        //draw the npcs
        //for (int i = 0; i < npcs.size(); i++) {
        //g.drawImage(npcs.get(i).image,npcs.get(i).x * tilesize - offsetx - tilesize,npcs.get(i).y * tilesize - offsety - tilesize, 64, 64,null);
        //}

    }
}
