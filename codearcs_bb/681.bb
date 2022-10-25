; ID: 681
; Author: DarkNature
; Date: 2003-05-10 01:54:55
; Title: Queueing behaviour
; Description: Please form an orderly line

; Queueing behaviour from DarkNature.

Graphics 640,480

Type person

	Field x,y
	Field tx,ty
	Field xd,yd
	Field img

End Type

Type target

	Field x,y

End Type

Global maxx=GraphicsWidth()
Global maxy=GraphicsHeight()
Global peepSize=10
Global maxpeeps=30
Global numpeeps=0
Global targets=3

SeedRnd(MilliSecs())

Function makePeople()
	
	imgPeep=CreateImage(peepSize,peepSize)
	SetBuffer(ImageBuffer(imgPeep))
	Color 255,255,255
	Oval 0,0,peepSize,peepSize
	Color 255,0,0
	Oval 0,0,peepSize,peepSize,0
	For i=0 To maxpeeps-1
		p.person=New person
		p\x=Rnd(maxx)
		p\y=Rnd(maxy)
		p\img=CopyImage(imgPeep)
		numpeeps=numpeeps+1
		MidHandle p\img
	Next

	FreeImage imgPeep
	SetBuffer BackBuffer()	
	
End Function

Function makeTargets()

	For i=0 To targets-1
		t.target=New target
		t\x=Rnd(maxx)
		t\y=Rnd(maxy)
	Next

End Function

Function chooseTarget(p.person)
	
	tmpD=999999
	For a.target=Each target
		tdist=Sqr((p\x-a\x)^2+(p\y-a\y)^2)
		If tdist<tmpD
			tmpD=tdist
			p\tx=a\x
			p\ty=a\y
		End If
	Next

End Function

Function doPeople()

	For a.person=Each person
	
		a\xd=0
		a\yd=0
		
		chooseTarget(a)
		
		If a\x>a\tx a\xd=-1
		If a\y>a\ty a\yd=-1
		If a\x<a\tx a\xd=1
		If a\y<a\ty a\yd=1

		For b.person=Each person
			If a<>b
			
				tdistA=Sqr((a\x-a\tx)^2+(a\y-a\ty)^2)
				tdistB=Sqr((b\x-b\tx)^2+(b\y-b\ty)^2)
				dist=Sqr((a\x-b\x)^2+(a\y-b\y)^2)
				cdist=peepSize*1.5
				
				If dist<cdist
					If tdistA>tdistB
						a\xd=0
						a\yd=0
					End If
				End If
				
			End If
		Next
		
		a\x=a\x+a\xd
		a\y=a\y+a\yd
		
		If a\x=a\tx And a\y=a\ty Delete a: numpeeps=numpeeps-1
		
	Next
	
End Function

Function drawTargets()

	For t.target=Each target
		
		Color 0,0,0
		Oval t\x-10,t\y-10,20,20
		Color 255,255,0
		Oval t\x-10,t\y-10,20,20,0
	
	Next

End Function

Function drawPeople()
	
	Color 255,255,255
	For a.person=Each person
		DrawImage a\img,a\x,a\y
	Next

End Function

ClsColor 0,0,100
tim=CreateTimer(30)
go=False
makeTargets()
makePeople()
While Not KeyHit(1)
	
	WaitTimer(tim)
	Cls
	Color 255,255,0
	Text 20,20,"Left: "+numpeeps
	If go=False	
		Text 300,450,"press space"
		If KeyHit(57) go=True
	End If
	If go doPeople()
	drawTargets()
	drawPeople()
	Flip
	If numpeeps=0
		For t.target=Each target
			Delete t
		Next
		makeTargets()
		makePeople()
	End If
	
Wend
End
