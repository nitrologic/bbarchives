; ID: 367
; Author: sswift
; Date: 2002-07-17 02:14:37
; Title: DeleteEntity vs. ClearSurface speed test
; Description: This tests the speed of copying one mesh to another constantly in two diffrent ways.


Graphics3D 640,480,16,0


Mesh = CreateSphere()


start=MilliSecs()

;----------------------------------------------------------------------------------------------------------------------
For c =1 To 10000

	NewMesh1 = CreateMesh()
	AddMesh Mesh, NewMesh1
	FreeEntity NewMesh1

Next
;----------------------------------------------------------------------------------------------------------------------


time1#=MilliSecs()-start
start=MilliSecs()


;----------------------------------------------------------------------------------------------------------------------
NewMesh1 = CreateMesh()
AddMesh Mesh, NewMesh1
Surface = GetSurface(NewMesh1, 1)

For c =1 To 10000

	ClearSurface Surface
	AddMesh Mesh, NewMesh1
	
Next
;----------------------------------------------------------------------------------------------------------------------


time2#=MilliSecs()-start
Print "The second method takes " + (time2# / time1#) + " times as long."
