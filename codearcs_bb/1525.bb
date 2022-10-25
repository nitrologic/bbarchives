; ID: 1525
; Author: Wings
; Date: 2005-11-10 06:41:08
; Title: Inventory example (Bag in bag system)
; Description: This is a simple Bag in Bag inventory example. Good for RPG games and RTS. Putt Multipple units in a Unit.

;Get whole source with data file here.
;http://www.tiberion.eu/2007/inventory_demo.zip

;this demo demostrates a simple inventory handling with mouse system.
Global countID=0
Global selectmode
Global selected_stuff.item

Graphics 800,600,32,0
SetBuffer BackBuffer()

Type ITEM
	Field child.ITEM[30]
	Field mother.item
	Field MaxSlots
	Field Name$
	Field id
	Field obj
	Field xpos
	Field ypos
	Field Nr ;Nr of items.
	Field Stack ;Nr of stack of same items.. good for stacking gold coins..
End Type


Dim ICON(100)
Global mark,mark2,empty,lmsg$
mark=LoadImage("mark.bmp")
mark2=LoadImage("markb.bmp")
empty=LoadImage("empty.bmp")
ICON(1)=LoadImage("sword.bmp")
ICON(2)=LoadImage("bag.bmp")
ICON(3)=LoadImage("healment.bmp")
ICON(4)=LoadImage("sheld.bmp")
ICON(5)=LoadImage("ring.bmp")
ICON(6)=LoadImage("flower.bmp")
ICON(7)=LoadAnimImage("coins.bmp",32,32,0,10)
;g=LoadAnimImage("coins.bmp",32,32,0,9)
;Stop

For i=1 To 12
	countID=countID+1
	x=Rnd(700)+50
	y=Rnd(500)+50
	stuff.item = New item
	r=Rnd(6)+1

	Select r
	Case 1;Create Sword
		stuff\Name$="Sword"
		stuff\MaxSLots=0
		stuff\id=countID
		stuff\obj=1 ;Ie sword image :)
	Case 2;Create bag
		stuff\Name$="Bag"
		stuff\maxslots=9
		stuff\id=countID
		stuff\obj=2 ;Bag image..
	Case 3;Create healment
		stuff\name$="Healment"
		stuff\MaxSlots=0
		stuff\id=countID
		stuff\obj=3 ;healment image..
	Case 4;Create sheld
		stuff\name$="Sheld"
		stuff\MaxSlots=0
		stuff\id=countID
		stuff\obj=4;Sheld image
	Case 5;Create ring
		stuff\name$="Ring"
		stuff\MaxSlots=0
		stuff\id=countID
		stuff\obj=5;Ring imgae..
	Case 6;Create a flower
		stuff\name$="Flower"
		stuff\MaxSlots=0
		stuff\id=countID
		stuff\obj=6;Flower image.
	Case 7;Create Coins.
		stuff\name$="Gold"
		stuff\MaxSlots=0
		stuff\id=countID
		stuff\obj=7
		stuff\nr=Rnd(9)+1
		stuff\stack=10
	End Select
	
	stuff\xpos=x
	stuff\ypos=y
Next









While Not KeyHit(1)

	Cls
	
	handle_input()
	drawall()
	Flip()
	
Wend










