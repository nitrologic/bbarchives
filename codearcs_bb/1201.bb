; ID: 1201
; Author: Cygnus
; Date: 2004-11-18 17:28:38
; Title: Cygnus RetroSound
; Description: Sound system similar to that of the NES/BBC systems

;
;
; Cygnus Software's "chip" simulator...
;
; Programmed Oct-Nov 2004
;
; Feel free to use this code anywhere,
; give credits where credits are due? I would do the same.
; http://danjeruz.servegame.com
; Have fun!!!!

;Main Engine


Global ss_channelcount%
Global ss_bpsample%=16

Global ss_maxchans=50
Global ss_buffersize=1024*8
Global ss_maxenvs=30
Global ss_sndtype=8
Global ss_lfocount=2

Dim ss_sounds(ss_maxchans)
Dim ss_volume#(ss_maxchans)
Dim ss_schan(ss_maxchans)
Dim ss_sLength#(ss_maxchans)
Dim ss_started(ss_maxchans)
Dim ss_actualvol#(ss_maxchans)
Dim ss_attackdone(ss_maxchans)
Dim ss_decaydone(ss_maxchans)

Dim ss_attack#(ss_maxchans)
Dim ss_decay#(ss_maxchans)
Dim ss_sustain#(ss_maxchans)
Dim ss_release#(ss_maxchans)
Dim ss_currentpitch#(ss_maxchans)
Dim ss_envelope(ss_maxchans)

Dim ss_defattack#(ss_maxenvs)
Dim ss_defdecay#(ss_maxenvs)
Dim ss_defsustain#(ss_maxenvs)
Dim ss_defrelease#(ss_maxenvs)
Dim ss_attackchange#(ss_maxenvs)
Dim ss_decaychange#(ss_maxenvs)
Dim ss_sustainchange#(ss_maxenvs)
Dim ss_releasechange#(ss_maxenvs)

Dim ss_lfoattack#(ss_lfocount,ss_maxenvs)
Dim ss_lfodecay#(ss_lfocount,ss_maxenvs)
Dim ss_lfosustain#(ss_lfocount,ss_maxenvs)
Dim ss_lforelease#(ss_lfocount,ss_maxenvs)

Dim ss_lfo#(ss_lfocount,ss_maxchans)
Dim ss_lfofreq#(ss_lfocount,ss_maxenvs)
Dim ss_lfocounter#(ss_maxchans)
Dim ss_bufferchan(ss_maxchans,ss_buffersize)
Dim ss_buffervol#(ss_maxchans,ss_buffersize)
Dim ss_bufferenv#(ss_maxchans,ss_buffersize)
Dim ss_bufferpitch(ss_maxchans,ss_buffersize)
Dim ss_bufferlength(ss_maxchans,ss_buffersize)
Dim ss_bufferpointer(ss_maxchans)
Dim ss_buffercount(ss_maxchans)
Dim ss_chancheck(ss_maxchans)
Dim ss_channelloop(ss_maxchans)
Dim ss_samplecnt(ss_maxchans)
Dim ss_soundbank(ss_sndtype),ss_samplecntbank(ss_sndtype)
Global forcesoundupdate=0
Global ss_stimer#=100
Global audiotimer=MilliSecs(),updateperiod#=1
Dim wavdata(0,0)
initsoundsystem()


Function initsoundsystem();channels,envelopes)
Local samples,o,nm,snd
Dim wavdata(1,4096)

samples=1
ss_channelcount=1

For o=0 To 10
wavdata(0,o)=-65525/2.1
Next
wavdata(0,1)=-65525/3
wavdata(0,2)=65525/3


samplerate=1
bpsample=16

wavdata(0,1)=-65525/3
wavdata(0,2)=-65525/3
wavdata(0,3)=65525/3
wavdata(0,4)=65525/3


For snd=1 To ss_sndtype
If SND=3 Then
For o=0 To 10
WAVDATA(0,O)=Sin(O*57)*65525/3.0
Next
EndIf
If SND=4 Then
For o=0 To SND*2
WAVDATA(0,O)=Sin((Float(O)/Float(SND*2))*360)*65525/3.0
Next
EndIf



samples=snd*4
Writewav(samples,"S"+Str$(snd)+".wav")
Next

For o=0 To 4096
wavdata(0,o)=Rnd(-65525/2,65525/2)
wavdata(1,o)=Rnd(-65525/2,65525/2)
Next
samples=4096
Writewav(samples,"S0.wav")

For o=0 To ss_sndtype
ss_soundbank(o)=LoadSound("s"+Str$(o)+".wav")
o2=o:If o=0 Then o2=5
ss_samplecntbank(o)=o2
DeleteFile "s"+Str$(o)+".wav"
LoopSound ss_soundbank(o)
Next

