package com.xsw.ui.anim.path;

/**
 * ClassName: {@link PathAnimatorEvaluatorDelegateImpl}
 * Description:
 * <p>
 * Create by X at 2021/05/11 10:25.
 */
class PathAnimatorEvaluatorDelegateImpl implements TypeEvaluatorDelegate<PathPointF> {

    private PathPointF mPathPointF;

    public PathAnimatorEvaluatorDelegateImpl(PathPointF mPathPointF) {
        this.mPathPointF = mPathPointF;
        judgePathPointF();
    }

    private void judgePathPointF() {
        if (null == mPathPointF) {
            mPathPointF = new PathPointF();
        }
    }

    @Override
    public PathPointF evaluate(float fraction, PathPointF startValue, PathPointF endValue) {
        if (null == startValue || null == endValue) {
            return null;
        }
        judgePathPointF();
        final float t = 1f - fraction;
        double x, y;
        final PathPointF.Type type = endValue.getType();
        if (type == PathPointF.Type.LINE) {
            x = startValue.getX() + (endValue.getX() - startValue.getX()) * fraction;
            y = startValue.getY() + (endValue.getY() - startValue.getY()) * fraction;
        } else if (type == PathPointF.Type.QUAD) {
            final double tt = Math.pow(t, 2);
            final double ff = Math.pow(fraction, 2);
            final float tf = t * fraction;
            x = startValue.getX() * tt
                    + 2 * endValue.getCx1() * tf
                    + endValue.getX() * ff;

            y = startValue.getY() * tt
                    + 2 * endValue.getCy1() * tf
                    + endValue.getY() * ff;
        } else if (type == PathPointF.Type.CUBIC) {
            final double ttt = Math.pow(t, 3);
            final double ttf = Math.pow(t, 2) * fraction;
            final double fff = Math.pow(fraction, 3);
            final double fft  = Math.pow(fraction, 2) * t;
            x = startValue.getX() * ttt
                    + 3 * endValue.getCx1() * ttf
                    + 3 * endValue.getCx2() * fft
                    + endValue.getX() * fff;

            y = startValue.getY() * ttt
                    + 3 * endValue.getCy1() * ttf
                    + 3 * endValue.getCy2() * fft
                    + endValue.getY() * fff;

        } else {
            x = endValue.getX();
            y = endValue.getY();
        }
        mPathPointF.setType(endValue.getType());
        mPathPointF.setX((float) x);
        mPathPointF.setY((float) y);
        return mPathPointF;
    }

}
