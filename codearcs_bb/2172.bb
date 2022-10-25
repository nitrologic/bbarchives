; ID: 2172
; Author: jfk EO-11110
; Date: 2007-12-24 02:17:15
; Title: Stepper Motor Control
; Description: Do Robotics or CNC with Blitz3D

; ****************************************************************************************************
; ********************* CALLING THE SMC800 STEPPERMOTOR CONTROLLER FROM BLITZ3D **********************
; ****************************************************************************************************

; This Program shows how to call the SMC800 Steppermotor Controller Card trough the Parallel Port.
; Other Controllers of the SMC Series may work with it too (SMC1500 etc.). See www.emis.com.

; The SMC can control 3 Stepper Motors and one contact switch. The contact switch is used
; to self-calibrate the Machine. Each axis can travel to its Zero Point, defined by a contact switch.
; After reaching the contact switch it has to move back a certain amount of mm (again out of the contact
; zone), so it's possible to start each row of a CNC Raster Milling Process at pretty much
; the same loaction. The 3 Switches are simply wired parallel and connected to the SMC800.
; This allows to "home" each Motor individually. Warning: using the Function "MotorHome()" 
; without working contact switches may cause your motors to seek for contact 
; forever. 

; Unlike many other Control Cards, the SMC800 expects a rudimentary Amplitude Sinus Modulation for the
; two phases of the Steppermotors. It's really a good thing to study and understand stepper Motors.
; The card offers 800mA for each Phase only, the SMC1500 allows 1.5Amp. Motors must be Two Phase
; Stepper Motors, operating at the same voltage that the SMC's power supply provides, this may be
; 12 To 30 Volts.

; The phase modulation can have a frequency up to 5 kHz. Due to the lack of programmable timers
; in Blitz3D we will use a Polling system here, based on the 1 kHz System Timer.


; The Comments in the Program explain the basic concept of calling the card in order to run
; a 3 Axis CNC Router/Locator. The following XYZ Convention is used:

; X = Motor 0 (left / right)
; Y = Motor 1 (down / up)
; Z = Motor 2 (back / forward)

; CNC People often name the vertical axis "Z", which is wrong from a scientific point of view. 
; In this Demo "Y" is the vertical axis. You may alter this in the code if you prefere Z for the
; vertical axis.



; In Addition to Blitz3D, a DLL is used, "DLPORTIO.dll" and also the blitz userlib "dlportio.decls",
; containing the following declarations:

;.lib "dlportio.dll"

;DlPortReadPortUchar%( port% )
;DlPortReadPortUshort%( port% )
;DlPortReadPortUlong%( port% )

;DlPortReadPortBufferUchar( port%, buffer*, count% )
;DlPortReadPortBufferUshort( port%, buffer*, count% )
;DlPortReadPortBufferUlong( port%, buffer*, count% )

;DlPortWritePortUchar( port%, value% )
;DlPortWritePortUshort( port%, value% )
;DlPortWritePortUlong( port%, value% )

;DlPortWritePortBufferUchar( port%, buffer*, count% )
;DlPortWritePortBufferUshort( port%, buffer*, count% )
;DlPortWritePortBufferUlong( port%, buffer*, count% )

; Special Thanks to the author of PortIO.dll for BLitz3D, right now I cannot find his name, not even
; in the original portio.zip, so please excuse. 



; This Demo Program is not a functional CNC Tool, it only shows how to call the
; Stepper Motors. Implementing a simple CNC Mill is pretty straight forward when
; you consider X and Z as the working space, and Y as the heights taken from a heightmap,
; pixel by pixel, or from a 3D scene using LinePick or CameraPick on a certain field of view.

; You may however add usage of Start- and Stop-Speed for the stepper motors, because this
; will allow to operate a machine with less vibration and less material stress, including 
; stress for the motors. There are several books and Websites about CNC programming.


Graphics 800,600,32,2 ; set up graphics
SetBuffer BackBuffer()



Global max_motor=2  ;(0=x, 1=y, 2=z)


Dim invert_axis(max_motor) ; here you can easily reverse a motors logic (0=normal operation, 1=reverse operation)
invert_axis(0)=1
invert_axis(1)=0
invert_axis(2)=0
;(these are the proxxon mf70 cnc settings)


Global LPT=$378 ; this is the parallel port of your machine. The number may differ, see "Resources" in the device manager's parallel port settings.
                ; and/or Bios. Note: you may have to disable the "bidirectional" parallel port mode in the bios by choosing eg. "unidirectional".


Dim phase_A(max_motor,8)    ; In these arrays we'll store the amplitude modulation curves
Dim phase_B(max_motor,8)
Dim phase_count#(max_motor) ; Used as modulation curves index





