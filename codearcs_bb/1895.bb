; ID: 1895
; Author: Diego
; Date: 2007-01-06 00:50:53
; Title: Lists 1.1
; Description: Draw scrollable lists, buttons and context menues in blitz (like the windows GUI)

Dim ListContent$(32768, 8)
Dim ListColors%(32768, 3, 8) ; 1 = Vorsergrund; 2 = Hintergrund; 3 = Frei
Dim ListLength%(8)

;Benutze -1 bzw. CHR$(1) um die Werte nicht zu verändern, bzw. -2 um Farbangaben zu entfernen.
Function SetItem(List%, Item%, ItemName$ = "", FontColor% = -1, BackgroundColor% = -1, Unused% = -1)
If ItemName$ <> Chr$(1) Then ListContent$(Item%, List%) = ItemName$
If FontColor% >= 0 Then ListColors%(Item%, 1, List%) = FontColor%
If FontColor% = -2 Then ListColors%(Item%, 1, List%) = -1
If BackgroundColor% >= 0 Then ListColors%(Item%, 2, List%) = BackgroundColor%
If BackgroundColor% = -2 Then ListColors%(Item%, 2, List%) = -1
If Unused% >= 0 Then ListColors%(Item%, 3, List%) = Unused%
End Function

;Benutze -1 bzw. CHR$(1) um die Werte nicht zu verändern, bzw. -2 um Farbangaben zu entfernen.
Function SetAllItems(List%, ItemName$ = "", FontColor% = -1, BackgroundColor% = -1, Unused% = -1)
For Item% = 1 To ListLength%(List%)
	If ItemName$ <> Chr$(1) Then ListContent$(Item%, List%) = ItemName$
	If FontColor% >= 0 Then ListColors%(Item%, 1, List%) = FontColor%
	If FontColor% = -2 Then ListColors%(Item%, 1, List%) = -1
	If BackgroundColor% >= 0 Then ListColors%(Item%, 2, List%) = BackgroundColor%
	If BackgroundColor% = -2 Then ListColors%(Item%, 2, List%) = -1
	If Unused% >= 0 Then ListColors%(Item%, 3, List%) = Unused%
	Next
End Function

Function ResetList(List%)
Local I%
ListLength%(List%) = 0
For I% = 1 To 32768
	ListContent$(I%, List%) = ""
	ListColors%(I%, 1, List%) = 0
	ListColors%(I%, 2, List%) = 0
	ListColors%(I%, 3, List%) = 0
	Next
End Function

Function AddItem(List%, ItemName$, FontColor% = -1, BackgroundColor% = -1, Unused% = 0)
ListLength%(List%) = ListLength%(List%) + 1
ListContent$(ListLength%(List%), List%) = ItemName$
ListColors%(ListLength%(List%), 1, List%) = FontColor%
ListColors%(ListLength%(List%), 2, List%) = BackgroundColor%
ListColors%(ListLength%(List%), 3, List%) = Unused%
End Function

Function DeleteItem(List%, Item% = -1)
Local I%
If Item% = -1 Then Item% = ListLength%(List%)
ListLength%(List%) = ListLength%(List%) - 1
For I% = Item% To ListLength%(List%)
	ListContent$(I%, List%) = ListContent$(I% + 1, List%)
	ListColors%(I%, 1, List%) = ListColors%(I% + 1, 1, List%)
	ListColors%(I%, 2, List%) = ListColors%(I% + 1, 2, List%)
	ListColors%(I%, 3, List%) = ListColors%(I% + 1, 3, List%)
	Next
ListContent$(I%, List%) = ""
ListColors%(I%, 1, List%) = 0
ListColors%(I%, 2, List%) = 0
ListColors%(I%, 3, List%) = 0
End Function

