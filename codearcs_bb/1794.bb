; ID: 1794
; Author: Ricky Smith
; Date: 2006-08-23 16:28:08
; Title: Load/Save Anim .b3d
; Description: Loading and saving an animated .b3d

;--------------------------------------------------------------
; 
; B3d importer
; Adapted for PaceMaker by Ricky Smith
; Based on code by M Sibley & Peter Sheultz
; 
;
;--------------------------------------------------------------

Include "b3dfile.bb" 	; get this file from www.blitzbasic.com -> 
				; Community  -> Specs And utils -> Sample Blitz code
				
;OSB:	in b3dfile.bb change "Function b3dReadFloat()" to "Function b3dReadFloat#()"			
				
Global mesh
Dim PMK_tex_id%(255,255)
Const PMK_ERROR_NONE=True
Const PMK_ERROR_NOFILE=-1
Const PMK_ERROR_FILENOTVALID =-2
Const PMK_ERROR_WRONGVERSION =-3


Const PMK_FLAG_StripTexturePaths=1
Const PMK_FLAG_StripBrushPaths=2
Const PMK_FLAG_StripNodePaths=4
Const PMK_FLAG_SkipVertices=8
Const PMK_FLAG_SkipTriangles=16
Const PMK_FLAG_SkipKeys=32
Const PMK_FLAG_SkipWeights=64


Const PMK_MAX_KEYS = 5000
Const PMK_MAX_VRTS = 50000
Const PMK_MAX_TRIS = 20000
Const PMK_MAX_TEXS = 255
Const PMK_MAX_BRUS = 255

Global PMK_emf
Global PMK_xml$
Global PMK_xmlpos
Global PMK_xmlpos2
Global PMK_xmlpos3
Global PMK_kflags
Global PMK_brushtexcount
Global PMK_vflags
Global PMK_vtexsets
Global PMK_vtexsize
;Global cname$
.types
Type  NodeChunk
	Field name$
	Field x_pos#				
	Field y_pos#
	Field z_pos#
	Field x_scl#
	Field y_scl#
	Field z_scl#
	Field w_rot#
	Field x_rot#
	Field y_rot#
	Field z_rot#
	Field gui_id%
	Field joint
	Field geo
	;Field bone.ray
	Field prename$
	Field class$
	Field xrot#
	Field yrot#
	Field zrot#
	Field norm=0
	;Field bp.bodypoint
	Field newborn=0
	Field Parent
	;Field rb.ODEGeom
	;Field loangle#=-Pi*0.25;-45 degrees
	;Field hiangle#=Pi*0.25;-45 degrees
	;Field Axis1=1
	;Field Axis2=2
	;Field joint_type% = 2
	;Field max_force#=0.0
	;Field vel#=0.0
	;Field angulardamp#=0.5
	;Field lineardamp#=0.5
	;Field fixed=False
	;Field nproxy
	Field anim_flags%,Num_Frames%,FramesPS#



	Field mesh_brush_id 
	

; VrtsChunk
	Field vrts_count%,vrts_flags%,texturecoordsets%,texturecoordsize%
	;fieldrS#[PMK_MAX_VRTS],gS#[PMK_MAX_VRTS],bS#[PMK_MAX_VRTS]
	Field vrts_index%[PMK_MAX_VRTS],x#[PMK_MAX_VRTS],y#[PMK_MAX_VRTS],z#[PMK_MAX_VRTS]
	Field u0#[PMK_MAX_VRTS],v0#[PMK_MAX_VRTS],w0#[PMK_MAX_VRTS];UVW coords
	Field u1#[PMK_MAX_VRTS],v1#[PMK_MAX_VRTS],w1#[PMK_MAX_VRTS];2nd UVW coords
	Field nx#[PMK_MAX_VRTS],ny#[PMK_MAX_VRTS],nz#[PMK_MAX_VRTS]
	Field r#[PMK_MAX_VRTS],g#[PMK_MAX_VRTS],b#[PMK_MAX_VRTS],a#[PMK_MAX_VRTS]
	Field vrts_ent%[PMK_MAX_VRTS],joint1%[PMK_MAX_VRTS],joint2%[PMK_MAX_VRTS],joint3%[PMK_MAX_VRTS],joint4%[PMK_MAX_VRTS]
	Field weight1#[PMK_MAX_VRTS],weight2#[PMK_MAX_VRTS],weight3#[PMK_MAX_VRTS],weight4#[PMK_MAX_VRTS]
	Field Frontfacing[PMK_MAX_VRTS]
;End 






; BoneChunk
	Field weight_count%,vert_target%[PMK_MAX_VRTS],weight#[PMK_MAX_VRTS]
;End 



