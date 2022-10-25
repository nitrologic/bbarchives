; ID: 1249
; Author: mongia2
; Date: 2004-12-30 04:16:07
; Title: new fast animation
; Description: animation mnz

;mnz animation
;mnzsoftware@aliceposta.it


dim bank_anim(100)
dim bank_anim_rt(100)


dim num_bo(100)
dim costante_anim(0)
costante_anim(0)=24050


dd=millisecs()

dim load(0)
load(0)=0

global mu


numero_omini=100
dim play_blending(numero_omini)
dim frame_iniziale(numero_omini)
dim frame_finale(numero_omini)
dim frame_spazio(numero_omini)
dim tipo_blending(numero_omini)
dim attivazione_blending(numero_omini)
dim incrementale_blending(numero_omini)
dim frame_blending(numero_omini)
dim transizione_blending(numero_omini)
dim tipologia_blending(100)




Graphics3D 800,600,16,2
SetBuffer BackBuffer()

apptitle "mnz compiler 001","Sei sicuro?"

camera=CreateCamera()

AmbientLight(100, 100, 100)


as$="load a animfile b3d"   ; load a animfile

; Load mesh
mesh=LoadanimMesh(as$)




positionentity mesh,0,0,0


xx=90
entityfx mesh,16


dim numero_bones(0)
dim nome_bones$(5,100)
numero_bones(0)=23

bon=numero_bones(0)
dim bones(bon,10)
fra=1000





dim angolo_bone_x#(bon,fra)
dim angolo_bone_y#(bon,fra)
dim angolo_bone_z#(bon,fra)
dim pos_bone_x#(bon,fra)
dim pos_bone_y#(bon,fra)
dim pos_bone_z#(bon,fra)


bank_anim_rt(0)=createbank(bon*18*4+100)




dim iniziale_ax#(bon,10)    ;0
dim iniziale_ay#(bon,10)    ;4
dim iniziale_az#(bon,10)    ;8
dim iniziale_pax#(bon,10)   ;12
dim iniziale_pay#(bon,10)   ;16
dim iniziale_paz#(bon,10)   ;20

dim attuale_ax#(bon,10)     ;24
dim attuale_ay#(bon,10)     ;28
dim attuale_az#(bon,10)     ;32
dim attuale_pax#(bon,10)    ;36
dim attuale_pay#(bon,10)    ;40
dim attuale_paz#(bon,10)    ;44


dim differenza_ax#(bon,10)  ;48
dim differenza_ay#(bon,10)  ;52
dim differenza_az#(bon,10)  ;56
dim differenza_pax#(bon,10) ;60
dim differenza_pay#(bon,10) ;64
dim differenza_paz#(bon,10) ;68



b3dhierarchy(mesh%,0)




if load(0)=0
num=0

;-------------------------------------zero°
for n=0 to numero_bones(0)
bones(n,num)=FindChild (mesh,nome_bones$(0,n))
for fra=0 to 699       ;qui 280
SetAnimTime mesh,fra,0  ;colpo a zero°

angolo_bone_x#(n,fra)=entitypitch#(bones(n,num))
angolo_bone_y#(n,fra)=entityyaw#(bones(n,num))
angolo_bone_z#(n,fra)=entityroll#(bones(n,num))
pos_bone_x#(n,fra)=entityx#(bones(n,num))
pos_bone_y#(n,fra)=entityy#(bones(n,num))
pos_bone_z#(n,fra)=entityz#(bones(n,num))


next
next


tempo_s2=millisecs()-dd