For o=0 To ss_maxchans
O2=ss_maxchans-O
nm=(((o2-1)/3) Mod ss_sndtype)+1
If o=0 Then nm=0
ss_sounds(o)=ss_soundbank(nm)
ss_samplecnt(o)=ss_samplecntbank(nm)
Next

For o=0 To ss_maxenvs
ss_defattack(o)=.1
ss_defdecay(o)=-.5
ss_defsustain(o)=0
ss_defrelease(o)=-.1
Next

For o=0 To ss_maxenvs
Next
End Function
Function sound(channel,vol#,Pitch,Length,envelope=-1)
;channel=ss_maxchans-channel
If envelope=-1 Then envelope=channel

Local count
;dim ss_bufferchan(ss_maxchans,ss_buffersize),buffervol(ss_maxchans,ss_buffersize)
;dim ss_bufferpitch(ss_maxchans,ss_buffersize)
;dim ss_bufferlength(ss_maxchans,ss_buffersize)
;dim ss_buffercount(ss_maxchans)
count=ss_buffercount(channel)
If count<0 Then count=0
ss_buffercount(channel)=count
ss_buffervol(channel,count)=vol
ss_bufferpitch(channel,count)=Pitch
ss_bufferlength(channel,count)=Length
ss_bufferenv(channel,count)=envelope

ss_buffercount(channel)=ss_buffercount(channel)+1
If ss_buffercount(channel)>ss_buffersize-1 Then Repeat:updatesound():Until ss_buffercount(channel)<ss_buffersize-1 Or ss_buffercount(channel)=0;:If buffercount(channel)<0 Then buffercount(channel)=0
End Function
Function updatesound()
Local n,ct,n2
If MilliSecs()-audiotimer>updateperiod Then
audiotimer=MilliSecs()
ct=Float(MilliSecs()-audiotimer)/Float(updateperiod)
If ct>2 Then ct=0
For n=0 To ct
For n2=1 To 1
doupdatesound
updatesoundtimer()
Next
Next

EndIf

End Function
Function doupdatesound()

;soundtimer()=soundtimer()+.01
Local o,count,o2,updvols=0,nx,env
Local divm#=1


For o=0 To ss_maxchans
ss_lfocounter(o)=ss_lfocounter(o)+1

env=ss_envelope(o)
;env=o
updvols=1

pastend=0
attacked=ss_attackdone(o)
If soundtimer()-ss_started(o)>=ss_slength(o)*1 Then pastend=1

count=ss_buffercount(o)

If ss_schan(o)<>0 Then
;ChannelVolume ss_schan(o),ss_volume(o)
If updvols=1 Then


;Dim ss_lfo#(ss_lfocount,ss_maxchans)
;Dim ss_lfofreq#(ss_lfocount,ss_maxchans)
;Dim ss_lfocounter(ss_maxchans)

;vlfo#=lfovalue(ss_lfocounter(o))*ss_lfofreq(2,o)*ss_lfo(2,o)
vlfo#=volumemultiplier(o)
plfo#=Pitchlfo(o)
;plfo#=lfovalue(ss_lfocounter(o))*ss_lfofreq(1,o)*ss_lfo(1,o)
;vlfo=1
;plfo=1
		;For n=1 To ss_lfocount
			;If ss_lfo(n,o)<ss_lfosustain(n,env) Then ss_lfo(n,o)=ss_lfo(n,o)+ss_lfoattack(n,env) Else ss_lfo(n,o)=ss_lfosustain(n,env)
		;Next
		
If pastend=0 And attacked=0 Then			;Attack section
	If ss_volume(o)<1 Then ss_volume(o)=ss_volume(o)+ss_attack(o)
	If ss_volume(o)>=1 Then ss_volume(o)=1

		For n=1 To ss_lfocount
			If ss_lfo(n,o)<1 Then ss_lfo(n,o)=ss_lfo(n,o)+ss_lfoattack(n,env) Else ss_lfo(n,o)=1;ss_lfosustain(n,env)
		Next

	
	av#=ss_volume#(o):ChannelVolume ss_schan(o),(av+vlfo)*ss_actualvol(o)
	ss_currentpitch(o)=ss_currentpitch(o)+ss_attackchange(env)
	nf=freq(ss_currentpitch(O),o)
	If nf>10 Then ChannelPitch ss_schan(o),nf
EndIf

If ss_volume(o)>=1 Then attacked=1:ss_attackdone(o)=1

If pastend=0 And attacked=1 Then			;Decay section
	ss_volume(o)=ss_volume(o)+ss_decay(o):If ss_volume(o)<ss_sustain(o) Then ss_volume(o)=ss_sustain(o):ss_decaydone(o)=1


		For n=1 To ss_lfocount
			ss_lfo(n,o)=ss_lfo(n,o)+ss_lfodecay(n,env)
			If ss_lfo(n,o)<ss_lfosustain(n,env) Then ss_lfo(n,o)=ss_lfosustain(n,env)
		Next

		av#=ss_volume#(o):ChannelVolume ss_schan(o),(av+vlfo)*ss_actualvol(o)
		ss_currentpitch(o)=ss_currentpitch(o)+ss_decaychange(env)
		nf=freq(ss_currentpitch(O),o)
	If nf>10 Then ChannelPitch ss_schan(o),nf
EndIf
If ss_decaydone(o)=1 And pastend=0 Then	;Sustain section
		ss_currentpitch(o)=ss_currentpitch(o)+ss_sustainchange(env)
		av#=ss_volume#(o):ChannelVolume ss_schan(o),(av+vlfo)*ss_actualvol(o)
		nf=freq(ss_currentpitch(O),o)
	If nf>10 Then ChannelPitch ss_schan(o),nf
EndIf

If pastend=1 Then
	ss_volume(o)=ss_volume(o)+ss_release(o):If ss_volume(o)<0 Then ss_volume(o)=0

		For n=1 To ss_lfocount
			ss_lfo(n,o)=ss_lfo(n,o)+ss_lforelease(n,env)
			If ss_lfo(n,o)<0 Then ss_lfo(n,o)=0
		Next

	av#=ss_volume#(o):ChannelVolume ss_schan(o),(av+vlfo)*ss_actualvol(o)
	ss_currentpitch(o)=ss_currentpitch(o)+ss_releasechange(env)
	nf=freq(ss_currentpitch(O),o)
	If nf>10 Then ChannelPitch ss_schan(o),nf
EndIf

EndIf




EndIf
If pastend=1 And forcesoundupdate=0 Then
count=ss_buffercount(o)
If count>0 And ss_buffercount(o)<ss_buffersize+1 Then
;For nx=1 To 10
;volume(o)=volume(o)*.9
;ChannelVolume schan(o),volume(o)
;Delay 1
;Next
	If ss_channelloop(o) Then
	
		addsound(o,ss_buffervol(o,ss_bufferpointer(o)),ss_bufferpitch(o,ss_bufferpointer(o)),ss_bufferlength(o,ss_bufferpointer(o)),ss_bufferenv(o,ss_bufferpointer(o))):ss_bufferpointer(o)=(ss_bufferpointer(o)+1) Mod count
		;For o2=1 To count:buffervol(o,o2-1)=buffervol(o,o2):bufferpitch(o,o2-1)=bufferpitch(o,o2):bufferlength(o,o2-1)=bufferlength(o,o2):Next:buffercount(o)=buffercount(o)-1
	EndIf
	If ss_channelloop(o)=0 Then
		addsound(o,ss_buffervol(o,ss_bufferpointer(o)),ss_bufferpitch(o,ss_bufferpointer(o)),ss_bufferlength(o,ss_bufferpointer(o)),ss_bufferenv(o,ss_bufferpointer(o)))
		For o2=1 To count
		ss_buffervol(o,o2-1)=ss_buffervol(o,o2)
		ss_bufferpitch(o,o2-1)=ss_bufferpitch(o,o2)
		ss_bufferlength(o,o2-1)=ss_bufferlength(o,o2)
		ss_bufferenv(o,o2-1)=ss_bufferenv(o,o2)

		Next:ss_buffercount(o)=ss_buffercount(o)-1:If ss_buffercount(o)<0 Then ss_buffercount(o)=0
	EndIf
		
EndIf

EndIf
Next
End Function

Function freq(Pitch,chan)
Local p2,f
If Pitch<-512 Then Pitch=Pitch+512*2
If Pitch>512 Then Pitch=Pitch-512*2
p2=(Pitch+200)+(Pitchlfo(chan));+Sin(ss_lfocounter(chan))
	f = 440 * 2^(((P2/4.0) - 58)/12)
	If f<10 Then f=10 Else If f>48000 Then f=48000
Return f*ss_samplecnt(chan)
End Function
Function lfovalue#(cnt)
;Return (Cos#(cnt)+1)/2.0
Return (Cos#(cnt))
End Function

Function volumemultiplier#(channel)
;env=ss_envelope(channel)
;If env<0 Or env>ss_maxenvs Then RuntimeError env
Return lfovalue(ss_lfocounter(channel)*ss_lfofreq(2,ss_envelope(channel)))*ss_lfo(2,channel)
;Return lfovalue(ss_lfocounter(channel))
End Function

Function Pitchlfo#(channel)
Return lfovalue(ss_lfocounter(channel)*ss_lfofreq(1,ss_envelope(channel)))*ss_lfo(1,channel)
End Function

Function addSound(channel,vol#,Pitch,Length#,envelope=-1)
Local p2#,o=ss_maxchans-channel,n
If envelope=-1 Then RuntimeError "sound not buffered correctly!"
ss_chancheck(channel)=0
;Goto Endbit
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
If ss_sounds(channel)<>0 Then
ss_lfocounter(channel)=0
;For n=0 To ss_lfocount
;Next
p2=freq(pitch,channel)

If p2>100 Then
SoundVolume ss_sounds(channel),0
If ChannelPlaying(ss_schan(channel))=0 Then ss_schan(channel)=0 Else StopChannel ss_schan(channel):ss_schan(channel)=0
If ss_schan(channel)=0 Then If ss_sounds(channel)<>0 Then ss_schan(channel)=PlaySound(ss_sounds(channel))

ChannelPitch ss_schan(channel),P2;+((P2+(P2*2.0))/12.0)*4.0
ChannelVolume ss_schan(channel),ss_defattack(envelope)*vol*volumemultiplier(channel);(vol)
SoundVolume ss_sounds(channel),1
For n=1 To ss_lfocount
	ss_lfo(n,channel)=ss_lfoattack(n,channel)
Next
ss_currentpitch(channel)=Pitch
ss_volume(channel)=ss_defattack(envelope);vol
ss_actualvol(channel)=vol
ss_sLength(channel)=Length
ss_started(channel)=soundtimer()
ss_attack(channel)=(ss_defattack(envelope)/15)*updateperiod#
ss_release(channel)=(ss_defrelease(envelope)/15)*updateperiod#;-.2
ss_sustain(channel)=ss_defsustain(envelope);.2
ss_decay(channel)=(ss_defdecay(envelope)/15)*updateperiod#;-.8
ss_attackdone(channel)=0
ss_decaydone(channel)=0
ss_chancheck(channel)=soundtimer()
ss_envelope(channel)=envelope
EndIf
EndIf
forcesoundupdate=1
updatesound()
forcesoundupdate=0
.endbit
End Function

Function updatesoundtimer()
;Return MilliSecs()
ss_stimer#=ss_stimer#+1
Return ss_stimer
End Function
Function soundtimer()
Return ss_stimer
End Function
Function loopchannel(channel,loop=1)
ss_channelloop(channel)=loop
End Function

Function LfoADSR(envelope,Lfo,cattack#,cdecay#,csustain#,crelease#)
;ss_lfo#(lfo,ss_maxchans)
;ss_lfo(lfo,
ss_lfoattack(lfo,envelope)=cattack
ss_lfodecay(lfo,envelope)=cdecay
ss_lfosustain(lfo,envelope)=csustain
ss_lforelease(lfo,envelope)=crelease

;Dim ss_lfoattack#(ss_lfocount,ss_maxenvs)
;Dim ss_lfodecay#(ss_lfocount,ss_maxenvs)
;Dim ss_lfosustain#(ss_lfocount,ss_maxenvs)
;Dim ss_lforelease#(ss_lfocount,ss_maxenvs)
;Dim ss_lfo#(ss_lfocount,ss_maxenvs)
;Dim ss_lfofreq#(ss_lfocount,ss_maxenvs)
End Function
Function Lfo(envelope,lfo,frequency)
ss_lfofreq#(lfo,envelope)=frequency
End Function

Function ADSR(envelope,cattack#,cdecay#,csustain#,crelease#)
ss_defattack(envelope)=cattack
ss_defdecay(envelope)=cdecay
ss_defsustain(envelope)=csustain
ss_defrelease(envelope)=crelease
End Function
Function PitchADSR(envelope,cattack#,cdecay#,csustain#,crelease#)
ss_attackchange(envelope)=cattack
ss_decaychange(envelope)=cdecay
ss_sustainchange(envelope)=csustain
ss_releasechange(envelope)=crelease
End Function
Function Soundshape(channel,sound)
If sound>-1 And sound<ss_sndtype+1 Then
ss_sounds(channel)=ss_soundbank(sound)
ss_samplecnt(channel)=ss_samplecntbank(sound)

EndIf
End Function
Function flushchannel(channel)
ss_bufferpointer(channel)=0
ss_buffercount(channel)=0
If ss_schan(channel)<>0 Then If ChannelPlaying(ss_schan(channel)) Then StopChannel ss_schan(channel):ss_schan(channel)=0
ss_started(channel)=-ss_slength(channel)
End Function
Function Stopsound()
Local n
For n=0 To ss_maxchans
flushchannel n
Next
End Function

Function Writewav(wavdatalen,filename$,ss_channelcount=1,samplerate=44100)
samples=wavdatalen
;Function Writewav(nosamples,filename$)
;wavdatalen=(ss_channelcount*nosamples*(bitspersample/8))/4
bitspersample=ss_bpsample
fs=WriteFile(filename$)
If fs=0 Then Return 0
WriteBinString fs,"RIFF"

wavlen=36+((wavdatalen*2)*ss_channelcount)
WriteInt fs,wavlen
Writebinstring fs,"WAVE"
Writebinstring fs,"fmt "
WriteInt fs,16

WriteShort fs,1

WriteShort fs,ss_channelcount
WriteInt fs,samplerate
bitspersample=16
byterate=SampleRate * ss_channelcount * bitspersample/8

WriteInt fs,byterate
WriteShort fs,ss_channelcount * bitspersample/8
WriteShort fs,bitspersample
Writebinstring fs,"data"
WriteInt fs,samples*ss_channelcount * BitsPerSample/8
;WriteInt fs,wavdatalen*4
For p=1 To wavdatalen
If ss_bpsample=8 Then Midr=128
If ss_bpsample=16 Then Midr=Midbit/2.0
If ss_bpsample=32 Then Midr=(Midbit*255)/2.0

For chan=0 To ss_channelcount-1
rval=wavdata(chan,p)
rval=rval-Midr
If rval<0 Then rval=rval+(Midr*2)
If rval<0 Then rval=0 Else If rval>(Midr*2)*2 Then rval=(Midr*2)
;If ss_bpsample>16 Then rval=rval/2.0;WriteShort fs,rval
WriteShort fs,wavdata(chan,p)*.5;rval

Next
Next
If ss_channelcount=1 Then ster$="Mono" Else ster$="Stereo"
ss_debugprint"Created "+filename$+" as "+ss_bpsample+" bit, "+samplerate+"hz, "+ster$+" wave file."

CloseFile fs
End Function

Function WriteBinString(filehandle,dat$)
For p=1 To Len(dat$)
WriteByte filehandle,Asc(Mid$(dat$,p,1))
Next
End Function

Function ss_debugprint(St$)
Print st$
End Function



;Demo Usage

For nz=1 To ss_maxchans
soundshape nz,2
Next
soundshape 5,1

For nz=0 To ss_maxenvs
adsr nz,1,-.1,.1,-.1
Next


Restore music1
Repeat
Read chan
If chan<>-1 Then
If chan<>0 Then chan=chan+5

Read vol#,Pitch,Length#
If chan=6 Or chan=7 Or chan=8 Then Pitch=Pitch-48
Pitch=Pitch+48
If chan=9 Then Pitch=Pitch-48*2
sound chan,vol#,Pitch+48,Length*30,chan
loopchannel chan,1
EndIf
Until chan=-1

Repeat
updatesound
Until KeyDown(1)
End

.music1


Data 1,.5,52,10
Data 2,.5,68,10
Data 3,.5,80,10
Data 1,.5,52,10
Data 2,.5,68,10
Data 3,.5,80,10

Data 1,.5,52,10
Data 2,.5,72,10
Data 3,.5,80,10
Data 1,.5,52,10
Data 2,.5,72,10
Data 3,.5,80,10



Data 5,1,100,10
Data 5,1,100,10
Data 5,1,92,5
Data 5,1,88,5
Data 5,1,80,10
Data 5,1,72,10
Data 5,1,80,10
Data 5,1,88,10
Data 5,1,80,10
;
Data 5,1,100,10
Data 5,1,100,10
Data 5,1,92,5
Data 5,1,88,5
Data 5,1,80,5
Data 5,1,80,45

Data 4,.5,52,5
Data 4,.5,80,5

Data 0,1,5,5
Data 0,0,50,15
Data 0,.5,150,10
Data 0,0,0,10
Data 0,1,5,5
Data 0,0,0,5
Data 0,1,5,5
Data 0,.5,20,5
Data 0,.5,150,10
Data 0,0,50,10

Data 0,1,5,5
Data 0,0,50,15
Data 0,.5,150,10
Data 0,0,0,10
Data 0,1,5,5
Data 0,0,0,5
Data 0,1,5,5
Data 0,.5,20,5
Data 0,.5,150,5
Data 0,0,150,5
Data 0,.5,150,5
Data 0,.5,150,5
Data -1
