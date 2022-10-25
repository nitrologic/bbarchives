; ID: 2738
; Author: Krischan
; Date: 2010-06-30 00:23:18
; Title: Loading Bar
; Description: Just a simple Loading bar example

Graphics 800,600,32,2

SetBuffer BackBuffer()

Global Screenwidth=GraphicsWidth()
Global Screenheight=GraphicsHeight()
Global title$="Loading Bar Demo"

For i=0 To 300 Step 2
	
	UpdateBar("Task 1",i,300,0,30)
	
Next

For j=0 To 1000 Step 4
	
	UpdateBar("Task 2",j,1000,30,100)
	
Next

Cls

Print "Finished!"

WaitKey

End

Function UpdateBar(info$,current#,max#,startpercent#=0.0,endpercent#=100.0,r1%=128,g1%=0,b1%=0,r2%=0,g2%=192,b2%=0,r3%=255,g3%=255,b3%=255)
	
	Local percent#,milepercent#
	
	Local width%=Screenwidth/2
	Local height%=18
	Local midx%=(Screenwidth/2)
	Local midy%=(Screenheight/2)
	Local sx%=midx-(width/2)
	Local sy%=midy-(height/2)
	Local factor#=width*1.0/100
	
	; program termination always possible
	If KeyHit(1) Then End
	
	; calc percentages
	percent=(current*100.0)/max
	milepercent=((percent*(endpercent-startpercent))/100.0)+startpercent
	
	; upper bar border, red bar background, green progress bar (total)
	DrawBar(sx,sy,width,height,r1,g1,b1)
	DrawBar(sx,sy,Int(milepercent*factor),height,r2,g2,b2)
	Color 0,0,0 : Rect sx,sy,width+1,height,0
	
	; lower bar border, red bar background, green progress bar (current task)
	DrawBar(sx,sy+height+3,width,height,r1,g1,b1)
	DrawBar(sx,sy+height+3,Int(percent*factor),height,r2,g2,b2)
	Color 0,0,0 : Rect sx,sy+height+3,width+1,height,0
	
	Color r3,g3,b3
	
	; text infos
	Text midx,midy-18,title,1,1
	Text midx,midy-1,Int(milepercent)+"%",1,1
	Text midx,midy+height+2,info,1,1
	
	Flip 1
	
End Function

Function DrawBar(x%,y%,width%,height%,r%,g%,b%)
	
	Local i%,c%
	
	For i=0 To height-1
		
		Color r,g,b
		Line x,y+i,x+width,y+i
		
	Next
	
End Function
