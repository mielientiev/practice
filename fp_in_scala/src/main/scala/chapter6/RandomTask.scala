package chapter6

object RandomTask {

  def main(args: Array[String]): Unit = {
    val (v, state) = RNG.simple(-10).nextInt
    println(RNG.positiveInt(state))
    println(RNG.double(state))
    println(RNG.double3(state))
    println(RNG.intDouble(state))
    println(RNG.doubleInt(state))
    println(RNG.ints(5)(state))
    println(RNG.positiveMax(1325823891)(state))
    println(RNG.flatMap(RNG.double)(x => RNG.unit(x))(state))
  }

  type Rand[+A] = RNG => (A, RNG)

  trait RNG {
    def nextInt: (Int, RNG)
  }

  object RNG {

    def simple(seed: Long): RNG = new RNG {
      def nextInt = {
        val seed2 = (seed * 0x5DEECE66DL + 0xBL) &
          ((1L << 48) - 1)
        ((seed2 >>> 16).asInstanceOf[Int], simple(seed2))
      }
    }

    /**
      * EXERCISE 1:
      * Write a function to generate a random positive integer. Note: you can use x.abs to take the absolute value of an Int, x.
      * Make sure to handle the corner case Int.MinValue, which doesn't have a positive counterpart.
      */
    def positiveInt(rng: RNG): (Int, RNG) = {
      val (rand1, state) = rng.nextInt
      if (rand1 == Int.MinValue) positiveInt(state)
      else if (rand1 < 0) (math.abs(rand1), state)
      else (rand1, state)
    }

    /**
      * EXERCISE 2:
      * Write a function to generate a Double between 0 and 1, not including 1.
      * Note: you can use Int.MaxValue to obtain the maximum positive integer value and you can use x.toDouble to convert an Int, x, to a Double.
      */
    def double(rng: RNG): (Double, RNG) = {
      val (v, state) = positiveInt(rng)
      (v / (Int.MaxValue.toDouble + 1), state)
    }


    /**
      * EXERCISE 3: Write functions to generate an (Int, Double) pair, a (Double, Int) pair, and a (Double, Double, Double)
      * 3-tuple. You should be able to reuse the functions you've already written.
      */

    def intDouble(rng: RNG): ((Int, Double), RNG) = {
      val (intV, state) = rng.nextInt
      val (doubleV, state2) = double(state)
      ((intV, doubleV), state2)
    }

    def doubleInt(rng: RNG): ((Double, Int), RNG) = {
      val ((intV, doubleV), state) = intDouble(rng)
      ((doubleV, intV), state)
    }

    def double3(rng: RNG): ((Double, Double, Double), RNG) = {
      val (v1, state) = double(rng)
      val (v2, state2) = double(state)
      val (v3, state3) = double(state2)
      ((v1, v2, v3), state3)
    }


    /**
      * EXERCISE 4:
      * Write a function to generate a list of random integers.
      */
    def ints(count: Int)(rng: RNG): (List[Int], RNG) = {
      (0 until count).foldRight((List.empty[Int], rng)){
        case (_, (list, state)) =>
          val (v, newState) = state.nextInt
          (v::list, newState)
      }
    }

    val int: Rand[Int] = _.nextInt

    def unit[A](a: A): Rand[A] = rng => (a, rng)

    def map[A, B](s: Rand[A])(f: A => B): Rand[B] = {
      rng => {
        val (v, state) = s(rng)
        (f(v), state)
      }
    }



    /**
      * EXERCISE 5:
      * Use map to generate an Int between 0 and n, inclusive
      */
    def positiveMax(n: Int): Rand[Int] = map[Int, Int](positiveInt)(x => if(x > n) x % n else x)


    /**
      * EXERCISE 6:
      * Use map to reimplement RNG.double in a more elegant way.
      */

    def doubleViaMap(rng: RNG): (Double, RNG) = map(positiveInt)(_ / (Int.MaxValue.toDouble + 1))(rng)


    /**
      * EXERCISE 7: Unfortunately, map is not powerful enough to implement intDouble and doubleInt from before.
      * What we need is a new combinator map2, that can combine two RNG actions into one using a binary rather than unary function.
      * Write its implementation and then use it to reimplement the intDouble and doubleInt functions.
      */

    def map2[A,B,C](ra: Rand[A], rb: Rand[B])(f: (A, B) => C): Rand[C] = {
      rng => {
        val (v, state) = ra(rng)
        val (v2, state2) = rb(state)
        (f(v,v2), state2)
      }
    }

    def intDoubleViaMap2(rng: RNG): ((Int, Double), RNG) = map2(int, double)((x, y) => (x, y))(rng)
    def doubleIntViaMap2(rng: RNG): ((Double, Int), RNG) = map2(int, double)((x, y) => (y, x))(rng)


    /**
      * EXERCISE 8 (hard):
      * If we can combine two RNG transitions, we should be able to combine a whole list of them.
      * Implement sequence, for combining a List of transitions into a single transition.
      * Use it to reimplement the ints function you wrote before. For the latter, you can use the standard library function
      * List.fill(n)(x) to make a list with x repeated n times.
      */
    def sequence[A](fs: List[Rand[A]]): Rand[List[A]] = {
        fs match {
          case Nil => unit(Nil)
          case x :: xs => map2(x, sequence(xs))(_ :: _)
        }
    }

    def sequence2[A](fs: List[Rand[A]]): Rand[List[A]] = {
      fs.foldRight(unit(List.empty[A]))((el, acc) => map2(el, acc)(_ :: _))
    }

    def intsViaSeq(count: Int): Rand[List[Int]] = {
      sequence(List.fill(count)(int))
    }

    /**
      * EXERCISE 9:
      * Implement flatMap, then use it to reimplement positiveInt.
      */
    def flatMap[A,B](f: Rand[A])(g: A => Rand[B]): Rand[B] = {
      rng => {
        val (a, state) = f(rng)
        val (b, state2) = g(a)(state)
        (b, state2)
      }
    }

    def positiveIntViaFlatMap: Rand[Int] = {
      flatMap(int){ x =>
        if (x != Int.MinValue) unit(x) else positiveIntViaFlatMap
      }
    }

    /**
      * EXERCISE 10:
      * Reimplement map and map2 in terms of flatMap.
      */
    // def map[S,A,B](a: S => (A,S))(f: A => B): S => (B,S)
    def mapViaFM[A, B](s: Rand[A])(f: A => B): Rand[B] = {
      flatMap(s)(a => unit(f(a)))
    }

    def map2ViaFM[A,B,C](ra: Rand[A], rb: Rand[B])(f: (A, B) => C): Rand[C] = {
      flatMap(ra)(a => map(rb)(b => f(a, b)))
    }


  }

}
