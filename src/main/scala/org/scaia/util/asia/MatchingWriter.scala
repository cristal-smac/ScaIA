package org.scaia.util.asia

import java.io._

import org.scaia.asia.{Activity, Group, IAProblem, Individual, Matching}
import org.scaia.solver.asia.{SelectiveSolver, Utilitarian}

/**
  * Build a matching from a text file
  * @param pathName the output filename
  * @param matching is the solution to write
  */
class MatchingWriter(pathName: String, matching : Matching){
  val file = new File(pathName)
  def write() : Unit = {
    val bw = new BufferedWriter(new FileWriter(file))
    bw.write(matching.toString)
    bw.close()
  }
}

/**
  * Test MatchingWriter
  */
object MatchingWriter extends App{
  // A IAProblem instance
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
  val solver = new SelectiveSolver(pb, false,Utilitarian)
  val matching= solver.solve()
  val writer=new MatchingWriter("examples/asia/circularPreferenceMatching.txt",matching)
  writer.write()
}
