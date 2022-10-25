; ID: 2638
; Author: Nilium
; Date: 2010-01-11 02:13:11
; Title: Vertical Tab Strip
; Description: Quicky vertical tab strip code for no good reason

SuperStrict

Import "color.bmx"
Import "animation.bmx"

Function WithinRect:Int(px!, py!, rx!, ry!, rw!, rh!)
	Return (px >= rx And py >= ry And px < rx+rw And py < ry+rh)
End Function

Type Tab
	Field name$				' Read-write before addition to tabstrip, read-only after
	Field color:TColor
	
	' Private
	Field _alpha!
	Field _index:Int
	
	Method Init:Tab(name$, red%=-1, green%=-1, blue%=-1)
		If red > -1 And green > -1 And blue > -1 Then
			color.InitWithIntComponents(red, green, blue, 255)
		EndIf
		Self.name = name
		Return Self
	End Method
	
	Method New()
		color = New TColor.InitWithHSV(Rnd(360!), Rnd(0.8!, 1!), Rnd(0.8!, 1!))
	End Method
End Type

Type TabStrip
	Field x!=0, y!=0				' read-write
	Field width!=12					' read-only
	Field selected:Tab = Null		' read-only
	
	'Private
	Field tabs:TList = New TList
	Field clickedIndex:Int = -1		' index of the tab clicked on - used for handling mouse up/down events
	
	Field backr!=32, backg!=32, backb!=32	' background color
	
	Method Height:Int()
		Return tabs.Count()*24
	End Method
	
	Method Render()
		Const th% = 24
		
		Local last:Int = tabs.Count()
		
		SetHandle(0,0)
		SetScale(1,1)
		SetRotation(0)
		
		SetAlpha(1)
		SetLineWidth(2)
		SetBlend(ALPHABLEND)
		
		' Outline
		
		SetColor(0,0,0)
		For Local t:Tab = EachIn tabs
			If t.name = "" Then Continue
			Local ty! = y+t._index*24
			Local tw! = width + 18*t._alpha
			DrawOval(x+tw-11.5, ty-2, 24, th+4)
			DrawRect(x,ty-2,tw+2,28)
		Next
		
		' End Outline
		
		If selected Then
			SetColor(backr, backg, backb)
			DrawRect(x, y, 12, last*24)
		EndIf
		
		Local tx! = x
		If selected then
			tx :+ 12
		EndIf
		
		For Local t:Tab = EachIn tabs
			If t.name = "" Then Continue
			Local ty! = y+t._index*24
			Local tw! = width + 18*t._alpha
			
			If t = selected Then
				SetColor(backr, backg, backb)
			Else
				Local tt!=32*(1!-t._alpha)
				SetColor(backr*t._alpha+tt, backg*t._alpha+tt, backb*t._alpha+tt)
			EndIf
			
			If selected Then
				tw :- 12
			EndIf
			
			DrawOval(tx+tw-10, ty, 20, th)
			DrawRect(tx, ty, tw, th)
			
			If t = selected Then
				Local h!, v!
				h = t.color.GetHue()
				v = t.color.GetValue()
				If 25.5 < h And h < 203.5 And .7 < v Then
					SetColor(32, 32, 32)
				Else
					SetColor(255,255,255)
				EndIf
			Else
				SetColor(255,255,255)
			EndIf
			
			DrawText(t.name, Floor(tx+8+12*(selected=Null)+9*t._alpha + .5), Floor(ty + 12 - TextHeight(t.name)*.5 + .5))
		Next
		
		' Odd little 'dings'
		If selected And selected._alpha < 0.9995! Then ' 0.9995 = arbitrary cutoff
			SetLineWidth(6)
			Local d! = selected._alpha
			Local tx! = x + width + 9*d + 6
			Local ty! = y + selected._index*24 + 8
			SetBlend(ALPHABLEND)
			Local s!=Sin(180*d)
			SetAlpha(s)
			Local r%,g%,b%,_%
			selected.color.IntComponents(r,g,b,_)
			SetColor(r,g,b)
			DrawLine(tx + 40*d, ty, tx+8+40*d, ty, False)
			DrawLine(tx + 24*d, ty+8+13*d, tx+9+24*d, ty+12+13*d, False)
			DrawLine(tx + 24*d, ty-8-13*d, tx+9+24*d, ty-12-13*d, False)
		EndIf
		' End dings
	End Method
	
	Method AddTab(t:Tab)
		If t = Null Then Return
		t._index = tabs.Count()
		t._alpha = 0
		tabs.AddLast(t)
		width = Max(TextWidth(t.name)+28, width)
	End Method
	
	' For a separator tab, pass an empty string
	Method AddTabWithName(name$)
		AddTab(New Tab.Init(name))
	End Method
	
	Method SelectTab(index%)
		If selected And selected._index = index Then
			Return
		EndIf
		
		Local newsel:Tab
		If index >= 0 And index < tabs.Count() Then
			newsel = Tab(tabs.ValueAtIndex(index))
			If newsel.name = "" Then
				' Selecting a separator tab = noop
				Return
			EndIf
		EndIf
		
		If selected Then
			Animate(selected, "_alpha", 0, 270)
		EndIf
		
		If newsel = Null Then
			selected = Null
			Animate(Self, "backr", 32, 100)
			Animate(Self, "backg", 32, 100)
			Animate(Self, "backb", 32, 100)
			Return
		Else
			selected = newsel
			Animate(selected, "_alpha", 1, 270)
			Local r%,g%,b%,_%
			selected.color.IntComponents(r,g,b,_)
			Animate(Self, "backr", r, 100)
			Animate(Self, "backg", g, 100)
			Animate(Self, "backb", b, 100)
		EndIf
	End Method
	
	Method HandleEvent:Int(event:TEvent)
		Local mx#, my#
		GetOrigin(mx,my)
		mx :+ event.x
		my :+ event.y
		
		Select event.id
			Case EVENT_MOUSEDOWN
				If event.data <> 1 Or Not WithinRect(mx, my, x+12, y, width, 24*tabs.Count()) Then
					Return False
				EndIf
				clickedIndex = (my - Int(y)) / 24
				Return True
				
			Case EVENT_MOUSEUP
				If clickedIndex = -1 Then
					Return False
				EndIf
				
				If WithinRect(mx, my, x+12, y+24*clickedIndex, width-12, 25) Then
					SelectTab(clickedIndex)
				EndIf
				
				clickedIndex = -1
				
				Return True
		End Select
		
		Return False
	End Method
