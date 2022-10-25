; ID: 3066
; Author: Pineapple
; Date: 2013-08-26 14:24:52
; Title: Top-down merge sort for arrays
; Description: Quickly sort an array of objects

' 	--+-----------------------------------------------------------------------------------------+--
'	  |   This code was originally written by Sophie Kirschner (sophiek@pineapplemachine.com)   |  
' 	  | It is released as public domain. Please don't interpret that as liberty to claim credit |  
' 	  |   that isn't yours, or to sell this code when it could otherwise be obtained for free   |  
'	  |                because that would be a really shitty thing of you to do.                |
' 	--+-----------------------------------------------------------------------------------------+--

SuperStrict



' Example Code

Rem

Framework brl.standardio
Import brl.random

Const numnodes%=10

SeedRnd MilliSecs()

Type node
	Field value%=Rand(0,99)
	Method compare%(obj:Object)
		If value>node(obj).value Return 1
		Return -1
	End Method
End Type

Local nodearray:node[numnodes]
For Local i%=0 Until numnodes
	nodearray[i]=New node
Next

Print "~nArray before sorting:"
For Local i%=0 Until numnodes
	Print nodearray[i].value
Next

MergeSortArray(nodearray)

Print "~nArray after sorting:"
For Local i%=0 Until numnodes
	Print nodearray[i].value
Next

EndRem



' Top-down merge sort
' algorithm taken from: http://en.wikipedia.org/wiki/Merge_sort#Top-down_implementation

Function MergeSortArray(array:Object[],ascending%=True,comparefunc(o1:Object,o2:Object)=_Array_CompareObjects)
	Local buffer:Object[]=New Object[array.length]
	_MergeSortAtoA array,buffer,0,array.length,comparefunc,ascending
End Function

Function _MergeSortAtoA(a:Object[],b:Object[],ll%,rr%,comparefunc(o1:Object,o2:Object),ascending%)
	If rr-ll>=2 Then
		Local mm%=(ll+rr)/2
		_MergeSortAtoB a,b,ll,mm,comparefunc,ascending
		_MergeSortAtoB a,b,mm,rr,comparefunc,ascending
		_MergeSortMerge b,a,ll,mm,rr,comparefunc,ascending
	EndIf
End Function

Function _MergeSortAtoB(a:Object[],b:Object[],ll%,rr%,comparefunc(o1:Object,o2:Object),ascending%)
	If rr-ll>=2 Then
		Local mm%=(ll+rr)/2
		_MergeSortAtoA a,b,ll,mm,comparefunc,ascending
		_MergeSortAtoA a,b,mm,rr,comparefunc,ascending
		_MergeSortMerge a,b,ll,mm,rr,comparefunc,ascending
	ElseIf rr-ll=1
		b[ll]=a[ll]
	EndIf
End Function

Function _MergeSortMerge(a:Object[],b:Object[],ll%,mm%,rr%,comparefunc(o1:Object,o2:Object),ascending%)
	Local l%=ll,r%=mm
	Local comparetarg%=1-ascending*2
	For Local o%=ll Until rr
		If r>=rr Or ((l<mm) And (comparefunc(a[l],a[r])=comparetarg)) Then ' a[l]<=a[r]
			b[o]=a[l]
			l:+1
		Else
			b[o]=a[r]
			r:+1
		EndIf
	Next
End Function

Function _Array_CompareObjects%(o1:Object,o2:Object)
	Return o1.Compare(o2)
End Function
