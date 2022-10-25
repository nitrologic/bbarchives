; ID: 3024
; Author: Prym
; Date: 2013-02-03 09:18:39
; Title: Galaxang
; Description: Special cosmos

.desbuts





;------------------------------------------------------------------------------------





; 	-	Votre message en trois lignes , probleme lecture ou ecriture userdata . -	;





;------------------------------------------------------------------------------------





;------------------------------------------------222-------------------------------- 
;Collisions type_ellipsoide , type_poly , method , response 
;----------------------------------------------------------------------------------- 
;method - méthode de détection de collision. 
;1 : Collisions d'ellipsoide sur ellipsoide 
;2 : Collisions d'ellipsoide sur polygone 
;3 : Collisions d'ellipsoide sur boîte 
;response - ce que l'entité source fait quand une collision a lieu. 
;1: arrêt 
;2: slide1 - la collision glisse complètement 
;3: slide2 - empêche les entités de glisser vers le bas 
;----------------------------------------------------------------------------------- 





;AppTitle "Inits variables" ; 		marche pas : temps trop court 





;	;	;	;	;	;	; Messages - Infos dev	;	;	;	;	;	;	;	;	;	; 

Const texteinfopb1$ = " " ; GLOB : coorREF > 400000" ; SYST-4  -  cols & pivs orbi4 ." 
Const texteinfopb2$ = " " ; SYST-4 : pas de collis " 
Const texteinfopb3$ = " " ; SYST-2 : bordel du mirror !" 
Const texteinfopb4$ = " " ; SYST-5 : et les arbres " ; Global  -  datas user in txt ?" 
Const texteinfopb5$ = " " ; CREATE : numpivpar CreatePivot" 





;FlushKeys 





; Include "b3dfile.bb" 	;	;	;	;	;	;	charge décharge B3D files 




;Les fonctions de TerraPak ( Blitzbasic toolbox ) 

Include "BonesBis.bb" 	;	;	;	;	;	;	Bones.bb modifiée	;	;	;	;	; 

;AppTitle "Codagimage ?" 











; types & variables pour terraformS4 
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

;Include "funcsterraf32.bb"










;	;	;	;	;	;	Début du prog GALAXANG		;	;	;	;	;	;	;	; 










.InitVars ; Init vars 

Global numimage% = 0 , nbmaxscans% = 0 , dossiercourant$ = "" ; , copyaide = 0 

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

;Global arialmsg$ = "" , Gabriolamsg$ = "" , vgafixmsg$ = "" ; , monbaitimsg$ = "" 
;Global georgiaTTF , monbaitiTTF ; , andlsoTTF , AncPol 
Global arialTTF = 0 , GabriolaTTF = 0 , app850fon = 0 
;Global vgafixFON = 0 , verdanabTTF = 0 ; BlitzFON = 0 ; , fonte80gif 
Global AncTailleFont% = FontHeight ( ) ; , chargefont = 0 
Global timerglobal = CreateTimer ( 100 ) , timerpanneaublitz 
Global nbcarstr1 = 0 , nbcarstr2 = 0 , nbcarstr3 = 0 , numread = 0 

Global campickedplayer0 = 0 ; , campicked = 0 
Global pickedplayer0 , player0int , texturecone1_rock ; , GETCOLmouse = 0 
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
Global choixobjet$ = "" , numsysdef$ = "0" ; creation = 0 
Global pivsysrepX = 0 , pivsysrepY = 0 , pivsysrepZ = 0 
Global usersysdir$ = "" , dirdusys = 0 

Global pivotastrig , astrig ;, astrigjeu 
Global indicesurface% = 1 , RANDcouleurastrig = 1 
Global x0% = 0 , y0% = 0 , z0% = 0 
Global x1% = 0 , y1% = 0 , z1% = 0 
Global x2% = 0 , y2% = 0 , z2% = 0 
Global v0 , v1 , v2 , triangle , astrig_ext , couleurextastrig 
Global cube25 , texcodecube25 , frame% = 1 , cube25jeu 
Global nbmaxastrigjeu% = 5 , nbmaxastrigjeuTMP% = 1 ;, ntrigjeu% = 0 
Global astrigjeupicked = False , numastrigjeu% = 0 ; pivotastrigjeu 
Global porti46 , porti47 , couleurportis 
Global surface46 , surface47 , porti46ext , porti47ext 
Global pivotturn46 , pivotporti46 , pivotturn47 , pivotporti47 
Global seq46 , seq47 , seqastrig 
Global pivotporti46jeu , pivotporti47jeu 
Global nastrigj = 0 

Global deplacx# = 0.0 , deplacy# = 0.0 , deplacz# = 0.0 , choixdeplacz% = 0 
Global camyaw# = 0.0 , campitch# = 0.0 , camroll# = 0.0 
Global deplajoyx# = 0.0 , deplajoyy# = 0.0 , JoystickZDir% = 0 
Global mvx% = 0 , mvy% = 0 , mvz% = 0 , vitesse% = 4 ; 153 ; 3 au début normalement 
Global inertx% = 0 , inerty% = 0 , inertz% = 0 
Global numfich_captecran% = 0 , timerinfocapture% = 100 , signaltimer = False 
Global findujeu = False 

Global coorXdanscube% = 0 , coorYdanscube% = 0 , coorZdanscube% = 0 
Global coorXpivotTEMP% = 0 , coorYpivotTEMP% = 0 , coorZpivotTEMP% = 0 
Global coorXcam% = 0 , coorYcam% = 0 , coorZcam% = 0 
Global curX# , curY# , curZ# , camspeed = 10 ; 16 
Global CamDX = 0 , CamDY = 0 , CamDZ = 0 
Global Cammode ; , CAM_CONDUCTEUR , CAM_POURSUITE 
;Global rotXorbplanete4 = 1 , rotYorbplanete4 = 1,  rotZorbplanete4 = 1 

Global numlieusuivi% = 0 , num% = 0 , numsysOKactu% = 0 
Global numcarac% = 0 
Global repcapture$ = "screen captures" 
Global repimage$ = "user images" 
Global posxsouris% = 0 , posysouris% = 0 , getcolored = 0 
;Global ancMouseXmenu = 0 , ancMouseYmenu = 0 
;Global pointermenu , backpointermenu ; , sortieplais = True 
;Global ancmouseX = 0 , ancmouseY = 0 

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
Global tablomur , texturemessage , texturemessedit , texturemessS2S3 ; , texturetablomur  
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
Global textureblitzlogo , texturedospanneau2code ; , texturefface 
Global xpostexdefil# = 1 , ypostexdefil# = 1 
Global xpostapiroul# = 1 , ypostapiroul# = 1 
Global stringcode$ = " " , numligstringcode% = 1 , numtexturecode% = 0 
;Global texturecode000galaxiang14 , texturecode001start14 
;Global texturecode002InitCoor14 , texturecode05CNSTsysteme1  
;Global texturecode021CNSTsoleilastre14 , texturecode022CNSTdivers14 
;Global texturecode03CNSTastrig14 
;Global texturecode06INITcubespaces14 , texturecode01CNSTcamera14 
Global pivopanotelepoS2 , panotelepoS2 , texturepanotelepoS2 , cubetelepoS2 
Global pivotpanneaucode , panneaucode 					   ; mirror (en chantier) 
Global pivotpanneau2code , panneau2code , dospanneau2code  ; blitzlogo
Global pivotpanneau3code , panneau3code , dospanneau3code  ; images
;Global pivotpanneau4code , panneau4code 
;Global pivotpanneau5code , panneau5code 
Global pivotpanneau6code , panneau6code , dospanneau6code  ; images
Global pivotpanneau7code , panneau7code , dospanneau7code  ; images
Global pivotpanneau8code , panneau8code , dospanneau8code  ; images
;Global texturecryptexCAH1 , texturedecryptexCAH1  
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
Global nbmaxIMApanoCOP% = 1 , numIMApanoCOP% = 0 	; 	nbmaxIMApanoCOP% = 42 max 
Global fichIMApanoCOP$ = "" , TEXpanoCOP ; , frameTEXpanoCOP% = 0 ; , unimagepano 

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
Global xpostexathmo# = 0.0 ; , ypostexathmo# = 0.0 
Global seriedeclair , temposeriedeclair , tempoeclair ; , lumipla4 
Global collizimeubtous = False , numzimeub = 0 , totalzimeub = 35 , zimturny4 = 1 
Global ancrollplayer0orbi4 = 0 , axorbiplanete4 
Global ancXplayer0orbi4# = 0.0 , ancYplayer0orbi4# = 0.0 , ancZplayer0orbi4# = 0.0
;Global ancXplayer0orbi4 = 0 , ancYplayer0orbi4 = 0 , ancZplayer0orbi4# = 0 
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
Global xposdefiltexS6# = 0.0 , yposdefiltexS6# = 0.0 ; -0.1 pour demar en negat ?
Global codexte , lignecodexte1$ = "" , lignecodexte2$ = "" , sekfilcodexte% = 0 ; , longfichcodexte% = 0 
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
Dim pivotsysteme ( 10 ) 		; nbmaxsysteme% = 10 
Dim photopivotsyst ( 10 ) 
Dim numsysOK ( 10 ) 
Dim pivotastrigjeu ( 5 ) 		; nbmaxastrigjeu% = 5 
;Dim cube25jeu ( 5 )			; nbmaxastrigjeu% = 5 
Dim zimeub ( 35 ) 
Dim RANDcarcodpaliss ( 6 ) 
Dim pivotbonom ( 4 ) 
Dim pivotplusmaison ( 25 )
Dim ciblebonom3 ( 4 )
Dim pivuser ( 5 , 10 ) 	; numpiv , numsys% 
Dim lumuser ( 5 , 10 ) 	; numpiv 
Dim meshuser ( 5 , 10 ) ; nummesh 
Dim nbmaxpivsyst ( 10 ) ; nb maxi de pivot par systeme
;Dim GE400 ( 20 , 20 , 20 ) 
Dim pivotsysrep ( 20 , 20 , 20 ) 
Dim indicefx ( 7 ) 
;Dim parentduplayer0 ( 10 ) 

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





