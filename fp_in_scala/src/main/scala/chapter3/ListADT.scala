package chapter3

import scala.annotation.tailrec

object ListADT {

  def main(args: Array[String]): Unit = {
    val list = Cons(1, Cons(2, Cons(3, Nil)))

    assert(List.tail(list) == Cons(2, Cons(3, Nil)))


    assert(List.drop(1, list) == Cons(2, Cons(3, Nil)))
    assert(List.drop(2, list) == Cons(3, Nil))
    assert(List.drop(5, list) == Nil)


    assert(List.dropWhile(list)(_ < 3) == Cons(3, Nil))
    assert(List.dropWhile(list)(_ < 10) == Nil)


    assert(List.setHead(10, list) == Cons(10, Cons(2, Cons(3, Nil))))
    assert(List.setHead(10, Nil) == Cons(10, Nil))


    assert(List.init(list) == Cons(1, Cons(2, Nil)))


    assert(List.length(list) == 3)
    assert(List.length(Nil) == 0)
    assert(List.length(Cons(3, Nil)) == 1)


    assert(List.foldLeft(1)(list)(_ * _) == 6)
    assert(List.sum(list) == 6)
    assert(List.product(list) == 6)
    assert(List.lengthViaFoldLeft(list) == 3)
    assert(List.lengthViaFoldLeft(Nil) == 0)


    assert(List.reverse(list) == Cons(3, Cons(2, Cons(1, Nil))))


    val list2 = Cons(4, Cons(5, Nil))
    assert(List.append(list, list2) == Cons(1, Cons(2, Cons(3, Cons(4, Cons(5, Nil))))))


    val listOfLists = Cons(Cons(1, Cons(2, Nil)), Cons(Cons(3, Cons(4, Nil)), Cons(Cons(5, Cons(6, Cons(7, Nil))), Nil)))
    assert(List.flatten(listOfLists) == Cons(1, Cons(2, Cons(3, Cons(4, Cons(5, Cons(6, Cons(7, Nil))))))))


    assert(List.map(list2)(_ + 1) == Cons(5, Cons(6, Nil)))
    assert(List.mapViaFoldRight(list2)(_ + 1) == Cons(5, Cons(6, Nil)))


    val list3 = Cons(4, Cons(5, Cons(6, Nil)))
    assert(List.filter(list3)(_ % 2 == 0) == Cons(4, Cons(6, Nil)))
    assert(List.filter(list3)(_ % 2 == 0) == Cons(4, Cons(6, Nil)))


    assert(List.flatMap(list3)(el => Cons(el, Cons(el, Nil))) == Cons(4, Cons(4, Cons(5, Cons(5, Cons(6, Cons(6, Nil)))))))


    assert(List.filterViaFlatMap(list3)(_ % 2 == 0) == Cons(4, Cons(6, Nil)))


    assert(List.add(list2, list3) == Cons(8, Cons(10, Nil)))


    assert(List.hasSubsequence(Cons(4, Cons(5, Cons(6, Nil))), Cons(4, Cons(5, Nil))))
    assert(List.hasSubsequence(Cons(4, Cons(5, Cons(6, Nil))), Cons(5, Cons(6, Nil))))
    assert(List.hasSubsequence(Cons(4, Cons(5, Cons(6, Nil))), Cons(6, Nil)))
    assert(!List.hasSubsequence(Cons(4, Cons(5, Cons(6, Nil))), Cons(3, Cons(5, Nil))))
    assert(!List.hasSubsequence(Cons(3, Cons(5, Nil)), Cons(4, Cons(5, Cons(6, Nil)))))
  }

  sealed trait List[+A]

  case class Cons[+A](head: A, private val tail: List[A]) extends List[A]

  case object Nil extends List[Nothing]

  object List {

    def empty[A]: List[A] = Nil

