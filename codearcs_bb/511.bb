; ID: 511
; Author: Vertex
; Date: 2002-11-28 12:37:58
; Title: Emboss
; Description: Bumpmapping ?

; ------------------------------------------------------------ 
  Graphics 640,480,32,2 
  SetBuffer BackBuffer() 
; ------------------------------------------------------------ 

; ------------------------------------------------------------ 
  Image = CreateImage(400,300) 
  Dim Buffer(ImageWidth(Image),ImageHeight(Image)) 
  Dim Picture(ImageWidth(Image),ImageHeight(Image)) 
; ------------------------------------------------------------ 

; ------------------------------------------------------------ 
  SetBuffer ImageBuffer(Image) 
  Font = LoadFont("Verdana",70,1,0,0) 
  SetFont Font 
  Text 200,150,"BlitzBASIC",1,1 
; ------------------------------------------------------------ 

; ------------------------------------------------------------ 
  LockBuffer ImageBuffer(Image) 

  For X = 0 To ImageWidth(Image) 
     For Y = 0 To ImageHeight(Image) 
        Buffer(X,Y) = ReadPixelFast(X,Y) 
     Next 
  Next 

  For X = 0 To ImageWidth(Image) - 3 
     For Y = 0 To ImageHeight(Image) - 1 
        BufferR1 = GetR(Buffer(X,Y)) 
        BufferG1 = GetG(Buffer(X,Y)) 
        BufferB1 = GetB(Buffer(X,Y)) 
       
        BufferR2 = GetR(Buffer(X + 3,Y + 1)) 
        BufferG2 = GetG(Buffer(X + 3,Y + 1)) 
        BufferB2 = GetB(Buffer(X + 3,Y + 1)) 
       
        TempR = Abs(BufferR1) - BufferR2 + 128 
        If TempR > 255 Then TempR = 255 
        If TempR < 0   Then TempR = 0 
       
        TempG = Abs(BufferG1) - BufferG2 + 128 
        If TempG > 255 Then TempG = 255 
        If TempG < 0   Then TempG = 0 
       
        TempB = Abs(BufferB1) - BufferB2 + 128 
        If TempB > 255 Then TempB = 255 
        If TempB < 0   Then TempB = 0    
       
          Picture(X,Y) = GetRGB(TempR,TempG,TempB) 
     Next     
  Next 
    
  For X = 0 To ImageWidth(Image) - 3 
     For Y = 0 To ImageHeight(Image) - 1 
        WritePixelFast X,Y,Picture(X,Y) 
     Next 
  Next     

  For X = ImageWidth(Image) - 3 To ImageWidth(Image) 
     For Y = ImageHeight(Image) - 1 To ImageHeight(Image) 
          WritePixelFast X,Y,Buffer(X,Y) 
     Next 
  Next 
  UnlockBuffer ImageBuffer(Image) 
; ------------------------------------------------------------ 

; ------------------------------------------------------------ 
  Dim Buffer(0,0) 
  Dim Picture(0,0) 
; ------------------------------------------------------------ 

; ------------------------------------------------------------ 
  SetBuffer BackBuffer() 
  DrawImage Image,0,0 : Flip 
; ------------------------------------------------------------ 

; ------------------------------------------------------------ 
  WaitKey : FreeImage Image : End 
; ------------------------------------------------------------ 

; ------------------------------------------------------------ 
  Function GetR(RGB) 
     Return (RGB And $FF0000) / $10000 
  End Function 

  Function GetG(RGB) 
     Return (RGB And $FF00) / $100 
  End Function 

  Function GetB(RGB) 
       Return RGB And $FF 
  End Function 

  Function GetRGB(R,G,B) 
     Return R * $10000 + G * $100 + B 
  End Function 
; ------------------------------------------------------------
