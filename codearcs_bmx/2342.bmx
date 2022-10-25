; ID: 2342
; Author: Warpy
; Date: 2008-10-21 17:30:22
; Title: Arbitrary precision integers
; Description: handle integers as large as you like, bigger than 32-bit

'Arbitrary precision integers
'the bignum type can store a number as big as your computer has memory
'the basic mathematical operations are defined: add, sub, mul, div and pow
'you can create a bignum either by using the fromint() method if it's small enough,
'or by using the fromstring() method
'you can get a string representation of a bignum by using its tostring() method
'or you can get an Int representation if it's small enough by using the toint() method

Type bignum
	Field sign
	Field digits:Byte[]
	
	Function Create:bignum( digits:Byte[], sign=1)
		b:bignum = New bignum
		b.sign = sign
		i=Len(digits)
		While i>0 And digits[i-1]=0
			i:-1
		Wend
		If l<>Len(digits) digits=digits[..i]
		b.digits = digits
		Return b
	End Function
	
	Function fromint:bignum( n )
		If n<0
			n=-n
			sign=-1
		Else
			sign=1
		EndIf
		acc=n
		p=0
		While acc>0
			p:+1
			acc:/10
		Wend
		Local digits:Byte[p]
		For i=0 To p-1
			digits[i]=n Mod 10
			n:/10
		Next
		Return bignum.Create(digits,sign)
	End Function
	
	Function fromstring:bignum( n$ )
		If n[0]=45
			sign=-1
			n=n[1..]
		Else
			sign=1
		EndIf
		l=Len(n)
		Local digits:Byte[l]
		For i=0 To l-1
			digits[l-i-1] = Int( Chr(n[i]) )
		Next
		Return bignum.Create( digits, sign )
	End Function
	
	Method tostring$()
		If Len(digits)=0 Return "0"
		txt$=""
		For i=0 To Len(digits)-1
			txt=String(digits[i])+txt
		Next
		If sign=-1 txt="-"+txt
		Return txt
	End Method
	
	Method toint() 'clearly this will break when you get past 2^32-1
		n=0
		For i=Len(digits)-1 To 0 Step -1
			n:*10
			n:+digits[i]
		Next
		Return n
	End Method
End Type

Function biggest( a, b )
	If a>b Return a Else Return b
End Function


Function lt( b1:bignum, b2:bignum )
	If Len(b1.digits) < Len(b2.digits) Return 1
	If Len(b1.digits) > Len(b2.digits) Return 0
	i=Len(b1.digits) - 1
	While i>=0 And b1.digits[i]=b2.digits[i]
		i:-1
	Wend
	If i>=0
		If b1.digits[i] < b2.digits[i] Return 1 Else Return 0
	Else
		Return 0
	EndIf
End Function

Function gt( b1:bignum, b2:bignum )
	Return lt( b2, b1 )
End Function

Function eq( b1:bignum, b2:bignum )
	If Len(b1.digits)<>Len(b2.digits) Return 0
	i=Len(b1.digits)-1
	While i>=0 And b1.digits[i]=b2.digits[i]
		i:-1
	Wend
	If i>=0 Return 0 Else Return 1
End Function

Function lteq( b1:bignum, b2:bignum )
	Return lt(b1, b2) Or eq(b1,b2)
End Function

Function gteq( b1:bignum, b2:bignum )
	Return gt(b1, b2) Or eq(b1,b2)
End Function

Function shift( arr:Byte[], n )
	For i=Len(arr)-n-1 To 0 Step -1
		arr[i+n]=arr[i]
		arr[i]=0
	Next
End Function

Function add:bignum( b1:bignum, b2:bignum )
	If b1.sign=-1 
		b:bignum=sub( bignum.Create( b1.digits, 1 ), b2 )
		b.sign=-b.sign
		Return b
	EndIf
	If b2.sign=-1
		b:bignum=sub( b1, bignum.Create( b2.digits, 1 ) )
		b.sign=-b.sign
		Return b
	EndIf
	l1=Len(b1.digits)
	l2=Len(b2.digits)
	l = biggest( l1, l2 ) + 1
	Local digits:Byte[ l ]
	r=0
	For n=0 To l-1
		If n<l1 
			r:+b1.digits[n]
		EndIf
		If n<l2 
			r:+b2.digits[n]
		EndIf
		m = r Mod 10
		r = (r-m)/10
		digits[n]=m
	Next
	Return bignum.Create(digits)
End Function

