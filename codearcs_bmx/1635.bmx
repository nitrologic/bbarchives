; ID: 1635
; Author: Diablo
; Date: 2006-03-06 11:35:41
; Title: GUI Base Code
; Description: Some GUI Base Code

SuperStrict

Rem
	Imports
EndRem
Import "rect.bmx"
Import BRL.LinkedList
Import BRL.Max2D

Rem
	bbdoc: The current handle of the active gadget
EndRem
Global GUIactiveGadget:GUI_TGadget

Rem
	bbdoc: The current gadget that the mouse is over
EndRem
Global GUIOverGadget:GUI_TGadget

Global GUIMouseState%[4]
Global GUIKeyState%[227]
Global GUIKeyStack:TList = CreateList()

Rem
	Just a helper type
EndRem
Type GUI_TKey

	Field key%
	
End Type

Rem
	bbdoc: Base type for all gadgets
endrem
Type GUI_TGadget
	
	Global gadgetList:TList = CreateList()	' List of all gadgets
		
	Field name$									' Name of the gadget
	
	Field parent:GUI_TGadget					' Parent of the gadget
	
	Field x%, y%, w%, h%						' Gadgets 'rect'
	Field lx%, ly%, lw%, lh%					' Gadgets local 'rect'
	Field z%									' z~ position of the gadget
	
	Field renderRect:GUI_TRect					' The render rect for this gadget
	
	Field r%, g%, b%, a# = 1					' Red, Green, Blue & alpha of the gadget

	Field ChildList:TList = CreateList()		' List of children

	Field ClientX%								' If a gadget is created and the parent is this, X is offseted by this
	Field clientY%								' If a gadget is created and the parent is this, Y is offseted by this
	
	Field ClientW%								' Client area with
	Field ClientH%								' Client area height
	
	Field disabled@								' Is this gadget disabled
	
	Field hidden@								' Is this gadget hidden
	
	Field active@								' Is this gadget the active one
	
	Field OnHide()								' Call this function on hidding
	Field OnShow()								' Call this function on showing
	
	Field OnMouseHit(_button%)					' Call this function if the mouse is hit
	Field OnMouseClicked(_button%)				' Call this function if a mouse button is clicked
	Field OnMouseDown(_button%)					' Call this function if a mouse button is down
	Field OnMouseOver(_mouseX%, _mouseY%)		' Call this function if the mouse is over
	Field OnMouseMove(_mouseX%, _mouseY%)		' Call this function if the mouse is moving on gadget
	Field OnMouseEnter(_mouseX%, _mouseY%)		' Call this function when mouse enters gadget
	Field OnMouseLeave(_mouseX%, _mouseY%)		' Call this function when the mouse leaves the gadget
	Field OnKeyHit(_key%)						' Call this function when a key is hit and gadget is active
	Field OnKeyDown(_key%)						' Call this function when a key is down
	Field OnKeyPressed(_key%)					' Call this function when a key is released
	Field OnFocusLoss()							' Call this function upon loss of focus i.e. no longer active gadget
	Field OnFocusGain()							' Call this function upon gain
	
	Field gadgetMouseState@[4]					' The state of mouse buttons
	Field gadgetKeyState@[227]					' The state of the keys
	
	Field gadgetMouseMoved@						' True if the mouse was moved over the gadget
	Field gadgetMouseOver@						' True if the mouse is over the gadget
	Field gadgetMouseEnter@						' True if the mouse entered the gadget
	Field gadgetMouseLeave@						' True if the mouse leaves the gadget
	
	Field gadgetAlwaysOnTop@					' Is the gadget always on top
	Field gadgetAlwaysOnBottom@					' Is the gadget always on bottom
	
	Field gadgetMouseMoveX%						' Amount the gadget mouse x has moved
	Field gadgetMouseMoveY%						' Amount the gadget mouse y has moved
	
	Field gadgetLocalMouseX%					' Local mouse x position
	Field gadgetLocalMouseY%					' Local mouse y position

	Const GADGET_KEYHIT%			= %0001
	Const GADGET_KEYDOWN%			= %0010
	Const GADGET_KEYPRESSED%		= %0100
	Const GADGET_KEYUP%				= %1000
	
	Rem
		bbdoc: Check if a key was hit
	endrem
	Method IsKeyHit@(_key%)
	
		If gadgetKeyState[_key] = GADGET_KEYHIT Then Return True
	
	End Method
	
	Rem
		bbdoc: Check if a key was pressed
	endrem
	Method IsKeyPressed@(_key%)
	
		 If gadgetKeyState[_key] = GADGET_KEYPRESSED Then Return True
	
	End Method
	
	Rem
		bbdoc: Check if a key is down
	endrem
	Method IsKeyDown@(_key%)
	
		If gadgetKeyState[_key] = GADGET_KEYDOWN Then Return True
	
	End Method
	
	Rem
		bbdoc: Check if a key is up
	endrem
	Method IsKeyUp@(_key%)
	
		If gadgetKeyState[_key] = GADGET_KEYUP Then Return True
	
	End Method
	
	Rem
		bbdoc: Check if mouse button was hit
	endrem
	Method IsMouseHit@(_button%)
	
		If gadgetMouseState[_button] = GADGET_KEYHIT Then Return True
		
	End Method
	
	Rem
		bbdoc: Check if a mouse button is down
	endrem
	Method IsMouseDown@(_button%)
	
		If gadgetMouseState[_button] = GADGET_KEYDOWN Then Return True
		
	End Method
	
	Rem
		bbdoc: Check if a mouse button was pressed
	endrem
	Method IsMousePressed@(_button%)
		
		If gadgetMouseState[_button] = GADGET_KEYPRESSED Then Return True
		
	End Method
	
	Rem
		bbdoc: Check if a mouse button is up
	endrem
	Method IsMouseUp@(_button%)
	
		If gadgetMouseState[_button] = GADGET_KEYUP Then Return True
	
	End Method
	
	Rem
		bbdoc: Check if the mouse was moved
		returns: True if mouse was moved
	EndRem
	Method IsMouseMoved@()

		Return gadgetMouseMoved
			
	End Method
	
	Rem
		bbdoc: Check if the mouse entered the gadget
		returns: True if the mouse entered the gadget
	EndRem
	Method IsMouseEntering@()
	
		Return  gadgetMouseEnter
	
	End Method
	
	Rem
		bbdoc: Check if the mouse has left the gadget
		returns: True if it is ?<
	EndRem
	Method IsMouseLeaving@()
	
		Return gadgetMouseLeave
	
	End Method
	
	Rem
		bbdoc: Get the amount the mouse was moved
		returns: The amount the mouse was move (x)
	EndRem
	Method GetGadgetMouseXSpeed%()
	
		Global lastX:Int = MouseX()
		Local result:Int = MouseX() - lastX
		lastX = MouseX()	
		Return result
		
	End Method
	
	Rem
		bbdoc: Get the amount the mouse was moved
		returns: The amount the mouse was move (y)
	EndRem
	Method GetGadgetMouseYSpeed%()
	
		Global lastY:Int = MouseY()
		Local result:Int = MouseY() - lastY
		lastY = MouseY()	
		Return result
		
	End Method
	
	Rem
		bbdoc: Move the gadget to a new position
	EndRem
	Method Move(_x%, _y%)
	
		x = _x
		y = _y
		
		For Local kid:GUI_TGadget = EachIn ChildList
			kid.Move(_x + kid.lx, _y + kid.ly)
		Next
	
	End Method
	
	Rem
		bbdoc: Is mouse over the gadget
	endrem
	Method IsMouseOver@()
	
		If IsInGadget(MouseX(), MouseY()) Then Return True
	
	End Method
	
	Rem
		bbdoc: Update the gadgets local rect
		about: Call this function after you have moved gadgets about
	endrem
	Method UpdateLocalRect()
	
		lx = x
		ly = y
		lw = w
		lh = h
		
		If parent Then
			
			lx:- parent.x
			ly:- parent.y
		
		EndIf
		
	End Method
	
	Rem
		bbdoc: Set if the gadget will always be on top
	EndRem
	Method SetAlwaysOnTop(_to@)
		
		gadgetAlwaysOnTop = _to
		If _to = True Then gadgetAlwaysOnBottom = False
		
	End Method
	
	Rem
		bbdoc: Set if the gadget will always be on bottom
	EndRem
	Method SetAlwaysOnbottom(_to@)
		
		gadgetAlwaysOnbottom = _to
		If _to = True Then gadgetAlwaysOnTop = False
		
	End Method
	
	Rem
		bbdoc: Render the gadget (debug render here!!!)
	EndRem
	Method Render()
	
		Local lastBlend% = GetBlend()
		
		Local vx%, vy%, vw%, vh%
		GetViewport(vx, vy, vw, vh)
		
		' Set this render rect to x,y,w,h (duh!)
		renderRect.Set(x, y, w, h)
		
		If parent Then 
			renderRect = parent.renderRect.InterRect(renderRect)
		EndIf
		
		SetViewport renderRect.x, renderRect.y, renderRect.w, renderRect.h
			
		SetBlend ALPHABLEND
		
		SetColor 0, 0, 0
		DrawRect x, y, w, 1
		DrawRect x, y, 1, h
		DrawRect (x + w) - 1, y, 1, h
		DrawRect x, (y + h) - 1, w, 1
		
		SetColor r, g, b
		SetAlpha a
		
		DrawRect x + 1, y + 1, w - 2, h - 2
		
		SetColor 0, 0, 0
		DrawText name, x + 3, y + 3
		DrawText z, x + w - 6 - 20, y + h - 6 - 12
		
		SetColor 255, 255, 255
		SetAlpha 1
		
		SetViewport(vx, vy, vw, vh)
		
	End Method
	
	Rem
		bbdoc: Update the gadget
	endrem
	Method Update()
		
		If IsInGadget(MouseX(), MouseY()) Then GUIOverGadget = Self
		
		' Set some globals up
		Global _lastMouseX% = MouseX(), _lastMouseY% = MouseY()
		Global mouseState%, lastMouseDown%
		Global keyState%, lastKeyDown%, lastKeyDownStack:TList = CreateList()
		
		' Clear some flags
		gadgetMouseEnter	= False
		gadgetMouseLeave	= False
		gadgetMouseOver		= False
		gadgetMouseMoved	= False
		
		If Not IsInGadget(_lastMouseX, _lastMouseY) Then
			
			If IsInGadget(MouseX(), MouseY()) Then
				
				If OnMouseEnter Then OnMouseEnter(MouseX(), MouseY())
				keyState = 0
				mouseState = 0
				
				gadgetMouseEnter = True

			EndIf
			
		End If
		
		
		If IsInGadget(MouseX(), MouseY()) Then
		
			' Now that we know the mouse if in the gadget we can call the mouse over function	
			If OnMouseOver Then OnMouseOver(MouseX() - x, MouseY() - y)
			gadgetMouseOver = True
			
			gadgetLocalMouseX = (MouseX() - x)
			gadgetLocalMouseY = (MouseY() - y)
			
			If GetGadgetMouseXSpeed() <> 0 Or GetGadgetMouseYSpeed() <> 0 Then 
				gadgetMouseMoved = True
				
				If OnMouseMove Then OnMouseMove(MouseX(), MouseY())
				
				_lastMouseX = MouseX()
				_lastMouseY = MouseY()	
			EndIf
			
			' Check if a key was pressed
			For Local key% = 8 To 226
				
				gadgetKeyState[key] = GADGET_KEYUP
				
				Local akey:GUI_TKey = New GUI_TKey
				akey.key = key
					
				' Check if a key was hit and call function
				If GUI_InStack(key, GUIKeyStack) = 1 Then

					If OnKeyHit Then OnKeyHit(key)
					gadgetKeyState[key] = GADGET_KEYHIT
				
				EndIf
				
				' Check if a key is down and call function
				If KeyDown(key) Then
				
					If OnKeyDown Then OnKeyDown(key)
					keyState = 2
					If Not GUI_InStack(key, lastKeyDownStack) Then lastKeyDownStack.AddLast akey
					If gadgetKeyState[key] <> GADGET_KEYHIT Then gadgetKeyState[key] = GADGET_KEYDOWN
					
				EndIf
				
				' Check is a key was 'pressed' and call function
				If Not KeyDown(key) And keyState = 2 And GUI_InStack(key, lastKeyDownStack) Then
					
					If OnKeyPressed Then OnKeyPressed(key)
					keyState = 0
					GUI_RemoveFromStack(key, lastKeyDownStack)
					gadgetKeyState[key] = GADGET_KEYPRESSED
					
				EndIf
			
			Next
			
			gadgetMouseState[1] = GADGET_KEYUP
			gadgetMouseState[2] = GADGET_KEYUP
			gadgetMouseState[3] = GADGET_KEYUP
			
			' Check for mouse hits and call functions
			If GUIMouseState[1] = 1 Then
				
				If OnMouseHit Then OnMouseHit(1)
				Activate()
				gadgetMouseState[1] = GADGET_KEYHIT
				
			EndIf
			
			If GUIMouseState[2] = 1 Then
			
				If OnMouseHit Then OnMouseHit(2)
				Activate()
				gadgetMouseState[2] = GADGET_KEYHIT
				
			EndIf
			
			If GUIMouseState[3] = 1 Then
			
				If OnMouseHit Then OnMouseHit(3)
				Activate()
				gadgetMouseState[3] = GADGET_KEYHIT
				
			EndIf
			
			' Check mouse down, call functions and set state
			If MouseDown(1) Then
			
				If OnMouseDown Then OnMouseDown(1)
				mouseState = 2
				lastMouseDown = 1
				If gadgetMouseState[1] <> GADGET_KEYHIT Then gadgetMouseState[1] = GADGET_KEYDOWN
				
			ElseIf MouseDown(2)
				
				If OnMouseDown Then OnMouseDown(2)
				mouseState = 2
				lastMouseDown = 2
				If gadgetMouseState[2] <> GADGET_KEYHIT Then gadgetMouseState[2] = GADGET_KEYDOWN
				
			ElseIf MouseDown(3)
			
				If OnMouseDown Then OnMouseDown(3)
				mouseState = 2
				lastMouseDown = 3
				If gadgetMouseState[3] <> GADGET_KEYHIT Then gadgetMouseState[3] = GADGET_KEYDOWN
				
			EndIf
			
			' Check if a mouse button is released (clicked)
			If Not MouseDown(1) And mouseState = 2 And lastMouseDown = 1 Then
			
				If OnMouseClicked Then OnMouseClicked(1)
				mouseState = 0
				gadgetMouseState[1] = GADGET_KEYPRESSED
				
			ElseIf Not MouseDown(2) And mouseState = 2 And lastMouseDown = 2
			
				If OnMouseClicked Then OnMouseClicked(2)
				mouseState = 0
				gadgetMouseState[2] = GADGET_KEYPRESSED
				
			ElseIf Not MouseDown(3) And mouseState = 2 And lastMouseDown = 3
			
				If OnMouseClicked Then OnMouseClicked(3)
				mouseState = 0
				gadgetMouseState[3] = GADGET_KEYPRESSED
			
			EndIf
			
		Else
		
			If	IsInGadget(_lastMouseX, _lastMouseY) Then
			
				gadgetMouseLeave = True
				
				If onMouseLeave Then OnMouseLeave(MouseX(), MouseY())
				
				lastMouseDown = -1
				gadgetMouseState[1] = GADGET_KEYUP
				gadgetMouseState[2] = GADGET_KEYUP
				gadgetMouseState[3] = GADGET_KEYUP
			
			EndIf
		
		EndIf	

	End Method
	
	Rem
		bbdoc: Check if a position is in the gadget
		returns: True if x AND y is in the gadget (byte)
	endrem
	Method IsInGadget@(_x%, _y%)
		
		If hidden Then Return False
		If disabled Then Return False
		
		' See if we'er in any other gadgets
		For Local gadget:GUI_TGadget = EachIn gadgetList

			If gadget <> Self Then

				If gadget.z > z Then
					
					If gadget.IsInGadget(_x, _y) Then Return False
						
				EndIf

			EndIf
			
		Next

		If _x <= x + w And _x => x And _y <= y + h And _y => y Then Return True
		Return False
		
	End Method
	
	Rem
		bbdoc: Disable this gadget and all its kids
	endrem
	Method Disable()
		
		If disabled Then Return
		
		disabled = True
		
		If ChildList Then
			For Local gadget:GUI_TGadget = EachIn ChildList
				gadget.Disable()
			Next
		EndIf
		
	End Method
	
	Rem
		bbdoc: Enable this gadget and all its kids
	endrem
	Method Enable()
		
		If disabled = False Then Return
		
		If disabled Then If parent.disabled Then Return
		
		disabled = False
		
		If ChildList Then
			For Local gadget:GUI_TGadget = EachIn ChildList
				gadget.Enable()
			Next
		EndIf
		
	End Method
	
	Rem
		bbdoc: Hides the gadget
	endrem
	Method Hide()
		
		If hidden Then Return
		
		If OnHide Then OnHide()
		hidden = True
		
		If ChildList Then
			For Local gadget:GUI_TGadget = EachIn ChildList
				gadget.Hide()
			Next
		EndIf
		
	End Method
	
	Rem
		bbdoc: Show a gadget
	endrem
	Method Show()
		
		If hidden = False Then Return
		
		If parent Then If parent.hidden Then Return
		
		If OnShow Then OnShow()
		hidden = False
		
		If ChildList Then
			For Local gadget:GUI_TGadget = EachIn ChildList
				gadget.Hide()
			Next
		EndIf
			
	End Method
	
	Rem
		bbdoc: Activate this gadget
	endrem
	Method Activate()
	
		If Not active Then
		
			active = True
			GUIactiveGadget = Self
			
			If OnFocusGain Then OnFocusGain()
			
			' Unactivate all other gadgets
			For Local gadget:GUI_TGadget = EachIn gadgetList
				If gadget <> Self Then
					gadget.active = False
					
					If gadget.OnFocusLoss Then gadget.OnFocusLoss()
				EndIf
			Next
		
		EndIf
	
	End Method
	
	Rem
		bbdoc: Create a *very* basic gadget
	endrem
	Function Create:GUI_TGadget(_name$, _x%, _y%, _w%, _h%, _parent:GUI_TGadget = Null)
	
		Local gadget:GUI_TGadget = New GUI_TGadget
		
		gadget.name 		= _name
		gadget.x 			= _x
		gadget.y			= _y
		gadget.w			= _w
		gadget.h			= _h
		
		gadget.UpdateLocalRect()
		
		gadget.ClientX		= 1
		gadget.clientY		= 1
		gadget.ClientW		= _w - 2
		gadget.ClientH		= _h - 2
		
		gadget.r			= 255
		gadget.g			= 255
		gadget.b			= 255
		gadget.a			= 1.0
		
		gadget.renderRect	= New GUI_TRect
		gadget.renderRect.x = _x
		gadget.renderRect.y = _y
		gadget.renderRect.w = _w
		gadget.renderRect.h = _h
		
		If _parent Then 
			gadget.SetParent(_parent)
			
			gadget.x:+ _parent.ClientX + _parent.x
			gadget.y:+ _parent.clientY + _parent.y
		EndIf
		
		gadget.BringToTop()
		
		gadget.Activate()
		
		gadgetList.AddLast(gadget)
		
		Return gadget
		
	End Function
	
	Rem
		bbdoc: Setup the gadget
	EndRem
	Method Setup(_name$, _x%, _y%, _w%, _h%, _parent:GUI_TGadget = Null)
		
		name 		= _name
		x 			= _x
		y			= _y
		w			= _w
		h			= _h
		
		UpdateLocalRect()
		
		ClientX		= 1
		clientY		= 1
		ClientW		= _w - 2
		ClientH		= _h - 2
		
		r			= 255
		g			= 255
		b			= 255
		a			= 1.0
		
		renderRect	= New GUI_TRect
		renderRect.x = _x
		renderRect.y = _y
		renderRect.w = _w
		renderRect.h = _h
		
		If _parent Then 
			SetParent(_parent)
			
			x:+ _parent.ClientX + _parent.x
			y:+ _parent.clientY + _parent.y
		EndIf
		
		BringToTop()
		
		Activate()
		
		If Not gadgetList.Contains(Self) Then	gadgetList.AddLast(Self)
		
	End Method
	
	Rem
		bbdoc: Set the parent of this gadget
	EndRem
	Method SetParent(_gadget:GUI_TGadget)
	
		' If it already has a parent the remove it from the child list
		If parent Then
			
			parent.ChildList.Remove(Self)
		
		EndIf
		
		' Make _gadget the new parent and add it to the list
		parent = _gadget
		
		If parent Then
			If Not parent.ChildList Then parent.ChildList = CreateList()
			parent.ChildList.AddLast(Self)	
			
			z = parent.z + 1
		EndIf
	
	End Method
	
	Rem
		bbdoc: Bring this gadget to the top and re-order kids
	endrem
	Method BringToTop()
		
		If gadgetAlwaysOnBottom Then z = 0; Return
		
		' If this gadget has a parent then bring the parent to top instead
		If parent Then 
			
			parent.BringToTop()
			
		EndIf
		
		' Set this z to the highest z...
		z = gadgetList.count()
		
		' ... and then set all other gadgets z - 1
		For Local gadget:GUI_TGadget = EachIn gadgetList
		
			If gadget <> Self Then 
				If gadget.z > 0 Then gadget.z:- 1
				
				If gadget.gadgetAlwaysOnTop Then gadget.BringToTop()
			EndIf
			
		Next

		OrderChildren()
		
	End Method
	
	Rem
		bbdoc: Re-order the gadgets children
	endrem
	Method OrderChildren()
	
		If ChildList = Null Then Return 
		For Local kid:GUI_TGadget = EachIn ChildList
			kid.z = z
			kid.OrderChildren()
		Next
	
	End Method
	
	Rem
		bbdoc: Free the gadget
	EndRem
	Method Free()
		
		For Local kid:GUI_TGadget = EachIn childList
			kid.free()
		Next
		
		gadgetList.Sort()
		gadgetList.Remove(Self)

		OnMouseHit 		= Null
		OnMouseClicked	= Null
		OnMouseDown		= Null
		OnMouseOver		= Null
		OnMouseMove		= Null
		OnMouseEnter	= Null
		OnMouseLeave	= Null
		OnKeyHit		= Null
		OnKeyDown		= Null
		OnKeyPressed	= Null
		OnFocusLoss		= Null
		
	End Method
		
	Rem
		Override the new method
	EndRem
	Method New()
		
		BringToTop()
		
	End Method
	
	Rem
		Override the compare method
	endrem
	Method Compare%(other:Object)
		
		If other = Self Then Return False
		
		Local otherGadget:GUI_TGadget = GUI_TGadget(other)
		
		Return z - otherGadget.z
		
		
		'If z > otherGadget.z Then Return True
		
		'Return False
		
	End Method
	
	Rem
		Override the delete method
	endrem
	Method Delete()
	
		For Local kid:GUI_TGadget = EachIn childList
			kid.free()
		Next
		
		gadgetList.Sort()
		gadgetList.Remove(Self)

		OnMouseHit 		= Null
		OnMouseClicked	= Null
		OnMouseDown		= Null
		OnMouseOver		= Null
		OnMouseMove		= Null
		OnMouseEnter	= Null
		OnMouseLeave	= Null
		OnKeyHit		= Null
		OnKeyDown		= Null
		OnKeyPressed	= Null
		OnFocusLoss		= Null
		
	End Method
	
	Rem
		Override the ToString method
	EndRem
	Method ToString$()
	
		Local rString$ = "Ref = " + Super.ToString() + "; x = " + x + "; y = " + y + "; w = " + w + "; h = " + h + "; z = " + z
		
		If parent Then rString:+ "~r~n Has Parent {" + parent.ToString() + "}"
		
		Return rString
		
	End Method
	
