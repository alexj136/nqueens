# nqueens
An implementation of a variant of the N-Queens problem
- Given an integer `n`, generate an `n` by `n` chess board with `n` queens on
  it, such that no queen can take another queen in a single move; and
- no three queens lie on the same straight line - for example, the following
  would be considered illegal:

        ┌───┬───┬───┬───┬───┐
        │   │   │   │   │   │
        ├───┼───┼───┼───┼───┤
        │ Q │   │   │   │   │
        ├───┼───┼───┼───┼───┤
        │   │   │ Q │   │   │
        ├───┼───┼───┼───┼───┤
        │   │   │   │   │ Q │
        ├───┼───┼───┼───┼───┤
        │   │   │   │   │   │
        └───┴───┴───┴───┴───┘

  since each Q lies on a straight line.

# Instructions
Run using Gradle, e.g. for `n == 8`:

    gradle run --args='8'

In case it's useful to know, my rusty laptop takes about 9 seconds to find all
solutions for `n == 14` and about 45 seconds for `n == 15`. If you have good
hardware you might get to `n == 18` or `19` if you're willing to go for a coffee
break while it runs.
