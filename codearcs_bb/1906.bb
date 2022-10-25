; ID: 1906
; Author: D4NM4N
; Date: 2007-01-27 01:33:30
; Title: Static Direct X, hierarchical mesh
; Description: Saves everything under 1 pivot to an X (static only)

Global XE_XF,XE_MAXtextures

Type XE_texdata
	Field idx,h,fn$
End Type
Function writemesh(mesh,e_filename$)
        ;Change to 0 for human readable but (((bigger))) output
        trunc=1

	;reset texture list
	XE_MAXtextures=0
	For td.xe_texdata =Each XE_texdata:Delete td:Next
	
	;open output file
	filenameout$=e_filename$
	XE_XF=WriteFile (filenameout$);
	
	;File version 
	WriteLine XE_XF,"xof 0302txt 0064"
	WriteLine XE_XF,""
	;Header 
	WriteLine XE_XF,"Header {"
	WriteLine XE_XF,"1;"
	WriteLine XE_XF,"0;"
	WriteLine XE_XF,"1;"
	WriteLine XE_XF,"}"
	WriteLine XE_XF,""
	
	WriteLine XE_XF,"//Exported By D-Mapper Registered to "+regname$

	;Write all materials in meshes

	XE_MAXtextures=0

	RecursiveAddMaterial(mesh,forcematerial$,e_directory)
	WriteLine XE_XF," //Truncatemarker" ; textures cannot be truncated for giles

	;Start of frame root
	WriteLine XE_XF,"Frame root {"
	WriteLine XE_XF,"   FrameTransformMatrix {"
	WriteLine XE_XF,"      1.000000,0.000000,0.000000,0.000000,"
	WriteLine XE_XF,"      0.000000,1.000000,0.000000,0.000000,"
	WriteLine XE_XF,"      0.000000,0.000000,1.000000,0.000000,"
	WriteLine XE_XF,"      0.000000,0.000000,0.000000,1.000000;;"
	WriteLine XE_XF,"   }"

	;chiname$=EntityName(mesh)
	RecursiveAddMesh(mesh)

	WriteLine XE_XF,"} //End of root"
	CloseFile XE_XF

	If trunc
		Rfile=ReadFile(filenameout$)
		Wfile=WriteFile(filenameout$+"temp.x")
		If rfile<>0 And wfile<>0
			headerwrite=1
			While Not Eof(rfile)
				lines=lines+1
				If headerwrite
					l$=ReadLine(rfile)
					WriteLine(wfile,l$)
					If Instr(l$,"//Exported By") headerwrite=0
				Else
					l$=ReadLine(rfile)
					If Instr (l$,"//") Then l$=usv(ReadLine(rfile),1,"//")
					l$=Trim(l$)
					WriteLine wfile,l$
					;dat$=dat$+l$
					;If MilliSecs()>gtime+1000 gtime=MilliSecs():messagebox(-1,0,300,"Truncating ","Collapsing whitespace, end Of Line characters and ","comments, Please Wait..","","Line "+lines+" "+ l$)

				EndIf
			Wend
			CloseFile rfile
			CloseFile wfile
		Else
			
			messagebox(0,0,300,"Error","Cannot create/read required file!",rfile,wfile)
		EndIf	
       EndIf
       ;CopyFile filenameout$+"temp",filenameout$

       ;anti NAN patch for X exporter
       fileR=openfile (filenameout)
       fileW=openfile (tempfilename)
       if fileR while not eof(fileR)
           l$=readline(fileR)             
           l$=replace (l$,"NaN","0.0")
           writeline(fileW,l$)
      wend
      closefile fileR
      closefile fileW
      copyfile fileW,fileR
      deletefile fileW

	
End Function


