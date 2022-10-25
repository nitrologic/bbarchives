; ID: 1868
; Author: Nebula
; Date: 2006-11-23 23:18:55
; Title: Box Unbox mesh functions
; Description: Box with side on/off switches (optimalization)

;
;
; Mesh creation functions
;
;
;


; top,bottom,left,right,front,back
Function makecube(a=True,b=True,c=True,d=True,e=True,f=True)
	z=CreateMesh() 

	surf=CreateSurface(z) 

	v0 = AddVertex(surf,0,0,0,	0,1)
	v3 = AddVertex(surf,4,0,0,	1,1)	
	v4 = AddVertex(surf,0,4,0,	0,0)
	v7 = AddVertex(surf,4,4,0,	1,0)

	v1 = AddVertex(surf,0,0,4	,0,1)
	v2 = AddVertex(surf,4,0,4	,1,1)	
	v5 = AddVertex(surf,0,4,4	,0,0)
	v6 = AddVertex(surf,4,4,4	,1,0)

	v8  = AddVertex(surf,0,0,0	,0,1)
	v9  = AddVertex(surf,0,0,4	,1,1)	
	v10 = AddVertex(surf,0,4,0	,0,0)
	v11 = AddVertex(surf,0,4,4	,1,0)

	v12 = AddVertex(surf,4,0,0  ,0,1)
	v13 = AddVertex(surf,4,0,4  ,1,1)	
	v14 = AddVertex(surf,4,4,0	,0,0)
	v15 = AddVertex(surf,4,4,4	,1,0)

	v16 = AddVertex(surf,0,4,0  ,0,1)
	v17 = AddVertex(surf,0,4,4	,1,1)	
	v18 = AddVertex(surf,4,4,0	,0,0)
	v19 = AddVertex(surf,4,4,4	,1,0)

	v20 = AddVertex(surf,0,0,0  ,0,1)
	v21 = AddVertex(surf,0,0,4	,1,1)	
	v22 = AddVertex(surf,4,0,0	,0,0)
	v23 = AddVertex(surf,4,0,4	,1,0)

	If a = True
		AddTriangle(Surf,v16,v17,v18) ;top
		AddTriangle(surf,v18,v17,v19)
	End If
	If b = True
		AddTriangle(surf,v21,v20,v22) ; bottom
		AddTriangle(surf,v21,v22,v23)
	End If
	If f = True Then
		AddTriangle(surf,v5,v1,v2) ; back
		AddTriangle(surf,v5,v2,v6)
	End If
	If e = True Then
		AddTriangle(surf,v0,v4,v3) ; front
		AddTriangle(surf,v3,v4,v7) ; 
	End If
	If d = True
		AddTriangle(surf,v15,v13,v12) ; left
		AddTriangle(surf,v15,v12,v14)
	End If
	If c = True
		AddTriangle(surf,v9,v11,v8) ; right
		AddTriangle(surf,v8,v11,v10)
	End If
	Return z
End Function

; top,bottom,left,right,front,back
Function make3dfrontdown(a=True,b=True,c=True,d=True,e=True)
	
	;    /|
	;  /  |
	;/____| 

	z=CreateMesh() 

	surf=CreateSurface(z) 

;	v16 = AddVertex(surf,0,4,0  ,0,1) ; top
;	v17 = AddVertex(surf,0,4,4	,1,1)	
;	v18 = AddVertex(surf,4,0,0	,0,0)
;	v19 = AddVertex(surf,4,0,4	,1,0)

	v20 = AddVertex(surf,0,0,0  ,0,1) ; bottom
	v21 = AddVertex(surf,0,0,4	,1,1)	
	v22 = AddVertex(surf,4,0,0	,0,0)
	v23 = AddVertex(surf,4,0,4	,1,0)

	v1 = AddVertex(surf,0,0,4	,0,1) ; back
	v2 = AddVertex(surf,4,0,4	,1,1)	
	v5 = AddVertex(surf,0,4,0	,0,0)
	v6 = AddVertex(surf,4,4,0	,1,0)
		
	v0 = AddVertex(surf,0,0,0,	0,1) ; front
	v3 = AddVertex(surf,4,0,0,	1,1)	
	v4 = AddVertex(surf,0,4,0,	0,0)
	v7 = AddVertex(surf,4,4,0,	1,0)
	
