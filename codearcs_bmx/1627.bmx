; ID: 1627
; Author: Nilium
; Date: 2006-02-25 05:10:27
; Title: BSP Parser
; Description: Parses Q3 BSP files

Strict

Import Brl.Blitz
Import Brl.Stream
Import Brl.Bank
Import Brl.BankStream
'Import Cower.Math3D
Import "resource.bmx"

Private

Const MAGIC_BSP% = $50534249

Const BSP_ENTITIES% = 0
Const BSP_TEXTURES% = 1
Const BSP_PLANES% = 2
Const BSP_NODES% = 3
Const BSP_LEAVES% = 4
Const BSP_LEAFFACES% = 5
Const BSP_LEAFBRUSHES% = 6
Const BSP_MODELS% = 7
Const BSP_BRUSHES% = 8
Const BSP_BRUSHSIDES% = 9
Const BSP_VERTICES% = 10
Const BSP_MESHVERTS% = 11
Const BSP_EFFECTS% = 12
Const BSP_FACES% = 12
Const BSP_LIGHTMAPS% = 14
Const BSP_LIGHTVOLS% = 15
Const BSP_VIS% = 16

Type bsplump
    Field offset%
    Field length%
End Type

Public

Type bspdata
    Field entities:bspentity[]
    Field textures:bsptexture[]
    Field planes:bspplane[]
    Field nodes:bspnode[]
    Field leaves:bspleaf[]
    Field leaffaces%[]
    Field leafbrushes%[]
    Field models:bspmodel[]
    Field brushes:bspbrush[]
    Field brushsides:bspbrushside[]
    Field vertices:bspvertex[]
    Field meshverts%[]
    Field effects:bspeffect[]
    Field faces:bspface[]
    Field lightmaps:TBank[]
    Field lightvols:bsplightvol[]
    Field vis:bspvis
End Type

Function LoadBSP:bspdata( url:Object )
    Rem
    Local res:TBank = GetResource( StripExt(n)+".bsp" )
    If res = Null Then
        Debuglog "BSP file does not exist"
        Return
    EndIf
    
    If res.PeekInt( 0 ) <> MAGIC_BSP Or res.PeekInt( 4 ) <> $2E Then
        Debuglog "Resource is not a valid BSP, version: "+res.PeekInt( 4 )+"  magic: "+res.PeekInt( 0 )
        Return
    EndIf
    
    Local s:TStream = ReadStream( res )
    s.Seek( 8 )
    EndRem  ' code from indigo, won't work unless you've got the engine
    Local s:TStream = OpenStream( url, True, False )
    If s.ReadInt( ) <> MAGIC_BSP Then
        Debuglog "Resource is not a valid BSP - magic int didn't match"
        s.Close( )
        Return Null
    EndIf
    
    If s.ReadInt( ) <> $2E Then
        Debuglog "BSP version is not supported"
        s.Close( )
        Return Null
    EndIf
    
    s.Seek( 8 ) ' Just in case
    Local lumps:bsplump[17]
    For Local i:Int = 0 To 16
        lumps[i] = New bsplump
        s.ReadBytes( Varptr lumps[i].offset, 8 )
        Debuglog "  lump: "+i+"  offset: "+lumps[i].offset+"  length: "+lumps[i].length
    Next
    
    Local bsp:bspdata = New bspdata
    bsp.entities = BSP_ParseEntities( s, lumps[BSP_ENTITIES] )
    bsp.textures = BSP_ParseTextures( s, lumps[BSP_TEXTURES] )
    bsp.planes = BSP_ParsePlanes( s, lumps[BSP_PLANES] )
    bsp.nodes = BSP_ParseNodes( s, lumps[BSP_NODES] )
    bsp.leaves = BSP_ParseLeaves( s, lumps[BSP_LEAVES] )
    bsp.leaffaces = BSP_ParseLeafFaces( s, lumps[BSP_LEAFFACES] )
    bsp.leafbrushes = BSP_ParseLeafBrushes( s, lumps[BSP_LEAFBRUSHES] )
    bsp.models = BSP_ParseModels( s, lumps[BSP_MODELS] )
    bsp.brushes = BSP_ParseBrushes( s, lumps[BSP_BRUSHES] )
    bsp.brushsides = BSP_ParseBrushSides( s, lumps[BSP_BRUSHSIDES] )
    bsp.vertices = BSP_ParseVertices( s, lumps[BSP_VERTICES] )
    bsp.meshverts = BSP_ParseMeshVertices( s, lumps[BSP_MESHVERTS] )
    bsp.effects = BSP_ParseEffects( s, lumps[BSP_EFFECTS] )
    bsp.faces = BSP_ParseFaces( s, lumps[BSP_FACES] )
    bsp.lightmaps = BSP_ParseLightmaps( s, lumps[BSP_LIGHTMAPS] )
    bsp.lightvols = BSP_ParseLightVols( s, lumps[BSP_LIGHTVOLS] )
    bsp.vis = BSP_ParseVisData( s, lumps[BSP_VIS] )
    
    s.Close( )
    Return bsp
