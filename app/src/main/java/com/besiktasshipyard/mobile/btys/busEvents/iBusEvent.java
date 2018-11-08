package com.besiktasshipyard.mobile.btys.busEvents;

/**
 * Created by aliarin on 20.6.2017.
 *
 * Uygulamada kullanlan tum BusEvent lerin dayandigi interface
 * Volley + GreenRobot mimarisi kullaniyoruz.
 * Volley: network cagrilarini asenkron yapan framework
 * GreenRobot EventBus: Volleyden gelen responselarin broadcast edildigi eventBus
 * Volley ile bilgileri aliyoruz
 * EventBus ile broadcast ediyoruz
 * EventBus broadcast ederken, callback event cagiriyor
 * bu callback eventlerin hepsi de bu interface e dayaniyor
 */


public interface iBusEvent {
    public Object getData();
    public void setData(Object data);

    public Object getError();
    public void setError(Object error);
}
