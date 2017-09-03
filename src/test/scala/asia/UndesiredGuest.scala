// Copyright (C) Maxime MORGE 2017
package asia

import org.scaia.asia.{Activity, Group, IAProblem, Individual}

/**
  * Toy example with an undesired guest
  * */
object UndesiredGuest {
  val debug = false

  /**
    * One activity with a capacity of 3
    */
  val a = new Activity("a",3)

  /**
    * The individual i1 prefers i2
    */
  val i1: Individual = new Individual("i1",3) {
    vMap+=("a" -> 0)
    wMap+=("i2"-> 0.5, "i3" -> -1)
  }

  /**
    * The individual i2 prefers i3
    */
  val i2: Individual = new Individual("i2",3) {
    vMap += ("a" -> 0.0)
    wMap += ("i1" -> 0.5, "i3" -> -1.0)
  }

  /**
    * The individual i3 prefers i1
    */
  val i3: Individual = new Individual("i3",3) {
    vMap+=("a" -> 0.0)
    wMap+=("i1" -> 0.5, "i2" -> 1.0)
  }

  /**
    * The ASIA problem
    */
  val pb= new IAProblem(Group(i1, i2, i3), Set(a))

}