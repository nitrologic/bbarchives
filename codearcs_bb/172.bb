; ID: 172
; Author: Matty B
; Date: 2002-01-07 11:22:34
; Title: Flame Anim
; Description: Create a flame animation

;Please use what you want and any suggestions please to 
;mattblackbeard@aol.com
;Fire Animation repeated across bottom of screen
;Screen X Y
Const S_WIDTH=800,S_HEIGHT=600 
;Fire Anim frames,Frame Width ,Frame height, Draw scale to scale up fire 
Const MAX_FRAMES=60,F_WIDTH=S_WIDTH/4,F_HEIGHT=128,F_SCALE=3
;arrays in out
Dim fire_in(F_WIDTH,F_HEIGHT)
Dim fire_out(F_WIDTH,F_HEIGHT)
;store rand bottom line for smooth cycle
Dim bott_line(F_WIDTH,MAX_FRAMES)

;setup graphics
Graphics S_WIDTH,S_HEIGHT
;create animation frames
Global fire_anim = CreateImage(F_WIDTH,F_HEIGHT,MAX_FRAMES)
;store Bottom line
make_bottom_line()

Print "Please Wait. Generating Flame Frames."

;run through fire function 3 times before writing the 
;pixels To the animation the 4th time round
fire(False)
fire(False)
fire(False)
fire(True)

SetBuffer BackBuffer()
Cls

;main loop
While Not KeyDown(1)
	;count frames in loop
   	i = i + 1
	If i >= MAX_FRAMES Then i = 0 
	;tile anim across bottom of screen
	For t = 0 To (S_WIDTH/F_WIDTH)-1 
	DrawBlock fire_anim,t*F_WIDTH,S_HEIGHT-F_HEIGHT,i
	Next
	Flip
Wend
FreeImage fire_anim
End

Function fire(do_anim)
;go through each frame
For frame_num = 0 To MAX_FRAMES-1
;put line from bott_line array into fire_in array
put_line(frame_num)
;if the anim is true set each of the frames for writting too
If do_anim=True Then SetBuffer ImageBuffer(fire_anim,frame_num)
;do the fire blur bit
For y = F_HEIGHT-1 To F_SCALE+1 Step -F_SCALE : For x = 0 To F_WIDTH-1 
xm = x - 1 : xp = x + 1
;If xm or xp go outa range then cycle onto other end of array
If x = 0 Then xm = F_WIDTH-1
If x = F_WIDTH-1 Then xp = 0
;get the four blur vals
cl = fire_in(x,y-1)
cl = cl + fire_in(xm,y)
cl = cl + fire_in(x,y)
cl = cl + fire_in(xp,y)
;div by 4 for average
cl = cl / 4
;decay by a larger amount the smaller the number gets
decay = (768*1.6) / (1+cl)
;set colors depending on brightness
If cl  < decay Then Color 0,0,0 : cl = 0 : decay = 0
If cl => decay And cl <= 255 Then Color cl,0,0      
If cl => 256   And cl <  512 Then Color 255,cl-256,0   
If cl => 512   And cl <  768 Then Color 255,255,cl-512 
cl = cl - decay
;scale the flame and write into out_flame
;if the do_anim is true plot pixel
For k = 0 To F_SCALE
fire_out(x,y-(1+k)) = cl
If do_anim=True Then Plot x,(y-k)
Next
Next : Next
;copy the fire_out into fire_in for next cycle threw
copy()
Next
End Function

;put bottom_line into bottom line of the fire_in
Function put_line(frame_num)
For x = 0 To F_WIDTH-1
fire_in(x,F_HEIGHT-1) = bott_line(x,frame_num)
Next
End Function

;copy fire_in to fire_out
Function copy()
For x = 0 To F_WIDTH-1 : For y = 0 To F_HEIGHT-1
fire_in(x,y) = fire_out(x,y)
Next : Next
End Function

;create random line of number and store
Function make_bottom_line()
SeedRnd MilliSecs()
For y = 0 To MAX_FRAMES-1 : For x = 0 To F_WIDTH-1 
c = Rnd(0,100)
If c > 55 Then bott_line(x,y) = 768
Next : Next
End Function
