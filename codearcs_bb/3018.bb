; ID: 3018
; Author: K
; Date: 2013-01-25 22:37:15
; Title: Generic Functions
; Description: Useful little thingies...

Function InBox(entity,x1,y1,z1,Call=0,x2=0,y2=0,z2=0)
;NOTE:  x1 and z1 must ALWAYS be smaller
;      and y1 must ALWAYS be on top.
;(The next line is for if you don't pass the _2's in.
; If you want to read these at 0, pass Call=2)
If Call=1 x2=x1+15 : y2=y1+10 : z2=z1+10
x = (EntityX(entity,1)<=x2)And(EntityX(entity,1)>=x1)
y = (EntityY(entity,1)>=y2)And(EntityY(entity,1)<=y1)
z = (EntityZ(entity,1)<=z2)And(EntityZ(entity,1)>=z1)
If(x And y And z) Return True Else Return False
End Function


Function Clone(k,t,method=0)
;0=repos
;1=rot
;2=pos with collisions
;3=<void>
;4=repos and rot
;5=pos and rot with collisions
 Select method
 Case 0 Repos(k,EntityX(t,1),EntityY(t,1),EntityZ(t,1))
 Case 1 RotateEntity k,EntityPitch(t,1),EntityYaw(t,1),EntityRoll(t,1),1
 Case 2 PositionEntity k,EntityX(t,1),EntityY(t,1),EntityZ(t,1),1
 Case 4 Repos(k,EntityX(t,1),EntityY(t,1),EntityZ(t,1)):RotateEntity k,EntityPitch(t,1),EntityYaw(t,1),EntityRoll(t,1),1
 Case 5 PositionEntity k,EntityX(t,1),EntityY(t,1),EntityZ(t,1),1 : RotateEntity k,EntityPitch(t,1),EntityYaw(t,1),EntityRoll(t,1),1 
 End Select
End Function


Function Swap%(entity,Parent=-1)
If Parent=-1 Parent=GetParent(entity)
FreeEntity entity
Return CreatePivot(Parent)
End Function


Function Repos(entity,fx#,fy#,fz#,Yaw=-500)
 t=GetEntityType(entity)
  EntityType entity,0
   PositionEntity entity,fx,fy,fz,1
  If Yaw<>-500RotateEntity entity,EntityPitch(entity,1),Yaw,EntityRoll(entity,1),1
 EntityType entity,t
End Function


Function ReleaseChildren(entity)
;I give props: Kryzon just now fixed this one for
;me. Thanks Rafael!
t=CountChildren(entity)
 For k=1To t
  EntityParent GetChild(entity,1),0
 Next
End Function


Function Hat(x,y)
x=x+1y=y+1
a=ATan(y/x)
Return a
End Function


Function divis(a#,b)
;this func tells you if a number is divisible by another
If Int(a/b)<>(a/b) Return False Else Return True
End Function


;Method param for future expansion,
;method>0 will return 0.
Function Distance#(x,y,z,entity,method=0)
Local a#
If method=0
a = Sqr((x-EntityX(entity,1))^2) +Sqr((y-EntityY(entity,1))^2)+Sqr((z-EntityZ(entity,1))^2)
EndIf
Return a
End Function


Function Direct(src,des,Call=0)
PointEntity src,des
If Call=1 Call=EntityRoll(src,0)
RotateEntity src,0,EntityYaw(src,0),Call,0
End Function


Function Point(x,y,z,entity,method=0)
k=CreatePivot()
PositionEntity k,x,y,z
If(Method=0)
PointEntity entity,k
ElseIf(method>0)And(method<2)
Direct entity,k,method-1
EndIf
FreeEntity k
End Function


Function LoadMD22%(Path$,Call=0);If you use MD2s... (:P)
If Call=1 k=13Else k=9
tex=LoadTexture(Path$+".BMP",k)
t=LoadMD2(Path$+".MD2")
EntityTexture t,tex : FreeTexture tex
Return t
End Function

;These use the power-of-two flags
;on whatever value you feed it.
Function CheckBinFlag(k,offset=0)
s=Int(Mid(Bin(k),32-offset,1))
If s=1Return True Else Return False
End Function

;Note: BinFlag() funcs must be called as follows...
;
;n=Pu__BinFlag%(n,offset)
;(otherwise you will get no result).
Function PushBinFlag%(k,offset=0)
k=k+(1Shl offset)
Return k%
End Function


Function PullBinFlag%(k,offset=0)
k=k-(1Shl offset)
Return k%
End Function
