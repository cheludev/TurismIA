package com.turismea.exception;

public class CityNotFoundException extends RuntimeException {
    private String name;
    private Long id;

    public CityNotFoundException(String city) {
        super("City " + city + " not found");
        this.name = city;
    }

    public CityNotFoundException(Long id) {
        super("City " + id + " not found");
        this.id = id;
    }

    public String getName() {
        return name;
    }
    public Long getId() {
        return id;
    }

}
