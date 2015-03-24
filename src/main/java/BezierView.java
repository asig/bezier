// Copyright 2015 Andreas Signer. All rights reserved.

import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.util.Arrays;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

public class BezierView extends JComponent implements MouseListener, MouseMotionListener {

    private final static int CONTROLPOINT_SIZE = 8;

    private int selectedControlPointIndex = -1;

    @Override
    public void mouseClicked(MouseEvent e) {
        System.out.println(e);
    }

    @Override
    public void mousePressed(MouseEvent e) {
        if (e.getButton() == 1) {
            selectedControlPointIndex = getControlPointIndex(e.getX(), e.getY());
        }
    }

    @Override
    public void mouseReleased(MouseEvent e) {
        if (e.getButton() == 1) {
            selectedControlPointIndex = -1;
        }
    }

    @Override
    public void mouseEntered(MouseEvent e) {
    }

    @Override
    public void mouseExited(MouseEvent e) {
    }

    @Override
    public void mouseDragged(MouseEvent e) {
        if (selectedControlPointIndex > -1) {
            Pt p = points.get(selectedControlPointIndex);
            p.x = e.getX();
            p.y = e.getY();
            repaint();
        }
    }

    @Override
    public void mouseMoved(MouseEvent e) {
        int ctrlPointIndex = getControlPointIndex(e.getX(), e.getY());
        int type = ctrlPointIndex < 0 ? Cursor.DEFAULT_CURSOR : Cursor.HAND_CURSOR;
        this.setCursor(Cursor.getPredefinedCursor(type));
    }

    public static class Pt {
        double x, y;

        public Pt(double x, double y) {
            this.x = x;
            this.y = y;
        }
    }

    private List<Pt> points = new LinkedList<Pt>();

    public BezierView() {
        points.add(new Pt(50,100));
        points.add(new Pt(150,50));
        points.add(new Pt(250,50));
        points.add(new Pt(350,100));

        this.addMouseListener(this);
        this.addMouseMotionListener(this);
    }

    @Override
    public void paint(Graphics g) {
        Graphics2D g2d = (Graphics2D)g;

        Rectangle bounds = this.getBounds();
        g2d.setColor(Color.WHITE);
        g2d.fillRect(0,0, bounds.width, bounds.height);

        Iterator<Pt> iter = points.iterator();
        Pt last = iter.next();
        while (iter.hasNext()) {
            Pt p1 = last;
            Pt p2 = iter.next();
            Pt p3 = iter.next();
            Pt p4 = iter.next();
            drawBezier(g2d, new Pt[] { p1, p2, p3, p4 });
            last = p4;
        }
    }

    private void drawBezier(Graphics2D g2d, Pt[] points) {
        g2d.setColor(Color.GREEN);
        for (int i = 0; i < points.length - 1; i++) {
            g2d.drawLine((int)points[i].x, (int)points[i].y, (int)points[i+1].x, (int)points[i+1].y);
        }

        g2d.setColor(Color.BLACK);
        Pt last = points[0];
        for (double t = 0; t <= 1; t += 0.005) {
            Pt[] p = Arrays.<Pt>copyOf(points, points.length);
            for (int l = p.length - 1; l >= 0; l--) {
                for (int i = 0; i < l; i++) {
                    p[i] = lerp(p[i], p[i+1], t);
                }
            }
            g2d.drawLine((int)last.x, (int)last.y, (int)p[0].x, (int)p[0].y);
            last = p[0];
        }

        g2d.setColor(Color.RED);
        for (Pt p : points) {
            drawControlPoint(g2d, p);
        }

    }

    private int getControlPointIndex(int x, int y) {
        int idx = 0;
        for (Pt p : points) {
            if (x > p.x - CONTROLPOINT_SIZE / 2 && x < p.x + CONTROLPOINT_SIZE / 2 && y > p.y - CONTROLPOINT_SIZE / 2 && y < p.y + CONTROLPOINT_SIZE / 2) {
                return idx;
            }
            idx++;
        }
        return -1;
    }

    private void drawControlPoint(Graphics2D g2d,Pt p) {
        g2d.drawRect((int)p.x - CONTROLPOINT_SIZE/2, (int)p.y - CONTROLPOINT_SIZE/2, CONTROLPOINT_SIZE, CONTROLPOINT_SIZE);
    }

    private Pt lerp(Pt p1, Pt p2, double t) {
        double s = 1-t;
        return new Pt(p1.x*s + p2.x*t, p1.y*s + p2.y*t);
    }
}
