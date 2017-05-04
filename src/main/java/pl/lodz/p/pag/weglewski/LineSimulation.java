package pl.lodz.p.pag.weglewski;

import org.joml.Matrix3f;
import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.lwjgl.Version;
import org.lwjgl.glfw.GLFWErrorCallback;
import org.lwjgl.glfw.GLFWVidMode;
import org.lwjgl.opengl.GL;
import org.lwjgl.opengl.GL11;
import org.lwjgl.system.MemoryStack;
import pl.lodz.p.pag.weglewski.utils.MatrixUtilities;

import java.nio.IntBuffer;
import java.util.ArrayList;
import java.util.List;

import static org.lwjgl.glfw.Callbacks.glfwFreeCallbacks;
import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL15.*;
import static org.lwjgl.opengl.GL20.glEnableVertexAttribArray;
import static org.lwjgl.opengl.GL20.glVertexAttribPointer;
import static org.lwjgl.opengl.GL30.glBindVertexArray;
import static org.lwjgl.opengl.GL30.glGenVertexArrays;
import static org.lwjgl.system.MemoryStack.stackPush;
import static org.lwjgl.system.MemoryUtil.NULL;

public class LineSimulation {
    private static final int WINDOW_WIDTH = 800;
    private static final int WINDOW_HEIGHT = 600;
    // Initialize positions of triangle
    List<Vector3f> positions = new ArrayList<Vector3f>() {{
        add(new Vector3f(0.0f, 1.0f, 0.0f));
        add(new Vector3f(1.0f, -1.0f, 0.0f));
        add(new Vector3f(-1.0f, -1.0f, 0.0f));
    }};


    int[] indices = {0, 1, 2};

    int VAO;
    int VBO;
    int IBO;

    Vector3f cameraPosition = new Vector3f(0.0f, 0.0f, 3.0f);
    Vector3f cameraLookAt = new Vector3f(0.0f, 0.0f, 0.0f);
    Vector3f cameraUp = new Vector3f(0.0f, 1.0f, 0.0f);
    Matrix4f worldMatrix = MatrixUtilities.initialize(1.0f);
    Matrix4f viewMatrix = MatrixUtilities.lookAt(cameraPosition, cameraLookAt, cameraUp);
    Matrix4f projectionMatrix = MatrixUtilities.perspectiveFov(Math.toRadians(60.0d), WINDOW_WIDTH, WINDOW_HEIGHT, 0.1f, 10.0f);
    Shader shader;
    Texture texture;
    // The window handle
    private long window;

    public static void main(String[] args) {
        new LineSimulation().run();
    }

    void windowSizeCallback(long window, int width, int height) {
        glViewport(0, 0, width, height);
        if (shader != null) {
            shader.setUniformMatrix4fv("viewProj", projectionMatrix.mul(viewMatrix));
        }
    }

    public void run() {
        System.out.println("Hello LWJGL " + Version.getVersion() + "!");

        init();

        loadContent();

        loop();

        // Free the window callbacks and destroy the window
        glfwFreeCallbacks(window);
        glfwDestroyWindow(window);

        // Terminate GLFW and free the error callback
        glfwTerminate();
        glfwSetErrorCallback(null).free();
    }

    private void init() {
        // Setup an error callback. The default implementation
        // will print the error message in System.err.
        GLFWErrorCallback.createPrint(System.err).set();

        // Initialize GLFW. Most GLFW functions will not work before doing this.
        if (!glfwInit())
            throw new IllegalStateException("Unable to initialize GLFW");

        // Configure GLFW
        glfwDefaultWindowHints(); // optional, the current window hints are already the default
        glfwWindowHint(GLFW_VISIBLE, GLFW_FALSE); // the window will stay hidden after creation
        glfwWindowHint(GLFW_RESIZABLE, GLFW_TRUE); // the window will be resizable

        // Create the window
        window = glfwCreateWindow(WINDOW_WIDTH, WINDOW_HEIGHT, "Line Simulation", NULL, NULL);
        if (window == NULL)
            throw new RuntimeException("Failed to create the GLFW window");

        // Setup a key callback. It will be called every time a key is pressed, repeated or released.
        glfwSetKeyCallback(window, (window, key, scancode, action, mods) -> {
            if (key == GLFW_KEY_ESCAPE && action == GLFW_RELEASE)
                glfwSetWindowShouldClose(window, true); // We will detect this in the rendering loop
        });

        // Get the thread stack and push a new frame
        try (MemoryStack stack = stackPush()) {
            IntBuffer pWidth = stack.mallocInt(1); // int*
            IntBuffer pHeight = stack.mallocInt(1); // int*

            // Get the window size passed to glfwCreateWindow
            glfwGetWindowSize(window, pWidth, pHeight);

            // Get the resolution of the primary monitor
            GLFWVidMode vidmode = glfwGetVideoMode(glfwGetPrimaryMonitor());

            // Center the window
            glfwSetWindowPos(
                    window,
                    (vidmode.width() - pWidth.get(0)) / 2,
                    (vidmode.height() - pHeight.get(0)) / 2
            );
        } // the stack frame is popped automatically

        // Make the OpenGL context current
        glfwMakeContextCurrent(window);




        glfwSetWindowSizeCallback(window, this::windowSizeCallback);

        // Enable v-sync
        glfwSwapInterval(1);

        // Make the window visible
        glfwShowWindow(window);
    }

