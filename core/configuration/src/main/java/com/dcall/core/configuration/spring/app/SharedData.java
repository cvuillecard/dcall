package com.dcall.core.configuration.spring.app;

public class SharedData {
    private Object datas;

    public <T> Object getDatas() {  return (T) datas; }
    public <T> void setDatas(T datas) { this.datas = datas; }
}