Function CreateColorBank(Hintergrund%, Vordergrund% = 0)
Local ColorBank% = CreateBank(48)
PokeInt ColorBank%, 0, Vordergrund% ;FRONT-Color
PokeInt ColorBank%, 4, Hintergrund% ;SCROLLBAR-FACE-Color
PokeInt ColorBank%, 8, (Hintergrund% And $FEFEFE) Shr 1 ;SCROLLBAR-TRACK-Color
PokeInt ColorBank%, 12, $FFFFFF - Hintergrund% ;SCROLLBAR-ARROW-Color
PokeInt ColorBank%, 16, $FFFFFF - ($FFFFFF - Hintergrund% And $FCFCFC) Shr 2 ;SCROLLBAR-HIGHLIGHT-Color
PokeInt ColorBank%, 20, $FFFFFF - ($FFFFFF - Hintergrund% And $FEFEFE) Shr 1 ;SCROLLBAR-3DLIGHT-Color
PokeInt ColorBank%, 24, (Hintergrund% And $FEFEFE) Shr 1 ;SCROLLBAR-SHADOW-Color
PokeInt ColorBank%, 28, (Hintergrund% And $FCFCFC) Shr 2 ;SCROLLBAR-DARKSHADOW-Color
PokeInt ColorBank%, 32, -1 ;MouseOverFontColor
PokeInt ColorBank%, 36, -1 ;MouseOverBackgroundColor
PokeInt ColorBank%, 40, -1 ;SelectedFontColor
PokeInt ColorBank%, 44, -1 ;SelectedBackgruondColor
Return ColorBank%
End Function

Function CreateUserColorBank(FontColor%, BackgroundColor%, Trackcolor%, Arrowcolor%, HighlightColor%, LightCol%, ShadowColor%, DarkShadowColor%, MouseOverFontColor% = -1, MouseOverBackgroundColor% = -1, SelectedFontColor% = -1, SelectedBackgruondColor% = -1)
Local ColorBank% = CreateBank(48)
PokeInt ColorBank%, 0, FontColor% ;FRONT-Color
PokeInt ColorBank%, 4, BackgroundColor% ;SCROLLBAR-FACE-Color
PokeInt ColorBank%, 8, Trackcolor% ;SCROLLBAR-TRACK-Color
PokeInt ColorBank%, 12, Arrowcolor% ;SCROLLBAR-ARROW-Color
PokeInt ColorBank%, 16, HighlightColor% ;SCROLLBAR-HIGHLIGHT-Color
PokeInt ColorBank%, 20, LightCol% ;SCROLLBAR-3DLIGHT-Color
PokeInt ColorBank%, 24, ShadowColor% ;SCROLLBAR-SHADOW-Color
PokeInt ColorBank%, 28, DarkShadowColor% ;SCROLLBAR-DARKSHADOW-Color
PokeInt ColorBank%, 32, MouseOverFontColor%
PokeInt ColorBank%, 36, MouseOverBackgroundColor%
PokeInt ColorBank%, 40, SelectedFontColor%
PokeInt ColorBank%, 44, SelectedBackgruondColor%
Return ColorBank%
End Function

Function GetFocus(X%, Y%, Width%, Height%, List%, Offset%)
Local Focus%
If MouseX() => X% And MouseX() <= X% + Width% And MouseY() >= Y% And MouseY() <= Y% + Height% Then Focus% = (MouseY() - Y% + FontHeight() - 2) / FontHeight() + Offset% Else Return 0
If Focus% <= ListLength%(List%) Then Return Focus% Else Return 0
End Function

Function ListOffset(X%, Y%, Width%, Height%, List%, Offset%)
Local LineHeight% = FontHeight(), Xleiste% = Width% - LineHeight% - 2, ScrollbarY%
Local MX% = MouseX() - X%, MY% = MouseY() - Y%, MB% = GetMouse()
If ListLength%(List%) * LineHeight% <= Height% Then Return Offset%
Offset% = Offset% - MouseZSpeed()
If MX% > Xleiste% And MX% < Width% Then
	If MB% <> 0 Then
		If MY% > 2 And MY% < 2 + LineHeight% Then Offset% = Offset% - MB% ^ 2
		If MY% > Height% - LineHeight% - 2 And MY% < Height% - 2 Then Offset% = Offset% + MB% ^ 2
		EndIf
	If MouseDown(1) And MY% > 2 + LineHeight% And MY% < Height% - LineHeight% - 2 Then Offset% = (MY% - 2 - LineHeight%) * ListLength%(List%) / (Height% - LineHeight% - 2)
	EndIf
