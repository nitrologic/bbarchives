; ID: 3045
; Author: Bobysait
; Date: 2013-03-26 15:26:09
; Title: Voronoi
; Description: Create a voronoi map

Type VorPoint Field y#,x#,d# End Type
Type VorPointL Field nc, p.VorPoint[50] End Type
Dim VoxVoronoi.VorPointL(64,64)
Dim VoxVoronoiL.VorPoint(0)

Function NewVorPoint.VorPoint(x,y)
	Local p.VorPoint = New VorPoint : p\x=x:p\y=y : Return p
End Function


InitVoxVoronoi()
Function InitVoxVoronoi()
	For a=0 To 64
		For b=0 To 64
			VoxVoronoi(a,b)=New VorPointL
			VoxVoronoi(a,b)\nc = 0
		Next
	Next
End Function

Dim VorImage#(0,0)

Function VoronoiUO(w,h, pCount%)
	
	Delete Each VorPoint
	
	Local p.VorPoint, n.VorPoint
	
	Local vl.VorPointL
	For a=0 To pCount-1
		NewVorPoint( Rand(1,w-2), Rand(1,h-2) )
	Next
	
	Local max_# = -1
	Local min_# = w*w+h*h
	
	Dim VorImage#(h,w)
	
	Local y#,x#
	For y=0 To h-1
		For x=0 To w-1
			n=First VorPoint
			For p = Each VorPoint : p\d=(x-p\x)*(x-p\x)+(y-p\y)*(y-p\y) : Next
			For p = Each VorPoint : If p\d<n\d:n=p:EndIf:Next
			VorImage(y,x) = Sqr(n\d)
			If VorImage(y,x)>max_ Then max_=VorImage(y,x)
			If VorImage(y,x)<min_ Then min_=VorImage(y,x)
		Next
	Next
	Local range_# = max_-min_
	Local image = CreateImage(w,h)
	Local cbuf = GraphicsBuffer()
	SetBuffer ImageBuffer(image)
	LockBuffer()
	For y=0 To h-1
		For x=0 To w-1
			Local c = 255-255*(VorImage(y,x)-min_)/range_
			WritePixelFast x,y,c*$010101 + $FF000000
		Next
	Next
	UnlockBuffer()
	SetBuffer cbuf
	Delete Each VorPoint
	
	Return image
	
End Function

Function Voronoi(w,h, pCount%)
	
	If pCount<20 Then Return VoronoiUO(w,h, pCount)
	
	Local MaxIJ = 64
	Local Ni = MaxIJ
	Local Nj = MaxIJ
	Local j,i
	
	Delete Each VorPoint
	Local ns=Sqr(pCount)/2
	Local ns_=1:While ns_<ns : ns_=ns_*2:Wend:ns=ns_/2
	If ns<MaxIJ Then Ni=ns:Nj=ns
	If ns>MaxIJ Then Ni=MaxIJ:Nj=MaxIJ
	If ns<8 Then Ni=8:Nj=8
	
	For a=0 To Nj-1
		For b=0 To Ni-1
			VoxVoronoi(a,b)\nc = 0
		Next
	Next
	
	Local p.VorPoint, n.VorPoint
	Local dw = Ceil(Float(w)/Ni)
	Local dh = Ceil(Float(h)/Nj)
	
	Local vl.VorPointL
	For a=0 To pCount-1
		p = NewVorPoint( Rand(1,w-2), Rand(1,h-2) )
		i=Floor(p\x/dw) : j=Floor(p\y/dh)
		vl = VoxVoronoi(j,i)
		vl\nc = vl\nc + 1
		vl\p[vl\nc] = p
	Next
	
	Local max_# = -1
	Local min_# = w*w+h*h
	
	Dim VorImage#(h,w)
	
	Local y#,x#
	For y=0 To h-1
		j = Floor(y/dh)
		For x=0 To w-1
			i=Floor(x/dw)
			vl=VoxVoronoi(j,i)
			nc = vl\nc
			If nc>0
				n=vl\p[1]
				For v=-1 To 1
					For u=-1 To 1
						Local ci=i+u, cj=j+v
						If cj>=0 And cj<Nj
						If ci>=0 And ci<Ni
							vl=VoxVoronoi(cj,ci)
							nc=vl\nc
							If nc
								For a=1 To nc : p=vl\p[a] : p\d = (x-p\x)*(x-p\x)+(y-p\y)*(y-p\y) : Next
							EndIf
						EndIf
						EndIf
					Next
				Next
				For v=-1 To 1
					For u=-1 To 1
						ci=i+u : cj=j+v
						If cj>=0 And cj<Nj
						If ci>=0 And ci<Ni
							vl=VoxVoronoi(cj,ci)
							nc=vl\nc
							If nc
								For a=1 To nc
									p=vl\p[a] : If p\d<n\d Then n=p
								Next
							EndIf
						EndIf
						EndIf
					Next
				Next
			Else
				n=First VorPoint
				For p = Each VorPoint : p\d=(x-p\x)*(x-p\x)+(y-p\y)*(y-p\y) : Next
				For p = Each VorPoint
					If p\d<n\d Then n=p
				Next
			EndIf
			VorImage(y,x) = Sqr(n\d)
			If VorImage(y,x)>max_ Then max_=VorImage(y,x)
			If VorImage(y,x)<min_ Then min_=VorImage(y,x)
		Next
	Next
	Local range_# = max_-min_
	Local image = CreateImage(w,h)
	Local cbuf = GraphicsBuffer()
	SetBuffer ImageBuffer(image)
	LockBuffer()
	For y=0 To h-1
		For x=0 To w-1
			Local c = 255-255*(VorImage(y,x)-min_)/range_
			WritePixelFast x,y,c*$010101 + $FF000000
		Next
	Next
	UnlockBuffer()
	SetBuffer cbuf
	Delete Each VorPoint
	
	Return image
	
