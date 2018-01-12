// Copyright (C) Maxime MORGE 2017
package org.scaia.solver.asia

import org.scaia.asia._
import org.scaia.util.asia.IAOPLWriter

import sys.process._
import scala.io.Source
import java.io.File

import akka.actor.ActorSystem
import com.typesafe.config.ConfigFactory

/**
  *  Mixed-Integer Quadratic Programming solver
  *  @param pb to solved
  *  @param rule to apply, i.e. maximize the utilitarian welfare
  */
class MIQPSolver(pb : IAProblem, rule: SocialRule) extends ASIASolver(pb){

  val config = ConfigFactory.load()

  @throws(classOf[RuntimeException])
  override def solve() : Matching = {
    rule match {
      case Egalitarian => throw new RuntimeException("MIQPSolver cannot maximize the egalitarian welfare")
      case _ =>
    }

    // 1 - Reformulate the problem
    val writer=new IAOPLWriter(config.getString("path.scaia")+"/"+config.getString("path.input") ,pb)
    writer.write()

    // 2 - Run the solver
    //See https://alvinalexander.com/scala/scala-execute-exec-external-system-commands-in-scala
    val command : String= config.getString("path.opl")+" "+
      config.getString("path.scaia")+"/"+config.getString("path.miqp")+" "+
      config.getString("path.scaia")+"/"+config.getString("path.input")

    if (debug) println(command)
    val success : Int = (command #> new File("/dev/null")).!
    if (success != 0) throw new RuntimeException("MIQPSolver failed")

    // 3 - TODO Reformulate the output
    new Matching(pb)
  }
}

/**
  * Test IAOPLWriter
  */
object MIQPSolver extends App{

  val config = ConfigFactory.load()

  //val pb = IAProblem.generateRandom(2, 200)// n (2) activity m (200) individuals
  import org.scaia.util.asia.DilemmaPref._

  val system = ActorSystem("MIQPSolver")//The Actor system

  val disselectiveSolver = new DistributedSelectiveSolver(pb, system, true, Utilitarian)

  val selectiveSolver = new SelectiveSolver(pb,  true, Utilitarian)

  var startingTime=System.currentTimeMillis()
  val resultSelective = selectiveSolver.solve()
  val selectiveTime=System.currentTimeMillis - startingTime
  val selectiveUW=resultSelective.utilitarianWelfare()

  println("SelectiveSolver: U/T")
  println(selectiveUW)
  println(selectiveTime)

  startingTime=System.currentTimeMillis()
  val disresultSelective = disselectiveSolver.solve()
  val disselectiveTime=System.currentTimeMillis - startingTime
  val disselectiveUW=disresultSelective.utilitarianWelfare()

  println("DisSelectiveSolver: U/T")
  println(disselectiveUW)
  println(disselectiveTime)


  val miqpSolver = new MIQPSolver(pb, Utilitarian)
  miqpSolver.debug = true
  miqpSolver.solve()
  println("MIQPSolver: U/T")
  val bufferedSource = Source.fromFile(config.getString("path.scaia")+"/"+config.getString("path.output"))
  for (line <- bufferedSource.getLines) {
    println(line)
  }

}