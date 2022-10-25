; ID: 1513
; Author: Sub_Zero
; Date: 2005-10-31 18:08:24
; Title: BlitzPDF
; Description: For creating PDF documents with blitz

;Declarations file for pdflib.dll v0.0.1.0

.lib "pdflib.dll"

PDF_add_bookmark%(p%,txt$,p%,o%):PDF_add_bookmark
PDF_add_launchlink(p%,llx#,lly#,urx#,ury#,fn$):PDF_add_launchlink
PDF_add_locallink(p%,llx#,lly#,urx#,ury#,page%,dest$):PDF_add_locallink
PDF_add_note(p%,llx#,lly#,urx#,ury#,c$,t$,i$,o$):PDF_add_note
PDF_add_pdflink(p%,llx#,lly#,urx#,ury#,fn$,p%,dest$):PDF_add_pdflink
PDF_add_thumbnail(p%,img%):PDF_add_thumbnail
PDF_add_weblink(p%,llx#,lly#,urx#,ury#,url$):PDF_add_weblink
PDF_arc(p%,x#,y#,r#,a#,b#):PDF_arc
PDF_arcn(p%,x#,y#,r#,a#,b#):PDF_arcn
PDF_attach_file(p%,llx#,lly#,urx#,ury#,fn$,desc$,auth$,mime$,icon$):PDF_attach_file
PDF_begin_page(p%,w#,h#):PDF_begin_page
PDF_begin_pattern(p%,w#,h#,xs#,ys#,pt%):PDF_begin_pattern
PDF_begin_template%(p%,w#,h#):PDF_begin_template
PDF_boot():PDF_boot
PDF_circle(p%,x#,y#,r#):PDF_circle
PDF_clip(p%):PDF_clip
PDF_close(p%):PDF_close
PDF_close_image(p%,img%):PDF_close_image
PDF_closepath(p%):PDF_closepath
PDF_closepath_stroke(p%):PDF_closepath_stroke
PDF_closepath_fill_stroke(p%):PDF_closepath_fill_stroke
PDF_concat(p%,a#,b#,c#,d#,e#,f#):PDF_concat
PDF_continue_text(p%,txt$):PDF_continue_text
PDF_continue_text2(p%,txt$,len%):PDF_continue_text2
PDF_curveto(p%,x1#,y1#,x2#,y2#,x3#,y3#):PDF_curveto
PDF_delete(p%):PDF_delete
PDF_endpath(p%):PDF_endpath
PDF_end_page(p%):PDF_end_page
PDF_end_pattern(p%):PDF_end_pattern
PDF_end_template(p%):PDF_end_template
PDF_fill(p%):PDF_fill
PDF_fill_stroke(p%):PDF_fill_stroke
PDF_findfont%(p%,font$,enc$,opt%):PDF_findfont
PDF_get_majorversion%():PDF_get_majorversion
PDF_get_minorversion%():PDF_get_minorversion
PDF_get_opaque%():PDF_get_opaque
PDF_get_parameter$(p%,key$,mod#):PDF_get_parameter
PDF_get_value#(p%,key$,mod#):PDF_get_value
PDF_initgraphics(p%):PDF_initgraphics
PDF_lineto(p%,x#,y#):PDF_lineto
PDF_makespotcolor(p%,sn$,len#):PDF_makespotcolor
PDF_moveto(p%,x#,y#):PDF_moveto
PDF_new%():PDF_new
PDF_open_ccitt%(p%,fn$,w%,h%,br%,k%,bl%):PDF_open_ccitt
PDF_open_file%(p%,fname$):PDF_open_file
PDF_open_image%(p%,t$,s$,d$,len%,w%,h%,comp%,bpc%,p$):PDF_open_image
PDF_open_image_file%(p%,t$,f$,sp$,intp%):PDF_open_image_file
;PDF_open_fp%(p%,fh%):PDF_open_fp				; C / C++ only :(
PDF_place_image(p%,ih%,x#,y#,s#):PDF_place_image
PDF_rect(p%,x#,y#,w#,h#):PDF_rect
PDF_restore(p%):PDF_restore
PDF_rotate(p%,phi#):PDF_rotate
PDF_save(p%):PDF_save
PDF_scale(p%,sx#,sy#):PDF_scale
PDF_setcolor(p%,t$,cs$,c1#,c2#,c3#,c4#):PDF_setcolor
PDF_setdash(p%,b#,w#):PDF_setdash
PDF_setflat(p%,fn#):PDF_setflat
PDF_setfont(p%,font%,size#):PDF_setfont
PDF_setlinecap(p%,lc%):PDF_setlinecap
PDF_setlinejoin(p%,lj%):PDF_setlinejoin
PDF_setlinewidth(p%,w#):PDF_setlinewidth
PDF_setmatrix(p%,a#,b#,c#,d#,e#,f#):PDF_setmatrix
PDF_setmiterlimit(p%,m#):PDF_setmiterlimit
;PDF_setpolydash(p%,darray#,len#):PDF_setpolydash		;Arrays not supported :(
PDF_set_border_color(p%,r#,g#,b#):PDF_set_border_color
PDF_set_border_dash(p%,b#,w#):PDF_set_border_dash
PDF_set_border_style(p%,style$,w#):PDF_set_border_style
PDF_set_info(p%,key$,str$):PDF_set_info
PDF_set_parameter(p%,key$,val$):PDF_set_parameter
PDF_set_text_pos(p%,x#,y#):PDF_set_text_pos
PDF_set_value(p%,key$,val#):PDF_set_value
PDF_show(p%,txt$):PDF_show
PDF_show2(p%,txt$,len%):PDF_show2
PDF_show_boxed%(p%,txt%,x#,y#,w#,h#,mode$,feat$):PDF_show_boxed
PDF_show_xy(p%,txt$,x#,y#):PDF_show_xy
PDF_show_xyz(p%,txt$,len%,x#,y#):PDF_show_xyz
PDF_shutdown():PDF_shutdown
PDF_skew(p%,a#,b#):PDF_skew
PDF_stringwidth#(p%,txt$,font%,size#):PDF_stringwidth
PDF_stringwidth2#(p%,txt$,len%,font%,size#):PDF_stringwidth2
PDF_stroke(p%):PDF_stroke
PDF_translate(p%,tx#,ty#):PDF_translate
