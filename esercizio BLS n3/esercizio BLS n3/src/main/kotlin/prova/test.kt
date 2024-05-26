package prova

import kotlinx.coroutines.*
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.channels.SendChannel
import kotlinx.coroutines.channels.actor
import kotlin.system.measureTimeMillis

/** val indica una variabile non modificabile **/
val counter : Int = 0

/** var indica una variabile modificabile **/
var counter2 : Int = 0

/** fun: dichiarazione di una funzione
  * per indicare parametri della funzione si usa "nome:Tipo"
  * per indicare il valore da restuire si usa ": Tipo" **/
fun sum(a:Int, b:Int) : Int {
    return a+b
}

var counter3 = 0
/** funzioni che non fanno riferimento a variabili locali; sono funzioni da evitare **/
fun incCounter() : Unit { counter3++ }
fun decCounter() { counter3-- }

/** funzione che restituisce un'altra funzione **/
fun counterCreate()  : ( cmd : String ) -> Int {
    /** variabile locale (dentro alla funzione) allocata nello stack **/
    var localCounter = 0
    return {
        /** it variabile riconosciuta ma si può cambiare dichiarandola prima.
         * Ad esempio: return { msg ->
         *             when (msg) { **/
        when (it) {
            /** cambia la variabile locale relativa alla macchina **/
            "inc" -> ++localCounter
            "dec" -> --localCounter
            "val" -> localCounter
            else -> throw Exception( "unknown" )
        }
    }
}

fun getInput() : String {
    println("Input  ...")
    return "myinput"
}
fun submit( v: Int, msg: String ) : String {
    println("Submit ...")
    return "$msg-$v"
}
/** funzione per conoscere il thread corrente **/
fun curThread() : String {
    return "thread=${Thread.currentThread().name}"
}
fun handle( msg: String ) {
    println("Handle $msg ${curThread()}")
}
fun doJob(n:Int) {
    val s = getInput()
    val v = submit( n, s )
    handle( v )
}

/** tramite le callback questa funzione (doJobCps) sarà più veloce di quella precedente (doJob)
 *  in quanto deve solo aspettare le "chiusure" e non che terminino ogni funzione col return **/
fun doJobCps( n: Int  ) {
    getInputCps(
        { input -> submitCps( n, input, {
                msg ->  handle( msg )
        }//handle
        )}//submitCps
    )//getInputCps
}
/** funzioni che terminano con una "chiusura" callback **/
fun getInputCps( callback:( String )-> Unit):Unit {
    println("Input  ...${curThread()}")
    Thread.sleep(1000)  /** se si aggiunge sleep allora nell'esecuzione si aspetterà 1 secondo
                          * e poi continuerà (procedimento Sincrono) **/
    callback( "myinputcps" )
}
fun submitCps(v:Int,msg:String,callback:(String)->Unit) {
    println("Submit ... ${curThread()}")
    callback( "$msg-$v" )
}

/** nel caso Asincrono si utilizza lo stesso thread per evitare conflitti;
  * per evitare che vengano eseguiti in disordine **/
fun getInputAsynchCps(  callback : ( String ) -> Unit ) : Unit {
    /** Single Abstract Method conversion (SAM) in kotlin.concurrent.thread:
      * when an object implements a SAM interface, we can pass a lambda instead. **/
    /** con questa notazione kotlin (kotlin.concurrent.thread(start = true)) si lancia un nuovo thread **/
    kotlin.concurrent.thread(start = true) {
        println("Input  ...")
        Thread.sleep(1000)
        println("Input received ")
        callback( "myinputasynchcps" )
    }
}
fun doJobAsynchCps( n: Int  ) {
    getInputAsynchCps(
        { input -> submitCps( n, input, {
                msg ->  handle( msg )
        }//handle
        )}//submitCps
    )//getInputAsynchCps
}

