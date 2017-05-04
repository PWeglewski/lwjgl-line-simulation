package pl.lodz.p.pag.weglewski.modeling.points;

import org.joml.Vector3f;
import org.joml.Vector4f;

import static java.lang.Math.sqrt;

/**
 * Created by piotr on 13.03.17.
 */
public class MaterialPoint {
    private Vector3f nextSpeed;
    private Vector3f currentSpeed;
    private Vector3f nextPosition;
    private Vector3f currentPosition;
    private int stepNumber;
    private Vector3f previousPosition;
    private float mass;
    private Vector4f color;

    void prepareMovementEuler(Vector3f acceleration, float timeStep) {
        nextSpeed = currentSpeed.add(acceleration.mul(timeStep));
        nextPosition = currentPosition.add(nextSpeed.mul(timeStep));
    }

    void prepareMovementVerlet(Vector3f acceleration, float timeStep) {
        if (stepNumber == 0) {
            prepareMovementEuler(acceleration, timeStep);
        } else {
            nextPosition = currentPosition.mul(2.0f).sub(previousPosition).add(acceleration.mul((float) sqrt(timeStep)));
            nextSpeed = (nextPosition.sub(previousPosition)).div(2.0f * timeStep);
        }
    }

    void prepareMovement(Vector3f force, float timeStep, Algorithm algorithm) {

        Vector3f acceleration = force.div(mass);

        switch (algorithm) {
            case EULER:
                prepareMovementEuler(acceleration, timeStep);
                break;
            case VERLET:
                prepareMovementVerlet(acceleration, timeStep);
                break;
        }
    }

    void move() {
        previousPosition = currentPosition;
        currentPosition = nextPosition;
        currentSpeed = nextSpeed;
        stepNumber++;
    }

    public void setPosition(Vector3f position) {
        this.currentPosition = position;
    }

    public void setSpeed(Vector3f speed) {
        this.currentSpeed = speed;
    }

    public void setColor(float r, float g, float b) {
        this.color = new Vector4f(r, g, b, 1f);
    }

    public Vector3f getPosition() {
        return currentPosition;
    }
}
