; ID: 2691
; Author: matibee
; Date: 2010-04-03 16:28:48
; Title: Deformable terrain
; Description: Quick and un-optimised deformable terrain test

SuperStrict 

' Horizontal line type
Type hline
	Field _start:Int
	Field _end:Int
	Field y:Int
	
	Method draw()
		DrawLine( _start, y, _end, y )
	End Method 
	
	Function Create:hline ( s:Int, e:Int, y:Int )
		Local hl:hline = New hline
		hl._start = s
		hl._end = e
		hl.y = y
		Return hl
	End Function 
	
	' Subtract another hline from this one, if this line is
	' deleted completely this function returns true.
	'
	' If this line is split, hlDest contains the newly created
	' line.
	Method Split:Int ( hl:hline, hlDest:hline Var )
		If ( hl.y <> y ) Return False 
		If ( _start < hl._start )
			If ( _end > hl._end )
				hlDest = hline.Create( hl._end, _end, y )
				_end = hl._start
			Else If ( _end > hl._start )
				_end = hl._start
			End If 
		Else If ( _start < hl._end )
			If ( _end < hl._end )
				Return True
			Else 
				_start = hl._end
			End If 
		End If 
		Return False 
	End Method 
	
	Method Move( _x:Int, _y:Int )
		_start :+ _x
		_end :+ _x
		y :+ _y
	End Method 
	
	Method Collide:Int ( _x:Int, _y:Int )
		If ( _y = y )
			If ( _x >= _start And _x <= _end )
				Return True 
			End If 
		End If 
		Return False 
	End Method 
	
End Type 

' A shape created from the horizontal lines..
Type hshape
	Field lines:TList
	Field x:Int, y:Int
	
	Function Create:hshape ()
		Local hs:hshape = New hshape
		hs.lines = New TList
		Return hs
	End Function 
	
	Method Draw()
		For Local hl:hline = EachIn lines
			hl.Draw()
		Next 
	End Method 
	
	Method AddLine( s:Float, e:Float, y:Float )
		lines.AddLast( hline.Create( s, e, y ) )
		SortList( lines, True, hshape.SortLinesByHeight )
	End Method 
	
	Function SortLinesByHeight:Int ( h0:Object, h1:Object )
		Return hline(h1).y < hline(h0).y
	End Function 
	
	Method Position( _x:Int, _y:Int )
		Local xmove:Int = _x - x
		Local ymove:Int = _y - y
		x :+ xmove
		y :+ ymove
		If ( xmove Or ymove )
			For Local hlThis:hline = EachIn lines
				hlThis.Move( xmove, ymove )
			Next
		End If 
	End Method 
	
	Method Subtract( hs:hshape )
		For Local hlOther:hline = EachIn hs.lines
			For Local hlThis:hline = EachIn lines
				Local hlNew:hline
				If ( hlOther.y < hlThis.y ) Exit 
				If ( hlThis.Split( hlOther, hlNew ) )
					lines.Remove( hlThis )
				End If 
				If ( hlNew ) 
					lines.AddLast( hlNew )
					SortList( lines, True, hshape.SortLinesByHeight )
				End If 
			Next
		Next 
	End Method 
	
	Method CollidePixel:Int ( x:Int, y:Int )
		For Local hlThis:hline = EachIn lines
			If ( hlThis.y > y ) 
				Return False 
			Else If ( hlThis.y = y And hlThis.Collide( x, y ) )
				Return True 
			End If 
		Next 
		Return False 
	End Method 
	
End Type 

Type phyxel ' simple physics pixel
	Field x:Float
	Field y:Float 
	Field vel:Float
	
	Function Create:phyxel ( x:Float, y:Float )
		Local p:phyxel = New phyxel
		p.x = x
		p.y = y
		Return p
	End Function 
	
	Method Draw()
		DrawRect( x, y, 1, 1 )
	End Method 
	
	Method Update( terrain:hshape )
		If ( Not terrain.CollidePixel( x, y + vel + 0.5 ) )
			vel :+ 0.1
			If ( vel > 0.98 ) vel = 0.98
		Else 
			vel :* -( 0.4 + RndFloat() * 0.5 )
		End If 
		y :+ vel
		If ( y > 600 ) y = 600
	End Method 
	
End Type 



' Simple test
Graphics 800, 600

' The first shape is the terrain
Local hs1:hshape = hshape.Create()
For Local t:Int = 300 To 599
	hs1.AddLine( 0, 799, t )
Next 

' The second shape is the eraser
Local hs2:hshape = hshape.Create()
For Local t:Int = 0 To 30
	hs2.AddLine( 0, 40, t )
Next 
hs2.AddLine( 1, 39, 31 )
hs2.AddLine( 2, 38, 32 )
hs2.AddLine( 4, 36, 33 )
hs2.AddLine( 6, 34, 34 )
hs2.AddLine( 8, 32, 35 )
hs2.AddLine( 11, 29, 36 )
hs2.AddLine( 15, 25, 37 )

' a list of falling pixels.
Local phyxelList:TList = New TList
For Local t:Int = 0 To 200
	phyxelList.AddLast( phyxel.Create( Rand( 5, 795 ), Rand( 20, 80 ) ) )
Next 


While Not AppTerminate()
	hs1.Subtract( hs2 )
	Cls
	SetColor( 0,0,255 )
	hs1.draw()
	SetColor( 255, 0, 0 )
	hs2.Position( MouseX(), MouseY() )
	hs2.draw()
	DrawText( "Line segments: " + hs1.lines.Count() , 0, 0 )
	SetColor( 0, 255, 0 )
	For Local p:phyxel = EachIn phyxelList
		p.Update( hs1 )
		p.Draw()
	Next 
	Flip	
Wend
