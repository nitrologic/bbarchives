; ID: 164
; Author: Seven
; Date: 2001-12-23 13:33:44
; Title: FadeBlock
; Description: Fader/Brighten function, changes selected area of any buffer or image

;FadeBlock function
;Created by: wickedRush Software
;e-mail: wickedrush@wickedrush.com
;
;modified on Dec 23,2001
;
;Fades/Brightens any buffer to specified rgb values
; syntax:
; fadeblock(x,y,width,height,source buffer,destination buffer,Red Fade Amount or total fade amount,
;           [green fade amount],[blue fade amount])
;
;if green and blue fade amounts are 0 or left off then function uses red fade amount for all colors.

hdl=LoadImage("c:\blitz3d\media\test.bmp") ; <-Change to any bitmap to test fade effect

SetBuffer BackBuffer()

DrawImage hdl,0,0 ; draw image to backbuffer
fadeblock(200,100,200,200,BackBuffer(),BackBuffer(),.7,.3,1) ; fade area 200,100 width 200, height 200
; from the backbuffer to the backbuffer
; fade red 70% as bright, fade green 30%, no fade for blue
fadeblock(10,10,100,100,BackBuffer(),BackBuffer(),1.5)
; brighten area 10,10 width 100, height 100. brighten entire area to 150% 
Flip

While Not KeyHit(1)
Wend
End

Function FadeBlock(x,y,x1,y1,frombuffer,tobuffer,fadeR#,fadeG#=0,fadeB#=0)
LockBuffer frombuffer
LockBuffer tobuffer
If fadeB=0 Then fadeB=fadeR
If fadeG=0 Then fadeG=fadeR
For s1=x To x+x1
 For s2=y To y+y1
  rgb=ReadPixelFast(s1,s2,frombuffer) And $ffffff
  r=(rgb Shr 16) And 255
  g=(rgb Shr 8) And 255
  b=rgb And 255
  r=r*fadeR
  b=b*fadeB
  g=g*fadeG
  If r>255 Then r=255
  If g>255 Then g=255
  If b>255 Then b=255
  rgb=(r Shl 16)+(g Shl 8)+b
  WritePixelFast s1,s2,rgb,tobuffer
 Next
Next
UnlockBuffer frombuffer
UnlockBuffer tobuffer
End Function
