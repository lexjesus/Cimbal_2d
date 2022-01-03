package com.example.java_test;

import static java.lang.Math.pow;

import android.graphics.Color;

import java.util.ArrayList;

public class Figura {
    //VARS
    ArrayList<MyPoint> points;
    public boolean foreground;
    public boolean convex;
    ArrayList<ArrayList<MyPoint>> convexMas;
    ArrayList<ArrayList<MyPoint>> cutEdges;
    public int colorNorm;
    public MyPoint centerPoint;
    public ArrayList<Float> tempT;


    //CONSTRUCTOR
    public Figura(){
        convex = true;
        points = new ArrayList<>();
    }

    //GETS
    public int getPointsSize(){
        return points.size();
    }
    public MyPoint getPoint(int ind){ return points.get(ind); }
    public boolean getConvex(){return convex;}
    public ArrayList<MyPoint> getArrPoints(){ return points;}
    public boolean getForeground(){
        return foreground;
    }
    public ArrayList<ArrayList<MyPoint>> getConvexMas(){ return convexMas; }
    public ArrayList<ArrayList<MyPoint>> getCutEdges(){ return cutEdges; }
    public int getCutEdgesSize(){return cutEdges.size();}

    //SETS
    public void setConvex(boolean b){convex = b;}
    public void setForeground(boolean b){
        foreground = b;
    }
    public void addPoint(MyPoint point){
        points.add(point);
    }
    public void changePoint(int x, int y, int ind){
        MyPoint point = points.get(ind);
        point.setX(x);
        point.setY(y);
    }
    public void setCutEdges(ArrayList<ArrayList<MyPoint>> m){
        if(cutEdges!= null)cutEdges.clear();
        cutEdges = m;}

    //METHODS
    public int[] getFloatMassivePoints(){
        int pSize = points.size();
        int[] pointsMassive = new int[pSize*2];
        int j = 0;
        for(int i = 0; i < pSize; i++){
            MyPoint p = points.get(i);
            pointsMassive[j] = (int)p.getX();
            j++;
            pointsMassive[j] = (int)p.getY();
            j++;
        }
        return pointsMassive;
    }

    public void getCenterPoint(){
        int[] pointMas = getFloatMassivePoints();
        int maxX = 0;
        int minX = 9999;
        int maxY = 0;
        int minY = 9999;
        int[] center = new int[2];
        for (int i = 0; i < pointMas.length; i++) {
            if(i%2 == 0){
                if (pointMas[i] > maxX){
                    maxX = pointMas[i];
                }
                else if(pointMas[i] < minX){
                    minX = pointMas[i];
                }
            }
            if(i%2 != 0){
                if (pointMas[i] > maxY){
                    maxY = pointMas[i];
                }
                else if(pointMas[i] < minY){
                    minY = pointMas[i];
                }
            }
        }
        center[0] = (int)((maxX + minX) / 2);
        center[1] = (int)((maxY + minY) / 2);

        centerPoint = new MyPoint(center[0], center[1]);
    }

    public boolean isConvex(MyPoint point, ArrayList<MyPoint> tempArr){
        int size = tempArr.size();
        double a = 0;
        int ind = tempArr.indexOf(point);
            if((ind != size - 1) && (ind != 0)){
                a = func1(vecProd(tempArr,ind, ind - 1,'x'),
                        vecProd(tempArr,ind, ind - 1, 'y'),
                        vecProd(tempArr,ind, ind + 1, 'x'),
                        vecProd(tempArr,ind, ind + 1, 'y'));
            }
            else if(ind == size - 1){
                a = func1(vecProd(tempArr,ind, ind - 1, 'x'),
                        vecProd(tempArr,ind, ind - 1, 'y'),
                        vecProd(tempArr,ind, 0, 'x'),
                        vecProd(tempArr,ind, 0, 'y'));
            }
            else if(ind == 0){
                a = func1(vecProd(tempArr,0, size - 1, 'x'),
                        vecProd(tempArr,0, size - 1, 'y'),
                        vecProd(tempArr,0, 1, 'x'),
                        vecProd(tempArr,0, 1, 'y'));
            }
        return (a > 0);
    }

