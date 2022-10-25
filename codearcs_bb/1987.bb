; ID: 1987
; Author: Petron
; Date: 2007-04-15 20:32:14
; Title: Data
; Description: An example of data in a mouse avoider game, few levels included.

Graphics 800,600,0,1
rotatehalfclockwiseposition = 0
rotatehalfcounterclockwiseposition = 0

rotateupdate = 0 
 mousecollition = CreateImage(6,6)
SetBuffer ImageBuffer(mousecollition)
Oval 0,0,6,6,1

 RotateImagehalf =CreateImage(40,40,16)
 For t = 0 To 15
 SetBuffer ImageBuffer(RotateImagehalf,t)
Color 255,0,0
If t = 0 
Line 20,19,20,0
Line 19,19,19,0
EndIf
If t = 1 
Line 20,19,28,2
Line 20,20,28,3
EndIf 
If t = 2 
Line 20,19,34,7
Line 20,20,35,7
EndIf 
If t = 3 
Line 20,19,37,13
Line 20,20,37,14
EndIf 
If t = 4
Line 20,19,39,19
Line 20,20,39,20
EndIf 
If t = 5
Line 20,19,37,25
Line 20,20,37,26
EndIf 
If t = 6 
Line 20,19,33,33
Line 19,19,32,33
EndIf
If t = 7 
Line 20,19,27,36
Line 21,19,28,36
EndIf 
If t = 8 
Line 20,19,20,39
Line 19,19,19,39
EndIf 
If t = 9 
Line 20,19,12,36
Line 19,19,11,36
EndIf 
If t = 10 
Line 20,19,6,33
Line 21,19,7,33
EndIf 
If t = 11
Line 20,19,2,26
Line 20,20,2,27
EndIf 
If t = 12
Line 20,19,0,19
Line 20,20,0,20
EndIf 
If t = 13
Line 20,19,2,13
Line 20,18,2,12
EndIf
If t = 14
Line 20,19,6,7
Line 20,18,6,6
EndIf
If t = 15
Line 20,19,13,3
Line 20,18,13,2
EndIf 
 Next

;Graphics GraphicsWidth(),GraphicsHeight(),0,1
FlushMouse 
Const LeftKey=203
Const RightKey=205
Const UpKey=200
Const DownKey=208
Map_Length=49
Map_Height=49

Global play = 0

Dim Map$(Map_Length,Map_Height)

Global playgame = 0

Restore level0
For i=0 To Map_Length 
	Read Value$ 
	For x=0 To Map_Length
		Map(x,y)=Mid(Value$,x+1,1)
	Next

	y=y+1
Next
Global greyscale = 255
Global greyscalecolor = 0 
Global greyscalechange = 0 

Global aportalx = 0 
Global aportaly = 0 
Global bportalx = 0 
Global bportaly = 0 
Global cportalx = 0 
Global cportaly = 0 
Global dportalx = 0 
Global dportaly = 0 

Global alaseron = 0 

Global reset = 0 
Global movewall = 0 
Global wallmove = 0 
Global wallmoveon = 1 
Global wallfreeze = 0 
Global onmovewall = 0 
Global offseton = 0 
Global lives = 5 
Const screencenterw = 400
Const screencenterh = 300
Global moveup = 0 
Global offsetx = screencenterw - 250
Global offsety = screencenterh - 250
SetBuffer BackBuffer()
Global showwall = 0
While Not KeyHit(1)
;	If KeyHit(57)
		;x = 0
		;y = 0
		;Restore level1
		;For i=0 To Map_Length 
		;	Read Value$ 
		;	For x=0 To Map_Length
		;		Map(x,y)=Mid(Value$,x+1,1)
		;	Next

		;	y=y+1
		;Next
;	EndIf
If KeyHit(57)
reset = 1
FlushKeys
EndIf 

;background and hud
greyscalechange = greyscalechange + 1
If greyscalechange > 1
greyscalechange = 0 
EndIf 
If greyscalechange = 1
 
If greyscalecolor = 0 
greyscale = greyscale - 1
EndIf 
If greyscalecolor = 1
greyscale = greyscale + 1
EndIf 
EndIf 

If greyscale < 1
greyscalecolor = 1 
greyscale = 2
EndIf 
If greyscale > 100
greyscale = 99
greyscalecolor = 0
EndIf 

