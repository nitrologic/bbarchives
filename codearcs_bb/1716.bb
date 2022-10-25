; ID: 1716
; Author: Jams
; Date: 2006-05-19 14:18:34
; Title: Tickers
; Description: Simple timer\ticker type

;EXAMPLE USAGE:
;
Local Tick% = Ticker_New( 1000 )
Repeat
	If Ticker_HasTicked( Tick% )
		Print "Tick!"
	EndIf
Until KeyHit( 1 )
Ticker_Dispose( Tick% )


;!====================================================================================================
; Ticker type.
;!====================================================================================================
Type Ticker
	Field LastTick%
	Field Frequency%
End Type

Function Ticker_New%( NewFrequency%=1000 )
	Local this.Ticker	= New Ticker

	this\LastTick%		= MilliSecs()
	this\Frequency%		= NewFrequency%

	Return Handle( this )
End Function

Function Ticker_Dispose%( HND% )
	Local this.Ticker = Object.Ticker( HND% )

	Delete this
End Function

Function Ticker_Frequency%( HND%, NewFrequency%=-1 )
	Local this.Ticker = Object.Ticker( HND% )

	If ( NewFrequeny% = -1 )
		Return this\Frequency%
	Else
		If NewFrequency% > 0
			this\Frequency% = NewFrequency%
		EndIf
	EndIf
End Function

Function Ticker_HasTicked%( HND% )
	Local this.Ticker = Object.Ticker( HND% )

	If ( MilliSecs() > ( this\LastTick% +this\Frequency% ) )
		this\LastTick% = MilliSecs()
		Return True
	EndIf

	Return False
End Function
