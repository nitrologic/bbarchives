; ID: 3192
; Author: GW
; Date: 2015-03-07 15:35:19
; Title: BriskVM invoker as function pointers
; Description: Convert BVM invoker from slow select/case to fast function pointers.

Rem
	BVM invoker converter by AWoodard 2014 (admin at kerneltrick.com)
EndRem

SuperStrict 
Framework brl.retro 

Local aa$[] = AppArgs[..]	'// for debugging

Global outStream:TStream 

If AppArgs.Length <> 2 Then 
	Print "Usage: BVM_Converter <invoker.bmx>"	
	End
EndIf

outstream =  WriteFile(StripExt(AppArgs[1]) + "_" + ".bmx" )

reprocess(AppArgs[1])
'------------------------------------------------------------------------------------------------------------
Function _Print(s$)
	WriteString(outStream,s+"~n")	
End Function
'------------------------------------------------------------------------------------------------------------
Function reprocess( file$)
	Local invokertext$	'raw text
	Local aLines$[]		'invoker text split by line
	Local startline%	'start of invoke function 
	Local endline%		'end of select/case 
	Local startSelect%	'line of 'Select ret%"
	Global firstLine$ = "Function BVM_Invoke%(withTimeOut% = False)"
	Local maxIndex% = 0
	Local Locals$[]
	Local cases:TList = CreateList()
		
	If Not FileType(file)=1 Then RuntimeError("Not a valid invoker file! " + file)
	invokertext = LoadText(file)
	aLines = invokertext.split("~n")
	
	startline = find_string(aLines, firstline)
	If Not startline Then RuntimeError("Cant Find function def!")
	
	endline = find_string(aLines, "End Function", startline+1 )
	If Not endline Then RuntimeError("Cant Find end block!")
	
	startselect = find_string(aLines, "Select ret%")
	
	maxIndex = FindMaxIndex(aLines, startline, endline)	'// find the largest case value

	Locals = GatherLocals(Locals, aLines, startline, endline)	'wtf!? 'Locals' array is not passes by ref?!
	

	'// write pre
	For Local i% = 0 Until startline	'// write out the file before our mods
		_Print(aLines[i])
	Next
	
	_Print "Function Err2%()"
	_Print "	BVM_SetLastError(BVM_ERR_ERROR%, ~qFatal error : unknown command of id ~q + BVM_IntToStr(tBVMf.ret))" '+ ", the invoker seems To be out of sync with the 'Test' command set")"
	_Print "	BVM_ReportDebugError(BVM_GetLastErrorMsg())"
	_Print "	Return 0"
	_Print "End function~n"


	_Print "Function BVM_FP_run%(index%)"
	_Print "	Local tmpfunc%() = tBVMf.FP[index]"
	_Print "	return tmpfunc()"
	_Print "End Function~n"

		
	_Print "Type tBVMf"
		_Print "~tGlobal hasInit%=0"
		_Print "~tGlobal withTimeOut%"
		_Print "~tGlobal FP:byte ptr["+(maxindex+1)+"]"
	'// Get locals from func
		For Local s$ = EachIn locals 
			_Print "~tGlobal " + s
		Next
		
	''Print "~t'GLOBALS"
	
	
	Local block$[]
	For Local i% = 0 Until MaxIndex 
