; ID: 2541
; Author: WildCat
; Date: 2009-07-22 14:46:20
; Title: Simple Proportional Font Routine
; Description: Font routine for further uses

Type WSymbol
	Field x
	Field y
	Field w
	Field h
	Field ox
	Field oy
	Field xa
	Field page
End Type

Type WFont
	Field pages[10]
	Field symbols[256]
	Field name$
End Type

Function WSymbolNew.WSymbol (x, y, w, h, ox, oy, xa, page)
	sym.WSymbol= New WSymbol
	sym\x = x
	sym\y = y
	sym\w = w
	sym\h = h
	sym\ox = ox
	sym\oy = oy
	sym\xa = xa
	sym\page = page
	
	Return (sym)
End Function

Function WFontNew.WFont (filename$)
	font.WFont = New WFont
	
	font\name$ = filename$
	
	If fontDir$ <> "" Then
		filename$ = fontDir$ + filename$
	EndIf
	
	file = ReadFile (filename$)
	a$ = ReadLine (file)
	a$ = ReadLine (file)
	
	
	b = Instr (a$, "pages=")
	pages = Mid (a$, b+6)
	
	If Left$(filename$,2) = ".." Then 
		beginFrom = 3
	Else
		beginFrom = 1
	EndIf
	
	c = Instr (filename$, ".", beginfrom)
	basename$ = Mid (filename$, 1, c-1)
	
	For b = 1 To pages
		nam$ = b-1
		While Len(nam$) < 2 
			nam$ = "0"+nam$
		Wend
		nam$ = basename$+"_"+nam$+".png"
		
		;If fontDir$ <> "" Then nam$ = fontDir$ + nam$
		
		font\pages[b] = LoadImage (nam$)
		DebLog("Page Load: "+nam$+": handle="+font\pages[b])
	Next
	
	While Eof (file) = 0 
		a$ = ReadLine (file)
		
		sp = Instr (a$, " ")
		If sp Then 
			fw$ = Mid (a$, 1, sp-1)
			If fw$ = "kerning" Then Goto wfontcycend
			If fw$ = "mask" Then
				sp2 = Instr (a$, " ", sp+1)
				sp3 = Instr (a$, " ", sp2+1)
				
				red = Mid (a$, sp+1, sp2-sp)
				green = Mid (a$, sp2+1, sp3-sp2)
				blue = Mid (a$, sp3+1)
				
				For b = 1 To pages 
					MaskImage (font\pages[b], red, green, blue)
				Next
				
				Goto wfontcycend
			EndIf
		EndIf
		
		b = Instr (a$, "id=")
		If b = 0 Then Goto wfontcycend
		
		c = Instr (a$, " ", b)
		id = Mid (a$, b+3, c-b-3)
		
		b = Instr (a$, "x=",c)
		c = Instr (a$, " ", b)
		x = Mid (a$, b+2, c-b-2)
		
		b = Instr (a$, "y=",c)
		c = Instr (a$, " ", b)
		y = Mid (a$, b+2, c-b-2)		
		
		b = Instr (a$, "width=",c)
		c = Instr (a$, " ", b)
		w = Mid (a$, b+6, c-b-6)		
		
		b = Instr (a$, "height=",c)
		c = Instr (a$, " ", b)
		h = Mid (a$, b+7, c-b-7)		
		
		b = Instr (a$, "xoffset=",c)
		c = Instr (a$, " ", b)
		ox = Mid (a$, b+8, c-b-8)		
		
		b = Instr (a$, "yoffset=",c)
		c = Instr (a$, " ", b)
		oy = Mid (a$, b+8, c-b-8)		
		
		b = Instr (a$, "xadvance=",c)
		c = Instr (a$, " ", b)
		xa = Mid (a$, b+9, c-b-9)		
		
		b = Instr (a$, "page=",c)
		page = Mid (a$, b+5)		
		
		font\symbols[id+1] = Handle (WSymbolNew(x, y, w, h, ox, oy, xa, page))
		
		.wfontcycend
	Wend
	
	Return (font)
End Function

Function WFontText (font.WFont, s$, x, y)
	For a = 1 To Len(s$)
		b = Asc (Mid(s$, a, 1))
		If font\symbols[b+1] Then
			sym.WSymbol = Object.WSymbol(font\symbols[b+1])
			DrawImageRect (font\pages[sym\page+1], x+sym\ox, y+sym\oy, sym\x, sym\y, sym\w, sym\h)			
			x = x + sym\xa
		EndIf
	Next
End Function

Function WFontWidth (font.WFont, s$)
	w = 0
	For a = 1 To Len(s$)
		b = Asc (Mid(s$, a, 1))
		If font\symbols[b+1] Then
			sym.WSymbol = Object.WSymbol(font\symbols[b+1])
			w = w + sym\xa
		EndIf
	Next
	Return w
End Function

Function WFontHeight (font.WFont, s$)
	h = 0
	For a = 1 To Len(s$)
		b = Asc (Mid(s$, a, 1))
		If font\symbols[b+1] Then
			sym.WSymbol = Object.WSymbol(font\symbols[b+1])
			If sym\oy+sym\h > h Then h = sym\oy+sym\h
		EndIf
	Next
	Return h		
End Function

Function WFontByName.WFont (name$)
	For font.WFont = Each WFont
		If font\name$ = name$ Then Return font
	Next
	Return Null
End Function

Function WFontSpare (font.WFont, s$, width, atSpace = True)
	w = 0
	For a = 1 To Len(s$)
		b = Asc (Mid(s$, a, 1))
		If font\symbols[b+1] Then
			sym.WSymbol = Object.WSymbol(font\symbols[b+1])
			wc = w + sym\ox + sym\w + sym\xa ;strange formula, but everything is clear this way :)
			w = w + sym\xa
			If wc > width Then 
				If atSpace Then
					b = a
					Repeat
						If Mid(s$, b, 1) = " " Then Return b+1
						b = b - 1
						If b = 0 Then Return WFontSpare (font, s$, width, False)
					Forever
					Return a
				Else
					Return a
				EndIf
			EndIf
		EndIf
	Next	
	
	Return 0
End Function

Function WFontFree (font.WFont)
	For page = 1 To 10
		If font\pages[page] Then FreeImage (font\pages[page])
	Next
	
	For a = 1 To 256
		If font\symbols[a] Then
			sym.WSymbol = Object.WSymbol(font\symbols[b+1])
			Delete sym	
		EndIf
	Next
	
	Delete font
End Function

Function WFontsFree ()
	For font.WFont = Each WFont
		WFontFree(font)
	Next
End Function