; KeysChunk
	Field keys_flag%,keys_count%,moved=0
	Field frame%[PMK_MAX_KEYS],frameP%[PMK_MAX_KEYS],frameS%[PMK_MAX_KEYS],frameR%[PMK_MAX_KEYS]
	Field key_px#[PMK_MAX_KEYS],key_py#[PMK_MAX_KEYS],key_pz#[PMK_MAX_KEYS]
	Field key_sx#[PMK_MAX_KEYS],key_sy#[PMK_MAX_KEYS],key_sz#[PMK_MAX_KEYS]
	Field key_rw#[PMK_MAX_KEYS],key_rx#[PMK_MAX_KEYS],key_ry#[PMK_MAX_KEYS],key_rz#[PMK_MAX_KEYS]
	;Field key_qrw#[PMK_MAX_KEYS],key_qrx#[PMK_MAX_KEYS],key_qry#[PMK_MAX_KEYS],key_qrz#[PMK_MAX_KEYS]
	;Field key_brw#[PMK_MAX_KEYS],key_brx#[PMK_MAX_KEYS],key_bry#[PMK_MAX_KEYS],key_brz#[PMK_MAX_KEYS]
	;Field key_pitch#[PMK_MAX_KEYS],key_yaw#[PMK_MAX_KEYS],key_roll#[PMK_MAX_KEYS]
	;Field bone_w#,bone_x#,bone_y#,bone_z#
	
End Type

Type Texs
			Field mesh
			Field name$[PMK_MAX_TEXS]
			Field flags%[PMK_MAX_TEXS]
			Field blend%[PMK_MAX_TEXS]
			Field xpos#[PMK_MAX_TEXS]
			Field ypos#[PMK_MAX_TEXS]
			Field xscale#[PMK_MAX_TEXS]
			Field yscale#[PMK_MAX_TEXS]
			Field rot#[PMK_MAX_TEXS]
			Field texs_count%
End Type


Type Brus
			Field 	name$[PMK_MAX_BRUS]
			Field 	red#[PMK_MAX_BRUS]
			Field	grn#[PMK_MAX_BRUS]
			Field	blu#[PMK_MAX_BRUS]
			Field	alp#[PMK_MAX_BRUS]
			Field 	shi#[PMK_MAX_BRUS]
			Field 	blend%[PMK_MAX_BRUS]
			Field 	fx%[PMK_MAX_BRUS]
		;	Field   tex_id%[PMK_MAX_TEXS]
			Field   brus_count%
			Field   brus_tex_count%
End Type

Type TrisChunk
	Field mesh,tris_count%,tris_brush_id%,tv0%[PMK_MAX_TRIS],tv1%[PMK_MAX_TRIS],tv2%[PMK_MAX_TRIS]
End Type




Global b3dbrush.brus
Global b3dtex.texs
Global b3dnode.NodeChunk
Global b3dmesh.NodeChunk
Global b3dtris.trischunk
;Global b3dseq.sequence
Global Cnode.NodeChunk
Global DoneRefKey=False
Global retval


;Dim leaf(100)
Global NodeCount=0
Global NewJointNo=0
;Dim Joint(100)
Global level









Function b3d2pmk(b3dfile$,dumpflags=0)
	mesh=LoadAnimMesh(b3dfile$)
	Local file


	file=ReadFile( b3dfile$)
	If Not file Return PMK_ERROR_NOFILE
	
	b3dSetFile( file )
	
	If b3dReadChunk$()<>"BB3D"  Return PMK_ERROR_FILENOTVALID ;RuntimeError "Invalid b3d file"
	
	version=b3dReadInt()
	
	If version/100>0 Return  PMK_ERROR_WRONGVERSION ;RuntimeError "Invalid b3d file version"
	
	
	
	
	
	
	
	
	
	DumpChunks(dumpflags,"*")
	
	b3dExitChunk()
	
	
	
	
	CloseFile file


	Return PMK_ERROR_NONE

End Function



Function DumpChunks(dumpflags, tab$="",level%=0)
	tt$=tab$
	tab$=tab$+"  "
	level%=level%+1
			While b3dChunkSize()
		chunk$=b3dReadChunk$()
		
.anim		
		Select chunk$
		Case "SEQS"
			
		;	b3dseq.sequence=New sequence
		;	b3dseq\name$=b3dReadString()
		;	b3dseq\fstart%=b3dReadInt()
		;	b3dseq\fend%=b3dReadInt()
		;	b3dseq\seq%=ExtractAnimSeq (mesh,b3dseq\fstart%,b3dseq\fend%)
		;	b3dseq\gui_id=AddListBoxItem( lstSeq, 0, b3dseq\seq% + " - " + b3dseq\name$)
		Case "PHYS"
		;	b3dnode\joint_type=b3dReadInt()
		;	b3dnode\loangle#=b3dReadFloat() 
		;	b3dnode\hiangle#=b3dReadFloat() 
		;	b3dnode\Axis1=b3dReadInt() 
		;	b3dnode\Axis2=b3dReadInt() 	
			
			
		Case "ANIM"
	
			flags=b3dReadInt()
			n_frames=b3dReadInt()
			fps=b3dReadFloat()			
						
			AnimFrames%=n_frames
		;	DebugLog "Anim Frames :" + AnimFrames
			AnimFPS#=fps
			
			;___________________________________
			
			b3dnode\anim_flags%=flags
			b3dnode\Num_Frames%=n_frames
			b3dnode\FramesPS#=fps
			;_____________________________________