    private void loop() {
        // This line is critical for LWJGL's interoperation with GLFW's
        // OpenGL context, or any context that is managed externally.
        // LWJGL detects the context that is current in the current thread,
        // creates the GLCapabilities instance and makes the OpenGL
        // bindings available for use.
        GL.createCapabilities();

        // Set the clear color
        glClearColor(1.0f, 0.0f, 0.0f, 0.0f);

        float startTime = (float) glfwGetTime();
        float newTime = 0.0f;
        float gameTime = 0.0f;

        // Run the rendering loop until the user has attempted to close
        // the window or has pressed the ESCAPE key.
        while (!glfwWindowShouldClose(window)) {
            glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT); // clear the framebuffer

                    /* Update game time value */
            newTime = (float) glfwGetTime();
            gameTime = newTime - startTime;

        /* Render here */
            render(gameTime);

            glfwSwapBuffers(window); // swap the color buffers

            // Poll for window events. The key callback above will only be
            // invoked during this call.
            glfwPollEvents();
        }
    }

    int loadContent() {
        /* Create and bind Vertex Array Object */
        VAO = glGenVertexArrays();
        glBindVertexArray(VAO);

    /* Create new Vertex Buffer Object to store our triangle's positions */
        VBO = glGenBuffers();

    /* Tell OpenGL to use this buffer and inform that this buffer will contain an array of positions*/
        glBindBuffer(GL_ARRAY_BUFFER, VBO);

    /* Fill buffer with data */
        glBufferData(GL_ARRAY_BUFFER, toDataBuffer(positions), GL_STATIC_DRAW);

    /* Enable a generic vertex attribute array */
        glEnableVertexAttribArray(0);

    /* Tell OpenGL how to interpret the data in the buffer */
        glVertexAttribPointer(0, 3, GL11.GL_FLOAT, false, 0, 0);


        /** TODO: VBO's for normals and texture coordinates.
         * Remember to flip texture coordinates on Y axis due to using stb_image library!
         */

    /* Create and initialize Index Buffer Object */
        IBO = glGenBuffers();
        glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, IBO);
        glBufferData(GL_ELEMENT_ARRAY_BUFFER, indices, GL_STATIC_DRAW);

    /* Good practice - unbind GL object to prevent any possible mistakes of overriding settings */
        glBindVertexArray(0);

    /* Create and apply basic shader */
        shader = new Shader("Basic.vert", "Basic.frag");
        shader.apply();

        shader.setUniformMatrix4fv("world", worldMatrix);
        Matrix3f normalMatrix = new Matrix3f();
        shader.setUniformMatrix3fv("normalMatrix", worldMatrix.get3x3(normalMatrix).transpose().invert());
        shader.setUniformMatrix4fv("viewProj", projectionMatrix.mul(viewMatrix));

        shader.setUniform3fv("cam_pos", cameraPosition);

//    /* Create and bind a texture */
//        texture = new Texture();
//        texture.load("bricks.png");
//        texture.bind();

        return 1;
    }

    void render(float time) {
        //TODO: clear depth buffer
        glClear(GL_COLOR_BUFFER_BIT);

    /* Draw our triangle */
        worldMatrix.rotate((float) Math.toRadians(90.0f), new Vector3f(0, 1, 0));

        shader.setUniformMatrix4fv("world", worldMatrix);
        Matrix3f normalMatrix = new Matrix3f();
        worldMatrix.get3x3(normalMatrix);
        shader.setUniformMatrix3fv("normalMatrix", normalMatrix.transpose().invert());

        shader.apply();
//        texture -> bind();

        glBindVertexArray(VAO);
        glDrawElements(GL_TRIANGLES, indices.length, GL_UNSIGNED_INT, 0);
    }

    private float[] toDataBuffer(List<Vector3f> positions) {
        float[] result = new float[positions.size() * 3];
        for (int i = 0; i < positions.size(); i++) {
            result[i] = positions.get(i).x();
            result[i + 1] = positions.get(i).x();
            result[i + 2] = positions.get(i).x();
        }
        return result;
    }

}
