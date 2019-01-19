package com.example.antoinerousselot.network;

public interface UrlConstants {
    String GET_URL = "http://node.oignon.ovh1.ec-m.fr/";
    String POST_URL = "http://node.oignon.ovh1.ec-m.fr/file-upload";
    String POST_URL_AUTH_PLAYER1 = "http://node.oignon.ovh1.ec-m.fr/authenticationPlayer1";
    String POST_URL_TEXT = "http://node.oignon.ovh1.ec-m.fr/postText";
    String POST_URL_LOGOUT = "http://node.oignon.ovh1.ec-m.fr/logout";

    int GET_URL_REQUEST_CODE = 1;
    int POST_URL_REQUEST_CODE = 2;
    int POST_URL_AUTHPLAYER1_REQUEST_CODE = 3;
    int POST_URL_TEXT_REQUEST_CODE = 4;
    int POST_URL_LOGOUT_CODE = 5;
}
