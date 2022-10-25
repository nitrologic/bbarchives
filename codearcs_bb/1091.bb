; ID: 1091
; Author: AntMan - Banned in the line of duty.
; Date: 2004-06-18 18:48:51
; Title: Vector/Matrix Lib
; Description: Various vector/matrix/transform funcs

Function matrixIdentity(matrix#[16])
  matrix[ 0] = 1.0;  matrix[ 1] = 0.0;  matrix[ 2] = 0.0;  matrix[ 3] = 0.0;
  matrix[ 4] = 0.0;  matrix[ 5] = 1.0;  matrix[ 6] = 0.0;  matrix[ 7] = 0.0;
  matrix[ 8] = 0.0;  matrix[ 9] = 0.0;  matrix[10] = 1.0;  matrix[11] = 0.0;
  matrix[12] = 0.0;  matrix[13] = 0.0;  matrix[14] = 0.0;  matrix[15] = 1.0;
End Function


Function gen_pitch_matrix(matrix#[16])
	matrix[0]=1:matrix[1]=0:matrix[2]=0:matrix[3]=0
	matrix[4]=0:matrix[5]=Cos(a):matrix[6]=Sin(a):matrix[7]=0
	matrix[8]=0:matrix[9]=-Sin(a):matrix[10]=Cos(a):matrix[11]=0
	matrix[12]=0:matrix[13]=0:matrix[14]=0:matrix[15]=1
End Function

Function gen_yaw_mat(matrix#[16])
	matrix[0]=Cos(a):matrix[1]=0:matrix[2]=-Sin(a):matrix[4]=0
	matrix[4]=0:matrix[5]=1:matrix[6]=0:matrix[7]=0
	matrix[8]=Sin(a):matrix[9]=0:matrix[10]=Cos(a):matrix[11]=0
	matrix[12]=0:matrix[13]=0:matrix[14]=0:matrix[15]=0
End Function

Function gen_roll_matrix(matrix#[16])
	matrix[0]=Cos(a):matrix[1]=Sin(a):matrix[2]=0:matrix[3]=0
	matrix[4]=-Sin(a):matrix[5]=Cos(a):matrix[6]=0:matrix[7]=0
	matrix[8]=0:matrix[9]=0:matrix[10]=0:matrix[11]=0
	matrix[12]=0:matrix[13]=0:matrix[14]=0:matrix[15]=0
End Function

Function gen_position_matrix(matrix#[16],x#,y#,z#)
	matrix[0]=1:matrix[1]=0:matrix[2]=0:matrix[3]=x
	matrix[4]=0:matrix[5]=1:matrix[6]=0:matrix[7]=y
	matrix[8]=0:matrix[9]=0:matrix[10]=0:matrix[11]=z
	matrix[12]=0:matrix[13]=0:matrix[14]=0:matrix[15]=1
End Function

Function multi_mat(matrix1#[16],matrix2#[16],matrix3#[16]) ;Takes matrices i1 and i2 and combines them, resulting in i3
	For m=0 To 3
		For m1=0 To 3
			matrix3[ m1*4+m]=0
			For m2=0 To 3
				matrix3[ m1*4+m]=matrix3[ m1*4+m1]+matrix2[m2*4+m]*matrix1[m1*4+m2]
			Next
		Next
	Next
End Function

Function gen_final_matrix( pitch#,yaw#,roll#,x#,y#,z#,outMatrix#[16])
	Local pitchMatrix#[16],yawMatrix#[16],rollMatrix#[16]
	Local positionMatrix#[16],tempMatrix#[16]
	gen_pitch_matrix( pitchMatrix,pitch)
	gen_yaw_matrix( yawMatrix,yaw)
	gen_roll_matrix( rollMatrix,roll)
	gen_position_matrix( positionMatrix,x,y,z)
	multi_mat(pitchMatrix,yawMatrix,tempMatrix)
	multi_mat(tempMatrix,rollMatrix,pitchMatrix)
	multi_mat(pitchMatrix,positionMatrix,outMatrix)
End Function

;//////////////////////////
;// Invert a matrix. (Matrix MUST be orhtonormal!)
;//   in - Input matrix
;//   out - Output matrix
;//////////////////////////
Function matrixInvert(in#[16], out#[16])
 ; // Transpose rotation
  out[ 0] = in[ 0];  out[ 1] = in[ 4];  out[ 2] = in[ 8];
  out[ 4] = in[ 1];  out[ 5] = in[ 5];  out[ 6] = in[ 9];
  out[ 8] = in[ 2];  out[ 9] = in[ 6];  out[10] = in[10];
  
 ; // Clear shearing terms
  out[3] = 0.0;f; out[7] = 0.0f; out[11] = 0.0f; out[15] = 1.0f;

 ; // Translation is minus the dot of tranlation And rotations
  out[12] = -(in[12]*in[ 0]) - (in[13]*in[ 1]) - (in[14]*in[ 2]);
  out[13] = -(in[12]*in[ 4]) - (in[13]*in[ 5]) - (in[14]*in[ 6]);
  out[14] = -(in[12]*in[ 8]) - (in[13]*in[ 9]) - (in[14]*in[10]);
End Function


;//////////////////////////
;// Multiply a vector by a matrix.
;//   vecIn - Input vector
;//   m - Input matrix
;///////////////////////////

Function vecMatMult(vecIn#[3],m#[16], vecOut#[3])
  vecOut[0] = (vecIn[0]*m[ 0]) + (vecIn[1]*m[ 4]) + (vecIn[2]*m[ 8]) + m[12];
  vecOut[1] = (vecIn[0]*m[ 1]) + (vecIn[1]*m[ 5]) + (vecIn[2]*m[ 9]) + m[13];
  vecOut[2] = (vecIn[0]*m[ 2]) + (vecIn[1]*m[ 6]) + (vecIn[2]*m[10]) + m[14];
End Function


;//////////////////////////
;// Multiply a vector by just the 3x3 portion of a matrix.
;//   vecIn - Input vector
;//   m - Input matrix
;//   vecOut - Output vector
;//////////////////////////
;void
Function vecMat3x3Mult(vecIn#[3], m#[16], vecOut#[3]) 
  vecOut[0] = (vecIn[0]*m[ 0]) + (vecIn[1]*m[ 4]) + (vecIn[2]*m[ 8]);
  vecOut[1] = (vecIn[0]*m[ 1]) + (vecIn[1]*m[ 5]) + (vecIn[2]*m[ 9]);
  vecOut[2] = (vecIn[0]*m[ 2]) + (vecIn[1]*m[ 6]) + (vecIn[2]*m[10]);
End Function


Function vecCrossProd (vecA#[3], vecB#[3], vecOut#[3])
   vecOut[0] =  vecA[1]*vecB[2] - vecA[2]*vecB[1];
   vecOut[1] =  vecA[2]*vecB[0] - vecA[0]*vecB[2];
   vecOut[2] =  vecA[0]*vecB[1] - vecA[1]*vecB[0];
End Function

Function vecNormalize#(vec#[3])
   mag# = Sqr(vec[0]*vec[0] +vec[1]*vec[1] +vec[2]*vec[2]);

   ;// don't divide by zero
   If (mag=0)
      vec[0] = 0.0;f;
      vec[1] = 0.0;f;
      vec[2] = 0.0;f;
      Return(0.0);
   EndIf

   vec[0] =vec[0]/mag;
   vec[1] =vec[1]/mag;
   vec[2] =vec[2]/mag;

   Return(mag);
End Function

Function vecDotProd#(vecA#[3], vecB#[3])
   Return(vecA[0]*vecB[0] +vecA[1]*vecB[1] +vecA[2]*vecB[2]);
End Function


Function vecCopy (vecIn#[3], vecOut#[3])
   vecOut[0] = vecIn[0];
   vecOut[1] = vecIn[1];
   vecOut[2] = vecIn[2];
 
End Function


Function vector( v1#,v2#,v3#,vect#[3])
	vect[0]=v1
	vect[1]=v2
	vect[2]=v3
End Function


Function tang( vertex#[3], vertex2#[3], vertex3#[3],texcoords#[2], texcoords2#[2], texcoords3#[2],polynormal#[3], tangent#[3], binormal#[3], normal#[3] )

Local txb#[3];
Local v1#[3],v2#[3]

VECTOR( vertex2[0] - vertex[0], texcoords2[0] - texcoords[0], texcoords2[1] - texcoords[1],v1 );
VECTOR( vertex3[0] - vertex[0], texcoords3[0] - texcoords[0], texcoords3[1] - texcoords[1],v2 );

crossProduct( v1, v2,txb );



If( Abs( txb[0] ) > EPSILON )
tangent[0]  = -txb[1] / txb[0];
binormal[0] = -txb[2] / txb[0];
EndIf




v1[0] = vertex2[1] - vertex[1];

v2[0] = vertex3[1] - vertex[1];


CrossProduct( v1, v2,txb );

If( Abs( txb[0] ) > EPSILON )
	tangent[1]  = -txb[1] / txb[0];
	binormal[1] = -txb[2] / txb[0];
EndIf

v1[0] = vertex2[2] - vertex[2];
v2[0] = vertex3[2] - vertex[2];


CrossProduct( v1, v2,txb);


If( Abs( txb[0] ) > EPSILON )

	tangent[2]  = -txb[1] / txb[0];
	binormal[2] = -txb[2] / txb[0];
EndIf


Normalize( tangent );

Normalize( binormal );


;// Make a normal based on the tangent And binormal b/c it may be different than the poly's
;// normal, this normal being computed here is better

CrossProduct( tangent, binormal,normal);
Normalize( normal );

;// Make tangent space vectors orthogonal by recomputing the binormal with the corrected 

;// tangent space normal.

CrossProduct( tangent, normal,biNormal);
Normalize( binormal );

If( vecDotProd( normal, polynormal ) < 0.0 )
	normal[0] = -normal[0];
	normal[1] = -normal[1];
	normal[2] = -normal[2]; 
EndIf

End Function






Function TriangleNormal#(v1#[3],v2#[3],v3#[3],o#[3])
;SubVector v1,v2,

ux#=VectorX()
uy#=VectorY()
uz#=VectorZ()
;SubVector Cx#,Cy#,Cz#,Bx#,By#,Bz#
vx#=VectorX()
vy#=VectorY()
vz#=VectorZ()
;CrossProduct vx#,vy#,vz#,ux#,uy#,uz#
;Normalize vectorx,vectory,vectorz
;Return Ax#*vectorx+Ay#*vectory+Az#*vectorz
End Function

Function VectorX#()
Return vectorx
End Function

Function VectorY#()
Return vectory
End Function

Function VectorZ#()
Return vectorz
End Function

Function VectorW#()
Return vectorw
End Function

Function SubVector(v1#[3],v2#[3],o#[3])
	o[0]=v1[0]-v2[0]
	o[1]=v1[1]-v2[1]
	o[2]=v1[2]=v2[2]
End Function

Function Normalize(v#[3])
If v[0]=0 And v[1]=0 And v[2]=0 Return
	m#=Magnitude(v)
	v[0]=v[0]/m#
	v[1]=v[1]/m#
	v[2]=v[2]/m#
End Function

Function CrossProduct(v1#[3],v2#[3],o#[3])
o[0]=v1[1]*v2[2]-v2[2]*v2[1]
o[1]=v1[2]*v2[0]-v1[0]*v2[2]
o[2]=v1[0]*v2[1]-v1[1]*v2[0]
End Function


Function Magnitude(v#[3]) 
	Return Sqr( (v[0]*v[0]) + (v[1]*v[1]) + (v[2]*v[2]) ) 
End Function


Function TriangleNX#(surf,tri_no)

v0=TriangleVertex(surf,tri_no,0)
v1=TriangleVertex(surf,tri_no,1)
v2=TriangleVertex(surf,tri_no,2)

ax#=VertexX#(surf,v1)-VertexX#(surf,v0)
ay#=VertexY#(surf,v1)-VertexY#(surf,v0)
az#=VertexZ#(surf,v1)-VertexZ#(surf,v0)

bx#=VertexX#(surf,v2)-VertexX#(surf,v1)
by#=VertexY#(surf,v2)-VertexY#(surf,v1)
bz#=VertexZ#(surf,v2)-VertexZ#(surf,v1)

nx#=(ay#*bz#)-(az#*by#)

Return nx#

End Function


Function TriangleNY#(surf,tri_no)

v0=TriangleVertex(surf,tri_no,0)
v1=TriangleVertex(surf,tri_no,1)
v2=TriangleVertex(surf,tri_no,2)

ax#=VertexX#(surf,v1)-VertexX#(surf,v0)
ay#=VertexY#(surf,v1)-VertexY#(surf,v0)
az#=VertexZ#(surf,v1)-VertexZ#(surf,v0)

bx#=VertexX#(surf,v2)-VertexX#(surf,v1)
by#=VertexY#(surf,v2)-VertexY#(surf,v1)
bz#=VertexZ#(surf,v2)-VertexZ#(surf,v1)

ny#=(az#*bx#)-(ax#*bz#)

Return ny#

End Function


Function TriangleNZ#(surf,tri_no)

v0=TriangleVertex(surf,tri_no,0)
v1=TriangleVertex(surf,tri_no,1)
v2=TriangleVertex(surf,tri_no,2)

ax#=VertexX#(surf,v1)-VertexX#(surf,v0)
ay#=VertexY#(surf,v1)-VertexY#(surf,v0)
az#=VertexZ#(surf,v1)-VertexZ#(surf,v0)

bx#=VertexX#(surf,v2)-VertexX#(surf,v1)
by#=VertexY#(surf,v2)-VertexY#(surf,v1)
bz#=VertexZ#(surf,v2)-VertexZ#(surf,v1)

nz#=(ax#*by#)-(ay#*bx#)

Return nz#

End Function
