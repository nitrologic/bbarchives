; ID: 1440
; Author: tesuji
; Date: 2005-08-08 14:23:24
; Title: Large 2D Image Object
; Description: Splits a pixmap up into multiple images and displays as a single image

' ------------------------------------------------------------------------------------
' Large Image made up of multiple fixed size textures.  
' o - handles rotation and scaling
' o - displays large images irrespective of graphic card limitations.
' tesuji 2005
' -------------------------------------------------------------------------------------

Strict

Const FRAGSIZE = 256 ' maximum image fragment size

' ------------------------------------------------------------------------------------
' example usage
' ------------------------------------------------------------------------------------

Graphics 800,600,0

Local imageUrl:String = "/Users/sean/Pictures/1280x1024_starwars.jpg"   ' <----- edit image file path
Local pixmap:TPixmap = LoadPixmap(imageUrl) 
Local img:BigImage = BigImage.Create(pixmap)

Local scaleinc:Float = .01

While Not KeyHit(KEY_ESCAPE)

    Cls
    img.rotation :+ 1
    img.scale :+ scaleinc
    If img.scale < 0 Or img.scale > 2 scaleinc = -scaleinc
    img.render()
    Flip

Wend
End

' =============================
' Image Fragment
' =============================

Type ImageFragment

    Field img:TImage
    Field x,y
    Field rotation:Float = 0
    Field angle:Double
    Field distance:Double

    ' ----------------------------------
    ' constructor
    ' ----------------------------------
    Function create:ImageFragment(pmap:TPixmap,x:Float,y:Float,w,h)
    
        Local frag:ImageFragment = New ImageFragment
        frag.img = LoadImage(PixmapWindow(pmap,x,y,w,h),0|FILTEREDIMAGE)
        x = (pmap.width*.5) - x
        y = (pmap.height*.5) - y
        frag.x = x
        frag.y = y
        frag.angle = ATan2(y,x)-180
        frag.distance = Sqr(x*x + y*y)

        Return frag
 
    End Function

    ' --------------------
    ' Draw individual tile
    ' --------------------
    Method render(scale:Float,xoff:Float=0,yoff:Float=0,rot:Float=0)

        SetRotation rot
        Local d:Float = Self.distance*scale
        SetScale(scale,scale)
        DrawImage(Self.img,(Cos(rot+Self.angle)*d)+xoff,(Sin(rot+Self.angle)*d)+yoff )

    End Method


End Type


' ==================================
' Big Image
' ==================================

Type BigImage 

    Field pixmap:TPixmap
    Field px,py
    Field fragments:TList
    Field scale:Float = 1
    Field width
    Field height
    Field x:Float = 0
    Field y:Float = 0
    Field rotation:Float = 0

    ' ----------------------------------
    ' constructor
    ' ----------------------------------
    Function create:BigImage(p:TPixmap)

        Local bi:BigImage = New BigImage
        bi.pixmap = p
        bi.width = p.width
        bi.height = p.height
        bi.fragments = CreateList()
        bi.load()

        Return bi

    End Function

    ' -------------------------------------
    ' convert pixmap into image fragments
    ' -------------------------------------
    Method load()

        Local px = 0
        Local py = 0
        Local loading = True

        While (loading)

            'FlushMem
            Local w = FRAGSIZE
            If Self.pixmap.width - px < FRAGSIZE w = Self.pixmap.width - px
            Local h = FRAGSIZE
            If Self.pixmap.height - py < FRAGSIZE h = Self.pixmap.height - py
            Local f1:ImageFragment = ImageFragment.create(Self.pixmap,px,py,w,h)
            ListAddLast Self.fragments,f1
            px:+FRAGSIZE
            If px >= Self.pixmap.width
                px = 0
                py:+FRAGSIZE
                If py >= Self.pixmap.height loading = False
            End If
        
        Wend

    End Method

    ' -----------------
    ' Draw entire image
    ' -----------------
    Method render()

        SetOrigin(GraphicsWidth()*.5,GraphicsHeight()*.5)
        For Local f:ImageFragment = EachIn Self.fragments
            f.render(Self.scale,Self.x,Self.y,Self.rotation)
        Next
        SetOrigin(0,0)

    End Method

End Type
