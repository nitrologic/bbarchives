; ID: 2379
; Author: xlsior
; Date: 2008-12-22 11:07:19
; Title: deHTML
; Description: Strip HTML tags from a string

' Basic DeHTML function
' Strips HTML tags And keeps the readable non-HTML portions
'
' By Marc van den Dikkenberg / xlsior
' http://www.xlsior.org
'
' For more extended HTML codes, see: http://www.ascii.cl/htmlcodes.htm
' Note that the extended HTML codes such as the pound symbol are currently ignored.
'


SuperStrict

Local Sample:String="<h1>This</h1><br><b><i> is</i> just</b> some <p>"sample"-html</p> <i>code</i><br><br><br><br>"

Print "Unmodified String: "
Print Sample:String
Print ""
Print "Stripped String: "
Print DeHTML(Sample:String)


Function DeHTML:String(SomeString:String)
	Local dehtmlmode:Int=False
	Local detempstring:String=""
	Local detempcounter:Int=0

	' Check if it's possible that there are any &..; HTML codes -- if so, search & replace them
	If Instr(SomeString,"&")<>0 And Instr(SomeString,";")<>0 Then
		SomeString=Replace(SomeString,"& nbsp ;"," ")
		SomeString=Replace(SomeString,"& quot ;",Chr$(34))
		SomeString=Replace(SomeString,"& amp ;","&")
		SomeString=Replace(SomeString,"& lt ;","<")
		SomeString=Replace(SomeString,"& gt ;",">")
		
	End If


	' Analyze the string for information in between <...> tags, and strip them all
	For detempcounter=0 To Len(SomeString)
		If Mid(SomeString,detempcounter,1)="<" Then
			dehtmlmode=True
		ElseIf Mid(SomeString,detempcounter,1)=">" Then
			dehtmlmode=False
		ElseIf dehtmlmode=False Then
			' Count non-HTML characters
			detempstring=detempstring+Mid(SomeString,detempcounter,1)
		End If
	Next

	Return detempString
End Function
