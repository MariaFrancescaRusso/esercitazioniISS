package kotlinBLS.elementsGUI

import it.unibo.bls.devices.gui.ButtonAsGui
import it.unibo.bls.devices.gui.ButtonBasic
import it.unibo.bls.interfaces.IButtonObservable
import it.unibo.bls.interfaces.IObserver
import it.unibo.bls.utils.Utils
import java.awt.Frame
import java.awt.GridLayout
import java.awt.event.ActionEvent
import java.awt.event.ActionListener
import java.util.*
import javax.swing.JFrame

class ButtonGui (cmd: String)  : Observable(), IButtonObservable, ActionListener
{
//    public fun createButton(cmd: String): ButtonGui
    init
    {
//        val button = ButtonGui()
        val jFrame = JFrame()
        jFrame.setSize(150, 80)
        jFrame.layout = GridLayout(1, 2)
        jFrame.isVisible = true
        ButtonBasic(jFrame, cmd, this)
//        return button
    }

    override fun addObserver(observer: IObserver)
    {
        super.addObserver(observer)
    }

    override fun actionPerformed(e: ActionEvent)
    {
        this.setChanged()
        this.notifyObservers(e.actionCommand)
    }

}