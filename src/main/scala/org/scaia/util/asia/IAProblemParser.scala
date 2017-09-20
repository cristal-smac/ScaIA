// Copyright (C) Maxime MORGE 2017
package org.scaia.util.asia

import org.scaia.asia._

import scala.io.Source
import scala.util.matching.Regex

/**
  * Build a IAProblem object from a text file
  */
object IAProblemParser extends App {
  val debug = true

  val directoryName = "examples/asia/"
  val fileName = "notBestUtil.txt"
  val path = directoryName + fileName
  var lineNumber = 0

  var m=0
  var n=0
  var activities = Set[Activity]()
  var individuals = Group()

  println(parse()) //Run main

  // Parse file
  def parse(): IAProblem = {
    val bufferedSource = Source.fromFile(path)
    for (line <- bufferedSource.getLines) {
      lineNumber += 1
      if (debug) println(s"parse $path line$lineNumber: $line")
      if (line.startsWith("//")) { //Drop comment
        if (debug) println(s"parse $path line$lineNumber: comment $line")
      } else parseLine(line)
    }
    bufferedSource.close
    new IAProblem(individuals, activities)
  }

  //Parse line
  def parseLine(line: String) : Unit= {
    val couple = line.split(":").map(_.trim)
    if (couple.size != 2) throw new RuntimeException(s"ERROR parseLine $path line$lineNumber: comment $line")
    val (key, value) = (couple(0), couple(1))
    //Firstly, the size (m,n) should be setup as strict positive integer
    if (m == 0 || n == 0) parseSize(key, value)
    //Secondly, the entities should be setup as non-empty
    else if ((m != 0) && (n != 0)){
      if (debug) println(s"parseLine m,n=$m,$n")
      if (activities.isEmpty || individuals.isEmpty) {
        parseEntities(key, value)
      }
      else if (!activities.isEmpty && !individuals.isEmpty) parsePreferences(key, value)
    }
  }

  //Parse the size of the IAProblem (m,n)
  def parseSize(key: String, value: String): Unit = key match {
    case "m" => m = value.toInt
    case "n" => n = value.toInt
    case _ => throw new RuntimeException("ERROR parseSize" + path + " L" + lineNumber + "=" + key + "," + value)
  }

  //Parse the entities of the IAProblem (activities,individualq)
  def parseEntities(key: String, value: String): Unit = key match {
    case "activities" => parseActivities(value)
    case "individuals" => parseIndividuals(value)
    case _ => throw new RuntimeException(s"ERROR parseEntities $path line$lineNumber: $key")
  }

  //Parse activities
  def parseActivities(namesAndCapacities: String): Unit={
    val array:Array[String]=namesAndCapacities.split(" ").map(_.trim)
    if (array.size!=n) throw new RuntimeException(s"ERROR parseActivities $path line$lineNumber: the number of activities  $n is wrong:" + array.size)
    val pattern = """(\w+)\s?\(([0-9]+)\)""".r
    array.foreach{ str: String =>
      str match {
        case pattern(activity, capacity) => {
          if (debug) println(s"parseActivities: $activity($capacity)")
          activities += new Activity(activity, capacity.toInt)
        }
        case _ => throw new RuntimeException("ERROR parseEntities " + path + " L" + lineNumber + "=" + str)
      }
    }
  }

  //Parse individuals
  def parseIndividuals(names: String): Unit={
    val array:Array[String]=names.split(" ").map(_.trim)
    array.foreach{ str: String =>
      if (debug) println(s"parseIndividual: $str")
      individuals+= new Individual(str,m)
    }
  }

  //TODO Parse the preferences
  def parsePreferences(key: String, value: String): Unit = key match {
    case _ => println("TODO "+key+" "+value)
  }

}