Function sub:bignum( b1:bignum, b2:bignum )
	If b1.sign=-1
		b:bignum=add( bignum.Create( b1.digits, 1 ), b2 )
		b.sign=-b.sign
		Return b
	EndIf
	If b2.sign=-1
		b:bignum=add( b1, bignum.Create( b2.digits, 1 ) )
		b.sign=-b.sign
		Return b
	EndIf
	
	l1=Len(b1.digits)
	l2=Len(b2.digits)
	
	If l2>l1
		b:bignum=sub( b2, b1 )
		b.sign=-b.sign
		Return b
	EndIf
	
	l = biggest( l1, l2 )
	Local digits:Byte[ l ]
	r=0
	For n=0 To l-1
		If n<l1 
			r:+b1.digits[n]
		EndIf
		If n<l2 
			r:-b2.digits[n]
		EndIf
		If r<0
			m=10+r
			r=-1
		Else
			m=r
			r=0
		EndIf
		digits[n]=m
	Next
	b:bignum = bignum.Create(digits)
	If r=-1
		b.digits[0]=9-b.digits[0]
		For i=1 To l-1
			b.digits[i]=10-b.digits[i]
		Next
		b.sign=-1
	EndIf
	Return b
End Function

Function mul:bignum( b1:bignum, b2:bignum )
	l1=Len(b1.digits)
	l2=Len(b2.digits)
	l=l1+l2
	Local digits:Byte[]
	
	out:bignum=bignum.Create([0:Byte])
	For i=0 To l2-1
		digits = New Byte[ l+1 ]
		r=0
		For ii=0 To l1-1
			n=b2.digits[i] * b1.digits[ii] + r
			m = n Mod 10
			r = (n - m) / 10
			digits[ii + i] = m
		Next
		digits[i + l1] = r
		b:bignum = bignum.Create( digits )
		out=add( out, b )
	Next
	out.sign = b1.sign*b2.sign
	Return out
End Function

Function div:bignum( b1:bignum, b2:bignum )
	Local digits:Byte[ Len(b1.digits) ]
	
	l1=Len(b1.digits)
	l2=Len(b2.digits)
	i=l1-1
	topi=l1-1
	Local rdigits:Byte[]=New Byte[l2+1]
	While i>=0
		r:bignum=bignum.Create(rdigits)
		While i>=0 And lt(r,b2)
			shift(rdigits,1)
			rdigits[0]=b1.digits[i]
			r=bignum.Create(rdigits)
			i:-1
			topi:-1
		Wend
		If i>=-1
			n=-1
			Local oldr:bignum
			While r.sign=1
				oldr=r
				r=sub(r, b2 )
				n:+1
			Wend
			digits[i+1]=n
			For tmp=0 To l2
				rdigits[tmp]=0
			Next
			For tmp=0 To Len(oldr.digits)-1
				rdigits[tmp]=oldr.digits[tmp]
			Next
			topi=Len(rdigits)-1
		EndIf
	Wend
	
	b:bignum=bignum.Create(digits, b1.sign * b2.sign )
	Return b
End Function

Function smallpow:bignum(b1:bignum, p )
	If p<0 Return bignum.fromint(0)
	If p=0 Return bignum.fromint(1)
	out:bignum=bignum.fromint(1)
	For n=1 To p
		out=mul( out, b1 )
	Next
	Return out
End Function

Function pow:bignum( b1:bignum, b2:bignum )
	If b2.sign=-1 Return bignum.fromint(0)
	If Len(b2.digits)=1
		Return smallpow( b1, b2.digits[0] )
	Else
		out:bignum=bignum.fromint(1)
		For n=Len(b2.digits)-1 To  0 Step -1
			out=mul( smallpow(out,10), smallpow(b1,b2.digits[n]) )
		Next
		Return out
	EndIf
End Function


'examples

b1:bignum=bignum.fromstring("232334")
b2:bignum=bignum.fromstring("24344")
Print "b1: "+b1.tostring()
Print "b2: "+b2.tostring()
Print "b1 + b2: "+add( b1, b2 ).tostring()
Print "b1 - b2: "+sub( b1, b2 ).tostring()
Print "b1 * b2: "+mul( b1, b2 ).tostring()
Print "b1 / b2: "+div( b1, b2 ).tostring()
b3:bignum = bignum.fromint(25)
b4:bignum = bignum.fromint(23)
Print "b3: "+b3.tostring()
Print "b4: "+b4.tostring()
Print "b3 ^ b4: "+pow( b3, b4 ).tostring()
End

b1:bignum=bignum.Create([1:Byte])
two:bignum=bignum.Create([2:Byte])
For i=0 To 100
	Print "2*"+i+" = "+b1.tostring()
	b1=mul( b1, two )
Next

Function factorial:bignum( n:bignum )
	Global one:bignum=bignum.fromint(1)
	If eq( n, one ) Return n
	Return mul( n, factorial( sub( n, one ) ) )
End Function

For n=1 To 100 Step 3
	bign:bignum=bignum.fromint(n)
	Print n+"! = "+factorial( bign ).tostring()
Next
