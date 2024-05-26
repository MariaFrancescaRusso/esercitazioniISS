| Design and build a new system, with the following requirements:
The system must control a chain of Led made of n>2: LedSegment.kt, so that:
1. R0 (START): the chain is activated (at time t0) when a command START is sent to it
2. R1 (BLINK): the Led-i (i=1..n) is turned-on for a prefixed time TIMEON, at time t0 + TIMEON*i
3. R2 (REPEAT): the chain blinking repeats
4. R3 (STOP): the chain blinking is stopped when the chain receives the command STOP |


Utilizzando la libreria it.unibo.bls19Local-1.0.jar è stato possibile realizzare un sistema software con un GUI Button e una Chain di GUI Led (LedSegmentHorizontal). È stata quindi realizzata una classe LedChain che implementa
l’interfaccia ILed (implementandone quindi i metodi turnOff, turnOn e getState) e che contiene una lista di LedSegment (con
relativi metodi get e set).
I metodi turnOn e turnOff di questa classe, quando chiamati dalla BLSApplicationLogic (al comando “START” dato alla
pressione del GUI Button in numero dispari e quindi al “BLINK/REPEAT” della chain), permettono rispettivamente di
accendere e spegnere in successione i led della chain (lista di LedSegment). Alla successiva pressione del GUI Button (in
numero pari) la BLSApplicationLogic mette in attesa il blink della chain (comando “STOP”).
All’interno della classe MainChainBLSFramework viene chiesto il numero di led da inserire all’interno del frame e della chain
e si crea il sistema, il frame e la chain ed il button da aggiungere al BlsFramework.