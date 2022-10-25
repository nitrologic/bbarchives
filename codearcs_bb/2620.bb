; ID: 2620
; Author: Krischan
; Date: 2009-12-01 08:10:21
; Title: Texture Splatting on Meshterrain
; Description: Creates transparent mesh Layers with different textures

Type triangle
	
	Field v%[2]
	Field alpha#
	
End Type

Global alphavertex#[65536]


; create Meshterrain
Function LoadMeshTerrain(hmap%,vscale#=16.0,cmap%,cscale#=1.0,amap%,r1%=False,g1%=False,b1%=False,base%=False)
	
	Local rgb%,r%,g%,b%,a#
	Local h#,x%,y%,vertex%
	Local vx#,vz#,u#,v#
	Local v0%,v1%,v2%,v3%
	
	; Heightmap
	If hmap Then
		Local hbuf%=ImageBuffer(hmap)
		Local size%=ImageWidth(hmap)-1
		Local verts%=size+1
	EndIf
	
	; Colormap
	If cmap Then 
		Local cbuf%=ImageBuffer(cmap)
		Local cwidth%=ImageWidth(cmap)
		Local cfactor#=cwidth*1.0/verts
	EndIf
	
	; Alphamap
	If amap Then Local abuf%=ImageBuffer(amap)
	
	; create the Mesh
	Local mesh%=CreateMesh()
	Local surf%=CreateSurface(mesh)
	
	; lock buffers
	LockBuffer hbuf
	If cmap Then LockBuffer cbuf
	If amap Then LockBuffer abuf
	
	For y=0 To size
		
		For x=0 To size
			
			; read height (only red channel) and normalize it
			h=Normalize((ReadPixelFast(x,size-y,hbuf) And $ff0000)/$10000,0,255,0.0,vscale)
			
			; read vertexcolor from colormap (R,G,B)
			If cmap Then
				rgb=ReadPixelFast(x*cfactor,(size-y)*cfactor,cbuf)
				r=Int(((rgb And $ff0000)/$10000)*cscale)
				g=Int(((rgb And $ff00)/$100)*cscale)
				b=Int((rgb And $ff)*cscale)
			Else
				r=128
				g=128
				b=128
			EndIf
			
			; read alpha value from alphamap (only red channel)
			If amap Then
				a=Normalize((ReadPixelFast(x,size-y,abuf) And $ff0000)/$10000,0,255,0,1)
			Else
				a=1.0
			EndIf
			
			; use forced colors if used
			If r1 Then r=r1
			If g1 Then g=g1
			If b1 Then b=b1
			
			; calculate vertex coordinates / texture coordinates
			vx=x-(size/2.0)
			vz=y-(size/2.0)
			u=x*1.0/size
			v=(size-y)*1.0/size
			
			; place vertex
			vertex=AddVertex(surf,vx,h,vz,u,v)
			
			; set vertex color and texture coordinates
			VertexColor surf,vertex,r,g,b,a
			
			; build alpha blitzarray for alpha check later
			alphavertex[vertex]=a
			
			; set triangles
			If y<size And x<size Then
				
				v0=x+((size+1)*y)
				v1=x+((size+1)*y)+(size+1)
				v2=(x+1)+((size+1)*y)
				v3=(x+1)+((size+1)*y)+(size+1)
				
				; add first triangle
				t.triangle = New triangle
				t\v[0]=v0
				t\v[1]=v1
				t\v[2]=v2
				
				; add second triangle
				t.triangle = New triangle
				t\v[0]=v2
				t\v[1]=v1
				t\v[2]=v3
				
			EndIf
			
		Next
		
	Next
	
	; flag all 100% transparent triangles (just add transparency values, higher than 0 = not transparent :-)
	For t.triangle = Each triangle
		
		t\alpha=alphavertex[t\v[0]]+alphavertex[t\v[1]]+alphavertex[t\v[2]]
		
	Next
	
	; create triangles
	For t.triangle = Each triangle
		
		; base layer = draw ALL triangles
		If base=True Then
			
			AddTriangle surf,t\v[0],t\v[1],t\v[2]
			
		Else
			
			; if they are not transparent
			If t\alpha>0 Then
				
				AddTriangle surf,t\v[0],t\v[1],t\v[2]
				
			EndIf
			
		EndIf
		
		; delete triangle from to do list
		Delete t.triangle
		
	Next
	
	; unlock buffers
	If amap Then UnlockBuffer abuf
	If cmap Then UnlockBuffer cbuf
	UnlockBuffer hbuf
	
	UpdateNormals mesh
	
	Return mesh
	
End Function


; Normalize a value
Function Normalize#(value#=128.0,value_min#=0.0,value_max#=255.0,norm_min#=0.0,norm_max#=1.0)
	
	Return ((value-value_min)/(value_max-value_min))*(norm_max-norm_min)+norm_min
	
End Function
