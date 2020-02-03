package edu.greenblitz.bigRodika.commands.shifter;

import edu.greenblitz.gblib.gears.Gear;

public class ToSpeed extends ShifterCommand {

    @Override
    public void initialize() {
        shifter.setShift(Gear.SPEED);
    }

    @Override
    public boolean isFinished() {
        return true;
    }
}
