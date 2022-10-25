; ID: 1624
; Author: Tiger
; Date: 2006-02-18 09:30:17
; Title: Editable combobox
; Description: Editable combobox without Api.

;
;
; ComboEx test. 2006-02-18
;
;

Global Window=CreateWindow("ComboEx Test.",0,0,640,480)



;
; Create explorer combo.
;
CreateLabel "Explorer Combo box.",10,10,100,20,Window
Global NCombo.tComboEx
NCombo.tComboEx=CreateComboBoxEx(10,30,200,20,Window,100,CE_AutoSearch+CE_EnterSelect)


CEx_AddItem(NCombo,"http://www.blitzbasic.com")
CEx_AddItem(NCombo,"http://www.google.se")
CEx_AddItem(NCombo,"http://www.download.com")
CEx_AddItem(NCombo,"http://www.codersworkshop.com")
CEx_AddItem(NCombo,"http://www.yahoo.com")
CEx_AddItem(NCombo,"http://www.voltroncc.org")

CEx_Sort(NCombo)

CEx_LimitItems(NCombo,8)	;Set to max 8 items in itemsbox.
CEx_SetTextMask(NCombo,"http://")	;remove http:// from search engine.
NCombo\AddButton=CreateButton("Add bookmark.",250,30,100,20,Window)
NCombo\DeleteButton=CreateButton("Del bookmark.",250,65,100,20,Window)

;;
;; Enter hex box
;;
CreateLabel("Enter hex value:",10,80,100,20,Window)	
Global HCombo.tComboEx
HCombo=CreateComboBoxEx(10,100,100,20,Window,50,CE_AutoSearch+CE_EnterNormal+CE_DisablePull)
CEx_SetFormat(HCombo,"$$$$")
CEx_SetPrefix(HCombo,"$")
CreateLabel("Int:",120,80,100,20,Window)
Global convlabel=CreateLabel("0",120,100,100,20,Window,1)

;;
;;More formating, upper/lower letters 
;;
CreateLabel("Enter string to add:",10,150,100,20,Window)
Global FCombo.tComboEx
FCombo=CreateComboBoxEx(10,170,100,20,Window,50,CE_EnterAddItem )
CEx_SetFormat(FCombo,"&¤&¤&¤&¤¤&¤&¤&¤&¤&")
;;
;; Another combobox
;;
CreateLabel("Limit to text, and another way to add.",10,220,200,20,Window)
Global ACombo.tComboEx
ACombo=CreateComboBoxEx(10,250,100,20,Window,100,CE_AutoSearch+CE_AutoSort+CE_EnterSelect)
CEx_LimitTextTo(ACombo,CE_LimitLetters)	
Global AddButton=CreateButton("Add.",120,280,45,20,Window)
	
	
Repeat
	Select WaitEventEx()
		Case $401
			Select EventSource()
				Case NCombo\AddButton
					If NCombo\Existed=True
						Notify("Why add something when it already exists??")
					Else
						Notify(NCombo\SelectedText+" is added to the list")
					EndIf
				
				Case NCombo\Key_Enter,NCombo\Items
					
					tomt=CreateHtmlView(250,100,370,300,Window,1)
					Notify(NCombo\SelectedText)
					HtmlViewGo(tomt,Replace(NCombo\SelectedText," ",""))		
				
				Case NCombo\Items
					Notify "Selected: "+NCombo\SelectedText	
				
				Case HCombo\TextField
					SetGadgetText(convlabel,HCombo\Advance\HexToInt)
				
				Case FCombo\Key_Enter
					Notify("This is a format test, your input: "+FCombo\SelectedText)
				
				Case FCombo\Items
					Notify("You selected:"+FCombo\SelectedText)	
				
				Case AddButton,ACombo\Key_Enter
					AddGadgetItem(ACombo\Items,ACombo\SelectedText)
								
			End Select
		
		Case $803
		    End
	End Select
