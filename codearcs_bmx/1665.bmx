; ID: 1665
; Author: Nilium
; Date: 2006-04-14 05:58:04
; Title: Max2D Stack
; Description: A stack similar to the OpenGL matrix/attrib stacks for Max2D's properties

Strict

Global State:State2D = New State2D

Function Push2D( )
    State = State.Push( )
End Function

Function Pop2D( )
    State = State.Pop( )
End Function

Type State2D
    Const CF_TRANSFORM = 0
    Const CF_BLEND = 1
    Const CF_VIEWPORT = 2
    Const CF_HANDLE = 3
    Const CF_ORIGIN = 4
    Const CF_ALPHA = 5
    Const CF_COLOR = 6
    Const CF_CLSCOLOR = 7
    
    Field _pred:State2D
    Field sx#=1, sy#=1
    Field rot#
    Field r%,g%,b%,a#
    Field ox#, oy#
    Field hx#, hy#
    Field vx%, vy%, vw%, vh%
    Field blend_% = SOLIDBLEND
    Field cr%, cg%, cb%
    Field cf[]=[1,1,1,1,1,1,1,1]
    
    ' Does not copy its predecessor
    Method Clone:State2D( )
        Local s:State2D = New State2D
        MemCopy( Varptr s.sx, Varptr sx, 76 )
        s.cf = cf[..]
        Return s
    End Method
    
    ' Creates a copy of the top state (Self prior to Push()ing) and sets self as its predecessor
    Method Push:State2D( )
        Local s:State2D = Clone( )
        s._pred = Self
        Return s
    End Method
    
    Method Pop:State2D( )
        Assert _pred,"State2D error: stack underflow"
        Local p:State2D = _pred
        _pred = Null
        p.Bind( )
        Return p
    End Method
    
    Method Scale( x#, y# )
        sx :* x
        sy :* y
        cf[CF_TRANSFORM]=True
    End Method
    
    Method Rotate( r# )
        rot :+ r
        cf[CF_TRANSFORM]=True
    End Method
    
    Method ScaleAbsolute( x#, y# )
        sx = x
        sy = y
        cf[CF_TRANSFORM]=True
    End Method
    
    Method Rotation( r# )
        rot = r
        cf[CF_TRANSFORM]=True
    End Method
    
    Method Color( r%, g%, b% )
        Self.r = r
        Self.g = g
        Self.b = b
        cf[CF_COLOR]=True
    End Method
    
    Method Alpha( a# )
        Self.a = a
        cf[CF_ALPHA]=True
    End Method
    
    Method ClearColor( r%, g%, b% )
        cr = r
        cg = g
        cb = b
        cf[CF_CLSCOLOR]=True
    End Method
    
    Method Handle( hx#, hy# )
        Self.hx = hx
        Self.hy = hy
        cf[CF_HANDLE]=True
    End Method
    
    Method Viewport( x%, y%, w%, h% )
        vx = x
        vy = y
        vw = w
        vh = h
        cf[CF_VIEWPORT]=True
    End Method
    
    Method Blend( b% )
        blend_ = b
        cf[CF_BLEND]=True
    End Method
    
    Method Origin( ox#, oy# )
        Self.ox = ox
        Self.oy = oy
        cf[CF_ORIGIN]=True
    End Method
    
    Method MoveOrigin( ox_#, oy_# )
        Self.ox :+ ox_
        Self.oy :+ oy_
        cf[CF_ORIGIN]=True
    End Method
    
    Method Identity( )
        sx = 1
        sy = 1
        rot = 0
        
        r = 255
        g = 255
        b = 255
        
        a = 1
        
        ox = 0
        oy = 0
        
        hx = 0
        hy = 0
        
        vx = 0
        vy = 0
        vw = GraphicsWidth( )
        vh = GraphicsHeight( )
        
        blend_ = SOLIDBLEND
        
        cr = 0
        cg = 0
        cb = 0
        
        cf = [1,1,1,1,1,1,1,1]
    End Method
    
    Method Bind( )
        Update( True )
    End Method
    
    Method Update( Force=0 )
        If cf[CF_ORIGIN] Or Force Then SetOrigin( ox, oy )
        If cf[CF_HANDLE] Or Force Then SetHandle( hx, hy )
        If cf[CF_TRANSFORM] Or Force Then SetTransform( rot, sx, sy )
        If cf[CF_VIEWPORT] Or Force Then SetViewport( vx, vy, vw, vh )
        If cf[CF_COLOR] Or Force Then SetColor( r, g, b )
        If cf[CF_ALPHA] Or Force Then SetAlpha( a )
        If cf[CF_BLEND] Or Force Then SetBlend( blend_ )
        If cf[CF_CLSCOLOR] Or Force Then SetClsColor( cr, cg, cb )
        cf = [0,0,0,0,0,0,0,0]
    End Method
End Type
