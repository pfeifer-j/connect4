//Tests for Connect4

//Win on level 1
  4inarow> level 1
  4inarow> move 4
  4inarow> move 1
  4inarow> print
  . . . . . . .
  . . . . . . .
  . . . . . . .
  . . . O . . .
  . . . O . . .
  X . . X . . .
  4inarow> move 2
  4inarow> print
  . . . . . . .
  . . . . . . .
  . . . O . . .
  . . . O . . .
  . . . O . . .
  X X . X . . .
  4inarow> move 3
  Congratulations! You won.
  4inarow> witness
  (1, 1), (1, 2), (1, 3), (1, 4)
  4inarow> quit

  Process finished with exit code 0


//Lose on level 1 with short commands
  4inarow> m 1
  4inarow> m 1
  4inarow> m 1
  4inarow> m 1
  4inarow> m 1
  Sorry! Machine wins.
  4inarow> p
  X . . . . . .
  X . . . . . .
  O . . O . . .
  X . . O . . .
  X . . O . . .
  X . . O . . .
  4inarow> w
  (1, 4), (2, 4), (3, 4), (4, 4)
  4inarow> quit

  Process finished with exit code 0

//Using new and switch
  4inarow> m 4
  4inarow> p
  . . . . . . .
  . . . . . . .
  . . . . . . .
  . . . . . . .
  . . . O . . .
  . . . X . . .
  4inarow> new
  4inarow> p
  . . . . . . .
  . . . . . . .
  . . . . . . .
  . . . . . . .
  . . . . . . .
  . . . . . . .
  4inarow> switch
  4inarow> p
  . . . . . . .
  . . . . . . .
  . . . . . . .
  . . . . . . .
  . . . . . . .
  . . . O . . .
  4inarow> quit

  Process finished with exit code 0

//Help Menu
  4inarow> help
  new: 			 start a new game.
  level i: 	 set level to i ∈ {1,2,3,4,5}.
  switch: 	 start new game with switched opener.
  move c: 	 put a stone in row c ∈ {1,2,...,n}. n = "Number of rows".
  witness: 	 print the group of stones which won the game.
  print: 		 print the current board.
  help: 		 print the help menu.
  quit: 		 quit the game.
  4inarow> quit

  Process finished with exit code 0

//Long won game
4inarow> m 1
4inarow> m 1
4inarow> m 1
4inarow> m 4
4inarow> m 2
4inarow> p
. . . . . . .
. . . . . . .
O . . . . . .
X . . X . . .
X . . O . . .
X X O O O . .
4inarow> m 6
4inarow> m 3
4inarow> m 5
4inarow> p
. . . . . . .
. . . . . . .
O . O O . . .
X . X X . . .
X . O O X . .
X X O O O X .
4inarow> m 6
4inarow> m 5
4inarow> m 1
4inarow> p
. . . . . . .
X . O O . . .
O . O O X . .
X . X X O . .
X . O O X X .
X X O O O X .
4inarow> m 1
4inarow> m 5
4inarow> m 3
4inarow> p
X . X O O . .
X . O O X . .
O . O O X . .
X . X X O O .
X . O O X X .
X X O O O X .
4inarow> m 6
4inarow> m 6
4inarow> m 7
4inarow> m 7
4inarow> p
X . X O O X .
X . O O X O O
O . O O X X X
X . X X O O O
X . O O X X X
X X O O O X O
4inarow> m 7
4inarow> m 2
Congratulations! You won.
4inarow> w
(3, 1), (3, 2), (3, 3), (3, 4)
4inarow> quit

Process finished with exit code 0


//Long losing game which is continued using the
//switch-command and a level-change from default to 1
//in this game all group-directions all calculated.
  4inarow> m 1
  4inarow> m 5
  4inarow> m 2
  4inarow> m 4
  4inarow> m 3
  4inarow> p
  . . . . . . .
  . . . . . . .
  . . . X . . .
  . . O O . . .
  . X O O O . .
  X X X O X . .
  4inarow> m 2
  4inarow> p
  . . . . . . .
  . . . . . . .
  . O . X . . .
  . X O O . . .
  . X O O O . .
  X X X O X . .
  4inarow> m 3
  4inarow> p
  . . . . . . .
  . . . O . . .
  . O X X . . .
  . X O O . . .
  . X O O O . .
  X X X O X . .
  4inarow> m 5
  4inarow> m 5
  4inarow> p
  . . . . . . .
  . . O O X . .
  . O X X O . .
  . X O O X . .
  . X O O O . .
  X X X O X . .
  4inarow> m 6
  Sorry! Machine wins.
  4inarow> switch
  4inarow> level 1
  4inarow> m 4
  4inarow> p
  . . . . . . .
  . . . . . . .
  . . . . . . .
  . . . . . . .
  . . . X . . .
  . . O O . . .
  4inarow> m 2
  4inarow> p
  . . . . . . .
  . . . . . . .
  . . . . . . .
  . . . . . . .
  . . . X . . .
  . X O O O . .
  4inarow> m 6
  4inarow> p
  . . . . . . .
  . . . . . . .
  . . . . . . .
  . . . . . . .
  . . O X . . .
  . X O O O X .
  4inarow> m 6
  4inarow> p
  . . . . . . .
  . . . . . . .
  . . . . . . .
  . . O . . . .
  . . O X . X .
  . X O O O X .
  4inarow> m 3
  4inarow> p
  . . . . . . .
  . . . . . . .
  . . X . . . .
  . . O O . . .
  . . O X . X .
  . X O O O X .
  4inarow> m 4
  4inarow> p
  . . . . . . .
  . . . . . . .
  . . X X . . .
  . . O O . . .
  . . O X O X .
  . X O O O X .
  4inarow> m 5
  4inarow> p
  . . . . . . .
  . . . . . . .
  . . X X O . .
  . . O O X . .
  . . O X O X .
  . X O O O X .
  4inarow> m 2
  4inarow> p
  . . . . . . .
  . . . . . . .
  . . X X O . .
  . . O O X O .
  . X O X O X .
  . X O O O X .
  4inarow> m 2
  4inarow> p
  . . . . . . .
  . . . O . . .
  . . X X O . .
  . X O O X O .
  . X O X O X .
  . X O O O X .
  4inarow> m 2
  Congratulations! You won.
  4inarow> w
  (1, 2), (2, 2), (3, 2), (4, 2)
  4inarow> quit

  Process finished with exit code 0


//Drop stone into a full column
4inarow> m 4
4inarow> m 4
4inarow> p
. . . . . . .
. . . . . . .
. . . O . . .
. . . X . . .
. . . O . . .
. . . X . . .
4inarow> m 4
4inarow> p
. . . . . . .
. . . X . . .
. . . O . . .
. . . X . . .
. . . O . . .
. . O X . . .
4inarow> m 4
4inarow> m 4
Error! This row is full.
4inarow> quit

Process finished with exit code 0