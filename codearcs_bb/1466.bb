; ID: 1466
; Author: mongia2
; Date: 2005-09-21 10:33:37
; Title: avi to texture
; Description: code for avi to texture

dim camera(0)
dim timer(0)
dim old_mousex(0)
dim old_mousey(0)


graphics3d 800,600,32,3
setbuffer backbuffer()

cleartexturefilters()

camera(0)=createcamera()

luce=createlight()

positionentity luce,0,50,0


timer(0)=createtimer(30)


positionentity camera(0),5,5,5


img=createtexture(512,512,256)
cube=createcube()


pointentity camera(0),cube

while not keydown(1)


te=millisecs()

if keydown(57) and movie=0
movie=1
mov=openmovie("avi/Seq_04.avi")
entitytexture cube,img
showentity cube
endif







if mov>0
drawmovie mov,0,0,512,512
if movieplaying(mov)=0
closemovie(mov)
mov=0
movie=0
hideentity cube

endif
endif

CopyRect 0,0,512,512,0,0,backBuffer(),textureBuffer(img)

temm=millisecs()-te

gestione_camera_editor()


renderworld



old_mousex(0)=mousex()
old_mousey(0)=mousey()




flip false
waittimer timer(0)
wend

end






function gestione_camera_editor()

    dif_x#=old_mousex(0)-mousex()
    dif_y#=old_mousey(0)-mousey()

  CamSpd#=1
		MoveEntity(camera(0), Float(KeyDown(205) - KeyDown(203)) * CamSpd#, 0, Float(KeyDown(200) - KeyDown(208)) * CamSpd#)


		If MouseDown(2)
			TurnSpeed# = 0.8
		TurnEntity(camera(0), Float(dif_y#)  * TurnSpeed#, 0, 0, False)
		TurnEntity(camera(0), 0, Float(dif_x#) * TurnSpeed#, 0, True)

		EndIf





end function