'		Print i
		block = GetBLockContents(aLInes, i, startline, endline ) 
		If block.length Then 
			WriteFuncBlock(i,block)
			cases.AddLast(String(i))
		End If
	Next
	
	'// set all the pointers to the ERR function at start
	_Print "	Function Init()"
		_Print "~t~tFor Local i% = 0 Until "+maxindex
		_Print "~t~t~tFP[i]=Err2"
		_Print "~t~tNext"
		
		'// now just replace the ones we use
		For Local s$ = EachIn cases
			_Print "~t~tFP["+Int(s)+"] = tBVMf._bvmf_" + Int(s)
		Next
	_Print "	hasInit=1"
	_Print "	End Function~n"
	

	
	_Print "End Type"
	
	_Print firstline
	_Print "local ret%"
	_print "tBVMf.withTimeOut%=withTimeOut"
	_print " if not tBVMf.hasInit then tBVMf.init()" 
	_Print " Repeat" 
	_Print "	If withTimeOut% Then"
	_Print "		ret% = BVM_RunWithTimeOut()"
	_Print "	Else"
	_Print "		ret% = BVM_Run()"
	_Print "	End If"
	_Print "	tBVMf.ret = ret"
	_Print "	Select ret"
	_Print "		Case 0"
	_print "			BVM_FlushDebugLog(); Return 1"
	_Print "		Case 1"
	_Print "			If withTimeOut% Then"
	_Print "				Return -1"
	_Print "			Else"
	_Print "				BVM_ReportDebugError(BVM_GetLastErrorMsg())"
	_Print "				Return 0"
	_Print "			End If"
	_Print "		Case -1"
	_Print "			BVM_ReportDebugError(BVM_GetLastErrorMsg())"
	_Print "			Return 0"
	_Print "		Default"
	_Print "	'		If Int(tBVMf.FP[ret]) <> 0 Then"
	_Print "				BVM_FP_run(ret)"
	_Print "	'		Else"
	_Print "	'			BVM_SetLastError(BVM_ERR_ERROR%, ~qFatal error : unknown command of id ~q + BVM_IntToStr(ret%))" '+ ", the invoker seems To be out of sync with the 'Test' command set")"
	_Print "	'			BVM_ReportDebugError(BVM_GetLastErrorMsg())"
	_Print "	'			Return 0"
	_Print "	'		endif"
	_Print "	End Select	"
	
	'_print "	if ret = 0 then" 
	'_print "		BVM_FlushDebugLog(); Return 1"
	'_print "	EndIf"	
	
	'_Print "	if ret = -1 then"

	'_Print "	EndIf"
	
	_Print " Forever"
	_Print "End Function~n"
	
	For Local I% = endline +1 Until aLines.Length
		_Print aLines[i]
	Next
	
End Function
'------------------------------------------------------------------------------------------------------------
Function GatherLocals$[](Locals$[], aLInes$[], _start%, _end%)
	For Local i% = _start To _end
		If Left(Trim(aLines[i]),5) = "Local" Then
			Locals :+ [ aLines[i].Split("Local ")[1] ]
		EndIf
	Next
	Return locals
End Function
'------------------------------------------------------------------------------------------------------------
Function Find_string%(aLines$[], needle$, start%=0)
	For Local i% = start Until aLines.Length
		If Lower(Trim(alines[i])) = Lower(Trim(needle)) Then
			Return i
		EndIf
	Next
	Return False
End Function
'------------------------------------------------------------------------------------------------------------
Function WriteFuncBlock$(num%, contents$[])
	_Print "	Function _bvmf_" + num + "%()"
	For Local s$ = EachIn contents
		_Print s.Replace("~t~t","~t")
	Next
	_Print "	End Function~n"
End Function
'------------------------------------------------------------------------------------------------------------
'------------------------------------------------------------------------------------------------------------
Function FindMaxIndex%( aLines$[], _start%, _end%)
	'// Find the largest case value //
	Local res%=-9999
	Local num%
	Local aTmp$[]
	
	Assert(_end < aLines.length)
		For Local i% = _start Until _end
			If alines[i].Contains("Case 0") Then Continue
			If Left(Trim(aLines[i]),4) = "Case" Then 
				aTmp =  Trim(aLines[i]).Split(" ")
				num = Int(aTmp[1])
				If Not num Then
					RuntimeError("wrong number at line " + i)
				End If	
				If num > res Then res = num
			EndIf
		Next
		
		Return res
End Function
'------------------------------------------------------------------------------------------------------------
Function GetBLockContents$[](aLInes$[], num%, _start%, _end%)
	'// get the contents of the case block and return as a string array or empty //
	Local state% = False
	Local block$[]
	
	For Local i% = _start Until _end	
		If Lower(Trim(aLines[i])) = "case " + num Then
			'If state Then Return block		
			state = True
			Continue
		EndIf 
		If state Then
			If Left(Lower(Trim(aLines[i])),4) = "case" Then Return block 
			block :+ [aLInes[i]]
		EndIf
'		Print i
	Next
	Return block
End Function

'------------------------------------------------------------------------------------------------------------