Color greyscale,greyscale,greyscale
Rect screencenterw - 250,screencenterh - 250,500,500,1
Color 255,255,255
Text 153,53,"Mouse Avoider Game - Lives: "+ lives +" Level: "+(play+1)	

;map
	For x=0 To Map_Length For y=0 To Map_Height
	 
		If Map(x,y)="0" ; blank
		;Color 0,0,0
		;Rect x*10+offsetx,y*10+offsety,10,10,1 
		If playgame = 1
			If Not onmovewall = 1	
				If (RectsOverlap(x*10+offsetx,y*10+offsety,10,10,MouseX()-3,MouseY()-3,6,6))
					offsetx = screencenterw - 250
					offsety = screencenterh - 250
					playgame = 0 
					alaseron = 0
				EndIf
			EndIf 	
		EndIf 
		
		EndIf                                

		If Map(x,y)="1" ; start
			Color 128,128,128
			Rect x*10+offsetx,y*10+offsety,10,10,1
			Color 0,255,0
			Oval x*10+offsetx,y*10+offsety,10,10,1 
			Color 0,0,0
			Oval x*10+offsetx,y*10+offsety,10,10,0
			If playgame = 0
				If (RectsOverlap(x*10+offsetx,y*10+offsety,10,10,MouseX()-3,MouseY()-3,6,6))
					If MouseHit(1)
						playgame =1
						FlushMouse
					EndIf
				EndIf
			EndIf
		EndIf

		If Map(x,y)="2" ;exit
			Color 128,128,128
			Rect x*10+offsetx,y*10+offsety,10,10,1
			Color 255,0,0
			Oval x*10+offsetx,y*10+offsety,10,10,1 
			Color 0,0,0
			Oval x*10+offsetx,y*10+offsety,10,10,0			If playgame = 1
				If (RectsOverlap(x*10+offsetx,y*10+offsety,10,10,MouseX()-3,MouseY()-3,6,6))
					reset = 1 
					FlushMouse
				EndIf
			EndIf
		EndIf

		If Map(x,y)="3" ;normal
			Color 128,128,128
			Rect x*10+offsetx,y*10+offsety,10,10,1
		 	If playgame = 1
		If onmovewall = 2 
				If (RectsOverlap(x*10+offsetx,y*10+offsety,10,10,MouseX()-3,MouseY()-3,6,6))
					onmovewall = 0  
					
				EndIf
		EndIf 		
			EndIf
		EndIf

		If Map(x,y)="4" ;changing
		
			If showwall < 50
				Color 0,0,0
				Rect x*10+offsetx,y*10+offsety,10,10,1
				If playgame = 1
					If (RectsOverlap(x*10+offsetx,y*10+offsety,10,10,MouseX()-3,MouseY()-3,6,6))
						offsetx = screencenterw - 250
						offsety = screencenterh - 250
						playgame = 0 
						alaseron = 0
					EndIf
				EndIf
			EndIf

			If showwall => 50
				Color 128,128,128
				Rect x*10+offsetx,y*10+offsety,10,10,1
			EndIf
		EndIf
		If Map(x,y)="5" ;changing different disapearences
		
			If showwall => 50
				Color 0,0,0
				Rect x*10+offsetx,y*10+offsety,10,10,1
				If playgame = 1
					If (RectsOverlap(x*10+offsetx,y*10+offsety,10,10,MouseX()-3,MouseY()-3,6,6))
						offsetx = screencenterw - 250
						offsety = screencenterh - 250
						playgame = 0 
						alaseron = 0
					EndIf
				EndIf
			EndIf

			If showwall < 50
				Color 128,128,128
				Rect x*10+offsetx,y*10+offsety,10,10,1
			EndIf
		EndIf
		If Map(x,y)="6";up arrow
			Color 128,128,128
			Rect x*10+offsetx,y*10+offsety,10,10,1 
			Color 0,0,0
			Line x*10+offsetx+4,y*10+offsety,x*10+offsetx+4,y*10+offsety+10
			Line x*10+offsetx+5,y*10+offsety,x*10+offsetx+5,y*10+offsety+10
			Line x*10+offsetx+4,y*10+offsety,x*10+offsetx,y*10+offsety+4
			Line x*10+offsetx+5,y*10+offsety,x*10+offsetx+10,y*10+offsety+5
			Line x*10+offsetx,y*10+offsety+4,x*10+offsetx+10,y*10+offsety+4
			Line x*10+offsetx+2,y*10+offsety+3,x*10+offsetx+7,y*10+offsety+3
			Line x*10+offsetx+3,y*10+offsety+2,x*10+offsetx+6,y*10+offsety+2
			Line x*10+offsetx+4,y*10+offsety+1,x*10+offsetx+5,y*10+offsety+1

			If playgame = 1
				If (RectsOverlap(x*10+offsetx,y*10+offsety,10,10,MouseX()-3,MouseY()-3,6,6))
					moveup = 1 
				EndIf
			EndIf

		EndIf 
		
			If Map(x,y)="7";moving horozontal
			Color 128,128,128
			Rect x*10+offsetx+wallmove-1,y*10+offsety,30,30,1 
			If playgame = 1
			If Not onmovewall = 1	
				If (RectsOverlap(x*10+offsetx,y*10+offsety,10,10,MouseX()-3,MouseY()-3,6,6))
					offsetx = screencenterw - 250
					offsety = screencenterh - 250
					playgame = 0 
					alaseron = 0
				EndIf
			EndIf 	
			

			
				If (RectsOverlap(x*10+offsetx+wallmove-1,y*10+offsety,30,30,MouseX()-3,MouseY()-3,6,6))
					onmovewall = 1  
				EndIf
				If onmovewall = 1 
				If Not (RectsOverlap(x*10+offsetx+wallmove-1,y*10+offsety,30,30,MouseX()-3,MouseY()-3,6,6))
					onmovewall = 2  
				EndIf
				EndIf 
			EndIf

		EndIf 	
			 If Map(x,y)="8";moving verticle
			Color 128,128,128
			Rect x*10+offsetx,y*10+offsety+wallmove-1,30,30,1 
			If playgame = 1
			If Not onmovewall = 1	
				If (RectsOverlap(x*10+offsetx,y*10+offsety,10,10,MouseX()-3,MouseY()-3,6,6))
					offsetx = screencenterw - 250
					offsety = screencenterh - 250
					playgame = 0 
					alaseron = 0
				EndIf
			EndIf 	
			

			
				If (RectsOverlap(x*10+offsetx,y*10+offsety+wallmove-1,30,30,MouseX()-3,MouseY()-3,6,6))
					onmovewall = 1  
				EndIf
				If onmovewall = 1 
				If Not (RectsOverlap(x*10+offsetx,y*10+offsety+wallmove-1,30,30,MouseX()-3,MouseY()-3,6,6))
					onmovewall = 2  
				EndIf
				EndIf 
			EndIf

		EndIf
		If Map(x,y)="9";rotate half clockwise
		Color 128,128,128
		Rect x*10+offsetx,y*10+offsety,40,40,1 
				DrawImage RotateImagehalf,x*10+offsetx,y*10+offsety,rotatehalfclockwiseposition
				If (ImagesCollide(RotateImagehalf,x*10+offsetx,y*10+offsety,rotatehalfclockwiseposition,mousecollition,MouseX()-3,MouseY()-3,0))
				offsetx = screencenterw - 250
				offsety = screencenterh - 250
				playgame = 0  
				alaseron = 0
				EndIf 
		EndIf 
		If Map(x,y)="A";rotate half counter clockwise
		Color 128,128,128
		Rect x*10+offsetx,y*10+offsety,40,40,1 
				DrawImage RotateImagehalf,x*10+offsetx,y*10+offsety,rotatehalfcounterclockwiseposition
				If (ImagesCollide(RotateImagehalf,x*10+offsetx,y*10+offsety,rotatehalfcounterclockwiseposition,mousecollition,MouseX()-3,MouseY()-3,0))
				offsetx = screencenterw - 250
				offsety = screencenterh - 250
				playgame = 0  
				alaseron = 0 
				EndIf 
		EndIf 
		
		If Map(x,y)="B";A Portal Link (USE ONLY ONE)
		aportalx = x*10+offsetx
		aportaly = y*10+offsety + 5  
			Color 128,128,128
			Rect x*10+offsetx,y*10+offsety,20,10,1
			Color 0,0,255
			Oval x*10+offsetx+10,y*10+offsety,10,10,1 
			Color 0,0,0
			Oval x*10+offsetx+10,y*10+offsety,10,10,0
			If playgame = 1
		If (RectsOverlap(x*10+offsetx+10,y*10+offsety,10,10,MouseX()-3,MouseY()-3,6,6))
			SetCursorPos(bportalx,bportaly) 
			;MouseX() = bportalx
			;MouseY() = bportaly  
		EndIf
