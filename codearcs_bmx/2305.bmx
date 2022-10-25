; ID: 2305
; Author: Beaker
; Date: 2008-08-24 21:45:32
; Title: TMap visualise
; Description: See what is happening inside a TMap

SuperStrict

Graphics 800,600
Global scrolly% = 20

Global myMap:TMap = New TMap

Global animal$,noise$
ReadData animal,noise
While animal <> "[end]"
	myMap.insert(animal,noise)
	main()
	ReadData animal,noise
	WaitKey
Wend

While Not KeyDown(KEY_ESCAPE)
	main()
Wend

End



Function main()
	Cls

	Local start:Tnode = myMap._root
	
	SetColor 100,100,100
	drawMapLines(start,scrolly,300, 400)
	SetColor 255,255,255
	drawMapText(start,scrolly,300, 400)
	
	If animal = "[end]"
		DrawText "Escape to exit",5,5
	Else
		DrawText "Any key to insert new item",5,5
		DrawText "Added "+animal+": "+noise,5,35
	EndIf

	Flip
	
	scrolly:+(KeyDown(KEY_LEFT)-KeyDown(KEY_RIGHT))*5
End Function



Function drawMapText(node:Tnode,x%,y%,space%)
	DrawText String(node.key()),x,y
	Local newspace% = space/3
	If node._left._parent = node
		drawMapText(node._left,x+100,y-newspace,space/2)
	EndIf
	If node._right._parent = node
		drawMapText(node._right,x+100,y+newspace,space/2)
	EndIf
End Function

Function drawMapLines(node:Tnode,x%,y%,space%)
	DrawOval x-2,y-2,5,5
	Local newspace% = space/3
	If node._left._parent = node
		SetColor 50,200,50
		DrawLine x,y,x+100,y-newspace
		drawMapLines(node._left,x+100,y-newspace,space/2)
	EndIf
	If node._right._parent = node
		SetColor 200,50,50
		DrawLine x,y,x+100,y+newspace
		drawMapLines(node._right,x+100,y+newspace,space/2)
	EndIf

End Function



#animals
DefData "dog","bark"
DefData "cat","meiow"
DefData "sheep","baah"
DefData "cow","moo"
DefData "wolf","howl"
DefData "nightingale","sing"
DefData "squid","squirt"
DefData "sloth","snore"
DefData "haggis","och"
DefData "fish","gulp"
DefData "mouse","squeek"
DefData "hyena","cackle"
DefData "chicken","cluck"
DefData "dolphin","click"
DefData "human","talk"
DefData "klingon","klingon"
DefData "banshee","howl"
DefData "whale","moan"
DefData "lamb","bleet"
DefData "cock","cockadoodle"
DefData "horse","nay"
DefData "donkey","eeyore"
DefData "cuckoo","cuckoo"
DefData "slug",".."
DefData "lion","roar"
DefData "pig","oink"
DefData "boar","grunt"
DefData "[end]","[end]"
