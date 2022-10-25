; ID: 665
; Author: Difference
; Date: 2003-05-01 17:44:54
; Title: B3D - &gt; XML -&gt; B3D
; Description: B3D text file format

;--------------------------------------------------------------
; Date: 2003.05.02
; B3D text file (xml) format Rev 0.3 by Peter Scheutz
; Based on the official Sample Blitz code for the B3D format
;
;
; This is a rough version, expect cleanups in the near future...
;
;--------------------------------------------------------------

Include "b3dfile.bb" 	; get this file from www.blitzbasic.com -> 
				; Community  -> Specs And utils -> Sample Blitz code
				
;OSB:	in b3dfile.bb change "Function b3dReadFloat()" to "Function b3dReadFloat#()"			
				


Const B3DXML_ERROR_NONE=True
Const B3DXML_ERROR_NOFILE=-1
Const B3DXML_ERROR_FILENOTVALID =-2
Const B3DXML_ERROR_WRONGVERSION =-3


Const B3DXML_FLAG_StripTexturePaths=1
Const B3DXML_FLAG_StripBrushPaths=2
Const B3DXML_FLAG_StripNodePaths=4
Const B3DXML_FLAG_SkipVertices=8
Const B3DXML_FLAG_SkipTriangles=16
Const B3DXML_FLAG_SkipKeys=32
Const B3DXML_FLAG_SkipWeights=64

Global b3dxml_emf
Global b3dxml_xml$
Global b3dxml_xmlpos
Global b3dxml_xmlpos2
Global b3dxml_xmlpos3
Global b3dxml_kflags
Global b3dxml_brushtexcount
Global b3dxml_vflags
Global b3dxml_vtexsets
Global b3dxml_vtexsize


;retval=b3d2xml( "man.b3d" ,"maplet.xml",127)

; convert a b3d to xml
retval=b3d2xml( "robot2.b3d" ,"robot.xml")

; path stripping version:
;retval=b3d2xml( "robot2.b3d" ,"robot.xml",7)

If  retval= B3DXML_ERROR_NONE Then
	msg$ = "File successfully converted"
Else
	Select retval
	
		Case B3DXML_ERROR_NOFILE
			msg$ = "Input file not found"	
		Case B3DXML_ERROR_FILENOTVALID
			msg$ = "This is not a valid B3D file"	
		Case B3DXML_ERROR_WRONGVERSION
			msg$ = "Wrong B3D version"	
	End Select


EndIf


; convert xml back to b3d
retval = xml2B3d( "robot.xml" ,"test.b3d")

If  retval= B3DXML_ERROR_NONE Then
	msg$ = "File successfully converted"
Else
	Select retval
	
		Case B3DXML_ERROR_NOFILE
			msg$ = "Input file not found"	
		Case B3DXML_ERROR_FILENOTVALID
			msg$ = "This is not a valid B3D file"	
		Case B3DXML_ERROR_WRONGVERSION
			msg$ = "Wrong B3D version"	
	End Select


EndIf


; convert to xml again
retval=b3d2xml( "test.b3d" ,"test.xml")


If  retval= B3DXML_ERROR_NONE Then
	msg$ = "File successfully converted"
Else
	Select retval
	
		Case B3DXML_ERROR_NOFILE
			msg$ = "Input file not found"	
		Case B3DXML_ERROR_FILENOTVALID
			msg$ = "This is not a valid B3D file"	
		Case B3DXML_ERROR_WRONGVERSION
			msg$ = "Wrong B3D version"	
	End Select


EndIf

;ExecFile "test.xml" ;uncomment to view in default xml viewer (usually IE)



Print msg$ 
DebugLog msg$ 


WaitKey

End



Function xml2B3d(xmlfile$,b3dfile$)

	Local xbank=CreateBank(FileSize(xmlfile$))




	file=ReadFile(xmlfile$)
	
	If Not file Return B3DXML_ERROR_NOFILE
	
	;ret=ReadBytes(xbank,file,0,BankSize(xbank))
	
	While Not Eof(file)
		b3dxml_xml$=b3dxml_xml$ + ReadLine(file)
	Wend
	
	
	CloseFile 	file	


;	DebugLog b3dxml_xml$

;	DebugLog "*************************************************"


	b3dxml_xml$ = Replace (b3dxml_xml$," >",">")
	b3dxml_xml$ = Replace (b3dxml_xml$," />","/>")



