; ID: 2864
; Author: superStruct
; Date: 2011-06-27 22:12:46
; Title: AVRDude Assistant
; Description: GUI for AVRDude

;GUIde 1.4 BlitzPlus export
;V1.0 AVRDude Assistant
;6/15/2011
;Version 1.0
AppTitle "AVRDude Assistant"

Global w_main
Global cb_partnum
Global cb_programmer
Global lb_partnum
Global lb_programmer
Global chb_advanced
Global lb_baudrate
Global tf_baudrate
Global tf_bitrate
Global lb_bitrate
Global cb_port
Global lb_port
Global tf_port
Global chb_preprogram
Global bt_finishup

Global w_download
Global chb_config
Global tf_inoutfile
Global lb_inout
Global bt_inoutbrowse
Global tf_config
Global bt_configbrowse
Global cb_memory
Global cb_action
Global lb_memory
Global lb_action
Global bt_finish
Global chb_erase

Global inputFile$
Global configFile$
Global batchFile

Global command$

w_main=CreateWindow("AVRDude Assistant",0,00,475,325,0,1)
	cb_partnum=CreateComboBox(24,32,125,20,w_main)
		SetGadgetLayout cb_partnum,1,0,1,0
	cb_programmer=CreateComboBox(208,32,250,20,w_main)
		SetGadgetLayout cb_programmer,1,0,1,0
	lb_partnum=CreateLabel("Part Number",24,16,64,16,w_main,0)
		SetGadgetLayout lb_partnum,1,0,1,0
	lb_programmer=CreateLabel("Programmer",208,16,64,16,w_main,0)
		SetGadgetLayout lb_programmer,1,0,1,0
	chb_advanced=CreateButton("Advanced",24,96,96,16,w_main,2)
		SetGadgetLayout chb_advanced,1,0,1,0
	lb_baudrate=CreateLabel("Baudrate",24,128,64,16,w_main,0)
		SetGadgetLayout lb_baudrate,1,0,1,0
		DisableGadget lb_baudrate
	tf_baudrate=CreateTextField(24,144,64,20,w_main)
		SetGadgetLayout tf_baudrate,1,0,1,0
		DisableGadget tf_baudrate
	tf_bitrate=CreateTextField(128,144,64,20,w_main)
		SetGadgetLayout tf_bitrate,1,0,1,0
		DisableGadget tf_bitrate
	lb_bitrate=CreateLabel("Bitrate",128,128,64,16,w_main,0)
		SetGadgetLayout lb_bitrate,1,0,1,0
		DisableGadget lb_bitrate
	cb_port=CreateComboBox(248,144,96,20,w_main)
		SetGadgetLayout cb_port,1,0,1,0
		DisableGadget cb_port
	lb_port=CreateLabel("Port",248,128,64,16,w_main,0)
		SetGadgetLayout lb_port,1,0,1,0
		DisableGadget lb_port
	tf_port=CreateTextField(352,144,60,20,w_main)
		SetGadgetLayout tf_port,1,0,1,0
		DisableGadget tf_port
	chb_preprogram=CreateButton("Preserve Program",24,176,102,16,w_main,2)
		SetGadgetLayout chb_preprogram,1,0,1,0
		DisableGadget chb_preprogram
	bt_finishup=CreateButton("Finish Up",144,224,150,50,w_main,1)
		SetGadgetLayout bt_finishup,1,0,1,0
		
w_download=CreateWindow("Last Step",0,024,328,348,w_main,17)
	HideGadget(w_download)
	chb_config=CreateButton("Config File",16,72,96,16,w_download,2)
		SetGadgetLayout chb_config,1,0,1,0
	tf_inoutfile=CreateTextField(16,24,200,25,w_download)
		SetGadgetLayout tf_inoutfile,0,0,0,0
	lb_inout=CreateLabel("File",16,8,144,16,w_download,0)
		SetGadgetLayout lb_inout,1,0,1,0
	bt_inoutbrowse=CreateButton("Browse",224,24,64,24,w_download,1)
		SetGadgetLayout bt_inoutbrowse,1,0,1,0
	tf_config=CreateTextField(16,96,200,25,w_download)
		SetGadgetLayout tf_config,1,0,1,0
		DisableGadget tf_config
	bt_configbrowse=CreateButton("Browse",224,96,64,24,w_download,1)
		SetGadgetLayout bt_configbrowse,1,0,1,0
		DisableGadget bt_configbrowse
	cb_memory=CreateComboBox(16,175,120,20,w_download)
		SetGadgetLayout cb_memory,1,0,1,0
	cb_action=CreateComboBox(160,175,120,20,w_download)
		SetGadgetLayout cb_action,1,0,1,0
	lb_memory=CreateLabel("Memory",16,159,64,16,w_download,0)
		SetGadgetLayout lb_memory,1,0,1,0
	lb_action=CreateLabel("Action",160,159,64,16,w_download,0)
		SetGadgetLayout lb_action,1,0,1,0
	bt_finish=CreateButton("Program",24,216,264,64,w_download,1)
		SetGadgetLayout bt_finish,1,0,1,0
	chb_erase = CreateButton("Erase Chip",75,140,96,16,w_download,2)
		SetGadgetLayout chb_erase,1,0,1,0

Include "guidef.lib"

;-mainloop--------------------------------------------------------------

Repeat
	id=WaitEvent()
	Select id
		Case $401									; interacted with gadget
			DoGadgetAction( EventSource() )
		Case $803									; close gadget
			If(EventSource() = w_main)
				Exit
			ElseIf EventSource() = w_download
				HideGadget(w_download)
			EndIf
	End Select
Forever


;-gadget actions--------------------------------------------------------

