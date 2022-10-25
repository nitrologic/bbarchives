; ID: 1420
; Author: Booticus
; Date: 2005-07-12 21:19:27
; Title: Heightmap Toolbox
; Description: Generates Heightmaps with varying techniques.

Strict

Rem

HeightMap Toolbox
V0.1.0 BETA

By Shane Raffa
May 2005

Incorporating code (adapted) by 
 * Shawn C. Swift (Perlin noise stuff)
 * Simon Wetterlind (Midpoint displacement stuff)

PURPOSE

	A self-contained package of heightmap creating, processing, exporting, and 
	importing utilities.

TEST SYSTEM

	Athlon XP 1600+, 512MB, GeForce 3 Ti200 (64MB)
	Windows XP, BlitzMax

IMPLEMENTED METHODS AND FUNCTIONS:
	
	createRandomMap
	createHillMap
	createParticleSMap
	createParticleRMap
	createFaultMap
	createPerlinMap
	createMPDMap
		recurseMPD
		MPD
	
	fill
	resize
	normalise
	smoothen
	flatten
	makeCoast
	
	exportPNG
	importPNG
	exportX3D
	exportBin
	importBin

    blendMapsAdd	
    blendWithMapAdd
    blendMapsSub	
    blendWithMapSub
    blendMapsMul
    blendWithMapMul
    blendMapsDiv
    blendWithMapDiv
    blendMapsHi
    blendWithMapHi
    blendMapsLo
    blendWithMapLo
    blendMapsRep
    blendWithMapRep

	renderToBackBuffer
	renderToImage
	renderToPixmap
	
TODO: 

	makeRivers
	makeIsland
	importX3D
	{Mac OS 10.x port}
	{improve documentation}

End Rem