;#####################################################
file_save=WriteFile("dati_scheletro_001.txt")
For n =0 To numero_bones(0)
For fra=0 to 1000
WriteFloat (file_save,angolo_bone_x#(n,fra))
WriteFloat (file_save,angolo_bone_y#(n,fra))
WriteFloat (file_save,angolo_bone_z#(n,fra))
WriteFloat (file_save,pos_bone_x#(n,fra))
WriteFloat (file_save,pos_bone_y#(n,fra))
WriteFloat (file_save,pos_bone_z#(n,fra))

next
next
closefile file_save
;######################################


endif
;#####################################



num=0
file_save=readFile("dati_scheletro_001.txt")
For n =0 To numero_bones(0)
bones(n,num)=FindChild(mesh,nome_bones$(0,n))
for fra=0 to 1000
angolo_bone_x#(n,fra)=READFloat(file_save)
angolo_bone_y#(n,fra)=READFloat(file_save)
angolo_bone_z#(n,fra)=READFloat(file_save)
pos_bone_x#(n,fra)=READFloat(file_save)
pos_bone_y#(n,fra)=READFloat(file_save)
pos_bone_z#(n,fra)=READFloat(file_save)
next
next
closefile file_save


file_save=readFile("dati_scheletro_001.txt")
bank_anim(0)=createbank(costante_anim(0)*(numero_bones(0)+1)*24.1)

for n=0 to numero_bones(0)
for fra=0 to 1000
PokeFloat bank_anim(0),0+fra*24+n*costante_anim(0),READFloat(file_save)
PokeFloat bank_anim(0),4+fra*24+n*costante_anim(0),READFloat(file_save)
PokeFloat bank_anim(0),8+fra*24+n*costante_anim(0),READFloat(file_save)
PokeFloat bank_anim(0),12+fra*24+n*costante_anim(0),READFloat(file_save)
PokeFloat bank_anim(0),16+fra*24+n*costante_anim(0),READFloat(file_save)
PokeFloat bank_anim(0),20+fra*24+n*costante_anim(0),READFloat(file_save)

next
next
closefile file_save






hide=0


tempo_s=millisecs()-dd




hide2=1

positionentity camera,5,5,5
pointentity camera,mesh


set_anim_mnz(0,1,2,0,639,2,40)



While Not KeyDown( 1 )


if keyhit(2) then set_anim_mnz(0,1,2,91,118,1,4)



if keydown(75) then xs#=xs#-.01
if keydown(77) then xs#=xs#+.01


;positionentity sfere,0,.305,xs#

	; Time elapsed between last frame
		Time% = MilliSecs()
		DeltaTime# = Float(Time - OldTime) / 1000   ; in seconds
		OldTime% = Time
	
		; Camera movement
		CamSpd# = 20 * DeltaTime
		MoveEntity(camera, Float(KeyDown(205) - KeyDown(203)) * CamSpd, 0, Float(KeyDown(200) - KeyDown(208)) * CamSpd)

		If MouseDown(1)
			TurnSpeed# = 0.8
			TurnEntity(camera, Float(MouseYSpeed())  * TurnSpeed#, 0, 0, False)
			TurnEntity(camera, 0, -Float(MouseXSpeed()) * TurnSpeed#, 0, True)
		Else
			MouseXSpeed() : MouseYSpeed()
		EndIf

    tempo_click=tempo_click-1



    ;if keyhit(72) then hideentity bones(63,0)
    ;if keyhit(80) then showentity bones(63,0)


if keydown(59)
set_anim_mnz(0,1,2,0,50,2,40)
endif

if keydown(60)
set_anim_mnz(0,1,2,100,150,2,10)
endif


if keydown(64) and tempo_click<0
tempo_click=3
mu=mu+1
if mu=78 then mu=78
set_anim_mnz(0,1,2,mu,mu,1,1)
endif


if keydown(62) and tempo_click<0
tempo_click=3
mu=mu-1
if mu<0 then mu=0
set_anim_mnz(0,1,2,mu,mu,1,1)
endif


te=millisecs()

anim_mnz_poke_new(0)

te2=millisecs()-te



updateworld
RenderWorld

num=0
text 10,80,"te2 "+te2







text 500,100,"frame_blending(num)"+frame_blending(0)



text 10,180,"AnimLength (mesh)"+AnimLength (mesh)

Flip
Wend

End




Function Point_Entity(entity,x#,y#,z#)
   xdiff# = EntityX(entity)-x#
   ydiff# = EntityY(entity)-y#
   zdiff# = EntityZ(entity)-z#
   dist#=Sqr#((xdiff#*xdiff#)+(zdiff#*zdiff#))
   pitch# = ATan2(ydiff#,dist#)
   yaw#   = ATan2(xdiff#,-zdiff#)
   RotateEntity entity,pitch#,yaw#,0
End Function



Function AngleDifference#(angle1#,angle2#)
Return ((angle2 - angle1) Mod 360 + 540) Mod 360 - 180
End Function


function set_anim_mnz(num,tipo,att,ini,fini,spa,trans)
attivazione_blending(num)=att
incrementale_blending(num)=0
frame_iniziale(num)=ini
frame_finale(num)=fini
frame_spazio(num)=spa
transizione_blending(num)=trans
tipologia_blending(num)=tipo

end function






function anim_mnz_poke_new(num)

if attivazione_blending(num)=2
if incrementale_blending(num)=0
for n=0 to numero_bones(0)

pokefloat bank_anim_rt(0),0+n*72,peekfloat(bank_anim_rt(0),24+n*72)
pokefloat bank_anim_rt(0),4+n*72,peekfloat(bank_anim_rt(0),28+n*72)
pokefloat bank_anim_rt(0),8+n*72,peekfloat(bank_anim_rt(0),32+n*72)



pokefloat bank_anim_rt(0),48+n*72,AngleDifference#(peekfloat(bank_anim_rt(0),24+n*72),peekFloat(bank_anim(0),frame_iniziale(num)*24+n*costante_anim(0)))/transizione_blending(num)
pokefloat bank_anim_rt(0),52+n*72,AngleDifference#(peekfloat(bank_anim_rt(0),28+n*72),peekFloat(bank_anim(0),4+frame_iniziale(num)*24+n*costante_anim(0)))/transizione_blending(num)
pokefloat bank_anim_rt(0),56+n*72,AngleDifference#(peekfloat(bank_anim_rt(0),32+n*72),peekFloat(bank_anim(0),8+frame_iniziale(num)*24+n*costante_anim(0)))/transizione_blending(num)


pokefloat bank_anim_rt(0),12+n*72,peekfloat(bank_anim_rt(0),36+n*72)
pokefloat bank_anim_rt(0),16+n*72,peekfloat(bank_anim_rt(0),40+n*72)
pokefloat bank_anim_rt(0),20+n*72,peekfloat(bank_anim_rt(0),44+n*72)


pokefloat bank_anim_rt(0),60+n*72,(peekfloat(bank_anim_rt(0),36+n*72)-peekFloat(bank_anim(0),12+frame_iniziale(num)*24+n*costante_anim(0)))/transizione_blending(num)
pokefloat bank_anim_rt(0),64+n*72,(peekfloat(bank_anim_rt(0),40+n*72)-peekFloat(bank_anim(0),16+frame_iniziale(num)*24+n*costante_anim(0)))/transizione_blending(num)
pokefloat bank_anim_rt(0),68+n*72,(peekfloat(bank_anim_rt(0),44+n*72)-peekFloat(bank_anim(0),20+frame_iniziale(num)*24+n*costante_anim(0)))/transizione_blending(num)


next
endif
incrementale_blending(num)=incrementale_blending(num)+1

for n=0 to numero_bones(0)

pokefloat bank_anim_rt(0),24+n*72,peekfloat(bank_anim_rt(0),0+n*72)+peekfloat(bank_anim_rt(0),48+n*72)* incrementale_blending(num)
pokefloat bank_anim_rt(0),28+n*72,peekfloat(bank_anim_rt(0),4+n*72)+peekfloat(bank_anim_rt(0),52+n*72)* incrementale_blending(num)
pokefloat bank_anim_rt(0),32+n*72,peekfloat(bank_anim_rt(0),8+n*72)+peekfloat(bank_anim_rt(0),56+n*72)* incrementale_blending(num)

rotateentity bones(n,num),peekfloat(bank_anim_rt(0),24+n*72),peekfloat(bank_anim_rt(0),28+n*72),peekfloat(bank_anim_rt(0),32+n*72)

pokefloat bank_anim_rt(0),36+n*72,peekfloat(bank_anim_rt(0),12+n*72)-peekfloat(bank_anim_rt(0),60+n*72)* incrementale_blending(num)
pokefloat bank_anim_rt(0),40+n*72,peekfloat(bank_anim_rt(0),16+n*72)-peekfloat(bank_anim_rt(0),64+n*72)* incrementale_blending(num)
pokefloat bank_anim_rt(0),44+n*72,peekfloat(bank_anim_rt(0),20+n*72)-peekfloat(bank_anim_rt(0),68+n*72)* incrementale_blending(num)


positionentity bones(n,num),peekfloat(bank_anim_rt(0),36+n*72),peekfloat(bank_anim_rt(0),40+n*72),peekfloat(bank_anim_rt(0),44+n*72)


next

if incrementale_blending(num)>=transizione_blending(num)
attivazione_blending(num)=1



incrementale_blending(num)=0
frame_blending(num)=frame_iniziale(num)+1
endif
endif



if attivazione_blending(num)=1 and tipologia_blending(num)=1

if incrementale_blending(num)=0
for n=0 to numero_bones(0)

pokefloat bank_anim_rt(0),0+n*72,peekfloat(bank_anim_rt(0),24+n*72)
pokefloat bank_anim_rt(0),4+n*72,peekfloat(bank_anim_rt(0),28+n*72)
pokefloat bank_anim_rt(0),8+n*72,peekfloat(bank_anim_rt(0),32+n*72)


pokefloat bank_anim_rt(0),48+n*72,AngleDifference#(peekfloat(bank_anim_rt(0),24+n*72),peekFloat(bank_anim(0),frame_blending(num)*24+n*costante_anim(0)))/frame_spazio(num)
pokefloat bank_anim_rt(0),52+n*72,AngleDifference#(peekfloat(bank_anim_rt(0),28+n*72),peekFloat(bank_anim(0),4+frame_blending(num)*24+n*costante_anim(0)))/frame_spazio(num)
pokefloat bank_anim_rt(0),56+n*72,AngleDifference#(peekfloat(bank_anim_rt(0),32+n*72),peekFloat(bank_anim(0),8+frame_blending(num)*24+n*costante_anim(0)))/frame_spazio(num)


pokefloat bank_anim_rt(0),12+n*72,peekfloat(bank_anim_rt(0),36+n*72)
pokefloat bank_anim_rt(0),16+n*72,peekfloat(bank_anim_rt(0),40+n*72)
pokefloat bank_anim_rt(0),20+n*72,peekfloat(bank_anim_rt(0),44+n*72)


pokefloat bank_anim_rt(0),60+n*72,(peekfloat(bank_anim_rt(0),36+n*72)-peekFloat(bank_anim(0),12+frame_blending(num)*24+n*costante_anim(0)))/frame_spazio(num)
pokefloat bank_anim_rt(0),64+n*72,(peekfloat(bank_anim_rt(0),40+n*72)-peekFloat(bank_anim(0),16+frame_blending(num)*24+n*costante_anim(0)))/frame_spazio(num)
pokefloat bank_anim_rt(0),68+n*72,(peekfloat(bank_anim_rt(0),44+n*72)-peekFloat(bank_anim(0),20+frame_blending(num)*24+n*costante_anim(0)))/frame_spazio(num)


next
endif
incrementale_blending(num)=incrementale_blending(num)+1




for n=0 to numero_bones(0)
;attuale_ax#(n,num)=peekfloat(bank_anim_rt(0),0+n*72)+differenza_ax#(n,num)* incrementale_blending(num)
;attuale_ay#(n,num)=peekfloat(bank_anim_rt(0),4+n*72)+differenza_ay#(n,num)* incrementale_blending(num)
;attuale_az#(n,num)=peekfloat(bank_anim_rt(0),8+n*72)+differenza_az#(n,num)* incrementale_blending(num)

pokefloat bank_anim_rt(0),24+n*72,peekfloat(bank_anim_rt(0),0+n*72)+peekfloat(bank_anim_rt(0),48+n*72)* incrementale_blending(num)
pokefloat bank_anim_rt(0),28+n*72,peekfloat(bank_anim_rt(0),4+n*72)+peekfloat(bank_anim_rt(0),52+n*72)* incrementale_blending(num)
pokefloat bank_anim_rt(0),32+n*72,peekfloat(bank_anim_rt(0),8+n*72)+peekfloat(bank_anim_rt(0),56+n*72)* incrementale_blending(num)

rotateentity bones(n,num),peekfloat(bank_anim_rt(0),24+n*72),peekfloat(bank_anim_rt(0),28+n*72),peekfloat(bank_anim_rt(0),32+n*72)


pokefloat bank_anim_rt(0),36+n*72,peekfloat(bank_anim_rt(0),12+n*72)-peekfloat(bank_anim_rt(0),60+n*72)* incrementale_blending(num)
pokefloat bank_anim_rt(0),40+n*72,peekfloat(bank_anim_rt(0),16+n*72)-peekfloat(bank_anim_rt(0),64+n*72)* incrementale_blending(num)
pokefloat bank_anim_rt(0),44+n*72,peekfloat(bank_anim_rt(0),20+n*72)-peekfloat(bank_anim_rt(0),68+n*72)* incrementale_blending(num)


positionentity bones(n,num),peekfloat(bank_anim_rt(0),36+n*72),peekfloat(bank_anim_rt(0),40+n*72),peekfloat(bank_anim_rt(0),44+n*72)

next

if incrementale_blending(num)>=frame_spazio(num)
attivazione_blending(num)=1

frame_blending(num)=frame_blending(num)+1
      if frame_blending(num)>frame_finale(num)
      attivazione_blending(num)=2

      endif

incrementale_blending(num)=0
endif
endif


end function


Function b3dhierarchy(parent%,numero)


	children%=CountChildren(parent%)
	For loop = 1 To children%
		child%=GetChild (parent%,loop)
		;Print EntityName$(child%)
	;	writeline (file(0),"nome_bones$("+tipo+","+str$(num(0))+")="+chr(34)+EntityName$(child%)+chr(34))
		
    nome_bones$(numero,num_bo(numero))=EntityName$(child%)
    num_bo(numero)=num_bo(numero)+1
		b3dhierarchy(child%,numero)
	Next
	

End Function
