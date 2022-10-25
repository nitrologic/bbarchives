; ID: 1921
; Author: rich41x
; Date: 2007-02-11 23:31:51
; Title: SetDrawMode
; Description: Sets the polygon drawing mode to filled, outlined, or outlined by points.

Const DRAW_POINT:Int = GL_POINT
Const DRAW_LINE:Int = GL_LINE
Const DRAW_FILL:Int = GL_FILL

Function SetDrawMode(mode:Int)
    glPolygonMode(GL_FRONT_AND_BACK, mode)
EndFunction
