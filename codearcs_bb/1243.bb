; ID: 1243
; Author: Techlord
; Date: 2004-12-22 18:08:48
; Title: Multi-Dimensional Arrays using a Single Array
; Description: Convert multiple indexes into a linear index for use with Blitz Single Arrays (ie: mytype.alien[100])

;bscvmArray an excerpt from the BlitzScript3D Engine
; @ http://www.blitzbasic.com/Community/posts.php?topic=39622
;Supports up to 4 dimensions using Row Major Ordering Formula 
; @ webster.cs.ucr.edu/AoA/Windows/HTML/Arraysa2.html
;There is a generic formula that will compute the offset into memory For an array 
;with any number of dimensions, however, you'll rarely use more than four. 

Global blitzscriptVar[65535] ;a single dimension array

Type bscvmArray
	Field name$
	Field i_size
	Field j_size
	Field k_size
	Field l_size
	Field head
	Field tail
End Type

Function bscvmArrayCreate.bscvmArray(i,j=0,k=0,l=0)
	this.bscvmArray = New bscvmArray
	this\i_size=i+1
	this\j_size=j+1
	this\k_size=k+1
	this\l_size=l+1
	this\head=0
	this\tail=this\head + this\i_size * this\j_size * this\k_size * this\l_size
	Return this
End Function

Function bscvmArrayGet(this.bscvmArray,i_index,j_index=0,k_index=0,l_index=0)
	Return blitzscriptVar[this\head+(((i_index * this\j_size+j_index) * this\k_size+k_index) * this\l_size+l_index)]
End Function

Function bscvmArraySet(this.bscvmArray,i_index,j_index=0,k_index=0,l_index=0,value$=0)
	blitzscriptVar[this\head+(((i_index * this\j_size+j_index) * this\k_size+k_index) * this\l_size+l_index)]=value
End Function

Function bscvmArrayLinearIndexGet(this.bscvmArray,i_index,j_index=0,k_index=0,l_index=0)
	Return this\head+(((i_index * this\j_size+j_index) * this\k_size+k_index) * this\l_size+l_index)
End Function

Function bscvmArrayTest()

	this.bscvmArray=bscvmArrayCreate(3,3,3,3)
	
	For i =  0 To 3
	For j =  0 To 3
	For k =  0 To 3
	For l =  0 To 3
		
	DebugLog(i+","+j+","+k+","+l+"= "+m+" /  "+bscvmArrayLinearIndexGet(this,i,j,k,l))
	m=m+1
	
	Next
	Next
	Next
	Next
	DebugLog "tail="+this\tail
	
End Function

bscvmArrayTest()
