; ID: 3261
; Author: dw817
; Date: 2016-03-10 22:32:52
; Title: Traffic
; Description: Visually simulate crowding conditions on a road of traffic.

'     ________________________________________
'    //                                     //
'   // "T R A F F I C"                     //
'  // Version: "Chivalry On The Road"     //
' // Written by David W (dw817) 03/11/16 //
'//_____________________________________//
'
' What's up ??
' Set it so there is more traffic and when it reaches the
' end, it does not stop but instead vanish - allowing more
' traffic to cross.
'
' Developed a clever method of handling whether or not I
' am READING or WRITING to the map by using a single
' variable called NEG.
'
' Handling 4-car pileup by backing up the car that's been
' waiting the longest. Now to my knowledge, this is a
' PERFECT solution.

' When a pileup does occur and you are holding down
' SPACEBAR for TURBO, it will be disabled so you can see how
' everyone helps to clear out the traffic jam.

' Once the jam is clear, you can enter TURBO mode again to
' try and find another problem.

Strict
SeedRnd MilliSecs() ' ensure random results each time

SetMaskColor 0,0,0
Global img_tile:TImage=LoadAnimImage(LoadBank("http::www.writerscafe.org/uploads/rte/0a3c15c2943abe552357bc7d46840e8c.png"),8,8,0,5,maskedimage)
' ^ load in 5 image tiles. Quicker than DefData.

Global scrn[31,23] ' full screen size of 31x23 tiles at 32x32 pixels, 8x8 pixels zoomed 4x
Global scrncount[31,23] ' prevent traffic jams by taking out a car if stationary for 5 turns

Local i,j,k,m,c,x,y,s,r,jam,neg=1

SetGraphicsDriver GLMax2DDriver(),0
Graphics 1024,768
SetScale 4,4

Repeat ' {* MAIN *}

r=(r+1)Mod 4 ' everyone gets equal traffic time
If jam<5 ' only add cars if there is no traffic jam
  If r=0
    setscrn fnr(16)*2,22,1*neg ' add a car IF the place is empty
  ElseIf r=1
    setscrn 30,fnr(11)*2,2*neg
  ElseIf r=2
    setscrn 0,fnr(11)*2+1,3*neg
  Else ' no need to compare for 3
    setscrn fnr(15)*2+1,0,4*neg
  EndIf
EndIf

update ' show our work
If jam Then jam:-1
For i=0 Until 23 ' scan entire playfield
  For j=0 Until 31
    c=getscrn(j,i) ' read in each tile of the playfield
    x=0 ; y=0 ' if changed means we are moving a vehicle
    If c=1*neg Then y=-1 ' Move arrow in designated direction IF area is free.
    If c=2*neg Then x=-1 ' Negative is necessary so the tile is not reselected
    If c=3*neg Then x=1 ' in this one pass or it won't work correctly.
    If c=4*neg Then y=1
    s=scrncount[j,i] ' how long has this vehicle been idle ?
    If x<>0 Or y<>0 Or s>4 ' car can move or is stalled
      If s>4 And getscrn(j-x,i-y)=0 ' qualifies as jammed
        If s>4 Then jam=8 ' 8 cycles will pass where you cannot use TURBO
        scrn[j,i]=0 ' erase position of car
        setscrn j-x,i-y,-c ' back up vehicle one step if possible
        setscrncount j-x,i-y,Rand(0,5) ' critical ! Car waits a specific time before backing up again
        scrncount[j,i]=Rand(0,5)
      ElseIf getscrn(j+x,i+y)=0 ' path is free
        scrn[j,i]=0 ' erase position of car
        scrn[j+x,i+y]=-c ' move car forward one step
        scrncount[j,i]=0 ' reset any stalled status
      EndIf
    ElseIf c ' car is here but it can't move, there is an obstacle in the way
      scrncount[j,i]:+1 ' increase stalled time
    Else
      scrncount[j,i]=0 ' always reset stalled time on empty space
    EndIf
    If i=0 And scrn[j,i]=1*neg Then scrn[j,i]=0 ' clear out any cars that have successfully
    If j=0 And scrn[j,i]=2*neg Then scrn[j,i]=0 ' traveled the full length of the screen across
    If j=30 And scrn[j,i]=3*neg Then scrn[j,i]=0
    If i=22 And scrn[j,i]=4*neg Then scrn[j,i]=0
  Next
Next

neg=-neg ' BAM ! This resets all positions so they can be read again

If KeyDown(32)=0 Or jam Then Delay 150 ' hold down SPACEBAR for TURBO, disabled if there is a traffic jam
If KeyDown(27) Then End ' exit on ESCAPE key

Forever ' {* END OF MAIN *}

' >> UPDATE SCREEN WITH TILES
Function update()
Local i,j,n,x,y
  Cls
  For i=0 Until 23
    For j=0 Until 31
      x=j*32+16 ; y=i*32
      DrawImage img_tile,x,y,0
      n=scrn[j,i]
      If n ' with no comparitive number it means <>0 (zero)
        SetColor 255,255,255 ' always reset color when plotting any tiles
        DrawImage img_tile,x,y,Abs(n) ' draw arrow images, NOTICE it is the absolute value !
      EndIf
    Next
  Next
  Flip 0 ' show our work
EndFunction

' >> RETRIEVE SCREEN TILE EVEN IF OFF EDGES
Function getscrn(x,y)
Local r=-1
  If x>=0 And y>=0 And x<=30 And y<=22 Then r=scrn[x,y]
  Return r
EndFunction

' >> PLOT A VEHICLE IF SPACE IS CLEAR
Function setscrn(x,y,z)
  If getscrn(x,y)=0 Then scrn[x,y]=z
EndFunction

' >> MARK A TILE WITH STALL TIME EVEN IF OFF EDGES
Function setscrncount(x,y,z)
  If x>=0 And y>=0 And x<=30 And y<=22 Then scrncount[x,y]=z
EndFunction

' >> RETURN COMMA DELIMITED TEXT FROM A$
Function yank$(a$ Var)
Local b=Instr(a$+",",","),c$=Left$(a$,b-1)
  a$=Mid$(a$,b+1) ' also remove it from input string
  Return c$
EndFunction

' >> QUICK RETRIEVE RANDOM 0 to A-1
Function fnr(a)
  Return Rand(a)-1
EndFunction
