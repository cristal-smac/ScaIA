package hedonic

import hedonic.TwoIsACompagnyThreeIsACrowd._
import org.scaia.hedonic.{Coalition, Matching, Partition}
import org.scaia.solver.hedonic.CISSolver
import org.scalatest.FlatSpec

class TestTwoIsACompagnyThreeIsACrowd extends FlatSpec {

  val matching= new Matching(g, new Partition(new Coalition(player1,player2,player3)))

  "The matching {{1,2,3}}" should "be sound" in {
    assert(matching.isSoundMatching())
  }

  "The matching {{1,2,3}}" should "be individually rational" in {
    assert(matching.isRational())
  }

  "The matching {{1,2,3}}" should "not be core stable" in {
    assert(! matching.isCoreStable())
  }

  "The matching {{1,2,3}}" should "be Nash stable" in {
    assert(matching.isNashStable())
  }

  "The matching {{1,2,3}}" should "be individually stable" in {
    assert(matching.isIndividuallyStable())
  }

  "The matching {{1,2,3}}" should "be contactually individually stable" in {
    assert(matching.isContractuallyIndividuallyStable())
  }

  val expectedMatching= new Matching(g, new Partition(new Coalition(player1,player3), new Coalition(player2)))

  "The CIS solver" should "returns the matching {{1,3},{2}}" in {
    var solver = new CISSolver(g)
    assert(expectedMatching.equals(solver.solve()))
  }

}