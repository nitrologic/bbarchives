; ID: 1750
; Author: CS_TBL
; Date: 2006-07-11 14:07:45
; Title: MaxGUI: Imagebutton
; Description: super-userfriendly Photoshop-like imagebuttons

SuperStrict

Const EVENT_IMAGEBUTTONCLICKED:Int=$13370666  ' or any other, less l33t-evil, number :P

Type TImagebutton

Rem 
	Imagebutton and Imagebuttongroup
	by CS_TBL
	
	usage:
	
	Local MyButton:TImagebutton=CreateImagebutton(x,y,imagehandle,parentgadgethandle,state)
	
	By default these are buttons that simply go on and off, if you place buttons like these into an
	Imagebuttongroup, then all buttons in such a group act like radiobuttons, e.g. only one can be
	switched on at one time. Typically you'd have to use a panel in B+/BmaxGUI for this with normal
	buttons.
	
	Local group:TImagebuttongroup=New TImagebuttongroup
	
	and then:
	
	group.Add MyButton1
	group.Add MyButton2
	group.Add MyButton3
	etc.
	
	It's all automated with eventhooks, <*> create it .. and use it! <*>
	
	Buttons are the size of your image. Activated buttons are simply the source-image, de-activated
	buttons are the same.. but grey. A little highlight is shown upon hovering with the mouse.
	
	[Z] and [X] when the mousepointer is on the button, double as leftclick and rightclick. The button
	can accept leftclicks and rightclicks and this can be read out from event.data or EventData().
	
EndRem

	Field canvas:TGadget
	Field parent:TGadget
	
	Field state:Int
	Field hoover:Int
	
	Field image:TImage
	Field grey:TImage
	
	Field hooverintensity:Int=24
	Field newevent:TEvent=New TEvent
	
	Function eventhook:Object(id:Int,data:Object,context:Object)
		If Timagebutton(context) Timagebutton(context).ev TEvent(data);Return data	
	EndFunction
	
	Method New()
		AddHook EmitEventHook,eventhook,Self
	End Method
	
	Method Free()
		RemoveHook EmitEventHook,eventhook
		GCCollect()
	End Method
	
	Method MakeGrey()
		Local r:Int,g:Int,b:Int,c:Int	
		Local x:Int,y:Int
		grey=CreateImage(ImageWidth(image),ImageHeight(image))
		
		Local pm:TPixmap=LockImage(image)
		Local pm2:TPixmap=LockImage(grey)
			For y=0 To ImageHeight(image)-1
				For x=0 To ImageWidth(image)-1
					c=ReadPixel(pm,x,y)
					r=c&255
					g=(c/256)&255
					b=(c/65536)&255
					c=(r+g+b)/3
					WritePixel pm2,x,y,c+256*c+65536*c|$ff000000
				Next
			Next
		UnlockImage image
		UnlockImage grey
	End Method
	
	Method ev(event:TEvent)
		If event.source=canvas
			If event.id=EVENT_GADGETPAINT update
			
			If event.id=EVENT_MOUSEENTER
				SetPointer POINTER_HAND		' <- comment out all SetPointer lines if you don't want changing cursors
				ActivateGadget canvas
				hoover=1
				update
				
			EndIf
			
			If event.id=EVENT_MOUSELEAVE
				SetPointer POINTER_DEFAULT
				ActivateGadget parent
				hoover=0
				update
			EndIf
			
			If event.id=EVENT_MOUSEDOWN
				Clickbutton event.data
			EndIf
			
			If event.id=EVENT_KEYDOWN
				If event.data=90 ' Z
					Clickbutton 1
				EndIf
				If event.data=88 ' X
					Clickbutton 2
				EndIf
			EndIf
		EndIf
		
	End Method
	
	Method Clickbutton(data:Int=1)
		If data=1
			newevent.data=1
		EndIf
		If data=2
			newevent.data=2
		EndIf
		state:+1
		state:Mod 2
		update
		
		newevent.source=Self
		newevent.id=EVENT_IMAGEBUTTONCLICKED
		
		EmitEvent newevent	
	End Method
	
	Method update()
		SetGraphics CanvasGraphics(canvas)
			SetClsColor 0,0,0;Cls
			If image
				SetBlend MASKBLEND
				SetColor 255,255,255
				
				If state
					DrawImage image,0,0
				Else
					DrawImage grey,0,0
				EndIf
				
				If hoover
					SetColor hooverintensity,hooverintensity,hooverintensity
					SetBlend LIGHTBLEND				
					DrawRect 0,0,GadgetWidth(canvas),GadgetHeight(canvas)
				EndIf
				SetBlend MASKBLEND
			EndIf
		Flip
	End Method
	
	Method SetState(st:Int)
		state=st
		update
	End Method
	
	Method GetState:Int()
		Return state
	End Method
	
	Method SetIntensity(i:Int)
		hooverintensity=i
	End Method
