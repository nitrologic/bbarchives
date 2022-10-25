; ID: 847
; Author: Ziltch
; Date: 2003-12-01 08:05:23
; Title: Windows Audio Mixer Blitz Functions
; Description: Control windows audio mixer

; Windows Audio Mixer Blitz Functions
; audiomixer.bb
; v1  ADAmor ZILTCH 31 July 2003

;---------------------- USERLIBS DECLS NEEDED
;  .lib "Kernel32.dll"
;  CopyStructFromPtr(struct*,ptr%,cb%):"RtlMoveMemory"
;  CopyPtrFromStruct(ptr%, struct*, cb%):"RtlMoveMemory"
;  GlobalAlloc%(wFlags%, dwBytes%)
;  GlobalLock%(hmem%)
;  GlobalFree%(hmem%)

;----------------------
;  .lib "winmm.dll"
;  mixerOpen%(phmx%, uMxId%, dwCallback%, dwInstance%, fdwOpen%)
;  mixerClose%(hmx%)

;  mixerGetControlDetails%(hmxobj%, pmxcd*, fdwDetails%):"mixerGetControlDetailsA"
;  mixerGetDevCaps%(uMxId%, pmxcaps*, cbmxcaps%): "mixerGetDevCapsA"
;  mixerGetID%(hmxobj%, pumxID%, fdwId%)
;  mixerGetLineControls%(hmxobj%, pmxlc*, fdwControls%): "mixerGetLineControlsA"
;  mixerGetLineInfo%(hmxobj%, pmxl*, fdwInfo%):"mixerGetLineInfoA"
;  mixerGetNumDevs%()
;  mixerMessage%(hmx%, uMsg%, dwParam1%, dwParam2%)
;  mixerSetControlDetails%(hmxobj%, pmxcd*, fdwDetails%)
;----------------------
;  .lib "Kernel32.dll"
;  CopyStructFromPtr(struct*,ptr%,cb%):"RtlMoveMemory"
;  CopyPtrFromStruct(ptr%, struct*, cb%):"RtlMoveMemory"
;  GlobalAlloc%(wFlags%, dwBytes%)
;  GlobalLock%(hmem%)
;  GlobalFree%(hmem%)

;----------------------

Const MMSYSERR_NOERROR       = 0
Const MMSYSERR_ERROR         = 1  ; unspecified error
Const MMSYSERR_BADDEVICEID   = 2  ; device ID out of range
Const MMSYSERR_NOTENABLED    = 3  ; driver failed enable
Const MMSYSERR_ALLOCATED     = 4  ; device already allocated
Const MMSYSERR_INVALHANDLE   = 5  ; device handle is invalid
Const MMSYSERR_NODRIVER      = 6  ; no device driver present
Const MMSYSERR_NOMEM         = 7  ; memory allocation error
Const MMSYSERR_NOTSUPPORTED  = 8  ; function isn't supported
Const MMSYSERR_BADERRNUM     = 9  ; error value out of range
Const MMSYSERR_INVALFLAG     = 10 ; invalid flag passed
Const MMSYSERR_INVALPARAM    = 11 ; invalid parameter passed
Const MMSYSERR_HANDLEBUSY    = 12 ; handle being used

Const MAXPNAMELEN            = 32
Const MIXER_LONG_NAME_CHARS  = 64
Const MIXER_SHORT_NAME_CHARS = 16

Const MIXER_GETLINEINFOF_DESTINATION       = $00000000
Const MIXER_GETLINEINFOF_SOURCE            = $00000001
Const MIXER_GETLINEINFOF_LINEID            = $00000002
Const MIXER_GETLINEINFOF_COMPONENTTYPE     = $00000003
Const MIXER_GETLINEINFOF_TARGETTYPE        = $00000004

Const MIXER_GETCONTROLDETAILSF_VALUE       = $0

Const MIXER_GETLINECONTROLSF_ALL           = $0
Const MIXER_GETLINECONTROLSF_ONEBYID       = $1
Const MIXER_GETLINECONTROLSF_ONEBYTYPE     = $2

;--  MIXERCONTROL.fdwControl

Const MIXERCONTROL_CONTROLF_UNIFORM        = $00000001
Const MIXERCONTROL_CONTROLF_MULTIPLE       = $00000002
Const MIXERCONTROL_CONTROLF_DISABLED       = $80000000



;--  MIXERCONTROL_CONTROLTYPE_xxx building block defines


Const MIXERCONTROL_CT_CLASS_MASK           = $F0000000
Const MIXERCONTROL_CT_CLASS_CUSTOM         = $00000000
Const MIXERCONTROL_CT_CLASS_METER          = $10000000
Const MIXERCONTROL_CT_CLASS_SWITCH         = $20000000
Const MIXERCONTROL_CT_CLASS_NUMBER         = $30000000
Const MIXERCONTROL_CT_CLASS_SLIDER         = $40000000
Const MIXERCONTROL_CT_CLASS_FADER          = $50000000
Const MIXERCONTROL_CT_CLASS_TIME           = $60000000
Const MIXERCONTROL_CT_CLASS_LIST           = $70000000


