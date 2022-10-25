; ID: 499
; Author: Neuro
; Date: 2002-11-20 15:25:51
; Title: Tower of Hanoi
; Description: Quick easy method to solve the Tower of Hanoi Puzzle

; Tower of Hanoi
; by Neuro 11-20-02
; -----------------
; Legend has it that in a temple in India, there stands 3 crystal pegs. 
; On the first peg, there are 64 gold discs, each biggest at the bottom and smallest
; at the top. The monks in the temple must move all the discs from the first peg to
; the third. But since each discs are so fragile, that only one can be moved at a time
; and a larger disc can not be placed on top of a smaller disc.
; It is said, that when monks completely move each disc to the third pegs, the temple 
; will collapse and thus be the end of the world.

; This was originally a C++ recursive assignment givin to me by my professor. 
; After messing with this for so long, I finnally got it working. So then I decided to
; try it out on Blitz. And it worked! 

; BTW, this is my first Blitz program.


Global counter=0

Function domove(n, a, c, b)   ;# of disc, first peg, last peg, middle peg
  If (n=1)
   Print a +"->"+c           ;Prints to screen after movement completed  
   counter=counter+1
  Else
   domove(n-1,a,b,c)         ;move n-1 from peg 1 -> peg 2
   domove(1,a,c,b)  		 ;move last disc(1) from peg 1 -> peg 3
   domove(n-1,b,c,a)         ;move n-1 disc from peg 2 -> peg 3
  EndIf
End Function


.Main
Print "Tower of Hanoi" : Print 
Print " 64 disc is not recommended!" : Print 
disc=Input(" Enter number of disc : ")
domove(disc,1,3,2)
Print "Total number of moves : "+ counter
WaitKey()
