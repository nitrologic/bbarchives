; ID: 1254
; Author: Jeppe Nielsen
; Date: 2005-01-06 15:01:01
; Title: Matrix Effect
; Description: The effect as seen in the Matrix triology

Type letter

Field i

Field n

Field x#,y#

Field ys#

End Type

Graphics 800,600,16,1
Dim image(255,3)

For n=0 To 3
Color 0,255-n*50,0

For i=0 To 255

image(i,n)=CreateImage(8,8)
SetBuffer ImageBuffer(image(i,n))

Text 4,4,Chr(i),1,1

Next

Next

SetBuffer BackBuffer()


For x=0 To 800 Step 16
	For y=0 To 600 Step 10
	
		l.letter=New letter
		l\x=x
		l\y=y
		
		l\i=Rand(0,255)
		l\n=Rand(0,3)
		
		l\ys#=Rnd(4,10)
		
	Next
Next


font=LoadFont("Times new roman",128)

SetFont font


Repeat
	Cls
	
	Color 0,64+Sin(MilliSecs()/10)*32,0
	
	Text 400,300,"Matrix",1,1
	
	For l.letter=Each letter
		
		DrawImage image(l\i,l\n),l\x,l\y
		
		l\y#=l\y#+l\ys
		
		If l\y>600 Then l\y=-16
		
	Next
	
	Flip
	
Until KeyDown(1)
End
