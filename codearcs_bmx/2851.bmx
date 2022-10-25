; ID: 2851
; Author: tesuji
; Date: 2011-05-13 14:57:45
; Title: Box Packing - Guillotine method
; Description: A way of squeezing arbitrary sized rectangles into a finite space

SuperStrict

' Simple guillotine packing example
' Paper on it here : http://clb.demon.fi/files/RectangleBinPack.pdf


Graphics 1024,768
SeedRnd 4

Local boxes:TList = New TList
'make some random sized boxes
For Local i:Int = 1 To 610
	Local box:Tbox = New Tbox
	box.w=Rnd(50)+10
	box.h=Rnd(50)+10
	boxes.addLast(box)		
Next

Local starttime:Int = MilliSecs()

boxes.sort() ' sort by area - optional. Improves space efficiency a little.

Local packer:TPackNode = New TPackNode
packer.setRect(0,0,GraphicsWidth(),GraphicsHeight()) ' set bounding area dimensions
Local displayBoxes:TList = New TList
For Local box:TBox = EachIn boxes
	Local freeArea:TPackNode = packer.pack(box.w,box.h)
	If freeArea <> Null
		box.x = freeArea.x
		box.y = freeArea.y
		displayBoxes.addLast(box)
	Else 
		Print "no space left for box "+box.w+","+box.h+"!!!"
	End If
Next

Local stoptime:Int = MilliSecs()-starttime

'display the boxes
Local boxarea# = 0
Local maxy:Int = 0
Local maxx:Int = 0
SetBlend ALPHABLEND
SetAlpha .5
For Local box:TBox = EachIn displayBoxes
	SetColor 63,127,255
	DrawRect box.x+1 ,box.y+1 ,box.w-2 ,box.h-2
	boxarea = boxarea + box.w*box.h
Next
Local totarea# = GraphicsWidth()*GraphicsHeight()

SetColor 0,0,0
SetBlend ALPHABLEND
SetAlpha .5
DrawRect 0,0,GraphicsWidth(),16
SetColor 255,255,255
DrawText "Boxes - "+(boxes.count())+" | Time - "+stoptime+"ms | Area usage - "+((boxarea*100)/totarea)+"%",0,0

Flip
WaitKey
End

' --------------------------------------------------

Type TPackNode

	Field childNode1:TPackNode
	Field childNode2:TPackNode
	
	Field x:Int,y:Int,w:Int,h:Int
	Field occupied:Int = False
		
	Method toString:String()
		Return "rect : "+x+" "+y+" "+w+" "+h
	End Method
	
	Method setRect(x:Int,y:Int,w:Int,h:Int)
		Self.x = x
		Self.y = y
		Self.w = w
		Self.h = h
	End Method
	
	' recursively split area until it fits the desired size
	Method pack:TPackNode(width:Int,height:Int)
		
    	If (childNode1 = Null And childNode2 = Null) 'If we are a leaf node

        	If occupied Or width > w Or height > h Return Null

        	If width = w And height = h
				occupied = True 
				Return Self
			Else
				splitArea(width,height)
   		     	Return childNode1.pack(width,height)
			End If
        
		Else 
		    ' Try inserting into first child
        	Local newNode:TPackNode = childNode1.pack(width,height)
        	If newNode <> Null Return newNode
        
        	'no room, insert into second
        	Return childNode2.pack(width,height)
		End If
	End Method
	
	Method splitArea(width:Int,height:Int)
		childNode1 = New TPackNode
        childNode2 = New TPackNode
        
        ' decide which way to split
        Local dw:Int = w - width
        Local dh:Int = h - height
        
        If dw > dh Then ' split vertically
            childNode1.setRect(x,y,width,h)
            childNode2.setRect(x+width,y,dw,h)
		Else ' split horizontally
            childNode1.setRect(x,y,w,height)
            childNode2.setRect(x,y+height,w,dh)
		End If	

	End Method
	
End Type

' -------------------------------------

Type TBox
	Field x:Int,y:Int,w:Int,h:Int
	
	Method Compare:Int(o:Object)
		Local box:TBox = TBox(o)
		If box.h*box.w < h*w Return -1
		If box.h*box.w > h*w Return 1
		Return 0
	End Method
End Type
