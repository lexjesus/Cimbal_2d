package com.example.java_test;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.os.Bundle;
import android.view.Display;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Arrays;

public class MainActivity extends AppCompatActivity {

    ArrayList<Figura> arrFigura = new ArrayList<>();
    Figura figura1 = null;
    Figura figura2 = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        RelativeLayout rel = (RelativeLayout) findViewById(R.id.drawCont);
        DrawView drawView = new DrawView(this);
        rel.addView(drawView);
    }

    class DrawView extends View {
        Paint p = new Paint();
        Paint p2 = new Paint();


        public DrawView(Context context) {
            super(context);
        }

        @Override
        protected void onDraw(Canvas canvas) {

            canvas.drawRGB(0, 0, 0);
            p.setStrokeWidth(10);
            p2.setStrokeWidth(10);
            p2.setARGB(255, 200, 200, 200);
            for (int f = 0; f < arrFigura.size(); f++) {
                Figura figura = arrFigura.get(f);
                p.setColor(figura.colorNorm);

                if (figura != null) {

                    if (figura.getForeground()) {
                        ArrayList<MyPoint> arr = figura.getArrPoints();
                        for (int g = 0; g < figura.getPointsSize(); g++) {
                            if (g == figura.getPointsSize() - 1) {
                                canvas.drawLine(arr.get(g).getX(), arr.get(g).getY(),
                                        arr.get(0).getX(), arr.get(0).getY(), p);
                            } else {
                                canvas.drawLine(arr.get(g).getX(), arr.get(g).getY(),
                                        arr.get(g + 1).getX(), arr.get(g + 1).getY(), p);
                            }
                        }
                    } else {
                        ArrayList<ArrayList<MyPoint>> arr = figura.getCutEdges();
                        ArrayList<ArrayList<MyPoint>> triangles;
                        Figura figAbove = null;
                        for (int y = 0; y < arrFigura.size(); y++) {
                            if (arrFigura.get(y).getForeground()) {
                                figAbove = arrFigura.get(y);
                                triangles = figAbove.getConvexMas();
                            }
                        }
                        for (int g = 0; g < arr.size(); g++) {
                            for (int h = 0; h < arr.get(g).size() - 1; h++) {
                                if (figAbove.getConvex()) {
                                    boolean ff = isInsideOfFigure(figAbove.getArrPoints(),
                                            figura.getCenterOfEdge(arr.get(g).get(h), arr.get(g).get(h + 1)));
                                    if (ff) {
                                        canvas.drawLine(arr.get(g).get(h).getX(), arr.get(g).get(h).getY(),
                                                arr.get(g).get(h + 1).getX(), arr.get(g).get(h + 1).getY(), p2);
                                    } else {
                                        canvas.drawLine(arr.get(g).get(h).getX(), arr.get(g).get(h).getY(),
                                                arr.get(g).get(h + 1).getX(), arr.get(g).get(h + 1).getY(), p);
                                    }
                                } else {
                                    boolean flag = false;
                                    for (int k = 0; k < figAbove.getConvexMas().size(); k++) {
                                        boolean fff = isInsideOfFigure(figAbove.getConvexMas().get(k),
                                                figura.getCenterOfEdge(arr.get(g).get(h), arr.get(g).get(h + 1)));
                                        if (fff) {
                                            flag = true;
                                        }
                                    }
                                    if (flag) {
                                        canvas.drawLine(arr.get(g).get(h).getX(), arr.get(g).get(h).getY(),
                                                arr.get(g).get(h + 1).getX(), arr.get(g).get(h + 1).getY(), p2);
                                    } else {
                                        canvas.drawLine(arr.get(g).get(h).getX(), arr.get(g).get(h).getY(),
                                                arr.get(g).get(h + 1).getX(), arr.get(g).get(h + 1).getY(), p);
                                    }
                                }
                            }
                        }
                    }
                }
            }
            invalidate();
        }
    }

    public void createFigura(View view) {
        if (figura1 == null && figura2 == null) {
            figura1 = new Figura();
            figura1.addPoint(new MyPoint(200, 400));
            figura1.addPoint(new MyPoint(800, 450));
            figura1.addPoint(new MyPoint(500, 1300));
            figura1.addPoint(new MyPoint(300, 900));
            figura1.setForeground(false);
            figura1.colorNorm = Color.RED;
            for (int i = 0; i < figura1.getPointsSize(); i++) {
                boolean r = figura1.isConvex(figura1.getPoint(i), figura1.getArrPoints());
                if (!r) {
                    figura1.setConvex(false);
                    figura1.triangulation();
                    break;
                }
            }
            figura2 = new Figura();
            figura2.addPoint(new MyPoint(450, 350));
            figura2.addPoint(new MyPoint(950, 450));
            figura2.addPoint(new MyPoint(650, 650));
            figura2.addPoint(new MyPoint(750, 1500));
            figura2.addPoint(new MyPoint(100, 1200));
            figura2.addPoint(new MyPoint(350, 750));
            figura2.setForeground(true);
            figura2.colorNorm = Color.BLUE;
            for (int i = 0; i < figura2.getPointsSize(); i++) {
                boolean r = figura2.isConvex(figura2.getPoint(i), figura2.getArrPoints());
                if (!r) {
                    figura2.setConvex(false);
                    figura2.triangulation();
                    break;
                }
            }
            arrFigura.add(figura1);
            arrFigura.add(figura2);
            cyrusBeckBuild();
        }
    }

    public void cyrusBeckBuild() {
        Figura figUnder = null;
        Figura figAbove = null;
        for (int i = 0; i < arrFigura.size(); i++) {
            if (arrFigura.get(i).getForeground()) {
                figAbove = arrFigura.get(i);
            } else {
                figUnder = arrFigura.get(i);
            }
        }
        ArrayList<ArrayList<MyPoint>> cutEdges = new ArrayList<>(figUnder.getPointsSize());
        if (!figAbove.getConvex()) {
            ArrayList<ArrayList<MyPoint>> triangleFigures = figAbove.getConvexMas();
            for (int i = 0; i < figUnder.getPointsSize(); i++) {
                ArrayList<MyPoint> a = new ArrayList<>();
                cutEdges.add(a);
                cutEdges.get(i).add(figUnder.getPoint(i));
                for (int j = 0; j < triangleFigures.size(); j++) {
                    ArrayList<MyPoint> tmp = new ArrayList<>();
                    if (i == figUnder.getPointsSize() - 1) {
                        tmp.addAll(cyrusBeck(new MyPoint[]{figUnder.getPoint(i),
                                figUnder.getPoint(0)}, triangleFigures.get(j)));
                    } else {
                        tmp.addAll(cyrusBeck(new MyPoint[]{figUnder.getPoint(i),
                                figUnder.getPoint(i + 1)}, triangleFigures.get(j)));
                    }
                    if (tmp != null) {
                        cutEdges.get(i).addAll(tmp);
                    }
                }
                if (i == figUnder.getPointsSize() - 1) {
                    cutEdges.get(i).add(figUnder.getPoint(0));
                } else {
                    cutEdges.get(i).add(figUnder.getPoint(i + 1));
                }
                if (cutEdges.get(i).size() > 3) {
                    for (int u = 0; u < cutEdges.get(i).size() - 1; u++) {
                        if ((cutEdges.get(i).get(u).getX() == cutEdges.get(i).get(u + 1).getX()) &&
                                (cutEdges.get(i).get(u).getY() == cutEdges.get(i).get(u + 1).getY())) {
                            cutEdges.get(i).remove(u);
                        }
                    }
                }
            }
        } else {
            for (int i = 0; i < figUnder.getPointsSize(); i++) {
                ArrayList<MyPoint> a = new ArrayList<>();
                cutEdges.add(a);
                cutEdges.get(i).add(figUnder.getPoint(i));
                ArrayList<MyPoint> tmp = new ArrayList();
                if(i == figUnder.getPointsSize() - 1) {
                    tmp.addAll(cyrusBeck(new MyPoint[]{figUnder.getPoint(i),
                            figUnder.getPoint(0)}, figAbove.getArrPoints()));
                }
                else{
                    tmp.addAll(cyrusBeck(new MyPoint[]{figUnder.getPoint(i),
                            figUnder.getPoint(i + 1)}, figAbove.getArrPoints()));
                }
                if (tmp != null) {
                    cutEdges.get(i).addAll(tmp);
                }
                if (i == figUnder.getPointsSize() - 1) {
                    cutEdges.get(i).add(figUnder.getPoint(0));
                } else {
                    cutEdges.get(i).add(figUnder.getPoint(i + 1));
                }
            }
        }

        figUnder.setCutEdges(cutEdges);
    }

    public void zoom(Figura figura, Boolean z) {
        float q;
        if (z) {
            q = (float) 1.1;
        } else {
            q = (float) 0.9;
        }

        int[] cent;
        cent = figura.getCenterPoint();
        for (int i = 0; i < figura.getPointsSize(); i++) {
            MyPoint p = figura.getPoint(i);
            float x;
            float y;
            x = p.getX() * q + (1 - q) * cent[0];
            y = p.getY() * q + (1 - q) * cent[1];

            figura.changePoint((int) x, (int) y, i);
        }

    }

    public void plusZoom(View view) {

        for (int i = 0; i < arrFigura.size(); i++) {
            if (arrFigura.get(i).getForeground()) {
                zoom(arrFigura.get(i), true);
            }
            if (!arrFigura.get(i).getConvex()) {
                arrFigura.get(i).triangulation();
            }
        }
        cyrusBeckBuild();
    }

    public void minusZoom(View view) {
        for (int i = 0; i < arrFigura.size(); i++) {
            if (arrFigura.get(i).getForeground()) {
                zoom(arrFigura.get(i), false);
            }
            if (!arrFigura.get(i).getConvex()) {
                arrFigura.get(i).triangulation();
            }
        }
        cyrusBeckBuild();
    }

    public void toLeft(View view) {
        for (int i = 0; i < arrFigura.size(); i++) {
            if (arrFigura.get(i).getForeground()) {
                Figura figura = arrFigura.get(i);
                for (int t = 0; t < figura.getPointsSize(); t++) {
                    figura.changePoint(figura.getPoint(t).getX() - 15, figura.getPoint(t).getY(), t);
                }
            }
            if (!arrFigura.get(i).getConvex()) {
                arrFigura.get(i).triangulation();
            }
        }
        cyrusBeckBuild();
    }

    public void toRight(View view) {
        for (int i = 0; i < arrFigura.size(); i++) {
            if (arrFigura.get(i).getForeground()) {
                Figura figura = arrFigura.get(i);
                for (int t = 0; t < figura.getPointsSize(); t++) {
                    figura.changePoint(figura.getPoint(t).getX() + 15, figura.getPoint(t).getY(), t);
                }
            }
            if (!arrFigura.get(i).getConvex()) {
                arrFigura.get(i).triangulation();
            }
        }
        cyrusBeckBuild();
    }

    public void toUp(View view) {
        for (int i = 0; i < arrFigura.size(); i++) {
            if (arrFigura.get(i).getForeground()) {
                Figura figura = arrFigura.get(i);
                for (int t = 0; t < figura.getPointsSize(); t++) {
                    figura.changePoint(figura.getPoint(t).getX(), figura.getPoint(t).getY() - 15, t);
                }
            }
            if (!arrFigura.get(i).getConvex()) {
                arrFigura.get(i).triangulation();
            }
        }
        cyrusBeckBuild();
    }

    public void toDown(View view) {
        for (int i = 0; i < arrFigura.size(); i++) {
            if (arrFigura.get(i).getForeground()) {
                Figura figura = arrFigura.get(i);
                for (int t = 0; t < figura.getPointsSize(); t++) {
                    figura.changePoint(figura.getPoint(t).getX(), figura.getPoint(t).getY() + 15, t);
                }
            }
            if (!arrFigura.get(i).getConvex()) {
                arrFigura.get(i).triangulation();
            }
        }
        cyrusBeckBuild();
    }

    public void rotate(View view) {
        for (int i = 0; i < arrFigura.size(); i++) {
            if (arrFigura.get(i).getForeground()) {
                Figura figura = arrFigura.get(i);
                int[] cent;
                cent = figura.getCenterPoint();
                for (int f = 0; f < figura.getPointsSize(); f++) {
                    double x;
                    double y;
                    x = cent[0] + (figura.getPoint(f).getX() - cent[0]) * Math.cos(Math.PI / 12) - (figura.getPoint(f).getY() - cent[1]) * Math.sin(Math.PI / 12);
                    y = cent[1] + (figura.getPoint(f).getX() - cent[0]) * Math.sin(Math.PI / 12) + (figura.getPoint(f).getY() - cent[1]) * Math.cos(Math.PI / 12);
                    figura.changePoint((int) x, (int) y, f);
                }
            }
            if (!arrFigura.get(i).getConvex()) {
                arrFigura.get(i).triangulation();
            }
        }
        cyrusBeckBuild();
    }

    public void tab(View view) {
        for (int i = 0; i < arrFigura.size(); i++) {
            if (arrFigura.get(i).getForeground()) {
                arrFigura.get(i).setForeground(false);
            } else {
                arrFigura.get(i).setForeground(true);
            }
            if (!arrFigura.get(i).getConvex()) {
                arrFigura.get(i).triangulation();
            }
        }
        cyrusBeckBuild();
    }

    public ArrayList<MyPoint> cyrusBeck(MyPoint[] direct, ArrayList<MyPoint> arrPoint) {
        int rSize = arrPoint.size();
        float[][] normals = new float[rSize][2];
        float[][] W = new float[rSize][2];
        float[][] D = new float[rSize][2];
        ArrayList<Float> t_up = new ArrayList<>();
        ArrayList<Float> t_down = new ArrayList<>();
        boolean insideCheck = true;
        for (int i = 0; i < rSize; i++) {
            if (i == 0) {
                normals[i][0] = -(arrPoint.get(i + 1).getY() - arrPoint.get(i).getY()) /
                        (float) (arrPoint.get(i + 1).getX() - arrPoint.get(i).getX());
                normals[i][1] = 1;
                float n1 = normals[i][0] * (arrPoint.get(rSize - 1).getX() - arrPoint.get(i + 1).getX());
                float n2 = normals[i][1] * (arrPoint.get(rSize - 1).getY() - arrPoint.get(i + 1).getY());
                if (n1 + n2 < 0) {
                    normals[i][0] *= -1;
                    normals[i][1] = -1;
                }
            } else if (i == rSize - 1) {
                normals[i][0] = -(arrPoint.get(0).getY() - arrPoint.get(i).getY()) /
                        (float) (arrPoint.get(0).getX() - arrPoint.get(i).getX());
                normals[i][1] = 1;
                float n1 = normals[i][0] * (arrPoint.get(i - 1).getX() - arrPoint.get(0).getX());
                float n2 = normals[i][1] * (arrPoint.get(i - 1).getY() - arrPoint.get(0).getY());
                if (n1 + n2 < 0) {
                    normals[i][0] *= -1;
                    normals[i][1] = -1;
                }
            } else {
                normals[i][0] = -(arrPoint.get(i + 1).getY() - arrPoint.get(i).getY()) /
                        (float) (arrPoint.get(i + 1).getX() - arrPoint.get(i).getX());
                normals[i][1] = 1;
                float n1 = normals[i][0] * (arrPoint.get(i - 1).getX() - arrPoint.get(i + 1).getX());
                float n2 = normals[i][1] * (arrPoint.get(i - 1).getY() - arrPoint.get(i + 1).getY());
                if (n1 + n2 < 0) {
                    normals[i][0] *= -1;
                    normals[i][1] = -1;
                }
            }
            W[i][0] = direct[0].getX() - arrPoint.get(i).getX();
            W[i][1] = direct[0].getY() - arrPoint.get(i).getY();
            D[i][0] = direct[1].getX() - direct[0].getX();
            D[i][1] = direct[1].getY() - direct[0].getY();
            float W_n = W[i][0] * normals[i][0] + W[i][1] * normals[i][1];
            float D_n = D[i][0] * normals[i][0] + D[i][1] * normals[i][1];
            float t = -W_n / D_n;
            boolean bb;

            if (D_n > 0) {
                if (t >= 0 && t <= 1) {
                    int x = direct[0].getX() + (int) ((direct[1].getX() - direct[0].getX()) * t);
                    int y = direct[0].getY() + (int) ((direct[1].getY() - direct[0].getY()) * t);
                    if (i == rSize - 1) {
                        bb = onEdge(arrPoint.get(i), arrPoint.get(0), x, y);
                    } else {
                        bb = onEdge(arrPoint.get(i), arrPoint.get(i + 1), x, y);
                    }
                    if (bb) {
                        t_down.add(t);
                    }
                }
            } else if (D_n < 0) {
                if (t >= 0 && t <= 1) {
                    int x = direct[0].getX() + (int) ((direct[1].getX() - direct[0].getX()) * t);
                    int y = direct[0].getY() + (int) ((direct[1].getY() - direct[0].getY()) * t);
                    if (i == rSize - 1) {
                        bb = onEdge(arrPoint.get(i), arrPoint.get(0), x, y);
                    } else {
                        bb = onEdge(arrPoint.get(i), arrPoint.get(i + 1), x, y);
                    }
                    if (bb) {
                        t_up.add(t);
                    }
                }
            }
        }
        ArrayList<MyPoint> result = new ArrayList<>();
        if (!t_down.isEmpty()) {
            float t1 = max(t_down);
            int x = direct[0].getX() + (int) ((direct[1].getX() - direct[0].getX()) * t1);
            int y = direct[0].getY() + (int) ((direct[1].getY() - direct[0].getY()) * t1);
            MyPoint p1 = new MyPoint(x, y);
            result.add(p1);
        }
        if (!t_up.isEmpty()) {
            float t2 = min(t_up);
            int x = direct[0].getX() + (int) ((direct[1].getX() - direct[0].getX()) * t2);
            int y = direct[0].getY() + (int) ((direct[1].getY() - direct[0].getY()) * t2);
            MyPoint p2 = new MyPoint(x, y);
            result.add(p2);
        }
        return result;
    }

    boolean onEdge(MyPoint p1, MyPoint p2, int x, int y) {
        if ((x > p1.getX() && x < p2.getX() ||
                x < p1.getX() && x > p2.getX()) &&
                (y > p1.getY() && y < p2.getY() ||
                        y < p1.getY() && y > p2.getY())) {
            return true;
        } else {
            return false;
        }
    }

    boolean isInsideOfFigure(ArrayList<MyPoint> arrPoint, MyPoint p) {
        boolean result = true;
        int rSize = arrPoint.size();
        float W_n;
        float Wx;
        float Wy;
        for (int i = 0; i < rSize; i++) {
            float[] normals = new float[2];
            if (i == 0) {
                normals[0] = -(arrPoint.get(i + 1).getY() - arrPoint.get(i).getY()) /
                        (float) (arrPoint.get(i + 1).getX() - arrPoint.get(i).getX());
                normals[1] = 1;
                float n1 = normals[0] * (arrPoint.get(rSize - 1).getX() - arrPoint.get(i + 1).getX());
                float n2 = normals[1] * (arrPoint.get(rSize - 1).getY() - arrPoint.get(i + 1).getY());
                if (n1 + n2 < 0) {
                    normals[0] *= -1;
                    normals[1] = -1;
                }
            } else if (i == rSize - 1) {
                normals[0] = -(arrPoint.get(0).getY() - arrPoint.get(i).getY()) /
                        (float) (arrPoint.get(0).getX() - arrPoint.get(i).getX());
                normals[1] = 1;
                float n1 = normals[0] * (arrPoint.get(i - 1).getX() - arrPoint.get(0).getX());
                float n2 = normals[1] * (arrPoint.get(i - 1).getY() - arrPoint.get(0).getY());
                if (n1 + n2 < 0) {
                    normals[0] *= -1;
                    normals[1] = -1;
                }
            } else {
                normals[0] = -(arrPoint.get(i + 1).getY() - arrPoint.get(i).getY()) /
                        (float) (arrPoint.get(i + 1).getX() - arrPoint.get(i).getX());
                normals[1] = 1;
                float n1 = normals[0] * (arrPoint.get(i - 1).getX() - arrPoint.get(i + 1).getX());
                float n2 = normals[1] * (arrPoint.get(i - 1).getY() - arrPoint.get(i + 1).getY());
                if (n1 + n2 < 0) {
                    normals[0] *= -1;
                    normals[1] = -1;
                }
            }
            Wx = p.getX() - arrPoint.get(i).getX();
            Wy = p.getY() - arrPoint.get(i).getY();
            W_n = Wx * normals[0] + Wy * normals[1];
            if (W_n < 0) {
                return false;
            }
        }
        return result;
    }

    float max(ArrayList<Float> mas) {
        float max = 0;
        for (int i = 0; i < mas.size(); i++) {
            if (mas.get(i) > max) {
                max = mas.get(i);
            }
        }
        return max;
    }

    float min(ArrayList<Float> mas) {
        float min = 99999999;
        for (int i = 0; i < mas.size(); i++) {
            if (mas.get(i) < min) {
                min = mas.get(i);
            }
        }
        return min;
    }

}
        //Toast toast = Toast.makeText(getApplicationContext(), "Пора покормить кота!", Toast.LENGTH_SHORT);
        //toast.show();
