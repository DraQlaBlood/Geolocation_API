package com.mars.locationservice.controller;


import com.mars.locationservice.model.LocationDetails;
import com.maxmind.geoip2.DatabaseReader;
import com.maxmind.geoip2.exception.GeoIp2Exception;
import com.maxmind.geoip2.model.CityResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.ResourceUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.io.File;
import java.io.IOException;
import java.net.InetAddress;

@RestController
public class locationController {

    private HttpServletRequest request;
    @Autowired
    public void setRequest(HttpServletRequest request){
        this.request=request;
    }

    private DatabaseReader dbReader;
    String cityName ;
    String latitude ;
    String longitude ;
    String countryName;
    private static final String[] IP_HEADER_CANDIDATES = {
            "X-Forwarded-For",
            "Proxy-Client-IP",
            "WL-Proxy-Client-IP",
            "HTTP_X_FORWARDED_FOR",
            "HTTP_X_FORWARDED",
            "HTTP_X_CLUSTER_CLIENT_IP",
            "HTTP_CLIENT_IP",
            "HTTP_FORWARDED_FOR",
            "HTTP_FORWARDED",
            "HTTP_VIA",
            "REMOTE_ADDR" };

    public locationController() throws IOException {

        File database = ResourceUtils.getFile("classpath:geolite/GeoLite2-City.mmdb");
        dbReader = new DatabaseReader.Builder(database).build();
    }

    public String getipAddress(HttpServletRequest request){

        for (String header : IP_HEADER_CANDIDATES) {
            String ip = request.getHeader(header);
            if (ip != null && ip.length() != 0 && !"unknown".equalsIgnoreCase(ip)) {
                return ip;
            }
        }
        return request.getRemoteAddr();
        //return ip;
    }

    @GetMapping(value = "/location")
    public LocationDetails getLocation()
            throws IOException, GeoIp2Exception {
        InetAddress ipAddress = InetAddress.getByName(getipAddress(request));
        if ((ipAddress.isAnyLocalAddress() ) ||(ipAddress.isLinkLocalAddress())||
                (ipAddress.isSiteLocalAddress())||(ipAddress.isLoopbackAddress())){
            cityName ="Inconnu";
            latitude ="Inconnu";
            longitude ="Inconnu";
            countryName="Inconnu";
        }else{
            CityResponse response = dbReader.city(ipAddress);
            cityName = response.getCity().getName();
            latitude = response.getLocation().getLatitude().toString();
            longitude = response.getLocation().getLongitude().toString();
            countryName=response.getCountry().getName().toUpperCase();
        }

        return new LocationDetails(getipAddress(request), cityName, countryName,latitude, longitude);
    }

}