.keys
		Case "KEYS"
				
			;For mark=0 To PMK_MAX_KEYS;b3dmesh\Num_Frames%
			;			b3dnode\frame%[mark]=-1
			;			b3dnode\frameP%[mark]=-1
			;			b3dnode\frameS%[mark]=-1
			;			b3dnode\frameR%[mark]=-1
		;	Next 
			flags=b3dReadInt()

						
			
			
			b3dnode\keys_flag%=flags
			

			sz=4
			If flags And 1 sz=sz+12
			If flags And 2 sz=sz+12
			If flags And 4 sz=sz+16
			n_keys=b3dChunkSize()/sz
			b3dnode\keys_count%=n_keys
									
				
			
			If n_keys*sz=b3dChunkSize() 
			
				While b3dChunkSize()
					frame=b3dReadInt()
						
					
					
					b3dnode\frame%[frame]=frame
					
					
					If flags And 1
						b3dnode\frameP%[frame]=frame
						key_px#=b3dReadFloat()
						key_py#=b3dReadFloat()
						key_pz#=b3dReadFloat()
						
							
						
						b3dnode\key_px#[frame]=key_px#
						b3dnode\key_py#[frame]=key_py#
						b3dnode\key_pz#[frame]=key_pz#			
						
					EndIf
					
					If flags And 2
						b3dnode\frameS%[frame]=frame
						key_sx#=b3dReadFloat()
						key_sy#=b3dReadFloat()
						key_sz#=b3dReadFloat()

						
						
						b3dnode\key_sx#[frame]=key_sx#
						b3dnode\key_sy#[frame]=key_sy#
						b3dnode\key_sz#[frame]=key_sz#

					EndIf
					
					If flags And 4
						b3dnode\frameR%[frame]=frame
						key_rw#=b3dReadFloat()
						key_rx#=b3dReadFloat()
						key_ry#=b3dReadFloat()
						key_rz#=b3dReadFloat()

					
						
						b3dnode\key_rw#[frame]=key_rw#
						b3dnode\key_rx#[frame]=key_rx#
						b3dnode\key_ry#[frame]=key_ry#
						b3dnode\key_rz#[frame]=key_rz#

					EndIf
					
								
				;	If frame  > highframe Then highframe=frame						
					
				Wend
			Else
				;;;DebugLog tab$+"***** Illegal number of keys *****"
			EndIf
		Case "TEXS"
			b3dtex.texs=New texs
			index=0
			While b3dChunkSize()
				name$=b3dReadString$()
				flags=b3dReadInt()
				blend=b3dReadInt()
				x_pos#=b3dReadFloat()
				y_pos#=b3dReadFloat()
				x_scl#=b3dReadFloat()
				y_scl#=b3dReadFloat()
				rot#=b3dReadFloat()



				If dumpflags And PMK_FLAG_StripTexturePaths Then  name$=StripPath(name$)

			
				
				
				
				b3dtex\name$[index]=name$
				b3dtex\flags%[index]=flags
				b3dtex\blend%[index]=blend
				b3dtex\xpos#[index]=x_pos#
				b3dtex\ypos#[index]=y_pos#
				b3dtex\xscale#[index]=x_scl#
				b3dtex\yscale#[index]=y_scl#
				b3dtex\rot#[index]=rot#
			
			index=index+1	
			Wend
			b3dtex\texs_count%=index
.brus
		Case "BRUS"
			b3dbrush.brus=New brus
			n_texs=b3dReadInt()
			b3dbrush\brus_tex_count%=n_texs

							
			
			index=0
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
				
				
				
			b3dbrush\name$[index]=name$
			b3dbrush\red#[index]=red#
			b3dbrush\grn#[index]=grn#
			b3dbrush\blu#[index]=blu#
			b3dbrush\alp#[index]=alp#
			b3dbrush\shi#[index]=shi#
			b3dbrush\blend%[index]=blend
			b3dbrush\fx%[index]=fx
		



				
				If dumpflags And PMK_FLAG_StripBrushPaths Then  name$=StripPath(name$)
				
				
				
					
				For k=0 To n_texs-1
					tex_id=b3dReadInt()
					
				    PMK_tex_id%(index,k)=tex_id
				Next
				
					
			index=index+1
			Wend
			b3dbrush\brus_count%=index



.verts				
		Case "VRTS"
			flags=b3dReadInt()
			tc_sets=b3dReadInt()
			tc_size=b3dReadInt()
			sz=12+tc_sets*tc_size*4
			If flags And 1 Then sz=sz+12
			If flags And 2 Then sz=sz+16
			n_verts=b3dChunkSize()/sz
			
			
				
			
		

			
			
			b3dnode\vrts_count%=n_verts		
			b3dnode\vrts_flags%=flags
			b3dnode\texturecoordsets%=tc_sets
			b3dnode\texturecoordsize%=tc_size
			
			
			
			
		
		
			If n_verts*sz=b3dChunkSize() 
				index%=0
				;read all verts in chunk
				While b3dChunkSize()
					
					
					b3dnode\vrts_index%[index]=0; Used for Selection !
					
									
					x#=b3dReadFloat()
					y#=b3dReadFloat()
					z#=b3dReadFloat()
					
					
					
					b3dnode\x#[index]=x#
					b3dnode\y#[index]=y#
					b3dnode\z#[index]=z#






					
					
					
									
					
					
					
					If flags And 1
						nx#=b3dReadFloat()
						ny#=b3dReadFloat()
						nz#=b3dReadFloat()
						
						
						
						b3dnode\nx#[index]=nx#
						b3dnode\ny#[index]=ny#
						b3dnode\nz#[index]=nz#
					
												
					EndIf
					If flags And 2
						r#=b3dReadFloat()
						g#=b3dReadFloat()
						b#=b3dReadFloat()
						a#=b3dReadFloat()
						
						
						
						
						b3dnode\r#[index]=r#
						b3dnode\g#[index]=g#
						b3dnode\b#[index]=b#
						b3dnode\a#[index]=a#

						
					EndIf
