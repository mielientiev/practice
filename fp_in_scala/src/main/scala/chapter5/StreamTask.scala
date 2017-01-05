package chapter5

import scala.annotation.tailrec
import scala.collection.mutable.ListBuffer

object StreamTask {

  def main(args: Array[String]): Unit = {
    val stream = 1 #:: 2 #:: 3 #:: 4 #:: Stream.empty
    println(stream.toList)
    println(stream.take(3).take(1).toList)
    println(stream.takeWhile(_ < 3).toList)
    println(stream.takeWhileUnfold(_ < 3).toList)
    println(stream.takeWhileViaFoldRight(_ < 3).toList)
    val stream2 = 5 #:: 6 #:: Stream.empty
    println(stream.append(stream2).toList)
    println(stream.append(stream2).filter(_ % 2 == 0).toList)
    println(stream.foldRight(0)(_ + _))
    val f: Int => Boolean = _ % 2 == 0
    println(Stream(2,2,3,4).forAll(f))
    println(Stream(2,2,3,4).mapUnfold(_ + 1).toList)

    println(Stream.constant(2).take(4).toList)
    println(Stream.constantUnfold(2).take(4).toList)
    println(Stream.from(2).take(10).toList)
    println(Stream.fromUnfold(2).take(10).toList)
    println(Stream.fib.take(10).toList)
    println(Stream.fibUnfold.takeUnfold(10).toList)
    println(stream.tails.map(_.toList).toList)

    val stream3 = Stream(1,2,3)
    println(stream3.scanRight(0)(_ + _).toList)
  }

  sealed trait Stream[+A] {
    def #::[B >: A](hd: => B): Stream[B] = Stream.cons(hd, this)

    /**
      * EXERCISE 1: Write a function to convert a Stream to a List, which will force its evaluation and let us look at it in the REPL.
      * You can convert to the regular List type in the standard library. You can place this and other functions that accept
      * a Stream inside the Stream trait.
      */
    def toList: List[A] = {
      val buffer = ListBuffer[A]()
      @tailrec
      def innerToList(stream: => Stream[A]): List[A] = stream match {
        case Cons(head, tail) => buffer+=head(); innerToList(tail())
        case Empty => buffer.toList
      }
      innerToList(this)
    }

    /**
      * EXERCISE 2:
      * Write a function take for returning the first n elements of a Stream.
      */
    def take(n: Int): Stream[A] = {
      if (n == 0) Empty
      else {
        this match {
          case Empty => Empty
          case Cons(h, tail) => Stream.cons(h(), tail().take(n - 1))
        }
      }
    }

    /**
      * EXERCISE 3:
      * Write the function takeWhile for returning all starting elements of a Stream that match the given predicate.
      */
    def takeWhile(p: A => Boolean): Stream[A] = this match {
      case Cons(h, tail) if p(h()) => println(h()); Stream.cons(h(), tail().takeWhile(p))
      case _ => Empty
    }

    def foldRight[B](z: => B)(f: (A, => B) => B): B = {
      this match {
        case Cons(head, tail) => f(head(), tail().foldRight(z)(f))
        case Empty => z
      }
    }

    def exists(p: A => Boolean): Boolean = foldRight(false)((a, b) => p(a) || b)


    /**
      * EXERCISE 4:
      * Implement forAll, which checks that all elements in the Stream match a given predicate.
      * Your implementation should terminate the traversal as soon as it encounters a non-matching value.
      */
    def forAll(p: A => Boolean): Boolean = {
      foldRight(true)((el, acc) =>  p(el) && acc)
    }

    /**
      * EXERCISE 5:
      * Use foldRight to implement takeWhile. This will construct a stream incrementally, and only if
      * the values in the result are demanded by some other expression.
      */

    def takeWhileViaFoldRight(p: A => Boolean): Stream[A] = {
      foldRight(Stream.empty[A])((el, acc) => if (p(el)) Stream.cons(el, acc) else Stream.empty)
    }


    /**
      * EXERCISE 6:
      * Implement map, filter, append, and flatMap using foldRight.
      */

    def map[B](f: A => B): Stream[B] = {
      foldRight(Stream.empty[B])((el, acc) => Stream.cons(f(el), acc))
    }

    def append[B >: A](str: => Stream[B]): Stream[B] = {
      foldRight(str)((el, accStr) => Stream.cons(el, accStr))
    }


    def filter(f: A => Boolean): Stream[A] = {
      foldRight(Stream.empty[A])((el, acc) => if (f(el)) Stream.cons(el, acc) else acc)
    }

    def flatMap[B](f: A => Stream[B]): Stream[B] = {
      foldRight(Stream.empty[B])((el, acc) => f(el).append(acc))
    }


    /**
      * EXERCISE 12:
      * Use unfold to implement map, take, takeWhile, zip (as in chapter 3), and zipAll.
      * The zipAll function should continue the traversal as long as either stream has more elements — it uses Option
      * to indicate whether each stream has been exhausted.
      */

    def mapUnfold[B](f: A => B): Stream[B] = Stream.unfold(this){
      case Cons(head, tail) => Some(f(head()), tail())
      case Empty => None
    }


