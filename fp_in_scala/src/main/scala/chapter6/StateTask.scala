package chapter6

/**
  * EXERCISE 11:
  * Generalize the functions unit, map, map2, flatMap, and sequence.
  * Add them as methods on the State case class where possible. Otherwise you should put them in a State companion object.
  */
case class State[S, +A](run: S => (A, S)) {

  def map2[B, C](state2: State[S, B])(f: (A, B) => C): State[S, C] = {
    flatMap(a => state2.map(b => f(a, b)))
  }

  def map[B](f: A => B): State[S, B] = flatMap(a => State.unit(f(a)))

  def flatMap[B](f: A => State[S, B]): State[S, B] = State { s =>
    val (v, state) = this.run(s)
    f(v).run(state)
  }

}


object State {
  def sequence[A, S](list: List[State[S, A]]): State[S, List[A]] = {
    list.foldRight[State[S, List[A]]](State.unit(List.empty[A]))((state, acc) => state.map2(acc)(_ :: _))
  }

  def unit[A, S](a: A): State[S, A] = State(s => (a, s))


  def modify[S](f: S => S): State[S, Unit] = for {
    s <- get
    _ <- set(f(s))
  } yield ()

  def get[S]: State[S, S] = State(s => (s, s))

  def set[S](s: S): State[S, Unit] = State(_ => ((), s))


}