Forever
;
;
;
; ComboBoxEx by Tiger (2006)
;
; Compiler: Blitz+
;
; Free to use and change how you want.
;
;
; ############################################################################################################
;
; Public Constants
;
; ############################################################################################################
;
; Style Flags
;
; Char conver options
Const CE_ChrNormal		=0  ;Like user input.
Const CE_ChrUpper		=2  ;All Char upper.
Const CE_ChrLower		=4  ;All Char lower.
Const CE_ChrFirst		=8  ;First Char upper.

; Enter options
Const CE_EnterNormal	=16 ;Press enter and trigger Key_Enter event.
Const CE_EnterAddItem	=32 ;Press enter and add user input in itembox.
Const CE_EnterDeleteItem=64 ;Press enter and delete user input in itembox.
Const CE_EnterSelect	=128;Press enter and select current item/text.

; Auto Search Mode
Const CE_AutoSearch		=256

; Sort added items
Const CE_AutoSort		=512

; Hide Item box + Pulldown button (it starts to works like a textfield)
Const CE_DisablePull	=1024

; Limit user input to Letters/Numbers, use in function CEx_LimitTextTo()
Const CE_LimitNormal	=0 ;(Default)
Const CE_LimitLetters	=1 
Const CE_LimitNumbers	=2

; Default Value

Const CE_Default = CE_ChrNormal + CE_EnterSelect + CE_AutoSearch



; ############################################################################################################
;
; Public Type
;
; ############################################################################################################
Type tComboEx
	Field Panel					;This can be used to but some cool colors/image in the background. (Panels commands)
	Field TextField				;Handler to Textfield.
	Field Items					;Handler to Items, use the normal combobox commands or CEx_AddItem(). 
	Field Key_Enter				;Handler to Enter key.
	Field Key_Esc				;Handler to Esc key.
	Field PreFix$				;Prefix used in textfield
	Field SelectedText$ 		;Selected text. Very nice to use, no need to convert combo index.
	Field AddButton				;User can create own add item button, create button and use this handler.
	Field DeleteButton			;User can create own delete item button, create button and use this handler 
	Field Existed				;Flag, sets when user try to add a already existed item to itembox.
	
	Field Advance.tComboBoxEX__ ;Here is more options
End Type




; ############################################################################################################
;
; Public functions
;
; ############################################################################################################

