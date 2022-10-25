; ID: 2900
; Author: TAS
; Date: 2011-11-04 13:50:40
; Title: Bank Arrays
; Description: Access banks just like multi-dimension arrays

;Thomas A Stevenson
;"TAS"
;11-4-2011
;war-game-programming.com

;Banks can be used in place of arrays with the following advantages;
;Banks can be passed to functions by reference
;Functions can be designed to work with any bank of any size,
;e.g. you need a separate Function for every array you may want to sort but only one 
;for any number and size of banks.
;Banks can be local and temporary.
;Functions can determine/query the size of passed banks
;Banks can be resized without losing data

;The one advantage that arrays have is their support for multi-dimensions
;The following code demonstrates how banks can be access just like multi-dimension arrays.

;So instead of
;   Dim MyData(5,5,5)
;   MyData(1,2,3)=x
;   If MyData(1,2,3)>y Then ......

;With bank objects we would write
;   Mydata.bankobj=bank_Create(4,5,5,5)
;   bankW(Mydata,x,1,2,3)
;   If bankR(MyData,1,2,3)>y Then ......

;Objects are one way of handling the needed parameter values for
;multi dimension bank arrays.  The first x number of bytes of the 
;bank could be used instead which would be more compact
;and provide slightly faster processing.
;But objects provide convenient built methods for tracking and using the banks.
;For maximum speed the bank object structure and calls can be bypassed/un-rolled for
;time critical tasks.
;The design also supports lowest index values other than 0, positive or negative.
;The code could be shorten and simplified by assuming a single data type (e.g. 4 byte integer)
;and a base value and by eliminating error checking.

Type bankobj
	Field ptr		;bank handle
	Field dim1	;number of elements for d1mension 1
	Field dim2	; "" 2
	Field dim3	; "" 3
	Field e1	;lowest index (base) value of dim 1, default=1
	Field e2	;ditto
	Field e3	;ditto
	Field elmSize	;in bytes, 1,2,4 allowed
End Type

;demonstrate we can fill and read a bank properly
b1.bankobj=bank_create(2,3,3,3)
For k=1 To 3	;loop most significant to least significant index
	For j=1 To 3
		For i=1 To 3
			bankW(b1,i+j*10+k*100,i,j,k)
		Next
	Next
Next

n=0
For k=1 To 3		;for proper readout must loop from highest index to lowest
	For j=1 To 3
		For i=1 To 3
			n=n+1
			;DebugLog Str(PeekShort(b1\ptr,2*(n-1)))+": "+bankR(b1,i,j,k)
			DebugLog Str(k)+" "+Str(j)+" "+Str(i)+" : "+bankR(b1,i,j,k)
		Next
	Next
Next
Stop


Function bank_Create.bankobj(elementsize,m1,m2,m3=0,e1=0,e2=0,e3=0)
	;create and return bank object
	;m1,m2,m3 is the number of elements rather than the highest alowed index
	;e1,e2,e3 index number of for first element of dimension 1,2,3
	Select elementsize
	Case 1,2,4
	Default
		Stop:elementsize=4
	End Select
	If m1=0 Then Stop: m1=1
	bytes=elementsize*(m1+1-e1)
	If m2>0 Then bytes=bytes*(m2+1-e1) 
	If m3>0 Then bytes=bytes*(m3+1-e1)

	;the descriptor information could be appended to the bank memory itself
	;to eliminate the two-part data structure (object + bank) but to what advantage?
	bnk.bankobj=New bankobj
	bnk\ptr=CreateBank(bytes)
	bnk\Dim1=m1
	bnk\Dim2=m2
	bnk\Dim3=m3
	bnk\e1=e1	;Option base
	bnk\e2=e2	;Option base
	bnk\e3=e3	;Option base
	bnk\elmSize=elementsize
	Return bnk
End Function

Function Bank_delete(bnk.bankobj)
	FreeBank bnk\ptr
	Delete bnk
End Function

Function Bank_fill(bnk.bankobj,n%)
	If bnk\elmSize=1
		For i=0 To BankSize(bnk\ptr)-1
			PokeByte bnk\ptr,i,n
		Next
	ElseIf bnk\elmSize=2
		For i=0 To BankSize(bnk\ptr)/2-1
			PokeShort bnk\ptr,i,n
		Next
	Else	;size=4
		For i=0 To BankSize(bnk\ptr)/4-1
			PokeInt bnk\ptr,i,n
		Next
	EndIf
End Function


Function bankW(bnk.bankobj,v,I1,I2,I3=0)
	;add value V to bank at index location i1,i2,3
	;bound checks
	If i1<bnk\e1 Then Stop:	Return
	;highest offset is 1 less than row size in elements (not bytes)
	If (i1-bnk\e1)>bnk\dim1 Then Stop: Return 
	offset=(i1-bnk\e1)
	If bnk\dim2 Then 
		If i2<bnk\e2 Then Stop: Return
		If (i2-bnk\e2)>bnk\dim2 Then Stop: Return
		offset=offset+bnk\dim1*(i2-bnk\e2)
	EndIf
	If bnk\dim3 Then 
		If i3<bnk\e3 Then Stop: Return
		If (i3-bnk\e3)>bnk\dim3 Then Stop: Return
		offset=offset+bnk\dim1*bnk\dim2*(i3-bnk\e3)
	EndIf
	offset=bnk\elmsize*offset
	If bnk\elmSize=1
		PokeByte bnk\ptr,offset,v
	ElseIf bnk\elmSize=2
		PokeShort bnk\ptr,offset,v
	Else	;size=4
		PokeInt bnk\ptr,offset,v
	EndIf
End Function

Function bankR(bnk.bankobj,I1,I2,I3=0)
	;reads the bank value at index location i1,i2,i3
	;bound checks
	If i1<bnk\e1 Then Stop:	Return
	;highest offset is 1 less than row size in elements (not bytes)
	If (i1-bnk\e1)>bnk\dim1 Then Stop: Return 
	offset=(i1-bnk\e1)
	If bnk\dim2 Then 
		If i2<bnk\e2 Then Stop: Return
		If (i2-bnk\e2)>bnk\dim2 Then Stop: Return
		offset=offset+bnk\dim1*(i2-bnk\e2)
	EndIf
	If bnk\dim3 Then 
		If i3<bnk\e3 Then Stop: Return
		If (i3-bnk\e3)>bnk\dim3 Then Stop: Return
		offset=offset+bnk\dim1*bnk\dim2*(i3-bnk\e3)
	EndIf
	offset=bnk\elmsize*offset

	If bnk\elmSize=1
		Return PeekByte(bnk\ptr,offset)
	ElseIf bnk\elmSize=2
		Return PeekShort(bnk\ptr,offset)
	Else	;size=4
		Return PeekInt(bnk\ptr,offset)
	EndIf
End Function
