; ID: 795
; Author: jfk EO-11110
; Date: 2003-09-20 12:03:54
; Title: Mask Maker
; Description: Bitmap and Texture Masking Utility

Graphics 800,600,16,2 
SetBuffer BackBuffer() 
a$="ast1.bmp" 
a2$="ast1b.bmp" 
TFormFilter 0 

Global img=LoadImage(a$) 
Global imgbk=CopyImage(img) 
DrawBlock img,0,0 
rgb=ReadPixel(i,j) 
r=(rgb Shr 16) And 255 
g=(rgb Shr 8 ) And 255 
b=rgb And 255 
t=100 


ClsColor 200,100,0 
While KeyDown(1)=0 
If MouseDown(1) 
  If dodo=0 
   mx=MouseX() 
   dodo=1 
  EndIf 
  my=MouseY() 
  If my>=100 And my<=366 
   myp=min(255,my-100) 
   If mx>=0 And mx<10 
    r=myp 
   EndIf 
   If mx>=10 And mx<20 
    g=myp 
   EndIf 
   If mx>=20 And mx<30 
    b=myp 
   EndIf 
   If mx>=30 And mx<40 
    t=myp 
   EndIf 
  EndIf 
  mask(r,g,b,t) 
Else 
  dodo=0 
EndIf 
Cls 
DrawImage img,100,0 
sliders(r,g,b,t) 
Flip 
Wend 

SaveBuffer(ImageBuffer(img),a2$) 


End 

;----------------------------------------------------------------------------------------- 

Function mask(sr,sg,sb,st) 
CopyRect 0,0,512,512,0,0,ImageBuffer(imgbk),ImageBuffer(img) 
SetBuffer ImageBuffer(img) 
LockBuffer 
For j=0 To ImageHeight(img)-1 
  For i=0 To ImageWidth(img)-1 
   rgb=ReadPixelFast(i,j) 
   r=(rgb Shr 16) And 255 
   g=(rgb Shr 8 ) And 255 
   b=rgb And 255 
   If (r>sr-st And r<sr+st) And  (g>sg-st And g<sg+st) And  (b>sb-st And b<sb+st) Then 
    WritePixelFast i,j,0 ; maskiert 
   Else 
    WritePixelFast i,j,rgb ; nicht maskiert 
   EndIf 
  Next 
Next 
UnlockBuffer 
SetBuffer BackBuffer() 
End Function 

Function min(zahl,min) 
If zahl> min Then 
  Return min 
Else 
  Return zahl 
EndIf 
End Function 

Function max(zahl,max) 
If zahl< max Then 
  Return max 
Else 
  Return zahl 
EndIf 
End Function 

Function sliders(r,g,b,t) 
Color 255,0,0 
Rect  0,100,9,266,0 
Rect  0,100+r,9,9,1 
Text 0,0,"Red "+r 

Color 0,255,0 
Rect 10,100,9,266,0 
Rect 10,100+g,9,9,1 
Text 10,16,"Green "+g 

Color 0,0,255 
Rect 20,100,9,266,0 
Rect 20,100+b,9,9,1 
Text 20,32,"Blue "+b 

Color 127,127,127 
Rect 30,100,9,266,0 
Rect 30,100+t,9,9,1 
Text 30,48,"Tolerance "+t 

End Function
