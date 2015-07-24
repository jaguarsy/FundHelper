package com.bitcage.fundhelper.Utils;

import android.graphics.Bitmap;

import com.bitcage.fundhelper.Models.Funder;
import com.bitcage.fundhelper.Models.FunderDetail;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by JohnnyCage on 2015-7-24.
 */
public class FundReader {

    private String content;
    private ArrayList<Funder> funderList;

    public FundReader(String content) {
        this.content = content
                .replace("var r =", "")
                .replace("[[", "")
                .replace("]]", "")
                .replace("\"", "")
                .trim();
        initList();
    }

    private void initList() {
        funderList = new ArrayList<Funder>();

        String[] results = this.content.split("\\],\\[");

        for (String item : results) {
            String[] itemResults = item.split(",");
            Funder funder = new Funder(
                    itemResults[0],
                    itemResults[1],
                    itemResults[2],
                    itemResults[3]);
            funderList.add(funder);
        }
    }

    public ArrayList<String> GetStringList() {
        ArrayList<String> list = new ArrayList<String>();

        for (Funder item : funderList) {
            list.add(item.Name);
        }

        return list;
    }

    public String Search(String name) {
        if (funderList == null) return null;
        for (Funder item : funderList) {
            if (item.Name.equals(name)) {
                return item.Code;
            }
        }
        return null;
    }

    public FunderDetail GetDetail(String content,Bitmap img) throws JSONException {
        content = content.replace("jsonpgz(", "").replace(")", "");

        JSONObject jsonObject = new JSONObject(content);
        FunderDetail detail = new FunderDetail(
                jsonObject.getString("jzrq"),
                jsonObject.getString("dwjz"),
                jsonObject.getString("gsz"),
                jsonObject.getString("gszzl"),
                jsonObject.getString("gztime"),
                img);
        return detail;
    }
}
