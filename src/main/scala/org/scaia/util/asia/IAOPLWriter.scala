// Copyright (C) Maxime MORGE 2017
package org.scaia.util.asia

import java.io._

import org.scaia.asia.{Activity, Group, IAProblem, Individual}

/**
  * Build a IAOPLWriter object from a text file
  * @param pathName the output filename
  * @param pb is a IAProblem
  */
class IAOPLWriter(pathName: String, pb : IAProblem){
  val file = new File(pathName)
  def write() : Unit = {
    val bw = new BufferedWriter(new FileWriter(file))
    bw.write(pb.toOPL)
    bw.close()
  }
}

/**
  * Test IAOPLWriter
  */
object IAOPLWriter extends App{
  // The IAProblem instance
  val club = new Activity("a",2)
  val ball = new Activity("b",2)

  val blue: Individual = new Individual("1",4) {
    vMap+=("a" -> .5, "b" -> .25)
    wMap+=("2"-> 1.0, "3" -> -0.5, "4" -> -1.0)
  }

  val cyan: Individual = new Individual("2",4) {
    vMap += ("a" -> 0.5, "b" -> 0.25)
    wMap += ("1" -> 1.0, "3" -> 0.5, "4"-> -1.0)
  }

  val magenta: Individual = new Individual("3",4) {
    vMap+=("a" -> 0.5, "b" -> 0.25)
    wMap+=("1" -> 1.0, "2" -> 0.5, "4" -> -1.0)
  }

  val red: Individual = new Individual("4",4) {
    vMap+=("a" -> 0.5, "b" -> 0.25)
    wMap+=("1" -> 1.0, "2" -> 1.0, "3" -> -1.0)
  }
  val pb= new IAProblem(Group(blue, cyan, magenta, red), Set(club, ball))
  val writer=new IAOPLWriter("experiments/OPL/dilemma.dat",pb)
  writer.write()
}


