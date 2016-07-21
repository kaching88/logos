package com.kaching.logos

import org.scalatest.{BeforeAndAfterEach, Matchers, WordSpec}
import uk.org.lidalia.slf4jtest.{LoggingEvent, TestLoggerFactory}
import com.kaching.logos.Ops._

class OpsTest extends WordSpec with Matchers with BeforeAndAfterEach {
  implicit val logger = TestLoggerFactory.getTestLogger(classOf[OpsTest])

  "Operator" when {
    "Info ~|: (before)" should {
      "generate info log before base execution" in {
        "foo" ~|: logger.error("middle")
        logger.getLoggingEvents should contain theSameElementsInOrderAs List(
          LoggingEvent.info("foo"),
          LoggingEvent.error("middle")
        )
      }
    }

    "Warn -|: (before)" should {
      "generate info log before base execution" in {
        "foo" -|: logger.error("middle")
        logger.getLoggingEvents should contain theSameElementsInOrderAs List(
          LoggingEvent.warn("foo"),
          LoggingEvent.error("middle")
        )
      }
    }

    "Debug +|: (before)" should {
      "generate info log before base execution" in {
        "foo" +|: logger.error("middle")
        logger.getLoggingEvents should contain theSameElementsInOrderAs List(
          LoggingEvent.debug("foo"),
          LoggingEvent.error("middle")
        )
      }
    }

    "Error *|: (before)" should {
      "generate info log before base execution" in {
        "foo" *|: logger.error("middle")
        logger.getLoggingEvents should contain theSameElementsInOrderAs List(
          LoggingEvent.error("foo"),
          LoggingEvent.error("middle")
        )
      }
    }

    "Info :|~ (after)" should {
      "generate info log before base execution" in {
        logger.error("middle") :|~ "foo"
        logger.getLoggingEvents should contain theSameElementsInOrderAs List(
          LoggingEvent.error("middle"),
          LoggingEvent.info("foo")
        )
      }
    }

    "Warn :|- (after)" should {
      "generate info log before base execution" in {
        logger.error("middle") :|- "foo"
        logger.getLoggingEvents should contain theSameElementsInOrderAs List(
          LoggingEvent.error("middle"),
          LoggingEvent.warn("foo")
        )
      }
    }

    "Debug :|+ (after)" should {
      "generate info log before base execution" in {
        logger.error("middle") :|+ "foo"
        logger.getLoggingEvents should contain theSameElementsInOrderAs List(
          LoggingEvent.error("middle"),
          LoggingEvent.debug("foo")
        )
      }
    }

    "Error :|* (after)" should {
      "generate info log before base execution" in {
        logger.error("middle") :|* "foo"
        logger.getLoggingEvents should contain theSameElementsInOrderAs List(
          LoggingEvent.error("middle"),
          LoggingEvent.error("foo")
        )
      }
    }
    
    "stacked" should {
      "apply one to another with correct execution order" in {
        "foo" +|: "foo1" ~|: logger.error("middle") :|~ "foo2" :|- "foo3" :|* "foo4"
        logger.getLoggingEvents should contain theSameElementsInOrderAs List(
          LoggingEvent.debug("foo"),
          LoggingEvent.info("foo1"),
          LoggingEvent.error("middle"),
          LoggingEvent.info("foo2"),
          LoggingEvent.warn("foo3"),
          LoggingEvent.error("foo4")
        )
      }
    }
  }

  override protected def afterEach(): Unit = TestLoggerFactory.clearAll()
}