;Parent vert boxes				
				;	b3dnode\vrts_ent[index]=CreateCube()
				;	NameEntity b3dnode\vrts_ent[index],index
				;	EntityColor b3dnode\vrts_ent[index],255,255,255
				;	EntityParent b3dnode\vrts_ent[index],b3dnode\joint
				;	PositionEntity b3dnode\vrts_ent[index],x,y,z,0
				;	ScaleMesh b3dnode\vrts_ent[index],.1,.1,.1
				;	b3dnode\rS#[index]=255
				;	b3dnode\gS#[index]=255
				;	b3dnode\bS#[index]=255
				;	EntityPickMode b3dnode\vrts_ent[index],2
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
							If Not set Then
								b3dnode\u0#[index]=uvw
							Else
								b3dnode\u1#[index]=uvw
							End If


						ElseIf coordnameindex=2
							cn$="v"
							If Not set Then
								b3dnode\v0#[index]=uvw
							Else
								b3dnode\v1#[index]=uvw
							End If



						Else
							cn$="w"
							If Not set Then
								b3dnode\w0#[index]=uvw
							Else
								b3dnode\w1#[index]=uvw
							End If



						EndIf
						
						
						
					Next

						
								
					
					index=index+1

				Wend
			Else
				;;;DebugLog tab$+"***** Illegal number of vertices *****"
			EndIf
		Case "TRIS"
			b3dtris.trischunk=New trischunk
		  	
			brush_id=b3dReadInt()
			sz=12
			n_tris=b3dChunkSize()/sz
			
				
			
			
			b3dtris\tris_count%=n_tris
			b3dtris\tris_brush_id%=brush_id	
			
.tris			
			If n_tris*sz=b3dChunkSize()
			index%=0
				;read all tris in chunk
				While b3dChunkSize()
					v0=b3dReadInt()
					v1=b3dReadInt()
					v2=b3dReadInt()

					
					b3dtris\tv0%[index]=v0
					b3dtris\tv1%[index]=v1
					b3dtris\tv2%[index]=v2
					index%=index%+1
				Wend
			Else
				;;;DebugLog tab$+"***** Illegal number of triangles *****"
			EndIf
		Case "MESH"
			brush_id=b3dReadInt()
			
			
			
			b3dnode\mesh_brush_id=brush_id
			b3dnode\class$="mesh"
			b3dmesh=b3dnode
			;HideEntity b3dmesh\geo				
.bone			
		Case "BONE"
			
		  
			sz=8
			n_weights=b3dChunkSize()/sz
			b3dnode\weight_count%=n_weights
			
			b3dnode\class$="bone"
			index%=0
			If n_weights*sz=b3dChunkSize()
				;read all weights
				While b3dChunkSize()
					vertex_id=b3dReadInt()
					weight#=b3dReadFloat()
					
			;		If weight#>b3dnode\weight#[index]
					b3dnode\vert_target%[vertex_id]=1;vertex_id
					
					b3dnode\weight#[vertex_id]=weight
					
				;	Else
					
				;	n_weights=n_weights-1
				;	End If
					
				;	For tmp.NodeChunk=Each NodeChunk
				;	EntityParent b3dmesh\vrts_ent[vertex_id],b3dnode\joint
				;	DebugLog "Parenting "+ EntityName(b3dnode\vrts_ent[vertex_id])+" to "+ b3dnode\name$
				flag=0					
				If  b3dmesh\joint1[vertex_id]=0 And flag=0 Then
						b3dmesh\joint1[vertex_id]=b3dnode\joint
						b3dmesh\weight1#[vertex_id]=weight
						flag=1
				End If
				
				If  b3dmesh\joint2[vertex_id]=0 And flag=0 Then
						b3dmesh\joint2[vertex_id]=b3dnode\joint
						b3dmesh\weight2#[vertex_id]=weight
						flag=2
				End If
				 
				If  b3dmesh\joint3[vertex_id]=0 And flag=0 Then 
						b3dmesh\joint3[vertex_id]=b3dnode\joint
						b3dmesh\weight3#[vertex_id]=weight
						flag=3
				End If
				
				If  b3dmesh\joint4[vertex_id]=0 And flag=0 Then
						b3dmesh\joint4[vertex_id]=b3dnode\joint
						b3dmesh\weight4#[vertex_id]=weight
						flag=4
				End If


						
					 
				
				
				
					
				
					index%=index%+1
				Wend
			Else
				;;;DebugLog tab$+"***** Illegal number of bone weights *****"
			EndIf
			
			
			
			
