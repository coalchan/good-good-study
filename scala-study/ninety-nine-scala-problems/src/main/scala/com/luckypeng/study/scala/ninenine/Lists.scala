package com.luckypeng.study.scala.ninenine

import scala.collection.mutable.ListBuffer
import scala.reflect.ClassTag
import scala.util.Random

/**
  * Working with lists:
  * In Scala, lists are objects of type List[A], where A can be any type.
  * Lists are effective for many recursive algorithms, because it's easy to add elements to the head of a list,
  * and to get the tail of the list, which is everything but the first element.
  * The solutions to the problems in this section will be in objects named after the problems (p01, p02, etc.).
  * You can compile the source files with scalac and thereafter use import to bring the functions into scope.
  * Some of the problems can be solved easily by using imported solutions to previous problems.
  * In many cases, there's more than one reasonable approach. The files linked here may include multiple solutions, with all but one commented out.
  * They'll also indicate whether there's a builtin method in Scala that accomplishes the task.
  */

object Lists {
  /**
    * P01 (*) Find the last element of a list.
    * @param list
    * @tparam T
    * @return
    */
  def last[T](list: List[T]): Option[T] = list match {
    case Nil => None
    case head :: Nil => Some(head)
    case head :: tail => last(tail)
  }

  /**
    * P02 (*) Find the last but one element of a list.
    * @param list
    * @tparam T
    * @return
    */
  def penultimate[T](list: List[T]): Option[T] = list match {
    case Nil => None
    case lastButOne :: last :: Nil => Some(lastButOne)
    case head :: tail => penultimate(tail)
  }

  /**
    * P03 (*) Find the Kth element of a list.
    * @param n
    * @param list
    * @tparam T
    * @return
    */
  def nth[T](n: Int, list: List[T]): T = {
    if (n == 0) {
      return list.head
    } else {
      nth(n-1, list.tail)
    }
  }

  /**
    * P04 (*) Find the number of elements of a list.
    * @param list
    * @tparam T
    * @return
    */
  def length[T](list: List[T]): Int = list match {
    case Nil => 0
    case _ => length(list.tail) + 1
  }

  /**
    * P05 (*) Reverse a list.
    * @param list
    * @tparam T
    * @return
    */
  def reverse[T](list: List[T]): List[T] = {
    var result: List[T] = Nil
    for (each <- list) result = each :: result
    result
  }

  /**
    * P06 (*) Find out whether a list is a palindrome.
    * @param list
    * @tparam T
    * @return
    */
  def isPalindrome[T](list: List[T]): Boolean = {
    list.zip(list.reverse).forall{case (a, b) => a == b}
  }

  /**
    * P07 (**) Flatten a nested list structure.
    * @param list
    * @return
    */
  def flatten(list: List[Any]): List[Any] = list flatMap {
    case ms: List[Any] => flatten(ms)
    case e => List(e)
  }

  /**
    * P08 (**) Eliminate consecutive duplicates of list elements.
    * dropWhile: 依次匹配去除符合条件的元素，直到不符合条件,且之后的元素不在判断
    * @param list
    * @tparam T
    * @return
    */
  def compress[T](list: List[T]): List[T] = list match {
    case head :: tail => head :: compress(tail.dropWhile(_ == head))
    case _ => Nil
  }

  /**
    * P09 (**) Pack consecutive duplicates of list elements into sublists.
    * foldRight: 右折叠函数
    * @param list
    * @tparam T
    * @return
    */
  def pack[T](list: List[T]): List[List[T]] = list.foldRight(List[List[T]]()) {
    (h, r) => {
      if(r.isEmpty || r.head.head != h) List(h) :: r
      else (List(h) ::: r.head) :: r.tail
    }
  }

  /**
    * P10 (*) Run-length encoding of a list.
    * @param list
    * @tparam T
    * @return
    */
  def encode[T](list: List[T]): List[(Int, T)] = pack(list).map(x => (x.length, x.head))

  /**
    * P11 (*) Modified run-length encoding.
    * @param list
    * @tparam T
    * @return
    */
  def encodeModified[T](list: List[T]): List[Any] = encode(list).map(x => if(x._1 == 1) x._2 else x)

