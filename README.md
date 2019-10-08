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
