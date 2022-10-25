; ID: 1245
; Author: xMicky
; Date: 2004-12-25 13:54:31
; Title: Binary decision tree
; Description: Code generator for binary decision trees

;-----------------------------------------------------------------------------------------------------
; USER SETTINGS :

; where the generated code to store in:
FileName$ ="C:\Tmp.bb"

; a decision tree for at least how many decisions you want:
maxDecs =32

; what variable should be asked to make the decisions:
varName$ ="z"

; END OF USER SETTINGS
;-----------------------------------------------------------------------------------------------------
Global curlevel

tmpDecs =maxDecs -1
While tmpDecs >1 
  tmpDecs =tmpDecs /2
  count=count +1
Wend
Dim globCount(count +1)

file =WriteFile(FileName$)
writeCodeLines(file, varName$, 2^count, 2 *2^count, 0)

End 
;-----------------------------------------------------------------------------------------------------
Function writeCodeLines(fileHandle, varName$, comparedUppper, compareStep, insertionlevel)

  curCode$ =String$(" ", insertionlevel) +"If " +varName$ +" <" +Trim$(Str$(comparedUppper +globCount(curlevel) *compareStep)) +" Then"
  WriteLine fileHandle, curCode$
  If comparestep =2 Then
    curCode$ ="; " +Trim$(Str$(comparedUppper +globCount(curlevel) *compareStep -1)) +" -------------------------------------------------------------------------------------------------"
    WriteLine fileHandle, curCode$
    curCode$ =String$(" ", insertionlevel) +"Else"
    WriteLine fileHandle, curCode$
    curCode$ ="; " +Trim$(Str$(comparedUppper +globCount(curlevel) *compareStep)) +" -------------------------------------------------------------------------------------------------"
    WriteLine fileHandle, curCode$
    curCode$ =String$(" ", insertionlevel) +"End If"
    WriteLine fileHandle, curCode$
    globCount(curlevel) =globCount(curlevel) +1
  Else
    globCount(curlevel) =globCount(curlevel) +1
    curlevel =curlevel +1
    writeCodeLines(fileHandle, varName$, comparedUppper /2, compareStep /2, insertionlevel +2)

    curCode$ =String$(" ", insertionlevel) +"Else"
    WriteLine fileHandle, curCode$

    curlevel =curlevel +1
    writeCodeLines(fileHandle, varName$, comparedUppper /2, compareStep /2, insertionlevel +2)

    curCode$ =String$(" ", insertionlevel) +"End If"
    WriteLine fileHandle, curCode$
  End If

  curlevel =curlevel -1

  Return

End Function
;-----------------------------------------------------------------------------------------------------
