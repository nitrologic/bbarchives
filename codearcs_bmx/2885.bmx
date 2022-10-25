; ID: 2885
; Author: Shagwana
; Date: 2011-08-30 13:54:44
; Title: Random number generator
; Description: Fast random number generator, repeatable and possible to instance

SuperStrict

'=========================================================================

Type RNDGenerator
	
  Const iSLIP:Int = 27
  Const i31BITSIGN:Int = $7FFFFFFF
  Const iSCANUPSTART:Int = 1
		
  Field iScanUp:Int
  Field iSeed:Int 
  Field iValue:Int
	
  Method New()
    iScanUp = iSCANUPSTART	
    iSeed = 0
    iValue = 0
  End Method
	
  'Seed the random number generator
  Method Seed(iRNDSeed:Int,iStartScanUp:Int=iSCANUPSTART)
    iValue = (iRNDSeed & i31BITSIGN)
    iSeed = (iRNDSeed & i31BITSIGN)
    iScanUp = (iStartScanUp & i31BITSIGN)
  End Method
	
  'Generate a number from 0 upto iMaxValue (not including)
  Method Generate:Int(iMaxValue:Int)
    'Step up
    iScanUp = (iScanUp + 1)  'Increase by one
    iScanUp = (iScanUp & i31BITSIGN) 'Make sure the sign value does Not get used (wraps it)
    'Re-seed self with new value
    iSeed = ((iSeed+(iSeed shr 8))-(iSLIP~iScanUp)) & i31BITSIGN 'Calculate new seed for next time
    'Wrap to get correct value
    iValue = (iSeed Mod iMaxValue)  'Just in case we need it		   
    Return iValue
  End Method
	
End Type


'=========================================================================
'Example of its use...

Print "--------------------------------------------------------"
Print "www.sublimegames.com simple fast random number generator"
Print "--------------------------------------------------------"


Const iMAX:Int = 20							'Max value to show (0 to iMAX-1)
Const iCOUNT:Int = 0						'Array location for the count
Const iSEEDAGE:Int = 1977
Const iTOTALLOOPS:Int = 999999

Local r:RNDGenerator = New RNDGenerator
Local iValues:Int[iMAX,1]


'Init counts
For Local iN:Int = 0 To (iMAX - 1)
  iValues[iN , iCOUNT] = 0
Next	


'Perform the random generating
r.Seed(iSEEDAGE)				'Seed the random number generator
For Local iN:Int = 0 To iTOTALLOOPS-1
	Local iNum:Int = r.Generate(iMAX)											'Generate a number in range
	'Print iNum
	iValues[iNum , iCOUNT] = iValues[iNum , iCOUNT] + 1			'Count how many times we encountered this one
Next


'Show spread
Local fTotal:Float = 0
Print "Show spread over " + iTOTALLOOPS + " loops, max value is " + (iMAX - 1)
Print ""
For Local iN:Int = 0 To (iMAX - 1)
	
	Local fPercent:Float = (Float(iValues[iN , iCOUNT])/Float(iTOTALLOOPS))*100.0
	Print "Number[" + iN + "]  Count(" + iValues[iN , iCOUNT] + ")  Percent(" + fPercent + ")"
	
	fTotal = (fTotal + fPercent)
	
Next	
Print ""
Print "Total Percent : "+fTotal
Print ""

End
