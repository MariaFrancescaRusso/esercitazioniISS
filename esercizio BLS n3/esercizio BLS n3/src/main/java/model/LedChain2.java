package model;

import it.unibo.bls.interfaces.ILed;
import it.unibo.bls.utils.Utils;
import it.unibo.chain.segment7.LedSegment;

import java.util.ArrayList;
import java.util.List;

public class LedChain2 implements ILed
{
    private boolean ledState;
    private List<LedSegment> ledSegmentList;

    public LedChain2()
    {
        this.ledSegmentList = new ArrayList<>();
        this.ledState  = false;
    }

    public List<LedSegment> getLedSegmentList()
    {
        return ledSegmentList;
    }

    public void setLedSegmentList(List<LedSegment> ledSegmentList)
    {
        this.ledSegmentList = ledSegmentList;
    }

    @Override
    public void turnOn()
    {
        ledState = true;

        for (LedSegment ledSegment : this.getLedSegmentList())
        {
            ledSegment.turnOn();
            System.out.println("ledSegment " + (this.getLedSegmentList().indexOf(ledSegment)+1) + " : turnOn");
            Utils.delay(500);
            ledSegment.turnOff();
        }
    }

    @Override
    public void turnOff()
    {
        ledState = false;

        for (LedSegment ledSegment : this.ledSegmentList)
        {
            ledSegment.turnOff();
            System.out.println("ledSegment " + (this.getLedSegmentList().indexOf(ledSegment)+1) + " : turnOff");
        }
    }

    @Override
    public boolean getState()
    {
        return ledState;
    }
}