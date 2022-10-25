; ID: 2436
; Author: slenkar
; Date: 2009-03-17 16:23:42
; Title: blitzmax GUI
; Description: a graphical GUI

Strict

Module keef.customgui
Import brl.max2d
Import brl.retro
'Import keef.gldraw


Global time
Global mouse_x
Global mouse_y
Global mouse_hit1
Global mouse_hit2
Global mouse_down1



Type gui_type

Field up_arrow:TImage
Field down_arrow:TImage
Field button_img:TImage
Field button_over_img:TImage
Field window_image:TImage
Field border_image:TImage
Field font:Timagefont
Global gui_pointer:gui_type
Field textr
Field textg
Field textb



Function init_gui(r,g,b,incstring$="",font:Timagefont=Null)
gui_pointer:gui_type=New gui_type
gui_pointer.font=font
gui_pointer.down_arrow = LoadImage(incstring+"guiskin\downarrow.png") 
gui_pointer.up_arrow=LoadImage(incstring+"guiskin\uparrow.png")
gui_pointer.window_image = LoadImage(incstring+"guiskin\paper.png") 
gui_pointer.button_img = LoadImage(incstring+"guiskin\button.png") 
gui_pointer.button_over_img=LoadImage(incstring+"guiskin\buttonpressed.png")
gui_pointer.border_image=LoadImage(incstring+"guiskin\border.png")
gui_pointer.textr=r
gui_pointer.textg=g
gui_pointer.textb=b
EndFunction



EndType

Function change_gui_text_color(r,g,b)
If gui_type.gui_pointer<>Null
gui_type.gui_pointer.textr=r
gui_type.gui_pointer.textg=g
gui_type.gui_pointer.textb=b
EndIf
EndFunction

Type gui_element Abstract
Field x
Field y
Field width:Float
Field height:Float
Field mouse_over
Field parent:gui_element
Field up_arrow:gui_element
Field down_arrow:gui_element
Field text$
Field rendered
Field hidden
Field gadget_list:TList=New TList
Field active
Field green,red,blue
Field pressed
Field pressed_timer
Field font:Timagefont
Global list:TList = New TList
Global clicked

Method destroy()
list.remove(Self)
EndMethod

Method After:gui_element(the_list:TList)
	Local link:TLink = the_List.FindLink(Self)
If link <> Null
link=link.NextLink()
EndIf

	If link<>Null
		Return gui_element(link.Value())
	Else
		Return Null
	EndIf
EndMethod


Method ispressed()
If Self.pressed=True
Self.pressed=False
Return True
EndIf
EndMethod

Method Before:gui_element(the_list:TList)
	Local link:TLink = the_List.FindLink(Self) 
If link <> Null
link=link.PrevLink()
EndIf
	If link<>Null

		Return gui_element(link.Value())
	Else
		Return Null
	EndIf
EndMethod

Method New()
red=255
blue=255
green=255
EndMethod

Method update(mouse_hit1,mouse_X,mouse_y) 
If detect_mouse_over(mouse_x,mouse_y) 
mouse_over = True
Else
mouse_over=False
EndIf
EndMethod

Method detect_mouse_over(mouse_X,Mouse_Y)
If Mouse_X > x
If Mouse_X < (x + width) 
If Mouse_Y > y
If Mouse_Y < (y + height) 
Return True
EndIf
EndIf
EndIf
EndIf



EndMethod

Method render() 

EndMethod

EndType



Type window Extends gui_element



Function Create:window(x,y,width,height) 
Local w:window = New window
w.x = x
w.y = y
w.width = width
w.height=height
w.gadget_list = New TList
w.hidden=True
gui_element.list.addlast(w)
Return w
EndFunction


Method update(mouse_hit1,mouse_x,mouse_y) 
For Local t:gui_element = EachIn Self.gadget_list
If t.hidden = False
t.update(mouse_hit1,mouse_x,mouse_Y)
EndIf
Next
EndMethod