Function DoGadgetAction( gadget )
	Select gadget
		Case cb_partnum
			; insert your action for cb_partnum here

		Case cb_programmer
			; insert your action for cb_programmer here

		Case chb_advanced	; user changed checkbox
			If ButtonState(chb_advanced)
				EnableGadget(lb_baudrate)
				EnableGadget(lb_bitrate)
				EnableGadget(tf_baudrate)
				EnableGadget(tf_bitrate)
				EnableGadget(chb_preprogram)
				EnableGadget(lb_port)
				EnableGadget(cb_port)
				EnableGadget(tf_port)
			Else
				DisableGadget(lb_baudrate)
				DisableGadget(lb_bitrate)
				DisableGadget(tf_baudrate)
				DisableGadget(tf_bitrate)
				DisableGadget(chb_preprogram)
				DisableGadget(lb_port)
				DisableGadget(cb_port)
				DisableGadget(tf_port)				
			EndIf		

		Case tf_baudrate
			If EventData() = 13 Then	; user pressed return in textfield
			EndIf

		Case tf_bitrate
			If EventData() = 13 Then	; user pressed return in textfield
			EndIf

		Case cb_port
			; insert your action for cb_port here

		Case tf_port
			If EventData() = 13 Then	; user pressed return in textfield
			EndIf

		Case chb_preprogram	; user changed checkbox

		Case bt_finishup	; user pressed button
			ShowGadget(w_download)

;----------------------------------------------------------------------------------------------;Finish Up Window

		Case chb_config	; user changed checkbox
			If ButtonState(chb_config)
				EnableGadget(tf_config)
				EnableGadget(bt_configbrowse)
			Else
				DisableGadget(tf_config)
				DisableGadget(bt_configbrowse)			
			EndIf

		Case tf_inoutfile
			If EventData() = 13 Then	; user pressed return in textfield
			EndIf

		Case bt_inoutbrowse	; user pressed button
			inputFile = RequestFile("Select a file...","*hex,eep",False," ")
			SetGadgetText(tf_inoutfile, inputFile)
			inputFile = Right(inputFile,Len(inputFile) - 2)		

		Case tf_config
			If EventData() = 13 Then	; user pressed return in textfield
			EndIf

		Case bt_configbrowse	; user pressed button

		Case cb_memory
			; insert your action for cb_memory here
			If SelectedGadgetItem(cb_memory) = 2 Or SelectedGadgetItem(cb_memory) = 3 Or SelectedGadgetItem(cb_memory) = 4 And SelectedGadgetItem(cb_action) <> 1
				DisableGadget(bt_inoutbrowse)
			Else
				EnableGadget(bt_inoutbrowse)
			EndIf

		Case cb_action
			; insert your action for cb_action here
			If SelectedGadgetItem(cb_memory) = 2 Or SelectedGadgetItem(cb_memory) = 3 Or SelectedGadgetItem(cb_memory) = 4 And SelectedGadgetItem(cb_action) <> 1
				DisableGadget(bt_inoutbrowse)
			Else
				EnableGadget(bt_inoutbrowse)
			EndIf			
			
		Case bt_finish	; user pressed button			
			batchFile = WriteFile("avrdude.bat")
			WriteLine(batchFile, "cd C:\")
			command = ("avrdude -p " + partnum(SelectedGadgetItem(cb_partnum)))
			If ButtonState(chb_advanced)
				If TextFieldText(tf_baudrate) <> ""
					command = command + " -b " + TextFieldText(tf_baudrate)
				EndIf
				
				If TextFieldText(tf_bitrate) <> ""
					command = command + " -B " + TextFieldText(tf_bitrate)
				EndIf
			EndIf
			
			If ButtonState(chb_config)
				command = command + " -C " + configFile
			EndIf
			
			command = command + " -c " + programmer(SelectedGadgetItem(cb_programmer))
			
			If ButtonState(chb_advanced)
				command = command + " -P " + port(SelectedGadgetItem(cb_port)) + TextFieldText(tf_port)
			EndIf
			
			If ButtonState(chb_erase)
				command = command + " -e "
			Else
				command = command + " -U " + memory(SelectedGadgetItem(cb_memory)) + ":" + action(SelectedGadgetItem(cb_action)) + ":"
				
				If SelectedGadgetItem(cb_memory) = 2 Or SelectedGadgetItem(cb_memory) = 3 Or SelectedGadgetItem(cb_memory) = 4 And SelectedGadgetItem(cb_action) <> 1
					command = command + TextFieldText(tf_inoutfile) + ":m"
				ElseIf SelectedGadgetItem(cb_memory) = 2 Or SelectedGadgetItem(cb_memory) = 3 Or SelectedGadgetItem(cb_memory) = 4 And SelectedGadgetItem(cb_action) = 1
					command = command + inputFile + ":i"
				Else
					command = command + inputFile
				EndIf
			EndIf
			
			WriteLine(batchFile,command)
			WriteLine(batchFile,"pause")
			CloseFile(batchFile)
			
			Notify "Make sure your programmer, and board are connected and powered."
			
			ExecFile("avrdude.bat")
			
			HideGadget(w_download)
			
		Case chb_erase
			If ButtonState(chb_erase)
				DisableGadget(lb_memory)
				DisableGadget(lb_action)
				DisableGadget(cb_memory)
				DisableGadget(cb_action)
				DisableGadget(tf_inoutfile)
				DisableGadget(bt_inoutbrowse)
				DisableGadget(lb_inout)
			Else
				EnableGadget(lb_memory)
				EnableGadget(lb_action)
				EnableGadget(cb_memory)
				EnableGadget(cb_action)
				EnableGadget(tf_inoutfile)
				EnableGadget(bt_inoutbrowse)
				EnableGadget(lb_inout)				
			EndIf
			
	End Select
End Function