Const MIXERCONTROL_CT_SUBCLASS_MASK        = $0F000000

Const MIXERCONTROL_CT_SC_SWITCH_BOOLEAN    = $00000000
Const MIXERCONTROL_CT_SC_SWITCH_BUTTON     = $01000000

Const MIXERCONTROL_CT_SC_METER_POLLED      = $00000000

Const MIXERCONTROL_CT_SC_TIME_MICROSECS    = $00000000
Const MIXERCONTROL_CT_SC_TIME_MILLISECS    = $01000000

Const MIXERCONTROL_CT_SC_LIST_SINGLE       = $00000000
Const MIXERCONTROL_CT_SC_LIST_MULTIPLE     = $01000000


Const MIXERCONTROL_CT_UNITS_MASK           = $00FF0000
Const MIXERCONTROL_CT_UNITS_CUSTOM         = $00000000
Const MIXERCONTROL_CT_UNITS_BOOLEAN        = $00010000
Const MIXERCONTROL_CT_UNITS_SIGNED         = $00020000
Const MIXERCONTROL_CT_UNITS_UNSIGNED       = $00030000
Const MIXERCONTROL_CT_UNITS_DECIBELS       = $00040000 ;in 10ths
Const MIXERCONTROL_CT_UNITS_PERCENT        = $00050000 ;in 10ths



;--  Commonly used control types for specifying MIXERCONTROL.dwControlType


