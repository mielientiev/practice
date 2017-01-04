trait Option[+A] {
  def map[B](f: A => B): Option[B]
  def flatMap[B](f: A => Option[B]): Option[B]
  def getOrElse[B >: A](default: => B): B
  def orElse[B >: A](ob: => Option[B]): Option[B]
  def filter(f: A => Boolean): Option[A]
}

EXERCISE 1: 

We'll explore when you'd use each of these next. But first, as an exercise, implement all of the above functions on Option. 
As you implement each function, try to think about what it means and in what situations you'd use it. 

=======================

EXERCISE 2:

Implement the variance function (if the mean is m, variance is the mean of math.pow(x - m, 2), see definition) in terms of mean and flatMap.

=======================

EXERCISE 3:

bothMatch is an instance of a more general pattern. Write a generic function map2, that combines two Option
values using a binary function.
If either Option value is None, then the return value is too.


=======================

EXERCISE 5:

Write a function sequence, that combines a list of Options into one option containing a list of all the Some 
values in the original list. If the original list contains None even once, the result of the function should be None, 
otherwise the result should be Some with a list of all the values. Here is its signature:5


=======================

EXERCISE 6: 

Implement this function. It is straightforward to do using map and sequence, but try for a 
more efficient implementation that only looks at the list once. In fact, implement sequence in terms of traverse.


=======================
trait Either[+E, +A] {
  def map[B](f: A => B): Either[E, B]
  def flatMap[EE >: E, B](f: A => Either[EE, B]): Either[EE, B]
  def orElse[EE >: E,B >: A](b: => Either[EE, B]): Either[EE, B]
  def map2[EE >: E, B, C](b: Either[EE, B])(f: (A, B) => C):
    Either[EE, C]
}

EXERCISE 7: 

Implement versions of map, flatMap, orElse, and map2 on Either that operate on the Right value.

=======================

EXERCISE 8:

Implement sequence and traverse for Either.

=======================