    def takeUnfold(n: Int): Stream[A] = {
      Stream.unfold((this, n)){
        case (Cons(head, tail), counter) if counter > 0 => Some(head(), (tail(), counter-1))
        case _ => None
      }
    }

    def takeWhileUnfold(p: A => Boolean): Stream[A] = {
      Stream.unfold(this){
        case Cons(head, tail) if p(head()) => Some(head(), tail())
        case _ => None
      }
    }

    def zip[B](str: Stream[B]): Stream[(A, B)] = {
      Stream.unfold((this, str)) {
        case (Cons(x, xs), Cons(y, ys)) => Some((x(), y()), (xs(), ys()))
        case _ => None
      }
    }


    def zipAll[B](str: Stream[B]): Stream[(Option[A], Option[B])] = {
      Stream.unfold((this, str)) {
        case (Empty, Empty) => None
        case (Empty, Cons(y, ys)) => Some((None, Some(y())), (Stream.empty, ys()))
        case (Cons(x, xs), Empty) => Some((Some(x()), None), (xs(), Stream.empty))
        case (Cons(x, xs), Cons(y, ys)) => Some((Some(x()), Some(y())), (xs(), ys()))
      }
    }


    /**
      * EXERCISE 13 (hard):
      * implement startsWith using functions you've written.
      * It should check if one Stream is a prefix of another.
      * For instance, Stream(1,2,3) starsWith Stream(1,2) would be true.
      */
    def startsWith[B >: A](str: Stream[B]): Boolean = {
      this.zipAll(str).takeWhile(_._2.isEmpty).forAll{
        case (x, y) => x == y
      }
    }

    /**
      * EXERCISE 14:
      * implement tails using unfold. For a given Stream, tails returns the Stream of suffixes of the input
      * sequence, starting with the original Stream. So, given Stream(1,2,3), it would return Stream(Stream(1,2,3), Stream(2,3), Stream(3), Stream.empty).
      */

    def tails: Stream[Stream[A]] = {
      Stream.unfold(this){
        case s@Cons(_, tail) => Some(s, tail())
        case Empty => None
      }.append(Stream(Stream.empty))
    }


    /**
      * EXERCISE 15 (hard, optional):
      * Generalize tails to the function scanRight, which is like a foldRight that returns a stream of the intermediate results.
      */
    def scanRight[B](z: B)(f: (A, => B) => B): Stream[B] = {
      foldRight((z, Stream(z))){
        case (el, (acc, streamAcc)) =>
          val res = f(el,acc)
          (res, Stream.cons(res, streamAcc))
      }._2
    }
  }

  case class Cons[+A](hd: () => A, tl: () => Stream[A]) extends Stream[A] {
    def isEmpty: Boolean = false
  }

  case object Empty extends Stream[Nothing] {
    def isEmpty: Boolean = true
  }

  object Stream {
    def empty[A]: Stream[A] = Empty

    def apply[A](as: A*): Stream[A] = {
      if (as.isEmpty) empty
      else cons(as.head, apply(as.tail: _*))
    }

    /**
      * EXERCISE 7:
      * Generalize ones slightly to the function constant which returns an infinite Stream of a given value.
      */
    def constant[A](a: A): Stream[A] = cons(a, constant(a))

    /**
      * EXERCISE 8:
      * Write a function that generates an infinite stream of integers, starting from n, then n + 1, n + 2, etc.
      */
    def from(n: Int): Stream[Int] = cons(n, from(n + 1))


    /**
      * EXERCISE 9:
      * Write a function fibs that generates the infinite stream of Fibonacci numbers: 0, 1, 1, 2, 3, 5, 8, and so on.
      */
    def fib: Stream[BigInt] = {
      def fromFib(f: BigInt, s: BigInt): Stream[BigInt] = cons(f, fromFib(s, f + s))
      fromFib(0, 1)
    }

    /**
      * EXERCISE 10:
      * We can write a more general stream building function.
      * It takes an initial state, and a function for producing both the next state and the next value in the generated stream.
      * It is usually called unfold
      */
    def unfold[A, S](z: S)(f: S => Option[(A, S)]): Stream[A] = {
      f(z) match {
        case Some((value, nextState)) => cons(value, unfold(nextState)(f))
        case None => empty[A]
      }
    }

    /**
      * EXERCISE 11:
      * Write fibs, from, constant, and ones in terms of unfold
      */

    def fibUnfold: Stream[BigInt] = {
      unfold((0, 1))(x => Some((x._1, (x._2, x._1 + x._2))))
    }

    def fromUnfold(n: Int): Stream[Int] = unfold(n)(x => Some(x, x+1))
    def constantUnfold[A](n: A): Stream[A] = unfold(n)(x => Some(x, x))


    def hasSubsequence[A](s1: Stream[A], s2: Stream[A]): Boolean =
      s1.tails.exists(_.startsWith(s2))

    private def cons[A](hd: => A, tl: => Stream[A]): Stream[A] = Cons(() => hd, () => tl)
  }

}