Const MIXERCONTROL_CONTROLTYPE_CUSTOM        = (MIXERCONTROL_CT_CLASS_CUSTOM Or MIXERCONTROL_CT_UNITS_CUSTOM)
Const MIXERCONTROL_CONTROLTYPE_BOOLEANMETER  = (MIXERCONTROL_CT_CLASS_METER Or MIXERCONTROL_CT_SC_METER_POLLED Or MIXERCONTROL_CT_UNITS_BOOLEAN)
Const MIXERCONTROL_CONTROLTYPE_SIGNEDMETER   = (MIXERCONTROL_CT_CLASS_METER Or MIXERCONTROL_CT_SC_METER_POLLED Or MIXERCONTROL_CT_UNITS_SIGNED)
Const MIXERCONTROL_CONTROLTYPE_PEAKMETER     = (MIXERCONTROL_CONTROLTYPE_SIGNEDMETER + 1)
Const MIXERCONTROL_CONTROLTYPE_UNSIGNEDMETER = (MIXERCONTROL_CT_CLASS_METER Or MIXERCONTROL_CT_SC_METER_POLLED Or MIXERCONTROL_CT_UNITS_UNSIGNED)
Const MIXERCONTROL_CONTROLTYPE_BOOLEAN       = (MIXERCONTROL_CT_CLASS_SWITCH Or MIXERCONTROL_CT_SC_SWITCH_BOOLEAN Or MIXERCONTROL_CT_UNITS_BOOLEAN)
Const MIXERCONTROL_CONTROLTYPE_ONOFF         = (MIXERCONTROL_CONTROLTYPE_BOOLEAN + 1)
Const MIXERCONTROL_CONTROLTYPE_MUTE          = (MIXERCONTROL_CONTROLTYPE_BOOLEAN + 2)
Const MIXERCONTROL_CONTROLTYPE_MONO          = (MIXERCONTROL_CONTROLTYPE_BOOLEAN + 3)
Const MIXERCONTROL_CONTROLTYPE_LOUDNESS      = (MIXERCONTROL_CONTROLTYPE_BOOLEAN + 4)
Const MIXERCONTROL_CONTROLTYPE_STEREOENH     = (MIXERCONTROL_CONTROLTYPE_BOOLEAN + 5)
Const MIXERCONTROL_CONTROLTYPE_BASS_BOOST    = (MIXERCONTROL_CONTROLTYPE_BOOLEAN + $00002277)
Const MIXERCONTROL_CONTROLTYPE_BUTTON        = (MIXERCONTROL_CT_CLASS_SWITCH Or MIXERCONTROL_CT_SC_SWITCH_BUTTON Or MIXERCONTROL_CT_UNITS_BOOLEAN)
Const MIXERCONTROL_CONTROLTYPE_DECIBELS      = (MIXERCONTROL_CT_CLASS_NUMBER Or MIXERCONTROL_CT_UNITS_DECIBELS)
Const MIXERCONTROL_CONTROLTYPE_SIGNED        = (MIXERCONTROL_CT_CLASS_NUMBER Or MIXERCONTROL_CT_UNITS_SIGNED)
Const MIXERCONTROL_CONTROLTYPE_UNSIGNED      = (MIXERCONTROL_CT_CLASS_NUMBER Or MIXERCONTROL_CT_UNITS_UNSIGNED)
Const MIXERCONTROL_CONTROLTYPE_PERCENT       = (MIXERCONTROL_CT_CLASS_NUMBER Or MIXERCONTROL_CT_UNITS_PERCENT)
Const MIXERCONTROL_CONTROLTYPE_SLIDER        = (MIXERCONTROL_CT_CLASS_SLIDER Or MIXERCONTROL_CT_UNITS_SIGNED)
Const MIXERCONTROL_CONTROLTYPE_PAN           = (MIXERCONTROL_CONTROLTYPE_SLIDER + 1)
Const MIXERCONTROL_CONTROLTYPE_QSOUNDPAN     = (MIXERCONTROL_CONTROLTYPE_SLIDER + 2)
Const MIXERCONTROL_CONTROLTYPE_FADER         = (MIXERCONTROL_CT_CLASS_FADER Or MIXERCONTROL_CT_UNITS_UNSIGNED)
Const MIXERCONTROL_CONTROLTYPE_VOLUME        = (MIXERCONTROL_CONTROLTYPE_FADER + 1)
Const MIXERCONTROL_CONTROLTYPE_BASS          = (MIXERCONTROL_CONTROLTYPE_FADER + 2)
Const MIXERCONTROL_CONTROLTYPE_TREBLE        = (MIXERCONTROL_CONTROLTYPE_FADER + 3)
Const MIXERCONTROL_CONTROLTYPE_EQUALIZER     = (MIXERCONTROL_CONTROLTYPE_FADER + 4)
Const MIXERCONTROL_CONTROLTYPE_SINGLESELECT  = (MIXERCONTROL_CT_CLASS_LIST Or MIXERCONTROL_CT_SC_LIST_SINGLE Or MIXERCONTROL_CT_UNITS_BOOLEAN)
Const MIXERCONTROL_CONTROLTYPE_MUX           = (MIXERCONTROL_CONTROLTYPE_SINGLESELECT + 1)
Const MIXERCONTROL_CONTROLTYPE_MULTIPLESELECT= (MIXERCONTROL_CT_CLASS_LIST Or MIXERCONTROL_CT_SC_LIST_MULTIPLE Or MIXERCONTROL_CT_UNITS_BOOLEAN)
Const MIXERCONTROL_CONTROLTYPE_MIXER         = (MIXERCONTROL_CONTROLTYPE_MULTIPLESELECT + 1)
Const MIXERCONTROL_CONTROLTYPE_MICROTIME     = (MIXERCONTROL_CT_CLASS_TIME Or MIXERCONTROL_CT_SC_TIME_MICROSECS Or MIXERCONTROL_CT_UNITS_UNSIGNED)
Const MIXERCONTROL_CONTROLTYPE_MILLITIME     = (MIXERCONTROL_CT_CLASS_TIME Or MIXERCONTROL_CT_SC_TIME_MILLISECS Or MIXERCONTROL_CT_UNITS_UNSIGNED)

