package edu.greenblitz.bigRodika.subsystems;

import com.ctre.phoenix.sensors.PigeonIMU;
import com.revrobotics.CANSparkMax;
import com.revrobotics.CANSparkMaxLowLevel;
import edu.greenblitz.bigRodika.OI;
import edu.greenblitz.bigRodika.RobotMap;
import edu.greenblitz.bigRodika.commands.chassis.driver.ArcadeDrive;
import edu.greenblitz.gblib.encoder.IEncoder;
import edu.greenblitz.gblib.encoder.SparkEncoder;
import edu.greenblitz.gblib.gyroscope.IGyroscope;
import edu.greenblitz.gblib.gyroscope.PigeonGyro;
import org.greenblitz.motion.Localizer;
import org.greenblitz.motion.base.Position;


public class Chassis extends GBSubsystem {
    private static Chassis instance;

    private CANSparkMax rightLeader, rightFollower1, rightFollower2, leftLeader, leftFollower1, leftFollower2;
    private IEncoder leftEncoder, rightEncoder;
    private IGyroscope gyroscope;
//    private PowerDistributionPanel robotPDP;
    private double lastLocationLeft;
    private double lastLocationRight;
    private double derivedLeftVel = 0;
    private double derivedRightVel = 0;
    private long lastTime;

    private Chassis() {
        rightLeader = new CANSparkMax(RobotMap.Limbo2.Chassis.Motor.RIGHT_LEADER, CANSparkMaxLowLevel.MotorType.kBrushless);
        rightFollower1 = new CANSparkMax(RobotMap.Limbo2.Chassis.Motor.RIGHT_FOLLOWER_1, CANSparkMaxLowLevel.MotorType.kBrushless);
        rightFollower2 = new CANSparkMax(RobotMap.Limbo2.Chassis.Motor.RIGHT_FOLLOWER_2, CANSparkMaxLowLevel.MotorType.kBrushless);
        leftLeader = new CANSparkMax(RobotMap.Limbo2.Chassis.Motor.LEFT_LEADER, CANSparkMaxLowLevel.MotorType.kBrushless);
        leftFollower1 = new CANSparkMax(RobotMap.Limbo2.Chassis.Motor.LEFT_FOLLOWER_1, CANSparkMaxLowLevel.MotorType.kBrushless);
        leftFollower2 = new CANSparkMax(RobotMap.Limbo2.Chassis.Motor.LEFT_FOLLOWER_2, CANSparkMaxLowLevel.MotorType.kBrushless);   //big-haim

        leftFollower1.follow(leftLeader);
        leftFollower2.follow(leftLeader);
        rightFollower1.follow(rightLeader);
        rightFollower2.follow(rightLeader);

        leftLeader.setInverted(true);
        leftFollower1.setInverted(true);
        leftFollower1.setInverted(true);

        leftEncoder = new SparkEncoder(RobotMap.Limbo2.Chassis.Encoder.NORM_CONST_SPARK, leftLeader);
        leftEncoder.invert(true);
        rightEncoder = new SparkEncoder(RobotMap.Limbo2.Chassis.Encoder.NORM_CONST_SPARK, rightLeader);
        rightEncoder.invert(true);

        gyroscope = new PigeonGyro(new PigeonIMU(0));   // chassis
//        gyroscope.inverse();

        lastTime = System.currentTimeMillis();
        lastLocationLeft = leftEncoder.getNormalizedTicks();
        lastLocationRight = rightEncoder.getNormalizedTicks();

    }

    public static void init() {
        if (instance == null) {
            instance = new Chassis();
            instance.setDefaultCommand(
                    new ArcadeDrive(OI.getInstance().getMainJoystick())
            );
        }
    }

    public static Chassis getInstance() {
        return instance;
    }

    public void changeGear() {
        leftEncoder.switchGear();
        rightEncoder.switchGear();
    }

    public void moveMotors(double left, double right) {
        putNumber("Left Power", left);
        putNumber("Right Power", right);
        rightLeader.set(right);
        leftLeader.set(left);
    }

    public void toBrake() {
        rightLeader.setIdleMode(CANSparkMax.IdleMode.kBrake);
        rightFollower1.setIdleMode(CANSparkMax.IdleMode.kBrake);
        rightFollower2.setIdleMode(CANSparkMax.IdleMode.kBrake);
        leftLeader.setIdleMode(CANSparkMax.IdleMode.kBrake);
        leftFollower1.setIdleMode(CANSparkMax.IdleMode.kBrake);
        leftFollower2.setIdleMode(CANSparkMax.IdleMode.kBrake);   //big-haim
    }

    public void toCoast() {
        rightLeader.setIdleMode(CANSparkMax.IdleMode.kCoast);
        rightFollower1.setIdleMode(CANSparkMax.IdleMode.kCoast);
        rightFollower2.setIdleMode(CANSparkMax.IdleMode.kCoast);
        leftLeader.setIdleMode(CANSparkMax.IdleMode.kCoast);
        leftFollower1.setIdleMode(CANSparkMax.IdleMode.kCoast);
        leftFollower2.setIdleMode(CANSparkMax.IdleMode.kCoast);   //big-haim
    }

    public void arcadeDrive(double moveValue, double rotateValue) {
        moveMotors(moveValue - rotateValue, moveValue + rotateValue);
    }

    public double getLeftMeters() {
        return leftEncoder.getNormalizedTicks();
    }

    public double getRightMeters() {
        return rightEncoder.getNormalizedTicks();
    }

    public double getLeftRate() {
        return leftEncoder.getNormalizedVelocity();
    }

    public double getRightRate() {
        return rightEncoder.getNormalizedVelocity();
    }

    public double getLinearVelocity() {
        return 0.5 * (getDerivedLeft() + getDerivedRight());
    }

    public double getAngularVelocityByWheels() {
        return getWheelDistance() * (getDerivedRight() - getDerivedLeft());
    }

    public double getAngle() {
        return gyroscope.getNormalizedYaw();
    }

    public double getRawAngle() {
        return gyroscope.getRawYaw();
    }

    public double getAngularVelocityByGyro() {
        return gyroscope.getYawRate();
    }

    public void resetGyro() {
        gyroscope.reset();
    }

    public double getWheelDistance() {
        return RobotMap.Limbo2.Chassis.WHEEL_DIST;
    }

    public Position getLocation() {
        return Localizer.getInstance().getLocation();
    }

    public double getDerivedLeft() {
        return derivedLeftVel;
    }

    public double getDerivedRight() {
        return derivedRightVel;
    }

    @Override
    public void periodic() {

        super.periodic();

        double dt = (System.currentTimeMillis() - lastTime) / 1000.0;
        double deltaLocLeft = leftEncoder.getNormalizedTicks() - lastLocationLeft;
        double deltaLocRight = rightEncoder.getNormalizedTicks() - lastLocationRight;

        derivedLeftVel = deltaLocLeft / dt;
        derivedRightVel = deltaLocRight / dt;

        lastLocationRight = lastLocationRight + deltaLocRight;
        lastLocationLeft = lastLocationLeft + deltaLocLeft;

        lastTime = System.currentTimeMillis();

        putNumber("left derv", derivedLeftVel);
        putNumber("right derv", derivedRightVel);
        putNumber("Angle", getAngularVelocityByWheels());
        putString("Location", Localizer.getInstance().getLocation().toString());

    }

    public void resetEncoders() {
        rightEncoder.reset();
        leftEncoder.reset();
    }

}
