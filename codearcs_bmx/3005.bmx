; ID: 3005
; Author: Jesse
; Date: 2012-11-28 00:41:56
; Title: Bitmap Font Generator
; Description: Ditto

SuperStrict


'************************************************** pack ******************************************************
Type TBitMap

	Field x				:Int
	Field y				:Int

	Field gx			:Int
	Field gy			:Int

	Field gw			:Int
	Field gh			:Int

	Field gImg			:TImage

	Field advance		:Int

	Function Create:TBitMap(image:TImage,x:Float,y:Float,adv:Int,w:Int,h:Int)
		
		Local b:TbitMap = New TBitMap

		b.gImg = image

		b.gx = x
		b.gy = y

		b.advance = adv

		b.gw = w
		b.gh = h

		Return b

	End Function
	
	Method Compare:Int(o:Object)

		Local box:TBitMap = TBitMap(o)

		If box.gh*box.gw < gh*gw Return -1
		If box.gh*box.gw > gh*gw Return 1

		Return 0

	End Method

	Method Draw(x:Float,y:Float)

			If gImg DrawImage gImg,x,y

	End Method	

End Type		

Type TBitMapFont
	
	Field bitMap		:Tbitmap[95]
	
	Function Create:TBitmapFont(url:String,size:Int,flags:Int)

		Local oldFont:TImageFont = GetImageFont()
		Local imgFont:TImageFont=LoadImageFont(url,size,flags)

		Local b:TbitMapFont = New TbitMapFont
		If imgfont=Null Return Null

		SetImageFont(imgfont)
		
		For Local i:Int = 0 Until 95
		
			Local n:Int = imgFont.CharToGlyph(i+32)
		
			If n > 0
		
				Local glyph:TImageGlyph = imgFont.LoadGlyph(n)
		
				If glyph
		
					b.bitMap[i] = TBitMap.Create(glyph._image,glyph._x,glyph._y,glyph._advance,glyph._w,glyph._h)			
		
				EndIf
		
			Else
		
			 	b.bitmap[i] = New TBitMap
		
			EndIf
		
		Next
		
		SetImageFont(oldFont)
		
		Return b

	End Function
	
End Type
	
	
	
	
	' --------------------------------------------------
	
Type TPackNode

	Field childNode1:TPackNode
	Field childNode2:TPackNode
	
	Field x:Int,y:Int,w:Int,h:Int
	Field occupied:Int = False
				
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
	
	Method GetRawBoxesArea:Int()
		
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
	
	

