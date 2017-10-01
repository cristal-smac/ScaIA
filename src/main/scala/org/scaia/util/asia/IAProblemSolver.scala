// Copyright (C) Maxime MORGE 2017
package org.scaia.util.asia

import org.scaia.solver.asia._

import java.nio.file.{Files, Paths}
import akka.actor.ActorSystem


/**
  * Solve a particular IAProblem instance
  * TODO test somme examples
  * TODO IAProblem generator
  * sbt "run org.scaia.util.asia.IAProblemSolver -a -h -i -d -e examples/asia/circularPreferencePb.txt examples/asia/circularPreferenceMatching.txt"
  * java -jar target/scala-2.11/ScaIA-assembly-0.3.jar -a -h -i -d examples/asia/circularPreferencePb.txt examples/asia/circularPreferenceMatching.txt
  */
object IAProblemSolver extends App {

  val debug = true
  val system = ActorSystem("ScaIA") //The Actor system
  val usage =
    """
    Usage: java -jar ScaIA-assembly-X.Y.jar [-heaid] inputFilename outputFilename
    The following options are available:
    -h: hillclimbing (false by default)
    -e: egalitarian (utilitarian by default)
    -a: approximation (false by default)
    -i: inclusive (false by default)
    -d: distributed (false by default)
  """

  // Default parameters for the solver
  var hillclimbing = false
  var approximation = false
  var inclusive = false
  var distributed = false
  var socialRule: SocialRule = Utilitarian
  // Default file for the input/output
  var inputFilename= new String()
  var outputFilename= new String()
  var inputPath= new String()
  var inputName= new String()


  if (args.length <= 2){
    println(usage)
    System.exit(1)
  }
  var argList = args.toList.drop(1)// drop Classname
  parseFilenames() // parse filenames
  println(s"input:$inputFilename output:$outputFilename")
  if (!nextOption(argList)) System.exit(1)// parse options
  val parser =new IAProblemParser(inputPath, inputName)
  val pb= parser.parse() // parse problem
  if (debug) println(pb)
  println(s"hillclimbing:$hillclimbing approximation:$approximation inclusive:$inclusive distributed:$distributed $socialRule")
  val solver= selectSolver()
  val matching= solver.solve()
  val writer=new MatchingWriter(outputFilename,matching)
  writer.write()
  println("Utilitarian welfare: "+matching.utilitarianWelfare())
  println("Egalitarian welfare: "+matching.egalitarianWelfare())
  println("That's all folk !")
  System.exit(0)

  /**
    * Parse filenames at first
    * @return inputPath inputFilename outputFile
    */
  def parseFilenames() : Unit= {
    outputFilename = argList.last.trim
    argList = argList.dropRight(1)// drop outputFile
    inputFilename = argList.last.trim
    val i = inputFilename.lastIndexOf("/")
    argList = argList.dropRight(1) //drop inputFile
    if (!Files.exists(Paths.get(inputFilename)) || Files.exists(Paths.get(outputFilename))) {
      println(s"Either $inputFilename does not exist or $outputFilename already exist")
      System.exit(1)
    }
    inputPath = inputFilename.substring(0, i)
    inputName = inputFilename.substring(i)
  }

  /**
    *  Parse options at second
    *  @param tags is the list of options
    */
  def nextOption(tags: List[String]) : Boolean = {
    if (tags.isEmpty) return true
    val tag : String=tags.head.substring(1)// remove '-'
    tag match{
      case "h" => hillclimbing=true
      case "e" => socialRule= Egalitarian
      case "a" => approximation=true
      case "i" => inclusive=true
      case "d" => distributed=true
      case _ => false
    }
    nextOption(tags.tail)
  }

  /**
    * Select the solver
    * @return the ASIA Solver
    */
  def selectSolver() : ASIASolver= {
    hillclimbing match {
      case true => // Local search techniques
        inclusive match {
          case true => new HillClimbingSolver(pb,socialRule)
          case false => new HillClimbingInclusiveSolver(pb,socialRule)
        }
      case false => // Multi-level
        distributed match { // Multi-agent
          case true =>
            inclusive match {
              case true => new DistributedInclusiveSolver(pb,system, approximation, socialRule)
              case false => new DistributedMNSolver(pb, system, approximation, socialRule)

            }
          case false =>
            inclusive match {
              case true => new InclusiveSolver(pb,socialRule)
              case false => new MNSolver(pb,approximation,socialRule)

            }
        }
    }
  }
}
