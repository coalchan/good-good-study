package com.luckypeng.study.scala.ninenine

import org.junit.jupiter.api.Assertions._
import org.junit.jupiter.api.Test
import Lists._

/**
  * Lists的测试用例
  */
class ListsTest {
  @Test
  def p01: Unit = {
    assertEquals(last(List(1, 1, 2, 3, 5, 8)), Some(8))
  }

  @Test
  def p02: Unit = {
    assertEquals(penultimate(List(1, 1, 2, 3, 5, 8)), Some(5))
  }

  @Test
  def p03: Unit = {
    assertEquals(nth(2, List(1, 1, 2, 3, 5, 8)), 2)
  }

  @Test
  def p04: Unit = {
    assertEquals(length((1 to 1000).toList), 1000)
  }

  @Test
  def p05: Unit = {
    assertEquals(reverse(List(1, 1, 2, 3, 5, 8)), List(8, 5, 3, 2, 1, 1))
  }

  @Test
  def p06: Unit = {
    assertTrue(isPalindrome(List(1, 2, 3, 2, 1)))
  }

  @Test
  def p07: Unit = {
    assertEquals(flatten(List(List(1, 1), 2, List(3, List(5, 8)))), List(1, 1, 2, 3, 5, 8))
  }

  @Test
  def p08: Unit = {
    assertEquals(compress(List('a, 'a, 'a, 'a, 'b, 'c, 'c, 'a, 'a, 'd, 'e, 'e, 'e, 'e)), List('a, 'b, 'c, 'a, 'd, 'e))
  }

  @Test
  def p09: Unit = {
    assertEquals(pack(List('a, 'a, 'a, 'a, 'b, 'c, 'c, 'a, 'a, 'd, 'e, 'e, 'e, 'e)),
      List(List('a, 'a, 'a, 'a), List('b), List('c, 'c), List('a, 'a), List('d), List('e, 'e, 'e, 'e)))
  }

  @Test
  def p10: Unit = {
    assertEquals(List((4,'a), (1,'b), (2,'c), (2,'a), (1,'d), (4,'e)),
      encode(List('a, 'a, 'a, 'a, 'b, 'c, 'c, 'a, 'a, 'd, 'e, 'e, 'e, 'e)))
  }

  @Test
  def p11: Unit = {
    assertEquals(List((4,'a), 'b, (2,'c), (2,'a), 'd, (4,'e)),
      encodeModified(List('a, 'a, 'a, 'a, 'b, 'c, 'c, 'a, 'a, 'd, 'e, 'e, 'e, 'e)))
  }

  @Test
  def p12: Unit = {
    assertEquals(List('a, 'a, 'a, 'a, 'b, 'c, 'c, 'a, 'a, 'd, 'e, 'e, 'e, 'e),
      decode(List((4, 'a), (1, 'b), (2, 'c), (2, 'a), (1, 'd), (4, 'e))))
  }

  @Test
  def p13: Unit = {
    assertEquals(List((4,'a), (1,'b), (2,'c), (2,'a), (1,'d), (4,'e)),
      encodeDirect(List('a, 'a, 'a, 'a, 'b, 'c, 'c, 'a, 'a, 'd, 'e, 'e, 'e, 'e)))
  }

