; ID: 42
; Author: Unknown
; Date: 2001-09-16 06:28:40
; Title: Blender
; Description: This is a fast 2D-Blender/Fader

;Example
GRAPHICS 640,480,0,1
b1=LOADIMAGE("1.bmp")
b2=LOADIMAGE("2.bmp")
b3=LOADIMAGE("3.bmp")

BlackBlender b1,170,190,0,3000
DELAY 1000
BlackBlender b1,170,190,1,1500
BlackBlender b2,170,190,0,3000
DELAY 1000
BlackBlender b2,170,190,1,1500
BlackBlender b3,170,190,0,3000
WAITKEY()
BlackBlender b3,170,190,1,1500
END

;-------------------------------------------------
FUNCTION BlackBlender(Image, X, Y, Mode, Time)
   Source=IMAGEBUFFER(Image)
   Dest=FRONTBUFFER()
   MaxX=IMAGEWIDTH(Image)-1
   MaxY=IMAGEHEIGHT(Image)-1
   Start=MILLISECS()
   LOCKBUFFER Source
   LOCKBUFFER Dest
   WHILE MILLISECS()-Start<Time
      Count=(Count+1) MOD 4
      IF Count=0 THEN MinX=0: MinY=0
      IF Count=1 THEN MinX=1: MinY=1
      IF Count=2 THEN MinX=1: MinY=0
      IF Count=3 THEN MinX=0: MinY=1
      IF Mode=0 THEN Value=MILLISECS()-Start
      IF Mode=1 THEN Value=Time-MILLISECS()+Start
      FOR ii=MinY TO MaxY STEP 2
         FOR i=MinX TO MaxX STEP 2
            rgb=READPIXELFAST(i,ii,Source)
            r=(rgb AND $FF0000)/$10000
            g=(rgb AND $FF00)/$100
            b=rgb AND $FF
            rgb=r*Value/Time*65536 + g*Value/Time*256 + b*Value/Time
            WRITEPIXELFAST X+i,Y+ii,rgb,Dest
         NEXT
      NEXT
   WEND
   UNLOCKBUFFER Source
   UNLOCKBUFFER Dest
   IF Mode=0 THEN DRAWBLOCK Image,X,Y
   IF Mode=1 THEN COLOR 0,0,0: RECT X,Y,MaxX+1,MaxY+1
END FUNCTION

;-------------------------------------------------
FUNCTION WhiteBlender(Image, X, Y, Mode, Time)
   Source=IMAGEBUFFER(Image)
   Dest=FRONTBUFFER()
   MaxX=IMAGEWIDTH(Image)-1
   MaxY=IMAGEHEIGHT(Image)-1
   Start=MILLISECS()
   LOCKBUFFER Source
   LOCKBUFFER Dest
   WHILE MILLISECS()-Start<Time
      Count=(Count+1) MOD 4
      IF Count=0 THEN MinX=0: MinY=0
      IF Count=1 THEN MinX=1: MinY=1
      IF Count=2 THEN MinX=1: MinY=0
      IF Count=3 THEN MinX=0: MinY=1
      IF Mode=0 THEN Value=Time-MILLISECS()+Start
      IF Mode=1 THEN Value=MILLISECS()-Start
      FOR ii=MinY TO MaxY STEP 2
         FOR i=MinX TO MaxX STEP 2
            rgb=READPIXELFAST(i,ii,Source)
            r=(rgb AND $FF0000)/$10000
            g=(rgb AND $FF00)/$100
            b=rgb AND $FF
            m=(r+g+b)/3
            rgb=((255-r)*Value/Time+r)*65536 + ((255-g)*Value/Time+g)*256 + ((255-b)*Value/Time+b)
            WRITEPIXELFAST X+i,Y+ii,rgb,Dest
         NEXT
      NEXT
   WEND
   UNLOCKBUFFER Source
   UNLOCKBUFFER Dest
   IF Mode=0 THEN DRAWBLOCK Image,X,Y
   IF Mode=1 THEN COLOR 255,255,255: RECT X,Y,MaxX+1,MaxY+1
END FUNCTION

;-------------------------------------------------
FUNCTION GrayBlender(Image, X, Y, Mode, Time)
   Source=IMAGEBUFFER(Image)
   Dest=FRONTBUFFER()
   MaxX=IMAGEWIDTH(Image)-1
   MaxY=IMAGEHEIGHT(Image)-1
   Start=MILLISECS()
   LOCKBUFFER Source
   LOCKBUFFER Dest
   WHILE MILLISECS()-Start<Time
      Count=(Count+1) MOD 4
      IF Count=0 THEN MinX=0: MinY=0
      IF Count=1 THEN MinX=1: MinY=1
      IF Count=2 THEN MinX=1: MinY=0
      IF Count=3 THEN MinX=0: MinY=1
      IF Mode=0 THEN Value=Time-MILLISECS()+Start
      IF Mode=1 THEN Value=MILLISECS()-Start
      FOR ii=MinY TO MaxY STEP 2
         FOR i=MinX TO MaxX STEP 2
            rgb=READPIXELFAST(i,ii,Source)
            r=(rgb AND $FF0000)/$10000
            g=(rgb AND $FF00)/$100
            b=rgb AND $FF
            m=(r+g+b)/3
            rgb=((m-r)*Value/Time+r)*65536 + ((m-g)*Value/Time+g)*256 + ((m-b)*Value/Time+b)
            WRITEPIXELFAST X+i,Y+ii,rgb,Dest
         NEXT
      NEXT
   WEND
   UNLOCKBUFFER Source
   UNLOCKBUFFER Dest
   IF Mode=0 THEN DRAWBLOCK Image,X,Y
END FUNCTION