End Type

Rem
	bbdoc: Render all the gadgets
endrem
Function GUI_Render()

	GUI_TGadget.gadgetList.Sort()
	
	For Local gadget:GUI_TGadget = EachIn GUI_TGadget.gadgetList
	
		If Not gadget.hidden Then gadget.Render()
		
	Next

End Function

Rem
	bbdoc: Update all the gadgets
endrem
Function GUI_Update()
	
	For Local i% = 1 To 3
		
		GUIMouseState[i] = -1
		If MouseHit(i) Then GUIMouseState[i] = 1
	
	Next
	
	GUIKeyStack.Clear()
	For Local i% = 1 To 226
	
		GUIKeyState[i] = -1
		If KeyHit(i) Then 
			GUIKeyState[i] = 1
 			If Not GUI_InStack(i, GUIKeyStack) Then 
				Local key:GUI_TKey = New GUI_TKey
				key.key = i
				GUIKeyStack.AddLast key
			EndIf
		EndIf
	
	Next
	
	For Local gadget:GUI_TGadget = EachIn GUI_TGadget.gadgetList
	
		If gadget.hidden = False Or gadget.disabled = False Then gadget.Update()
		
	Next
	
	If GUIOverGadget And GUIMouseState[1] = 1 Then GUIOverGadget.BringToTop()
	
End Function

Rem
	bbdoc: Check if a key is in the stack
	returns: True on success (byte)
endrem
Function GUI_InStack@(_key%, _stack:TList)
	
	For Local key:GUI_TKey = EachIn _stack
		If key.key = _key Then Return True
	Next
	
	Return False
	
End Function

Rem
	bbdoc: Remove a key from a stack
endrem
Function GUI_RemoveFromStack(_key%, _stack:TList)
	
	For Local key:GUI_TKey = EachIn _stack
		If key.key = _key Then
			_stack.Remove(key)
			Return
		EndIf
	Next

End Function
