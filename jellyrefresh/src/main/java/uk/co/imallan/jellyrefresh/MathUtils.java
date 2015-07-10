package uk.co.imallan.jellyrefresh;

/**
 * Created by yilun
 * on 18/05/15.
 */
class MathUtils {

    public static int constrains(int input, int a, int b) {
        int result = input;
        final int min = Math.min(a, b);
        final int max = Math.max(a, b);
        result = result > min ? result : min;
        result = result < max ? result : max;
        return result;
    }

    public static float constrains(float input, float a, float b) {
        float result = input;
        final float min = Math.min(a, b);
        final float max = Math.max(a, b);
        result = result > min ? result : min;
        result = result < max ? result : max;
        return result;
    }

}