; -------------------- Création serveur ---------------------- 
; 
; rien , jamais dans le même programme que Création client !!! 
; 
; -------------------- Création serveur ---------------------- 





AppTitle "Config Ecran"

; Include "00-1start17.bb" ; Test graphique 

;If GfxMode3DExists ( 1920 , 1080 , 32 ) Then 
;	GW = 1920 : GH = 1080 : GD = 32 
;ElseIf GfxMode3DExists ( 1280 , 1024 , 32 ) Then 
;	GW = 1280 : GH = 1024 : GD = 32 
;ElseIf GfxMode3DExists ( 1024 , 600 , 32 ) Then 
;	GW = 1024 : GH = 600 : GD = 32 
;Else 
	SetGfx ( ) ;( GW , GH , GD , GM ) 	;	Merci Agore	(samples blitzbasic.com) ! 
;EndIf 	





Graphics3D GW% , GH% , GD% , GM% 

GB# = GraphicsBuffer ( ) 





capslevel = GfxDriverCaps3D ( )
If capslevel% < 110 Then 
	Write "Votre carte graphique ne supporte pas les maps d'environnement cubique. Effets innattendus." 
	If capslevel% < 100 RuntimeError "Votre carte graphique ne supporte pas les commandes BlitzBasic." 
	WaitKey ( ) 
EndIf 





; Fonctions de terraformation ( Blitzbasic toolbox : Spherical Landscapes )

Include "funcsterraf32.bb"

;Include "Fonctions pluie.bb" 





.InitCoors ; Init coors 

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





;Print Bin$ ( "17" ) 
;Print 
;Print "OK ?" 
;WaitKey ( ) 





AppTitle "Demarrage" 





dossiercourant$ = CurrentDir$ ( ) 

paramCMD$ = CommandLine$ ( )
paramCMDnb% = CommandLine ( ) 
paramCMDleft$ = Left$ ( paramCMD$ , 3 ) 





Viewport 0 , 0 , GW , GH 
;ClsColor 10, 10, 10 
;Color 100, 100, 100
;Cls 






; Include codagimage26.bb ( au kaou ) 
;If Right$ ( paramCMD$ , 3 ) = "cod" And FileType ( "codagimage26.bb" ) = 1 Then 
;	Cls 
;	Print 
;	Print "Codage : appuyez sur c." 
;	Print 
;	WaitKey ( ) 
;	If KeyHit ( 46 ) Then 
;		Include "codagimage26.bb" 
;	EndIf 
;	RuntimeError nbmaxscans + " images ARGBcryptées !" ; "C'est fini !" : Print 
;
;EndIf 





; Charge image parapry
;sketchapy = LoadImage ( "SNC00005-pencil07.jpg" ) 	;	seulement pour Galaxang > 10 mo





; Charge son Windows ; " ) Windows - Contrôle de compte d'utilisateur.wav
CrissMingo = LoadSound ( "C:\Windows\Media\Windows User Account Control.wav" ) ; On charge le bip depuis dossier Windows 





; Charge polices 