End Type





' TESTO PRESTO
' THIS IS JUST UGLY, UGLY CODE

'buildopt:gui

Graphics 800,600,0

' You can grab this font at http://android.git.kernel.org/?p=platform/frameworks/base.git;a=tree;f=data/fonts;hb=HEAD
Local font:TImageFont = LoadImageFont("DroidSans.ttf",12)
Local qfont:TImageFont = LoadImageFont("DroidSans-Bold.ttf",28*(Double(GraphicsWidth())/800))

Local loadGameTab:Tab = New Tab.Init("Load Game", 255, 109, 0)
Local quitGameTab:Tab = New Tab.Init("Quit", 65, 148, 239)

Local ts:TabStrip = New TabStrip
ts.AddTab(New Tab.Init("New Game", 219, 0, 147))
ts.AddTab(loadGameTab)
ts.AddTab(New Tab.Init("Options", 221, 32, 32))
ts.AddTab(New Tab.Init("Achievements", 159, 79, 238))
ts.AddTab(New Tab.Init("", 221, 32, 32))
ts.AddTab(New Tab.Init("Credits", 150, 230, 57))
ts.AddTab(quitGameTab)

Local toogies:TabStrip = New TabStrip
For Local i:Int = 1 To 8
	toogies.AddTabWithName("Game #"+i)
Next


Local CenterX! = GraphicsWidth()*.5
If qfont Then
	SetImageFont(qfont)
	CenterX = Min(CenterX, GraphicsWidth()*.7-TextWidth("This doesn't actually quit!")*.8)
Else
	CenterX = Min(CenterX, GraphicsWidth()*.7-TextWidth("This doesn't actually quit!")*1.8)
EndIf

ts.x = Floor(CenterX-ts.width*.5)
ts.y = (GraphicsHeight()-ts.Height())*.5
toogies.x = Floor(CenterX-ts.width-toogies.width)
toogies.y = (GraphicsHeight()-toogies.Height())*.5

SetClsColor(80,80,80)

Local running:Int = True

SetBlend(ALPHABLEND)
Local poly#[] = [12#, 0#, 0#, 0#, 0#, Float GraphicsHeight(), 12#, Float GraphicsHeight()]
Local a!=GraphicsWidth()
Local lastSelected:Tab = Null


Function SinInterp!(s!,f!,t!)
	Return s+Sin(t*115)*((f-s)*1.09369221296335!)
End Function

Local rotoff!=Rnd(0,22)