Function WaitEventEx(F=0)
	Local CB.tComboBoxEX__
	Local WaitEvent_=WaitEvent(F)
	Local Index
	Local Txt$
	Local ED=EventData()
		Select WaitEvent_
			Case $401
				
				t=EventSource()
				For CB=Each tComboBoxEx__

					
					Select t
				
						Case CB\DownButton
							If CB\DownActive=False
								ComboEXActive___(CB)
								ActivateGadget CB\Parent
							Else
								ComboEXHide___(CB)
							EndIf

						Case CB\TextField

							
							Txt$=TextFieldText(CB\TextField)
							;
							; Text limit to Letters/Numbers.
							;
							If ED<>32	;space char should be okay, don't you think??
							Select CB\LimitInput
								Case CE_LimitLetters
									If ED<Asc("A") Or ED>Asc("z")
										Txt$=Replace(Txt$,Chr$(ED),"")
										SetGadgetText(CB\TextField,Txt$)	
									EndIf
								
								Case CE_LimitNumbers
									If ED<Asc("0") Or ED>Asc("9")
										Txt$=Replace(Txt$,Chr$(ED),"")
										SetGadgetText(CB\TextField,Txt$)										
									EndIf
							End Select	
							EndIf
							
							;
							; Text Limit size
							;
							If CB\TextLimitSize>0
								If Len(Txt$)>CB\TextLimitSize
									SetGadgetText(CB\TextField,Mid$(Txt$,1,Len(Txt$)-1))
									Txt$=TextFieldText(CB\TextField)
								EndIf
							EndIf
							
							ComboEXConverChar___(CB)
							;
							; View Prefix
							;
							If CB\UserInterface\PreFix<>""
								If Mid$(Txt$,1,Len(CB\UserInterface\PreFix))<>CB\UserInterface\PreFix
									SetGadgetText(CB\TextField,CB\UserInterface\PreFix)
								EndIf		
							EndIf
						
							If CB\Format<>""
								Txt$=CEx_Format___(CB\UserInterface,Txt$)
								SetGadgetText(CB\TextField,Txt$)
							EndIf
							;
							; Search mode
							;
							Found=False
							Select CB\SearchMode
								Case CE_AutoSearch
									For Index=0 To CountGadgetItems(CB\DownListBox)-1
										Org$=Upper$(GadgetItemText(CB\DownListBox,Index))
										Off=1
										Mask$=Upper$(CB\TextMask)
										If Mask$<>""	
											If Mask$=Mid$(Org$,1,Len(CB\TextMask))
												Off=Len(CB\TextMask)+1
											EndIf
										EndIf
										SelTxt$=Mid$(Org$,off,Len(Txt$))
										If SelTxt$=Upper(Txt$)

											SelectGadgetItem(CB\DownListBox,Index)
											CB\UserInterface\SelectedText=GadgetItemText(CB\DownListBox,Index)
											Found=True
											Exit
										EndIf	
									Next	
									If Found=False 
										CB\UserInterface\SelectedText=Txt$
										ComboEXHide___(CB) 
									Else	
										ComboEXActive___(CB)
									EndIf
								Default
									CB\UserInterface\SelectedText=Txt$
										
							End Select	
									
						Case CB\ESCButton
							CB\DownActive=False
							ComboEXHide___(CB)
							ActivateGadget(CB\Parent)
						
						Case CB\DownListBox 
							Local SelObj=SelectedGadgetItem(CB\DownListBox)
							If SelObj>-1
								SetGadgetText(CB\TextField,GadgetItemText(CB\DownListBox,SelObj))
								CB\UserInterface\SelectedText=GadgetItemText(CB\DownListBox,SelObj)
								ComboEXConverChar___(CB)
								ComboEXHide___(CB)
								If CB\EnterButtonStatus=CE_EnterDeleteItem
									If CEx_DeleteItem(CB\UserInterface,TextFieldText(CB\TextField))=False
										WaitEvent_=0
									EndIf
								EndIf
							EndIf	
						Case CB\EnterButton
						
							Select CB\EnterButtonStatus
	
								Case CE_EnterNormal
									ComboEXHide___(CB)
									ComboEXConverChar___(CB)
									CB\UserInterface\SelectedText=TextFieldText(CB\TextField)
							
								Case CE_EnterAddItem
									
									Local NrOfITems=0
									Txt$=CB\UserInterface\SelectedText
									If Txt$<>""
										CEx_AddItem(CB\UserInterface,CB\UserInterface\SelectedText,True);CB\UserInterface\SelectedText,True)
									Else
										WaitEvent_=0
									EndIf	
								
								Case CE_EnterDeleteItem
									If CEx_DeleteItem(CB\UserInterface,TextFieldText(CB\TextField))=False
										WaitEvent_=0
									EndIf
									
								Case CE_EnterSelect
									
									If CB\DownActive
									
										Found=False								
										For Index=0 To CountGadgetItems(CB\UserInterface\Items)-1
											If Upper$(GadgetItemText(CB\UserInterface\Items,Index))=Upper$(CB\UserInterface\SelectedText)
												Found=True
												Exit
											EndIf
										Next
										
										
										If Found
											SelObj=SelectedGadgetItem(CB\DownListBox)
											If SelObj>-1
												SetGadgetText(CB\TextField,GadgetItemText(CB\DownListBox,SelObj))
												CB\UserInterface\SelectedText=GadgetItemText(CB\DownListBox,SelObj)
												ActivateGadget(CB\Parent)
												ComboEXConverChar___(CB)
												ComboEXHide___(CB)
											Else

												WaitEvent_=0
											EndIf			
										Else
											CB\UserInterface\SelectedText=TextFieldText(CB\TextField)
											ActivateGadget(CB\Parent)
											ComboEXConverChar___(CB)
											ComboEXHide___(CB)
									EndIf	
								EndIf	
							End Select		
							
						Default
							ComboEXHide___(CB)
								
					End Select	
					
					;
					; Check extern add button. 
					;
					If CB\UserInterface\AddButton<>0
						If t=CB\UserInterface\AddButton
							NrOfITems=0
							Txt$=CB\UserInterface\SelectedText;TextFieldText(CB\TextField)
							If Txt$<>""
								CEx_AddItem(CB\UserInterface,Txt$,True)
							Else
								WaitEvent_=0
							EndIf		
						EndIf						
					EndIf
					
					;
					;Check extern delete button.
					;
					If CB\UserInterface\DeleteButton<>0
						If t=CB\UserInterface\DeleteButton
							If CEx_DeleteItem(CB\UserInterface,TextFieldText(CB\TextField))=False
								WaitEvent_=0
							EndIf	
						EndIf
					EndIf
				Next
			
			Case $201
				;
				; Close any active pulldown windows when clicking at emty spaces.
				;				
				For CB=Each tComboBoxEx__	
					If CB\DownActive=True
						ComboEXHide___(CB)
						Exit
					EndIf
				Next
				
			Case $101
				
				;
				; Controll arrows in listbox
				;
					
					Listbox=EventData()
					If Listbox<>0 And EventSource()=$120
						TextField=EventX()
						Key=EventY()
									
						NrOfITems=CountGadgetItems(Listbox)-1
						For CB=Each tComboBoxEX__
							If CB\DownActive
								Exit
							EndIf			
						Next
						
						If Key=208 
							DisableGadget TextField
							EnableGadget TextField
							SelObj=SelectedGadgetItem(Listbox)
							If SelObj<NrOfITems Then SelObj=SelObj+1  
							SelectGadgetItem(Listbox,SelObj)
							CB\UserInterface\SelectedText=GadgetItemText(Listbox,SelObj)
						EndIf	
						If Key=200
							DisableGadget TextField
							EnableGadget TextField
							SelObj=SelectedGadgetItem(Listbox)
							If SelObj>0 
								SelObj=SelObj-1 
							Else
								ActivateGadget(CB\TextField) 
							EndIf
							SelectGadgetItem(Listbox,SelObj)							
							CB\UserInterface\SelectedText=GadgetItemText(Listbox,SelObj)
						EndIf
					EndIf

		End Select
		
		

	Return WaitEvent_

