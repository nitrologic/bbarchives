; ID: 2223
; Author: Ion
; Date: 2008-02-26 11:09:38
; Title: Win32 Color Picker Dialog
; Description: Shows a color picker where the user can choose a color

'########################################
'## Win32 Color-Picker Dialog
'## Copyright © E.Sandberg (Ion), 2008
'## Free for public use.
'########################################

Import "-lcomdlg32"
Import Pub.Win32

Type TColor
	Field r:Byte,g:Byte,b:Byte
EndType

Type TCHOOSECOLOR
	Field lStructSize:Int
	Field hwndOwner:Int
	Field hInstance:Int
	Field rgbResult:Int
	Field lpCustColors:Long[]
	Field flags:Int
	Field lCustData:Int
	Field lpfnHook:Int
	Field lpTemplateName:Byte
EndType

Const CC_ANYCOLOR:Int = 256
Const CC_FULLOPEN:Int = 2

Extern "Win32"
	Function ChooseColorA(typ:Byte Ptr)
EndExtern

Function ShowColorDialog:TColor()
	Local ColorDialog:TCHOOSECOLOR = New TCHOOSECOLOR
	Local pos:Byte Ptr = ColorDialog
	Local aColorRef:Long[16]
	For i:Int = 0 To 15
		aColorRef[i] = 0
	Next
	ColorDialog.hwndOwner = Null
	ColorDialog.hInstance = Null
	ColorDialog.rgbResult = 0
	ColorDialog.lpCustColors = aColorRef
	ColorDialog.flags = CC_ANYCOLOR | CC_FULLOPEN
	ColorDialog.lCustData = Null
	ColorDialog.lStructSize = 36
	ChooseColorA(ColorDialog)
	Local col:TColor = New TColor
	col.r = ColorDialog.rgbResult
	col.g = ColorDialog.rgbResult Shr 8
	col.b = ColorDialog.rgbResult Shr 16
	Return col
EndFunction