End Function


Function VoronoiTileUO(w,h, pCount%)
	
	Delete Each VorPoint
	
	Local p.VorPoint, n.VorPoint
	
	Local vl.VorPointL
	For a=0 To pCount-1
		NewVorPoint( Rand(1,w-2), Rand(1,h-2) )
	Next
	
	Local max_# = -1
	Local min_# = w*w+h*h
	
	Dim VorImage#(h,w)
	
	Local y#,x#
	For y=0 To h-1
		For x=0 To w-1
			n=First VorPoint
			For p = Each VorPoint
				dx=Abs(x-p\x):If w-dx<dx Then dx=w-dx
				dy=Abs(y-p\y):If h-dy<dy Then dy=h-dy
				p\d=dx*dx+dy*dy
			Next
			For p = Each VorPoint:If p\d<n\d:n=p:EndIf:Next
			VorImage(y,x) = Sqr(n\d)
			If VorImage(y,x)>max_ Then max_=VorImage(y,x)
			If VorImage(y,x)<min_ Then min_=VorImage(y,x)
		Next
	Next
	Local range_# = max_-min_
	Local image = CreateImage(w,h)
	Local cbuf = GraphicsBuffer()
	SetBuffer ImageBuffer(image)
	LockBuffer()
	For y=0 To h-1
		For x=0 To w-1
			Local c = 255-255*(VorImage(y,x)-min_)/range_
			WritePixelFast x,y,c*$010101 + $FF000000
		Next
	Next
	UnlockBuffer()
	SetBuffer cbuf
	Delete Each VorPoint
	
	Return image
	
End Function