.nodes
		Case "NODE"
		
			;If level%>1
			b3dnode.NodeChunk=New NodeChunk
		;	End If
			b3dnode\class$="root"			;
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
	;		If w_rot#<>1.0 Then b3dnode\norm=1
			
			b3dnode\joint=findchildentity(mesh,name$)
		;	DebugLog "creating "+name$ +" "+ b3dnode\joint


			b3dnode\name$=name$
			b3dnode\x_pos#=x_pos#
			b3dnode\y_pos#=y_pos#
			b3dnode\z_pos#=z_pos#
			b3dnode\x_scl#=x_scl#
			b3dnode\y_scl#=y_scl#
			b3dnode\z_scl#=z_scl#
			b3dnode\w_rot#=w_rot#
			b3dnode\x_rot#=x_rot#
			b3dnode\y_rot#=y_rot#
			b3dnode\z_rot#=z_rot#
			
			For mark=0 To PMK_MAX_KEYS;b3dmesh\Num_Frames%
						b3dnode\frame%[mark]=-1
						b3dnode\frameP%[mark]=-1
						b3dnode\frameS%[mark]=-1
						b3dnode\frameR%[mark]=-1
			Next
			;b3dnode\loangle#=-45.0;-45;-Pi*0.25;-45 degrees
			;b3dnode\hiangle#=45.0;Pi*0.25;-45 degrees
			;b3dnode\Axis1=1
			;b3dnode\Axis2=2
			;b3dnode\joint_type% = 2
			;b3dnode\max_force#=0.0
			;b3dnode\vel#=0.0
			;b3dnode\angulardamp#=0.5
			;b3dnode\lineardamp#=0.5
			;b3dnode\nproxy=CreatePivot()
		;	DebugLog  b3dnode.NodeChunk\name$

	If b3dnode\joint=0 Then
		;b3dnode\gui_id=AddTreeViewNode( treeJoints, name$ );
	;	DebugLog "adding node  :"+b3dnode\name$ +"  parent="+b3dnode\joint+"   mesh="+mesh

	Else If GetParent(b3dnode\joint)=mesh Then
			
			;b3dnode\gui_id=AddTreeViewNode( treeJoints, name$ )
			b3dnode\parent=mesh
	Else
		
		For tmp.Nodechunk=Each NodeChunk
	;	DebugLog "looking for.."+tmp\joint+"  parent   :"+GetParent (b3dnode\joint)
			If tmp\joint=GetParent (b3dnode\joint) Then
				;b3dnode\gui_id=AddTreeViewNode( tmp\gui_id, name$ )
			;	DebugLog "adding child node  :"+b3dnode\name$+"  to  "+ tmp\name$
				;dad.nodechunk=getnodechunk(GetParent (b3dnode\joint))
				;b3dnode\bone=createRay.ray(EntityX#(dad\joint,1),EntityY#(dad\joint,1),EntityZ#(dad\joint,1),EntityX#(b3dnode\joint,1),EntityY#(b3dnode\joint,1),EntityZ#(b3dnode\joint,1), punit#*0.1,dad\joint, 1)
				;EntityOrder b3dnode\bone\entity,-1
				;UpdateRay(b3dnode.nodechunk)
			Else 
				
			;b3dnode\gui_id=AddTreeViewNode( treeJoints, name$ )
			End If
		Next
				
	End If
	
	;b3dnode\geo=CreateSphere()
	hnd.NodeChunk=b3dnode.NodeChunk
	;NameEntity b3dnode\geo,"Sphere: " + name$
	;EntityPickMode b3dnode\geo,2
	;EntityAlpha b3dnode\geo,0.6
	;EntityOrder b3dnode\geo,-3
	;EntityColor b3dnode\geo,0,0,255
	;EntityParent b3dnode\geo,b3dnode\joint,0
	;EntityOrder b3dnode\geo,-1 - make optional 
	;HideEntity b3dnode\geo
	;If b3dnode\ent>0 DebugLog EntityName(b3dnode\geo) + " : " + EntityName(b3dnode\ent)
	;joint(NodeCount)=CreateSphere();
	;ScaleEntity b3dnode\geo,.1,.1,.1;
	;EntityParent joint(NodeCount),findchildentity(mesh,name$),0;
	;NodeCount=NodeCount+1;
	;NewJointNo=NewJointNo+1
	;PositionEntity ball,x_pos#,y_pos#,z_pos# ,True

			If dumpflags And PMK_FLAG_StripNodePaths Then  name$=StripPath(name$)	
			
						
			
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

		DumpChunks(dumpflags, tab$,level%)	;dump any subchunks
		b3dExitChunk()					;exit this chunk
	
	
	   	
		
	Wend
End Function


Function StripPath$(name$)
	Local pos

	For pos=Len(name$) To 1 Step -1
		If Mid$(name$,pos,1)="\" Or Mid$(name$,pos,1)="/" Then Exit
	Next
	Return Right$(name$,Len(name$)-pos)


End Function

;### name   : 1-call recursive child search ###
;### by     : jonathan pittock (skn3)       ###
;### contact: skn3@acsv.net                 ###
;### www    : www.acsv.net                  ###

;This value is used to size the buffer bank below. If the data needs more space,
;it will resize the bank in blocks of the amount below. (in bytes, 1k = 1024 bytes)
Const recursive_resize=1024

;This bank is used in each call to the search function. It is outside the function,
;as creating and deleting over and over from memory, can cause fragmentation, not...
;to mention slow downs.
Global recursive_bank=CreateBank(recursive_resize),recursive_size=recursive_resize

;These are misc values, having them defined as global speeds up the function as...
;they don't need to be created/destroyed each time the function is called
Global recursive_entity,recursive_parent,recursive_id,recursive_start,recursive_total,recursive_offset

;The function
;It will return the entity if found, or 0 if not.
;MyChild=findchildentity(entity,"child name")
Function findchildentity(entity,name$)
	name$=Lower$(name$)
	recursive_parent=entity
	recursive_start=1
	recursive_offset=0
	.recursive_label
		recursive_total=CountChildren(recursive_parent)
		For recursive_id=recursive_start To recursive_total
			recursive_entity=GetChild(recursive_parent,recursive_id)
			If name$=Lower$(EntityName$(recursive_entity))
				Return recursive_entity
			Else
				If recursive_offset+8 > recursive_size-1
					ResizeBank(recursive_bank,recursive_size+recursive_resize)
					recursive_size=recursive_size+recursive_resize
				End If
				PokeInt(recursive_bank,recursive_offset,recursive_id+1)
				PokeInt(recursive_bank,recursive_offset+4,recursive_parent)
				recursive_offset=recursive_offset+8
				recursive_start=1
				recursive_parent=recursive_entity
				Goto recursive_label
			End If
		Next
		If recursive_offset=0
			Return 0
		Else
			recursive_start=PeekInt(recursive_bank,recursive_offset-8)
			recursive_parent=PeekInt(recursive_bank,recursive_offset-4)
			recursive_offset=recursive_offset-8
			Goto recursive_label
		End If
End Function



;;;;;;;;;;;;;;;;;;;EXPORT FUNCTIONS:::::::::::::::::::::::::::::::::

Function pmk2b3d( f_name$,mesh )

	file=WriteFile( f_name$ )

	b3dSetFile( file )
	
	b3dBeginChunk( "BB3D" )
	DebugLog "Begin BB3d"
		b3dWriteInt( 1 )	;version
		If b3dtex.texs<>Null  Then
		b3dBeginChunk("TEXS")
		DebugLog "Begin Chunk Texs"
			For x=0 To b3dtex\texs_count%-1
				b3dWriteString b3dtex\name$[x]
				b3dWriteInt b3dtex\flags%[x]
				b3dWriteInt b3dtex\blend%[x]
				b3dWritefloat b3dtex\xpos#[x]
				b3dWritefloat b3dtex\ypos#[x]
				b3dWritefloat b3dtex\xscale#[x]
				b3dWritefloat b3dtex\yscale#[x]
				b3dWritefloat b3dtex\rot#[x]
				
			Next
			
		b3dEndChunk();TEXS
		DebugLog "End Chunk Texs"
		End If								
.brus			
		If b3dbrush.brus <> Null Then
		For tmpbrush.brus=Each brus

		b3dBeginChunk( "BRUS" )
		DebugLog "Begin Chunk Brus"
				b3dWriteInt tmpbrush\brus_tex_count%

			For x=0 To tmpbrush\brus_count%-1
																		;0 textures per brush
				b3dWriteString  tmpbrush\name$[x]		;name
				b3dWriteFloat   tmpbrush\red#[x]					;red
				b3dWriteFloat   tmpbrush\grn#[x] 					;green
				b3dWriteFloat   tmpbrush\blu#[x] 					;blue
				b3dWriteFloat   tmpbrush\alp#[x] 					;alpha
				b3dWriteFloat   tmpbrush\shi#[x] 				;shininess
				b3dWriteInt     tmpbrush\blend%[x] 					;blend
				b3dWriteInt     tmpbrush\fx%[x] 
				
				For k=0 To tmpbrush\brus_tex_count%-1
					b3dWriteInt PMK_tex_id% (x,k)
				Next
			;b3dWriteInt x

			Next	
									
		
		b3dEndChunk()	;end of BRUS chunk
		DebugLog "End Chunk Brus"
		Next
		End If
		DebugLog "Begin NODE Chunk"
			b3dBeginChunk("NODE")
				b3dWriteString b3dmesh\name$
				b3dWritefloat b3dmesh\x_pos#
				b3dWritefloat b3dmesh\y_pos#
				b3dWritefloat b3dmesh\z_pos#
				b3dWritefloat b3dmesh\x_scl#
				b3dWritefloat b3dmesh\y_scl#
				b3dWritefloat b3dmesh\z_scl#
				b3dWritefloat b3dmesh\w_rot#
				b3dWritefloat b3dmesh\x_rot#
				b3dWritefloat b3dmesh\y_rot#
				b3dWritefloat b3dmesh\z_rot#
				
			
				b3dBeginChunk("MESH")
				DebugLog tab$+"Begin Chunk Mesh"
				b3dWriteInt b3dmesh\mesh_brush_id
				
				b3dBeginChunk("VRTS")
				DebugLog tab$+"Begin Chunk Vrts"
				
							b3dWriteInt b3dmesh\vrts_flags%
							b3dWriteInt b3dmesh\texturecoordsets%
							b3dWriteInt b3dmesh\texturecoordsize%
												
														
			For x=0 To b3dmesh\vrts_count%-1
							
					
								b3dWritefloat b3dmesh\x#[x]
								b3dWritefloat b3dmesh\y#[x]
								b3dWritefloat b3dmesh\z#[x]
			
									
						
						If b3dmesh\vrts_flags% And 1
								b3dWritefloat b3dmesh\nx#[x]
								b3dWritefloat b3dmesh\ny#[x]
								b3dWritefloat b3dmesh\nz#[x]						
										
						EndIf
						If b3dmesh\vrts_flags% And 2
								b3dWritefloat b3dmesh\r#[x]
								b3dWritefloat b3dmesh\g#[x]
								b3dWritefloat b3dmesh\b#[x]
								b3dWritefloat b3dmesh\a#[x]
						EndIf


					If b3dmesh\texturecoordsets%=1 Then
				
				
						If b3dmesh\texturecoordsize%>0 Then b3dWritefloat b3dmesh\u0#[x]
						If b3dmesh\texturecoordsize%>1 Then b3dWritefloat b3dmesh\v0#[x]				
						If b3dmesh\texturecoordsize%>2 Then b3dWritefloat b3dmesh\w0#[x]
						
					End If
					
					If b3dmesh\texturecoordsets%=2 Then
				
						If b3dmesh\texturecoordsize%>0 Then b3dWritefloat b3dmesh\u0#[x]
						If b3dmesh\texturecoordsize%>1 Then b3dWritefloat b3dmesh\v0#[x]				
						If b3dmesh\texturecoordsize%>2 Then b3dWritefloat b3dmesh\w0#[x]

						If b3dmesh\texturecoordsize%>0 Then b3dWritefloat b3dmesh\u1#[x]
						If b3dmesh\texturecoordsize%>1 Then b3dWritefloat b3dmesh\v1#[x]				
						If b3dmesh\texturecoordsize%>2 Then b3dWritefloat b3dmesh\w1#[x]
						
					End If
						
				



			Next
			b3dEndChunk();VRTS
			DebugLog tab$+"End Chunk Vrts"
			For b3dtris.trischunk=Each trischunk
			
			b3dBeginChunk("TRIS")
			DebugLog tab$+"Begin Chunk Tris"
			
				b3dWriteInt b3dtris\tris_brush_id%
				
				For x = 0 To b3dtris\tris_count%-1
				
						b3dWriteInt b3dtris\tv0%[x]
						b3dWriteInt b3dtris\tv1%[x]
						b3dWriteInt b3dtris\tv2%[x]
				Next
		
			b3dEndChunk();TRIS
			
			Next
		
			DebugLog tab$+"End Chunk Tris"
			
			

		
		
			
			b3dEndChunk();MESH
			DebugLog tab$+"End Chunk Mesh"
			
			b3dBeginChunk("ANIM")
			DebugLog tab$+"Begin Chunk Anim"
			b3dWriteInt b3dmesh\anim_flags%
			b3dWriteInt b3dmesh\Num_Frames%
			b3dWriteFloat b3dmesh\FramesPS#
			b3dEndChunk();ANIM
			DebugLog tab$+"End Chunk Anim"
			
			If CountChildren(mesh)>0 Then	
				WriteNodes(mesh); write joint hierarchy
			
			End If
			
		

			
			DebugLog "End Chunk ROOT"	
			b3dEndChunk();End B3D Chunk
			
			
			CloseFile  file
			Return PMK_ERROR_NONE
			
End Function

Function WriteNodes(parent,tab$="")
		tab$=tab$+"	 "
		WriteNode (parent,tab$)
		If CountChildren(parent)>0 Then
		For a=1 To CountChildren(parent)
		 	child=GetChild(parent,a)
			fin=WriteNodes(child,tab$)
			
			
		Next
		End If
		b3dEndChunk(); end NODE chunk
		DebugLog tab$+"End Chunk NOde ***********"+ EntityName(parent)
			
		
			
	
	;If parent=mesh Then	b3dEndChunk(); end NODE chunk
	;DebugLog tab$+"End Chunk NOde"+ currname$
	

	
End Function







Function WriteNode(child,tab$)
	For tmp.NodeChunk=Each NodeChunk
		If tmp\joint=child And tmp\class$="bone" Then
				currname$=tmp\name$

				b3dBeginChunk("NODE")
				DebugLog tab$+"Begin Chunk Node  "+currname$
				b3dWriteString tmp\name$
				b3dWritefloat tmp\x_pos#
				b3dWritefloat tmp\y_pos#
				b3dWritefloat tmp\z_pos#
				b3dWritefloat tmp\x_scl#
				b3dWritefloat tmp\y_scl#
				b3dWritefloat tmp\z_scl#
				b3dWritefloat tmp\w_rot#
				b3dWritefloat tmp\x_rot#
				b3dWritefloat tmp\y_rot#
				b3dWritefloat tmp\z_rot#



					



			If tmp\class$="bone" Then
				b3dBeginChunk("BONE")
				DebugLog tab$+"Begin Chunk Bone"
			
				;If tmp\weight_count%>0
					For x=0 To b3dmesh\vrts_count%-1
						If tmp\vert_target[x]=1
						b3dWriteInt x
						b3dWriteFloat tmp\weight#[x]
					;	DebugLog tab$+"VERT "+tmp\vert_target%[x]+"  WEIGHT "+tmp\weight#[x]
						End If
					Next
				;End If
			b3dEndChunk(); end BONE Chunk
			DebugLog tab$+"End Chunk Bone"



			If tmp\keys_count%>0 Then 
				b3dBeginChunk("KEYS")
				DebugLog tab$+"Begin Chunk Keys"
				
				b3dWriteInt (1);Compressed for now....Pos and Rot only - get flag value at import
				
				For x=0 To PMK_MAX_KEYS-1;animframes;b3dmesh\Num_Frames%
					If tmp\frameP%[x]>-1 Then
					b3dWriteInt x
				
				;	If tmp\keys_flag% And 1
			
					b3dWritefloat tmp\key_px#[x]
					b3dWritefloat tmp\key_py#[x]
					b3dWritefloat tmp\key_pz#[x]
				
				;	EndIf
			
				;	If tmp\keys_flag% And 2
			
				;	b3dWritefloat (1.0);tmp\key_sx#[x]
				;	b3dWritefloat (1.0);tmp\key_sy#[x]
				;	b3dWritefloat (1.0);tmp\key_sz#[x]
			
				;	End If
			
				;	If tmp\keys_flag% And 4
				
				;	b3dWritefloat tmp\key_rw#[x]
				;	b3dWritefloat tmp\key_rx#[x]
				;	b3dWritefloat tmp\key_ry#[x]
				;	b3dWritefloat tmp\key_rz#[x]
					End If
				Next
			
			b3dEndChunk(); end KEYS chunk
			
			;b3dBeginChunk("KEYS")
			;	DebugLog tab$+"Begin Chunk Keys"
				
			;	b3dWriteInt (2);Compressed for now....Pos and Rot only - get flag value at import
				
			;	For x=0 To PMK_MAX_KEYS-1;b3dmesh\Num_Frames%
			;		If tmp\frameS%[x]>-1 Then
			;		b3dWriteInt x
				
				;	If tmp\keys_flag% And 1
			
				;	b3dWritefloat tmp\key_px#[x]
				;	b3dWritefloat tmp\key_py#[x]
				;	b3dWritefloat tmp\key_pz#[x]
				
				;	EndIf
			
				;	If tmp\keys_flag% And 2
			
			;		b3dWritefloat tmp\key_sx#[x]
			;		b3dWritefloat tmp\key_sy#[x]
			;		b3dWritefloat tmp\key_sz#[x]
			
				;	End If
			
				;	If tmp\keys_flag% And 4
				
				;	b3dWritefloat tmp\key_rw#[x]
				;	b3dWritefloat tmp\key_rx#[x]
				;	b3dWritefloat tmp\key_ry#[x]
				;	b3dWritefloat tmp\key_rz#[x]
			;		End If
			;	Next
			
		;	b3dEndChunk(); end KEYS chunk
			
			b3dBeginChunk("KEYS")
				DebugLog tab$+"Begin Chunk Keys"
				
				b3dWriteInt (4);Compressed for now....Pos and Rot only - get flag value at import
				
				For x=0 To PMK_MAX_KEYS-1;b3dmesh\Num_Frames%
					If tmp\frameR%[x]>-1 Then
					b3dWriteInt x
				
				;	If tmp\keys_flag% And 1
			
				;	b3dWritefloat tmp\key_px#[x]
				;	b3dWritefloat tmp\key_py#[x]
				;	b3dWritefloat tmp\key_pz#[x]
				
				;	EndIf
			
				;	If tmp\keys_flag% And 2
			
				;	b3dWritefloat (1.0);tmp\key_sx#[x]
				;	b3dWritefloat (1.0);tmp\key_sy#[x]
				;	b3dWritefloat (1.0);tmp\key_sz#[x]
			
				;	End If
			
				;	If tmp\keys_flag% And 4
				
					b3dWritefloat tmp\key_rw#[x]
					b3dWritefloat tmp\key_rx#[x]
					b3dWritefloat tmp\key_ry#[x]
					b3dWritefloat tmp\key_rz#[x]
					End If
				Next
			
			b3dEndChunk(); end KEYS chunk
			;PMK_write_physics_nodes(tmp.nodechunk)
			DebugLog tab$+"End Chunk Keys"
			;b3dEndChunk();End Node
			Return True
			End If
	End If
	
End If			
			
Next
Return False
End Function



;:::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::
;;;;;;;;;;;;;TEST;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;First Load and Parse File: You will end up with a  NodeChunk type for each node in the model.
; There will be a global NodeChunk type for the Mesh node - B3dmesh. This type contains the UV coords in
; B3dmesh\u0#[vert index],B3dmesh\v0#[vert index],B3dmesh\w0#[vert index] for set 1 and
; B3dmesh\u1#[vert index],B3dmesh\v1#[vert index],B3dmesh\w1#[vert index] for set 2 if exists
; Set 2 is normally used for lightmapping and so will not normally be found on animated models.
Graphics3D 800,600, 16,2
file1$="oldfile.b3d"
file2$="newfile.b3d"
retval=b3d2pmk(file1$);Load and Parse Saving all info to types
If retval=PMK_ERROR_NONE
	Repeat  
		Text 10,10,"OK - Do whatever you want here ! .
		Text 10,20,"Hit Enter to save
		Flip
	Until KeyHit(28)
	retval=pmk2b3d(file2$,mesh);Save animated model using values held in types
Else
	RuntimeError "Error loading file
End If
FreeEntity mesh 
FreeBank recursive_bank