.CONTROLTYPES
Data "MIXERCONTROL_CONTROLTYPE_CUSTOM       ",(MIXERCONTROL_CT_CLASS_CUSTOM Or MIXERCONTROL_CT_UNITS_CUSTOM)
Data "MIXERCONTROL_CONTROLTYPE_BOOLEANMETER ",(MIXERCONTROL_CT_CLASS_METER Or MIXERCONTROL_CT_SC_METER_POLLED Or MIXERCONTROL_CT_UNITS_BOOLEAN)
Data "MIXERCONTROL_CONTROLTYPE_SIGNEDMETER  ",(MIXERCONTROL_CT_CLASS_METER Or MIXERCONTROL_CT_SC_METER_POLLED Or MIXERCONTROL_CT_UNITS_SIGNED)
Data "MIXERCONTROL_CONTROLTYPE_PEAKMETER    ",(MIXERCONTROL_CONTROLTYPE_SIGNEDMETER + 1)
Data "MIXERCONTROL_CONTROLTYPE_UNSIGNEDMETER",(MIXERCONTROL_CT_CLASS_METER Or MIXERCONTROL_CT_SC_METER_POLLED Or MIXERCONTROL_CT_UNITS_UNSIGNED)
Data "MIXERCONTROL_CONTROLTYPE_BOOLEAN      ",(MIXERCONTROL_CT_CLASS_SWITCH Or MIXERCONTROL_CT_SC_SWITCH_BOOLEAN Or MIXERCONTROL_CT_UNITS_BOOLEAN)
Data "MIXERCONTROL_CONTROLTYPE_ONOFF        ",(MIXERCONTROL_CONTROLTYPE_BOOLEAN + 1)
Data "MIXERCONTROL_CONTROLTYPE_MUTE         ",(MIXERCONTROL_CONTROLTYPE_BOOLEAN + 2)
Data "MIXERCONTROL_CONTROLTYPE_MONO         ",(MIXERCONTROL_CONTROLTYPE_BOOLEAN + 3)
Data "MIXERCONTROL_CONTROLTYPE_LOUDNESS     ",(MIXERCONTROL_CONTROLTYPE_BOOLEAN + 4)
Data "MIXERCONTROL_CONTROLTYPE_STEREOENH    ",(MIXERCONTROL_CONTROLTYPE_BOOLEAN + 5)
Data "MIXERCONTROL_CONTROLTYPE_BASS_BOOST   ",(MIXERCONTROL_CONTROLTYPE_BOOLEAN + $00002277)
Data "MIXERCONTROL_CONTROLTYPE_BUTTON       ",(MIXERCONTROL_CT_CLASS_SWITCH Or MIXERCONTROL_CT_SC_SWITCH_BUTTON Or MIXERCONTROL_CT_UNITS_BOOLEAN)
Data "MIXERCONTROL_CONTROLTYPE_DECIBELS     ",(MIXERCONTROL_CT_CLASS_NUMBER Or MIXERCONTROL_CT_UNITS_DECIBELS)
Data "MIXERCONTROL_CONTROLTYPE_SIGNED       ",(MIXERCONTROL_CT_CLASS_NUMBER Or MIXERCONTROL_CT_UNITS_SIGNED)
Data "MIXERCONTROL_CONTROLTYPE_UNSIGNED     ",(MIXERCONTROL_CT_CLASS_NUMBER Or MIXERCONTROL_CT_UNITS_UNSIGNED)
Data "MIXERCONTROL_CONTROLTYPE_PERCENT      ",(MIXERCONTROL_CT_CLASS_NUMBER Or MIXERCONTROL_CT_UNITS_PERCENT)
Data "MIXERCONTROL_CONTROLTYPE_SLIDER       ",(MIXERCONTROL_CT_CLASS_SLIDER Or MIXERCONTROL_CT_UNITS_SIGNED)
Data "MIXERCONTROL_CONTROLTYPE_PAN          ",(MIXERCONTROL_CONTROLTYPE_SLIDER + 1)
Data "MIXERCONTROL_CONTROLTYPE_QSOUNDPAN    ",(MIXERCONTROL_CONTROLTYPE_SLIDER + 2)
Data "MIXERCONTROL_CONTROLTYPE_FADER        ",(MIXERCONTROL_CT_CLASS_FADER Or MIXERCONTROL_CT_UNITS_UNSIGNED)
Data "MIXERCONTROL_CONTROLTYPE_VOLUME       ",(MIXERCONTROL_CONTROLTYPE_FADER + 1)
Data "MIXERCONTROL_CONTROLTYPE_BASS         ",(MIXERCONTROL_CONTROLTYPE_FADER + 2)
Data "MIXERCONTROL_CONTROLTYPE_TREBLE       ",(MIXERCONTROL_CONTROLTYPE_FADER + 3)
Data "MIXERCONTROL_CONTROLTYPE_EQUALIZER    ",(MIXERCONTROL_CONTROLTYPE_FADER + 4)
Data "MIXERCONTROL_CONTROLTYPE_SINGLESELECT ",(MIXERCONTROL_CT_CLASS_LIST Or MIXERCONTROL_CT_SC_LIST_SINGLE Or MIXERCONTROL_CT_UNITS_BOOLEAN)
Data "MIXERCONTROL_CONTROLTYPE_MUX          ",(MIXERCONTROL_CONTROLTYPE_SINGLESELECT + 1)
Data "MIXERCONTROL_CONTROLTYPE_MULTIPLESELECT", (MIXERCONTROL_CT_CLASS_LIST Or MIXERCONTROL_CT_SC_LIST_MULTIPLE Or MIXERCONTROL_CT_UNITS_BOOLEAN)
Data "MIXERCONTROL_CONTROLTYPE_MIXER        ",(MIXERCONTROL_CONTROLTYPE_MULTIPLESELECT + 1)
Data "MIXERCONTROL_CONTROLTYPE_MICROTIME    ",(MIXERCONTROL_CT_CLASS_TIME Or MIXERCONTROL_CT_SC_TIME_MICROSECS Or MIXERCONTROL_CT_UNITS_UNSIGNED)
Data "MIXERCONTROL_CONTROLTYPE_MILLITIME    ",(MIXERCONTROL_CT_CLASS_TIME Or MIXERCONTROL_CT_SC_TIME_MILLISECS Or MIXERCONTROL_CT_UNITS_UNSIGNED)

