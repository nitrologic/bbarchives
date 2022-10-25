; ID: 674
; Author: starfox
; Date: 2003-05-06 17:41:56
; Title: Lightwave3d Exporter
; Description: Export to .lwo from Blitz

Function CreateLwoObject(mesh,filename$)
Local vertcount=0
Local tricount=0
Local tempvar=0
For surfe = 1 To CountSurfaces(mesh)
surf = GetSurface(mesh,surfe)
surfsize = surfsize + FindStringLength("Surface "+surfe)
vertcount = vertcount + CountVertices(surf)
tricount = tricount + CountTriangles(surf)
Next
tagsize = surfsize
pointsize = vertcount*12
polysize = tricount*8 + 4;(tricount*2) + 4
ptagsize = tricount*4 + 4
surfsize = surfsize + (CountSurfaces(mesh)*(30))
uvsize = 18+(vertcount*10)
totalsize = 70+pointsize+polysize+ptagsize+surfsize+uvsize+tagsize
w = WriteFile(filename)
writelwostring("FORM",totalsize,w) ;Header
writelwostring("LWO2",0,w)
writelwostring("TAGS",tagsize,w)
For surfe = 1 To CountSurfaces(mesh)
	writelworstring("Surface "+surfe,w)
Next
writelwostring("LAYR",18,w)
	writelwoshort(0,w)
	writelwoshort(0,w)
	writelwopointfloat(0,0,0,w)
	Writelworstring("",w)
writelwostring("PNTS",pointsize,w) ;Point Header
For surfe = 1 To CountSurfaces(mesh)
surf = GetSurface(mesh,surfe)
	For vertindex = 0 To CountVertices(surf)-1
	writelwopointfloat(VertexX(surf,vertindex),VertexY(surf,vertindex),VertexZ(surf,vertindex),w)
	Next
Next
writelwostring("POLS",polysize,w)
	writelwostring("FACE",0,w)
For surfe = 1 To CountSurfaces(mesh)
surf = GetSurface(mesh,surfe)
	For index = 0 To CountTriangles(surf)-1
	writelwoshort(3,w)
	in1 = TriangleVertex(surf,index,0):in2 = TriangleVertex(surf,index,1)
	in3 = TriangleVertex(surf,index,2)
	in1 = findvertindex(in1,surf,mesh)
	in2 = findvertindex(in2,surf,mesh)
	in3 = findvertindex(in3,surf,mesh)
	writelwoshort(in1,w):writelwoshort(in2,w):writelwoshort(in3,w)
	Next
Next
tempvar = 0
writelwostring("PTAG",ptagsize,w)
	writelwostring("SURF",0,w)
For surfe = 1 To CountSurfaces(mesh)
surf = GetSurface(mesh,surfe)
	For vertindex = 0 To CountTriangles(surf)-1
	writelwoshort(tempvar,w):writelwoshort(surfe-1,w)
	tempvar = tempvar + 1
	Next
Next

For surfe = 1 To CountSurfaces(mesh)
length = findstringlength("Surface "+surfe)+22
writelwostring("SURF",length,w)
	writelworstring("Surface "+surfe,w)
	Writelworstring("",w)
	writelwostring("COLR",0,w):writelwoshort(14,w)
		writelwopointfloat(Rnd(1),Rnd(1),Rnd(1),w)
		Writelwoshort(0,w)
Next
writelwostring("VMAP",uvsize,w)
	writelwostring("TXUV",0,w)
	writelwoshort(2,w)
	writelworstring("UV Texture",w)
	tempvar = 0
For surfe = 1 To CountSurfaces(mesh)
surf = GetSurface(mesh,surfe)
	For vertindex = 0 To CountVertices(surf)-1
	writelwoshort(tempvar,w)
	writelwofloat(w,VertexU(surf,vertindex))
	writelwofloat(w,1.0-VertexV(surf,vertindex))
	tempvar = tempvar + 1
	Next
Next

CloseFile w
End Function



Function WriteLwoString(stri$,size,file)
For e = 1 To Len(stri)
ty$ = Mid(stri,e,1)
val = Asc(ty)
WriteByte(file,val)
Next
If size <> 0
WriteMotorolaInt(size,file)
;WriteInt(file,size)
EndIf
End Function

Function WriteLwoPointFloat(floa#,floa1#,floa2#,file)
WriteLwoFloat(file,floa)
WriteLwoFloat(file,floa1)
WriteLwoFloat(file,floa2)
End Function

Function WriteLwoFloat(file,floa#)
bank = CreateBank(4)
PokeFloat(bank, 0, floa) ; write the float value
swapped% = Int_SwapEndian(PeekInt(bank, 0))
FreeBank(bank)
WriteInt(file,swapped)
End Function

Function WriteLwoShort(short,file)
WriteByte(file,short Shr 8 And $FF)
WriteByte(file,short And $FF)
;WriteShort(file,short)
End Function

Function FindStringLength(stri$)
size = Len(stri)+1
even = size Mod 2
If even <> 0 Then size = size + 1
Return size
End Function

Function FindVertIndex(index,surfi,mesh)
count = 0
For surfe = 1 To CountSurfaces(mesh)
surf = GetSurface(mesh,surfe)
	For vertindex = 0 To CountVertices(surf)-1
	If index = vertindex And surf = surfi Then Return count
	count = count + 1
	Next
Next
End Function

Function WriteLwoRString(stri$,file)
For e = 1 To Len(stri)
ty$ = Mid(stri,e,1)
WriteByte(file,Asc(ty))
Next
WriteByte(file,0)
even = (Len(stri)+1) Mod 2
If even <> 0 Then WriteByte(file,0)
End Function

Function WriteMotorolaInt(a,w)
WriteByte(w, a Shr 24 And $FF)
WriteByte(w, a Shr 16 And $FF)
WriteByte(w, a Shr 8 And $FF)
WriteByte(w, a And $FF)
End Function

Function Int_SwapEndian%(n#)
var = $FF000000
var2 = $FF0000
Return ((n And $FF) Shl 24) Or ((n And $FF00) Shl 8) Or ((n And var2) Shr 8) Or ((n And var) Shr 24)
End Function
