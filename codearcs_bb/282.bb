; ID: 282
; Author: toxic_waste
; Date: 2002-03-26 16:36:19
; Title: Wierd rtc colortexture
; Description: Nothing special, just a nice looking effect - sort of plasma-like.

; ##############################################################
; #                                                            #
; # SIMPLE EXAMPLE FOR REALTIME-CALCULATED PIXEL-TEXTURE       #
; # This is some sort of mutation of the fire-algorythm.       #
; # I hope this doesn't flicker to death on slow PCs...        #
; # This is nothing special at all, but quite beautiful in my  #
; # opinion. BB3D only!                                        #
; # Please excuse some of the odd-named variables, i'm from    #
; # germany and this piece of code was originally commented    #
; # and coded for www.blitzforum.de                            #
; #  -ToXic Waste                                              #
; #                                                            #
; ##############################################################

Graphics3D 640,480,16,0

fnt=LoadFont("Arial Black",170,True,False,False)
If Not fnt Then End
SetFont fnt

camera=CreateCamera()
MoveEntity camera,0,0,-10
CameraZoom camera,1.6

Global texture=CreateImage (64,68) ;Image is higher than texture.. See further down for explanation
Global txtr=CreateTexture (64,64)

cube=CreateCube()
EntityTexture cube,txtr,0,0
licht=CreateLight(1)
LightColor licht,150,120,190
PointEntity camera,cube
PointEntity licht,cube

MoveEntity licht,-20,0,-6
MoveEntity cube,.4,0,-6

; Creating an array of 26 textures for letters A-Z

Dim letter(26)
ClsColor 90,0,210
For i=0 To 25
  letter(i)=CreateTexture (128,128)
  SetBuffer TextureBuffer(letter(i))
  Cls
  Text 62,63,Chr$(65+i),True,True
Next
ClsColor 0,0,0

; Define a type for scrolltext-cubes: entitiyhandle, x- and y position.

Type wuerfel
  Field hand
  Field xpos#
  Field ypos#
End Type

Global scroll$="     JUST A LITTLE DEMO FOR REALTIME CALCULATED TEXTURES AND TYPE BASED SCROLLTEXT     CHEERS   TXW     "

newscrollitem=0        ;Delaycounter for next scrolltext-cube
Global sc_offset=1     ;Offset for relevant character of scroll$
flp=1                  ;switch for only rendering texture every second loop
SeedRnd MilliSecs()
Color 40,0,90

While Not KeyHit(1)
  newscrollitem=newscrollitem+1
  If newscrollitem > 24                            ;Is it time for engaging the next scrollcube?
    char=Asc(Mid$(scroll$,sc_offset,1))-65         ;compute ASCIIcode of relevant character into Array-Index for letter(X)
    If char >= 0 And char < 26                     ;create new cube object and initialize its fields.
      sci.wuerfel=New wuerfel
      sci\xpos#=-4.8
      sci\ypos#=-7
      sci\hand=CreateCube ()
      ScaleEntity sci\hand,.5,.5,.5
      EntityTexture sci\hand,letter(char),0,1      ;Assign appropriate texture for relevant character
      EntityTexture sci\hand,txtr                  ;...just as the color-texture
    EndIf
    newscrollitem=0                                ;reset delaycounter
    sc_offset=sc_offset+1                          ;update relevant character of scroll$
    If sc_offset > Len(scroll$) Then sc_offset=1   ;wrap scrolltext if ended
  EndIf
  For sci.wuerfel=Each wuerfel                     ;Update entity-positions and delete if necessary...
    PositionEntity sci\hand,sci\xpos#,sci\ypos#,0
    TurnEntity sci\hand,.2,.3,.4
    sci\ypos#=sci\ypos#+.07
    If sci\ypos#>7                                 
      FreeEntity sci\hand
      Delete sci.wuerfel
    EndIf
  Next
  flp=1-flp                                        ;flp switches between 1 and 0 (1-1=0 -> 1-0=1)
  If Flp=1 Then updatetexture()                    ;updatetexture() will therefor only be called every 2nd loop 
  SetBuffer BackBuffer()
  TurnEntity cube,.3,.7,.8
  UpdateWorld
  RenderWorld
  Rect 20,0,2,479                                  ;just for optics..
  Rect 123,0,2,479
  Flip
Wend
ClearWorld() 
End

Function updatetexture()
  SetBuffer ImageBuffer(texture)
  LockBuffer ImageBuffer(texture)
  For i=1 To Rnd(0,20)                             ;Add random number of pixels in random colors
      r=Rnd(0,255)
      g=Rnd(0,255)
      b=Rnd(0,255)
      WritePixelFast Rnd(0,64),33,r Shl 16+g Shl 8+b
  Next
  For x=0 To 64                                    ;Recalculate color if every pixel from upper half of texture from color-
    For y=33 To 0 Step -1                          ;values of itself and its x-neighbours
      heinz=ReadPixelFast (x,y)
      heinz2=ReadPixelFast (x+1,y)
      heinz3=ReadPixelFast (x-1,y)
      heinz4=(heinz3+heinz2)/2
      heinz=(heinz4+heinz)/2
      WritePixelFast x,y-1,heinz                   ;Write pixel in upper half
      WritePixelFast x,67-y,heinz                  ;...and lower half
    Next
  Next
  UnlockBuffer ImageBuffer(texture)
  SetBuffer TextureBuffer(txtr)                    ;Finally blit the texture image into texture
  DrawBlockRect texture,0,0,0,0,64,32              ;but leave out the four pixel-rows in the middle for
  DrawBlockRect texture,0,32,0,36,64,32            ;better look. Just try DrawBlock texture,0,0 to
End Function                                       ;compare
