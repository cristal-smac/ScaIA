package hedonic

import hedonic.TwoIsACompagnyThreeIsIrational._
import org.scaia.hedonic.{Coalition, Matching, Partition}
import org.scaia.solver.hedonic.CISSolver
import org.scalatest.FlatSpec

class wTestTwoIsACompagnyThreeIsIrational extends FlatSpec {

  val matching1= new Matching(g, new Partition(new Coalition(player1,player2), new Coalition(player3)))

  "The matching {{1,2},{3}}" should "be sound" in {
    assert(matching1.isSoundMatching())
  }

  "The matching {{1,2},{3}}" should "be individually rational" in {
    assert(matching1.isRational())
  }

  "The matching {{1,2},{3}}" should "not be core stable" in {
    assert(! matching1.isCoreStable())
  }

  "The matching {{1,2},{3}}" should "not be Nash stable" in {
    assert(! matching1.isNashStable())
  }

  "The matching {{1,2},{3}}" should "not be individually stable" in {
    assert(! matching1.isIndividuallyStable())
  }

  "The matching {{1,2},{3}}" should "be contactually individually stable" in {
    assert(matching1.isContractuallyIndividuallyStable())
  }

  val matching2= new Matching(g, new Partition(new Coalition(player1,player3), new Coalition(player2)))

  "The matching {{1,3},{2}}" should "be sound" in {
    assert(matching2.isSoundMatching())
  }

  "The matching {{1,3},{2}}" should "be individually rational" in {
    assert(matching2.isRational())
  }

  "The matching {{1,3},{2}}" should "not be core stable" in {
    assert(! matching2.isCoreStable())
  }

  "The matching {{1,3},{2}}" should "not be Nash stable" in {
    assert(! matching2.isNashStable())
  }

  "The matching {{1,3},{2}}" should "not be individually stable" in {
    assert(! matching2.isIndividuallyStable())
  }

  "The matching {{1,3},{2}}" should "be contactually individually stable" in {
    assert(matching2.isContractuallyIndividuallyStable())
  }

  val matching3= new Matching(g, new Partition(new Coalition(player2,player3), new Coalition(player1)))

  "The matching {{2,3},{1}}" should "be sound" in {
    assert(matching3.isSoundMatching())
  }

  "The matching {{2,3},{1}}" should "be individually rational" in {
    assert(matching3.isRational())
  }

  "The matching {{2,3},{1}}" should "not be core stable" in {
    assert(! matching3.isCoreStable())
  }

  "The matching {{2,3},{1}}" should "not be Nash stable" in {
    assert(! matching3.isNashStable())
  }

  "The matching {{2,3},{1}}" should "not be individually stable" in {
    assert(! matching3.isIndividuallyStable())
  }

  "The matching {{2,3},{1}}" should "be contractually individually stable" in {
    assert(matching3.isContractuallyIndividuallyStable())
  }

  "The CIS solver" should "returns the matching {{1,3},{2}}" in {
    var solver = new CISSolver(g)
    assert(matching2.equals(solver.solve()))
  }

}