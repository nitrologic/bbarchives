; ID: 2513
; Author: RifRaf
; Date: 2009-06-24 18:07:28
; Title: Realtime Vertex lights
; Description: Realtime Vertex Lights

Graphics3D 640,480,32,2 
;Wireframe True
	camera=CreateCamera() 
	PositionEntity camera,-15,40,-50
	;create some random junk
	For i=1 To 2
	sc=Rand(1,10)
	Cube=CreateSphere(12)
	ScaleMesh cube,sc,sc,sc:EntityPickMode cube,2,True
	createfakelight_Receiver(cube)
	PositionEntity cube,Rand(-20,20),Rand(-20,20),Rand(-20,20)
	Next

	For i=-5 To 5
		For ii=-5 To 5
	        land=createsquare(2)
		    ScaleMesh land,10,1,10
	    	PositionEntity land,i*10,-3,ii*10
		    createfakelight_Receiver(land)
	    Next
	Next

    globallight=createfakelight(100,500,100,38,38,38,500)
	light1=createfakelight(10,5,10,0,0,255,53.5)
	light2=createfakelight(10,5,10,255,0,0,53.5)
	lightvisual=CreateCube()
	lightvisual2=CreateCube()
	; put a visual cube on the lights
	EntityParent lightvisual,light1,False
	EntityParent lightvisual2,light2,False
	TranslateEntity light1,0,10,20
	TranslateEntity light2,0,10,-20
	PointEntity camera,cube


	; update it all
	While Not KeyDown(1)

		MoveEntity light1,0,0,2
		TurnEntity light1,0,1.5,0
		MoveEntity light2,0,0,-.25
		TurnEntity light2,0,-.5,0

	 	lightfakereceivers()
		UpdateWorld()
		RenderWorld 
		Flip 
	Wend 
	End


Type FakeLight
 Field Ent
 Field r,g,b
 Field range#
 Field active
End Type

Type FakeLightReceiver
 Field ent    ;just the entity handle
End Type

Function CreateFakeLight(x#,y#,z#,r#,g#,b#,range#)
 fl.fakelight=New fakelight
 fl\ent=CreatePivot()
 PositionEntity fl\ent,x,y,z
 fl\r=r
 fl\g=g
 fl\b=b
 fl\range=range
 fl\active=1
 Return fl\ent
End Function 

Function FakeLightOnOFF(toggle=0)
 For fl.fakelight=Each fakelight
 	If fl\ent=ent Then 
       fl\active=toggle
       Exit
    EndIf
 Next
End Function  

Function SetFakeLightRange(ent,newrange#)
 For fl.fakelight=Each fakelight
 	If fl\ent=ent Then 
    	fl\range=newrange#
        Exit
    EndIf
 Next
End Function 

Function SetFakeLightRGB(ent,newr,newg,newb)
 For fl.fakelight=Each fakelight
 	If fl\ent=ent Then 
    	fl\r=newr
    	fl\g=newg
    	fl\b=newb
        Exit
    EndIf
 Next
End Function 

Function CreateFakeLight_Receiver(ent,fx=3)
flr.fakelightreceiver=New fakelightreceiver
flr\ent=ent
EntityFX ent,fx
End Function



Function LightFakeReceivers()
For flr.FakeLightReceiver=Each FakeLightReceiver
	LightMesh flr\ent,-255,-255,-255
	For fl.fakelight=Each fakelight
		If fl\active=1 And EntityDistance(flr\ent,fl\ent)=<(fl\range*4) Then 
	        If EntityVisible (fl\ent,flr\ent) Then 
				TFormPoint 0,0,0,fl\ent,flr\ent 
				LightMesh flr\ent,fl\r,fl\g,fl\b,fl\range*.25,TFormedX(),TFormedY(),TFormedZ()
			EndIf					
		EndIf
	Next
Next
End Function

Function createsquare(segs#=2,parent=0)
    mesh=CreateMesh( parent )
    surf=CreateSurface( mesh )
    l# =-.5
    b# = -.5
    tvc= 0
    Repeat
      u# = l + .5
      v# = b + .5
      AddVertex surf,l,0,b,u,1-v
      tvc=tvc + 1
      l = l + 1/segs
      If l > .501 Then
        l = -.5
        b = b + 1/segs
      End If
    Until b > .5
    vc# =0
    Repeat
      AddTriangle (surf,vc,vc+segs+1,vc+segs+2)
      AddTriangle (surf,vc,vc+segs+2,vc+1)
      vc = vc + 1
      tst# =  ((vc+1) /(segs+1)) -Floor ((vc+1) /(segs+1))
      If (vc > 0) And (tst=0) Then
        vc = vc + 1
      End If
    Until vc=>tvc-segs-2
    UpdateNormals mesh
    Return mesh
End Function