End Function
;;; <summary>Create extended combobox.</summary>
;;; <param name="X"></param>
;;; <param name="Y"></param>
;;; <param name="Width"></param>
;;; <param name="Height"></param>
;;; <param name="Group"></param>
;;; <param name="BoxHeight"></param>
;;; <param name="Style">test <br></br> test  </param>
;;; <param name="IconStrip"></param>
;;; <remarks></remarks>
;;; <returns></returns>
;;; <subsystem></subsystem>
;;; <example></example>
Function CreateComboBoxEx.tComboEx(X,Y,Width,Height,Group,BoxHeight=100,Style=CE_Default,IconStrip=0)

	If Group=0 Then Notify("ComboBoxEx needs a parent to work.")
	
	Local CB.tComboBoxEX__
	Local Out.tComboEx
	Local Panel
	
	CB=New tComboBoxEX__
	If CB=Null Then RuntimeError("Can't create tComboBoxEX__")
	;
	; Mask Style Flag
	;
	Local S_=Style
	
	Local ChrFirst 		= S_ And ( CE_ChrFirst )
	Local ChrLower 		= S_ And ( CE_ChrLower )
	Local ChrNormal		= S_ And ( CE_ChrNormal )
	Local ChrUpper 		= S_ And ( CE_ChrUpper )
	
	Local EnterAddItem 	= S_ And ( CE_EnterAddItem )
	Local EnterDelItem 	= S_ And ( CE_EnterDeleteItem )
	Local EnterNormal 	= S_ And ( CE_EnterNormal )
	Local EnterSearch	= S_ And ( CE_EnterSelect )
	
	Local AutoSearch 	= S_ And ( CE_AutoSearch )
	
	Local AutoSort		= S_ And ( CE_AutoSort )
	
	Local EnterButton 	= S_ And ( CE_EnterButton )
	Local EscButton		= S_ And ( CE_EscButton )

	Local DisablePull	= S_ And ( CE_DisablePull )
	

	Local CharCase=ChrFirst+ChrLower+ChrNormal+ChrUpper
	Local EnterStatus=EnterAddItem+EnterButton+EnterNormal+EnterDelItem+EnterSearch
	
	;
	; Normalize if not set by user.
	;
	If CharCase=0 Then CharCase=CE_ChrNormal
	If EnterStatus=0 Then EnterStatus=CE_EnterNormal
	
	Panel=CreatePanel(X,Y,Width+20,Height,Group,2)
	CB\X=X
	CB\Y=Y
	CB\Width=Width+20
	CB\Height=Height
	CB\ExpandHeight=Height+BoxHeight
	CB\Panel=Panel
	CB\TextField=CreateTextField(0,0,Width,Height,Panel,0)
	
	If DisablePull=0
		CB\DownButton=CreateButton(Chr(94),Width,0,20,Height,Panel,1)
		CB\PullButtonEnable=True
		SetGadgetLayout(CB\DownButton,1,0,1,0)
	Else
		CB\PullButtonEnable=False
	EndIf		
	
	CB\DownWindow=CreateCanvas(0,Height+2,Width,BoxHeight,Panel)
	
	CB\DownListBox=CreateListBox(0,0,Width,BoxHeight,CB\DownWindow,IconStrip)
	
	CB\CharCase=CharCase
	CB\SearchMode=AutoSearch
	CB\AutoSort=AutoSort
	CB\EnterButtonStatus=EnterStatus
	
	CB\Parent=Group
	CB\ESCButton=CreateButton("",0,0,0,0,CB\Panel,5)
	SetGadgetLayout(CB\ESCButton,1,0,1,0)
	CB\EnterButton=CreateButton("",0,0,0,0,CB\Panel,4)
	
	
	
	HideGadget CB\DownWindow
	
	SetGadgetLayout(CB\Panel,1,0,1,0)
	SetGadgetLayout(CB\TextField,1,0,1,0)
	SetGadgetLayout(CB\EnterButton,1,0,1,0)
	SetGadgetLayout(CB\DownWindow,1,0,1,0)

	
	;
	; Create user inteface
	;
	Out=New tComboEx
	Out\TextField=CB\TextField
	Out\Items=CB\DownListBox
	Out\Key_Enter=CB\EnterButton
	Out\Key_Esc=CB\ESCButton
	Out\Panel=CB\Panel
	Out\Advance=CB
	
	CB\UserInterface=Out
	
	Return Out