;	DebugLog b3dxml_xml$


;	WaitKey
;	End

	file=WriteFile( b3dfile$ )

	b3dSetFile( file )
	
	b3dxml_xmlpos=Instr(b3dxml_xml$,"<BB3D")-1
	TransChunks 
	
	CloseFile 	file

	Return B3DXML_ERROR_NONE	

End Function






Function bankToString$(bank)
	Local ret$

	For n=0 To BankSize(bank)-1
		ret$=ret$ + Chr$(PeekByte(bank,n))	
	Next

	Return ret$

End Function




Function xmlReadTag$()
	Local pos2
	
	b3dxml_xmlpos=Instr(b3dxml_xml$,"<",b3dxml_xmlpos+1)
	
	
	
	If b3dxml_xmlpos>0 Then
		b3dxml_xmlpos2=Instr(b3dxml_xml$," ",b3dxml_xmlpos+1)
		pos3=Instr(b3dxml_xml$,">",b3dxml_xmlpos+1)
		
		If pos3<>0 
			If b3dxml_xmlpos2<>0
				If pos3<b3dxml_xmlpos2 Then b3dxml_xmlpos2=pos3
			Else
				b3dxml_xmlpos2=pos3
			EndIf
		
		EndIf

		If b3dxml_xmlpos2<>0
			b3dxml_xmlpos3=Instr(b3dxml_xml$,">",b3dxml_xmlpos2)
			Return Mid$(b3dxml_xml$,b3dxml_xmlpos+1,b3dxml_xmlpos2-b3dxml_xmlpos-1) 
		EndIf
	EndIf

End Function

Function xmlReadInt(attrib$,defval=0)
	Local pos1,pos2
	Local lookfor$=attrib$ + "=" + Chr$(34)

	pos1=Instr(b3dxml_xml$,lookfor$,b3dxml_xmlpos)
	If Not pos1 Return defval
	pos1=pos1+Len(lookfor$)
	pos2=Instr(b3dxml_xml$,Chr$(34),pos1+1)
	
	
	 
	
	Return Int(Mid$(b3dxml_xml$,pos1,pos2-pos1))


End Function


Function xmlReadString$(attrib$)
	Local pos1,pos2
	Local lookfor$=attrib$ + "=" + Chr$(34)

	pos1=Instr(b3dxml_xml$,lookfor$,b3dxml_xmlpos)
	If Not pos1 Return
	pos1=pos1+Len(lookfor$)
	pos2=Instr(b3dxml_xml$,Chr$(34),pos1+1)
	
	

	
	
	Return Mid$(b3dxml_xml$,pos1,pos2-pos1)


End Function



Function xmlReadFloat#(attrib$,defval#=0)
	Local pos1,pos2
	Local lookfor$=attrib$ + "=" + Chr$(34)

	pos1=Instr(b3dxml_xml$,lookfor$,b3dxml_xmlpos)
	If Not pos1 Return defval

	pos1=pos1+Len(lookfor$)
	pos2=Instr(b3dxml_xml$,Chr$(34),pos1+1)
	
	;DebugLog "here. " + Mid$(b3dxml_xml$,pos1,pos2-pos1)
	
	;DebugLog "****" + Mid$(b3dxml_xml$,pos1,pos2-pos1) + "****"	
	
	Return Float(Mid$(b3dxml_xml$,pos1,pos2-pos1)) 


End Function