arialTTF = LoadFont ( "C:\Windows\Fonts\Arial\arial.ttf" , 8 * Int ( rapGW1920# + rapGH1080# ) , 0 , 0 , 0 ) ; 8 * Int ( rapGW1920# + rapGH1080# ) , 0 , 0 , 0 ) 
If arialTTF = 0 Then Print " Manque arial.ttf dans C:\Windows\Fonts\Arial\ ==> défaut formatage texte" : WaitKey ( ) : Print 

GabriolaTTF = LoadFont ( "C:\Windows\Fonts\Gabriola.ttf" , 24 * Int ( rapGW1920# + rapGH1080# ) , 0 , 0 , 0 ) 
If GabriolaTTF = 0 Then Print " Manque Gabriola.ttf dans C:\Windows\Fonts\ ==> défaut formatage texte" : WaitKey ( ) : Print 

;If FileType ( "Blitz.fon" ) Then 
;	CopyFile "Blitz.fon" , "C:\Windows\Fonts\Blitz.fon" 
;	DeleteFile "Blitz.fon" 
;EndIf 
;BlitzFON = LoadFont ( "C:\Windows\Fonts\Blitz.fon" , 10 * Int ( rapGW1920# + rapGH1080# ) , 0 , 0 , 0 ) 
;BlitzFON = LoadFont ( "Blitz.fon" , 10 * Int ( rapGW1920# + rapGH1080# ) , 0 , 0 , 0 )
;vgafixFON = LoadFont ( "C:\Windows\Fonts\vgafix.fon" , 10 * Int ( rapGW1920# + rapGH1080# ) , 1 , 0 , 0 ) 
;verdanabTTF = LoadFont ( "C:\Windows\Fonts\Verdana\verdanab.ttf" , 12 , 1 , 0 , 0 ) 
;comicbdTTF = LoadFont ( "C:\Windows\Fonts\Comic Sans MS\comicbd.ttf" , 12 , 1 , 0 , 0 ) 
app850fon = LoadFont ( "C:\Windows\Fonts\Terminal\app850.fon" , 12 , 1 , 0 , 0 ) 
If app850fon = 0 Then Print " Manque app850.fon dans C:\Windows\Fonts\Terminal\ ==> défaut formatage texte" : WaitKey ( ) : Print 
;Terminal\app850.fon
;comicbdTTF = LoadFont ( "C:\Windows\Fonts\vgasys.fon" , 12 , 1 , 0 , 0 ) 
;vgasys.fon




;andlsoTTF = LoadFont ( "C:\Windows\Fonts\andlso.ttf" , 12 * Int ( rapGW1920# + rapGH1080# ) , 0 , 0 , 0 )
;If andlsoTTF = 0 Then 
;	Print "andlso.ttf n'est pas dans C:\Windows\Fonts\"
;	PlaySound ( CrissMingo )
;	WaitKey ( )
;	andlsoTTF = LoadFont ( "andlso.ttf" , 12 * Int ( rapGW1920# + rapGH1080# ) , 0 , 0 , 0 )
;EndIf 

;monbaitiTTF = LoadFont ( "C:\Windows\Fonts\monbaiti.ttf" , 16 * Int ( rapGW1920# + rapGH1080# ) , 0 , 0 , 0 )
;If monbaitiTTF = 0 Then monbaitiTTF = LoadFont ( "monbaiti.ttf" , 16 * Int ( rapGW1920# + rapGH1080# ) , 0 , 0 , 0 )
;If monbaitiTTF = 0 Then monbaitiTTF = LoadFont ( Pak ( "monbaiti.ttf" ) , 16 * Int ( rapGW1920# + rapGH1080# ) , 0 , 0 , 0 )
;If monbaitiTTF = 0 Then monbaitimsg$ = " Manque monbaiti dans C:\Windows\Fonts\ : défaut formatage texte , chargez monbaiti.ttf "
;;monbaitiTTF = LoadFont ( Pak ( "monbaiti.ttf" ) , 8 * Int ( rapGW1920# + rapGH1080# ) , 0 , 0 , 0 ) : DLPak ( )
;;If chargefont = 0 Then monbaitimsg$ = " Manque monbaiti dans C:\Windows\Fonts\ : défaut formatage texte , chargez monbaiti "

;georgiaTTF = LoadFont ( "C:\Windows\Fonts\Georgia\georgia.ttf" , 12 * Int ( rapGW1920# + rapGH1080# ) , 0 , 0 , 0 )
;If georgiaTTF = 0 Then 
;	Print "georgia.ttf n'est pas dans C:\Windows\Fonts\Georgia\"
;	PlaySound ( CrissMingo )
;;	WaitKey ( )
;	georgiaTTF = LoadFont ( "georgia.ttf" , 12 * Int ( rapGW1920# + rapGH1080# ) , 0 , 0 , 0 )
;EndIf 





; Création codepaliss aléatoire 

SeedRnd ( MilliSecs ( ) ) 			 
RANDcarcodpaliss% ( 0 ) = Rand ( 48 , 90 ) 
RANDcarcodpaliss% ( 1 ) = Rand ( 48 , 90 ) 
RANDcarcodpaliss% ( 2 ) = Rand ( 48 , 90 ) 
RANDcarcodpaliss% ( 3 ) = Rand ( 48 , 90 ) 
RANDcarcodpaliss% ( 4 ) = Rand ( 48 , 90 ) 
RANDcarcodpaliss% ( 5 ) = Rand ( 48 , 90 ) 





; Init variables blocage systemes 

For numsys% = 0 To 9 numsysOK ( numsys% ) = False Next 





; Ecriture du fichier "userdata" 

If FileType ( "userdata" ) <> 1 And FileType ( "Galaxang32.exe" ) = 1 Then ; Or FileType ( "osa_Galaxang32.osa" ) = 1 ) Then 

	filerecords = WriteFile ( "userdata" ) 

			WriteInt filerecords , 0 ; 		EntityX ( pivotTEMP ) : par defaut system 0 
			WriteInt filerecords , 0 ; 		EntityY ( pivotTEMP ) 
			WriteInt filerecords , 0 ; 		EntityZ ( pivotTEMP ) 
			WriteInt filerecords , 1000 ; 	EntityX ( player0 ) 
			WriteInt filerecords , 1000 ; 	EntityY ( player0 ) 
			WriteInt filerecords , 1000 ; 	EntityZ ( player0 ) 
		WriteInt filerecords , 0 ; infospac ( = 0 au début ) 
		WriteInt filerecords , 0 ; numsysOKactu ( = 0 au début ) 
		WriteInt filerecords , 0 ; USERSnbmaxbumps ( = 0 au début ) 
		WriteInt filerecords , 0 ; USERSdureemaxdetir ( = 0 au début ) 
				WriteInt filerecords , 1 ; nbmaxastrigjeuTMP ( = 1 au début ) 
					WriteInt filerecords , 0 ; EntityX ( astrigjeu 1 ) : par defaut 0 
					WriteInt filerecords , 0 
					WriteInt filerecords , 0 
						WriteInt filerecords , 0 ; EntityX ( astrigjeu 2 ) : par defaut 0 
						WriteInt filerecords , 0 
						WriteInt filerecords , 0 
							WriteInt filerecords , 0 ; EntityX ( astrigjeu 3 ) : par defaut 0 
							WriteInt filerecords , 0 
							WriteInt filerecords , 0 	
								WriteInt filerecords , 0 ; EntityX ( astrigjeu 4 ) : par defaut 0 
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
	
;		WriteString filerecords , "pivotemp" ; = nameparentplayer0memo$ 

	CloseFile filerecords 

EndIf 





; ; Test si repcapture$ existe 
;repcapture$ = "screen captures" 
;If FileType ( repcapture$ ) <> 2 Then 
;	CreateDir "screen captures" 
;	repcapturexiste = 1 
;EndIf 
;readircurrentdir = ReadDir ( CurrentDir$ ( ) ) 
;Repeat 
;	repcapture$ = NextFile$ ( readircurrentdir ) 
;	If repcapture$ = "" Then Exit 
;	If FileType ( repcapture$ ) Then 
;		If repcapture$ = "screen captures" Then 
;			repcapturexiste = 1 
;			Exit 
;		EndIf
;	EndIf
;Forever
;If repcapturexiste = 0 Then 
;	CreateDir "screen captures" 
;	repcapturexiste = 1 
;EndIf

repcapture$ = "screen captures" 

If FileType ( "Galaxang32.exe" ) = 1 And FileType ( repcapture$ ) = 2 Then 

	numfich_captecran = 0 
	While FileType ( "screen captures\savimageG32" + RSet$ ( numfich_captecran , 2 ) + ".BMP" ) = 1 
		numfich_captecran = numfich_captecran + 1 
		Delay 100 
	Wend 
	numfich_captecran = numfich_captecran - 1 
	
EndIf 





; ; Test si repimage$ existe 
repimage$ = "user images" 
If FileType ( repimage$ ) <> 2 And FileType ( "Galaxang32.exe" ) = 1 Then CreateDir repimage$ 










AppTitle "Infos & Option"





; Infos diverses avant lancement 

SetBuffer FrontBuffer ( ) 

Viewport 0 , 0 , GW , GH 
ClsColor 80, 30, 150 
Color 200 , 70 , 90 
Cls 
			Text coor960W , coor600H , texteinfopb1$ ; , 1 , 0 ; coor1400W , coor600H 
			Text coor960W , coor620H , texteinfopb2$ ; , 1 , 0  
			Text coor960W , coor640H , texteinfopb3$ ; , 1 , 0  
			Text coor960W , coor660H , texteinfopb4$ ; , 1 , 0  
			Text coor960W , coor680H , texteinfopb5$ ; , 1 , 0  
Color 255 , 255 , 255 
;Text GW / 2 , coor440H , arialmsg$ ; , 1 , 0 
;Text GW / 2 , coor450H , Gabriolamsg$ ; , 1 , 0
;Text GW / 2 , coor460H , vgafixmsg$ ; , 1 , 0 
;Text GW / 2 , coor470H , Andybmsg$ , 1 , 0 
;Text GW / 2 , coor480H , BIDULmsg$ , 1 , 0 
Plot 0 , 1 
Plot GW - 1 , 1 
Plot 0 , GH - 1 
Plot GW - 1 , GH - 1 
;Text coor960W , coor500H , "Text 0 , 0         Text GW-1 , 0         Text 0 , GH-1         Text GW-1 , GH-1" , 1 , 1 
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

PakOutputDir "C:\windows\Temp" ; This can be set anywhere, a good example would be "C:\windows\Temp"
PakInit "Galaxang32.exe" , $0 , "TEMP" , $0 ; Open the pak
;LoadImage ( Pak ( "SNC00005-pencil07.jpg" , "photpivsys4.bmp" "mesh_defo.bmp" 
presentimage = LoadImage ( Pak ( "photpivsys4.bmp" ) ) : DLPak ( ) 
DrawImage presentimage , GW / 2 - ImageWidth ( presentimage ) / 2 , GH / 2 - ImageHeight ( presentimage ) / 2 

Color 255 , 255 , 255 
;Print 
;Print 
;Print "RECHERCHE DE SERVEURS ?    ( W ) " 

; Info si dutipok 
;Print 
;If paramCMDleft$ = "000" And FileType ( "DUTI.POK" ) Then Print "DUTI.POK détecté" 

; Image de présentation avec titre 
;ScaleImage sketchapy , 0.06 , 0.06 
;DrawImage sketchapy , 6 * ( GW / 8 ) , 5 * ( GH / 8 ) 
;Text 6 * ( GW / 8 ) + ( ImageWidth ( sketchapy ) / 2 ) , 7 * ( GH / 8 ) , "G A L A X A N G" , 1 , 1 
;Text 6 * ( GW / 8 ) + ( ImageWidth ( sketchapy ) / 2 ) , 7 * ( GH / 8 ) + 30 , "parapry" , 1 , 1 
Text 6 * ( GW / 8 ) , 7 * ( GH / 8 ) , "G A L A X A N G" , 1 , 1 
Text 6 * ( GW / 8 ) , 7 * ( GH / 8 ) + 30 , "parapry" , 1 , 1 
Locate GW / 8 , 7 * ( GH / 8 ) 
Print "Presse a quai" ;     ( W => réseau )" ) 
Print 
PlaySound ( CrissMingo ) 
FlushKeys 

WaitKey ( ) ; Attente touche : texes, fin, suite 










.InitBisTextures17bb ; Include "00-2InitBisTextures&Fonts17.bb" 










; ECH -> fin
	If KeyHit ( 1 ) Then End ; 	Echap --> fin 

; W -> test web ; -------------------- Création client ---------------------- 
;	If KeyHit ( 44 ) Then ; touche W --> init et essai web : Création client 
;		Include "CreeClientTCP.bb" 
;	EndIf 		; -------------------- Création client ---------------------- 

; Retour Arriere -> charge textures 
	If KeyHit ( 14 ) > 0 And FileType ( "Galaxang32.exe" ) = 1 Then ; touche Ret Arr 
		Include "initterpoktex32.bb" ; décodage images en supplément
	EndIf 
	
;	Else 
;		;PakBulkOverWrite = True ; Set this to True when in Development / False when Released
;		;Include "BonesBis.bb" 
;		PakOutputDir "C:\windows\Temp" ; This can be set anywhere, a good example would be "C:\windows\Temp"
;		;PakInit "Glaxyang222.exe", $3A4BDE97, "TMP", $D6143E88
;		;PakInit "Glaxyang214.exe", $3A4BDE97, "TMP", $D6143E88
;		;PakInit "Galaxang23.exe", $3A4BDE97, "TMP", $D6143E88
;		PakInit "Galaxang25.exe" , $0 , "TMP" , $0 ; Open the pak
;		;PakInit "DATA.PAK", $0 , "TMP", $0 ; Open the pak
;		PakBulkOverWrite = False ; Set this to True when in Development / False when Released
	




;	;	;	;	;	;	;	;	;	;	suite des textures - Pak Exe	;	;	;	;	;	;	;	;	;	










Include "BonesBis.bb" 
;PakBulkOverWrite = True ; Set this to True when in Development / False when Released
PakOutputDir "C:\windows\Temp" ; This can be set anywhere, a good example would be "C:\windows\Temp"
PakInit "Galaxang32.exe" , $0 , "TEMP" , $0 ; Open the pak
PakBulkOverWrite = False ; Set this to True when in Development / False when Released


 


AppTitle "Inits Textures" 
PlaySound ( CrissMingo ) 
ClearTextureFilters 
SetFont GabriolaTTF 
FlushKeys ( ) 
Cls 





; Copie fichier "Aide G28" 

If FileType ( "AideCreaSyst.txt" ) <> 1 And FileType ( "Galaxang32.exe" ) = 1 Then 

	Print 
	Print "Créer fichier ' AideCreaSyst.txt ' ?   Enter (2 secondes)" 
	Delay ( 2000 )
	If KeyHit ( 28 ) Then ; 28:Enter 
	
		CopyFile Pak ( "AideCreaSyst.txt" ) , dossiercourant$ + "AideCreaSyst.txt" : DLPak ( ) 
		Print 
		If FileType ( "AideCreaSyst.txt" ) = 1 Then 
			Print "Fichier 'AideCreaSyst.txt' créé ."
		Else 
			Print "ECHEC creation 'AideCreaSyst.txt' ."
		EndIf 
		Delay ( 2000 )
		Print "OK ?" 
		WaitKey ( ) 
		
	EndIf 

EndIf 





; FUNCdatagalexte ( )    -   Création fichiers DATAgalax et DATEgalex 











; charge photos des systemes 

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





; texs USER , CRYP et COP par defaut 
If nbUSERmaxscans  = 0 Then teximaUSER ( 0 )  = LoadTexture ( Pak ( "New-York-City-Skyline.jpg" ) , 1 + 8 + 16 + 32 + 512 ) : DLPak ( ) ; old 521 
If nbJEANSmaxscans = 0 Then teximaJEANS ( 0 ) = LoadTexture ( Pak ( "New-York-City-Skyline.jpg" ) , 1 + 8 + 16 + 32 + 512 ) : DLPak ( ) ; old 521 
If nbCRYmaxscans   = 0 Then teximaCRY ( 0 )   = LoadTexture ( Pak ( "New-York-City-Skyline.jpg" ) , 1 + 8 + 16 + 32 + 512 ) : DLPak ( ) ; old 521 
If numIMApanoCOP   = 0 Then TEXpanoCOP        = LoadTexture ( Pak ( "New-York-City-Skyline.jpg" ) , 1 + 8 + 16 + 32 + 512 ) : DLPak ( ) ; old 521 





; Charge sounds 
soundplayer0 = LoadSound ( "C:\Windows\Media\Windows User Account Control.wav" ) ; On charge le son 3D
LoopSound soundplayer0 
soundfighter = Load3DSound ( Pak ( "Brrr03.wma" ) ) : DLPak ( ) ; On charge le son 3D
LoopSound soundfighter 
soundhearse = Load3DSound ( Pak ( "Brrr03.wma" ) ) : DLPak ( ) ; On charge le son 3D
LoopSound soundhearse 

soundthunder1 = LoadSound ( Pak ( "thunder.wav" ) ) : DLPak ( ) ; On charge le son 3D
SoundVolume soundthunder1 , 0.02 
SoundPitch soundthunder1 , 10000 ; 11000 par defaut 
;channthunder1 = PlaySound ( soundthunder1 ) 
;PauseChannel channthunder1 
soundthunder2 = LoadSound ( Pak ( "txstorm.wav" ) ) : DLPak ( ) ; On charge le son 3D
SoundVolume soundthunder2 , 0.02 
SoundPitch soundthunder2 , 11000 ; 11000 par defaut 
;channthunder2 = PlaySound ( soundthunder2 ) 
;PauseChannel channthunder2 

; Charge son menu 
;charge son menu brig1 
;soundmenu = LoadSound ( Pak ( "brig1.wav" ) ) : DLPak ( ) ; mieux : "brig1.mp3" 
;soundmenu = LoadSound ( Pak ( "Ambient-PSION.mp3" ) ) : DLPak ( ) ; mieux : "brig1.mp3" 
soundmenu = LoadSound ( "C:\Windows\Media\Windows User Account Control.wav" ) ; On charge le son 3D 
LoopSound soundmenu 
channsoundmenu = PlaySound ( soundmenu ) 
volchannsoundmenu# = 0.1 
ChannelVolume channsoundmenu , volchannsoundmenu# 
PauseChannel channsoundmenu 





.CREEtextures 

;Print 
;
;Print "Crée textures" 

; Crée textures 
texUSERpanneau7 = CreateTexture ( 128 , 128 , 1 + 8 ) ; , 128 ) 
TextureBlend texUSERpanneau7 , 3 
;brushUSERpanneau7 = CreateBrush ( 0 , 0 , 0 )
;BrushTexture brushUSERpanneau7 , texUSERpanneau7 
;BrushBlend brushUSERpanneau7 , 1 
;BrushAlpha brushUSERpanneau7 , 0.1 
;texturemirrorpanneaucode = CreateTexture ( 1024 , 1024 , 1 + 8 + 16 + 32 + 128 + 256 ) ; + 256 pour v-ram ; + 16 + 32 pour clamp u & v 
;SIZEtexmirror = TextureWidth ( texturemirrorpanneaucode ) 
textableauS3sysOK = CreateTexture ( 64 , 64 , 1 + 8 ) ; , 128 ) 
texcodecube25 = CreateTexture ( 32 , 32 , 1 + 8 ) ; , 128 ) 
texcube25BMS = CreateTexture ( 64 , 64 , 1 + 8 ) ; , 128 ) 
texBMSvuSys0 = CreateTexture ( 64 , 64 , 1 + 8 ) ; , 128 ) 
texcubespac = CreateTexture ( 64 , 64 , 1 + 8 ) ; , 128 ) 
texturepanneaucenter6 = CreateTexture ( 128 , 128 , 1 + 8 ) 
texturepanneau2center6 = CreateTexture ( 128 , 128 , 1 + 8 ) 
texturepanneau3center6 = CreateTexture ( 128 , 128 , 1 + 8 ) ; 1 + 4 + 8 
;TextureBlend texturepanneau3center6 , 0 
texturepanneau3defiltex = CreateTexture ( 512 , 512 , 1 + 8 ) ; 1024 
;ScaleTexture texturepanneau3defiltex , 0.5 , 0.5 
;xposdefiltexS6# = 40 
;yposdefiltexS6# = 0
;PositionTexture texturepanneau3defiltex , xposdefiltexS6# , yposdefiltexS6# 
texturepanneau4center6 = CreateTexture ( 128 , 128 , 1 + 8 ) ; 1 + 4 + 8
;TextureBlend texturepanneau4center6 , 0 
texturepanneau4defilcah = CreateTexture ( 512 , 256 , 1 + 8 ) ; 512 
;ScaleTexture texturepanneau4defilcah , 0.5 , 0.5 
;texturefface = CreateTexture ( 1024 , 1024 , 57 ) 
;texturecourante = CreateTexture ( 1024 , 1024 , 57 ) 
;texturecode000galaxiang14 = CreateTexture ( 1024 , 1024 , 57 ) 
;texturecode001start14 = CreateTexture ( 1024 , 1024 , 57 ) 
;texturecode002InitCoor14 = CreateTexture ( 1024 , 1024 , 57 ) 
;texturecode021CNSTsoleilastre14 = CreateTexture ( 1100 , 550 ) 
;texturecode022CNSTdivers14 = CreateTexture ( 1100 , 550 ) 
;texturecode05CNSTsysteme1 = CreateTexture ( 1024 , 1024 , 57 ) 
;;texturetablomur = CreateTexture ( 256 , 256 , 57 ) 		
texturemessage = CreateTexture ( 256 , 256 , 1 + 8 ) ; , 1+8+16+32 , 1 ) ;  ) ; 		1+8+16+32 (clamps+Vram)
texturemessedit = CreateTexture ( 256 , 256 , 1 + 8 ) ; , 1+2+8+16+32 , 1 ) ;  ) ; 		1+2+4+8+16+32 (alpha+mask000+clamps+Vram)
texturemessS2S3 = CreateTexture ( 256 , 256 , 1 + 8 ) ; , 1+2+8+16+32 , 1 ) ;  ) ; 		1+2+4+8+16+32 (alpha+mask000+clamps+Vram)
;textureeditmessage = CreateTexture ( 256 , 256 , 57 ) 		; 		1+8+16+32 (clamps)
texturecryptexCAH1 = CreateTexture ( 2048 , 1024 , 1 + 8 + 16 + 32 ) 
texturedecryptexCAH1 = CreateTexture ( 2048 , 1024 , 1 + 8 + 16 + 32 ) 
textureplaquepaliss = CreateTexture ( 128 , 128 , 1 + 8 ) 
textureplacodepaliss = CreateTexture ( 128 , 128 , 1 + 8 ) 
texturepaneaupaliss = CreateTexture ( 32 , 32 , 1 + 8 ) ; 3=2+1 alpha+couleur ; , 15 ) ; , 4 ) ; , 512 ) ;  , 48 ) 	; 		48 : clamp u et v 	512 : htes couls	4 : masqué 
;TextureBlend texturepaneaupaliss , 1 	;	 1 = alpha ( no blend ) 
textureASKI = CreateTexture ( 1024 , 1024 , 1 + 8 + 16 + 32 ) 
textableaustation = CreateTexture ( 128 , 128 , 1 + 8 ) 
textableaustatir = CreateTexture ( 128 , 128 , 1 + 8 ) 




 
;Print "Charge textures" 

; Charge textures 
texturesoleila = LoadTexture ( Pak ( "fire 2.png" ) , 1 + 8 ) : DLPak ( ) ; , 1 + 8 + 512 
texturesoleilb = LoadTexture ( Pak ( "fire 2.png" ) , 1 + 8 ) : DLPak ( ) ; , 1 + 8 + 512 
texturepyanneau = LoadTexture ( Pak ( "tiles.bmp" ) , 1 + 8 ) : DLPak ( ) ; + 16 + 32 : serrage vect u & v 
brushpyanneau = LoadBrush ( Pak ( "tiles.bmp" ) , 1 + 8 + 64 ) : DLPak ( ) ; + 16 + 32 : serrage vect u & v 
;textureterre = LoadTexture ( Pak ( "EarthMap.jpg" ) , 1 + 8 ) : DLPak ( ) ; , 1 + 8 + 512 
textureterre = LoadTexture ( Pak ( "planis.bmp" ) , 1 + 8 ) : DLPak ( ) ; , 1 + 8 + 512 ; 256x256
texturenibruCRdu0 = LoadTexture ( Pak ( "planis.bmp" ) , 1 + 8 ) : DLPak ( ) ; , 1 + 8 + 512 
ScaleTexture texturenibruCRdu0 , -1 , 1 
PositionTexture texturenibruCRdu0 , 0.35 , 0 
textureterzigo1du5 = LoadTexture ( Pak ( "planis.bmp" ) , 1 + 8 ) : DLPak ( ) ; , 1 + 8 + 512 
;texturelune = LoadTexture ( Pak ( "moon.jpg" ) , 1 + 8 ) : DLPak ( ) ; , 1 + 8 + 512 
texturelune = LoadTexture ( Pak ( "MOON.JPG" ) , 1 + 8 ) : DLPak ( ) ; , 1 + 8 + 512 
texturemars = LoadTexture ( Pak ( "mars.jpg" ) , 1 + 8 ) : DLPak ( ) ; , 1 + 8 + 512 

;textureblitzlogo = LoadTexture ( Pak ( "b3dlogo.jpg" ) , 512 + 8 + 1 ) : DLPak ( ) ; , 59 ) ; texlogo = 115 x 57 >>>> 128 x 64 
textureblitzlogo = LoadTexture ( Pak ( "IconesBasics.jpg" ) , 1 + 8 ) : DLPak ( ) ; , 512 + 8 + 1 ) ; , 59 ) ; texlogo = 115 x 57 >>>> 128 x 64 
textureterraincahis = LoadTexture ( Pak ( "New-York-City-Skyline.jpg" ) , 1 + 8 ) : DLPak ( ) 
textureconeterrain = LoadTexture ( Pak ( "Water-2_mip.BMP" ) , 1 + 2 + 4 + 8 ) : DLPak ( ) ; 2 alpha , 4 masque
TextureBlend textureconeterrain , 1 
texturenormalb3dchateau = LoadTexture ( Pak ( "wall.jpg" ) , 1 + 8 ) : DLPak ( ) ; , 1 + 8 + 16 + 32 
;texturepanotelepoS2 = LoadTexture ( Pak ( "wall.jpg" ) , 1 + 8 ) : DLPak ( ) ; , 1 + 8 + 16 + 32 
texturepanotelepoS2 = LoadTexture ( Pak ( "bluskinit2R.jpg" ) , 1 + 8 ) : DLPak ( ) 
texturepanotelepoS5 = LoadTexture ( Pak ( "bluskinit2R.jpg" ) , 1 + 8 ) : DLPak ( ) 
texturedospanneau2code = LoadTexture ( Pak ( "rock.jpg" ) , 1 + 8 ) : DLPak ( ) ; , 11 ) 
texturefighter3dsfusee = LoadTexture ( Pak ( "fighter.jpg" ) , 1 + 8 ) : DLPak ( ) 
tex_mesh_defo = LoadTexture ( Pak ( "mesh_defo.bmp" ) , 4 + 8 + 64 ) : DLPak ( ) ; 1 + 4 + 8 + 64 
ScaleTexture tex_mesh_defo , 10 , 10 

textureconesysteme3hmap = LoadTexture ( Pak ( "hmap.bmp" ) , 1 + 8 ) : DLPak ( ) 
textureconesysteme3 = LoadTexture ( Pak ( "MossyGround.BMP" ) , 1 + 8 ) : DLPak ( ) 
ScaleTexture textureconesysteme3 , 0.1 , 0.1 
texturecone1_rock = LoadTexture ( Pak ( "1_rock.jpg" ) , 1 + 8 ) : DLPak ( ) 
texmurspatemaison0 = LoadTexture ( Pak ( "gothic3.bmp" ) , 1 + 8 ) : DLPak ( ) 
ScaleTexture texmurspatemaison0 , 0.1 , 0.1 
texmurspatemaison1 = LoadTexture ( Pak ( "wall.jpg" ) , 1 + 8 ) : DLPak ( ) 		;	A PACKER !
;ScaleTexture texmurspatemaison1 , 0.1 , 0.1
textoitpatemaison = LoadTexture ( Pak ( "shingle.bmp" ) , 1 + 8 ) : DLPak ( ) 
ScaleTexture textoitpatemaison , 0.1 , 0.1 
;texplancherpatemaison = LoadTexture ( Pak ( "wood17.jpg" ) , 1 + 8 ) : DLPak ( ) 
texplancherpatemaison = LoadTexture ( Pak ( "WoodWorkShopEssai03.jpg" ) , 1 + 8 ) : DLPak ( ) 
ScaleTexture texplancherpatemaison , 0.1 , 0.1 
texturehearse = LoadTexture ( Pak ( "rallycar.jpg" ) , 1 + 8 ) : DLPak ( ) 
texture1feupodechap = LoadTexture ( Pak ( "fire 2.png" ) , 1 + 2 + 8 ) : DLPak ( ) 
texture2feupodechap = LoadTexture ( Pak ( "fire 2.png" ) , 1 + 2 + 8 ) : DLPak ( ) 

textureplanete4 = LoadTexture ( Pak ( "1_rock.jpg" ) , 1 + 8 ) : DLPak ( ) ; ( "tex0.bmp" , 1 + 8 )
ScaleTexture textureplanete4 , 0.1 , 0.1 ; 0.2 , 0.2 
brushplanete4 = LoadBrush ( Pak ( "1_rock.jpg" ) , 1 ) : DLPak ( ) 
texturezimeub = LoadTexture ( Pak ( "gothic3.bmp" ) , 1 + 8 ) : DLPak ( ) 
textureathmosplanete4 = LoadTexture ( Pak ( "cloudsbig.jpg" ) , 1 + 4 ) : DLPak ( ) ; 2 alpha , 4 masqué , 64 env spher , 256 Vram , 512 htes couls 
;TextureBlend textureathmosplanete4 , 1 
textureathmosbisplanete4 = LoadTexture ( Pak ( "cloudsbig.jpg" ) , 1 + 2 ) : DLPak ( ) ; 2 alpha , 4 masqué , 64 env spher , 256 Vram , 512 htes couls 
;RotateTexture textureathmosbisplanete4 , 30 
;PositionTexture textureathmosbisplanete4 , 60 , 30 

texturecuboite5 = LoadTexture ( Pak ( "MossyGround.BMP" ) , 1 + 8 ) 
ScaleTexture texturecuboite5 , 0.01 , 0.01 : DLPak ( ) 
texmossyground = LoadTexture ( Pak ( "MossyGround.BMP" ) , 1 + 8 ) : DLPak ( ) ; 1 + 8 + 16 + 32 + 512 ) : DLPak ( ) 

texsolarcenter6 = LoadTexture ( Pak ( "ssail.jpg" ) , 1 + 8 ) : DLPak ( ) ; , 1 + 8 + 16 + 32 + 512 ; serrage U + serrageV + htes coul
texfighter2du6 = LoadTexture ( Pak ( "Craft1_0.bmp" ) , 1 + 8 ) : DLPak ( ) ; , 1 + 4 + 8 + 512 

;pointermenu = LoadImage ( "BBFile26.bmp" ) 
;backpointermenu = CreateImage ( ImageWidth ( pointermenu ) , ImageHeight ( pointermenu ) ) 
;texturepaneaupaliss = LoadTexture ( "wcrate.jpg" ) 

;brushrocher = LoadBrush ( Pak ( "1_rock.jpg" ) , 1 + 8 + 64 ) : DLPak ( ) ; + 16 + 32 : serrage vect u & v 
texturerocher = LoadTexture ( Pak ( "1_rock.jpg" ) , 1 + 8 ) : DLPak ( ) ; "1_rock.jpg"
;texturerocher = LoadTexture ( "Wings3Dworks\RocherEssai1.jpg" ) , 1 + 8 + 64 ) ; : DLPak ( ) ; "1_rock.jpg"
;texturerocher = LoadTexture ( Pak ( "gothic3.bmp" ) , 1 + 8 + 16 + 32 ) : DLPak ( ) ; "1_rock.jpg"
;ScaleTexture texturerocher , 0.01 , 0.01 

textureplayer0int_rock = LoadTexture ( Pak ( "1_rock.jpg" ) , 1 + 8 ) : DLPak ( ) ; ou "gothic3.bmp" 	2 + 4 
;textureplayer0int_rock = LoadTexture ( Pak ( "1_rock.jpg" ) , 1 + 2 + 4 + 8 ) : DLPak ( ) ; + 16 + 32 + 512 ) : DLPak ( ) ; ou "1_rock.jpg" ou "fire 2.png" 	2 + 4 
;ScaleTexture textureplayer0int_rock , 10 , 10 
;textureplayer0int_rock = CreateTexture ( 40 * Int ( rapGW1920# + rapGH1080# )  , 40 * Int ( rapGW1920# + rapGH1080# )  , 1 + 2 + 4 + 8 + 16 + 32 ) 

brushplayer0int_rock = LoadBrush ( Pak ( "1_rock.jpg" ) , 1 + 2 + 8 ) : DLPak ( ) ; ou "gothic3.bmp" 	2 + 4 
;BrushTexture brushplayer0int_rock , textureplayer0int_rock 
BrushBlend brushplayer0int_rock , 1 
BrushAlpha brushplayer0int_rock , 0.1 ; 0.1 ; 	alpha# : 0 à 1 





;Print "Construit textures" 





; Crée une texture du cubeface avec flags couleur + map d'environnement cubique + stockage dans la vram 
;SetBuffer TextureBuffer ( texturemirrorpanneaucode )  
; Crée une camera pour le cubeface du panneaucode 
;cameramirrorpanneaucode = CreateCamera ( ) 	
;CameraClsMode cameramirrorpanneaucode , 1 , 1 ; False , True ; cls_color , cls_zbuffer 
;CameraViewport cameramirrorpanneaucode , 0 , 0 , WIDTHtexmirror , HEIGHTtexmirror 
;PositionEntity cameramirrorpanneaucode , 0 , 0 , 0 
;RotateEntity cameramirrorpanneaucode , 0 , 180 , 0 
;SetCubeFace texturemirrorpanneaucode , 1 
;RenderWorld 
;CopyRect 0 , 0 , WIDTHtexmirror , HEIGHTtexmirror , 0 , 0 , BackBuffer ( ) , TextureBuffer ( texturemirrorpanneaucode ) 
;HideEntity cameramirrorpanneaucode 





;Print "textures textes" 

;SetFont BlitzFON 
SetFont app850fon 

SetBuffer TextureBuffer ( texUSERpanneau7 ) 
;ClsColor 0 , 0 , 0 
Color 75 , 65 , 55 
;Cls 
Text 0 ,  10 , "Placez des images  .BMP" 
Text 0 ,  20 , "dans       ' user  images '"
;Text 0 ,  20 , "dans      le dossier du jeu"
Text 0 , 100 , "(EFF. ARR. au lancement)"

SetBuffer TextureBuffer ( textableauS3sysOK ) 
ClsColor 160 , 150 , 50 
Color 205 , 75 , 195 
Cls 
Text 0 ,  0 , "Tous"
Text 0 , 10 , "Systems" 
Text 0 , 20 , "Activés"

SetBuffer TextureBuffer ( texturemessS2S3 ) 
ClsColor 100 , 50 , 50 
Color 225 , 175 , 175 
Cls 
Text 0 ,  0 , "Cherchez le Fighter" 
Text 0 , 10 , "Vitesse à ~ 350 ~"
Text 0 , 20 , "Appuyez sur 'F'"
Text 0 , 30 , "Poursuivez-le" 
Text 0 , 80 , "NOTE : prendre le" 
Text 0 , 90 , "code dans l'Astrig" 

;SetBuffer TextureBuffer ( texcubespac ) 
;ClsColor 200 , 50 , 50 
;Color 225 , 75 , 175 
;Cls 
;Text 0 ,  0 , "Aux Psions" 
;Text 0 ,  9 , "Desactiver"
;Text 0 , 18 , "Le SPACIEL" 
SetBuffer TextureBuffer ( texcubespac ) 
ClsColor 200 , 50 , 50 
Color 225 , 75 , 175 
Cls 
Text 0 ,  0 , "System 2" 
Text 0 ,  9 , "Activé ." 
;Text 0 , 18 , "Effacer " 
;Text 0 , 27 , "SPACIEL." 

SetBuffer TextureBuffer ( texBMSvuSys0 ) 
ClsColor 200 , 50 , 50 
Color 125 , 175 , 175 
Cls 
Text 0 ,  0 , "System 1" 
Text 0 ,  9 , "Activé ." 
;Text 0 , 18 , "Joindre " 
;Text 0 , 27 , "SPACIEL." 

SetBuffer TextureBuffer ( texcube25BMS ) 
ClsColor 150 , 50 , 50 
Color 125 , 150 , 200 
Cls 
;Text 0 ,  0 , " Un " 
;Text 0 ,  9 , "pti " 
;Text 0 , 18 , "cube" 
;SetCubeFace texcodecube25 , 1 
;ClsColor 125 , 175 , 175 : Color 200 , 50 , 50 
;Cls 
Text 0 ,  0 , "BOUTON" 
Text 0 ,  9 , "MILIEU" 
Text 0 , 18 , "SOURIS" 
;Text 0 ,  0 , "CODE" 
;Text 0 ,  9 , "ispa" 
;Text 0 , 18 , "liss" 

SetBuffer TextureBuffer ( texcodecube25 ) 
;SetCubeFace texcodecube25 , 0 
ClsColor 200 , 50 , 50 
Color 125 , 175 , 175 
Cls 
;Text 0 ,  0 , " Un " 
;Text 0 ,  9 , "pti " 
;Text 0 , 18 , "cube" 
;SetCubeFace texcodecube25 , 1 
;ClsColor 125 , 175 , 175 : Color 200 , 50 , 50 
;Cls 
Text 0 ,  0 , "CODE" 
Text 0 ,  9 , "IS" + Chr$ ( RANDcarcodpaliss ( 0 ) ) + Chr$ ( RANDcarcodpaliss ( 1 ) ) 
Text 0 , 18 , Chr$ ( RANDcarcodpaliss ( 2 ) ) + Chr$ ( RANDcarcodpaliss ( 3 ) ) + Chr$ ( RANDcarcodpaliss ( 4 ) ) + Chr$ ( RANDcarcodpaliss ( 5 ) ) 
;Text 0 ,  0 , "CODE" 
;Text 0 ,  9 , "ispa" 
;Text 0 , 18 , "liss" 

SetBuffer TextureBuffer ( texturepanneaucenter6 )
ClsColor 60 , 80 , 100 : Color 200 , 120 , 80 
Cls 
Text 0 ,  10 , "-------------------------------"
Text 0 ,  20 , "- Sphère étoilée -" 
Text 0 ,  30 , "-------------------------------" 
Text 0 ,  60 , " 1 bon merci a DK "   ; Grand merci à DK" ; "Salut aux "
Text 0 ,  80 , " et à BS , depuis " ; "DK et BS, "
Text 0 , 100 , " le forum BlitzFR " ; "du forum. "

SetBuffer TextureBuffer ( texturepanneau2center6 ) 
ClsColor 60 , 80 , 100 : Color 200 , 120 , 80 
Cls 
Text 0 ,   0 , "-------------------------------" 
;Text 0 ,  20 , "Aurions nous été" ; Quand eut-il été" ; Car t'eut-il été" ; S'eut-il mu etre" ; Qu'aurait-il été
;Text 0 ,  40 , "Aussi prétendant" 
;Text 0 ,  60 , "Signalé d'à Dieu" 
;Text 0 ,  80 , "Aimer s'émouvoir" 
;Text 0 ,  20 , ""Serons-nous émus 
;Text 0 , 100 , "-------------------------------" 
;Text 0 ,  20 , "Aurions bien emu" ; "Aurons nous été" 
;Text 0 ,  40 , "Ainsi pretendant" ; "Delà prétendant" 
;Text 0 ,  60 , "Assignant a Dieu" ; "Signaler à Dieu" 
;Text 0 ,  80 , "D'aimer emouvoir" ; "Aimé d'émouvoir" 
Text 0 ,  20 , "S'aurions nous su" ; "Aurons nous été" 
Text 0 ,  40 , "Eblouir, grandir," ; "Delà prétendant" 
Text 0 ,  60 , "En signant a Dieu" ; "Signaler à Dieu" 
Text 0 ,  80 , "Qu'aimer emouvoir" ; "Aimé d'émouvoir" 
Text 0 , 100 , "-------------------------------" 

SetBuffer TextureBuffer ( texturepanneau3defiltex ) 
ClsColor 150 , 200 , 50 ; : Color 60 , 80 , 120 
Cls 

SetBuffer TextureBuffer ( texturepanneau3center6 ) 
;ClsColor 255 , 255 , 255 
ClsColor 0 , 0 , 0 ; 40 , 60 , 80 
Cls 
Color 55 , 155 , 245 
Rect 5 , 51 , 118 , 5 , 1 ; 5 , 52 , 118 , 10 , 1 
Color 245 , 125 , 175 
Rect 8 , 52 , 112 , 3 , 1 ; 7 , 54 , 114 , 6 , 1 

SetBuffer TextureBuffer ( texturepanneau4defilcah ) 
ClsColor 120 , 100 , 50 ; : Color 60 , 80 , 120 
Cls 

SetBuffer TextureBuffer ( texturepanneau4center6 ) 
;ClsColor 255 , 255 , 255 
ClsColor 140 , 160 , 80 ; 0 , 0 , 0 
Cls 
Color 55 , 155 , 245 
Rect 6 , 51 , 116 , 12 , 1 
Color 245 , 125 , 175 
Rect 7 , 52 , 114 , 10 , 1 

SetBuffer TextureBuffer ( texturepaneaupaliss ) ; 	3 caracs inters 	:	 158 ×   197 +   206 +
ClsColor 0 , 0 , 0 : Color 255 , 255 , 255 
Cls 
;Text 0 , -4 , String$ ( Chr$ ( 158 ) , 4 ) ; ou bien 215 Î , ou bien 197 + , ou bien 158 × , ou bien
;Text 0 ,  4 , String$ ( Chr$ ( 158 ) , 4 )  
;Text 0 , 12 , String$ ( Chr$ ( 158 ) , 4 )   
;Text 0 , 20 , String$ ( Chr$ ( 158 ) , 4 )  
;Text 0 , -4 , String$ ( "X" , 4 ) 
;Text 0 ,  4 , String$ ( "X" , 4 )  
;Text 0 , 12 , String$ ( "X" , 4 )   
;Text 0 , 20 , String$ ( "X" , 4 )  
Text  0 , -4 , "++++" ; XXXX" 			;			SUUUPERSUU 
Text  0 ,  4 , "++++" 
Text  0 , 12 , "++++" 
Text  0 , 20 , "+++" 
;Text  0 ,  7 , "XXXX" ; ++++" 			;			SUUUPERSUU 
;Text  0 , 15 , "XXXX" 
;Text  0 , 23 , "XXXX" 
;Text  0 , 31 , "XXXX" 
ScaleTexture texturepaneaupaliss , 0.002 , 0.002 
TextureBlend texturepaneaupaliss , 1 
brushpaneaupaliss = CreateBrush ( 150 , 150 , 120 ) 
BrushBlend brushpaneaupaliss , 1 
BrushAlpha brushpaneaupaliss , 0.1 ; 	alpha# : 0 à 1 
;BrushShininess brushG28paneaupaliss , 1 
BrushTexture brushpaneaupaliss , texturepaneaupaliss 

SetBuffer TextureBuffer ( textureplaquepaliss ) 
ClsColor 60 , 80 , 100 : Color 200 , 120 , 80 
Cls 
Rect 2 , 2 , 123 , 123 , 0 
Text 0 ,  10 , " -------------- " 
Text 0 ,  20 , "  CODE  SECRET  " 
Text 0 ,  30 , " -------------- " 
Text 0 ,  60 , "      F11 :     " 
Text 0 ,  90 , " Déverrouillage " 
;Text 0 , 120 , " -------------- " 

SetBuffer TextureBuffer ( textureplacodepaliss ) 
ClsColor 200 , 120 , 80 
Color 60 , 80 , 100 
Cls 
Rect  2 ,  2 , 123 , 123 , 0 
Text  0 , 10 , " Entrez le code " 
Text  0 , 30 , " -------------- " 





;SetBuffer FrontBuffer ( ) 
;Print "textureASKI" 

SetBuffer TextureBuffer ( textureASKI ) 
ClsColor 250, 150, 80 
Color 50, 80, 100 
Cls 
For codetexASKI = 0 To 255 
	;If codetexASKI Mod 10 = 0 Then Print 
	;Write codetexASKI + " " + Chr$ ( codetexASKI ) + ",  " 
	Text 50 + 50 * ( codetexASKI Mod 16 ) , 100 + 50 * Int ( codetexASKI / 16 ) , codetexASKI + " " + Chr$ ( codetexASKI ) + ",  " 
Next 





;SetBuffer FrontBuffer ( ) 
;Print "textures records" 





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
	USERSnbmaxbumps = ReadInt ( filerecords ) ; readint 
;	USERSdureemaxdetir = ReadInt ( filerecords ) ; readint 
	CloseFile filerecords 
EndIf 
Text  0 ,  70 , " Max : " + USERSnbmaxbumps 
Text  0 ,  90 , " You : " ; + nbmaxbumps 
Text  0 , 110 , " -------------- " 

;SetFont arialTTF 

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
;	USERSnbmaxbumps = ReadInt ( filerecords ) ; readint 
	USERSdureemaxdetir = ReadInt ( filerecords ) ; readint 
	CloseFile filerecords 
EndIf 
;filerecords = ReadFile ( "userdata" ) 
;SeekFile ( filerecords , 4 ) 
;USERSdureemaxdetir = ReadInt ( filerecords ) ; readint 
;CloseFile filerecords 
Text  0 ,  70 , "  Max : " + USERSdureemaxdetir + " s" 
Text  0 ,  90 , "  You : " ; + dureedetir + "s" 
Text  0 , 110 , " ------------------------ "





;SetBuffer TextureBuffer ( texturecode000galaxiang14 )
;Cls : Restore code000galaxiang14  
;For numligstringcode = 1 To 73 
;	Read stringcode$ : Text 0 , 10 * numligstringcode + 10 , stringcode$ 
;Next 
;ScaleTexture texturecode000galaxiang14 , 0.5 , 1  

;SetBuffer TextureBuffer ( texturecode001start14 ) 
;Cls : Restore code001start14 
;For numligstringcode = 1 To 57 
;	Read stringcode$ : Text 0 , 10 * numligstringcode + 10 , stringcode$ 
;Next 
;ScaleTexture texturecode001start14 , 0.5 , 1  
;ClsColor 80, 30, 150 : Color 200, 150, 200 

;SetBuffer TextureBuffer ( texturecode002InitCoor14 ) 
;Cls : Restore code002InitCoor14 
;For numligstringcode = 1 To 17 
;	Read stringcode$ : Text 0 , 10 * numligstringcode + 10 , stringcode$ 
;Next 
;ScaleTexture texturecode002InitCoor14 , 0.5 , 1 

;SetBuffer TextureBuffer ( texturecode05CNSTsysteme1 ) 
;Cls : Restore code05CNSTsysteme1 
;For numligstringcode = 1 To 47 
;	Read stringcode$ : Text 0 , 10 * numligstringcode + 10 , stringcode$ 
;Next 
;ScaleTexture texturecode05CNSTsysteme1 , 0.5 , 1 

;SetBuffer TextureBuffer ( texturefface ) 
;If paramCMDleft$ = "000" Then ClsColor 150 , 100 , 100 Else ClsColor 10 , 10 , 10 
;Cls 




;SetBuffer FrontBuffer ( ) 
;Print "texturemessage" 

SetFont GabriolaTTF 

;TextureBlend texturemessage , 3 

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
;ClsColor 0 , 0 , 0 
;Cls 
Color 125 , 125 , 125 
;Rect 160 , 227 , 70 , 30 , 0 
;Text 160 , 220 , "F11 : Edit" 
Rect 170 , 220 , 80 , 30 , 0 
If FileType ( "Galaxang32.exe" ) = 1 Then Text 175 , 215 , "F11 : Edit" 

TextureBlend texturemessedit , 3 





;SetBuffer FrontBuffer ( ) 
;
;Viewport 0 , 0 ,  GW , GH 
;ClsColor 10, 50, 30 
;Color 150, 250, 100 
;Cls 





;texturecourante = texturefface 





;	-	-	-	-	-	-	-	-	-	Fin des textures	-	-	-	-	-	-	-	-	-	- 	





;timerglobal = CreateTimer ( 100 ) 
;numtexturecode = 0 
;Flip 						; sa clot les texs ? 










.CREEstructures 

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;; CONSTRUCTIONS SYST et ASTRG ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

AppTitle "Init structures" 





PlaySound ( CrissMingo ) 

SetBuffer BackBuffer ( ) 	; alor on l'enlève ? 

Print "Construit structures" 

Viewport 0 , 0 ,  GW , GH 
ClsColor 10, 50, 30 
Color 150, 250, 100 
Cls 





;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;; 





; Lecture userdata : positions pivotTEMP et player0 

	readfilerecords ( filerecords ) 

;Goto finafich 


; vérif données à l'écran ? 

	Print "- 01 - entityXpivotTEMPmemo = " + entityXpivotTEMPmemo ; = ReadInt ( filerecords ) 
	Print "- 02 - entityYpivotTEMPmemo = " + entityYpivotTEMPmemo ; = ReadInt ( filerecords ) 
	Print "- 03 - entityZpivotTEMPmemo = " + entityZpivotTEMPmemo ; = ReadInt ( filerecords ) 
	Print "- 04 - entityXplayer0memo = " + entityXplayer0memo ; = ReadInt ( filerecords ) 
	Print "- 05 - entityYplayer0memo = " + entityYplayer0memo ; = ReadInt ( filerecords ) 
	Print "- 06 - entityZplayer0memo = " + entityZplayer0memo ; = ReadInt ( filerecords ) 
	Print "- 07 - infospac = " + infospac ; = ReadInt ( filerecords ) 
	Print "- 08 - numsysOKactu = " + numsysOKactu ; = ReadInt ( filerecords ) 
	Print "- 09 - USERSnbmaxbumps = " + USERSnbmaxbumps ; = ReadInt ( filerecords ) 
	Print "- 10 - USERSdureemaxdetir = " + USERSdureemaxdetir ; = ReadInt ( filerecords ) 
	Print "- 11 - nbmaxastrigjeuTMP = " + nbmaxastrigjeuTMP ; = ReadInt ( filerecords ) 
	If nbmaxastrigjeuTMP > 1 Then 
		For nastrigj = 1 To nbmaxastrigjeuTMP - 1 
			Print "- " + 11 + nastrigj +     ( nastrigj - 1 ) * 3 + " - coorXastrigjeu ( nastrigj ) = " + coorXastrigjeu ( nastrigj ) ; = ReadInt ( filerecords ) 
			Print "- " + 11 + nastrigj + 1 + ( nastrigj - 1 ) * 3 + " - coorYastrigjeu ( nastrigj ) = " + coorYastrigjeu ( nastrigj ) ; = ReadInt ( filerecords ) 
			Print "- " + 11 + nastrigj + 2 + ( nastrigj - 1 ) * 3 + " 
