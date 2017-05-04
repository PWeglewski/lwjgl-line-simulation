package pl.lodz.p.pag.weglewski;

import org.joml.Matrix3f;
import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.lwjgl.BufferUtils;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.nio.FloatBuffer;
import java.util.Map;

import static org.lwjgl.opengl.GL20.*;

/**
 * Created by piotr on 25.04.17.
 */
public class Shader {
    private static FloatBuffer floatBuffer = BufferUtils.createFloatBuffer(16);
    int programId;
    boolean isLinked;
    Map<String, Integer> uniformsLocations;

    public Shader(String vertexShaderFilename, String fragmentShaderFilename) {
        this.programId = 0;
        this.isLinked = false;

        String[] shaderCodes = {
                loadFile(vertexShaderFilename)
        };
    }

    private String loadFile(String fileName) {
        if (fileName.isEmpty()) {
            return "";
        }
        StringBuilder fileText = new StringBuilder();
        String line;

        try {
            BufferedReader reader = new BufferedReader(new FileReader("src/main/resources/" + fileName));
            reader.lines().forEach(l -> {
                fileText.append(l + "\n");
            });
            return fileText.toString();
        } catch (FileNotFoundException e) {
            System.out.println("Could not open file.");
            e.printStackTrace();
            return "";
        }

    }

    public void setUniformMatrix4fv(String viewProj, Matrix4f matrix) {

    }

    public void setUniformMatrix3fv(String uniformName, Matrix3f matrix3f) {
        if (!uniformsLocations.containsKey(uniformName)) {
            getUniformLocation(uniformName);
        }
        matrix3f.get(floatBuffer);
        glUniformMatrix3fv(uniformsLocations.get(uniformName), false, floatBuffer);
    }

    private boolean getUniformLocation(String uniformName) {
        int uniformLocation = glGetUniformLocation(programId, uniformName);
        if (uniformLocation != -1) {
            uniformsLocations.put(uniformName, uniformLocation);
            return true;
        }
        System.out.println("Error! Can't find uniform " + uniformName);
        return false;
    }

    public void apply() {
        if (programId != 0 && isLinked) {
            glUseProgram(programId);
        }
    }

    public void setUniform3fv(String cam_pos, Vector3f cameraPosition) {
    }
}
