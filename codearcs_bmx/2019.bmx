; ID: 2019
; Author: Oddball
; Date: 2007-05-25 19:53:01
; Title: glPerspective
; Description: An alternative to gluPespective

Function glPerspective( fovx:Float, ratio:Float, near:Float, far:Float )
		Local x:Float=Tan(fovx/2.0)*near
		Local y:Float=x/ratio
		glFrustum -x,x,-y,y,near,far
End Function
