package kotlinBLS.distributedBLS

import it.unibo.`is`.interfaces.protocols.IConnInteraction
import it.unibo.kactor.ActorBasic
import it.unibo.kactor.ApplMessage
import it.unibo.supports.FactoryProtocol

class Proxy (name: String, protocol: String, host: String, port: Int) : ActorBasic(name)
{
    private var connection: IConnInteraction? = null

    init
    {
        when (protocol)
        {
            "TCP", "UDP" -> {
                val factoryProtocol = FactoryProtocol(null, protocol, name)
                try
                {
                    connection = factoryProtocol.createClientProtocolSupport(host, port)
                }
                catch( e: Exception )
                {
                    println("NO connection to $host")
                }
            }

            "SERIAL" -> {
                val  factoryProtocol =  FactoryProtocol(null, protocol, "")
                connection = factoryProtocol.createSerialProtocolSupport("")
            }

            else -> println("protocol $protocol unknown!")
        }
    }

    override suspend fun actorBody(msg: ApplMessage)
    {
        println("${msg.msgReceiver()}: ricevuto messaggio ${msg.msgContent()} da ${msg.msgSender()}")
        //ricevo msg da Button
        //invio msg a Server
        connection?.sendALine("$msg")
    }
}