EndIf 
		
		EndIf 
		If Map(x,y)="C";B Portal Link (USE ONLY ONE)
		bportalx = x*10+offsetx + 16
		bportaly = y*10+offsety + 5 
		Color 128,128,128
			Rect x*10+offsetx,y*10+offsety,20,10,1
			Color 0,0,255
			Oval x*10+offsetx,y*10+offsety,10,10,1 
			Color 0,0,0
			Oval x*10+offsetx,y*10+offsety,10,10,0
			If playgame = 1 
		If (RectsOverlap(x*10+offsetx,y*10+offsety,10,10,MouseX()-3,MouseY()-3,6,6))
			SetCursorPos(aportalx,aportaly)
			;MouseX() = aportalx
			;MouseY() = aportaly  
		EndIf
		EndIf 
		EndIf 
			If Map(x,y)="D";C Portal Link (USE ONLY ONE)
		cportalx = x*10+offsetx
		cportaly = y*10+offsety + 5  
			Color 128,128,128
			Rect x*10+offsetx,y*10+offsety,20,10,1
			Color 0,0,255
			Oval x*10+offsetx+10,y*10+offsety,10,10,1 
			Color 0,0,0
			Oval x*10+offsetx+10,y*10+offsety,10,10,0
			If playgame = 1 
		If (RectsOverlap(x*10+offsetx+10,y*10+offsety,10,10,MouseX()-3,MouseY()-3,6,6))
			SetCursorPos(dportalx,dportaly) 
			;MouseX() = bportalx
			;MouseY() = bportaly  
		EndIf
