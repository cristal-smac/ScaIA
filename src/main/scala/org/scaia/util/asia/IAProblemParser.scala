// Copyright (C) Maxime MORGE 2017
package org.scaia.util.asia

import org.scaia.asia._

import scala.io.Source
import scala.util.matching.Regex

/**
  * Build a IAProblem object from a text file
  *
  */
class IAProblemParser(val directoryName: String, val fileName: String ) {
  val debug = false

  val path = directoryName + fileName
  var lineNumber = 0

  var m=0
  var n=0
  var activities = Set[Activity]()
  var individuals = Group()

  /**
    * Parse file
    */
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
    val pb = new IAProblem(individuals, activities)
    if (pb.isTotalPreferences()) pb
    else throw new RuntimeException(s"ERROR parse incomplete preferences")

  }

  /**
    * Parse a
    * @param line
    */
  def parseLine(line: String) : Unit= {
    val couple = line.split(":").map(_.trim)
    if (couple.length != 2) throw new RuntimeException(s"ERROR parseLine $path line$lineNumber: comment $line")
    val (key, value) = (couple(0), couple(1))
    //Firstly, the size (m,n) should be setup as strict positive integer
    if (m == 0 || n == 0) parseSize(key, value)
    //Secondly, the entities should be setup as non-empty
    else if ((m != 0) && (n != 0)){
      if (debug) println(s"parseLine m,n=$m,$n")
      if (activities.isEmpty || individuals.isEmpty) {
        parseEntities(key, value)
      }
      else if (activities.nonEmpty && individuals.nonEmpty) parsePreferences(key, value)
    }
  }

  /**
    * Parse the size of the IAProblem (m,n)
    * @param key of the line
    * @param value of the line
    */
  def parseSize(key: String, value: String): Unit = key match {
    case "m" => m = value.toInt
    case "n" => n = value.toInt
    case _ => throw new RuntimeException("ERROR parseSize" + path + " L" + lineNumber + "=" + key + "," + value)
  }

  /**
    * Parse the entities (activities,individuals) of the IAProblem
    * @param key of the line
    * @param value of the line
    */
  def parseEntities(key: String, value: String): Unit = key match {
    case "activities" => parseActivities(value)
    case "individuals" => parseIndividuals(value)
    case _ => throw new RuntimeException(s"ERROR parseEntities $path line$lineNumber: $key")
  }

  /**
    * Parse the activities
    * @param namesAndCapacities e.g. a string "a(2) b(2)"
    */
  def parseActivities(namesAndCapacities: String): Unit={
    val array:Array[String]=namesAndCapacities.split(" ").map(_.trim)
    if (array.length!=n) throw new RuntimeException(s"ERROR parseActivities $path line$lineNumber: the number of activities  $n is wrong: ${array.size}")
    val pattern = """(\w+)\s?\(([0-9]+)\)""".r
    array.foreach{ str: String =>
      str match {
        case pattern(activity, capacity) =>
          if (debug) println(s"parseActivities: $activity($capacity)")
          activities += new Activity(activity, capacity.toInt)
        case _ => throw new RuntimeException(s"ERROR parseEntities $path line$lineNumber: $str")
      }
    }
  }

  /**
    * Parse the individuals
    * @param names e.g. a string i1 i2 i3
    */
  def parseIndividuals(names: String): Unit={
    val array:Array[String]=names.split(" ").map(_.trim)
    array.foreach{ str: String =>
      if (debug) println(s"parseIndividual: $str")
      individuals+= new Individual(str,m)
    }
  }

  /**
    * Parse the preferences
    * @param key of the line
    * @param value of the line
    */
  def parsePreferences(key: String, value: String): Unit = {
    if (debug) println(s"parsePreferences $key")
    val source=individuals.getIndividual(key)
    val couple:Array[String]=value.split(" ").map(_.trim)
    if (couple.size!=2) throw new RuntimeException(s"ERROR parsePreferences $path line$lineNumber: $value")
    val target = couple(0)
    val valuation : Double = try {
      couple(1).toDouble }catch{
      case e: NumberFormatException => throw  new RuntimeException(s"ERROR parsePreferences $path line$lineNumber: ${couple(1)}")
    }
    //The individuals/preferences can be declared in any order
    if (individuals.isNameOfAIndividual(target)) {
      source.wMap+=(target -> valuation)
      if (debug) println(s"parsePreference weight: ${source.w(target)}")
    } else {
      source.vMap+=(target -> valuation)
      if (debug) println(s"parsePreference valuation: ${source.v(target)}")
    }
  }

}

/**
  * Test IAProblemParser
  */
object IAProblemParser extends App {
  val parser =new IAProblemParser("examples/asia/", "circularPreference.txt")//"notBestUtil.txt"
  println(parser.parse()) //Run main
}