Function TransChunks()
	Local closing


	While b3dxml_xmlpos
		closing=False
		chunk$=xmlReadTag$()
		

		Select chunk$
		Case "BB3D"
			b3dBeginChunk(chunk$)
			b3dWriteInt xmlReadInt("version")
			
		Case "/BB3D"
			b3dEndChunk	

		Case "ANIM"
			b3dBeginChunk(chunk$)
			
			b3dWriteInt xmlReadInt("version")
			b3dWriteInt xmlReadInt("frames")
			b3dWritefloat xmlReadFloat("fps")
			
			b3dEndChunk


		Case "KEYS"
			b3dBeginChunk(chunk$)
			b3dxml_kflags=xmlReadInt("flags")
			b3dWriteInt b3dxml_kflags

		Case "/KEYS"
			b3dEndChunk
		Case "KEY"
				
			b3dWriteInt xmlReadInt("frame")

			
			If b3dxml_kflags And 1
				b3dWritefloat xmlReadFloat("xpos")
				b3dWritefloat xmlReadFloat("ypos")
				b3dWritefloat xmlReadFloat("zpos")
			EndIf
			
			If b3dxml_kflags And 2
				b3dWritefloat xmlReadFloat("xscale")
				b3dWritefloat xmlReadFloat("yscale")
				b3dWritefloat xmlReadFloat("zscale")

			EndIf
			
			If b3dxml_kflags And 4
				b3dWritefloat xmlReadFloat("wrot")
				b3dWritefloat xmlReadFloat("xrot")
				b3dWritefloat xmlReadFloat("yrot")
				b3dWritefloat xmlReadFloat("zrot")
			EndIf

		Case "TEXS"
			b3dBeginChunk(chunk$)
		Case "/TEXS"
			b3dEndChunk
		
		Case "TEXTURE"
			b3dWriteString xmlReadString("name")
			b3dWriteInt xmlReadInt("flags")
			b3dWriteInt xmlReadInt("blend")
			b3dWritefloat xmlReadFloat("xpos")
			b3dWritefloat xmlReadFloat("ypos")
			b3dWritefloat xmlReadFloat("xscale")
			b3dWritefloat xmlReadFloat("yscale")
			b3dWritefloat xmlReadFloat("rot")
		

		Case "BRUS"
			b3dBeginChunk(chunk$)
			b3dxml_brushtexcount=xmlReadInt("count")
			b3dWriteInt b3dxml_brushtexcount
		Case "/BRUS"
			b3dEndChunk
			
		Case "BRUSH"
			b3dWriteString xmlReadString("name")
			b3dWritefloat xmlReadFloat("red")
			b3dWritefloat xmlReadFloat("green")
			b3dWritefloat xmlReadFloat("blue")
			b3dWritefloat xmlReadFloat("alpha")
			b3dWritefloat xmlReadFloat("shininess")
			b3dWriteInt xmlReadInt("blend")
			b3dWriteInt xmlReadInt("fx")

														
			For n=0 To b3dxml_brushtexcount-1
				b3dWriteInt xmlReadInt("texture" + n)		
			Next		
					
		Case "NODE"
			b3dBeginChunk(chunk$)
			b3dWriteString xmlReadString("name")
			
		;	DebugLog "*" + xmlReadString("name") + "*"

		;	DebugLog "*" + xmlReadFloat("xscale") + "*"
			
			
			b3dWritefloat xmlReadFloat("xpos")
			b3dWritefloat xmlReadFloat("ypos")
			b3dWritefloat xmlReadFloat("zpos")
			
			b3dWritefloat xmlReadFloat("xscale")
			b3dWritefloat xmlReadFloat("yscale")
			b3dWritefloat xmlReadFloat("zscale")
			
			b3dWritefloat xmlReadFloat("wrot")
			b3dWritefloat xmlReadFloat("xrot")
			b3dWritefloat xmlReadFloat("yrot")
			b3dWritefloat xmlReadFloat("zrot")
		
		Case "/NODE"
			b3dEndChunk
		
		Case "MESH"
			b3dBeginChunk(chunk$)
			b3dWriteInt xmlReadInt("brush")
			
		Case "/MESH"
			b3dEndChunk

		Case "BONE"
			b3dBeginChunk(chunk$)
		
		Case "/BONE"
			b3dEndChunk
			
		Case "WEIGHT"
			b3dWriteInt xmlReadInt("vertex")
			b3dWritefloat xmlReadFloat("weight")
		
	
	
		Case "VRTS"
			b3dBeginChunk(chunk$)
			b3dxml_vflags=xmlReadInt("flags")
			b3dxml_vtexsets=xmlReadInt("texturecoordsets")
			b3dxml_vtexsize=xmlReadInt("texturecoordsize")
			
			b3dWriteInt b3dxml_vflags
			b3dWriteInt b3dxml_vtexsets
			b3dWriteInt b3dxml_vtexsize

		Case "/VRTS"
			b3dEndChunk
			
		Case "VERTEX"
		
		
			b3dWritefloat xmlReadFloat("x")
			b3dWritefloat xmlReadFloat("y")
			b3dWritefloat xmlReadFloat("z")
			
											
						
			If b3dxml_vflags And 1
				b3dWritefloat xmlReadFloat("nx")
				b3dWritefloat xmlReadFloat("ny")
				b3dWritefloat xmlReadFloat("nz")						
										
			EndIf
			If b3dxml_vflags And 2
				b3dWritefloat xmlReadFloat("r")
				b3dWritefloat xmlReadFloat("g")
				b3dWritefloat xmlReadFloat("b")
				b3dWritefloat xmlReadFloat("a")
			EndIf


			For j=1 To b3dxml_vtexsets
				
				
				If b3dxml_vtexsize>0 Then b3dWritefloat xmlReadFloat("u"+(j-1))
				If b3dxml_vtexsize>1 Then b3dWritefloat xmlReadFloat("v"+(j-1))				
				If b3dxml_vtexsize>2 Then b3dWritefloat xmlReadFloat("w"+(j-1))
						
			Next

		Case "TRIS"
			b3dBeginChunk(chunk$)
			b3dWriteInt xmlReadInt("brush")
					  
		Case "/TRIS"
			b3dEndChunk			

		Case "TRI"

			b3dWriteInt xmlReadInt("v0")
			b3dWriteInt xmlReadInt("v1")
			b3dWriteInt xmlReadInt("v2")
			
		End Select

	;	TransChunks()	;dump any subchunks
	;	If	closing Then b3dEndChunk					;exit this chunk
;		DebugLog "- " + Mid$(b3dxml_xml$,b3dxml_xmlpos3-1,1)
	
	 ; If chunk$<>"ANIM" Then emit "</" + chunk$ +  ">" 	
		
	Wend
End Function







Function b3d2xml(b3dfile$,xmlfile$,dumpflags=0)

	Local file


	file=ReadFile( b3dfile$)
	If Not file Return B3DXML_ERROR_NOFILE
	
	b3dSetFile( file )
	
	If b3dReadChunk$()<>"BB3D"  Return B3DXML_ERROR_FILENOTVALID ;RuntimeError "Invalid b3d file"
	
	version=b3dReadInt()
	
	If version/100>0 Return Return B3DXML_ERROR_WRONGVERSION ;RuntimeError "Invalid b3d file version"
	
	
	emitfile$=xmlfile$
	
	b3dxml_emf=WriteFile(emitfile$)
	
	
	emit  "<?xml version=" + Chr$(34) + "1.0"  +  Chr$(34) +  "?>"
	emit  "<BB3D version=" +  Chr$(34) + version +Chr$(34) + " exporter=" + Chr$(34) + "dumpb3dasxml version 0.1" + Chr$(34) +">"
	
	
	DumpChunks(dumpflags,"*")
	
	b3dExitChunk()
	
	emit "</BB3D>"
	
	If b3dxml_emf Then CloseFile b3dxml_emf
	
	
	CloseFile file


	Return B3DXML_ERROR_NONE

End Function



