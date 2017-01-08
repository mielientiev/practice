EXERCISE 1:
 
Write a function to generate a random positive integer. Note: you can use x.abs to take the absolute value of an Int, x. 
Make sure to handle the corner case Int.MinValue, which doesn't have a positive counterpart.


==========

EXERCISE 2:

Write a function to generate a Double between 0 and 1, not including 1.
Note: you can use Int.MaxValue to obtain the maximum positive integer value and you can use x.toDouble to convert an Int, x, to a Double.


==========

EXERCISE 3: 

Write functions to generate an (Int, Double) pair, a (Double, Int) pair, and a (Double, Double, Double)
3-tuple. You should be able to reuse the functions you've already written.


==========

EXERCISE 4:

Write a function to generate a list of random integers.


==========

EXERCISE 5:

Use map to generate an Int between 0 and n, inclusive


==========

EXERCISE 6:

Use map to reimplement RNG.double in a more elegant way.


==========

EXERCISE 7: 

Unfortunately, map is not powerful enough to implement intDouble and doubleInt from before.
What we need is a new combinator map2, that can combine two RNG actions into one using a binary rather than unary function.
Write its implementation and then use it to reimplement the intDouble and doubleInt functions.


==========

EXERCISE 8 (hard):

If we can combine two RNG transitions, we should be able to combine a whole list of them.
Implement sequence, for combining a List of transitions into a single transition.
Use it to reimplement the ints function you wrote before. For the latter, you can use the standard library function
List.fill(n)(x) to make a list with x repeated n times.



==========

EXERCISE 9:

Implement flatMap, then use it to reimplement positiveInt.


==========

EXERCISE 10:

Reimplement map and map2 in terms of flatMap.


==========

EXERCISE 11:

Generalize the functions unit, map, map2, flatMap, and sequence.
Add them as methods on the State case class where possible. Otherwise you should put them in a State companion object.


==========