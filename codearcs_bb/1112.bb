; ID: 1112
; Author: Ziltch
; Date: 2004-07-19 18:24:54
; Title: Make Audio example
; Description: A simple example of how to make wav files

Include "MakeAudio.bb"

Print "Create Wav in memory"
WavBank=CreateWavBank(6000,22050,16,2 )

Print "Generate Sine and square waves"
Freq# = 14000
amp#= 2000
;CreateSinWav(wavbank,Freq#,amp#,LEFT_CHANNEL)
;CreateSqrWav(wavbank,22000,1000,RIGHT_CHANNEL)
;
CreateSinWav(wavbank,8000,5000,ALL_CHANNELS)

Print "Play Sample"
PlaySoundbank(wavbank ,snd_loop) ; play sample

Print ""
Print "Hit any key to save file"
While GetKey()=0
Wend


BankToFile(wavbank,"SinWave.wav") ; save modified sample
Print "stop sample playing"
StopSoundBank()

Print ""
Print "Hit any key"
While GetKey()=0
Wend
Print "Generate Sine Sweeps"

StartFreq# = 52000
EndFreq#   = 100
CreateSinSweep(wavbank,StartFreq#,EndFreq#,2500,LEFT_CHANNEL)
StartFreq# = 100
EndFreq#   = 52100
CreateSinSweep(wavbank,StartFreq#,EndFreq#,2500,RIGHT_CHANNEL)

Print "Play Sample"
PlaySoundbank(wavbank ,snd_loop)

Print ""
Print "Hit any key to save file"
While GetKey()=0
Wend


BankToFile(wavbank,"SinSweep.wav") ; save modified sample
StopSoundBank() ; stop sample playing

FreeBank wavbank

Print "finished"
Print ""
Print "Hit any key"
While GetKey()=0
Wend
End