Method render() 
For Local t:gui_element = EachIn Self.gadget_list
If t.hidden = False
t.render()
EndIf
Next
EndMethod

EndType

Type label Extends gui_element
Method render()
SetColor (gui_type.gui_pointer.textr , gui_type.gui_pointer.textg , gui_type.gui_pointer.textb) 
DrawText Self.text,Self.x,Self.y
EndMethod

Function Create:label(x,y,text$,parent:gui_element)
Local l:label=New label
l.x=x
l.y=y
l.text=text
l.height=TextHeight(text)
If parent<>Null
l.parent = parent
l.parent.gadget_list.addlast(l) 
EndIf
Return l
EndFunction

EndType

Type img_button Extends gui_element

Field image:TImage
Field radio_list:TList
Function Create:img_button(x , y , used_image:TImage , text$,parent:gui_element,radio_list:TList=Null)

Local t:img_button=New img_button
t.x = x
t.y = y
t.text=text$
t.width = ImageWidth(used_image)
t.height = ImageHeight(used_image)
t.image=used_image
t.radio_list=radio_list
If parent<>Null
t.parent = parent
t.parent.gadget_list.addlast(t)
EndIf

If radio_list<>Null
radio_list.addlast(t)
EndIf

Return t
EndFunction

Method update(mouse_hit1,mouse_x,mouse_y)

Super.update(mouse_hit1,mouse_x,mouse_y)
Self.pressed=False
If gui_element.clicked=False
If mouse_hit1
If mouse_over= True
pressed_timer=MilliSecs()
pressed = True
gui_element.clicked=True
If Self.radio_list<>Null
For Local t:img_button=EachIn Self.radio_list
t.active=False
Next
Self .active=True
EndIf

EndIf
EndIf
EndIf
EndMethod

Method render()

SetColor(255,255,255) 
SetScale (Self.width / ImageWidth(image) , height / ImageHeight(image)) 
Local draw_x
Local draw_y
'SetImageHandle(image,0,0) 





SetColor (170,100,100)
If active = False And mouse_over=False
SetColor (255 ,255 ,255) 
EndIf

If pressed_timer+300>MilliSecs()
DrawImage image , x , y+2
Else
DrawImage image,x,y
EndIf

If active=True
SetColor 100,100,100
DrawRect (Self.x,Self.y,ImageWidth(image),5)
SetColor 160,160,160
DrawRect (Self.x,Self.y,5,ImageHeight(image))
SetColor 160,160,160
DrawRect (Self.x,Self.y+(ImageHeight(image)-5),ImageHeight(image),5)
SetColor 220,220,220
DrawRect (Self.x+ImageWidth(image)-5,Self.y,5,ImageHeight(image))
EndIf

SetScale(1 , 1) 
SetColor(255 , 255 , 255) 
If text<>""
DrawText (text,(Self.x+Self.width/2)-(TextWidth(Self.text)/2),Self.y+Self.height)
EndIf
EndMethod

EndType

Function gui_input()
mouse_X=MouseX()
mouse_y=MouseY()
mouse_hit1=MouseHit(1)
For Local t:gui_element = EachIn gui_element.list
If t.hidden=False
t.update(mouse_hit1 , mouse_x , mouse_y) 
EndIf
Next
EndFunction

Function draw_gui()
For Local t:gui_element = EachIn gui_element.list
If t.hidden=False
t.render() 
EndIf
Next
EndFunction

Function is_Gui_clicked()
Return gui_element.clicked
EndFunction

Function add_button_to_gadget_Scroller(name$,radio:Int=False,g:gadget_scroller)
g.add_button(name,radio)
EndFunction

Function create_textbox:textbox(x:Int,y:Int,width:Float,height:Float)
Return textbox.Create(x,y,width,height)
EndFunction

Function Create_gadget_Scroller:gadget_scroller(x:Int,y:Int,width:Float,height:Float,parent:window) 
Return gadget_Scroller.Create(x,y,width,height,parent:window) 
EndFunction

