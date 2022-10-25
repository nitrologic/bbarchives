; ID: 3210
; Author: codermax
; Date: 2015-06-07 13:16:06
; Title: 2D Grid Class
; Description: Flexible 2D grid with draw/selection functions.

Type GridData
	Field X:Float,Y:Float,W:Float,H:Float, VS:Float,HS:Float
	Field CW:Float, CH:Float, CursorPos:Vector2, Deselect:Int=0
	
	'Allows users to create specific grids in the main loop with one only line of code.
	Function Create:GridData(A:Float,B:Float,C:Float,D:Float,E:Float,F:Float)
		Local tmp:GridData=New GridData
			tmp.CursorPos=Vector2.Create(0,0)
			tmp.X=A tmp.Y=B tmp.W=C tmp.H=D
			tmp.VS=E tmp.HS=F
			tmp.CW=tmp.W/tmp.VS tmp.CH=tmp.H/tmp.HS
		Return tmp
	End Function

	'Updates grid selection.
	Method Update()
		Local MV2:Vector2=Vector2.Create(0,0)	
			MV2=SnapToGrid()
		If MouseDown(1) And MV2.X<>-1 
			CursorPos.Set(MV2.X,MV2.Y) Deselect=0
		EndIf
		If MouseHit(2) And MV2.X<>-1 
			Deselect:+1 If Deselect>1 Then Deselect=0
		EndIf
	End Method
	
	'Draws a 2D grid and draws the grid cursor.
	Method Draw()
		'calculate the grid cell size.
			Local divX:Float=W/VS, divY:Float=H/HS
		
		'draw the vertical lines.
			For Local x1:Int=0 To VS
				DrawLine(X+(divX*x1),Y,X+(divX*x1),Y+H)
			Next
			
		'draw the horizontal lines.
			For Local y1:Int=0 To HS
				DrawLine(X,Y+(divY*y1),X+W,Y+(divY*y1))
			Next
			
			SetAlpha 0.3 SetColor 0,0,255
				If Deselect=0
					DrawRect X+(CW*CursorPos.X),Y+(CH*CursorPos.Y),CW,CH
				EndIf
				
			SetAlpha 1 SetColor 255,255,255
	End Method
	
	Rem
		Makes sure the mouse selects cells inside the grid properly.
		Outputs a 2D vector representing the mouse's current cell position on the grid.
		A 2D Vector value of -1,-1, that means that the mouse cursor is outside the grid.
	End Rem
	Method SnapToGrid:Vector2()
		Local V2:Vector2=Vector2.Create(-1,-1), MX:Float, MY:Float
		Local divX:Float=W/VS, divY:Float=H/HS
			
		If RectsOverlap(MouseX(),MouseY(),1,1, X,Y,W,H)=True
			V2.X=Int( (MouseX()-X)/divX ) V2.X=Clamp( V2.X, 0, VS-1 )
			V2.Y=Int( (MouseY()-Y)/divY ) V2.Y=Clamp( V2.Y, 0, HS-1 )
		EndIf
		
		Return V2	
	End Method

	Rem
		Outputs an integer that represents the cumulative grid cursor position.
		Purpose is for determining image frames, based on grid selection.
	End Rem
	Method GetRowPos:Int()
		Local i:Int=0
			i=(HS*CursorPos.Y)+CursorPos.X
		Return i
	End Method
	
End Type

'REQUIRED FOR 2D GRID CLASS TO WORK.
'extra 2D vector class.
Type Vector2
	Field X:Float,Y:Float
	
	Function Create:Vector2(A:Float,B:Float)
		Local tmp:Vector2=New Vector2
			tmp.X=A tmp.Y=B
		Return tmp
	End Function
	
	Method Set(A:Float,B:Float)
		X=A Y=B
	End Method
End Type

'extra functions.
Function RectsOverlap:Int(x0:Double,y0:Double,w0:Double,h0:Double,x2:Double,y2:Double,w2:Double,h2:Double)
            If (x0 > (x2 + w2) Or (x0 + w0) < x2)
        	    Return 0
			EndIf
            If (y0 > (y2 + h2) Or (y0 + h0) < y2)
          	 	Return 0
			EndIf
          	  Return 1
End Function

Function Clamp:Float(V:Float,MinV:Float,MaxV:Float)
	If V<MinV Then V=MinV
	If V>MaxV Then V=MaxV
	Return V
End Function