End Function

'-------------- entities
Type bspkey
    Field name$
    Field value$
End Type

Type bspentity
    Field keys:TList = New TList
    
    Method GetKey$( name$, _default$="" )
        For Local i:bspkey = EachIn keys
            If i.name = name Then Return i.value
        Next
        Local i:bspkey = New bspkey
        keys.AddLast( i )
        i.name = name
        i.value = _default
        Return _default
    End Method
End Type

Private

Function BSP_ParseEntities:bspentity[]( s:TStream, lump:bsplump )
    s.Seek( lump.offset )
    Local entityString$ = s.ReadString( lump.Length )
    Local entities:TList = New TList
    Local c$, state%, val$, nam$, ent:bspentity
    For Local i:Int = 0 To entityString.Length-1
        c = Chr(entityString[i])
        
        Select c
            Case "{"
                Debuglog "new entity opened"
                ent = New bspentity
                entities.AddLast( ent )
            Case "}"
                ent = Null
            Case "~q"
                If state < 3 Then
                    state :+ 1
                ElseIf state = 3 Then
                    Local k:bspkey = New bspkey
                    k.name = nam
                    k.value = val
                    ent.keys.AddLast( k )
                    Debuglog "  adding bspkey to entity, name: "+nam+"  value: "+val
                    k = Null
                    nam = ""
                    val = ""
                    state = 0
                EndIf
            Default
                Select state
                    Case 1
                        nam :+ c
                    Case 3
                        val :+ c
                End Select
        End Select
    Next
    Return bspentity[](entities.ToArray( ))
End Function


'------------- textures
Public
Type bsptexture
    Field name$
    Field flags%
    Field content%
End Type

Private
Function BSP_ParseTextures:bsptexture[]( s:TStream, lump:bsplump )
    s.Seek( lump.offset )
    Local textures:bsptexture[lump.length/72]
    
    For Local i:Int = 0 To textures.Length-1
        Local name$ = s.ReadString( 64 ).Trim( )
        Local flags% = s.ReadInt( )
        Local content% = s.ReadInt( )
        Local tex:bsptexture = New bsptexture
        tex.name = name
        tex.flags = flags
        tex.content = content
        textures[i] = tex
        Debuglog "  texture name: "+name+"  flags: "+flags+"  content: "+content
    Next
    Return textures
End Function


'------------------- planes
Public
Type bspplane
    Field x#,y#,z#,d#
End Type

Private
Function BSP_ParsePlanes:bspplane[]( s:TStream, lump:bsplump )
    s.Seek( lump.offset )
    Local planes:bspplane[lump.length/12]
    For Local i:Int = 0 To planes.Length-1
        planes[i] = New bspplane
        s.ReadBytes( Varptr planes[i].x, 12 )
        Debuglog "  plane: "+planes[i].x+", "+planes[i].y+", "+planes[i].z+", "+planes[i].d
    Next
    Return planes
