package main;

import appLogic.BLSApplicationLogic;
import appLogic.BLSApplicationLogicChain;
import it.unibo.bls.devices.gui.ButtonAsGui;
import it.unibo.bls.utils.Utils;
import it.unibo.blsFramework.appl.BlsFramework;
import it.unibo.blsFramework.interfaces.IBlsFramework;
import it.unibo.chain.appl.LedInChainCtrlActor;
import it.unibo.chain.appl.LedSegmChainSystemDistr;
import it.unibo.chain.appl.LedSegmChainSystemMonolitic;
import it.unibo.chain.model.ModelChainSystemMonolitic;
import it.unibo.chain.segment7.LedSegmHorizontal;
import it.unibo.chain.segment7.LedSegment;

import javax.swing.*;
import java.awt.*;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class MainBLSFramework extends BlsFramework
{
    public MainBLSFramework (String cmdName)
    {
        super(cmdName);
    }

    private static int getNumLed()
    {
        int numLed;

        System.out.println("How many horizontal virtual leds do you want in BLSystem? (int >= 3 (default))");
        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
        try
        {
            numLed = Integer.parseInt(br.readLine());
        }
        catch (IOException | NumberFormatException e)
        {
            System.err.println("Insert a number! By default number of led is 3..");
            numLed = 3;
        }

        if (numLed < 3)
        {
            System.err.println("The number of leds must be >= 3! By default number of led is 3..");
            numLed = 3;
        }

        return numLed;
    }

    public static void main(String[] args)
    {
        /* request about how many virtual leds in the BlSystem */
        int numLed = getNumLed();
        System.out.println("Number of horizontal virtual leds: " + numLed);

        /* create BLSystem */
        IBlsFramework blSystem = BlsFramework.createTheSystem("BLSF");

        /* create JFrame */
        JFrame jFrame = new JFrame();
        jFrame.setSize(120*numLed, 100);
        jFrame.setLayout(new GridLayout(1, 2));
        jFrame.setVisible(true);

        /* create virtual leds */
        for (int i=0; i<numLed; i++)
        {
            String ledName = "led" + (i+1);
            LedSegmHorizontal ledSegmHorizontal = new LedSegmHorizontal(ledName, 10, 5);

            /* add led to JFrame */
            jFrame.add(ledSegmHorizontal);

            /* add led to BlsFramework */
            if (i == 0)
                blSystem.setConcreteLed(ledSegmHorizontal);
            else
                blSystem.addConcreteLed(ledSegmHorizontal);
        }

        /* add Arduino Led to BlsFramework */
        //blSystem.addConcreteLed(LedProxyArduino.create(DeviceConfig.serialPortNum, DeviceConfig.serialBaudrate));

        /* add button to BlsFramework */
        blSystem.addConcreteButton(ButtonAsGui.createButton("Click Button"));

        Utils.delay(5000);
        System.out.println(" ================== CHANGE CONTROL ================= ");
        blSystem.setApplLogic(new BLSApplicationLogic());
    }
}