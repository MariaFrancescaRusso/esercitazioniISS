package appLogic;

import it.unibo.bls.interfaces.ILed;
import it.unibo.bls.utils.Utils;
import it.unibo.blsFramework.interfaces.IAppLogic;
import it.unibo.blsFramework.interfaces.ILedModel;

public class BLSApplicationLogic implements IAppLogic
{
    private ILed led;
    private int numOfCalls = 0;
    private boolean doBlink = false;
    private final Object monitor = new Object();

    @Override
    public void setControlled(ILedModel iLedModel)
    {
        this.led = iLedModel;
        this.doBlinkTheLedWaiting();
    }

    @Override
    public void execute(String cmd)
    {
        System.out.println("\tBlsApplicationLogic | execute cmd=" + cmd);
        if (cmd.equals("stop"))
        {
            this.doBlink = false;
        }
        else /* cmd = "start" */
        {
            ++this.numOfCalls;
            this.doBlink = this.numOfCalls % 2 != 0;
            System.out.println("\tBlsApplicationLogic | numOfCalls=" + this.numOfCalls + " doBlink=" + this.doBlink);
            synchronized(this.monitor)
            {
                this.monitor.notify();
            }
        }
    }

    @Override
    public int getNumOfCalls()
    {
        return this.numOfCalls;
    }

    private void doBlinkTheLedWaiting()
    {
        (new Thread() {
            public void run()
            {
                System.out.println("\tBlsApplicationLogic | doBlinkTheLedWaiting Thread STARTS   ...");

                try {
                    while(true)
                    {
                        synchronized(BLSApplicationLogic.this.monitor)
                        {
                            while(!BLSApplicationLogic.this.doBlink)
                            {
                                BLSApplicationLogic.this.monitor.wait();
                            }
                        }

                        BLSApplicationLogic.this.switchTheLed();
                        Utils.delay(250);
                    }
                } catch (Exception var4) {
                }
            }
        }).start();
    }

    protected void switchTheLed()
    {
        if (this.led != null)
        {
            if (this.led.getState())
            {
                this.led.turnOff();
            }
            else {
                this.led.turnOn();
            }

        }
    }
}