End Type

Function CreateImagebutton:TImagebutton(x:Int,y:Int,image:TImage,parent:TGadget,state:Int=0)
	If image=Null RuntimeError "no image given"
	If parent=Null RuntimeError "no parent given"
	
	Local a:TImagebutton=New TImagebutton
	a.image=image
	a.MakeGrey	
	a.canvas=CreateCanvas(x,y,ImageWidth(image),ImageHeight(image),parent)
	a.parent=parent
	a.state=state
	Return a
End Function

Type TImagebuttongroup

	Field list:TImagebutton[]
	Field amount:Int=0
	Field newevent:TEvent=New TEvent
	
	Function eventhook:Object(id:Int,data:Object,context:Object)
		If TImagebuttongroup(context) TImagebuttongroup(context).ev TEvent(data);Return data	
	EndFunction
	
	Method New()
		AddHook EmitEventHook,eventhook,Self
	End Method
	
	Method Free()
		RemoveHook EmitEventHook,eventhook
		GCCollect()
	End Method
	
	Method ev(event:TEvent)
		If event.id=EVENT_IMAGEBUTTONCLICKED
			Local t:Int
			Local tt:Int
			For t=0 To amount-1
				If event.source=list[t]
					For tt=0 To amount-1
						list[tt].SetState 0
					Next
					list[t].SetState 1
				EndIf
			Next
		EndIf
	End Method
	
	Method Add(t:TImagebutton)
		amount:+1
		list=list[..amount]
		list[amount-1]=New TImagebutton
		list[amount-1]=t
	End Method
End Type









Rem

 EXAMPLE!

EndRem










Local window:TGadget=CreateWindow(".",0,0,640,480)

' 4 images
Local im1:TImage=LoadImage("brush.png")
Local im2:TImage=LoadImage("cut.png")
Local im3:TImage=LoadImage("paint.png")
Local im4:TImage=LoadImage("pencil.png")

' 4 buttons that can all be switched on and off
Local MyButton1:TImagebutton=CreateImagebutton(32,32,im1,window,0)
Local MyButton2:TImagebutton=CreateImagebutton(32,80,im2,window,0)
Local MyButton3:TImagebutton=CreateImagebutton(96,32,im3,window,0)
Local MyButton4:TImagebutton=CreateImagebutton(96,80,im4,window,0)

' same stuff again..
Local MyButton5:TImagebutton=CreateImagebutton(232,32,im1,window,1)
Local MyButton6:TImagebutton=CreateImagebutton(232,80,im2,window,0)
Local MyButton7:TImagebutton=CreateImagebutton(232,128,im3,window,0)
Local MyButton8:TImagebutton=CreateImagebutton(232,176,im4,window,0)

' ..but now tucked away into a group so they act as radiobuttons (only one is active)
Local group:TImagebuttongroup=New TImagebuttongroup
group.Add MyButton5
group.Add MyButton6
group.Add MyButton7
group.Add MyButton8

Repeat
	WaitEvent()
	
	If EventID()=EVENT_WINDOWCLOSE End
	
	If EventID()=EVENT_IMAGEBUTTONCLICKED
	
		DebugLog Mid("leftclicked rightclicked",1+(EventData()-1)*12,12)
		
		Select EventSource()
			Case MyButton1,MyButton5
				DebugLog "brush!"
			Case MyButton2,MyButton6
				DebugLog "cut!"
			Case MyButton3,MyButton7
				DebugLog "paint!"
			Case MyButton4,MyButton8
				DebugLog "pencil!"
		End Select

	EndIf
Forever