While running
	While PollEvent()
		If Not (ts.HandleEvent(CurrentEvent) Or (ts.selected = loadGameTab And loadGameTab._alpha > .99999 And toogies.HandleEvent(CurrentEvent))) And (CurrentEvent.id = EVENT_APPTERMINATE Or (CurrentEvent.id = EVENT_KEYDOWN And CurrentEvent.data = KEY_ESCAPE)) Then
			running = False
		endIf
	Wend
	
	If ts.selected <> lastSelected Then
		rotoff!=Rnd(0,22)
	EndIf
	
	If ts.selected = loadGameTab And lastSelected <> loadGameTab Then
		Animate(toogies, "x", Floor(CenterX-toogies.width*.5), 230, SinInterp)
		Animate(ts, "x", Floor(CenterX-toogies.width*2.5), 100)
	ElseIf lastSelected = loadGameTab And ts.selected <> loadGameTab
		toogies.SelectTab(-1)
		Animate(toogies, "x", Floor(CenterX-ts.width-toogies.width), 200)
		Animate(ts, "x", Floor(CenterX-ts.width*.5), 230, SinInterp)
	EndIf
	lastSelected = ts.selected
	
	Animation.UpdateAnimations
	
	SetClsColor(ts.backr*.25, ts.backg*.25, ts.backb*.25)
	
	Cls
	
	If font Then SetImageFont(font)
	
	' Fancy background
	
	SetBlend(ALPHABLEND)
	
	SetAlpha(.02)
	SetLineWidth(3)
	Local odd%=False
	For Local i:Int = 0 Until 100 Step 2
		SetBlend(ALPHABLEND)
		odd=~odd
		If odd Then
			SetColor(ts.backr, ts.backg, ts.backb)
		Else
			SetColor(ts.backr*1.8, ts.backg*1.8, ts.backb*1.8)
		EndIf
		SetAlpha(.02)
		Local d! = 1.0 - Double(i)/100
		poly[2] = (200+a+100*(1.0-d))*d
		poly[4] = a*d
		DrawPoly(poly)
		SetBlend(LIGHTBLEND)
		SetColor(ts.backr*1.5, ts.backg*1.5, ts.backb*1.5)
		SetAlpha(.25)
		DrawLine(poly[2],poly[3], poly[4],poly[5])
		i :+ Int(9*d)
	Next
	
	' Toogies!
	
	SetViewport(ts.x, 0, GraphicsWidth()-ts.x, GraphicsHeight())
	
	SetBlend(ALPHABLEND)
	SetAlpha(1)
	SetColor(0,0,0)
	DrawRect(toogies.x+12, 0, 2, GraphicsHeight())
	
	' End toogies background
	
	toogies.Render
	
	' Bit more background
	
	If ts.x < toogies.x Then
		SetColor(toogies.backr, toogies.backg, toogies.backb)
		SetBlend(ALPHABLEND)
		SetAlpha(.7)
		DrawRect(ts.x, 0, toogies.x-ts.x+12, GraphicsHeight())
		SetAlpha(1)
		DrawRect(toogies.x, 0, 12, GraphicsHeight())
	EndIf
	
	
	SetBlend(ALPHABLEND)
	SetAlpha(1)
	SetColor(0,0,0)
	DrawRect(ts.x+12, 0, 2, GraphicsHeight())
	
	SetViewport(0, 0, GraphicsWidth(), GraphicsHeight())
	
	' End fancy/toogies background
	
	ts.Render
	
	' Yet more background
	
	SetColor(ts.backr, ts.backg, ts.backb)
	SetBlend(ALPHABLEND)
	SetAlpha(.7)
	DrawRect(0, 0, ts.x+12, GraphicsHeight())
	SetAlpha(1)
	DrawRect(ts.x, 0, 12, GraphicsHeight())
	
	' Ok now we're really done
	
	' Text!
	If ts.selected And ts.selected <> loadGameTab Then
		If qfont Then
			SetImageFont(qfont)
		Else
			SetImageFont(Null)
		EndIf
		Local txt$
		Select ts.selected.name
			Case "New Game" txt = "Yeah, right"
			Case "Options" txt = "You have none"
			Case "Achievements" txt = "You've achieved nothing"
			Case "Credits" txt = "Noel Cower"
			Case "Quit" txt = "This doesn't actually quit!"
		End Select
		SetColor(255,255,255)
		SetAlpha(ts.selected._alpha)
		SetHandle(TextWidth(txt)*.5, TextHeight(txt)*.5)
		Local angle!=360*Sin(ts.selected._alpha*105) + rotoff
		SetRotation(angle)
		Local s!=ts.selected._alpha
		If Not qfont Then
			s :* 1.75
		EndIf
		SetScale(s,s)
		DrawText(txt, GraphicsWidth()*.7, GraphicsHeight()*.5)
		SetRotation(0)
		SetScale(1,1)
		SetHandle(0,0)
	EndIf
	' End text!
	
	Flip
Wend
