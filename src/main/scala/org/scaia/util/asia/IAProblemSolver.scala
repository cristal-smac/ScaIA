// Copyright (C) Maxime MORGE 2017
package org.scaia.util.asia

import org.scaia.solver.asia._
import org.scaia.solver.asia.{Egalitarian, Utilitarian}

import java.nio.file.{Files, Paths}
import akka.actor.ActorSystem


/**
  * Solve a particular IAProblem instance
  * TODO IAProblem generator
  * TODO make experiments with an inclusive hillClimbing
  * sbt "run org.scaia.util.asia.IAProblemSolver -a -t -h -i -v -e -d examples/asia/undesiredGuestPb.txt examples/asia/undesiredGuestMatching.txt"
  * java -jar ScaIA-assembly-0.3.jar org.scaia.util.asia.IAProblemSolver -a -t -h -i -v -e -d examples/asia/undesiredGuestPb.txt  examples/asia/undesiredGuestMatching.txt
  *
  */
object IAProblemSolver extends App {

  val debug = true
  val system = ActorSystem("IAProblemSolver") //The Actor system
  val usage =
    """
    Usage: java -jar ScaIA-assembly-X.Y.jar [-athived] inputFilename outputFilename
    The following options are available:
    -a: approximation (false by default)
    -t: trace (false by default)
    -h: hillclimbing (false by default)
|   -i: inclusive (false by default)
    -v: verbose (false by default)
    -d: distributed (false by default)
    -e: egalitarian (utilitarian by default)
  """

  // Default parameters for the solver
  var approximation = false
  var trace = false
  var hillClimbing = false
  var inclusive = false
  var verbose = false
  var socialRule: SocialRule = Utilitarian
  var distributed = false

  // Default fileNames/path for the input/output
  var inputFilename= new String()
  var outputFilename= new String()


  if (args.length <= 2){
    println(usage)
    System.exit(1)
  }
  var argList = args.toList.drop(1)// drop Classname
  parseFilenames() // parse filenames
  if (verbose) {
    println(s"inputFile:$inputFilename")
    println(s"output:$outputFilename")
  }
  if (!nextOption(argList)) {
    println(s"ERROR IAProblemSolver: options cannot be parsed ")
    System.exit(1)
  }// fail if options cannot be parsed
  val parser =new IAProblemParser(inputFilename)
  val pb= parser.parse() // parse problem
  if (verbose) println(pb)
  if (verbose) println(
    s"""
    Run solver with the following parameters:
    hillclimbing:$hillClimbing approximation:$approximation inclusive:$inclusive distributed:$distributed $socialRule trace:$trace
    ...
  """)
  val solver= selectSolver()
  solver.debug=trace
  val matching= solver.solve()
  val writer=new MatchingWriter(outputFilename,matching)
  writer.write()
  println(f"U(M): ${matching.utilitarianWelfare()}")
  println(f"E(M): ${matching.egalitarianWelfare()}")
  System.exit(0)

  /**
    * Parse filenames at first
    */
  def parseFilenames() : Unit= {
    outputFilename = argList.last.trim
    argList = argList.dropRight(1)// drop outputFile
    inputFilename = argList.last.trim
    argList = argList.dropRight(1) //drop inputFile
    if (!Files.exists(Paths.get(inputFilename)) || Files.exists(Paths.get(outputFilename))) {
      println(s"ERROR parseFilename: either $inputFilename does not exist or $outputFilename already exist")
      System.exit(1)
    }
  }

  /**
    *  Parse options at second
    *  @param tags is the list of options
    */
  def nextOption(tags: List[String]) : Boolean = {
    if (tags.isEmpty) return true
    val tag : String=tags.head.substring(1)// remove '-'
    tag match{
      case "h" => hillClimbing=true
      case "e" => socialRule= Egalitarian
      case "a" => approximation=true
      case "t" => trace=true
      case "i" => inclusive=true
      case "d" => distributed=true
      case "v" => verbose=true
      case _ => false
    }
    nextOption(tags.tail)
  }

  /**
    * Select the solver
    * @return the ASIA Solver
    */
  def selectSolver() : ASIASolver= {
    hillClimbing match {
      case true => // Local search techniques
        if (verbose && (inclusive || approximation || distributed)) {
          println(s"WARNING: inclusive or approximation or distributed is useless")
        }
        new HillClimbingSolver(pb,socialRule)
      case false => // Multi-agent approach
        distributed match { // Distributed algorithm
          case true =>
            inclusive match {
              case true =>
                if (socialRule == Utilitarian) println(s"WARNING: socialRule($socialRule) and inclusive($inclusive) are contradictory")
                new DistributedInclusiveSolver(pb,system, socialRule)
              case false =>
                if (socialRule == Egalitarian) println(s"WARNING: socialRule($socialRule) and inclusive($inclusive) are contradictory")
                new DistributedSelectiveSolver(pb, system, approximation, socialRule)

            }
          case false => // Centralize algorithm
            inclusive match {
              case true =>
                if (socialRule == Utilitarian) println(s"WARNING: socialRule($socialRule) and inclusive($inclusive) are contradictory")
                new InclusiveSolver(pb,socialRule)
              case false =>
                if (socialRule == Egalitarian) println(s"WARNING: socialRule($socialRule) and inclusive($inclusive) are contradictory")
                new SelectiveSolver(pb,approximation,socialRule)
            }
        }
    }
  }
}
