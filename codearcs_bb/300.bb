; ID: 300
; Author: John Pickford
; Date: 2002-04-21 12:39:41
; Title: FONT MAKER
; Description: AUTOMATICALLY  Create Nice Looking Bitmapped Fonts

[code]
;********************************************************************
;
;     FONT MAKER  by John Pickford 21st April 2002
;
;
;	Creates a bitmap font from any Truetype Font installed on your
;   system.  Font can have a coloured outline and a gradient fill
;
;   Experiment with the editable parameters below
;
;
;   NO NEED TO COMPILE - designed to be run from IDE
;
;
;*********************************************************************




	Const screenwidth=1024
	Const screenheight=768


	Graphics screenwidth,screenheight,32
	SetBuffer FrontBuffer()
	
	
;******************  USER EDITABLE PARAMETERS HERE  **************************************************************

	
	Global font$="arial black"	;MUST be the EXACT name of a font installed on your system
	Const size=29	   ;size of font 8-40  (use trial and error till you get the size you need)
	Const thickness=1  ;thickness of outline value 1-4
	
	;colour gradients
	
	Const botlr#=255,botlg#=255,botlb#=255	     ;color of bottom left corner	
	Const botrr#=255,botrg#=255,botrb#=255	 	 ;color of bottom right corner
	Const toplr#=255,toplg#=00,toplb#=00		 ;color of top left corner
	Const toprr#=255,toprg#=00,toprb#=00		 ;color of top right corner
	
	Const transr=255,trandg=0,transb=255		 ;color of transparent pixels
	Const outliner=0,outlineg=0,outlineb=0		 ;color of outline
	
	;Characters to be rendered into bitmap
	
	Const charset$="0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz!£$%^&*()_+-=;'#:@~,./<>?\|"
	
	
;******************************************************************************************************************

	
	Dim outlinegrid(size*3,size*3)
	Dim gred#(size*3,size*3)
	Dim ggreen#(size*3,size*3)
	Dim gblue#(size*3,size*3)
	Dim width(Len (charset$))
	
	
	Global fwidth,fheight,forgx,forgy
	Global rightmost=-size*2,leftmost=size*2,topmost=size*2,botmost=-size*2	
	myfont=LoadFont (font$,size*1.5,False,False,False)
	SetFont myfont
	
	ClsColor transr,transg,transb
	Cls
	display_font()
	
	End

	

Function find_dimensions(xx,yy,this,do_outline)

	thisl=size*4
	thisr=-size*4

	For f=1 To size*2-2
		For g=1 To size*2-2

			outline=0
			
			GetColor (xx+f,yy+g)

			If ColorRed()=transr And ColorGreen()=transg And ColorBlue()=transb


			

				
				GetColor (xx-1+f,yy+g):If Not (ColorRed()=transr And ColorGreen()=transg And ColorBlue()=transb) Then outline=1
				GetColor (xx-1+f,yy+g-1):If Not (ColorRed()=transr And ColorGreen()=transg And ColorBlue()=transb) Then outline=1
				GetColor (xx-1+f,yy+g+1):If Not (ColorRed()=transr And ColorGreen()=transg And ColorBlue()=transb) Then outline=1
				

				
				GetColor (xx+1+f,yy+g):If Not (ColorRed()=transr And ColorGreen()=transg And ColorBlue()=transb) Then outline=1
				GetColor (xx+1+f,yy+g-1):If Not (ColorRed()=transr And ColorGreen()=transg And ColorBlue()=transb) Then outline=1
				GetColor (xx+1+f,yy+g+1):If Not (ColorRed()=transr And ColorGreen()=transg And ColorBlue()=transb) Then outline=1
			

			
				GetColor (xx+f,yy+g+1):If Not (ColorRed()=transr And ColorGreen()=transg And ColorBlue()=transb) Then outline=1
				GetColor (xx+f,yy+g-1):If Not (ColorRed()=transr And ColorGreen()=transg And ColorBlue()=transb) Then outline=1
			
				
				
				

			Else
			
				If g<topmost Then topmost=g
				If g>botmost Then botmost=g
				If f>rightmost Then rightmost=f
				If f<leftmost Then leftmost=f
				If f>thisr Then thisr=f
				If f<thisl Then thisl=f

	
			EndIf

				outlinegrid (f,g)=outline
			

		Next
	
	Next
	
		width(this)=1+thisr-thisl

	If do_outline

		Color outliner,outlineg,outlineb
		
	For f=1 To size*2-2
		For g=1 To size*2-2

			If outlinegrid (f,g) Then Plot (xx+f,yy+g)
		
		Next
		
	Next	

	EndIf

