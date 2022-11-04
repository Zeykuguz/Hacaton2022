package motherboarddest;

import robocode.*;
import robocode.*;
import robocode.Robot;

import java.awt.*;

import static robocode.util.Utils.normalRelativeAngleDegrees;

public class MotherBoardDestroyers extends Robot {

    double moveAmount;
    double steps;
    double bfHeight;
    double bfWidth;

    final int stepsToMove = 50;

    boolean hasDamage = false;

    boolean moveRight = true;
    boolean fired = false;

    double lastDistance = 0;

    double lastbearingFromGun = 1;

    boolean allowMove = true;

    public void run() {
        this.setBodyColor(Color.black);
        this.setGunColor(Color.black);
        this.setRadarColor(Color.orange);
        this.setBulletColor(Color.cyan);
        this.setScanColor(Color.cyan);

        this.steps = 0;

        this.bfHeight = this.getBattleFieldHeight();
        this.bfWidth = this.getBattleFieldWidth();

        this.moveAmount = Math.max(this.bfWidth, this.bfHeight);

        this.turnLeft(this.getHeading() % 90.0D);
        this.ahead(this.moveAmount);

        this.turnGunRight(360.0D);

        this.setAdjustGunForRobotTurn(true);

        while (true) {
            this.move();
        }
    }

    public void move() {
        if (!fired) {
            turnGunRight(360);
        }
        int extraMove = 0;

        if (allowMove) {
            if (hasDamage) {
                extraMove += 50;
                hasDamage = false;
            }

            if (moveRight) {
                this.ahead(this.stepsToMove + extraMove);
            } else {
                this.ahead(-this.stepsToMove - extraMove);
            }
            turnGunRight(20);
            turnGunLeft(40);
        }

        fired = false;

    }

    public void onScannedRobot(ScannedRobotEvent e) {
        // Calculate exact location of the robot
        double absoluteBearing = getHeading() + e.getBearing();
        double bearingFromGun = normalRelativeAngleDegrees(absoluteBearing - getGunHeading());

        turnGunRight(bearingFromGun);
        double distance = e.getDistance();

        if (lastDistance == 0) {
            lastDistance = distance;
        }


        if (lastbearingFromGun == bearingFromGun) {
            allowMove = false;
            fire(Math.abs(Math.min(5, getEnergy() - .1)));
        } else {
            if (distance < 300 ) {
                fire(Math.abs(Math.min(2, getEnergy() - .1)));
            } else if (distance < 450) {
                fire(Math.abs(Math.min(1, getEnergy() - .1)));
            } else if (distance < 600) {
                fire(Math.abs(Math.min(0.5, getEnergy() - .1)));
            } else {
                fire(Math.abs(Math.min(0.25, getEnergy() - .1)));
            }
            allowMove = true;
        }

        lastbearingFromGun = bearingFromGun;
        fired = true;
        if (distance < 300 && lastDistance > distance) {
            this.moveRight = false;
        } else if (distance > 300) {
            this.moveRight = true;
        }

        this.lastDistance = distance;

        this.move();
    }


    /**
     * onHitByBullet: What to do when you're hit by a bullet
     */
    public void onHitByBullet(HitByBulletEvent e) {
        hasDamage = true;
    }

    /**
     * onHitWall: What to do when you hit a wall
     */
    public void onHitWall(HitWallEvent e) {
        this.steps = 0;
        this.turnRight(90.0D);
    }

    public void onWin(WinEvent e) {
        for (int i = 0; i < 50; ++i) {
            this.turnRight(30.0D);
            this.turnLeft(30.0D);
        }
    }
}