  /**
    * P12 (**) Decode a run-length encoded list.
    * @param list
    * @tparam T
    * @return
    */
  def decode[T](list: List[(Int, T)]): List[T] = list.flatMap(x => List.fill(x._1)(x._2))

  /**
    * P13 (**) Run-length encoding of a list (direct solution).
    * @param list
    * @tparam T
    * @return
    */
  def encodeDirect[T](list: List[T]): List[(Int, T)] = list.foldRight(List[(Int, T)]()) {
    (h, r) => {
      if(r.isEmpty || r.head._2 != h) (1, h) :: r
      else (r.head._1 + 1, r.head._2) :: r.tail
    }
  }

  /**
    * P14 (*) Duplicate the elements of a list.
    * @param list
    * @tparam T
    * @return
    */
  def duplicate[T](list: List[T]): List[T] = list.flatMap(x => List.fill(2)(x))

  /**
    * P15 (**) Duplicate the elements of a list a given number of times.
    * @param times
    * @param list
    * @tparam T
    * @return
    */
  def duplicateN[T](times: Int, list: List[T]): List[T] = list.flatMap(x => List.fill(times)(x))

  /**
    * P16 (**) Drop every Nth element from a list. Example:
    * @param index
    * @param list
    * @tparam T
    * @return
    */
  def drop[T](index: Int, list: List[T]): List[T] = {
    def getWhile(num: Int, xs: List[T]) : List[T] = xs match {
      case Nil => Nil
      case head :: tail if (num % index != 0) => head :: getWhile(num + 1, tail)
      case head :: tail => getWhile(num + 1, tail)
    }
    getWhile(1, list)
  }

  /**
    * P17 (*) Split a list into two parts. The length of the first part is given. Use a Tuple for your result.
    * @param index
    * @param list
    * @tparam A
    * @return
    */
  def split[A](index: Int, list: List[A]): (List[A], List[A]) = {
    var front = new ListBuffer[A]
    var end = list
    var i = 0
    while (!end.isEmpty && i < index) {
      front += end.head
      i += 1
      end = end.tail
    }
    (front.toList, end)
  }

  /**
    * P18 (**) Extract a slice from a list.
    * Given two indices, I and K, the slice is the list containing the elements from and including the Ith element up to
    * but not including the Kth element of the original list. Start counting the elements with 0.
    * @param start
    * @param end
    * @param list
    * @tparam A
    * @return
    */
  def slice[A](start: Int, end: Int, list: List[A]): List[A] = {
    var xs = new ListBuffer[A]
    var i = 0
    var ls = list
    while (!ls.isEmpty && i < end) {
      if (i >= start) xs += ls.head
      i += 1
      ls = ls.tail
    }
    xs.toList
  }

  /**
    * P19 (**) Rotate a list N places to the left.
    * @param offset
    * @param list
    * @tparam A
    * @return
    */
  def rotate[A](offset: Int, list: List[A]): List[A] = {
    var index = offset
    if (offset < 0) index = list.length + offset
    var left = list
    var right = new ListBuffer[A]
    var i = 0
    while (i < index) {
      right += left.head
      left = left.tail
      i += 1
    }
    left ::: right.toList
  }

  /**
    * P20 (*) Remove the Kth element from a list. Return the list and the removed element in a Tuple.
    * Elements are numbered from 0.
    * @param at
    * @param list
    * @tparam A
    * @return
    */
  def removeAt[A](at: Int, list: List[A]): (List[A], A) = {
    val rest = list.drop(at)
    (list.take(at) ::: rest.tail, rest.head)
  }

  /**
    * P21 (*) Insert an element at a given position into a list.
    * @param insert
    * @param at
    * @param list
    * @tparam A
    * @return
    */
  def insertAt[A](insert: A, at: Int, list: List[A]): List[A] = {
    list.take(at) ::: List(insert) ::: list.drop(at)
  }

