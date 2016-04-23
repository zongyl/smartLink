package com.hiflying.smartlink;

public abstract interface OnSmartLinkListener
{
  public abstract void onLinked(SmartLinkedModule paramSmartLinkedModule);
  
  public abstract void onCompleted();
  
  public abstract void onTimeOut();
}


/* Location:              E:\manniu\manniu\libs\iots-android-smartlink3.7.0.jar!\com\hiflying\smartlink\OnSmartLinkListener.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */