; ID: 263
; Author: Snarty
; Date: 2002-03-21 15:50:57
; Title: Fast Flood Fill (ProPixel Code)
; Description: Flood fill code not using a stack. (ProPixel Code)

; Fill Routines
; Written By Paul Snart (Snarty)
; Oct 2001

; RCol = RGB Color to Fill with
; Ax = X Start on Image to Fill
; Ay = Y Start on Image to Fill
; Image = Image to fill on

Type Point
     Field x
     Field y
End Type

Function FloodFill(RCol,ax,ay,Image)

     timeit=MilliSecs()
     
     temp=CreateImage(1,1):SetBuffer ImageBuffer(temp)
     LockBuffer:WritePixelFast 0,0,RCol
     RCol=ReadPixelFast(0,0)
     UnlockBuffer:FreeImage Temp
     SetBuffer ImageBuffer(Image):LockBuffer
     BCol=ReadPixelFast(ax,ay)
     ImW=ImageWidth(Image)
     ImH=ImageHeight(Image)
     
     If BCol<>RCol
          Hlt=-1:Hlb=-1
          Hrt=-1:Hrb=-1
          Entrys=1
          Fp.Point = New Point
          Fp\x=ax
          Fp\y=ay
          Repeat
               Fp.Point=First Point
               Lx=Fp\x:Rx=Fp\x+1
               HitL=False:HitR=False
               Hlt=-1:Hlb=-1
               Hrt=-1:Hrb=-1
               Repeat
                    If Lx=>0 And HitL=False
                         CColL=ReadPixelFast(Lx,Fp\y)
                         If CColL=BCol
                              WritePixelFast Lx,Fp\y,RCol
                              If Fp\y>0
                                   CColL=ReadPixelFast(Lx,Fp\y-1)
                                   If CColL=BCol
                                        Hlt=Lx
                                   Else
                                        If Hlt<>-1
                                             y=Fp\y-1
                                             Fp.Point = New Point
                                             Fp\y=y:Fp\x=Hlt
                                             Hlt=-1
                                             Fp.Point = First Point
                                             Entrys=Entrys+1
                                        EndIf
                                   EndIf
                              EndIf
                              If Fp\y<ImH-1 
                                   CColL=ReadPixelFast(Lx,Fp\y+1)
                                   If CColL=BCol
                                        Hlb=Lx
                                   Else
                                        If Hlb<>-1
                                             y=Fp\y+1
                                             Fp.Point = New Point
                                             Fp\y=y:Fp\x=Hlb
                                             Hlb=-1
                                             Fp.Point = First Point
                                             Entrys=Entrys+1
                                        EndIf
                                   EndIf
                              EndIf
                              Lx=Lx-1
                         Else
                              HitL=True
                              If Hlt<>-1 
                                   y=Fp\y-1
                                   Fp.Point = New Point
                                   Fp\y=y:Fp\x=Hlt
                                   Hlt=-1
                                   Fp.Point = First Point
                                   Entrys=Entrys+1
                              EndIf
                              If Hlb<>-1 
                                   y=Fp\y+1
                                   Fp.Point = New Point
                                   Fp\y=y:Fp\x=Hlb
                                   Hlb=-1
                                   Fp.Point = First Point
                                   Entrys=Entrys+1
                              EndIf
                         EndIf
                    Else
                         HitL=True
                         If Hlt<>-1 
                              y=Fp\y-1
                              Fp.Point = New Point
                              Fp\y=y:Fp\x=Hlt
                              Hlt=-1
                              Fp.Point = First Point
                              Entrys=Entrys+1
                         EndIf
                         If Hlb<>-1 
                              y=Fp\y+1
                              Fp.Point = New Point
                              Fp\y=y:Fp\x=Hlb
                              Hlb=-1
                              Fp.Point = First Point
                              Entrys=Entrys+1
                         EndIf
                    EndIf
                    If Rx<=ImW-1 And HitR=False
                         CColR=ReadPixelFast(Rx,Fp\y)
                         If CColR=BCol
                              WritePixelFast Rx,Fp\y,RCol
                              If Fp\y>0 
                                   CColR=ReadPixelFast(Rx,Fp\y-1)
                                   If CColR=BCol
                                        Hrt=Rx
                                   Else
                                        If Hrt<>-1
                                             y=Fp\y-1
                                             Fp.Point = New Point
                                             Fp\y=y:Fp\x=Hrt
                                             Hrt=-1
                                             Fp.Point = First Point
                                             Entrys=Entrys+1
                                        EndIf
                                   EndIf
                              EndIf
                              If Fp\y<ImH-1
                                   CColR=ReadPixelFast(Rx,Fp\y+1)
                                   If CColR=BCol
                                        Hrb=Rx
                                   Else
                                        If Hrb<>-1
                                             y=Fp\y+1
                                             Fp.Point = New Point
                                             Fp\y=y:Fp\x=Hrb
                                             Hrb=-1
                                             Fp.Point = First Point
                                             Entrys=Entrys+1
                                        EndIf
                                   EndIf
                              EndIf
                              Rx=Rx+1
                         Else
                              HitR=True
                              If Hrt<>-1
                                   y=Fp\y-1
                                   Fp.Point = New Point
                                   Fp\y=y:Fp\x=Hrt
                                   Hrt=-1
                                   Fp.Point = First Point
                                   Entrys=Entrys+1
                              EndIf
                              If Hrb<>-1
                                   y=Fp\y+1
                                   Fp.Point = New Point
                                   Fp\y=y:Fp\x=Hrb
                                   Hrb=-1
                                   Fp.Point = First Point
                                   Entrys=Entrys+1
                              EndIf
                         EndIf
                    Else
                         HitR=True
                         If Hrt<>-1
                              y=Fp\y-1
                              Fp.Point = New Point
                              Fp\y=y:Fp\x=Hrt
                              Hrt=-1
                              Fp.Point = First Point
                              Entrys=Entrys+1
                         EndIf
                         If Hrb<>-1
                              y=Fp\y+1
                              Fp.Point = New Point
                              Fp\y=y:Fp\x=Hrb
                              Hrb=-1
                              Fp.Point = First Point
                              Entrys=Entrys+1
                         EndIf
                    EndIf
               Until (HitR=True And HitL=True) Or KeyHit(1)
               Fp.Point=First Point
               Delete Fp
               Entrys=Entrys-1
          Until Entrys=False Or KeyHit(1)
     EndIf
               
     UnlockBuffer
     SetBuffer BackBuffer()
     mhit=False
     DebugLog (Float(MilliSecs()-TimeIt)/1000)+" seconds"

End Function