/** suspend indica che la funzione è sospendibile **/
suspend fun delayedFun() {
    println("delayedFun STARTS ${curThread()}")
    /** kotlinx.coroutines.delay può essere utilizzata solo con funzioni suspend o coroutine **/
    kotlinx.coroutines.delay(500)
    println("delayedFun ENDS ")
}

suspend fun ioBoundFun() {
    val timeElapsed = measureTimeMillis {
        println("IO operation ${curThread()}")
        kotlinx.coroutines.delay(500)
    }
    println("Done, time=$timeElapsed")
}

suspend fun activate() {
    /** async per lanciare più coroutine in parallelo **/
    val job1 = GlobalScope.async{
        ioBoundFun()
    }
    val job2 = GlobalScope.async{
        ioBoundFun()
    }
    val job3 = GlobalScope.async{
        ioBoundFun()
    }
    val job4 = GlobalScope.async{
        ioBoundFun()
    }
    val job5 = GlobalScope.async{
        ioBoundFun()
    }
    if(! job1.isCompleted || ! job2.isCompleted)
        println("Waiting for completion ${curThread()}")
    /** await per attendere il completamento dei job (--> si ha quando terminano il body) **/
    val end1 = job1.await()
    val end2 = job2.await()
    val end3 = job2.await()
    val end4 = job2.await()
    val end5 = job2.await()
    println("All jobs done")
}

suspend fun launchAction(i:Int,scope:CoroutineScope){
    launchDefault(i,scope)
}
suspend fun launchDefault(i:Int,scope:CoroutineScope){
    scope.launch( Dispatchers.Default ){ action(i) }
}
fun action(i: Int) { println("hello $i curThread=${curThread()}") }

suspend fun launchActionIO(i:Int,scope:CoroutineScope){
    launchIO(i,scope)
}
suspend fun launchIO(i:Int,scope:CoroutineScope){
    scope.launch( Dispatchers.IO ){ action(i) }
}

val th = newSingleThreadContext("My Thread")
suspend fun launchActionSingle(i:Int,scope:CoroutineScope){
    launchSingle(i,scope)
}
suspend fun launchSingle(i:Int,scope:CoroutineScope){
    scope.launch( th ){ action(i) }
}

var counter4 = 0
/** concurrency **/
/* funzione che incrementa un counter 1000 volte */
suspend fun CoroutineScope.massiveRun (action: suspend () -> Unit) {
    val n=100  //number of coroutines to launch
    val k=1000 //times an action is repeated by each coroutine
    val time = measureTimeMillis {
        val jobs = List(n) {
            launch {
                repeat(k) { action() }
            }
        }
        jobs.forEach { it.join() }
    }
    println("Completed ${n * k} actions in $time ms")
}

var counter5 = 0
suspend fun channelTest() {
    val timeElapsed = measureTimeMillis {
        val n = 5
        val channel = Channel<Int>(2)

        val sender = GlobalScope.launch {
            repeat( n ) {
                channel.send(it)
                println("SENDER | sent $it")
            }
        }
        delay(500) //The receiver starts after a while ...
        val receiver = GlobalScope.launch {
            for( i in 1..n ) {
                val v = channel.receive()
                println("RECEIVER | receives $v")
            }
        }

        delay(3000)
    }
    println("Done. time=$timeElapsed")
}

class CounterMsg(
    val cmd:String,val response:CompletableDeferred<Int>?=null){ }
/** actor **/
fun CoroutineScope.counterActor() : SendChannel<CounterMsg> = actor {
        var localCounter = 0 // actor state
        for (msg in channel) { // iterate over incoming messages
            when ( msg.cmd ) {
                "INC" -> localCounter++
                "DEC" -> localCounter--
                /** diversamente da prima, con gli actor per restituire qualcosa --> si deve inviare un messaggio **/
                "GET" -> msg.response?.complete(localCounter)
                else -> throw Exception( "unknown" )
            }    }
    }

