; ID: 2092
; Author: Filax
; Date: 2007-08-09 06:21:21
; Title: Xml Localization file
; Description: Add simply localization in your programs :)

The XML file :

<?xml version="1.0"?>

<game title="Vectory version 1.0 By Philippe Agnisola Copyright 2006-2007">
	<str_yes fr="Oui" en="Yes" de="Ja"/>
	<str_no fr="Non" en="No" de="Nein"/>
	<str_cancel fr="Annuler" en="Cancel" de="Annullieren"/>
</game>


And the example :


Import BaH.Libxml

' ----------------------------------
' Var used to init localization file
' ----------------------------------
Global LOC_LangageFile:String

' ------------------------------
' Function to init langages file
' ------------------------------
Function LOC_InitLangageFile(filename:String)
	If FileType(filename)=1 Then
		LOC_LangageFile=filename
	Else
		Notify("Unable to open file!")
	EndIf
End Function

' ---------------------------------------
' Function to get a string under XML file
' ---------------------------------------
Function LOC_GetString:String(name:String,langage:String)
	If LOC_LangageFile<>"" Then
		Local LOC_Node:xmlNode
		Local LOC_Root:xmlNode
		Local LOC_Find:String
			
		Local LOC_Doc:xmlDocument = New xmlDocument
		LOC_Doc.Load(LOC_LangageFile)
		
		LOC_Root = LOC_Doc.root()
		LOC_Node = LOC_Root.FirstChild()	'First, get the first child of the root...
	
		While LOC_Node <> Null
			If Upper(LOC_Node.name)=Upper(Name) Then
				LOC_Find=String(LOC_Node.Attribute(langage).value)
				LOC_Doc=Null
				
				Return LOC_Find
			EndIf
			
			LOC_Node = LOC_Node.NextSibling()	'Get the next node
		Wend
		
		
		LOC_Doc=Null
		Return "..."
	Else
		Notify("Please init langage file before!")
		Return ""
	EndIf
End Function

' ---------------------------------------
' Function to display all langages string
' ---------------------------------------
Function LOC_LoadLangage(Filename:String)
	Local LOC_Node:xmlNode
	Local LOC_Root:xmlNode
	
	Local LOC_Doc:xmlDocument = New xmlDocument
	LOC_Doc.Load(Filename)
	
	LOC_Root = LOC_Doc.root()
	LOC_Node = LOC_Root.FirstChild() 'First, get the first child of the root...
	
	While LOC_Node <> Null
		Print LOC_Node.name+" / "+String(LOC_Node.Attribute("fr").value)+" / "+String(LOC_Node.Attribute("en").value)+" / "+String(LOC_Node.Attribute("de").value)

		LOC_Node = LOC_Node.NextSibling()	'Get the next node
	Wend
	
	LOC_Doc=Null
EndFunction

' ------------
' Example test
' ------------

'LOC_LoadLangage("localization.xml")
LOC_InitLangageFile("localization.xml")

Print LOC_GetString("str_yes","de")
Print LOC_GetString("str_yes","fr")
Print LOC_GetString("str_yes","en")
