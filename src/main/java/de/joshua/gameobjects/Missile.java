package de.joshua.gameobjects;

import de.joshua.util.Coordinate;
import de.joshua.util.GameObject;

import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.geom.Ellipse2D;

public class Missile extends GameObject {

    Coordinate position;
    double width;
    double height;
    double cannonLength;
    double turningDirection;
    double shootRange;
    double yShootRange;
    boolean rotateTrue = true;
    double canonDirection = 0;
    boolean firstRound = true;
    double endDirection = 0;
    String direction;
    public Missile(Coordinate position, double width, double height, double cannonLength, double turningDirection) {
        super(position, width, height);

        this.position = position;
        this.width = width;
        this.height = height;
        this.cannonLength = cannonLength;
        this.turningDirection = turningDirection;
    }

    public void changeMissileYShootRange(){
        if(firstRound){
            yShootRange = yShootRange * -1;
        }
    }
    public void setMissileDirection(String direction){
        this.direction = direction;
    }
    @Override
    public void paintMe(java.awt.Graphics g) {
        Graphics2D g2d = (Graphics2D) g;
        g2d.setColor(Color.GREEN);
        AffineTransform transform = new AffineTransform();
        Ellipse2D missileShape = new Ellipse2D.Double(getObjectPosition().getX() + getWidth() * (0.75 / 2) + cannonLength + shootRange * 10,
                getObjectPosition().getY() + getHeight() * (0.16 / 0.5) + yShootRange,
                10, 10);

        if (rotateTrue) {
            transform.rotate(turningDirection, getObjectPosition().getX() + getWidth() * 0.4, getObjectPosition().getY() + getHeight() * 0.4);
            canonDirection = canonDirection + turningDirection;
        } else{
            if (firstRound) {
                endDirection = canonDirection;
                firstRound = false;
            }
            canonDirection = canonDirection + turningDirection;
            transform.rotate(endDirection, getObjectPosition().getX() + getWidth() * 0.4, getObjectPosition().getY() + getHeight() * 0.4);
        }
        Shape transformedMissileShape = transform.createTransformedShape(missileShape);
        g2d.fill(transformedMissileShape);
    }

}
