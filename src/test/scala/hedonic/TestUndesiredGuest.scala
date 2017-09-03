package hedonic

import hedonic.UndesiredGuest._
import org.scaia.hedonic.{Coalition, Matching, Partition}
import org.scaia.solver.hedonic.CISSolver
import org.scalatest.FlatSpec

class TestUndesiredGuest extends FlatSpec {

  val matching= new Matching(g, new Partition(new Coalition(player1,player2), new Coalition(player3)))

  "The matching {{1,2},{3}}" should "be sound" in {
    assert(matching.isSoundMatching())
  }

  "The matching {{1,2},{3}}" should "be individually rational" in {
    assert(matching.isRational())
  }

  "The matching {{1,2},{3}}" should "be core stable" in {
    assert(matching.isCoreStable())
  }

  "The matching {{1,2},{3}}" should "be not Nash stable" in {
    assert(! matching.isNashStable())
  }

  "The matching {{1,2},{3}}" should "be individually stable" in {
    assert(matching.isIndividuallyStable())
  }

  "The matching {{1,2},{3}}" should "be contactually individually stable" in {
    assert(matching.isContractuallyIndividuallyStable())
  }

  "The CIS solver" should "returns the matching {{1,2},{3}}" in {
    var solver = new CISSolver(g)
    assert(matching.equals(solver.solve()))
  }

}