package chapter3

object TreeADT {
  def main(args: Array[String]): Unit = {

    assert(Tree.size(Branch(Branch(Leaf(1), Leaf(2)), Branch(Leaf(1), Leaf(3)))) == 7)
    assert(Tree.max(Branch(Branch(Leaf(1), Branch(Leaf(5), Leaf(7))), Branch(Leaf(1), Leaf(3)))) == 7)
    assert(Tree.depth(Branch(Branch(Leaf(1), Branch(Leaf(5), Leaf(7))), Branch(Leaf(1), Leaf(3)))) == 3)
    assert(Tree.map(Branch(Branch(Leaf(1), Branch(Leaf(5), Leaf(7))), Branch(Leaf(1), Leaf(3))))(_.toString) ==
      Branch(Branch(Leaf("1"), Branch(Leaf("5"), Leaf("7"))), Branch(Leaf("1"), Leaf("3"))))


    assert(Tree.size(Branch(Branch(Leaf(1), Leaf(2)), Branch(Leaf(1), Leaf(3)))) == Tree.sizeFold(Branch(Branch(Leaf(1), Leaf(2)), Branch(Leaf(1), Leaf(3)))))
    assert(Tree.max(Branch(Branch(Leaf(1), Leaf(2)), Branch(Leaf(1), Leaf(3)))) == Tree.maxFold(Branch(Branch(Leaf(1), Leaf(2)), Branch(Leaf(1), Leaf(3)))))
    assert(Tree.depth(Branch(Branch(Leaf(1), Leaf(2)), Branch(Leaf(1), Leaf(3)))) == Tree.depthFold(Branch(Branch(Leaf(1), Leaf(2)), Branch(Leaf(1), Leaf(3)))))
    assert(Tree.map(Branch(Branch(Leaf(1), Branch(Leaf(5), Leaf(7))), Branch(Leaf(1), Leaf(3))))(_.toString) ==
      Tree.mapFold(Branch(Branch(Leaf(1), Branch(Leaf(5), Leaf(7))), Branch(Leaf(1), Leaf(3))))(_.toString))
  }

  sealed trait Tree[+A]

  case class Leaf[A](value: A) extends Tree[A]

  case class Branch[A](left: Tree[A], right: Tree[A]) extends Tree[A]

  object Tree {

    /**
      * EXERCISE 25:
      * Write a function size that counts the number of nodes in a tree.
      */
    def size[A](tree: Tree[A]): Int = tree match {
      case Leaf(_) => 1
      case Branch(left, right) => 1 + size(left) + size(right)
    }

    /**
      * EXERCISE 26:
      * Write a function maximum that returns the maximum element in a Tree[Int].
      * (Note: in Scala, you can use x.max(y) or x max y to compute the maximum of two integers x and y.)
      */
    def max(tree: Tree[Int]): Int = tree match {
      case Leaf(v) => v
      case Branch(left, right) => math.max(max(left), max(right))
    }

    /**
      * EXERCISE 27:
      * Write a function depth that returns the maximum path length from the root of a tree to any leaf.
      */

    def depth[A](tree: Tree[A]): Int = tree match {
      case Leaf(_) => 0
      case Branch(left, right) => 1 + math.max(depth(left), depth(right))
    }

    /**
      * EXERCISE 28:
      * Write a function map, analogous to the method of the same
      */

    def map[A, B](tree: Tree[A])(f: A => B): Tree[B] = {
      tree match {
        case Leaf(v) => Leaf(f(v))
        case Branch(left, right) => Branch(map(left)(f), map(right)(f))
      }
    }

    /**
      * EXERCISE 29:
      * Generalize size, maximum, depth, and map, writing a new function fold that abstracts over their similarities.
      * Reimplement them in terms of this more general function.
      */

    def fold[A, B](tree: Tree[A])(l: A => B)(b: (B, B) => B): B = {
      tree match {
        case Leaf(v) => l(v)
        case Branch(left, right) => b(fold(left)(l)(b), fold(right)(l)(b))
      }
    }

    def maxFold(tree: Tree[Int]): Int = fold(tree)(identity)(math.max)

    def sizeFold[A](tree: Tree[A]): Int = fold(tree)(_ => 1)((l, r) => 1 + l + r)

    def depthFold[A](tree: Tree[A]): Int = fold(tree)(_ => 0)(1 + math.max(_, _))

    def mapFold[A, B](tree: Tree[A])(f: A => B): Tree[B] = fold(tree)(x => Leaf(f(x)): Tree[B])(Branch(_, _))
  }

}
