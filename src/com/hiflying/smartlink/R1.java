/*     */ package com.hiflying.smartlink;
/*     */ 
/*     */ import java.lang.reflect.Field;

import android.content.Context;
import android.content.res.Resources;
/*     */ 
/*     */ 
/*     */ 
/*     */ public class R1
/*     */ {
/*     */   private static Context context;
/*     */   
/*     */   public static void initContext(Context _context)
/*     */   {
/*  13 */     context = _context;
/*     */   }
/*     */   
/*     */   public static int anim(String name) {
/*  17 */     return getIdentifier(context, name, "anim");
/*     */   }
/*     */   
/*     */   public static int animator(String name) {
/*  21 */     return getIdentifier(context, name, "animator");
/*     */   }
/*     */   
/*     */   public static int array(String name) {
/*  25 */     return getIdentifier(context, name, "array");
/*     */   }
/*     */   
/*     */   public static int attr(String name) {
/*  29 */     return getIdentifier(context, name, "attr");
/*     */   }
/*     */   
/*     */   public static int color(String name) {
/*  33 */     return getIdentifier(context, name, "color");
/*     */   }
/*     */   
/*     */   public static int dimen(String name) {
/*  37 */     return getIdentifier(context, name, "dimen");
/*     */   }
/*     */   
/*     */   public static int drawable(String name) {
/*  41 */     return getIdentifier(context, name, "drawable");
/*     */   }
/*     */   
/*     */   public static int id(String name) {
/*  45 */     return getIdentifier(context, name, "id");
/*     */   }
/*     */   
/*     */   public static int integer(String name) {
/*  49 */     return getIdentifier(context, name, "integer");
/*     */   }
/*     */   
/*     */   public static int layout(String name) {
/*  53 */     return getIdentifier(context, name, "layout");
/*     */   }
/*     */   
/*     */   public static int raw(String name) {
/*  57 */     return getIdentifier(context, name, "raw");
/*     */   }
/*     */   
/*     */   public static int string(String name) {
/*  61 */     return getIdentifier(context, name, "string");
/*     */   }
/*     */   
/*     */   public static int style(String name) {
/*  65 */     return getIdentifier(context, name, "style");
/*     */   }
/*     */   
/*     */   public static int[] styleable(String name)
/*     */   {
/*  70 */     return (int[])getFieldFromStyleable(context, name);
/*     */   }
/*     */   
/*     */   public static <T> T styleable(String name, Class<T> clazz)
/*     */   {
/*  75 */     return (T)getFieldFromStyleable(context, name);
/*     */   }
/*     */   
/*     */   private static int getIdentifier(Context context, String name, String type)
/*     */   {
/*  80 */     if (context == null) {
/*  81 */       new NullPointerException("Must call initContext(Context _context), recommend application context");
/*     */     }
/*     */     
/*  84 */     int resource = context.getResources().getIdentifier(name, type, context.getPackageName());
/*  85 */     if (resource == 0) {
	
/*  86 */       throw new Resources.NotFoundException(String.format("Resource for id R.%s.%s not found!", new Object[] { type, name }));
/*     */     }
/*     */     
/*  89 */     return resource;
/*     */   }
/*     */   
/*     */ 
/*     */   public static final <T> T getFieldFromStyleable(Context context, String name)
/*     */   {
/*     */     try
/*     */     {
/*  97 */       Field field = Class.forName(context.getPackageName() + ".R$styleable").getField(name);
/*  98 */       if (field != null) {
/*  99 */         return (T)field.get(null);
/*     */       }
/*     */     } catch (Throwable t) {
/* 102 */       t.printStackTrace();
/*     */     }
/* 104 */     return null;
/*     */   }
/*     */ }


/* Location:              E:\manniu\manniu\libs\iots-android-smartlink3.7.0.jar!\com\hiflying\smartlink\R1.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */