package pl.lodz.p.pag.weglewski.utils;

/**
 * Created by piotr on 13.03.17.
 */
public class TVector<T> {
    T x;
    T y;
    T z;
    T w;

    TVector(){

    }

    TVector(T x, T y, T z){
        this.x = x;
        this.y = y;
        this.z = z;
    }

    TVector(T x, T y, T z, T w){
        this.x = x;
        this.y = y;
        this.z = z;
        this.w = w;
    }

    public T getX() {
        return x;
    }

    public void setX(T x) {
        this.x = x;
    }

    public T getY() {
        return y;
    }

    public void setY(T y) {
        this.y = y;
    }

    public T getZ() {
        return z;
    }

    public void setZ(T z) {
        this.z = z;
    }

    public T getW() {
        return w;
    }

    public void setW(T w) {
        this.w = w;
    }
}