;	v12 = AddVertex(surf,4,0,0  ,0,1) ; right
;	v13 = AddVertex(surf,4,0,4  ,1,1)	
;	v14 = AddVertex(surf,4,4,0	,0,0)
;	v15 = AddVertex(surf,4,4,4	,1,0)

	v12 = AddVertex(surf,4,0,0  ,0,1) ; right
	v13 = AddVertex(surf,4,0,4  ,1,1)	
	v14 = AddVertex(surf,4,4,0	,0,0)
	v15 = AddVertex(surf,4,4,0	,1,0)



	v8  = AddVertex(surf,0,0,0	,0,1) ; left
	v9  = AddVertex(surf,0,0,4	,1,1)	
	v10 = AddVertex(surf,4,4,0	,0,0)
	v11 = AddVertex(surf,4,4,4	,1,0)


If a = True
		AddTriangle(surf,v21,v20,v22) ; bottom
		AddTriangle(surf,v21,v22,v23)
End If
If e = True Then
		AddTriangle(surf,v6,v1,v2) ; back
		AddTriangle surf,v5,v1,v6
End If
If d = True Then ; front
		AddTriangle surf,v3,v0,v6
		AddTriangle surf,v6,v0,v5
	End If
If c = True Then
		AddTriangle(surf,v15,v13,v12) ; right
		;AddTriangle(surf,v15,v12,v14)
	End If
If b = True Then
		AddTriangle(surf,v5,v8,v9) ; left
	End If
	Return z

End Function




; top,bottom,left,right,front,back
Function make3dfrontup(a=True,b=True,c=True,d=True,e=True)
	
	;    /|
	;  /  |
	;/____| 

	z=CreateMesh() 

	surf=CreateSurface(z) 

;	v16 = AddVertex(surf,0,4,0  ,0,1) ; top
;	v17 = AddVertex(surf,0,4,4	,1,1)	
;	v18 = AddVertex(surf,4,0,0	,0,0)
;	v19 = AddVertex(surf,4,0,4	,1,0)

	v20 = AddVertex(surf,0,0,0  ,0,1) ; bottom
	v21 = AddVertex(surf,0,0,4	,1,1)	
	v22 = AddVertex(surf,4,0,0	,0,0)
	v23 = AddVertex(surf,4,0,4	,1,0)

	v1 = AddVertex(surf,0,0,4	,0,1) ; back
	v2 = AddVertex(surf,4,0,4	,1,1)	
	v5 = AddVertex(surf,0,4,4	,0,0)
	v6 = AddVertex(surf,4,4,4	,1,0)
		
	v0 = AddVertex(surf,0,0,0,	0,1) ; front
	v3 = AddVertex(surf,4,0,0,	1,1)	
	v4 = AddVertex(surf,0,4,0,	0,0)
	v7 = AddVertex(surf,4,4,0,	1,0)
	
	v12 = AddVertex(surf,4,0,0  ,0,1) ; right
	v13 = AddVertex(surf,4,0,4  ,1,1)	
	v14 = AddVertex(surf,4,4,0	,0,0)
	v15 = AddVertex(surf,4,4,4	,1,0)

	v8  = AddVertex(surf,0,0,0	,0,1) ; left
	v9  = AddVertex(surf,0,0,4	,1,1)	
	v10 = AddVertex(surf,4,4,0	,0,0)
	v11 = AddVertex(surf,4,4,4	,1,0)


If a = True
		AddTriangle(surf,v21,v20,v22) ; bottom
		AddTriangle(surf,v21,v22,v23)
End If
If e = True Then
		AddTriangle(surf,v6,v1,v2) ; back
		AddTriangle surf,v5,v1,v6
End If
If d = True Then ; front
		AddTriangle surf,v3,v0,v6
		AddTriangle surf,v6,v0,v5
	End If

If c = True Then
		AddTriangle(surf,v15,v13,v12) ; right
		;AddTriangle(surf,v15,v12,v14)
	End If
	
If b = True Then
		AddTriangle(surf,v5,v8,v9) ; left
	End If
	Return z

End Function



; top,bottom,left,right,front,back
Function make3leftup(a=True,b=True,c=True,d=True,e=True)
	
	;    /|
	;  /  |
	;/____| 

	z=CreateMesh() 

	surf=CreateSurface(z) 