Function Create_window:window(x:Int,y:Int,width:Float,height:Float) 
Return window.Create(x,y,width,height) 
EndFunction

Function Create_button:button(x:Int , y:Int , width:Float , height:Float , text$,parent:gui_element,font:Timagefont=Null)
Return button.Create(x , y , width:Float , height:Float , text$,parent:gui_element,font)
EndFunction

Type button Extends gui_element

Global img:TImage
Global img2:TImage
Field disabled
Method disable()
Self.disabled=True
EndMethod


Function Create:button(x , y , width:Float , height:Float , text$,parent:gui_element,font:Timagefont=Null)
If font=Null
font=gui_type.gui_pointer.font
EndIf

Local t:button=New button
t.x=x
t.y=y
t.width=width
t.height=height
t.text=text
t.font=font
If img = Null
img=gui_type.gui_pointer.button_img
img2=gui_type.gui_pointer.button_over_img
EndIf

If parent<>Null
t.parent = parent
t.parent.gadget_list.addlast(t)
EndIf

Return t
EndFunction

Method update(mouse_hit1,mouse_x,mouse_y) 
Super.update(mouse_hit1,mouse_x,mouse_y)
Self.pressed=False
If gui_element.clicked=False
If mouse_hit1
If mouse_over= True
If disabled=False
pressed_timer=MilliSecs()
pressed = True
gui_element.clicked=True
EndIf
EndIf
EndIf
EndIf
EndMethod

Method render()
SetImageFont(Self.font)
SetColor(255,255,255) 
Local draw_x
Local draw_y


If pressed_timer+300>MilliSecs()
SetScale (Self.width / ImageWidth(img) , height / ImageHeight(img)) 
DrawImage img2 , x , y
SetScale (1,1) 
 draw_x=(x+(width/2))-(TextWidth(text)/2)
 draw_y = ((y + (height / 2) ) - (TextHeight(text)/3))+2
Else
SetScale (1,1) 
 draw_x=(x+(width/2))-(TextWidth(text)/2)
 draw_y = (y + (height / 2) ) - (TextHeight(text)/3) 
SetScale (Self.width / ImageWidth(img) , height / ImageHeight(img)) 
DrawImage img,x,y
EndIf


SetScale (1,1) 

If disabled=False
If active = False And mouse_over=False
SetColor (gui_type.gui_pointer.textr , gui_type.gui_pointer.textg , gui_type.gui_pointer.textb) 
Else
SetColor (170,0,0)
EndIf
EndIf

If disabled=True
SetColor 50,50,50
EndIf

DrawText text,draw_x,draw_y',149,149,149
SetColor(255 , 255 , 255) 

EndMethod

EndType


Type Inputbox Extends gui_element
Field the_string$
Field has_focus
Field lastinput
Field blink

