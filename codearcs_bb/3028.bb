; ID: 3028
; Author: Prym
; Date: 2013-02-05 09:46:32
; Title: Galaxang ( NoComments )
; Description: Experimental cosmos

.desbuts
Const texteinfopb1$ = " "
Const texteinfopb2$ = " "
Const texteinfopb3$ = " "
Const texteinfopb4$ = " "
Const texteinfopb5$ = " "
.bones1
Type UnpackedFiles
Field file$
End Type
Type ExpDir
Field dir$
End Type
Dim pakfiles$(4000)
Dim paklocate(4000)
Dim PakSize(4000)
Dim AddPack$(4000)
Dim AddPackSize(4000)
Dim AddPackOffset(4000)
Global AddPackCount
Global AddPackHeaderMask
Global AddPackKey
Global AddFileTrack
Global OutputDir$
Global LastPak$
Global PakBulkOverwrite
Global PackCount
Global key
Global Packname$
Global exeoffset
Global pakAPND$
Global ChunkName$, Chunklen
Function PakInit(myfile$,mykey,APND$,headermask)
Local Exists
Local Headcount
Local pack
Local Retrieve
Local Offset
Local Block
Local Tem$
Local secbit
Local Hold
Local T
Local Fty
Local cnt
Local Filecnt
key = mykey
pakApnd$=APND$
If FileType(myfile$)<>0 Then
packname$=myfile$
Else
packname$="Data.pak"
EndIf
If Upper$(Right$(packname$,4))=".EXE" Or Upper$(Right$(packname$,4))=".SCR" Then
retrieve = ReadFile(packname$)
SeekFile(retrieve,FileSize(packname$)-10)
exeoffset = ReadInt(retrieve)-1
exeoffset=exeoffset - headermask
CloseFile(retrieve)
EndIf
pack = ReadFile(packname$)
SeekFile(pack,exeoffset)
offset = ReadInt(pack)
offset = offset Xor headermask
SeekFile(pack,(offset-1)+exeoffset)
PackCount = ReadInt(Pack)
headcount=(FileSize(packname$)-FilePos(pack))+4
block = CreateBank(headcount)
ReadBytes block,pack,0,headcount-4
If key<>0 Then
For filecnt=1 To headcount-4 Step 4
hold=PeekInt(block,filecnt-1)
hold = hold Xor key
PokeInt block,filecnt-1,hold
Next
EndIf
tem$ = ""
secbit=0
cnt = 1
For t=0 To headcount-5
If PeekByte(block,t)=13 Then
If fty = 0 Then
pakfiles$(cnt)=Upper$(tem$)
EndIf
If fty = 1 Then
paklocate(cnt)=tem$
paklocate(cnt)=paklocate(cnt)+exeoffset
EndIf
If fty = 2 Then
Paksize(cnt)=tem$
cnt=cnt+1
EndIf
fty=fty+1
If fty=3 Then fty=0
tem$=""
Else
tem$=tem$ + Chr$(PeekByte(block,t))
EndIf
Next
FreeBank block
CloseFile (pack)
End Function
Function pak$(pkf$)
If Left$(pkf$,1)="\" Or Left$(pkf$,1)="/" Then pkf$=Right$(pkf$,Len(pkf$)-1)
Local IsoName
Local cnt
Local item
Local Pack
Local Output
Local Block
Local filecnt
Local Hold
Local Ext$
Local Ex2$
Local outf$
If Instr(pkf$,"\")=0 And Instr(pkf$,"/")=0 Then
IsoName = True
Else
CreateDirTree IsoPath(pkf$)
EndIf
For cnt=1 To packcount
If IsoName = True Then
If IsolateName(Upper$(pakfiles$(cnt)))=Upper$(pkf$) Then
item = cnt
Exit
EndIf
Else
If Upper$(pakfiles$(cnt))=Upper$(pkf$) Then
item = cnt
Exit
EndIf
EndIf
Next
If item = 0 Then
Return pkf$
EndIf
LastPak$ = IsoPath(pkf$)+pakapnd$+IsolateName(pkf$)
For lpk.unpackedfiles = Each unpackedfiles
If Upper$(lpk\file$) = Upper$(lastpak$) Then exists = 1 : Exit
Next
If exists=0 Then
lpk.UnpackedFiles = New UnpackedFiles
lpk\file$ = LastPak$
EndIf
If FileType(IsoPath(pkf$)+pakapnd$+IsolateName(pkf$))=0 Or PakBulkOverwrite = True Then
pack = ReadFile(packname$)
SeekFile(pack,paklocate(item))
output = WriteFile(IsoPath(pkf$)+pakapnd$+IsolateName(pkf$))
block = CreateBank(paksize(item)+4)
ReadBytes block,pack,0,paksize(item)
If key<>0 Then
For filecnt=1 To paksize(item) Step 4
hold=PeekInt(block,filecnt-1)
hold = hold Xor key
PokeInt block,filecnt-1,hold
Next
EndIf
WriteBytes block,output,0,paksize(item)
CloseFile(output)
CloseFile(pack)
EndIf
ext$ = Upper$(Right$(pkf$,4))
ex2$ = Upper$(Right$(pkf$,2))
FreeBank block
If ext$=".3DS" Or ex2$=".X" Or ext$="MD2" Then texcheck(IsoPath(pkf$)+pakapnd$+IsolateName(pkf$))
If ext$=".B3D" Then ParseB3D(IsoPath(pkf$)+pakapnd$+IsolateName(pkf$))
Return IsoPath(pkf$)+pakapnd$+IsolateName(pkf$)
End Function
Function texpak$(pkf$)
DebugLog pkf$
If Left$(pkf$,1)="\" Or Left$(pkf$,1)="/" Then pkf$=Right$(pkf$,Len(pkf$)-1)
Local cnt
Local item
Local Pack
Local Output
Local Block
Local filecnt
Local Hold
Local IsoName
If Instr(pkf$,"\")=0 And Instr(pkf$,"/")=0 Then
IsoName = True
Else
CreateDirTree IsoPath(pkf$)
EndIf
For cnt=1 To packcount
If IsoName = True Then
If IsolateName(Upper$(pakfiles$(cnt)))=Upper$(pkf$) Then
item = cnt
Exit
EndIf
Else
If Upper$(pakfiles$(cnt))=Upper$(pkf$) Then
item = cnt
Exit
EndIf
EndIf
Next
If item = 0 Then
Return pkf$
EndIf
outf$ = Upper$(IsoPath(pkf$)+IsolateName(pkf$))
For lpk.unpackedfiles = Each unpackedfiles
If Upper$(lpk\file$) = outf$ Then exists = 1 : Exit
Next
If exists=0 Then
lpk.UnpackedFiles = New UnpackedFiles
lpk\file$ = IsoPath(pkf$)+IsolateName(pkf$)
EndIf
If FileType(outf$)=0 Or PakBulkOverwrite = True Then
pack = ReadFile(packname$)
SeekFile(pack,paklocate(item))
output = WriteFile(IsoPath(pkf$)+IsolateName(pkf$))
block = CreateBank(paksize(item)+4)
ReadBytes block,pack,0,paksize(item)
If key<>0 Then
For filecnt=1 To paksize(item) Step 4
hold=PeekInt(block,filecnt-1)
hold = hold Xor key
PokeInt block,filecnt-1,hold
Next
EndIf
WriteBytes block,output,0,paksize(item)
CloseFile(pack)
CloseFile(output)
FreeBank block
EndIf
Return isopath(pkf$)+isolatename(pkf$)
End Function
Function PakClean()
Local cnt.unpackedfiles
For cnt.unpackedfiles = Each unpackedfiles
DeleteFile cnt\file$
Delete cnt
Next
End Function
Function texcheck(modname$)
Local file
Local size
Local png
Local jpg
Local bmp
Local tga
Local pnga
Local jpga
Local bmpa
Local tgaa
Local Bank
Local t
Local ser
Local m
Local tex$
Local k
Local ltp$
file = ReadFile(modname$)
If file = 0 Then Return
putit$ = JustPath(Modname$)
putit$ = Right$(putit$,Len(putit$)-Len(outputdir$))
size = FileSize(modname$)
bank = CreateBank(size+4)
png = svl(".png")
jpg = svl(".jpg")
bmp = svl(".bmp")
tga = svl(".tga")
pnga = svl(".PNG")
jpga = svl(".JPG")
bmpa = svl(".BMP")
tgaa = svl(".TGA")
ReadBytes bank,file,0,size
CloseFile file
ltp$=""
For t = 1 To size
ser = PeekInt(bank,t)
If ser = png Or ser = jpg Or ser = bmp Or ser = pnga Or ser = jpga Or ser = bmpa Or ser = tga Or ser = tgaa Then
m = t
While PeekByte(bank,m)<>0 And m>0 And PeekByte(bank,m)<>34 And PeekByte(bank,m)>31 And PeekByte(bank,m)<127
m=m-1
Wend
tex$ = ""
For k=m+1 To t+3
tex$=tex$+Chr$(PeekByte(bank,k))
Next
tex$=Isolatename(tex$)
If ltp$<>(putit$+tex$) Then
texpak(putit$+tex$)
ltp$ = putit$+tex$
EndIf
EndIf
Next
FreeBank bank
End Function
Function svl(ser$)
Local try
Local retval
try = CreateBank(4)
PokeByte try,0,Asc(Mid$(ser$,1,1))
PokeByte try,1,Asc(Mid$(ser$,2,1))
PokeByte try,2,Asc(Mid$(ser$,3,1))
PokeByte try,3,Asc(Mid$(ser$,4,1))
retval = PeekInt(try,0)
FreeBank try
Return retval
End Function
Function CreatePakFile(destfile$, APKey, Head)
Local Crap
SeedRnd MilliSecs()
AddFileTrack = WriteFile(destfile$)
AddPackCount=0
AddPackHeaderMask = Head
AddPackKey = APKey
WriteInt AddFileTrack, 0
For Crap = 1 To Rand(500)
WriteByte AddFileTrack,Rand(254)
Next
End Function
Function AddtoPak(myfile$)
Local ScratchBank
Local FullSize
Local FileAxis
Local Crypt
Local Hold
AddPackCount = AddPackCount + 1
AddPack$(AddPackCount) = Myfile$
AddPackSize(AddPackCount) = FileSize(Myfile$)
AddPackOffset(AddPackCount) = FilePos(AddFileTrack)
FullSize = FileSize(MyFile$)
ScratchBank = CreateBank(FullSize + 8)
FileAxis = ReadFile(MyFile$)
ReadBytes ScratchBank,FileAxis,0,FullSize
If AddPackKey<>0 Then
For Crypt = 0 To Fullsize+4 Step 4
hold=PeekInt(ScratchBank,Crypt)
hold = hold Xor AddPackKey
PokeInt ScratchBank,Crypt,hold
Next
EndIf
WriteBytes ScratchBank,AddFileTrack,0,FullSize
FreeBank ScratchBank
CloseFile FileAxis
End Function
Function CloseCreatedPak()
Local N$=""
Local S$=""
Local O$=""
Local Full$=""
Local Cnt = 0
Local ScratchBank
Local Crypt
Local Hold
Local Crap
For cnt = 1 To AddPackCount
N$ = AddPack$(cnt)
S$ = AddPackSize(cnt)
O$ = AddPackOffSet(cnt)
Full$ = Full$ + N$ + Chr$(13)
Full$ = Full$ + O$ + Chr$(13)
Full$ = Full$ + S$ + Chr$(13)
Next
ScratchBank = CreateBank(Len(full$)+4)
For cnt = 1 To Len(Full$)
PokeByte ScratchBank, cnt-1, Asc(Mid$(full$,cnt,1))
Next
If AddPackKey<>0 Then
For Crypt = 0 To Len(full$) Step 4
hold=PeekInt(ScratchBank,Crypt)
hold = hold Xor AddPackKey
PokeInt ScratchBank,Crypt,hold
Next
EndIf
Hold = FilePos(AddFileTrack)+1
WriteInt AddFileTrack, AddPackCount
WriteBytes ScratchBank, AddFileTrack,0,Len(full$)
FreeBank ScratchBank
SeedRnd MilliSecs()
For Crap = 1 To Rand(500)
WriteByte AddFileTrack,Rand(255)
Next
SeekFile AddFileTrack, 0
hold = hold Xor AddPackHeaderMask
WriteInt AddFileTrack, Hold
CloseFile AddFileTrack
End Function
Function AppendToExe(myexefile$, myPakName$)
Local TempAxis
Local TempPax
Local EXELen
Local PakLen
Local ScratchBank
PakLen = FileSize(MyPakName$)
EXELen = FileSize(myexefile$)
TempAxis = OpenFile(myexefile$)
TempPax = OpenFile(myPakName$)
ScratchBank = CreateBank(PakLen + 4)
ReadBytes ScratchBank,TempPax,0,PakLen
SeekFile TempAxis,EXELen
WriteBytes ScratchBank, TempAxis, 0, PakLen
WriteInt TempAxis, (EXELen + AddPackHeaderMask + 1)
WriteByte TempAxis,Asc("D") : WriteByte TempAxis,Asc("A") : WriteByte TempAxis,Asc("T") : WriteByte TempAxis,Asc("P") : WriteByte TempAxis,Asc("A") : WriteByte TempAxis,Asc("K")
CloseFile TempAxis
CloseFile TempPax
FreeBank ScratchBank
End Function
Function isolatename$(wha$)
For t=Len(wha$) To 1 Step -1
If Mid$(wha$,t,1)="\" Or Mid$(wha$,t,1)="/" Then
Return Right$(wha$,Len(wha$)-t)
EndIf
Next
Return wha$
End Function
Function IsoPath$(wha$)
For t=Len(wha$) To 1 Step -1
If Mid$(wha$,t,1)="\" Or Mid$(wha$,t,1)="/" Then
Return OutputDir + Left$(wha$,t)
EndIf
Next
Return OutputDir
End Function
Function justPath$(wha$)
For t=Len(wha$) To 1 Step -1
If Mid$(wha$,t,1)="\" Or Mid$(wha$,t,1)="/" Then
Return Left$(wha$,t)
EndIf
Next
Return ""
End Function
Function PakOutputDir(myDir$)
If Len(mydir$)<3 Then
mydir$=""
Return
EndIf
If Right$(mydir$,1)<>"\" Or Right$(mydir$,1)<>"/" Then mydir$=mydir$+"\"
OutputDir$ = mydir$
createdirtree mydir$
End Function
Function DLPak()
Local t.unpackedfiles
For t.unpackedfiles = Each unpackedfiles
If Upper$(t\file$)=Upper$(lastpak$) Then
DeleteFile LastPak$
Delete t
EndIf
Next
End Function
Function CreateDirTree(mydir$)
Local t
Local exists
Local nux$
mydir$=mydir$+"\"
If Mid$(mydir$,2,1)=":" Then t = 4 Else t = 1
Repeat
exists = 0
If Mid$(mydir$,t,1)="\" Or Mid$(mydir$,t,1)="/" Then
For mug.expdir = Each expdir
If Upper$(mug\dir$) = Upper$(Left$(mydir$,t-1)) Then exists = 1 : Exit
Next
If exists=0 Then
nux$=Left$(mydir$,t-1)
If Right$(nux$,1)="\" Or Right$(nux$,1)="/" Then nux$=Left$(nux$,Len(nux$)-1)
CreateDir nux$
mug.expdir = New expdir
mug\dir$ = Upper$(nux$)
EndIf
EndIf
t=t+1
Until t=Len(mydir$)
End Function
Function ParseB3D(B3DFile$)
If Upper$(Right$(B3DFile$,4))<>".B3D" Then Return
Local BHand, tind, texf, texb
Local CSize, Fsize, Entries
Local Putit$
putit$ = JustPath(B3DFile$)
putit$ = Right$(putit$,Len(putit$)-Len(outputdir$))
Bhand = ReadFile(B3DFile$)
If Bhand = 0 Then Return
fsize = FileSize(B3DFile$)
ReadChunk(Bhand)
ReadInt(BHand)
While FilePos(Bhand)<Fsize
ReadChunk(Bhand)
mypos = FilePos(Bhand)
Select Chunkname$
Case "TEXS"
While FilePos(Bhand)<(mypos+Chunklen)
texpak(putit$+IsolateName$(StringRead$(Bhand)))
ReadInt(Bhand)
ReadInt(Bhand)
ReadFloat(Bhand)
ReadFloat(Bhand)
ReadFloat(Bhand)
ReadFloat(Bhand)
ReadFloat(Bhand)
tind = tind +1
Wend
SeekFile Bhand,mypos+Chunklen
Default
SeekFile Bhand,mypos+Chunklen
End Select
Wend
CloseFile bhand
End Function
Function ReadChunk(Strm)
Local chunk$
Chunk$ = ""
chunk$=Chr$(ReadByte(strm))
Chunk$=Chunk$ + Chr$(ReadByte(strm))
Chunk$=Chunk$ + Chr$(ReadByte(strm))
Chunk$=Chunk$ + Chr$(ReadByte(strm))
ChunkName$=Upper$(Chunk$)
Chunklen = ReadInt(Strm)
End Function
Function StringRead$(Strm)
Local Gb
Local RT$
Repeat
gb=ReadByte(strm)
If gb<>0 Then rt$=rt$+Chr$(gb)
Until gb=0
Return rt$
End Function
Type vector
Field teraf_x#
Field teraf_y#
Field teraf_z#
Field teraf_u#
Field teraf_v#
End Type
Global G_width
Global G_height
Global G_Depth
Global Bearing.vector=New vector
Global Normal.vector=New vector
Global CProd.vector=New vector
Global Tnorm.vector=New vector
Global DProd#
.InitVars
Global numimage% = 0 , nbmaxscans% = 0 , dossiercourant$ = ""
Global valeur% = 0 , cntglobal = 1 , PackCountglobal = 1
Global microphone , soundfighter
Global presentimage = 0 , sketchapy = 0
Global CrissMingo
Global nouvellepartie = 0 , TCP32 = 0 , retourmenu = False , dirsys = 0
Global playername$ = "" , playerID = 0 , gamename$ = "Galaxang32"
Global MULTI = False , coinsicoinsa = ""
Global textureTMP = 0
Global strStreamcli = 0 , filemajprogcli = 0
Global syncapture = 1
Global GW% = 0 , GH% = 0 , GD% = 0 , GM% = 0 , GB# = 0 , capslevel% = 0
Global paramCMD$ = "" , paramCMDleft$ = "" , paramCMDnb% = 0
Global type_ellipsoide = 1 , type_poly = 2 , type_arbre = 3 , type_zimeub = 4 , type_chateau = 5
Global arialTTF = 0 , GabriolaTTF = 0 , app850fon = 0
Global AncTailleFont% = FontHeight ( )
Global timerglobal = CreateTimer ( 100 ) , timerpanneaublitz
Global nbcarstr1 = 0 , nbcarstr2 = 0 , nbcarstr3 = 0 , numread = 0
Global campickedplayer0 = 0
Global pickedplayer0 , player0int , texturecone1_rock
Global textureplayer0int_rock , textureplayer0_rock , brushplayer0
Global pivotplayer0 , player0 , travelticket = False , pointplayer = False
Global numplay0 = 0 , parentplayer0 = 0 , nameparentplayer0memo$ = ""
Global entityXpivotTEMPmemo = 1000 , entityYpivotTEMPmemo = 1000 , entityZpivotTEMPmemo = 1000
Global entityXplayer0memo = 10 , entityYplayer0memo = 10 , entityZplayer0memo = 10
Global soundmenu , channsoundmenu , volchannsoundmenu# = 0.0
Global soundplayer0 , channsoundplayer0 , MARCHARRETchannplayer0% = 1
Global volumesoundplayer0# = 0.0 , pitchsoundplayer0# = 0.0
Global anctravelpitchpivotplayer0# = 0.0 , anctravelyawpivotplayer0# = 0.0
Global travelpitchpivotplayer0# = 0.0 , travelyawpivotplayer0# = 0.0
Global poscollX = 0 , poscollY = 0 , poscollZ = 0
Global SpriteBalle , BalleSprite , BalleExiste = False , entitytouche
Global BalleTemps = 0 , MAX_VIE_BALLE = 100 , BalleSound
Global SpriteExplose , ExploseSprite , frameexplose% , seqexplose
Global ExploseExiste = False , ExploseTemps = 0 , MAX_VIE_EXPLOSE = 10
Global cubecamera , camera , baguette , indicdir , shownindicdir = 0
Global camerafusee , cubecamerafusee
Global choixcible = True , ciblechoisie , denomcible$ = " SYSTEM 0"
Global cameramenu
Global pivot222 , soleil222 , cube222 , pivotREF , pivotTEMP
Global numx% = 0 , numy% = 0 , numz% = 0
Global pivotrondeciel , numsoleil% = 1
Global nbmaxsysteme% = 10 , numpivpar% = 0
Global numpiv% = 0 , nummesh% = 0 , numsys% = 0 , nsys% = 0
Global filesys , dirdujeu , datatestfilesys% = 0 , datatestTCP32% = 0 , nomfilesys$ = ""
Global choixobjet$ = "" , numsysdef$ = "0"
Global pivsysrepX = 0 , pivsysrepY = 0 , pivsysrepZ = 0
Global usersysdir$ = "" , dirdusys = 0
Global pivotastrig , astrig
Global indicesurface% = 1 , RANDcouleurastrig = 1
Global x0% = 0 , y0% = 0 , z0% = 0
Global x1% = 0 , y1% = 0 , z1% = 0
Global x2% = 0 , y2% = 0 , z2% = 0
Global v0 , v1 , v2 , triangle , astrig_ext , couleurextastrig
Global cube25 , texcodecube25 , frame% = 1 , cube25jeu
Global nbmaxastrigjeu% = 5 , nbmaxastrigjeuTMP% = 1
Global astrigjeupicked = False , numastrigjeu% = 0
Global porti46 , porti47 , couleurportis
Global surface46 , surface47 , porti46ext , porti47ext
Global pivotturn46 , pivotporti46 , pivotturn47 , pivotporti47
Global seq46 , seq47 , seqastrig
Global pivotporti46jeu , pivotporti47jeu
Global nastrigj = 0
Global deplacx# = 0.0 , deplacy# = 0.0 , deplacz# = 0.0 , choixdeplacz% = 0
Global camyaw# = 0.0 , campitch# = 0.0 , camroll# = 0.0
Global deplajoyx# = 0.0 , deplajoyy# = 0.0 , JoystickZDir% = 0
Global mvx% = 0 , mvy% = 0 , mvz% = 0 , vitesse% = 4
Global inertx% = 0 , inerty% = 0 , inertz% = 0
Global numfich_captecran% = 0 , timerinfocapture% = 100 , signaltimer = False
Global findujeu = False
Global coorXdanscube% = 0 , coorYdanscube% = 0 , coorZdanscube% = 0
Global coorXpivotTEMP% = 0 , coorYpivotTEMP% = 0 , coorZpivotTEMP% = 0
Global coorXcam% = 0 , coorYcam% = 0 , coorZcam% = 0
Global curX# , curY# , curZ# , camspeed = 10
Global CamDX = 0 , CamDY = 0 , CamDZ = 0
Global Cammode
Global numlieusuivi% = 0 , num% = 0 , numsysOKactu% = 0
Global numcarac% = 0
Global repcapture$ = "screen captures"
Global repimage$ = "user images"
Global posxsouris% = 0 , posysouris% = 0 , getcolored = 0
Global pivotsysteme0
Global pivotrevolnibruCRdu0 , nibruCRdu0 , pivotnibruCRdu0 , texturenibruCRdu0
Global angrevonibruCRdu0 = 90 , nbrevolsterre = 0 , blocageterrenibruCRdu0 = True
Global texcube25BMS , texBMSvuSys0 , signaltimersys0 = False
Global pivotsoleil , lumiere , soleil , texturesoleila , texturesoleilb
Global pivotrevolpyanneau , pivotpyanneau , pyanneau
Global brushpyanneau , texturepyanneau
Global pivotrevolpointcube , pivotpointcube , pointcube
Global pivotrevolterre , pivotterre , terre , textureterre
Global pivotrevollune , lune , texturelune
Global pivotrevolmars , pivotmars , mars , texturemars
Global pivotsysteme1
Global infospac = False , machin , texcubespac
Global soleilpivotsysteme1 , soleilsysteme1 , cubesysteme1
Global systeme1pshX% = 0 , systeme1pshY% = 0 , systeme1pshZ% = 0
Global soleilcoin , cubetubespace
Global demicubetubespaceVerAvG , demicubetubespaceHorAvBaG , demicubetubespaceDirzBaG
Global pivotsysteme2 , conebase1 , conebase2
Global pivotconeterrain1 , pivotterrainchateau , pivotbulleterrain1
Global terrainchateau , terrainchateau2 , textureterraincahis , texmossyground
Global pivotchateau , chateaunormalb3dchateau , texturenormalb3dchateau
Global tablomur , texturemessage , texturemessedit , texturemessS2S3
Global filerecords
Global textureASKI , codetexASKI% = 0
Global numcaracmess% = 0 , nbmaxcaracmess% = 0 , caracASKImessage%
Global linemess1$ = "" , linemess2$ = "" , linemess3$ = ""
Global tapiroulant , bulleterrain1
Global coneterrain1 , textureconeterrain
Global rocher1bordS2 , rocher2bordS2 , rocher3bordS2
Global rocher , texturerocher , brushrocher
Global buse1tapiroulant , buse2tapiroulant , buse1terrain
Global mesh_defo , tex_mesh_defo
Global pivotfusee, fuseefighter3dsfusee , texturefighter3dsfusee
Global tempofusee = 0 , pivogarajS2
Global textureblitzlogo , texturedospanneau2code
Global xpostexdefil# = 1 , ypostexdefil# = 1
Global xpostapiroul# = 1 , ypostapiroul# = 1
Global stringcode$ = " " , numligstringcode% = 1 , numtexturecode% = 0
Global pivopanotelepoS2 , panotelepoS2 , texturepanotelepoS2 , cubetelepoS2
Global pivotpanneaucode , panneaucode
Global pivotpanneau2code , panneau2code , dospanneau2code
Global pivotpanneau3code , panneau3code , dospanneau3code
Global pivotpanneau6code , panneau6code , dospanneau6code
Global pivotpanneau7code , panneau7code , dospanneau7code
Global pivotpanneau8code , panneau8code , dospanneau8code
Global arbreS2ref
Global nbCRYessai% = 0 , nbNEXTimage% = 0
Global numJEANSimage% = 0 , nbJEANSmaxscans% = 0
Global numCRYimage% = 0 , nbCRYmaxscans% = 0
Global numUSERimage% = 0 , nbUSERmaxscans% = 0
Global texUSERpanneau7 , brushUSERpanneau7
Global dirimaUSER , imaUSERfile$ = ""
Global CRYPimaTEMP , imaTEMP , ORIGimaTEMP
Global octoffsetX% = 0 , octoffsetY% = 0
Global argumentARGB% , offsetARGB% = 0
Global pointimageX% = 1 , pointimageY% = 1 , crypointimageX% = 1 , crypointimageY% = 1
Global dirCAH , dirCAHstr$ = ""
Global scancahCRYPfile$ = "" , repfilcahCRYP$ = ""
Global ORIGfile$ = "" , CRYPfiles = ""
Global dirORIGstr$ = "" , dirORIG = 0
Global dirCRYPstr$ = "" , dirCRYP = 0
Global carac3Ddiz , carac3Duni , numcaracStalker = 0
Global arrivadestpivotfusee = True , numdestfusee = 3 , travelfusee = False
Global SIZEtexmirror = 0 , WIDTHtexmirror = 0 , HEIGHTtexmirror = 0
Global texturemirrorpanneaucode , cameramirrorpanneaucode , boulecampanneaucode
Global panoCOPrep , panoramaCOP , IMApanoCOP$ = ""
Global nbmaxIMApanoCOP% = 1 , numIMApanoCOP% = 0
Global fichIMApanoCOP$ = "" , TEXpanoCOP
Global cubepalissade3 , paneaupaliss , texturepaneaupaliss
Global portepaliss , seqpaliss , plaquepaliss
Global textureplaquepaliss , textureplacodepaliss , brushpaneaupaliss
Global uncode$ = "" , cleportepaliss = False , timerportepaliss% = 0
Global palisscodant = False , ASCuncar% = 0
Global pivotsysteme3
Global tableaustatir , textableaustatir , USERSdureemaxdetir = 0
Global tableaucodeS3 , tableauS3sysOK , textableauS3sysOK
Global bougertir = True , tempojeudetir = 0 , bonomasaute = False , dureedetir = 0
Global tableaustation , textableaustation , posfilemess = 0
Global nbmaxbumps% = 0 , numbumps% = 0 , delaibumps% = 0 , USERSnbmaxbumps%
Global stationservice , texstation
Global fileLOADLUV , surfcountLOADLUV , surfLOADLUV , vercountLOADLUV , LOADLUVu# , LOADLUVv#
Global conesysteme3 , conesysteme3bis
Global textureconesysteme3 , textureconesysteme3hmap
Global pivothearse , hearse , Xrothearse = 0
Global texturehearse , soundhearse
Global vitrothearse = 0 , pivotrotahearse , impacthearseTEMP = 0
Global podechap , podechap2
Global texture1feupodechap , texture2feupodechap
Global pivotbonom0
Global corpsbonom , tetebonom , mvtautobonom% = 1 , numbonom = 0
Global pivotbrasgauche , pivotbrasdroit , pivotjambegauche , pivotjambedroite
Global brasgauche , brasdroit , jambegauche , jambedroite , framebonom%
Global seqbrasgauche , seqbrasdroit , seqjambegauche , seqjambedroite , seqbonom
Global pivsurfbonom , ciblebonom2, ciblebonomcosX# = 0.0 , ciblebonomsinX# = 0.0
Global ciblebonom3actu = 0
Global briqpatemaison
Global pivotpatemaison0 , murspatemaison0 , texmurspatemaison0
Global toitpatemaison0 , plancherpatemaison0
Global textoitpatemaison , texplancherpatemaison
Global pivotpatemaison1 , murspatemaison1 , texmurspatemaison1
Global toitpatemaison1 , plancherpatemaison1
Global pivotsysteme4
Global pivotplanete4 , pivotorbiplanete4 , planete4 , athmosplanete4 , athmosbisplanete4
Global textureplanete4 , brushplanete4 , textureathmosplanete4 , textureathmosbisplanete4
Global xpostexathmo# = 0.0
Global seriedeclair , temposeriedeclair , tempoeclair
Global collizimeubtous = False , numzimeub = 0 , totalzimeub = 35 , zimturny4 = 1
Global ancrollplayer0orbi4 = 0 , axorbiplanete4
Global ancXplayer0orbi4# = 0.0 , ancYplayer0orbi4# = 0.0 , ancZplayer0orbi4# = 0.0
Global texturezimeub
Global soundthunder1 , channthunder1 , soundthunder2 , channthunder2
Global tityFX
Global mongyourt , piquyourt , mongyourtYintsect = 0 , basyourt , angTMP = 0
Global pivotsysteme5
Global pivotcuboite5 , cuboite5 , texturecuboite5 , arbre
Global pivopanotelepoS5 , panotelepoS5 , texturepanotelepoS5 , cubetelepoS5
Global pivopanotelepoS5cuboite
Global pivotrevolterzigo1du5 , terzigo1du5 , pivotterzigo1du5 , textureterzigo1du5
Global angrevoterzigo1du5 = 90 , blocageterreterzigo1du5 = True
Global pivotrevolterzigo2du5 , terzigo2du5 , pivotterzigo2du5 , textureterzigo2du5
Global angrevoterzigo2du5 =  0 , blocageterreterzigo2du5 = True
Global pivotrevolterzigo3du5 , terzigo3du5 , pivotterzigo3du5 , textureterzigo3du5
Global angrevoterzigo3du5 = 90 , blocageterreterzigo3du5 = True
Global pivotrevolterzigo4du5 , terzigo4du5 , pivotterzigo4du5 , textureterzigo4du5
Global angrevoterzigo4du5 =  0 , blocageterreterzigo4du5 = True
Global pivotrevolterzigo5du5 , terzigo5du5 , pivotterzigo5du5 , textureterzigo5du5
Global angrevoterzigo5du5 = 90 , blocageterreterzigo5du5 = True
Global pivotrevolterzigo6du5 , terzigo6du5 , pivotterzigo6du5 , textureterzigo6du5
Global angrevoterzigo6du5 =  0 , blocageterreterzigo6du5 = True
Global pivotrevolterzigo7du5 , terzigo7du5 , pivotterzigo7du5 , textureterzigo7du5
Global angrevoterzigo7du5 = 90 , blocageterreterzigo7du5 = True
Global pivotrevolterzigo8du5 , terzigo8du5 , pivotterzigo8du5 , textureterzigo8du5
Global angrevoterzigo8du5 =  0 , blocageterreterzigo8du5 = True
Global pivotsysteme6
Global x# = 0.0 , y# = 0.0 , z# = 0.0 , dotsyst6 , boulepoints6
Global solarcenter6 , panneaucenter1de6 , panneaucenter2de6
Global postePC1 , postePC2
Global panneaucenter3de6 , panneaucenter4de6
Global texturepanneaucenter6 , texturepanneau2center6
Global texturepanneau3center6 , texturepanneau3defiltex
Global texturepanneau4center6 , texturepanneau4defilcah
Global xposdefiltexS6# = 0.0 , yposdefiltexS6# = 0.0
Global codexte , lignecodexte1$ = "" , lignecodexte2$ = "" , sekfilcodexte% = 0
Global cahdexte , lignecahdexte$ = "" , sekfilcahdexte% = 0 , timercahdexte% = 0
Global destcar = 0 , datagal$ = "" , timercodexte% = 0
Global DATAgalaxOK = False , DATEgalexOK = False
Global bankdefiltex = 0 , bankdefilcah = 0
Global pivotfighter2du6 , fighter2du6 , fighter2du6menu
Global numsysfighter2du6% = 4 , nbtrfighter2du6% = 0
Global texsolarcenter6 , texfighter2du6
Dim player ( 10 )
Dim surface ( 45 )
Dim coorXastrigjeu ( 5 )
Dim coorYastrigjeu ( 5 )
Dim coorZastrigjeu ( 5 )
Dim pivotespace ( 3 , 3 , 3 )
Dim teximaJEANS ( 100 )
Dim teximaUSER ( 100 )
Dim teximaCRY ( 100 )
Dim carac3Ddizaines ( 10 )
Dim carac3Dunites ( 10 )
Dim carac3DStalker ( 18 )
Dim pivotsysteme ( 10 )
Dim photopivotsyst ( 10 )
Dim numsysOK ( 10 )
Dim pivotastrigjeu ( 5 )
Dim zimeub ( 35 )
Dim RANDcarcodpaliss ( 6 )
Dim pivotbonom ( 4 )
Dim pivotplusmaison ( 25 )
Dim ciblebonom3 ( 4 )
Dim pivuser ( 5 , 10 )
Dim lumuser ( 5 , 10 )
Dim meshuser ( 5 , 10 )
Dim nbmaxpivsyst ( 10 )
Dim pivotsysrep ( 20 , 20 , 20 )
Dim indicefx ( 7 )
nbmaxpivsyst ( 0 ) = 0
nbmaxpivsyst ( 1 ) = 0
nbmaxpivsyst ( 2 ) = 0
nbmaxpivsyst ( 3 ) = 0
nbmaxpivsyst ( 4 ) = 0
nbmaxpivsyst ( 5 ) = 0
nbmaxpivsyst ( 6 ) = 0
nbmaxpivsyst ( 7 ) = 0
nbmaxpivsyst ( 8 ) = 0
nbmaxpivsyst ( 9 ) = 0
AppTitle "Config Ecran"
SetGfx ( )
Graphics3D GW% , GH% , GD% , GM%
GB# = GraphicsBuffer ( )
capslevel = GfxDriverCaps3D ( )
If capslevel% < 110 Then
Write "Votre carte graphique ne supporte pas les maps d'environnement cubique. Effets innattendus."
If capslevel% < 100 RuntimeError "Votre carte graphique ne supporte pas les commandes BlitzBasic."
WaitKey ( )
EndIf
.funcsterraf32bb
Function do_Landscaping(mesh,iterate,scale#)
surf=GetSurface(mesh,CountSurfaces(mesh))
n.vector=New vector
p.vector=New vector
teraf_v.vector=New vector
For a=0 To iterate
e#=Rnd(-scale,scale)
Random_Vector(n)
For b=0 To CountVertices(surf)-1
teraf_v\teraf_x=VertexX(surf,b)
teraf_v\teraf_y=VertexY(surf,b)
teraf_v\teraf_z=VertexZ(surf,b)
prod#=Vect_Product(n,teraf_v)
If prod>0 Then
VertexCoords surf,b,VertexX(surf,b)+(VertexNX(surf,b)*e),VertexY(surf,b)+(VertexNY(surf,b)*e),VertexZ(surf,b)+(VertexNZ(surf,b)*e)
Else
VertexCoords surf,b,VertexX(surf,b)-(VertexNX(surf,b)*e),VertexY(surf,b)-(VertexNY(surf,b)*e),VertexZ(surf,b)-(VertexNZ(surf,b)*e)
End If
Next
fst=False
Next
Delete n
Delete p
Delete teraf_v
Return vcnt
End Function
Function Random_Vector(teraf_v.vector)
teraf_v\teraf_x=Rnd(-1,1)
teraf_v\teraf_y=Rnd(-1,1)
teraf_v\teraf_z=Rnd(-1,1)
Normalise(teraf_v)
End Function
Function CrossProduct(teraf_x1#,teraf_y1#,teraf_z1#,teraf_x2#,teraf_y2#,teraf_z2#)
CProd\teraf_x=(teraf_y1*teraf_z2)-(teraf_z1*teraf_y2)
CProd\teraf_y=(teraf_z1*teraf_x2)-(teraf_x1*teraf_z2)
CProd\teraf_z=(teraf_x1*teraf_y2)-(teraf_y1*teraf_x2)
End Function
Function DotProduct#(teraf_x1#,teraf_y1#,teraf_z1#,teraf_x2#,teraf_y2#,teraf_z2#)
DProd=((teraf_x1*teraf_x2)+(teraf_y1*teraf_y2)+(teraf_z1*teraf_z2))
Return DProd
End Function
Function CproductX#()
Return CProd\teraf_x
End Function
Function CproductY#()
Return CProd\teraf_y
End Function
Function CproductZ#()
Return CProd\teraf_z
End Function
Function DProduct#()
Return DProd
End Function
Function Delete_Vectors()
Delete Each vector
End Function
Function GetBearingVector(ent1,ent2)
Bearing\teraf_x#=EntityX(ent2)-EntityX(ent1)
Bearing\teraf_y#=EntityY(ent2)-EntityY(ent1)
Bearing\teraf_z#=EntityZ(ent2)-EntityZ(ent1)
Normalise(Bearing)
End Function
Function BearingX#()
Return Bearing\teraf_x
End Function
Function BearingY#()
Return Bearing\teraf_y
End Function
Function BearingZ#()
Return Bearing\teraf_z
End Function
Function Normalise(a.vector)
l# = Mag(a\teraf_x,a\teraf_y,a\teraf_z)
a\teraf_x=a\teraf_x/l
a\teraf_y=a\teraf_y/l
a\teraf_z=a\teraf_z/l
Return
End Function
Function Norm(teraf_x#,teraf_y#,teraf_z#)
l# = Mag(teraf_x,teraf_y,teraf_z)
Normal\teraf_x=teraf_x/l
Normal\teraf_y=teraf_y/l
Normal\teraf_z=teraf_z/l
End Function
Function NormX#()
Return Normal\teraf_x
End Function
Function NormY#()
Return Normal\teraf_y
End Function
Function NormZ#()
Return Normal\teraf_z
End Function
Function Mag#(teraf_x#,teraf_y#,teraf_z#)
Return Sqr(teraf_x^2+teraf_y^2+teraf_z^2)
End Function
Function TurntoFace(ent1,ent2,tol#)
GetBearingVector(ent1,ent2)
dx#=BearingX()*360/tol
dy#=BearingY()*360/tol
dz#=BearingZ()*360/tol
TurnEntity ent1,-dy,-dx,0
End Function
Function range#(ent1,ent2)
teraf_x#=EntityX(ent2)-EntityX(ent1)
teraf_y#=EntityY(ent2)-EntityY(ent1)
teraf_z#=EntityZ(ent2)-EntityZ(ent1)
Return Sqr(teraf_x^2+teraf_y^2+teraf_z^2)
End Function
Function range2d#(ent1,ent2)
teraf_x#=EntityX(ent2)-EntityX(ent1)
teraf_z#=EntityZ(ent2)-EntityZ(ent1)
Return Sqr(teraf_x^2+teraf_z^2)
End Function
Function rangepoint#(teraf_x1#,teraf_y1#,teraf_z1#,teraf_x2#,teraf_y2#,teraf_z2#)
teraf_x#=teraf_x1-teraf_x2
teraf_y#=teraf_y1-teraf_y2
teraf_z#=teraf_z1-teraf_z2
Return Sqr(teraf_x^2+teraf_y^2+teraf_z^2)
End Function
Function Get_Terrain_Normal(terr,teraf_x#,teraf_z#)
teraf_x1#=teraf_x:teraf_z1#=teraf_z:teraf_y1#=TerrainY(terr,teraf_x1,0,teraf_z1)
teraf_x2#=teraf_x+.5:teraf_z2#=teraf_z:teraf_y2#=TerrainY(terr,teraf_x2,0,teraf_z2)
teraf_x3#=teraf_x:teraf_z3#=teraf_z+.5:teraf_y3#=TerrainY(terr,teraf_x3,0,teraf_z3)
xx1#=teraf_x2-teraf_x1:yy1#=teraf_y2-teraf_y1:zz1#=teraf_z2-teraf_z1
xx2#=teraf_x3-teraf_x1:yy2#=teraf_y3-teraf_y1:zz2#=teraf_z3-teraf_z1
Norm(xx1,yy1,zz1)
xx1=NormX():yy1=normy():zz1=normz()
Norm(xx2,yy2,zz2)
xx2=NormX():yy2=normy():zz2=normz()
CrossProduct(xx2,yy2,zz2,xx1,yy1,zz1)
TNorm\teraf_x=CProductX()
TNorm\teraf_y=CProductY()
TNorm\teraf_z=CProductZ()
End Function
Function TNormalX#()
Return Tnorm\teraf_x
End Function
Function TNormalY#()
Return Tnorm\teraf_y
End Function
Function TNormalZ#()
Return Tnorm\teraf_z
End Function
Function Vect_Product#(a.vector,b.vector)
DProd=((a\teraf_x*b\teraf_x)+(a\teraf_y*b\teraf_y)+(a\teraf_z*b\teraf_z))
Return DProd
End Function
Function distance_to_plane#(teraf_x1#,teraf_y1#,teraf_z1#,teraf_x2#,teraf_y2#,teraf_z2#,teraf_x3#,teraf_y3#,teraf_z3#)
End Function
.InitCoors
Global GWr# = Float GW%
Global GHr# = Float GH%
Global rapGW1920# = GWr# / 1920
Global rapGH1080# = GHr# / 1080
Global coor010W%  = Int (   10 * rapGW1920# )
Global coor011W%  = Int (   11 * rapGW1920# )
Global coor020W%  = Int (   20 * rapGW1920# )
Global coor022W%  = Int (   22 * rapGW1920# )
Global coor030W%  = Int (   30 * rapGW1920# )
Global coor040W%  = Int (   40 * rapGW1920# )
Global coor060W%  = Int (   60 * rapGW1920# )
Global coor078W%  = Int (   78 * rapGW1920# )
Global coor080W%  = Int (   80 * rapGW1920# )
Global coor090W%  = Int (   90 * rapGW1920# )
Global coor100W%  = Int (  100 * rapGW1920# )
Global coor120W%  = Int (  120 * rapGW1920# )
Global coor130W%  = Int (  130 * rapGW1920# )
Global long156W%  = Int (  156 * rapGW1920# )
Global long160W%  = Int (  160 * rapGW1920# )
Global coor180W%  = Int (  180 * rapGW1920# )
Global coor200W%  = Int (  200 * rapGW1920# )
Global coor220W%  = Int (  220 * rapGW1920# )
Global long260W%  = Int (  260 * rapGW1920# )
Global long280W%  = Int (  280 * rapGW1920# )
Global coor300W%  = Int (  300 * rapGW1920# )
Global long384W%  = Int (  386 * rapGW1920# )
Global long390W%  = Int (  390 * rapGW1920# )
Global coor400W%  = Int (  400 * rapGW1920# )
Global coor480W%  = Int (  480 * rapGW1920# )
Global coor520W%  = Int (  520 * rapGW1920# )
Global coor600W%  = Int (  600 * rapGW1920# )
Global coor660W%  = Int (  660 * rapGW1920# )
Global coor760W%  = Int (  760 * rapGW1920# )
Global coor762W%  = Int (  762 * rapGW1920# )
Global coor800W%  = Int (  800 * rapGW1920# )
Global coor900W%  = Int (  900 * rapGW1920# )
Global coor920W%  = Int (  920 * rapGW1920# )
Global coor950W%  = Int (  950 * rapGW1920# )
Global coor960W%  = Int (  960 * rapGW1920# )
Global coor1000W% = Int ( 1000 * rapGW1920# )
Global coor1100W% = Int ( 1100 * rapGW1920# )
Global coor1200W% = Int ( 1200 * rapGW1920# )
Global coor1160W% = Int ( 1160 * rapGW1920# )
Global coor1400W% = Int ( 1400 * rapGW1920# )
Global coor1570W% = Int ( 1570 * rapGW1920# )
Global coor1580W% = Int ( 1580 * rapGW1920# )
Global coor1600W% = Int ( 1600 * rapGW1920# )
Global coor1700W% = Int ( 1700 * rapGW1920# )
Global coor1720W% = Int ( 1720 * rapGW1920# )
Global coor1740W% = Int ( 1740 * rapGW1920# )
Global coor1742W% = Int ( 1742 * rapGW1920# )
Global coor1800W% = Int ( 1800 * rapGW1920# )
Global coor1820W% = Int ( 1820 * rapGW1920# )
Global haut008H%  = Int (   08 * rapGH1080# )
Global haut010H%  = Int (   10 * rapGH1080# )
Global haut018H%  = Int (   18 * rapGH1080# )
Global haut020H%  = Int (   20 * rapGH1080# )
Global haut024H%  = Int (   24 * rapGH1080# )
Global haut030H%  = Int (   30 * rapGH1080# )
Global haut036H%  = Int (   36 * rapGH1080# )
Global haut040H%  = Int (   40 * rapGH1080# )
Global haut050H%  = Int (   50 * rapGH1080# )
Global haut060H%  = Int (   60 * rapGH1080# )
Global haut070H%  = Int (   70 * rapGH1080# )
Global haut076H%  = Int (   76 * rapGH1080# )
Global haut078H%  = Int (   78 * rapGH1080# )
Global haut080H%  = Int (   80 * rapGH1080# )
Global haut090H%  = Int (   90 * rapGH1080# )
Global haut100H%  = Int (  100 * rapGH1080# )
Global haut110H%  = Int (  110 * rapGH1080# )
Global haut120H%  = Int (  120 * rapGH1080# )
Global haut130H%  = Int (  130 * rapGH1080# )
Global coor140H%  = Int (  140 * rapGH1080# )
Global coor150H%  = Int (  150 * rapGH1080# )
Global coor160H%  = Int (  160 * rapGH1080# )
Global coor170H%  = Int (  170 * rapGH1080# )
Global coor180H%  = Int (  180 * rapGH1080# )
Global coor190H%  = Int (  190 * rapGH1080# )
Global coor200H%  = Int (  200 * rapGH1080# )
Global coor210H%  = Int (  210 * rapGH1080# )
Global coor220H%  = Int (  220 * rapGH1080# )
Global coor228H%  = Int (  228 * rapGH1080# )
Global coor229H%  = Int (  229 * rapGH1080# )
Global coor230H%  = Int (  230 * rapGH1080# )
Global coor240H%  = Int (  240 * rapGH1080# )
Global coor246H%  = Int (  246 * rapGH1080# )
Global coor250H%  = Int (  250 * rapGH1080# )
Global coor260H%  = Int (  260 * rapGH1080# )
Global coor270H%  = Int (  270 * rapGH1080# )
Global coor280H%  = Int (  280 * rapGH1080# )
Global coor290H%  = Int (  290 * rapGH1080# )
Global coor300H%  = Int (  300 * rapGH1080# )
Global coor310H%  = Int (  310 * rapGH1080# )
Global coor320H%  = Int (  320 * rapGH1080# )
Global coor330H%  = Int (  330 * rapGH1080# )
Global coor350H%  = Int (  350 * rapGH1080# )
Global coor360H%  = Int (  360 * rapGH1080# )
Global coor370H%  = Int (  370 * rapGH1080# )
Global coor380H%  = Int (  380 * rapGH1080# )
Global coor390H%  = Int (  390 * rapGH1080# )
Global coor400H%  = Int (  400 * rapGH1080# )
Global coor410H%  = Int (  410 * rapGH1080# )
Global coor420H%  = Int (  420 * rapGH1080# )
Global coor430H%  = Int (  430 * rapGH1080# )
Global coor440H%  = Int (  440 * rapGH1080# )
Global coor450H%  = Int (  450 * rapGH1080# )
Global coor460H%  = Int (  460 * rapGH1080# )
Global coor470H%  = Int (  470 * rapGH1080# )
Global coor490H%  = Int (  490 * rapGH1080# )
Global coor500H%  = Int (  500 * rapGH1080# )
Global coor516H%  = Int (  516 * rapGH1080# )
Global coor530H%  = Int (  530 * rapGH1080# )
Global coor550H%  = Int (  550 * rapGH1080# )
Global coor570H%  = Int (  570 * rapGH1080# )
Global coor590H%  = Int (  590 * rapGH1080# )
Global coor600H%  = Int (  600 * rapGH1080# )
Global coor610H%  = Int (  610 * rapGH1080# )
Global coor620H%  = Int (  620 * rapGH1080# )
Global coor640H%  = Int (  640 * rapGH1080# )
Global coor660H%  = Int (  660 * rapGH1080# )
Global coor680H%  = Int (  680 * rapGH1080# )
Global coor850H%  = Int (  850 * rapGH1080# )
Global coor860H%  = Int (  860 * rapGH1080# )
Global coor880H%  = Int (  880 * rapGH1080# )
Global coor900H%  = Int (  900 * rapGH1080# )
Global coor910H%  = Int (  910 * rapGH1080# )
Global coor920H%  = Int (  920 * rapGH1080# )
Global coor940H%  = Int (  940 * rapGH1080# )
Global coor941H%  = Int (  941 * rapGH1080# )
Global coor942H%  = Int (  942 * rapGH1080# )
Global coor950H%  = Int (  950 * rapGH1080# )
Global coor960H%  = Int (  960 * rapGH1080# )
Global coor970H%  = Int (  970 * rapGH1080# )
Global coor980H%  = Int (  980 * rapGH1080# )
Global coor990H%  = Int (  990 * rapGH1080# )
AppTitle "Demarrage"
dossiercourant$ = CurrentDir$ ( )
paramCMD$ = CommandLine$ ( )
paramCMDnb% = CommandLine ( )
paramCMDleft$ = Left$ ( paramCMD$ , 3 )
Viewport 0 , 0 , GW , GH
CrissMingo = LoadSound ( "C:\Windows\Media\Windows User Account Control.wav" )
arialTTF = LoadFont ( "C:\Windows\Fonts\Arial\arial.ttf" , 8 * Int ( rapGW1920# + rapGH1080# ) , 0 , 0 , 0 )
If arialTTF = 0 Then Print " Manque arial.ttf dans C:\Windows\Fonts\Arial\ ==> d?faut formatage texte" : WaitKey ( ) : Print
GabriolaTTF = LoadFont ( "C:\Windows\Fonts\Gabriola.ttf" , 24 * Int ( rapGW1920# + rapGH1080# ) , 0 , 0 , 0 )
If GabriolaTTF = 0 Then Print " Manque Gabriola.ttf dans C:\Windows\Fonts\ ==> d?faut formatage texte" : WaitKey ( ) : Print
app850fon = LoadFont ( "C:\Windows\Fonts\Terminal\app850.fon" , 12 , 1 , 0 , 0 )
If app850fon = 0 Then Print " Manque app850.fon dans C:\Windows\Fonts\Terminal\ ==> d?faut formatage texte" : WaitKey ( ) : Print
SeedRnd ( MilliSecs ( ) )
RANDcarcodpaliss% ( 0 ) = Rand ( 48 , 90 )
RANDcarcodpaliss% ( 1 ) = Rand ( 48 , 90 )
RANDcarcodpaliss% ( 2 ) = Rand ( 48 , 90 )
RANDcarcodpaliss% ( 3 ) = Rand ( 48 , 90 )
RANDcarcodpaliss% ( 4 ) = Rand ( 48 , 90 )
RANDcarcodpaliss% ( 5 ) = Rand ( 48 , 90 )
For numsys% = 0 To 9 numsysOK ( numsys% ) = False Next
If FileType ( "userdata" ) <> 1 And FileType ( "Galaxang32.exe" ) = 1 Then
filerecords = WriteFile ( "userdata" )
WriteInt filerecords , 0
WriteInt filerecords , 0
WriteInt filerecords , 0
WriteInt filerecords , 1000
WriteInt filerecords , 1000
WriteInt filerecords , 1000
WriteInt filerecords , 0
WriteInt filerecords , 0
WriteInt filerecords , 0
WriteInt filerecords , 0
WriteInt filerecords , 1
WriteInt filerecords , 0
WriteInt filerecords , 0
WriteInt filerecords , 0
WriteInt filerecords , 0
WriteInt filerecords , 0
WriteInt filerecords , 0
WriteInt filerecords , 0
WriteInt filerecords , 0
WriteInt filerecords , 0
WriteInt filerecords , 0
WriteInt filerecords , 0
WriteInt filerecords , 0
WriteInt filerecords , 13
WriteInt filerecords , Asc ( "V" )
WriteInt filerecords , Asc ( "o" )
WriteInt filerecords , Asc ( "t" )
WriteInt filerecords , Asc ( "r" )
WriteInt filerecords , Asc ( "e" )
WriteInt filerecords , Asc ( " " )
WriteInt filerecords , Asc ( "m" )
WriteInt filerecords , Asc ( "e" )
WriteInt filerecords , Asc ( "s" )
WriteInt filerecords , Asc ( "s" )
WriteInt filerecords , Asc ( "a" )
WriteInt filerecords , Asc ( "g" )
WriteInt filerecords , Asc ( "e" )
WriteInt filerecords , 8
WriteInt filerecords , Asc ( "e" )
WriteInt filerecords , Asc ( "n" )
WriteInt filerecords , Asc ( " " )
WriteInt filerecords , Asc ( "t" )
WriteInt filerecords , Asc ( "r" )
WriteInt filerecords , Asc ( "o" )
WriteInt filerecords , Asc ( "i" )
WriteInt filerecords , Asc ( "s" )
WriteInt filerecords , 6
WriteInt filerecords , Asc ( "l" )
WriteInt filerecords , Asc ( "i" )
WriteInt filerecords , Asc ( "g" )
WriteInt filerecords , Asc ( "n" )
WriteInt filerecords , Asc ( "e" )
WriteInt filerecords , Asc ( "s" )
CloseFile filerecords
EndIf
repcapture$ = "screen captures"
If FileType ( "Galaxang32.exe" ) = 1 And FileType ( repcapture$ ) = 2 Then
numfich_captecran = 0
While FileType ( "screen captures\savimageG32" + RSet$ ( numfich_captecran , 2 ) + ".BMP" ) = 1
numfich_captecran = numfich_captecran + 1
Delay 100
Wend
numfich_captecran = numfich_captecran - 1
EndIf
repimage$ = "user images"
If FileType ( repimage$ ) <> 2 And FileType ( "Galaxang32.exe" ) = 1 Then CreateDir repimage$
AppTitle "Infos & Option"
SetBuffer FrontBuffer ( )
Viewport 0 , 0 , GW , GH
ClsColor 80, 30, 150
Color 200 , 70 , 90
Cls
Text coor960W , coor600H , texteinfopb1$
Text coor960W , coor620H , texteinfopb2$
Text coor960W , coor640H , texteinfopb3$
Text coor960W , coor660H , texteinfopb4$
Text coor960W , coor680H , texteinfopb5$
Color 255 , 255 , 255
Plot 0 , 1
Plot GW - 1 , 1
Plot 0 , GH - 1
Plot GW - 1 , GH - 1
Print
Print Str ( 2.5 )
Print "parammu"
Print "Galaxang32" + FileType ( "DUTI.POK" ) + paramCMD$
Print
Print
Print
Print "Largeur : " + GraphicsWidth  ( )
Print "Hauteur : " + GraphicsHeight ( )
Print "Dossier actuel : "
Print dossiercourant$
Print
Print
Print
Print
Print
Color 255 , 200 , 55
Print "Images supp   :   touche 'Retour Arriere' "
Print "Abandonner   :   touche 'Echap' "
Print "Continuer   :   touche "
PakOutputDir "C:\windows\Temp"
PakInit "Galaxang32.exe" , $0 , "TEMP" , $0
presentimage = LoadImage ( Pak ( "photpivsys4.bmp" ) ) : DLPak ( )
DrawImage presentimage , GW / 2 - ImageWidth ( presentimage ) / 2 , GH / 2 - ImageHeight ( presentimage ) / 2
Color 255 , 255 , 255
Text 6 * ( GW / 8 ) , 7 * ( GH / 8 ) , "G A L A X A N G" , 1 , 1
Text 6 * ( GW / 8 ) , 7 * ( GH / 8 ) + 30 , "parapry" , 1 , 1
Locate GW / 8 , 7 * ( GH / 8 )
Print "Presse a quai"
Print
PlaySound ( CrissMingo )
FlushKeys
WaitKey ( )
.InitBisTextures17bb
If KeyHit ( 1 ) Then End
.bones2
.endofbones
PakOutputDir "C:\windows\Temp"
PakInit "Galaxang32.exe" , $0 , "TEMP" , $0
PakBulkOverWrite = False
AppTitle "Inits Textures"
PlaySound ( CrissMingo )
ClearTextureFilters
SetFont GabriolaTTF
FlushKeys ( )
Cls
If FileType ( "AideCreaSyst.txt" ) <> 1 And FileType ( "Galaxang32.exe" ) = 1 Then
Print
Print "Cr?er fichier ' AideCreaSyst.txt ' ?   Enter (2 secondes)"
Delay ( 2000 )
If KeyHit ( 28 ) Then
CopyFile Pak ( "AideCreaSyst.txt" ) , dossiercourant$ + "AideCreaSyst.txt" : DLPak ( )
Print
If FileType ( "AideCreaSyst.txt" ) = 1 Then
Print "Fichier 'AideCreaSyst.txt' cr?? ."
Else
Print "ECHEC creation 'AideCreaSyst.txt' ."
EndIf
Delay ( 2000 )
Print "OK ?"
WaitKey ( )
EndIf
EndIf
photopivotsyst ( 0 ) = LoadImage ( Pak ( "photpivsys0.BMP" ) ) : DLPak ( )
photopivotsyst ( 1 ) = LoadImage ( Pak ( "photpivsys1.BMP" ) ) : DLPak ( )
photopivotsyst ( 2 ) = LoadImage ( Pak ( "photpivsys2.BMP" ) ) : DLPak ( )
photopivotsyst ( 3 ) = LoadImage ( Pak ( "photpivsys3.BMP" ) ) : DLPak ( )
photopivotsyst ( 4 ) = LoadImage ( Pak ( "photpivsys4.BMP" ) ) : DLPak ( )
photopivotsyst ( 5 ) = LoadImage ( Pak ( "photpivsys5.BMP" ) ) : DLPak ( )
photopivotsyst ( 6 ) = LoadImage ( Pak ( "photpivsys6.BMP" ) ) : DLPak ( )
photopivotsyst ( 7 ) = LoadImage ( Pak ( "photpivsys789.bmp" ) ) : DLPak ( )
photopivotsyst ( 8 ) = LoadImage ( Pak ( "photpivsys789.bmp" ) ) : DLPak ( )
photopivotsyst ( 9 ) = LoadImage ( Pak ( "photpivsys789.bmp" ) ) : DLPak ( )
If nbUSERmaxscans  = 0 Then teximaUSER ( 0 )  = LoadTexture ( Pak ( "New-York-City-Skyline.jpg" ) , 1 + 8 + 16 + 32 + 512 ) : DLPak ( )
If nbJEANSmaxscans = 0 Then teximaJEANS ( 0 ) = LoadTexture ( Pak ( "New-York-City-Skyline.jpg" ) , 1 + 8 + 16 + 32 + 512 ) : DLPak ( )
If nbCRYmaxscans   = 0 Then teximaCRY ( 0 )   = LoadTexture ( Pak ( "New-York-City-Skyline.jpg" ) , 1 + 8 + 16 + 32 + 512 ) : DLPak ( )
If numIMApanoCOP   = 0 Then TEXpanoCOP        = LoadTexture ( Pak ( "New-York-City-Skyline.jpg" ) , 1 + 8 + 16 + 32 + 512 ) : DLPak ( )
soundplayer0 = LoadSound ( "C:\Windows\Media\Windows User Account Control.wav" )
LoopSound soundplayer0
soundfighter = Load3DSound ( Pak ( "Brrr03.wma" ) ) : DLPak ( )
LoopSound soundfighter
soundhearse = Load3DSound ( Pak ( "Brrr03.wma" ) ) : DLPak ( )
LoopSound soundhearse
soundthunder1 = LoadSound ( Pak ( "thunder.wav" ) ) : DLPak ( )
SoundVolume soundthunder1 , 0.02
SoundPitch soundthunder1 , 10000
soundthunder2 = LoadSound ( Pak ( "txstorm.wav" ) ) : DLPak ( )
SoundVolume soundthunder2 , 0.02
SoundPitch soundthunder2 , 11000
soundmenu = LoadSound ( "C:\Windows\Media\Windows User Account Control.wav" )
LoopSound soundmenu
channsoundmenu = PlaySound ( soundmenu )
volchannsoundmenu# = 0.1
ChannelVolume channsoundmenu , volchannsoundmenu#
PauseChannel channsoundmenu
.CREEtextures
texUSERpanneau7 = CreateTexture ( 128 , 128 , 1 + 8 )
TextureBlend texUSERpanneau7 , 3
textableauS3sysOK = CreateTexture ( 64 , 64 , 1 + 8 )
texcodecube25 = CreateTexture ( 32 , 32 , 1 + 8 )
texcube25BMS = CreateTexture ( 64 , 64 , 1 + 8 )
texBMSvuSys0 = CreateTexture ( 64 , 64 , 1 + 8 )
texcubespac = CreateTexture ( 64 , 64 , 1 + 8 )
texturepanneaucenter6 = CreateTexture ( 128 , 128 , 1 + 8 )
texturepanneau2center6 = CreateTexture ( 128 , 128 , 1 + 8 )
texturepanneau3center6 = CreateTexture ( 128 , 128 , 1 + 8 )
texturepanneau3defiltex = CreateTexture ( 512 , 512 , 1 + 8 )
texturepanneau4center6 = CreateTexture ( 128 , 128 , 1 + 8 )
texturepanneau4defilcah = CreateTexture ( 512 , 256 , 1 + 8 )
texturemessage = CreateTexture ( 256 , 256 , 1 + 8 )
texturemessedit = CreateTexture ( 256 , 256 , 1 + 8 )
texturemessS2S3 = CreateTexture ( 256 , 256 , 1 + 8 )
texturecryptexCAH1 = CreateTexture ( 2048 , 1024 , 1 + 8 + 16 + 32 )
texturedecryptexCAH1 = CreateTexture ( 2048 , 1024 , 1 + 8 + 16 + 32 )
textureplaquepaliss = CreateTexture ( 128 , 128 , 1 + 8 )
textureplacodepaliss = CreateTexture ( 128 , 128 , 1 + 8 )
texturepaneaupaliss = CreateTexture ( 32 , 32 , 1 + 8 )
textureASKI = CreateTexture ( 1024 , 1024 , 1 + 8 + 16 + 32 )
textableaustation = CreateTexture ( 128 , 128 , 1 + 8 )
textableaustatir = CreateTexture ( 128 , 128 , 1 + 8 )
texturesoleila = LoadTexture ( Pak ( "fire 2.png" ) , 1 + 8 ) : DLPak ( )
texturesoleilb = LoadTexture ( Pak ( "fire 2.png" ) , 1 + 8 ) : DLPak ( )
texturepyanneau = LoadTexture ( Pak ( "tiles.bmp" ) , 1 + 8 ) : DLPak ( )
brushpyanneau = LoadBrush ( Pak ( "tiles.bmp" ) , 1 + 8 + 64 ) : DLPak ( )
textureterre = LoadTexture ( Pak ( "planis.bmp" ) , 1 + 8 ) : DLPak ( )
texturenibruCRdu0 = LoadTexture ( Pak ( "planis.bmp" ) , 1 + 8 ) : DLPak ( )
ScaleTexture texturenibruCRdu0 , -1 , 1
PositionTexture texturenibruCRdu0 , 0.35 , 0
textureterzigo1du5 = LoadTexture ( Pak ( "planis.bmp" ) , 1 + 8 ) : DLPak ( )
texturelune = LoadTexture ( Pak ( "MOON.JPG" ) , 1 + 8 ) : DLPak ( )
texturemars = LoadTexture ( Pak ( "mars.jpg" ) , 1 + 8 ) : DLPak ( )
textureblitzlogo = LoadTexture ( Pak ( "IconesBasics.jpg" ) , 1 + 8 ) : DLPak ( )
textureterraincahis = LoadTexture ( Pak ( "New-York-City-Skyline.jpg" ) , 1 + 8 ) : DLPak ( )
textureconeterrain = LoadTexture ( Pak ( "Water-2_mip.BMP" ) , 1 + 2 + 4 + 8 ) : DLPak ( )
TextureBlend textureconeterrain , 1
texturenormalb3dchateau = LoadTexture ( Pak ( "wall.jpg" ) , 1 + 8 ) : DLPak ( )
texturepanotelepoS2 = LoadTexture ( Pak ( "bluskinit2R.jpg" ) , 1 + 8 ) : DLPak ( )
texturepanotelepoS5 = LoadTexture ( Pak ( "bluskinit2R.jpg" ) , 1 + 8 ) : DLPak ( )
texturedospanneau2code = LoadTexture ( Pak ( "rock.jpg" ) , 1 + 8 ) : DLPak ( )
texturefighter3dsfusee = LoadTexture ( Pak ( "fighter.jpg" ) , 1 + 8 ) : DLPak ( )
tex_mesh_defo = LoadTexture ( Pak ( "mesh_defo.bmp" ) , 4 + 8 + 64 ) : DLPak ( )
ScaleTexture tex_mesh_defo , 10 , 10
textureconesysteme3hmap = LoadTexture ( Pak ( "hmap.bmp" ) , 1 + 8 ) : DLPak ( )
textureconesysteme3 = LoadTexture ( Pak ( "MossyGround.BMP" ) , 1 + 8 ) : DLPak ( )
ScaleTexture textureconesysteme3 , 0.1 , 0.1
texturecone1_rock = LoadTexture ( Pak ( "1_rock.jpg" ) , 1 + 8 ) : DLPak ( )
texmurspatemaison0 = LoadTexture ( Pak ( "gothic3.bmp" ) , 1 + 8 ) : DLPak ( )
ScaleTexture texmurspatemaison0 , 0.1 , 0.1
texmurspatemaison1 = LoadTexture ( Pak ( "wall.jpg" ) , 1 + 8 ) : DLPak ( )
textoitpatemaison = LoadTexture ( Pak ( "shingle.bmp" ) , 1 + 8 ) : DLPak ( )
ScaleTexture textoitpatemaison , 0.1 , 0.1
texplancherpatemaison = LoadTexture ( Pak ( "WoodWorkShopEssai03.jpg" ) , 1 + 8 ) : DLPak ( )
ScaleTexture texplancherpatemaison , 0.1 , 0.1
texturehearse = LoadTexture ( Pak ( "rallycar.jpg" ) , 1 + 8 ) : DLPak ( )
texture1feupodechap = LoadTexture ( Pak ( "fire 2.png" ) , 1 + 2 + 8 ) : DLPak ( )
texture2feupodechap = LoadTexture ( Pak ( "fire 2.png" ) , 1 + 2 + 8 ) : DLPak ( )
textureplanete4 = LoadTexture ( Pak ( "1_rock.jpg" ) , 1 + 8 ) : DLPak ( )
ScaleTexture textureplanete4 , 0.1 , 0.1
brushplanete4 = LoadBrush ( Pak ( "1_rock.jpg" ) , 1 ) : DLPak ( )
texturezimeub = LoadTexture ( Pak ( "gothic3.bmp" ) , 1 + 8 ) : DLPak ( )
textureathmosplanete4 = LoadTexture ( Pak ( "cloudsbig.jpg" ) , 1 + 4 ) : DLPak ( )
textureathmosbisplanete4 = LoadTexture ( Pak ( "cloudsbig.jpg" ) , 1 + 2 ) : DLPak ( )
texturecuboite5 = LoadTexture ( Pak ( "MossyGround.BMP" ) , 1 + 8 )
ScaleTexture texturecuboite5 , 0.01 , 0.01 : DLPak ( )
texmossyground = LoadTexture ( Pak ( "MossyGround.BMP" ) , 1 + 8 ) : DLPak ( )
texsolarcenter6 = LoadTexture ( Pak ( "ssail.jpg" ) , 1 + 8 ) : DLPak ( )
texfighter2du6 = LoadTexture ( Pak ( "Craft1_0.bmp" ) , 1 + 8 ) : DLPak ( )
texturerocher = LoadTexture ( Pak ( "1_rock.jpg" ) , 1 + 8 ) : DLPak ( )
textureplayer0int_rock = LoadTexture ( Pak ( "1_rock.jpg" ) , 1 + 8 ) : DLPak ( )
brushplayer0int_rock = LoadBrush ( Pak ( "1_rock.jpg" ) , 1 + 2 + 8 ) : DLPak ( )
BrushBlend brushplayer0int_rock , 1
BrushAlpha brushplayer0int_rock , 0.1
SetFont app850fon
SetBuffer TextureBuffer ( texUSERpanneau7 )
Color 75 , 65 , 55
Text 0 ,  10 , "Placez des images  .BMP"
Text 0 ,  20 , "dans       ' user  images '"
Text 0 , 100 , "(EFF. ARR. au lancement)"
SetBuffer TextureBuffer ( textableauS3sysOK )
ClsColor 160 , 150 , 50
Color 205 , 75 , 195
Cls
Text 0 ,  0 , "Tous"
Text 0 , 10 , "Systems"
Text 0 , 20 , "Activ?s"
SetBuffer TextureBuffer ( texturemessS2S3 )
ClsColor 100 , 50 , 50
Color 225 , 175 , 175
Cls
Text 0 ,  0 , "Cherchez le Fighter"
Text 0 , 10 , "Vitesse ? ~ 350 ~"
Text 0 , 20 , "Appuyez sur 'F'"
Text 0 , 30 , "Poursuivez-le"
Text 0 , 80 , "NOTE : prendre le"
Text 0 , 90 , "code dans l'Astrig"
SetBuffer TextureBuffer ( texcubespac )
ClsColor 200 , 50 , 50
Color 225 , 75 , 175
Cls
Text 0 ,  0 , "System 2"
Text 0 ,  9 , "Activ? ."
SetBuffer TextureBuffer ( texBMSvuSys0 )
ClsColor 200 , 50 , 50
Color 125 , 175 , 175
Cls
Text 0 ,  0 , "System 1"
Text 0 ,  9 , "Activ? ."
SetBuffer TextureBuffer ( texcube25BMS )
ClsColor 150 , 50 , 50
Color 125 , 150 , 200
Cls
Text 0 ,  0 , "BOUTON"
Text 0 ,  9 , "MILIEU"
Text 0 , 18 , "SOURIS"
SetBuffer TextureBuffer ( texcodecube25 )
ClsColor 200 , 50 , 50
Color 125 , 175 , 175
Cls
Text 0 ,  0 , "CODE"
Text 0 ,  9 , "IS" + Chr$ ( RANDcarcodpaliss ( 0 ) ) + Chr$ ( RANDcarcodpaliss ( 1 ) )
Text 0 , 18 , Chr$ ( RANDcarcodpaliss ( 2 ) ) + Chr$ ( RANDcarcodpaliss ( 3 ) ) + Chr$ ( RANDcarcodpaliss ( 4 ) ) + Chr$ ( RANDcarcodpaliss ( 5 ) )
SetBuffer TextureBuffer ( texturepanneaucenter6 )
ClsColor 60 , 80 , 100 : Color 200 , 120 , 80
Cls
Text 0 ,  10 , "-------------------------------"
Text 0 ,  20 , "- Sph?re ?toil?e -"
Text 0 ,  30 , "-------------------------------"
Text 0 ,  60 , " 1 bon merci a DK "
Text 0 ,  80 , " et ? BS , depuis "
Text 0 , 100 , " le forum BlitzFR "
SetBuffer TextureBuffer ( texturepanneau2center6 )
ClsColor 60 , 80 , 100 : Color 200 , 120 , 80
Cls
Text 0 ,   0 , "-------------------------------"
Text 0 ,  20 , "S'aurions nous su"
Text 0 ,  40 , "Eblouir, grandir,"
Text 0 ,  60 , "En signant a Dieu"
Text 0 ,  80 , "Qu'aimer emouvoir"
Text 0 , 100 , "-------------------------------"
SetBuffer TextureBuffer ( texturepanneau3defiltex )
ClsColor 150 , 200 , 50
Cls
SetBuffer TextureBuffer ( texturepanneau3center6 )
ClsColor 0 , 0 , 0
Cls
Color 55 , 155 , 245
Rect 5 , 51 , 118 , 5 , 1
Color 245 , 125 , 175
Rect 8 , 52 , 112 , 3 , 1
SetBuffer TextureBuffer ( texturepanneau4defilcah )
ClsColor 120 , 100 , 50
Cls
SetBuffer TextureBuffer ( texturepanneau4center6 )
ClsColor 140 , 160 , 80
Cls
Color 55 , 155 , 245
Rect 6 , 51 , 116 , 12 , 1
Color 245 , 125 , 175
Rect 7 , 52 , 114 , 10 , 1
SetBuffer TextureBuffer ( texturepaneaupaliss )
ClsColor 0 , 0 , 0 : Color 255 , 255 , 255
Cls
Text  0 , -4 , "++++"
Text  0 ,  4 , "++++"
Text  0 , 12 , "++++"
Text  0 , 20 , "+++"
ScaleTexture texturepaneaupaliss , 0.002 , 0.002
TextureBlend texturepaneaupaliss , 1
brushpaneaupaliss = CreateBrush ( 150 , 150 , 120 )
BrushBlend brushpaneaupaliss , 1
BrushAlpha brushpaneaupaliss , 0.1
BrushTexture brushpaneaupaliss , texturepaneaupaliss
SetBuffer TextureBuffer ( textureplaquepaliss )
ClsColor 60 , 80 , 100 : Color 200 , 120 , 80
Cls
Rect 2 , 2 , 123 , 123 , 0
Text 0 ,  10 , " -------------- "
Text 0 ,  20 , "  CODE  SECRET  "
Text 0 ,  30 , " -------------- "
Text 0 ,  60 , "      F11 :     "
Text 0 ,  90 , " D?verrouillage "
SetBuffer TextureBuffer ( textureplacodepaliss )
ClsColor 200 , 120 , 80
Color 60 , 80 , 100
Cls
Rect  2 ,  2 , 123 , 123 , 0
Text  0 , 10 , " Entrez le code "
Text  0 , 30 , " -------------- "
SetBuffer TextureBuffer ( textureASKI )
ClsColor 250, 150, 80
Color 50, 80, 100
Cls
For codetexASKI = 0 To 255
Text 50 + 50 * ( codetexASKI Mod 16 ) , 100 + 50 * Int ( codetexASKI / 16 ) , codetexASKI + " " + Chr$ ( codetexASKI ) + ",  "
Next
SetBuffer TextureBuffer ( textableaustation )
ClsColor 200 , 120 , 80
Color 60 , 80 , 100
Cls
Rect  2 ,  2 , 123 , 123 , 0
Text  0 ,  10 , " --------------"
Text  0 ,  30 , " Bumps  Records"
Text  0 ,  50 , " -------------- "
If FileType ( "userdata" ) = 1 And ( FileType ( "Galaxang32.exe" ) = 1 ) Then
filerecords = ReadFile ( "userdata" )
SeekFile ( filerecords , 24 + 4 + 4 )
USERSnbmaxbumps = ReadInt ( filerecords )
CloseFile filerecords
EndIf
Text  0 ,  70 , " Max : " + USERSnbmaxbumps
Text  0 ,  90 , " You : "
Text  0 , 110 , " -------------- "
SetBuffer TextureBuffer ( textableaustatir )
ClsColor 200 , 20 , 80
Color 60 , 80 , 100
Cls
Rect  2 ,   2 , 123 , 123 , 0
Text  0 ,  10 , " T:deb  BDS:fin "
Text  0 ,  30 , " Survie Records "
Text  0 ,  50 , " ------------------------ "
If FileType ( "userdata" ) = 1 And ( FileType ( "Galaxang32.exe" ) = 1 ) Then
filerecords = ReadFile ( "userdata" )
SeekFile ( filerecords , 24 + 4 + 4 + 4 )
USERSdureemaxdetir = ReadInt ( filerecords )
CloseFile filerecords
EndIf
Text  0 ,  70 , "  Max : " + USERSdureemaxdetir + " s"
Text  0 ,  90 , "  You : "
Text  0 , 110 , " ------------------------ "
SetFont GabriolaTTF
SetBuffer TextureBuffer ( texturemessage )
ClsColor 200 , 50 , 80
Cls
Color 50 , 250 , 250
If FileType ( "userdata" ) = 1 And ( FileType ( "Galaxang32.exe" ) = 1 ) Then
filerecords = ReadFile ( "userdata" )
SeekFile ( filerecords , 24 + 16 + 4 + ( nbmaxastrigjeu - 1 ) * 3 * 4 )
nbmaxcaracmess = ReadInt ( filerecords )
For numcaracmess = 1 To nbmaxcaracmess Text numcaracmess * 10 ,  50 , Chr$ ( ReadInt ( filerecords ) ) Next
nbmaxcaracmess = ReadInt ( filerecords )
For numcaracmess = 1 To nbmaxcaracmess Text numcaracmess * 10 , 100 , Chr$ ( ReadInt ( filerecords ) ) Next
nbmaxcaracmess = ReadInt ( filerecords )
For numcaracmess = 1 To nbmaxcaracmess Text numcaracmess * 10 , 150 , Chr$ ( ReadInt ( filerecords ) ) Next
CloseFile filerecords
EndIf
SetBuffer TextureBuffer ( texturemessedit )
Color 125 , 125 , 125
Rect 170 , 220 , 80 , 30 , 0
If FileType ( "Galaxang32.exe" ) = 1 Then Text 175 , 215 , "F11 : Edit"
TextureBlend texturemessedit , 3
.CREEstructures
AppTitle "Init structures"
PlaySound ( CrissMingo )
SetBuffer BackBuffer ( )
Print "Construit structures"
Viewport 0 , 0 ,  GW , GH
ClsColor 10, 50, 30
Color 150, 250, 100
Cls
readfilerecords ( filerecords )
Print "- 01 - entityXpivotTEMPmemo = " + entityXpivotTEMPmemo
Print "- 02 - entityYpivotTEMPmemo = " + entityYpivotTEMPmemo
Print "- 03 - entityZpivotTEMPmemo = " + entityZpivotTEMPmemo
Print "- 04 - entityXplayer0memo = " + entityXplayer0memo
Print "- 05 - entityYplayer0memo = " + entityYplayer0memo
Print "- 06 - entityZplayer0memo = " + entityZplayer0memo
Print "- 07 - infospac = " + infospac
Print "- 08 - numsysOKactu = " + numsysOKactu
Print "- 09 - USERSnbmaxbumps = " + USERSnbmaxbumps
Print "- 10 - USERSdureemaxdetir = " + USERSdureemaxdetir
Print "- 11 - nbmaxastrigjeuTMP = " + nbmaxastrigjeuTMP
If nbmaxastrigjeuTMP > 1 Then
For nastrigj = 1 To nbmaxastrigjeuTMP - 1
Print "- " + 11 + nastrigj +     ( nastrigj - 1 ) * 3 + " - coorXastrigjeu ( nastrigj ) = " + coorXastrigjeu ( nastrigj )
Print "- " + 11 + nastrigj + 1 + ( nastrigj - 1 ) * 3 + " - coorYastrigjeu ( nastrigj ) = " + coorYastrigjeu ( nastrigj )
Print "- " + 11 + nastrigj + 2 + ( nastrigj - 1 ) * 3 + " - coorZastrigjeu ( nastrigj ) = " + coorZastrigjeu ( nastrigj )
Next
EndIf
Print "- 23 - Len ( linemess1$ ) = " + Len ( linemess1$ )
Print "- 24 - linemess1$ = " + linemess1$
Print "- " + ( 25 + Len ( linemess1$ ) ) + " - Len ( linemess2$ ) = " + Len ( linemess2$ )
Print "- " + ( 26 + Len ( linemess1$ ) ) + " - linemess2$ = " + linemess2$
Print "- " + ( 27 + Len ( linemess1$ ) + Len ( linemess2$ ) ) + " - Len ( linemess3$ ) = " + Len ( linemess3$ )
Print "- " + ( 28 + Len ( linemess1$ ) + Len ( linemess2$ ) ) + " - linemess3$ = " + linemess3$
pivsysrepX = 0
pivsysrepY = 0
pivsysrepZ = 0
pivotsysrep ( pivsysrepX , pivsysrepY , pivsysrepZ ) = CreatePivot ( )
pivotsysteme ( 0 ) = CreatePivot ( pivotsysrep ( pivsysrepX , pivsysrepY , pivsysrepZ ) )
pivotsysteme ( 1 ) = CreatePivot ( pivotsysrep ( pivsysrepX , pivsysrepY , pivsysrepZ ) )
pivotsysteme ( 2 ) = CreatePivot ( pivotsysrep ( pivsysrepX , pivsysrepY , pivsysrepZ ) )
pivotsysteme ( 3 ) = CreatePivot ( pivotsysrep ( pivsysrepX , pivsysrepY , pivsysrepZ ) )
pivotsysteme ( 4 ) = CreatePivot ( pivotsysrep ( pivsysrepX , pivsysrepY , pivsysrepZ ) )
pivotsysteme ( 5 ) = CreatePivot ( pivotsysrep ( pivsysrepX , pivsysrepY , pivsysrepZ ) )
pivotsysteme ( 6 ) = CreatePivot ( pivotsysrep ( pivsysrepX , pivsysrepY , pivsysrepZ ) )
pivotsysteme ( 7 ) = CreatePivot ( pivotsysrep ( pivsysrepX , pivsysrepY , pivsysrepZ ) )
pivotsysteme ( 8 ) = CreatePivot ( pivotsysrep ( pivsysrepX , pivsysrepY , pivsysrepZ ) )
pivotsysteme ( 9 ) = CreatePivot ( pivotsysrep ( pivsysrepX , pivsysrepY , pivsysrepZ ) )
pivot222 = CreatePivot ( ) : PositionEntity pivot222 , 0 , 16000 , 0
soleil222 = CreateSphere ( 16 , pivot222 )
ScaleMesh soleil222 , 2 , 2 , 2
EntityPickMode soleil222 , 1
EntityFX soleil222 , 1
EntityAutoFade soleil222 , 15000 , 35000
EntityColor soleil222 , 250 , 250 , 50
EntityTexture soleil222 , texturesoleila , 0 , 0
EntityTexture soleil222 , texturesoleilb , 0 , 1
pivotTEMP = CreatePivot ( pivotsysrep ( pivsysrepX , pivsysrepY , pivsysrepZ ) )
NameEntity pivotTEMP , "pivotemp"
For numx = 0 To 2
For numy = 0 To 2
For numz = 0 To 2
pivotespace ( numx , numy , numz ) = CopyEntity ( pivot222 , pivotTEMP )
pivotrondeciel = pivotespace ( numx , numy , numz )
CREErondeciels ( pivotrondeciel )
pivotespace ( numx , numy , numz ) = pivotrondeciel
PositionEntity pivotespace ( numx , numy , numz ) , ( numx - 1 ) * 20000 , ( numy - 1 ) * 20000 , ( numz - 1 ) * 20000
Next
Next
Next
If numsysdef$ = "ch?paskifofer" Then
Select numsysdef$
Case "0"
PositionEntity pivotTEMP ,       0 ,       0 ,        0
Case "1"
PositionEntity pivotTEMP ,       0 ,       0 ,   -20000
Case "2"
PositionEntity pivotTEMP , -100000 , -100000 ,  -100000
Case "3"
PositionEntity pivotTEMP ,  100000 , -100000 ,  -100000
Case "4"
PositionEntity pivotTEMP ,  110000 ,  110000 ,   110000
Case "5"
PositionEntity pivotTEMP , -100000 ,  100000 ,   100000
Case "6"
PositionEntity pivotTEMP , -100000 ,  100000 ,  -100000
Default
PositionEntity pivotTEMP ,       0 ,       0 ,        0
End Select
Else
PositionEntity pivotTEMP , entityXpivotTEMPmemo , entityYpivotTEMPmemo , entityZpivotTEMPmemo
EndIf
.CNSTplayer17bb
Print "Player0"
player0 = CreateSphere ( 16 , pivotTEMP )
ScaleMesh player0 , 7 , 20 , 7
EntityRadius player0 , 7 , 20
EntityType player0 , type_ellipsoide
EntityPickMode player0 , 1
EntityColor player0 , 160 , 160 , 60
EntityTexture player0 , textureplayer0int_rock
ScaleEntity player0 , 1 , 1 , 1
PositionEntity player0 , entityXplayer0memo , entityYplayer0memo , entityZplayer0memo
player0int = CreateSphere ( 16 , player0 )
PaintEntity player0int , brushplayer0int_rock
ScaleEntity player0int , -7 , -20 , -7
microphone = CreateListener ( player0 , 1 , 1 , 0.5 )
channsoundplayer0 = PlaySound ( soundplayer0 )
PauseChannel channsoundplayer0
CamDX = 0 : CamDY = 5 : CamDZ = -3
pivotplayer0 = CreatePivot ( player0 )
PositionEntity pivotplayer0 , CamDX , CamDY , CamDZ
camera = CreateCamera ( pivotplayer0 )
CameraFogMode camera , 1
CameraFogRange camera , 20000 , 80000
CameraRange camera , 1 , 80000
CameraViewport camera , 0 , 0 , GW , 3 * ( GH / 4 )
PositionEntity camera , 0 , 0 , 0
Cammode = 0
baguette = CreateCone ( 16 , 1 , pivotplayer0 )
ScaleMesh baguette , 1 , 5 , 1
RotateMesh baguette , 70 , 20 , 0
PositionEntity baguette , 2 , -2 , 50
indicdir = CreateCone ( 16 , 1 , baguette )
ScaleMesh indicdir , 1 , 3 , 1
RotateMesh indicdir , 90 , 0 , 0
PositionEntity indicdir , -2 , 2 , 5
HideEntity indicdir
pivotREF = pivotTEMP
.Fonctionspluie
Type Splashtex
Field x#,y#
Field sx#,sy#
Field ox#,oy#
Field c#
End Type
Type Splash3D
Field sprite
Field x#,y#,z#
Field texture
Field frame#,frames
Field active
End Type
Type Rain3D
Field sprite
Field x#,y#,z#
Field sx#,sy#,sz#
Field active
End Type
Type Ring3D
Field sprite
Field x#,y#,z#
Field texture
Field frame#,frames
Field active
End Type
Global s.splashtex
Global s3d.splash3d
Global r3d.rain3d
Global t3d.ring3d
Global gbuff , rtex , drops , size , frames
Global envr = 76
Global envg = 100
Global envb = 110
Global RainSpeed# = 3.0
splashframes = 20
splashtex=CreateSplashTexture(500,64,splashframes)
CreateSplashes3D(100,splashtex,splashframes)
ringframes = 20
ringtex=CreateRingTexture(50,128,ringframes)
CreateRings3D(50,ringtex,ringframes)
raintex=CreateRainTexture(50,128)
CreateRain3D(20,raintex)
Global gameFPS   = 30
Global framePeriod  = 1000 / gameFPS
Global frameTime  = MilliSecs () - framePeriod
Global frameTween#  = 1.0
Function UpdatePluie()
UpdateSplashes3D(camera,5)
UpdateRings3D()
UpdateRain3D(camera,20)
End Function
Function UpdateSplashes3D(camera,rainpercent)
For s3d.splash3d = Each splash3d
If s3d\active
s3d\frame = s3d\frame + 0.75
If s3d\frame < s3d\frames-1
EntityTexture s3d\sprite,s3d\texture,s3d\frame
Else
s3d\active = False
HideEntity s3d\sprite
End If
Else
If Rand(100)<rainpercent
If CameraPick(camera,Rand(GraphicsWidth()),Rand(GraphicsHeight()))
If PickedNY()>0.25
s3d\x = PickedX()
s3d\y = PickedY()
s3d\z = PickedZ()
s3d\frame = 0
s3d\active = True
tmp# = Rnd(2,4)
ScaleSprite    s3d\sprite,Rnd(2,4)*PickedNY(),Rnd(2,4)*PickedNY()
PositionEntity s3d\sprite,s3d\x,s3d\y,s3d\z
ShowEntity    s3d\sprite
If PickedNY()>0.75
SpawnRing3D(PickedX(),PickedY(),PickedZ(),PickedNX(),PickedNY(),PickedNZ())
End If
End If
End If
End If
End If
Next
End Function
Function SpawnRing3D(x#,y#,z#,nx#,ny#,nz#)
For t3d.ring3d = Each ring3d
If t3d\active = False
t3d\x = x#
t3d\y = y#+0.05
t3d\z = z#
t3d\frame = 0
t3d\active = True
tmp# = Rnd(4,7)
ScaleSprite    t3d\sprite,tmp*ny,tmp*ny
PositionEntity t3d\sprite,t3d\x,t3d\y,t3d\z
ShowEntity    t3d\sprite
AlignToVector  t3d\sprite,nx,ny,nz,0
Exit
End If
Next
End Function
Function UpdateRings3D()
For t3d.ring3d = Each ring3d
If t3d\active
t3d\frame = t3d\frame + 0.5
If t3d\frame < t3d\frames-1
EntityTexture t3d\sprite,t3d\texture,t3d\frame
Else
t3d\active = False
HideEntity t3d\sprite
End If
End If
Next
End Function
Function UpdateRain3D(camera,maxdist#)
For r3d.rain3d = Each rain3d
If r3d\active
r3d\x = r3d\x + r3d\sx
r3d\y = r3d\y + r3d\sy
r3d\z = r3d\z + r3d\sz
PositionEntity r3d\sprite,r3d\x,r3d\y,r3d\z
If EntityY(r3d\sprite)<-20
r3d\active = False
HideEntity r3d\sprite
End If
Else
d#  = Rnd(5,maxdist)
d2# = Rnd(-50,50)
r3d\x = EntityX(camera,True) - (Sin(EntityYaw(camera,True)+d2)*d)
r3d\y = EntityY(camera,True) + d
r3d\z = EntityZ(camera,True) + (Cos(EntityYaw(camera,True)+d2)*d)
r3d\sx = Rnd(-0.1,0.1)
r3d\sy = Rnd(-RainSpeed,-RainSpeed*0.75)
r3d\sz = Rnd(-0.1,0.1)
r3d\active = True
ShowEntity r3d\sprite
End If
Next
End Function
Function CreateSplashes3D(amou