End Function

Function CEx_Sort(CB.tComboEx,Txt$="")
	
	Local S.tComboSort__
	Local BB.tComboSort__
	Local BBB.tComboSort__
	Local Index=0
	Local NrOfItems=CountGadgetItems(CB\Items)-1
	If NrOfItems=<0 Then Return False
	
	;
	; Conver list to tComboSort type.
	;
	For Index=0 To NrOfItems
		S=New tComboSort__
		S\String=GadgetItemText(CB\Items,Index)
		S\TrimString=Replace(S\String," ","")
	Next
	
	ClearGadgetItems(CB\Items)
	
	;
	; Begin sort process, thanks SkidRacer for this sorting routine.
	;
	b.tComboSort__=First tComboSort__
    flag=True
    While flag
        flag=False
        bb.tComboSort__=Last tComboSort__
        While bb<>b
            BBB.tComboSort__=Before BB
            If BBB=Null Exit
             
            If Upper$(BB\TrimString)<Upper$(BBB\TrimString) 
                Insert bbb After bb
                flag=True
            Else
                bb=bbb
            EndIf
        Wend
        b=After bb
    Wend

	For S=Each tComboSort__
		AddGadgetItem(CB\Items,S\String)
	Next
	
	Delete Each tComboSort__
	
	;
	; Select gadgetitem
	;
	If Txt$<>"" And CountGadgetItems(CB\Items)>0	
		For Index=0 To CountGadgetItems(CB\Items)-1
			If GadgetItemText(CB\Items,Index)=Txt$ Then SelectGadgetItem(CB\Items,Index):Exit
		Next
	EndIf
