package chapter4

object OptionTask {

  /**
    * EXERCISE 1:
    * We'll explore when you'd use each of these next. But first, as an exercise, implement all of the above functions on Option.
    * As you implement each function, try to think about what it means and in what situations you'd use it.
    */

  trait Option[+A] {
    def flatMap[B](f: A => Option[B]): Option[B] = map(f).getOrElse(None)

    def map[B](f: A => B): Option[B] = fold(Option.empty[B])(x => Some(f(x)))

    def getOrElse[B >: A](default: => B): B = fold(default)(identity)

    def orElse[B >: A](ob: => Option[B]): Option[B] = fold(ob)(_ => this)

    def fold[B](empty: => B)(f: A => B): B = this match {
      case None => empty
      case Some(v) => f(v)
    }

    def filter(f: A => Boolean): Option[A] = this match {
      case Some(v) if f(v) => this
      case _ => None
    }
  }

  case class Some[+A](value: A) extends Option[A]
  case object None extends Option[Nothing]

  object Option {
    def empty[A]: Option[A] = None


    /**
      * EXERCISE 2:
      * Implement the variance function (if the mean is m, variance is the mean of math.pow(x - m, 2), see definition) in terms of mean and flatMap.
      */
    def variance(xs: Seq[Double]): Option[Double] = {
      def mean(xs: Seq[Double]): Option[Double] = if (xs.isEmpty) None else Some(xs.sum / xs.length)

      mean(xs).flatMap(m => mean(xs.map(x => math.pow(x - m, 2))))
    }


    /**
      * EXERCISE 3:
      * bothMatch is an instance of a more general pattern. Write a generic function map2, that combines two Option
      * values using a binary function.
      * If either Option value is None, then the return value is too.
      */
    def map2[A, B, C](a: Option[A], b: Option[B])(f: (A, B) => C): Option[C] = {
      for {
        aValue <- a
        bValue <- b
      } yield f(aValue, bValue)
    }

    /**
      * EXERCISE 5:
      * Write a function sequence, that combines a list of Options into one option containing a list of all the Some
      * values in the original list. If the original list contains None even once, the result of the function should be None,
      * otherwise the result should be Some with a list of all the values.
      */
    def sequence[A](a: List[Option[A]]): Option[List[A]] = {
      a match {
        case Nil => Some(List())
        case x :: xs =>
          map2(x, sequence(xs))(_ :: _)
      }
    }

    /**
      * EXERCISE 6:
      * Implement this function. It is straightforward to do using map and sequence, but try for a
      * more efficient implementation that only looks at the list once. In fact, implement sequence in terms of traverse.
      */
    def traverse[A, B](a: List[A])(f: A => Option[B]): Option[List[B]] = {
      a.foldRight[Option[List[B]]](Some(Nil))((el, acc) => map2(f(el), acc)(_ :: _))
    }

    def traverse_1[A, B](a: List[A])(f: A => Option[B]): Option[List[B]] = {
      a match {
        case Nil => Some(Nil)
        case x :: xs =>
          map2(f(x), traverse_1(xs)(f))(_ :: _)
      }
    }

    def sequence2[A](a: List[Option[A]]): Option[List[A]] = traverse_1(a)(identity)
  }

}
