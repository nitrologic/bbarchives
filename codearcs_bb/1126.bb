; ID: 1126
; Author: Tails
; Date: 2004-08-05 16:14:35
; Title: Google Search Bar for BlitzPlus Interface
; Description: Add a Google Search Bar to any BlitzPlus Application

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;Google Search Bar Example   ;
;by Steve Mountain           ;
;(Tails)                     ;
;05/08/2004                  ;
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;


AppTitle "Google Search Bar"


;Global Variables
Global Main
Global File
Global Close
Global Google
Global Search
Global HTML


;Function to Create the Main Interface
Function CreateMain()
Main = CreateWindow("Google Search Bar",104,97,800,600,Desktop(),15)
  
File = CreateMenu("File",1,WindowMenu(Main)) 
Close = CreateMenu("Close",2,File) 

Google = CreateTextField(5,25,350,20,Main,0) 
SetGadgetLayout Google,1,0,1,0 
Search = CreateButton("Search Google",350,25,100,20,Main,0) 
HTML = CreateHtmlView(6,65,780,490,Main,0) 
SetGadgetLayout HTML,1,1,1,1 

UpdateWindowMenu Main 

End Function ;ends the CreateMain function.

CreateMain() 
While WaitEvent()<>$803 
EvID=EventID()

EvData=EventData()


Select EvID 
     Case $1001 ;Menu Action
        Select EvData
          Case 2 
          If Confirm("Do you really want to Exit?")                  
 End
          EndIf 
        End Select
End Select

;google bar setup
     
If EvID=$401 And EventSource()=Search Then
HtmlViewGo html,"http://www.google.co.uk/search?hl=en&ie=UTF-8&q="+TextFieldText$(google)+"&btnG=Search&meta=cr%3DcountryUK%7CcountryGB" 
EndIf

Wend ;ends the while loop
