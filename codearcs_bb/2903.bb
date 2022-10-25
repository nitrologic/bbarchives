; ID: 2903
; Author: ThePict
; Date: 2011-11-18 14:05:04
; Title: HexTiles
; Description: My latest game

AppTitle("HexTiles - Enter RULES for Player One's name to see the rules.")
Graphics 800,400,16,2
SetBuffer BackBuffer()
Global g01=0
Global g02=0
Global g03=0
Global g04=0
Global g05=0
Global g06=0
Global g07=0
Global g08=0
Global g09=0
Global g10=0
Global g11=0
Global g12=0
Global g13=0
Global g14=0
Global g15=0
Global g16=0
Global g17=0
Global g18=0
; game data - 18 places either 0=empty, 1=Player1, 2=Player2
Global game$="000000000000000000"
Global phx01=40
Global phy01=20
Global phx02=80
Global phy02=20
Global phx03=120
Global phy03=20
Global phx04=160
Global phy04=20
Global phx05=20
Global phy05=50
Global phx06=100
Global phy06=50
Global phx07=180
Global phy07=50
Global phx08=40
Global phy08=80
Global phx09=80
Global phy09=80
Global phx10=120
Global phy10=80
Global phx11=160
Global phy11=80
Global phx12=20
Global phy12=110
Global phx13=100
Global phy13=110
Global phx14=180
Global phy14=110
Global phx15=40
Global phy15=140
Global phx16=80
Global phy16=140
Global phx17=120
Global phy17=140
Global phx18=160
Global phy18=140
Global ax=60
Global ay=50
Global bx=140
Global by=50
Global cx=60
Global cy=110
Global dx=140
Global dy=110
.BackToGame
Locate 0,0
Global p1$:p1$=Input$("Enter Player One's Name: ")
If Upper(p1$)="RULES" Then Rules()
If Upper(p1$)="RULES" Then Goto BackToGame
Global p2$:p2$=Input$("Enter Player Two's Name: ")

For n=1 To 18
Whosgo=n Mod 2
TakeTurn(Whosgo)
xx=CheckForWinner()
If xx<>0 Then Exit
Next
If xx=1 Then RuntimeError(p1$+" WON!")
If xx=2 Then RuntimeError(p2$+" WON!")
If xx=0 Then RuntimeError("This game was a draw")

Function Rules()
Drawboard()
Locate 44,200: Print "HexTiles ('til I think up a better name) is a Two-Player Pass'n'Play Game."
Locate 44,215:Print "Eighteen numbered place-holders for counter pieces are arranged in four"
Locate 44,230:Print "hexagons named A, B, C And D. Your aim is to make a horizontal line of four"
Locate 44,245:Print "of your coloured counters."
Locate 44,260:Print "But here's the twist...  each turn is a pair of moves. First you place your"
Locate 44,275:Print "counter on any empty numbered placeholder, then you rotate a hexagon by "
Locate 44,290:Print "60 degrees in either direction."
Locate 44,305:Print "Turns are entered with a 4 character string - first the number, then the "
Locate 44,320:Print "hexagon letter then either C or A for clockwise or anti."
Locate 44,335:Print "If this game is worth the bother I'll pretty it up with graphics and even"
Locate 44,350:Print "a mouse interface. For now though enjoy and tell me what you think."


Flip
FlushKeys
WaitKey()
Cls:Flip
End Function

Function CheckForWinner()
winner=0
If g01=g02 And g02=g03 And g03=g04 And g01=1 Then winner=1
If g01=g02 And g02=g03 And g03=g04 And g01=2 Then winner=2
If g08=g09 And g09=g10 And g10=g11 And g08=1 Then winner=1
If g08=g09 And g09=g10 And g10=g11 And g08=2 Then winner=2
If g15=g16 And g16=g17 And g17=g18 And g15=1 Then winner=1
If g15=g16 And g16=g17 And g17=g18 And g15=2 Then winner=2
Return winner
End Function

Function TakeTurn(who)
.invalid
DrawBoard()
Flip
If who=1 Then Color 0,0,255
If who=0 Then Color 255,0,0
If GetMove(who)=False Then Goto invalid
Drawboard()
Flip
End Function

Function Drawboard()
Cls
RenderOval(phx01,phy01,Mid$(game$,1,1),"01")
RenderOval(phx02,phy02,Mid$(game$,2,1),"02")
RenderOval(phx03,phy03,Mid$(game$,3,1),"03")
RenderOval(phx04,phy04,Mid$(game$,4,1),"04")
RenderOval(phx05,phy05,Mid$(game$,5,1),"05")
RenderOval(phx06,phy06,Mid$(game$,6,1),"06")
RenderOval(phx07,phy07,Mid$(game$,7,1),"07")
RenderOval(phx08,phy08,Mid$(game$,8,1),"08")
RenderOval(phx09,phy09,Mid$(game$,9,1),"09")
RenderOval(phx10,phy10,Mid$(game$,10,1),"10")
RenderOval(phx11,phy11,Mid$(game$,11,1),"11")
RenderOval(phx12,phy12,Mid$(game$,12,1),"12")
RenderOval(phx13,phy13,Mid$(game$,13,1),"13")
RenderOval(phx14,phy14,Mid$(game$,14,1),"14")
RenderOval(phx15,phy15,Mid$(game$,15,1),"15")
RenderOval(phx16,phy16,Mid$(game$,16,1),"16")
RenderOval(phx17,phy17,Mid$(game$,17,1),"17")
RenderOval(phx18,phy18,Mid$(game$,18,1),"18")
Color 128,128,128
Oval ax,ay,20,20,1
Oval bx,by,20,20,1
Oval cx,cy,20,20,1
Oval dx,dy,20,20,1
Color 255,255,0
Text ax+5,ay+2,"A",False,False
Text bx+5,by+2,"B",False,False
Text cx+5,cy+2,"C",False,False
Text dx+5,dy+2,"D",False,False
End Function

