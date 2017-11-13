// Copyright (C) Maxime MORGE 2017
package asia

import org.scaia.asia.{Activity, Group, IAProblem, Individual}

/**
  * Toy example with circular social preferences
  * */
object CircularPref {
  val debug = false

  /**
    * One activity with a capacity of 2
    */
  val a = new Activity("a",2)

  /**
    * The individual i1 prefers i2
    */
  val i1: Individual = new Individual("i1",3) {
    vMap+=("a" -> 0.25)
    wMap+=("i2"-> 1.0, "i3" -> -1.0)
  }

  /**
    * The individual i2 prefers i3
    */
  val i2: Individual = new Individual("i2",3) {
    vMap += ("a" -> 0.25)
    wMap += ("i3" -> 1.0, "i1" -> -1.0)
  }

  /**
    * The individual i3 prefers i1
    */
  val i3: Individual = new Individual("i3",3) {
    vMap+=("a" -> 0.25)
    wMap+=("i1" -> 1.0, "i2" -> -1.0)
  }

  /**
    * The ASIA problem
    */
  val pb= new IAProblem(Group(i1, i2, i3), Set(a))

}