Function Create:inputbox(x# , y# , width# , height#,parent:gui_element,the_string$) 
	Local i:inputbox = New inputbox
	i.x = x
	i.y = y
	i.width = width
	i.height = height
	If parent<>Null
	i.parent = parent
	i.parent.gadget_list.addlast(i) 
	EndIf
	i.has_focus = True
	i.the_string=the_string
	FlushKeys()
Return i
EndFunction

Method changetext(the_text$) 
	the_string=the_text
EndMethod

Method last_input() 
	Return lastinput
EndMethod

Method render() 

Local end_string$=""

If has_focus = True
If blink=False
blink=True
end_string="|"
Else
blink=False
end_string=""
EndIf
EndIf

SetColor 255,255,255
SetScale (width/ImageWidth(gui_type.gui_pointer.window_image),height/ImageHeight(gui_type.gui_pointer.window_image))
DrawImage gui_type.gui_pointer.window_image,x,y 
SetScale(1,1)
SetColor gui_type.gui_pointer.textr , gui_type.gui_pointer.textg ,gui_type.gui_pointer.textb
Local x1 = x + 10
Local y1 = y + 5

If TextWidth(the_String)<(width-10)
DrawText the_string+end_string ,x1 , y1
Else

For Local x=1 To the_string.length
Local temp_string$=Right(the_string,x)
If TextWidth(temp_string)>(width-10)
DrawText Right(temp_string,temp_string.length-1)+end_string,x1,y1
Exit
EndIf
Next

EndIf

SetColor 255,255,255
EndMethod

Method update(mouse_hit1,mouse_x,mouse_y) 

Super.update(mouse_hit1,mouse_x,mouse_y)

Self.pressed=False

If gui_element.clicked=False
If mouse_hit1
FlushKeys()
If mouse_over= True
Self.pressed=True
gui_element.clicked = True
has_focus=True
Else
has_focus=False
EndIf
EndIf
EndIf
If has_focus = True
Local char = GetChar() 


lastinput = char

If char>8 And char<>13
the_string = the_string + Chr(char)
EndIf

If char=8
the_string = Left(the_string, Len(the_string)-1)
EndIf

Self.text=the_string
EndIf

EndMethod

EndType

Type textbox Extends gui_element

Field lines$[400]
Field line_list:TList = New TList
Field current_line

Global img


Function Create:textbox(x,y,width,height) 
Local t:textbox=New textbox 
t.x = x
t.y = y
t.width = width
t.height = height
Local u:up_arrow=up_arrow.Create(t)
Local d:down_arrow=down_arrow.Create(t) 
t.gadget_list = New TList
t.gadget_list.addlast(u) 
t.gadget_list.addlast(d)
t.list.addlast(t)
Return t
End Function


Method clear()
line_list.clear()
EndMethod

Method add_text(the_text$,flash)

Self.text=the_text
Local iter = 0
If TextWidth(the_text) > width - 9
Local word_array$[] = the_text.split(" ") 
Local new_string$
Local long_string$


While iter<word_array.length
Local a$ = word_array[iter]

long_string$ = new_string + A$

If TextWidth(long_string)>width-9
line_list.addlast(new_string) 

new_string = a+" "
long_string=""

If iter = word_array.length-1
line_list.addlast(New_String)
EndIf

Else
new_string = new_string + a + " "

If iter=word_array.length-1
line_list.addlast(new_string)
EndIf

EndIf

iter = iter + 1

Wend
Else
line_list.addlast(the_text)
EndIf

If line_list.count()>(height/TextHeight("HA"))
current_line = Int(Float(line_list.count()) -(Float(height)/Float(TextHeight("HA"))))+1
Else
Current_line=0
EndIf
DebugLog "current_ line: "+current_line

If flash=1
Self.red=0
Self.blue=0
Self.green=255
EndIf
If flash=2
Self.green=0
Self.blue=0
Self.red=255
EndIf
EndMethod

Method update(mouse_hit1 , mouse_x , mouse_y)

For Local t:gui_element = EachIn gadget_list
t.update(mouse_hit1,mouse_x,mouse_y)
Next

Super.update(mouse_hit1,mouse_x,mouse_y)

If gui_element.clicked=False
If mouse_hit1
If Self.mouse_over=True
gui_element.clicked=True
EndIf

If Self.up_arrow.mouse_over = True
gui_element.clicked=True
If current_line>0
current_line = current_line - 1
EndIf
EndIf


If Self.down_arrow.mouse_over = True
gui_element.clicked=True
If current_line<line_list.count()-1
current_line = current_line + 1
EndIf
EndIf


EndIf
EndIf

If red<255
red:+6
EndIf
If blue<255
blue:+6
EndIf
If green<255
green:+6
EndIf
EndMethod

Method render() 
If hidden=False
SetColor red,green,blue
SetScale (width/ImageWidth(gui_type.gui_pointer.window_image),height/ImageHeight(gui_type.gui_pointer.window_image))
DrawImage gui_type.gui_pointer.window_image,x,y 
Local border_width#=10

SetScale (border_width/ImageWidth(gui_type.gui_pointer.border_image),height/ImageWidth(gui_type.gui_pointer.border_image))
DrawImage (gui_type.gui_pointer.border_image,x-border_width,y)
DrawImage (gui_type.gui_pointer.border_image,x+width,y)
SetScale(width/ImageWidth(gui_type.gui_pointer.border_image),border_width/ImageHeight(gui_type.gui_pointer.border_image))
DrawImage (gui_type.gui_pointer.border_image,x,y-border_width)
DrawImage (gui_type.gui_pointer.border_image,x,y+height)

SetScale(border_width/ImageWidth(gui_type.gui_pointer.border_image),border_width/ImageHeight(gui_type.gui_pointer.border_image))

DrawImage(gui_Type.gui_pointer.border_image,x-border_width,y-border_width)
DrawImage(gui_Type.gui_pointer.border_image,x+width,y-border_width)
DrawImage(gui_Type.gui_pointer.border_image,x-border_width,y+height)
DrawImage(gui_Type.gui_pointer.border_image,x+width,y+height)


SetScale(1,1)
SetColor gui_type.gui_pointer.textr , gui_type.gui_pointer.textg ,gui_type.gui_pointer.textb

Local line_position=0
Local iter=0
For Local a$ = EachIn line_list

If line_position > current_line - 1
If line_position < (current_line + (height / TextHeight(a)) )-1
DrawText a,x+9,y+((iter*TextHeight("HA"))+5)
iter=iter+1
EndIf
EndIf

line_position=line_position+1
Next

For Local t:gui_element = EachIn gadget_list
t.render
Next

SetColor(255,255,255)

EndIf
EndMethod

EndType


Type draggable_image Extends gui_element

Field image:TImage
Field being_dragged
Field offset_x
Field offset_y
Field dropped

Function Create:draggable_image(x,y,offset_x,offset_y,used_image:TImage,parent:gui_element)
Local d:draggable_image=New draggable_image
d.x=x
d.y=y
d.width=ImageWidth(used_image)
d.height=ImageHeight(used_image)
d.image=used_image
d.offset_x=offset_x
d.offset_y=offset_y
If parent<>Null
d.parent=parent
If d.parent.gadget_list=Null
d.parent.gadget_list=New TList
EndIf
d.parent.gadget_list.addlast(d)
EndIf
Return d
EndFunction

Method render()

SetColor 255,255,255
SetScale 1,1
SetRotation 0

If being_dragged=True
SetViewport(0,0,GraphicsWidth(),GraphicsHeight()) 
DrawImage image,mouse_x,mouse_y
EndIf

DrawImage image,x+offset_x,y+offset_y

EndMethod


Method update(mouse_hit1,mouse_x,mouse_y) 

Super.update(mouse_hit1,mouse_x,mouse_y)

If being_dragged=True
If Not Mouse_Down1
being_dragged=False
dropped=True
EndIf
EndIf

Local an_image_dragged

For Local d:draggable_image=EachIn Self.parent.gadget_list
If d.being_dragged=True
an_image_dragged=True
EndIf
Next

If an_image_dragged=False
If mouse_over= True
If mouse_down1=True
If being_dragged=False
being_dragged=True
pressed_timer=MilliSecs()
pressed = True
gui_element.clicked=True
EndIf
EndIf
EndIf
EndIf
EndMethod

EndType

Type gadget_scroller Extends gui_element

Field radio_list:TList=New TList
Field current_gadget:gui_element
Field virtual_height

Method add_button:button(name$,radio=False)
Local but:button=Button.Create(25 , 25 , width-10 , 25 , name,Null)
add_gadget(but,radio)
Return but
EndMethod

Method add_gadget(o:gui_element,radio=False,column=1)

If o.parent<>Null
o.parent.gadget_list.remove(o) 
EndIf

gui_element.list.remove(o)

If gadget_list.count()=0 And radio=True
o.active=True
EndIf


If radio=False
gadget_list.addlast(o) 
EndIf

If radio=True
Self.radio_list.addlast(o)
gadget_list.addlast(o)
EndIf

If current_gadget=Null
current_gadget = o
EndIf

o.x = x+5
o.y = y
'o.width=width-10

EndMethod

Method remove_all()
Self.current_gadget=Null
Self.gadget_List.clear()
Self.radio_list.clear()
EndMethod

Function Create:gadget_scroller(x,y,width,height,parent:window) 

Local o:gadget_scroller = New gadget_scroller
o.x = x
o.y = y
o.width = width
o.height=height
o.gadget_list = New TList
parent.gadget_list.addlast(o) 
o.up_arrow=up_arrow.Create(o)
o.down_arrow=down_arrow.Create(o) 

Return o

EndFunction

Method render() 
SetViewport(0,0,GraphicsWidth(),GraphicsHeight())
SetColor 255,255,255
SetScale (width/ImageWidth(gui_type.gui_pointer.window_image),height/ImageHeight(gui_type.gui_pointer.window_image))
DrawImage gui_type.gui_pointer.window_image,x,y 
Local border_width#=10
SetScale (border_width/ImageWidth(gui_type.gui_pointer.border_image),height/ImageWidth(gui_type.gui_pointer.border_image))
DrawImage (gui_type.gui_pointer.border_image,x-border_width,y)
DrawImage (gui_type.gui_pointer.border_image,x+width,y)
SetScale(width/ImageWidth(gui_type.gui_pointer.border_image),border_width/ImageHeight(gui_type.gui_pointer.border_image))
DrawImage (gui_type.gui_pointer.border_image,x,y-border_width)
DrawImage (gui_type.gui_pointer.border_image,x,y+height)


SetScale(border_width/ImageWidth(gui_type.gui_pointer.border_image),border_width/ImageHeight(gui_type.gui_pointer.border_image))

DrawImage(gui_Type.gui_pointer.border_image,x-border_width,y-border_width)
DrawImage(gui_Type.gui_pointer.border_image,x+width,y-border_width)
DrawImage(gui_Type.gui_pointer.border_image,x-border_width,y+height)
DrawImage(gui_Type.gui_pointer.border_image,x+width,y+height)


SetScale(1,1)

For Local o:gui_element = EachIn gadget_list
o.rendered = False
Next

Local render_height=0
Local render_width=0
Local found_first


For Local o:gui_element = EachIn gadget_list
If o.hidden = False


If o = Self.current_gadget
found_first=True
EndIf

If found_first=True
If render_height < height
'SetViewport (x , y , width , height)
o.y=render_height+5+y

If o.active=True
SetColor 170,0,0
EndIf

o.x=Self.x+5
o.render()
o.rendered=True
render_height = render_height + o.height+3

If o.width>render_width
render_width=o.width
EndIf


EndIf
EndIf

EndIf
Next


For Local o:gui_element=EachIn gadget_list
If o.gadget_list<>Null
For Local i:gui_element=EachIn o.gadget_list
i.x=render_width+5
i.y=(o.y+(o.height/2))
i.render()
i.rendered=True
Next
EndIf
Next


SetViewport(0,0,GraphicsWidth(),GraphicsHeight())
Self.up_arrow.render() 
Self.down_arrow.render()
EndMethod



Method update(mouse_hit1,mouse_X,mouse_y)


For Local o:gui_element = EachIn gadget_list
If o.hidden=False
If o.rendered = True
o.update(mouse_hit1,mouse_x,mouse_Y)
EndIf
EndIf
Next


For Local o:gui_element = EachIn radio_list

If o.hidden=False
If o.rendered = True
If o.pressed=True
o.active=True
For Local j:gui_element=EachIn radio_List
If j<>o
j.active=False
EndIf
Next

EndIf
EndIf
EndIf

Next


Self.down_arrow.update(mouse_hit1,mouse_x,mouse_y)
Self.up_arrow.update(mouse_hit1 , mouse_x , mouse_y) 

If gui_element.clicked=False
If mouse_hit1

If Self.up_arrow.mouse_over = True
gui_element.clicked = True
If current_gadget<>Null
If current_gadget.before(gadget_list) <> Null
current_gadget=current_gadget.before(gadget_list)
EndIf
EndIf
EndIf

If Self.down_arrow.mouse_over = True
gui_element.clicked = True
If current_gadget<>Null
If current_gadget.after(gadget_list) <> Null
current_gadget=current_gadget.after(gadget_list)
EndIf
EndIf
EndIf
If Self.detect_mouse_over(mouse_x,Mouse_Y) = True
gui_element.clicked = True
EndIf


EndIf
EndIf
EndMethod

EndType

Type up_arrow Extends gui_element


Function Create:up_arrow(parent:gui_element)
Local u:up_arrow = New up_arrow
u.x = (parent.x + parent.width)+10
u.y=parent.y
u.width = 29
u.height = 29
u.parent = parent
parent.up_arrow = u
Return u
EndFunction

Method render()
SetRotation(0) 
SetViewport 0,0,GraphicsWidth(),GraphicsHeight()
SetColor 255 , 255 , 255
SetScale 0.5 , 0.5
DrawImage gui_type.gui_pointer.up_arrow,x,y
SetScale(1,1)
EndMethod

Method update(mouse_hit1,mouse_x,mouse_y) 
x = (parent.x + parent.width)+10
y = parent.y
Super.update(mouse_hit1,mouse_x,mouse_y)
EndMethod

EndType

Type down_arrow Extends gui_element


Function Create:down_arrow(parent:gui_element)
Local u:down_arrow = New down_arrow
u.width = 29
u.height = 29
u.x = (parent.x + parent.width)+10
u.y=(parent.y + parent.height) - 58
u.parent = parent
parent.down_arrow = u
Return u
EndFunction

Method render()

SetRotation(0) 
SetViewport 0,0,GraphicsWidth(),GraphicsHeight()
SetColor 255 , 255 , 255
SetScale 0.5 , 0.5
DrawImage gui_type.gui_pointer.down_arrow,x,y
SetScale(1,1)
EndMethod

Method update(mouse_hit1 , mouse_x , mouse_y) 
x = (parent.x + parent.width)+10
y = (parent.y + parent.height) - 29
Super.update(mouse_hit1 , mouse_x , mouse_y) 
EndMethod

EndType


Type toggle_button Extends gui_element

Global img:TImage
Global img2:TImage
Field active
Field pressed

Function Create:toggle_button(x , y , width:Float , height:Float , text$,parent:gui_element)
Local t:toggle_button=New toggle_button
t.x = x
t.y = y
t.width = width
t.height = height
t.text = text

If img = Null
img=gui_type.gui_pointer.button_img
img2=gui_type.gui_pointer.button_over_img
EndIf

parent.gadget_list.addlast(t)

Return t
EndFunction

Method update(mouse_hit1,mouse_x,mouse_y) 
Super.update(mouse_hit1,mouse_x,mouse_y)

If gui_element.clicked=False
If mouse_hit1
If mouse_over = True
If active = False
active = True
Else
active=False
EndIf
gui_element.clicked=True
pressed = True
EndIf
EndIf
EndIf
EndMethod

Method render()

SetColor(255,255,255) 
SetScale (Self.width / ImageWidth(img) , height / ImageHeight(img)) 

If active=True
DrawImage img2 , x , y
Else
DrawImage img,x,y
EndIf

SetScale(1 , 1) 

If active=False
SetColor (gui_type.gui_pointer.textr , gui_type.gui_pointer.textg , gui_type.gui_pointer.textb) 
Else
SetColor (170,0,0)
EndIf

Local draw_x=(x+(width/2))-(TextWidth(text)/2)
Local draw_y=(y+(height/2))-(TextHeight(text)/2)
DrawText text,draw_x,draw_y
SetColor(255 , 255 , 255) 

EndMethod

EndType
