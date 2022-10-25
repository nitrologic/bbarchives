; ID: 837
; Author: xlsior
; Date: 2003-11-23 07:30:46
; Title: Blur Function
; Description: Blurs an image either horizonally, vertically, or both

;
; Blur -- Horizontal and Vertical Blur routines
; 11/23/2003, by Marc van den Dikkenberg / xlsior
; 
; Usage: HorizontalBlur(perct#)
;        Verticalblur(perct#)
;        perct# is the level of the effect, and expects a value between 50 and 99.
;    
; The color value of a pixel is smeared onto its neighbours - the higher the
; value, the more it gets smeared.
;
; To get a bi-directional blur effect, simply call the horizontal and Vertical
; blur functions directly after each other, before performing a 'flip' operation.
;       
; Tidbit: These two functions were created entirely by accident. I was working
; on my Monochrome() function when I accidentally forgot to declare a set of
; placeholder variables, and color information accidentally got passed on to
; the next pixel. Oh, well - maybe someone can use them.
;

Graphics 640,480,16,2
SetBuffer BackBuffer()
img%=LoadImage("monkey4.jpg")

For t=50 To 100 Step 5
   DrawImage img%,0,0
   HorizontalBlur(t)
   VerticalBlur(t)
   Flip
   If KeyDown(1) Then End
Next

WaitKey()
End


Function HorizontalBlur(perct#) 
   desr=0:desg=0:desb=0
   If perct#<50 Then perct#=50
   If perct#>99 Then perct#=99
   SetBuffer BackBuffer() 
   LockBuffer 
   For y=0 To 479 
      For x=0 To 639 
         temp1=ReadPixel(x,y) 
         orgb=(temp1 And $FF) 
         orgg=(temp1 And $FF00) Shr 8 
         orgr=(temp1 And $FF0000) Shr 16 
         desr=orgr*(1-(perct#/100))+desr*(perct#/100) 
         desg=orgg*(1-(perct#/100))+desg*(perct#/100) 
         desb=orgb*(1-(perct#/100))+desb*(perct#/100) 
         WritePixel x,y,desb+(desg Shl 8)+(desr Shl 16) 
      Next 
   Next 
   UnlockBuffer 
End Function 

Function VerticalBlur(perct#) 
   desr=0:desg=0:desb=0
   If perct#<50 Then perct#=50
   If perct#>99 Then perct#=99
   SetBuffer BackBuffer() 
   LockBuffer 
   For x=0 To 639 
      For y=0 To 479 
         temp1=ReadPixel(x,y) 
         orgb=(temp1 And $FF) 
         orgg=(temp1 And $FF00) Shr 8 
         orgr=(temp1 And $FF0000) Shr 16 
         desr=orgr*(1-(perct#/100))+desr*(perct#/100) 
         desg=orgg*(1-(perct#/100))+desg*(perct#/100) 
         desb=orgb*(1-(perct#/100))+desb*(perct#/100) 
         WritePixel x,y,desb+(desg Shl 8)+(desr Shl 16) 
      Next 
   Next 
   UnlockBuffer 
End Function
