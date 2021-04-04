package edu.greenblitz.bigRodika.commands.chassis.test;

import edu.greenblitz.bigRodika.commands.chassis.ChassisCommand;
import edu.greenblitz.bigRodika.subsystems.Chassis;
import org.greenblitz.debug.RemoteCSVTarget;
import org.greenblitz.debug.RemoteCSVTargetBuffer;

//TODO: add CSV
public class CheckMaxRot extends ChassisCommand {

    int count;
    private double power;
    private double previousAngle;
    private double previousVel;
    private double previousTime;
    private double prevGyroV;
    private double prevWheelV;
    private long tStart;
    private RemoteCSVTargetBuffer target;

    public CheckMaxRot(double power) {
        this.power = power;
    }

    @Override
    public void initialize() {
        previousTime = System.currentTimeMillis() / 1000.0;
        previousAngle = Chassis.getInstance().getLocation().getAngle();
        previousVel = 0;
        count = 0;
        tStart = System.currentTimeMillis();
        target = new RemoteCSVTargetBuffer("RotationalData", "time", "vel", "gyroVel", "wheelVel", "acc","gyroA", "WheelAccByVel");
    }

    @Override
    public void execute() {
        count++;

        while (System.currentTimeMillis() - tStart < 5000) {
            Chassis.getInstance().moveMotors(-power, power);

            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            double time = System.currentTimeMillis() / 1000.0;
            double dt = time - previousTime;
            double angle = Chassis.getInstance().getLocation().getAngle();
            double V = (angle - previousAngle) / dt;
            double GyroV = Chassis .getInstance().getAngularVelocityByGyro();
            double WheelV = Chassis.getInstance().getAngularVelocityByWheels();
            double GyroA = (GyroV - prevGyroV)/dt;
            double WheelA = (WheelV - prevWheelV)/dt;

            double A =(V - previousVel) / (time - previousTime);
            if(Math.abs(A )<= 100) target.report(time -  tStart/1000.0, V,GyroV,WheelV, A, GyroA, WheelA);

            previousAngle = angle;
            previousTime = time;
            previousVel = V;
            prevWheelV = WheelV;
            prevGyroV = GyroV;



        }
    }

    @Override
    public void end(boolean interrupted){
        target.passToCSV(true);
    }
    @Override
    public boolean isFinished() {
        return System.currentTimeMillis() - tStart > 5000;
    }
}
