package com.company;

import javax.imageio.ImageIO;
import java.awt.*;
import java.io.File;
import java.io.IOException;

public class NPC {
    public int x;
    public int y;
    public Image image;

    public NPC()
    {
        try {

            image = ImageIO.read(new File("npc.jpg"));

        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }
}
