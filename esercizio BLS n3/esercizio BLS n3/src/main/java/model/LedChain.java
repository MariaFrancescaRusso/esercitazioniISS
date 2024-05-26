package model;

import it.unibo.bls.interfaces.IObserver;
import it.unibo.bls.utils.Utils;
import it.unibo.blsFramework.interfaces.ILedModel;
import it.unibo.chain.segment7.LedSegment;

import java.util.*;

public class LedChain extends Observable implements ILedModel
{
    private boolean ledState;
    private List<LedSegment> ledSegmentList;

    public LedChain()
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

    //Factory method
    public static ILedModel createLed()
    {
        return new LedChain();
    }

    public static ILedModel createLed(IObserver observer)
    {
        ILedModel ledChain = new LedChain();
        ledChain.addObserver(observer);
        return ledChain;
    }

    protected void update()
    {
        System.out.println("	LedChain | update state=" + ledState );
        this.setChanged();
        this.notifyObservers("" + ledState);		//Always a String
    }

    @Override
    public void turnOn()
    {
        ledState = true;
        update();

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
        update();

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

    @Override
    public void addObserver(IObserver iObserver)
    {
        if( iObserver != null )
            super.addObserver(iObserver);
    }
}