; ID: 3251
; Author: dw817
; Date: 2016-02-03 10:58:47
; Title: The Chess Queen Puzzle
; Description: Solve how to place 8-queens on a chessboard where no queen can be captured by another.

'     __________________________________
'    //                               //
'   // The Chess Queen Puzzle        //
'  // Version, "Blitz Solution"     //
' // Written by David W - 02/03/16 //
'//_______________________________//

' Attempt to place =8= Queens on a chessboard
' that do not intersect their capture points.
' Challenge accepted and met !

' My answer to this puzzle ? A very quick one
' indeed !

Strict
SeedRnd MilliSecs()

Local queen[8],i,j,r,r2,ok,c

For i=0 To 7 ' We're stepping vertically only
  r=Rand(0,7) ; r2=r ' choose horizontal place for queen
  Repeat
    r=(r+1)Mod 8 ' move to right, if hit edge, start back at far left
    ok=1 ' flag okay to go
    For j=0 To i-1
      If queen[j]=r Or Abs(i-j)=Abs(queen[j]-r) Then ok=0 ' tricky ! if queen is lined up, mark not okay
    Next
    If ok Then queen[i]=r ; Exit ' Nice ! found a place, mark it and go to next row
  Until r2=r ' scan all sides before we abort
  If r2=r Then i=0 ' so close, do it over
Next

' We've solved the board ! Now draw it.

SetGraphicsDriver GLMax2DDriver(),0 ' zero forces front buffer
Graphics 664,664 ' perfect size for chessboard
For i=0 To 7
  For j=0 To 7
    c=64+(j+i)Mod 2*32 ' neat ! changes color per square
    SetColor c,c,c ' board is simple pattern of gray
    DrawRect j*83,i*83,81,81 ' 81 instead of 83 for black outline to each square
    If j=queen[i] Then
      SetColor 0,0,0
      DrawOval j*83+6,i*83+6,68,68 ' draw black outline for queen first
      SetColor 255,255,255
      DrawOval j*83+10,i*83+10,60,60 ' queens are simple white circles
    EndIf
  Next
Next
glflush ' update screen
WaitKey ' hit a key and exit
