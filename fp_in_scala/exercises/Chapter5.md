EXERCISE 1:
 
Write a function to convert a Stream to a List, which will force its evaluation and let us look at it in the REPL. 
You can convert to the regular List type in the standard library. You can place this and other functions that accept a Stream inside the Stream trait.

==========

EXERCISE 2:

Write a function take for returning the first n elements of a Stream.

==========

EXERCISE 3:
 
Write the function takeWhile for returning all starting elements of a Stream that match the given predicate.

==========

EXERCISE 4: 

Implement forAll, which checks that all elements in the Stream match a given predicate. 
Your implementation should terminate the traversal as soon as it encounters a non-matching value.

==========

EXERCISE 5:
 
Use foldRight to implement takeWhile. 
This will construct a stream incrementally, and only if the values in the result are demanded by some other expression.


==========

EXERCISE 6: 

Implement map, filter, append, and flatMap using foldRight.


==========

EXERCISE 7: 

Generalize ones slightly to the function constant which returns an infinite Stream of a given value.

==========

EXERCISE 8: 

Write a function that generates an infinite stream of integers, starting from n, then n + 1, n + 2, etc.


==========

EXERCISE 9: 

Write a function fibs that generates the infinite stream of Fibonacci numbers: 0, 1, 1, 2, 3, 5, 8, and so on.


==========

EXERCISE 10: 

We can write a more general stream building function. 
It takes an initial state, and a function for producing both the next state and the next value in the generated stream.
It is usually called unfold



==========

EXERCISE 11:

Write fibs, from, constant, and ones in terms of unfold

==========

EXERCISE 12:

Use unfold to implement map, take, takeWhile, zip (as in chapter 3), and zipAll. 
The zipAll function should continue the traversal as long as either stream has more elements — it uses Option 
to indicate whether each stream has been exhausted.


==========

EXERCISE 13 (hard): 

implement startsWith using functions you've written.
It should check if one Stream is a prefix of another.
For instance, Stream(1,2,3) starsWith Stream(1,2) would be true.

==========

EXERCISE 14: 

implement tails using unfold. For a given Stream, tails returns the Stream of suffixes of the input
sequence, starting with the original Stream. So, given Stream(1,2,3), it would return Stream(Stream(1,2,3), Stream(2,3), Stream(3), Stream.empty).

==========

EXERCISE 15 (hard, optional): 

Generalize tails to the function scanRight, which is like a foldRight that returns a stream of the intermediate results. For example:

scala> Stream(1,2,3).scanRight(0)(_ + _).toList
res0: List[Int] = List(6,5,3,0)

==========