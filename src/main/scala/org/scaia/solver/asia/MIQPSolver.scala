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
class MIQPSolver(pb : IAProblem, rule: SocialRule) extends ASIADualSolver(pb){

  val config = ConfigFactory.load()
  val inputPath =config.getString("path.scaia")+"/"+config.getString("path.input")
  val miqpPath = config.getString("path.scaia")+"/"+config.getString("path.miqp")
  val outputPath = config.getString("path.scaia")+"/"+config.getString("path.output")

  @throws(classOf[RuntimeException])
  override def solve() : Matching = {
    rule match {
      case Egalitarian => throw new RuntimeException("MIQPSolver cannot maximize the egalitarian welfare")
      case _ =>
    }
    // 1 - Reformulate the problem
    val startingTime = System.nanoTime()
    val writer=new IAOPLWriter(inputPath ,pb)
    writer.write()
    preSolvingTime = System.nanoTime() - startingTime
    // 2 - Run the solver
    val command : String= config.getString("path.opl")+" "+
      miqpPath+" "+
      inputPath
    if (debug) println(command)
    val success : Int = (command #> new File("/dev/null")).!
    if (success != 0) throw new RuntimeException("MIQPSolver failed")
    // 3 - Reformulate the output
    return fromOPL()
  }

  /**
    * Return the matching based on the text output of OPL
    */
  def fromOPL() : Matching = {
    val startingTime = System.nanoTime()
    val m = new Matching(pb)
    val bufferedSource = Source.fromFile(outputPath)
    // Activities/individuals sorted by names
    val orderedIndividuals = pb.individuals.toSeq.sortBy(_.name)
    val orderedActivities = pb.activities.toSeq.sortBy(_.name)
    var linenumber = 0
    for (line <- bufferedSource.getLines) { // foreach line
      if (linenumber == 0) {
        val u = line.toDouble
        if (debug) println(s"U(M) = $u")
      }
      if (linenumber == 1) {
        val t = line.toDouble
        if (debug) println(s"T (ms) = $t")
      }
      if (linenumber > 1) {
        val individual = orderedIndividuals(linenumber - 2)
        val activityNumber = line.toInt
        if (activityNumber == 0) {
          if (debug) println(s"${individual.name} -> $Activity.VOID")
          m.a += (individual -> Activity.VOID)
        }
        else {
          val activity = orderedActivities(activityNumber - 1)
          if (debug) println(s"${individual.name} -> $activity")
          m.a += (individual -> activity)
        }
      }
      linenumber += 1
    }
    // Buid group
    pb.activities.foreach { a =>
      val g = m.p(a)
      g.foreach { i =>
        m.g+=(i -> g)
      }
    }
    pb.individuals.foreach { i =>
      if (m.a(i).equals(Activity.VOID)){
        m.g+=(i -> new Group(i))
      }
    }
    if (debug) println(m)
    postSolvingTime = System.nanoTime() - startingTime
    return m
  }
}

/**
  * Test IAOPLWriter
  */
object MIQPSolver extends App{

  val config = ConfigFactory.load()

  val pb = IAProblem.randomProblem(2, 100)// n (2) activity m (200) individuals
  //import org.scaia.util.asia.DilemmaPref._
  //import org.scaia.util.asia.NotBestUtil._
  //import org.scaia.util.asia.CircularPref._

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
  //miqpSolver.debug = true
  startingTime=System.currentTimeMillis()
  val miqpResult=miqpSolver.solve()
  val miqpTime=System.currentTimeMillis - startingTime
  val miqpUW=miqpResult.utilitarianWelfare()
  println("MIQPSolver: U/T")
  println(miqpUW)
  println(miqpTime)
}