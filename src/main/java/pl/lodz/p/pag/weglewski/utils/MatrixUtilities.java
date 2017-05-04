package pl.lodz.p.pag.weglewski.utils;

import org.joml.Matrix4f;
import org.joml.Vector3f;

/**
 * Created by piotr on 28.03.17.
 */
public class MatrixUtilities {
    public static Matrix4f initialize(float f) {
        return new Matrix4f(f, f, f, f,
                f, f, f, f,
                f, f, f, f,
                f, f, f, f);
    }

    /**
     * http://stackoverflow.com/questions/19740463/lookat-function-im-going-crazy
     */
    public static Matrix4f lookAt(Vector3f eye, Vector3f center, Vector3f up) {
        Vector3f f = (center.sub(eye)).normalize();
        Vector3f u = up.normalize();
        Vector3f s = (f.cross(u)).normalize();

        u = s.cross(f);

        Matrix4f result = initialize(1);

        result.m00(s.x);
        result.m10(s.y);
        result.m20(s.z);

        result.m01(u.x);
        result.m11(u.y);
        result.m21(u.z);

        result.m02(-f.x);
        result.m12(-f.y);
        result.m22(-f.z);

        result.m30(-(s.dot(eye)));
        result.m31(-(u.dot(eye)));
        result.m32(-(f.dot(eye)));

        return result;
    }

    /**
     * http://www.geeks3d.com/20090729/howto-perspective-projection-matrix-in-opengl/
     */
    public static Matrix4f perspectiveFov(double v, float fov, float aspect, float zNear, float zFar) {
        float XYMax = (float) (zNear * Math.tan(fov * (Math.PI / 360)));
        float YMin = -XYMax;
        float XMin = -XYMax;

        float width = XYMax - XMin;
        float height = XYMax - YMin;

        float depth = zFar - zNear;
        float q = -(zFar + zNear) / depth;
        float qn = -2 * (zFar * zNear) / depth;

        float w = 2 * zNear / width;
        w = w / aspect;
        float h = 2 * zNear / height;

        float[] m = {w, 0, 0, 0,
                0, h, 0, 0,
                0, 0, q, -1,
                0, 0, qn, 0
        };

        Matrix4f result = new Matrix4f();
        result.set(m);
        return result;
    }
}
