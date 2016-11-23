package com.xpple.jahoqy.BaseClass;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

import com.xpple.jahoqy.R;
import com.xpple.jahoqy.ui.activity.AcceptHelp;
import com.xpple.jahoqy.ui.activity.Showmytaskhelp;

import cn.bmob.im.BmobNotifyManager;

/**
 * Created by Koreleone on 2015/11/8.
 */
public class MyPushMessageReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        // TODO Auto-generated method stub
        String json = intent.getStringExtra("msg");
        String[] sourceStrArray = json.split(",");
        switch (sourceStrArray[1]){
            case "wh":
                Intent AccHelp=new Intent(context, AcceptHelp.class);
                AccHelp.putExtra("shid",sourceStrArray[3]);
                BmobNotifyManager.getInstance(context)
            .showNotifyWithExtras(true,
                    true, R.mipmap.ic_logo2,
                    "有人帮你啦", sourceStrArray[2], "申请帮忙",
                    AccHelp);
                break;
            case "ac":
                Intent help=new Intent(context, Showmytaskhelp.class);
                //Toast.makeText(context,sourceStrArray[3],Toast.LENGTH_LONG).show();
                help.putExtra("shid",sourceStrArray[3]);
                help.putExtra("ABC","NO");
                BmobNotifyManager.getInstance(context)
                        .showNotifyWithExtras(true,
                                true, R.mipmap.ic_logo2,
                                "帮忙申请已通过", sourceStrArray[2], "谢谢你，来帮我吧！",
                                help);
                break;
        }

    }
}