Function RecursiveAddMaterial(h,forcematerial$,e_directory$="")
DebugLog CountChildren(h)

	If EntityClass$(h)="Mesh"
		;get neccacaries
		For surfc=1 To CountSurfaces(h)
			surf=GetSurface(h,surfc)
			brush=GetSurfaceBrush(surf)
			tex=GetBrushTexture (brush)
			
			;get rid of tex path & copy textures
			If forcematerial$="" Then
				from$=TextureName(tex)
				XE_XFilen$=strip_path(from)
				CopyFile from,e_directory+XE_XFilen$
				DebugLog "Copying tex to "+e_directory+XE_XFilen$
			Else
				XE_XFilen$=strip_path(forcematerial$)
			EndIf 
	
			;mark possible 'new' texture index
			Tindex=XE_MAXtextures
	
			;see id texture is alreadt in database, if so make tindex match the existing one
			For txs.XE_texdata = Each XE_texdata
				If txs\fn=XE_XFilen$ Then noadd=1:Tindex=txs\idx
			Next
	
			;Save texture data in index with surface handle for an index key
			txs.XE_texdata = New XE_texdata
			txs\h = surf
			txs\idx = Tindex
			txs\fn = XE_XFilen$
			
			;only add texture if a new one is present
			If Not noadd
				WriteLine XE_XF,"Material dx_brush"+Str(Tindex)+" {"
				WriteLine XE_XF,"   1.000000;1.000000;1.000000;1.000000;;"
				WriteLine XE_XF,"   0.000000;"
				WriteLine XE_XF,"   1.000000;1.000000;1.000000;;"
				WriteLine XE_XF,"   0.000000;0.000000;0.000000;;"
				WriteLine XE_XF,"   TextureFilename {"
				WriteLine XE_XF,"      "+Chr(34)+XE_XFilen$+Chr(34)+";"
				WriteLine XE_XF,"   }"
				WriteLine XE_XF,"}"
				XE_MAXtextures=XE_MAXtextures+1
			EndIf 
	
			;do the cleaning
			FreeTexture Tex
			FreeBrush brush
		Next 
	EndIf 

	For cc =CountChildren(h) To 1 Step -1
		chi=GetChild (h,cc)
		RecursiveAddMaterial(chi,forcematerial$)
	Next 

End Function

Function GetStringofMatElement$(mesh,x,y)
	me$=GetMatElement(mesh,x,y)
	dp$=usv(me$,2,".")
	final$=me$
	If Len(dp$)=0
		final$=me$+"000000"
	ElseIf Len(dp$)=1
		final$=me$+"00000"
	ElseIf Len(dp$)=2
		final$=me$+"0000"
	ElseIf Len(dp$)=3
		final$=me$+"000"
	ElseIf Len(dp$)=4
		final$=me$+"00"
	ElseIf Len(dp$)=5
		final$=me$+"0"
	EndIf
	DebugLog "Matrix="+me$+" ("+dp$+") "+final$
	Return final$
End Function