If Offset% < 0 Then Offset% = 0
If Offset% > ListLength%(List%) - (Height% - 6) / LineHeight% Then Offset% = ListLength%(List%) - (Height% - 6) / LineHeight% 
Return Offset%
End Function

Function DrawList(X%, Y%, Width%, Height%, ColorBank%, List%, Offset% = 0, Deep% = 1)
If Deep% <> 0 Then Deep% = 1
Local LineHeight% = FontHeight(), I%, MouseOverElement%, BackupColor%, BackupBackground%
Origin X%, Y%
DrawBox 0, 0, Width%, Height%, ColorBank%, Deep%
If PeekInt(ColorBank%, 32) <> -1 Or PeekInt(ColorBank%, 36) <> -1 Then ; OnMouseOver-Effekt
	MouseOverElement% = GetFocus(X%, Y%, Width%, Height%, List%, Offset%)
	BackupColor% = ListColors%(MouseOverElement%, 1, List%)
	BackupBackground% = ListColors%(MouseOverElement%, 2, List%)
	ListColors%(MouseOverElement%, 1, List%) = PeekInt(ColorBank%, 32)
	ListColors%(MouseOverElement%, 2, List%) = PeekInt(ColorBank%, 36)
	EndIf
If ListLength%(List%) * LineHeight% > Height% Then ; Zeichne Scrollbalken
	HeightPerLine# = Float(Height% - LineHeight% * 2 - 6) / ListLength%(List%)
	DrawBox Width% - LineHeight% - 2, 2, LineHeight, LineHeight%, ColorBank%, Not Deep%
	DrawBox Width% - LineHeight% - 2, Height% - LineHeight% - 2, LineHeight%, LineHeight%, ColorBank%, Not Deep%
	ScrollbarY% = LineHeight% + 3 + HeightPerLine# * Offset%
	If ScrollbarY% + HeightPerLine# * Height% / LineHeight% - 1 > Height% - LineHeight% Then ScrollbarY% = Height% - LineHeight% - HeightPerLine# * Height% / LineHeight%
	DrawBox Width% - LineHeight% - 2, ScrollbarY%, LineHeight%, HeightPerLine# * Height% / LineHeight% - 3, ColorBank%, Not Deep%
	Color 0, 0, PeekInt(ColorBank%, 12)
	DrawPolygon Width% - LineHeight% / 2 - 2, 2 + LineHeight% * 3 / 8, LineHeight% / 4
	DrawPolygon Width% - LineHeight% / 2 - 2, Height% - LineHeight% * 5 / 8 - 2, LineHeight% / 4, 1
	Viewport X% + 3, Y% + 3, Width% - 6 - LineHeight%, Height% - 6
	Else
	Viewport X% + 3, Y% + 3, Width% - 6, Height% - 6
	EndIf
For I% = 0 To Height% / LineHeight% ; Liste Zeichnen
	If I% + 1 + Offset% > ListLength%(List%) Then Exit
	If ListColors%(I% + 1 + Offset%, 2, List%) <> -1 Then
		Color 0, 0, ListColors%(I% + 1 + Offset%, 2, List%)
		Rect 3, 3 + I% * LineHeight%, Width% - 6, LineHeight%
		EndIf
	If ListColors%(I% + 1 + Offset%, 1, List%) <> -1 Then Color 0, 0, ListColors%(I% + 1 + Offset%, 1, List%) Else Color 0, 0, PeekInt(ColorBank%, 0)
	Text 3, 3 + I% * LineHeight%, ListContent$(I% + 1 + Offset%, List%)
	Next