Function DumpChunks(dumpflags, tab$="" )
	tt$=tab$
	tab$=tab$+"  "
	While b3dChunkSize()
		chunk$=b3dReadChunk$()
		
		
		Select chunk$
		Case "ANIM"
	
			flags=b3dReadInt()
			n_frames=b3dReadInt()
			fps=b3dReadFloat()
			
			out$=BeginTag$("ANIM")
			out$=AddIntegerAttrib(out$,"flags",flags)
			out$=AddIntegerAttrib(out$,"frames",n_frames)
			out$=AddFloatAttrib(out$,"fps",fps)																							
			out$ = CloseAndEndTag(out$)				
			emit out$			
			
						
		Case "KEYS"
		
			flags=b3dReadInt()

			out$=BeginTag$("KEYS")
			out$=AddIntegerAttrib(out$,"flags",flags)																						
			out$ = CloseTag(out$)				
			emit out$			
			

			sz=4
			If flags And 1 sz=sz+12
			If flags And 2 sz=sz+12
			If flags And 4 sz=sz+16
			n_keys=b3dChunkSize()/sz
			If n_keys*sz=b3dChunkSize() 
			
				While b3dChunkSize()
					frame=b3dReadInt()
						
					out$=BeginTag$("KEY")
					out$=AddIntegerAttrib(out$,"frame",frame)
					
					
					If flags And 1
						key_px#=b3dReadFloat()
						key_py#=b3dReadFloat()
						key_pz#=b3dReadFloat()
						
						out$=AddFloatAttrib(out$,"xpos",key_px)
						out$=AddFloatAttrib(out$,"ypos",key_py)
						out$=AddFloatAttrib(out$,"zpos",key_pz)				
						
					EndIf
					
					If flags And 2
						key_sx#=b3dReadFloat()
						key_sy#=b3dReadFloat()
						key_sz#=b3dReadFloat()

						out$=AddFloatAttrib(out$,"xscale",key_sx)
						out$=AddFloatAttrib(out$,"yscale",key_sy)
						out$=AddFloatAttrib(out$,"zscale",key_sz)

					EndIf
					
					If flags And 4
						key_rw#=b3dReadFloat()
						key_rx#=b3dReadFloat()
						key_ry#=b3dReadFloat()
						key_rz#=b3dReadFloat()

						out$=AddFloatAttrib(out$,"wrot",key_rw)
						out$=AddFloatAttrib(out$,"xrot",key_rx)
						out$=AddFloatAttrib(out$,"yrot",key_ry)
						out$=AddFloatAttrib(out$,"zrot",key_rz)

					EndIf
					
					out$ = CloseAndEndTag(out$)				
					If Not (dumpflags And B3DXML_FLAG_SkipKeys )
						emit out$
					EndIf						
					
				Wend
			Else
				;;;DebugLog tab$+"***** Illegal number of keys *****"
			EndIf
		Case "TEXS"
		  emit "<" + chunk$ + ">"
		
			While b3dChunkSize()
				name$=b3dReadString$()
				flags=b3dReadInt()
				blend=b3dReadInt()
				x_pos#=b3dReadFloat()
				y_pos#=b3dReadFloat()
				x_scl#=b3dReadFloat()
				y_scl#=b3dReadFloat()
				rot#=b3dReadFloat()



				If dumpflags And B3DXML_FLAG_StripTexturePaths Then  name$=StripPath(name$)

				out$=BeginTag$("TEXTURE")
				out$=AddStringAttrib(out$,"name",name$)
				out$=AddIntegerAttrib(out$,"flags",flags)				
				out$=AddIntegerAttrib(out$,"blend",blend)
				out$=AddFloatAttrib(out$,"xpos",x_pos)	; Changed from xoffset to xpos
				out$=AddFloatAttrib(out$,"ypos",y_pos)	; Changed from yoffset to ypos
				out$=AddFloatAttrib(out$,"xscale",x_scl)
				out$=AddFloatAttrib(out$,"yscale",y_scl)
				out$=AddFloatAttrib(out$,"rot",rot)																								
				
				out$ = CloseAndEndTag(out$)				
				emit out$	
				
			Wend
		Case "BRUS"

			n_texs=b3dReadInt()
			
			out$=BeginTag$("BRUS")
			out$=AddIntegerAttrib(out$,"count",n_texs)																						
			out$ = CloseTag(out$)				
			emit out$				
			
			
			;read all brushes in chunk...
			While b3dChunkSize()
				name$=b3dReadString$()
				red#=b3dReadFloat()
				grn#=b3dReadFloat()
				blu#=b3dReadFloat()
				alp#=b3dReadFloat()
				shi#=b3dReadFloat()
				blend=b3dReadInt()
				fx=b3dReadInt()
				
				If dumpflags And B3DXML_FLAG_StripBrushPaths Then  name$=StripPath(name$)
				
				
				out$=BeginTag$("BRUSH")
				out$=AddStringAttrib(out$,"name",name$)
				out$=AddFloatAttrib(out$,"red",red)				
				out$=AddFloatAttrib(out$,"green",grn)
				out$=AddFloatAttrib(out$,"blue",blu)
				out$=AddFloatAttrib(out$,"alpha",alp)
				out$=AddFloatAttrib(out$,"shininess",shi)
				out$=AddIntegerAttrib(out$,"blend",blend)
				out$=AddIntegerAttrib(out$,"fx",fx)																								
				
					
				For k=0 To n_texs-1
					tex_id=b3dReadInt()
					out$ =  AddIntegerAttrib(out$,"texture" + k ,tex_id)
				Next
				
				out$ = CloseAndEndTag(out$)				
				emit out$	
				
			Wend
		Case "VRTS"
			flags=b3dReadInt()
			tc_sets=b3dReadInt()
			tc_size=b3dReadInt()
			sz=12+tc_sets*tc_size*4
			If flags And 1 Then sz=sz+12
			If flags And 2 Then sz=sz+16
			n_verts=b3dChunkSize()/sz
			
			
			out$=BeginTag$("VRTS")
			out$=AddIntegerAttrib(out$,"count",n_verts) 			
			out$=AddIntegerAttrib(out$,"flags",flags) 
			out$=AddIntegerAttrib(out$,"texturecoordsets",tc_sets)
			out$=AddIntegerAttrib(out$,"texturecoordsize",tc_size)
			out$ = CloseTag(out$)				
			emit out$			
			
			If n_verts*sz=b3dChunkSize() 
				;read all verts in chunk
				While b3dChunkSize()
									
					x#=b3dReadFloat()
					y#=b3dReadFloat()
					z#=b3dReadFloat()
					
					out$=BeginTag$("VERTEX")
					out$=AddFloatAttrib(out$,"x",x)
					out$=AddFloatAttrib(out$,"y",y)
					out$=AddFloatAttrib(out$,"z",z)
					
					If flags And 1
						nx#=b3dReadFloat()
						ny#=b3dReadFloat()
						nz#=b3dReadFloat()
						
						out$=AddFloatAttrib(out$,"nx",nx)
						out$=AddFloatAttrib(out$,"ny",ny)
						out$=AddFloatAttrib(out$,"nz",nz)						
												
					EndIf
					If flags And 2
						r#=b3dReadFloat()
						g#=b3dReadFloat()
						b#=b3dReadFloat()
						a#=b3dReadFloat()
						
						out$=AddFloatAttrib(out$,"r",r)
						out$=AddFloatAttrib(out$,"g",g)
						out$=AddFloatAttrib(out$,"b",b)						
						out$=AddFloatAttrib(out$,"a",a)
						
					EndIf


					coordnameindex=0
					set=0
					;read tex coords...
					For j=1 To tc_sets*tc_size
						uvw#=b3dReadFloat()
						
						coordnameindex=coordnameindex + 1
						If coordnameindex>tc_size Then
							coordnameindex =1
							set=set+1
						EndIf

						If coordnameindex=1
							cn$="u"
						ElseIf coordnameindex=2
							cn$="v"
						Else
							cn$="w"
						EndIf
						
						out$=AddFloatAttrib(out$,cn$ + Str$(set),uvw)
						
					Next

					out$ = CloseAndEndTag(out$)	
								
					If Not (dumpflags And B3DXML_FLAG_SkipVertices )
						emit out$
					EndIf
					
				Wend
			Else
				;;;DebugLog tab$+"***** Illegal number of vertices *****"
			EndIf
		Case "TRIS"
		  
			brush_id=b3dReadInt()
			sz=12
			n_tris=b3dChunkSize()/sz
			
			out$=BeginTag$("TRIS")
			out$=AddIntegerAttrib(out$,"count",n_tris) ; trianglecount changed to count
			out$=AddIntegerAttrib(out$,"brush",brush_id)
			out$ = CloseTag(out$)				
			emit out$			
			
			If n_tris*sz=b3dChunkSize()
				;read all tris in chunk
				While b3dChunkSize()
					v0=b3dReadInt()
					v1=b3dReadInt()
					v2=b3dReadInt()

					If Not (dumpflags And B3DXML_FLAG_SkipTriangles )
						out$=BeginTag$("TRI")
						out$=AddIntegerAttrib(out$,"v0",v0)
						out$=AddIntegerAttrib(out$,"v1",v1)
						out$=AddIntegerAttrib(out$,"v2",v2)
						out$ = CloseAndEndTag(out$)				
						emit out$						
					EndIf
					
				Wend
			Else
				;;;DebugLog tab$+"***** Illegal number of triangles *****"
			EndIf
		Case "MESH"
			brush_id=b3dReadInt()
			
			out$=BeginTag$("MESH")
			out$=AddIntegerAttrib(out$,"brush",brush_id)																						
			out$ = CloseTag(out$)				
			emit out$				
			
		Case "BONE"
		  emit "<" + chunk$ + ">"
			sz=8
			n_weights=b3dChunkSize()/sz
			
			If n_weights*sz=b3dChunkSize()
				;read all weights
				While b3dChunkSize()
					vertex_id=b3dReadInt()
					weight#=b3dReadFloat()
					
					If Not (dumpflags And B3DXML_FLAG_SkipWeights )
						out$=BeginTag$("WEIGHT")
						out$=AddIntegerAttrib(out$,"vertex",vertex_id)
						out$=AddFloatAttrib(out$,"weight",weight)
						out$ = CloseAndEndTag(out$)				
						emit out$						
					EndIf
					
				Wend
			Else
				;;;DebugLog tab$+"***** Illegal number of bone weights *****"
			EndIf
		Case "NODE"
			name$=b3dReadString$()
			x_pos#=b3dReadFloat()
			y_pos#=b3dReadFloat()
			z_pos#=b3dReadFloat()
			x_scl#=b3dReadFloat()
			y_scl#=b3dReadFloat()
			z_scl#=b3dReadFloat()
			w_rot#=b3dReadFloat()
			x_rot#=b3dReadFloat()
			y_rot#=b3dReadFloat()
			z_rot#=b3dReadFloat()
	
			If dumpflags And B3DXML_FLAG_StripNodePaths Then  name$=StripPath(name$)	
			
			out$=BeginTag$("NODE")
			out$=AddStringAttrib(out$,"name",name$)
			out$=AddFloatAttrib(out$,"xpos",x_pos)				
			out$=AddFloatAttrib(out$,"ypos",y_pos)
			out$=AddFloatAttrib(out$,"zpos",z_pos)
			out$=AddFloatAttrib(out$,"xscale",x_scl)
			out$=AddFloatAttrib(out$,"yscale",y_scl)
			out$=AddFloatAttrib(out$,"zscale",z_scl)
			out$=AddFloatAttrib(out$,"wrot",w_rot)
			out$=AddFloatAttrib(out$,"xrot",x_rot)
			out$=AddFloatAttrib(out$,"yrot",y_rot)
			out$=AddFloatAttrib(out$,"zrot",z_rot)
		
			out$ = CloseTag(out$)				
			emit out$			
			
		Default
			; OBS untested!

	;		out$=BeginTag$(chunk$)
	;		out$=AddIntegerAttrib(out$,"size",b3dChunkSize())
	;		out$ = CloseTag(out$)				
	;		emit out$
			
	;		out$="<![CDATA[" 
		
	;		While b3dChunkSize()
	;			out$=out$ + Chr$(b3dReadByte())	
	;		Wend
		
	;		out$=out$ + "]]>"
	;		emit out$
		
			
		End Select

		DumpChunks(dumpflags, tab$ )	;dump any subchunks
		b3dExitChunk()					;exit this chunk
	
	
	  If chunk$<>"ANIM" Then emit "</" + chunk$ +  ">" 	
		
	Wend
