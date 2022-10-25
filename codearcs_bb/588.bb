; ID: 588
; Author: Jim Teeuwen
; Date: 2003-02-14 17:11:39
; Title: Splitter Gadget
; Description: A gadget that allows realtime resizing of gadets with a simple mousedrag

;//
;// Name	: Splitter Control
;// Desc	: BlitzPlus extension,
;// 		  A Splitter can simultaniously scale 2 controls,
;// 		  By simply dragging the splitter back and forth.
;// 		  It serves for resizing purposes, and can be applied for
;// 		  Both horizontal and vertical resizing.
;// Author	: Jim Teeuwen / Defiance
;// 		  http://www.transmuter.nl :: blitz@transmuter.nl
;//
;// 		  (c) 2002-2004, Jim Teeuwen. All rights reserved.
;//

Type Splitter
	Field Gadget
	Field FirstControl, SecondControl
	Field Alignment
	Field Group
	Field Size
	Field Canvas
End Type

Const SPLITTER_ALIGN_VERTICAL		= 0
Const SPLITTER_ALIGN_HORIZONTAL		= 1


Function CreateSplitter.Splitter(Group, PrimaryGadget, SecundaryGadget, Alignment = SPLITTER_ALIGN_VERTICAL, Size = 3)

	spl.Splitter		= New Splitter
	spl\FirstControl	= PrimaryGadget 
	spl\SecondControl	= SecundaryGadget
	spl\Alignment		= Alignment
	spl\Group			= Group
	spl\Size			= Size
	spl\Gadget			= CreatePanel(0, 0, spl\Size, ClientHeight(Group), Group, 0)
	
	If(spl\Alignment = SPLITTER_ALIGN_VERTICAL) Then
		SetGadgetLayout(spl\gadget, 1, 1, 1, 1)
	Else
		SetGadgetLayout(spl\gadget, 1, 1, 0, 0)
	End If
	
	spl\Canvas			= CreateCanvas(0, 0, GadgetWidth(spl\Gadget), GadgetHeight(spl\Gadget), spl\Gadget)
		SetGadgetLayout(spl\Canvas, 1, 1, 1, 1)
		SetBuffer(CanvasBuffer(spl\Canvas))
		Color(100, 100, 112)
		Rect(0, 0, GadgetWidth(spl\Gadget), GadgetHeight(spl\Gadget))
	
	UpdateSplitter(spl)
	
	If(spl\SecondControl <> 0) Then
		If(spl\Alignment = SPLITTER_ALIGN_VERTICAL) Then
			SetGadgetShape(spl\SecondControl, GadgetX(spl\SecondControl) + spl\Size, GadgetY(spl\SecondControl), GadgetWidth(spl\SecondControl) - spl\Size, GadgetHeight(spl\SecondControl))
		Else
			SetGadgetShape(spl\SecondControl, GadgetX(spl\SecondControl), GadgetY(spl\SecondControl) + spl\Size, GadgetWidth(spl\SecondControl), GadgetHeight(spl\SecondControl) - spl\Size)
		End If
	End If
	
	Return spl

End Function

Function UpdateSplitter(spl.Splitter)

	If(spl\FirstControl = 0) Then
		If(spl\Alignment = SPLITTER_ALIGN_VERTICAL) Then
			SetGadgetShape(spl\Gadget, 0, 0, spl\Size, ClientHeight(spl\Group))
		Else
			SetGadgetShape(spl\Gadget, 0, 0, ClientWidth(spl\Group), 3)
		End If
	Else
		If(spl\Alignment = SPLITTER_ALIGN_VERTICAL) Then
			SetGadgetShape(spl\Gadget, GadgetWidth(spl\FirstControl), GadgetY(spl\FirstControl), spl\Size, GadgetHeight(spl\FirstControl))
		Else
			SetGadgetShape(spl\Gadget, GadgetX(spl\FirstControl), GadgetHeight(spl\FirstControl), GadgetWidth(spl\FirstControl), spl\Size)
		End If
	End If

End Function

