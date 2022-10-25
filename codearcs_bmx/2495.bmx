; ID: 2495
; Author: Oddball
; Date: 2009-06-03 09:51:08
; Title: FilterGraphicsModes
; Description: Filters an array of TGraphicsMode types

Function FilterGraphicsModes:TGraphicsMode[]( gfxModes:TGraphicsMode[], minWidth:Int, maxWidth:Int, minHeight:Int, maxHeight:Int, minDepth:Int=0, maxDepth:Int=32, minHertz:Int=60, maxHertz:Int=120 )
	Local gmCount:Int=0
	For Local gm:TGraphicsmode=EachIn gfxModes
		If gm.width>=minWidth And gm.width<=maxWidth And gm.height>=minHeight And gm.height<=maxHeight And gm.depth>=minDepth And gm.depth<=maxDepth And gm.hertz>=minHertz And gm.hertz<=maxHertz
			gfxModes[gmCount]=gm
			gmCount:+1
		EndIf
	Next
	If gmCount=0 Return Null
	Return gfxModes[..gmCount]
End Function


'Function test----------------------------------
For Local gm:TGraphicsMode=EachIn FilterGraphicsModes(GraphicsModes(),640,1024,480,768,32,32,60,60)
	Print gm.ToString()
Next