/** il triangolo verde indica che è riconosciuto un entry point (main) **/
fun main() : Unit   /** se : Tipo è Unit --> indica void e può essere omesso **/
{
    var i = 0

    println("/****** ESERCIZIO ${i++} *******/")
    /** counter++ non si può fare perchè val non è modificabile **/
    /** $ permette di scrivere il valore di counter senza la concatenazione (necessaria invece in java) **/
    println("Counter val = $counter") //Counter val = 0

    println("/****** ESERCIZIO ${i++} *******/")
    counter2++
    println("Counter var = $counter2") //Counter var = 1

    println("/****** ESERCIZIO ${i++} *******/")
    val name = "Bob"
    val str = "Hello $name"
    println( str )	//Hello Bob

    println("/****** ESERCIZIO ${i++} *******/")
    val name2 : String? = null
    val v2 = name2?.length ?: 0
    println("v = $v2")	//0

    println("/****** ESERCIZIO ${i++} *******/")
    val name3 : String? = "a"
    val v3 = name3?.length ?: 0
    println("v = $v3")	//1

    println("/****** ESERCIZIO ${i++} *******/")
    /** Any in kotlin equivale a Object in java **/
    /** is dice se è vero o falso qualcosa **/
    val v : Any = 23    //v instanziata come Any ma gli viene associato un numero --> quindi è Int
    println( "v is String=${v is String}") //false
    println( "v is Int=${v is Int}")	//true

    println("/****** ESERCIZIO ${i++} *******/")
    val v4 : Any = "Bob"
    println("$v4") // stampa valore associato all'instanza Any di v4
    //val n = v4.length //ERROR --> serve il cast a String
    val n = (v4 as String).length
    println( "$n") //3
    //println( "${v4 as Int}") //java.lang.ClassCastException --> ? permette di considerare anche il null
    println( "${v4 as? Int}")  //null

    println("/****** ESERCIZIO ${i++} *******/")
    val t : Int = v4 as? Int ?: 100
    println( "$t") //100  --> perchè v4 non è instanziato a Int

    println("/****** ESERCIZIO ${i++} *******/")
    /** associazione del tipo a val o var è fatta in automatico da kotlin (inferenza di tipo) **/
    val s1 = "a"
    var s2 = "a"
    println( "s1 === s2 : ${s1 === s2} ") //true --> perchè il compilatore ottimizza e fa si che puntino allo stesso valore "a"
    println( "s1 == s2  : ${s1 == s2} ") //true

    println("/****** ESERCIZIO ${i++} *******/")
    val aToz = "a".."z"
    /** .. per definire un intervallo (range) **/
    val q = "q"
    println( "q in aToz = ${q in aToz}") //true
    println( "1 in aToz = ${"""1""" in aToz}") //false
    /** """ per definire valore da considerare **/

    println("/****** ESERCIZIO ${i++} *******/")
    /** arrayOf per dichiarare un array **/
    var a = arrayOf(1,2,3)
    println( "array of size=${a.size}")
    println( "firstEl=${a[0]}  lastEl=${a[a.size-1]}")

    println("/****** ESERCIZIO ${i++} *******/")
    println( " ${sum(3,6)} " ) //9

    println("/****** ESERCIZIO ${i++} *******/")
    val res = sum(2, 5)
    println("Somma = $res")

    println("/****** ESERCIZIO ${i++} *******/")
    println( "pre=$counter3  " ) //pre=0
    incCounter()
    println( "postInc=$counter3 " ) //post=1
    decCounter()
    println( "postDec=$counter3  " ) //pre=0

    println("/****** ESERCIZIO ${i++} *******/")
    /** inline function : definizione di una funzione di tipo inline **/
    /** = indica quindi il return della funzione **/
    fun square(v: Int) = v * v
    /** ${} per indicare il valore della funzione **/
    println( " ${square(3)} " ) //9

    println("/****** ESERCIZIO ${i++} *******/")
    /** function literal --> 2 tipi: lambda expression e **/
    /** val che indicano delle funzioni **/
    /** val dichiarati ma non inizializzati **/
    val action : () -> Unit
    //function type (no args, returns nothing useful (Unit)
    val sum : ( Int,  Int) -> Int
    //function type (two args, returns an Int)
    val greet: ( String )-> ()->Unit
    //ingresso una String e returns another function //--> senza ingressi e uscita void
    /** lambda expression : associo qualcosa da fare all'interno della val
     *                      --> quindi inserisco qualcosa all'interno del body della funzione **/
    /** essendo val --> allora una volta associata una lambda expression non può essere modificata **/
    action = { println("hello") }   //è una rappresentazione di una lambda
    /** può essere chiamata come una funzione con nulla in ingresso, come da definizione della funzione **/
    action() //hello
    sum = { x:Int, y:Int -> x+y }  //lambda expression
    println("$sum") // restituisce rappresentazione della fun sum: (kotlin.Int, kotlin.Int) -> kotlin.Int
    val a2 = sum(1,2)
    println("a=$a2")    //a=3
    /** seconde parentesi () e {} perchè restituisce una funzione (chiusure lessicali) **/
    greet = { m: String -> {println(m)}  }
    greet( "Hello World" )() //Hello World

    println("/****** ESERCIZIO ${i++} *******/")
    val fl = { print( "Last exp val=" ); 100 }
    /** val fl legata a una funzione senza ingresso nè uscita; ha però ha un body **/
    /** con println se chiamo fl() --> con () allora eseguo il body
      *             se non metto () allora la println restituisce function --> indica che fl è una funzione **/
    println( "${fl()}" )  //Last exp val=100

    println("/****** ESERCIZIO ${i++} *******/")
    fun exec23( op:(Int,Int) -> Int ) : Int { return op(2,3) }
    /** funzione exec23 accetta una funzione in ingresso e restituisce un intero:
      * nel body della funzione si dice che viene restituito come Int il risultato della funzione op(2,3) in ingresso */
    /** creo due funzioni lambda expression **/
    val sum2 = { x:Int, y:Int -> x+y }
    val mul = { x:Int, y:Int -> x*y }
    /** si può passare le funzioni lambda alla funzione exec23 **/
    println("${ exec23(sum2) }")	      //5
    println("${ exec23(mul) }")	      //6
    /** procedura di funzionamento: **/
    val e1 = exec23( { x:Int, y:Int -> x-y } ) //no shortcut
    println("e1=$e1")	      //e1=-1
    val e2 = exec23() { x:Int, y:Int -> x-y } //lamda is last arg
    println("e2=$e2")	      //e2=-1
    val e3 = exec23{ x:Int, y:Int -> x-y } //() can be removed
    println("e3=$e3")	      //e3=-1
    val e4 = exec23{ x,y -> x-y } //arg types inferred
    println("e4=$e4")	      //e4=-1

    println("/****** ESERCIZIO ${i++} *******/")
    fun p2( op: ( Int ) -> Int ) : Int { return op(2) }
    /** it = nome convenzionale dato all'unico parametro che ha la funzione passata come argomento (qui di p2) **/
    val v5 = p2 { it*it }
    println("v5=$v5")   //v5=4
    /** procedura di funzionamento: **/
    val pv1 = p2( { x:Int-> x*x } )
    val pv2 = p2( )  { x:Int-> x*x }
    val pv3 =  p2 { x:Int-> x*x }
    val pv4 =  p2 { x -> x*x }
    val pv5 =  p2 { it -> it*it }

    println("/****** ESERCIZIO ${i++} *******/")
    val c0 = counterCreate()
    c0("inc")
    println("${c0}")    //(kotlin.String) -> kotlin.Int
    println("c0 = ${c0("val")}")
    var c1 = counterCreate()
    for( i in 1..3 ) c1("inc")
    println("c1=${c1("val")}")	      //c1=3
    var c2 = counterCreate()	//another instance
    for( i in 1..3 ) c2("dec")
    println("c2=${c2("val")}")	     //c2=-3
    /** essendo variabili è possibile scrivere c1=c2 --> saranno due variabili che puntano allo stesso oggetto **/

    println("/****** ESERCIZIO ${i++} *******/")
    println("BEGINS")
    doJob(10)
    println("ENDS")

    println("/****** ESERCIZIO ${i++} *******/")
    println("BEGINS ${curThread()}")
    doJobCps( 10  )
    println("ENDS ${curThread()}")

    println("/****** ESERCIZIO ${i++} *******/")
    /** diversamente da doJobCps, in Asincrono con doJobAsynchCps si continua con l'esecuzione, infatti:
      * viene stampato prima BEGINS thread=main e ENDS thread=main
      * poi quando termina la funzione doJobAsynchCps, ne viene stampato il risultato **/
    println("BEGINS ${curThread()}")
    doJobAsynchCps( 10  )
    println("ENDS ${curThread()}")

    println("/****** ESERCIZIO ${i++} *******/")
    /** runBlocking è un costruttore di coroutine, utilizato solo col main, attende che vengano eseguite tutte le
      * funzioni al suo interno prima di terminare il main:
      * "runBlocking is a Coroutine Builder wrapper that runs new coroutine and blocks current thread
      * interruptibly until its completion. It is designed to bridge regular blocking code to libraries
      * that are written in suspending style, to be used in main functions and in tests."  **/
    fun main2() : Unit = runBlocking {
        println("BEGINS ${curThread()}")
        delayedFun()
        /** se non si vuole utilizzare runBlocking si può scrivere: GlobalScope.launch { delayedFun() } **/
        println("ENDS")
    }
    main2()

    println("/****** ESERCIZIO ${i++} *******/")
    /** Una Coroutine è un thread leggero che può essere attivato con un Coroutine builder in un CoroutineScope.
     * Coroutine Builder:   GlobalScope.launch | launch | async
     *                      GlobalScope.actor
     * GlobalScope.launch is used to launch top-level coroutines, which operate on the whole application lifetime.
     * However, we could launch coroutines in the specific scope of the operation we are performing. **/
    fun main3() = runBlocking {
        println("BEGINS ${curThread()}")
        ioBoundFun()
        println("ENDS")
    }
    main3()

    println("/****** ESERCIZIO ${i++} *******/")
    /** ogni coroutine può essere associata ad un Dispatcher che determina il thread dove viene eseguita **/
    fun main4() = runBlocking {
        println("BEGINS")
        val job = GlobalScope.launch {
            ioBoundFun()
        }
        println("CONTINUE ${curThread()}")
        /** se coroutine start con launch, allora restituisce un job **/
        /** job si può usare per: cancel the computation con cancel
         *                        o await its completion con join **/
        job.join()      //IO operation thread=DefaultDispatcher-worker-1
                        /** --> viene associata a DefaultDispatcher **/
        /** DefaultDispatcher can be provided in two ways:
         *  - explicitly: the coroutine builder receives a coroutine context as a first parameter
         *  - by the coroutine scope **/
        println("ENDS")
    }
    main4()

    println("/****** ESERCIZIO ${i++} *******/")
    /** structured concurrency: coroutines confined to different scopes are more maintainable and manageable.
      * We can remove the explicit join, since runBlocking won't complete before all of its child coroutines finish **/
    fun main5() = runBlocking {
        println("BEGINS ${curThread()}")
        val job = launch {
            ioBoundFun()
        }
        //job.join()
        println("ENDS ${curThread()}")
    }
    main5()

    println("/****** ESERCIZIO ${i++} *******/")
    fun main6() = runBlocking {
        println("BEGINS ${curThread()}")
        activate()
        /** ad attendere sarà il main (unico thread in questo caso) **/
        println("ENDS ${curThread()}")
    }
    main6()

    println("/****** ESERCIZIO ${i++} *******/")
    /** Tutte le funzioni coroutine builder (es: launch e async) accettano come parametri uno di questi
      * princiali Dispatcher:
      * - Default --> for CPU-intensive tasks. It can use as many threads as CPU cores
      * - IO --> IO-intensive tasks waiting for an answer from another system. The size of this thread pool is 64
      * - newSingleThreadContext --> creates a new thread for the coroutine to run
      * - Unconfined: not confined --> will work with main thread (don’t use it unless you’re very sure of what you’re doing)
      * - Main: a special dispatcher that is included in UI related coroutine libraries (see kotlinx.coroutines.Main) **/
    fun main7() = runBlocking {
        println("BEGINS ${curThread()}")
        launch { //context of the parent runBlocking
            println("1) thread=${Thread.currentThread().name}")
        }
        launch(Dispatchers.Unconfined) { // in main thread
            println("2) thread=${Thread.currentThread().name}")
        }
        launch(Dispatchers.Default) { // DefaultDispatcher
            println("3) thread=${Thread.currentThread().name}")
        }
        launch(
            newSingleThreadContext("MyThr")) { //new thread
            println("4) thread=${Thread.currentThread().name}")
        }
        println("ENDS ${curThread()}")
    }
    main7()
    /* output:
     * BEGINS thread=main
     * 2) thread=main
     * 3) thread=DefaultDispatcher-worker-2
     * ENDS thread=main
     * 1) thread=main
     * 4) thread=MyThr */

    println("/****** ESERCIZIO ${i++} *******/")
    /** DefaultDispatcher **/
    fun main8() = runBlocking {
        val cpus = Runtime.getRuntime().availableProcessors()
        println("AT START | CPU=$cpus threads=${Thread.activeCount()} curThread=${Thread.currentThread().name}")
        println("BEGINS")
        for (i in 1..6) launchAction(i,this)
        println("ENDS")
    }
    main8()

    println("/****** ESERCIZIO ${i++} *******/")
    /** IO Dispatcher **/
    fun main9() = runBlocking {
        val cpus = Runtime.getRuntime().availableProcessors()
        println("AT START | CPU=$cpus threads=${Thread.activeCount()} curThread=${Thread.currentThread().name}")
        println("BEGINS")
        for (i in 1..6) launchActionIO(i,this)
        println("ENDS")
    }
    main9()

    println("/****** ESERCIZIO ${i++} *******/")
    /** newSingleThreadContext **/
    fun main10() = runBlocking {
        val cpus = Runtime.getRuntime().availableProcessors()
        println("AT START | CPU=$cpus threads=${Thread.activeCount()} curThread=${Thread.currentThread().name}")
        println("BEGINS")
        for (i in 1..6) launchActionSingle(i,this)
        println("ENDS")
    }
    main10()

    println("/****** ESERCIZIO ${i++} *******/")
    fun main11() = runBlocking{
        val cpus = Runtime.getRuntime().availableProcessors()
        println("BEGINS with $cpus  cores")
        GlobalScope.massiveRun {
            counter4++
        }
        println("ENDS with Counter = $counter4")
    }
    main11()

    println("/****** ESERCIZIO ${i++} *******/")
    fun main12() = runBlocking{
        val cpus = Runtime.getRuntime().availableProcessors()
        println("BEGINS with $cpus  cores")
        channelTest()
        println("ENDS with Counter = $counter5")
    }
    main12()

    println("/****** ESERCIZIO ${i++} *******/")
    /** utilizzo di actor **/
    fun main13() = runBlocking {
        val cpus = Runtime.getRuntime().availableProcessors();
        println("BEGINS with $cpus  cores")
        val counter = counterActor() // create the actor
        val initVal = CompletableDeferred<Int>()
        counter.send(CounterMsg("GET", initVal))
        println("Counter INITIAL VALUE=${initVal.await()}")
        GlobalScope.massiveRun {
            counter.send(CounterMsg("INC") )
        }
        val finalVal = CompletableDeferred<Int>()
        counter.send(CounterMsg("GET", finalVal))
        println("Counter FINAL VALUE= = ${finalVal.await()}")
        counter.close() // shutdown the actor
        println("ENDS ")
    }
    main13()


}