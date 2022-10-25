; ID: 2767
; Author: Matt Merkulov
; Date: 2010-09-15 05:36:46
; Title: BlitzMax benchmark
; Description: Testing various commands of BlitzMax

SuperStrict
SeedRnd( Millisecs() )

Type Type1
	Field IntField:Int
	Field FloatField:Int
	Field StringField:String
	
	
	
	Method EmptyMethod()
	End Method
	
	
	
	Method ExistingMethod()
		IntField :+ 1
	End Method
	
	
	
	Method IntMethod:Int()
		Return IntField
	End Method
	
	
	
	Method FloatMethod:Float()
		Return FloatField
	End Method
	
	
	
	Method StringMethod:String()
		Return StringField
	End Method
	
	
	
	Method Method1( Value1:Int )
		Value1 = 0
	End Method
	
	
	
	Method Method2( Value1:Int, Value2:Int )
		Value1 = Value2
	End Method
	
	
	
	Method Method3( Value1:Int, Value2:Int, Value3:Int )
		Value1 = Value2 + Value3
	End Method
	
	
	
	Method Method1Var( Value1:Int Var )
		Value1 = 0
	End Method
	
	
	
	Method Method2Var( Value1:Int Var, Value2:Int Var )
		Value1 = 1
		Value2 = 2
	End Method
	
	
	
	Method Method3Var( Value1:Int Var, Value2:Int Var, Value3:Int Var )
		Value1 = 1
		Value2 = 2
		Value3 = 3
	End Method
End Type



Type Type2 Extends Type1
	Method ExistingMethod()
		IntField :+ 2
	End Method
End Type



Const Cycles:Int = 10000000
Const Passes:Int = 10
Const Size:Int = 10
Const TestsQuantity:Int = 86

Local TestTime:Int[] = New Int[ TestsQuantity ]
Local TestName:String[] = New String[ TestsQuantity ]

Local IntArray:Int[] = New Int[ Size ]
Local FloatArray:Float[] = New Float[ Size ]
Local StringArray:String[] = New String[ Size ]

Local File:TStream = WriteFile( "output.txt" )

Graphics 800, 600

