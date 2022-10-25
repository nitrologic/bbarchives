; ID: 2757
; Author: beanage
; Date: 2010-08-27 17:54:01
; Title: Position to Iso Index
; Description: Converts from a 2d position to the x/y index of the underlying isometric tile.

SuperStrict

AppTitle = "Isometric Index Algorithm Demo"

Global TILESIZE:Int = 50
Global DEBUGINFO:Double[7] '[n,y,m,x,h,m_,n_]

Type isoWorld
	Field _terrain:isoTerritory[][] '[y][x]
	Field _scale:Double = TILESIZE
	Field _tile_w:Int = 2
	Field _tile_h:Int = 1
	Field _width:Double = 500.0
	Field _height:Double = 500.0
	
	Method _initTerritory( divx_:Int, divy_:Int, load_instant_:Int )
		Local w_:Double = _width/ Double divx_
		Local h_:Double = _width/ Double divx_
		Local m_:Int = w_/ _scale
		Local n_:Int = h_/ _scale
		
		_terrain = _terrain[..divy_ ]
		For Local j_:Int = 0 Until divy_
			_terrain[ j_ ] = _terrain[ j_][..divx_ ]
			For Local i_:Int = 0 Until divx_
				_terrain[ j_][i_ ] = isoTerritory.Create(Self, i_, j_, m_, n_, w_, h_)
			Next
		Next
	End Method
	
	Method getTileScale:Double()
		Return _scale
	End Method
	
	Method getTileHWratio:Double()
		Return (Double _tile_h)/(Double _tile_w)
	End Method
	
	Method getTerritory:isoTerritory( i_:Int, j_:Int )
		If (j_< _terrain.Length) And (j_>= 0)
			If (i_< _terrain[ j_ ].Length) And (i_>= 0)
				Return _terrain[ j_][i_ ]
			End If
			
		End If
	End Method
End Type

Type isoTerritory
	Field _world:isoWorld
	Field _w:Double
	Field _h:Double
	Field _i:Int
	Field _j:Int
	Field _objects:Object[][] '[x][y] TList|isoObject
	
	Function Create:isoTerritory( world_:isoWorld, i_:Int, j_:Int, m_:Int, n_:Int, w_:Double, h_:Double )
		Local ret_:isoTerritory = New isoTerritory
		ret_._world = world_
		ret_._w = w_
		ret_._h = h_
		ret_._i = i_
		ret_._j = j_
		'init object list arrays
		ret_._objects = ret_._objects[..n_ ]
		For Local k_:Int = 0 Until n_
			ret_._objects[ k_ ] = ret_._objects[ k_ ][..m_ ]
		Next
		
		Return ret_
	End Function

	Method getNumTilesW:Int()
		Return _objects[0].Length
	End Method
	
	Method getNumTilesH:Int()
		Return _objects.Length
	End Method

	Function DoublesAreEqual:Int( d1_:Double, d2_:Double )
		Return Abs(d1_- d2_)< .00001!
	End Function
	
	Method getTileIndizesAtPosition:Int( pos_:Double[], out_i_:Int Ptr, out_j_:Int Ptr ) 'returns false if no tile at that position. pos must be relative to the territory.
		If pos_.Length> 1 'just to make it sure
			Local halfsize_:Double = _world.getTileScale()/ 2
			Local y_:Double = pos_[1] 'y pos
			Local n_:Int = y_/ halfsize_ 'tile line index (presumption)
			If n_ Then n_:- 1
			
			'################
			DEBUGINFO[2] = n_ 
			'################
			y_ = (n_+ 1)* halfsize_ - y_ 'make y relative to tile
			'################
			DEBUGINFO[1] = y_
			'################
			Local off_:Double = ( n_ Mod 2 )* halfsize_ 'tile border function offset
			Local m_:Int = ( pos_[0]- off_ )/ _world.GetTileScale() 'tile row index (presumption)
			'################
			DEBUGINFO[4] = m_
			'################
			Local x_:Double = ( ( pos_[0] - off_ ) Mod _world.getTileScale() )+ off_
			'################
			DEBUGINFO[3] = x_
			'################
			
			out_i_[0] = m_
			out_j_[0] = n_
			If Not DoublesAreEqual(x_, halfsize_+ off_) 'calculate h
				Local h_:Double
				
				If x_< ( halfsize_+ off_ )
					h_ = x_- off_
					If Abs(y_)> h_ 'find neighbouring tile..
						If m_ And Not off_
							out_i_[0]:- 1
						ElseIf ..
							( off_ And ( Abs(y_) < (off_- x_) ) And Not m_ ) .. 'this covers a special case where <n> is indented (off > 0) and <m> is zero.
							Or Not ( off_ Or m_ )
						
							Return False
						End If
					Else
						Return True
						
					End If
				Else
					h_ = halfsize_- ( x_- off_- halfsize_ )
					If Abs(y_)> h_ 'find neighbouring tile..
						If off_ And ( m_ < ( getNumTilesW()- 1 ) )
							out_i_[0]:+ 1
						ElseIf off_
							Return False
						End If
					Else
						Return True
						
					End If
				
				End If
				'################
				DEBUGINFO[0] = h_
				'################
				If ( y_< 0 ) And ( n_< getNumTilesH()- 1 )
					out_j_[0]:+ 1
				ElseIf ( y_> 0 ) And n_
					out_j_[0]:- 1
				Else
					Return False
				End If
			
			'################
			Else
			DEBUGINFO[4] = -1
			'################
			End If
			
		End If
		Return True
	End Method