Type THeightMap

  Field Width = 128				'Width (units) > 0
  Field Height = 128			'Height (units) > 0
  Field Map#[Width, Height]  	'Matrix of height values (floats) >=0.0 <=1.0 normalised

  Rem fill(val# = 0.0)
  Set all heights to a specific value.
  val#: value to use. Use 0.0 to clear the map.
  End Rem
  Method fill(val# = 0.0)

    Local x, z

    For x = 0 To Width - 1
      For z = 0 To Height - 1
        Map#[x, z] = val#
      Next
    Next

  End Method
 
  Rem resize(w, h)
  Resize the map.
  w: width. > 0. Higher = slower performance and increased memory consumption. 
  h: height. > 0. Higher = slower performance and increased memory consumption.
  End Rem
  Method resize(w, h)
    
    If Not ((w > 0) And (h > 0)) Return 

    Map# = Null
    FlushMem

    Width = w
    Height = h
    Map# = New Float[Width, Height]

  End Method

  Rem createRandomMap(seed)
  Create a map of random height between 0.0 and 1.0.
  seed: random seed. Use Millisecs() to generate a different result every time.
  End Rem
  Method createRandomMap(seed)

    Local x, z

    SeedRnd(seed)

    For x = 0 To Width - 1
      For z = 0 To Height - 1
        Map#[x, z] = Rnd()
      Next
    Next

  End Method

  Rem createHillMap(seed, numhills, maxhsize)
  Create a map using the "hill algorithm".
  seed: random seed. Use Millisecs() to generate a different result every time.
  numhills: number of hills. (>1000) is best. Minimum 1.
  maxhsize: maximum hill radius. For best results, value should be significantly 
  less than the map's shortest dimension.
  End Rem
  Method createHillMap(seed, numhills, maxhsize)

    Local i, n, x, z, centrex, centrez, radius, startx, endx, startz, endz, ipos
    Local y#
  
    SeedRnd(seed)

    fill(0.0)
  
    For n = 1 To numhills

	  centrex = Rand(0, Width - 1)
	  centrez = Rand(0, Height - 1)
	  radius = Rand(0, Abs(maxhsize))

      startx = centrex - radius
      If startx < 0
	    startx = 0
      EndIf

      endx = centrex + radius
	  If endx > Width
	    endx = Width
      EndIf

      startz = centrez - radius
      If startz < 0
	    startz = 0
      EndIf

      endz = centrez + radius
      If endz > Height 
	    endz = Height
      EndIf

      For x = startx To endx - 1
        For z = startz To endz - 1
	   	  y# = (radius * radius) - (((x - centrex) * (x - centrex)) + ((z - centrez) * (z - centrez)))
	      If y# > 0.0
	        Map#[x, z] :+ y#
	      EndIf
	    Next
	  Next
    
    Next
  
    normalise()
  
  End Method

  Rem createParticleSMap(seed, particles, clusters)
  Create a map using the "sticky particle deposition" algorithm.
  seed: random seed. Use Millisecs() to generate a different result every time.
  particles: number of particles to calculate per cluster. Minimum 1.
  cluster: number of clusters: higher number gives greater map density. Minimum 1.
  End Rem
  Method createParticleSMap(seed, particles, clusters)

    Local m, n, x, z
    Local y#, disp# = 1.0
  
    SeedRnd(seed)

    fill(0.0)
  
    For m = 0 To clusters - 1
  
      x = Rand(0, Width - 1) 
      z = Rand(0, Height - 1)
	
      For n = 0 To particles - 1
	
        If ((x < Width) And (z < Height) And (x >= 0) And (z >= 0))
	      y# = Map#[x, z] + disp#
	      Map#[x, z] = y#
	    EndIf
	
	    Select Rand(0, 3)
          Case 0
            x :+ 1
          Case 1
            x :- 1
          Case 2
            z :+ 1
          Case 3
            z :- 1
        End Select

      Next

    Next

    normalise()
  
  End Method

  Rem createParticleRMap(seed, particles, clusters)
  Create a map using the "rolling particle deposition" algorithm.
  seed: random seed. Use Millisecs() to generate a different result every time.
  particles: number of particles to calculate per cluster. Minimum 1.
  cluster: number of clusters: higher number gives greater map density. Minimum 1.
  End Rem
  Method createParticleRMap(seed, particles, clusters)

    Local m, n, x, z, x2, z2, startx, endx, startz, endz
    Local y#, y2#, disp# = 1.0
  
    SeedRnd(seed)

    fill(0.0)
  
    For m = 0 To clusters - 1
  
      x = Rand(0, Width - 1) 
      z = Rand(0, Height - 1)
	
      For n = 0 To particles - 1
	
        If ((x < Width) And (z < Height) And (x >= 0) And (z >= 0))

	      y# = Map#[x, z] + disp#
	      Map#[x, z] = y#
	
	      startx = x - 1
          If startx < 0  
		    startx = 0
		  EndIf
          endx = x + 1
          If endx > Width 
  		    endx = Width
          EndIf

          startz = z - 1
          If startz < 0 
		    startz = 0
		  EndIf
          endz = z + 1
          If endz > Height
		    endz = Height
          EndIf

          For x2 = startx To endx - 1
            For z2 = startz To endz - 1
		      y2# = Map#[x2, z2]
              If y2# < y# 
                Map#[x2, z2] = y2# + disp#
              EndIf
            Next
          Next
	
	    EndIf
	
	    Select Rand(0, 3)
          Case 0
            x :+ 1
          Case 1
            x :- 1
          Case 2
            z :+ 1
          Case 3
            z :- 1
        End Select

      Next

    Next

    normalise()
  
  End Method
  
  Rem createFaultMap(seed, realism)
  Create a map using the "fault line" algorithm.
  seed: random seed. Use Millisecs() To generate a different result every time.
  realism: number of fault lines: higher number (> 1000) gives greater realism. Minimum 1.
  End Rem
  Method createFaultMap(seed, realism)

    Local n, x, z
    Local d#, disp#, v#, a#, b#, c#
  
    SeedRnd(seed)

    fill(0.0)
  
    d# = Sqr((Width * Width) + (Height * Height))
    disp# = 10.0

    For n = 1 To realism
  
      v# = Rnd(1,359)
	  a# = Sin(v#)
      b# = Cos(v#)
      c# = (RndFloat() * d#) - (d# / 2)

      For x = 0 To Width - 1
        For z = 0 To Height - 1
          If (((a# * x) + (b# * z) - c#) > 0.0)
            Map#[x, z] :+ disp#
          Else
            Map#[x, z] :- disp#
          EndIf
        Next
      Next

    Next

    normalise()

  End Method

  Rem createPerlinMap(seed, scale#, multiplier#)
  Create a map using the "perlin noise" alogorithm.
  ADAPTED from code obtained from the blitzbasic.com code archives.
  **** ORIGINAL AUTHOR: Shawn C. Swift ****
  IMPORTANT NOTE: This method will only work properly on maps with equal Width and 
  Height dimensions that are a power of two: eg 256 x 256.
  seed: random seed. Use Millisecs() To generate a different result every time.
  "scale# is the maximum height of the most frequent and smallest bumps in the terrain." - S. Swift.
  "multiplier# is how much each successive pass multiplies scale# by." - S. Swift.
  End Rem
  Method createPerlinMap(seed, scale#, multiplier#)

    If Not(Width = Height) Return

    SeedRnd(seed)

    Local size = Width
    Local x, z, ipos, ScaleDifference, Hx, Hz, Height_Z, Height_X,  Noise_Z, Noise_X
    Local max_height# = scale
    Local NoiseMapSize = Floor(size / 2)
 	Local i;
	Local NoiseMap#[NoiseMapSize + 1, NoiseMapSize + 1]

	Local StepSize#, N1#, N2#, N3#, N4#, Iy#, ICy#, Ix#, ICx#, Na#, Nb#, Nc#, Nd#, y#
	
	For x = 0 To size - 1
      For z = 0 To size - 1
        Map#[x, z] = RndFloat() * scale
	  Next
  	Next
  
    max_height# :* multiplier#
	
   Repeat
   
      For Noise_Z = 0 To NoiseMapSize
	    For Noise_X = 0 To NoiseMapSize
		    NoiseMap#[Noise_X, Noise_Z] = RndFloat() * max_height#
		Next
	  Next
	 
      ScaleDifference = size / NoiseMapSize
      StepSize = 1.0 / ScaleDifference
      	  
      For Noise_Z = 0 To NoiseMapSize - 1
	    For Noise_X = 0 To NoiseMapSize - 1
  	      N1# = NoiseMap#[Noise_X, Noise_Z]
		  N2# = NoiseMap#[(Noise_X + 1), Noise_Z]  
	      N3# = NoiseMap#[Noise_X, (Noise_Z + 1)]
	      N4# = NoiseMap#[(Noise_X + 1), (Noise_Z + 1)]
	      Hx = Noise_X * ScaleDifference
	      Hz = Noise_Z * ScaleDifference
	      Iy# = 0.0
	
	      For Height_Z = 0 To ScaleDifference - 1
			ICy# = 1.0 - ((Cos(Iy# * 180.0) + 1.0) / 2.0)
		    Ix# = 0.0			
            
		    For Height_X = 0 To ScaleDifference - 1
			   ICx = 1.0 - ((Cos(Ix * 180.0) + 1.0) / 2.0)
  		       Na# = N1# * (1.0 - ICx#)
		       Nb# = N2# * ICx#;
		       Nc# = N3# * (1.0 - ICx#)
	  	       Nd# = N4# * ICx#
							
		       y# = Map#[(Hx + Height_X), (Hz + Height_Z)]
		       Map#[(Hx + Height_X), (Hz + Height_Z)] = (y# + (Na# + Nb#) * (1.0 - ICy#) + (Nc# + Nd#) * ICy#)
		       Ix# = Ix# + StepSize;
		    Next
					
		    Iy# = Iy# + StepSize#	

	      Next
	
	    Next  	
      Next
				
    NoiseMapSize :/ 2
    max_height# :* multiplier
			
    Until (NoiseMapSize < 1)

	normalise()

  End Method

  Rem
  createMPDMap(seed, grain#)
  recurseMPD(x0, y0, x2, y2, grain#, level#)
  mpd#(x0, y0, x1, y1, x2, y2, grain#, level#)

  ADAPTED from code obtained from the BlitzCoder.com code database.
  **** ORIGINAL AUTHOR: Simon Wetterlind 13-08-2002 ****
  **** v.0.9.3 - Revised: 18-11-2002 by Simon Wetterlind. ****

  These methods are used to create a terrain map using the 
  Midpoint Displacement fractal algorithm (ie Plasma algorithm).
  grain# is the "graininess" i.e. the influence of randomness.

  seed: random seed. Use Millisecs() To generate a different result every time.
  End Rem
  Method createMPDMap(seed, grain#)

     fill(-1.0)

     SeedRnd(seed)

     Map#[0, 0] = RndFloat()
     Map#[(Width - 1), 0] = RndFloat()
     Map#[0, (Height - 1)]= RndFloat()
     Map#[(Width - 1), (Height - 1)] = RndFloat()

     recurseMPD(0, 0, Width - 1, Height - 1, grain#, 1.0)

     normalise()

  End Method

  Method recurseMPD(x0, z0, x2, z2, grain#, level#)

     If (x2 - x0 < 2) And (z2 - z0 < 2) Then Return
     
     Local v#, i#

     ' change the calculation of level# To (possibly) achieve 
     ' strange results
     level# = 2.0 * level#
     
     Local x1 = Ceil((x0 + x2) / 2.0)
     Local z1 = Ceil((z0 + z2) / 2.0)
     
     v# = Map#[x1, z0]
     If v# = -1.0 
       v# = MPD#(x0, z0, x1, z0, x2, z0, grain#, level#)
     EndIf
     i# = v#
     
     v# = Map#[x2, z1]
     If v# = -1.0 
       v# = MPD#(x2, z0, x2, z1, x2, z2, grain#, level#)
     EndIf
     i# :+ v#
     
     v# = Map#[x1, z2]
     If v# = -1.0 
       v# = MPD#(x0, z2, x1, z2, x2, z2, grain#, level#)
     EndIf
     i# :+ v#

     v# = Map#[x0, z1]
     If v# = -1.0 
       v# = MPD#(x0, z0, x0, z1, x0, z2, grain#, level#)
     EndIf
     i# :+ v#

     If Map#[x1, z1] = -1.0 
       Map#[x1, z1] = i# / 4.0 + Rnd(-grain#, grain#) / level#
     EndIf
 
     RecurseMPD(x0, z0, x1, z1, grain#, level#)
     RecurseMPD(x1, z0, x2, z1, grain#, level#)
     RecurseMPD(x1, z1, x2, z2, grain#, level#)
     RecurseMPD(x0, z1, x1, z2, grain#, level#)

  End Method

  Method MPD#(x0, z0, x1, z1, x2, z2, grain#, level#)

     Local r# = 0

     r# :+ (Map#[x0, z0] + Map#[x2, z2]) / 2.0
     
     If r# < 0.0 Then r# = 0.0
     If r# > 1.0 Then r# = 1.0
 
     Map#[x1, z1] = r#
     
     Return r#
     
  End Method

  Rem normalise()
    Normalise all map values to between 0 and 1.
  End Rem
  Method normalise()

   Local x, z;
   Local minv# = 10^38, maxv# = 10^-38, y#
  
   For x = 0 To Width - 1
     For z = 0 To Height - 1

       y# = Map#[x, z]
 	   If y# < minv#
	     minv# = y#
	   Else
	     If y# > maxv#
		   maxv# = y#
		 EndIf
	   EndIf
	  
     Next
   Next

   For x = 0 To Width - 1
     For z = 0 To Height - 1
       y# = (Map#[x, z] - minv#) / (maxv# - minv#)
	   Map#[x, z] = y#
	 Next
   Next	
	
  End Method

  Rem smoothen(k#) 
  Smooth the map values.
  Some maps require more smoothing than others to create more realistic terrains.
  k#: smoothing factor. Minumum 0.0 (extreme). Maxumum 1.0 (no smoothing).
  End Rem
  Method smoothen(k#)
 
    Local x, z
    Local y#, y2#

    For x = 1 To Width - 1
      For z = 0 To Height - 1
	    y# = Map#[x, z]
	    y2# = Map#[(x - 1), z]
	    Map#[x, z] = y2# * (1 - k#) + y# * k#
      Next
    Next

    For x = (Width - 3) To 0 Step -1
      For z = 0 To Height - 1
	    y# = Map#[x, z]
	    y2# = Map#[(x + 1), z]
	    Map#[x, z] = y2# * (1 - k#) + y# * k#
      Next
    Next

    For x = 0 To Width - 1
      For z = 1 To Height - 1
	    y# = Map#[x, z]
	    y2# = Map#[x, (z - 1)]
	    Map#[x, z] = y2# * (1 - k#) + y# * k#
      Next
    Next

    For x = 0 To Width - 1
      For z = (Height - 3) To 0 Step -1
	    y# = Map#[x, z]
	    y2# = Map#[x, (z + 1)]
	    Map#[x, z] = y2# * (1 - k#) + y# * k#
      Next
    Next

  End Method

  Rem flatten(value#)
  'Flatten' the map values.
  value#: flattening amount. 1 = no flattening. Minimum 1. Maximum (suggested) 4.
  End Rem
  Method flatten(value#)

    Local x, z
    Local y#
  
    For x = 0 To Width - 1
      For z = 0 To Height - 1
        y# = Map#[x, z] ^ value#
        Map#[x, z] = y#
      Next
    Next

  End Method
  
  Rem makeCoast(extent#, depth#)
  Create a crude coastline effect. For best results, smoothing should be applied 
  after this process.
  extent#: extent of coastilisation. 0.0 to 1.0 = None To 100%.
  depth#: depth. 0.0 To 1.0 = Shallow to deep.*/
  End Rem
  Method makeCoast(extent#, depth#)

    Local x, z
	Local y#, e#, d#, minv# = 10^38, maxv# = 10^-38
	
    For x = 0 To Width - 1
      For z = 0 To Height - 1
        y# = Map#[x, z]
	    If y# < minv#
	      minv# = y#
	    Else
	      If y# > maxv#
		    maxv# = y#
		  EndIf
	    EndIf
	  Next
	Next

	e# = (maxv# - minv#) * extent#
	d# = (maxv# - minv#) * depth#
	
    For x = 0 To Width - 1
      For z = 0 To Height - 1
 	    y# = Map#[x, z]
	    If y# <= (minv# + e#)
	      Map#[x, z] = y# - d#
	    EndIf
	  Next
	Next   
   
    normalise()

  End Method

  Rem exportToPNG(url$ = "test.png", clvl = 9)
    Save the map to a greyscale PNG file.
    url$: target file. 
    clvl: Compression level: 0 to 9: low to high. Higher = slower.
  End Rem
  Method exportPNG(url$ = "test.png", clvl = 9)

    Local result = False
    Local pmap:TPixmap = CreatePixmap(Width, Height, PF_RGBA8888)

    renderToPixmap(pmap)
    result = SavePixmapPNG(pmap, url$, clvl)

    pmap = Null
    FlushMem

    If result 
      Print url$ + " saved." 
    Else
      Print url$ + " could NOT be saved."
    EndIf

  End Method

  Rem importFromPNG(url$)
    Import from a PNG file.
    url$: source file. Only the blue component data is used.
  End Rem
  Method importPNG(url$)

    Local x, z, c

    Local pmap:TPixmap = LoadPixmapPNG(url$)

    If pmap = Null
      Print url$ + " could NOT be loaded."
      FlushMem
      Return
    Else
      Print url$ + " loaded."       
    EndIf

    resize(pmap.Width, pmap.Height)
   
    For x = 0 To Width - 1
      For z = 0 To Height - 1
        c = ReadPixel(pmap, x, z)
        c :| c Shr 24
        Map#[x, z] = $000000 | c
      Next
    Next

    normalise()

    pmap = Null
    FlushMem

  End Method

  Rem exportToX3D(url$ = "test.x3d", tpl$ = "", hmulti# = 10.0, xspacing# = 100.0, zspacing# = 100.0, prec = 4)
  Reads an X3d V3.0 file (used as a template) and generates a new X3d file 
  incorporating the original file and "ElevationGrid" data generated from the map data.
  url$: Target file name. Any existing file with the same name will be REPLACED.
  templ$ = source X3d file name: the "ElevationGrid" tag must be empty and start 
  on a new line ie: "<ElevationGrid />" without the quotes. If the template file is 
  not found then internal X3d code is used.
  hmulti# = height multiplyer: higher values will produce higher/steeper terrain elements.
  xSpacing# = corresponds to the x3d ElevationGrid xSpacing property.
  zSpacing# = corresponds to the x3d ElevationGrid zSpacing property.
  prec = precision (number of digits) for the exported height values. 1 to 8. eg 4 = 1.34 or 12.2 etc
  End Rem
  Method exportX3D(url$ = "test.x3d", tpl$ = "", hmulti# = 10.0, xspacing# = 100.0, zspacing# = 100.0, prec = 4)

    Local pre$ = ""
    Local post$ = ""
    Local tag$ = "<ElevationGrid ccw='true' "
    tag$ :+ "creaseAngle='0.76' "
    tag$ :+ "solid='false' "
    tag$ :+ "colorPerVertex='true' "
    tag$ :+ "normalPerVertex='true' "
    tag$ :+ "xDimension='" + Width + "' "
    tag$ :+ "xSpacing='" + xspacing# + "' "
    tag$ :+ "zDimension='" + Height + "' "
    tag$ :+ "zSpacing='" + zspacing# + "' height='~r"

    Local templ:TStream = OpenFile(tpl$)
    If Not templ
      Print "Template file " + tpl$ + " could NOT be opened. Using internal template..."
      pre$ = "<?xml version='1.0'?>~r"
      pre$ :+ "<!DOCTYPE X3D Public 'ISO//Web3D//DTD X3D 3.0//EN' 'http://www.web3d.org/specifications/x3d-3.0.dtd'>~r"
      pre$ :+ "<X3D profile='Immersive' xmlns:xsd='http://www.w3.org/2001/XMLSchema-instance' xsd:noNamespaceSchemaLocation='http://www.web3d.org/specifications/x3d-3.0.xsd'>~r"
      pre$ :+ "<head><meta name='generator' content='Heightmap Toolbox V0.1'/></head>~r"
      pre$ :+ "<Scene><NavigationInfo Type='~qFLY~q ~qANY~q' speed='3.0' /><Viewpoint description='VP1' orientation='0 0 0 0' position='20 20 100' centerOfRotation='90 0 0'/>~r"
      pre$ :+ "<Shape><Appearance><Material diffuseColor='0.4 0.4 0.1' emissiveColor='0.3 0.3 0.3' /></Appearance>~r"
      post$ = "~r</Shape></Scene></X3D>"
    Else 
      Print "Using template file " + tpl$ + "."
    EndIf

    Local result = CreateFile(url$)

    If Not result  
      Print url$ + " could NOT be created."
      Return
    EndIf

    Local x, z, spos
    Local n$, t$, l$

    Local file:TStream = OpenFile(url$)

    Try

    If templ
      t$ = "<ElevationGrid />"
      l$ = ReadLine (templ)
      spos = Instr(l$, t$)
      While Not Eof(templ) 
        If spos > 0 Then Exit
        WriteLine file, l$
        l$ = ReadLine (templ)
        spos = Instr(l$, t$)
      Wend
      
    Else 
      WriteString(file, pre$)
    EndIf

    WriteString(file, tag$)

    For x = 0 To Width - 1
      For z = 0 To Height - 1
        n$ = String.FromFloat(Map#[x, z] * hmulti#)
        n$ = n$[..prec] + " "
        WriteString(file, n$)
      Next
    Next

    WriteString(file, "'/>")

    If templ
      While Not Eof(templ)
        l$ = ReadLine (templ)
        WriteLine file, l$
      Wend
    Else
      WriteString(file, post$)
    EndIf

    CloseFile(file)
    If templ CloseFile(templ)

    Catch ex:Object
      Print "File IO error."
      Return
    End Try

    Print "X3D file " + url$ + " saved."

  End Method

  Rem exportBin(url$ = "test.bin")
    Save heightmap data as a binary file. 
    File Structure:
    	1 * Int (Width)
    	1 * Int (Height) 
    	Width * Height * Floats (height values) 
    url$: target file name. 
  End Rem
  Method exportBin(url$ = "test.bin")

    Local result = False

    result = CreateFile(url$)

    If Not result  
      Print url$ + " could NOT be created."
      Return
    EndIf

    Local x, z

    Try

    Local file:TStream = OpenFile(url$)

    WriteInt(file, Width)
    WriteInt(file, Height)

    For x = 0 To Width - 1
      For z = 0 To Height - 1
        WriteFloat(file, Map#[x, z])
      Next
    Next
    
    CloseFile(file)

    Catch ex:Object
      Print "File IO error."
      Return
    End Try

    Print "Binary file " + url$ + " created."

  End Method

  Rem importBin(url$ = "test.bin")
    Load heightmap data from a binary file that was previously saved using exportBin. 
    url$: source file name. 
  End Rem
  Method importBin(url$ = "test.bin")

    Local x, z

    Local file = False

    file = OpenFile(url$)

    If Not file  
      Print url$ + " could NOT be opened."
      Return
    EndIf

    Try

    resize(Readint(file), Readint(file))

    For x = 0 To Width - 1
      For z = 0 To Height - 1
        Map#[x, z] = ReadFloat(file)
      Next
    Next

    Catch ex:Object
      Print "File IO error."
      Return
    End Try

    Print "Binary file " + url$ + " loaded."

  End Method

  Rem blendMapsAdd(src1:THeightMap, src2:THeightMap, outp:THeightMap)
  Blends two maps using an additive process.
  src1: source THeightMap instance #1.
  src2: source THeightMap instance #2.
  outp: output THeightMap instance into which the results will be stored.
  End Rem
  Function blendMapsAdd(src1:THeightMap, src2:THeightMap, outp:THeightMap)

    Local x, z
    Local y1#, y2#
    
    For x = 0 To outp.Width - 1
      For z = 0 To outp.Height - 1

         If (x < src1.Width) And (z < src1.Height)
           y1# = src1.Map#[x, z]
         Else
           y1# = 0.0
         EndIf  
         If (x < src2.Width) And (z < src2.Height)
           y2# = src2.Map#[x, z]
         Else
           y2# = 0.0
         EndIf  

         outp.Map#[x, z] = y1# + y2#

      Next
    Next
    
    outp.normalise()

  End Function

  Rem blendWithMapAdd(src:THeightMap)
  Blends the current THeightMap instance with another THeightMap instance using a 
  an additive process.
  src: THeightMap to blend with.
  End Rem
  Method blendWithMapAdd(src:THeightMap)

    blendMapsAdd(Self, src, Self)

  End Method

  Rem blendMapsSub(src1:THeightMap, src2:THeightMap, outp:THeightMap)
  Blends two maps using a subtractive process.
  src1: source THeightMap instance #1.
  src2: source THeightMap instance #2.
  outp: output THeightMap instance into which the results will be stored.
  End Rem
  Function blendMapsSub(src1:THeightMap, src2:THeightMap, outp:THeightMap)

    Local x, z
    Local y1#, y2#
    
    For x = 0 To outp.Width - 1
      For z = 0 To outp.Height - 1

         If (x < src1.Width) And (z < src1.Height)
           y1# = src1.Map#[x, z]
         Else
           y1# = 0.0
         EndIf  
         If (x < src2.Width) And (z < src2.Height)
           y2# = src2.Map#[x, z]
         Else
           y2# = 0.0
         EndIf  

         outp.Map#[x, z] = y1# - y2#

      Next
    Next
    
    outp.normalise()

  End Function

  Rem blendWithMapSub(src:THeightMap)
  Blends the current THeightMap instance with another THeightMap instance using a 
  a subtractive process.
  src: THeightMap to blend with.
  End Rem
  Method blendWithMapSub(src:THeightMap)

    blendMapsSub(Self, src, Self)

  End Method

  Rem blendMapsMul(src1:THeightMap, src2:THeightMap, outp:THeightMap)
  Blends two maps using an multiplicative process.
  src1: source THeightMap instance #1.
  src2: source THeightMap instance #2.
  outp: output THeightMap instance into which the results will be stored.
  End Rem
  Function blendMapsMul(src1:THeightMap, src2:THeightMap, outp:THeightMap)

    Local x, z
    Local y1#, y2#
    
    For x = 0 To outp.Width - 1
      For z = 0 To outp.Height - 1

         If (x < src1.Width) And (z < src1.Height)
           y1# = src1.Map#[x, z]
         Else
           y1# = 0.0
         EndIf  
         If (x < src2.Width) And (z < src2.Height)
           y2# = src2.Map#[x, z]
         Else
           y2# = 0.0
         EndIf  

         outp.Map#[x, z] = y1# * y2#

      Next
    Next
    
    outp.normalise()

  End Function

  Rem blendWithMapMul(src:THeightMap)
  Blends the current THeightMap instance with another THeightMap instance using a 
  an multiplicative process.
  src: THeightMap to blend with.
  End Rem
  Method blendWithMapMul(src:THeightMap)

    blendMapsMul(Self, src, Self)

  End Method

  Rem blendMapsDiv(src1:THeightMap, src2:THeightMap, outp:THeightMap)
  Blends two maps using a division process.
  src1: source THeightMap instance #1.
  src2: source THeightMap instance #2.
  outp: output THeightMap instance into which the results will be stored.
  End Rem
  Function blendMapsDiv(src1:THeightMap, src2:THeightMap, outp:THeightMap)

    Local x, z
    Local y1#, y2#
    
    For x = 0 To outp.Width - 1
      For z = 0 To outp.Height - 1

         If (x < src1.Width) And (z < src1.Height)
           y1# = src1.Map#[x, z]
         Else
           y1# = 0.0
         EndIf  
         If (x < src2.Width) And (z < src2.Height)
           y2# = src2.Map#[x, z]
         Else
           y2# = 0.0
         EndIf  

         outp.Map#[x, z] = y1# / y2#

      Next
    Next
    
    outp.normalise()

  End Function

  Rem blendWithMapDiv(src:THeightMap)
  Blends the current THeightMap instance with another THeightMap instance using a 
  an division process.
  src: THeightMap to blend with.
  End Rem
  Method blendWithMapDiv(src:THeightMap)

    blendMapsDiv(Self, src, Self)

  End Method

  Rem blendMapsHi(src1:THeightMap, src2:THeightMap, outp:THeightMap)
  Blends two maps, favouring the highest values.
  src1: source THeightMap instance #1.
  src2: source THeightMap instance #2.
  outp: output THeightMap instance into which the results will be stored.
  End Rem
  Function blendMapsHi(src1:THeightMap, src2:THeightMap, outp:THeightMap)

    Local x, z
    Local y1#, y2#
    
    For x = 0 To outp.Width - 1
      For z = 0 To outp.Height - 1

         If (x < src1.Width) And (z < src1.Height)
           y1# = src1.Map#[x, z]
         Else
           y1# = 0.0
         EndIf  
         If (x < src2.Width) And (z < src2.Height)
           y2# = src2.Map#[x, z]
         Else
           y2# = 0.0
         EndIf  

         If y1# > y2# Then
           outp.Map#[x, z] = y1#
         Else
           outp.Map#[x, z] = y2#
         EndIf

      Next
    Next
    
    outp.normalise()

  End Function

  Rem blendWithMapHi(src:THeightMap)
  Blends the current THeightMap instance with another THeightMap instance, 
  favouring the highest values.
  src: THeightMap to blend with.
  End Rem
  Method blendWithMapHi(src:THeightMap)

    blendMapsHi(Self, src, Self)

  End Method

  Rem blendMapsHi(src1:THeightMap, src2:THeightMap, outp:THeightMap)
  Blends two maps, favouring the lowest values.
  src1: source THeightMap instance #1.
  src2: source THeightMap instance #2.
  outp: output THeightMap instance into which the results will be stored.
  End Rem
  Function blendMapsLo(src1:THeightMap, src2:THeightMap, outp:THeightMap)

    Local x, z
    Local y1#, y2#
    
    For x = 0 To outp.Width - 1
      For z = 0 To outp.Height - 1

         If (x < src1.Width) And (z < src1.Height)
           y1# = src1.Map#[x, z]
         Else
           y1# = 0.0
         EndIf  
         If (x < src2.Width) And (z < src2.Height)
           y2# = src2.Map#[x, z]
         Else
           y2# = 0.0
         EndIf  

         If y1# < y2# Then
           outp.Map#[x, z] = y1#
         Else
           outp.Map#[x, z] = y2#
         EndIf

      Next
    Next
    
    outp.normalise()

  End Function

  Rem blendWithMapLo(src:THeightMap)
  Blends the current THeightMap instance with another THeightMap instance, 
  favouring the lowest values.
  src: THeightMap to blend with.
  End Rem
  Method blendWithMapLo(src:THeightMap)

    blendMapsLo(Self, src, Self)

  End Method

  Rem blendMapsRep(src1:THeightMap, src2:THeightMap, outp:THeightMap)
  Blends two maps, replacing outp's values with src1's values. ie copy src1 to outp.
  src1: source THeightMap instance #1.
  src2: source THeightMap instance #2.
  outp: output THeightMap instance into which the results will be stored.
  End Rem
  Function blendMapsRep(src1:THeightMap, src2:THeightMap, outp:THeightMap)

    Local x, z
    Local y1#, y2#
    
    For x = 0 To outp.Width - 1
      For z = 0 To outp.Height - 1

         If (x < src1.Width) And (z < src1.Height)
           y1# = src1.Map#[x, z]
         Else
           y1# = 0.0
         EndIf  
         If (x < src2.Width) And (z < src2.Height)
           y2# = src2.Map#[x, z]
         Else
           y2# = 0.0
         EndIf  

         outp.Map#[x, z] = y1#

      Next
    Next
    
    outp.normalise()

  End Function

  Rem blendWithMapRep(src:THeightMap)
  Replaces the current THeightMap values with those of another THeightMap instance. 
  src: THeightMap to blend with.
  End Rem
  Method blendWithMapRep(src:THeightMap)

    blendMapsRep(Self, src, Self)

  End Method

  Rem renderToImage(img:TImage, offx = 0, offz = 0)
  Render the map to a BlitzMax image object.
  img: Destination TImage object. Must be the same dimensions as the map.
  offx: x offset (pixels) into img. >=0 and <=img width.
  offz: z offset (pixels) into img. >=0 and <=img height.
  End Rem
  Method renderToImage(img:TImage, offx = 0, offz = 0)

    Local pm = LockImage(img)
    renderToPixmap(pm, offx, offz)
    UnlockImage(img)

  End Method

  Rem renderToPixmap(pm:TPixmap, offx = 0, offz = 0)
  Render the map to a BlitzMax pixmap object.
  pm: Destination TPixmap object. Must be the same dimensions as the 
  map. The pixmap format should be PF_RGBA8888
  offx: x offset (pixels) into pm. >=0 and <=pm width.
  offz: z offset (pixels) into pm. >=0 and <=pm height.
  End Rem
  Method renderToPixmap(pm:TPixmap, offx = 0, offz = 0)

    If Not ((offx >= 0) And (offx < pm.Width)) Return
    If Not ((offz >= 0) And (offz < pm.Height)) Return

    Local x, z, r, g, b, a = $FF000000

    For x = 0 To Width - 1
      For z = 0 To Height - 1
		If ((offx + x) < pm.Width) And ((offz + z) < pm.Height)
          r = Floor(Map#[x, z] * 255)
          g = r
          b = r
          WritePixel(pm, (offx + x), (offz + z), a Shl 24 | r Shl 16 | g Shl 8 | b Shl 0)
        EndIf
      Next
    Next

  End Method

  Rem renderToBackBuffer(offx = 0, offz = 0)
  Render the map directly to the backbuffer.
  offx: x offset (pixels) backbuffer. >=0 and <=screen width.
  offz: z offset (pixels) backbuffer. >=0 and <=screen height.
  TODO; Validate offx and offz.
  End Rem
  Method renderToBackBuffer(offx = 0, offz = 0)
    
    Local gw = GraphicsWidth()
    Local gh = GraphicsHeight()

    If Not ((offx >= 0) And (offx < gw)) Return
    If Not ((offz >= 0) And (offz < gh)) Return

    Local x, z, c

    For x = 0 To Width - 1
      For z = 0 To Height - 1
        If ((offx + x) < gw) And ((offz + z) < gh)
          c = Floor(Map#[x, z] * 255)
          SetColor c, c, c
          Plot (offx + x), (offz + z)
        EndIf
      Next
    Next

  End Method

End Type





'==================================
' Cut and paste this and save it 
' as hmaptest.bmx or whatever.
'==================================



Rem

Height Map Test
V0.1.0 BETA

By Shane Raffa
May 2005

PURPOSE

	A crude, non-gui heightmap editing program to demonstrate usage 
	of the THeightMap class. (See hmap010.bmx)

TEST SYSTEM

	Athlon XP 1600+, 512MB, GeForce 3 Ti200 (64MB)
	Windows XP, BlitzMax

End Rem

Strict

Import "hmap010.bmx"

Type THMapTest Extends THeightMap

  Field Img1:TImage

  Field ScrnX
  Field ScrnZ

  Method create(w, h, sx = 0, sz = 0)

    ScrnX = sx
    ScrnZ = sz
    resize(w, h)
    Img1:TImage = CreateImage(Width, Height, 1, DYNAMICIMAGE)
    generateMap()

  End Method
  
  Method destroy()

    Img1 = Null

  End Method

  Method generateMap()

    Local n = Rand(0, 5)

    DrawText "Generating Map... ", ScrnX, ScrnZ
    Flip

    Select n
      Case 0
        createPerlinMap(MilliSecs(), 0.25, 1.5)
      Case 1
        createMPDMap(MilliSecs(), 0.5)
      Case 2
        createHillMap(MilliSecs(), 1000, 30)
      Case 3
        createParticleSMap(MilliSecs(), 1000, 1000)
        smoothen(0.25)
      Case 4
        createParticleRMap(MilliSecs(), 1000, 1000)
        smoothen(0.25)
      Case 5
        createFaultMap(MilliSecs(), 500)
        smoothen(0.25)
    End Select
    
    renderToImage(Img1, 0, 0)

    render()
    Flip

  End Method

  Method render()

    DrawImage(Img1, ScrnX, ScrnZ)
    
  End Method

End Type


Graphics 800, 600

SetBlend(SOLIDBLEND)
SetLineWidth(2.0)

Global Menu = True
Global Active = 0

Global HMap:THMapTest[] = [New THMapTest, New THMapTest, New THMapTest]

HMap[0].create(256, 256, 0, 0)
HMap[1].create(256, 256, HMap[0].Width, 0)
HMap[2].create(256, 256, HMap[0].Width + HMap[1].Width, 0)

While Not KeyHit(KEY_ESCAPE)

  Cls

  For Local i:THMapTest = EachIn HMap
    i.render()
  Next

  If MouseDown(1)
    Local c = 0
    For Local i:THMapTest = EachIn HMap
      If (MouseX() >= i.ScrnX) And (MouseX() < (i.ScrnX + i.Width))
        If (MouseY() >= i.ScrnZ) And (MouseY() < (i.ScrnZ + i.Height))
          Active = c
        EndIf
      EndIf
      c :+ 1
    Next
  EndIf

  If Menu showMenu()

  drawActiveBox()

  processKeys()
  FlushKeys()

  Flip

Wend

For Local i:THMapTest = EachIn HMap
  i.destroy()
Next

FlushMem

EndGraphics

End

Function drawActiveBox()

  SetColor(0, 255, 0)
  DrawLine HMap[Active].ScrnX, HMap[Active].ScrnZ, HMap[Active].ScrnX + HMap[Active].Width - 1, HMap[Active].ScrnZ
  DrawLine HMap[Active].ScrnX + HMap[Active].Width - 1, HMap[Active].ScrnZ, HMap[Active].ScrnX + HMap[Active].Width - 1, HMap[Active].ScrnZ + HMap[Active].Height - 1
  DrawLine HMap[Active].ScrnX, HMap[Active].ScrnZ + HMap[Active].Height - 1, HMap[Active].ScrnX + HMap[Active].Width - 1, HMap[Active].ScrnZ + HMap[Active].Height - 1
  DrawLine HMap[Active].ScrnX, HMap[Active].ScrnZ, HMap[Active].ScrnX, HMap[Active].ScrnZ + HMap[Active].Height - 1
  SetColor(255, 255, 255)

End Function

Function showMenu()

  DrawText "SELECT", 5, 300
  DrawText "------", 5, 315
  DrawText "{Left Click} - Select", 5, 330
  DrawText "UP ARROW - Next", 5, 345
  DrawText "RIGHT ARROW - Next", 5, 360
  DrawText "DOWN ARROW - Previous", 5, 375
  DrawText "LEFT ARROW - Previous", 5, 390
  DrawText "ESC - Exit", 5, 405
  DrawText "TAB - Toggle Menu", 5, 420

  DrawText "CREATE", 200, 300
  DrawText "------", 200, 315
  DrawText "1 - Random", 200, 330
  DrawText "2 - Perlin", 200, 345
  DrawText "3 - Midpoint Disp.", 200, 360
  DrawText "4 - Hill", 200, 375
  DrawText "5 - Sticky Particle", 200, 390
  DrawText "6 - Rolling Particle", 200, 405
  DrawText "7 - Fault", 200, 420
  DrawText "0 - Clear", 200, 435

  DrawText "PROCESS", 400, 300
  DrawText "-------", 400, 315
  DrawText "SPACE - Smoothen", 400, 330
  DrawText "f - Flatten", 400, 345
  DrawText "c - Coast", 400, 360
  DrawText "a - Blend Add", 400, 375
  DrawText "s - Blend Subtract", 400, 390
  DrawText "m - Blend Multiply", 400, 405
  DrawText "d - Blend Divide", 400, 420
  DrawText "h - Blend Highest", 400, 435
  DrawText "l - Blend Lowest", 400, 450
  DrawText "r - Blend Replace", 400, 465

  DrawText "IMPORT/EXPORT", 600, 300
  DrawText "-------------", 600, 315
  DrawText "F1 - Export Binary", 600, 330
  DrawText "F2 - Export PNG", 600, 345
  DrawText "F3 - Export X3D", 600, 360
  DrawText "F5 - Import Binary", 600, 390
  DrawText "F6 - Import PNG", 600, 405
  DrawText "F10 - Export...", 600, 435
  DrawText "F12 - Import...", 600, 450

End Function

Function processKeys()
  
  If KeyDown(KEY_0)
    DrawText "Clearing... ", HMap[Active].ScrnX, HMap[Active].ScrnZ
    Flip
    HMap[Active].fill(0.0)
    HMap[Active].renderToImage(HMap[Active].Img1, 0, 0)
  End If
  If KeyDown(KEY_1)
    DrawText "Generating Random... ", HMap[Active].ScrnX, HMap[Active].ScrnZ
    Flip
    HMap[Active].createRandomMap(MilliSecs())
    HMap[Active].renderToImage(HMap[Active].Img1, 0, 0)
  End If
  If KeyDown(KEY_2)
    DrawText "Generating Perlin... ", HMap[Active].ScrnX, HMap[Active].ScrnZ
    Flip
    HMap[Active].createPerlinMap(MilliSecs(), 0.25, 1.5)
    HMap[Active].renderToImage(HMap[Active].Img1, 0, 0)
  End If
  If KeyDown(KEY_3)
    DrawText "Generating MPD... ", HMap[Active].ScrnX, HMap[Active].ScrnZ
    Flip
    HMap[Active].createMPDMap(MilliSecs(), 0.5)
    HMap[Active].renderToImage(HMap[Active].Img1, 0, 0)
  End If
  If KeyDown(KEY_4)
    DrawText "Generating Hill... ", HMap[Active].ScrnX, HMap[Active].ScrnZ
    Flip
    HMap[Active].createHillMap(MilliSecs(), 1000, 30)
    HMap[Active].renderToImage(HMap[Active].Img1, 0, 0)
  End If
  If KeyDown(KEY_5)
    DrawText "Generating S Particle... ", HMap[Active].ScrnX, HMap[Active].ScrnZ
    Flip
    HMap[Active].createParticleSMap(MilliSecs(), 1000, 1000)
    HMap[Active].renderToImage(HMap[Active].Img1, 0, 0)
  End If
  If KeyDown(KEY_6)
    DrawText "Generating R Particle... ", HMap[Active].ScrnX, HMap[Active].ScrnZ
    Flip
    HMap[Active].createParticleRMap(MilliSecs(), 1000, 1000)
    HMap[Active].renderToImage(HMap[Active].Img1, 0, 0)
  End If
  If KeyDown(KEY_7)
    DrawText "Generating Fault... ", HMap[Active].ScrnX, HMap[Active].ScrnZ
    Flip
    HMap[Active].createFaultMap(MilliSecs(), 500)
    HMap[Active].renderToImage(HMap[Active].Img1, 0, 0)
  End If

  If KeyDown(KEY_UP) Or KeyDown(KEY_RIGHT)
    Active :+ 1
    If Active >= HMap.length Active = 0
  End If

  If KeyDown(KEY_DOWN) Or KeyDown(KEY_LEFT)
    Active :- 1
    If Active < 0 Active = HMap.length - 1
  End If

  If KeyDown(KEY_TAB) 
    Menu = Not Menu
  End If

  If KeyDown(KEY_SPACE)
    DrawText "Smoothing... ", HMap[Active].ScrnX, HMap[Active].ScrnZ
    Flip
    HMap[Active].smoothen(0.75)
    HMap[Active].renderToImage(HMap[Active].Img1, 0, 0)
  EndIf

  If KeyDown(KEY_F)
    DrawText "Flattening... ", HMap[Active].ScrnX, HMap[Active].ScrnZ
    Flip
    HMap[Active].flatten(2)
    HMap[Active].renderToImage(HMap[Active].Img1, 0, 0)
  EndIf

  If KeyDown(KEY_C)
    DrawText "Making Coast... ", HMap[Active].ScrnX, HMap[Active].ScrnZ
    Flip
    HMap[Active].makeCoast(0.5, 0.1)
    HMap[Active].renderToImage(HMap[Active].Img1, 0, 0)
  EndIf

  If KeyDown(KEY_A)
    DrawText "Blending (Add)... ", HMap[Active].ScrnX, HMap[Active].ScrnZ
    Flip
    Select Active
      Case 0
        THeightMap.blendMapsAdd(HMap[1], HMap[2], HMap[Active])
      Case 1
        THeightMap.blendMapsAdd(HMap[0], HMap[2], HMap[Active])
      Case 2
        THeightMap.blendMapsAdd(HMap[0], HMap[1], HMap[Active])
      End Select
    HMap[Active].renderToImage(HMap[Active].Img1, 0, 0)
  EndIf

  If KeyDown(KEY_S)
    DrawText "Blending (Subtract)... ", HMap[Active].ScrnX, HMap[Active].ScrnZ
    Flip
    Select Active
      Case 0
        THeightMap.blendMapsSub(HMap[1], HMap[2], HMap[Active])
      Case 1
        THeightMap.blendMapsSub(HMap[0], HMap[2], HMap[Active])
      Case 2
        THeightMap.blendMapsSub(HMap[0], HMap[1], HMap[Active])
      End Select
    HMap[Active].renderToImage(HMap[Active].Img1, 0, 0)
  EndIf
  If KeyDown(KEY_M)
    DrawText "Blending (Multiply)... ", HMap[Active].ScrnX, HMap[Active].ScrnZ
    Flip
    Select Active
      Case 0
        THeightMap.blendMapsMul(HMap[1], HMap[2], HMap[Active])
      Case 1
        THeightMap.blendMapsMul(HMap[0], HMap[2], HMap[Active])
      Case 2
        THeightMap.blendMapsMul(HMap[0], HMap[1], HMap[Active])
      End Select
    HMap[Active].renderToImage(HMap[Active].Img1, 0, 0)
  EndIf
  If KeyDown(KEY_D)
    DrawText "Blending (Divide)... ", HMap[Active].ScrnX, HMap[Active].ScrnZ
    Flip
    Select Active
      Case 0
        THeightMap.blendMapsDiv(HMap[1], HMap[2], HMap[Active])
      Case 1
        THeightMap.blendMapsDiv(HMap[0], HMap[2], HMap[Active])
      Case 2
        THeightMap.blendMapsDiv(HMap[0], HMap[1], HMap[Active])
      End Select
    HMap[Active].renderToImage(HMap[Active].Img1, 0, 0)
  EndIf
  If KeyDown(KEY_H)
    DrawText "Blending (Highest)... ", HMap[Active].ScrnX, HMap[Active].ScrnZ
    Flip
    Select Active
      Case 0
        THeightMap.blendMapsHi(HMap[1], HMap[2], HMap[Active])
      Case 1
        THeightMap.blendMapsHi(HMap[0], HMap[2], HMap[Active])
      Case 2
        THeightMap.blendMapsHi(HMap[0], HMap[1], HMap[Active])
      End Select
    HMap[Active].renderToImage(HMap[Active].Img1, 0, 0)
  EndIf
  If KeyDown(KEY_L)
    DrawText "Blending (Lowest)... ", HMap[Active].ScrnX, HMap[Active].ScrnZ
    Flip
    Select Active
      Case 0
        THeightMap.blendMapsLo(HMap[1], HMap[2], HMap[Active])
      Case 1
        THeightMap.blendMapsLo(HMap[0], HMap[2], HMap[Active])
      Case 2
        THeightMap.blendMapsLo(HMap[0], HMap[1], HMap[Active])
      End Select
    HMap[Active].renderToImage(HMap[Active].Img1, 0, 0)
  EndIf
  If KeyDown(KEY_R)
    DrawText "Blending (Replace)... ", HMap[Active].ScrnX, HMap[Active].ScrnZ
    Flip
    Select Active
      Case 0
        THeightMap.blendMapsRep(HMap[1], HMap[2], HMap[Active])
      Case 1
        THeightMap.blendMapsRep(HMap[0], HMap[2], HMap[Active])
      Case 2
        THeightMap.blendMapsRep(HMap[0], HMap[1], HMap[Active])
      End Select
    HMap[Active].renderToImage(HMap[Active].Img1, 0, 0)
  EndIf

  If KeyDown(KEY_F1)
    DrawText "Exporting test.bin... ", HMap[Active].ScrnX, HMap[Active].ScrnZ
    Flip
    HMap[Active].exportBin("test.bin")
  EndIf
  If KeyDown(KEY_F5)
    DrawText "Importing test.bin... ", HMap[Active].ScrnX, HMap[Active].ScrnZ
    Flip
    HMap[Active].importBin("test.bin")
    HMap[Active].renderToImage(HMap[Active].Img1, 0, 0)
  EndIf
  If KeyDown(KEY_F2)
    DrawText "Exporting test.png... ", HMap[Active].ScrnX, HMap[Active].ScrnZ
    Flip
    HMap[Active].exportPNG("test.png", 9)
  EndIf
  If KeyDown(KEY_F6)
    DrawText "Importing test.png... ", HMap[Active].ScrnX, HMap[Active].ScrnZ
    Flip
    HMap[Active].importPNG("test.png")
    HMap[Active].renderToImage(HMap[Active].Img1, 0, 0)
  EndIf
  If KeyDown(KEY_F3)
    DrawText "Exporting test.x3d... ", HMap[Active].ScrnX, HMap[Active].ScrnZ
    Flip
    HMap[Active].exportX3d("test.x3d", "templ01.x3d", 40.0, 2.0, 2.0)
  EndIf
  If KeyDown(KEY_F12)
    Local fn$ = RequestFile("Import file...", "Supported Files:png,bin;All Files:*", False)
    If Right$(fn$, 4) = ".png"
      DrawText "Importing PNG: " + fn$ + "... ", HMap[Active].ScrnX, HMap[Active].ScrnZ
      Flip
      HMap[Active].importPNG(fn$)
    Else
      If Right$(fn$, 4) = ".bin"
        DrawText "Importing Binary: " + fn$ + "... ", HMap[Active].ScrnX, HMap[Active].ScrnZ
        Flip
        HMap[Active].importBin(fn$)
      EndIf
    EndIf
    HMap[Active].renderToImage(HMap[Active].Img1, 0, 0)
  EndIf
  If KeyDown(KEY_F10)
    Local fn$ = RequestFile("Export file...", "Supported Files:png,bin,x3d", True)
    If Right$(fn$, 4) = ".png"
      DrawText "Exporting PNG: " + fn$ + "... ", HMap[Active].ScrnX, HMap[Active].ScrnZ
      Flip
      HMap[Active].exportPNG(fn$)
    Else
      If Right$(fn$, 4) = ".bin"
        DrawText "Exporting Binary: " + fn$ + "... ", HMap[Active].ScrnX, HMap[Active].ScrnZ
        Flip
        HMap[Active].exportBin(fn$)
      Else 
        If Right$(fn$, 4) = ".x3d"
          DrawText "Exporting X3D: " + fn$ + "... ", HMap[Active].ScrnX, HMap[Active].ScrnZ
          Flip
          HMap[Active].exportX3d(fn$, "templ01.x3d", 40.0, 2.0, 2.0)
        EndIf
      EndIf
    EndIf
  EndIf

End Function
