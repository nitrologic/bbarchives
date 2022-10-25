; ID: 1707
; Author: Leon Drake
; Date: 2006-05-10 23:15:58
; Title: Create Tiled Terrain
; Description: Create a Terrain that exists as a mesh with tile mapping!

; just some types i use for creating the plane.

Type bsurface
Field geohandle,surfacen$,shandle,surf
End Type


Type btri
Field v1handle,v2handle,v3handle,shandle,thandle,tri
End Type


Function CreatePlane()
; lets set how wide and thick each tile will be
;a width of 30 can make a pretty decent sized plane
local rectbrush_widthw,rectbrush_depthd,rectbrush_ripple,rectbrush_heighth 
rectbrush_widthw = 30
rectbrush_depthd = 30
; i set the height to 0 as it will be a flat plane
rectbrush_heighth = 0
; i was using this for water but variable here stands for 
;how many segments are in the plane. 6 will do
; it will create a plane in 6x6 tiles
rectbrush_ripple = 6
mainbrush = CreateMesh()

;i created a type just to make it easier than creating a bunch of vars for creating the plane.

xbrush.bsurface = New bsurface
xbrush\shandle = Rand(1,1000000)
xbrush\surfacen$ = "myplane"
xbrush\surf = CreateSurface(mainbrush) 

For tempstuffy = 0 To rectbrush_ripple-1
For tempstuffx = 0 To rectbrush_ripple-1
;tempstuffx = tempstuffy

ttri.btri = New btri
ttri\shandle = xbrush\shandle
ttri\thandle = Rand(1,1000000)
ttri\v1handle = AddVertex (xbrush\surf, 0+(rectbrush_widthw*tempstuffx),rectbrush_heighth,rectbrush_depthd+(rectbrush_depthd*tempstuffy), 0 ,1) 
ttri\v2handle = AddVertex (xbrush\surf, rectbrush_widthw+(rectbrush_widthw*tempstuffx),rectbrush_heighth,0+(rectbrush_depthd*tempstuffy), 1 ,0) 
ttri\v3handle = AddVertex (xbrush\surf, 0+(rectbrush_widthw*tempstuffx),rectbrush_heighth,0+(rectbrush_depthd*tempstuffy), 0,0) 
ttri\tri = AddTriangle (xbrush\surf,ttri\v1handle,ttri\v2handle,ttri\v3handle) 
ttri.btri = New btri
ttri\shandle = xbrush\shandle
ttri\thandle = Rand(1,1000000)
ttri\v1handle = AddVertex (xbrush\surf, rectbrush_widthw+(rectbrush_widthw*tempstuffx),rectbrush_heighth,rectbrush_depthd+(rectbrush_depthd*tempstuffy), 1 ,1) 
ttri\v2handle = AddVertex (xbrush\surf, rectbrush_widthw+(rectbrush_widthw*tempstuffx),rectbrush_heighth,0+(rectbrush_depthd*tempstuffy), 1 ,0) 
ttri\v3handle = AddVertex (xbrush\surf, 0+(rectbrush_widthw*tempstuffx),rectbrush_heighth,rectbrush_depthd+(rectbrush_depthd*tempstuffy), 0,1) 
ttri\tri = AddTriangle (xbrush\surf,ttri\v2handle,ttri\v3handle,ttri\v1handle) 

Next
Next

For brush.bsurface = Each bsurface
Delete brush
Next

For ttri.btri = Each btri
Delete ttri
Next

return mainbrush

End Function
