; ID: 2255
; Author: Kistjes
; Date: 2008-05-22 09:08:38
; Title: Container behavior
; Description: Makes position, scale & rotation relative to a container object

Rem *****************************************************************************************
This is a small and simple example of using sprites that are related to each other.
A sprite is a type with it's own coordinates, scaleX/Y value and rotation.
Also each sprite has a Draw() method.

Sprites can contain other sprites (and, therefor, a sprite can be content of an other sprite. 
This relation creates a relative dependence of it's location, scaleX/Y and rotation of the sprite's container. 
It was something I was looking for and was finaly able to create myself ;)

Note: it is simplified code. This sprite is always a white rectangle. 
You should create abstracts of the TSprite type like TRectange, TCircle, TImage, TText, etc. 
*********************************************************************************************
End Rem

SuperStrict

Graphics 800, 600, 0
SetBlend(ALPHABLEND)

Global golSprites:TList = CreateList()			'list with all sprites

'let's create some sprites
Local sp1:TSprite = TSprite.Create(Null, 400, 300, 160, 200, 0, 1)
sp1.SetRegPoint(80,80)
Local sp2:TSprite = TSprite.Create(sp1, 0, 0, 120, 120)
Local sp3:TSprite = TSprite.Create(sp2, 0,80,120,20)
sp3.SetRegPoint(60,0)

Local a# = 0
'main loop
Repeat
	Cls
	DrawGrid()		'I draw this grid as a check for the location of the sprites
	
	SetAlpha(0.75)	'make the sprites a little bit transparent
	For Local o:TSprite = EachIn golSprites
		o.Draw()
	Next
	
'ok, now add some dynamics to the sprites
'(remark or un-remark (is that English?) the following lines to see what happens...)	

	sp1.RotateRel(0.2)													'rotate sp1 CCW around it's registration point
'	sp1.SetAbsS(1.0 + Cos(a)*0.25, 1.0 + Cos(a+180) * 0.25)	'scale sp1 in a different X and Y value
'	sp2.SetAbsS(1)														'prevent sp2 to scale when sp1 is scaled in the previous line
'	sp2.RotateRel(-1.2)													'rotate sp2 CW around it's registration point
'	sp3.RotateRel(1.2)													'rotate sp3 CCW around it's registration point 
'	sp1.pfY:+0.2															'move sp1 downward in small steps, taking sp2 and sp3 with it.
																			'	so it looks like it is not rotating relative to sp2
	a:+1
	Flip
Until KeyHit(KEY_ESCAPE)

End

'As I said, this is a grid to check the sprites location
Function DrawGrid()
	SetTransform()
	SetHandle(0,0)
	SetAlpha(0.2)
	DrawRect(0,0,400, 300)
	DrawRect(400,300,400, 300)
	SetAlpha(0.3)
	DrawRect(400,0,400, 300)
	DrawRect(0,300,400, 300)
End Function

'the actual sprite class (or Type)
Type TSprite
	Field poContainer:TSprite = Null
	Field polContent:TList = CreateList()
	
	'all these values are relative
	Field pfX#
	Field pfY#
	Field pfW#
	Field pfH#
	Field pfR#		'rotation
	Field pfSX#		'scaleX
	Field pfSY#		'scaleY
	
	Field pfRegX#	'registration point X
	Field pfRegY#	'registration point Y
	
	Function Create:TSprite(oContainer:TSprite = Null, fx#, fy#, fw#, fh#, fr# = 0, fsx# = 0, fsy# = 0)
		Local o:TSprite = New TSprite
		If oContainer Then
			o.poContainer = oContainer
			oContainer.AddContent(o)
		End If
		o.pfX = fx
		o.pfY = fy
		o.pfW = fw
		o.pfH = fh
		o.pfR = fr
		o.pfRegX = fw/2
		o.pfRegY = fh/2
		If fsx = 0 Then
			If oContainer Then
				fsx = oContainer.GetAbsSX()
			Else
				fsx = 1
			End If
		End If

		If Not fsy Then fsy = fsx		 
		o.SetAbsS(fsx, fsy)
		golSprites.AddLast(o)
		Return o
	End Function

	Method AddContent(oSprite:TSprite)
		If Not polContent.Contains(oSprite) Then	polContent.AddLast(oSprite) 
	End Method
	
	Method GetAbsX#()
		If poContainer Then 
			'here all the relations to the container sprite are calculated to the proper absolute X position
			Local dx# = pfX * poContainer.GetAbsSX() * Cos(poContainer.GetAbsR()) - pfY * poContainer.GetAbsSY() * Sin(poContainer.GetAbsR())
			Return poContainer.GetAbsX() + dx
		End If
		'if there is no container, the pfX value is absolute
		Return pfX
	End Method
	
	Method GetAbsY#()
		If poContainer Then 
			'here all the relations to the container sprite are calculated to the proper absolute Y position
			Local dy# = pfY * poContainer.GetAbsSY() * Cos(poContainer.GetAbsR()) + pfX * poContainer.GetAbsSX() * Sin(poContainer.GetAbsR())
			Return poContainer.GetAbsY() + dy
		End If
		'if there is no container, the pfY value is absolute
		Return pfY
	End Method
	
	Method GetAbsR#()
		If poContainer Then Return poContainer.GetAbsR() + pfR
		Return pfR
	End Method
	
	Method GetAbsSX#()
		If poContainer Then Return poContainer.GetAbsSX() + pfSX
		Return pfSX
	End Method
	

	Method GetAbsSY#()
		If poContainer Then Return poContainer.GetAbsSY() + pfSY
		Return pfSY
	End Method
	

	Method GetRelX#()
		Return pfX
	End Method
	
	Method GetRelY#()
		Return pfY
	End Method
	
	Method GetRelR#()
		Return pfR
	End Method
	
	Method GetRelSX#()
		Return pfSX
	End Method
	
	Method GetRelSY#()
		Return pfSY
	End Method
	
	Method RotateRel(r#)
		pfR:+r
	End Method
	
	Method SetAbsS(fsx#, fsy# = -1)
		If fsy = -1 Then fsy = fsx
		If poContainer Then
			Self.SetRelSX(fsx - poContainer.GetAbsSX())
			Self.SetRelSY(fsy - poContainer.GetAbsSY())
		Else
			Self.SetRelSX(fsx)
			Self.SetRelSY(fsy)
		End If
	End Method

	Method SetRelSX(fsx#)
		pfSX = fsx
	End Method

	Method SetRelSY(fsy#)
		pfSY = fsy
	End Method
	
	Method SetRegPoint(fRegX#, fRegY#)
		pfRegX = fRegX
		pfRegY = fRegY
	End Method
	
	Method Draw()
		SetTransform(Self.GetAbsR(), Self.GetAbsSX(), Self.GetAbsSY())
		SetHandle(pfRegX, pfRegY)
		DrawRect(Self.GetAbsX(), Self.GetAbsY(), pfW, pfH)
	End Method
End Type
