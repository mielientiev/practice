package chapter7

import java.util.concurrent.{ExecutorService, Future, TimeUnit}

object ParTask {

  object Par {

    type Par[A] = ExecutorService => Future[A]

    private case class SimpleFuture[A](value: A) extends Future[A] {
      def isDone = true
      def get(timeout: Long, units: TimeUnit) = value
      def isCancelled = false
      def cancel(evenIfRunning: Boolean): Boolean = false

      override def get(): A = value
    }

    def equal[A](e: ExecutorService)(p: Par[A], p2: Par[A]): Boolean =
      p(e).get == p2(e).get

    //extracts a value from a Par by actually performing the computation.
    def run[A](es: ExecutorService)(f: Par[A]): Future[A] = f(es)

    //injects a constant into a parallel computation
    def unit[A](value: A): Par[A] = _ => SimpleFuture(value)

    //spawns a parallel computation. The computation will not be spawned until forced by run.
    def fork[A](a: => Par[A]): Par[A] = { es =>
      es.submit(() => a(es).get())
    }

    def async[A](a: => A): Par[A] = fork(unit(a))

    def asyncF[A, B](f: A => B): A => Par[B] = a => _ => SimpleFuture(f(a))

    def sortPar(l: Par[List[Int]]) = map(l)(_.sorted)

//    def map[A,B](fa: Par[A])(f: A => B): Par[B] =
//      map2(fa, unit(()))((a,_) => f(a))

    def product[A, B](fa: Par[A], fb: Par[B]): Par[(A, B)] = { es =>
      SimpleFuture((fa(es).get(), fb(es).get()))
    }

    def map[A, B](fa: Par[A])(f: A => B): Par[B] = { es =>
      SimpleFuture(f(fa(es).get()))
    }

    //combines the results of two parallel computations with a binary function.
    def map2[A, B, C](fa: Par[A], fb: Par[B])(f: (A, B) => C): Par[C] =
      map(product(fa, fb))(x => f(x._1, x._2))

    def parMap[A, B](l: List[A])(f: A => B): Par[List[B]] = fork {
      val listF: List[Par[B]] = l.map(asyncF(f))
      map(sequenceBalanced(listF.toIndexedSeq))(_.toList)
    }

    def sequence_1[A](list: List[Par[A]]): Par[List[A]] = {
      list match {
        case Nil => unit(Nil)
        case x :: xs => map2(x, fork(sequence_1(xs)))((x, y) => x :: y)
      }
    }

    def sequenceBalanced[A](as: IndexedSeq[Par[A]]): Par[IndexedSeq[A]] = {
      fork {
        if (as.isEmpty) unit(Vector())
        else if (as.length == 1) map(as.head)(a => Vector(a))
        else {
          val (left, right) = as.splitAt(as.length / 2)
          map2(sequenceBalanced(left), sequenceBalanced(right))((l, r) => l ++ r)
        }
      }
    }

    def parFilter[A](l: List[A])(f: A => Boolean): Par[List[A]] = fork {
      val listF = l.map(asyncF{x => if (f(x)) List(x) else List()})
      map(sequenceBalanced(listF.toIndexedSeq))(_.toList.flatten)
    }
  }
}
