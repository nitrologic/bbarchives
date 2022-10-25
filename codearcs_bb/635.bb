; ID: 635
; Author: Michael Reitzenstein
; Date: 2003-03-28 23:13:31
; Title: Addition Program
; Description: Adds two numbers together using just Or, And, Not, Shr, Shl

Global a[ 32 ]
Global b[ 32 ]
Global o[ 32 ]
Global num[ 2 ]
Global rem
Global result

num[ 1 ] = Input( "Number 1: " )
num[ 2 ] = Input( "Number 2: " )

For Loop = 1 To 32
	
	a[ Loop ] = ( ( num[ 1 ] And ( ( ( %1 Shl ( Loop ) ) Shr 1 ) ) ) Shl 1 ) Shr Loop
	b[ Loop ] = ( ( num[ 2 ] And ( ( ( %1 Shl ( Loop ) ) Shr 1 ) ) ) Shl 1 ) Shr Loop
	
Next

o[ 1 ] = ( a[ 1 ] Or b[ 1 ] ) And ( Not ( a[ 1 ] And b[ 1 ] ) )
rem = ( a[ 1 ] And b[ 1 ] )

For Loop = 2 To 32
	
	o[ Loop ] = Calc( Loop )
	
Next

For Loop = 1 To 32

	result = result Or ( ( o[ Loop ] Shl ( Loop ) ) Shr 1 )
	
Next

Print result
Input
End

Function Calc( Loop_Count )
	
	Local i[ 3 ]
	Local out
	
	i[ 1 ] = rem
	i[ 2 ] = a[ Loop_Count ]
	i[ 3 ] = b[ Loop_Count ]
	
	;Sorting Remainder
	i[ 0 ] = ( i[ 1 ] Or i[ 3 ] ) 
	i[ 3 ] = ( i[ 1 ] And i[ 3 ] )
	i[ 1 ] = i[ 0 ]
	
	i[ 0 ] = ( i[ 2 ] Or i[ 3 ] ) 
	i[ 3 ] = ( i[ 2 ] And i[ 3 ] )
	i[ 2 ] = i[ 0 ]
	
	i[ 0 ] = ( i[ 1 ] Or i[ 2 ] )
	i[ 2 ] = ( i[ 1 ] And i[ 2 ] )
	i[ 1 ] = i[ 0 ]
	
	out = ( ( Not i[ 1 ] ) And ( Not i[ 2 ] ) And i[ 3 ] )
	out = ( out ) Or ( ( Not ( i[ 1 ] And i[ 2 ] ) ) And ( i[ 1 ] Or i[ 2 ] ) ) And ( Not ( i[ 3 ] ) )
	out = ( out ) Or ( i[ 1 ] And i[ 2 ] And i[ 3 ] )
	
	rem = ( ( Not ( i[ 1 ] And i[ 2 ] ) ) And ( i[ 1 ] Or i[ 2 ] ) ) And i[ 3 ]
	rem = ( rem ) Or ( ( i[ 1 ] And i[ 2 ] ) And ( Not i[ 3 ] ) )
	rem = ( rem ) Or ( ( i[ 1 ] And i[ 2 ] And i[ 3 ] ) )

	Return out
	
End Function
