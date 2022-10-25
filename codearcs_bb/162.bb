; ID: 162
; Author: add
; Date: 2001-12-10 07:13:50
; Title: Examples of the parser
; Description: Shows how the parser function works

; you need to define these variables inorder to use the parser functions!!!!
; Define Globals Used
Type parsereturn 
	Field word$
	Field num
	Field real#
End Type
Global back.parsereturn=New parsereturn
Global RealReturn#=0
; End of globals

Include "ParserFunction.bb"


Graphics 640,480 
Cls
limit$=" ,=[]" ;limiters used in these examples
.start
Read dis$
If dis$="###" Then End
Read example$
Color 255,100,0
Print dis$ 
Color 100,255,100
Print example$

If Instr(example$,"longseperator",1)>0 Then
	example$=Replace$(example$,"longseperator",",")
	Color 255,100,0:Print "Converts to"
	Color 100,255,100:Print example$
End If

result=parse(example$,limit$)
Color 0,200,200
Print "Parts of example="+result
For back.parsereturn=Each parsereturn 
	Print "word="+back\word$+" number="+back\num+" real="+back\real
Next
Print
Print
WaitKey()
Goto start



Data "Examples of Using the Parser","Mynumber=-445.2"
Data "The Val function supports fraction,negative and exponents","-4,-5.003,0.909,0.0000000001,7.4e-2,9.00000e12"
Data "Numbers burried in words are extracted","The-39.4steps"
Data "Complex parsing","Type32c=2 rgb=[45,78,128] pos=[0.14,-12,-2.2e-016] Hat[21]=90" 
Data "Multiple seperators are ignored","fournumbers=5,6,,8 ,==[12]"
Data "Remember that you can use 'replace' to convert multicharacter seperators","14longseperator16,7,12   longseperator13"
Data "###"