EndIf 
		
		EndIf 
		If Map(x,y)="E";D Portal Link (USE ONLY ONE)
		dportalx = x*10+offsetx + 16
		dportaly = y*10+offsety + 5 
		Color 128,128,128
			Rect x*10+offsetx,y*10+offsety,20,10,1
			Color 0,0,255
			Oval x*10+offsetx,y*10+offsety,10,10,1 
			Color 0,0,0
			Oval x*10+offsetx,y*10+offsety,10,10,0
			If playgame = 1 
		If (RectsOverlap(x*10+offsetx,y*10+offsety,10,10,MouseX()-3,MouseY()-3,6,6))
			SetCursorPos(cportalx,cportaly)
			;MouseX() = aportalx
			;MouseY() = aportaly  
		EndIf
		EndIf 
		EndIf 
		If Map(x,y)="F";laser a switch
			Color 128,128,128
			Rect x*10+offsetx,y*10+offsety,10,10,1
			Color 255,255,0
			Oval x*10+offsetx,y*10+offsety,10,10,1 
			Color 0,0,0
			Oval x*10+offsetx,y*10+offsety,10,10,0
			If playgame = 1 
			If (RectsOverlap(x*10+offsetx,y*10+offsety,10,10,MouseX()-3,MouseY()-3,6,6))
			alaseron = 1 
			EndIf 
			EndIf 
		EndIf 
		If Map(x,y)="G";laser a 
			Color 128,128,128
			Rect x*10+offsetx,y*10+offsety,10,50,1
			Color 255,255,0
			Oval x*10+offsetx,y*10+offsety,10,10,1 
			Color 0,0,0
			Oval x*10+offsetx,y*10+offsety,10,10,0
			If (RectsOverlap(x*10+offsetx,y*10+offsety,10,10,MouseX()-3,MouseY()-3,6,6))
			offsetx = screencenterw - 250
			offsety = screencenterh - 250
			playgame = 0 
			alaseron = 0
			EndIf 
			
			Color 255,255,0
			Oval x*10+offsetx,y*10+offsety+40,10,10,1 
			Color 0,0,0
			Oval x*10+offsetx,y*10+offsety+40,10,10,0
			If (RectsOverlap(x*10+offsetx,y*10+offsety+40,10,10,MouseX()-3,MouseY()-3,6,6))
			offsetx = screencenterw - 250
			offsety = screencenterh - 250
			playgame = 0 
			alaseron = 0
			EndIf 
			
			If alaseron = 0 
			Rect x*10+offsetx+4,y*10+offsety+10,2,30
			If (RectsOverlap(x*10+offsetx+4,y*10+offsety+10,2,30,MouseX()-3,MouseY()-3,6,6))
			offsetx = screencenterw - 250
			offsety = screencenterh - 250
			playgame = 0 
			alaseron = 0
			EndIf 
			EndIf 
		EndIf 
		If Map(x,y)="H";boost left
		Color 128,128,128
		Rect x*10+offsetx,y*10+offsety,10,10,1
		Color 0,0,0
		Line x*10+offsetx+4,y*10+offsety,x*10+offsetx+8,y*10+offsety+4
		Line x*10+offsetx+4,y*10+offsety+9,x*10+offsetx+8,y*10+offsety+5
		Line x*10+offsetx+3,y*10+offsety+0,x*10+offsetx+7,y*10+offsety+4
		Line x*10+offsetx+7,y*10+offsety+5,x*10+offsetx+3,y*10+offsety+9
		Line x*10+offsetx+2,y*10+offsety+0,x*10+offsetx+6,y*10+offsety+4
		Line x*10+offsetx+2,y*10+offsety+9,x*10+offsetx+6,y*10+offsety+5
		If playgame = 1 
		If (RectsOverlap(x*10+offsetx,y*10+offsety,10,10,MouseX()-3,MouseY()-3,6,6))
		SetCursorPos(MouseX()+ 3,MouseY())
		EndIf 	
		EndIf
		EndIf 
		
		If Map(x,y)="I";boost down
		Color 128,128,128
		Rect x*10+offsetx,y*10+offsety,10,10,1
		Color 0,0,0
		Line x*10+offsetx+0,y*10+offsety+2,x*10+offsetx+4,y*10+offsety+6
		Line x*10+offsetx+0,y*10+offsety+3,x*10+offsetx+4,y*10+offsety+7
		Line x*10+offsetx+0,y*10+offsety+4,x*10+offsetx+4,y*10+offsety+8
		Line x*10+offsetx+5,y*10+offsety+6,x*10+offsetx+9,y*10+offsety+2
		Line x*10+offsetx+5,y*10+offsety+7,x*10+offsetx+9,y*10+offsety+3
		Line x*10+offsetx+5,y*10+offsety+8,x*10+offsetx+9,y*10+offsety+4
		If playgame = 1 
		If (RectsOverlap(x*10+offsetx,y*10+offsety,10,10,MouseX()-3,MouseY()-3,6,6))
		SetCursorPos(MouseX(),MouseY()+3)
		EndIf 	
		EndIf
		EndIf 
		
		If Map(x,y)="J";boost left
		Color 128,128,128
		Rect x*10+offsetx,y*10+offsety,10,10,1
		Color 0,0,0
		Line x*10+offsetx+1,y*10+offsety+4,x*10+offsetx+5,y*10+offsety+0
		Line x*10+offsetx+2,y*10+offsety+4,x*10+offsetx+6,y*10+offsety+0
		Line x*10+offsetx+3,y*10+offsety+4,x*10+offsetx+7,y*10+offsety+0
		Line x*10+offsetx+1,y*10+offsety+5,x*10+offsetx+5,y*10+offsety+9
		Line x*10+offsetx+2,y*10+offsety+5,x*10+offsetx+6,y*10+offsety+9
		Line x*10+offsetx+3,y*10+offsety+5,x*10+offsetx+7,y*10+offsety+9
		If playgame = 1 
		If (RectsOverlap(x*10+offsetx,y*10+offsety,10,10,MouseX()-3,MouseY()-3,6,6))
		SetCursorPos(MouseX()-3,MouseY())
		EndIf 	
		EndIf
		EndIf 
		
		If Map(x,y)="K";boost up
		Color 128,128,128
		Rect x*10+offsetx,y*10+offsety,10,10,1
		Color 0,0,0
		Line x*10+offsetx+0,y*10+offsety+5,x*10+offsetx+4,y*10+offsety+1
		Line x*10+offsetx+0,y*10+offsety+6,x*10+offsetx+4,y*10+offsety+2
		Line x*10+offsetx+0,y*10+offsety+7,x*10+offsetx+4,y*10+offsety+3
		Line x*10+offsetx+5,y*10+offsety+1,x*10+offsetx+9,y*10+offsety+5
		Line x*10+offsetx+5,y*10+offsety+2,x*10+offsetx+9,y*10+offsety+6
		Line x*10+offsetx+5,y*10+offsety+3,x*10+offsetx+9,y*10+offsety+7
		If playgame = 1 
		If (RectsOverlap(x*10+offsetx,y*10+offsety,10,10,MouseX()-3,MouseY()-3,6,6))
		SetCursorPos(MouseX(),MouseY()-3)
		EndIf 	
		EndIf
		EndIf 
		
		If Map(x,y)="Z";absolutely NOTHING w00t
		EndIf 
	Next Next
