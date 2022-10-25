; ID: 2542
; Author: ImaginaryHuman
; Date: 2009-07-22 17:03:42
; Title: Arbitrary-Control Bezier Curves
; Description: Calculate bezier curves with any number of control points per bezier

Function CurvePointAny(X:Float[],Y:Float[],NumberOfControls:Int=3,Position:Float,PointX:Float Var,PointY:Float Var)
	'Calculate a point on a bezier curve with any number of control points using single-precision floating point math
	'Requires at least 3 control points with X and Y coordinates and a current position on the curve in the range 0..1
	'Can handle any number of control points >=3, but the more you add the more calculation is required
	'Coordinates are returned in PointX and PointY variables
	'Contents of X and Y arrays will be trashed by the calculations, so create copies if you need to preserve values

	'Calculate bezier curve with any number of control points
	Local OuterLoop:Int=NumberOfControls-1			'Total number of outer-loop iterations
	Local InnerLoop:Int
	While OuterLoop>0
		InnerLoop=0									'Start inner loop at 0
		While InnerLoop<OuterLoop
			X[InnerLoop]:+((X[InnerLoop+1]-X[InnerLoop])*Position)	'Calculate X point on the curve
			Y[InnerLoop]:+((Y[InnerLoop+1]-Y[InnerLoop])*Position)	'Calculate Y point on the curve
			InnerLoop:+1								'Next inner loop
		Wend
		OuterLoop:-1									'Next outer loop
	Wend	

	'Copy final position into return variables
	PointX=X[0]
	PointY=Y[0]
End Function
