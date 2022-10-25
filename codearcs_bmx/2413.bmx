; ID: 2413
; Author: Otus
; Date: 2009-02-21 10:13:18
; Title: MaxGUI Layout
; Description: Layout management for MaxGUI

SuperStrict

Import MaxGUI.MaxGUI

' Abstract type that handles resize events
Type TLayout Abstract
	
	Field parent:TGadget
	
	Method Update() Abstract
	
	Method SetParent(g:TGadget)
		If Not parent Then AddHook EmitEventHook, _Hook, Self
		parent = g
	End Method
	
	Global resize_event:Int = AllocUserEventId("Layout Resize")
	
	Function _Hook:Object(id:Int, data:Object, context:Object)
		Local event:TEvent = TEvent(data)
		If event.id=EVENT_WINDOWSIZE Or event.id=resize_event
			Local l:TLayout = TLayout(context)
			If l And event.source=l.parent Then l.Update()
		End If
		Return data
	End Function
	
End Type

' Grid layout makes gadget fill cells in a grid
Type TGridLayout Extends TLayout
	
	Field items:TList = New TList
	
	Field rows:Int, cols:Int, hgap:Int, vgap:Int, egap:Int
	
	' Add a gadget at position
	Method AddGadget(g:TGadget, x:Int, y:Int)
		Assert (0<=x) And (0<=y) And (x<cols) And (y<rows),..
			"Gadget position outside grid!"
		Local i:TGridItem = New TGridItem
		i.gadget = g
		i.x = x
		i.y = y
		items.AddLast i
		Update
	End Method
	
	Method Update()
		Local gw:Float = Float(ClientWidth(parent) - hgap*(cols-1+2*egap)) / cols
		Local gh:Float = Float(ClientHeight(parent) - vgap*(rows-1+2*egap)) / rows
		For Local i:TGridItem = EachIn items
			Local gx:Int = gw*i.x + hgap*(i.x+egap), gy:Int = gh*i.y + vgap*(i.y+egap)
			SetGadgetShape i.gadget, gx, gy, gw, gh
			EmitEvent TEvent.Create(TLayout.resize_event, i.gadget)
		Next
	End Method
	
	' Create a grid layout. Optional gaps between cells. Setting egap=True also adds gaps outside grid.
	Function Create:TGridLayout(g:TGadget, cols:Int, rows:Int, hgap:Int=0, vgap:Int=0, egap:Int=False)
		Local l:TGridLayout = New TGridLayout
		l.SetParent g
		l.rows = rows
		l.cols = cols
		l.hgap = hgap
		l.vgap = vgap
		l.egap = egap
		Return l
	End Function
	
End Type

Type TGridItem
	
	Field gadget:TGadget
	
	Field x:Int, y:Int
	
End Type

' Box layout stacks gadgets either vertically or horizontally
Type TBoxLayout Extends TLayout
	
	Const X_AXIS:Int = 0
	Const Y_AXIS:Int = 1
	
	Const ALIGN_LEFT:Int = 0
	Const ALIGN_CENTER:Int = 1
	Const ALIGN_RIGHT:Int = 2
	
	Const ALIGN_TOP:Int = 0
	Const ALIGN_BOTTOM:Int = 2
	
	Field items:TList = New TList
	
	Field axis:Int, align:Int, gap:Int
	
	Field wtot:Int, htot:Int, num:Int
	
	' Adds a gadget
	Method AddGadget(g:TGadget)
		Local i:TBoxItem = New TBoxItem
		i.gadget = g
		i.w = GadgetWidth(g)
		i.h = GadgetHeight(g)
		items.AddLast i
		UpdateTotals
		Update
	End Method
	
	Method UpdateTotals()
		wtot = 0
		htot = 0
		num = 0
		For Local i:TBoxItem = EachIn items
			wtot :+ i.w
			htot :+ i.h
			num :+ 1
		Next
	End Method
	
	Method Update()
		Local w:Int = ClientWidth(parent)
		Local h:Int = ClientHeight(parent)
		If axis = X_AXIS
			Local x:Int, n:Int
			Local factor:Float = Float(w-(num-1)*gap)/wtot
			For Local i:TBoxItem = EachIn items
				Local gx:Int = x*factor
				x :+ i.w
				Local gw:Int = x*factor - gx
				gx :+ n*gap
				n :+ 1
				Local gy:Int
				Local gh:Int = Min(i.h, h)
				If align=ALIGN_TOP
					gy = 0
				Else If align=ALIGN_CENTER
					gy = (h-gh)/2
				Else
					gy = h-gh
				End If
				SetGadgetShape i.gadget, gx, gy, gw, gh
			Next
		Else
			Local y:Int, n:Int
			Local factor:Float = Float(h-(num-1)*gap)/htot
			For Local i:TBoxItem = EachIn items
				Local gy:Int = y*factor
				y :+ i.h
				Local gh:Int = y*factor - gy
				gy :+ n*gap
				n :+ 1
				Local gx:Int
				Local gw:Int = Min(i.w, w)
				If align=ALIGN_LEFT
					gx = 0
				Else If align=ALIGN_CENTER
					gx = (w-gw)/2
				Else
					gx = w-gw
				End If
				SetGadgetShape i.gadget, gx, gy, gw, gh
			Next
		End If
	End Method
	
	' Creates a box layout. See constants above for valid axis and align values.
	Function Create:TBoxLayout(g:TGadget, axis:Int, align:Int, gap:Int=0)
		Local l:TBoxLayout = New TBoxLayout
		l.SetParent g
		l.axis = axis
		l.align = align
		l.gap = gap
		Return l
	End Function
	
End Type

Type TBoxItem
	
	Field gadget:TGadget
	
	Field w:Int, h:Int
	
End Type

' Grid intersection layout positions gadgets in a grid, but only resizes when necessary
Type TGridIntLayout Extends TLayout
	
	Field items:TList = New TList
	
	Field rows:Int, cols:Int
	
	' Adds a gadgt at position
	Method AddGadget(g:TGadget, x:Int, y:Int)
		Assert (0<=x) And (0<=y) And (x<cols) And (y<rows),..
			"Gadget position outside grid!"
		Local i:TGridIntItem = New TGridIntItem
		i.gadget = g
		i.x = x
		i.y = y
		i.w = GadgetWidth(g)
		i.h = GadgetHeight(g)
		items.AddLast i
		Update
	End Method
	
	Method Update()
		Local w:Int = ClientWidth(parent) / cols
		Local h:Int = ClientHeight(parent) / rows
		For Local i:TGridIntItem = EachIn items
			Local gw:Int = Min(i.w, w)
			Local gh:Int = Min(i.h, h)
			Local gx:Int = (i.x+0.5)*w- gw/2, gy:Int = (i.y+0.5)*h- gh/2
			SetGadgetShape i.gadget, gx, gy, gw, gh
			EmitEvent TEvent.Create(TLayout.resize_event, i.gadget)
		Next
	End Method
	
	' Creates a layout for gadget, allows optional gaps
	Function Create:TGridIntLayout(g:TGadget, cols:Int, rows:Int)
		Local l:TGridIntLayout = New TGridIntLayout
		l.SetParent g
		l.rows = rows
		l.cols = cols
		Return l
	End Function
	
End Type

Type TGridIntItem
	
	Field gadget:TGadget
	
	Field x:Int, y:Int, w:Int, h:Int
	
End Type