End Function


Function StripPath$(name$)
	Local pos

	For pos=Len(name$) To 1 Step -1
		If Mid$(name$,pos,1)="\" Or Mid$(name$,pos,1)="/" Then Exit
	Next

	Return Right$(name$,Len(name$)-pos)


End Function

Function emit(out$)
	;DebugLog out$

	If b3dxml_emf Then WriteLine b3dxml_emf,out$
End Function


Function AddFloatAttrib$(tag$,name$,value#)

	If Right$(tag$,1)<>" " Then
		Return tag$ + " " + name$ + "=" + Chr$(34) + value# + Chr$(34)
	Else
		Return tag$ + name$ + "=" + Chr$(34) + value# + Chr$(34)
	EndIf


End Function

Function AddStringAttrib$(tag$,name$,value$)

	If Right$(tag$,1)<>" " Then
		Return tag$ + " " + name$ + "=" + Chr$(34) + value$ + Chr$(34)
	Else
		Return tag$ + name$ + "=" + Chr$(34) + value$ + Chr$(34)
	EndIf


End Function

Function AddIntegerAttrib$(tag$,name$,value%)

	If Right$(tag$,1)<>" " Then
		Return tag$ + " " + name$ + "=" + Chr$(34) + value% + Chr$(34)
	Else
		Return tag$ + name$ + "=" + Chr$(34) + value% + Chr$(34)
	EndIf


End Function



Function BeginTag$(tag$)
	Return "<" + tag$

End Function

Function CloseTag$(tag$)
	Return tag$ + ">"

End Function

Function CloseAndEndTag$(tag$)
	Return tag$ + "/>"

End Function