rotateupdate = rotateupdate + 1
If rotateupdate => 35
 rotatehalfclockwiseposition = rotatehalfclockwiseposition + 1 
 rotatehalfcounterclockwiseposition = rotatehalfcounterclockwiseposition - 1 

rotateupdate = 0 
EndIf  
If rotatehalfclockwiseposition > 15 Then rotatehalfclockwiseposition = 0	
If rotatehalfcounterclockwiseposition < 0 Then rotatehalfcounterclockwiseposition = 15	

If wallmoveon = 1 
	If movewall = 1 
		wallmove = wallmove + 1 
	EndIf 
		If movewall = 2 
			wallmove = wallmove - 1 
		EndIf 
		
		If movewall = 3 
			wallfreeze = wallfreeze + 1 

		If wallmove = 1
			If wallfreeze > 50
				movewall = 1
				wallfreeze = 0 
			EndIf
		EndIf 
		If wallmove = 51
			If wallfreeze > 50
				movewall = 2
				wallfreeze = 0 
			EndIf
		EndIf 
	EndIf 
EndIf 


wallmoveon = wallmoveon + 1 


If wallmoveon => 3
wallmoveon = 1 
EndIf 


If wallmove => 52
movewall = 3 
wallmove = 51
EndIf 


If wallmove =< 0
movewall = 3
wallmove = 1
EndIf


