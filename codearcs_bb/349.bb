; ID: 349
; Author: Rob Farley
; Date: 2002-06-20 13:32:58
; Title: Colour Chart
; Description: Colour picker... Click the colour and copy the rgb values

; http://www.mentalillusion.co.uk
;
; Click on a colour and it will copy to the clipboard,
; Thanks For JimB For the clipboard Decl And functions
;
;
; There's a delay 50 in the main loop to free up processor time, this means you should be able to run
; other stuff at the same time as this without slowdown. Blitz hogs all the processor otherwise.



; Clipboard Text Read / Write
; ===========================
; Syntax Error & Ed from Mars
; userlib declarations - 'user32.decls'
; *********************************************
; .lib "user32.dll"
; OpenClipboard%(hwnd%):"OpenClipboard"
; CloseClipboard%():"CloseClipboard"
; ExamineClipboard%(format%):"IsClipboardFormatAvailable"
; EmptyClipboard%():"EmptyClipboard"
; GetClipboardData$(format%):"GetClipboardData"
; SetClipboardData%(format%,txt$):"SetClipboardData"
; *********************************************




AppTitle "Colour Picker"
Graphics 256,270,32,2

SetBuffer BackBuffer()


Dim colour(1536,3)
For n=0 To 255
	colour(n,1)=255
	colour(n,2)=n
	colour(n,3)=0
	colour(n+256,1)=255-n
	colour(n+256,2)=255
	colour(n+256,3)=n
	colour(n+512,1)=0
	colour(n+512,2)=255-n
	colour(n+512,3)=255
	colour(n+768,1)=n
	colour(n+768,2)=0
	colour(n+768,3)=255-n
	colour(n+1024,1)=colour(n,1)
	colour(n+1024,2)=colour(n,2)
	colour(n+1024,3)=colour(n,3)
	colour(n+1280,1)=colour(n+256,1)
	colour(n+1280,2)=colour(n+256,2)
	colour(n+1280,3)=colour(n+256,3)
Next


colourchart=CreateImage (256,256)
LockBuffer ImageBuffer(colourchart)

For y=0 To 255 Step 2
	For x=0 To 1023 Step 4
		yy#=Float(y)/255
		r=colour(x,1)*yy
		g=Colour(x,2)*yy
		b=Colour(x,3)*yy
		writergb(colourchart,x/4,y/2,r,g,b)
		r=255-(colour(x+512,1)*yy)
		g=255-(Colour(x+512,2)*yy)
		b=255-(Colour(x+512,3)*yy) 
		writergb(colourchart,x/4,255-(y/2),r,g,b)
	Next
Next

UnlockBuffer ImageBuffer(colourchart)


Repeat

	DrawBlock colourchart,0,0
	
	LockBuffer BackBuffer()
		argb=ReadPixelFast(MouseX(),MouseY(),BackBuffer())
		r = (ARGB Shr 16) And $ff 
		g = (ARGB Shr 8) And $ff 
		b = ARGB And $ff
		
		WritePixelFast (MouseX()-2,MouseY(),0,BackBuffer())
		WritePixelFast (MouseX()+2,MouseY(),0,BackBuffer())
		WritePixelFast (MouseX(),MouseY()-2,0,BackBuffer())
		WritePixelFast (MouseX(),MouseY()+2,0,BackBuffer())
		
		WritePixelFast (MouseX()-1,MouseY()-1,0,BackBuffer())
		WritePixelFast (MouseX()-1,MouseY()+1,0,BackBuffer())
		WritePixelFast (MouseX()+1,MouseY()-1,0,BackBuffer())
		WritePixelFast (MouseX()+1,MouseY()+1,0,BackBuffer())
	UnlockBuffer BackBuffer()
	
	Color r,g,b
	Rect 0,256,256,14
		
	Color 255-r,255-g,255-b
	Text 128,263,"R:"+r+" G:"+g+" B:"+b,True,True

	
	If MouseHit(1)=True Then WriteClipboardText "color "+r+","+g+","+b
	Delay 50
	Flip
Until KeyHit(1)

Function WriteClipboardText(txt$)
	Local cb_TEXT=1
	If txt$="" Then Return 
	If OpenClipboard(0)
		EmptyClipboard
		SetClipboardData cb_TEXT,txt$
		CloseClipboard
	EndIf
End Function

Function writergb(image_name,x,y,red,green,blue)
	argb=blue + (green Shl 8) + (red Shl 16); + ($ff000000)
	WritePixelFast x,y,argb,ImageBuffer(image_name)
End Function
