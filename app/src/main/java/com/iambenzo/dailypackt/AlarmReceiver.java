package com.iambenzo.dailypackt;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;

/**
 * Class Description
 * Goes Here
 *
 * @author Ben
 * @version 1.0
 * @since 11/05/2017
 */

public class AlarmReceiver
        extends BroadcastReceiver
{
    public void onReceive(Context paramContext, Intent paramIntent)
    {
        paramIntent = new Intent(paramContext, NotificationService.class);
        paramIntent.setData(Uri.parse("custom://" + System.currentTimeMillis()));
        paramContext.startService(paramIntent);
    }
}
