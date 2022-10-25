; ID: 2018
; Author: Yahfree
; Date: 2007-05-23 21:45:49
; Title: DropDown Menus!
; Description: My second GUI, for my calculator

Global CursorX, CursorY, CursorHit

Type DropDown
Field width
Field drawdrop
Field scrolly
Field shown
Field x,y
Field words$
Field Accepts
End Type

;-----------------------------------------------------------------
;                       CreateDropDown()
;-----------------------------------------------------------------




Function CreateDropDown.DropDown(x,y,dd_width,Accepted)



NewDD.DropDown = New DropDown
NewDD\x=x
NewDD\y=y
NewDD\drawdrop=False
NewDD\scrolly=y+20
NewDD\shown=y
NewDD\Accepts=Accepted
NewDD\width=dd_width


Return NewDD
End Function

;----------------------------------------------------------------
;              DrawDropDowns()
;----------------------------------------------------------------

Function DrawDropDowns(Creation_Info.DropDown)



drawdrop = Creation_Info\drawdrop
scrolly = Creation_Info\scrolly
shown = Creation_Info\shown
x = Creation_Info\x
y = Creation_Info\y
words$ = Creation_Info\words$
number = Creation_Info\Accepts
width = Creation_Info\width

;If you click on the button, dropdown=True
If RectsOverlap(x+width-25,y,25,20,CursorX,CursorY,5,5) And CursorHit
  If drawdrop=False
     drawdrop=True
Else
   drawdrop=False
  End If
End If



If drawdrop=True
Rect x,y,width,200,0
Line x+width,y+20,x+width-12.5,y+10
Line x+width-25,y+20,x+width-12.5,y+10


;For...Each ... should get the name of all the objects.
i=0
For checklist.list = Each list

If checklist\ID=number
i=i+1

;Made this For i...2 loop so the Text can loop itself...
Viewport x,y+20,125,180
   Text x,shown+(i*20),checklist\name
Viewport 0,0,GraphicsWidth(),GraphicsHeight()

;If you click on one of the names it will put it up top.
    If RectsOverlap(x,shown+(i*20),width-25,20,CursorX,CursorY,5,5) And CursorX > x And CursorX< x+125 And CursorY > y+20 And CursorY < y+200 And MouseDown(1)
        words$=checklist\name
        drawdrop=False
        shown=y
        scrolly=y+20
     End If


End If
Next
Rect x+width-25,y+20,25,180,False
If i>9 

 


   Rect x+width-25,scrolly,25,20,True
  If RectsOverlap(x+width-25,y+20,25,180,CursorX,CursorY,5,5) And MouseDown(1)
     scrolly=MouseY()
End If




If scrolly<y+20 scrolly=y+20
If scrolly>y+180 scrolly=y+180

;The more things in the Text the faster it scrolls To compensate..
ScrollbarPos = ScrollY - y-20
ScrollbarMax = y+180- y-20
ListSize = (i - 9) * 20 
shown = y - (scrollbarpos * listsize / scrollbarmax)


End If

Else

Line x+width-25,y,x+width-12.5,y+10
Line x+width,y,x+width-12.5,y+10

End If



;Draws the 3 main parts of the dropdown, with all the math from above: Main box, DD button, 
Rect x,y,width,20,0
Rect x+width-25,y,25,20,0
Text x,y,words


;Takes the Creation info and Equals it to the messed with dropdown info.
Creation_Info\scrolly = scrolly
Creation_Info\drawdrop = drawdrop
Creation_Info\shown = shown
Creation_Info\words$ = words$

End Function

;-------------------------------------------------------------------------
;                          DeleteDropDown(DropDown_ID)
;-------------------------------------------------------------------------

Function DeleteDropDown(ID.Dropdown)
Delete ID
End Function

;----------------------------------------------------------------------
;             Example program
;----------------------------------------------------------------------



Graphics 300,500,16,2

SetBuffer BackBuffer()

;---------------------

Type list
   Field name$
   Field ID
End Type

For i=1 To 200

list1.list = New list
list1\name="bah"+i
list1\ID=1

Next

For i=1 To 400

list1.list = New list
list1\ID=2
list1\name="test"+i

Next

;---------------------------

dd1.DropDown=CreateDropDown(50,0,100,1) ; ID=1
dd2.DropDown=CreateDropDown(180,0,100,1) ; ID=2
dd3.DropDown=CreateDropDown(50,250,100,2) ; ID=3
dd4.DropDown=CreateDropDown(180,250,100,2) ; ID=4

;--------------------------------------
While Not KeyHit(1)
Cls

CursorX = MouseX()
CursorY = MouseY()
CursorHit = MouseHit(1)


For CheckDD.DropDown = Each DropDown
DrawDropDowns(CheckDD.DropDown)
Next

Text 0,460,"Press 2-5 to delete Dropdowns"
If KeyHit(3) DeleteDropDown(dd1)
If KeyHit(4) DeleteDropDown(dd2)
If KeyHit(5) DeleteDropDown(dd3)
If KeyHit(6) DeleteDropDown(dd4)




Delay 5
Flip
Wend