  /**
    * P22 (*) Create a list containing all integers within a given range.
    * @param start
    * @param end
    * @return
    */
  def range(start: Int, end: Int): List[Int] = {
    var xs = List[Int]()
    var i = end
    while (i >= start) {
      xs = i :: xs
      i -= 1
    }
    xs
  }

  /**
    * P23 (**) Extract a given number of randomly selected elements from a list.
    * @param num
    * @param list
    * @tparam A
    * @return
    */
  def randomSelect[A](num: Int, list: List[A]): List[A] = {
    if (num <= 0) Nil
    else {
      val (rest, x) = removeAt((new Random).nextInt(list.length), list)
      x :: randomSelect(num - 1, rest)
    }
  }

  /**
    * P24 (*) Lotto: Draw N different random numbers from the set 1..M.
    * @param num
    * @param max
    * @return
    */
  def lotto(num: Int, max: Int): List[Int] = {
    randomSelect(num, range(1, max))
  }

  /**
    * P25 (*) Generate a random permutation of the elements of a list. Hint: Use the solution of problem P23.
    * @param list
    * @tparam A
    * @return
    */
  // TODO 如果不加 ClassTag 就会报错，why?
  def randomPermute[A:ClassTag](list: List[A]): List[A] = {
    val ls = list.toArray
    val r = new Random
    for (i <- ls.length - 1 to 1 by -1) {
      val index = r.nextInt(i + 1)
      val swap = ls(index)
      ls.update(index, ls(i))
      ls.update(i, swap)
    }
    ls.toList
  }

  def flatMapSublists[A,B](ls: List[A])(f: (List[A]) => List[B]): List[B] = ls match {
    case Nil => Nil
    case sublist @ _ :: tail => f(sublist) ::: flatMapSublists(tail)(f)
  }

  /**
    * P26 (**) Generate the combinations of K distinct objects chosen from the N elements of a list.
    * 即穷举所有组合方式.
    *
    * 这里实现的算法思路为: 比如从1,2,3,4,5中选择3个元素，则可拆分为:
    * i)   选取1, 然后从剩下的 2,3,4,5 中选择2个元素
    * ii)  选取2, 然后从剩下的 3,4,5 中选择2个元素
    * iii) 选取3, 然后从剩下的 4,5 中选择2个元素
    * iv)  选取4, 然后从剩下的 5 中选择2个元素
    * v)   选取5, 然后从剩下的 Nil 中选择2个元素
    *
    * 然后递归:
    * 上述的i)继续拆分:
    * a) 选取2, 然后从剩下的 3,4,5 中选择1个元素
    * b) 选取3, 然后从剩下的 4,5 中选择1个元素
    * c) 选取4, 然后从剩下的 5 中选择1个元素
    * d) 选取5, 然后从剩下的 Nil 中选择1个元素
    *
    * 依次类推，即可得到所有的组合方式
    * @param num
    * @param list
    * @tparam A
    * @return
    */
  def combinations[A](num: Int, list: List[A]): List[List[A]] = {
    if (num == 0) List(Nil)
    else flatMapSublists(list) { sl =>
      combinations(num - 1, sl.tail) map {sl.head :: _}
    }
  }

  /**
    * P27 (**) Group the elements of a set into disjoint subsets.
    * @param combineList
    * @param list
    * @tparam A
    * @return
    */
  def group[A](combineList: List[Int], list: List[A]): List[List[List[A]]] = combineList match {
    case Nil => List(Nil)
    case n :: tail => combinations(n, list) flatMap { ls =>
      group(tail, list.diff(ls)) map (ls :: _)
    }
  }

  /**
    * P28 (**)
    * a) Sorting a list of lists according to length of sublists.
    * @param list
    * @tparam A
    * @return
    */
  def lSort[A](list: List[List[A]]): List[List[A]] = list.sortBy(_.length)

  /**
    * P28 (**)
    * b) Sorting a list of lists according to length frequency of sublists.
    * @param list
    * @tparam A
    * @return
    */
  def lSortFreq[A](list: List[List[A]]): List[List[A]] = {
    list.groupBy(_.length).toList.sortBy(_._2.length).map(_._2).reduce((a, b) => a:::b)
  }
}