Restore CONTROLTYPES
Dim CONTROLTYPES_Name$(31)
Dim CONTROLTYPES_Val$(31)
For count = 1 To 31
  Read CONTROLTYPES_Name$(count),CONTROLTYPES_Val$(count)
Next

; -- Component types for mixer destinations and sources

Const MIXERLINE_COMPONENTTYPE_DST_FIRST = $0
Const MIXERLINE_COMPONENTTYPE_DST_UNDEFINED   = MIXERLINE_COMPONENTTYPE_DST_FIRST + 0
Const MIXERLINE_COMPONENTTYPE_DST_DIGITAL     = MIXERLINE_COMPONENTTYPE_DST_FIRST + 1
Const MIXERLINE_COMPONENTTYPE_DST_LINE        = MIXERLINE_COMPONENTTYPE_DST_FIRST + 2
Const MIXERLINE_COMPONENTTYPE_DST_MONITOR     = MIXERLINE_COMPONENTTYPE_DST_FIRST + 3
Const MIXERLINE_COMPONENTTYPE_DST_SPEAKERS    = MIXERLINE_COMPONENTTYPE_DST_FIRST + 4
Const MIXERLINE_COMPONENTTYPE_DST_HEADPHONES  = MIXERLINE_COMPONENTTYPE_DST_FIRST + 5
Const MIXERLINE_COMPONENTTYPE_DST_TELEPHONE   = MIXERLINE_COMPONENTTYPE_DST_FIRST + 6
Const MIXERLINE_COMPONENTTYPE_DST_WAVEIN      = MIXERLINE_COMPONENTTYPE_DST_FIRST + 7
Const MIXERLINE_COMPONENTTYPE_DST_VOICEIN     = MIXERLINE_COMPONENTTYPE_DST_FIRST + 8
Const MIXERLINE_COMPONENTTYPE_DST_LAST        = MIXERLINE_COMPONENTTYPE_DST_FIRST + 8

Const MIXERLINE_COMPONENTTYPE_SRC_FIRST = $1000
Const MIXERLINE_COMPONENTTYPE_SRC_UNDEFINED   = MIXERLINE_COMPONENTTYPE_SRC_FIRST + 0
Const MIXERLINE_COMPONENTTYPE_SRC_DIGITAL     = MIXERLINE_COMPONENTTYPE_SRC_FIRST + 1
Const MIXERLINE_COMPONENTTYPE_SRC_LINE        = MIXERLINE_COMPONENTTYPE_SRC_FIRST + 2
Const MIXERLINE_COMPONENTTYPE_SRC_MICROPHONE  = MIXERLINE_COMPONENTTYPE_SRC_FIRST + 3
Const MIXERLINE_COMPONENTTYPE_SRC_SYNTHESIZER = MIXERLINE_COMPONENTTYPE_SRC_FIRST + 4
Const MIXERLINE_COMPONENTTYPE_SRC_COMPACTDISC = MIXERLINE_COMPONENTTYPE_SRC_FIRST + 5
Const MIXERLINE_COMPONENTTYPE_SRC_TELEPHONE   = MIXERLINE_COMPONENTTYPE_SRC_FIRST + 6
Const MIXERLINE_COMPONENTTYPE_SRC_PCSPEAKER   = MIXERLINE_COMPONENTTYPE_SRC_FIRST + 7
Const MIXERLINE_COMPONENTTYPE_SRC_WAVEOUT     = MIXERLINE_COMPONENTTYPE_SRC_FIRST + 8
Const MIXERLINE_COMPONENTTYPE_SRC_AUXILIARY   = MIXERLINE_COMPONENTTYPE_SRC_FIRST + 9
Const MIXERLINE_COMPONENTTYPE_SRC_ANALOG      = MIXERLINE_COMPONENTTYPE_SRC_FIRST + 10
Const MIXERLINE_COMPONENTTYPE_SRC_LAST        = MIXERLINE_COMPONENTTYPE_SRC_FIRST + 10


; --  MIXERLINE.Target.dwType

Const MIXERLINE_TARGETTYPE_UNDEFINED    = 0
Const MIXERLINE_TARGETTYPE_WAVEOUT      = 1
Const MIXERLINE_TARGETTYPE_WAVEIN       = 2
Const MIXERLINE_TARGETTYPE_MIDIOUT      = 3
Const MIXERLINE_TARGETTYPE_MIDIIN       = 4
Const MIXERLINE_TARGETTYPE_AUX          = 5

Type MIXERCAPS
  Field wMid%                             ;0   manufacturer id
  Field wPid%                             ;4   product id
  Field vDriverVersion%                   ;8   version of the driver
  Field szPname$                          ;12  product name
  Field fdwSupport%                       ;44  misc. support bits
  Field cDestinations%                    ;48  count of destinations
