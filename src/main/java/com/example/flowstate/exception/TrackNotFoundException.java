package com.example.flowstate.exception;

public class TrackNotFoundException extends RuntimeException{
    public TrackNotFoundException(Long id){
        super("Трек с id"+id+"не найден");
    }
}
