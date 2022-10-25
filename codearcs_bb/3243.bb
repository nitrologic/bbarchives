; ID: 3243
; Author: marksibly_v2
; Date: 2016-01-12 16:44:21
; Title: test
; Description: test

Method SetIconStrip(pStrip:TIconStrip)
		?Debug
		If pStrip=Null Throw "Icon Strip is Null."
		?
		' pixmaps from the Icon Strip
		Self.pixmaps = New TPixmap[pStrip.Count]
		For Local i:Int = 0 Until Self.pixmaps.length
			Self.pixmaps[i] = pStrip.ExtractIconPixmap(i)
		Next
		' update the panels with the new fresh Icons
		Self._updateitems(True)
	EndMethod