;	v16 = AddVertex(surf,0,4,0  ,0,1) ; top
;	v17 = AddVertex(surf,0,4,4	,1,1)	
;	v18 = AddVertex(surf,4,0,0	,0,0)
;	v19 = AddVertex(surf,4,0,4	,1,0)

	v20 = AddVertex(surf,0,0,0  ,0,1) ; bottom
	v21 = AddVertex(surf,0,0,4	,1,1)	
	v22 = AddVertex(surf,4,0,0	,0,0)
	v23 = AddVertex(surf,4,0,4	,1,0)

	v1 = AddVertex(surf,0,0,4	,0,1) ; back
	v2 = AddVertex(surf,4,0,4	,1,1)	
	v5 = AddVertex(surf,0,4,4	,0,0)
	v6 = AddVertex(surf,4,4,4	,1,0)
		
	v0 = AddVertex(surf,0,0,0,	0,1) ; front
	v3 = AddVertex(surf,4,0,0,	1,1)	
	v4 = AddVertex(surf,0,4,0,	0,0)
	v7 = AddVertex(surf,4,4,0,	1,0)
	
	v12 = AddVertex(surf,4,0,0  ,0,1) ; right
	v13 = AddVertex(surf,4,0,4  ,1,1)	
	v14 = AddVertex(surf,4,4,0	,0,0)
	v15 = AddVertex(surf,4,4,4	,1,0)

	v8  = AddVertex(surf,0,0,0	,0,1) ; left
	v9  = AddVertex(surf,0,0,4	,1,1)	
	v10 = AddVertex(surf,4,4,0	,0,0)
	v11 = AddVertex(surf,4,4,4	,1,0)


If a = True
		AddTriangle(surf,v21,v20,v22) ; bottom
		AddTriangle(surf,v21,v22,v23)
End If
If e = True Then
		AddTriangle(surf,v6,v1,v2) ; back
End If
If d = True Then ; front
		AddTriangle surf,v3,v0,v7
	End If
If c = True Then
		AddTriangle(surf,v15,v13,v12) ; right
		AddTriangle(surf,v15,v12,v14)
	End If
If b = True Then
		AddTriangle(surf,v9,v11,v8) ; left
		AddTriangle(surf,v8,v11,v10)
	End If
	Return z

End Function


Function make3rightup(a=True,b=True,c=True,d=True,e=True)

; |\
; |  \
; |____\


	z=CreateMesh() 

	surf=CreateSurface(z) 

;	v16 = AddVertex(surf,0,4,0  ,0,1) ; top
;	v17 = AddVertex(surf,0,4,4	,1,1)	
;	v18 = AddVertex(surf,4,0,0	,0,0)
;	v19 = AddVertex(surf,4,0,4	,1,0)

	v20 = AddVertex(surf,0,0,0  ,0,1) ; bottom
	v21 = AddVertex(surf,0,0,4	,1,1)	
	v22 = AddVertex(surf,4,0,0	,0,0)
	v23 = AddVertex(surf,4,0,4	,1,0)

	v1 = AddVertex(surf,0,0,4	,0,1) ; back
	v2 = AddVertex(surf,4,0,4	,1,1)	
	v5 = AddVertex(surf,0,4,4	,0,0)
	v6 = AddVertex(surf,4,4,4	,1,0)
		
	v0 = AddVertex(surf,0,0,0,	0,1) ; front
	v3 = AddVertex(surf,4,0,0,	1,1)	
	v4 = AddVertex(surf,0,4,0,	0,0)
	v7 = AddVertex(surf,4,4,0,	1,0)
	
	v12 = AddVertex(surf,4,0,0  ,0,1) ; right
	v13 = AddVertex(surf,4,0,4  ,1,1)	
	v14 = AddVertex(surf,0,4,0	,0,0)
	v15 = AddVertex(surf,0,4,4	,1,0)

	v8  = AddVertex(surf,0,0,0	,0,1) ; left
	v9  = AddVertex(surf,0,0,4	,1,1)	
	v10 = AddVertex(surf,0,4,0	,0,0)
	v11 = AddVertex(surf,0,4,4	,1,0)


If a = True
		AddTriangle(surf,v21,v20,v22) ; bottom
		AddTriangle(surf,v21,v22,v23)
End If
If e = True Then
		AddTriangle(surf,v5,v1,v2) ; back
End If
If d = True Then
		AddTriangle(surf,v0,v4,v3) ; front
;		AddTriangle(surf,v3,v4,v7) ; 
	End If
If c = True Then
		AddTriangle(surf,v15,v13,v12) ; right
		AddTriangle(surf,v15,v12,v14)
	End If
If b = True Then
		AddTriangle(surf,v9,v11,v8) ; left
		AddTriangle(surf,v8,v11,v10)
	End If
	Return z

End Function
