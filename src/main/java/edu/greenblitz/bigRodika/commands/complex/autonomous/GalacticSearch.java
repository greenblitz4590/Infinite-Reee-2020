package edu.greenblitz.bigRodika.commands.complex.autonomous;

import edu.greenblitz.bigRodika.RobotMap;
import edu.greenblitz.bigRodika.commands.chassis.profiling.Follow2DProfileCommand;
import edu.greenblitz.bigRodika.commands.intake.ExtendAndCollect;
import edu.greenblitz.bigRodika.commands.intake.RetractAndStop;
import edu.greenblitz.bigRodika.commands.intake.roller.RollByConstant;
import edu.greenblitz.bigRodika.subsystems.Chassis;
import edu.greenblitz.gblib.threading.ThreadedCommand;
import edu.wpi.first.wpilibj2.command.ParallelCommandGroup;
import edu.wpi.first.wpilibj2.command.SequentialCommandGroup;
import edu.wpi.first.wpilibj2.command.WaitCommand;
import org.greenblitz.motion.base.Point;
import org.greenblitz.motion.base.Position;
import org.greenblitz.motion.base.State;

import java.util.ArrayList;

public class GalacticSearch extends ParallelCommandGroup {

    long startTime;


    public GalacticSearch(){

        ArrayList<State> redPathB = new ArrayList<>(){{

            add(new State(razToMeter(1.52), razToMeter(5),-2.23,0,0));
            add(new State(razToMeter(3), razToMeter(4),-2.23,3.6,7));
            add(new State(razToMeter(5), razToMeter(2),-1.55,3.6,9.5));
            add(new State(razToMeter(8), razToMeter(4),-2.2,3.6,-9.5));
            add(new State(razToMeter(10), razToMeter(-3),-2.7,0,0));

        }};
        ArrayList<State> bluePathA = new ArrayList<>(){
            {
                add(new State(razToMeter(2.1),razToMeter(2),-0.5*Math.PI,0,0));
                add(new State(razToMeter(6),razToMeter(1),-0.5,3.6,9.5));
                add(new State(razToMeter(4.5),razToMeter(3),-1.2,3.6,-2));
                add(new State(razToMeter(6),razToMeter(3 + 1.5*0.39),-1.2,3.6,0));
                add(new State(razToMeter(7),razToMeter(6),-0.4,0,0));
            }};

        ArrayList<State> bluePathB = new ArrayList<>(){{
            add(new State(razToMeter(0.9),razToMeter(1.5),-0.5*Math.PI,0,0));
            add(new State(razToMeter(6),razToMeter(2),-0.5*Math.PI + 1,1.0,3.6));
            add(new State(razToMeter(8),razToMeter(4),-1.95,3.6,-9.5));
            add(new State(razToMeter(11),razToMeter(3),-1.85,3.6,1));
            add(new State(razToMeter(12),razToMeter(3),-0.5*Math.PI,0,0));
        }};

        rotatePath(bluePathB,4, -1.5);



        Follow2DProfileCommand command = new Follow2DProfileCommand(bluePathB, RobotMap.Limbo2.Chassis.MotionData.CONFIG, 0.5
                , false);

        command.setSendData(true);


        addCommands(
                new SequentialCommandGroup(
                        new ExtendAndCollect(1){
                            @Override
                            public boolean isFinished(){
                                return (System.currentTimeMillis() - startTime)/1000.0 > 8;
                            }
                        },
                        new RetractAndStop()
                ),

                        new ThreadedCommand(command, Chassis.getInstance())
        );
    }

    @Override
    public void initialize(){
        super.initialize();
        startTime = System.currentTimeMillis();

    }


    public static double razToMeter(double raz){
        return 0.762 * raz;
    }

    public static void rotatePath(ArrayList<State> path, int index, double radians){
        Position rotateAxis = path.get(index-1);

        for( int i = index; i < path.size(); i++){
            Point curr = path.get(i);
            curr.set(curr.getX()-rotateAxis.getX(), curr.getY() - rotateAxis.getY());
            curr.rotate(-radians);
            curr.set(curr.getX() + rotateAxis.getX(), curr.getY() + rotateAxis.getY());
            path.set(i, new State(curr, path.get(i).getAngle() + radians,path.get(i).getLinearVelocity(), path.get(i).getAngularVelocity()));
        }

    }
}
