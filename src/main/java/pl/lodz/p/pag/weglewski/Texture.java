package pl.lodz.p.pag.weglewski;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL13.GL_TEXTURE0;
import static org.lwjgl.opengl.GL13.glActiveTexture;

/**
 * Created by piotr on 29.04.17.
 */
public class Texture {
    public boolean useLinear;
    private int toId;

    Texture() {
        useLinear = true;
        toId = 0;
    }

    public void close() {
        if (toId != 0) {
            glDeleteTextures(toId);
            toId = 0;
        }
    }

    boolean load(String fileName) {
        if (fileName.isEmpty()) return false;

        boolean isLoaded = false;
        int width, height, components;

        BufferedImage img = null;
        try {
            img = ImageIO.read(new File("resources/textures/" + fileName));
            width = img.getWidth();
            height = img.getHeight();
            components = img.getColorModel().getNumColorComponents();



        } catch (IOException e) {
            e.printStackTrace();
        }

        return false;
    }

    void bind(int index) {
        if (toId != 0) {
            glActiveTexture(GL_TEXTURE0 + index);
            glBindTexture(GL_TEXTURE_2D, toId);
        }
    }
}
