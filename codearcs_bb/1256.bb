; ID: 1256
; Author: JoshK
; Date: 2005-01-09 17:30:30
; Title: sGLU32.dll
; Description: DLL to call GL and GLU functions with single floats

sGLU32.decls:
.lib "dlls\sGlu32.dll"
gluOrtho2D(nleft#,nright#,nbottom#,ntop#)
gluPerspective(fovy#,aspect#,zNear#,zFar#)
gluPickMatrix(x#,y#,nwidth#,nheight#,pviewport*)
gluSphere(qobj,radius#,slices,stacks)
glOrtho(nleft#,nright#,bottom#,top#,zNear#,zFar#)
gluProject(objx#,objy#,objz#,modelmatrix*,projmatrix*,viewport*,winx#,winy#,winz#)
gluUnProject%(winx#,winy#,winz#,modelmatrix*,projmatrix*,viewport*,objx*,objy*,objz*)
gluLookAt(eyex#,eyey#,eyez#,centerx#,centery#,centerz#,upx#,upy#,upz#)
gluCylinder(qobj,baseRadius#,topRadius#,nheight#,slices,stacks)
gluDisk(qobj,innerRadius#,outerRadius#,slices,loops)
gluPartialDisk(qobj,innerRadius#,outerRadius#,slices,loops,startAngle#,sweepAngle#)

sGLU32.dll:
Structure double
a.l
b.l
EndStructure

ProcedureDLL gluOrtho2D(nleft.f,nright.f,nbottom.f,ntop.f)
gluOrtho2D__(nleft.f,nright.f,nbottom.f,ntop.f)
EndProcedure

ProcedureDLL gluPerspective(fovy.f,aspect.f,zNear.f,zFar.f)
gluPerspective__(fovy.f,aspect.f,zNear.f,zFar.f)
EndProcedure

ProcedureDLL gluPickMatrix(x.f,y.f,nwidth.f,nheight.f,pviewport)
gluPickMatrix__(x.f,y.f,nwidth.f,nheight.f,pviewport)
EndProcedure

ProcedureDLL gluSphere(qobj,radius.f,slices,stacks)
gluSphere__(qobj,radius.f,slices,stacks)
EndProcedure

ProcedureDLL glOrtho(nleft.f,nright.f,bottom.f,top.f,zNear.f,zFar.f)
glOrtho__(nleft.f,nright.f,bottom.f,top.f,zNear.f,zFar.f)
EndProcedure

ProcedureDLL gluLookAt(eyex.f,eyey.f,eyez.f,centerx.f,centery.f,centerz.f,upx.f,upy.f,upz.f)
gluLookAt__(eyex.f,eyey.f,eyez.f,centerx.f,centery.f,centerz.f,upx.f,upy.f,upz.f)
EndProcedure

ProcedureDLL gluProject(objx.f,objy.f,objz.f,modelmatrix,projmatrix,viewport,winx.f,winy.f,winz.f)
gluProject__(objx.f,objy.f,objz.f,modelmatrix,projmatrix,viewport,winx.f,winy.f,winz.f)
EndProcedure

ProcedureDLL gluCylinder(qobj,baseRadius.f,topRadius.f,nheight.f,slices,stacks)            
gluCylinder__(qobj,baseRadius.f,topRadius.f,nheight.f,slices,stacks) 
EndProcedure

ProcedureDLL gluDisk(qobj,innerRadius.f,outerRadius.f,slices,loops)
gluDisk__(qobj,innerRadius.f,outerRadius.f,slices,loops)
EndProcedure

ProcedureDLL gluPartialDisk(qobj,innerRadius.f,outerRadius.f,slices,loops,startAngle.f,sweepAngle.f)
gluPartialDisk__(qobj,innerRadius.f,outerRadius.f,slices,loops,startAngle.f,sweepAngle.f)
EndProcedure

;ProcedureDLL.l gluUnProject(winx.f,winy.f,winz.f,modelmatrix,projmatrix,viewport,objx,objy,objz)
;ProcedureReturn gluUnProject__(winx.f,winy.f,winz.f,modelmatrix,projmatrix,viewport,objx,objy,objz)
;EndProcedure

ProcedureDLL.l gluUnProject(winx.f,winy.f,winz.f,modelmatrix,projmatrix,viewport,objx,objy,objz)
dwinx.double
dwiny.double
dwinz.double
F64_Float(dwinx.double,winx)
F64_Float(dwiny.double,winy)
F64_Float(dwinz.double,winz)
dobjx=AllocateMemory(8)
dobjy=AllocateMemory(8)
dobjz=AllocateMemory(8)
result=gluUnProject_(dwinx\a,dwinx\b,dwiny\a,dwiny\b,dwinz\a,dwinz\b,modelmatrix,projmatrix,viewport,dobjx,dobjy,dobjz)
x.double
y.double
z.double
x\a=PeekL(dobjx+0)
x\b=PeekL(dobjx+4)
y\a=PeekL(dobjy+0)
y\b=PeekL(dobjy+4)
z\a=PeekL(dobjz+0)
z\b=PeekL(dobjz+4)
rx.f=F64_toFloat(x)
ry.f=F64_toFloat(y)
rz.f=F64_toFloat(z)
PokeF(objx,rx)
PokeF(objy,ry)
PokeF(objz,rz)
FreeMemory(dobjx)
FreeMemory(dobjy)
FreeMemory(dobjz)
ProcedureReturn result
EndProcedure