Global recuse_depth
Function RecursiveAddMesh(h,basescaleX#=1,basescaleY#=1,basescaleZ#=1)
		recuse_depth=recuse_depth+1
		
		chiname$=EntityName(h)
		DebugLog "Recursing "+chiname+" Childs="+CountChildren(h)+" Depth="+recuse_depth
		If chiname="" Or Instr(chiname,"NoName") chiname="NoName"+MilliSecs()
		DebugLog chiname
		
;		mesh=CopyEntity(h)
;		EntityParent mesh,0
		mesh=h

		WriteLine XE_XF,"   Frame "+ChiName$+" {"
		WriteLine XE_XF,"      FrameTransformMatrix {"
		WriteLine XE_XF,"         "+GetStringOfMatElement(mesh,0,0)+","+GetStringOfMatElement(mesh,0,1)+","+GetStringOfMatElement(mesh,0,2)+",0.000000,"
		WriteLine XE_XF,"         "+GetStringOfMatElement(mesh,1,0)+","+GetStringOfMatElement(mesh,1,1)+","+GetStringOfMatElement(mesh,1,2)+",0.000000,"
		WriteLine XE_XF,"         "+GetStringOfMatElement(mesh,2,0)+","+GetStringOfMatElement(mesh,2,1)+","+GetStringOfMatElement(mesh,2,2)+",0.000000,"
		WriteLine XE_XF,"         "+GetStringOfMatElement(mesh,3,0)+","+GetStringOfMatElement(mesh,3,1)+","+GetStringOfMatElement(mesh,3,2)+",1.000000;;"
		WriteLine XE_XF,"   }"

		
		If EntityClass$(mesh)="Mesh"
			For surfc=1 To CountSurfaces (mesh)
				surf=GetSurface(mesh,surfc)
				verts=CountVertices(surf)
				tris=CountTriangles(surf)
				;meshinfo
				WriteLine XE_XF,"      Mesh "+ChiName$+" {"
	
	
				;Number of verts;
				WriteLine XE_XF,"         "+verts+";"
	
	
				;X;Y;Z; of verts;
				For v=0 To verts-1
					If v=verts-1 Then term$=";;" Else term$=";,"
					WriteLine XE_XF,"         "+VertexX (surf,v)+";"+VertexY (surf,v)+";"+VertexZ (surf,v)+term$
				Next
	
				
				;No of tris;
				WriteLine XE_XF,"         "+tris+";"
	
	
				;Tri ordering 3;t0,t1,t2;,
				For t=0 To Tris-1
					If t=tris-1 Then term$=";;" Else term$=";,"
					WriteLine XE_XF,"         3;"+TriangleVertex(Surf,t,0)+","+TriangleVertex(Surf,t,1)+","+TriangleVertex(Surf,t,2)+term$
				Next 
	
				;Material List
				WriteLine XE_XF,"         MeshMaterialList {"
				WriteLine XE_XF,"            1;";num of materials for mesh
				WriteLine XE_XF,"            "+tris+";";number of faces/tris
				For t=0 To tris-1
					If t=tris-1 Then term$=";;" Else term$=","
					WriteLine XE_XF,"            0"+term$ ; face indexes?
				Next 
				WriteLine XE_XF,"            {dx_brush"+LookUpTindex(surf)+"}"
				WriteLine XE_XF,"         } // end of material list"
	
				;Normals List
				WriteLine XE_XF,"         MeshNormals {"
				WriteLine XE_XF,"            "+verts+";"
				For v=0 To verts-1
					If v=verts-1 Then term$=";;" Else term$=";,"
					WriteLine XE_XF,"            "+VertexNX (surf,v)+";"+VertexNY (surf,v)+";"+VertexNZ (surf,v)+term$
				Next
				WriteLine XE_XF,"            "+tris+";"
				For t=0 To Tris-1
					If t=tris-1 Then term$=";;" Else term$=";,"
					WriteLine XE_XF,"            3;"+TriangleVertex(Surf,t,0)+","+TriangleVertex(Surf,t,1)+","+TriangleVertex(Surf,t,2)+term$
				Next 
				WriteLine XE_XF,"         } // end of normal list"
	
				;Texturecoords List
				WriteLine XE_XF,"         MeshTextureCoords {"
				WriteLine XE_XF,"            "+verts+";"
				For v=0 To verts-1
					If v=verts-1 Then term$=";;" Else term$=";,"
					WriteLine XE_XF,"            "+VertexU (surf,v)+";"+VertexV (surf,v)+term$
				Next
				WriteLine XE_XF,"         } // end of Texturecoord list"
	
				;Vertexcolor List
				WriteLine XE_XF,"         MeshVertexColors {"
				WriteLine XE_XF,"            "+verts+";"
				For v=0 To verts-1
					If v=verts-1 Then term$=";;" Else term$=";,"
					vred#=VertexRed (surf,v)/256
					vgreen#=VertexGreen (surf,v)/256
					vblue#=VertexBlue (surf,v)/256
					WriteLine XE_XF,"            "+v+";"+vred+";"+vgreen+";"+vblue+";"+VertexAlpha (surf,v)+term$
				Next
				WriteLine XE_XF,"         } // end of Vertexcolor list"
	
				;End of mesh
				WriteLine XE_XF,"      } // end of mesh block for "+ChiName$
	
			Next	 
		EndIf

		;FreeEntity mesh
		
		;do all childs.. of childs etc
		DebugLog "has "+CountChildren(h)
		For cc=CountChildren(h) To 1 Step -1
			chi=GetChild (h,cc)
			RecursiveAddMesh(chi,basescaleX#,basescaleY#,basescaleZ#)
		Next 


		;close branch frame
		WriteLine XE_XF,"   } // End of frame"+ChiName$
		DebugLog "recuse_depth Depth "+recuse_depth


	recuse_depth=recuse_depth-1

End Function

Function LookUpTindex(surf)
	;look up tindex from surface handle
	For t.XE_texdata = Each XE_texdata
		If t\h=surf Return t\idx
	Next 
End Function

Function strip_path$(f$)
	 	f$=Lower$(f$) ; Full (!) Texture Path
		lastknown=0
		For p=1 To Len (f$)
			If Instr(f$,"\",p) Then lastknown=lastknown+1
		Next
		fnl=Len(f$)-lastknown
		f$=Right(f$,fnl)
 		;DebugLog "filename stripped"+ f$

		Return f$
End Function

;'user separated values

Function USV$(in$,which%=1,sep$=",")

;''pipe seprated values

	Local n% = 1

	Local offset% = 0

	Local nextoffset% = 1

	Local ValueRet$ =""

	

	While offset<Len(in$)

		nextoffset = Instr(in$,sep$,offset+1)

		If nextoffset = 0

			nextoffset = Len(in$)+1

			which = n

		End If

		valueret$ = Mid$(in$,offset+1,nextoffset-offset-1)

		If which = n	

			Return valueret	

		End If

		offset = nextoffset

		n=n+1

	Wend



	Return n-1



End Function
