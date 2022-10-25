; ID: 1875
; Author: CS_TBL
; Date: 2006-12-08 10:08:55
; Title: Foldable panels
; Description: Foldable panels (MaxGUI)

SuperStrict

Type TFold

Rem
	TFold, foldable menus, MAXGUI

	by: CS_TBL


	usage:
	
	MyFoldmenu:TFold=CreateFold(x,y,width,height,parent,panelflags=PANEL_BORDER,titlebarheight=16)
	
	leftclick on a titlebar opens or closes it
	rightclick on a titlebar opens the one and closes the rest
	
	left/right-drag on panels moves the long panel up/down
	mousewheel on either panel or titlebar moves the long panel up/down
	

	user methods:
	
	MyFoldmenu.Add height,title1$
	MyFoldmenu.Add height,title2$
	MyFoldmenu.Add height,title3$
	MyFoldmenu.Add height,title4$   (etc.)
	
	MyFoldmenu.GetPanel(which) ' returns the panel at index 'which'
	                             you'll need this to add gadgets to the panel, to actually use it, so to say :P
	MyFoldmenu.GetBackPanel() ' returns the backpanel which you can mostly see when enough subs are closed
	
	MyFoldmenu.Open(which) ' opens sub 'which'
	
	MyFoldmenu.Close(which) ' closes sub 'which'
	
	MyFoldmenu.CloseAll(except=-1) ' closes all subs, except when a sub is given
	
	MyFoldmenu.SetTitleimage(img) ' uses an image to soup up the titlebars, if none is used then the titlebar is b/w

	For the rest: it all works automagically, and is superstrict complient.
	
		
EndRem

 	' --------------------------------------------------------------
	' viewpanel, size given by the user
 	' --------------------------------------------------------------
	Field panel:TGadget	
	Field width:Int
	Field vheight:Int
		
 	' --------------------------------------------------------------
	' longpanel, internal scrollable panel
 	' --------------------------------------------------------------
	Field longpanel:TGadget
	Field height:Int=0
	Field offset:Int=0

 	' --------------------------------------------------------------	
	' foldable subs
 	' --------------------------------------------------------------
	
	Field subcanvas:TGadget[]
	Field subpanel:TGadget[]
	Field subheight:Int[]
	Field subtitle:String[]
	Field substate:Int[]
	
	Field titleheight:Int ' height of the sub's titlebars

	Field count:Int=0
	
 	' --------------------------------------------------------------
	' misc
 	' --------------------------------------------------------------

	Field lmd:Int
	Field oldy:Int
	Field titleimg:TImage
	
 	' --------------------------------------------------------------
 	' --------------------------------------------------------------

	Function eventhook:Object(id:Int,data:Object,context:Object)
		If TFold(context) TFold(context).ev TEvent(data);Return data	
	EndFunction
	
	Method New()
		AddHook EmitEventHook,eventhook,Self
	End Method
	
	Method Free()
		RemoveHook EmitEventHook,eventhook
		GCCollect()
	End Method

 	' --------------------------------------------------------------
 	' --------------------------------------------------------------
	
	Method ev(event:TEvent)
		Local t:Int
		
		For t=0 To count-1 ' all subs
		
			If event.source=subcanvas[t]
			
				If event.id=EVENT_GADGETPAINT updatesub t
				
				If event.id=EVENT_MOUSEDOWN
				
					If event.data=1
						substate[t]=1-substate[t]
						Organize
					EndIf
					
					If event.data=2 CloseAll t
					
				EndIf
				
				If event.id=EVENT_MOUSEWHEEL 
					ChangeOffset Sgn(event.data)*32
					SetGadgetShape longpanel,0,offset,width,height
				EndIf
				
			EndIf
			
			If event.source=subpanel[t]
			
				If event.id=EVENT_MOUSEDOWN
					oldy=event.y
					lmd=1
				EndIf
				If event.id=EVENT_MOUSEUP
					lmd=0
				EndIf
				
				If lmd
					If event.id=EVENT_MOUSEMOVE
						ChangeOffset(event.y-oldy)
						SetGadgetShape longpanel,0,offset,width,height
					EndIf
				EndIf
			
				If event.id=EVENT_MOUSEWHEEL
					ChangeOffset Sgn(event.data)*32
					SetGadgetShape longpanel,0,offset,width,height
				EndIf
			EndIf
		Next
		
	End Method
	
 	' --------------------------------------------------------------
 	' --------------------------------------------------------------

	Method ChangeOffset(with:Int)
		If vheight<height
			offset:+with
			If offset>0 offset=0
			If offset<(vheight-height) offset=vheight-height
		EndIf
	End Method

 	' --------------------------------------------------------------
 	' --------------------------------------------------------------
	
	Method updatesub(which:Int)
		SetGraphics CanvasGraphics(subcanvas[which])
			Cls
			
			SetColor 255,255,255
			If titleimg<>Null TileImage titleimg
			
			SetColor 0,0,0			
			If substate[which]
				DrawText "-",1,1
				SetColor 255,192,0				
				DrawText "-",0,0
			Else
				DrawText "+",1,1
				SetColor 255,192,0				
				DrawText "+",0,0
			EndIf
			
			SetColor 0,0,0
			DrawText subtitle[which],17,1
			SetColor 255,255,255
			DrawText subtitle[which],16,0
		Flip
	End Method

 	' --------------------------------------------------------------
 	' --------------------------------------------------------------
	
	Method Add(h:Int,t$)
	
		' stretch array
		count:+1
		
		subcanvas=subcanvas[..count]
		subpanel=subpanel[..count]
		subheight=subheight[..count]
		subtitle=subtitle[..count]
		substate=substate[..count]
		
		
		' add new sub
		Local c:Int=count-1
		
		subcanvas[c]=CreateCanvas(0,-titleheight,width,titleheight,longpanel)
		
		subpanel[c]=CreatePanel(0,-h,width,h,longpanel,PANEL_ACTIVE)
			
		subheight[c]=h
		subtitle[c]=t
		substate[c]=1
		
		Organize
	End Method
	
 	' --------------------------------------------------------------
 	' --------------------------------------------------------------

	Method Organize()
		Local t:Int
		Local total:Int=0
		
		' calc longpanel height
		For t=0 To count-1
			total:+titleheight+(subheight[t]*substate[t])
		Next
		height=total
		
		If height<=vheight offset=0
		SetGadgetShape longpanel,0,offset,width,total
				
		Local pos:Int=0
		
		' relocate subs
		For t=0 To count-1
			SetGadgetShape subcanvas[t],0,pos,width,titleheight
			pos:+titleheight
			
			If substate[t] ' is opened?
				ShowGadget subpanel[t]
				SetGadgetShape subpanel[t],0,pos,width,subheight[t]
				pos:+subheight[t]
			Else
				HideGadget subpanel[t]
			EndIf
		Next
		
	End Method

 	' --------------------------------------------------------------
 	' --------------------------------------------------------------
	
	Method Open(which:Int)
		If which>(count-1) which=count-1
		If which<0 which=0
		substate[which]=1
		Organize
	End Method

	Method Close(which:Int)
		If which>(count-1) which=count-1
		If which<0 which=0
		substate[which]=0
		Organize
	End Method

	Method CloseAll(except:Int=-1)
		For Local t:Int=0 To count-1
			substate[t]=0
			If t=except substate[t]=1
		Next
		Organize
	End Method
	
	Method GetPanel:TGadget(which:Int)
		If count
			If which>(count-1) which=count-1
			If which<0 which=0
			Return subpanel[which]
		Else
			Return Null
		EndIf
	End Method

	Method GetBackPanel:TGadget()
		Return panel
	End Method
	
	Method SetTitleimage(img:TImage)
		titleimg=img
		If count>0
			For Local t:Int=0 To count-1
				updatesub t
			Next
		EndIf
	End Method