For Local Pass:Int = 0 Until Passes
	Local TestNumArray:Int[] = New Int[ TestsQuantity ]
	For Local N:Int = 0 Until TestsQuantity
		TestNumArray[ N ] = N
	Next
	
	For Local N:Int = 0 Until TestsQuantity - 1
		TestNumArray[ N ] = N
		Local M:Int = Rand( N, TestsQuantity - 1 )
		Local Z:Int = TestNumArray[ M ]
		TestNumArray[ M ] = TestNumArray[ N ]
		TestNumArray[ N ] = Z
	Next	
	
	For Local ArrayNum:Int = 0 Until TestsQuantity
		Local IntVar1:Int
		Global IntVar2:Int = Rand( -1000000000, 1000000000 )
		Global IntVar3:Int = Rand( -1000000000, 1000000000 )
		
		Local FloatVar1:Float
		Global FloatVar2:Float = Rnd( -1000000000.0, 1000000000.0 )
		Global FloatVar3:Float = Rnd( -1000000000.0, 1000000000.0 )
		
		Local StringVar1:String
		Global StringVar2:String = RndString( Size )
		Global StringVar3:String = RndString( Size )
		For Local N:Int = 0 Until Size
			StringArray[ N ] = RndString( Size )
		Next
		
		Local NumString:String = ""
		For Local N:Int = 1 To 9
			NumString :+ Chr( 48 + Rand( 0, 9 ) )
		Next
		
		Local BitNum:Int = Rand( 0, 31 )
		Local ElementNum:Int = Rand( 0, Size - 1 )
		Local ElementNum1:Int = Rand( 0, Size / 2 - 1 )
		Local ElementNum2:Int = ElementNum1 + Rand( 0, Size / 2 - 1 )
		Local SubString1:String = StringVar2[ ElementNum1 + 5..ElementNum1 + 8 ]
		Local SubString2:String = RndString( 3 )
		Local LogValue:Float = Rnd( 2.0, 1000000000.0 )
		Local SinValue:Float = Rnd( -1.0, 1.0 )
		Local Power:Float = Rnd( -20, 20 )
		Local Symbol:String = Chr( Rand( 32, 127 ) )
		Local SymbolNum:Int = Rand( 32, 127 )
		
		Local Object1:Type1 = New Type1
		Local Object2:Type2 = New Type2

		Local Time:Int = Millisecs()
		Local TestNum:Int = TestNumArray[ ArrayNum ]
		Select TestNum
			Case 0
				For Local N:Int = 0 Until Cycles
				Next
				TestName[ TestNum ] = "Empty cycle"
			Case 1
				For Local N:Int = 0 Until Cycles
					IntVar1 = IntVar2
				Next
				TestName[ TestNum ] = "IntVar1 = IntVar2"
			Case 2
				For Local N:Int = 0 Until Cycles
					FloatVar1 = FloatVar2
				Next
				TestName[ TestNum ] = "FloatVar1 = FloatVar2"
			Case 3
				For Local N:Int = 0 Until Cycles
					StringVar1 = StringVar2
				Next
				TestName[ TestNum ] = "StringVar1 = StringVar2"
			Case 4
				For Local N:Int = 0 Until Cycles
					If IntVar1 = IntVar2 Then IntVar1 = IntVar2
				Next
				TestName[ TestNum ] = "If IntVar1 = IntVar2 Then"
			Case 5
				For Local N:Int = 0 Until Cycles
					If FloatVar1 = FloatVar2 Then IntVar1 = IntVar2
				Next
				TestName[ TestNum ] = "If FloatVar1 = FloatVar2 Then"
			Case 6
				For Local N:Int = 0 Until Cycles
					If StringVar1 = StringVar2 Then IntVar1 = IntVar2
				Next
				TestName[ TestNum ] = "If StringVar1 = StringVar2 Then"
			Case 7
				For Local N:Int = 0 Until Cycles
					If IntVar1 > IntVar2 Then IntVar1 = IntVar2
				Next
				TestName[ TestNum ] = "If IntVar1 > IntVar2 Then"
			Case 8
				For Local N:Int = 0 Until Cycles
					If FloatVar1 > FloatVar2 Then IntVar1 = IntVar2
				Next
				TestName[ TestNum ] = "If FloatVar1 > FloatVar2 Then"
			Case 9
				For Local N:Int = 0 Until Cycles
					IntVar1 = IntArray[ ElementNum ]
				Next
				TestName[ TestNum ] = "IntVar1 = IntArray[ ElementNum ]"
			Case 10
				For Local N:Int = 0 Until Cycles
					FloatVar1 = FloatArray[ ElementNum ]
				Next
				TestName[ TestNum ] = "FloatVar1 = FloatArray[ ElementNum ]"
			Case 11
				For Local N:Int = 0 Until Cycles
					StringVar1 = StringArray[ ElementNum ]
				Next
				TestName[ TestNum ] = "StringVar1 = StringArray[ ElementNum ]"
			Case 12
				For Local N:Int = 0 Until Cycles
					IntVar1 = IntFunction()
				Next
				TestName[ TestNum ] = "IntVar1 = IntFunction()"
			Case 13
				For Local N:Int = 0 Until Cycles
					FloatVar1 = FloatFunction()
				Next
				TestName[ TestNum ] = "FloatVar1 = FloatFunction()"
			Case 14
				For Local N:Int = 0 Until Cycles
					StringVar1 = StringFunction()
				Next
				TestName[ TestNum ] = "StringVar1 = StringFunction()"
			Case 15
				For Local N:Int = 0 Until Cycles
					Function1( IntVar1 )
				Next
				TestName[ TestNum ] = "Function1( IntVar1 )"
			Case 16
				For Local N:Int = 0 Until Cycles
					Function2( IntVar1, IntVar2 )
				Next
				TestName[ TestNum ] = "Function1( IntVar1, IntVar2 )"
			Case 17
				For Local N:Int = 0 Until Cycles
					Function3( IntVar1, IntVar2, IntVar3 )
				Next
				TestName[ TestNum ] = "Function3( IntVar1, IntVar2, IntVar3 )"
			Case 18
				For Local N:Int = 0 Until Cycles
					IntVar1 = FloatVar2
				Next
				TestName[ TestNum ] = "IntVar1 = FloatVar2"
			Case 19
				For Local N:Int = 0 Until Cycles
					FloatVar1 = IntVar2
				Next
				TestName[ TestNum ] = "FloatVar1 = IntVar2"
			Case 20
				For Local N:Int = 0 Until Cycles
					IntVar1 = NumString.ToInt()
				Next
				TestName[ TestNum ] = "IntVar1 = NumString"
			Case 21
				For Local N:Int = 0 Until Cycles
					FloatVar1 = NumString.ToInt()
				Next
				TestName[ TestNum ] = "FloatVar1 = NumString"
			Case 22
				For Local N:Int = 0 Until Cycles
					StringVar1 = IntVar2
				Next
				TestName[ TestNum ] = "StingVar1 = IntVar2"
			Case 23
				For Local N:Int = 0 Until Cycles
					StringVar1 = FloatVar2
				Next
				TestName[ TestNum ] = "StringVar1 = FloatVar2"
			Case 24
				For Local N:Int = 0 Until Cycles
					IntVar1 = IntVar2 + IntVar3
				Next
				TestName[ TestNum ] = "IntVar1 = IntVar2 + IntVar3"
			Case 25
				For Local N:Int = 0 Until Cycles
					FloatVar1 = FloatVar2 + FloatVar3
				Next
				TestName[ TestNum ] = "FloatVar1 = FloatVar2 + FloatVar3"
			Case 26
				For Local N:Int = 0 Until Cycles
					IntVar1 = IntVar2 - IntVar3
				Next
				TestName[ TestNum ] = "IntVar1 = IntVar2 - IntVar3"
			Case 27
				For Local N:Int = 0 Until Cycles
					FloatVar1 = FloatVar2 - FloatVar3
				Next
				TestName[ TestNum ] = "FloatVar1 = FloatVar2 - FloatVar3"
			Case 28
				For Local N:Int = 0 Until Cycles
					IntVar1 = IntVar2 * IntVar3
				Next
				TestName[ TestNum ] = "IntVar1 = IntVar2 * IntVar3"
			Case 29
				For Local N:Int = 0 Until Cycles
					FloatVar1 = FloatVar2 * FloatVar3
				Next
				TestName[ TestNum ] = "FloatVar1 = FloatVar2 * FloatVar3"
			Case 30
				For Local N:Int = 0 Until Cycles
					IntVar1 = IntVar2 / IntVar3
				Next
				TestName[ TestNum ] = "IntVar1 = IntVar2 / IntVar3"
			Case 31
				For Local N:Int = 0 Until Cycles
					FloatVar1 = FloatVar2 / FloatVar3
				Next
				TestName[ TestNum ] = "FloatVar1 = FloatVar2 / FloatVar3"
			Case 32
				For Local N:Int = 0 Until Cycles
					IntVar1 = IntVar2 ^ IntVar3
				Next
				TestName[ TestNum ] = "IntVar1 = IntVar2 ^ Power"
			Case 33
				For Local N:Int = 0 Until Cycles
					FloatVar1 = FloatVar2 ^ FloatVar3
				Next
				TestName[ TestNum ] = "FloatVar1 = FloatVar2 ^ Power"
			Case 34
				For Local N:Int = 0 Until Cycles
					IntVar1 = Abs( IntVar2 )
				Next
				TestName[ TestNum ] = "IntVar1 = Abs( IntVar2 )"
			Case 35
				For Local N:Int = 0 Until Cycles
					FloatVar1 = Abs( FloatVar2 )
				Next
				TestName[ TestNum ] = "FloatVar1 = Abs( FloatVar2 )"
			Case 36
				For Local N:Int = 0 Until Cycles
					IntVar1 = Sgn( IntVar2 )
				Next
				TestName[ TestNum ] = "IntVar1 = Sgn( IntVar2 )"
			Case 37
				For Local N:Int = 0 Until Cycles
					FloatVar1 = Sgn( FloatVar2 )
				Next
				TestName[ TestNum ] = "FloatVar1 = Sgn( FloatVar2 )"
			Case 38
				For Local N:Int = 0 Until Cycles
					IntVar1 = Rand( IntVar2 )
				Next
				TestName[ TestNum ] = "IntVar1 = Rand( IntVar2 )"
			Case 39
				For Local N:Int = 0 Until Cycles
					FloatVar1 = Rnd( FloatVar2 )
				Next
				TestName[ TestNum ] = "FloatVar1 = Rnd( FloatVar2 )"
			Case 40
				For Local N:Int = 0 Until Cycles
					IntVar1 = Int( FloatVar2 )
				Next
				TestName[ TestNum ] = "IntVar1 = Int( FloatVar2 )"
			Case 41
				For Local N:Int = 0 Until Cycles
					IntVar1 = Floor( FloatVar2 )
				Next
				TestName[ TestNum ] = "IntVar1 = Floot( FloatVar2 )"
			Case 42
				For Local N:Int = 0 Until Cycles
					IntVar1 = Ceil( FloatVar2 )
				Next
				TestName[ TestNum ] = "IntVar1 = Ceil( FloatVar2 )"
			Case 43
				For Local N:Int = 0 Until Cycles
					IntVar1 = IntVar2 And IntVar3
				Next
				TestName[ TestNum ] = "IntVar1 = IntVar2 And IntVar3"
			Case 44
				For Local N:Int = 0 Until Cycles
					IntVar1 = IntVar2 Or IntVar3
				Next
				TestName[ TestNum ] = "IntVar1 = IntVar2 Or IntVar3"
			Case 46
				For Local N:Int = 0 Until Cycles
					IntVar1 = Not( IntVar2 )
				Next
				TestName[ TestNum ] = "IntVar1 = Not( IntVar2 )"
			Case 47
				For Local N:Int = 0 Until Cycles
					IntVar1 = IntVar2 & IntVar3
				Next
				TestName[ TestNum ] = "IntVar1 = IntVar2 & IntVar3"
			Case 48
				For Local N:Int = 0 Until Cycles
					IntVar1 = IntVar2 | IntVar3
				Next
				TestName[ TestNum ] = "IntVar1 = IntVar2 | IntVar3"
			Case 49
				For Local N:Int = 0 Until Cycles
					IntVar1 = IntVar2 Shl BitNum
				Next
				TestName[ TestNum ] = "IntVar1 = IntVar2 Shl BitNum"
			Case 50
				For Local N:Int = 0 Until Cycles
					IntVar1 = IntVar2 Shr BitNum
				Next
				TestName[ TestNum ] = "IntVar1 = IntVar2 Shr BitNum"
			Case 51
				For Local N:Int = 0 Until Cycles
					FloatVar1 = Sin( FloatVar2 )
				Next
				TestName[ TestNum ] = "FloatVar1 = Sin( FloatVar2 )"
			Case 52
				For Local N:Int = 0 Until Cycles
					FloatVar1 = Cos( FloatVar2 )
				Next
				TestName[ TestNum ] = "FloatVar1 = Cos( FloatVar2 )"
			Case 53
				For Local N:Int = 0 Until Cycles
					FloatVar1 = Tan( FloatVar2 )
				Next
				TestName[ TestNum ] = "FloatVar1 = Tan( FloatVar2 )"
			Case 54
				For Local N:Int = 0 Until Cycles
					FloatVar1 = ASin( SinValue )
				Next
				TestName[ TestNum ] = "FloatVar1 = ASin( SinValue )"
			Case 55
				For Local N:Int = 0 Until Cycles
					FloatVar1 = ACos( SinValue )
				Next
				TestName[ TestNum ] = "FloatVar1 = ACos( SinValue )"
			Case 56
				For Local N:Int = 0 Until Cycles
					FloatVar1 = ATan( SinValue )
				Next
				TestName[ TestNum ] = "FloatVar1 = ATan( SinValue )"
			Case 57
				For Local N:Int = 0 Until Cycles
					FloatVar1 = ATan2( FloatVar2, FloatVar3 )
				Next
				TestName[ TestNum ] = "FloatVar1 = ATan2( FloatVar2, FloatVar3 )"
			Case 58
				For Local N:Int = 0 Until Cycles
					FloatVar1 = Sqr( FloatVar2 )
				Next
				TestName[ TestNum ] = "FloatVar1 = Sqr( FloatVar2 )"
			Case 59
				For Local N:Int = 0 Until Cycles
					FloatVar1 = Log( LogValue )
				Next
				TestName[ TestNum ] = "FloatVar1 = Log( LogValue )"
			Case 60
				For Local N:Int = 0 Until Cycles
					FloatVar1 = Log10( LogValue )
				Next
				TestName[ TestNum ] = "FloatVar1 = Log10( LogValue )"
			Case 61
				For Local N:Int = 0 Until Cycles
					FloatVar1 = Exp( Power )
				Next
				TestName[ TestNum ] = "FloatVar1 = Exp( Power )"
			Case 62
				For Local N:Int = 0 Until Cycles
					StringVar1 = StringVar2[ ..ElementNum ]
				Next
				TestName[ TestNum ] = "StringVar1 = StringVar2[ ..ElementNum ]"
			Case 63
				For Local N:Int = 0 Until Cycles
					StringVar1 = StringVar2[ ElementNum.. ]
				Next
				TestName[ TestNum ] = "StringVar1 = StringVar2[ ElementNum.. ]"
			Case 64
				For Local N:Int = 0 Until Cycles
					StringVar1 = StringVar2[ ElementNum1..ElementNum2 ]
				Next
				TestName[ TestNum ] = "StringVar1 = StringVar2[ ElementNum1..ElementNum2 ]"
			Case 65
				For Local N:Int = 0 Until Cycles
					StringVar1 = StringVar2.Find( SubString1 )
				Next
				TestName[ TestNum ] = "StringVar1 = StringVar2.Find( SubString1 )"
			Case 66
				For Local N:Int = 0 Until Cycles
					StringVar1 = StringVar2.Replace( SubString1, SubString2 )
				Next
				TestName[ TestNum ] = "StringVar1 = StringVar2.Replace( SubString1, SubString2 )"
			Case 67
				For Local N:Int = 0 Until Cycles
					StringVar1 = StringVar2.ToLower()
				Next
				TestName[ TestNum ] = "StringVar1 = StringVar2.ToLower()"
			Case 68
				For Local N:Int = 0 Until Cycles
					StringVar1 = Chr( SymbolNum )
				Next
				TestName[ TestNum ] = "StringVar1 = Chr( SymbolNum )"
			Case 69
				For Local N:Int = 0 Until Cycles
					IntVar1 = Asc( Symbol )
				Next
				TestName[ TestNum ] = "IntVar1 = Asc( Symbol )"
			Case 70
				For Local N:Int = 0 Until Cycles
					Object1 = Object2
				Next
				TestName[ TestNum ] = "Object1 = Object2"
			Case 71
				For Local N:Int = 0 Until Cycles
					Object1.IntField = IntVar2
				Next
				TestName[ TestNum ] = "Object1.IntField = IntVar2"
			Case 72
				For Local N:Int = 0 Until Cycles
					Object1.FloatField = FloatVar2
				Next
				TestName[ TestNum ] = "Object1.FloatField = FloatVar2"
			Case 73
				For Local N:Int = 0 Until Cycles
					Object1.StringField = StringVar2
				Next
				TestName[ TestNum ] = "Object1.StringField = StringVar2"
			Case 74
				For Local N:Int = 0 Until Cycles
					IntVar1 = Object1.IntMethod()
				Next
				TestName[ TestNum ] = "IntVar1 = Object1.IntMethod()"
			Case 75
				For Local N:Int = 0 Until Cycles
					FloatVar1 = Object1.FloatMethod()
				Next
				TestName[ TestNum ] = "FloatVar1 = Object1.FloatMethod()"
			Case 76
				For Local N:Int = 0 Until Cycles
					StringVar1 = Object1.StringMethod()
				Next
				TestName[ TestNum ] = "StringVar1 = Object1.StringMethod()"
			Case 77
				For Local N:Int = 0 Until Cycles
					Object1.EmptyMethod()
				Next
				TestName[ TestNum ] = "Object1.EmptyMethod()"
			Case 78
				For Local N:Int = 0 Until Cycles
					Object1.ExistingMethod()
				Next
				TestName[ TestNum ] = "Object1.ExistingMethod()"
			Case 79
				For Local N:Int = 0 Until Cycles
					Object2.ExistingMethod()
				Next
				TestName[ TestNum ] = "Object2.ExistingMethod()"
			Case 80
				For Local N:Int = 0 Until Cycles
					Object1.Method1( IntVar1 )
				Next
				TestName[ TestNum ] = "Object1.Method1( IntVar1 )"
			Case 81
				For Local N:Int = 0 Until Cycles
					Object1.Method2( IntVar1, IntVar2 )
				Next
				TestName[ TestNum ] = "Object1.Method2( IntVar1, IntVar2 )"
			Case 82
				For Local N:Int = 0 Until Cycles
					Object1.Method3( IntVar1, IntVar2,IntVar3 )
				Next
				TestName[ TestNum ] = "Object1.Method3( IntVar1, IntVar2,IntVar3 )"
			Case 83
				For Local N:Int = 0 Until Cycles
					Object1.Method1Var( IntVar1 )
				Next
				TestName[ TestNum ] = "Object1.Method1Var( IntVar1 )"
			Case 84
				For Local N:Int = 0 Until Cycles
					Object1.Method2Var( IntVar1, IntVar2 )
				Next
				TestName[ TestNum ] = "Object1.Method2Var( IntVar1, IntVar2 )"
			Case 85
				For Local N:Int = 0 Until Cycles
					Object1.Method3Var( IntVar1, IntVar2,IntVar3 )
				Next
				TestName[ TestNum ] = "Object1.Method3Var( IntVar1, IntVar2,IntVar3 )"
		End Select
		
		TestTime[ TestNum ] :+ Millisecs() - Time
	Next
	
	DrawRect( 0, 0, ( Pass + 1 ) * 16, 16 )
	Flip
Next



For Local TestNum:Int = 0 Until TestsQuantity
	WriteLine( File,  TestName[ TestNum ] + ": " + 1000000.0 * TestTime[ TestNum ] / Passes / Cycles + " ns" )
Next

CloseFile( File )
OpenURL( "output.txt" )


Function IntFunction:Int()
	Return 0
End Function



Function FloatFunction:Float()
	Return 0.0
End Function



Function StringFunction:String()
	Return ""
End Function



Function Function1( Value1:Int )
	Value1 = 0
End Function



Function Function2( Value1:Int, Value2:Int )
	Value1 = Value2
End Function



Function Function3( Value1:Int, Value2:Int, Value3:Int )
	Value1 = Value2 + Value3
End Function



Function RndString:String( Symbols:Int )
	Local St:String = ""
	For Local N:Int = 0 Until Symbols
		St :+ Chr( Rand( 32, 127 ) )
	Next
	Return St
End Function