If PeekInt(ColorBank%, 32) <> -1 Or PeekInt(ColorBank%, 36) <> -1 Then ; OnMouseOver-Effekt zurücksetzen
	ListColors%(MouseOverElement%, 1, List%) = BackupColor%
	ListColors%(MouseOverElement%, 2, List%) = BackupBackground%
	EndIf
Viewport 0, 0, GraphicsWidth(), GraphicsHeight()
Origin 0, 0
End Function

Function DrawBox(X%, Y%, Width%, Height%, ColorBank%, Deep% = 1)
Color 0, 0, PeekInt(ColorBank%, 4) ; Background
Rect X%, Y%, Width%, Height%, 1
Color 0, 0, PeekInt(ColorBank%, 16 + Deep% * 12) ; Border
Line X%, Y%, X% + Width%, Y%
Line X%, Y%, X%, Y% + Height% - 1
Color 0, 0, PeekInt(ColorBank%, 20 + Deep * 4)
Line X% + 1, Y% + 1, X% + Width% - 1, Y% + 1
Line X% + 1, Y% + 1, X% + 1, Y% + Height% - 2
Color 0, 0, PeekInt(ColorBank%, 24 - Deep * 4)
Line X% + Width% - 1, Y% + 2, X% + Width% - 1, Y% + Height% - 1
Line X% + 1, Y% + Height% - 1, X% + Width% - 1, Y% + Height% - 1
Color 0, 0, PeekInt(ColorBank%, 28 - Deep * 12)
Line X% + Width% , Y% + 1, X% + Width%, Y% + Height%
Line X%, Y% + Height%, X% + Width%, Y% + Height%
End Function

Function DrawPolygon(X%, Y%, Height%, Down = 0)
Local I%
If Down% Then
	For I% = 1 To Height% - 1
		Line X% - Height% + I%, Y% + I%, X% + Height% - I%, Y% + I%
		Next
	Plot X%, Y% + Height%
	Else
	Plot X%, Y%
	For I% = 1 To Height% - 1
		Line X% - I%, Y% + I%, X% + I%, Y% + I%
		Next
	EndIf
End Function

Function DrawButton(X%, Y%, Width%, Height%, ColorBank%, ButtonText$)
Local Set%
If MouseDown(1) And MouseX() >= X% And MouseX() <= X% + Width% And MouseY() >= Y% And MouseY() < Y% + Height% Then Set% = 1 Else Set% = 0
DrawBox X%, Y%, Width%, Height%, ColorBank%, Set%
Color 0, 0, PeekInt(ColorBank%, 0)
Text X% + Width% / 2 + Set%, Y% + Height% / 2 + Set%, ButtonText$, 1, 1
Return Set%
End Function

Function ContextMenue(List%, ColorBank%, Deep% = 0)
Local I%, ItemWidth%, MenueWidth% = 1, MenueHeight% = FontHeight() * ListLength%(List%) + 6, X% = MouseX(), Y% = MouseY()
For I% = 1 To ListLength%(List%)
	ItemWidth% = StringWidth(ListContent$(I%, List%))
	If ItemWidth% > MenueWidth% Then MenueWidth% = ItemWidth%
	Next
MenueWidth% = MenueWidth% + 6
If X% + MenueWidth% > GraphicsWidth() Then X% = GraphicsWidth() - MenueWidth% - 1
If Y% + MenueHeight% > GraphicsHeight() Then Y% = GraphicsHeight() - MenueHeight% - 1
For I% = 1 To 10
	DrawBox X%, Y%, MenueWidth% * I% / 20, MenueHeight% * I% / 20, ColorBank%, Deep%
	Flip
	Next
While Not MouseDown(1)
	DrawList X%, Y%, MenueWidth%, MenueHeight%, ColorBank%, List%, 0, Deep%
	Flip
	Wend
Return GetFocus(X%, Y%, MenueWidth%, MenueHeight%, List%, 0)
End Function
