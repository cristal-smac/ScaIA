package org.scaia.util.asia

import java.io._

import org.scaia.asia.{Activity, Group, IAProblem, Individual}

/**
  * Build a IAProblem object from a text file
  */
object IAProblemWriter extends App{
  // The IAProblem instance
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
  // FileWriter
  val file = new File("examples/asia/circularPreferencePb.txt")
  val bw = new BufferedWriter(new FileWriter(file))
  bw.write(pb.toString)
  bw.close()
}
