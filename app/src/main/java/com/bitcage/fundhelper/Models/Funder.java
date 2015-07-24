package com.bitcage.fundhelper.Models;

/**
 * Created by JohnnyCage on 2015-7-24.
 */
public class Funder {

    public String Code;
    public String SimpleName;
    public String Name;
    public String Type;

    public Funder(String code, String simpleName, String name, String type) {
        this.Code = code;
        this.SimpleName = simpleName;
        this.Name = name;
        this.Type = type;
    }
}
