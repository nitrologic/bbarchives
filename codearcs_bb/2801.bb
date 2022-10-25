; ID: 2801
; Author: Leon Drake
; Date: 2010-12-25 12:06:59
; Title: OBJ Exporter
; Description: export a mesh in blitz3d to obj format

Function SaveMeshOBJ(mesh,filename$)


	Local file,surfcount,syrf,si,brush,brushtex,texname$,nov,facedef$,offset,vi
	If mesh = 0 Return False
	
	file=WriteFile(stripext$(filename$)+".mtl")
	If Not file Return False ;fail code
	
	WriteLine(file,"#  Wavefront material file")
	WriteLine(file,"#  Converted from b3d by Landon Ritchie")
	
	;get a list of materials
	surfcount = CountSurfaces(mesh)
	
	For si = 1 To CountSurfaces(mesh)
	
		surf=GetSurface(mesh,si)
	brush = GetSurfaceBrush(surf)
		brushtex = GetBrushTexture(brush)
		texname$ = TextureName$(brushtex)
			WriteLine(file,"newmtl "+stripext$(stripdir$(texname$))+"1073741824")
			WriteLine(file,"Ka 1 1 1")
			WriteLine(file,"Ns 8")
			WriteLine(file,"illum 2")
			WriteLine(file,"map_Kd "+stripdir$(texname$))
			
			
	
	Next
	CloseFile(file)
	file=WriteFile(filename$)
	If Not file Return False ;fail code	
	
	
	WriteLine(file,"#  Wavefront object file")	
	WriteLine(file,"#  Converted from b3d by Landon Ritchie")	
	WriteLine(file,"mtllib "+stripdir$(stripext$(filename$)+".mtl"))
	offset = 1
	For si = 1 To CountSurfaces(mesh)
	
		surf=GetSurface(mesh,si)
	brush = GetSurfaceBrush(surf)
		brushtex = GetBrushTexture(brush)
		texname$ = TextureName$(brushtex)
			WriteLine(file,"usemtl "+stripext$(stripdir$(texname$))+"1073741824")
			
			facedef$ = "f"
			nov=CountVertices(surf)
			For vi = 0 To CountVertices(surf)-1
			
				facedef$ = facedef$ + " "+Str(vi+offset)+"/"+Str(vi+offset)+"/"+Str(vi+offset)
			
			Next
			
			WriteLine(file,facedef$)

			For vi = 0 To CountVertices(surf)-1
			
				WriteLine(file,"v "+VertexX(surf,vi)+" "+VertexY(surf,vi)+" "+VertexZ(surf,vi))
			
			Next
			For vi = 0 To CountVertices(surf)-1
			
				WriteLine(file,"vt "+(-VertexU(surf,vi))+" "+(-VertexV(surf,vi))+" "+VertexW(surf,vi))
			
			Next
			For vi = 0 To CountVertices(surf)-1
			
				WriteLine(file,"vn "+Abs(VertexNX(surf,vi))+" "+Abs(VertexNY(surf,vi))+" "+Abs(VertexNZ(surf,vi)))
			
			Next
						
			offset = offset + CountVertices(surf)
	
	Next		
	
	CloseFile(file)
	DebugLog "Exported OBJ"
	
	

End Function


Function stripdir$(filename$)

	If Len(filename$) = 0 Then Return filename$

	For i = Len(filename$) To 1 Step -1
	
		
	
		If Mid(filename$,i,1) = "\" Or Mid(filename$,i,1) = "/" Then
		
			;DebugLog("WAT: "+Right(filename$,Len(filename$)-i))
			Return Right(filename$,Len(filename$)-i)
		
		EndIf
	
	Next
	Return filename$


End Function



Function stripext$(filename$)
	If Len(filename$) = 0 Then Return filename$
	For i = Len(filename$) To 1 Step -1
	
		If Mid(filename$,i,1) = "." Then
		
			Return Left(filename$,i-1)
		
		EndIf
	
	Next
	Return filename$


End Function


Function StripAll$(filename$)

If Len(filename$) = 0 Then Return filename$
Return stripext(stripdir(filename$))


End Function
