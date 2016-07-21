package com.kaching.logos

import macrocompat.bundle

import scala.language.experimental.macros

object Ops {
  import org.slf4j.Logger

  implicit class LoggerOps[A](a: A)(implicit logger: Logger) {
    def *|:(message: String): A = macro OpsMacros.beforeErrorImpl[A]
    def :|*(message: String): A = macro OpsMacros.afterErrorImpl[A]

    def ~|:(message: String): A = macro OpsMacros.beforeInfoImpl[A]
    def :|~(message: String): A = macro OpsMacros.afterInfoImpl[A]

    def +|:(message: String): A = macro OpsMacros.beforeDebugImpl[A]
    def :|+(message: String): A = macro OpsMacros.afterDebugImpl[A]

    def -|:(message: String): A = macro OpsMacros.beforeWarnImpl[A]
    def :|-(message: String): A = macro OpsMacros.afterWarnImpl[A]
  }
}

import scala.reflect.macros.whitebox

@bundle
class OpsMacros(val c: whitebox.Context) {
  import c.universe._

  def beforeInfoImpl[A](message: c.Expr[String]): c.Expr[A] = {
    val (base, logger) = enclosingClassProperties
    c.Expr[A] { q"{ $logger.info($message); $base }" }
  }

  def afterInfoImpl[A](message: c.Expr[String]): c.Expr[A] = {
    val (base, logger) = enclosingClassProperties
    c.Expr[A] { q"{ val res = $base; $logger.info($message); res }" }
  }

  def beforeWarnImpl[A](message: c.Expr[String]): c.Expr[A] = {
    val (base, logger) = enclosingClassProperties
    c.Expr[A] { q"{ $logger.warn($message); $base }" }
  }

  def afterWarnImpl[A](message: c.Expr[String]): c.Expr[A] = {
    val (base, logger) = enclosingClassProperties
    c.Expr[A] { q"{ val res = $base; $logger.warn($message); res }" }
  }

  def beforeDebugImpl[A](message: c.Expr[String]): c.Expr[A] = {
    val (base, logger) = enclosingClassProperties
    c.Expr[A] { q"{ $logger.debug($message); $base }" }
  }

  def afterDebugImpl[A](message: c.Expr[String]): c.Expr[A] = {
    val (base, logger) = enclosingClassProperties
    c.Expr[A] { q"{ val res = $base; $logger.debug($message); res }" }
  }

  def beforeErrorImpl[A](message: c.Expr[String]): c.Expr[A] = {
    val (base, logger) = enclosingClassProperties
    c.Expr[A] { q"{ $logger.error($message); $base }" }
  }

  def afterErrorImpl[A](message: c.Expr[String]): c.Expr[A] = {
    val (base, logger) = enclosingClassProperties
    c.Expr[A] { q"{ val res = $base; $logger.error($message); res }" }
  }

  private def enclosingClassProperties = {
    c.prefix.tree match {
      case Apply(Apply(_, List(base)), List(logger)) => (base, logger)
      case t => c.abort(c.enclosingPosition, s"Can't get enclosing class properties from $t tree.")
    }
  }
}