End Type

Function CreateFold:TFold(x:Int,y:Int,w:Int,h:Int,parent:TGadget,flags:Int=PANEL_BORDER,theight:Int=16)
	Local a:TFold=New TFold
	
	a.panel=CreatePanel(x,y,w,h,parent,flags)
		SetGadgetLayout a.panel,1,0,1,0
		SetGadgetColor a.panel,128,128,128
	a.longpanel=CreatePanel(0,0,w,0,a.panel)
		SetGadgetLayout a.longpanel,1,0,1,0
	a.width=w
	a.titleheight=theight
	a.vheight=h
	
	Return a
End Function



' let's create an image and a pixmap to fancy things up a bit
Local img:TImage=MakeSomething()
Local p:TPixmap=MakeSomething2()


' a window to start with
Local window:TGadget=CreateWindow("o_O",320,256,640,512)



' and then...: an empty foldmenu!
Local f:TFold=CreateFold(8,8,256,400,window)

' attach the image to the titlebars
f.SetTitleimage img

' add 3 submenus
f.Add 220,"Bla1"
f.Add 160,"Bla2"
f.Add 50,"Bla3"

f.CloseAll

' and attach pixmaps to 'em
SetPanelPixmap f.GetPanel(0),p
SetPanelPixmap f.GetPanel(1),p
SetPanelPixmap f.GetPanel(2),p


' another foldmenu, on one of the previous submenus;
Local f2:TFold=CreateFold(4,4,160,140,f.GetPanel(0))
SetPanelColor f2.GetBackPanel(),144,120,120
f2.SetTitleimage img

f.Open 1

f2.Add 40,"Waa!1"
f2.Add 50,"Waa!2"
f2.Add 30,"Waa!3"
f2.CloseAll



'oh and on a more serious note:
Local f3:TFold=CreateFold(500,8,120,400,window)
f3.Add 100,"Shapes"
f3.Add 100,"Lights"
f3.Add 100,"Cameras"
f3.Add 100,"Textures"
f3.Add 100,"Effects"
f3.Add 100,"Animate"

f3.CloseAll 1

f3.SetTitleimage img

For Local t:Int=0 To 5
	SetPanelPixmap f3.GetPanel(t),p
Next

CreateButton "w00!",4,4,40,20,f3.GetPanel(1)
CreateButton "O_o?",4,24,40,20,f3.GetPanel(1)






' that's it, now it's at your service!

Repeat
	WaitEvent()
	If EventID()=EVENT_WINDOWCLOSE End
Forever








' ----------------------------------------------------------------------------
' unrelated, just to create some image and pixmap. You can live on without 'em
' ----------------------------------------------------------------------------
Function MakeSomething:TImage()
	Local img:TImage=CreateImage(4,16)
	Local i:Int
	Local pm:TPixmap=LockImage(img)
		For Local y:Int=0 To 15
			For Local x:Int=0 To 3
				i=128+Cos((y+x)*(360/32))*40			
				WritePixel pm,x,y,$ff000000|i+256*i+65536*i
			Next
		Next
	UnlockImage img
	Return img	
End Function

Function MakeSomething2:TPixmap()
	Local i:Int
	Local pm:TPixmap=CreatePixmap(4,4,PF_RGBA8888)
		For Local y:Int=0 To 3
			For Local x:Int=0 To 3
				i=160
				WritePixel pm,x,y,$ff000000|i+256*i+65536*i
			Next
		Next
		i=192;WritePixel pm,0,0,$ff000000|i+256*i+65536*i
		i=128;WritePixel pm,3,3,$ff000000|i+256*i+65536*i
	Return pm
End Function