    /**
      * EXERCISE 2:
      * Implement the function tail for "removing" the first element of a List.
      * Notice the function takes constant time. What are different choices you could make in your implementation
      * if the List is Nil?
      */
    def tail[A](list: List[A]): List[A] = list match {
      case Nil =>
        throw new RuntimeException("Couldn't get tail from empty list")
      case Cons(_, tail) => tail
    }

    /**
      * EXERCISE 3:
      * Generalize tail to the function drop, which removes the first n elements from a list.
      */
    def drop[A](n: Int, list: List[A]): List[A] = {
      if (n == 0) list
      else {
        list match {
          case Nil => Nil
          case _ => drop(n - 1, tail(list))
        }
      }
    }

    /**
      * EXERCISE 4: Implement dropWhile,10 which removes elements from the List prefix as long as they match a predicate.
      * Again, notice these functions take time proportional only to the number of elements being dropped
      * we do not need to make a copy of the entire List.
      */
    def dropWhile[A](list: List[A])(f: A => Boolean): List[A] = {
      list match {
        case Cons(el, tail) if f(el) => dropWhile(tail)(f)
        case rest@_ => rest
      }
    }

    /**
      * EXERCISE 5:
      * Using the same idea, implement the function setHead for replacing the first element of a List with a different value.
      */
    def setHead[A](head: A, list: List[A]): List[A] = {
      list match {
        case Nil => Cons(head, Nil)
        case Cons(_, tail) => Cons(head, tail)
      }
    }

    /**
      *
      * EXERCISE 6:
      * Not everything works out so nicely. Implement a function, init, which returns a List consisting of
      * all but the last element of a List. So, given List(1,2,3,4), init will return List(1,2,3)
      */
    def init[A](list: List[A]): List[A] = {
      list match {
        case Nil =>
          throw new RuntimeException(
            "Couldn't remove last element from empty list")
        case Cons(_, Nil) => Nil
        case Cons(head, tail) => Cons(head, init(tail))
      }
    }

    def foldRight[A, B](z: B)(list: List[A])(f: (A, B) => B): B = {
      list match {
        case Nil => z
        case Cons(x, xs) => f(x, foldRight(z)(xs)(f))
      }
    }

    /**
      * EXERCISE 9:
      * Compute the length of a list using foldRight.
      */
    def length[A](list: List[A]): Int = foldRight(0)(list)((_, acc) => acc + 1)

    /**
      * EXERCISE 10: foldRight is not tail-recursive and will StackOverflow for large lists.
      * Convince yourself that this is the case, then write another general list-recursion function,
      * foldLeft that is tail-recursive, using the techniques we discussed in the previous chapter.
      */
    @tailrec
    def foldLeft[A, B](z: B)(list: List[A])(f: (B, A) => B): B = {
      list match {
        case Nil => z
        case Cons(head, tail) => foldLeft(f(z, head))(tail)(f)
      }
    }

    /**
      * EXERCISE 11:
      * Write sum, product, and a function to compute the length of a list using foldLeft.
      */
    def sum[A](list: List[A])(implicit num: Numeric[A]): A = foldLeft(num.zero)(list)(num.plus)

    def product[A](list: List[A])(implicit num: Numeric[A]): A = foldLeft(num.one)(list)(num.times)

    def lengthViaFoldLeft[A](list: List[A]): Int = foldLeft(0)(list)((acc, _) => acc + 1)

    /**
      * EXERCISE 12:
      * Write a function that returns the reverse of a list (so given List(1,2,3) it returns List(3,2,1)).
      * See if you can write it using a fold.
      */

    def reverse[A](list: List[A]): List[A] = foldLeft(List.empty[A])(list)((acc, el) => Cons(el, acc))

    /**
      * EXERCISE 13 (hard):
      * Can you write foldLeft in terms of foldRight? How about the other way around?
      */
    def foldRightViaFoldLeft[A, B](z: B)(list: List[A])(f: (A, B) => B): B = {
      foldLeft(z)(reverse(list))((b, a) => f(a, b))
    }


