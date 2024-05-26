package appLogic;

import it.unibo.bls.interfaces.ILed;
import it.unibo.blsFramework.interfaces.IAppLogic;
import it.unibo.blsFramework.interfaces.ILedModel;

public class BLSApplicationLogicChain implements IAppLogic
{
    private ILed ledChain;
    private int numOfCalls = 0;
    private boolean doBlink = false;
    private final Object monitor = new Object();

    @Override
    public void setControlled(ILedModel iLedModel)
    {
        this.ledChain = iLedModel;
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

                try
                {
                    while(true)
                    {
                        synchronized(BLSApplicationLogicChain.this.monitor)
                        {
                            /*if (BLSApplicationLogicChain.this.doBlink)
                                BLSApplicationLogicChain.this.ledChain.turnOff();
                                //in questo modo smette turnOn ma aspetta con led acceso finchè successione non finisce*/

                            while(!BLSApplicationLogicChain.this.doBlink)
                            {
                                BLSApplicationLogicChain.this.monitor.wait();
                                BLSApplicationLogicChain.this.ledChain.turnOn(); //change led status
                                //in questo caso parte con turnOn (senza parte con turnOff)
                            }
                        }

                        BLSApplicationLogicChain.this.switchTheLed();
                    }
                } catch (Exception var4) {
                }
            }
        }).start();
    }

    protected void switchTheLed()
    {
        if (this.ledChain != null)
        {
            if (this.ledChain.getState())
            {
                System.out.println("	LedChain | update state=" + this.ledChain.getState());
                this.ledChain.turnOff();
            }
            else
            {
                System.out.println("	LedChain | update state=" + this.ledChain.getState());
                this.ledChain.turnOn();
            }

        }
    }
}