Function SplitterEventLoop(spl.Splitter)

	If(MouseDown(1)) Then	
		offsetX					= EventX()
		offsetY					= EventY()

		If(spl\Alignment = SPLITTER_ALIGN_VERTICAL) Then
			If((GadgetWidth(spl\FirstControl) + offsetX) < (ClientWidth(spl\Group) - spl\Size) And (GadgetWidth(spl\FirstControl) + offsetX) > 0) Then
				SetGadgetShape(spl\FirstControl, GadgetX(spl\FirstControl), GadgetY(spl\FirstControl), (GadgetWidth(spl\FirstControl) + offsetX), GadgetHeight(spl\FirstControl))
				SetGadgetShape(spl\Gadget, GadgetWidth(spl\FirstControl), GadgetY(spl\FirstControl), spl\Size, GadgetHeight(spl\FirstControl))
				SetGadgetShape(spl\SecondControl, GadgetX(spl\Gadget) + spl\Size, GadgetY(spl\SecondControl), GadgetWidth(spl\SecondControl) - offsetX, GadgetHeight(spl\SecondControl))
			End If
		Else
			If((GadgetHeight(spl\FirstControl) + offsetY) < (GadgetHeight(spl\Group) - spl\Size) And (GadgetHeight(spl\FirstControl) + offsetY) > 0) Then
				SetGadgetShape(spl\FirstControl, GadgetX(spl\FirstControl), GadgetY(spl\FirstControl), GadgetWidth(spl\FirstControl), (GadgetHeight(spl\FirstControl) + offsetX))
				SetGadgetShape(spl\Gadget, GadgetX(spl\FirstControl), GadgetHeight(spl\FirstControl), GadgetWidth(spl\FirstControl), spl\Size)
				SetGadgetShape(spl\SecondControl, GadgetX(spl\SecondControl), (GadgetY(spl\Gadget) + spl\Size), GadgetWidth(spl\SecondControl), GadgetHeight(spl\SecondControl) - offsetX)
			End If
		End If
	End If

	If(spl\Alignment = SPLITTER_ALIGN_VERTICAL) Then
		SetCursor(LoadCursor(0, IDC_SIZEWE))
	Else
		SetCursor(LoadCursor(0, IDC_SIZENS))
	End If
	
	UpdateSplitter()

End Function


;// ### DEMO #####################################################################################

SplitterDemo()
Global mySplitter.Splitter

;/* Window Messages */
Const WM_MOUSEMOVE				= $203
Const WM_RESIZE					= $802
Const WM_CLOSE					= $803

Const IDC_SIZENONE				= 32641		;// No Cursor
Const IDC_SIZENWSE				= 32642		;// Top left to bottom right
Const IDC_SIZENESW				= 32643		;// Top right to bottom left
Const IDC_SIZEWE				= 32644		;// Left to right
Const IDC_SIZENS				= 32645		;// Top to Bottom
Const IDC_SIZEMOVE				= 32646		;// Cross (4 directions)
Const IDC_SIZEDENIED			= 32648		;// Circle with diagonal Line
Const IDC_SIZEHAND				= 32649		;// Hand cursor
Const IDC_SIZEHOURGLASS			= 32650		;// Hourglass
Const IDC_SIZEHELP				= 32651		;// QuestionMark
Const IDC_SIZEMOVENS			= 32652		;// Move Top to Bottom
Const IDC_SIZEMOVEWE			= 32653		;// Move Left to right
Const IDC_SIZEMOVENSEW			= 32654		;// Move 4 directions
Const IDC_SIZEMOVEN				= 32655		;// Move Up
Const IDC_SIZEMOVES				= 32656		;// Move Down
Const IDC_SIZEMOVEW				= 32657		;// Move Left
Const IDC_SIZEMOVEE				= 32658		;// Move Right
Const IDC_SIZEMOVENW			= 32659		;// Move Top Left
Const IDC_SIZEMOVENE			= 32660		;// Move Top Right
Const IDC_SIZEMOVESW			= 32661		;// Move Bottom Left
Const IDC_SIZEMOVESE			= 32662		;// Move Bottom Right
Const IDC_SIZECD				= 32663		;// CD loading Icon