End Type

Global world:isoWorld = New isoWorld
world._initTerritory(1, 1, 0)
Global territory:isoTerritory = world.getTerritory(0, 0)

Function drawTerritory()
	Local mpos_:Double[] = [Double MouseX(), Double MouseY()]
	Local reti_:Int
	Local retj_:Int
	
	If Not territory.getTileIndizesAtPosition(mpos_, VarPtr reti_, VarPtr retj_)
		reti_ = -1
		retj_ = -1
		
	End If
	DEBUGINFO[5] = retj_
	DEBUGINFO[6] = reti_
	For Local j_:Int = 0 Until territory.getNumTilesH()
		For Local i_:Int = 0 Until territory.getNumTilesW()
			If ( reti_ = i_ ) And ( retj_ = j_ )
				SetColor 255, 0, 0
			Else
				SetColor 255, 255, 255
			End If
			Local x_:Int = i_* TILESIZE + ( j_ Mod 2 )* TILESIZE/ 2
			Local y_:Int = j_* TILESIZE/ 2
			Local swh_:Int[] = [TextWidth(String(i_)), TextHeight(String(i_))]
			
			DrawLine x_+ 1, y_+ TILESIZE/ 2, x_+ TILESIZE/ 2, y_+ 1
			DrawLine x_+ TILESIZE/ 2, y_+ 1, x_+ TILESIZE - 1, y_+ TILESIZE/ 2
			DrawLine x_+ TILESIZE - 1, y_+ TILESIZE/ 2, x_+ TILESIZE/ 2, y_+ TILESIZE - 1
			DrawLine x_+ 1, y_+ TILESIZE/ 2, x_+ TILESIZE/ 2, y_+ TILESIZE - 1
			DrawText i_, x_ + TILESIZE/ 2 - swh_[0]/ 2, y_ + TILESIZE/ 2 - swh_[1]/ 2
		Next
	Next
End Function

Function DrawDebugInfo:Int( x_:Int, y_:Int, boxw_:Int, caption_:String, value_:String )
	caption_:+ ":"
	Local w_:Int = TextWidth(caption_)
	Local h_:Int = TextHeight(caption_)
	
	SetColor 128, 128, 128
	DrawRect x_- 5, y_- 5, boxw_, h_+ 10
	SetColor 255, 255, 255
	DrawText caption_, x_, y_
	SetColor 0, 0, 0
	DrawText value_, x_+ boxw_- TextWidth(value_)- 10, y_
	Return h_+ 10
End Function

Graphics 800, 600
Repeat
	Cls
	drawTerritory
	Local y_:Int = 600- (TextHeight("X") + 10)
	
	For Local i_:Int = 0 To 6
		Local caption_:String
		
		Select i_
			Case 6
				caption_ = "Final horizontal index	"
			Case 5
				caption_ = "Final vertical index"
			Case 2 'n
				caption_ = "Vertical index guess"
			Case 1 'y
				caption_ = "Y relative to y index guess"
			Case 4 'm
				caption_ = "Horizontal index guess"
			Case 3 'x
				caption_ = "X relative to tile"
			Case 0 'h
				caption_ = "Calculated h(x)"
		End Select
		DrawDebugInfo 10, y_, 400, caption_, String(DEBUGINFO[i_])[..5]
		y_:- 30
	Next
	SetColor 0, 255, 0
	DrawLine MouseX(), MouseY()- 10, MouseX(), MouseY()+ 10
	DrawLine MouseX()- 10, MouseY(), MouseX()+ 10, MouseY()
	Flip False
Until AppTerminate() Or KeyHit(KEY_ESCAPE)
