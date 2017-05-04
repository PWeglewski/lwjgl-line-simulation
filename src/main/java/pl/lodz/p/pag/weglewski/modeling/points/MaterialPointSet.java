package pl.lodz.p.pag.weglewski.modeling.points;

import org.joml.Vector3f;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by piotr on 21.03.17.
 */
public abstract class MaterialPointSet {
    int amount;
    List<MaterialPoint> points;
    boolean[] connections;

    public MaterialPointSet(int amount) {
        this.amount = amount;
        this.points = new ArrayList<>();
        this.connections = new boolean[amount];
        for (int i = 0; i < amount; i++) {
            points.add(new MaterialPoint());
            this.connections[i] = false;
        }
    }

    public void prepareMovement(float timeStep, Algorithm algorithm) {
        for (int i = 0; i < amount; i++) {
            if (!connections[i]) {
                points.get(i).prepareMovement(force(i), timeStep, algorithm);
            }
        }
    }

    public abstract Vector3f force(int i);

    protected MaterialPoint getMaterialPoint(int i) {
        if (i < 0 || i >= amount) {
            return null;
        }
        return points.get(i);
    }
}
