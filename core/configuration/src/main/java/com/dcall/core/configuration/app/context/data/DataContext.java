package com.dcall.core.configuration.app.context.data;

public class DataContext {
    private Object datas;

    public <T> Object getDatas() {  return (T) datas; }
    public <T> void setDatas(T datas) { this.datas = datas; }
}