Function SplitterDemo()

	;// Create a new form with some panels on it.
	;// We will use the splitter control to be able to resize these panels
	;// at runtime. The splitter will appear as a darkgray 3-pixel wide line between 2 panels
	;// To use it., simply click on the splitter and drag it left/right(vertically aligned splitter)
	;// or up/down (horizontally aligned Splitter)

	;/* frmMain */
	frmMain		= CreateWindow("UI Test", 20, 20, ClientWidth(Desktop()) - 40, ClientHeight(Desktop()) - 40, 0)
		SetMinWindowSize(frmMain, 320, 240)
		SetGadgetText(frmMain, "Splitter Control Demo")
		
	;/* Workspace */
	pnlWorkSpace	= CreatePanel(0, 0, ClientWidth(frmMain) - 193, ClientHeight(frmMain) , frmMain, 0)
		SetGadgetLayout(pnlWorkSpace, 1, 1, 1, 1)
		SetGadgetText(pnlWorkSpace, "WorkSpace")
		SetPanelColor(pnlWorkSpace, 136, 147, 158)
	
	;/* pnlProperties *.
	pnlProperties	= CreatePanel(GadgetWidth(frmMain) - 200, 0, 200, GadgetHeight(frmMain), frmMain, 0)
		SetGadgetLayout(pnlProperties, 0, 1, 1, 1)
		SetGadgetText(pnlProperties, "Properties")
	
	trvProject		= CreateTreeView(0, 0, GadgetWidth(pnlProperties) - 8, GadgetHeight(pnlProperties) / 3, pnlProperties)
		SetGadgetLayout(trvProject, 1, 1, 1, 0)
		
	;/* Splitter */
	;// Here we create the splitter. 
	;// This splitter will 'split' the 2 panels 'pnlWorkSpace' and 'pnlProperties'
	;// Normally we would add this splitter gadget after the first panel, but we need a valid
	;// reference to the second Properties panel for this to work. Thus, we create the splitters
	;// AFTER ALL THE PANELS ARE CREATED!
	;// This splitter will be Vertically aligned
	;// Calling the create method takes a few parameters:
	;//
	;// 	spl = CreateSplitter(group, firstControl, secondControl, alignment)
	;//
	;// 	group			: This is the group to wich the splitter is added. works the same as
	;// 					  adding a normal gadget.
	;// 	firstControl	: This is the first gadget to wich the splitter will apply.
	;// 					  In our case this is 'pnlWorkSpace'
	;// 	secondControl	: Naturally, this is the second gadget to wich the splitter applies
	;// 					  You see now why we need to wait with splitter creation till ALL
	;// 					  the regular controls have been initialised.
	;// 	Alignment		: This is the splitter alignment. Can be either SPLITTER_ALIGN_VERTICAL (default),
	;// 					  or SPLITTER_ALIGN_HORIZONTAL. WIth the Vertical splitter, your 2 gadgets
	;// 					  to wich it applies should be left/right of eachother
	;// 					  With the Horizontal splitter, your 2 gadgets to wich the splitter applies
	;// 					  should be above/underneath eachother.
	;//
	;// You do not have to care about the positioning of the 2 gadgets, this will all be handled
	;// by the Splitter code.
	mySplitter.Splitter	= CreateSplitter(frmMain, pnlWorkSpace, pnlProperties, SPLITTER_ALIGN_VERTICAL)
	
	
	;// now we enter the main eventloop for the form and check for messages
	;// The messages we want to handle for the splitter are:
	;// WM_RESIZE and WM_MOUSEMOVE
	Repeat
	
		Select(WaitEvent())
			Case WM_CLOSE		;// $803 Form close event
				End
			
			Case WM_RESIZE		;// $802
				;// Update the splitter
				UpdateSplitter(mySplitter)
	
			Case WM_MOUSEMOVE	;// $203
				;// Handle the movement
				;// Note that only the Canvas gadget will detect MouseMove events, so I
				;// added a special Canvas that streches the entire surface of the splitter
				;// This will detect the Mouseevents for us. we have to determine here of the
				;// mousemove event was fired by our splitter
				Select(EventSource())
					Case mySplitter\Canvas
						SplitterEventLoop(mySplitter)

				End Select
			
		End Select
	
	Forever
	
	;// Ready! Now start the app and notice the small darkgray line between the 2 panels
	;// just click on it and drag the line Left/right while holding the mousebutton to see
	;// the Splitter in action!

End Function
