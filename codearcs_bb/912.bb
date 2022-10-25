; ID: 912
; Author: DarkNature
; Date: 2004-02-04 16:13:05
; Title: Conway's Game of Life
; Description: A simple Cellular Automaton in Blitz

AppTitle "Life! by DarkNature"

Graphics 800,600,16,2
SetBuffer BackBuffer()

Global sWidth=GraphicsWidth()
Global sHeight=GraphicsHeight()
Global bWidth=65
Global bHeight=55
Global tSize=10
Global xoff=110
Global yoff=20
Global started=False
Global population=0
Global generation=0
Global font=LoadFont("tahoma",12,0,0,0)

Dim b0(bwidth,bheight)
Dim b1(bwidth,bheight)

SeedRnd(MilliSecs())

Function randomize()
	
	setup()
	
	prob=Rnd(60,80)
	For y=0 To bheight-1
		For x=0 To bwidth-1
			live=Rnd(100)
			If live>prob
				live=1
				population=population+1
			Else live=0
			End If
			b0(x,y)=live
		Next
	Next

End Function

Function setup()

	population=0
	generation=0

	For y=0 To bheight-1
		For x=0 To bwidth-1
			live=Rnd(0,100)
			b0(x,y)=0
			b1(x,y)=0
		Next
	Next

End Function

Function switchem()

	For y=0 To bheight-1
		For x=0 To bwidth-1
			b0(x,y)=b1(x,y)
		Next
	Next

End Function

Function drawboard()
	
	Color 0,0,70
	Rect xoff+10,yoff+10,bwidth*tsize,bheight*tsize

	Color 50,50,50
	Rect xoff,yoff,bwidth*tsize,bheight*tsize

	population=0
	
	For y=0 To bheight-1
		For x=0 To bwidth-1
		
			Local alive=False
		
			If b0(x,y)=1
				alive=True
				population=population+1
			End If
			
			Color 150,150,150
			Rect xoff+x*tsize,yoff+y*tsize,tsize,tsize,alive
						
		Next
	Next
	
	SetFont(font)
	Color 255,255,0
	Text 15,30,"Population: "+population
	Text 15,40,"Generation: "+generation
	Text 15,65,"c:clear r:random"
	Text 15,75,"space bar:start"

End Function

Function live()

	For y=0 To bheight-1
		For x=0 To bwidth-1
			
			ncount=0
			For y1=y-1 To y+1
				For x1=x-1 To x+1
					If (x1>=0 And x1<=bwidth-1) And (y1>=0 And y<=bheight-1)
						If (Not(x1=x And y1=y))
							If b0(x1,y1)=1 ncount=ncount+1
						End If
					End If
				Next
			Next
			
			If ncount=3 And b0(x,y)=0 b1(x,y)=1
			If ncount=2 b1(x,y)=b0(x,y)
			If ncount<2 Or ncount>3 And b0(x,y)=1 b1(x,y)=0		
		Next
	Next
	
	generation=generation+1

End Function

Function trackmouse()

	mx=MouseX()
	my=MouseY()
	x=(mx-xoff)/tsize
	y=(my-yoff)/tsize
		
	If mx>=xoff And x<=bwidth-1 And my>=yoff And y<=bheight-1
	
		If (MouseHit(1) And started=False)
			b0(x,y)=Not b0(x,y)
			b1(x,y)=b0(x,y)
		End If
	
	End If
	
	If (mx>=xoff And x<=bwidth-1) And (my>=yoff And y<=bheight-1)
		Color 255,255,0
		Rect xoff+x*tsize,yoff+y*tsize,tsize,tsize,0
	End If
				
End Function

setup()
t=CreateTimer(30)
ClsColor 0,0,100
timenow=MilliSecs()
While Not KeyHit(1)

	Cls
	
	drawboard()
	
	If KeyHit(46)
		started=False
		setup()
	End If

	If KeyHit(19)
		started=False
		randomize()
	End If

	
	If KeyHit(57) started=Not started
		
	If started
		If MilliSecs()>timenow+150
			live()
			switchem()
			timenow=MilliSecs()
		End If
	End If
	
	trackmouse()
	
	Flip
	
	WaitTimer(t)

Wend
End
