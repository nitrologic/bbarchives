; ID: 368
; Author: starfox
; Date: 2002-07-17 15:21:56
; Title: Correct Offset
; Description: Corrects the offset

Function CorrectOffset(mesh)
;This little function corrects a mesh offset from its centre and positions the
;mesh's pivot into the center of the physical mesh. It does this by calculating the
;offset and positioning it. Created by David Dawkins, thanks also goes to BlitzSupport
;for the centermesh command. Command is free to use but with give credit towards me.
surf =GetSurface(mesh,1)
oldx# = VertexX(surf,1) : oldy# = VertexY(surf,1) : oldz# = VertexZ(surf,1)
TFormPoint(oldx,oldy,oldz,mesh,0) : oldx = TFormedX() : oldy = TFormedY() : oldz = TFormedZ()
mw = MeshWidth(mesh) : mh = MeshHeight(mesh) : md = MeshDepth(mesh)
FitMesh mesh,-mw/2,-mh/2,-md/2,mw,mh,md,1
newx# = VertexX(surf,1) : newy# = VertexY(surf,1) : newz# = VertexZ(surf,1)
TFormPoint(newx,newy,newz,mesh,0) : newx = TFormedX() : newy = TFormedY() : newz = TFormedZ()
offx# = oldx - newx : offy# = oldy - newy : offz# = oldz - newz
TranslateEntity mesh,offx,offy,offz
End Function
