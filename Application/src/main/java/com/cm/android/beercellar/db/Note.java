package com.cm.android.beercellar.db;

import java.io.Serializable;


public class Note implements Serializable{
    public long id;
    public String beer="";
    public String rating="0.0";
    public String textExtract="";
    public String notes="";
    public String share="";
    public String picture="";
    public String uri = "";
    public long created;
    public long updated;
}
