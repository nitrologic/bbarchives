; ID: 3052
; Author: Dan
; Date: 2013-04-21 05:48:22
; Title: CreateWindow style flags helper
; Description: b+ CreateWindow Style flag helper tool

;Gui written with GUIde 1.4 for BlitzPlus 

; Decls file needed:
;.lib "user32.dll"
; 
;api_GetWindowLong%(hwnd%,nIndex%):"GetWindowLongA"
;api_SetWindowLong%(hwnd%,nIndex%,dwNewLong%):"SetWindowLongA"
;
AppTitle "B+ Window Flag Editor"

Global EditWindow
Global chbCheckBox0
Global chbCheckBox1
Global chbCheckBox2
Global chbCheckBox3
Global chbCheckBox4
Global chbCheckBox5
Global txtfield
Global Result=0
Global Oldkey$="0"
Global Btnok
Global btnCancel

; Code for text only textfield used from seldon
Const GWL_STYLE=-16
Const ES_NUMBER=8192
		

EditWindow=CreateWindow("Window Flag Editor",0,00,220,145,0,9)
	chbCheckBox0=CreateButton("Titlebar",3,0,111,16,EditWindow,2)
		SetGadgetLayout chbCheckBox0,1,0,1,0
	chbCheckBox1=CreateButton("Resizable",3,16,96,16,EditWindow,2)
		SetGadgetLayout chbCheckBox1,1,0,1,0
	chbCheckBox2=CreateButton("+Menu",3,33,96,16,EditWindow,2)
		SetGadgetLayout chbCheckBox2,1,0,1,0
	chbCheckBox3=CreateButton("+Status",3,50,96,16,EditWindow,2)
		SetGadgetLayout chbCheckBox3,1,0,1,0
	chbCheckBox4=CreateButton("Its Tool window",3,67,96,16,EditWindow,2)
		SetGadgetLayout chbCheckBox4,1,0,1,0
	chbCheckBox5=CreateButton("window shape is in Client coordinate",3,83,189,16,EditWindow,2)
		SetGadgetLayout chbCheckBox5,1,0,1,0
	txtfield=CreateTextField(173,14,25,20,EditWindow)
		SetGadgetText txtfield,"0"
        h_txtfield=QueryObject(txtfield,1)
        api_SetWindowLong(h_txtfield,GWL_STYLE,api_GetWindowLong(h_txtfield,GWL_STYLE) Or ES_NUMBER)
    	CreateLabel("Flag Nr: (press enter to show)",121,17,40,50,EditWindow,0)
		SetGadgetLayout txtfield,1,0,1,0
    Btnok=CreateButton("Ok",0,0,0,0,EditWindow,4)           ; Size and Position set to 0 to hide this button, Flag 4 = Default OK button
		SetGadgetLayout Btnok,1,0,1,0
    btnCancel=CreateButton("Abbruch",0,0,0,0,EditWindow,5)  ; ; Size and Position set to 0 to hide this button, Flag 5 = Default Cancel button
		SetGadgetLayout btnCancel,1,0,1,0

;-mainloop--------------------------------------------------------------

Repeat
	id=WaitEvent()
	Select id
		Case $401									; interacted with gadget
			DoGadgetAction( EventSource() )
		Case $803									; close gadget
			Exit
    End Select
Forever

End

; * Mainloop end

Function SetCheckbox(nr)
; Checks or uncheck the Checkboxes according to the binary value of the number entered
; as only 6 states are allowed, only 6 binary places are needed
    VBin$=Right$(Bin(nr),6)
    SetButtonState  chbCheckBox0,Mid$(VBin$,6,1)
    SetButtonState  chbCheckBox1,Mid$(VBin$,5,1)
    SetButtonState  chbCheckBox2,Mid$(VBin$,4,1)
    SetButtonState  chbCheckBox3,Mid$(VBin$,3,1)
    SetButtonState  chbCheckBox4,Mid$(VBin$,2,1)
    SetButtonState  chbCheckBox5,Mid$(VBin$,1,1)
    CalcResult
 
End Function

Function CalcResult()
;Calculates the result variable from the checkbox states
; Decimal 1 = binary 00001  ; 16 = 010000 ; 32 = 100000
Result=0  ; Resets the result variable 
          If ButtonState(chbCheckBox0) = 1    ; if the checkbox is checked add the decimal value to the result (see binary meanings of it)
            Result=Result+1
          Else
            If Result=>1 Then Result=Result-1 ; if checkbox isnt checked, check if result is greater than the value  (to prevent going into minus)
          EndIf
          If ButtonState(chbCheckBox1) = 1
            Result=Result+2
          Else
            If Result=>2 Then Result=Result-2
          EndIf
          If ButtonState(chbCheckBox2) = 1
             Result=Result+4
          Else
           If Result=>4 Then Result=Result-4
          EndIf
          If ButtonState(chbCheckBox3) = 1
            Result=Result+8
          Else
            If Result=>8 Then Result=Result-8
          EndIf
          If ButtonState(chbCheckBox4) = 1
            Result=Result+16
          Else
            If Result=>16 Then Result=Result-16
          EndIf
          If ButtonState(chbCheckBox5) = 1
            Result=Result+32
          Else
            If Result=>32 Then Result=Result-32
          EndIf
          
          SetGadgetText txtfield,Result       ;Update the textfield 
          Oldkey$=Result                      ;and the Oldkey$ with the result
End Function
;-gadget actions--------------------------------------------------------

Function DoGadgetAction( gadget )
	Select gadget
		Case chbCheckBox0	; user changed checkbox 1
            CalcResult
		Case chbCheckBox1	; user changed checkbox 2
		    CalcResult
		Case chbCheckBox2	; user changed checkbox 3
		    CalcResult
		Case chbCheckBox3	; user changed checkbox 4
		    CalcResult
		Case chbCheckBox4	; user changed checkbox 5
		    CalcResult
		Case chbCheckBox5	; user changed checkbox 6
		    CalcResult
        Case txtfield       ;something is written in the textbox
                  If  Len(TextFieldText$(txtfield))<=2 ; if the text length is 2
	                Oldkey$= TextFieldText$(txtfield) ; save the textfield value in oldkey$
	              Else
	                SetGadgetText txtfield,Oldkey$    ; if higher than 2 sets the textfield value to the oldkey$ variable and this prevents more chars to be written !
                  EndIf
         Case Btnok     ;Button Ok was pressed
              SetCheckbox(Oldkey$)  ; Hidden OK button was pressed ! needed when the enter key is in the textfield
              SetGadgetText txtfield,Oldkey$
         Case btnCancel ;Button Cancel was pressed and the Textfield is active
                Oldkey$="0"
                SetCheckbox(Oldkey$)
                SetGadgetText txtfield,Oldkey$
	End Select

	SetStatusText EditWindow,"Window flag number is:"+Result  ;Show the result in the toolbar
	
End Function
