package com.kaching.logos

import com.kaching.logos.enums._
import org.scalatest.{BeforeAndAfterEach, Matchers, WordSpec}
import uk.org.lidalia.slf4jtest.{LoggingEvent, TestLoggerFactory}

class LogTest extends WordSpec with Matchers with BeforeAndAfterEach {
  val logger = TestLoggerFactory.getTestLogger(classOf[LogTest])

  val testClass = new TestClass

  class TestClass {
    @Log(logger, INFO, AFTER, "foo")
    def afterTest(): String = {
      logger.error("middle")
      "foobar"
    }

    @Log(logger, INFO, BEFORE, "foo")
    def beforeTest(): String = {
      logger.error("middle")
      "foobar"
    }

    @Log(logger, INFO, AROUND, "foo")
    def aroundTest(): String = {
      logger.error("middle")
      "foobar"
    }

    @Log(logger, INFO, BEFORE, "foo1")
    @Log(logger, INFO, AFTER, "foo2")
    def around1Test(): String = {
      logger.error("middle")
      "foobar"
    }

    @Log(logger, DEBUG, BEFORE, "foo")
    @Log(logger, WARN, BEFORE, "foo1")
    @Log(logger, INFO, BEFORE, "foo2")
    @Log(logger, ERROR, BEFORE, "foo3")
    def levelTest(): Unit = {}
  }

  "Log annotation" when {
    "created with AFTER mode" should {
      "create log after method body" in {
        val resultValue: String = testClass.afterTest()
        logger.getLoggingEvents should contain theSameElementsInOrderAs List(
          LoggingEvent.error("middle"),
          LoggingEvent.info("foo")
        )
        resultValue shouldEqual "foobar"
      }
    }

    "created with BEFORE mode" should {
      "create log before method body" in {
        val resultValue: String = testClass.beforeTest()
        logger.getLoggingEvents should contain theSameElementsInOrderAs List(
          LoggingEvent.info("foo"),
          LoggingEvent.error("middle")
        )
        resultValue shouldEqual "foobar"
      }
    }

    "created with AROUND mode" should {
      "create log around method body" in {
        val resultValue: String = testClass.aroundTest()
        logger.getLoggingEvents should contain theSameElementsInOrderAs List(
          LoggingEvent.info("foo"),
          LoggingEvent.error("middle"),
          LoggingEvent.info("foo")
        )
        resultValue shouldEqual "foobar"
      }
    }

    "stacked" should {
      "apply one to another" in {
        val resultValue: String = testClass.around1Test()
        logger.getLoggingEvents should contain theSameElementsInOrderAs List(
          LoggingEvent.info("foo1"),
          LoggingEvent.error("middle"),
          LoggingEvent.info("foo2")
        )
        resultValue shouldEqual "foobar"
      }
    }

    "annotated with concrete level" should {
      "create that level log" in {
        testClass.levelTest()
        val loggingEvents = logger.getLoggingEvents
        loggingEvents should contain theSameElementsAs List(
          LoggingEvent.debug("foo"),
          LoggingEvent.warn("foo1"),
          LoggingEvent.info("foo2"),
          LoggingEvent.error("foo3")
        )
      }
    }
  }

  override protected def afterEach(): Unit = TestLoggerFactory.clear()

}