;Handles all inputs
Function handle_input()

	x=MouseX()
	y=MouseY()

	Select selectmode
	
	Case 0
		If MouseDown(1)
			For stuff.item = Each item
				If RectsOverlap(x,y,1,1,stuff\xpos,stuff\ypos,32,32) And stuff\mother=Null 
					selected_stuff=stuff
					selectmode=1
					Exit
				End If
			Next
		End If
	Case 1
		selected_stuff\xpos=x-16
		selected_stuff\ypos=y-16
		lmsg$="Picked "+selected_stuff\nr+" of "+selected_stuff\name$

		If MouseDown(1)=0 Then selectmode=2
	Case 2
		For stuff.item = Each item
			If RectsOverlap(x,y,1,1,stuff\xpos,stuff\ypos,32,32)
				If stuff\MaxSlots>0 And stuff<>selected_stuff
					If stuff\mother=Null
						For i=1 To stuff\MaxSlots
							If stuff\CHILD[i]=Null
							
								stuff\CHILD[i]=selected_stuff
								selected_stuff\mother=stuff
								Exit
						
							End If
						Next
					End If
				Else If stuff\name$="Gold" And selected_stuff\name$="Gold" And stuff<>selected_stuff And stuff\mother=Null

					stuff\nr=stuff\nr+selected_stuff\nr
					Delete selected_stuff
					lmsg$="Stacked "+stuff\nr+" of "+stuff\name$
				End If
			End If
		Next
		
		selectmode=0
	End Select


	;Splatt out all things on screen for fun :)
	If MouseDown(2)
		For stuff.item = Each item
			If RectsOverlap(x,y,1,1,stuff\xpos,stuff\ypos,32,32) And stuff\mother=Null 
				
				If stuff\name$="Gold"
					nr=Input ("Enter nr to splitt")
					If nr>stuff\nr Then nr=stuff\nr
					stuff\nr=stuff\nr-nr
					x=stuff\xpos+16
					y=stuff\ypos+16
					If stuff\nr<1 Then Delete stuff
					countID=countID+1
					stuff.item = New item
					stuff\name$="Gold"
					stuff\MaxSlots=0
					stuff\id=countID
					stuff\obj=7
					stuff\nr=nr
					stuff\stack=10
					stuff\xpos=x
					stuff\ypos=y
					

					Exit
				End If
				
				If stuff\MaxSlots>0 
					For i=1 To stuff\MaxSlots
						If stuff\CHILD[i]<>Null
							x=stuff\xpos+Rnd(50)-25
							y=stuff\ypos+Rnd(50)-25
							stuff\CHILD[i]\xpos=x
							stuff\CHILD[i]\ypos=y
							stuff\CHILD[i]\mother=Null
							stuff\CHILD[i]=Null
						End If

					Next
					Exit
				End If
			End If
		Next
	End If


End Function













Function drawall()

	For stuff.item=Each item
		If stuff\mother=Null ; If item got a mother it will not be drawn on screen. :)
			
			
			
			If stuff\name$="Gold"
				If stuff\nr<10
					DrawImage icon(stuff\obj),stuff\xpos,stuff\ypos,stuff\nr-1
				Else
					DrawImage icon(stuff\obj),stuff\xpos,stuff\ypos,9
				End If
			Else
				DrawImage icon(stuff\obj),stuff\xpos,stuff\ypos
			
				If stuff\MaxSlots>0
					x1=stuff\xpos
					y1=stuff\ypos-20
					x2=0:y2=0
					For i=1 To stuff\MaxSlots
					
						If stuff\CHILD[i]<>Null
							If stuff\CHILD[i]\MaxSlots>0
								DrawImage mark2,x1+x2,y1+y2
							Else
								DrawImage mark,x1+x2,y1+y2
							End If
							x2=x2+5	
							If x2>25 Then x2=0:y2=y2+5	
						
						End If
					Next
			
				End If
			End If
		End If
	
	Next


If selected_stuff<>Null And selectmode=1
	stuff.item = selected_stuff
	
	If selected_stuff\name$="Gold"
		If stuff\nr<10
			DrawImage icon(selected_stuff\obj),selected_stuff\xpos,selected_stuff\ypos,selected_stuff\nr-1

		Else
			DrawImage icon(selected_stuff\obj),selected_stuff\xpos,selected_stuff\ypos	,9
		End If
	Else
		DrawImage icon(selected_stuff\obj),selected_stuff\xpos,selected_stuff\ypos
	End If
End If



Text 0,0,lmsg$

End Function
