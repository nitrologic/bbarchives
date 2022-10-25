; ID: 3191
; Author: zoqfotpik
; Date: 2015-02-24 04:11:33
; Title: Apple IIe Text Console
; Description: Another Text Console

' Quick and Dirty Text Display Console.  For roguelikes or whatever else you might
' want.

Graphics 1280,960
Global text:String[10000] 	' This is the text string.  It's not a 2D array to make it
							' easier to display actual readable strings but is addressable
							' by x and y with function plotchar.
Global textimage:TImage = New TImage

' The Apple IIe font as an image.
' Grab it here:  
' https://www.dropbox.com/s/s4flyf1345qjbb8/applefont-wide.png?dl=0

textimage = LoadImage("applefont-wide.png")

Cls

' This is the order of characters as they appear in the font.  When drawing
' we use a text search to find where in the string the character is,
' then translate that into x and y coordinates so that we can clip
' the correct subimagerect out of the font bitmap for pasting.
Global fontstring$ = " !~q#$%&'()*+,-./0123456789:;<=>?@ABCDEFGHIJKLMNOPQRSTUVWXYZ[\]^_`abcdefghijklmnopqrstuvwxyz(|}~~"
'stringtotext("Test",1)
Global curkey$
Global cursorx=0
Global cursory=0

Function drawapplefontchar(a$,x:Int,y:Int)
	Local fontimagex:Int
	Local fontimagey:Int
	Local infontstring=fontstring.find(a$)
	If infontstring > -1 
		fontimagex = infontstring Mod 16
		fontimagey = infontstring / 16
	EndIf
	drawimagesubrect(textimage,x,y,fontimagex*16,64+fontimagey*32,16,32)
	
	End Function

While Not KeyDown(KEY_ESCAPE)
curkey = GetChar()
If curkey>0
	cursorx = cursorx + 1
	text[cursorx]=Chr(Int(curkey))
EndIf
Cls
curchar=0

For j = 0 To 30
For i = 0 To 80
curchar=curchar+1
dist=distance(i*16,j*32,MouseX(),MouseY())
SetColor(255-dist,255-dist/2,255)
drawapplefontchar text[curchar],i*16,j*32

Next
Next

For i = 0 To 10
plotchar(Chr(33+Rand(48)) ,Rand(80)-1,Rand(40)-1)
Next 
Flip
Wend

Function stringtotext(a$,num:Int)
	For Local i:Int = 0 To Len(a$)-1
	text[num+i]=Chr(a$[i])
	Next
End Function

Function plotchar(a$,x:Int,y:Int)
	text[y*80+x] = a
End Function

Function Distance#(x0#,y0#,x1#,y1#)
	Local dx# = x0-x1
	Local dy# = y0-y1
	Return Sqr(dx*dx + dy*dy)
End Function

Function DrawImageSubRect(Image:TImage, DrawX#, DrawY#, PartX#, PartY#, PartWidth#, PartHeight#, Frame# = 0)
' Not my code, someone else here did this a long time ago.  Draws a sub-rectangle of a given
' image to the screen.
	Local OldX:Int
	Local OldY:Int
	Local OldWidth:Int
	Local OldHeight:Int
	
	Local ViewportX:Int = DrawX
	Local ViewportY:Int = DrawY
	
	' Save current viewport settings
	GetViewport(OldX, OldY, OldWidth, OldHeight)
	
	' Calculate viewport coordinates based on image's handle	
	If Image.Handle_X Then
		Local PercentX:Float
		PercentX = Float(Image.Handle_X) / Float(Image.Width)
		ViewportX = DrawX - (PercentX * PartWidth)
	EndIf
	If Image.Handle_Y Then
		Local PercentY:Float
		PercentY = Float(Image.Handle_Y) / Float(Image.Height)
		ViewportY = DrawY - (PercentY * PartHeight)
	EndIf
	
	SetViewport(ViewportX, ViewportY, PartWidth, PartHeight)
	DrawImage(Image, DrawX-PartX, DrawY-PartY, Frame)
	
	' Restore old viewport settings
	SetViewport(OldX, OldY, OldWidth, OldHeight)
End Function
