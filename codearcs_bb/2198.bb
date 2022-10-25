; ID: 2198
; Author: Nebula
; Date: 2008-01-20 12:43:30
; Title: Barkanoid
; Description: Color sorting ; color stacking

; (Speed programming challenge (2 hours)
;
; By Nebula / Crom Design (www.cromdesign.nl)
;
; barkanoid


Graphics 640,480,16,2
SetBuffer BackBuffer()
ClsColor 30,30,30 : Cls
;
Type game
	Field btop,bleft
	;player
	Field pleft,ptop,pwidth,pheight
	Field pblock
	Field pblockcnt
	Field mapbottom
	End Type
g.game = New game
resetlevel
;
Global myfont = LoadFont("verdana",16,True)
SetFont myfont
Global timer = CreateTimer(60)
;
Dim blocks(40,40)
;
setupblocks()
;
While KeyDown(1) = False
	WaitTimer(timer)
	Cls
	moveship
	drawbeam(0)
	DrawBlocks
	drawship
	grabblock
	releaseblock
	drawplayingscreen()
	If KeyHit(59) Then resetlevel : setupblocks
	Color 255,255,255:	Text 52,15,"F1 to ",1
	Color 255,255,255:	Text 52,32,"reset level ",1
	Flip
Wend
End

Function resetlevel()
	g.game = First game
	g\btop = -256
	g\bLeft= 96
	g\pleft = GraphicsWidth()/2
	g\pheight = 64
	g\ptop = GraphicsHeight() - g\pheight
	g\pwidth = 48
	g\pblock = 0
	g\pblockcnt = 0
	g\mapbottom = 40
End Function

Function drawplayingscreen()
Color 0,0,0
Rect 0,0,96,GraphicsHeight()
Color 50,70,40
Rect 2,2,94,GraphicsHeight()
End Function

Function dropmap()
g.game = First game

For y=38 To 1 Step -1
For x=0 To 40
	If ono = False
		If blocks(x,y) > 0 Then
			bb = y
			ono = True
		End If
	End If
Next:Next
q = g\mapbottom-bb
If q > 10 Then g\mapbottom = (bb+10)
End Function

Function checkcombo(x1,y1,num,rv)
	g.game = First game
	;x = g\pleft+24
	;x=x/48-2
	;
	For y=y1 To 0 Step -1
		If noway = False
		If blocks(x1,y) = num Then
			cnt=cnt+1
		;blocks(x1,y) = 0	
		Else
		noway = True
		End If
		End If
	Next
	;
	cnt=cnt-1
	;DebugLog cnt
	;
	If cnt > rv Then
		For y=y1 To y1-cnt Step - 1
			blocks(x1,y) = 0
		Next
		dropmap
	End If
	;
	;RuntimeError cnt
End Function

Function releaseblock()
	g.game = First game
	cb = g\pblock
	If cb = 0 Then Return
	x = g\pleft+24
	x=x/48-2
	
	Color 255,0,0
	Text 0,0,x

	If MouseHit(2) = True Then

		For y1=40 To 0 Step -1
			If blocks(x,y1) <> 0 And bb = 0 Then
				bb = y1
			End If
		Next
		bb=bb+1
		For y1=bb To bb+g\pblockcnt-1
			blocks(x,y1) = g\pblock			
		Next
		checkcombo(x,bb+g\pblockcnt-1,g\pblock,g\pblockcnt)
		g\pblockcnt = 0
		g\pblock = 0
		
	EndIf
	
End Function

Function grabblock()

g.game = First game

;cb = g\pblock
x = g\pleft+24

x=x/48-2

;Color 255,0,0
;Text 0,0,x

If MouseHit(1) = True And g\pblockcnt < 5 Then

lb = 0 : ly = 40
blockselect = False
For y=40 To 0 Step -1

	Stepdone = False
	bt = blocks(x,y)
	
	If g\pblock = 0 Then	
		If bt > 0
			g\pblock = bt
		EndIf			
	End If
	
	;	
	If (lb = 0 And bt <> 0) And blockselect = False Then
		If g\pblock = bt 
			lb = bt
			ly = y; : DebugLog " set to : " + ly + " from : " + y
			blockselect = True			
			Stepdone = True
			Else
			Return
		End If
	End If
	
	;
	If Stepdone = False And blockselect = True Then		
		If lb = bt Then				
			If (y+1) = ly Then
				ly = y
				cnt = cnt + 1				
			End If
		EndIf
	EndIf
Next

;
If lb <> 0 Then ;RuntimeError "found blok"
	g\pblock = lb
	g\pblockcnt = cnt + 1 + g\pblockcnt
	;remove blocks
	For y=ly To 40
	blocks(x,y) = 0
	Next
End If
;

End If


End Function

Function drawbeam(num)
g.game = First game

x = g\pleft
y = 0
w = g\pwidth
h = GraphicsHeight()

Color 40,40,200

Rect x,y,w,h,True

End Function
Function setupblocks()
	For x=0 To 10
	For y=0 To 30
		blocks(x,y) = Rand(1,4)
	Next:Next
End Function
Function moveship()

	g.game = First game
	
	If RectsOverlap(MouseX(),MouseY(),1,1,96+g\pwidth/2,0,GraphicsWidth()-(96+g\pwidth),GraphicsHeight()) Then
		g\pleft = MouseX() - g\pwidth/2
		Else
		If MouseX() < 96 Then
		g\pLeft = 96
		End If		
	End If

If g\pleft > 12*48 Then g\pleft = 12*48
	
End Function
Function drawship()
	g.game = First game
	x = g\pleft
	y = g\ptop
	w = g\pwidth
	h = g\pheight
	Color 0,0,0
	Rect x,y,w,h,False
	Color 160,170,170
	x=x+1:w=w-2 : y=y+1:h=h-2
	Rect x,y,w,h,True
	;
	
	If g\pblockcnt > 0 Then	
		For y1=y To y-((g\pblockcnt-1)*16) Step -16
			draw1block(x,y1,g\pblock)
		Next
	End If
	;
End Function

Function DrawBlocks()

g.game = First game
y2 = (40*16)-(g\mapbottom*16)
For x=0 To 40
For y=0 To 40
	draw1block((x*48)+g\bleft,((y*16)+g\btop)+y2,blocks(x,y))
Next:Next
End Function
Function Draw1Block(x,y,num)
If num = 0 Then Return
Color 0,0,0
Rect x,y,48,16,False
Select num
	Case 1:Color 255,0,0
	Case 2:Color 0,255,0
	Case 3:Color 0,0,255
	Case 4:Color 255,0,255
End Select
Rect (x+1),(y+1),48-2,16-2,True
End Function