End Function

;Not ready, no use...
Function CEx_SetAutoSortRange(CB.tComboEx,FromIndex=0,ToIndex=0)
	CB\Advance\AutoSortFrom=FromIndex
	CB\Advance\AutoSortTo=ToIndex
End Function

Function CEx_SetPrefix(CB.tComboEx,Prefix$)
	SetGadgetText(CB\TextField,Prefix$)
	ComboEXConverChar___(CB\Advance)
	CB\PreFix=TextFieldText(CB\TextField)
End Function

Function CEx_SetTextMask(CB.tComboEx,Mask$)
	CB\Advance\TextMask$=Upper$(Mask$)
End Function

Function CEx_LimitTextLenght(CB.tComboEx,Lenght)
	CB\Advance\TextLimitSize=Lenght
End Function

Function CEx_LimitTextTo(CB.tComboEx,To_=CE_LimitLetters)
	CB\Advance\LimitInput=To_
End Function

Function CEx_LimitItems(CB.tComboEx,Items=10)
	CB\Advance\LimitNrItems=Items
End Function

Function CEx_AddItem(CB.tComboEx,Item$,Sel=False,Icon=0)
	
	If Item$<>""
		;
		; Check if item allready exists
		;
		For Index=0 To CountGadgetItems(CB\Items)-1
			If Upper$(Item$)=Upper$(GadgetItemText(CB\Items,index))
				CB\Existed=True
				SetGadgetText(CB\TextField,Item$)
				Return False
			EndIf
		Next		
		
		CB\Existed=False
		
		;
		; Check items limit.
		;	
		If CB\Advance\LimitNrItems>0
			NrOfItems=CountGadgetItems(CB\Advance\DownListBox)
			If NrOfITems>=CB\Advance\LimitNrItems
				RemoveGadgetItem(CB\Advance\DownListBox,0)
			EndIf
		EndIf		
		AddGadgetItem(CB\Advance\DownListBox,Item$,Sel,Icon)
		If CB\Advance\AutoSort=CE_AutoSort Then CEx_Sort(CB,Item$)
		CB\SelectedText=Item$
		SetGadgetText(CB\TextField,CB\PreFix)	
		Return True
	Else
		AddGadgetItem(CB\Items,"")
		ComboEXHide___(CB\Advance)
		Return False
	EndIf	
	
End Function

Function CEx_DeleteItem(CB.tComboEx,Txt$="")
Local NrOfItems=CountGadgetItems(CB\Items)-1
Local Index=0, SelectedItem=SelectedGadgetItem(CB\Items)
Local Found=False
If NrOfItems=0 Then Return False	
If Txt$=""	
	If SelectedItem=-1 Then Return False
	CB\SelectedText=GadgetItemText(CB\Items,SelectedItem)
	RemoveGadgetItem(CB\Items,SelectedItem)
	SetGadgetText(CB\TextField,CB\PreFix)	
	Found=True
Else
	For Index=0 To NrOfItems
		If Upper$(GadgetItemText(CB\Items,Index))=Upper$(Txt$)
			RemoveGadgetItem(CB\Items,Index) 
			CB\SelectedText=Txt$
			Found=True
			SetGadgetText(CB\TextField,CB\PreFix)
			NrOfItems=NrOfItems-1
		EndIf
	Next
