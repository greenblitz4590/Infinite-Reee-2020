package edu.greenblitz.bigRodika.commands.shooter.pidshooter;

import com.revrobotics.CANPIDController;
import edu.greenblitz.bigRodika.commands.funnel.inserter.InsertByConstant;
import edu.greenblitz.bigRodika.commands.funnel.pusher.PushByConstant;
import edu.greenblitz.bigRodika.commands.generic.WaitMiliSeconds;
import edu.greenblitz.bigRodika.commands.shooter.ShootByConstant;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.wpilibj2.command.ParallelCommandGroup;
import edu.wpi.first.wpilibj2.command.ParallelRaceGroup;
import edu.wpi.first.wpilibj2.command.SequentialCommandGroup;
import org.greenblitz.motion.pid.PIDObject;

public class FullyAutoThreeStage extends SequentialCommandGroup {

    public FullyAutoThreeStage(double target, double ff){

        // 0.6 = 3100
        // 0.48 = 2800

        addCommands(

                new ParallelRaceGroup(
                        new WaitUntilShooterSpeedClose(target, 100),
                        new ShootByConstant(1.0)
                ),

                new ParallelRaceGroup(
                        new ShootBySimplePid(
                                new PIDObject(0.0015,0.000004,0.0, ff), target
                        ),
                        new WaitUntilShooterSpeedClose(target, 5, 15) // Temp, replace by something better
                ),

                new ParallelCommandGroup(
                        new StageThreePID(
                                new PIDObject(0.002,0.000004,0.00015, ff), target
                        ),
                        new InsertByConstant(0.6),
                        new PushByConstant(0.7)
                )
        );



    }


}
