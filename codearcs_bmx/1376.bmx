; ID: 1376
; Author: Nilium
; Date: 2005-05-17 16:13:20
; Title: GlHelper Class
; Description: Provides an easy way to figure out if a card supports Gl extensions and what its limits are

Strict

Import Pub.OpenGL
Import Pub.Glew

Private

Global __glob_glHelp:Byte = 0

Public

' Gl Helper Globals
Type GlHelper
  ' Strings
  Global Ext$
  Global Vendor$
  Global Renderer$
  Global Version$
  
  ' Extensions
	Global GL_ARB_multitexture
	Global GLX_ARB_get_proc_address
	Global GL_ARB_transpose_matrix
	Global WGL_ARB_buffer_region
	Global GL_ARB_multisample
	Global GL_ARB_texture_env_add
	Global GL_ARB_texture_cube_map
	Global WGL_ARB_extensions_string
	Global WGL_ARB_pixel_format
	Global WGL_ARB_make_current_read
	Global WGL_ARB_pbuffer
	Global GL_ARB_texture_compression
	Global GL_ARB_texture_border_clamp
	Global GL_ARB_point_parameters
	Global GL_ARB_vertex_blend
	Global GL_ARB_matrix_palette
	Global GL_ARB_texture_env_combine
	Global GL_ARB_texture_env_crossbar
	Global GL_ARB_texture_env_dot3
	Global WGL_ARB_render_texture
	Global GL_ARB_texture_mirrored_repeat
	Global GL_ARB_depth_texture
	Global GL_ARB_shadow
	Global GL_ARB_shadow_ambient
	Global GL_ARB_window_pos
	Global GL_ARB_vertex_program
	Global GL_ARB_fragment_program
	Global GL_ARB_vertex_buffer_object
	Global GL_ARB_occlusion_query
	Global GL_ARB_shader_objects
	Global GL_ARB_vertex_shader
	Global GL_ARB_fragment_shader
	Global GL_ARB_shading_language_100
	Global GL_ARB_texture_non_power_of_two
	Global GL_ARB_point_sprite
	Global GL_ARB_fragment_program_shadow
	Global GL_ARB_draw_buffers
	Global GL_ARB_texture_rectangle
	Global GL_ARB_color_buffer_float
	Global GL_ARB_half_float_pixel
	Global GL_ARB_texture_float
	Global GL_ARB_pixel_buffer_object
	Global GL_EXT_abgr
	Global GL_EXT_blend_color
	Global GL_EXT_polygon_offset
	Global GL_EXT_texture
	Global GL_EXT_texture3D
	Global GL_SGIS_texture_filter4
	Global GL_EXT_subtexture
	Global GL_EXT_copy_texture
	Global GL_EXT_histogram
	Global GL_EXT_convolution
	Global GL_SGI_color_matrix
	Global GL_SGI_color_table
	Global GL_SGIS_pixel_texture
	Global GL_SGIX_pixel_texture
	Global GL_SGIS_texture4D
	Global GL_SGI_texture_color_table
	Global GL_EXT_cmyka
	Global GL_EXT_texture_object
	Global GL_SGIS_detail_texture
	Global GL_SGIS_sharpen_texture
	Global GL_EXT_packed_pixels
	Global GL_SGIS_texture_lod
	Global GL_SGIS_multisample
	Global GL_EXT_rescale_normal
	Global GLX_EXT_visual_info
	Global GL_EXT_vertex_array
	Global GL_EXT_misc_attribute
	Global GL_SGIS_generate_mipmap
	Global GL_SGIX_clipmap
	Global GL_SGIX_shadow
	Global GL_SGIS_texture_edge_clamp
	Global GL_SGIS_texture_border_clamp
	Global GL_EXT_blend_minmax
	Global GL_EXT_blend_subtract
	Global GL_EXT_blend_logic_op
	Global GLX_SGI_swap_control
	Global GLX_SGI_video_sync
	Global GLX_SGI_make_current_read
	Global GLX_SGIX_video_source
	Global GLX_EXT_visual_rating
	Global GL_SGIX_interlace
	Global GLX_EXT_import_context
	Global GLX_SGIX_fbconfig
	Global GLX_SGIX_pbuffer
	Global GL_SGIS_texture_select
	Global GL_SGIX_sprite
	Global GL_SGIX_texture_multi_buffer
	Global GL_EXT_point_parameters
	Global GL_SGIX_instruments
	Global GL_SGIX_texture_scale_bias
	Global GL_SGIX_framezoom
	Global GL_SGIX_tag_sample_buffer
	Global GL_SGIX_reference_plane
	Global GL_SGIX_flush_raster
	Global GLX_SGI_cushion
	Global GL_SGIX_depth_texture
	Global GL_SGIS_fog_function
	Global GL_SGIX_fog_offset
	Global GL_HP_image_transform
	Global GL_HP_convolution_border_modes
	Global GL_SGIX_texture_add_env
	Global GL_EXT_color_subtable
	Global GLU_EXT_object_space_tess
	Global GL_PGI_vertex_hints
	Global GL_PGI_misc_hints
	Global GL_EXT_paletted_texture
	Global GL_EXT_clip_volume_hint
	Global GL_SGIX_list_priority
	Global GL_SGIX_ir_instrument1
	Global GLX_SGIX_video_resize
	Global GL_SGIX_texture_lod_bias
	Global GLU_SGI_filter4_parameters
	Global GLX_SGIX_dm_buffer
	Global GL_SGIX_shadow_ambient
	Global GLX_SGIX_swap_group
	Global GLX_SGIX_swap_barrier
	Global GL_EXT_index_texture
	Global GL_EXT_index_material
	Global GL_EXT_index_func
	Global GL_EXT_index_array_formats
	Global GL_EXT_compiled_vertex_array
	Global GL_EXT_cull_vertex
	Global GLU_EXT_nurbs_tessellator
	Global GL_SGIX_ycrcb
	Global GL_EXT_fragment_lighting
	Global GL_IBM_rasterpos_clip
	Global GL_HP_texture_lighting
	Global GL_EXT_draw_range_elements
	Global GL_WIN_phong_shading
	Global GL_WIN_specular_fog
	Global GLX_SGIS_color_range
	Global GL_EXT_light_texture
	Global GL_SGIX_blend_alpha_minmax
	Global GL_EXT_scene_marker
	Global GL_SGIX_pixel_texture_bits
	Global GL_EXT_bgra
	Global GL_SGIX_async
	Global GL_SGIX_async_pixel
	Global GL_SGIX_async_histogram
	Global GL_INTEL_texture_scissor
	Global GL_INTEL_parallel_arrays
	Global GL_HP_occlusion_test
	Global GL_EXT_pixel_transform
	Global GL_EXT_pixel_transform_color_table
	Global GL_EXT_shared_texture_palette
	Global GLX_SGIS_blended_overlay
	Global GL_EXT_separate_specular_color
	Global GL_EXT_secondary_color
	Global GL_EXT_texture_env
	Global GL_EXT_texture_perturb_normal
	Global GL_EXT_multi_draw_arrays
	Global GL_EXT_fog_coord
	Global GL_REND_screen_coordinates
	Global GL_EXT_coordinate_frame
	Global GL_EXT_texture_env_combine
	Global GL_APPLE_specular_vector
	Global GL_APPLE_transform_hint
	Global GL_SUNX_constant_data
	Global GL_SUN_global_alpha
	Global GL_SUN_triangle_list
	Global GL_SUN_vertex
	Global WGL_EXT_display_color_table
	Global WGL_EXT_extensions_string
	Global WGL_EXT_make_current_read
	Global WGL_EXT_pixel_format
	Global WGL_EXT_pbuffer
	Global WGL_EXT_swap_control
	Global GL_EXT_blend_func_separate
	Global GL_INGR_color_clamp
	Global GL_INGR_interlace_read
	Global GL_EXT_stencil_wrap
	Global WGL_EXT_depth_float
	Global GL_EXT_422_pixels
	Global GL_NV_texgen_reflection
	Global GL_SGIX_texture_range
	Global GL_SUN_convolution_border_modes
	Global GLX_SUN_get_transparent_index
	Global GL_EXT_texture_env_add
	Global GL_EXT_texture_lod_bias
	Global GL_EXT_texture_filter_anisotropic
	Global GL_EXT_vertex_weighting
	Global GL_NV_light_max_exponent
	Global GL_NV_vertex_array_range
	Global GL_NV_register_combiners
	Global GL_NV_fog_distance
	Global GL_NV_texgen_emboss
	Global GL_NV_blend_square
	Global GL_NV_texture_env_combine4
	Global GL_MESA_resize_buffers
	Global GL_MESA_window_pos
	Global GL_EXT_texture_compression_s3tc
	Global GL_IBM_cull_vertex
	Global GL_IBM_multimode_draw_arrays
	Global GL_IBM_vertex_array_lists
	Global GL_3DFX_texture_compression_FXT1
	Global GL_3DFX_multisample
	Global GL_3DFX_tbuffer
	Global WGL_EXT_multisample
	Global GL_SGIX_vertex_preclip
	Global GL_SGIX_resample
	Global GL_SGIS_texture_color_mask
	Global GLX_MESA_copy_sub_buffer
	Global GLX_MESA_pixmap_colormap
	Global GLX_MESA_release_buffers
	Global GLX_MESA_set_3dfx_mode
	Global GL_EXT_texture_env_dot3
	Global GL_ATI_texture_mirror_once
	Global GL_NV_fence
	Global GL_IBM_static_data
	Global GL_IBM_texture_mirrored_repeat
	Global GL_NV_evaluators
	Global GL_NV_packed_depth_stencil
	Global GL_NV_register_combiners2
	Global GL_NV_texture_compression_vtc
	Global GL_NV_texture_rectangle
	Global GL_NV_texture_shader
	Global GL_NV_texture_shader2
	Global GL_NV_vertex_array_range2
	Global GL_NV_vertex_program
	Global GLX_SGIX_visual_select_group
	Global GL_SGIX_texture_coordinate_clamp
	Global GLX_OML_swap_method
	Global GLX_OML_sync_control
	Global GL_OML_interlace
	Global GL_OML_subsample
	Global GL_OML_resample
	Global WGL_OML_sync_control
	Global GL_NV_copy_depth_to_color
	Global GL_ATI_envmap_bumpmap
	Global GL_ATI_fragment_shader
	Global GL_ATI_pn_triangles
	Global GL_ATI_vertex_array_object
	Global GL_EXT_vertex_shader
	Global GL_ATI_vertex_streams
	Global WGL_I3D_digital_video_control
	Global WGL_I3D_gamma
	Global WGL_I3D_genlock
	Global WGL_I3D_image_buffer
	Global WGL_I3D_swap_frame_lock
	Global WGL_I3D_swap_frame_usage
	Global GL_ATI_element_array
	Global GL_SUN_mesh_array
	Global GL_SUN_slice_accum
	Global GL_NV_multisample_filter_hint
	Global GL_NV_depth_clamp
	Global GL_NV_occlusion_query
	Global GL_NV_point_sprite
	Global WGL_NV_render_depth_texture
	Global WGL_NV_render_texture_rectangle
	Global GL_NV_texture_shader3
	Global GL_NV_vertex_program1_1
	Global GL_EXT_shadow_funcs
	Global GL_EXT_stencil_two_side
	Global GL_ATI_text_fragment_shader
	Global GL_APPLE_client_storage
	Global GL_APPLE_element_array
	Global GL_APPLE_fence
	Global GL_APPLE_vertex_array_object
	Global GL_APPLE_vertex_array_range
	Global GL_APPLE_ycbcr_422
	Global GL_S3_s3tc
	Global GL_ATI_draw_buffers
	Global WGL_ATI_pixel_format_float
	Global GL_ATI_texture_env_combine3
	Global GL_ATI_texture_float
	Global GL_NV_float_buffer
	Global GL_NV_fragment_program
	Global GL_NV_half_float
	Global GL_NV_pixel_data_range
	Global GL_NV_primitive_restart
	Global GL_NV_texture_expand_normal
	Global GL_NV_vertex_program2
	Global GL_ATI_map_object_buffer
	Global GL_ATI_separate_stencil
	Global GL_ATI_vertex_attrib_array_object
	Global GL_OES_byte_coordinates
	Global GL_OES_fixed_point
	Global GL_OES_single_precision
	Global GL_OES_compressed_paletted_texture
	Global GL_OES_read_format
	Global GL_OES_query_matrix
	Global GL_EXT_depth_bounds_test
	Global GL_EXT_texture_mirror_clamp
	Global GL_EXT_blend_equation_separate
	Global GL_MESA_pack_invert
	Global GL_MESA_ycbcr_texture
	Global GL_EXT_pixel_buffer_object
	Global GL_NV_fragment_program_option
	Global GL_NV_fragment_program2
	Global GL_NV_vertex_program2_option
	Global GL_NV_vertex_program3
	Global GLX_SGIX_hyperpipe
	Global GLX_MESA_agp_offset
	Global GL_EXT_texture_compression_dxt1
	Global GL_EXT_framebuffer_object
	Global GL_GREMEDY_string_marker
  
  ' Get returns
  Global MaxTextureUnits
  Global MaxTextureSize
  Global MaxAttribStackDepth
  Global MaxClientAttribStackDepth
  Global MaxClipPlanes
  Global Max3DTextureSize
  Global MaxCubeMapTextureSize
  Global MaxLights
  Global MaxModelViewStackDepth
  Global MaxProjectionStackDepth
  Global MaxTextureLODBias
  Global MaxTextureStackDepth
  
  Method New()
    If __glob_glHelp = 0 Then
      Ext = String.FromCString( glGetString( GL_EXTENSIONS ) )
      Vendor = String.FromCString( glGetString( GL_VENDOR ) )
      Renderer = String.FromCString( glGetString( GL_RENDERER ) )
      Version = String.FromCString( glGetString( GL_VERSION ) )
      GL_ARB_multitexture = Ext.Find( "GL_ARB_multitexture" )>-1
			GLX_ARB_get_proc_address = Ext.Find( "GLX_ARB_get_proc_address" )>-1
			GL_ARB_transpose_matrix = Ext.Find( "GL_ARB_transpose_matrix" )>-1
			WGL_ARB_buffer_region = Ext.Find( "WGL_ARB_buffer_region" )>-1
			GL_ARB_multisample = Ext.Find( "GL_ARB_multisample" )>-1
			GL_ARB_texture_env_add = Ext.Find( "GL_ARB_texture_env_add" )>-1
			GL_ARB_texture_cube_map = Ext.Find( "GL_ARB_texture_cube_map" )>-1
			WGL_ARB_extensions_string = Ext.Find( "WGL_ARB_extensions_string" )>-1
			WGL_ARB_pixel_format = Ext.Find( "WGL_ARB_pixel_format" )>-1
			WGL_ARB_make_current_read = Ext.Find( "WGL_ARB_make_current_read" )>-1
			WGL_ARB_pbuffer = Ext.Find( "WGL_ARB_pbuffer" )>-1
			GL_ARB_texture_compression = Ext.Find( "GL_ARB_texture_compression" )>-1
			GL_ARB_texture_border_clamp = Ext.Find( "GL_ARB_texture_border_clamp" )>-1
			GL_ARB_point_parameters = Ext.Find( "GL_ARB_point_parameters" )>-1
			GL_ARB_vertex_blend = Ext.Find( "GL_ARB_vertex_blend" )>-1
			GL_ARB_matrix_palette = Ext.Find( "GL_ARB_matrix_palette" )>-1
			GL_ARB_texture_env_combine = Ext.Find( "GL_ARB_texture_env_combine" )>-1
			GL_ARB_texture_env_crossbar = Ext.Find( "GL_ARB_texture_env_crossbar" )>-1
			GL_ARB_texture_env_dot3 = Ext.Find( "GL_ARB_texture_env_dot3" )>-1
			WGL_ARB_render_texture = Ext.Find( "WGL_ARB_render_texture" )>-1
			GL_ARB_texture_mirrored_repeat = Ext.Find( "GL_ARB_texture_mirrored_repeat" )>-1
			GL_ARB_depth_texture = Ext.Find( "GL_ARB_depth_texture" )>-1
			GL_ARB_shadow = Ext.Find( "GL_ARB_shadow" )>-1
			GL_ARB_shadow_ambient = Ext.Find( "GL_ARB_shadow_ambient" )>-1
			GL_ARB_window_pos = Ext.Find( "GL_ARB_window_pos" )>-1
			GL_ARB_vertex_program = Ext.Find( "GL_ARB_vertex_program" )>-1
			GL_ARB_fragment_program = Ext.Find( "GL_ARB_fragment_program" )>-1
			GL_ARB_vertex_buffer_object = Ext.Find( "GL_ARB_vertex_buffer_object" )>-1
			GL_ARB_occlusion_query = Ext.Find( "GL_ARB_occlusion_query" )>-1
			GL_ARB_shader_objects = Ext.Find( "GL_ARB_shader_objects" )>-1
			GL_ARB_vertex_shader = Ext.Find( "GL_ARB_vertex_shader" )>-1
			GL_ARB_fragment_shader = Ext.Find( "GL_ARB_fragment_shader" )>-1
			GL_ARB_shading_language_100 = Ext.Find( "GL_ARB_shading_language_100" )>-1
			GL_ARB_texture_non_power_of_two = Ext.Find( "GL_ARB_texture_non_power_of_two" )>-1
			GL_ARB_point_sprite = Ext.Find( "GL_ARB_point_sprite" )>-1
			GL_ARB_fragment_program_shadow = Ext.Find( "GL_ARB_fragment_program_shadow" )>-1
			GL_ARB_draw_buffers = Ext.Find( "GL_ARB_draw_buffers" )>-1
			GL_ARB_texture_rectangle = Ext.Find( "GL_ARB_texture_rectangle" )>-1
			GL_ARB_color_buffer_float = Ext.Find( "GL_ARB_color_buffer_float" )>-1
			GL_ARB_half_float_pixel = Ext.Find( "GL_ARB_half_float_pixel" )>-1
			GL_ARB_texture_float = Ext.Find( "GL_ARB_texture_float" )>-1
			GL_ARB_pixel_buffer_object = Ext.Find( "GL_ARB_pixel_buffer_object" )>-1
			GL_EXT_abgr = Ext.Find( "GL_EXT_abgr" )>-1
			GL_EXT_blend_color = Ext.Find( "GL_EXT_blend_color" )>-1
			GL_EXT_polygon_offset = Ext.Find( "GL_EXT_polygon_offset" )>-1
			GL_EXT_texture = Ext.Find( "GL_EXT_texture" )>-1
			GL_EXT_texture3D = Ext.Find( "GL_EXT_texture3D" )>-1
			GL_SGIS_texture_filter4 = Ext.Find( "GL_SGIS_texture_filter4" )>-1
			GL_EXT_subtexture = Ext.Find( "GL_EXT_subtexture" )>-1
			GL_EXT_copy_texture = Ext.Find( "GL_EXT_copy_texture" )>-1
			GL_EXT_histogram = Ext.Find( "GL_EXT_histogram" )>-1
			GL_EXT_convolution = Ext.Find( "GL_EXT_convolution" )>-1
			GL_SGI_color_matrix = Ext.Find( "GL_SGI_color_matrix" )>-1
			GL_SGI_color_table = Ext.Find( "GL_SGI_color_table" )>-1
			GL_SGIS_pixel_texture = Ext.Find( "GL_SGIS_pixel_texture" )>-1
			GL_SGIX_pixel_texture = Ext.Find( "GL_SGIX_pixel_texture" )>-1
			GL_SGIS_texture4D = Ext.Find( "GL_SGIS_texture4D" )>-1
			GL_SGI_texture_color_table = Ext.Find( "GL_SGI_texture_color_table" )>-1
			GL_EXT_cmyka = Ext.Find( "GL_EXT_cmyka" )>-1
			GL_EXT_texture_object = Ext.Find( "GL_EXT_texture_object" )>-1
			GL_SGIS_detail_texture = Ext.Find( "GL_SGIS_detail_texture" )>-1
			GL_SGIS_sharpen_texture = Ext.Find( "GL_SGIS_sharpen_texture" )>-1
			GL_EXT_packed_pixels = Ext.Find( "GL_EXT_packed_pixels" )>-1
			GL_SGIS_texture_lod = Ext.Find( "GL_SGIS_texture_lod" )>-1
			GL_SGIS_multisample = Ext.Find( "GL_SGIS_multisample" )>-1
			GL_EXT_rescale_normal = Ext.Find( "GL_EXT_rescale_normal" )>-1
			GLX_EXT_visual_info = Ext.Find( "GLX_EXT_visual_info" )>-1
			GL_EXT_vertex_array = Ext.Find( "GL_EXT_vertex_array" )>-1
			GL_EXT_misc_attribute = Ext.Find( "GL_EXT_misc_attribute" )>-1
			GL_SGIS_generate_mipmap = Ext.Find( "GL_SGIS_generate_mipmap" )>-1
			GL_SGIX_clipmap = Ext.Find( "GL_SGIX_clipmap" )>-1
			GL_SGIX_shadow = Ext.Find( "GL_SGIX_shadow" )>-1
			GL_SGIS_texture_edge_clamp = Ext.Find( "GL_SGIS_texture_edge_clamp" )>-1
			GL_SGIS_texture_border_clamp = Ext.Find( "GL_SGIS_texture_border_clamp" )>-1
			GL_EXT_blend_minmax = Ext.Find( "GL_EXT_blend_minmax" )>-1
			GL_EXT_blend_subtract = Ext.Find( "GL_EXT_blend_subtract" )>-1
			GL_EXT_blend_logic_op = Ext.Find( "GL_EXT_blend_logic_op" )>-1
			GLX_SGI_swap_control = Ext.Find( "GLX_SGI_swap_control" )>-1
			GLX_SGI_video_sync = Ext.Find( "GLX_SGI_video_sync" )>-1
			GLX_SGI_make_current_read = Ext.Find( "GLX_SGI_make_current_read" )>-1
			GLX_SGIX_video_source = Ext.Find( "GLX_SGIX_video_source" )>-1
			GLX_EXT_visual_rating = Ext.Find( "GLX_EXT_visual_rating" )>-1
			GL_SGIX_interlace = Ext.Find( "GL_SGIX_interlace" )>-1
			GLX_EXT_import_context = Ext.Find( "GLX_EXT_import_context" )>-1
			GLX_SGIX_fbconfig = Ext.Find( "GLX_SGIX_fbconfig" )>-1
			GLX_SGIX_pbuffer = Ext.Find( "GLX_SGIX_pbuffer" )>-1
			GL_SGIS_texture_select = Ext.Find( "GL_SGIS_texture_select" )>-1
			GL_SGIX_sprite = Ext.Find( "GL_SGIX_sprite" )>-1
			GL_SGIX_texture_multi_buffer = Ext.Find( "GL_SGIX_texture_multi_buffer" )>-1
			GL_EXT_point_parameters = Ext.Find( "GL_EXT_point_parameters" )>-1
			GL_SGIX_instruments = Ext.Find( "GL_SGIX_instruments" )>-1
			GL_SGIX_texture_scale_bias = Ext.Find( "GL_SGIX_texture_scale_bias" )>-1
			GL_SGIX_framezoom = Ext.Find( "GL_SGIX_framezoom" )>-1
			GL_SGIX_tag_sample_buffer = Ext.Find( "GL_SGIX_tag_sample_buffer" )>-1
			GL_SGIX_reference_plane = Ext.Find( "GL_SGIX_reference_plane" )>-1
			GL_SGIX_flush_raster = Ext.Find( "GL_SGIX_flush_raster" )>-1
			GLX_SGI_cushion = Ext.Find( "GLX_SGI_cushion" )>-1
			GL_SGIX_depth_texture = Ext.Find( "GL_SGIX_depth_texture" )>-1
			GL_SGIS_fog_function = Ext.Find( "GL_SGIS_fog_function" )>-1
			GL_SGIX_fog_offset = Ext.Find( "GL_SGIX_fog_offset" )>-1
			GL_HP_image_transform = Ext.Find( "GL_HP_image_transform" )>-1
			GL_HP_convolution_border_modes = Ext.Find( "GL_HP_convolution_border_modes" )>-1
			GL_SGIX_texture_add_env = Ext.Find( "GL_SGIX_texture_add_env" )>-1
			GL_EXT_color_subtable = Ext.Find( "GL_EXT_color_subtable" )>-1
			GLU_EXT_object_space_tess = Ext.Find( "GLU_EXT_object_space_tess" )>-1
			GL_PGI_vertex_hints = Ext.Find( "GL_PGI_vertex_hints" )>-1
			GL_PGI_misc_hints = Ext.Find( "GL_PGI_misc_hints" )>-1
			GL_EXT_paletted_texture = Ext.Find( "GL_EXT_paletted_texture" )>-1
			GL_EXT_clip_volume_hint = Ext.Find( "GL_EXT_clip_volume_hint" )>-1
			GL_SGIX_list_priority = Ext.Find( "GL_SGIX_list_priority" )>-1
			GL_SGIX_ir_instrument1 = Ext.Find( "GL_SGIX_ir_instrument1" )>-1
			GLX_SGIX_video_resize = Ext.Find( "GLX_SGIX_video_resize" )>-1
			GL_SGIX_texture_lod_bias = Ext.Find( "GL_SGIX_texture_lod_bias" )>-1
			GLU_SGI_filter4_parameters = Ext.Find( "GLU_SGI_filter4_parameters" )>-1
			GLX_SGIX_dm_buffer = Ext.Find( "GLX_SGIX_dm_buffer" )>-1
			GL_SGIX_shadow_ambient = Ext.Find( "GL_SGIX_shadow_ambient" )>-1
			GLX_SGIX_swap_group = Ext.Find( "GLX_SGIX_swap_group" )>-1
			GLX_SGIX_swap_barrier = Ext.Find( "GLX_SGIX_swap_barrier" )>-1
			GL_EXT_index_texture = Ext.Find( "GL_EXT_index_texture" )>-1
			GL_EXT_index_material = Ext.Find( "GL_EXT_index_material" )>-1
			GL_EXT_index_func = Ext.Find( "GL_EXT_index_func" )>-1
			GL_EXT_index_array_formats = Ext.Find( "GL_EXT_index_array_formats" )>-1
			GL_EXT_compiled_vertex_array = Ext.Find( "GL_EXT_compiled_vertex_array" )>-1
			GL_EXT_cull_vertex = Ext.Find( "GL_EXT_cull_vertex" )>-1
			GLU_EXT_nurbs_tessellator = Ext.Find( "GLU_EXT_nurbs_tessellator" )>-1
			GL_SGIX_ycrcb = Ext.Find( "GL_SGIX_ycrcb" )>-1
			GL_EXT_fragment_lighting = Ext.Find( "GL_EXT_fragment_lighting" )>-1
			GL_IBM_rasterpos_clip = Ext.Find( "GL_IBM_rasterpos_clip" )>-1
			GL_HP_texture_lighting = Ext.Find( "GL_HP_texture_lighting" )>-1
			GL_EXT_draw_range_elements = Ext.Find( "GL_EXT_draw_range_elements" )>-1
			GL_WIN_phong_shading = Ext.Find( "GL_WIN_phong_shading" )>-1
			GL_WIN_specular_fog = Ext.Find( "GL_WIN_specular_fog" )>-1
			GLX_SGIS_color_range = Ext.Find( "GLX_SGIS_color_range" )>-1
			GL_EXT_light_texture = Ext.Find( "GL_EXT_light_texture" )>-1
			GL_SGIX_blend_alpha_minmax = Ext.Find( "GL_SGIX_blend_alpha_minmax" )>-1
			GL_EXT_scene_marker = Ext.Find( "GL_EXT_scene_marker" )>-1
			GL_SGIX_pixel_texture_bits = Ext.Find( "GL_SGIX_pixel_texture_bits" )>-1
			GL_EXT_bgra = Ext.Find( "GL_EXT_bgra" )>-1
			GL_SGIX_async = Ext.Find( "GL_SGIX_async" )>-1
			GL_SGIX_async_pixel = Ext.Find( "GL_SGIX_async_pixel" )>-1
			GL_SGIX_async_histogram = Ext.Find( "GL_SGIX_async_histogram" )>-1
			GL_INTEL_texture_scissor = Ext.Find( "GL_INTEL_texture_scissor" )>-1
			GL_INTEL_parallel_arrays = Ext.Find( "GL_INTEL_parallel_arrays" )>-1
			GL_HP_occlusion_test = Ext.Find( "GL_HP_occlusion_test" )>-1
			GL_EXT_pixel_transform = Ext.Find( "GL_EXT_pixel_transform" )>-1
			GL_EXT_pixel_transform_color_table = Ext.Find( "GL_EXT_pixel_transform_color_table" )>-1
			GL_EXT_shared_texture_palette = Ext.Find( "GL_EXT_shared_texture_palette" )>-1
			GLX_SGIS_blended_overlay = Ext.Find( "GLX_SGIS_blended_overlay" )>-1
			GL_EXT_separate_specular_color = Ext.Find( "GL_EXT_separate_specular_color" )>-1
			GL_EXT_secondary_color = Ext.Find( "GL_EXT_secondary_color" )>-1
			GL_EXT_texture_env = Ext.Find( "GL_EXT_texture_env" )>-1
			GL_EXT_texture_perturb_normal = Ext.Find( "GL_EXT_texture_perturb_normal" )>-1
			GL_EXT_multi_draw_arrays = Ext.Find( "GL_EXT_multi_draw_arrays" )>-1
			GL_EXT_fog_coord = Ext.Find( "GL_EXT_fog_coord" )>-1
			GL_REND_screen_coordinates = Ext.Find( "GL_REND_screen_coordinates" )>-1
			GL_EXT_coordinate_frame = Ext.Find( "GL_EXT_coordinate_frame" )>-1
			GL_EXT_texture_env_combine = Ext.Find( "GL_EXT_texture_env_combine" )>-1
			GL_APPLE_specular_vector = Ext.Find( "GL_APPLE_specular_vector" )>-1
			GL_APPLE_transform_hint = Ext.Find( "GL_APPLE_transform_hint" )>-1
			GL_SUNX_constant_data = Ext.Find( "GL_SUNX_constant_data" )>-1
			GL_SUN_global_alpha = Ext.Find( "GL_SUN_global_alpha" )>-1
			GL_SUN_triangle_list = Ext.Find( "GL_SUN_triangle_list" )>-1
			GL_SUN_vertex = Ext.Find( "GL_SUN_vertex" )>-1
			WGL_EXT_display_color_table = Ext.Find( "WGL_EXT_display_color_table" )>-1
			WGL_EXT_extensions_string = Ext.Find( "WGL_EXT_extensions_string" )>-1
			WGL_EXT_make_current_read = Ext.Find( "WGL_EXT_make_current_read" )>-1
			WGL_EXT_pixel_format = Ext.Find( "WGL_EXT_pixel_format" )>-1
			WGL_EXT_pbuffer = Ext.Find( "WGL_EXT_pbuffer" )>-1
			WGL_EXT_swap_control = Ext.Find( "WGL_EXT_swap_control" )>-1
			GL_EXT_blend_func_separate = Ext.Find( "GL_EXT_blend_func_separate" )>-1
			GL_INGR_color_clamp = Ext.Find( "GL_INGR_color_clamp" )>-1
			GL_INGR_interlace_read = Ext.Find( "GL_INGR_interlace_read" )>-1
			GL_EXT_stencil_wrap = Ext.Find( "GL_EXT_stencil_wrap" )>-1
			WGL_EXT_depth_float = Ext.Find( "WGL_EXT_depth_float" )>-1
			GL_EXT_422_pixels = Ext.Find( "GL_EXT_422_pixels" )>-1
			GL_NV_texgen_reflection = Ext.Find( "GL_NV_texgen_reflection" )>-1
			GL_SGIX_texture_range = Ext.Find( "GL_SGIX_texture_range" )>-1
			GL_SUN_convolution_border_modes = Ext.Find( "GL_SUN_convolution_border_modes" )>-1
			GLX_SUN_get_transparent_index = Ext.Find( "GLX_SUN_get_transparent_index" )>-1
			GL_EXT_texture_env_add = Ext.Find( "GL_EXT_texture_env_add" )>-1
			GL_EXT_texture_lod_bias = Ext.Find( "GL_EXT_texture_lod_bias" )>-1
			GL_EXT_texture_filter_anisotropic = Ext.Find( "GL_EXT_texture_filter_anisotropic" )>-1
			GL_EXT_vertex_weighting = Ext.Find( "GL_EXT_vertex_weighting" )>-1
			GL_NV_light_max_exponent = Ext.Find( "GL_NV_light_max_exponent" )>-1
			GL_NV_vertex_array_range = Ext.Find( "GL_NV_vertex_array_range" )>-1
			GL_NV_register_combiners = Ext.Find( "GL_NV_register_combiners" )>-1
			GL_NV_fog_distance = Ext.Find( "GL_NV_fog_distance" )>-1
			GL_NV_texgen_emboss = Ext.Find( "GL_NV_texgen_emboss" )>-1
			GL_NV_blend_square = Ext.Find( "GL_NV_blend_square" )>-1
			GL_NV_texture_env_combine4 = Ext.Find( "GL_NV_texture_env_combine4" )>-1
			GL_MESA_resize_buffers = Ext.Find( "GL_MESA_resize_buffers" )>-1
			GL_MESA_window_pos = Ext.Find( "GL_MESA_window_pos" )>-1
			GL_EXT_texture_compression_s3tc = Ext.Find( "GL_EXT_texture_compression_s3tc" )>-1
			GL_IBM_cull_vertex = Ext.Find( "GL_IBM_cull_vertex" )>-1
			GL_IBM_multimode_draw_arrays = Ext.Find( "GL_IBM_multimode_draw_arrays" )>-1
			GL_IBM_vertex_array_lists = Ext.Find( "GL_IBM_vertex_array_lists" )>-1
			GL_3DFX_texture_compression_FXT1 = Ext.Find( "GL_3DFX_texture_compression_FXT1" )>-1
			GL_3DFX_multisample = Ext.Find( "GL_3DFX_multisample" )>-1
			GL_3DFX_tbuffer = Ext.Find( "GL_3DFX_tbuffer" )>-1
			WGL_EXT_multisample = Ext.Find( "WGL_EXT_multisample" )>-1
			GL_SGIX_vertex_preclip = Ext.Find( "GL_SGIX_vertex_preclip" )>-1
			GL_SGIX_resample = Ext.Find( "GL_SGIX_resample" )>-1
			GL_SGIS_texture_color_mask = Ext.Find( "GL_SGIS_texture_color_mask" )>-1
			GLX_MESA_copy_sub_buffer = Ext.Find( "GLX_MESA_copy_sub_buffer" )>-1
			GLX_MESA_pixmap_colormap = Ext.Find( "GLX_MESA_pixmap_colormap" )>-1
			GLX_MESA_release_buffers = Ext.Find( "GLX_MESA_release_buffers" )>-1
			GLX_MESA_set_3dfx_mode = Ext.Find( "GLX_MESA_set_3dfx_mode" )>-1
			GL_EXT_texture_env_dot3 = Ext.Find( "GL_EXT_texture_env_dot3" )>-1
			GL_ATI_texture_mirror_once = Ext.Find( "GL_ATI_texture_mirror_once" )>-1
			GL_NV_fence = Ext.Find( "GL_NV_fence" )>-1
			GL_IBM_static_data = Ext.Find( "GL_IBM_static_data" )>-1
			GL_IBM_texture_mirrored_repeat = Ext.Find( "GL_IBM_texture_mirrored_repeat" )>-1
			GL_NV_evaluators = Ext.Find( "GL_NV_evaluators" )>-1
			GL_NV_packed_depth_stencil = Ext.Find( "GL_NV_packed_depth_stencil" )>-1
			GL_NV_register_combiners2 = Ext.Find( "GL_NV_register_combiners2" )>-1
			GL_NV_texture_compression_vtc = Ext.Find( "GL_NV_texture_compression_vtc" )>-1
			GL_NV_texture_rectangle = Ext.Find( "GL_NV_texture_rectangle" )>-1
			GL_NV_texture_shader = Ext.Find( "GL_NV_texture_shader" )>-1
			GL_NV_texture_shader2 = Ext.Find( "GL_NV_texture_shader2" )>-1
			GL_NV_vertex_array_range2 = Ext.Find( "GL_NV_vertex_array_range2" )>-1
			GL_NV_vertex_program = Ext.Find( "GL_NV_vertex_program" )>-1
			GLX_SGIX_visual_select_group = Ext.Find( "GLX_SGIX_visual_select_group" )>-1
			GL_SGIX_texture_coordinate_clamp = Ext.Find( "GL_SGIX_texture_coordinate_clamp" )>-1
			GLX_OML_swap_method = Ext.Find( "GLX_OML_swap_method" )>-1
			GLX_OML_sync_control = Ext.Find( "GLX_OML_sync_control" )>-1
			GL_OML_interlace = Ext.Find( "GL_OML_interlace" )>-1
			GL_OML_subsample = Ext.Find( "GL_OML_subsample" )>-1
			GL_OML_resample = Ext.Find( "GL_OML_resample" )>-1
			WGL_OML_sync_control = Ext.Find( "WGL_OML_sync_control" )>-1
			GL_NV_copy_depth_to_color = Ext.Find( "GL_NV_copy_depth_to_color" )>-1
			GL_ATI_envmap_bumpmap = Ext.Find( "GL_ATI_envmap_bumpmap" )>-1
			GL_ATI_fragment_shader = Ext.Find( "GL_ATI_fragment_shader" )>-1
			GL_ATI_pn_triangles = Ext.Find( "GL_ATI_pn_triangles" )>-1
			GL_ATI_vertex_array_object = Ext.Find( "GL_ATI_vertex_array_object" )>-1
			GL_EXT_vertex_shader = Ext.Find( "GL_EXT_vertex_shader" )>-1
			GL_ATI_vertex_streams = Ext.Find( "GL_ATI_vertex_streams" )>-1
			WGL_I3D_digital_video_control = Ext.Find( "WGL_I3D_digital_video_control" )>-1
			WGL_I3D_gamma = Ext.Find( "WGL_I3D_gamma" )>-1
			WGL_I3D_genlock = Ext.Find( "WGL_I3D_genlock" )>-1
			WGL_I3D_image_buffer = Ext.Find( "WGL_I3D_image_buffer" )>-1
			WGL_I3D_swap_frame_lock = Ext.Find( "WGL_I3D_swap_frame_lock" )>-1
			WGL_I3D_swap_frame_usage = Ext.Find( "WGL_I3D_swap_frame_usage" )>-1
			GL_ATI_element_array = Ext.Find( "GL_ATI_element_array" )>-1
			GL_SUN_mesh_array = Ext.Find( "GL_SUN_mesh_array" )>-1
			GL_SUN_slice_accum = Ext.Find( "GL_SUN_slice_accum" )>-1
			GL_NV_multisample_filter_hint = Ext.Find( "GL_NV_multisample_filter_hint" )>-1
			GL_NV_depth_clamp = Ext.Find( "GL_NV_depth_clamp" )>-1
			GL_NV_occlusion_query = Ext.Find( "GL_NV_occlusion_query" )>-1
			GL_NV_point_sprite = Ext.Find( "GL_NV_point_sprite" )>-1
			WGL_NV_render_depth_texture = Ext.Find( "WGL_NV_render_depth_texture" )>-1
			WGL_NV_render_texture_rectangle = Ext.Find( "WGL_NV_render_texture_rectangle" )>-1
			GL_NV_texture_shader3 = Ext.Find( "GL_NV_texture_shader3" )>-1
			GL_NV_vertex_program1_1 = Ext.Find( "GL_NV_vertex_program1_1" )>-1
			GL_EXT_shadow_funcs = Ext.Find( "GL_EXT_shadow_funcs" )>-1
			GL_EXT_stencil_two_side = Ext.Find( "GL_EXT_stencil_two_side" )>-1
			GL_ATI_text_fragment_shader = Ext.Find( "GL_ATI_text_fragment_shader" )>-1
			GL_APPLE_client_storage = Ext.Find( "GL_APPLE_client_storage" )>-1
			GL_APPLE_element_array = Ext.Find( "GL_APPLE_element_array" )>-1
			GL_APPLE_fence = Ext.Find( "GL_APPLE_fence" )>-1
			GL_APPLE_vertex_array_object = Ext.Find( "GL_APPLE_vertex_array_object" )>-1
			GL_APPLE_vertex_array_range = Ext.Find( "GL_APPLE_vertex_array_range" )>-1
			GL_APPLE_ycbcr_422 = Ext.Find( "GL_APPLE_ycbcr_422" )>-1
			GL_S3_s3tc = Ext.Find( "GL_S3_s3tc" )>-1
			GL_ATI_draw_buffers = Ext.Find( "GL_ATI_draw_buffers" )>-1
			WGL_ATI_pixel_format_float = Ext.Find( "WGL_ATI_pixel_format_float" )>-1
			GL_ATI_texture_env_combine3 = Ext.Find( "GL_ATI_texture_env_combine3" )>-1
			GL_ATI_texture_float = Ext.Find( "GL_ATI_texture_float" )>-1
			GL_NV_float_buffer = Ext.Find( "GL_NV_float_buffer" )>-1
			GL_NV_fragment_program = Ext.Find( "GL_NV_fragment_program" )>-1
			GL_NV_half_float = Ext.Find( "GL_NV_half_float" )>-1
			GL_NV_pixel_data_range = Ext.Find( "GL_NV_pixel_data_range" )>-1
			GL_NV_primitive_restart = Ext.Find( "GL_NV_primitive_restart" )>-1
			GL_NV_texture_expand_normal = Ext.Find( "GL_NV_texture_expand_normal" )>-1
			GL_NV_vertex_program2 = Ext.Find( "GL_NV_vertex_program2" )>-1
			GL_ATI_map_object_buffer = Ext.Find( "GL_ATI_map_object_buffer" )>-1
			GL_ATI_separate_stencil = Ext.Find( "GL_ATI_separate_stencil" )>-1
			GL_ATI_vertex_attrib_array_object = Ext.Find( "GL_ATI_vertex_attrib_array_object" )>-1
			GL_OES_byte_coordinates = Ext.Find( "GL_OES_byte_coordinates" )>-1
			GL_OES_fixed_point = Ext.Find( "GL_OES_fixed_point" )>-1
			GL_OES_single_precision = Ext.Find( "GL_OES_single_precision" )>-1
			GL_OES_compressed_paletted_texture = Ext.Find( "GL_OES_compressed_paletted_texture" )>-1
			GL_OES_read_format = Ext.Find( "GL_OES_read_format" )>-1
			GL_OES_query_matrix = Ext.Find( "GL_OES_query_matrix" )>-1
			GL_EXT_depth_bounds_test = Ext.Find( "GL_EXT_depth_bounds_test" )>-1
			GL_EXT_texture_mirror_clamp = Ext.Find( "GL_EXT_texture_mirror_clamp" )>-1
			GL_EXT_blend_equation_separate = Ext.Find( "GL_EXT_blend_equation_separate" )>-1
			GL_MESA_pack_invert = Ext.Find( "GL_MESA_pack_invert" )>-1
			GL_MESA_ycbcr_texture = Ext.Find( "GL_MESA_ycbcr_texture" )>-1
			GL_EXT_pixel_buffer_object = Ext.Find( "GL_EXT_pixel_buffer_object" )>-1
			GL_NV_fragment_program_option = Ext.Find( "GL_NV_fragment_program_option" )>-1
			GL_NV_fragment_program2 = Ext.Find( "GL_NV_fragment_program2" )>-1
			GL_NV_vertex_program2_option = Ext.Find( "GL_NV_vertex_program2_option" )>-1
			GL_NV_vertex_program3 = Ext.Find( "GL_NV_vertex_program3" )>-1
			GLX_SGIX_hyperpipe = Ext.Find( "GLX_SGIX_hyperpipe" )>-1
			GLX_MESA_agp_offset = Ext.Find( "GLX_MESA_agp_offset" )>-1
			GL_EXT_texture_compression_dxt1 = Ext.Find( "GL_EXT_texture_compression_dxt1" )>-1
			GL_EXT_framebuffer_object = Ext.Find( "GL_EXT_framebuffer_object" )>-1
			GL_GREMEDY_string_marker = Ext.Find( "GL_GREMEDY_string_marker" )>-1
      glGetIntegerv( GL_MAX_TEXTURE_UNITS_ARB, Varptr MaxTextureUnits )
      glGetIntegerv( GL_MAX_TEXTURE_SIZE, Varptr MaxTextureSize )
      glGetIntegerv( GL_MAX_ATTRIB_STACK_DEPTH, Varptr MaxAttribStackDepth )
      glGetIntegerV( GL_MAX_CLIENT_ATTRIB_STACK_DEPTH, Varptr MaxClientAttribStackDepth )
      glGetIntegerv( GL_MAX_CLIP_PLANES, Varptr MaxClipPlanes )
      glGetIntegerv( GL_MAX_3D_TEXTURE_SIZE, Varptr Max3DTextureSize )
      glGetIntegerv( GL_MAX_CUBE_MAP_TEXTURE_SIZE, Varptr MaxCubeMapTextureSize )
      glGetIntegerv( GL_MAX_LIGHTS, Varptr MaxLights )
      glGetIntegerv( GL_MAX_MODELVIEW_STACK_DEPTH, Varptr MaxModelViewStackDepth )
      glGetIntegerv( GL_MAX_PROJECTION_STACK_DEPTH, Varptr MaxProjectionStackDepth )
      glGetIntegerv( GL_MAX_TEXTURE_LOD_BIAS, Varptr MaxTextureLODBias )
      glGetIntegerv( GL_MAX_TEXTURE_STACK_DEPTH, Varptr MaxTextureStackDepth )
      __glob_glHelp = 1
    EndIf
  End Method
End Type
