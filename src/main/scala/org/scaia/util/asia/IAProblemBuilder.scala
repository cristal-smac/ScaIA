// Copyright (C) Maxime MORGE 2017
package org.scaia.util.asia

import org.scaia.asia._

/**
  * Build a IAProblem object from a text file
  */

object IAProblemBuilder extends App{
  //cirularPref
  val a = new Activity("a",2)
  val i1: Individual = new Individual("i1",3) {
    vMap+=("a" -> 0.5)
    wMap+=("i2"-> 1.0, "i3" -> -1.0)
  }
  val i2: Individual = new Individual("i2",3) {
    vMap += ("a" -> 0.5)
    wMap += ("i3" -> 1.0, "i1" -> -1.0)
  }
  val i3: Individual = new Individual("i3",3) {
    vMap+=("a" -> 0.5)
    wMap+=("i1" -> 1.0, "i2" -> -1.0)
  }
  val pb= new IAProblem(Group(i1, i2, i3), Set(a))
  println(pb.toString)

}