; The SMC800 allows for 3 levels of power: 20%, 50% and 100%
; In fact this will only set the curves to something more flat.
SetMotorPower(0,100) ; use 20, 50 or 100
SetMotorPower(1,100)
SetMotorPower(2,100)









Global Special_msg$="" ; used to tell the user some infos







; Defining some standard speeds, where only the Reference Travel speed is used in this example.
cnc_MainSpeed#=0.25
cnc_StartSpeed#=0.125
cnc_StopSpeed#=0.125
cnc_RefSpeed#=0.5
; Note. these speeds represent the frequency in kHz. 1.0 is the max! Use a fraction of 1.0 (1.0, 0.5, 0.25, 0.2,  0.125 ...)






;************************************     Main Test Loop     ***************************************
While KeyDown(1)=0
 Cls
 Text 0,0, "Cursor= left right, back , forward"
 Text 0,16,"PgUp/Dn= up, down"
 Text 0,32,"H= Home all Motors"
 Text 0,48,"Esc= Exit"
 Text 0,64, Special_msg$ ;  ; (Number of pulses for last MotorHome(): always Z axis in this demo, because it was the last one)
 Flip

 ; manual control of the 3 Motors
 k=203
 If KeyDown(k)
  While KeyDown(k)
   sync_timer(cnc_RefSpeed#)
   status = call_motor(0, 1) ; x minus
  Wend
 EndIf
 k=205
 If KeyDown(k)
  While KeyDown(k)
   sync_timer(cnc_RefSpeed#)
   status = call_motor(0, 0) ; x plus
  Wend
 EndIf
 k=208
 If KeyDown(k)
  While KeyDown(k)
   sync_timer(cnc_RefSpeed#)
   status = call_motor(2, 1) ; z minus
  Wend
 EndIf
 k=200
 If KeyDown(k)
  While KeyDown(k)
   sync_timer(cnc_RefSpeed#)
   status = call_motor(2, 0) ; z plus
  Wend
 EndIf
 k=209
 If KeyDown(k)
  While KeyDown(k)
   sync_timer(cnc_RefSpeed#)
   status = call_motor(1, 1) ; y minus
  Wend
 EndIf
 k=201
 If KeyDown(k)
  While KeyDown(k)
   sync_timer(cnc_RefSpeed#)
   status = call_motor(1, 0) ; y plus
  Wend
 EndIf

 ; home...
 k=35
 If KeyHit(35) Then
  ; (Note: it's neccessary to first Home the vertical motor!)
  MotorHome(1,cnc_RefSpeed#)
  MotorHome(0,cnc_RefSpeed#)
  MotorHome(2,cnc_RefSpeed#)
 EndIf

Wend
End














Function call_motor(motor, direction)

 ; motor choice: 0,1,2
 ; direction: 0 (away from home) or 1 (back to home)


 STB = LPT + 2 ; parallel port status port
 stb_byte = inp(STB) ; (we need this later for a printerport handshake)

 ;*** assembling the data byte for the SMC ***
 pc=Floor(phase_count(motor))
 byte=(phase_A(motor,pc) Or phase_B(motor,pc))  Or (motor Shl 6)


 ;*** Sending Data to Controller ***
 outp(LPT, (byte))   ; Actually send the Amplitude data and Motor Selection


 ; *** LPT Handshake *** 
 ;The following low level handshake may be a relict of needle printer times, 
 ;but it seems it's still neccessary with a lot of of parallel port devices, 
 ;such as this control card.
 outp(STB, stb_byte Or 1) ; gotta do a quick handshake to the "printer"
 ; eventually have to add a small delay here: (tho seems to work without it)
 ; For i=0 To 1000
 ; Next
 outp(STB, stb_byte And (~1)) ; note: "~1" means "Not(1)", or simply "254"
 ; end of LPT handshake


 ; Increment/Loop Phase curve index
 If direction=invert_axis(motor)
  phase_count(motor)=phase_count(motor)+1.0
  If phase_count(motor)>=8.0 Then phase_count(motor)=phase_count(motor)-8.0
 Else
  phase_count(motor)=phase_count(motor)-1.0
  If phase_count(motor)<0.0 Then phase_count(motor)=phase_count(motor)+8.0
 EndIf



 ; *** Checking the Contact Switch ***
 ; At the IO Port LPT+1 we read the control byte of the parallel port. Bit number 8
 ; will signal if the contact switch is closed or not:
 ; Bit is set: no contact
 ; Bit is zero: contact!

 CTR = LPT + 1 ; parallel port control port
 ctr_byte = inp(CTR)
 Return ctr_byte  ; bit 8 of this byte is the low active contact switch
End Function







; Functions to utilize BIOS input/output, to access the printer port on the hardware level.
Function outp(adress, value)
 DlPortWritePortUchar(adress , value )
End Function


Function inp(adress)
 value = DlPortReadPortUchar%( adress )
 Return value
End Function









Function MotorHome(motor,speed#=0.5)
 reached_ref=0
 count_pulses=0
 While reached_ref=0
  ; drive slide to contact switch:
  code=sync_timer(speed#) ; this will sync the motor calling with a certain frequency (Hz = speed * 1000)
  If code=777 Then:Return code:EndIf ; (aborted by user?)
  status = call_motor(motor, 1)
  count_pulses=count_pulses+1
  If (status And 128)=0  ;(reached contact switch ?)
   While (status And 128)=0 ; drive a little back, again out of contact 
    code=sync_timer(speed#)
    If code=777 Then:Return code:EndIf
    status = call_motor(motor, 0)
    count_pulses=count_pulses-1
   Wend
   counter=0
   While counter<1600 ; add some extra mm (eg. ~2 turns with 1.8deg steppermotors and M5 lathe axis)
    code=sync_timer(speed#)
    If code=777 Then:Return code:EndIf
    status = call_motor(motor, 0)
    counter=counter+1
    count_pulses=count_pulses-1
   Wend
   reached_ref=1
  EndIf
 Wend
 Special_msg$="Last Motor Home required: "+ count_pulses +" Pulses." ; this is useful for metric calibrations!
 Return 0
End Function






; This function will wait for a certain while in order to synchronize
; the calling of the motors with the choosen speed (Hz=speed*1000)
Function sync_timer(local_speed#=1.0)
 secs=(1.0/local_speed#)-1
 t=MilliSecs()+secs
 While t >= MilliSecs()
  If KeyDown(57) ; allow space key to abort things
   Delay 200
   FlushKeys()
   Return 777  ; return some abort code
  EndIf
 Wend
 Return 0 ; return zero for "successful execution"
End Function





Function SetMotorPower(motor,power)
 ; (For the Bits Description see also SMC800 Manual)
 If motor<0 Then motor=0
 If motor>max_motor Then motor=max_motor

 ; 20% power (set default anyway)
 phase_A(motor,0)= %111
 phase_A(motor,1)= %110
 phase_A(motor,2)= %110
 phase_A(motor,3)= %110
 phase_A(motor,4)= %011
 phase_A(motor,5)= %010
 phase_A(motor,6)= %010
 phase_A(motor,7)= %010
 phase_A(motor,8)= phase_A(motor,0)

 phase_B(motor,0)= %110000
 phase_B(motor,1)= %110000
 phase_B(motor,2)= %011000
 phase_B(motor,3)= %010000
 phase_B(motor,4)= %010000
 phase_B(motor,5)= %010000
 phase_B(motor,6)= %111000
 phase_B(motor,7)= %110000
 phase_B(motor,8)= phase_B(motor,0)

 If power=50 Then
  ; 50% power
  phase_A(motor,0)= %111
  phase_A(motor,1)= %101
  phase_A(motor,2)= %101
  phase_A(motor,3)= %101
  phase_A(motor,4)= %011
  phase_A(motor,5)= %001
  phase_A(motor,6)= %001
  phase_A(motor,7)= %001
  phase_A(motor,8)= phase_A(motor,0)

  phase_B(motor,0)= %101000
  phase_B(motor,1)= %101000
  phase_B(motor,2)= %011000
  phase_B(motor,3)= %001000
  phase_B(motor,4)= %001000
  phase_B(motor,5)= %001000
  phase_B(motor,6)= %111000
  phase_B(motor,7)= %101000
  phase_B(motor,8)= phase_B(motor,0)
 EndIf
 If power=100 Then
  ; 100% power
  phase_A(motor,0)= %111
  phase_A(motor,1)= %100
  phase_A(motor,2)= %100
  phase_A(motor,3)= %100
  phase_A(motor,4)= %011
  phase_A(motor,5)= %000
  phase_A(motor,6)= %000
  phase_A(motor,7)= %000
  phase_A(motor,8)= phase_A(motor,0)

  phase_B(motor,0)= %100000
  phase_B(motor,1)= %100000
  phase_B(motor,2)= %011000
  phase_B(motor,3)= %000000 
  phase_B(motor,4)= %000000
  phase_B(motor,5)= %000000
  phase_B(motor,6)= %111000
  phase_B(motor,7)= %100000
  phase_B(motor,8)= phase_B(motor,0)
 EndIf

End Function