End Function


'------------- nodes
Public
Type bspnode
    Field planei%
    Field childa%
    Field childb%
    Field minx%
    Field miny%
    Field minz%
    Field maxx%
    Field maxy%
    Field maxz%
End Type

Private
Function BSP_ParseNodes:bspnode[]( s:TStream, lump:bsplump )
    s.Seek( lump.offset )
    Local nodes:bspnode[lump.length/36]
    For Local i:Int = 0 To nodes.Length-1
        nodes[i] = New bspnode
        s.ReadBytes( Varptr nodes[i].planei, 36 )
    Next
    Return nodes
End Function


'------------- leaves
Public
Type bspleaf
    Field cluster%
    Field area%
    Field minx%, miny%, minz%
    Field maxx%, maxy%, maxz%
    Field face%, faces%
    Field brush%, brushes%
End Type

Private
Function BSP_ParseLeaves:bspleaf[]( s:TStream, lump:bsplump )
    s.Seek( lump.offset )
    Local leaves:bspleaf[lump.length/Sizeof(bspleaf)]
    For Local i:Int = 0 To leaves.Length-1
        leaves[i] = New bspleaf
        s.ReadBytes( Varptr leaves[i].cluster, Sizeof(bspleaf) )
    Next
    Return leaves
End Function


'---------------- leaf faces
Private
Function BSP_ParseLeafFaces%[]( s:TStream, lump:bsplump )
    Local arr%[lump.Length/4]
    s.Seek( lump.offset )
    s.ReadBytes( arr, lump.length )
    Return arr
End Function


'--------------- leaf brushes
Private
Function BSP_ParseLeafBrushes%[]( s:TStream, lump:bsplump )
    Local arr%[lump.Length/4]
    s.Seek( lump.offset )
    s.ReadBytes( arr, lump.length )
    Return arr
End Function


'--------------- models
Public
Type bspmodel
    Field minx#, miny#, minz#
    Field maxx#, maxy#, maxz#
    Field face%, faces%
    Field brush%, brushes%
End Type

Private
Function BSP_ParseModels:bspmodel[]( s:TStream, lump:bsplump )
    s.Seek( lump.offset )
    Local models:bspmodel[lump.length/Sizeof(bspmodel)]
    For Local i:Int = 0 To models.Length-1
        models[i] = New bspmodel
        s.ReadBytes( Varptr models[i].minx, Sizeof(bspmodel) )
    Next
    Return models
End Function


'---------------- brushes
Public
Type bspbrush
    Field side%, sides%
    Field texture%
End Type

Private
Function BSP_ParseBrushes:bspbrush[]( s:TStream, lump:bsplump )
    s.Seek( lump.offset )
    Local brushes:bspbrush[lump.length/Sizeof(bspbrush)]
    For Local i:Int = 0 To brushes.Length-1
        brushes[i] = New bspbrush
        s.ReadBytes( Varptr brushes[i].side, Sizeof(bspbrush) )
    Next
    Return brushes
End Function


'--------------- brush sides
Public
Type bspbrushside
    Field planei%
    Field texture%
End Type

Private
Function BSP_ParseBrushSides:bspbrushside[]( s:TStream, lump:bsplump )
    s.Seek( lump.offset )
    Local sides:bspbrushside[lump.length/Sizeof(bspbrushside)]
    For Local i:Int = 0 To sides.Length-1
        sides[i] = New bspbrushside
        s.ReadBytes( Varptr sides[i].planei, Sizeof(bspbrushside) )
    Next
    Return sides
End Function


'-------------- vertices
Public
Type bspvertex
    Field x#,y#,z#
    Field su#,sv#
    Field lu#,lv#
    Field nx#, ny#, nz#
    Field r@, g@, b@, a@
End Type

