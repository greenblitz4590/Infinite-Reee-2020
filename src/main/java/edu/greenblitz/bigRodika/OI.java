package edu.greenblitz.bigRodika;

import edu.greenblitz.bigRodika.commands.chassis.driver.ArcadeDrive;
import edu.greenblitz.bigRodika.commands.chassis.motion.ChainFetch;
import edu.greenblitz.bigRodika.commands.chassis.motion.DumbAlign;
import edu.greenblitz.bigRodika.commands.chassis.motion.PreShoot;
import edu.greenblitz.bigRodika.commands.dome.DomeMoveByConstant;
import edu.greenblitz.bigRodika.commands.funnel.InsertIntoShooter;
import edu.greenblitz.bigRodika.commands.funnel.inserter.InsertByConstant;
import edu.greenblitz.bigRodika.commands.funnel.inserter.StopInserter;
import edu.greenblitz.bigRodika.commands.funnel.pusher.PushByConstant;
import edu.greenblitz.bigRodika.commands.funnel.pusher.StopPusher;
import edu.greenblitz.bigRodika.commands.intake.extender.ToggleExtender;
import edu.greenblitz.bigRodika.commands.intake.roller.RollByConstant;
import edu.greenblitz.bigRodika.commands.intake.roller.StopRoller;
import edu.greenblitz.bigRodika.commands.shooter.StopShooter;
import edu.greenblitz.bigRodika.commands.shooter.pidshooter.threestage.FullyAutoThreeStage;
import edu.greenblitz.bigRodika.commands.turret.MoveTurretByConstant;
import edu.greenblitz.bigRodika.commands.turret.ResetEncoderWhenInFront;
import edu.greenblitz.bigRodika.commands.turret.StopTurret;
import edu.greenblitz.bigRodika.commands.turret.TurretByVision;
import edu.greenblitz.bigRodika.utils.VisionMaster;
import edu.greenblitz.gblib.hid.SmartJoystick;
import edu.wpi.first.wpilibj2.command.ParallelCommandGroup;

public class OI {
    private static OI instance;

    private SmartJoystick mainJoystick;
    private SmartJoystick secondStick;

    private OI() {
        mainJoystick = new SmartJoystick(RobotMap.Limbo2.Joystick.MAIN,
                RobotMap.Limbo2.Joystick.MAIN_DEADZONE);
        secondStick = new SmartJoystick(RobotMap.Limbo2.Joystick.SIDE,
                RobotMap.Limbo2.Joystick.SIDE_DEADZONE);

        initTestButtons();
//        initOfficalButtons();
    }

    public static OI getInstance() {
        if (instance == null) {
            instance = new OI();
        }
        return instance;
    }

    private void initTestButtons() {

        //mainJoystick.R1.whileHeld(new ChainFetch(5, mainJoystick));
        //mainJoystick.R1.whenReleased(new ArcadeDrive(mainJoystick));
        mainJoystick.B.whenPressed(new DumbAlign(5.0, 0.1, 0.5));
        mainJoystick.X.whenPressed(new ResetEncoderWhenInFront());

        secondStick.L3.whenPressed(new ResetEncoderWhenInFront());
        secondStick.A.whenPressed(new TurretByVision(VisionMaster.Algorithm.HEXAGON));
        secondStick.A.whenReleased(new StopTurret());

        secondStick.POV_UP.whenPressed(new DomeMoveByConstant(0.3));
        secondStick.POV_UP.whenReleased(new DomeMoveByConstant(0));
        secondStick.POV_DOWN.whenPressed(new DomeMoveByConstant(-0.3));
        secondStick.POV_DOWN.whenReleased(new DomeMoveByConstant(0));


        secondStick.START.whenPressed(new MoveTurretByConstant(0.3));
        secondStick.START.whenReleased(new StopTurret());

        secondStick.BACK.whenPressed(new MoveTurretByConstant(-0.3));
        secondStick.BACK.whenReleased(new StopTurret());

    }

    private void initOfficalButtons() {

        mainJoystick.R1.whileHeld(new ChainFetch(5, mainJoystick));
        mainJoystick.R1.whenReleased(new ArcadeDrive(mainJoystick));

        mainJoystick.L1.whileHeld(new PreShoot(5.5));

//        mainJoystick.L3.whenReleased(new ToggleShift());

        secondStick.R1.whenPressed(new FullyAutoThreeStage(4500, 0.8));
        secondStick.R1.whenReleased(new StopShooter());

        secondStick.L1.whileHeld(new InsertIntoShooter(1, 0.3, 0.5));
        secondStick.L1.whenReleased(new ParallelCommandGroup(new StopPusher(),
                new StopInserter(), new StopRoller()));

        secondStick.Y.whileHeld(new
                ParallelCommandGroup(new PushByConstant(-0.7), new InsertByConstant(-0.6)));
        secondStick.Y.whenReleased(new ParallelCommandGroup(new StopPusher(), new StopInserter()));

        secondStick.B.whenPressed(new ToggleExtender());
        secondStick.A.whileHeld(new RollByConstant(1.0));

        mainJoystick.A.whenPressed(new TurretByVision(VisionMaster.Algorithm.HEXAGON));
        mainJoystick.A.whenReleased(new StopTurret());

        mainJoystick.START.whenPressed(new MoveTurretByConstant(0.3));
        mainJoystick.START.whenReleased(new MoveTurretByConstant(0));

        mainJoystick.BACK.whenPressed(new MoveTurretByConstant(-0.3));
        mainJoystick.BACK.whenReleased(new MoveTurretByConstant(0));

        secondStick.START.whenPressed(new DomeMoveByConstant(0.3));
        secondStick.START.whenReleased(new DomeMoveByConstant(0));

        secondStick.BACK.whenPressed(new DomeMoveByConstant(-0.3));
        secondStick.BACK.whenReleased(new DomeMoveByConstant(0));
    }

    public SmartJoystick getMainJoystick() {
        return mainJoystick;
    }

    public SmartJoystick getSideStick() {
        return secondStick;
    }
}