End Type                                  ;Size 52

Type MIXERCONTROL
  Field cbStruct%                  ;0    size in Byte of MIXERCONTROL
  Field dwControlID%               ;4    unique control id for mixer device
  Field dwControlType%             ;8    MIXERCONTROL_CONTROLTYPE_xxx
  Field fdwControl%                ;12   MIXERCONTROL_CONTROLF_xxx
  Field cMultipleItems%            ;16   if MIXERCONTROL_CONTROLF_MULTIPLE
  Field szShortName$               ;20   short name of control
  Field szName$                    ;36   long name of control
  Field lMinimum%                  ;100  Minimum value
  Field lMaximum%                  ;104  Maximum value
;  field reserved()%               ;108  reserved structure space
End Type                           ;size 148

Type MIXERCONTROLDETAILS
  Field cbStruct%              ;0   size in Byte of MIXERCONTROLDETAILS
  Field dwControlID%           ;4   control id to get/set details on
  Field cChannels%             ;8   number of channels in paDetails array
  Field item%                  ;12  hwndOwner or cMultipleItems
  Field cbDetails%             ;16  size of _one_ details_XX struct
  Field paDetails%             ;20  pointer to array of details_XX structs
End Type                       ;size 24

Type MIXERCONTROLDETAILS_UNSIGNED
  Field dwValue%               ;0  value of the control
End Type                       ;size 4

Type MIXERLINE
  Field cbStruct%                      ;0   size of MIXERLINE structure
  Field dwDestination%                 ;4   zero based destination index
  Field dwSource%                      ;8   zero based source index (if source)
  Field dwLineID%                      ;12  unique line id for mixer device
  Field fdwLine%                       ;16  state/information about line
  Field dwUser%                        ;20  driver specific information
  Field dwComponentType%               ;24  component type line connects to
  Field cChannels%                     ;28  number of channels line supports
  Field cConnections%                  ;32  number of connections (possible)
  Field cControls%                     ;36  number of controls at this line
  Field szShortName$                   ;40
  Field szName$                        ;56
  Field dwType%                        ;120
  Field dwDeviceID%                    ;124
  Field wMid%                          ;128
  Field wPid%                          ;130
  Field vDriverVersion%                ;132
  Field szPname$                       ;136
End Type                               ;size 168

Type MIXERLINECONTROLS
  Field cbStruct%              ;0   size in Byte of MIXERLINECONTROLS
  Field dwLineID%              ;4   line id (from MIXERLINE.dwLineID)
                               ;    MIXER_GETLINECONTROLSF_ONEBYID or
  Field dwControl%             ;8   MIXER_GETLINECONTROLSF_ONEBYTYPE
  Field cControls%             ;12  count of controls pmxctrl points to
  Field cbmxctrl%              ;16  size in Byte of _one_ MIXERCONTROL
  Field pamxctrl%              ;20  pointer to first MIXERCONTROL array
End Type                       ;size 24

Type MIXERCONTROLDETAILS_LISTTEXT
  Field  dwParam1             ;0
  Field  dwParam2             ;4
  Field  szname$              ;8
End Type                      ;len 72



Function GetControlName$(ControlNum)

  Local count

  For count = 1 To 31
     If controlnum = CONTROLTYPES_Val$(count) Then
      Return CONTROLTYPES_Name$(count)
    End If
  Next
  Return "Control type unkown " + Controlnum
End Function


Function GetMixerControl(hmixer%, componentType%, ctrlType%, mxc.MIXERCONTROL,mode=0)