End Function

	
	

Function display_font()

	nochars=1+Len (charset$)
	
	this=1
	xx=size/2
	yy=size/2
	
	While this<nochars
		
		Color 255,255,0
		;Rect xx-size/2,yy-size/2,size*2,size*2,0
		Color 255,255,255
		Text xx-size/4,yy-size/4,Mid$ (charset$,this,1)
		For f=1 To thickness:find_dimensions (xx-size/2,yy-size/2,this,1):Next
		find_dimensions (xx-size/2,yy-size/2,this,0)
		
		
		this=this+1
	
		
		xx=xx+size*2
		If xx>(screenwidth-size*2)
	
		 xx=size/2
		 yy=yy+size*2
	
		EndIf
	
	
	
	
	
	Wend


	
	this=1
	xx=size/2
	yy=size/2
	
		Color 255,255,0
	
	leftmost=leftmost;-thickness
	rightmost=rightmost;+thickness
	topmost=topmost;-thickness
	botmost=botmost;+thickness
	fwidth=1+rightmost-leftmost
	fheight=1+botmost-topmost
	forgx=leftmost
	forgy=topmost
	
;calc gradients - scaled according to size of largest character

    For x#=forgx To forgx+fwidth
     For y#=forgy To forgy+fheight

		
		xf#=(x-forgx)/fwidth
		yf#=(y-forgy)/fheight
		
		
		rt#=toprr*xf+toplr*(1-xf)
		gt#=toprg*xf+toplg*(1-xf)
		bt#=toprb*xf+toplb*(1-xf)
		
		rb#=botrr*xf+botlr*(1-xf)
		gb#=botrg*xf+botlg*(1-xf)
		bb#=botrb*xf+botlb*(1-xf)
		
		r#=rb*yf+rt*(1-yf)
		g#=gb*yf+gt*(1-yf)
		b#=bb*yf+bt*(1-yf)
		
		
		gred (x,y)=r
		ggreen (x,y)=g
		gblue (x,y)=b
		



     Next
	Next
		
	
	

	Color 255,255,255
	While this<nochars
		
		Color 255,255,255
	;	Rect xx-size/2+forgx-1,yy-size/2+forgy-1,fwidth+2,fheight+2,0
	
		  For x#=forgx To forgx+fwidth
		     For y#=forgy To forgy+fheight

				GetColor (x+xx-size/2,y+yy-size/2)
				
				If ColorRed()=255 And ColorGreen()=255 And ColorBlue()=255
				
				
					Color gred(x,y),ggreen(x,y),gblue(x,y)
					Plot x+xx-size/2,y+yy-size/2
				
				
				EndIf


			 Next
			
	      Next
	
	
		this=this+1
	
		
		xx=xx+size*2
		If xx>(screenwidth-size*2)
	
		 xx=size/2
		 yy=yy+size*2
	
		EndIf
	
	
	
	
	
	Wend


	imagefile=CreateImage (fwidth*nochars,fheight) 


	this=1
	xx=size/2
	yy=size/2
	
	While this<nochars
		
		Color 255,255,255
		
		CopyRect xx-size/2+forgx,yy-size/2+forgy,fwidth,fheight,(this-1)*fwidth,0,FrontBuffer(),ImageBuffer(imagefile)


		this=this+1
	
		
		xx=xx+size*2
		If xx>(screenwidth-size*2)
	
		 xx=size/2
		 yy=yy+size*2
	
		EndIf
	
	
	
	
	
	Wend
	
	filename$="Font["+font$+"] " + "(" + Str$(fwidth) + "x" + Str$(fheight) + ").bmp"

	SaveImage (imagefile,filename$) 


	filename$="Font["+font$+"] " + "(" + Str$(fwidth) + "x" + Str$(fheight) + ").txt"

	DeleteFile (filename$)
	file=WriteFile (filename$)

	WriteLine (file,"[charset]")
	WriteLine (file,charset$)
	WriteLine (file,"[number of frames]")
	WriteLine (file,Len(charset$))
	WriteLine (file,"[frame size]")
	WriteLine (file,fwidth)
	WriteLine (file,fheight)
	WriteLine (file,"[mask color]")
	WriteLine (file,transr)
	WriteLine (file,transg)
	WriteLine (file,transb)
	WriteLine (file,"[width table]")
	For f=1 To Len (charset$)
		WriteLine (file,width(f))
	Next	



End Function

[/code]
