// Copyright (C) Maxime MORGE 2017
package org.scaia.util.asia

import org.scaia.asia._

import scala.io.Source
import java.io._

/**
  * Build a IAProblem object from a text file
  */
object IAProblemParser extends App {
  val debug = true

  val directoryName = "examples/asia/"
  val fileName = "circularPreference.txt"
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
      if (debug) println(path + " L" + lineNumber + "=" + line)
      if (line.startsWith("//")) { //Drop comment
        if (debug) println("WARNING parse " + path + " L" + lineNumber + "=" + line)
      } else parseLine(line)
    }
    bufferedSource.close
    new IAProblem(individuals, activities)
  }

  //Parse line
  def parseLine(line: String) = {
    val couple = line.split(":").map(_.trim)
    if (couple.size != 2) throw new RuntimeException("ERROR parseLine " + path + " L" + lineNumber + "=" + line)
    val (key, value) = (couple(0), couple(1))
    //Firstly, the size (m,n) should be setup as strict positive integer
    if (m == 0 || n == 0) parseSize(key, value)
    else if ((m != 0) && (n != 0)){
      if (debug) println(path + " (m,n)=" + (m, n).toString())
      //Secondly, The entities should be setup as non-empty
      if (activities.isEmpty || individuals.isEmpty) {
        println(line)
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
    case _ => throw new RuntimeException("ERROR parseEntities " + path + " L" + lineNumber + "=" + key + "," + value)
  }

  //Parse activities
  def parseActivities(namesAndCapacities: String): Unit={
    println("TODO "+namesAndCapacities)
  }

  //Parse individuals
  def parseIndividuals(names: String): Unit={
    println("TODO "+names)
  }

  //Parse the entities of the IAProblem (activities,individualq)
  def parsePreferences(key: String, value: String): Unit = key match {
    case _ => println("TODO "+key+" "+value)
  }

}