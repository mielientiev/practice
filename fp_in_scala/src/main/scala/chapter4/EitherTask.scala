package chapter4

object EitherTask {

  /**
    * EXERCISE 7:
    * Implement versions of map, flatMap, orElse, and map2 on Either that operate on the Right value.
    */

  trait Either[+E, +A] {
    def map[B](f: A => B): Either[E, B] = this match {
      case a@Left(_) => a
      case Right(value) => Right(f(value))
    }
    def flatMap[EE >: E, B](f: A => Either[EE, B]): Either[EE, B] = this match {
      case a@Left(_) => a
      case Right(value) => f(value)
    }

    def orElse[EE >: E,B >: A](b: => Either[EE, B]): Either[EE, B] = {
      case Left(_) => b
      case r@Right(_) => r
    }

    def map2[EE >: E, B, C](b: Either[EE, B])(f: (A, B) => C): Either[EE, C] = {
      for {
        el1 <- this
        el2 <- b
      } yield f(el1, el2)
    }
  }

  case class Left[+E](err: E) extends Either[E, Nothing]
  case class Right[+A](value: A) extends Either[Nothing, A]

  object Either {
    /**
      * EXERCISE 8:
      * Implement sequence and traverse for Either.
      */
    def traverse[E,A,B](a: List[A])(f: A => Either[E, B]): Either[E, List[B]] = {
      a.foldRight[Either[E, List[B]]](Right(Nil))((el, acc) => f(el).map2(acc)(_ :: _))
    }

    def sequence2[A, E](a: List[Either[E,A]]): Either[E, List[A]] = traverse(a)(identity)
  }


}
