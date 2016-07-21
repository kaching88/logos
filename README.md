## Logos

### Overview
Logos is a lightweight, efficient library of utility functions, 
which makes logging in Scala much more painless. 

### Why?
In general, logging is a necessary and helpful addition to project. 
We can control a flow of our applications and prevent bugs with greater efficiency by using a logos.
In my opinion traditional logging by Loggers is very pain in Scala language. 
Mainly cause is just only one - in Scala we have verbose syntax, 
which doesn't work well to Unit returning logging functions.
For example: 
```scala
val logger = LoggerFactory.getLogger(classOf[FooBar])

def foo: A = {
  logger.info("Entering in foo method...")
  val res = doSmth() 
  logger.info("Foo method completed...")
  res
} 
```
In a method above there is a lot of boilerplate.
 Without logging it is one-liner without curly braces. 
 Any method, function and lambda in which we need logging 
 make our code uglier and less clean.
  
### Getting Started
Logos it’s in early development stage and currently 
doesn't exist released version of library.
First release will be happen in a near future.

### Main Features
##### Log annotation
Log annotation provides logging out of the box in annotated method. 
This work more less similar like AOP in Java, 
but this feature is fully realised 
within Scala macros. Simply annotate method, choose logger, 
level and message and it works.

```scala
import com.kaching.logos.Log

val logger = LoggerFactory.getLogger(classOf[FooBar])

@Log(logger, INFO, BEFORE, "Entering in foo method...")
@Log(logger, INFO, AFTER, "Foo method completed")
def foo: A = doSmth()
```
Currently supported version of annotation can be used only with defs.

##### Logging operators
Sometimes we need logging not only around function, 
but also within logic in many places. 
This is a moment, when operators come into the game.
 Currently exists four core operator patterns based on logging levels:
 
```
+|: - log.debug ~> execution, :|+ - execution ~> log.debug  
~|: - log.info ~> execution,  :|~ - execution ~> log.info
-|: - log.warn ~> execution,  :|- - execution ~> log.warn
*|: - log.error ~> execution, :|* - execution ~> log.error
```
Operator always return result of execution block and they are stackable. 
Flow of this operators go from left to right 
so it can be joined one to another. 
It will result accurate logging execution.
Usage example: 
```scala
import com.kaching.logos.Ops._

def foo: ResultType of doSmth1 = {
  implicit val logger = LoggerFactory.getLogger(classOf[FooBar])
  val x = "foo" ~|: doSmth() :|~ "bar" // info("Foo"), exec of doSmth(), info("bar")
  "foo1" -|: "foo2" ~|: doSmth1(x) :|+ "bar1" // warn("foo1"), info("foo2"), exec of doSmth1(), debug("bar1")   
}
```
In opposite of Scala implicit class Ops implementation 
these operators are implemented as Scala macros. 
They don't construct instance every time on operator use and another 
to simulate delayed execution. 
Simply they are completely free from runtime performance point of view.