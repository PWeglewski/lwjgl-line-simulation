package pl.lodz.p.pag.weglewski.modeling.points;

import org.joml.Vector3f;

/**
 * Created by piotr on 22.03.17.
 */
public class Oscilator extends MaterialPointSet {
    private double k; //współczynnik sprężystości

    Oscilator(double k){
        super(1);
        this.k = k;

        MaterialPoint point = getMaterialPoint(0);
        point.setPosition(new Vector3f(-1.0f,0f,0f));
        point.setSpeed(new Vector3f(1f,1f,0));
        point.setColor(0f,1f,0.5f);
    }



    @Override
    public Vector3f force(int i) {
        return getMaterialPoint(0).getPosition().mul((float) (-k));
    }
}