  @Test
  def p14: Unit = {
    assertEquals(List('a, 'a, 'b, 'b, 'c, 'c, 'c, 'c, 'd, 'd), duplicate(List('a, 'b, 'c, 'c, 'd)))
  }

  @Test
  def p15: Unit = {
    assertEquals(List('a, 'a, 'a, 'b, 'b, 'b, 'c, 'c, 'c, 'c, 'c, 'c, 'd, 'd, 'd),
      duplicateN(3, List('a, 'b, 'c, 'c, 'd)))
  }

  @Test
  def p16: Unit = {
    assertEquals(List('a, 'b, 'd, 'e, 'g, 'h, 'j, 'k), drop(3, List('a, 'b, 'c, 'd, 'e, 'f, 'g, 'h, 'i, 'j, 'k)))
  }

  @Test
  def p17: Unit = {
    assertEquals((List('a, 'b, 'c),List('d, 'e, 'f, 'g, 'h, 'i, 'j, 'k)), split(3, List('a, 'b, 'c, 'd, 'e, 'f, 'g, 'h, 'i, 'j, 'k)))
  }

  @Test
  def p18: Unit = {
    assertEquals(List('d, 'e, 'f, 'g), slice(3, 7, List('a, 'b, 'c, 'd, 'e, 'f, 'g, 'h, 'i, 'j, 'k)))
  }

  @Test
  def p19: Unit = {
    assertEquals(List('d, 'e, 'f, 'g, 'h, 'i, 'j, 'k, 'a, 'b, 'c), rotate(3, List('a, 'b, 'c, 'd, 'e, 'f, 'g, 'h, 'i, 'j, 'k)))
    assertEquals(List('j, 'k, 'a, 'b, 'c, 'd, 'e, 'f, 'g, 'h, 'i), rotate(-2, List('a, 'b, 'c, 'd, 'e, 'f, 'g, 'h, 'i, 'j, 'k)))
  }

  @Test
  def p20: Unit = {
    assertEquals((List('a, 'c, 'd),'b), removeAt(1, List('a, 'b, 'c, 'd)))
  }

  @Test
  def p21: Unit = {
    assertEquals(List('a, 'new, 'b, 'c, 'd), insertAt('new, 1, List('a, 'b, 'c, 'd)))
  }

  @Test
  def p22: Unit = {
    assertEquals(List(4, 5, 6, 7, 8, 9), range(4, 9))
  }

  @Test
  def p23: Unit = {
    assertEquals(3, randomSelect(3, List('a, 'b, 'c, 'd, 'f, 'g, 'h)).length)
  }

  @Test
  def p24: Unit = {
    assertEquals(6, lotto(6, 49).length)
  }

  @Test
  def p25: Unit = {
    assertEquals(6, randomPermute(List('a, 'b, 'c, 'd, 'e, 'f)).length)
  }

  @Test
  def p26: Unit = {
    assertEquals(List(List(1, 2, 3), List(1, 2, 4), List(1, 3, 4), List(2, 3, 4)), combinations(3, List(1, 2, 3, 4)))
  }

  @Test
  def p27: Unit = {
    assertEquals(List(
        List(List("Aldo", "Beat"), List("Carla", "David", "Evi")),
        List(List("Aldo", "Carla"), List("Beat", "David", "Evi")),
        List(List("Aldo", "David"), List("Beat", "Carla", "Evi")),
        List(List("Aldo", "Evi"), List("Beat", "Carla", "David")),
        List(List("Beat", "Carla"), List("Aldo", "David", "Evi")),
        List(List("Beat", "David"), List("Aldo", "Carla", "Evi")),
        List(List("Beat", "Evi"), List("Aldo", "Carla", "David")),
        List(List("Carla", "David"), List("Aldo", "Beat", "Evi")),
        List(List("Carla", "Evi"), List("Aldo", "Beat", "David")),
        List(List("David", "Evi"), List("Aldo", "Beat", "Carla"))
      ),
      group(List(2, 3), List("Aldo", "Beat", "Carla", "David", "Evi")))
  }

  @Test
  def p28: Unit = {
    assertEquals(List(List('o), List('d, 'e), List('d, 'e), List('m, 'n), List('a, 'b, 'c), List('f, 'g, 'h), List('i, 'j, 'k, 'l))
      ,lSort(List(List('a, 'b, 'c), List('d, 'e), List('f, 'g, 'h), List('d, 'e), List('i, 'j, 'k, 'l), List('m, 'n), List('o))))

    assertEquals(List(List('i, 'j, 'k, 'l), List('o), List('a, 'b, 'c), List('f, 'g, 'h), List('d, 'e), List('d, 'e), List('m, 'n))
      ,lSortFreq(List(List('a, 'b, 'c), List('d, 'e), List('f, 'g, 'h), List('d, 'e), List('i, 'j, 'k, 'l), List('m, 'n), List('o))))
  }
}