Type TFontGenerator

	Global glyphsList:TList = New TList
	
	Global packedList:TList = New TList

	Global packedImg:TImage

	Global myFont:TBitmapFont
	
	Method PackFont(width:Int,height:Int)
		If myFont = Null
			Notify "Please, load a bitmap font first"
			End
		EndIf
		
		glyphsList.Clear()
		
		For Local i:Int =  0 Until 95
			
			glyphsList.addLast(myFont.BitMap[i])		
		
		Next
		
		glyphsList.sort() ' sort by area - optional. Improves space efficiency a little.
	
		'make some random sized boxes
		
		
		Local packer:TPackNode = New TPackNode
		
		packer.setRect(0,0,width,height) ' set bounding area dimensions
		
		
		For Local box:TBitMap = EachIn glyphsList
		
			Local freeArea:TPackNode = packer.pack(box.gw,box.gh)
		
			If freeArea <> Null
		
				box.x = freeArea.x
				
				box.y = freeArea.y
				
				packedList.addLast(box)
		
			Else 
		
				Print "no space left for box "+box.gw+","+box.gh+"!!!"
		
			End If
		Next
	
	End Method
	
	Method PrintInfo()
		If myfont = Null 
			Notify "Please, Load font first"
			End
		EndIf
		Local i:Int = 0

		Print "id,x,y,width,height,xoffset,yoffset,xadvance,page,chnl"

		For Local box:TBitMap = EachIn myFont.bitMap

			Print i+","+box.x+","+box.y+","+box.gw+","+box.gh+","+box.gx+","+box.gy+","+box.advance+","+0

			i :+ 1

		Next

	End Method
	
	Function SaveInfo(url:String)
		If myfont = Null
			Notify "Please Load font first"
			End
		EndIf
		
		CreateFile(url)

		Local file:TStream = OpenFile(url)

		If file

			Local i:Int = 32

			WriteLine(file, "id,x,y,width,height,xoffset,yoffset,xadvance,page,chnl")

			For Local box:TBitMap = EachIn myFont.bitMap

				WriteLine(file,i+","+box.x+","+box.y+","+box.gw+","+box.gh+","+box.gx+","+box.gy+","+box.advance+","+0)

				i :+ 1

			Next

			CloseFile(file)

		Else 

			Notify "Unable to create file "+url
			End

		EndIf

	End Function
	
	Method DisplayBoxes(x:Int,y:Int)
		If myFont = Null 
			Notify "please Load Font first"
			End
		EndIf
		
		For Local box:TBitMap = EachIn packedList

			If box.gImg

				DrawImage box.gImg,x+box.x ,y+box.y	

			EndIf

		Next
	
	End Method
	
	Method MakeBitMap:TPixmap(pixmap:TPixmap)

		For Local box:TBitMap = EachIn packedList


			If box.gImg 

				pixmap.Paste(LockImage(box.gImg),box.x,box.y)

			EndIf

		Next

	End Method
	
	Method GetBoxArea:Int()
	
		If myFont = Null
	
			Notify "Plase Load Font first"
			End
	
		EndIf
			
		Local boxArea:Int=0

		For Local box:TBitMap = EachIn packedList

			boxarea = boxarea + box.gw*box.gh

		Next

		Return boxArea

	End Method	
	
	Method Create:TPixmap(url:String,size:Int,pixWidth:Int,pixHeight:Int,flags:Int = SMOOTHFONT)

		SetBlend Alphablend

		myfont = TBitmapFont.Create(Url,size,flags)

		If myFont = Null 

			Notify "Unable to load Font"

			End

		EndIf
		
		Local pixmap:TPixmap = CreatePixmap(pixWidth,pixHeight,PF_RGBA8888),ClearPixels()
			
		Local starttime:Int = MilliSecs()
			
		packFont(pixWidth,pixHeight)
		
		Local stoptime:Int = MilliSecs()-starttime
		
		MakeBitmap(pixmap)
			
		Return pixmap	
		
	End Method
	
End Type

Global fontGenerator:TFontGenerator = New TFontGenerator


' -------------------------------------

Graphics 640,640

SetBlend ALPHABLEND


'Local FontUrl:String = "/Library/Fonts/Ayuthaya.ttf" 'replace these with wateever font you want to use.
Local FontUrl:String = "/Library/Fonts/Arial Rounded Bold.ttf"
Local size:Int = 40 'modify this to fit all Sprites with in pixmap_width and pixmap_height
Local pixmap_width:Int  = 256 ' size of of bitmap font image to be created
Local pixmap_Height:Int = 256 '       "                 "           "


Local pixmap:TPixmap = FontGenerator.Create(fontUrl,size,pixmap_width,pixmap_height,BOLDFONT| ITALICFONT | SMOOTHFONT)

If pixmap
	
	DrawText "Building, Please wait…..",200,200
	
	Flip()
	SetColor 50,50,50
	DrawRect 64,64,pixmap_width,pixmap_height
	SetColor 255,255,255
	FontGenerator.displayboxes(64,64)
	Local name:String = StripAll(FontUrl)+pixmap_width+"x"+pixmap_Height
	If SavePixmapPNG(pixmap,name+".png",9) = False Print "uanble To save pixmap"
	fontGenerator.SaveInfo(name+".txt")
	Print 
Else
	Print "Unable to create pixmap"
EndIf
Flip
WaitKey
End