Function RenderOval(x,y,w$,t$)
solid=1
Color 255,255,255
If w$="0" Then solid=0
If w$="1" Then Color 0,0,255
If w$="2" Then Color 255,0,0
Oval(x,y,20,20,solid)
If w$="0" Then Text x+2,y+2,t$,0,0
End Function

Function GetMove(who)
If who=0 Then who=2
If who=1 Then Color 0,0,255 Else Color 255,0,0
Locate 0,0
If who=1 Then pp$=p1$ Else pp$=p2$
go$=Input$("Please enter "+pp$+"'s move (eg 06AC)")
If go$="" Then End
valid=False
pl=0
If Mid$(go$,1,2)="01" And Mid$(game$,1,1)="0" Then pl=1
If Mid$(go$,1,2)="02" And Mid$(game$,2,1)="0" Then pl=2
If Mid$(go$,1,2)="03" And Mid$(game$,3,1)="0" Then pl=3
If Mid$(go$,1,2)="04" And Mid$(game$,4,1)="0" Then pl=4
If Mid$(go$,1,2)="05" And Mid$(game$,5,1)="0" Then pl=5
If Mid$(go$,1,2)="06" And Mid$(game$,6,1)="0" Then pl=6
If Mid$(go$,1,2)="07" And Mid$(game$,7,1)="0" Then pl=7
If Mid$(go$,1,2)="08" And Mid$(game$,8,1)="0" Then pl=8
If Mid$(go$,1,2)="09" And Mid$(game$,9,1)="0" Then pl=9
If Mid$(go$,1,2)="10" And Mid$(game$,10,1)="0" Then pl=10
If Mid$(go$,1,2)="11" And Mid$(game$,11,1)="0" Then pl=11
If Mid$(go$,1,2)="12" And Mid$(game$,12,1)="0" Then pl=12
If Mid$(go$,1,2)="13" And Mid$(game$,13,1)="0" Then pl=13
If Mid$(go$,1,2)="14" And Mid$(game$,14,1)="0" Then pl=14
If Mid$(go$,1,2)="15" And Mid$(game$,15,1)="0" Then pl=15
If Mid$(go$,1,2)="16" And Mid$(game$,16,1)="0" Then pl=16
If Mid$(go$,1,2)="17" And Mid$(game$,17,1)="0" Then pl=17
If Mid$(go$,1,2)="18" And Mid$(game$,18,1)="0" Then pl=18
If pl=0 Then Goto nonono
rot$=Upper(Mid$(go$,3,2))
comp$=" AC AA BC BA CC CA DC DA "
If Instr(comp$," "+rot$+" ")=0 Then Goto nonono
valid=True
If pl=1 Then g01=who
If pl=2 Then g02=who
If pl=3 Then g03=who
If pl=4 Then g04=who
If pl=5 Then g05=who
If pl=6 Then g06=who
If pl=7 Then g07=who
If pl=8 Then g08=who
If pl=9 Then g09=who
If pl=10 Then g10=who
If pl=11 Then g11=who
If pl=12 Then g12=who
If pl=13 Then g13=who
If pl=14 Then g14=who
If pl=15 Then g15=who
If pl=16 Then g16=who
If pl=17 Then g17=who
If pl=18 Then g18=who
Select rot$
Case "AC"
	valid=True
	t1=g01:t2=g02:t3=g06:t4=g09:t5=g08:t6=g05
	g01=t6:g02=t1:g06=t2:g09=t3:g08=t4:g05=t5
Case "AA"
	valid=True
	t1=g01:t2=g02:t3=g06:t4=g09:t5=g08:t6=g05
	g01=t2:g02=t3:g06=t4:g09=t5:g08=t6:g05=t1
Case "BC"
	valid=True
	t1=g03:t2=g04:t3=g07:t4=g11:t5=g10:t6=g06
	g03=t6:g04=t1:g07=t2:g11=t3:g10=t4:g06=t5
Case "BA"
	valid=True
	t1=g03:t2=g04:t3=g07:t4=g11:t5=g10:t6=g06
	g03=t2:g04=t3:g07=t4:g11=t5:g10=t6:g06=t1
Case "CC"
	valid=True
	t1=g08:t2=g09:t3=g13:t4=g16:t5=g15:t6=g12
	g08=t6:g09=t1:g13=t2:g16=t3:g15=t4:g12=t5
Case "CA"
	valid=True
	t1=g08:t2=g09:t3=g13:t4=g16:t5=g15:t6=g12
	g08=t2:g09=t3:g13=t4:g16=t5:g15=t6:g12=t1
Case "DC"
	valid=True
	t1=g10:t2=g11:t3=g14:t4=g18:t5=g17:t6=g13
	g10=t6:g11=t1:g14=t2:g18=t3:g17=t4:g13=t5
Case "DA"
	valid=True
	t1=g10:t2=g11:t3=g14:t4=g18:t5=g17:t6=g13
	g10=t2:g11=t3:g14=t4:g18=t5:g17=t6:g13=t1
End Select
.nonono
If valid=False Then Print "INVALID ENTRY - TRY AGAIN":Delay 2000
game$=Str$(g01)+Str$(g02)+Str$(g03)+Str$(g04)+Str$(g05)+Str$(g06)+Str$(g07)+Str$(g08)+Str$(g09)+Str$(g10)+Str$(g11)+Str$(g12)+Str$(g13)+Str$(g14)+Str$(g15)+Str$(g16)+Str$(g17)+Str$(g18)
Return valid
End Function
