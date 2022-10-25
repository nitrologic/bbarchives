; ID: 1408
; Author: Telemental
; Date: 2005-06-24 14:51:11
; Title: button library
; Description: A simple button class

' Button Include
' button.x, y: button location
' button.txoff, tyoff: x&y offset for text.
' button.overlay: whether text appears on button or not.
' button.hover: whether there is a hover image.
' button.alpha: the alpha of the hover image.
' button.text: Text for button.
' button.zorder: What screen layer the button is on. Only the highest ZOrder buttons will activate.
' button.image and button.himage: image and hoverimage. Need to add a down image
' methods
' button.draw: draws button
' button.checkover: sees whether mouse is over button.



Global buttonlist:TList = New TList
Global stat_leftbutton 'leftbutton
Global stat_rightbutton 'rightbutton

Type button
	Field x#, y#, h#, w#, zorder#
	Field txoff# = 5
	Field tyoff# = 5
	Field overlay = 0
	Field hover# = 0
	Field alpha# = 1
	Field text$ = Null
	Field image:TImage
	Field himage:TImage
	
	
	Method Set(bimage:TImage, bx, by, bz=0)
		x = bx
		y = by
		w = ImageWidth(bimage)
		h = ImageHeight(bimage)
		image = bimage
		zorder = bz
	End Method
	
	Method New()
		buttonlist.addlast Self
	End Method
	
	Method Draw(collisionlayer = 1)
		DrawImage image, x, y
		If Not himage Then hover = 0
		If CheckOver() And hover Then
			SetAlpha(alpha)
			SetBlend(ALPHABLEND)
			DrawImage(himage, x, y)
			SetAlpha(1)
		EndIf
		
		If Not text = Null Then
			If TextWidth(text) > w And overlay = 0 Then 
				DrawText text, x+w+txoff, y
			Else
				DrawText text, x+txoff, y+tyoff
			EndIf
		EndIf

		CollideImage(image, x, y, 0, 0, collisionlayer)
	End Method
	
	Method CheckOver()
		If HighestZ() > z Then Return False
		If MouseX() < x Or MouseX() > x+w Then Return False
		If MouseY() < y Or MouseY() > y+h Then Return False
		Return True
	End Method
	
	Method HighestZ()
		tempz = 0
		For temp:button = EachIn buttonlist
			tempz = Max(temp.zorder, tempz)
		Next
		Return tempz
	End Method
End Type


Function getmousestatus()
	stat_leftbutton = MouseHit(1)
	stat_rightbutton = MouseHit(2)
End Function