; This function attempts to obtain a mixer control.
; Returns True if successful.
;mode 1 uses lineID instead of control type

   Local mxlc.MIXERLINECONTROLS = New MIXERLINECONTROLS
   Local mxl.MIXERLINE = New MIXERLINE
   Local hmem%
   Local rc%
   Local mxlcbank = CreateBank(24)
   Local mxlbank  = CreateBank(168)

   mxl\cbStruct = BankSize(mxlbank)
   mxl\dwComponentType = componentType

   PokeInt(mxlbank,0,BankSize(mxlbank))

   Select mode
     Case 0; Obtain a line corresponding to the component type
       PokeInt(mxlbank,24,componentType)
       rc = mixerGetLineInfo(hmixer, mxlbank, MIXER_GETLINEINFOF_COMPONENTTYPE)
     Case 1; Obtain a line from its lineID
       PokeInt(mxlbank,8,0)
       PokeInt(mxlbank,12,componentType)
       rc=mixerGetLineInfo(hMixer, mxlbank, MIXER_GETLINEINFOF_LINEID)
   End Select

   If (MMSYSERR_NOERROR <> rc) Then
     DebugLog "error "+rc+" mixer get line info failed. For lineID/componentType of "+componentType
   Else
     mxl\dwDestination%   = PeekInt(mxlbank,4);   zero based destination index
     mxl\dwSource%        = PeekInt(mxlbank,8);   zero based source index (if source)
     mxl\dwLineID%        = PeekInt(mxlbank,12);  unique line id for mixer device
     mxl\fdwLine%         = PeekInt(mxlbank,16);  state/information about line
     mxl\dwUser%          = PeekInt(mxlbank,20);  driver specific information
     mxl\cChannels%       = PeekInt(mxlbank,28);  number of channels line supports
     mxl\cConnections%    = PeekInt(mxlbank,32);  number of connections (possible)
     mxl\cControls%       = PeekInt(mxlbank,36);  number of controls at this line
     mxl\szShortName$     = peekstr(mxlbank,16,40)
     mxl\szName$          = peekstr(mxlbank,64,56)
     mxl\dwType%          = PeekInt(mxlbank, 120)
     mxl\dwDeviceID%      = PeekInt(mxlbank,124)
     mxl\wMid%            = PeekInt(mxlbank,128)
     mxl\wPid%            = PeekInt(mxlbank,130)
     mxl\vDriverVersion%  = PeekInt(mxlbank,132)
     mxl\szPname$         = peekstr(mxlbank,32,136)

     mxlc\cbStruct = BankSize(mxlcbank);24
     mxlc\dwLineID = mxl\dwLineID
     mxlc\dwControl = ctrlType
     mxlc\cControls = 1
     PokeInt(mxlcbank,0, BankSize(mxlcbank));24
     PokeInt(mxlcbank,4,mxl\dwLineID)
     PokeInt(mxlcbank,8,ctrlType)
     PokeInt(mxlcbank,12,1)

     outsize=148
     ; Allocate a buffer for the control
     hmem = GlobalAlloc($40, outsize)  ;$40
     mxlc\pamxctrl = GlobalLock(hmem)
     mxlc\cbmxctrl = outsize
     PokeInt(mxlcbank,16,outsize)
     PokeInt(mxlcbank,20,mxlc\pamxctrl)

     mxc\cbStruct = outsize
     mxcbank = CreateBank(outsize)
     PokeInt(mxcbank,0,mxc\cbStruct)

     ; Get the control
     rc = mixerGetLineControls(hmixer, mxlcbank, MIXER_GETLINECONTROLSF_ONEBYTYPE)
;     debuglog componentType%+" " +mxl\szShortName$
     GetMixerControl = False

     If (  MMSYSERR_NOERROR= rc) Then
         GetMixerControl = True
         ; Copy the control into the destination structure

         CopyStructFromPtr mxcbank, mxlc\pamxctrl, outsize

         mxc\cbStruct%      = PeekInt(mxcbank,0)  ;  size in Byte of MIXERCONTROL
         mxc\dwControlID%   = PeekInt(mxcbank,4)  ;  unique control id for mixer device
         mxc\dwControlType% = PeekInt(mxcbank,8)  ;  MIXERCONTROL_CONTROLTYPE_xxx
         mxc\fdwControl%    = PeekInt(mxcbank,12) ;  MIXERCONTROL_CONTROLF_xxx
         mxc\cMultipleItems%= PeekInt(mxcbank,16) ;  if MIXERCONTROL_CONTROLF_MULTIPLE
         mxc\szShortName$   = peekstr(mxcbank,16,20) ;  set short name of control
         mxc\szName$        = peekstr(mxcbank,64,36) ;  long name of control
         mxc\lMinimum%      = PeekInt(mxcbank,100);  Minimum value
         mxc\lMaximum%      = PeekInt(mxcbank,104);  Maximum value
     End If

     GlobalFree (hmem)
     FreeBank mxcbank

   End If

   FreeBank mxlcbank
   FreeBank mxcbank
   FreeBank mxlbank
   Return GetMixerControl
End Function

Function SetVolumeControl(hmixer% , mxc.MIXERCONTROL, volume% )
; This function sets the value for a volume control.
; Returns True if successful

   Local mxcd.MIXERCONTROLDETAILS = New MIXERCONTROLDETAILS
   Local vol.MIXERCONTROLDETAILS_UNSIGNED = New MIXERCONTROLDETAILS_UNSIGNED
   Local mxcdbank   = CreateBank(24)
   Local volbank    = CreateBank(4)
   Local mem,rc

   mxcd\cbStruct    = BankSize(mxcdbank)
   mxcd\cbDetails   = 4
   mxcd\item        = 0
   mxcd\dwControlID = mxc\dwControlID

   PokeInt(mxcdbank,0, BankSize(mxcdbank));24
   PokeInt(mxcdbank,4, mxc\dwControlID)
   PokeInt(mxcdbank,8, 1)
   PokeInt(mxcdbank,12,0)
   PokeInt(mxcdbank,16,BankSize(volbank) )

   ; Allocate a buffer for the control value buffer
   mem = GlobalAlloc($40, 4)
   mxcd\paDetails = GlobalLock(mem)
   mxcd\cChannels = 1

   PokeInt(mxcdbank,20,mxcd\paDetails )
