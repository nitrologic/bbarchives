; ID: 924
; Author: DarkNature
; Date: 2004-02-07 08:24:22
; Title: Forrest fire model
; Description: Burn your screen off

Graphics 640,480,16,0

SetBuffer BackBuffer()



Global sWidth=GraphicsWidth()

Global sHeight=GraphicsHeight()

Global bWidth=160

Global bHeight=120

Global tSize=4

Global p



Dim b0(bwidth,bheight)

Dim b1(bwidth,bheight)

Dim c(bwidth,bheight)



SeedRnd(MilliSecs())



Function setup()



	For y=0 To bheight-1

		For x=0 To bwidth-1

			b0(x,y)=1

			b1(x,y)=1

		Next

	Next



	For i=0 To 1+Rnd(3)

		b0(Rnd(bwidth),Rnd(bheight))=2

	Next

	
End Function



Function switchem()



	For y=0 To bheight-1

		For x=0 To bwidth-1

			b0(x,y)=b1(x,y)

		Next

	Next



End Function



Function drawboard()



	For y=0 To bheight-1

		For x=0 To bwidth-1

			
			If c(x,y)>5

				Color c(x,y),c(x,y),0

				Rect x*tsize,y*tsize,tsize,tsize,1

				c(x,y)=c(x,y)-5

			Else c(x,y)=0

			End If

							
		Next

	Next

	
End Function



Function burn()

	
	p=0

	
	For y=0 To bheight-1

		For x=0 To bwidth-1

			
			ncount=0

			For y1=y-1 To y+1

				For x1=x-1 To x+1

					If (x1>=0 And x1<=bwidth-1) And (y1>=0 And y<=bheight-1)

						If (Not(x1=x And y1=y))

							If b0(x1,y1)=2 ncount=ncount+1

						End If

					End If

				Next

			Next

			
			prob=55

			
			If b0(x,y)=2 b1(x,y)=3

			
			If ncount>0 And b0(x,y)=1

				ignite=Rnd(100)

				If ignite>prob And b0(x,y)=1

					b1(x,y)=2

					c(x,y)=255

					p=p+1

				End If

			End If

						
		Next

	Next

	
	
End Function



;ClsColor 0,0,100

setup()

t=CreateTimer(75)

While Not KeyHit(1)



	Cls



	drawboard()

	burn()

	switchem()

		
	Flip

	
	If p=0 setup()

	WaitTimer(t)



Wend

End
