; ID: 1145
; Author: EdzUp[GD]
; Date: 2004-08-23 12:17:29
; Title: Apply bump/normal map
; Description: Apply bump/normal map to any surface

Function ApplyBumpMap( Mesh, SearchTexture$, BumpMap, Frame=0, LightMapped=1 )
	;this function changes the texture on a mesh so you can apply a bump/normal map
	Local SurfS=CountSurfaces( Mesh ) 
	Local SurfI=0
	Local BlankBrush = CreateTexture( 64, 64 )
	Local t0, t1, t2, t3
	
	SetBuffer TextureBuffer( BlankBrush )
	ClsColor 255, 255, 255
	Cls
	ClsColor 0, 0, 0
	SetBuffer BackBuffer()
	
	For SurfI=1 To Surfs
		s=GetSurface( Mesh, SurfI ) 
		b=GetSurfaceBrush(s) 
		t0=GetBrushTexture( b, 0 ) 
		t1=GetBrushTexture( b, 1 )
		t2=GetBrushTexture( b, 2 )
		
		If Upper$( StripPath$( TextureName$( t0 ) ) ) = Upper$( SearchTexture$ )
			BrushTexture b, BumpMap, Frame, 0
			BrushTexture b, t0, Frame, 2
			
			;lightmap is always on channel (index) 1
			If LightMapped=0
				BrushTexture b, BlankBrush, 0, 1
			Else
				BrushTexture b, t1, 0, 1
			EndIf
			
			PaintSurface s, b
		EndIf 
		
		If t0<>0
			FreeTexture t0
		EndIf
	Next 
	
	FreeTexture BlankBrush
End Function

Function StripPath$(file$) 
	;borrowed from Blitz Help file
	If Len(file$)>0 
		For i=Len(file$) To 1 Step -1 
			mi$=Mid$(file$,i,1) 
			If mi$="\" Or mi$="/" Then Return name$ Else name$=mi$+name$ 
		Next 
	EndIf 

	Return name$ 
End Function