EndIf	
Return Found
End Function
;;; <summary>Set user input format.</summary>
;;; <param name="CB"></param>
;;; <param name="Format">$=HEX %=BIN #=Numbers &=Lowercase convert ¤=Uppercase convert *=no format change.</param>
;;; <remarks></remarks>
;;; <returns></returns>
;;; <subsystem></subsystem>
;;; <example></example>
Function CEx_SetFormat(CB.tComboEx,Format$)
	CB\Advance\Format=Format$
End Function

Function CEx_Format___$(CB.tComboEx,Txt$)	
	Local Format$=CB\Advance\Format
	Local Prefix$=CB\PreFix
	
	Local LetterLow$="&"	;Lowcase
	Local LetterUpp$="¤"	;Uppercase
	Local Letter$	="*"	;Don't change case.
	Local Number$	="#"	;0-9
	Local Hexs$		="$"	;A-F , 0-9
	Local Bins$		="%"	;0/1

	Local TxtSize=Len(Txt$)
	Local FormSize=Len(Format$)	

	Local CountTxt=1,Get$,Form$,Out$
	Local CountForm=1
	Local Conv$,Ok=True
	
	If Prefix<>"" Then Out=Prefix$:Txt$=Replace(Txt$,Prefix,"")

	
	CB\Advance\HexToInt=0
	
	
	While CountForm<FormSize+1 
		Get$=Mid$(Txt$, CountTxt ,1)
		Form$=Mid$(Format$,CountForm,1) 	
		Select Form$
			Case LetterLow$,LetterUpp$,Letter$
				If (Get$>="A" And Get$=<"Z") Or (Get$>="a" And Get$=<"z") Or Get$=" " Or Get$="." Or Get$=","
					If Form$=LetterLow$ Then Get$=Lower$(Get$)
					If Form$=LetterUpp$ Then Get$=Upper$(Get$)
					CountTxt=CountTxt+1
					Out$=Out$+Get$
				Else
					Exit
				EndIf	
				
			Case Number$
				If Get$>="0" And Get$=<"9" Or Get$=","
					CountTxt=CountTxt+1
					Out$=Out$+Get$
				Else
					Exit
				EndIf	
				
			Case Hexs$
				If (Get$>="A" And Get$=<"F") Or (Get$>="a" And Get$=<"f") Or (Get$>="0" And Get$=<"9"); Or Get$=","
					CountTxt=CountTxt+1
					Out$=Out$+Get$
					;
					;Convert Hex to int.
					;
	
						t2$=Upper$(Out)
						Local d%=0
						For z=1 To Len(t2$)
							i=Instr("0123456789ABCDEF",Mid$(t2$,z,1))
							If i>0 Then d=d*16+i-1
						Next
						CB\Advance\HexToInt=d
	
				Else						
					Exit
				EndIf				
			
			Case Bins$
				If Get$="0" Or Get$="1"; Or Get$=","
					CountTxt=CountTxt+1
					Out$=Out$+Get$
				EndIf
			Default
				Out$=Out$+Get$
				CountTxt=CountTxt+1
		End Select
		
		CountForm=CountForm+1
	Wend	
	
	Return Out
End Function


; ############################################################################################################
;
; Private Functions
;
; ############################################################################################################

Function ComboEXActive___(CB.tComboBoxEX__)
	If CB\PullButtonEnable=False Then Return

	
	
	If ComboEXActive__=Null 
		ComboEXActive__=CB
		ShowGadget(CB\DownWindow)
		SetGadgetShape(CB\Panel,CB\X,CB\Y,CB\Width,CB\ExpandHeight)
		CB\DownActive=True
		ActivateGadget(CB\TextField)
		ComboHotKey(CB,True)
		Return
	EndIf
	If CB<>ComboEXActive__
		HideGadget(ComboEXActive__\DownWindow):ComboEXActive__\DownActive=False
		ShowGadget(CB\DownWindow):CB\DownActive=True
		SetGadgetShape(CB\Panel,CB\X,CB\Y,CB\Width,CB\ExpandHeight)
		ActivateGadget(CB\TextField)
		ComboEXActive__=CB
		ComboHotKey(CB,True)
	EndIf
	

		
