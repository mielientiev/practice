import scala.annotation.tailrec

/*
EXERCISE 1:
Write a function to get the nth Fibonacci number. The
first two Fibonacci numbers are 0 and 1, and the next number is always the sum of
the previous two. Your definition should use a local tail-recursive function.
 */

def fib(n: Int): Int = {
  @tailrec
  def loop(prev: Int, curr: Int, num: Int): Int = {
    if (num == 0) curr
    else loop(curr, prev + curr, num - 1)
  }
  loop(0, 1, n)
}

println(fib(5))

/*
EXERCISE 2:
Implement isSorted, which checks whether an Array[A] is sorted according to a
given comparison function.
 */
def isSorted[A](as: Array[A])(gt: (A,A) => Boolean): Boolean = {
  def loop(start: Int): Boolean = {
    if (start == as.length - 1) true
    else if (!gt(as(start), as(start+1))) false
    else loop(start + 1)
  }
  loop(0)
}

isSorted(Array(1,2,3,4,5))(_ < _)
isSorted(Array(1,2,3,3,5))(_ <= _)


/*
EXERCISE 3 (hard):
Implement partial1 and write down a concrete usage of it.
There is only one possible implementation that compiles.
 */
def partial1[A,B,C](a: A, f: (A,B) => C): B => C = f(a, _)

val partialF = partial1(1, (x: Int, y: Int) => x + y)
partialF(3)

/*
EXERCISE 4 (hard):
Let's look at another example, currying, which converts a function of N arguments
into a function of one argument that returns another function as its result.11
Here again, there is only one implementation that typechecks
 */

def curry[A,B,C](f: (A, B) => C): A => (B => C) = x => y => f(x, y)
val curryF = curry((x: Int, y: Int) => x.toString + y.toString)
curryF(1)(4)

/*
EXERCISE 5 (optional):
Implement uncurry, which reverses the transformation of curry.
Note that since => associates to the right, A => (B => C) can be written as A => B => C.
 */
def uncurry[A,B,C](f: A => B => C): (A, B) => C = (x, y) => f(x)(y)

val uncurryF = uncurry(curryF)
uncurryF(10, 10)

/*
EXERCISE 6:
Implement the higher-order function that composes two functions.
 */

def compose[A,B,C](f: B => C, g: A => B): A => C = x => f(g(x))
val composeF = compose((x: String) => x + " Hello", (b: Boolean) => b.toString)
composeF(true)