If reset = 1 
	offsetx = screencenterw - 250
	offsety = screencenterh - 250
	playgame = 0 
	play = play + 1 
	x = 0
	y = 0
	Select play 
	Case 1 
	Restore level1
	Case 2 
	End
	End Select
	For i=0 To Map_Length 
		Read Value$ 
		For x=0 To Map_Length
			Map(x,y)=Mid(Value$,x+1,1)
		Next
	y=y+1
	Next
reset = 0 
EndIf 

If playgame = 1
	;If KeyDown(RightKey) Then offsetx = offsetx + 2
	;If KeyDown(LeftKey) Then offsetx = offsetx - 2
	;If KeyDown(DownKey) Then offsety = offsety + 2
	;If KeyDown(UpKey) Then offsety = offsety - 2
	If offseton = 0 
	If moveup = 1 
	offsety = offsety - 2
	moveup = 0 
	EndIf 

	offsety = offsety + 1
	EndIf 
	offseton = offseton + 1 
	If offseton > 2
	offseton = 0
	EndIf 
	EndIf 
	Color 255,255,255
	Oval MouseX()-3,MouseY()-3,6,6
	Color 0,0,0
	Oval MouseX()-4,MouseY()-4,8,8,0

	ConfineCursor(150,50,500,500)
	Color 255,255,255
	Rect screencenterw - 250,screencenterh - 250,500,500,0
	Color 0,0,0
	Rect 0,0,800,50 ; top
	Rect 0,0,150,600 ;left
	Rect 0,550,800,600 ; bottom
	Rect 650,0,800,600 ;right
	showwall = showwall + 1
	 
	If showwall > 100 Then showwall = 0
	Flip
	Cls
Wend

End





Function ConfineCursor(x%,y%,w%,h%)
	Local r%=(CreateBank(16))
	PokeInt(r,0,x%):PokeInt(r,4,y%):PokeInt(r,8,x%+w%):PokeInt(r,12,y%+h%)
	api_ClipCursor%(r)
	FreeBank(r)
	Return(True)
End Function