;   debuglog "max "+mxc\lMaximum%
   If volume < mxc\lMinimum% Then volume = mxc\lMinimum%
   vol\dwValue%  = volume Mod mxc\lMaximum%
   PokeInt volbank,0,volume

   ; Copy the data into the control value buffer
   CopyPtrFromStruct mxcd\paDetails, vol, BankSize(volbank)

   ; Set the control value
   rc = mixerSetControlDetails(hmixer, mxcdbank, MIXER_SETCONTROLDETAILSF_VALUE)

   GlobalFree (mem)

   FreeBank mxcdbank
   FreeBank volbank
   If (MMSYSERR_NOERROR = rc) Then
       SetVolumeControl = True
   Else
       SetVolumeControl = False
   End If

   Return SetVolumeControl

End Function



Function GetVolumeControlLevel(hmixer% , mxc.MIXERCONTROL)
; This function sets the value for a volume control.
; Returns True if successful

   Local mxcd.MIXERCONTROLDETAILS = New MIXERCONTROLDETAILS
   Local vol.MIXERCONTROLDETAILS_UNSIGNED = New MIXERCONTROLDETAILS_UNSIGNED
   Local mxcdbank   = CreateBank(24)
   Local volbank    = CreateBank(4)
   Local mem,rc

   mxcd\cbStruct    = BankSize(mxcdbank)
   mxcd\cbDetails   = 4
   mxcd\item        = mxc\cMultipleItems
   mxcd\dwControlID = mxc\dwControlID

   PokeInt(mxcdbank,0, BankSize(mxcdbank));24
   PokeInt(mxcdbank,4, mxc\dwControlID)
   PokeInt(mxcdbank,8, 1)
   PokeInt(mxcdbank,12,mxc\cMultipleItems)
   PokeInt(mxcdbank,16,BankSize(volbank) )

   ; Allocate a buffer for the control value buffer
   mem = GlobalAlloc($40, 4)
   mxcd\paDetails = GlobalLock(mem)
   mxcd\cChannels = 1

   PokeInt(mxcdbank,20,mxcd\paDetails )

  ; Set the control value
   rc = mixerGetControlDetails(hmixer, mxcdbank, MIXER_GETCONTROLDETAILSF_VALUE)

   If rc <> 0 Then
     DebugLog "error "+rc+" can not get control"
   End If

   CopyStructFromPtr volbank, mxcd\paDetails, 4
   OUTvolume = PeekInt(volbank,0)
   If (OUTvolume < 0) Then OUTvolume = -OUTvolume

   GlobalFree (mem)

   FreeBank mxcdbank
   FreeBank volbank
   If (MMSYSERR_NOERROR = rc) Then
       SetVolumeControl = True
   Else
       SetVolumeControl = False
       OUTvolume = -1
   End If

   Return OUTVolume

End Function



Function SetVolume$(Control=4,NewVolume,mode=0)
;returns name of Control if OK

;mode 1 uses lineID instead of control type

  Local ok, hmixer = 0
  volCtrl.mixercontrol = New mixercontrol ;

  ok = GetMixerControl(hmixer, Control, MIXERCONTROL_CONTROLTYPE_VOLUME, volCtrl.mixercontrol,mode)

  If ok Then ok = SetVolumeControl(hmixer, volCtrl, NewVolume)
  If ok Then
    ShortName$ = volCtrl\szShortName$
    Delete volctrl
    Return ShortName$
  End If
End Function



Function GetVolume$(Control=4)
;returns name of Control if OK

  Local ok, hmixer = 0
  volCtrl.mixercontrol = New mixercontrol

  ok = GetMixerControl(hmixer, Control,MIXERCONTROL_CONTROLTYPE_VOLUME , volCtrl.mixercontrol)

  If ok Then
    OUTVolume = GetVolumeControlLevel(hmixer, volCtrl)
  Else
    OUTVolume = - 1
  End If

  Delete volctrl
  Return OUTVolume

End Function


;-- bank funcions

Function PeekStr$(Tbank,Size=64,Offset=0)
;note offset is 3rd param not 2nd like peekint, etc.

  Local count,newchr
  Local NewStr$ = ""
  Local bsize = BankSize(tbank)
  Local lastbyte = (offset+size-1)

  If lastbyte > bsize Then lastbyte = bsize

  For count = offset To lastbyte
    newchr = PeekByte(Tbank,count)

    If (newchr = 0) Then  Exit

    newstr$ = newstr$ + Chr$(newchr)
  Next
  Return Newstr$

End Function
