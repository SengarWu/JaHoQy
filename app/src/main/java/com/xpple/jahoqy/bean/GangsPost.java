package com.xpple.jahoqy.bean;

import cn.bmob.v3.BmobObject;

/**
 * Created by caolin on 2015/10/5.
 */
public class GangsPost extends BmobObject {
    private String positionName;
    private int positionGrade;
    public String getPositionName(){return positionName;}
    public int getPositionGrade(){return positionGrade;}
    public void setPositionName(String positionName){
        this.positionName=positionName;
    }
    public void setPositionGrade(int positionGrade){
        this.positionGrade=positionGrade;
    }
}
