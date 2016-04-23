package com.hiflying.smartlink;

import android.content.Context;

public abstract interface ISmartLinker
{
  public static final int V3 = 3;
  public static final int V5 = 5;
  
  public abstract void setOnSmartLinkListener(OnSmartLinkListener paramOnSmartLinkListener);
  
  public abstract void start(Context paramContext, String paramString, String... paramVarArgs)
    throws Exception;
  
  public abstract void stop();
  
  public abstract boolean isSmartLinking();
}


/* Location:              E:\manniu\manniu\libs\iots-android-smartlink3.7.0.jar!\com\hiflying\smartlink\ISmartLinker.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */