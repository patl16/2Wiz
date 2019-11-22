package com.company;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.*;

public class Game  {


    public static void main(String[] args) {
	    // write your code here
        JFrame frame = new JFrame();
        GameFrame g = new GameFrame();

        g.LoadTiles();
        g.LoadMap();
        g.CreateNPCS();
        g.addKeyListener(g);
        g.setFocusable(true);

        frame.add(g);
        frame.setSize(1024,768);
        frame.setTitle("Llama Quest 2019 (tm)");
        frame.setVisible(true);
        frame.setResizable(false);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);




    }
}