End Function

Function ComboHotKey(CB.tComboBoxEX__,Flag)
	If Flag=True
		HotKeyEvent(208,0,$101,CB\DownListBox,CB\TextField,208,0,$120)
		HotKeyEvent(200,0,$101,CB\DownListBox,CB\TextField,200,0,$120)	
		HotKeyEvent(028,0,$401,0,0,0,0,CB\EnterButton)		
	Else
		HotKeyEvent(208,0,0,0)
		HotKeyEvent(200,0,0,0)	
		HotKeyEvent(028,0,0,0)
	EndIf	
End Function

Function ComboEXHide___(CB.tComboBoxEX__)
	If CB\PullButtonEnable=False Then Return
	If CB\DownActive=False Then Return
	CB\DownActive=False
	ComboEXActive__=Null
	HideGadget(CB\DownWindow)
	HideGadget(CB\Panel)
	SetGadgetShape(CB\Panel,CB\x,CB\y,CB\Width,CB\Height)
	ShowGadget(CB\Panel)	
	ComboHotKey(CB,False)
End Function

Function ComboEXConverChar___(CB.tComboBoxEX__,Txt$="")
	;
	; Set input text to upper/lower.
	;
	Txt$=TextFieldText(CB\TextField)
	Select CB\CharCase
		Case CE_ChrUpper
			SetGadgetText(CB\TextField,Upper$(Txt$))	
		Case CE_ChrLower
			SetGadgetText(CB\TextField,Lower$(Txt$))
		Case CE_ChrFirst
			Local Firstletter$=Left(txt$,1)
			Firstletter$=Upper$(Firstletter$)
			SetGadgetText(CB\TextField,Firstletter$+Mid$(txt$,2,Len(txt$)-1))	
	
		
	End Select	
End Function


; ############################################################################################################
;
; Private Globals
;
; ############################################################################################################

Global ComboEXActive__.tComboBoxEx__ ; Last activated ComboBoxEx pointer.
; ############################################################################################################
;
; Private Type (Can be used with the Advance pointer in tComboEx.)
;
; ############################################################################################################
Type tComboBoxEX__

	Field Parent		; Parent to comboboxEx.
	Field Panel			; Working area on screen. ( same panel in tComboEx )
	
	Field X,Y,Width,Height
	Field ExpandHeight
	;
	Field TextField 	; User input textfield ( same textfield in tComboEx )
	Field TextLimitSize
	
	Field Format$		; Input format.
	
	Field TextMask$
	
	Field LimitInput	;0=No limit, 1=Only letters, 2=only numbers.
	;
	Field LimitNrItems
	;
	Field CharCase		; Upper/Lower user input (CE_ChrUpper/CE_ChrLower/CE_ChrNormal)
	;
	Field DownButton	; Handler to PullDown Button.
	Field DownWindow	; Handler to PullDown Window
	Field DownListBox	; Handler to PullDown listbox in window.
	Field DownActive	; True/False if PullDown window is open.
	;
	Field SearchMode	; Search in combolist after text matching user input.
	
	Field AutoSort
	Field AutoSortFrom	;Not in use
	Field AutoSortTo	;Not in use
	
	Field HexToInt		;This is in use when using format(hex)
	
	Field UserInterface.tComboEx
	;---------------------------
	;
	; Special user input keys
	;
	;---------------------------
	; Enter
	Field EnterButton		; Handler to Enter button 
	Field EnterButtonStatus ; CE_EnterAddItem	= Enter input to the combo list.
	Field PullButtonEnable

	; ESC
	Field ESCButton
	
	
End Type
;
; Sort storage.
;
Type tComboSort__
	Field NoSort,String$,TrimString$
End Type