Private
Function BSP_ParseVertices:bspvertex[]( s:TStream, lump:bsplump )
    s.Seek( lump.offset )
    Local vertices:bspvertex[lump.length/Sizeof(bspvertex)]
    For Local i:Int = 0 To vertices.Length-1
        vertices[i] = New bspvertex
        s.ReadBytes( Varptr vertices[i].x, Sizeof(bspvertex) )
    Next
    Return vertices
End Function


'---------------- meshverts
Private
Function BSP_ParseMeshVertices%[]( s:TStream, lump:bsplump )
    Local arr%[lump.Length/4]
    s.Seek( lump.offset )
    s.ReadBytes( arr, lump.length )
    Return arr
End Function


'---------------- effects
Public
Type bspeffect
    Field name$
    Field brush%
    Field unknown%
End Type

Private
Function BSP_ParseEffects:bspeffect[]( s:TStream, lump:bsplump )
    s.Seek( lump.offset )
    Local effects:bspeffect[lump.length/Sizeof(bspeffect)]
    For Local i:Int = 0 To effects.Length-1
        effects[i] = New bspeffect
        effects[i].name = s.ReadString(64).Trim( )
        s.ReadBytes( Varptr effects[i].brush, 8 )
    Next
    Return effects
End Function


'------------------ faces
Public
Type bspface
    Field texture%
    Field effect%
    Field _type%
    Field vertex%
    Field vertices%
    Field meshvert%
    Field meshverts%
    Field lightmap%
    Field lmx%, lmy%
    Field lmw%, lmh%
    Field lmox#, lmoy#, lmoz#
    Field lmsx#, lmsy#, lmsz#
    Field lmtx#, lmty#, lmtz#
    Field nx#, ny#, nz#
    Field pw%, ph%
End Type

Private
Function BSP_ParseFaces:bspface[]( s:TStream, lump:bsplump )
    s.Seek( lump.offset )
    Local faces:bspface[lump.length/Sizeof(bspface)]
    For Local i:Int = 0 To faces.Length-1
        faces[i] = New bspface
        s.ReadBytes( Varptr faces[i].texture, Sizeof(bspface) )
    Next
    Return faces
End Function


'----------------- lightmaps
Private
Function BSP_ParseLightmaps:TBank[]( s:TStream, lump:bsplump )
    s.Seek( lump.offset )
    Local lm:TBank[lump.Length/49152]
    For Local i:Int = 0 To lm.Length - 1
        lm[i] = TBank.Create( 49152 )
        s.ReadBytes( lm[i].Buf( ), 49152 )
    Next
    Return lm
End Function


'----------------- light vols
Public
Type bsplightvol
    Field ar@, ag@, ab@
    Field dr@, dg@, db@
    Field du@, dv@
End Type

Private
Function BSP_ParseLightVols:bsplightvol[]( s:TStream, lump:bsplump )
    s.Seek( lump.offset )
    Local lv:bsplightvol[lump.length/8]
    For Local i:Int = 0 To lv.Length-1
        lv[i] = New bsplightvol
        s.ReadBytes( Varptr lv[i].ar, 8 )
    Next
    Return lv
End Function


'----------------- vis
Public
Type bspvis
    Field n_vecs%
    Field sz_vecs%
    Field vecs@ Ptr
    
    Method Delete( )
        If vecs Then MemFree( vecs )
    End Method
End Type

Private
Function BSP_ParseVisData:bspvis( s:TStream, lump:bsplump )
    s.Seek( lump.offset )
    Local vis:bspvis = New bspvis
    vis.n_vecs = s.ReadInt( )
    vis.sz_vecs = s.ReadInt( )
    Debuglog "  vis size: "+(vis.n_vecs*vis.sz_vecs)
    If vis.n_vecs > 0 And vis.sz_vecs > 0 Then
        vis.vecs = MemAlloc( vis.n_vecs*vis.sz_vecs )
        s.ReadBytes( vis.vecs, vis.n_vecs*vis.sz_vecs )
    EndIf
    Return vis
End Function
