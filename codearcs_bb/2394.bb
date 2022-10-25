; ID: 2394
; Author: Yasha
; Date: 2009-01-13 22:03:35
; Title: Voronoi map
; Description: Create a tiling cellular texture

Function voronoi(fx,fy,f,d=100,minh#=0,alpha#=1,seed=-1)		;Creates a voronoi map. Size, no. features, feature density,
																;min. cutoff, brightness, RNDseed
Local img=CreateImage(fx,fy)
Local imgw=fx-1
Local imgh=fy-1
Local map=CreateBank((f+1)*12)	;Rather confused sub for an array
Local maxdist#,dist1#,dist2#,dist3#,temp#
Local rs,x,y,nn,px,py,pd,op,opx,opy,np,h

If seed>-1 Then rs=RndSeed():SeedRnd seed

SetBuffer ImageBuffer(img)
LockBuffer ImageBuffer(img)

For p=1 To f
	PokeInt(map,p*12,Rand(0,imgw))
	PokeInt(map,p*12+4,Rand(0,imgh))
	If d<100 Then PokeInt(map,p*12+8,Rand(1,100))
Next

For x=0 To imgw
For y=0 To imgh
	dist1#=0
	dist2#=0
	For nn=1 To f
		px=PeekInt(map,nn*12):py=PeekInt(map,nn*12+4)
		dist1=(x-px)*(x-px)+(y-py)*(y-py)
		dist2=(x-opx)*(x-opx)+(y-opy)*(y-opy)
		If dist1<dist2
			op=nn
			opx=PeekInt(map,nn*12)
			opy=PeekInt(map,nn*12+4)
		EndIf
	Next
	temp=(x-opx)*(x-opx)+(y-opy)*(y-opy)
	If temp>maxdist Then maxdist=temp
Next
Next

maxdist=Sqr(Floor(maxdist))
op=1:np=1

For x=0 To imgw
For y=0 To imgh
	dist1#=0
	dist2#=0
	dist3#=0
	For nn=1 To f		;Identify nearest two neighbours
		px=PeekInt(map,nn*12):py=PeekInt(map,nn*12+4)
		dist1=(x-px)*(x-px)+(y-py)*(y-py)
		
		opx=PeekInt(map,op*12):opy=PeekInt(map,op*12+4)
		npx=PeekInt(map,np*12):npy=PeekInt(map,np*12+4)
		dist2=(x-opx)*(x-opx)+(y-opy)*(y-opy)
		dist3=(x-npx)*(x-npx)+(y-npy)*(y-npy)
		
		If dist3<dist2 Then op=np:dist2=dist3
		If dist1<dist2 Then np=op:op=nn
		If op=np Then np=nn
		If dist1>dist2 And dist1<dist3 Then np=nn
	Next
	
	opx=PeekInt(map,op*12):opy=PeekInt(map,op*12+4)
	npx=PeekInt(map,np*12):npy=PeekInt(map,np*12+4)
	h=Sqr((x-opx)*(x-opx)+(y-opy)*(y-opy))
	h=Sqr((x-npx)*(x-npx)+(y-npy)*(y-npy))-h

	h=(h/(maxdist)-minh)*(1/(1-minh))*(255*alpha)
	If h<0 Then h=0
	If h>(255*alpha) Then h=(255*alpha)
	If PeekInt(map,op*12+8)>d Then h=0			;Edits out (100-d)% of nodes
	WritePixelFast(x,y,h Or (h Shl 8) Or (h Shl 16))
Next
Next

UnlockBuffer ImageBuffer(img)
SetBuffer BackBuffer()
FreeBank map

If seed>-1 Then SeedRnd rs
Return img

End Function
