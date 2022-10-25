; ID: 3250
; Author: Dan
; Date: 2016-01-26 16:16:55
; Title: Sprites2Go - C64 Sprite - BB3d (bb+?!)
; Description: Paint Sprites or use c64 type data statement in your Code.

;======================================================================================
; Project: Sprites 2 Go - C64 sprite drawing method, expandable
;               Save this as c64sprite_inc.bb
; Version: 1.0
; Author: Dan
; Email: -.-
; Copyright: PD
; Description:    Draw Sprites in your Code, or use the C64 sprite data format 
;                   This makes your program Independant on external bitmap 
;                   Usage:
;                   AddPal(r,g,b) ;Adds color to the Palette, Call this multiple times to add More colors
;                             1st palette entry will always be transparent (make sure that if you set this to 0,0,0 your black collor shall be 0,0,1
;                   C64DS (" 1 1 2 3") Draws to the sprite (1 line here). Youll need to draw them manualy, (see at the bottom for the template)
;
;                   C64datspr(dat,newsprite,color1,multicolor=2,color3).  Adds C64 sprite data, 1 byte = 8 pixel to the sprite)
;                             (c64ds and c64datspr functions cannot be mixed/used together to draw to a same spite !, each other saves the current sprite and starts a new one
;
;                   c64_finalize() - needs to be called after your drawing is done or you may miss the last image or the whole sprite bitmap 
;
;                   c64drawsprite (nr,x,y) same as DrawImage, but this one uses your sprite drawings, starting with nr 0, at x,y coordinates
;                             This function is drawing the Sprites to the current buffer, so if you want to have collison detection with ImagesCollide or ImagesOverlap commands,
;                             youll need to make image holders for the sprites, set the buffer to their image buffer
;                             and call this function, with only image number to be copied to the holder (x=0,y=0 are set to default).Then Draw your sprites to the screen and do the collision check.
;
;                   aditionaly, you can use c64color(nr) function to set the foreground collor, from the defined palette
;======================================================================================
;how this works:
;You draw a small picture:
; ####
; #  #     save it into a bigger one:
; #### ---> ##################                                                ##################
;           #  #             #                                                #  #  #  #  #  # #
;           ####             #      --------------copy------------->          ##################
;           #                #                                                #  #  #  #  #  # #
;           ##################                                                ##################
;                              when the big picture is filled,make a copy,save it into a bitmap, 
;                              and reset the big picture.Repeat the process.


;Dont change the lines below, unless you know what you are doing
Const AddPaletteorder$=" 0123456789abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ" ;64 colors (63+transparent)

Dim c64pal(Len(AddPaletteorder$)+1,2)

;Used to hold the Sprite frames in one or more bitmaps 
Type c64sprmap
    Field image
End Type

Type c64data
	Field palcount,imgdrawcount 
	Field dx,dy,maxdrawx
	Field spr_width,spr_height
	Field c64image,maxsprites
	Field imgcount,NrX,NrY,MaxNrX,MaxNrY
	Field DrawTo,picture[1]
	Field collection,recursion
	Field drawn
End Type

Global c64i.c64data = New c64data
Global c64_image.c64sprmap = New c64sprmap
;Dont change the lines above, unless you know what you are doing


;If you have a need to adapt the code to a bigger/smaller size, the next 5 variables should be changed and set up
c64i\maxdrawx=3					;c64datspr() - 3 bytes * 8bits = 24 pixel, make sure your data statement provide this many bytes * c64i\spr_height

;used for C64datSpr() and C64ds()   - needed for both and need to be adjusted manualy
c64i\spr_width=24					;width of the drawn image, for c64 is 3*8=24 !!!        - Correct this manually, if you use your own width/height format
c64i\spr_height=21					;height of the drawn image,c64 image standard is 21 !!! - Correct this manually, if you use your own width/height format

;How many Sprite images shall one bitmap contain:
c64i\MaxNrX=50					;50 images * 24 pixels = 1200 pixels width    ! depends on spr_width   - for C64storeimg()
c64i\MaxNrY=50					;50 images * 21 pixels = 1050 pixels height   ! depends on spr_height   - for C64storeimg()

;#####################################
;Dont change the values below :
c64i\palcount=0					;count added palette colors
c64i\imgdrawcount=-1				;c64ds() to count lines drawn, depends on spr_Height setting !
c64i\dx=1						;c64datspr() - holds the current x drawing position - DONT CHANGE THIS
c64i\dy=0						;c64datspr() - holds the current y drawing position - DONT CHANGE THIS
c64i\imgcount=0					;c64storeImg() - how many images have been stored	- DONT CHANGE THIS
c64i\drawn=0					;Check if something was drawn				  DONT CHANGE This
c64i\NrX=0						;Holder of the current image on the X axis	- DONT CHANGE THIS
c64i\NrY=1						;Holder of the current image on the y axis	- DONT CHANGE THIS
c64i\DrawTo=0					;Draw to image buffer holder				- DONT CHANGE THIS
c64i\collection=0				;How many image pages are created, (when maxnrx + maxnry) are reached 
c64i\c64image=CreateImage(c64i\spr_width,c64i\spr_height)				;imagage to draw sprite !

;Dont Change the values above this line.
;#######################################

Function c64drawsprite (nr,x=0,y=0)
;sprite Nr begins with 0 !
	
	page=(nr)/(c64i\MaxNrX*c64i\MaxNrY)
	xp=((nr) Mod c64i\MaxNrX)*c64i\spr_width
	yp=(((nr-(page*(c64i\MaxNrX*c64i\MaxNrY)))/c64i\MaxNrY))*c64i\spr_height
	z=0
	For tmp.c64sprmap = Each c64sprmap
		If z=page
			DrawImageRect tmp\image,x,y,xp,yp,c64i\spr_width,c64i\spr_height
		EndIf
		z=z+1 
	Next
	
End Function

Function c64datspr(dat,newspr=0,col1=2,col2=-1,col3=-1)
;Usage: if col2 is set to a collor (=>0), then it will be a multi collored sprite !
;Use newspr to force drawing of the next sprite, (this can be left out, if your data has enough bytes to draw 1 whole sprite)
;
;draws 8 pixels of a sprite in a line, using commodore 64 sprite data format
;1 byte equals 8 pixel, where each bit represents 1 pixel
; 
;Spr width = c64i\maxdrawx * 8
;spr height= c64i\spr_height
;bytes needed for drawing = c64i\maxdrawx * c64i\spr_height
	
	SetBuffer ImageBuffer(c64i\c64image)
	
	If newspr=1
	    If c64i\drawn>0 Then C64storeimg() : SetBuffer ImageBuffer(c64i\c64image)
        ClsColor c64pal(0,0),c64pal(0,1),c64pal(0,2)
		Cls
	Else
	    If c64i\imgdrawcount>0 Or c64i\dy>c64i\spr_height-1				;Used to save the sprite from the c64ds function !
			C64storeimg() 
			SetBuffer ImageBuffer(c64i\c64image)
			ClsColor c64pal(0,0),c64pal(0,1),c64pal(0,2)
			Cls
		EndIf
	EndIf	
	
	If col2<0				;Draw Mono (hires) sprites 
		For x=1 To 8
			Select BitState(dat,x)
				Case 0
					C64color(0)
				Case 1
					C64color(col1)
			End Select
			Plot (c64i\dx*8)-x,c64i\dy
		Next
		
	Else					;Draw Multicolored (lowres) sprite
		For x=1 To 8 Step 2
			temp=(BitState(dat,x+1)*2)+BitState(dat,x)
			Select temp
				Case 0
					C64color (0)
				Case 1
					C64color (col1)
				Case 2
					C64color (col2)
				Case 3
					C64color (col3)
			End Select 
			
			Plot (c64i\dx*8)-x,c64i\dy 
			Plot ((c64i\dx*8)-x)-1,c64i\dy
		Next
		
	EndIf
	c64i\drawn=1
	c64i\dx=c64i\dx+1
	If c64i\dx>c64i\maxdrawx
		c64i\dx=1
		c64i\dy=c64i\dy+1
	EndIf
	
	SetBuffer BackBuffer()
End Function

;################################################################

Function c64ds (dat$,newspr=0)
	
	SetBuffer ImageBuffer(c64i\c64image)
	If newspr=1		;Check if new
	    If c64i\drawn>0 Then C64storeimg() : SetBuffer ImageBuffer(c64i\c64image) ;save to bigger pic if something was drawn
        ClsColor c64pal(0,0),c64pal(0,1),c64pal(0,2)  ;set the background collor (just in case)
		Cls
	Else
		If (c64i\imgdrawcount<(c64i\spr_height-1)) And c64i\dy=0	;is the end of the spite reached ? and is c64datspr used 
			c64i\imgdrawcount=c64i\imgdrawcount+1					;if no, add a new line
		Else
        ;Save the image to a background image and start a new sprite
			C64storeimg() : SetBuffer ImageBuffer(c64i\c64image)     ;else save it first before drawing new sprite
			ClsColor c64pal(0,0),c64pal(0,1),c64pal(0,2)
			Cls
		EndIf
	EndIf
	
	If Len(dat$)>c64i\spr_width 		;is the dat$ longer than the Sprite width ?
		tmpx=c64i\spr_width				;then set the tmpx to the length of the sprite
	Else
		tmpx=Len(dat$)					;set it to the width of the sprite (or length of the line (dat$))
	EndIf
	
	For x=0 To tmpx				;Drawing is done here
		C64color (Instr(AddPaletteorder$,Mid$(dat$,x+1,1))-1)		;Choose the color from the char
		Plot x,c64i\imgdrawcount									;Plot the pixel
	Next
	c64i\drawn=1
	SetBuffer BackBuffer()
End Function

Function AddPal(r,g,b)
;r red value, g= green value, b= blue value
	If (r=>0 And r<=$ff) And (g=>0 And g<=$ff) And (b=>0 And b<=$ff)
	;D'OH  ;)
	Else			;invalid collors, set the default to $0
		r=0
		g=0
		b=0
	EndIf
	If c64i\palcount=<Len(AddPaletteorder$)-1
		c64pal(c64i\palcount,0)=r
		c64pal(c64i\palcount,1)=g
		c64pal(c64i\palcount,2)=b
		c64i\palcount=c64i\palcount+1
	EndIf
End Function

Function C64color(number)
	Color c64pal(number,0),c64pal(number,1),c64pal(number,2)
End Function

Function C64storeimg(final=0)
;	DebugLog c64i\DrawTo +" "+ c64i\NrX+"/" +c64i\NrY+" -- " + " "+c64i\imgcount 
	
	c64i\NrX=c64i\NrX+1
	c64i\maxsprites=c64i\maxsprites+1
	
    If c64i\NrX>c64i\MaxNrX 
;		DebugLog "reached 50 img"
		c64i\NrX=1
		c64i\NrY=c64i\NrY+1
	EndIf
	
	If c64i\imgcount>c64i\MaxNrX-1
		iw=50*c64i\spr_width
	Else
		iw=c64i\NrX*c64i\spr_width
	EndIf
	c64i\picture[c64i\DrawTo] = CreateImage(iw,c64i\NrY*c64i\spr_height)
	
	If c64i\imgcount >0 
		CopyRect 0,0,ImageWidth(c64i\picture[(c64i\DrawTo+1) Mod 2]),ImageHeight(c64i\picture[(c64i\DrawTo+1) Mod 2]),0,0,ImageBuffer(c64i\picture[(c64i\DrawTo+1) Mod 2]),ImageBuffer(c64i\picture[c64i\DrawTo])
		FreeImage c64i\picture[(c64i\DrawTo+1) Mod 2]
	EndIf
	
	SetBuffer ImageBuffer(c64i\picture[c64i\DrawTo]) 
	DrawBlock c64i\c64image,(c64i\NrX-1)*c64i\spr_width,(c64i\NrY-1)*c64i\spr_height
	
	If (c64i\NrY=c64i\MaxNrY) And c64i\NrX=c64i\MaxNrX
		c64_finalize(1)
;		DebugLog "finalize 1"
	;set up to a new image
	Else
		If final=0
			c64i\DrawTo=(c64i\DrawTo + 1) Mod 2
			c64i\imgcount=c64i\imgcount+1
		Else
			c64i\recursion=0
		EndIf
	EndIf
	
	SetBuffer BackBuffer()
	c64i\drawn=0
	c64i\imgdrawcount=0						;Reset C64ds + C64datspr function drawings
	c64i\dy=0
	c64i\dx=1
	ClsColor $0,$0,$0
End Function

Function c64_finalize(nr=0)
	
	If nr=0
		If (c64i\dy>0 Or c64i\imgdrawcount>0) 
			C64storeimg(1)
			If c64i\recursion>0
			    c64i\recursion=0
				Goto getout
			Else
				c64_image\image=CopyImage(c64i\picture[c64i\DrawTo])
				MaskImage c64_image\image,c64pal(0,0),c64pal(0,1),c64pal(0,2)
				c64i\collection=c64i\collection+1
				FreeImage c64i\picture[c64i\DrawTo]
				c64i\DrawTo=0
				c64i\NrX=0
				c64i\NrY=1
				c64i\imgdrawcount=0						;Reset C64ds + C64datspr function drawings
				c64i\dy=0
				c64i\dx=1
				c64i\imgcount=0
			EndIf
		EndIf
	Else 
		c64_image\image=CopyImage(c64i\picture[c64i\DrawTo])
		MaskImage c64_image\image,c64pal(0,0),c64pal(0,1),c64pal(0,2)
		c64i\collection=c64i\collection+1
		c64_image.c64sprmap = New c64sprmap
		FreeImage c64i\picture[c64i\DrawTo]
		c64i\DrawTo=0
		c64i\NrX=0
		c64i\NrY=1
		c64i\imgdrawcount=0						;Reset C64ds + C64datspr function drawings
		c64i\dy=0
		c64i\dx=1
		c64i\imgcount=0
		c64i\recursion=c64i\recursion+1
		Return
	EndIf
	
	.getout
	For tmp.c64sprmap = Each c64sprmap   ; clear unused !
		If tmp\image=0 
			Delete tmp
		EndIf
	Next
	
End Function

Function BitState(a,b)
;a = variable
;b = bit number to Check
	a=Mid(Bin$(a),Len(Bin$(a))-(b-1),1)
	Select a
		Case "0"
			Return False
		Case "1"
			Return True
	End Select
End Function

;To display the Template correctly, use Fixed width font, like "Courier New","Lucida Console" or "Terminal"
; this is 24x21 template
;X=     123456789012345678901234	  v-- y
;c64ds("                        ",1);01			;Template ! use overwrite mode to draw inside ""
;C64DS("                        ")	;02			;,1 Starts a new sprite drawing
;c64ds("                        ")	;03			;if the whole line is transparent, you can use " " instead
;c64ds("                        ")	;04
;c64ds("                        ")	;05
;c64ds("                        ")	;06
;c64ds("                        ")	;07
;c64ds("                        ")	;08
;c64ds("                        ")	;09
;c64ds("                        ")	;10
;c64ds("                        ")	;11
;c64ds("                        ")	;12
;c64ds("                        ")	;13
;c64ds("                        ")	;14
;c64ds("                        ")	;15
;c64ds("                        ")	;16
;c64ds("                        ")	;17
;c64ds("                        ")	;18
;c64ds("                        ")	;19
;c64ds("                        ")	;20
;c64ds("                        ")	;21

;Following are standard c64 Palette colors, you can change your own
;AddPal ($00,$00,$00) ; transparent	" "
;AddPal ($01,$00,$00) ; Black		0
;AddPal ($FF,$FF,$FF) ; White		1
;AddPal ($88,$20,$00) ; Red			2
;AddPal ($68,$d0,$a8) ; Cyan		3
;AddPal ($a8,$38,$a0) ; Purple		4
;AddPal ($50,$b8,$18) ; Green		5
;AddPal ($18,$10,$90) ; Blue		6
;AddPal ($f0,$e8,$58) ; Yellow		7
;AddPal ($a0,$48,$00) ; Orange		8
;AddPal ($47,$2b,$1b) ; Brown		9
;AddPal ($c8,$78,$70) ; Light Red	a
;AddPal ($48,$48,$48) ; Dark Gray	b
;AddPal ($80,$80,$80) ; Medium Gray	c
;AddPal ($98,$ff,$98) ; Light Green	d
;AddPal ($50,$90,$d0) ; Light Blue	e
;AddPal ($b8,$b8,$b8) ; Light Gray	f