    /**
      * EXERCISE 14:
      * Implement append in terms of either foldLeft or foldRight.
      */
    def append[A](list1: List[A], list2: List[A]): List[A] = foldRight(list2)(list1)((el, acc) => Cons(el, acc))

    /**
      * EXERCISE 15 (hard):
      * Write a function that concatenates a list of lists into a single list.
      * Its runtime should be linear in the total length of all lists. Try to use functions we have already defined.
      */
    def flatten[A](list: List[List[A]]): List[A] = {
      foldLeft(List.empty[A])(list)(append)
    }

    /**
      * EXERCISE 16-18:
      * Write a function map, that generalizes modifying each element in a list while maintaining the structure of the list
      */
    def map[A, B](list: List[A])(f: A => B): List[B] = {
      def loop(list: List[A], acc: List[B]): List[B] = {
        list match {
          case Nil => acc
          case Cons(el, tail) => loop(tail, Cons(f(el), acc))
        }
      }

      reverse(loop(list, List.empty))
    }

    def mapViaFoldRight[A, B](list: List[A])(f: A => B): List[B] = {
      foldRightViaFoldLeft(List.empty[B])(list)((el, acc) => Cons(f(el), acc))
    }


    /**
      * EXERCISE 19:
      * Write a function filter that removes elements from a list unless they satisfy a given predicate.
      * Use it to remote all odd numbers from a List[Int]
      */
    def filter[A](list: List[A])(f: A => Boolean): List[A] = {
      foldRightViaFoldLeft(List.empty[A])(list)((el, acc) => if (f(el)) Cons(el, acc) else acc)
    }


    /**
      * EXERCISE 20:
      * Write a function flatMap, that works like map except that the function given will return a list instead of a
      * single result, and that list should be inserted into the final resulting list.
      */

    def flatMap[A, B](list: List[A])(f: A => List[B]): List[B] = {
      flatten(map(list)(f))
    }


    /**
      * EXERCISE 21:
      * Can you use flatMap to implement filter?
      */
    def filterViaFlatMap[A](list: List[A])(f: A => Boolean): List[A] = {
      flatMap(list)(el => if (f(el)) Cons(el, Nil) else Nil)
    }


    /**
      * EXERCISE 22:
      * Write a function that accepts two lists and constructs a new list by adding corresponding elements.
      * For example, List(1,2,3) and List(4,5,6) becomes List(5,7,9)
      */
    def add(list1: List[Int], list2: List[Int]): List[Int] = (list1, list2) match {
      case (Nil, _) => Nil
      case (_, Nil) => Nil
      case (Cons(x, xs), Cons(y, ys)) => Cons(x + y, add(xs, ys))
    }


    /**
      * EXERCISE 23:
      * Generalize the function you just wrote so that it's not specific to integers or addition.
      */
    def addGeneric[A](list1: List[A], list2: List[A])(f: (A, A) => A): List[A] = (list1, list2) match {
      case (Nil, _) => Nil
      case (_, Nil) => Nil
      case (Cons(x, xs), Cons(y, ys)) => Cons(f(x, y), addGeneric(xs, ys)(f))
    }


    /**
      * EXERCISE 24 (hard):
      * As an example, implement hasSubsequence for checking whether a List contains another
      * List as a subsequence. For instance, List(1,2,3,4) would have List(1,2), List(2,3), and List(4) as subsequences,
      * among others.
      * You may have some difficulty finding a concise purely functional implementation that is also efficient.
      */
    @tailrec
    def hasSubsequence[A](list: List[A], sub: List[A]): Boolean = {

      @tailrec
      def loop(l: List[A], s: List[A]): Boolean = (l, s) match {
        case (_, Nil) => true
        case (Cons(x, xs), Cons(y, ys)) if x == y => loop(xs, ys)
        case (_, _) => false
      }

      list match {
        case Nil => false
        case _ if loop(list, sub) => true
        case _ => hasSubsequence(List.tail(list), sub)
      }

    }

  }

}