Function VoronoiTile(w,h, pCount%)
	
	If pCount<20 Then Return VoronoiTileUO(w,h, pCount)
	
	Local MaxIJ = 64
	Local Ni = MaxIJ
	Local Nj = MaxIJ
	Local j,i
	
	Delete Each VorPoint
	Local ns=Sqr(pCount)/2
	Local ns_=1:While ns_<ns : ns_=ns_*2:Wend:ns=ns_/2
	If ns<MaxIJ Then Ni=ns:Nj=ns
	If ns>MaxIJ Then Ni=MaxIJ:Nj=MaxIJ
	If ns<8 Then Ni=8:Nj=8
	
	For a=0 To Nj-1
		For b=0 To Ni-1
			VoxVoronoi(a,b)\nc = 0
		Next
	Next
	
	Dim VoxVoronoiL(pCount)
	Local p.VorPoint, n.VorPoint
	Local dw = Ceil(Float(w)/Ni)
	Local dh = Ceil(Float(h)/Nj)
	
	Local vl.VorPointL
	For a=0 To pCount-1
		p = NewVorPoint( Rand(1,w-2), Rand(1,h-2) )
		i=Floor(p\x/dw) : j=Floor(p\y/dh)
		vl = VoxVoronoi(j,i)
		vl\nc = vl\nc + 1
		vl\p[vl\nc] = p
	Next
	
	Local max_# = -1
	Local min_# = w*w+h*h
	
	Dim VorImage#(h,w)
	
	Local w2=w/2
	Local h2=h/2
	Local y#,x#
	For y=0 To h-1
		j = Floor(y/dh)
		For x=0 To w-1
			i=Floor(x/dw)
			vl=VoxVoronoi(j,i)
			nc = vl\nc
			If nc>0
				ic=0
				n=First VorPoint
				For v=-1 To 1
					For u=-1 To 1
						Local ci=i+u, cj=j+v
						If cj<0 Then cj=Nj-1
						If ci<0 Then ci=Ni-1
						If cj=Nj Then cj=0
						If ci=Ni Then ci=0
						vl=VoxVoronoi(cj,ci)
						nc=vl\nc
						For a=1 To nc
							ic=ic+1
							VoxVoronoiL(ic)=vl\p[a]
							p=vl\p[a]
							dx=Abs(x-p\x):If dx>w2 Then dx=w-dx
							dy=Abs(y-p\y):If dy>h2 Then dy=h-dy
							p\d=dx*dx+dy*dy
						Next
					Next
				Next
				n = VoxVoronoiL(1)
				For ii=2 To ic
					If VoxVoronoiL(ii)\d<n\d Then n=VoxVoronoiL(ii)
				Next
			Else
				n=First VorPoint
				For p = Each VorPoint
					dx=Abs(x-p\x):If dx>w2 Then dx=w-dx
					dy=Abs(y-p\y):If dy>h2 Then dy=h-dy
					p\d=dx*dx+dy*dy
				Next
				For p = Each VorPoint
					If p\d<n\d Then n=p
				Next
			EndIf
			VorImage(y,x) = Sqr(n\d)
			If VorImage(y,x)>max_ Then max_=VorImage(y,x)
			If VorImage(y,x)<min_ Then min_=VorImage(y,x)
		Next
	Next
	Local range_# = max_-min_
	Local image = CreateImage(w,h)
	Local cbuf = GraphicsBuffer()
	SetBuffer ImageBuffer(image)
	LockBuffer()
	For y=0 To h-1
		For x=0 To w-1
			Local c  = 255-255*(VorImage(y,x)-min_)/range_
			WritePixelFast x,y,c*$010101 + $FF000000
			; - Normal map -
			; Local rl,rr,gu,gd
			; rl = 255-255*(VorImage(y,w-1)-min_)/range_ : If x>0 Then rl = 255-255*(VorImage(y,x-1)-min_)/range_
			; rr = 255-255*(VorImage(y,0)-min_)/range_ : If x<w-1 Then rl = 255-255*(VorImage(y,x+1)-min_)/range_
			; gu = 255-255*(VorImage(h-1,x)-min_)/range_ : If y>0 Then gu = 255-255*(VorImage(y-1,x)-min_)/range_
			; gd = 255-255*(VorImage(0,x)-min_)/range_ : If y<h-1 Then gd = 255-255*(VorImage(y+1,x)-min_)/range_
			; WritePixelFast x,y,$FF000000 + (128+(rr-rl)/2) Shl(16) + (128+(gu-gd)/2) Shl(8) + c
		Next
	Next
	UnlockBuffer()
	SetBuffer cbuf
	Delete Each VorPoint
	
	Return image
	
End Function




Graphics 1024,600,0,2
SetBuffer BackBuffer()
Local size=256
Local count=120
Local seed=13
Local t0 = MilliSecs()
SeedRnd(seed)
Local img = VoronoiTileUO ( size,size, count ) ; UnOptimized algoryhtm
Local t1 = MilliSecs()
SeedRnd(seed)
Local img2 = VoronoiTile ( size,size, count ) ; Optimized with a simple array of the point list
Local t2 = MilliSecs()
DrawImage img, 0,0
DrawImage img, size,0
DrawImage img, 0,size
DrawImage img, size,size
DrawImage img2, 1+2*size,0
DrawImage img2, 1+3*size,0
DrawImage img2, 1+2*size,size
DrawImage img2, 1+3*size,size
Color 255,255,255:Text 10,580,(t1-t0)
Color 255,255,255:Text size*2+1,580,(t2-t1)
Flip True
WaitKey

End