    public void triangulation(){
        ArrayList<ArrayList<MyPoint>> convexFigures = new ArrayList<>();
        int i = 0;
        ArrayList<MyPoint> tempArr = new ArrayList<>(points);
        while(true){
            boolean q = false;
            if(i == 0) {
                MyPoint p1 = tempArr.get(tempArr.size() - 1);
                MyPoint p2 = tempArr.get(i);
                MyPoint p3 = tempArr.get(i + 1);
                if(!isConvex(tempArr.get(i), tempArr)){i++; continue;}
                for(int j = i + 2; j < tempArr.size() - 2; j++){
                    if(isInside(p1, p2, p3, tempArr.get(j))){
                        q = true;
                        break;
                    }
                }
                if(!q){
                    ArrayList<MyPoint> arr = new ArrayList<>(3);
                    arr.add(p1);
                    arr.add(p2);
                    arr.add(p3);
                    convexFigures.add(arr);
                    tempArr.remove(i);
                    i--;
                }
            }
            else if(i == tempArr.size() - 1){
                MyPoint p1 = tempArr.get(i - 1);
                MyPoint p2 = tempArr.get(i);
                MyPoint p3 = tempArr.get(0);
                if(!isConvex(tempArr.get(i), tempArr)){i++; continue;}
                for(int j = 1; j < tempArr.size() - 3; j++) {
                    if (isInside(p1, p2, p3, tempArr.get(j))) {
                        q = true;
                        break;
                    }
                }
                if(!q){
                    ArrayList<MyPoint> arr = new ArrayList<>(3);
                    arr.add(p1);
                    arr.add(p2);
                    arr.add(p3);
                    convexFigures.add(arr);
                    tempArr.remove(i);
                    i--;
                }
            }
            else {
                MyPoint p1 = tempArr.get(i - 1);
                MyPoint p2 = tempArr.get(i);
                MyPoint p3 = tempArr.get(i + 1);
                if (!isConvex(tempArr.get(i), tempArr)) {
                    continue;
                }
                if (i == 1) {
                    for (int j = tempArr.size() - 1; j < 0; j++) {
                        if (isInside(p1, p2, p3, tempArr.get(j))) {
                            q = true;
                            break;
                        }
                    }
                    if (!q) {
                        ArrayList<MyPoint> arr = new ArrayList<>(3);
                        arr.add(p1);
                        arr.add(p2);
                        arr.add(p3);
                        convexFigures.add(arr);
                        tempArr.remove(i);
                        i--;
                    }
                }
                else {
                    for (int j = 0; j < i - 1; j++) {
                        if (isInside(p1, p2, p3, tempArr.get(j))) {
                            q = true;
                            break;
                        }
                    }
                    for (int j = i + 2; j < tempArr.size(); j++) {
                        if (isInside(p1, p2, p3, tempArr.get(j))) {
                            q = true;
                            break;
                        }
                    }
                    if(!q){
                        ArrayList<MyPoint> arr = new ArrayList<>(3);
                        arr.add(p1);
                        arr.add(p2);
                        arr.add(p3);
                        convexFigures.add(arr);
                        tempArr.remove(i);
                        i--;
                    }
                }
            }
            if(tempArr.size() == 3){
                ArrayList<MyPoint> arr = new ArrayList<>(3);
                arr.addAll(tempArr);
                convexFigures.add(arr);
                break;
            }
            i++;
        }
        convexMas = convexFigures;
    }

    public MyPoint getCenterOfEdge(MyPoint p1, MyPoint p2){
        return new MyPoint(((int)(p1.getX()+p2.getX())/2),
                (int)((p1.getY()+p2.getY())/2) );
    }

    //INTERNAL FUNCS
    private int vecProd(ArrayList<MyPoint> tempArr, int ind1, int ind2, char xy){
        if(xy == 'x'){
            return tempArr.get(ind1).getX() - tempArr.get(ind2).getX();
        }
        else if(xy == 'y'){
            return tempArr.get(ind1).getY() - tempArr.get(ind2).getY();
        }
        else return 0;
    }

    private double func1(int x1, int y1, int x2, int y2){
        return -(x1*y2 - x2*y1);
    }

    private float func2(MyPoint p1, MyPoint p2, MyPoint g){
        return (p1.getX() - g.getX())*(p2.getY() - p1.getY()) - (p2.getX() - p1.getX())*(p1.getY() - g.getY());
    }

    private boolean isInside(MyPoint p1, MyPoint p2, MyPoint p3, MyPoint g){
        return (func2(p1, p2, g) > 0) && (func2(p2, p3, g) > 0) && (func2(p3, p1, g) > 0);
    }
}
