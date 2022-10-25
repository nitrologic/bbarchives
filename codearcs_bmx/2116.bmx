; ID: 2116
; Author: xlsior
; Date: 2007-09-30 19:46:26
; Title: Blur a pixmap
; Description: Blurs a pixmap by a given percentage

' Blur -- Horizontal And Vertical Blur routines
' By Marc van den Dikkenberg / xlsior
'
' Original Release    11/23/2003
' BlitzMax Converison 09/30/2007
' 
' Usage: HorizontalBlur(perct#)
'        Verticalblur(perct#)
'        perct# is the level of the effect, And expects a value between 50 And 99.
'    
' The color value of a pixel is smeared onto its neighbours - the higher the
' value, the more it gets smeared.
'
' To get a bi-directional blur effect, simply call the horizontal And Vertical
' blur functions directly after each other, before performing a 'flip' operation.
'       
' Tidbit: These two functions were created entirely by accident. I was working
' on my Monochrome() Function when I accidentally forgot to declare a set of
' placeholder variables, And color information accidentally got passed on To
' the Next pixel. Oh, well - maybe someone can use them.

Graphics 640,480,32

pimg:TPixmap=LoadPixmap("j:\monkey4.jpg")
dimg:TPixmap=CreatePixmap(640,480,PF_RGB888)

Repeat
	DrawPixmap(pimg:TPixmap,0,0)
	For t=50 To 98 Step 2
	   HorizontalBlur(pimg:TPixmap,dimg:TPixmap,t)
	   VerticalBlur(dimg:TPixmap,dimg:TPixmap,t)
		DrawPixmap dimg,0,0
	   Flip
	   If KeyDown(key_escape) Then End
	Next
	For t=98 To 50 Step -2
	   HorizontalBlur(pimg:TPixmap,dimg:TPixmap,t)
	   VerticalBlur(dimg:TPixmap,dimg:TPixmap,t)
		DrawPixmap dimg,0,0
	   Flip
	   If KeyDown(key_escape) Then End
	Next
Forever

Function HorizontalBlur(PixIn:TPixmap, PixOut:TPixmap,perct#) 
   desr=0
	desg=0
	desb=0
   If perct#<50 Then perct#=50
   If perct#>99 Then perct#=99
   For y=0 To pixin.height-1
      For x=0 To pixin.width-1
         temp1=ReadPixel(PixIn:TPixmap,x,y) 
         orgb=(temp1 & $FF) 
         orgg=(temp1 & $FF00) Shr 8 
         orgr=(temp1 & $FF0000) Shr 16 
         desr=orgr*(1-(perct#/100))+desr*(perct#/100) 
         desg=orgg*(1-(perct#/100))+desg*(perct#/100) 
         desb=orgb*(1-(perct#/100))+desb*(perct#/100) 
         WritePixel PixOut:TPixmap,x,y,desb+(desg Shl 8)+(desr Shl 16) 
      Next 
   Next 
'   UnlockBuffer 
End Function 

Function VerticalBlur(PixIn:TPixmap, PixOut:TPixmap,perct#)
   desr=0
	desg=0
	desb=0
   If perct#<50 Then perct#=50
   If perct#>99 Then perct#=99
   For x=0 To pixin.width-1
      For y=0 To pixin.height-1
         temp1=ReadPixel(PixIn:TPixmap,x,y) 
         orgb=(temp1 & $FF) 
         orgg=(temp1 & $FF00) Shr 8 
         orgr=(temp1 & $FF0000) Shr 16 
         desr=orgr*(1-(perct#/100))+desr*(perct#/100) 
         desg=orgg*(1-(perct#/100))+desg*(perct#/100) 
         desb=orgb*(1-(perct#/100))+desb*(perct#/100) 
         WritePixel PixOut:TPixmap,x,y,desb+(desg Shl 8)+(desr Shl 16) 
      Next 
   Next 
End Function