.level0
Data "00000000000000000000000000000000000000000000000000";1
Data "00000000000000000000000000000000000000000000000000";2
Data "00000000000000000000000000000000000000000000000000";3
Data "00000000000000000000000000000000000000000000000000";4
Data "00000000000000000000000000000000000000000000000000";5
Data "00000000000000000000000000000000000000000000000000";6
Data "00000000000000000000000000000000000000000000000000";7
Data "00000000000000000000000000000000000000000000000000";8
Data "00000000000000000000000000000000000000000000000000";9
Data "00000000000000000000000000000000000000000000000000";10
Data "00000000000000000000000000000000000000000000000000";11
Data "00000000000000000000000000000000000000000000000000";12
Data "00000000000000000000000000000000000000000000000000";13
Data "00000000000000000000000000000000000000000000000000";14
Data "00000000000000000000000000000000000000000000000000";15
Data "00000000000000000000000000000000000000000000000000";16
Data "00000000000000000000000000000000000000000000000000";17
Data "00000000000000000000000000000000000000000000000000";18
Data "00000000000000000000000000000000000000000000000000";19
Data "00000000000000000000000000000000000000000000000000";20
Data "00000000000000000000000000000000000000000000000000";21
Data "00000000000000000000000333000000000000000000000000";22
Data "0000000000000000G000003333300000000000000000000000";23
Data "3333333000033333Z333333333333333333333333333333333";24
Data "313F3DZ0000EZ333Z333333363333333333333333333333323";25
Data "3333333000033333Z333333333333333333333333333333333";26
Data "0000000000000000Z000003333300000000000000000000000";27
Data "00000000000000000000000333000000000000000000000000";28
Data "00000000000000000000000000000000000000000000000000";29
Data "00000000000000000000000000000000000000000000000000";30
Data "00000000000000000000000000000000000000000000000000";31
Data "00000000000000000000000000000000000000000000000000";32
Data "00000000000000000000000000000000000000000000000000";33
Data "00000000000000000000000000000000000000000000000000";34
Data "00000000000000000000000000000000000000000000000000";35
Data "00000000000000000000000000000000000000000000000000";36
Data "00000000000000000000000000000000000000000000000000";37
Data "00000000000000000000000000000000000000000000000000";38
Data "00000000000000000000000000000000000000000000000000";39
Data "00000000000000000000000000000000000000000000000000";40
Data "00000000000000000000000000000000000000000000000000";41
Data "00000000000000000000000000000000000000000000000000";42
Data "00000000000000000000000000000000000000000000000000";43
Data "00000000000000000000000000000000000000000000000000";44
Data "00000000000000000000000000000000000000000000000000";45
Data "00000000000000000000000000000000000000000000000000";46
Data "00000000000000000000000000000000000000000000000000";47
Data "00000000000000000000000000000000000000000000000000";48
Data "00000000000000000000000000000000000000000000000000";49
Data "00000000000000000000000000000000000000000000000000";50
.level1
Data "00000000000000000000000000000000000000000000000000";1
Data "00000000000000000000000000000000000000000000000000";2
Data "00000000000000000000000000000000000000000000000000";3
Data "00000000000000000000000000000000000000000000000000";4
Data "00000000000000000000000000000000000000000000000000";5
Data "00000000000000000000000000000000000000000000000000";6
Data "00000000000000000000000000000000000000000000000000";7
Data "00000000000000000000000000000000000000000000000000";8
Data "00000000000000000000000000000000000000000000000000";9
Data "00000000000000000000000000000000000000000000000000";10
Data "00000000000000000000000000000000000000000000000000";11
Data "00000000000000000000000000000000000000000000000000";12
Data "00000000000000000000000000000000000000000000000000";13
Data "00000000000000000000000000000000000000000000000000";14
Data "00000000000000000000000000000000000000000000000000";15
Data "00000000000000000000000000000000000000000000000000";16
Data "00000000000000000000000000000000000000000000000000";17
Data "00000000000000000000000000000000000000000300000000";18
Data "00000000000000000300000030000000000000003330000000";19
Data "00000000000000003330000333000000000000033333000000";20
Data "00000000000000033333003333300000000000333333300000";21
Data "00000000000000333333333333330000000003333333330000";22
Data "00030000000003333333333333333000000033333033333000";23
Data "33333000000033333033333333333300000333330003333333";24
Data "31333300000333330003333366333330003333300000333323";25
Data "33333330003333300000333333333333033333000000033333";26
Data "00033333033333000000033333333333333330000000003000";27
Data "00003333333330000000003333333333333300000000000000";28
Data "00000333333300000000000333330033333000000000000000";29
Data "00000033333000000000000033300003330000000000000000";30
Data "00000003330000000000000003000000300000000000000000";31
Data "00000000300000000000000000000000000000000000000000";32
Data "00000000000000000000000000000000000000000000000000";33
Data "00000000000000000000000000000000000000000000000000";34
Data "00000000000000000000000000000000000000000000000000";35
Data "00000000000000000000000000000000000000000000000000";36
Data "00000000000000000000000000000000000000000000000000";37
Data "00000000000000000000000000000000000000000000000000";38
Data "00000000000000000000000000000000000000000000000000";39
Data "00000000000000000000000000000000000000000000000000";40
Data "00000000000000000000000000000000000000000000000000";41
Data "00000000000000000000000000000000000000000000000000";42
Data "00000000000000000000000000000000000000000000000000";43
Data "00000000000000000000000000000000000000000000000000";44
Data "00000000000000000000000000000000000000000000000000";45
Data "00000000000000000000000000000000000000000000000000";46
Data "00000000000000000000000000000000000000000000000000";47
Data "00000000000000000000000000000000000000000000000000";48
Data "00000000000000000000000000000000000000000000000000";49
Data "00000000000000000000000000000000000000000000000000";50


