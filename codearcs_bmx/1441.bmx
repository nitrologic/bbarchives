; ID: 1441
; Author: Booticus
; Date: 2005-08-09 04:08:25
; Title: fake paragraph graphics
; Description: well, makes fake paragraph graphics

Strict

Framework brl.glmax2d

Import  brl.random
Import  brl.graphics
Import  brl.math 
Import  brl.retro
Import  brl.system

SetGraphicsDriver GLMax2DDriver()
Graphics 640,480,0
SetBlend ALPHABLEND

For Local a:Int = 0 To 2
	Local theparagraph:paragraph= New paragraph
	theparagraph.init(Rand(0,500),Rand(0,500))
Next

While Not KeyDown(KEY_ESCAPE)
	Cls
	Local a:Int=Rand(0,100)
	If a = 50
		createrandomparagraph
	EndIf
	displayparagraphs
	Flip
Wend


Type paragraph
	Field x:Int
	Field y:Int
	Field wordlength:Int[500]
	Field wordcount:Int
	Field currentword:Int		
	Field boundary:Int
	Field thecount:Int
	Field wordsactive:Int=0
	Field alpha:Float
	Field life:Int
	Field lifecounter:Int
	Field dead:Int=False
	Field timer:Int=0
	Field timerincrement:Int
	Field timermax:Int
	Field doneprinting:Int = False
	Global paragraphlist:TList
	
	Method New ()
		If paragraphlist= Null Then paragraphlist= CreateList ()
		ListAddLast paragraphlist, Self	
	End Method


	Method init(thex:Int,they:Int)
		timerincrement=1
		timermax=1
		currentword=0
		life=Rand(300,800)
		lifecounter=0
		alpha=1.0
		x=thex
		y=they
		wordcount=Rand(100,399)
		For Local a:Int = 0 To wordcount
			wordlength[a]=Rand(3,9)
		Next
		boundary=Rand(100,300)
		boundary=100
	End Method
	
	Method update()
		lifecounter:+1
		timer:+timerincrement' The bigger the number, the faster it will make words appear		
		If timer>timermax
			currentword:+1
			If currentword>wordcount
				currentword=wordcount
				doneprinting=True
			EndIf
			timer=0
		EndIf
		
		If (doneprinting)
			alpha:-0.005
			If alpha<0.0
				paragraphlist.remove(Self)
			EndIf
		EndIf
		Self.draw
	End Method


	Method draw()
		' Play our printout noise
		SetAlpha alpha
		Local ty:Int=y
		Local tx:Int=x	
		tx=tx+5 ' This will indent our first word
		For Local i:Int=0 To currentword
			SetColor 0,255,0
			DrawLine tx,ty,tx+wordlength[i],ty
			tx=tx+wordlength[i]+3
			If tx+wordlength[i]>boundary+x
				ty=ty+2
				tx=x
			EndIf
		Next
		SetColor 255,255,255
		SetAlpha 1.0
	End Method
End Type


Function displayparagraphs()
	Local totalparagraphs:Int=0
	If paragraph.paragraphlist<>Null
		For Local theparagraph:paragraph = EachIn paragraph.paragraphlist
			theparagraph.Update
			totalparagraphs:+1
		Next
	EndIf
End Function

Function createrandomparagraph()
	Local theparagraph:paragraph= New paragraph
	theparagraph.init(Rand(0,500),Rand(0,500))
End Function
