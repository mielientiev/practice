EXERCISE 2:

Implement the function tail for "removing" the first element of a List.
Notice the function takes constant time. What are different choices you could make in your implementation
if the List is Nil?

=======================
EXERCISE 3:

Generalize tail to the function drop, which removes the first n elements from a list.

=======================
EXERCISE 4: 

Implement dropWhile,10 which removes elements from the List prefix as long as they match a predicate.
Again, notice these functions take time proportional only to the number of elements being dropped
we do not need to make a copy of the entire List.
     

========================
EXERCISE 5:

Using the same idea, implement the function setHead for replacing the first element of a List with a different value.

========================
EXERCISE 6:

Not everything works out so nicely. Implement a function, init, which returns a List consisting of
all but the last element of a List. So, given List(1,2,3,4), init will return List(1,2,3)

========================
EXERCISE 9:

Compute the length of a list using foldRight.

========================
EXERCISE 10: 

foldRight is not tail-recursive and will StackOverflow for large lists.
Convince yourself that this is the case, then write another general list-recursion function,
foldLeft that is tail-recursive, using the techniques we discussed in the previous chapter.

========================
EXERCISE 11:

Write sum, product, and a function to compute the length of a list using foldLeft.

========================
EXERCISE 12:

Write a function that returns the reverse of a list (so given List(1,2,3) it returns List(3,2,1)).
See if you can write it using a fold.

========================
EXERCISE 13 (hard):

Can you write foldLeft in terms of foldRight? How about the other way around?

========================
EXERCISE 14:

Implement append in terms of either foldLeft or foldRight.

========================
EXERCISE 15 (hard):

Write a function that concatenates a list of lists into a single list.
Its runtime should be linear in the total length of all lists. Try to use functions we have already defined.

========================
EXERCISE 16-18:

Write a function map, that generalizes modifying each element in a list while maintaining the structure of the list

========================
EXERCISE 19:

Write a function filter that removes elements from a list unless they satisfy a given predicate.
Use it to remote all odd numbers from a List[Int]

========================
EXERCISE 20:

Write a function flatMap, that works like map except that the function given will return a list instead of a
single result, and that list should be inserted into the final resulting list.

========================
EXERCISE 21:

Can you use flatMap to implement filter?

========================
EXERCISE 22:

Write a function that accepts two lists and constructs a new list by adding corresponding elements.
For example, List(1,2,3) and List(4,5,6) becomes List(5,7,9)

========================
EXERCISE 23: 

Generalize the function you just wrote so that it's not specific to integers or addition.

========================
EXERCISE 24 (hard): 

As an example, implement hasSubsequence for checking whether a List contains another
List as a subsequence. For instance, List(1,2,3,4) would have List(1,2), List(2,3), and List(4) as subsequences,
among others.
You may have some difficulty finding a concise purely functional implementation that is also efficient.

========================
EXERCISE 25:

Write a function size that counts the number of nodes in a tree.

========================
EXERCISE 26:

Write a function maximum that returns the maximum element in a Tree[Int].
(Note: in Scala, you can use x.max(y) or x max y to compute the maximum of two integers x and y.)

========================
EXERCISE 27:

Write a function depth that returns the maximum path length from the root of a tree to any leaf.

========================
EXERCISE 28:

Write a function map, analogous to the method of the same

========================
EXERCISE 29:

Generalize size, maximum, depth, and map, writing a new fu
Reimplement them in terms of this more general function.

========================
