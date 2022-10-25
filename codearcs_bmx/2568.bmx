; ID: 2568
; Author: Matt Merkulov
; Date: 2009-08-25 03:47:07
; Title: Tetris
; Description: 993 symbols

Global W[21,23],F[7,4,4,4],L[7];For S$=Eachin "ABEF,AEFJEFBC,IEFBABFG,BFJNEFGH,AEFGCBFJEFGKIJFB,EFGCBFJKIEFGABFJ,EFBGBFGJEFGJBEFJ".Split(",");L[M]=Len(S)/4-1;For I=0 To L[M];For J=0 To 3;A=S[I*4+J]-65;F[M,I,A&3,A Shr 2]=1;Next;Next;M:+1;Next;Graphics 800,600;Y=19;Repeat;Cls;For I=0 To 20;W[1,I]=1;W[12,I]=1;W[I,20]=1;For J=1 To 12;If W[J,I] Then DrawRect J*8,I*8,8,8
Next;Next;For I=0 To 3;For J=0 To 3;If F[N,R,I,J] Then DrawRect (X+I)*8,(Y+J)*8,8,8
Next;Next;Flip;A=X+KeyHit(39)-KeyHit(37);B=Y+KeyDown(40);Q=(R+KeyHit(87)-KeyHit(81))&L[N];IF C(A,B,N,Q)=0 Then X=A;Y=B;R=Q
M=Millisecs();If M>T Then
T=M+999;If C(X,Y+1,N,R) Then
If Y=0 Then End
For I=0 To 3;For J=0 To 3;If F[N,R,I,J] Then W[X+I,Y+J]=1
Next;Next;D=0;For I=19 To 0 Step -1;Q=0;For J=2 To 11;If W[J,I] Then Q:+1
W[J,I+D]=W[J,I];Next;IF Q>9 Then D:+1
Next;X=5;Y=-1;N=Rand(0,6);EndIf;Y:+1;End If;Until KeyHit(27);Function C(X,Y,N,R);For I=0 To 3;For J=0 To 3;If F(N,R,I,J)*W[I+X,J+Y] Then Return 1
Next;Next;EndFunction