;blank map for use
;Data "00000000000000000000000000000000000000000000000000";1
;Data "00000000000000000000000000000000000000000000000000";2
;Data "00000000000000000000000000000000000000000000000000";3
;Data "00000000000000000000000000000000000000000000000000";4
;Data "00000000000000000000000000000000000000000000000000";5
;Data "00000000000000000000000000000000000000000000000000";6
;Data "00000000000000000000000000000000000000000000000000";7
;Data "00000000000000000000000000000000000000000000000000";8
;Data "00000000000000000000000000000000000000000000000000";9
;Data "00000000000000000000000000000000000000000000000000";10
;Data "00000000000000000000000000000000000000000000000000";11
;Data "00000000000000000000000000000000000000000000000000";12
;Data "00000000000000000000000000000000000000000000000000";13
;Data "00000000000000000000000000000000000000000000000000";14
;Data "00000000000000000000000000000000000000000000000000";15
;Data "00000000000000000000000000000000000000000000000000";16
;Data "00000000000000000000000000000000000000000000000000";17
;Data "00000000000000000000000000000000000000000000000000";18
;Data "00000000000000000000000000000000000000000000000000";19
;Data "00000000000000000000000000000000000000000000000000";20
;Data "00000000000000000000000000000000000000000000000000";21
;Data "00000000000000000000000000000000000000000000000000";22
;Data "00000000000000000000000000000000000000000000000000";23
;Data "00000000000000000000000000000000000000000000000000";24
;Data "00000000000000000000000000000000000000000000000000";25
;Data "00000000000000000000000000000000000000000000000000";26
;Data "00000000000000000000000000000000000000000000000000";27
;Data "00000000000000000000000000000000000000000000000000";28
;Data "00000000000000000000000000000000000000000000000000";29
;Data "00000000000000000000000000000000000000000000000000";30
;Data "00000000000000000000000000000000000000000000000000";31
;Data "00000000000000000000000000000000000000000000000000";32
;Data "00000000000000000000000000000000000000000000000000";33
;Data "00000000000000000000000000000000000000000000000000";34
;Data "00000000000000000000000000000000000000000000000000";35
;Data "00000000000000000000000000000000000000000000000000";36
;Data "00000000000000000000000000000000000000000000000000";37
;Data "00000000000000000000000000000000000000000000000000";38
;Data "00000000000000000000000000000000000000000000000000";39
;Data "00000000000000000000000000000000000000000000000000";40
;Data "00000000000000000000000000000000000000000000000000";41
;Data "00000000000000000000000000000000000000000000000000";42
;Data "00000000000000000000000000000000000000000000000000";43
;Data "00000000000000000000000000000000000000000000000000";44
;Data "00000000000000000000000000000000000000000000000000";45
;Data "00000000000000000000000000000000000000000000000000";46
;Data "00000000000000000000000000000000000000000000000000";47
;Data "00000000000000000000000000000000000000000000000000";48
;Data "00000000000000000000000000000000000000000000000000";49
;Data "00000000000000000000000000000000000000000000000000";50
