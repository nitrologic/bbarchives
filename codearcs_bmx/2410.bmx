; ID: 2410
; Author: Jesse
; Date: 2009-02-09 06:54:18
; Title: Old School tunnel
; Description: graphics transformation

' Last iteration's tick value
'trancelation from C using SDL to bmax
'original code found at: http://sol.gfxile.net/gp/ch17.html
' by Jari Komppa AKA Sol. Translated by Jesse.

SuperStrict
' Screen surface
Global gScreen:TPixmap
Global GscreenPtr:Int Ptr
Global PITCH:Int
' Texture surface
Global gTexture:TPixmap
Global gTexturePtr:Int Ptr

' Look-up table
Global  gLut:Short[]

' Distance mask
Global gMask:Int[] 

' Screen width
Const WIDTH:Int = 480
' Screen height
Const HEIGHT:Int = 320

' Physics iterations per second
Const PHYSICSFPS:Int = 10

' Last iteration's tick value
Global gLastTick:Int


Function init()

  Local temp:TPixmap = LoadPixmap("texture17.bmp")
  gTexture = ConvertPixmap(temp,PF_RGBA8888)
  gTexturePtr = Int Ptr(gTexture.pixels)

  gLut = New Short[WIDTH * HEIGHT * 4]
  gMask = New Int[WIDTH * HEIGHT * 4]

  Local i:Int, j:Int
  Local distance:Int
    For i = 0 Until HEIGHT * 2
  
    For j = 0 Until WIDTH * 2
    
      Local xdist:Int = j - WIDTH
      Local ydist:Int = i - HEIGHT
     
      ' round
      distance:Int = Sqr(xdist * xdist + ydist * ydist)
  
      ' square
 
	 'If (Abs(xdist) > Abs(ydist)) distance = Abs(xdist) Else distance = Abs(ydist)
	
      ' diamond
      'distance = (Abs(xdist) + Abs(ydist)) / 2
     
      ' flower
      'distance :+ (Sin(ATan2(xdist,ydist) * 5)*8)*57.2957795
     
      If (distance <= 0) distance = 1
  
      Local d:Int = distance
      If (d > 255) d = 255
      gMask[i * WIDTH * 2 + j] = d * $010101
     
      distance = (64 * 256 / distance) & $ff
  
      Local angle:Int = (((ATan2(Float(xdist), Float(ydist)) / Pi*2) + 1.0) * 128)*0.0174532925
  
      gLut[i * WIDTH * 2 + j] = (distance Shl 8) + angle
    Next
  Next
End Function 


Function blend_mul:Int(source:Int, target:Int)

  Local sourcer:Int = (source Shr  0) & $ff
  Local sourceg:Int = (source Shr  8) & $ff
  Local sourceb:Int = (source Shr 16) & $ff
  Local targetr:Int = (target Shr  0) & $ff
  Local targetg:Int = (target Shr  8) & $ff
  Local targetb:Int = (target Shr 16) & $ff

  targetr = (sourcer * targetr) Shr 8
  targetg = (sourceg * targetg) Shr 8
  targetb = (sourceb * targetb) Shr 8

  Return (targetr Shl  0) | (targetg Shl  8) | (targetb Shl 16)
End Function 


Function render()
    
  ' Ask For the time in milliseconds
  Local tick:Int = MilliSecs()

  If (tick <= gLastTick) 
  
    Delay(1)
    Return
  EndIf

  While (gLastTick < tick)
  
    ' 'physics' here

    gLastTick :+ 100 / PHYSICSFPS
  Wend

  Local posx:Int = (Sin((tick * 0.000645234)*57.2957795) + 1) * WIDTH / 2
  Local posy:Int = (Sin(tick * 0.000445234*57.2957795) + 1) * HEIGHT / 2
  Local posx2:Int =(Sin(-tick * 0.000645234*57.2957795) + 1) * WIDTH / 2
  Local posy2:Int =(Sin(-tick * 0.000445234*57.2957795) + 1) * HEIGHT / 2

  Local i:Int, j:Int
  For i = 0 Until HEIGHT
  
    For j = 0 Until WIDTH
    
      Local lut:Int = gLut[(i + posy) * WIDTH * 2 + j + posx] - gLut[(i + posy2) * WIDTH * 2 + j + posx2]
      Local mask:Int = gMask[(i + posy) * WIDTH * 2 + j + posx]
      Local mask2:Int = gMask[(i + posy2) * WIDTH * 2 + j + posx2]

      gScreenPtr[(j) + (i) * PITCH] =.. 
        blend_mul(..
        blend_mul(..
        gTexturePtr[((lut + tick / 32) & $ff) +.. 
                                         (((lut Shr 8) + tick / 8) & $ff) *.. 
                                         (gTexture.pitch/4)],..
         mask),..
         mask2)
    Next
  Next

End Function 


  
' Attempt To Create a WIDTHxHEIGHT window with 32bit pixels.

Graphics width,height
gScreen = CreatePixmap(WIDTH, HEIGHT ,PF_RGBA8888)
gScreenPtr = Int Ptr(gScreen.pixels)
PITCH = gScreen.pitch/4
init()

' Main loop: loop Forever.

Repeat
  Cls
  render()
  DrawPixmap gScreen,0,0
  Flip(0)
Until KeyDown(KEY_ESCAPE)
