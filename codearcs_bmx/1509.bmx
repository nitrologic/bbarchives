; ID: 1509
; Author: klepto2
; Date: 2005-10-29 04:58:38
; Title: Non Recursive FloodFill
; Description: Convertation from Snartys FloodFill Algo

'Fill Routines
' Written By Paul Snart (Snarty) - Converted to BMax by klepto2 in Oct 2005
' Oct 2001

' RCol = RGB Color To Fill with
' Ax = X Start on Image To Fill
' Ay = Y Start on Image To Fill
' Image = Image To fill on

Strict


Type Point
	Global Point_List:TList
     Field x
     Field y

	Method New()
		If Point.Point_List = Null Then Point.Point_List = New TList
		Point.Point_List.AddLast(Self)
	End Method
	
End Type

Function FloodFill(RCol,ax,ay,Image:TPixmap)

    	 Local timeit=MilliSecs()
         Local y:Int
 		 Local BCol=Image.ReadPixel(ax,ay)
    	 Local ImW=Image.width
    	 Local ImH=Image.Height
     
      If BCol<>RCol
      
	Local   Hlt=-1
	Local  	Hlb=-1
    Local   Hrt=-1
	Local	Hrb=-1
    Local   Entrys=1
	Local   FP:Point = New Point
            Fp.x=ax
            Fp.y=ay  
            
          Repeat
               FP:Point=Point(Point.Point_List.First())
        Local  Lx=Fp.x
        Local  Rx=Fp.x+1
        Local  HitL=False
	   	Local  HitR=False
               Hlt=-1
			   Hlb=-1
               Hrt=-1 
			   Hrb=-1
               Repeat
                    If Lx=>0 and HitL=False
        Local           CColL=Image.ReadPixel(Lx,Fp.y)
                         If CColL=BCol
                              Image.WritePixel Lx,Fp.y,RCol
                              If Fp.y>0
                                   CColL=Image.ReadPixel(Lx,Fp.y-1)
                                   If CColL=BCol
                                        Hlt=Lx
                                   Else
                                        If Hlt<>-1
                                             y=Fp.y-1
                                             FP:Point = New Point
                                             Fp.y=y
									    Fp.x=Hlt
                                             Hlt=-1
                                             FP:Point = Point(Point.Point_List.First())
                                             Entrys=Entrys+1
                                        EndIf
                                   EndIf
                              EndIf
                              If Fp.y<ImH-1 
                                   CColL=Image.ReadPixel(Lx,Fp.y+1)
                                   If CColL=BCol
                                        Hlb=Lx
                                   Else
                                        If Hlb<>-1
                                             y=Fp.y+1
                                             FP:Point = New Point
                                             Fp.y=y
									    Fp.x=Hlb
                                             Hlb=-1
                                             FP:Point = Point(Point.Point_List.First())
                                             Entrys=Entrys+1
                                        EndIf
                                   EndIf
                              EndIf
                              Lx=Lx-1
                         Else
                              HitL=True
                              If Hlt<>-1 
                                   y=Fp.y-1
                                   FP:Point = New Point
                                   Fp.y=y
							   Fp.x=Hlt
                                   Hlt=-1
                                   FP:Point = Point(Point.Point_List.First())
                                   Entrys=Entrys+1
                              EndIf
                              If Hlb<>-1 
                                   y=Fp.y+1
                                   FP:Point = New Point
                                   Fp.y=y
							   Fp.x=Hlb
                                   Hlb=-1
                                   FP:Point = Point(Point.Point_List.First())
                                   Entrys=Entrys+1
                              EndIf
                         EndIf
                    Else
                         HitL=True
                         If Hlt<>-1 
                              y=Fp.y-1
                              FP:Point = New Point
                              Fp.y=y
						   Fp.x=Hlt
                              Hlt=-1
                              FP:Point = Point(Point.Point_List.First())
                              Entrys=Entrys+1
                         EndIf
                         If Hlb<>-1 
                              y=Fp.y+1
                              FP:Point = New Point
                              Fp.y=y
						   Fp.x=Hlb
                              Hlb=-1
                              FP:Point = Point(Point.Point_List.First())
                              Entrys=Entrys+1
                         EndIf
                    EndIf
                    If Rx<=ImW-1 and HitR=False
        Local           CColR=Image.ReadPixel(Rx,Fp.y)
                         If CColR=BCol
                              Image.WritePixel Rx,Fp.y,RCol
                              If Fp.y>0 
                                   CColR=Image.ReadPixel(Rx,Fp.y-1)
                                   If CColR=BCol
                                        Hrt=Rx
                                   Else
                                        If Hrt<>-1
                                             y=Fp.y-1
                                             FP:Point = New Point
                                             Fp.y=y
									    Fp.x=Hrt
                                             Hrt=-1
                                             FP:Point = Point(Point.Point_List.First())
                                             Entrys=Entrys+1
                                        EndIf
                                   EndIf
                              EndIf
                              If Fp.y<ImH-1
                                   CColR=Image.ReadPixel(Rx,Fp.y+1)
                                   If CColR=BCol
                                        Hrb=Rx
                                   Else
                                        If Hrb<>-1
                                             y=Fp.y+1
                                             FP:Point = New Point
                                             Fp.y=y
									    Fp.x=Hrb
                                             Hrb=-1
                                             FP:Point = Point(Point.Point_List.First())
                                             Entrys=Entrys+1
                                        EndIf
                                   EndIf
                              EndIf
                              Rx=Rx+1
                         Else
                              HitR=True
                              If Hrt<>-1
                                   y=Fp.y-1
                                   FP:Point = New Point
                                   Fp.y=y
							   Fp.x=Hrt
                                   Hrt=-1
                                   FP:Point = Point(Point.Point_List.First())
                                   Entrys=Entrys+1
                              EndIf
                              If Hrb<>-1
                                   y=Fp.y+1
                                   FP:Point = New Point
                                   Fp.y=y
							   Fp.x=Hrb
                                   Hrb=-1
                                   FP:Point = Point(Point.Point_List.First())
                                   Entrys=Entrys+1
                              EndIf
                         EndIf
                    Else
                         HitR=True
                         If Hrt<>-1
                              y=Fp.y-1
                              FP:Point = New Point
                              Fp.y=y
	   					   Fp.x=Hrt
                              Hrt=-1
                              FP:Point = Point(Point.Point_List.First())
                              Entrys=Entrys+1
                         EndIf
                         If Hrb<>-1
                              y=Fp.y+1
                              FP:Point = New Point
                              Fp.y=y
						 Fp.x=Hrb
                              Hrb=-1
                              FP:Point = Point(Point.Point_List.First())
                              Entrys=Entrys+1
                         EndIf
                    EndIf
                    
               Until (HitR=True And HitL=True) or KeyHit(1)
               FP:Point=Point(Point.Point_List.First())
               Point.Point_List.Remove(FP)
               Entrys=Entrys-1  
              
               
          Until Entrys=False or KeyHit(1)
     EndIf
               
	Local     mhit=False
    		  DebugLog (Float(MilliSecs()-TimeIt)/1000)+" seconds"

End Function

Graphics 800,600,0,-1



SetColor 255,0,255

DrawOutline(200,100,400,200) 

Local Test:TPixmap = GrabPixmap(0,0,800,600)

FloodFill($FFff00ff,0,599,Test)

DrawPixmap (Test,0,0)

Flip

WaitKey()

Function DrawOutline(x1:Int, y1:Int, w1:Int, h1:Int,_b:Int = 1)
	DrawRect x1, y1, w1 - _B, _B
	DrawRect x1, y1, _B, h1
	DrawRect x1 + w1 - _B, y1, _B, h1
	DrawRect x1, y1 + h1, w1 , _B
End Function
