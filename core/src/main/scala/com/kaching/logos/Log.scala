package com.kaching.logos

import com.kaching.logos.enums.{Level, Mode}
import macrocompat.bundle
import org.slf4j.Logger

import annotation.compileTimeOnly
import language.experimental.macros

@compileTimeOnly("enable macro paradise to expand macro annotations")
class Log (logger: Logger, level: Level, mode: Mode, message: String) extends annotation.StaticAnnotation {
  def macroTransform(annottees: Any*): Any = macro LogMacros.logImpl
}

import scala.reflect.macros.whitebox

@bundle
class LogMacros(val c: whitebox.Context) {
  import c.universe._

  def logImpl(annottees: c.Expr[Any]*): c.Expr[Any] = {
    val (logger, level, mode, message) = enclosingAnnotationProperties
    val block = extractBlock(annottees.head.tree)
    val resultBlock = generateBlock(logger, message, block, level, mode)
    val resultTree = replaceBlocks(annottees.head.tree, resultBlock)
    c.Expr[Any](q"$resultTree")
  }

  private def enclosingAnnotationProperties = {
    c.prefix.tree match {
      case q"new Log($logger, ${Ident(TermName(level))}, ${Ident(TermName(mode))}, $message)" =>
        (logger, level.toLowerCase, mode, message)
      case t => c.abort(c.enclosingPosition, s"Can't get enclosing annotation properties from $t tree.")
    }
  }

  private def extractBlock(tree: c.Tree): c.Tree = {
    tree match {
      case DefDef(_, _, _, _, _, block) => block
      case t => c.abort(c.enclosingPosition, s"Can't extract block from $t tree.")
    }
  }

  private def replaceBlocks(base: c.Tree, block: c.Tree): c.Tree = {
    base match {
      case DefDef(modifiers, termName, typeList, args, args1, _) =>
        DefDef(modifiers, termName, typeList, args, args1, block)
      case t => c.abort(c.enclosingPosition, s"Can't replace blocks on $t tree." )
    }
  }

  private def generateBlock(logger: c.Tree, message: c.Tree,
                                                 block: c.Tree, level: String, mode: String) = {
    mode match {
      case "BEFORE" =>
        q"""{
              $logger.${TermName(level)}($message);
              $block
        }"""
      case "AFTER" =>
        q"""{
              val res = $block;
              $logger.${TermName(level)}($message);
              res
        }"""
      case "AROUND" =>
        q"""{
              $logger.${TermName(level)}($message);
              val res = $block;
              $logger.${TermName(level)}($message);
              res
        }"""
    }
  }
}
